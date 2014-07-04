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
package com.connexience.server.workflow.blocks.processor;

import org.pipeline.core.drawing.*;
import org.pipeline.core.drawing.customio.*;
import org.pipeline.core.drawing.model.*;
import org.pipeline.core.xmlstorage.*;

import com.connexience.server.workflow.service.*;
import com.connexience.server.workflow.engine.*;
import com.connexience.server.model.security.*;
import com.connexience.server.util.*;
import com.connexience.server.workflow.service.clients.AutoDeployDataProcessorClient;
import com.connexience.server.workflow.service.clients.RPCDataProcessorClient;

import java.util.*;
import java.io.*;
import org.apache.log4j.*;

/**
 * This block invokes a generic data processor service.
 * @author hugo
 */
public class DataProcessorBlock extends CustomisableDefaultBlockModel {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(DataProcessorBlock.class);
    /** Service definition */
    private DataProcessorServiceDefinition definition = null;
    
    /** Execution status flag. This is set to true by the workflow engine when an update
     * message is received */
    private boolean finished = false;
    
    /** Workflow invocation ID */
    private String invocationId = "";
    
    /** Global data store */
    private GlobalDataSource globalStore;
    
    /** Security ticket */
    private Ticket ticket;
    
    /** Data transfer type */
    private String dataTransferType = DataProcessorDataSource.FILE_DATA_SOURCE;

    /** ID of the service used for this block */
    private String serviceId;

    /** ID Of the service version to use for this block */
    private String versionId = null;

    /** ID of the workflow document */
    private String workflowId = null;

    /** ID of the workflow version */
    private String workflowVersionId = null;
    
    /** Does this processor use the latest version of the service */
    private boolean usesLatest = true;
    
    /** Version number of the service */
    private int versionNumber;

    /** Message plan used to create messages */
    private WorkflowInvocationMessagePlan messagePlan;

    /** Link used to fetch service definitions on demand */
    private DataProcessorServiceFetcher serviceFetcher;

    /** Is this a dynamic service */
    private boolean dynamicService = false;

    /** Is this block currently fetching its service definition */
    private boolean fetchingDefinition = false;

    /** Block description */
    private String description = "Data processor block";

    /** Is the service being called idempotent */
    private boolean idempotent = true;

    /** Is the service being called deterministic */
    private boolean deterministic = true;

    /** Ignore unconnected inputs when creating call message. This is used in the debugger 
     * and is false by default */
    private boolean unconnectedInputErrorsIgnored = false;

    /** Username to run processes as */
    private String systemUsername = "";
    
    /** Run processes as a different user */
    private boolean runAsDifferentUser = false;
    
    public DataProcessorBlock() throws DrawingException {
        super();
        setRendererClass(DataProcessorBlockRenderer.class);
        setLabel("Processor");
        getEditableProperties().add("EnforceInvocationTimeout", true, "Should the invocation timeout be enforced for this block");
        getEditableProperties().add("InvocationTimeout", 3600, "Maximum waiting time for service (seconds)");
        getEditableProperties().add("StreamingChunkSize", 1000, "Number of rows to pass through the block in any single streaming chunk");
        getEditableProperties().add("StdOutSize", 4096, "Size of the standard output capture buffer that will be sent back to the database");
        getEditableProperties().add("ProgressUpdateInterval", 30, "Number of seconds that must elapse before a progress update message is sent");
        getEditableProperties().add("AllowRetriesOnTimeout", false, "Should the workflow engine attempt to rerun the block if the execution times out");
        getEditableProperties().add("TimeoutRetries", 1, "Number of times the workflow engine will try to rerun this block if the execution times out");
    }

    /** Set the username that will be used to execute blocks */
    public void setSystemUsername(String systemUsername) {
        this.systemUsername = systemUsername;
    }

    /** Get the username that will be used to execute blocks */
    public String getSystemUsername() {
        return systemUsername;
    }

    /** Set whether to execute blocks as a different user */
    public void setRunAsDifferentUser(boolean runAsDifferentUser) {
        this.runAsDifferentUser = runAsDifferentUser;
    }

