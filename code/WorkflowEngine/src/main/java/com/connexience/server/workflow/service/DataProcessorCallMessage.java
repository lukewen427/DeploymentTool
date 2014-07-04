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
package com.connexience.server.workflow.service;

import org.pipeline.core.xmlstorage.*;

import java.io.*;

/**
 * This class represents the message that is used to initiate a remote
 * data processor. It contains links to the source DataTransfer objects
 * and also the names of the required DataTransfer response objects.
 * @author hugo
 */
public class DataProcessorCallMessage implements Serializable, XmlStorable {
	private static final long serialVersionUID = 1L;

	/** Workflow invocation id */
    private String invocationId = "";

    /** Namespace for the XML document version of this message */
    public static final String XML_NAMESPACE = "http://www.connexience.com/namespace/dataprocessorcallmessage/v1";
    /** Invocation context id. 
     * This refers to the part of the workflow  that triggered the
     * call. Typically this is the GUID of the workflow block */
    private String contextId = "";

    /** List of modes for the data sources */
    private String[] dataSourceModes = new String[0];

    /** List of source data objects */
    private String[] dataSources = new String[0];
    
    /** List of types for data sources */
    private String[] dataSourceTypes = new String[0];
    
    /** List of the ports connected to the data sources. These are
     * needed to get the correct data from the server */
    private String[] dataSourceConnections = new String[0];
    
    /** List of the blocks connected to the data sources */
    private String[] dataSourceConnectionContexts = new String[0];
    
    /** List of required return objects */
    private String[] dataOutputs = new String[0];
    
    /** List of types for return data objects */
    private String[] dataOutputTypes = new String[0];
    
    /** Routine to call within the service */
    private String serviceRoutine = "";
    
    /** URL of the server */
    private String serviceUrl = "";
    
    /** URL of the data storage location */
    private String storageUrl = "";
    
    /** Type of data store */
    private String dataTransferType = DataProcessorDataSource.FILE_DATA_SOURCE;
    
    /** List of service property values */
    private XmlDataStore properties = new XmlDataStore();
    
    /** Security ticket data */
    private byte[] ticketData = new byte[0];
    
    /** Back end type for this service */
    private String serviceBackend = DataProcessorServiceDefinition.NO_SCRIPT;

    /** Streaming Mode */
    private String streamMode = DataProcessorServiceDefinition.STREAM_NO_STREAM_MODE;
    
    /** ID Of the script resource for this service if there is one */
    private String scriptId = "";

    /** Service ID for dynamic services */
    private String serviceId = "";

    /** Version ID for versioned service */
    private String versionId = "";

    /** Is the latest version to be used */
    private boolean useLatest = true;

    /** ID of the workflow document that contains this service */
    private String workflowId = null;

    /** ID of the workflow version */
    private String workflowVersionId = null;
    
    /** Maximum number of standard output data to buffer */
    private int maxStdOutBufferSize = 4096;

    /** Should the process allow a debugger to attach */
    private boolean debugEnabled = false;

    /** Should the process freeze and wait for a debugger to attach */
    private boolean debugSuspended = true;

    /** Debugger port to start on */
    private int debugPort = 5005;

    /** Is the service being called idempotent */
    private boolean idempotent = true;

    /** Is the service being called deterministic*/
    private boolean deterministic = true;

    /** Is it ok to retry this message */
    private boolean okToRetry = false;
    
    /** Maximum number of permitted retry attempts */
    private int retryAttempts = 0;
    
    /** Metadata propagation mode */
    private String metadataPropagationMode = DataProcessorServiceDefinition.META_DATA_DEFAULT_PROPAGATION;
    
    /** Username to run processes as */
    private String systemUsername = "";
    
    /** Run processes as a different user */
    private boolean runAsDifferentUser = false;
    
    /** Create an empty response message */
    public DataProcessorCallMessage(){
        
    }
    
    /** Create with invocation and context ids */
    public DataProcessorCallMessage(String invocationId, String contextId){
        this.contextId = contextId;
        this.invocationId = invocationId;
    }

    /** Does this message contain an input with a specific data type */
    public boolean containsInputType(String inputType){
        for(String s : dataSourceTypes){
            if(s.equals(inputType)){
                return true;
            }
        }
        return false;
    }
    
    /** Get the maximumum number of bytes to store from the standard output of the process used to execute this service */
    public int getStdOutBufferSize(){
        return maxStdOutBufferSize;
    }

