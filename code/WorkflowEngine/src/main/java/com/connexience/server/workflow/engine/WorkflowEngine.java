/**
 * e-Science Central
 * Copyright (C) 2008-2013 School of Computing Science, Newcastle University
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package com.connexience.server.workflow.engine;

import com.connexience.server.ConnexienceException;

import com.connexience.server.workflow.service.*;
import com.connexience.server.model.security.*;
import com.connexience.server.workflow.api.*;
import com.connexience.server.workflow.api.ApiProvider;
import com.connexience.server.workflow.api.impl.APIBrokerImpl;
import com.connexience.server.workflow.cloud.library.ServiceLibrary;
import com.connexience.server.workflow.cloud.library.installer.InstallerException;
import com.connexience.server.workflow.cloud.library.installer.SystemManager;
import com.connexience.server.workflow.cloud.library.installer.SystemManagerFactory;
import com.connexience.server.workflow.util.ZipUtils;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.drawing.*;

import java.rmi.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.rmi.server.UnicastRemoteObject;
import org.apache.log4j.*;

/**
 * This class provides a singleton workflow management and execution engine. It
 * contains all of the WorkflowInvocation objects and manages status updates and
 * co-ordinates the status update messages. It uses JMS as the mechanism for
 * obtaining status updates.
 *
 * @author hugo
 */
public class WorkflowEngine extends UnicastRemoteObject implements APIBrokerFactory, APIBrokerContainer {
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(WorkflowEngine.class);
    
    /**
     * Currently executing workflow invocations
     */
    private ConcurrentHashMap<String, WorkflowInvocation> invocations = new ConcurrentHashMap<>();
    
    /**
     * Global data source
     */
    private GlobalDataSource globalData = new GlobalDataSource(System.getProperty("user.home") + File.separator + "temp");
    /**
     * Desired transfer format for services
     */
    private String dataTransferType = DataProcessorDataSource.FILE_DATA_SOURCE;
    /**
     * API Provider that is used to create new API objects
     */
    private ApiProvider apiProvider;
    /**
     * Counter to provide PID numbers for new invocations
     */
    private long pidCounter = 0;
    /**
     * Should the results data be stored in the workflow execution report. If this is false, the results
     * will be posted back directly to the database
     */
    private boolean reportUsedForCommandOutput = true;

    /** Listeners */
    private ArrayList<WorkflowEngineListener> listeners = new ArrayList<>();

    /** API Brokers */
    private ArrayList<APIBroker> apiBrokers = new ArrayList<>();
    
    /** Service library */
    private ServiceLibrary library;
    
    /**
     * Construct with a data source
     */
    public WorkflowEngine(GlobalDataSource globalData, ApiProvider apiProvider, ServiceLibrary library) throws RemoteException {
        this.globalData = globalData;
        this.apiProvider = apiProvider;
        this.library = library;
        logger.debug("Created workflow engine in: " + globalData.getBaseDirectory());
    }

    /**
     * Construct with a default data source
     */
    public WorkflowEngine(ApiProvider apiProvider, ServiceLibrary library) throws RemoteException {
        this.apiProvider = apiProvider;
        this.library = library;
        logger.debug("Created workflow engine in default location");
    }

    /** Add a listener */
    public void addWorkflowEngineListener(WorkflowEngineListener listener){
        listeners.add(listener);
    }

    /** Remove a listener */
    public void removeWorkflowEngineListener(WorkflowEngineListener listner){
        listeners.remove(listner);
    }

    /** Return the global service library */
    public ServiceLibrary getLibrary() {
        return library;
    }

    /** Notify listeners that an invocation has started */
    public void notifyInvocationStarted(WorkflowInvocation invocation){
        for(int i=0;i<listeners.size();i++){
            listeners.get(i).invocationStarted(invocation);
        }
    }

    /** Notify listeners that an invocation has finised */
    private void notifyInvocationFinished(WorkflowInvocation invocation){
        for(int i=0;i<listeners.size();i++){
            listeners.get(i).invocationFinished(invocation);
        }
    }

    /**
     * Get the next sequential PID
     */
    public synchronized long getNextPid() {
        pidCounter++;
        return pidCounter;
    }

    /**
     * Provide an Enumeration of the running invocations
     */
    public Enumeration<WorkflowInvocation> listInvocations() {
        return invocations.elements();
    }