    /** Should blocks be executed as a different user */
    public boolean isRunAsDifferentUser() {
        return runAsDifferentUser;
    }
    
    /** Check whether errors when listing unconnected inputs are ignored */
    public boolean isUnconnectedInputErrorsIgnored() {
        return unconnectedInputErrorsIgnored;
    }

    /** Get whether errors when listing unconnected inputs are ignored */
    public void setUnconnectedInputErrorsIgnored(boolean unconnectedInputErrorsIgnored) {
        this.unconnectedInputErrorsIgnored = unconnectedInputErrorsIgnored;
    }

    
    /** Is this block currently fetching its service definition */
    public boolean isFetchingDefinition(){
        return fetchingDefinition;
    }
    
    /** Is this a dynamic service */
    public boolean isDynamicService(){
        return dynamicService;
    }

    /** Set whether this is a dynamic service */
    public void setDynamicService(boolean dynamicService){
        this.dynamicService = dynamicService;
    }
    
    /** Get the service definition object */
    public DataProcessorServiceDefinition getServiceDefinition() throws BlockExecutionException {
        if(definition==null){
            try {
                fetchServiceSync();
            } catch (Exception e){
                throw new BlockExecutionException("Error getting service definition: " + e.getMessage());
            }
        }
        return definition;
    }

    /** Set whether this block uses the latest version of its assigned service */
    public void setUsesLatest(boolean usesLatest){
        this.usesLatest = usesLatest;
    }

    /** Set whether this block uses the latest version of its assigned service */
    public boolean getUsesLatest(){
        return usesLatest;
    }
    
    /** Get the service version number */
    public int getVersionNumber(){
        return versionNumber;
    }

    /** Set the service version number */
    public void setVersionNumber(int versionNumber){
        this.versionNumber = versionNumber;
    }
    
    /** Set the service fetcher object */
    public void setServiceFetcher(DataProcessorServiceFetcher serviceFetcher){
        this.serviceFetcher = serviceFetcher;
    }

    /** Get the service fetcher object */
    public DataProcessorServiceFetcher getServiceFetcher(){
        return serviceFetcher;
    }

    /** Get the ID of the workflow document */
    public String getWorkflowId(){
        return workflowId;
    }

    /** Set the ID of the workflow document */
    public void setWorkflowId(String workflowId){
        this.workflowId = workflowId;
    }

    /** Set the ID of the workflow version */
    public void setWorkflowVersionId(String workflowVersionId) {
        this.workflowVersionId = workflowVersionId;
    }

    /** Get the ID of the workflow version */
    public String getWorkflowVersionId() {
        return workflowVersionId;
    }
    
    /** Set the service ID */
    public String getServiceId(){
        return serviceId;
    }

    /** Get the service ID */
    public void setServiceId(String serviceId){
        this.serviceId = serviceId;
    }

    /** Set the service version ID */
    public void setVersionId(String versionId){
        this.versionId = versionId;
    }

    /** Get the service version ID */
    public String getVersionId(){
        return versionId;
    }

    /** Set the message plan reference */
    public void setMessagePlan(WorkflowInvocationMessagePlan messagePlan){
        this.messagePlan = messagePlan;
    }
    
    /** Set the data transfer type */
    public void setDataTransferType(String dataTransferType){
        this.dataTransferType = dataTransferType;
    }
    
    /** Set the invocation ID */
    public void setInvocationId(String invocationId){
        this.invocationId = invocationId;
    }
    
    /** Set the global data source */
    public void setGlobalDataStore(GlobalDataSource globalStore) {
        this.globalStore = globalStore;
    }
    
    /** Is this execution finished */
    public synchronized boolean isFinished(){
        return finished;
    }
    
    /** Set the status of this block to finished */
    public synchronized void setFinished(){
        finished = true;
    }
    
    /** Set the DataProcessorService that this block will invoke */
    public void setServiceDefinition(DataProcessorServiceDefinition definition) {
        this.definition = definition;
        if(definition.getServiceType()==DataProcessorServiceDefinition.AUTODEPLOY_SERVICE){
            dynamicService = true;
        } else {
            dynamicService = false;
        }
    }
    
