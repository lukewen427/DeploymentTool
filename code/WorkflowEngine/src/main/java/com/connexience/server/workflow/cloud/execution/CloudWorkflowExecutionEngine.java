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
package com.connexience.server.workflow.cloud.execution;

import com.connexience.server.model.logging.performance.WorkflowEngineInstance;
import com.connexience.server.model.logging.performance.WorkflowEngineStatusChange;
import com.connexience.server.workflow.api.ApiProvider;
import com.connexience.server.workflow.cloud.library.*;
import com.connexience.server.workflow.engine.*;
import com.connexience.server.workflow.cloud.rmi.*;
import com.connexience.server.workflow.engine.cloud.*;
import com.connexience.server.workflow.service.clients.*;
import com.connexience.server.workflow.service.*;
import com.connexience.server.workflow.cloud.download.*;
import com.connexience.server.workflow.api.*;
import com.connexience.server.model.security.*;
import com.connexience.server.model.workflow.control.*;
import com.connexience.server.model.workflow.*;
import com.connexience.server.util.SerializationUtils;
import com.connexience.server.util.XmlUtils;
import com.connexience.server.util.provenance.PerformanceLoggerClient;
import com.connexience.server.workflow.cloud.CloudWorkflowEngine;

import com.connexience.server.workflow.cloud.WorkflowJMSListener;
import com.connexience.server.workflow.cloud.library.installer.SystemManager;
import com.connexience.server.workflow.cloud.library.installer.SystemManagerFactory;
import com.connexience.server.workflow.util.SigarData;
import com.connexience.server.workflow.util.ZipUtils;
import org.pipeline.core.xmlstorage.*;

import org.w3c.dom.*;
import java.rmi.server.*;
import java.rmi.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import org.apache.log4j.*;

/**
 * this class provides a workflow engine that can either run on a server or can
 * be contained within the desktop workflow development environment.
 * @author hugo
 */