    /**
     * Get the API Provider for this engine
     */
    public ApiProvider getAPIProvider() {
        return apiProvider;
    }

    /**
     * Get the data transfer type. This specifies how services are expected
     * to get hold of and set transfer data
     */
    public String getDataTransferType() {
        return dataTransferType;
    }

    /**
     * Set the data transfer type. This specifies how services are expected
     * to get hold of and set transfer data
     */
    public void setDataTransferType(String dataTransferType) {
        this.dataTransferType = dataTransferType;
    }

    /**
     * Is the XML workflow report used to contain the output stream from the service
     */
    public boolean isReportUsedForCommandOutput() {
        return reportUsedForCommandOutput;
    }

    /**
     * Is the XML workflow report used to contain the output stream from the service
     */
    public void setReportUsedForCommandOutput(boolean reportUsedForCommandOutput) {
        this.reportUsedForCommandOutput = reportUsedForCommandOutput;
    }

    /**
     * Get a WorkflowInvocation by ID
     */
    public WorkflowInvocation getInvocation(String invocationId) {
        if (invocations.containsKey(invocationId)) {
            return invocations.get(invocationId);
        } else {
            return null;
        }
    }

    /** 
     * Is an invocation present
     */
    public boolean invocationPresent(String invocationId){
        return invocations.containsKey(invocationId);
    }
    
    /**
     * Return the number of invocations currently in the engine
     */
    public int getInvocationCount(){
        return invocations.size();
    }

    /**
     * Get the number of active invocations currently in the engine. This
     * is defined as the number of invocations that are not waiting on
     * a workflow lock/
     */
    public int getActiveInvocationCount(){
        WorkflowInvocation invocation;
        Enumeration<WorkflowInvocation> invs = invocations.elements();
        int count = 0;
        while(invs.hasMoreElements()){
            invocation = invs.nextElement();
            if(!invocation.isWatitingForLock()){
                count++;
            }
        }
        return count;
    }

    /**
     * Get a reference to the global data store
     */
    public GlobalDataSource getDataSource() {
        return globalData;
    }

    /**
     * Create a WorkflowInvocation from a directory
     */
    public WorkflowInvocation createWorkflowInvocation(File directory) throws WorkflowInvocationException {
        WorkflowInvocation invocation = new WorkflowInvocation(apiProvider, directory);
        invocation.setDataSource(globalData.createInvocationDataSource(invocation.getInvocationId()));
        invocation.setParent(this);
        logger.debug("Created workflow invocation from invocation directory. InvocationID=" + invocation.getInvocationId());
        return invocation;
    }
    
    /**
     * Create a WorkflowInvocation ready to start
     */
    public WorkflowInvocation createWorkflowInvocation(XmlDataStore drawingData, Ticket ticket) throws WorkflowInvocationException {
        // Load the drawing
        String invocationId = new RandomGUID().toString();
        WorkflowInvocation invocation = new WorkflowInvocation(drawingData, ticket, apiProvider, invocationId);
        invocation.setDataSource(globalData.createInvocationDataSource(invocation.getInvocationId()));
        invocation.setParent(this);
        logger.debug("Creating workflow invocation object and assigning an invocation id. InvocationID=" + invocation.getInvocationId());
        return invocation;
    }

    /**
     * Create a workflow invocation with a pre-existing invocation ID
     */
    public WorkflowInvocation createWorkflowInvocation(XmlDataStore drawingData, Ticket ticket, String invocationId) throws WorkflowInvocationException {
        WorkflowInvocation invocation = new WorkflowInvocation(drawingData, ticket, apiProvider, invocationId);
        invocation.setDataSource(globalData.createInvocationDataSource(invocationId));
        invocation.setParent(this);
        logger.debug("Created workflow invocation object. InvocationID=" + invocation.getInvocationId());
        return invocation;
    }

