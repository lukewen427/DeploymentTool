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

import com.connexience.server.workflow.blocks.processor.*;
import com.connexience.server.workflow.service.*;
import com.connexience.server.workflow.api.*;
import com.connexience.server.workflow.engine.parameters.*;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.document.*;
import com.connexience.server.model.workflow.*;
import com.connexience.server.model.workflow.notification.WorkflowLock;
import com.connexience.server.util.RegistryUtil;
import com.connexience.server.util.SerializationUtils;

import com.connexience.server.workflow.api.ApiProvider;
import com.connexience.server.workflow.cloud.CloudWorkflowEngine;
import com.connexience.server.workflow.cloud.execution.runners.server.SingleVMServerProcessContainer;
import com.connexience.server.workflow.service.DataProcessorClientListener;
import org.pipeline.core.drawing.*;
import org.pipeline.core.drawing.spanning.*;
import org.pipeline.core.drawing.model.*;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.io.*;
import java.util.*;
import java.io.*;
import org.apache.log4j.*;


/**
 * This class manages the invocation of a Drawing object. It looks after the
 * persistence of data etc.
 * @author hugo
 */
public class WorkflowInvocation implements DrawingExecutionListener, Serializable, DataProcessorServiceFetcher, Runnable, XmlStorable {
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(WorkflowInvocation.class);
    
    /** Drawing being executed */
    private transient DrawingModel drawing = null;
    
    /** Drawing data */
    private XmlDataStore drawingData = null;
    
    /** Execution engine */
    private transient DrawingExecutionProcessor executor = null;
    
    /** Temporary data source */
    private InvocationDataSource dataSource;
    
    /** Listeners */
    private Vector<WorkflowInvocationListener> listeners = new Vector<>();
    
    /** ID of this invocation */
    private String invocationId;
    
    /** ID of the workflow document */
    private String workflowId;
    
    /** ID of the workflow version */
    private String versionId;
    
    /** Invocation PID. This is used by the command line tools to provide an easy way
     * of manipulating invocations */
    private long pid = 0;
    
    /** Block Execution Reports */
    private Hashtable executionReports = new Hashtable();
    
    /** Parent workflow engine */
    private WorkflowEngine parent = null;
    
    /** Security ticket of calling user */
    private Ticket ticket = null;
    
    /** ID of the data file to process using the workflow */
    private String targetFileId = null;
    
    /** Name of the data target block */
    private String inputBlockName = null;
    
    /** Message plan for this invocation */
    private WorkflowInvocationMessagePlan messagePlan = new WorkflowInvocationMessagePlan();
    
    /** Currently running message item */
    private volatile WorkflowInvocationMessagePlan.MessageItem currentItem = null;
    
    /** List of invocation properties. These are used to store things like API clients as a workflow runs */
    private Hashtable<String, Object> invocationProperties = new Hashtable<>();

    private ApiProvider apiProvider = null;
    
    /** Has this invocation been killed */
    private boolean killFlag = false;

    /** Time this invocation was started */
    private Date startTime = null;

    /** Is this invocation running */
    private boolean running = false;
    
    /** Should the results data be stored in the workflow execution report. If this is false, the results
     * will be posted back directly to the database */
    private boolean reportUsedForCommandOutput = true;
    
    /** Parameter replacements */
    private XmlDataStore parameterReplacements = null;
    
    /** Workflow service EngineID */
    private String engineId = null;
    
    /** Is this invocation waiting for a lock */
    private boolean waitingForLock = false;
    
    /** ID of the lock waiting for */
    private long lockId = 0;
    
    /** Should the invocation be deleted if successful */
    private boolean deletedOnSuccess = false;
    
    /** Only upload output data for failed blocks */
    private boolean onlyFailedOutputsUploaded = false;
    
    /** Does this workflow operate in single VM mode */
    private boolean singleVmMode = true;

    /** Service fetcher object */
    private DataProcessorServiceFetcher serviceFetcher = null;
    
    /** Was there an error during invocation */
    private boolean invocationFailed = false;
    
    /** Flag set when the workflow is being restarted after being suspended */
    private boolean resumeFlag = false;
    
    /** Flag set when a workflow is being suspended */
    private boolean suspendFlag = false;
    
    /** Name of the workflow */
    private String workflowName;
    
    /** API Connection */
    private API apiLink = null;
    
    /** List of additional API links created for other users */
    private ArrayList<API> additionalApiLinks = new ArrayList<>();
    
    /** Should services in this invocation be executed as a different user to the engine */
    private boolean executeAsDifferentUser = false;
    
    /** Username to use for service invocations */
    private String systemUsername = "";
    
    /** Group name for workflow engine */
    private String workflowEngineGroupname = "";
    
    /** External server process for running services in a single VM */
    private SingleVMServerProcessContainer serverProcess = null;
    
    /** Create an invocation by loading a drawing from storage */
    public WorkflowInvocation(XmlDataStore drawingData, Ticket ticket, ApiProvider apiProvider, String invocationId) throws WorkflowInvocationException {
        drawing = new DefaultDrawingModel();
        this.drawingData = drawingData;
        this.ticket = ticket;
        this.invocationId = invocationId;
        serviceFetcher = this;
        try {

            ((DefaultDrawingModel) drawing).recreateObject(drawingData);
        } catch (XmlStorageException xmlse) {
            logger.error("Error parsing workflow data. InvocationID=" + invocationId, xmlse);
            throw new WorkflowInvocationException("Error reading drawing data");
        }
        executor = new DrawingExecutionProcessor(drawing);
        executor.addDrawingExecutionListener(this);

        this.apiProvider = apiProvider;
        try {
            apiLink = apiProvider.createApi(ticket);
        } catch (Exception e){
            throw new WorkflowInvocationException("Error creating workflow API: " + e.getMessage(), e);
        }
    }

