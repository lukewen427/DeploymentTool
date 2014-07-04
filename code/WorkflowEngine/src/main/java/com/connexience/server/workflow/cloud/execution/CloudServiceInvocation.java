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

import com.connexience.server.model.logging.performance.Execution;
import com.connexience.server.model.logging.performance.Port;
import com.connexience.server.model.logging.performance.Property;
import com.connexience.server.util.provenance.PerformanceLoggerClient;
import com.connexience.server.util.provenance.ProvenanceLoggerClient;
import com.connexience.server.model.logging.graph.WorkflowDataServiceOperation;
import com.connexience.server.model.logging.graph.WorkflowDataTransferOperation;
import com.connexience.server.workflow.cloud.library.*;
import com.connexience.server.workflow.engine.cloud.*;
import com.connexience.server.workflow.service.*;
import com.connexience.server.util.*;
import com.connexience.server.model.security.*;
import com.connexience.server.workflow.api.*;
import com.connexience.server.workflow.cloud.CloudWorkflowEngine;
import com.connexience.server.workflow.cloud.execution.runners.AbstractRunner;
import com.connexience.server.workflow.cloud.execution.runners.OneShotJVMRunner;
import com.connexience.server.workflow.cloud.execution.runners.SingleVMRunnerClient;
import com.connexience.server.workflow.engine.WorkflowInvocation;
import com.connexience.server.workflow.engine.datatypes.FileWrapper;
import com.connexience.server.workflow.service.clients.DataProcessorDataSourceFileClient;
import com.connexience.server.workflow.util.SigarData;
import com.connexience.server.workflow.util.XmlSerializationUtils;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.io.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.net.*;

import org.apache.log4j.*;

/**
 * This class handles a single call to the cloud workflow engine. It deals with
 * the unpacking of the service container and any dependencies. One of these is
 * created each time a cloud message is received by the cloud engine.
 *
 * @author nhgh
 */
public class CloudServiceInvocation {
    static Logger logger = Logger.getLogger(CloudServiceInvocation.class);

    /**
     * Call message
     */
    private DataProcessorCallMessage message;

    /**
     * Message destination
     */
    private CloudDataProcessorMessageDestination responseDestination;

    /**
     * Library containing all of the downloaded services
     */
    private ServiceLibrary library;

    /**
     * Parent service manager
     */
    private CloudServiceInvocationManager parent;

    /**
     * User ticket for this invocation
     */
    private Ticket ticket = null;

    /**
     * Client for accessing invocation directory
     */
    private DataProcessorDataSourceFileClient sourceClient = null;

    /**
     * Top level library item that contains the code to execute
     */
    private CloudWorkflowServiceLibraryItem libraryItem = null;

    /**
     * External process to run the data processor
     */
    private AbstractRunner externalProcess = null;

    /**
     * Has this service been killed
     */
    private volatile boolean killed = false;

    /**
     * Do we expect the process to be running
     */
    private AtomicBoolean running = new AtomicBoolean(false);

    /**
     * Download report generated during dependency preparation
     */
    private LibraryPreparationReport report = new LibraryPreparationReport();

    /**
     * Service invocation directory
     */
    private File invocationDirectory = null;

    /**
     * Last ping time for this service
     */
    private volatile long lastPingTime = System.currentTimeMillis();

    /**
     * Service start time
     */
    private long startTime = System.currentTimeMillis();

    /**
     * Time at which the invocation will timeout
     */
    private long timeoutTime = System.currentTimeMillis() + (3600000);

    /**
     * Should the invocation timeout be enforced
     */
    private boolean timeoutEnforced = true;

    /**
     * API Object
     */
    private API api = null;

    /**
     * Parent invocation object
     */
    private WorkflowInvocation invocation;

    /**
     * Was the API created locally
     */
    private boolean localApi = false;


    
    public CloudServiceInvocation(CloudServiceInvocationManager parent, String invocationId, DataProcessorCallMessage message, CloudDataProcessorMessageDestination responseDestination, ServiceLibrary library) {
        logger.debug("Created service invocation. InvocationID=" + invocationId + " BlockID=" + message.getContextId());
        this.message = message;
        this.responseDestination = responseDestination;
        this.library = library;
        this.parent = parent;

        // Set the parent invocation in the report
        if (parent.getParentEngine() != null && parent.getParentEngine().getExecutionEngine() != null) {
            invocation = parent.getParentEngine().getExecutionEngine().getInvocation(message.getInvocationId());
            if (invocation != null) {
                report.setParentInvocation(invocation);
            } else {
                report.setParentInvocation(null);
            }
        } else {
            report.setParentInvocation(null);
        }
    }

