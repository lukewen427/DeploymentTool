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
package com.connexience.server.workflow.api.impl;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.datasets.Dataset;
import com.connexience.server.model.datasets.DatasetItem;
import com.connexience.server.model.datasets.DatasetQuery;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.logging.graph.GraphOperation;
import com.connexience.server.model.metadata.MetadataCollection;
import com.connexience.server.model.security.Group;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.User;
import com.connexience.server.model.social.Link;
import com.connexience.server.model.storage.DataStore;
import com.connexience.server.model.workflow.DynamicWorkflowLibrary;
import com.connexience.server.model.workflow.DynamicWorkflowService;
import com.connexience.server.model.workflow.WorkflowDocument;
import com.connexience.server.model.workflow.WorkflowInvocationFolder;
import com.connexience.server.model.workflow.WorkflowParameterList;
import com.connexience.server.model.workflow.notification.WorkflowLock;
import com.connexience.server.rmi.IProvenanceLogger;
import com.connexience.server.util.JSONContainer;
import com.connexience.server.util.provenance.ProvenanceLoggerClient;
import com.connexience.server.workflow.api.API;
import com.connexience.server.workflow.api.ApiProvider;
import com.connexience.server.workflow.api.Downloader;
import com.connexience.server.workflow.api.Uploader;
import com.connexience.server.workflow.api.downloaders.DirectDownloader;
import com.connexience.server.workflow.api.downloaders.HttpDownloader;
import com.connexience.server.workflow.api.uploaders.DirectUploader;
import com.connexience.server.workflow.api.uploaders.HttpUploader;
import com.connexience.server.workflow.service.DataProcessorServiceDefinition;
import com.connexience.server.workflow.types.WorkflowFilesystemScanner;
import com.connexience.server.workflow.types.WorkflowProject;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.apache.log4j.*;


/**
 * This class provides an RMI client to the workflow server
 * @author hugo
 */
public class RMIClient implements API{    
    private static Logger logger = Logger.getLogger(RMIClient.class);
    ObjectMapper mapper = new ObjectMapper();
    private int callCount = 0;
    
    /** Remote service */
    private RMIService service;
    
    /** Provenance properties */
    private XmlDataStore provenanceProperties;
    
    /** Name of the remote host */
    private String hostName = "localhost";
    
    /** Port on the remote host for downloading data */
    private int httpPort = 8080;
    
    /** Security ticket of this connection */
    private Ticket ticket = null;
    
    /** Organisation data store */
    private DataStore dataStore;
    
    /** Parent API provider */
    private ApiProvider parentProvider;
    