public class CloudWorkflowExecutionEngine extends UnicastRemoteObject implements IWorkflowEngine, CloudDataProcessorMessageDestination, WorkflowEngineListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CloudWorkflowExecutionEngine.class);
    /** Core execution engine */
    private WorkflowEngine engine;

    /** Top level data source for all of the workflow invocations created by
     * this workflow engine */
    private GlobalDataSource globalData;

    /** API Provider */
    private ApiProvider apiProvider = null;

    /** Service library */
    private ServiceLibrary serviceLibrary;

    /** Engine listeners */
    private ArrayList<CloudWorkflowExecutionEngineListener> listeners = new ArrayList<>();

    /** Manager to look after the individual service calls */
    private CloudServiceInvocationManager invocationManager;

    /** Maximum number of concurrent service invocations */
    private int maxConcurrentServiceInvocations = 4;

    /** Should this engine allow services to be started with debugging enabled */
    private boolean debuggingAllowed = false;

    /** ID of the service process */
    private String hostId = "";

    /** Maximum number of concurrent workflows */
    private int maxConcurrentWorkflows = 10;

    /** Start time of the engine */
    private Date startTime;
    
    /** Total workflows executed */
    private long totalWorkflowsStarted = 0;
    
    /** Total workflows succeeded */
    private long totalWorkflowsSucceeded = 0;
    
    /** Total workflows failed */
    private long totalWorkflowsFailed = 0;
    
    /** JMS message container */
    private WorkflowJMSListener jmsListener;
    
    /** Queue of messages due to be processed */
    private CopyOnWriteArrayList<WorkflowInvocationMessage> waitingMessages = new CopyOnWriteArrayList<>();

    /** Max VM size */
    private int maxVmSize = 256;

    /** PermGen size */
    private int permGenSize = 256;

    /** Run each workflow as a different user.  Currently only implemented in Linux */
    private boolean workflowSeparationEnforced = false;

    /** Map linking invocations to usernames. This is used if workflow separation is enforced */
    private InvocationUsernameManager invocationUserManager = new InvocationUsernameManager();
    
    /** Should all users processes be terminated on completion. This is only valid if separation is enforced */
    private boolean invocationUserProcessTerminationEnabled = true;
    
    /** Parent engine */
    private CloudWorkflowEngine parentEngine;
    
    public CloudWorkflowExecutionEngine(CloudWorkflowEngine parentEngine, String invocationDirectory, ServiceLibrary serviceLibrary, ApiProvider apiProvider, WorkflowJMSListener jmsListener, String hostId) throws RemoteException {
        logger.debug("Created CloudWorkflowExecutionEngine");
        startTime = new Date();
        this.globalData = new GlobalDataSource(invocationDirectory);
        this.hostId = hostId;
        this.serviceLibrary = serviceLibrary;
        this.engine = new WorkflowEngine(globalData, apiProvider, serviceLibrary);
        this.jmsListener = jmsListener;
        this.apiProvider = apiProvider;
        this.parentEngine = parentEngine;

        engine.addWorkflowEngineListener(this);

        invocationManager = new CloudServiceInvocationManager(this, maxConcurrentServiceInvocations);
        engine.setReportUsedForCommandOutput(false);    // Reports are sent straight back to the server
        // Set this to be the default message destination for cloud workflow messages
        AutoDeployDataProcessorClient.setGlobalMessageDestination(this);
        
        // Notify server that the engine has started
        notifyEngineStartup();
        
        // Search for any old messages
        loadAndRestartInvocationMessages();
        
        // Get the workflow engine to restart anything left hanging
        engine.restartWorkflows();
    }

    /** Get the JMS listener object */
    public WorkflowJMSListener getJmsListener(){
        return jmsListener;
    }

    /** Set whether users processes are terminated when an invocation finishes */
    public void setInvocationUserProcessTerminationEnabled(boolean invocationUserProcessTerminationEnabled) {
        this.invocationUserProcessTerminationEnabled = invocationUserProcessTerminationEnabled;
    }

    /** Are users processes terminated after an invocation finishes */
    public boolean isInvocationUserProcessTerminationEnabled() {
        return invocationUserProcessTerminationEnabled;
    }
    
    /** Get the engine start time */
    public Date getStartTime() {
        return startTime;
    }

    public void setMaxVmSize(int maxVmSize){
        this.maxVmSize = maxVmSize;
    }

    public int getMaxVmSize(){
        return maxVmSize;
    }

    public CloudServiceInvocationManager getInvocationManager() {
        return invocationManager;
    }

    public void setPermGenSize(int permGenSize){
        this.permGenSize = permGenSize;
    }

    public int getPermSize(){
        return permGenSize;
    }

    /** Get the maximum number of concurrent workflows */
    public int getMaxConcurrentWorkflows(){
        return maxConcurrentWorkflows;
    }

    /** Set the maximum number of concurrent workflows */
    public void setMaxConcurrentWorkflows(int maxConcurrentWorkflows){
        this.maxConcurrentWorkflows = maxConcurrentWorkflows;
        
        // Reset the username map
        invocationUserManager.setup("", maxConcurrentWorkflows);
    }
    
    /** Get the invocation username manager object */
    public InvocationUsernameManager getInvocationUserMap(){
        return invocationUserManager;
    }

    
    /** Set the unique ID of the host */
    /*
    public void setHostId(String hostId){
        this.hostId = hostId;
    }
    * */
    
    /** Get the unique ID of the host */
    public String getHostId(){
        return hostId;
    }
    
    /** Set the maximum number of concurrent service executions */
    public void setMaxConcurrentServiceInvocations(int maxConcurrentServiceInvocations){
        this.maxConcurrentServiceInvocations = maxConcurrentServiceInvocations;
        invocationManager.setMaxConcurrentInvocations(maxConcurrentServiceInvocations);
    }

    public int getMaxConcurrentServiceInvocations() {
        return maxConcurrentServiceInvocations;
    }

    /** Add a listener */
    public void addCloudWorkflowExecutionEngineListner(CloudWorkflowExecutionEngineListener listener){
        listeners.add(listener);
    }

    /** Remove a listener */
    public void removeCloudWorkflowExecutionEngineListner(CloudWorkflowExecutionEngineListener listener){
        listeners.remove(listener);
    }

    /** Notify the server that this engine has started */
    public void notifyEngineStartup(){
        try {
            logger.debug("Notifying engine startup for ID: " + hostId);
            API api = apiProvider.createApi();
            api.notifyEngineStartupAsync(hostId);
            api.terminate();
        } catch (Exception e){
            logger.error("Error notifying server of engine startup: " + e.getMessage());
        }
        
        // Log startup to performance server
        PerformanceLoggerClient client = new PerformanceLoggerClient();
        WorkflowEngineStatusChange msg = new WorkflowEngineStatusChange();
        msg.setIpAddress(parentEngine.getServerIp());
        msg.setStatus(WorkflowEngineInstance.ENGINE_RUNNING);
        
        // Add in the extra engine properties if available
        if(SigarData.SYSTEM_DATA.isAvailable()){
            msg.getExtraProperties().put(WorkflowEngineStatusChange.PROPERTY_CPU_COUNT, SigarData.SYSTEM_DATA.getCpuCount());
            msg.getExtraProperties().put(WorkflowEngineStatusChange.PROPERTY_CPU_SPEED, SigarData.SYSTEM_DATA.getAverageCpuSpeed());
            msg.getExtraProperties().put(WorkflowEngineStatusChange.PROPERTY_ARCHITECTURE, SigarData.SYSTEM_DATA.getArchitecture());
            msg.getExtraProperties().put(WorkflowEngineStatusChange.PROPERTY_PHYSICAL_RAM, SigarData.SYSTEM_DATA.getPhysicalRam());
            msg.getExtraProperties().put(WorkflowEngineStatusChange.PROPERTY_OPERATING_SYSTEM, SigarData.SYSTEM_DATA.getOperatingSystem());
        }
        client.log(msg);

    }
    
    /** Notify the server that this engine has terminated */
    private void notifyEngineShutdown(){
        try {
            logger.debug("Notifying engine shutdown for ID: " + hostId);
            API api = apiProvider.createApi();
            api.notifyEngineShutdownAsync(hostId);
            api.terminate();
        } catch (Exception e){
            logger.error("Error notifying server of engine startup: " + e.getMessage());
        }
        
        // Log shutdown to performance server
        PerformanceLoggerClient client = new PerformanceLoggerClient();
        WorkflowEngineStatusChange msg = new WorkflowEngineStatusChange();
        msg.setIpAddress(parentEngine.getServerIp());
        msg.setStatus(WorkflowEngineInstance.ENGINE_STOPPED);
        client.log(msg);        
    }
    
    /** Notify listeners that a shutdown signal has been received */
    private void notifyEngineShutdownSignalReceived(boolean interactive){
        for(int i=0;i<listeners.size();i++){
            listeners.get(i).engineShutdownSignalReceived(this, interactive);
        }
    }

    /** Notify listeners that an invocation has started */
    private void notifyInvocationStarted(WorkflowInvocation invocation){
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

    /** An invocation has finished */
    public void invocationFinished(WorkflowInvocation invocation) {
        if(invocation.isInvocationFailed()){
            totalWorkflowsFailed++;
        } else {
            totalWorkflowsSucceeded++;
        }
        
        // Set the permissions back on the data directory if the workflow
        // was running as a different user
        if(invocation.isExecuteAsDifferentUser()){
            // Kill all user processes if enabled
            if(invocationUserProcessTerminationEnabled){
                try {
                    logger.debug("Killing all processes for: " + invocation.getSystemUsername());
                    SystemManager mgr = SystemManagerFactory.newInstance();
                    mgr.killAllUserTasks(invocation.getSystemUsername());
                } catch (Exception e){
                    logger.error("Error getting system manager object: " + e.getMessage());
                }                        
            }
            
            // Return the username to the pool
            invocationUserManager.releaseUsername(invocation.getInvocationId());
        }
        
        
        notifyInvocationFinished(invocation);
    }

    /** An invocation has started */
    public void invocationStarted(WorkflowInvocation invocation) {
        logger.debug("Workflow Invocation Startup Complete. InvocationID=" + invocation.getInvocationId());
        notifyInvocationStarted(invocation);
        deleteWorkflowInvocationMessage(invocation.getInvocationId());
    }

    /** Shutdown this engine */
    public void shutdown() throws RemoteException {
        logger.debug("Shutting down CloudWorkflowExecutionEngine");

        // Kill all of the outstanding workflow instances
        engine.killAll(true);

        // Notify the server
        notifyEngineShutdown();
        
        // Notify listeners
        notifyEngineShutdownSignalReceived(false);
    }
    
    public void interactiveShutdown(){
        logger.debug("Shutting down CloudWorkflowExecutionEngine in response to keyboard command");
        
        // Disconnect the parent from JMS
        if(parentEngine!=null){
            logger.debug("Asking parent engine to detach JMS");
            parentEngine.detachJms();
        } else {
            logger.debug("No parent engine set for JMS detach request");
        }
        
        // Kill all of the outstanding workflow instances
        engine.killAll(true);

        // Notify the server
        notifyEngineShutdown();
        
        // Notify listeners
        notifyEngineShutdownSignalReceived(true);        
    }

    /** Get the actual execution engine */
    public WorkflowEngine getExecutionEngine(){
        return engine;
    }

    /** Get the service library */
    public ServiceLibrary getServiceLibrary(){
        return serviceLibrary;
    }

    /** Get the API provider */
    public ApiProvider getApiProvider(){
        return apiProvider;
    }
    
    /** Open a control connection to this workflow engine */
    public IWorkflowEngineControl openControlConnection(Ticket ticket) throws RemoteException {
        return new WorkflowEngineControlImpl(this, ticket);
    }

    /** Open a non authenticated control connection */
    public IWorkflowEngineControl openControlConnection() throws RemoteException {
        return new WorkflowEngineControlImpl(this);
    }

    /** Terminate an invocation */
    public void terminate(DataProcessorCallMessage message) throws DataProcessorException {
        invocationManager.terminate(message);
    }

    /** Process a data processor call message. This will kick of a service deploy if
     * needed */
    public boolean postCallMessage(DataProcessorCallMessage message) throws DataProcessorException {
        // Delegate this call message straight to the service invocation manager
        return invocationManager.postCallMessage(message);
    }

    /** Post a data processor response message. */
    public boolean postResponseMessage(DataProcessorResponseMessage message) throws DataProcessorException {
        logger.debug("Service completion response message received by cloud workflow execution engine. InvocationID=" + message.getInvocationId() + " BlockID=" + message.getContextId());
        engine.processResponseMessage(message);
        return true;
    }

    /** Get the service invocation manager that deals with indivdual service runs */
    public CloudServiceInvocationManager getServiceInvocationManager(){
        return invocationManager;
    }

    public long getTotalWorkflowsFailed() {
        return totalWorkflowsFailed;
    }

    public void setTotalWorkflowsFailed(long totalWorkflowsFailed) {
        this.totalWorkflowsFailed = totalWorkflowsFailed;
    }

    public long getTotalWorkflowsStarted() {
        return totalWorkflowsStarted;
    }

    public void setTotalWorkflowsStarted(long totalWorkflowsStarted) {
        this.totalWorkflowsStarted = totalWorkflowsStarted;
    }

    public long getTotalWorkflowsSucceeded() {
        return totalWorkflowsSucceeded;
    }

    public void setTotalWorkflowsSucceeded(long totalWorkflowsSucceeded) {
        this.totalWorkflowsSucceeded = totalWorkflowsSucceeded;
    }

    public boolean isDebuggingAllowed() {
        return debuggingAllowed;
    }

    public void setDebuggingAllowed(boolean debuggingAllowed) {
        this.debuggingAllowed = debuggingAllowed;
    }

    /** Get the number of outstanding messages. These are messages that have been accepted but not yet initialised by the engine */
    public int getOutstandingMessageCount(){
        return waitingMessages.size();
    }

    /** Get the number of total messages + running workflows */
    public int getJobQueueSize(){
        return engine.getActiveInvocationCount() + waitingMessages.size();
    }

    public boolean isWorkflowSeparationEnforced() {
        return workflowSeparationEnforced;
    }

    public void setWorkflowSeparationEnforced(boolean workflowSeparationEnforced) {
        this.workflowSeparationEnforced = workflowSeparationEnforced;
    }


    /** Get a status data snapshot */
    public WorkflowEngineStatusData getEngineStatus(){
        WorkflowEngineStatusData status = new WorkflowEngineStatusData();
        status.setDiskSize(getServiceLibrary().getLibraryDirectory().getTotalSpace());
        status.setFreeSpace(getServiceLibrary().getLibraryDirectory().getUsableSpace());
        status.setEngineStartTime(getStartTime());
        status.setTotalWorkflowsFailed(getTotalWorkflowsFailed());
        status.setTotalWorkflowsStarted(getTotalWorkflowsStarted());
        status.setTotalWorkflowsSucceeded(getTotalWorkflowsSucceeded());
        status.setWorkflowCount(getJobQueueSize());
        status.setWorkflowCapacity(getMaxConcurrentWorkflows());
        return status;        
    }
    
    /** Start a workflow invocation */
    public void startWorkflow(final WorkflowInvocationMessage message){
        logger.debug("Workflow start message recieved. IncocationID = " + message.getInvocationId());
        saveWorkflowInvocationMessage(message);
        waitingMessages.add(message);
        
        new Thread(new Runnable(){
            public void run(){
                // Download and start an invocation
                totalWorkflowsStarted++;
                API apiLink = null;
                try {
                    apiLink = apiProvider.createApi(message.getTicket());
                } catch (Exception e){
                    logger.error("Error creating API link: " + e.getMessage());
                }
                                
                if(apiLink!=null){
                    try {

                        try {
                            apiLink.logWorkflowDequeuedAsync(message.getInvocationId());
                        } catch (Exception e) {
                            logger.error("Error sending Invocation status ", e);
                        }

                        // Check to see that the invocation has not been marked in an error condition. If if
                        // has, then this means that the server has decided not to execute the workflow after
                        // the message has been queued
                        WorkflowInvocationFolder invocationFolder = apiLink.getWorkflowInvocation(message.getInvocationId());
                        if(invocationFolder==null){
                            throw new Exception("Workflow invocation no longer exists");
                        }
                        
                        if(invocationFolder.getInvocationStatus()==WorkflowInvocationFolder.INVOCATION_FINISHED_WITH_ERRORS){
                            throw new Exception("Workflow execution cancelled");
                        }
                        
                        WorkflowDataFetcher fetcher;

                        if(message.isUseLatest()){
                            fetcher = new WorkflowDataFetcher(message.getWorkflowId(), apiLink);
                        } else {
                            fetcher = new WorkflowDataFetcher(message.getWorkflowId(), message.getVersionId(), apiLink);
                        }
                        XmlDataStore workflowData = fetcher.download();
                        WorkflowInvocation invocation = engine.createWorkflowInvocation(workflowData, message.getTicket(), message.getInvocationId());
                        
                        // Save the name in the invocation
                        if(fetcher.getWorkflowDocument()!=null){
                            invocation.setWorkflowName(fetcher.getWorkflowDocument().getName());
                        } else {
                            invocation.setWorkflowName("Unknown");
                        }
                        
                        // Set the XML parameter replacements in the invocation
                        if(message.getParameterXmlData()!=null){
                            invocation.parseReplacementParameterXmlData(message.getParameterXmlData());
                            invocation.replaceWorkflowParameters(); // Do parameter replacement
                        } else {
                            invocation.clearReplacementParameters();
                        }

                        // Use the library to fetch service xml documents
                        invocation.setServiceFetcher(new ExecutionEngineServiceFetcher(apiLink));

                        // Results storage options
                        invocation.setDeletedOnSuccess(message.isDeletedOnSuccess());
                        invocation.setOnlyFailedOutputsUploaded(message.isOnlyFailedOutputsUploaded());

                        // VM option
                        invocation.setSingleVmMode(message.isSingleVmMode());
                        
                        // Set the input document if required
                        if(message.getTargetFileId()!=null){
                            invocation.setInputBlockName(message.getInputBlockName());
                            invocation.setTargetFileId(message.getTargetFileId());
                        }
                        invocation.setWorkflowId(message.getWorkflowId());
                        invocation.setVersionId(message.getVersionId());
                        invocation.setEngineId(hostId);
                        
                        // Set the security properties of the invocation
                        if(workflowSeparationEnforced){
                            invocation.setExecuteAsDifferentUser(true);
                            String invocationUsername = invocationUserManager.reserveUsername(invocation.getInvocationId());
                            invocation.setSystemUsername(invocationUsername);
                            invocation.setWorkflowEngineGroupname(invocationUserManager.getEngineGroupName());
                        }
                        
                        logger.debug("Starting invocation in WorkflowEngine. InvocationID=" + invocation.getInvocationId());
                        engine.startInvocation(invocation);
                        
                    } catch (Exception e){
                        // TODO: NEED TO SEND THE ERROR MESSAGE TO THE WORKFLOW SERVER
                        logger.error("Exception starting execution. InvocationID=" + message.getInvocationId() + ": " + e.getMessage());
                        deleteWorkflowInvocationMessage(message.getInvocationId());
                        try {
                            WorkflowInvocationFolder folder = apiLink.getWorkflowInvocation(message.getInvocationId());
                            if(folder!=null){
                                folder.setMessage("Error starting workflow in CloudExecutionEngine: " + e.getMessage());
                                folder.setInvocationStatus(WorkflowInvocationFolder.INVOCATION_FINISHED_WITH_ERRORS);
                                apiLink.saveWorkflowInvocation(folder);
                            } else {
                                throw new Exception("Workflow invocation no longer exists");
                            }
                        } catch (Exception ex2){
                            logger.error("Error sending error code back to server: " + ex2.getMessage() + ". InvocationID=" + message.getInvocationId());
                        }

                    }
                }
                waitingMessages.remove(message);
            }            
        }, "CWEE: " + message.getInvocationId()).start();
    }

    public void saveWorkflowInvocationMessage(WorkflowInvocationMessage message){
        try {
            File workingDir = new File(engine.getDataSource().getBaseDirectory());
            File msgFile = new File(workingDir, message.getInvocationId() + ".msg");
            SerializationUtils.serialize(message, msgFile);
        } catch(Exception e){
            logger.error("Error saving WorkflowInvocationMessage: " + e.getMessage());
        }
    }
    
    public void deleteWorkflowInvocationMessage(String invocationId){
        try {
            File workingDir = new File(engine.getDataSource().getBaseDirectory());
            File msgFile = new File(workingDir, invocationId + ".msg");
            msgFile.delete();
        } catch(Exception e){
            logger.error("Error deleting WorkflowInvocationMessage: " + e.getMessage());
        }
    }
    
    public final void loadAndRestartInvocationMessages(){
        File workingDir = new File(engine.getDataSource().getBaseDirectory());
        if(workingDir.exists()){
            File[] contents = workingDir.listFiles();
            WorkflowInvocationMessage msg;
            
            ArrayList<WorkflowInvocationMessage> messages = new ArrayList<>();
            for(int i=0;i<contents.length;i++){
                try {
                    if(contents[i].isFile() && contents[i].getName().endsWith(".msg")){
                        msg = (WorkflowInvocationMessage)SerializationUtils.deserialize(contents[i]);
                        messages.add(msg);
                    }
                } catch (Exception e){
                    logger.error("Cannot reload invocation message: " + contents[i].getName() + ": " + e.getMessage());
                }
            }
            
            // Now start the workflows running
            File invocationDirectory;
            for(int i=0;i<messages.size();i++){
                msg = messages.get(i);
                invocationDirectory = new File(workingDir, msg.getInvocationId());
                if(invocationDirectory.isDirectory() && invocationDirectory.exists()){
                    // Need to remove old invocation directory
                    logger.debug("Removing old invocation directory because an invocation message is still present");
                    try {
                        ZipUtils.removeDirectory(invocationDirectory);
                    } catch (Exception e){
                        logger.error("Error removing old invocation directory: " + e.getMessage());
                    }
                }
                
                startWorkflow(msg);
            }
        } else {
            logger.error("Workflow invocaiton directory not found when reloading invocation messages");
        }
            
    }
    public class ExecutionEngineServiceFetcher implements DataProcessorServiceFetcher{
        private API apiLink;

        public ExecutionEngineServiceFetcher(API apiLink) {
            this.apiLink = apiLink;
        }
        
        
        /** Get the latest version of a service definition */
        public DataProcessorServiceDefinition getServiceDefinition(String serviceId) throws DataProcessorException {
            try {
                logger.debug("Warning: Going to server for service definition: " + serviceId);
                return apiLink.getService(serviceId);
            } catch (Exception e) {
                logger.error("Error getting service definition. ServiceID=" + serviceId, e);
                throw new DataProcessorException("Error getting service defintion: " + e.getMessage() + ": " + e.getMessage());
            }
        }

        /** Get a specific version of a service definition */
        public DataProcessorServiceDefinition getServiceDefinition(String serviceId, String versionId) throws DataProcessorException {
            try {
                CloudWorkflowServiceLibraryItem item = serviceLibrary.locateServiceItem(serviceId, versionId);
                if(item!=null){
                    File xmlFile = item.getFile("/service.xml");
                    if(xmlFile.exists()){
                        try {
                            DataProcessorServiceDefinition def = new DataProcessorServiceDefinition();
                            Document doc = XmlUtils.readXmlDocumentFromFile(xmlFile);
                            def.loadXmlDocument(doc);
                            return def;
                        } catch (Exception e){
                            logger.error("Warning: cannot parse service.xml from library: " + e.getMessage());
                            return apiLink.getService(serviceId, versionId);
                        }
                        
                    } else {
                        logger.debug("Warning: service.xml not found in library. Going to server for definition");
                        return apiLink.getService(serviceId, versionId);
                    }
                } else {
                    logger.debug("GetServiceDefinition: item not yet in library: " + serviceId + " & " + versionId);
                    return apiLink.getService(serviceId, versionId);
                }
            } catch (Exception e) {
                logger.error("Error getting service definition. ServiceID=" + serviceId, e);
                throw new DataProcessorException("Error getting service defintion: " + e.getMessage());
            }
        }
    }
}