    /**
     * Get the last ping time
     */
    public long getLastPingTime() {
        return lastPingTime;
    }

    /**
     * Update the last ping time
     */
    public void updateLastPingTime() {
        lastPingTime = System.currentTimeMillis();
    }

    /**
     * Set the cloud library item to execute
     */
    private void setLibraryItem(CloudWorkflowServiceLibraryItem libraryItem) {
        this.libraryItem = libraryItem;
    }

    /**
     * Get the invocation ID
     */
    public String getInvocationId() {
        return message.getInvocationId();
    }

    /**
     * Get the block context ID
     */
    public String getContextId() {
        return message.getContextId();
    }

    /**
     * Get the service start time
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Get the service timeout time
     */
    public long getTimeoutTime() {
        return timeoutTime;
    }

    /**
     * Is the invocation timeout property to be observed
     */
    public boolean isTimeoutEnforced() {
        return timeoutEnforced;
    }

    public long getMaximumResidentMemory(){
        if(externalProcess!=null){
            try {
                return externalProcess.getMaximumResidentMemory();
            } catch (Exception e){
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    public long getMaximumMemorySize(){
        if(externalProcess!=null){
            try {
                return externalProcess.getMaximumMemorySize();
            } catch (Exception e){
                return 0;
            }
        } else {
            return 0;
        }        
    }
    
    /**
     * Execute this invocation
     */
    public void start() throws DataProcessorException {
        logger.debug("Starting service invocation Thread. InvocationID=" + getInvocationId() + " BlockID=" + getContextId());
        new Thread(new Runnable() {

            public void run() {
                startTime = System.currentTimeMillis();
                long timeout = message.getProperties().longValue("InvocationTimeout", 3600);
                timeoutEnforced = message.getProperties().booleanValue("EnforceInvocationTimeout", true);
                timeoutTime = startTime + (timeout * 1000);

                ticket = null;
                try {
                    ticket = (Ticket) SerializationUtils.deserialize(message.getTicketData());
                    if (invocation != null) {
                        api = invocation.getApiLink();
                        localApi = false;
                    } else {
                        logger.warn("No parent invocation in CloudServiceInvocation. Creating new API");
                        api = parent.getParentEngine().getApiProvider().createApi(ticket);
                        localApi = true;
                    }
                } catch (Exception e) {
                    logger.error("Error creating API", e);
                    notifyError("Cannot create API", e.getMessage());
                }

                if (ticket != null) {
                    try {
                        // Check with the library that a service is ready and call the
                        // service
                        LibraryCallback callback = new LibraryCallback() {

                            /** Library preparation suceeded */
                            public void libraryReady(CloudWorkflowServiceLibraryItem libraryItem, LibraryPreparationReport report) {
                                if (message.getDataTransferType().equals(DataProcessorDataSource.FILE_DATA_SOURCE)) {
                                    try {
                                        sourceClient = new DataProcessorDataSourceFileClient(message.getStorageUrl());
                                    } catch (Exception e) {
                                        notifyError("Error creating data source client: ", e.getMessage());
                                    }
                                } else {
                                    notifyError("Cloud services only operate on file data sources", "Service error");
                                }
                                setLibraryItem(libraryItem);
                                execute();

                                // Terminate the API
                                if (api != null) {
                                    try {
                                        if (localApi) {
                                            logger.debug("Service Invocation API(" + api.getClass().getSimpleName() + ") Terminate. InvocationID=" + getInvocationId());
                                            api.terminate();
                                        } else {
                                            logger.debug("CloudServiceInvocation used API from parent Invocation object");
                                        }

                                    } catch (Exception e) {
                                        logger.error("Error terminating API: " + e.getMessage());
                                    }
                                }
                            }

                            /** Library could not be prepared */
                            public void libraryPreparationFailed(String message, LibraryPreparationReport report) {
                                logger.error("Workflow Library preparation failed: " + message + " InvocationID=" + getInvocationId());
                                notifyError(message, "");
                            }
                        };

                        // Ask library for the service. The callback is notified when this is finished
                        if (message.usesLatestVersion()) {
                            library.prepareService(api, message.getServiceId(), callback, report, true);
                        } else {
                            library.prepareService(api, message.getServiceId(), message.getVersionId(), callback, report, true);
                        }

                    } catch (Exception e) {
                        logger.error("Exception in service invocation thread. InvocationID=" + getInvocationId() + " BlockID=" + getContextId());
                        notifyError("Exception in service thread", e.getMessage());
                    }
                } else {
                    logger.error("No ticket. InvocationID=" + getInvocationId() + " BlockID=" + getContextId());
                    notifyError("No ticket", "");
                }

            }
        }, "CSI: " + getInvocationId()).start();
    }

    /**
     * Run this invocation
     */
    private void execute() {
        logger.debug("Starting service execution process. InvocationID=" + getInvocationId() + " BlockID=" + getContextId());
        if (libraryItem != null) {
            if (!killed) {
                try {
                    invocationDirectory = new File(sourceClient.getStorageDirectory(message.getInvocationId()));
                } catch (Exception e) {
                    logger.error("Cannot find invocation directory. InvocationID=" + getInvocationId());
                    notifyError("Cannot find invocation directory: " + e.getMessage(), "");
                    return;
                }
            } else {
                notifyError("Invocation killed", "");
                return;
            }

            // Prepare the invocation directory with files from the library
            // if there are any
            if (!killed) {
                try {
                    libraryItem.prepareInvocationDirectory(invocationDirectory, message);
                } catch (DataProcessorException dpe) {
                    logger.error("Error preparing invocation directory. InvocationID=" + getInvocationId(), dpe);
                    notifyError("Error copying library files", dpe.getMessage());
                    return;
                }
            } else {
                notifyError("Invocation kileld", "");
                return;
            }

            // Build a classpath of all of the jars in the library and
            // any dependencies
            ArrayList<URL> classpath = new ArrayList<>();
            if (!killed) {
                try {
                    libraryItem.addLibraryJarsToClasspath(classpath);

                } catch (Exception e) {
                    logger.error("Error building classpath. InvocationID=" + getInvocationId(), e);
                    notifyError("Error building classpath", e.getMessage());
                    return;
                }
            } else {
                notifyError("Invocation killed", "");
                return;
            }

            // Save the message to the working directory
            if (!killed) {
                try {
                    File messageFile = new File(invocationDirectory, message.getContextId() + "-message.msg");
                    XmlSerializationUtils.xmlDataStoreSerialize(messageFile, message);
                } catch (Exception e) {
                    logger.error("Error saving call message. InvocationID=" + getInvocationId(), e);
                    notifyError("Error saving call message", e.getMessage());
                    return;
                }
            } else {
                notifyError("Invocation killed", "");
                return;
            }

            // Serialize the library and dependencies to the working directory
            if (!killed) {
                try {
                    File libraryFile = new File(invocationDirectory, message.getContextId() + "-library.dat");
                    //SerializationUtils.serialize(libraryItem, libraryFile);
                    //SerializationUtils.XmlSerialize(libraryItem, libraryFile);
                    XmlSerializationUtils.xmlDataStoreSerialize(libraryFile, libraryItem);
                } catch (Exception e) {
                    logger.error("Error saving library details. InvocationID=" + getInvocationId(), e);
                    notifyError("Error saving library details", e.getMessage());
                    return;
                }
            } else {
                notifyError("Invocation killed", "");
            }

            if (!killed) {
                externalExecute(invocationDirectory, classpath);
            } else {
                notifyError("Invocation killed", "");
                return;
            }


        } else {
            logger.error("Library does not exist. InvocationID=" + getInvocationId() + " BlockID=" + getContextId());
            notifyError("Library does not exist", "Service library was not downloaded");
        }
    }

    /**
     * Execute in a separate process
     */
    private void externalExecute(File invocationDirectory, ArrayList<URL> classpathList) {
        try {
            //AbstractRunner externalProcess = new OneShotJVMRunner(message);
            if( message.containsInputType("object-wrapper")){
                // Force external VM for serialized input
                logger.debug("OneShotJVMRunner started for block containing and object-wrapper input. InvocationID=" + getInvocationId());
                externalProcess = new OneShotJVMRunner(message);
            } else if((CloudWorkflowEngine.SINGLETON.isSingleVMPerWorkflowMode() && invocation.isSingleVmMode())){
                externalProcess = new SingleVMRunnerClient(message);
            } else {
                externalProcess = new OneShotJVMRunner(message);
            }
            
            externalProcess.setMaxVmSize(parent.getParentEngine().getMaxVmSize());
            externalProcess.setPermSize(parent.getParentEngine().getPermSize());
            externalProcess.setBaseDir(parent.getParentEngine().getExecutionEngine().getDataSource().getBaseDirectory());
            externalProcess.setMessage(message);
            externalProcess.setClasspathList(classpathList);
            externalProcess.setInvocationDirectory(new File(externalProcess.getBaseDir(), message.getInvocationId()));
            externalProcess.setDebugAllowed(parent.getParentEngine().isDebuggingAllowed());
            if(message.isDebugEnabled() && parent.getParentEngine().isDebuggingAllowed()){
                externalProcess.setDebugPort(parent.getFreeDebugPort(this, message.getDebugPort()));
            }
            
            
            if (killed == false) {                
                File flag = new File(invocationDirectory, getContextId() + "-running.flag");
                flag.createNewFile();

                File startFlag = new File(invocationDirectory, getContextId() + "-started.flag");
                startFlag.createNewFile();

                running.set(true);

                ProvenanceLoggerClient provClient = new ProvenanceLoggerClient();
                PerformanceLoggerClient perfClient = new PerformanceLoggerClient();

                // Log the fact that each input has received data
                int inputSize = message.getDataSources().length;

                int index;
                String inputName;
                String outputName;
                String sourceContext;
                String targetContext;
                String dataType;
                long dataSize = 0;
                long totalConsumedDataSize = 0;
                long totalProducedDataSize = 0;

                for (int i = 0; i < inputSize; i++) {
                    inputName = message.getDataSources()[i];
                    index = message.getDataSourceIndex(inputName);
                    outputName = message.getDataSourceConnections()[index];
                    sourceContext = message.getDataSourceConnectionContexts()[index];
                    targetContext = message.getContextId();
                    dataType = message.getDataSourceTypes()[index];
                    String outputHash = null;

                    // Wrap data size in try catch in case upstream blocks havent worked
                    if (dataType.equals("file-wrapper")) {
                        // File wrappers are more complex
                        if (sourceClient.allowsFileSystemAccess()) {
                            try {
                                FileWrapper fr = new FileWrapper();
                                fr.loadFromInputStream(sourceClient.getInputDataStream(message.getInvocationId(), sourceContext, outputName));
                                dataSize = fr.getTotalFileSize(new File(sourceClient.getStorageDirectory(message.getInvocationId())));
                                totalConsumedDataSize = totalConsumedDataSize + dataSize;
                            } catch (Exception e) {
                                logger.error("Error establishing produced file sizes. Sending 0: " + e.getMessage());
                            }
                        } else {
                            logger.error("Data source will not allow file system access. Sending 0 for input data size");
                            dataSize = 0;
                        }
                    } else {
                        // Other data types are transferred in a single file
                        try {
                            dataSize = sourceClient.getInputDataLength(message.getInvocationId(), sourceContext, outputName);
                            totalConsumedDataSize = totalConsumedDataSize + dataSize;
                        } catch (Exception e) {
                            logger.error("Error establising data size sending provenance. Sending 0: " + e.getMessage());
                            dataSize = 0;
                        }
                    }

                    // Try and get the MD5 for the output
                    outputHash = sourceClient.getOutputHash(message.getInvocationId(), sourceContext, outputName);

                    try {
                        WorkflowDataTransferOperation op = new WorkflowDataTransferOperation();
                        op.setUserId(ticket.getUserId());
                        op.setInvocationId(message.getInvocationId());
                        op.setDataType(dataType);
                        op.setSourceBlockUUID(sourceContext);
                        op.setSourcePortName(outputName);
                        op.setTargetBlockUUID(targetContext);
                        op.setTargetPortName(inputName);
                        op.setDataSize(dataSize);
                        op.setHashValue(outputHash);
                        op.setProjectId(ticket.getDefaultProjectId());
                        op.setSourceServiceId(message.getServiceId());
                        op.setSourceServiceVersionId(message.getVersionId());

                        provClient.log(op);
                    } catch (Exception e) {
                        logger.error("Error sending provenance data. InvocationID=" + getInvocationId() + " BlockID=" + getContextId(), e);
                    }
                }

                startTime = new Date().getTime();

                logger.debug("Starting external process. InvocationID=" + getInvocationId() + " BlockID=" + getContextId());
                externalProcess.start();
                logger.debug("External process launch succeeded. InvocationID=" + getInvocationId() + " BlockId=" + getContextId());
                externalProcess.startDumpers();

                int result = externalProcess.waitFor();
                running.set(false);

                logger.debug("External process finished. InvocationID=" + getInvocationId() + " BlockID=" + getContextId());

                //Capture the end time of the block code
                long endTime = new Date().getTime();

                logger.debug("Stopping InputStream dumpers");
                externalProcess.stopDumpers();
 
                String outData = "";
                try {
                    outData = InputStreamDumper.readFileIntoString(externalProcess.getStdOutFile());
                } catch (Exception e) {
                    logger.error("Error reading stdout from service process: " + e.getMessage());
                }
                try {
                    outData += InputStreamDumper.readFileIntoString(externalProcess.getStdErrFile());
                } catch (Exception e) {
                    logger.error("Error reading stderr from service process: " + e.getMessage());
                }

                if(externalProcess!=null){
                    externalProcess.stopMonitoring();
                    logger.debug("Maximum memory used by: " + externalProcess.getProcessCount() + " processes for InvocationID=" + getInvocationId() + ": Size=" + externalProcess.getMaximumMemorySize() + " Resident=" + externalProcess.getMaximumResidentMemory());
                }
                
                // Load the response from file
                if (!killed) {
                    // Only send a message back if the process has exited normally i.e. not killed
                    try {
                        File responseFile = new File(invocationDirectory, message.getContextId() + "-response.msg");
                        if (responseFile.exists()) {
                            // Load the response created by the external process
                            DataProcessorResponseMessage response = (DataProcessorResponseMessage) XmlSerializationUtils.xmlDataStoreDeserialize(responseFile);
                            String responseOutData = response.getCommandOutput();
                            if (responseOutData != null) {
                                // Join std out with response data
                                response.setCommandOutput(appendDownloadReport(outData + responseOutData));
                            } else {
                                response.setCommandOutput(appendDownloadReport(outData));
                            }

                            // Make sure the error code is set if there was an error running the service
                            if (result != 0) {
                                response.setStatus(DataProcessorResponseMessage.SERVICE_EXECUTION_ERROR);
                            } else {
                                // Load the library back in to get dependency data
                                File libraryFile = new File(invocationDirectory, message.getContextId() + "-library.dat");
                                CloudWorkflowServiceLibraryItem savedLibrary = (CloudWorkflowServiceLibraryItem) XmlSerializationUtils.xmlDataStoreDeserialize(libraryFile);
                                XmlDataStore depData = savedLibrary.getDependencyChainData();
                                XmlDataStore props = message.getProperties();


                                logger.debug("Logging provenance. InvocationID=" + getInvocationId() + " BlockID=" + getContextId());
                                try {
                                    int outputSize = message.getDataOutputs().length;
                                    for (int i = 0; i < outputSize; i++) {
                                        outputName = message.getDataOutputs()[i];
                                        dataType = message.getDataOutputTypes()[i];
                                        if (dataType.equals("file-wrapper")) {
                                            // File wrappers are more complex
                                            if (sourceClient.allowsFileSystemAccess()) {
                                                FileWrapper fr = new FileWrapper();
                                                fr.loadFromInputStream(sourceClient.getInputDataStream(message.getInvocationId(), message.getContextId(), outputName));
                                                totalProducedDataSize = totalProducedDataSize + fr.getTotalFileSize(new File(sourceClient.getStorageDirectory(message.getInvocationId())));
                                            } else {
                                                logger.error("Data source will not allow file system access. Sending 0 for output data size");
                                            }
                                        } else {
                                            // Other types are transferred in a single file
                                            try {
                                                totalProducedDataSize = totalProducedDataSize + sourceClient.getOutputDataLength(message.getInvocationId(), message.getContextId(), outputName);
                                            } catch (Exception e) {
                                                logger.error("Error establishing produced data size for provenance. Sending 0");
                                            }
                                        }
                                    }

                                    Execution ex = new Execution();
                                    ex.setServiceId(savedLibrary.getZipDocumentRecord().getId());
                                    ex.setServiceName(savedLibrary.getZipDocumentRecord().getName());
                                    ex.setVersionId(savedLibrary.getZipDocumentVersion().getId());
                                    ex.setVersionNum(savedLibrary.getZipDocumentVersion().getVersionNumber());
                                    ex.setInvocationId(message.getInvocationId());
                                    ex.setStartTime(startTime);
                                    ex.setEndTime(endTime);
                                    ex.setDuration(endTime - startTime);
                                    ex.setBlockUUID(getContextId());
                                    ex.setWorkflowId(message.getWorkflowId());
                                    ex.setWorkflowVersionId(message.getWorkflowVersionId());
                                    ex.setWorkflowEngineIp(parent.getParentEngine().getHostId());
                                    ex.setConcurrentServiceInvocations(parent.getServiceCallCount());
                                    ex.setExitCode(result);   //todo: it only logs the successful blocks at present.  Do we want to log failures?

                                    if (SigarData.SYSTEM_DATA.isAvailable()) {
                                        ex.setAverageCpuSpeed(SigarData.SYSTEM_DATA.getAverageCpuSpeed());
                                        ex.setCpuVendor(SigarData.SYSTEM_DATA.getCpuVendor());
                                        ex.setCpuModel(SigarData.SYSTEM_DATA.getCpuModel());
                                        ex.setAverageCpuCacheSize(SigarData.SYSTEM_DATA.getAverageCpuCacheSize());
                                        ex.setPhysicalRam(SigarData.SYSTEM_DATA.getPhysicalRam());
                                        ex.setOperatingSystem(SigarData.SYSTEM_DATA.getOperatingSystem());
                                        ex.setCpuCount(SigarData.SYSTEM_DATA.getCpuCount());
                                        ex.setArchitecture(SigarData.SYSTEM_DATA.getArchitecture());
                                    }

                                    for (int i = 0; i < inputSize; i++) {
                                        inputName = message.getDataSources()[i];
                                        index = message.getDataSourceIndex(inputName);
                                        outputName = message.getDataSourceConnections()[index];
                                        sourceContext = message.getDataSourceConnectionContexts()[index];
                                        dataType = message.getDataSourceTypes()[index];
                                        long thisInputSize = 0L;

                                        // Wrap data size in try catch in case upstream blocks havent worked
                                        if (dataType.equals("file-wrapper")) {
                                            // File wrappers are more complex
                                            if (sourceClient.allowsFileSystemAccess()) {
                                                try {
                                                    FileWrapper fr = new FileWrapper();
                                                    fr.loadFromInputStream(sourceClient.getInputDataStream(message.getInvocationId(), sourceContext, outputName));
                                                    thisInputSize = fr.getTotalFileSize(new File(sourceClient.getStorageDirectory(message.getInvocationId())));
                                                } catch (Exception e) {
                                                    logger.error("Error gathering performance data input size.  Sending 0: " + e.getMessage());
                                                }
                                            } else {
                                                logger.error("Data source will not allow file system access. Sending 0 for input data size");
                                            }
                                        } else {
                                            // Other data types are transferred in a single file
                                            try {
                                                thisInputSize = sourceClient.getInputDataLength(message.getInvocationId(), sourceContext, outputName);
                                            } catch (Exception e) {
                                                logger.error("Error gathering performance data input size.  Sending 0: " + e.getMessage());
                                            }
                                        }

                                        Port consumed = new Port();
                                        consumed.setName(inputName);
                                        consumed.setData(thisInputSize);
                                        consumed.setServiceId(message.getServiceId());
                                        consumed.setVersionId(message.getVersionId());
                                        ex.addInputPort(consumed);
                                    }

                                    for (int i = 0; i < outputSize; i++) {
                                        outputName = message.getDataOutputs()[i];
                                        dataType = message.getDataOutputTypes()[i];
                                        long thisOutputSize = 0L;

                                        if (dataType.equals("file-wrapper")) {
                                            // File wrappers are more complex
                                            if (sourceClient.allowsFileSystemAccess()) {
                                                FileWrapper fr = new FileWrapper();
                                                fr.loadFromInputStream(sourceClient.getInputDataStream(message.getInvocationId(), message.getContextId(), outputName));
                                                thisOutputSize = fr.getTotalFileSize(new File(sourceClient.getStorageDirectory(message.getInvocationId())));
                                            } else {
                                                logger.error("Data source will not allow file system access. Sending 0 for output data size");
                                            }
                                        } else {
                                            // Other types are transferred in a single file
                                            try {
                                                thisOutputSize = sourceClient.getOutputDataLength(message.getInvocationId(), message.getContextId(), outputName);
                                            } catch (Exception e) {
                                                logger.error("Error establishing produced data size for provenance. Sending 0");
                                            }
                                        }

                                        Port produced = new Port();
                                        produced.setName(outputName);
                                        produced.setData(thisOutputSize);
                                        produced.setServiceId(message.getServiceId());
                                        produced.setVersionId(message.getVersionId());
                                        ex.addOutputPort(produced);
                                    }

                                    //todo: Check with HGH that this is the best way to load them
                                    XmlFileIO reader = new XmlFileIO(new File(sourceClient.getStorageDirectory(message.getInvocationId()), message.getContextId() + "-performance-data.xml"));
                                    XmlDataStore xmlProperties = reader.readFile();

                                    // Add in the memory stats if available
                                    if(externalProcess!=null){
                                        long maxMemory = externalProcess.getMaximumMemorySize();
                                        long maxResident = externalProcess.getMaximumResidentMemory();
                                        ex.addProperty(new Property("MaxMemorySize", Property.PropertyType.GATHERED, (double)maxMemory));
                                        ex.addProperty(new Property("MaxResidentMemory", Property.PropertyType.GATHERED, (double)maxResident));
                                        ex.addProperty(new Property("ProcessCount", Property.PropertyType.GATHERED, (double)externalProcess.getProcessCount()));
                                        ex.setMaxMemorySize(maxMemory);
                                        ex.setMaxResidentMemory(maxResident);
                                    }
                                    
                                    Vector<?> propNames = xmlProperties.getNames();
                                    for (Object nameObj : propNames) {
                                        String name = (String) nameObj;
                                        Double val = (Double) xmlProperties.get(name).getValue();
                                        Property prop = new Property(name, Property.PropertyType.SPECIFIED, val);
                                        ex.addProperty(prop);
                                    }

                                    perfClient.log(ex);


                                    WorkflowDataServiceOperation op = new WorkflowDataServiceOperation();
                                    op.setTimestamp(new Date(startTime));
                                    op.setEndTimestamp(new Date(endTime));
                                    op.setUserId(ticket.getUserId());
                                    op.setInvocationId(message.getInvocationId());
                                    op.setBlockUUID(getContextId());
                                    op.setServiceName(savedLibrary.getZipDocumentRecord().getName());
                                    op.setServiceId(savedLibrary.getZipDocumentRecord().getId());
                                    op.setVersionId(savedLibrary.getZipDocumentVersion().getId());
                                    op.setVersionNum(savedLibrary.getZipDocumentVersion().getVersionNumber());
                                    op.setWorkflowVersionId(message.getWorkflowVersionId());
                                    op.setDataConsumedSize(totalConsumedDataSize);
                                    op.setDataProducedSize(totalProducedDataSize);
                                    op.setWorkflowId(message.getWorkflowId());
                                    op.setWorkflowEngineIp(parent.getParentEngine().getHostId());
                                    op.setConcurrentServiceInvocations(parent.getServiceCallCount());


                                    int libSize = depData.size();
                                    String[] ids = new String[libSize];
                                    String[] vids = new String[libSize];
                                    String[] names = new String[libSize];
                                    for (int i = 0; i < libSize; i++) {
                                        ids[i] = depData.stringValue("Dependency" + i + "ID", null);
                                        vids[i] = depData.stringValue("Dependency" + i + "VersionID", null);
                                        names[i] = depData.stringValue("Dependency" + i + "Name", null);
                                    }
                                    op.setDependencyIds(ids);
                                    op.setDependencyNames(names);
                                    op.setDependencyVersionIds(vids);

                                    XmlDataStoreByteArrayIO writer = new XmlDataStoreByteArrayIO(props);
                                    op.setPropertiesData(writer.toByteArray());

                                    op.setIdempotent(message.isIdempotent());
                                    op.setDeterministic(message.isDeterministic());
                                    op.setProjectId(ticket.getDefaultProjectId());

                                    if (SigarData.SYSTEM_DATA.isAvailable()) {
                                        op.setAverageCpuSpeed(SigarData.SYSTEM_DATA.getAverageCpuSpeed());
                                        op.setCpuVendor(SigarData.SYSTEM_DATA.getCpuVendor());
                                        op.setCpuModel(SigarData.SYSTEM_DATA.getCpuModel());
                                        op.setAverageCpuCacheSize(SigarData.SYSTEM_DATA.getAverageCpuCacheSize());
                                        op.setPhysicalRam(SigarData.SYSTEM_DATA.getPhysicalRam());
                                        op.setOperatingSystem(SigarData.SYSTEM_DATA.getOperatingSystem());
                                        op.setCpuCount(SigarData.SYSTEM_DATA.getCpuCount());
                                        op.setArchitecture(SigarData.SYSTEM_DATA.getArchitecture());
                                    }

//

                                    provClient.log(op);

                                } catch (Exception ex) {
                                    logger.error("Error sending provenance data. InvocationID=" + getInvocationId() + " BlockID=" + getContextId());
                                }
                            }

                            responseDestination.postResponseMessage(response);

                        } else {
                            // No message found, send back a standard error response
                            DataProcessorResponseMessage response = new DataProcessorResponseMessage(message.getInvocationId(), message.getContextId());
                            response.setStatus(DataProcessorResponseMessage.SERVICE_EXECUTION_ERROR);
                            response.setStatusMessage("Service did not write a response message");
                            response.setCommandOutput(appendDownloadReport(outData));
                            responseDestination.postResponseMessage(response);
                        }


                    } catch (Exception e) {
                        logger.error("Error loading response message. InvocationID=" + getInvocationId() + " BlockID=" + getContextId(), e);
                        notifyError("Error loading response message", e.getMessage());
                    }
                } else {
                    logger.debug("Service Execution killed. InvocationID=" + getInvocationId() + " BlockID=" + getContextId());
                }

            } else {
                notifyError("Error executing process", "External process already exists");
            }
        } catch (Exception e) {
            running.set(false);
            logger.debug("Exception executing service process. InvocationID=" + getInvocationId() + " BlockID=" + getContextId(), e);
            notifyError("Error executing process", e.getMessage());
        }
    }

    /**
     * Append the library download report to a string
     */
    private String appendDownloadReport(String message) {
        if (report.size() > 0) {
            return report.toString() + "\n" + message;
        } else {
            return message;
        }
    }

    /**
     * Add an external process
     */
    public void addExternalPID(long pid){
        logger.debug("Adding PID=" + pid + " for IncocationID=" + getInvocationId() + " ContextID=" + getContextId());
        if(externalProcess!=null){
            try {
                externalProcess.addExternalPID(pid);
            } catch (Exception e){
                logger.error("Error adding external PID: " + e.getMessage());
            }
        }
    }
    
    /**
     * Terminate this processor
     */
    public void kill() {
        killed = true;
        if (externalProcess != null) {
            externalProcess.kill();
        }
        notifyError("Process killed", "Service has been terminated", DataProcessorResponseMessage.SERVICE_KILLED);
    }

    /**
     * Terminate this process due to timeout
     */
    public void timeoutKill() {
        killed = true;
        if (externalProcess != null) {
            externalProcess.kill();
        }
        notifyError("Process Timeout", "Service has been terminated due to timeout", DataProcessorResponseMessage.SERVICE_TIMEOUT);
    }

    /**
     * Send an error message back to the engine
     */
    private void notifyError(String errorMessage, String commandOutput) {
        DataProcessorResponseMessage msg = new DataProcessorResponseMessage();
        msg.setInvocationId(message.getInvocationId());
        msg.setContextId(message.getContextId());
        msg.setStatus(DataProcessorResponseMessage.SERVICE_EXECUTION_ERROR);
        msg.setStatusMessage(errorMessage);
        msg.setCommandOutput(appendDownloadReport(commandOutput));
        try {
            responseDestination.postResponseMessage(msg);
        } catch (Exception e) {
            logger.error("Error sending response message. InvocationID=" + getInvocationId(), e);
        }
    }

    /**
     * Notify an error with a message and a code
     */
    private void notifyError(String errorMessage, String commandOutput, int statusCode) {
        DataProcessorResponseMessage msg = new DataProcessorResponseMessage();
        msg.setInvocationId(message.getInvocationId());
        msg.setContextId(message.getContextId());
        msg.setStatus(statusCode);
        msg.setStatusMessage(errorMessage);
        msg.setCommandOutput(appendDownloadReport(commandOutput));
        try {
            responseDestination.postResponseMessage(msg);
        } catch (Exception e) {
            logger.error("Error sending response message. InvocationID=" + getInvocationId(), e);
        }
    }
}