    /**
     * Start a workflow invocation
     */
    public void startInvocation(WorkflowInvocation invocation) throws WorkflowInvocationException {
        invocation.setPid(getNextPid());
        
        // Set directory permissions
        if(invocation.isExecuteAsDifferentUser()){
            File dir = invocation.getDataSource().getStorageDir();
            
            try {
                SystemManager mgr = SystemManagerFactory.newInstance();
                // Kill everything owned by the user before starting
                mgr.killAllUserTasks(invocation.getSystemUsername());
                
                // Set file to be owned by invocation
                logger.debug("Setting owner to: " + invocation.getSystemUsername() + ". InvocationID=" + invocation.getInvocationId());
                mgr.setOwnerOnFile(dir, invocation.getSystemUsername(), true);
                
                // Add group permission to workflow engine
                logger.debug("Setting permissions on folder. InvocationID=" + invocation.getInvocationId());
                mgr.changePermissions(dir, true, true, false, true);
                
            } catch (Exception e){
                throw new WorkflowInvocationException("Error setting directory permissions: " + e.getMessage(), e);
            }
        }
        
        invocations.put(invocation.getInvocationId(), invocation);
        invocation.start();
    }

    /**
     * Find a workflow invocation by pid. This is not an efficient search, but is
     * only used from the command line tools
     */
    public WorkflowInvocation findInvocationByPid(long pid) {
        Enumeration<WorkflowInvocation> i = invocations.elements();
        WorkflowInvocation invocation;

        while (i.hasMoreElements()) {
            invocation = i.nextElement();
            if (invocation.getPid() == pid) {
                return invocation;
            }
        }
        return null;
    }

    /**
     * Process a response message
     */
    public void processResponseMessage(DataProcessorResponseMessage response) {
        String invocationId = response.getInvocationId();    // Invocation to update
        
        // Notify the correct invocation of the response
        if (invocations.containsKey(invocationId)) {
            logger.debug("Workflow engine received service completion message. InvocationID=" + response.getInvocationId() + " BlockID=" + response.getContextId());
            invocations.get(invocationId).processorFinished(response);
        } else {
            logger.error("Received response message for non-existant workflow. InvocationID=" + response.getInvocationId());
        }
    }

    /**
     * An invocation has terminated
     */
    public void invocationFinished(WorkflowInvocation invocation) {
        logger.debug("Workflow invocation finished. InvocationID=" + invocation.getInvocationId());
        invocations.remove(invocation.getInvocationId());

        // What is the purpose of this block of code if status is a local variable and 
        // is never used?
        // TODO: remove this block of code completely
        /*
        //get the status of the workflow
        String status = WorkflowInvocationFolder.OK_MESSAGE;
        Hashtable reports = invocation.getExecutionReports();
        for (Object key : reports.keySet()) {
            BlockExecutionReport report = (BlockExecutionReport) reports.get(key);
            if (report.getExecutionStatus() != BlockExecutionReport.NO_ERRORS) {
                status = WorkflowInvocationFolder.FAILED_MESSAGE;
            }
        }
        */
        notifyInvocationFinished(invocation);
    }

    /**
     * Kill a specific invocation
     */
    public void killInvocation(String invocationId) {
        logger.debug("Killing invocation. InvocationID=" + invocationId);
        WorkflowInvocation invocation = getInvocation(invocationId);
        if (invocation != null) {
            invocation.kill();
        }
    }

    /**
     * Kill all the invocations
     */
    public void killAll(boolean engineInitiated) {
        logger.debug("Killing all workflow invocations");
        ArrayList<WorkflowInvocation> killList = new ArrayList<>();
        Enumeration<WorkflowInvocation> i = invocations.elements();
        while (i.hasMoreElements()) {
            killList.add(i.nextElement());
        }

        API api = null;
        if(engineInitiated){
            try {
                api = apiProvider.createApi();
            } catch (Exception e){
                logger.error("Error notifying server of engine startup", e);
            }            
        }
        
        for (WorkflowInvocation invocation : killList) {
            invocation.kill();
            // If this is a workflow initiated kill, then tell the server so that
            // it can clean up any locks
            if(engineInitiated && api!=null){
                try {
                    logger.debug("Notifying server of engine initiated workflow kill for InvocationID=" + invocation.getInvocationId());
                    api.workflowTerminatedByEngine(invocation.getInvocationId());
                } catch (Exception e){
                    logger.error("Error notifying server of engine initiated workflow kill for InvocationID=" + invocation.getInvocationId() + ": " + e.getMessage());
                }
            }            
        }
        
        if(api!=null){
            api.terminate();
        }        

    }
    