    /** Set the maximumum number of bytes to store from the standard output of the process used to execute this service */
    public void setMaxStdOutBufferSize(int maxStdOutBufferSize) {
        if(maxStdOutBufferSize>=0){
            this.maxStdOutBufferSize = maxStdOutBufferSize;
        }
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
    
    /** Get the metadata propagation mode */
    public String getMetadataPropagationMode() {
        return metadataPropagationMode;
    }
    
    /** Set the metadata propagation mode */
    public void setMetadataPropagationMode(String metadataPropagationMode) {
        this.metadataPropagationMode = metadataPropagationMode;
    }

    /** Get the streaming mode */
    public String getStreamMode(){
        return streamMode;
    }

    /** Set the streaming mode */
    public void setStreamMode(String streamMode){
        String lsm = streamMode.toLowerCase();
        if(lsm.equals(DataProcessorServiceDefinition.STREAM_NO_STREAM_MODE) || lsm.equals(DataProcessorServiceDefinition.STREAM_PARALLEL_MODE) || lsm.equals(DataProcessorServiceDefinition.STREAM_SQEUENTIAL_MODE)){
            this.streamMode = lsm;
        }
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

    /** Get the service ID. This is used for dynamically deployed services */
    public String getServiceId(){
        return serviceId;
    }

    /** Set the service ID. This is used for dynamically deployed services */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /** Does a dynamic version use the latest service version */
    public boolean usesLatestVersion(){
        return useLatest;
    }

    /** Set whether a dynamic version use the latest service version */
    public void setUsesLatest(boolean useLatest){
        this.useLatest = useLatest;
    }

    /** Get the specific ID of a dynamic service to call */
    public String getVersionId(){
        return versionId;
    }

    /** Get the specific ID of a dynamic service to call */
    public void setVersionId(String versionId){
        this.versionId = versionId;
    }
    
    /** Get the ID of the script file that provides this service */
    public String getScriptId(){
        return scriptId;
    }

    /** Set the ID of the script file that provides this service */
    public void setScriptId(String scriptId){
        this.scriptId = scriptId;
    }
    
    /** Get the provisioning back end type for this service. This will
     * either be a Java provisioned internal service or an interpreted service
     * such as an R-Script or JavaScript file */
    public String getServiceBackend(){
        return serviceBackend;
    } 
    
    /** Get the provisioning back end type for this service. This will
     * either be a Java provisioned internal service or an interpreted service
     * such as an R-Script or JavaScript file */
    public void setServiceBackend(String serviceBackend){
        this.serviceBackend = serviceBackend;
    } 

    /** Set the transfer modes of the inputs */
    public void setDataSourceModes(String[] dataSourceModes){
        this.dataSourceModes = dataSourceModes;
    }

    /** Get the transfer modes of the inputs */
    public String[] getDataSourceModes(){
        return dataSourceModes;
    }

    /** Set the data source names */
    public void setDataSources(String[] dataSources){
        this.dataSources = dataSources;
    }
    
    /** Get the data source names */
    public String[] getDataSources(){
        return dataSources;
    }
    
    /** Get a list of data source type names */
    public String[] getDataSourceTypes(){
        return dataSourceTypes;
    }
    
    /** Get a list of the ports connected to the inputs */
    public String[] getDataSourceConnections(){
        return dataSourceConnections;
    }
    
    /** Set the list of ports connected to the inputs */
    public void setDataSourceConnections(String[] dataSourceConnections){
        this.dataSourceConnections = dataSourceConnections;
    }
    
    /** Get a list of the block contexts attached to each of the inputs */
    public String[] getDataSourceConnectionContexts(){
        return dataSourceConnectionContexts;
    }
    
    /** Set the list of the block contexts attached to each of the inputs */
    public void setDataSourceConnectionContexts(String[] dataSourceConnectionContexts){
        this.dataSourceConnectionContexts = dataSourceConnectionContexts;
    }
    
    /** Get the source data type for a specific input */
    public String getDataSourceType(String inputName) {
        for(int i=0;i<dataSources.length;i++){
            if(dataSources[i].equals(inputName)){
                return dataSourceTypes[i];
            }
        }
        return null;
    }
    
    /** Get the position of a source within the source list */
    public int getDataSourceIndex(String inputName){
        for(int i=0;i<dataSources.length;i++){
            if(dataSources[i].equals(inputName)){
                return i;
            }
        }
        return -1;
    }

    /** Get the mode of an input */
    public String getDataSourceMode(String inputName){
        int index = getDataSourceIndex(inputName);
        if(index!=-1){
            if(index>=0 && index<dataSourceModes.length){
                return dataSourceModes[index];
            } else {
                return DataProcessorIODefinition.NON_STREAMING_CONNECTION;
            }
        } else {
            return DataProcessorIODefinition.NON_STREAMING_CONNECTION;
        }
    }
    
    /** Set the data source types */
    public void setDataSourceTypes(String[] dataSourceTypes){
        this.dataSourceTypes = dataSourceTypes;
    }
    
    /** Get the data outputs */
    public String[] getDataOutputs(){
        return dataOutputs;
    }
    
    /** Set the data outputs */
    public void setDataOutputs(String[] dataOutputs){
        this.dataOutputs = dataOutputs;
    }
    
    /** Get a list of data output type names */
    public String[] getDataOutputTypes(){
        return dataOutputTypes;
    }
    
    /** Get the source data type for a specific output */
    public String getDataOutputType(String outputName) {
        for(int i=0;i<dataOutputs.length;i++){
            if(dataOutputs[i].equals(outputName)){
                return dataOutputTypes[i];
            }
        }
        return null;
    }
    
    /** Set the data output type names */
    public void setDataOutputTypes(String[] dataOutputTypes){
        this.dataOutputTypes = dataOutputTypes;
    }
    
    /** Set the invocation ID */
    public void setInvocationId(String invocationId){
        this.invocationId = invocationId;
    }
    
    /** Set the contextID */
    public void setContextId(String contextId){
        this.contextId = contextId;
    }
    
    /** Get the invocation ID */
    public String getInvocationId(){
        return invocationId;
    }
    
    /** Get the context ID */
    public String getContextId(){
        return contextId;
    }    
    
    /** Set the name of the routine within the service to call */
    public void setServiceRoutine(String serviceRoutine){
        this.serviceRoutine = serviceRoutine;
    }
    
    /** Get the name of the routine within the service to call */
    public String getServiceRoutine(){
        return serviceRoutine;
    }
    
    /** Get the URL of the service that the block will invoke */
    public String getServiceUrl() {
        return serviceUrl;
    }

    /** Set the URL of the service that the block will invoke */
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
    
    /** Set the storage URL */
    public void setStorageUrl(String storageUrl){
        this.storageUrl = storageUrl;
    }
    
    /** Get the storage URL */
    public String getStorageUrl(){
        return storageUrl;
    }
    
    /** Get the data transfer type. This specifies how the service should
     * retrieve and upload data */
    public String getDataTransferType(){
        return dataTransferType;
    }
    
    /** Get the data transfer type. This specifies how the service should
     * retrieve and upload data */
    public void setDataTransferType(String dataTransferType){
        this.dataTransferType = dataTransferType;
    }
    
    /** Get the service properties object */
    public XmlDataStore getProperties(){
        return properties;
    }
    
    /** Set the ticket data */
    public void setTicketData(byte[] ticketData){
        this.ticketData = ticketData;
    }
    
    /** Get the ticket data */
    public byte[] getTicketData(){
        return ticketData;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public boolean isDebugSuspended() {
        return debugSuspended;
    }
    
    public void setDebugSuspended(boolean suspended) {
        debugSuspended = suspended;
    }

    public int getDebugPort() {
        return debugPort;
    }

    public void setDebugPort(int debugPort) {
        this.debugPort = debugPort;
    }

    public boolean isIdempotent()
    {
      return idempotent;
    }

    public void setIdempotent(boolean idempotent)
    {
      this.idempotent = idempotent;
    }

    public boolean isDeterministic()
    {
      return deterministic;
    }

    public void setDeterministic(boolean deterministic)
    {
      this.deterministic = deterministic;
    }

    public void setOkToRetry(boolean okToRetry) {
        this.okToRetry = okToRetry;
    }

    public boolean isOkToRetry() {
        return okToRetry;
    }

    public void setRetryAttempts(int retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    
  /** Save this object to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("DataProcessorMessage");
        store.add("InvocationID", invocationId);
        store.add("ContextID", contextId);     
        store.add("ServiceRoutine", serviceRoutine);
        store.add("ServiceURL", serviceUrl);
        store.add("SourceCount", dataSources.length);
        store.add("StorageURL", storageUrl);
        store.add("DataTransferType", dataTransferType);
        store.add("Properties", properties);
        store.add("TicketData", ticketData);
        store.add("ScriptID", scriptId);
        store.add("ServiceBackend", serviceBackend);
        store.add("StreamMode", streamMode);
        store.add("ServiceID", serviceId);
        store.add("UseLatestServiceVersion", useLatest);
        store.add("VersionID", versionId);
        store.add("MaxStdOutBufferSize", maxStdOutBufferSize);
        store.add("DebugEnabled", debugEnabled);
        store.add("DebugSuspended", debugSuspended);
        store.add("DebugPort", debugPort);
        store.add("WorkflowID", workflowId);
        store.add("WorkflowVersionID", workflowVersionId);
        store.add("Idempotent", idempotent);
        store.add("Deterministic", deterministic);
        store.add("OKToRetry", okToRetry);
        store.add("RetryAttempts", retryAttempts);
        store.add("MetadataPropagationMode", metadataPropagationMode);
        store.add("RunAsDifferentUser", runAsDifferentUser);
        store.add("SystemUserName", systemUsername);
        
        for(int i=0;i<dataSources.length;i++){
            store.add("DataSource" + i, dataSources[i]);
            store.add("DataSourceType" + i, dataSourceTypes[i]);
            store.add("DataSourceConnection" + i, dataSourceConnections[i]);
            store.add("DataSourceConnectionContext" + i, dataSourceConnectionContexts[i]);
            store.add("DataSourceMode" + i, dataSourceModes[i]);
        }
        
        store.add("OutputCount", dataOutputs.length);
        for(int i=0;i<dataOutputs.length;i++){
            store.add("DataOutput" + i, dataOutputs[i]);
            store.add("DataOutputType" + i, dataOutputTypes[i]);
        }
        return store;
    }

    /** Recreate this object from storage */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        this.invocationId = store.stringValue("InvocationID", "");
        this.contextId = store.stringValue("ContextID", contextId);        
        serviceRoutine = store.stringValue("ServiceRoutine", "");
        serviceUrl = store.stringValue("ServiceURL", ""); 
        storageUrl = store.stringValue("StorageURL", "");
        scriptId = store.stringValue("ScriptID", "");
        serviceBackend = store.stringValue("ServiceBackend", DataProcessorServiceDefinition.NO_SCRIPT);
        streamMode = store.stringValue("StreamMode", DataProcessorServiceDefinition.STREAM_NO_STREAM_MODE);
        serviceId = store.stringValue("ServiceID", "");
        versionId = store.stringValue("VersionID", "");
        useLatest = store.booleanValue("UseLatestServiceVersion", true);
        maxStdOutBufferSize = store.intValue("MaxStdOutBufferSize", 4096);
        debugEnabled = store.booleanValue("DebugEnabled", false);
        debugSuspended = store.booleanValue("DebugSuspended", true);
        debugPort = store.intValue("DebugPort", 5005);
        workflowId = store.stringValue("WorkflowID", null);
        workflowVersionId = store.stringValue("WorkflowVersionID", null);
        idempotent = store.booleanValue("Idempotent", true);
        deterministic = store.booleanValue("Deterministic", true);
        okToRetry = store.booleanValue("OKToRetry", false);
        retryAttempts = store.intValue("RetryAttempts", 0);
        metadataPropagationMode = store.stringValue("MetadataPropagationMode", DataProcessorServiceDefinition.META_DATA_DEFAULT_PROPAGATION);
        runAsDifferentUser = store.booleanValue("RunAsDifferentUser", false);
        systemUsername = store.stringValue("SystemUserName", "");
        int size = store.intValue("SourceCount", 0);
        dataTransferType = store.stringValue("DataTransferType", DataProcessorDataSource.FILE_DATA_SOURCE);
        properties = store.xmlDataStoreValue("Properties");
        ticketData = store.byteArrayValue("TicketData");
        dataSources = new String[size];
        dataSourceTypes = new String[size];
        dataSourceConnections = new String[size];
        dataSourceConnectionContexts = new String[size];
        dataSourceModes = new String[size];
        
        for(int i=0;i<size;i++){
            dataSources[i] = store.stringValue("DataSource" + i, "");
            dataSourceTypes[i] = store.stringValue("DataSourceType" + i, "");
            dataSourceConnections[i] = store.stringValue("DataSourceConnection" + i, "");
            dataSourceConnectionContexts[i] = store.stringValue("DataSourceConnectionContext" + i, "");
            dataSourceModes[i] = store.stringValue("DataSourceMode" + i, DataProcessorIODefinition.NON_STREAMING_CONNECTION);
        }
        
        size = store.intValue("OutputCount", 0);
        dataOutputs = new String[size];
        dataOutputTypes = new String[size];
        for(int i=0;i<size;i++){
            dataOutputs[i] = store.stringValue("DataOutput" + i, "");
            dataOutputTypes[i] = store.stringValue("DataOutputType" + i, "");
        }
    }
}