    /** Set the security ticket */
    public void setTicket(Ticket ticket){
        this.ticket = ticket;
    }

    public boolean isIdempotent() {
        return idempotent;
    }

    public void setIdempotent(boolean idempotent) {
        this.idempotent = idempotent;
    }

    public boolean isDeterministic() {
        return deterministic;
    }

    public void setDeterministic(boolean deterministic) {
        this.deterministic = deterministic;
    }

    /** Fetch the service from a remote machine asynchronously. This is used when
     editing a workflow using the GUI */
    public void fetchServiceAsync(){
        if(serviceFetcher!=null){
            new Thread(new Runnable(){
                public void run(){
                    fetchingDefinition = true;
                    requestRedraw();
                    try {
                        if(usesLatest){
                            definition = serviceFetcher.getServiceDefinition(serviceId);
                            versionNumber = definition.getVersionNumber();
                        } else {
                            definition = serviceFetcher.getServiceDefinition(serviceId, versionId);
                            versionNumber = definition.getVersionNumber();
                        }
                        initialiseForService();
                        fetchingDefinition = false;
                        requestRedraw();
                    } catch (Exception e){
                        fetchingDefinition = false;
                        setLabel(e.getMessage());
                        requestRedraw();
                    }

                }
            }).start();

        }
    }