    /** Create a workflow invocation by loading state from a workflow invocation directory */
    public WorkflowInvocation(ApiProvider apiProvider, File invocationDir) throws WorkflowInvocationException {
        // Load the workflow data file
        try {
            loadInvocationData(invocationDir);
        } catch (Exception e){
            throw new WorkflowInvocationException("Error loading invocation data: " + e.getMessage(), e);
        }
        
        this.apiProvider = apiProvider;
        try {
            apiLink = apiProvider.createApi(ticket);
        } catch (Exception e){
            throw new WorkflowInvocationException("Error creating workflow API: " + e.getMessage(), e);
        }

        // Set up the drawing and all of the execution processors
        drawing = new DefaultDrawingModel();
        serviceFetcher = this;
        try {
            ((DefaultDrawingModel) drawing).recreateObject(drawingData);
        } catch (XmlStorageException xmlse) {
            logger.error("Error parsing workflow data. InvocationID=" + invocationId, xmlse);
            throw new WorkflowInvocationException("Error reading drawing data");
        }
        executor = new DrawingExecutionProcessor(drawing);
        executor.addDrawingExecutionListener(this);        
        
        // Set the resume flag
        resumeFlag = true;
    }

    public boolean isSingleVmMode() {
        return singleVmMode;
    }

    public void setSingleVmMode(boolean singleVmMode) {
        this.singleVmMode = singleVmMode;
    }

    public void setWorkflowEngineGroupname(String workflowEngineGroupname) {
        this.workflowEngineGroupname = workflowEngineGroupname;
    }

    public String getWorkflowEngineGroupname() {
        return workflowEngineGroupname;
    }

    public void setExecuteAsDifferentUser(boolean executeAsDifferentUser) {
        this.executeAsDifferentUser = executeAsDifferentUser;
    }

    public boolean isExecuteAsDifferentUser() {
        return executeAsDifferentUser;
    }

    public void setSystemUsername(String systemUsername) {
        this.systemUsername = systemUsername;
    }

    public String getSystemUsername() {
        return systemUsername;
    }
    
    private synchronized API createApi()  {
        if(apiLink!=null){
            return apiLink;
        } else {
            try {
                apiLink = apiProvider.createApi(ticket);
                return apiLink;
            } catch (Exception e){
                return null;
            }
        }
    }
    
    public API createAdditionalApi(Ticket ticket) {
        try {
            API api = apiProvider.createApi(ticket);
            additionalApiLinks.add(api);
            return api;
        } catch (Exception e){
            return null;
        }
    }
    
    public InvocationDataSource getDataSource(){
        return dataSource;
    }
    
    public boolean isInvocationFailed() {
        return invocationFailed;
    }

    public void setServiceFetcher(DataProcessorServiceFetcher serviceFetcher) {
        this.serviceFetcher = serviceFetcher;
    }

    public void setDeletedOnSuccess(boolean deletedOnSuccess) {
        this.deletedOnSuccess = deletedOnSuccess;
    }

    public boolean isDeletedOnSuccess() {
        return deletedOnSuccess;
    }

    public void setOnlyFailedOutputsUploaded(boolean onlyFailedOutputsUploaded) {
        this.onlyFailedOutputsUploaded = onlyFailedOutputsUploaded;
    }

    public boolean isOnlyFailedOutputsUploaded() {
        return onlyFailedOutputsUploaded;
    }

    /** This sets the ID of the service host containing the engine. If this
     * property is present, the engine will make a registration based upon
     * host ID as opposed to host IP. Otherwise an old style IP registration 
     * will be made.
     */
    public void setEngineId(String engineId) {
        this.engineId = engineId;
    }

    /** Get the workflow PID number */
    public long getPid() {
        return pid;
    }

    /** Set the workflow PID number */
    public void setPid(long pid) {
        this.pid = pid;
    }

    /** Get the ID of the workflow document */
    public String getWorkflowId() {
        return workflowId;
    }

    /** Set the ID of the workflow document */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    /** Set the ID of the workflow version */
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    /** Set an invocation property */
    public void setInvocationProperty(String name, Object value) {
        invocationProperties.put(name, value);
    }

    /** Get an invocation property */
    public Object getInvocationProperty(String name) {
        return invocationProperties.get(name);
    }

    /** Clear an invocation property */
    public void clearInvocationProperty(String name) {
        invocationProperties.remove(name);
    }

    /** Set the name of the block that will be used to receive the data */
    public void setInputBlockName(String inputBlockName) {
        this.inputBlockName = inputBlockName;
    }

    /** Set the ID of the target file. This file will be passed to the specfied
     * input data block using the attribute name Source. It is the responsibility
     * of the workflow to check that the data type is correct */
    public void setTargetFileId(String targetFileId) {
        this.targetFileId = targetFileId;
    }

    /** Get the security ticket */
    public Ticket getTicket() {
        return ticket;
    }

    /** Get the API link object */
    public API getApiLink(){
        return apiLink;
    }
    
    /** Get the drawing object */
    public DrawingModel getDrawing() {
        return drawing;
    }

    /** Get the drawing data */
    public XmlDataStore getDrawingData() {
        return drawingData;
    }

    /** Set the parent workflow engine */
    public void setParent(WorkflowEngine parent) {
        this.parent = parent;
        this.reportUsedForCommandOutput = parent.isReportUsedForCommandOutput();    // Set output properties
    }

    /** Get the invocation ID */
    public String getInvocationId() {
        return invocationId;
    }

    /** Set the invocation ID */
    public void setInvocationId(String invocationId) {
        this.invocationId = invocationId;
    }

    /** Add a listener */
    public void addWorkflowInvocationListener(WorkflowInvocationListener listener) {
        listeners.add(listener);
    }

    /** Remove a listener */
    public void removeWorkflowInvocationListener(WorkflowInvocationListener listener) {
        listeners.remove(listener);
    }

    /** Notify listeners that the execution has finished */
    private void notifyFinished() {
        Iterator<WorkflowInvocationListener> i = listeners.iterator();
        while (i.hasNext()) {
            i.next().executionFinished(this);
        }
    }