    /**
     * Suspend all invocations
     */
    public void suspendAll(){
        logger.debug("Suspending all workflow invocations");
        ArrayList<WorkflowInvocation> killList = new ArrayList<>();
        Enumeration<WorkflowInvocation> i = invocations.elements();
        while (i.hasMoreElements()) {
            killList.add(i.nextElement());
        }

        for (WorkflowInvocation invocation : killList) {
            invocation.suspend();
        }        
    }
    /**
     * Restart all of the workflows by reading the invocation directories
     */
    public void restartWorkflows(){
        File baseDirectory = new File(globalData.getBaseDirectory());
        if(baseDirectory.exists()){
            logger.debug("Searching for unfinished invocations");
            File[] children = baseDirectory.listFiles();
            ArrayList<WorkflowInvocation> restoredInvocations = new ArrayList<>();
            WorkflowInvocation invocation;
            for(int i=0;i<children.length;i++){
                if(children[i].isDirectory()){
                    logger.debug("Attempting to recreate workflow from: " + children[i].getPath());
                    try {
                        invocation = createWorkflowInvocation(children[i]);
                        restoredInvocations.add(invocation);
                    } catch (Exception e){
                        logger.error("Error recreating workflow from: " + children[i].getPath() + ": " + e.getMessage());
                        try {
                            ZipUtils.removeDirectory(children[i]);
                        } catch (Exception ex){
                            logger.error("Error removing corrupt invocation folder: " + children[i].getPath(), ex);
                        }
                    }
                }
            }
            
            // Try and start the invocations
            for(WorkflowInvocation i : restoredInvocations){
                try {
                    logger.debug("Attempting to restart workflow invocation. InvocationID=" + i.getInvocationId());
                    startInvocation(i);
                } catch (Exception e){
                    logger.error("Error restarting invocation. InvocationID=" + i.getInvocationId());
                }
            }
        }
    }

    /** Create an APIBroker object */
    @Override
    public APIBroker createApi(Ticket ticket, String invocationId) throws RemoteException, ConnexienceException {
        try {
            WorkflowInvocation invocation = getInvocation(invocationId);
            if(invocation!=null){
                API api = invocation.getApiLink();
                api.setProvenanceProperties(null);
                APIBroker broker = new APIBrokerImpl(this, api, this);
                apiBrokers.add(broker);
                return broker;
            } else {
                throw new ConnexienceException("No invocation to create APIBroker for");
            }
        } catch (RemoteException re){
            throw re;
        } catch (ConnexienceException c){
            throw new ConnexienceException("Error creating APIBroker: " + c.getMessage(), c);
        } catch (Exception e){
            throw new ConnexienceException("Exception creating APIBroker: " + e.getMessage(), e);
        }
    }

    @Override
    public APIBroker createApi(Ticket ticket, String invocationId, XmlDataStore provenanceData) throws RemoteException, ConnexienceException {
        try {
            WorkflowInvocation invocation = getInvocation(invocationId);
            if(invocation!=null){
                API api = invocation.getApiLink();
                api.setProvenanceProperties(provenanceData);
                APIBroker broker = new APIBrokerImpl(this, api, this);
                apiBrokers.add(broker);
                return broker;
            } else {
                throw new ConnexienceException("No Invocation to create broker for");
            }
        } catch (RemoteException re){
            throw re;
        } catch (ConnexienceException c){
            throw new ConnexienceException("Error creating APIBroker: " + c.getMessage(), c);
        } catch (Exception e){
            throw new ConnexienceException("Exception creating APIBroker: " + e.getMessage(), e);
        }
    }

    @Override
    public APIBroker createApi(User user, String invocationId) throws RemoteException, ConnexienceException {
        try {
            WorkflowInvocation invocation = getInvocation(invocationId);
            API api = invocation.getApiLink();
            Ticket userTicket = api.createTicketForUser(user.getId());
            if(userTicket!=null){
                API extraApi = invocation.createAdditionalApi(userTicket);
                extraApi.setProvenanceProperties(null);
                APIBroker broker = new APIBrokerImpl(this, extraApi, this);
                apiBrokers.add(broker);
                return broker;
            } else {
                throw new Exception("No ticket returned");
            }
        
        } catch (Exception e){
            throw new ConnexienceException("Error creating additional APIBroker: " + e.getMessage());
        }
    }
    
    
    @Override
    public void releaseApiBroker(APIBroker broker) {
        apiBrokers.remove(broker);
        
        // Remove the provenance from the existing API
        ((APIBrokerImpl)broker).getApiLink().setProvenanceProperties(null);
    }
}