    public RMIClient(ApiProvider parentProvider, Ticket ticket, RMIService service){
        mapper.enableDefaultTyping();
        this.service = service;
        this.parentProvider = parentProvider;
        this.ticket = ticket;
    }
    
    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }    
    
    /*
     
        try {
            
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
     * 
     * 
     */
    
    @Override
    public boolean authenticate(String username, String password) throws ConnexienceException {
        try {
            callCount++;
            Ticket t = service.authenticate(username, password);
            if(t!=null){
                return true;
            } else {
                return false;
            }            
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public int changeObjectId(String originalId, String requestedId) throws ConnexienceException {
        try {
            callCount++;
            return service.changeObjectId(originalId, requestedId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public DocumentVersion createNextVersion(DocumentRecord document) throws ConnexienceException {
        logger.debug("createNextVersion");
        try {
            callCount++;
            return service.createNextVersion(document.getId());
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public WorkflowLock createWorkflowLock(String invocationId, String contextId, boolean allowFailedSubworkflows, boolean pauseOnFailures) throws ConnexienceException {
        logger.debug("createWorkflowLock");
        try {
            callCount++;
            return service.createWorkflowLock(invocationId, contextId, allowFailedSubworkflows, pauseOnFailures);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }


    @Override
    public void attachInvocationToLock(String invocationId, long lockId) throws ConnexienceException
    {
        logger.debug("attachInvocationToLock");
        try {
             service.attachInvocationToLock(invocationId, lockId);
        } catch (RemoteException x) {
            throw new ConnexienceException("RMI Communication error: " + x.getMessage(), x);
        }
    }


    @Override
    public void deleteFolderAsync(String folderId) throws ConnexienceException {
        logger.debug("deleteFolderAsync");
        try {
            callCount++;
            service.deleteFolder(folderId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public void deleteDocument(String documentId) throws ConnexienceException {
        logger.debug("deleteDocument");
        try {
            callCount++;
            service.deleteDocument(documentId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    
    /** Create a downloader object */
    private Downloader createDownloader() {
        callCount++;
        if(dataStore!=null){
            if(dataStore.isDirectAccessSupported()){
                logger.debug("Direct access supported for data store: " + dataStore.getClass().getSimpleName());
                return new DirectDownloader();
            } else {
                logger.debug("Direct access not supported for data store: " + dataStore.getClass().getSimpleName() + " using HttpDownloader");
                return new HttpDownloader();
            }
        } else {
            logger.debug("No datastore present - using HttpDownloader");
            return new HttpDownloader();
        }
    }    
    
    /** Create an uploader object */
    private Uploader createUploader(boolean forceHttp){
        callCount++;
        if(forceHttp){
            logger.debug("Forcing HTTP upload");
            return new HttpUploader();
        } else {
            if(dataStore!=null){
                if(dataStore.isDirectAccessSupported()){
                    logger.debug("Direct access supported for data store: " + dataStore.getClass().getSimpleName());
                    return new DirectUploader();
                } else {
                    logger.debug("Direct access not supported for data store: " + dataStore.getClass().getSimpleName() + " using HttpUploader");
                    return new HttpUploader();
                }
            } else {
                logger.debug("No datastore present - using HttpUploader");
                return new HttpUploader();
            }        
        }
    }  
    
    @Override
    public DocumentVersion upload(DocumentRecord document, InputStream stream) throws ConnexienceException {
        logger.debug("upload");
        callCount++;
        boolean forceHttp = false;
        if(document instanceof DynamicWorkflowLibrary || document instanceof DynamicWorkflowService || document instanceof WorkflowDocument){
            // Force http for these types at the moment
            forceHttp = true;
        }
        Uploader up = createUploader(forceHttp);
        up.setDocument(document);
        up.setParent(this);
        up.setStream(stream);
        if(up.upload()){
            return up.getUploadedDocumentVersion();
        } else {
            throw new ConnexienceException("Error uploading document");
        }
    }

    @Override
    public void download(DocumentRecord document, OutputStream stream) throws ConnexienceException {
        logger.debug("download");
        callCount++;
        Downloader down = createDownloader();
        down.setDocument(document);
        down.setParent(this);
        down.setStream(stream);
        down.download();
    }

    @Override
    public void download(DocumentRecord document, String versionId, OutputStream stream) throws ConnexienceException {
        logger.debug("download");
        callCount++;
        Downloader down = createDownloader();
        down.setDocument(document);
        down.setVersionId(versionId);
        down.setParent(this);
        down.setStream(stream);
        down.download();     
    }

    @Override
    public WorkflowInvocationFolder executeWorkflow(WorkflowDocument workflow, WorkflowParameterList parameters, long lockId, String folderName) throws ConnexienceException {
        logger.debug("executeWorkflow");
        callCount++;
        try {
            // Add the parent invocation id from the provenance if it is known
            String parentInvocationId;
            if(provenanceProperties!=null){
                parentInvocationId = provenanceProperties.stringValue("InvocationID", "");
            } else {
                parentInvocationId = "";
            }            
            return service.executeWorkflow(workflow.getId(), parameters, parentInvocationId, lockId, folderName);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public List<DocumentRecord> getChildDocuments(String folderId) throws ConnexienceException {
        logger.debug("getChildDocuments");
        callCount++;
        try {
            DocumentRecord[] results = service.getChildDocuments(folderId);
            List<DocumentRecord> docs = new ArrayList<>();
            for(int i=0;i<results.length;i++){
                docs.add(results[i]);
            }
        return docs;              
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public List<Folder> getChildFolders(String folderId) throws ConnexienceException {
        logger.debug("getChildFolders");
        callCount++;
        try {
            Folder[] results = service.getChildFolders(folderId);
            List<Folder> folders = new ArrayList<>();
            for(int i=0;i<results.length;i++){
                folders.add(results[i]);
            }
            return folders;            
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }        
    }

    @Override
    public DataStore getDataStore() throws ConnexienceException {
        return dataStore;
    }

    @Override
    public DocumentRecord getDocument(String id) throws ConnexienceException {
        logger.debug("getDocument");
        callCount++;
        try {
            return service.getDocument(id);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public DocumentVersion getDocumentVersion(DocumentRecord document, int versionNumber) throws ConnexienceException {
        logger.debug("getDocumentVersion");
        callCount++;
        try {
            return service.getDocumentVersion(document.getId(), versionNumber);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public List<DocumentVersion> getDocumentVersions(DocumentRecord document) throws ConnexienceException {
        logger.debug("getDocumentVersions");
        callCount++;
        try {
            DocumentVersion[] results = service.getDocumentVersions(document.getId());
            List<DocumentVersion> versions = new ArrayList<>();
            for(int i=0;i<results.length;i++){
                versions.add(results[i]);
            }
            return versions;
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public DynamicWorkflowLibrary getDynamicWorkflowLibraryByName(String libraryName) throws ConnexienceException {
        logger.debug("getDynamicWorkflowLibraryByName");
        callCount++;
        try {
            return service.getDynamicWorkflowLibraryByName(libraryName);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public DynamicWorkflowService getDynamicWorkflowService(String id) throws ConnexienceException {
        logger.debug("getDynamicWorkflowService");
        callCount++;
        try {
            return service.getDynamicWorkflowService(id);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public Folder getFolder(String folderId) throws ConnexienceException {
        logger.debug("getFolder");
        callCount++;
        try {
            return service.getFolder(folderId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public Folder getHomeFolder(String userId) throws ConnexienceException {
        logger.debug("getHomeFolder");
        callCount++;
        try {
            return service.getHomeFolder(userId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public DocumentVersion getLatestVersion(String documentId) throws ConnexienceException {
        logger.debug("getLatestVersion");
        callCount++;
        try {
            return service.getLatestVersion(documentId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public String getLatestVersionId(String documentId) throws ConnexienceException {
        logger.debug("getLatestVersionId");
        callCount++;
        try {
            return service.getLatestVersionId(documentId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public MetadataCollection getMetadata(String objectId) throws ConnexienceException {
        logger.debug("getMetadata");
        callCount++;
        try {
            return service.getMetadata(objectId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public Folder getNamedSubdirectory(String id, String name) throws ConnexienceException {
        logger.debug("getNamedSubdirectory");
        callCount++;
        try {
            return service.getNamedSubdirectory(id, name);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public DocumentRecord getOrCreateDocumentRecord(String parentFolderId, String name) throws ConnexienceException {
        logger.debug("getOrCreateDocumentRecord");
        callCount++;
        try {
            return service.getOrCreateDocumentRecord(parentFolderId, name);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public XmlDataStore getProvenanceProperties() {
        return provenanceProperties;
    }

    @Override
    public User getPublicUser() throws ConnexienceException {
        logger.debug("getPublicUser");
        callCount++;
        try {
            return service.getPublicUser();
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public String getServerContext() {
        return "/workflow";
    }

    @Override
    public Date getServerTime() throws ConnexienceException {
        logger.debug("getServerTime");
        callCount++;
        try {
            return service.getServerTime();
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public DataProcessorServiceDefinition getService(String serviceId) throws ConnexienceException {
        logger.debug("getService");
        callCount++;
        try {
            String xml = service.getServiceXml(serviceId);
            DataProcessorServiceDefinition def = new DataProcessorServiceDefinition();
            def.loadXmlString(xml);
            return def;
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        } catch (Exception e){
            throw new ConnexienceException("Error parsing XML data: " + e.getMessage(), e);
        }
    }

    @Override
    public DataProcessorServiceDefinition getService(String serviceId, String versionId) throws ConnexienceException {
        logger.debug("getService");
        callCount++;
        try {
            String xml = service.getServiceXml(serviceId, versionId);
            DataProcessorServiceDefinition def = new DataProcessorServiceDefinition();
            def.loadXmlString(xml);
            return def;
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        } catch (Exception e){
            throw new ConnexienceException("Error parsing XML data: " + e.getMessage(), e);
        }
    }

    @Override
    public Ticket getTicket() {
        if(ticket==null){
            logger.debug("getTicket");
            callCount++;
            try {
                ticket = service.getTicket();
                return ticket;
            } catch (RemoteException re){
                return null;
            }
        } else {
            return ticket;
        }
    }

    @Override
    public User getUser(String userId) throws ConnexienceException {
        logger.debug("getUser");
        callCount++;
        try {
            return service.getUser(userId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public WorkflowDocument getWorkflow(String id) throws ConnexienceException {
        logger.debug("getWorkflow");
        callCount++;
        try {
            return service.getWorkflow(id);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public WorkflowInvocationFolder getWorkflowInvocation(String invocationId) throws ConnexienceException {
        logger.debug("getWorkflowInvocation");
        callCount++;
        try {
            return service.getWorkflowInvocation(invocationId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public List<WorkflowInvocationFolder> listWorkflowInvocations(String workflowId) throws ConnexienceException {
        logger.debug("listWorkflowInvocations");
        callCount++;
        try {
            return service.listWorkflowInvocations(workflowId);
        } catch (RemoteException re) {
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }
    
    @Override
    public void grantObjectPermission(ServerObject object, ServerObject principal, String permission) throws ConnexienceException {
        logger.debug("grantObjectPermission");
        callCount++;
        try {
            service.grantObjectPermission(object.getId(), principal.getId(), permission);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public List<WorkflowDocument> listWorkflows() throws ConnexienceException {
        logger.debug("listWorkflows");
        callCount++;
        try {
            WorkflowDocument[] results = service.listWorkflows();
            ArrayList<WorkflowDocument> workflows = new ArrayList<>();
            for(int i=0;i<results.length;i++){
                workflows.add(results[i]);
            }
            return workflows;           
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }        
    }

    @Override
    public DataStore loadDataStore() throws ConnexienceException {
        logger.debug("loadDataStore");
        callCount++;
        try {
            dataStore = service.loadDataStore();
            return dataStore;
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public void logWorkflowCompleteAsync(String invocationId, String status) throws ConnexienceException {
        //if(parentProvider.isUseJMS() && ticket!=null){        
        //    parentProvider.getJmsHelper().notifyWorkflowFinished(ticket, invocationId);
        //} else {
            logger.debug("logWorkflowCompleteAsync");
            callCount++;
            try {
                service.logWorkflowComplete(invocationId, status);
            } catch (RemoteException re){
                throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
            }
        //}
    }

    @Override
    public void logWorkflowDequeuedAsync(String invocationId) throws ConnexienceException {
        //if(parentProvider.isUseJMS() && ticket!=null){        
        //    parentProvider.getJmsHelper().notifyWorkflowDequeued(ticket, invocationId);
        //} else {        
            logger.debug("logWorkflowDequeuedAsync");
            callCount++;
            try {
                service.logWorkflowDequeued(invocationId);
            } catch (RemoteException re){
                throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
            }
        //}
    }

    @Override
    public void logWorkflowExecutionStartedAsync(String invocationId) throws ConnexienceException {
        //if(parentProvider.isUseJMS() && ticket!=null){        
        //    parentProvider.getJmsHelper().notifyWorkflowStarted(ticket, invocationId);
        //} else {        
            callCount++;
            logger.debug("logWorkflowExecutionStartedAsync");
            try {
                service.logWorkflowExecutionStarted(invocationId);
            } catch (RemoteException re){
                throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
            }
        //}
    }

    @Override
    public void notifyEngineShutdownAsync(String hostId) throws ConnexienceException {
        logger.debug("notifyEngineShutdownAsync");
        callCount++;
        try {
            service.notifyEngineShutdown(hostId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public void notifyEngineStartupAsync(String hostId) throws ConnexienceException {
        logger.debug("notifyEngineStartupAsync");
        callCount++;
        try {
            service.notifyEngineStartup(hostId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }   
    }

    @Override
    public JSONContainer queryDatasetItem(String datasetId, String itemName) throws ConnexienceException {
        logger.debug("queryDatasetItem");
        callCount++;
        try {
            return service.queryDatasetItem(datasetId, itemName);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public JSONContainer queryDatasetItem(DatasetQuery query) throws ConnexienceException {
        logger.debug("queryDatasetItem");
        callCount++;
        try {
            return service.queryDatasetItem(query);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }
    
    

    @Override
    public void refreshLockStatusAsync(long lockId) throws ConnexienceException {
        logger.debug("refreshLockStatusAsync");
        callCount++;
        try {
            service.refreshLockStatus(lockId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public void removeWorkflowLock(long lockId) throws ConnexienceException {
        logger.debug("removeWorkflowLock");
        callCount++;
        try {
            service.removeWorkflowLock(lockId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public void resetDataset(String datasetId) throws ConnexienceException {
        logger.debug("resetDataset");
        callCount++;
        try {
            service.resetDataset(datasetId);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public DocumentRecord saveDocument(Folder parent, DocumentRecord doc) throws ConnexienceException {
        logger.debug("saveDocument");
        callCount++;
        try {
            return service.saveDocument(parent.getId(), doc);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public DynamicWorkflowLibrary saveDynamicWorkflowLibrary(DynamicWorkflowLibrary library) throws ConnexienceException {
        logger.debug("saveDynamicWorkflowLibrary");
        callCount++;
        try {
            return service.saveDynamicWorkflowLibrary(library);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public DynamicWorkflowService saveDynamicWorkflowService(DynamicWorkflowService svc) throws ConnexienceException {
        logger.debug("saveDynamicWorkflowService");
        callCount++;
        try {
            return service.saveDynamicWorkflowService(svc);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public Folder saveFolder(Folder f) throws ConnexienceException {
        logger.debug("saveFolder");
        callCount++;
        try {
            return service.saveFolder(f);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public WorkflowInvocationFolder saveWorkflowInvocation(WorkflowInvocationFolder invocation) throws ConnexienceException {
        logger.debug("saveWorkflowInvocation");
        callCount++;
        try {
            return service.saveWorkflowInvocation(invocation);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public void setCurrentBlockAsync(String invocationId, String contextId, int percentComplete) throws ConnexienceException {
        if(parentProvider.isUseJMS() && ticket!=null){
            parentProvider.getJmsHelper().setCurrentBlock(ticket, invocationId, contextId, percentComplete);
        } else {
            logger.debug("setCurrentBlockAsync");
            callCount++;
            try {
                service.setCurrentBlock(invocationId, contextId, percentComplete);
            } catch (RemoteException re){
                throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
            }
        }
    }

    @Override
    public void setCurrentBlockStreamingProcessAsync(String invocationId, String contextId, long totalBytesToStream, long bytesStreamed) throws ConnexienceException {
        logger.debug("setCurrentBlockStreamingProcessAsync");
        callCount++;
        try {
            service.setCurrentBlockStreamingProcess(invocationId, contextId, totalBytesToStream, bytesStreamed);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public void setDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }


    @Override
    public void setInvocationEngineId(String invocationId, String engineId) throws ConnexienceException {
        //if(parentProvider.isUseJMS() && ticket!=null){
        //    parentProvider.getJmsHelper().setInvocationEngineId(ticket, invocationId, engineId);
        //} else {
            logger.debug("setInvocationEngineId");
            callCount++;
            try {
                service.setInvocationEngineId(invocationId, engineId);
            } catch (RemoteException re){
                throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
            }
        //}
    }

    @Override
    public void setProvenanceProperties(XmlDataStore provenanceProperties) {
        this.provenanceProperties = provenanceProperties;
    }

    @Override
    public void setServerContext(String serverContext) {
        
    }

    @Override
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public void setWorkflowLockStatus(long lockId, String status) throws ConnexienceException {
        logger.debug("setWorkflowLockStatus");
        callCount++;
        try {
            service.setWorkflowLockStatus(lockId, status);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public void setWorkflowStatus(String invocationId, int status, String message) throws ConnexienceException {
        //if(parentProvider.isUseJMS() && ticket!=null){
        //    parentProvider.getJmsHelper().setWorkflowStatus(ticket, invocationId, status, message);
        //} else {
            logger.debug("setWorkflowStatus");
            callCount++;
            try {
                service.setWorkflowStatus(invocationId, status, message);
            } catch (RemoteException re){
                throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
            }
        //}
    }

    @Override
    public void terminate() {
        try {
            logger.debug("RMIClient Call count at terminate: " + callCount);
            service.terminate();
        } catch (RemoteException re){
            
        }
    }

    @Override
    public void updateDatasetItem(String datasetId, String itemName, long rowId, JSONContainer data) throws ConnexienceException {
        logger.debug("updateDatasetItem");
        callCount++;
        try {
            service.updateDatasetItemWithJson(datasetId, itemName, rowId, data);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public void updateDatasetItem(String datasetId, String itemName, JSONContainer data) throws ConnexienceException {
        logger.debug("updateDatasetItem");
        callCount++;
        try {
            service.updateDatasetItemWithJson(datasetId, itemName, data);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public void updateDatasetItem(String datasetId, String itemName, Number data) throws ConnexienceException {
        logger.debug("updateDatasetItem");
        callCount++;
        try {
            service.updateDatasetItemWithNumber(datasetId, itemName, data);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public DocumentVersion updateDocumentVersion(DocumentVersion version) throws ConnexienceException {
        logger.debug("updateDocumentVersion");
        callCount++;
        try {
            return service.updateDocumentVersion(version);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public void updateServiceLogAsync(String invocationId, String contextId, String outputData, String statusText, String statusMessage) throws ConnexienceException {
        if(parentProvider.isUseJMS() && ticket!=null){
            parentProvider.getJmsHelper().updateServiceLog(ticket, invocationId, contextId, outputData, statusText, statusMessage);
        } else {
            logger.debug("updateServiceLogAsync");
            callCount++;
            try {
                service.updateServiceLog(invocationId, contextId, outputData, statusText, statusMessage);
            } catch (RemoteException re){
                throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
            }
        }
    }

    @Override
    public void updateServiceLogMessageAsync(String invocationId, String contextId, String statusText, String statusMessage) throws ConnexienceException {
        if(parentProvider.isUseJMS() && ticket!=null){
            parentProvider.getJmsHelper().updateServiceLogMessage(ticket, invocationId, contextId, statusText, statusMessage);
        } else {
            logger.debug("updateServiceLogMessageAsync");
            callCount++;
            try {
                service.updateServiceLogMessage(invocationId, contextId, statusText, statusMessage);
            } catch (RemoteException re){
                throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
            }
        }
    }


    @Override
    public void uploadMetadata(String objectId, MetadataCollection metaData) throws ConnexienceException {
        logger.debug("uploadMetadata");
        callCount++;
        try {
            service.uploadMetadata(objectId, metaData);
        } catch (RemoteException re){
            throw new ConnexienceException("RMI Communication error: " + re.getMessage(), re);
        }
    }

    @Override
    public void uploadProvenance(GraphOperation op) throws ConnexienceException {
        try {
            IProvenanceLogger provClient = new ProvenanceLoggerClient();
            provClient.log(op);
        } catch (Exception e){
            throw new ConnexienceException("Error uploading provenance: " + e.getMessage());
        }
    }

    @Override
    public List<DocumentRecord> getDocumentLinks(String id) throws ConnexienceException {
        logger.debug("getDocumentLinks");
        callCount++;
        try {
            DocumentRecord[] docs = service.getDocumentLinks(id);
            List<DocumentRecord> docsList = new ArrayList<>();
            Collections.addAll(docsList, docs);
            return docsList;
        } catch (Exception e) {
            throw new ConnexienceException("Error getting document links");
        }

    }

    @Override
    public Link addDocumentLink(String sourceObjectId, String targetObjectId) throws ConnexienceException {
        logger.debug("addDocumentLink");
        callCount ++;
        try {
            return service.addDocumentLink(sourceObjectId, targetObjectId);
        } catch (Exception e) {
            throw new ConnexienceException("Error adding document link");
        }
    }
    
    @Override
    public void workflowTerminatedByEngine(String invocationId) throws ConnexienceException {
        logger.debug("workflowTerminatedByEngine");
        callCount++;
        try {
            service.workflowTerminatedByEngine(invocationId);
        } catch (Exception e){
            throw new ConnexienceException("Error notifying server of engine initiated workflow termination");
        }
    }    

    @Override
    public WorkflowLock getWorkflowLock(long lockId) throws ConnexienceException {
        logger.debug("getWorkflowLock");
        callCount++;
        try {
            return service.getWorkflowLock(lockId);
        } catch (Exception e){
            throw new ConnexienceException("Error getting workflow lock: " + e.getMessage());
        }
    }

    @Override
    public Folder getDeploymentFolder(Integer deploymentId) throws ConnexienceException {
        logger.debug("getDeploymentFolder");
        callCount++;
        try {
            return service.getDeploymentFolder(deploymentId);
        } catch (Exception e){
            throw new ConnexienceException("Error getting deployment folder: " + e.getMessage(), e);
        }
    }

    @Override
    public Integer getDeploymentId(String studyCode, String loggerSerialNumber) throws ConnexienceException {
        logger.debug("getDeploymentId");
        callCount++;
        try {
            return service.getDeploymentId(studyCode, loggerSerialNumber);
        } catch (Exception e){
            throw new ConnexienceException("Error getting deployment id: " + e.getMessage(), e);
        }
    }

    @Override
    public void addDataToDeployment(Integer deploymentId, String documentRecordId) throws ConnexienceException {
        logger.debug("addDataToDeployment");
        callCount++;
        try {
            service.addDataToDeployment(deploymentId, documentRecordId);
        } catch (Exception e){
            throw new ConnexienceException("Error adding data to deployment: " + e.getMessage(), e);
        }
    }

    @Override
    public Dataset saveDataset(Dataset ds) throws ConnexienceException {
        logger.debug("saveDataset");
        callCount++;
        try {
            return service.saveDataset(ds);
        } catch (Exception e){
            throw new ConnexienceException("Error saving dataset: " + e.getMessage(), e);
        }
                
    }

    @Override
    public DatasetItem saveDatasetItem(DatasetItem item) throws ConnexienceException {
        logger.debug("saveDatasetItem");
        callCount++;
        try {
            return service.saveDatasetItem(item);
        } catch (Exception e){
            throw new ConnexienceException("Error saving dataset item: " + e.getMessage(), e);
        }
    }

    @Override
    public User createAccount(String firstName, String surname, String logon, String password) throws ConnexienceException {
        logger.debug("createAccount");
        callCount++;
        try {
            return service.createAccount(firstName, surname, logon, password);
        } catch (Exception e){
            throw new ConnexienceException("Error creating account: " + e.getMessage(), e);
        }
    }

    @Override
    public Group createGroup(String groupName, boolean ownerApprovalRequired, boolean nonMembersView) throws ConnexienceException {
        logger.debug("createGroup");
        callCount++;
        try {
            return service.createGroup(groupName, ownerApprovalRequired, nonMembersView);
        } catch (Exception e){
            throw new ConnexienceException("Error creating group: " + e.getMessage(), e);
        }
    }
    
    

    @Override
    public Ticket createTicketForUser(String userId) throws ConnexienceException {
        logger.debug("createTicketForUser");
        callCount++;
        try {
            return service.createTicketForUser(userId);
        } catch (Exception e){
            throw new ConnexienceException("Error creating ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public DatasetItem lookupUserDatasetItemByName(String datasetName, String itemName) throws ConnexienceException {
        logger.debug("lookupUserDatasetItemByName");
        callCount++;
        try {
            return service.lookupUserDatasetItemByName(datasetName, itemName);
        } catch (Exception e){
            throw new ConnexienceException("Error looking up dataset item by name: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean userHasNamedDataset(String datasetName) throws ConnexienceException {
        logger.debug("userHasNamedDataset");
        callCount++;
        try {
            return service.userHasNamedDataset(datasetName);
        } catch (Exception e){
            throw new ConnexienceException("Error checking if user has named dataset: " + e.getMessage(), e);
        }
    }

    @Override
    public Dataset lookupUserDatasetByName(String datasetName) throws ConnexienceException {
        logger.debug("lookupUserDatasetByName");
        callCount++;
        try {
            return service.lookupUserDatasetByName(datasetName);
        } catch (Exception e){
            throw new ConnexienceException("Error getting named dataset: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DatasetItem> listDatasetItems(String datasetId) throws ConnexienceException {
        logger.debug("listDatasetItems");
        callCount++;
        try {
            DatasetItem[] results = service.listDatasetItems(datasetId);
            ArrayList<DatasetItem>items = new ArrayList<>();
            for(DatasetItem i : results){
                items.add(i);
            }
            return items;
        } catch (Exception e){
            throw new ConnexienceException("Error listing dataset items: " + e.getMessage(), e);
        }
    }

    @Override
    public Group getGroupByName(String name) throws ConnexienceException {
        logger.debug("getGroupByName");
        callCount++;
        try {
            return service.getGroupByName(name);
        } catch (Exception e){
            throw new ConnexienceException("Error getting group by name: " + e.getMessage(), e);
        }
    }

    @Override
    public void addUserToGroup(String groupId, String userId) throws ConnexienceException {
        logger.debug("addUserToGroup");
        callCount++;
        try {
            service.addUserToGroup(groupId, userId);
        } catch (Exception e){
            throw new ConnexienceException("Error adding user to group: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeUserFromGroup(String groupId, String userId) throws ConnexienceException {
        logger.debug("removeUserFromGroup");
        callCount++;
        try {
            service.addUserToGroup(groupId, userId);
        } catch (Exception e){
            throw new ConnexienceException("Error removing user from group: " + e.getMessage(), e);
        }
    }

    @Override
    public ServerObject getServerObject(String objectId) throws ConnexienceException {
        logger.debug("getServerObject");
        callCount++;
        try {
            return service.getServerObject(objectId);
        } catch (Exception e){
            throw new ConnexienceException("Error getting server object: " + e.getMessage(), e);
        }        
    }

    @Override
    public WorkflowProject getProject(int projectId) throws ConnexienceException {
        logger.debug("getProject");
        callCount++;
        try {
            return service.getProject(projectId);
        } catch (Exception e){
            throw new ConnexienceException("Error getting project: " + e.getMessage(), e);
        }          
    }

    @Override
    public WorkflowFilesystemScanner getScanner(long scannerId) throws ConnexienceException {
        logger.debug("getScanner");
        callCount++;
        try {
            return service.getScanner(scannerId);
        } catch (Exception e){
            throw new ConnexienceException("Error getting scanner: " + e.getMessage(), e);
        }            
    }
    
    
    
}