    /** Set the data source */
    public void setDataSource(InvocationDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** Get the time that this invocation started */
    public Date getStartTime() {
        return startTime;
    }

    /** Is the XML workflow report used to contain the output stream from the service */
    public boolean isReportUsedForCommandOutput() {
        return reportUsedForCommandOutput;
    }

    /** Is this invocation currenly waiting for a lock */
    public boolean isWatitingForLock() {
        return waitingForLock;
    }

    /** Is the XML workflow report used to contain the output stream from the service */
    public void setReportUsedForCommandOutput(boolean reportUsedForCommandOutput) {
        this.reportUsedForCommandOutput = reportUsedForCommandOutput;
    }

    public void run(){
        logger.debug("Starting workflow invocation thread. InvocationID=" + invocationId);
        // Set the invocation ID and data source in all of the blocks
        startTime = new Date();
        running = true;
        Enumeration blocks = drawing.blocks();
        Enumeration ports;
        PortModel port;
        
        // Save the invocation data
        saveInvocationData();

        parent.notifyInvocationStarted(this);
        
        BlockModel block;
        String serviceVersionId;
        DocumentRecord serviceDoc;
        while (blocks.hasMoreElements()) {
            block = (BlockModel) blocks.nextElement();
            // Set the invocation ID
            if (block instanceof DataProcessorBlock) {
                DataProcessorBlock dpb = (DataProcessorBlock)block;     
                dpb.setInvocationId(invocationId);
                dpb.setGlobalDataStore(dataSource.getParent());
                dpb.setTicket(ticket);
                dpb.setMessagePlan(messagePlan);
                dpb.setServiceFetcher(serviceFetcher);
                dpb.setWorkflowId(workflowId);
                dpb.setWorkflowVersionId(versionId);
                dpb.setRunAsDifferentUser(executeAsDifferentUser);
                dpb.setSystemUsername(systemUsername);
                
                // Get the latest version ID
                if(dpb.getUsesLatest()){
                    try {
                        serviceDoc = new DocumentRecord();
                        serviceDoc.setId(dpb.getServiceId());
                        serviceVersionId = parent.getLibrary().getLatestDocumentVersionId(serviceDoc, createApi());
                        if(serviceVersionId!=null){
                            dpb.setVersionId(serviceVersionId);
                            dpb.setUsesLatest(false);
                        }
                    } catch  (Exception e){
                        logger.error("Could not get latest version of service from cache: " + e.getMessage() + " InvocationID=" + invocationId);
                        running = false;
                        try {
                            createApi().logWorkflowCompleteAsync(getInvocationId(), WorkflowInvocationFolder.FAILED_MESSAGE);
                        } catch (Exception apie) {
                            logger.error("Error sending workflow status back to server. InvocationID=" + invocationId, apie);
                        }
                        finishExecution(true, "Could not download service code");
                        return;                        
                    }
                }
            }
        }

        // Set up the input data file if there is one and the drawing has
        // said that it can process an input data file
        if (targetFileId != null && inputBlockName != null) {
            block = getBlock(inputBlockName);
            if (block instanceof DefaultBlockModel) {
                DefaultBlockModel dbm = (DefaultBlockModel) block;
                if (dbm.getEditableProperties().propertyExists("Source")) {
                    try {
                        DocumentRecord sourceRecord = createApi().getDocument(targetFileId);
                        dbm.getEditableProperties().add("Source", sourceRecord);
                    } catch (Exception ce) {
                        running = false;
                        try {
                            createApi().logWorkflowCompleteAsync(getInvocationId(), WorkflowInvocationFolder.FAILED_MESSAGE);
                        } catch (Exception apie) {
                            logger.error("Error sending workflow status back to server. InvocationID=" + invocationId, apie);
                        }
                        logger.error("Error setting workflow input data details. InvocationID=" + invocationId, ce);
                        finishExecution(true, "Error setting workflow input data details: " + ce.getMessage());
                        return;
                    }

                } else {
                    try {
                        createApi().logWorkflowCompleteAsync(getInvocationId(), WorkflowInvocationFolder.FAILED_MESSAGE);
                    } catch (Exception apie) {
                        logger.error("Error sending workflow status back to server. InvocationID=" + invocationId, apie);
                    }
                    running = false;
                    finishExecution(true, "No suitable block property for input data on block: " + inputBlockName);
                    return;
                }
            }
        }

        // Save the drawing data into the invocation folder
//        WorkflowInvocationFolder folder = null;
        try {
            // Update the drawing data to reflect changes to the block data
            drawingData = ((DefaultDrawingModel) drawing).storeObject();
            createApi().setWorkflowStatus(invocationId, WorkflowInvocationFolder.INVOCATION_RUNNING, "");
            
            // Set the workflow engine running this invocation
            createApi().setInvocationEngineId(invocationId, engineId);            

        } catch (Exception e) {
            logger.error("Error starting and initialising the workflow data on the server. InvocationID=" + invocationId);
            try {
                createApi().logWorkflowCompleteAsync(getInvocationId(), WorkflowInvocationFolder.FAILED_MESSAGE);
            } catch (Exception apie) {
                logger.error("Error sending workflow status back to server. InvocationID=" + invocationId);
            }
            running = false;
            finishExecution(true, "Exception sending workflow information to server: " + e.getMessage());
            return;
        }
        
        // Run the drawing
        try {
            messagePlan.clear();

            // Builds a mesage plan. When this has finished messages will start being sent
            logger.debug("Running workflow to build message plan. InvocationID=" + invocationId);
            executor.executeFromAllSourceBlocks();

        } catch (Exception e) {
            logger.error("Error creating workflow message plan. InvocationID=" + invocationId);
            try {
                createApi().logWorkflowCompleteAsync(getInvocationId(), WorkflowInvocationFolder.FAILED_MESSAGE);
            } catch (Exception apie) {
                logger.error("Error sending worklfow status back to server. InvocationID=" + invocationId);
            }
            finishExecution(true, "Error setting up drawing for execution: " + e.getMessage());
            return;
        }        
    }
    
    /** start executing the drawing */
    public void start() {
        killFlag = false;
        new Thread(this).run();
    }

    /** Send the next message in the message plan. Returns true if a message was sent, or
     * false if there are no more messages left to send */
    private boolean sendNextMessage() {
        final WorkflowInvocationMessagePlan.MessageItem item = messagePlan.pop();
        if (item != null && killFlag == false) {
            // Send the message
            currentItem = item;
            saveInvocationState();
            
            // Add a listener to the client the process the message response
            item.getClient().addListener(new DataProcessorClientListener() {

                /** Server accepted the message */
                public void messageRecieved() {
                    item.setStatus(WorkflowInvocationMessagePlan.MESSAGE_RECEIVED);
                    item.getClient().removeListener(this);
                }

                /** Server would not process the message */
                public void messageRejected(String errorMessage) {
                    item.setStatus(WorkflowInvocationMessagePlan.MESSAGE_REJECTED);
                    item.getClient().removeListener(this);
                    currentItem = null;
                }
            });

            try {
                int percent;
                if(messagePlan.getMessageCount()>0){
                    percent = (int)(((double)(messagePlan.positionOfBlock(item.getMessage().getContextId()) + 1) / (double)messagePlan.getMessageCount()) * 100.0);
                } else {
                    percent = 0;
                }
                apiLink.setCurrentBlockAsync(invocationId, item.getMessage().getContextId(), percent);
            } catch (Exception e){
                logger.error("Error sending current block ID. InvocationID=" + item.getMessage().getInvocationId(), e);
            }
            
            try {
                logger.debug("Sending invocation message. InvocationID=" + item.getMessage().getInvocationId() + " BlockID=" + item.getMessage().getContextId());
                item.getClient().invoke(item.getMessage());
                item.setStatus(WorkflowInvocationMessagePlan.MESSAGE_SENT);
                
            } catch (Exception e) {
                logger.error("Error sending invocation message. InvocationID=" + item.getMessage().getInvocationId() + ": " + e.getMessage());
                item.setStatus(WorkflowInvocationMessagePlan.MESSAGE_TRANSMISSION_ERROR);
                return false;
            }

            return true;

        } else {
            if (killFlag == true) {
                try {
                    apiLink.logWorkflowCompleteAsync(getInvocationId(), WorkflowInvocationFolder.KILLED_MESSAGE);
                } catch (Exception apie) {
                    logger.error("Error sending workflow status back to server. InvocationID=" + item.getMessage().getInvocationId() + ": " + apie.getMessage());
                }
            }
            return false;
        }
    }

    /** Processor service for a block has returned a completion message. This tells the
    block that it can return from its execute method */
    public synchronized void processorFinished(DataProcessorResponseMessage response) {
        logger.debug("Workflow invocation received a data processor completion response message. InvocationID=" + response.getInvocationId() + " BlockID=" + response.getContextId());
        WorkflowInvocationMessagePlan.MessageItem message = messagePlan.getMessageForContextId(response.getContextId());
        boolean resent = false;

        // Check to see if we are being suspended
        if(suspendFlag==true){
            logger.debug("Workflow has been suspended. InvocationID=" + response.getInvocationId() + " BlockID=" + response.getContextId());
            return;
        }
        
        // Did the service timeout 
        if(response.getStatus()==DataProcessorResponseMessage.SERVICE_TIMEOUT){
            logger.debug("Service terminated due to timeout. InvocationID=" + response.getInvocationId() + " BlockID=" + response.getContextId());
            if(message.isOkToRetry()){
                if(message.getRemainingRetries()>0){
                    logger.debug("Resending message to service. Retries left: " + message.getRemainingRetries() + " InvocationID=" + response.getInvocationId() + " BlockID=" + response.getContextId());
                    message.setRemainingRetries(message.getRemainingRetries() - 1);
                    messagePlan.pushBack(message);
                    resent = true;
                } else {
                    logger.debug("Not resending message. Run out of retries. InvocationID=" + response.getInvocationId() + " BlockID=" + response.getContextId());
                    message.setStatusText("Service timed out and retry limit reached");
                    resent = false;
                }
            } else {
                logger.debug("Service not allowed to retry. InvocationID=" + response.getInvocationId() + " BlockID=" + response.getContextId());
            }
        }
        
        if(!resent){
            // Set the correct status flag in the message plan
            message.setCommandOutput(response.getCommandOutput());
            if (response.getStatus() == DataProcessorResponseMessage.SERVICE_EXECUTION_OK) {
                message.setStatus(WorkflowInvocationMessagePlan.MESSAGE_PROCESSING_COMPLETED_OK);
                message.setStatusText("");
            } else {
                message.setStatus(response.getStatus());
                message.setStatusText(response.getStatusMessage());
                invocationFailed = true;
            }

            message.setCommandOutput(response.getCommandOutput());  // Copy output data

            // Send the output data back to the server via the API link
            if (reportUsedForCommandOutput == false) {
                boolean upload = true;

                if (onlyFailedOutputsUploaded && message.getStatus() == WorkflowInvocationMessagePlan.MESSAGE_PROCESSING_COMPLETED_OK) {
                    upload = false;
                }

                if (upload) {
                    try {
                        String statusText;
                        if(response.getStatus()==DataProcessorResponseMessage.SERVICE_EXECUTION_OK){
                            statusText = WorkflowServiceLog.SERVICE_EXECUTION_OK;
                        } else {
                            statusText = WorkflowServiceLog.SERVICE_EXECUTION_ERROR;
                        }

                        apiLink.updateServiceLogAsync(response.getInvocationId(), response.getContextId(), response.getCommandOutput(), statusText, message.getStatusText());
                    } catch (Exception e) {
                        logger.error("Error sending block debugging data back to server. InvocationID=" + message.getMessage().getInvocationId() + ": " + e.getMessage());
                        message.setCommandOutput("Error sending debugging data: " + e.getMessage());
                        message.setStatusText(message.getStatusText() + " (+ Error sending output)");
                    }
                } else {
                    try {
                        String statusText;
                        if(response.getStatus()==DataProcessorResponseMessage.SERVICE_EXECUTION_OK){
                            statusText = WorkflowServiceLog.SERVICE_EXECUTION_OK;
                        } else {
                            statusText = WorkflowServiceLog.SERVICE_EXECUTION_ERROR;
                        }

                        apiLink.updateServiceLogAsync(response.getInvocationId(), response.getContextId(), "", statusText, message.getStatusText());
                    } catch (Exception e) {
                        logger.error("Error sending block debugging data back to server. InvocationID=" + message.getMessage().getInvocationId() + ": " + e.getMessage());
                        message.setCommandOutput("Error sending debugging data: " + e.getMessage());
                        message.setStatusText(message.getStatusText() + " (+ Error sending output)");
                    }                
                }
            }
        }

        // Send the next message if possible. If there are no more left, then finish
        // up the execution process.
        currentItem = null;

        // Send next message or sleep waiting for the lock to be released
        if (response.isWaitingForLock() && resent==false) {
            logger.debug("Workflow is waiting for a lock to be release. InvocationID=" + getInvocationId());
            lockId = response.getLockId();
            waitingForLock = true;
        } else {
            waitingForLock = false;
            lockId = 0;
            logger.debug("Checking to see if there are more messages to be sent. InvocationID=" + getInvocationId());
            if(killFlag==false){
                if (!sendNextMessage()) {
                    logger.debug("No more messages to be sent. Finishing the workflow invocation. InvocationID=" + getInvocationId());
                    finishExecution(false, "");
                }
            } else {
                logger.debug("Workflow killed, so not attempting more messages");
            }
        }
    }

    /** Resume processing after a lock */
    public void resumeAfterLock(String contextId, long lockId, int failedWorkflowCount) {
        if (waitingForLock) {
            if (this.lockId == lockId) {
                waitingForLock = false;

                // Get the lock
                WorkflowLock lock = null;
                try {
                    lock = createApi().getWorkflowLock(lockId);
                } catch (Exception e){
                    try {
                        createApi().updateServiceLogMessageAsync(invocationId, contextId, WorkflowServiceLog.SERVICE_EXECUTION_ERROR, "Could not access workflow lock");
                    } catch (Exception ex){
                        logger.error("Error sending status message to server: " + e.getMessage());
                    }
                    finishExecution(true, "Could not access workflow lock data");
                    return;
                }
                    
                // Remove the lock from the server
                try {
                    createApi().removeWorkflowLock(lockId);
                } catch (Exception e){
                    logger.error("Error removing workflow lock from server: " + e.getMessage());
                }
                
                if(failedWorkflowCount==0){
                    if (!sendNextMessage()) {
                        finishExecution(false, "");
                    }
                } else {
                    // See what to do on lock failure
                    if(lock!=null){
                        if(lock.isAllowFailedSubworkflows()){
                            try {
                                createApi().updateServiceLogMessageAsync(invocationId, contextId, WorkflowServiceLog.SERVICE_EXECUTION_OK, failedWorkflowCount + " child workflows contained execution errors");
                            } catch (Exception e){
                                logger.error("Error updating status message for block after lock returned failed invocations: " + e.getMessage());
                            }
                            
                            if(!sendNextMessage()){
                                finishExecution(false, "");
                            }
                        } else {
                            try {
                                createApi().updateServiceLogMessageAsync(invocationId, contextId, WorkflowServiceLog.SERVICE_EXECUTION_ERROR, failedWorkflowCount + " child workflows contained execution errors");
                            } catch (Exception e){
                                logger.error("Error updating status message for block after lock returned failed invocations: " + e.getMessage());
                            }
                            finishExecution(true, failedWorkflowCount + " child workflows contained execution errors");                            
                        }
                        
                    } else {
                        finishExecution(true, "No lock data available");
                    }
                }
            }
        }
    }

    /** Get the currently executing item */
    public WorkflowInvocationMessagePlan.MessageItem getCurrentItem() {
        return currentItem;
    }
    
    /** Get the current block ID */
    public String getCurrentContextId(){
        return currentItem.getMessage().getContextId();
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }
    
    
    /** Do any of the execution reports contain an error */
    private boolean isExectionErrorPresent() {
        Enumeration reports = executionReports.elements();
        BlockExecutionReport report;
        while (reports.hasMoreElements()) {
            if (((BlockExecutionReport) reports.nextElement()).getExecutionStatus() != BlockExecutionReport.NO_ERRORS) {
                return true;
            }
        }
        return false;
    }

    /** Finish off the workflow execution */
    public synchronized void finishExecution(boolean error, String message) {
        logger.debug("Finishing workflow invocation. Error=" + error + " InvocationID=" + invocationId);
        
        if(serverProcess!=null && serverProcess.isRunning()){
            serverProcess.stopServer();
        }
        
        // Clean up the data store
        invocationFailed = error;
        dataSource.emptyDirectory();

        // Store the execution reports
        executionReports = executor.getExecutionReports();
        Enumeration reports = executionReports.elements();
        //WorkflowInvocationFolder folder = null;
        BlockExecutionReport report;
        // Check exectution reports agains message plan and store any errors
        while (reports.hasMoreElements()) {
            report = (BlockExecutionReport) reports.nextElement();

            if (report != null) {
                // Try and set the output data in the report
                WorkflowInvocationMessagePlan.MessageItem item = messagePlan.getMessageForContextId(report.getBlockGuid());
                if (item != null && reportUsedForCommandOutput) {
                    report.setCommandOutputStored(true);
                    report.setCommandOutput(item.getCommandOutput());
                } else {
                    report.setCommandOutputStored(false);
                }

                if(item!=null){
                    // Set the status in the report
                    if (item.getStatus() == WorkflowInvocationMessagePlan.MESSAGE_PROCESSING_COMPLETED_OK) {
                        report.setExecutionStatus(BlockExecutionReport.NO_ERRORS);

                    } else if (item.getStatus() == WorkflowInvocationMessagePlan.MESSAGE_PROCESSING_COMPLETED_WITH_ERRORS) {
                        report.setExecutionStatus(BlockExecutionReport.INTERNAL_ERROR);
                        report.setAdditionalMessage(item.getStatusText());

                    } else if (item.getStatus() == WorkflowInvocationMessagePlan.MESSAGE_REJECTED) {
                        report.setExecutionStatus(BlockExecutionReport.INTERNAL_ERROR);
                        report.setAdditionalMessage("Call message rejected: " + item.getStatusText());

                    } else if (item.getStatus() == WorkflowInvocationMessagePlan.MESSAGE_TRANSMISSION_ERROR) {
                        report.setExecutionStatus(BlockExecutionReport.INTERNAL_ERROR);
                        report.setAdditionalMessage("Message transmission error: " + item.getStatusText());

                    } else {
                        report.setExecutionStatus(BlockExecutionReport.INTERNAL_ERROR);
                        report.setAdditionalMessage("Unspecified error: " + item.getStatusText());

                    }
                } else {
                    report.setExecutionStatus(BlockExecutionReport.INTERNAL_ERROR);
                    report.setAdditionalMessage("No report available for block");
                }
            } else {
                System.out.println("*** WARNING: Missing block report ***");
            }
        }
               

        

        try {
            if(error){
                // Some kind of setup error
                logger.debug("Invocation finished with an error: " + message + " InvocationID=" + invocationId);
                createApi().setWorkflowStatus(invocationId, WorkflowInvocationFolder.INVOCATION_FINISHED_WITH_ERRORS, message);
                
            } else {
                // Set the status
                if (isExectionErrorPresent()) {
                    createApi().setWorkflowStatus(invocationId, WorkflowInvocationFolder.INVOCATION_FINISHED_WITH_ERRORS, message);
                } else {
                    createApi().setWorkflowStatus(invocationId, WorkflowInvocationFolder.INVOCATION_FINISHED_OK, "");
                }

                if (isExectionErrorPresent()) {
                    try {
                        createApi().logWorkflowCompleteAsync(getInvocationId(), WorkflowInvocationFolder.FAILED_MESSAGE);
                    } catch (Exception apie) {
                        logger.error("Error sending workflow status back to server. InvocationID=" + invocationId + ": " + apie.getMessage());
                    }
                } else {
                    try {
                        createApi().logWorkflowCompleteAsync(getInvocationId(), WorkflowInvocationFolder.OK_MESSAGE);
                    } catch (Exception apie) {
                        logger.error("Error sending workflow status back to server. InvocationID=" + invocationId + ": " + apie.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error saving workflow status details. InvocationID=" + invocationId + ": " + e.getMessage());
        }


        
        // Notify listeners
        running = false;
        notifyFinished();

        // Remove from the parent
        parent.invocationFinished(this);

        // Delete the invocation folder if needed
        if (deletedOnSuccess) {
            if (!isExectionErrorPresent()) {
                try {
                    createApi().deleteFolderAsync(getInvocationId());
                } catch (Exception e) {
                    logger.error("Error deleting workflow invocation folder on server. InvocationID=" + invocationId + ": " + e.getMessage());
                }
            }
        }
        
        // Terminate the API
        if(apiLink!=null){
            try {
                logger.debug("Workflow Invocation API(" + apiLink.getClass().getSimpleName() + ") Terminate. InvocationID=" + getInvocationId());
                apiLink.terminate();
            } catch (Exception e){
                logger.error("Error terminating API in WorkflowInvocation: " + e.getMessage());
            }
        }
        
        // Terminate any additional links
        if(additionalApiLinks.size()>0){
            for(API api : additionalApiLinks){
                logger.debug("Workflow Invocation Additional API(" + api.getClass().getSimpleName() + ") Terminate. InvocationID=" + getInvocationId());
                try {
                    api.terminate();
                } catch (Exception e){
                    logger.error("Error terminating additional API link in WorkflowInvocation: " + e.getMessage());
                }
            }
        }
            
    }

    /** Is this invocation executing */
    public boolean isRunning() {
        return running;
    }

    /** Block has finished executing */
    public void blockExecutionFinished(BlockModel block) {
    }

    /** Block has started executing */
    public void blockExecutionStarted(BlockModel block) {
    }

    /** Drawing has finished. This means that the message plan is complete, so messages can start being sent */
    public void drawingExecutionFinished(DrawingModel drawing) {
        logger.debug("Message plan creation finished. InvocationID=" + invocationId);
        if(isExectionErrorPresent()){
            logger.error("Error detected creating message plan. InvocationID=" + invocationId);
            finishExecution(true, "Error setting up drawing for execution");
        } else {
            // Create a server to handle all services in a single VM
            if(CloudWorkflowEngine.SINGLETON.isSingleVMPerWorkflowMode() && isSingleVmMode()){
                serverProcess = new SingleVMServerProcessContainer(RegistryUtil.DEFAULT_PORT, invocationId, dataSource.getStorageDir());
                serverProcess.setDebuggingEnabled(CloudWorkflowEngine.SINGLETON.isDebuggerAllowed());
                if (CloudWorkflowEngine.SINGLETON.isDebuggerAllowed()) {
                    serverProcess.setDebuggingSuspended(CloudWorkflowEngine.SINGLETON.isSingleVMDebuggingSuspended());
                }
                serverProcess.start();
                boolean ok = serverProcess.waitForRMIRegistration(CloudWorkflowEngine.SINGLETON.getSingleVMCreationTimeout());
                if(!ok){
                    logger.error("Server process did not write a startup file");
                } else {
                    logger.debug("Server startup file created");
                }
            } else {
                logger.debug("Running workflow in VM / service mode");
            }
            
            logger.debug("Starting to send service call messages. InvocationID=" + invocationId);

            try {
                createApi().logWorkflowExecutionStartedAsync(invocationId);
            } catch (Exception e) {
                logger.error("Error sending status to server", e);
            }

            // Should we resume execution from a previous run
            if(resumeFlag==true && invocationStateExists()){
                // Load the drawing state
                try {
                    XmlDataStore state = loadInvocationState();
                    waitingForLock = state.booleanValue("WaitingForLock", false);
                    lockId = state.longValue("LockID", 0);
                    invocationFailed = state.booleanValue("InvocationFailed", false);
                    killFlag = state.booleanValue("KillFlag", false);
                    if(state.booleanValue("HasCurrentItem", false)){
                        String contextId = state.stringValue("CurrentContextID", null);
                        if(contextId!=null){
                            // Pop messages off the queue until we reach the current message
                            messagePlan.moveToContextId(contextId);
                        }
                    }
                    
                } catch (Exception e){
                    logger.error("Error loading drawing state for resume. InvocationID=" + invocationId);
                    finishExecution(true, "Error loading invocation state");
                }
            }
            
            // Check lock status
            if(resumeFlag==false){
                if(!sendNextMessage()) {
                    finishExecution(false, "");
                }               
                
            } else {
                if(!waitingForLock){
                    // Not waiting for lock, run as normal
                    if (!sendNextMessage()) {
                        finishExecution(false, "");
                    }
                } else {
                    // Ask the server to send a lock completed message if the lock has finished
                    logger.debug("Resumed invocation is waititng for a lock. Requesting update from server. InvocationID=" + invocationId);
                    try {
                        createApi().refreshLockStatusAsync(lockId);
                    } catch (Exception e){
                        logger.error("Error requesting lock status update from server. InvocationID=" + invocationId, e);
                    }
                }
            }
        }
    }

    /** Drawing execution has begun */
    public void drawingExecutionStarted(DrawingModel drawing) {
    }

    /** Get all of the execution reports */
    public Hashtable getExecutionReports() {
        return executionReports;
    }

    /** Get the exection report for a block */
    public BlockExecutionReport getExecutionReport(String blockId) {
        if (executionReports.containsKey(blockId)) {
            return (BlockExecutionReport) executionReports.get(blockId);
        } else {
            return null;
        }
    }
    
    /** Get an API */
    

    /** Get a block by name */
    public BlockModel getBlock(String name) {
        Enumeration blocks = drawing.blocks();
        BlockModel block;
        while (blocks.hasMoreElements()) {
            block = (BlockModel) blocks.nextElement();
            if (block.getName().equals(name)) {
                return block;
            }
        }
        return null;
    }

    /** Get the latest version of a service definition */
    public DataProcessorServiceDefinition getServiceDefinition(String serviceId) throws DataProcessorException {
        try {
            return createApi().getService(serviceId);
        } catch (Exception e) {
            logger.error("Error getting service definition data. ServiceID=" + serviceId + " InvocationID=" + invocationId);
            throw new DataProcessorException("Error getting service defintion: " + e.getMessage(), e);
        }
    }

    /** Get a specific version of a service definition */
    public DataProcessorServiceDefinition getServiceDefinition(String serviceId, String versionId) throws DataProcessorException {
        try {
            return createApi().getService(serviceId, versionId);
        } catch (Exception e) {
            logger.error("Error getting service definition data. ServiceID=" + serviceId + " v " + versionId + " InvocationID=" + invocationId);
            throw new DataProcessorException("Error getting service defintion: " + e.getMessage(), e);
        }
    }

    /** Kill this workflow invocation */
    public void kill() {
        logger.debug("Killing workflow. InvocationID=" + invocationId);
        killFlag = true;
        WorkflowInvocationMessagePlan.MessageItem itemToKill = currentItem;
        if (itemToKill != null) {
            DataProcessorClient client = itemToKill.getClient();
            DataProcessorCallMessage message = itemToKill.getMessage();
            try {
                client.terminate(message);
            } catch (Exception e) {
                logger.debug("Error sending workflow termination message. InvocationID=" + invocationId, e);
            }
        }
        logger.debug("Forcing workflow finish routine. InvocationID=" + invocationId);
        finishExecution(true, "Workflow Killed");
        
    }
    
    /** Suspend this invocation and stop running */
    public void suspend(){
        // Set status to suspended
        logger.debug("Suspending workflow. InvocationID=" + invocationId);
        
        // Kill the current block if there is one
        killFlag = true;
        WorkflowInvocationMessagePlan.MessageItem itemToKill = currentItem;
        if(itemToKill!=null){
            DataProcessorClient client = itemToKill.getClient();
            DataProcessorCallMessage message = itemToKill.getMessage();
            try {
                client.terminate(message);
            } catch (Exception e) {
                logger.debug("Error sending workflow termination message. InvocationID=" + invocationId + ": " + e.getMessage());
                finishExecution(true, "Could not suspend workflow");
            }            
        }

    }

    /** Save the state of the invocation to the invocation directory */
    public void saveInvocationState() {
        try {
            File stateFile = new File(dataSource.getStorageDir(), "_invocationState.xml");
            XmlDataStore state = new XmlDataStore("InvocationState");
            WorkflowInvocationMessagePlan.MessageItem ci = currentItem;
            
            // Save some essential data to allow resuming
            state.add("Running", running);
            state.add("WaitingForLock", waitingForLock);
            state.add("LockID", lockId);
            state.add("InvocationFailed", invocationFailed);
            state.add("Killed", killFlag);
            
            if(ci!=null){
                state.add("HasCurrentItem", true);
                state.add("CurrentContextID", ci.getContextId());
            } else {
                state.add("HasCurrentItem", false);
                state.add("CurrentContextID", "");
            }

            XmlFileIO writer = new XmlFileIO(state);
            writer.writeFile(stateFile);
        } catch (Exception e){
            logger.error("Error saving invocation state: " + e.getMessage());
        }
    }
    
    /** Load the invocation state */
    public XmlDataStore loadInvocationState() throws Exception {
        File stateFile = new File(dataSource.getStorageDir(), "_invocationState.xml");
        XmlFileIO reader = new XmlFileIO(stateFile);
        return reader.readFile();
    }
    
    /** Does the invocation state file exist */
    public boolean invocationStateExists() {
        File stateFile = new File(dataSource.getStorageDir(), "_invocationState.xml");
        return stateFile.exists();
    }
    
    /** Save all of the invocation data */
    public void saveInvocationData() {
        try {
            File dataFile = new File(dataSource.getStorageDir(), "_invocationData.xml");
            XmlFileIO writer = new XmlFileIO(storeObject());
            writer.writeFile(dataFile);
        } catch (Exception e){
            logger.error("Error saving invocation data: " + e.getMessage());
        }
    }
    
    /** Load invocation data from a directory */
    public void loadInvocationData(File sourceDir) throws Exception {
        File dataFile = new File(sourceDir, "_invocationData.xml");
        if(dataFile.exists()){
            XmlFileIO reader = new XmlFileIO(dataFile);
            recreateObject(reader.readFile());
        } else {
            throw new Exception("Cannot locate invocation data file");
        }
    }
    
    /** Parse a set of replacement properties as a byte[] array representation of an IWorkflowParameterList object */
    public void parseReplacementParameterXmlData(byte[] parameterData) throws WorkflowInvocationException {
        try {

            Object obj = SerializationUtils.deserialize(parameterData);
            if (obj instanceof WorkflowParameterList) {
                WorkflowParameterList parameterList = (WorkflowParameterList)obj;
                WorkflowParameter parameter;

                parameterReplacements = new XmlDataStore("ReplacementParameters");
                XmlDataStore blockParameters;
                for (int i = 0; i < parameterList.size(); i++) {
                    parameter = parameterList.getParameter(i);
                    if (parameterReplacements.containsName(parameter.getBlockName())) {
                        blockParameters = parameterReplacements.xmlDataStoreValue(parameter.getBlockName());
                    } else {
                        blockParameters = new XmlDataStore(parameter.getBlockName());
                        parameterReplacements.add(parameter.getBlockName(), blockParameters);
                    }
                    blockParameters.add(parameter.getName(), parameter.getValue());
                }

            } else {
                parameterReplacements = null;
                throw new Exception("Data does not contain a parameter list");
            }
        } catch (Exception e) {
            parameterReplacements = null;
            throw new WorkflowInvocationException("Error parsing parameter data: " + e.getMessage(), e);
        }
    }

    /** Replace the drawing parameters with the replacement parameter set */
    public void replaceWorkflowParameters() throws WorkflowInvocationException {
        try {
            if (drawing != null && parameterReplacements != null) {
                DrawingParameterReplacer replacer = new DrawingParameterReplacer(drawing, createApi());
                replacer.replaceParameters(parameterReplacements);
            }
        } catch (Exception e) {
            throw new WorkflowInvocationException("Error replacing workflow parameters: " + e.getMessage(), e);
        }
    }

    /** Clear the parameter replacement object */
    public void clearReplacementParameters() {
        parameterReplacements = null;
    }


    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("WorkflowInvocation");
        store.add("DrawingData", drawingData);
        store.add("DeletedOnSuccess", deletedOnSuccess);
        store.add("EngineID", engineId);
        store.add("InputBlockName", inputBlockName);
        store.add("InvocationFailed", invocationFailed);
        store.add("InvocationID", invocationId);
        store.add("KillFlag", killFlag);
        store.add("LockID", lockId);
        store.add("OnlyFailedOutputsUploaded", onlyFailedOutputsUploaded);
        store.add("ReportUsedForCommandOutput", reportUsedForCommandOutput);
        store.add("Running", running);
        store.add("StartTime", startTime);
        store.add("TargetFileID", targetFileId);
        try {
            store.add("Ticket", SerializationUtils.serialize(ticket));
        } catch (IOException ioe){
            logger.error("Error saving ticket data: " + ioe.getMessage());
        }
        store.add("VersionID", versionId);
        store.add("WaitingForLock", waitingForLock);
        store.add("WorkflowID", workflowId);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        drawingData = store.xmlDataStoreValue("DrawingData");
        deletedOnSuccess = store.booleanValue("DeletedOnSuccess", false);
        engineId = store.stringValue("EngineID", null);
        inputBlockName = store.stringValue("InputBlockName", null);
        invocationFailed = store.booleanValue("InvocationFailed", false);
        invocationId = store.stringValue("InvocationID", null);
        killFlag = store.booleanValue("KillFlag", false);
        lockId = store.longValue("LockID", 0);
        onlyFailedOutputsUploaded = store.booleanValue("OnlyFailedOutputsUploaded", false);
        reportUsedForCommandOutput = store.booleanValue("ReportUsedForCommandOutput", true);
        running = store.booleanValue("Running", false);
        startTime = store.dateValue("StartTime", new Date());
        targetFileId = store.stringValue("TargetFileID", null);
        if(store.containsName("Ticket")){
            try {
                ticket = (Ticket)SerializationUtils.deserialize(store.byteArrayValue("Ticket"));
            } catch (Exception e){
                logger.error("Error loading ticket data");
                throw new XmlStorageException("Error loading ticket data");
            }
        } else {
            ticket = null;
        }
        versionId = store.stringValue("VersinoID", null);
        waitingForLock = store.booleanValue("WaitingForLock", false);
        workflowId = store.stringValue("WorkflowID", null);
    }
}