    private void requestRedraw(){
        javax.swing.SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                getParentDrawing().redrawRequest();
            }
        });
    }

    /** Fetch the service from a remote machine synchronously. This is used when the
     service is executing on the server and a check is being made prior to creating the
     service message */
    public void fetchServiceSync() throws Exception {
        try {
            fetchingDefinition = true;
            if(serviceFetcher!=null){
                if(usesLatest){
                    definition = serviceFetcher.getServiceDefinition(serviceId);
                    if(definition.getServiceType()==DataProcessorServiceDefinition.AUTODEPLOY_SERVICE){
                        dynamicService = true;
                    } else {
                        dynamicService = false;
                    }
                    versionNumber = definition.getVersionNumber();
                } else {
                    definition = serviceFetcher.getServiceDefinition(serviceId, versionId);
                    versionNumber = definition.getVersionNumber();
                }
                resetDataTypes();
            }
        } catch (Exception e){
            throw e;
        } finally {
            fetchingDefinition = false;
        }
    }

    /** Setup the block inputs and outputs by querying the service */
    public void initialiseForService() throws DrawingException, BlockExecutionException {
        if(getServiceDefinition()!=null){
            if(getServiceDefinition().getServiceType()==DataProcessorServiceDefinition.AUTODEPLOY_SERVICE){
                dynamicService = true;
            } else {
                dynamicService = false;
            }

            setLabel(getServiceDefinition().getName());
            
            // Copy the properties
            try {
                getEditableProperties().copyProperties(getServiceDefinition().getServiceProperties());
            } catch (Exception e){
                throw new DrawingException("Error setting service properties");
            }
            description = getServiceDefinition().getDescription();
            
            getInputDefinitions().removeAllDefinitions();
            getOutputDefinitions().removeAllDefinitions();

            idempotent = getServiceDefinition().isIdempotent();
            deterministic = getServiceDefinition().isDeterministic();
            
            // Create the inputs
            Iterator<DataProcessorIODefinition> inputs = getServiceDefinition().getInputs().iterator();
            DataProcessorIODefinition ioDef;
            CustomInputDefinition inPortDef;
            CustomPortDefinition outPortDef;
            
            while(inputs.hasNext()){
                ioDef = inputs.next();
                if(ioDef.getType()==DataProcessorIODefinition.INPUT_DEFINITION){
                    inPortDef = (CustomInputDefinition)getInputDefinitions().createDefinition(ioDef.getName());
                    inPortDef.setDataType(DataTypes.getDataType(ioDef.getDataTypeName()));
                    inPortDef.setDataTypeName(ioDef.getDataTypeName());
                    if(ioDef.getMode().equals(DataProcessorIODefinition.STREAMING_CONNECTION)){
                        inPortDef.setStreamable(true);
                    } else {
                        inPortDef.setStreamable(false);
                    }
                    inPortDef.setOptional(ioDef.isOptional());
                }       
            }
            
            // Create the outputs
            Iterator<DataProcessorIODefinition> outputs = getServiceDefinition().getOutputs().iterator();
            while(outputs.hasNext()){
                ioDef = outputs.next();
                if(ioDef.getType()==DataProcessorIODefinition.OUPUT_DEFINITION){
                    outPortDef = getOutputDefinitions().createDefinition(ioDef.getName());
                    outPortDef.setDataType(DataTypes.getDataType(ioDef.getDataTypeName()));
                    outPortDef.setDataTypeName(ioDef.getDataTypeName());
                }
            }
            
            repositionPorts(getInputDefinitions());
            repositionPorts(getOutputDefinitions());
            syncIOPorts();
        } else {
            throw new DrawingException("No service defined");
        }
    }

    /** Reset the input and output data types */
    public void resetDataTypes(){
        String typeName;
        String portName;
        DataType type;
        DefaultInputPortModel input;
        DefaultOutputPortModel output;

        for(int i=0;i<getInputDefinitions().getDefinitionCount();i++){
            try {
                typeName = getInputDefinitions().getDefinition(i).getDataTypeName();
                if(!typeName.trim().equals("")){
                    portName = getInputDefinitions().getDefinition(i).getName();
                    input = (DefaultInputPortModel)getInput(portName);
                    type = DataTypes.getDataType(typeName);
                    if(type!=null){
                        input.addDataType(type);
                        input.setDataTypeRestricted(true);
                    }
                }

            } catch (Exception e){
                logger.error("Error setting input data type", e);
            }
        }

        for(int i=0;i<getOutputDefinitions().getDefinitionCount();i++){
            try {
                typeName = getOutputDefinitions().getDefinition(i).getDataTypeName();
                if(!typeName.trim().equals("")){
                    portName = getOutputDefinitions().getDefinition(i).getName();
                    output = (DefaultOutputPortModel)getOutput(portName);
                    type = DataTypes.getDataType(typeName);
                    if(type!=null){
                        output.addDataType(type);
                        output.setDataTypeRestricted(true);
                    }
                }

            } catch (Exception e){
                logger.error("Error setting output data type", e);
            }

        }
    }
    
    /** Create the service client */
    private DataProcessorClient createClient() throws BlockExecutionException {
        getServiceDefinition();
        if(definition!=null){
            int serviceType = getServiceDefinition().getServiceType();
            DataProcessorClient client = null;

            switch(serviceType){
                case DataProcessorServiceDefinition.RPC_SERVICE:
                    client = new RPCDataProcessorClient();
                    break;

                case DataProcessorServiceDefinition.AUTODEPLOY_SERVICE:
                    client = new AutoDeployDataProcessorClient();
                    break;
            }

            if(client!=null){
                client.setUrl(getServiceDefinition().getServiceUrl());
                client.setTimeout(getEditableProperties().intValue("InvocationTimeout", 3600));
            }
            
            return client;
        } else {
            logger.error("Could not fetch service definition");
            throw new BlockExecutionException("Could not fetch service definition");
        }
    }
    
    /** Reposition a set of inputs or outputs */
    private void repositionPorts(CustomPortDefinitionList ports){
        int size = ports.getDefinitionCount();
        switch(size){
            case 0:
                break;
                
            case 1:
                ports.getDefinition(0).setOffset(50);
                break;
                
            case 2:
                ports.getDefinition(0).setOffset(30);
                ports.getDefinition(1).setOffset(70);
                break;
                
            case 3:
                ports.getDefinition(0).setOffset(20);
                ports.getDefinition(1).setOffset(50);
                ports.getDefinition(2).setOffset(80);
                break;
                
            case 4:
                ports.getDefinition(0).setOffset(20);
                ports.getDefinition(1).setOffset(40);
                ports.getDefinition(2).setOffset(60);
                ports.getDefinition(3).setOffset(80);
                break;
                
            case 5:
                ports.getDefinition(0).setOffset(10);
                ports.getDefinition(1).setOffset(30);
                ports.getDefinition(2).setOffset(50);
                ports.getDefinition(3).setOffset(70);
                ports.getDefinition(4).setOffset(90);
                break;
                
            default:
                int step = 80 / size;
                
                for(int i=0;i<ports.getDefinitionCount();i++){
                    ports.getDefinition(i).setOffset(10 + (i * step));
                }
        }
    }
    
    /** Create the invocation message */
    public DataProcessorCallMessage createCallMessage() throws DrawingException, BlockExecutionException {
        DataProcessorCallMessage message = new DataProcessorCallMessage();
        
        // Set the debugging flags
        message.setDebugEnabled(getEditableProperties().booleanValue("DebugMode", false));
        message.setDebugSuspended(getEditableProperties().booleanValue("DebugSuspended", true));
        message.setDebugPort(getEditableProperties().intValue("DebugPort", 5005));
        message.setMaxStdOutBufferSize(getEditableProperties().intValue("StdOutSize", 4096));
        CustomPortDefinitionList inputs = this.getInputDefinitions();
        
        // Security
        message.setRunAsDifferentUser(runAsDifferentUser);
        message.setSystemUsername(systemUsername);
        
        String[] sourceList = new String[inputs.getDefinitionCount()];
        String[] sourceTypeList = new String[inputs.getDefinitionCount()];
        String[] sourceConnections = new String[inputs.getDefinitionCount()];
        String[] sourceConnectionContexts = new String[inputs.getDefinitionCount()];
        String[] sourceModes = new String[inputs.getDefinitionCount()];
        OutputPortModel port;
        
        for(int i=0;i<inputs.getDefinitionCount();i++){
            sourceList[i] = inputs.getDefinition(i).getName();
            sourceTypeList[i] = inputs.getDefinition(i).getDataTypeName();

            if(inputs.getDefinition(i).isStreamable()){
                sourceModes[i] = DataProcessorIODefinition.STREAMING_CONNECTION;
            } else {
                sourceModes[i] = DataProcessorIODefinition.NON_STREAMING_CONNECTION;
            }
            
            port = getConnectedOutput(inputs.getDefinition(i).getName());
            if(port!=null){
                sourceConnections[i] = port.getName();
                sourceConnectionContexts[i] = port.getParentBlock().getBlockGUID();
            } else {
                sourceConnections[i] = "???";
                sourceConnectionContexts[i] = "???";
            }
            
        }
        
        message.setDataSources(sourceList);
        message.setDataSourceTypes(sourceTypeList);
        message.setDataSourceConnections(sourceConnections);
        message.setDataSourceConnectionContexts(sourceConnectionContexts);
        message.setDataSourceModes(sourceModes);
        
        CustomPortDefinitionList outputs = this.getOutputDefinitions();
        String[] outputList = new String[outputs.getDefinitionCount()];
        String[] outputTypeList = new String[outputs.getDefinitionCount()];
        
        for(int i=0;i<outputs.getDefinitionCount();i++){
            outputList[i] = outputs.getDefinition(i).getName();
            outputTypeList[i] = outputs.getDefinition(i).getDataTypeName();

        }
        message.setDataOutputs(outputList);
        message.setDataOutputTypes(outputTypeList);
        
        // Set the various IDs in the message */
        message.setContextId(getBlockGUID());
        message.setInvocationId(invocationId);
        message.setServiceRoutine(getServiceDefinition().getServiceRoutine());
        message.setServiceUrl(getServiceDefinition().getServiceUrl());
        message.setStorageUrl(globalStore.getBaseDirectory());
        message.setDataTransferType(dataTransferType);
        message.setStreamMode(getServiceDefinition().getStreamMode());
        message.setWorkflowId(workflowId);
        message.setWorkflowVersionId(workflowVersionId);
        message.setIdempotent(idempotent);
        message.setDeterministic(deterministic);
        message.setOkToRetry(getEditableProperties().booleanValue("AllowRetries", false));
        message.setRetryAttempts(getEditableProperties().intValue("TimeoutRetries", 2));
        message.setMetadataPropagationMode(getServiceDefinition().getMetadataPropagationMode());
        
        // Dynamic service properties
        if(getServiceDefinition().getServiceType()==DataProcessorServiceDefinition.AUTODEPLOY_SERVICE){
            message.setServiceId(serviceId);
            message.setVersionId(versionId);
            message.setUsesLatest(usesLatest);
        }

        //XmlDataStore props = getEditableProperties();
        
        // Scripting properties
        message.setServiceBackend(getServiceDefinition().getServiceBackend());
        message.setScriptId(getServiceDefinition().getScriptId());
        
        try {
            message.getProperties().copyProperties(getEditableProperties());
        } catch (Exception e) {
            throw new DrawingException("Cannot set service properties");
        }
                
        try {
            message.setTicketData(SerializationUtils.serialize(ticket));
        } catch (IOException ioe){
            message.setTicketData(new byte[0]);
        }
        return message;
    }

    /** Get the output port connected to a specific input */
    private OutputPortModel getConnectedOutput(String inputName) throws DrawingException {
        try {
            InputPortModel input = getInput(inputName);
            Enumeration<?> e = input.connections();
            if(e.hasMoreElements()){
                ConnectionModel link = (ConnectionModel)e.nextElement();
                if(link.getSourcePort()!=null){
                    return link.getSourcePort();
                } else {
                    throw new DrawingException("No source block found for input: " + inputName);
                }
            } else {
                if(!input.isOptional()){
                    throw new DrawingException("Input: " + inputName + " is not connected");
                } else {
                    return null;
                }
            }
        } catch (DrawingException de){
            if(!unconnectedInputErrorsIgnored){
                throw de;
            } else {
                return null;
            }
        }       
    }    
    
    @Override
    public BlockExecutionReport execute() throws BlockExecutionException {
        
        DataProcessorClient client = createClient();
        if(client==null){
            logger.error("Could not execute DataProcessorBlock. No service client");
            return new BlockExecutionReport(this, BlockExecutionReport.INTERNAL_ERROR, "Cannot create service client");
        }
        
        // Create a call message
        DataProcessorCallMessage message = null;
        
        try {
            message = createCallMessage();
        } catch(DrawingException de){
            logger.error("Exception creating call message in DataProcessorBlock", de);
            return new BlockExecutionReport(this, BlockExecutionReport.INPUT_DATA_ERROR);
        }

        // Updated message plan code
        WorkflowInvocationMessagePlan.MessageItem item = messagePlan.createMessageItem(message, client);
        item.setOkToRetry(getEditableProperties().booleanValue("AllowRetriesOnTimeout", false));
        item.setRemainingRetries(getEditableProperties().intValue("TimeoutRetries", 1));
        messagePlan.push(item);

        // Must be Ok if we get this far
        return new BlockExecutionReport(this);
    }

    /** Get the block description */
    public String getDescription(){
        return description;
    }

    /** Set the block description */
    public void setDescription(String description){
        this.description = description;
    }
    
    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        /*
        if(definition!=null){
            store.add("ServiceDefinition", definition);
        }
         */
        store.add("ServiceID", serviceId);
        store.add("VersionID", versionId);
        store.add("VersionNumber", versionNumber);
        store.add("UsesLatestVersion", usesLatest);
        store.add("DynamicService", dynamicService);
        store.add("Description", description);
        store.add("Idempotent", idempotent);
        store.add("Deterministic", deterministic);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        super.recreateObject(xmlDataStore);
        /*
        if(xmlDataStore.containsName("ServiceDefinition")){
            definition = (DataProcessorServiceDefinition)xmlDataStore.xmlStorableValue("ServiceDefinition");
        } else {
            definition = null;
        }
         */
        dynamicService = xmlDataStore.booleanValue("DynamicService", false);
        serviceId = xmlDataStore.stringValue("ServiceID", null);
        versionId = xmlDataStore.stringValue("VersionID", null);
        versionNumber = xmlDataStore.intValue("VersionNumber", 0);
        usesLatest = xmlDataStore.booleanValue("UsesLatestVersion", true);
        description = xmlDataStore.stringValue("Description", "Data processor block");
        idempotent = xmlDataStore.booleanValue("Idempotent", true);
        deterministic = xmlDataStore.booleanValue("Deterministic", true);

        resetDataTypes();
        
    }
}