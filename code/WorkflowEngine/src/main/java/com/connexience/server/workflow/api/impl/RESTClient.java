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

import com.connexience.server.model.document.*;
import com.connexience.server.model.folder.*;
import com.connexience.server.model.security.*;
import com.connexience.server.model.social.Link;
import com.connexience.server.model.workflow.*;
import com.connexience.server.ConnexienceException;
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.datasets.Dataset;
import com.connexience.server.model.datasets.DatasetItem;
import com.connexience.server.model.datasets.DatasetQuery;
import com.connexience.server.model.logging.graph.GraphOperation;
import com.connexience.server.model.metadata.MetadataCollection;
import com.connexience.server.model.project.Project;
import com.connexience.server.model.storage.DataStore;
import com.connexience.server.model.workflow.notification.WorkflowLock;
import com.connexience.server.util.provenance.ProvenanceLoggerClient;
import com.connexience.server.rmi.IProvenanceLogger;
import com.connexience.server.util.JSONContainer;
import com.connexience.server.workflow.api.API;
import com.connexience.server.workflow.api.ApiProvider;
import com.connexience.server.workflow.api.Downloader;
import com.connexience.server.workflow.api.Uploader;
import com.connexience.server.workflow.api.downloaders.*;
import com.connexience.server.workflow.api.uploaders.*;
import com.connexience.server.workflow.service.DataProcessorServiceDefinition;
import com.connexience.server.workflow.types.WorkflowFilesystemScanner;
import com.connexience.server.workflow.types.WorkflowProject;
import org.pipeline.core.xmlstorage.*;

import java.io.*;
import java.util.*;

import javax.ws.rs.core.MediaType;
import org.apache.log4j.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * REST Client to the workflow API
 * @author hugo
 */
public class RESTClient implements API {
    ObjectMapper mapper = new ObjectMapper();

    private static Logger logger = Logger.getLogger(API.class);

    /** URL for the download servlet */
    private String serverContext = "/workflow";

    /** Web host name */
    private String hostName = "localhost";

    /** Webserver port of the host */
    private int httpPort = 8080;

    /** Security ticket */
    private Ticket ticket;

    /** Organisation data store */
    private DataStore dataStore;

    /** Provenance properties */
    private XmlDataStore provenanceProperties;

    /** Parent API provider */
    private ApiProvider parentProvider;
    
    public RESTClient(ApiProvider parentProvider){
        mapper.enableDefaultTyping();
        this.parentProvider = parentProvider;
    }

    public ObjectMapper getMapper(){
        return mapper;
    }

    public XmlDataStore getProvenanceProperties() {
        return provenanceProperties;
    }

    public void setProvenanceProperties(XmlDataStore provenanceProperties) {
        this.provenanceProperties = provenanceProperties;
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

    public String getServerContext() {
        return serverContext;
    }

    public void setServerContext(String serverContext) {
        this.serverContext = serverContext;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public void setDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    private ClientRequest createRequest(String url) throws IOException, ConnexienceException {
    	
        ClientRequest request = new ClientRequest("http://" + hostName + ":" + httpPort + serverContext + "/rest/wf" + url);
        request.accept(MediaType.APPLICATION_JSON);

        // Add ticket if one is present
        if(ticket!=null){
            // Write the ticket as request header parameters
            request.header("cnx-userid", ticket.getUserId());
            request.header("cnx-organisationid", ticket.getOrganisationId());

            // Send in groups if there are any
            if(ticket instanceof WebTicket){
                WebTicket wt = (WebTicket)ticket;
                String[] gids = wt.getGroupIds();
                request.header("cnx-groups", gids.length);
                for(int i=0;i<gids.length;i++){
                    request.header("cnx-group" + i, gids[i]);
                }
            }
        }

        return request;
    }

    public DataStore getDataStore() throws ConnexienceException {
        return dataStore;
    }

    /** Terminate the API. This closes the InitialContext etc */
    public void terminate(){

    }

    /** Create a downloader object */
    private Downloader createDownloader() {
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
    /** Log the completion of a workflow asynchronously */
    public void logWorkflowCompleteAsync(String invocationId, String status) throws ConnexienceException {
        //if(parentProvider.isUseJMS() && ticket!=null){        
        //    parentProvider.getJmsHelper().notifyWorkflowFinished(ticket, invocationId);
        //} else {        
            try {
                ClientRequest request = createRequest("/workflows/invocations/{id}/finished");
                request.pathParameter("id", invocationId);
                request.formParameter("status", status);
                request.post();
            } catch (Exception e) {
                throw new ConnexienceException("Error logging workflow complete: " + e.getMessage(), e);
            }
        //}
    }

    /** Get a document record by ID */
    public DocumentRecord getDocument(String id) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/documents/{id}");
            request.pathParameter("id", id);
            return request.get(DocumentRecord.class).getEntity();
        } catch (Exception e) {
            throw new ConnexienceException("Error getting document: " + e.getMessage(), e);
        }
    }

    /** Get / create a document in a folder */
    public DocumentRecord getOrCreateDocumentRecord(String parentFolderId, String name) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/folders/{id}/nameddocuments/{name}");
            request.pathParameter("id", parentFolderId);
            request.pathParameter("name", name);
            return request.get(DocumentRecord.class).getEntity();
        } catch (Exception e) {
            throw new ConnexienceException("Error getting/creating document: " + e.getMessage(), e);
        }
    }

    /** List all of the users workflows */
    public List<WorkflowDocument> listWorkflows() throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows");
            WorkflowDocument[] results = request.get(WorkflowDocument[].class).getEntity();
            ArrayList<WorkflowDocument> workflows = new ArrayList<>();
            for(int i=0;i<results.length;i++){
                workflows.add(results[i]);
            }
            return workflows;
        } catch (Exception e){
            throw new ConnexienceException("Error listing workflows");
        }
    }

    /** List all invocations for the given workflow */
    @SuppressWarnings("unchecked")
    public List<WorkflowInvocationFolder> listWorkflowInvocations(String workflowId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/{id}/invocations");
            request.pathParameter("id", workflowId);
            return (List<WorkflowInvocationFolder>)request.get(List.class).getEntity();
        } catch (Exception e) {
            throw new ConnexienceException("Error listing invocations");
        }
    }


    /** Get a workflow document by ID */
    public WorkflowDocument getWorkflow(String id) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/{id}");
            request.pathParameter("id", id);
            return request.get(WorkflowDocument.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error getting workflow: " + e.getMessage(), e);
        }
    }

    /** Get a workflow invocation */
    public WorkflowInvocationFolder getWorkflowInvocation(String invocationId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/invocations/{id}");
            request.pathParameter("id", invocationId);
            return request.get(WorkflowInvocationFolder.class).getEntity();
        } catch (Exception e) {
            throw new ConnexienceException("Error getting workflow invocation: " + e.getMessage(), e);
        }
    }

    /** Set the engine ID for an invocation */
    public void setInvocationEngineId(String invocationId, String engineId) throws ConnexienceException {
        //if(parentProvider.isUseJMS() && ticket!=null){
        //    parentProvider.getJmsHelper().setInvocationEngineId(ticket, invocationId, engineId);
        //} else {        
            try {
                ClientRequest request = createRequest("/workflows/invocations/{id}/engineid");
                request.pathParameter("id", invocationId);
                request.formParameter("engineid", engineId);
                request.post();
            } catch (Exception e) {
                throw new ConnexienceException("Error getting workflow invocation: " + e.getMessage(), e);
            }
        //}
    }

    /** Save a workflow invocation synchronously */
    public WorkflowInvocationFolder saveWorkflowInvocation(WorkflowInvocationFolder invocation) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/invocations/");
            request.formParameter("invocation", mapper.writeValueAsString(invocation));
            return request.post(WorkflowInvocationFolder.class).getEntity();
        } catch (Exception e) {
            throw new ConnexienceException("Error saving workflow invocation: " + e.getMessage(), e);
        }
    }

    /** Set the current block asynchonously */
    public void setCurrentBlockAsync(String invocationId, String contextId, int percentComplete) throws ConnexienceException {
        if(parentProvider.isUseJMS() && ticket!=null){
            parentProvider.getJmsHelper().setCurrentBlock(ticket, invocationId, contextId, percentComplete);
        } else {        
            try {
                ClientRequest request = createRequest("/workflows/invocations/{id}/currentblock");
                request.pathParameter("id", invocationId);
                request.formParameter("contextId", contextId);
                request.formParameter("percentComplete", percentComplete);
                request.post();
            } catch (Exception e) {
                throw new ConnexienceException("Error setting current workflow block invocation: " + e.getMessage(), e);
            }
        }
    }

    /** Set the streaming progress of a block */
    public void setCurrentBlockStreamingProcessAsync(String invocationId, String contextId, long totalBytesToStream, long bytesStreamed) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/invocations/{id}/blocks/{context}/progress");
            request.pathParameter("id", invocationId);
            request.pathParameter("context", contextId);
            request.formParameter("total", totalBytesToStream);
            request.formParameter("bytes", bytesStreamed);
            request.post();
        } catch (Exception e) {
            throw new ConnexienceException("Error setting block streaming progress: " + e.getMessage(), e);
        }
    }

    /** Update the service log for a block */
    public void updateServiceLogAsync(String invocationId, String contextId, String outputData, String statusText, String statusMessage) throws ConnexienceException {
        if(parentProvider.isUseJMS() && ticket!=null){
            parentProvider.getJmsHelper().updateServiceLog(ticket, invocationId, contextId, outputData, statusText, statusMessage);
        } else {
            try {
                ClientRequest request = createRequest("/workflows/invocations/{id}/blocks/{context}/log");
                request.pathParameter("id", invocationId);
                request.pathParameter("context", contextId);
                request.formParameter("output", outputData);
                request.formParameter("status", statusText);
                request.formParameter("message", statusMessage);
                request.post();
            } catch (Exception e) {
                throw new ConnexienceException("Error updating service log: " + e.getMessage(), e);
            }
        }
    }

    /** Update the log message for a service */
    public void updateServiceLogMessageAsync(String invocationId, String contextId, String statusText, String statusMessage) throws ConnexienceException {
        if(parentProvider.isUseJMS() && ticket!=null){
            parentProvider.getJmsHelper().updateServiceLogMessage(ticket, invocationId, contextId, statusText, statusMessage);
        } else {
            try {
                ClientRequest request = createRequest("/workflows/invocations/{id}/blocks/{context}/message");
                request.pathParameter("id", invocationId);
                request.pathParameter("context", contextId);
                request.formParameter("status", statusText);
                request.formParameter("message", statusMessage);
                request.post();
            } catch (Exception e) {
                throw new ConnexienceException("Error updating service log message: " + e.getMessage(), e);
            }
        }
    }

    /** Delete a folder asynchronously */
    public void deleteFolderAsync(String folderId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/folders/{id}/delete");
            request.pathParameter("id", folderId);
            request.delete();
        } catch (Exception e) {
            throw new ConnexienceException("Error deleting folder: " + e.getMessage(), e);
        }
    }
    
    /** Delete a document */
    public void deleteDocument(String documentId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/documents/{id}/delete");
            request.pathParameter("id", documentId);
            request.delete();
        } catch (Exception e) {
            throw new ConnexienceException("Error deleting document: " + e.getMessage(), e);
        }        
    }

    /** Save a folder */
    public Folder saveFolder(Folder f) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/folders");
            request.formParameter("folder", mapper.writeValueAsString(f));
            return request.post(Folder.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error saving folder: " + e.getMessage(), e);
        }
    }


    /** Log start of a workflow */
    public void logWorkflowExecutionStartedAsync(String invocationId) throws ConnexienceException {
        //if(parentProvider.isUseJMS() && ticket!=null){        
        //    parentProvider.getJmsHelper().notifyWorkflowStarted(ticket, invocationId);
        //} else {           
            try {
                ClientRequest request = createRequest("/workflows/invocations/{id}/started");
                request.pathParameter("id", invocationId);
                request.post();
            } catch (Exception e) {
                throw new ConnexienceException("Error logging execution started: " + e.getMessage(), e);
            }
        //}
    }

    /** Ask for a resend of a lock */
    public void refreshLockStatusAsync(long lockId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/locks/{id}/refresh");
            request.pathParameter("id", lockId);
            request.post();
        } catch (Exception e) {
            throw new ConnexienceException("Error refreshing lock status: " + e.getMessage(), e);
        }
    }

    /** Get a service definition object. This gets the XML from the server and parses it here */
    public DataProcessorServiceDefinition getService(String serviceId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/services/{id}/definitions/latest/get");
            request.pathParameter("id", serviceId);
            String xml = request.get(String.class).getEntity();
            DataProcessorServiceDefinition def = new DataProcessorServiceDefinition();
            def.loadXmlString(xml);
            return def;
        } catch (Exception e) {
            throw new ConnexienceException("Error getting service: " + e.getMessage(), e);
        }

    }

    /** Get a service definition object. This gets the XML from the server and parses it here */
    public DataProcessorServiceDefinition getService(String serviceId, String versionId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/services/{id}/definitions/{version}/get");
            request.pathParameter("id", serviceId);
            request.pathParameter("version", versionId);
            String xml = request.get(String.class).getEntity();
            DataProcessorServiceDefinition def = new DataProcessorServiceDefinition();
            def.loadXmlString(xml);
            return def;
        } catch (Exception e) {
            throw new ConnexienceException("Error getting service: " + e.getMessage(), e);
        }
    }

    /** Get a dynamic workflow library by name */
    public DynamicWorkflowLibrary getDynamicWorkflowLibraryByName(String libraryName) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/libraries/byname");
            request.formParameter("name", libraryName);
            return request.post(DynamicWorkflowLibrary.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error saving folder: " + e.getMessage(), e);
        }
    }

    /** Get the ID of the latest version of a document */
    public String getLatestVersionId(String documentId) throws ConnexienceException {
        try {
            ClientRequest request = new ClientRequest("http://" + hostName + ":" + httpPort + serverContext + "/rest/wf/objects/{id}/latestid");
            request.accept(MediaType.TEXT_PLAIN);
            request.pathParameter("id", documentId);
            return request.get(String.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error getting latest versionID: " + e.getMessage(), e);
        }
    }

    /** Get the latest version of a document */
    public DocumentVersion getLatestVersion(String documentId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/objects/{id}/latest");
            request.pathParameter("id", documentId);
            return request.get(DocumentVersion.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error getting latest version: " + e.getMessage());
        }
    }

    /** Get a folder by ID */
    public Folder getFolder(String folderId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/folders/{id}/get");
            request.pathParameter("id", folderId);
            return request.get(Folder.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error getting folder: " + e.getMessage(), e);
        }
    }

    /** Get a named subdirectory */
    public Folder getNamedSubdirectory(String id,
            String name) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/folders/{id}/namedsubdirectories/{name}");
            request.pathParameter("id", id);
            request.pathParameter("name", name);
            return request.get(Folder.class).getEntity();

        } catch (Exception e){
            throw new ConnexienceException("Error getting folder: " + e.getMessage());
        }
    }

    /** Remove a workflow lock */
    public void removeWorkflowLock(long lockId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/locks/{id}/remove");
            request.pathParameter("id", lockId);
            request.delete();
        } catch (Exception e){
            throw new ConnexienceException("Error removing workflow lock: " + e.getMessage(), e);
        }
    }

    /** Set the status of a lock */
    public void setWorkflowLockStatus(long lockId, String status) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/locks/{id}/setstatus");
            request.pathParameter("id", lockId);
            request.formParameter("status", status);
            request.post();
        } catch (Exception e){
            throw new ConnexienceException("Error setting workflow lock status: " + e.getMessage(), e);
        }
    }

    /** Create a lock for a workflow */
    public WorkflowLock createWorkflowLock(String invocationId, String contextId, boolean allowFailedSubworkflows, boolean pauseOnFailures) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/locks/create/{invocationid}/{contextid}");
            request.pathParameter("invocationid", invocationId);
            request.pathParameter("contextid", contextId);
            request.formParameter("allowFailedSubworkflows", allowFailedSubworkflows);
            request.formParameter("pauseOnFailures", pauseOnFailures);
            return request.post(WorkflowLock.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error creating workflow lock: " + e.getMessage(), e);
        }
    }


    public void attachInvocationToLock(String invocationId, long lockId) throws ConnexienceException
    {
        try {
            ClientRequest request = createRequest("/workflows/locks/{lockId}/invocations/{invocationId}");
            request.pathParameter("lockId", lockId);
            request.pathParameter("invocationId", invocationId);
            request.post();
        } catch (Exception e) {
            throw new ConnexienceException("Error attaching workflow invocation to lock: " + e.getMessage(), e);
        }
    }


    /** Execute a workflow */
    public WorkflowInvocationFolder executeWorkflow(WorkflowDocument workflow, WorkflowParameterList parameters, long lockId, String folderName) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/{id}/execute");
            request.pathParameter("id", workflow.getId());
            request.formParameter("parameters", mapper.writeValueAsString(parameters));
            request.formParameter("lockid", lockId);
            request.formParameter("folder", folderName);

            // Add the parent invocation id from the provenance if it is known
            if(provenanceProperties!=null){
                request.formParameter("parentinvocationid", provenanceProperties.stringValue("InvocationID", ""));
            } else {
                request.formParameter("parentinvocationid", "");
            }

            return request.post(WorkflowInvocationFolder.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error creating workflow lock: " + e.getMessage(), e);
        }
    }

    /** Upload a stream to a document */
    public DocumentVersion upload(DocumentRecord document, InputStream stream) throws ConnexienceException {
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

    /** Save a document to a folder */
    public DocumentRecord saveDocument(Folder parent, DocumentRecord doc) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/folders/{containerid}/documents/save");
            request.pathParameter("containerid", parent.getId());
            request.formParameter("document", mapper.writeValueAsString(doc));
            return request.post(DocumentRecord.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error saving document: " + e.getMessage(), e);
        }
    }

    /** Get the organisation data store */
    public DataStore loadDataStore() throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/ds");
            ClientResponse<DataStore> resp = request.get(DataStore.class);
            dataStore = resp.getEntity();
            return dataStore;

        } catch (Exception e) {
            throw new ConnexienceException("Error loading data store: " + e.getMessage(), e);
        }
    }

    /** Download to an output stream */
    public void download(DocumentRecord document, OutputStream stream) throws ConnexienceException {
        Downloader down = createDownloader();
        down.setDocument(document);
        down.setParent(this);
        down.setStream(stream);
        down.download();
    }

    /** Download to an output stream */
    public void download(DocumentRecord document, String versionId, OutputStream stream) throws ConnexienceException {
        Downloader down = createDownloader();
        down.setDocument(document);
        down.setVersionId(versionId);
        down.setParent(this);
        down.setStream(stream);
        down.download();
    }

    /** Get the time from the server */
    public Date getServerTime() throws ConnexienceException {
        try {
            ClientRequest request = new ClientRequest("http://" + hostName + ":" + httpPort + serverContext + "/rest/wf/time");
            request.accept(MediaType.APPLICATION_JSON);
            return request.get(Date.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error getting server time: " + e.getMessage(), e);
        }
    }

    /** Notify startup of an engine */
    public void notifyEngineStartupAsync(String hostId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/engines/startup");
            request.formParameter("hostid", hostId);
            request.post();
        } catch (Exception e){
            throw new ConnexienceException("Error notifying engine startup: " + e.getMessage(), e);
        }
    }

    /** Notify shutdown of an engine */
    public void notifyEngineShutdownAsync(String hostId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/engines/shutdown");
            request.formParameter("hostid", hostId);
            request.post();
        } catch (Exception e){
            throw new ConnexienceException("Error notifying engine shutdown: " + e.getMessage(), e);
        }
    }

    /** Log dequeue of a workflow */
    public void logWorkflowDequeuedAsync(String invocationId) throws ConnexienceException {
        //if(parentProvider.isUseJMS() && ticket!=null){        
        //    parentProvider.getJmsHelper().notifyWorkflowDequeued(ticket, invocationId);
        //} else {         
            try {
                ClientRequest request = createRequest("/workflows/invocations/{id}/dequeued");
                request.pathParameter("id", invocationId);
                request.post();
            } catch (Exception e) {
                throw new ConnexienceException("Error logging workflow dequeued: " + e.getMessage(), e);
            }
        //}
    }

    /** Get versions of a document */
    public List<DocumentVersion> getDocumentVersions(DocumentRecord document) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/documents/{id}/getversions");
            request.pathParameter("id", document.getId());
            DocumentVersion[] results = request.get(DocumentVersion[].class).getEntity();
            List<DocumentVersion> versions = new ArrayList<>();
            for(int i=0;i<results.length;i++){
                versions.add(results[i]);
            }
            return versions;
        } catch (Exception e) {
            throw new ConnexienceException("Error listing document versions: " + e.getMessage(), e);
        }
    }

    /** Get a version of a document by number */
    public DocumentVersion getDocumentVersion(DocumentRecord document, int versionNumber) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/documents/{id}/versionnumbers/{version}/getversion");
            request.pathParameter("id", document.getId());
            request.pathParameter("version", versionNumber);
            return request.get(DocumentVersion.class).getEntity();
        } catch (Exception e) {
            throw new ConnexienceException("Error getting document version: " + e.getMessage(), e);
        }
    }

    /** Create the next version of a document */
    public DocumentVersion createNextVersion(DocumentRecord document) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/documents/{id}/nextversion");
            request.pathParameter("id", document.getId());
            return request.post(DocumentVersion.class).getEntity();
        } catch (Exception e) {
            throw new ConnexienceException("Error creating next document version: " + e.getMessage(), e);
        }
    }

    /** Update a document version */
    public DocumentVersion updateDocumentVersion(DocumentVersion version) throws ConnexienceException{
        try {
            ClientRequest request = createRequest("/documents/versions/updateversion");
            request.formParameter("version", mapper.writeValueAsString(version));
            return request.post(DocumentVersion.class).getEntity();
        } catch (Exception e) {
            throw new ConnexienceException("Error updating document version: " + e.getMessage(), e);
        }
    }

    /** Sign in with a username and password */
    public boolean authenticate(String username, String password) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/login");
            request.formParameter("username", username);
            request.formParameter("password", password);
            ClientResponse<Ticket> resp = request.post(Ticket.class);
            Ticket t = resp.getEntity();
            if(t!=null){
                ticket = t;
                return true;
            } else {
                ticket = null;
                return false;
            }

        } catch (Exception e) {
            throw new ConnexienceException("Error authenticating: " + e.getMessage(), e);
        }
    }

    /** Get the home folder for a user */
    public Folder getHomeFolder(String userId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/folders/home/{userid}");
            request.pathParameter("userid", userId);
            return request.get(Folder.class).getEntity();
        } catch (Exception e) {
            throw new ConnexienceException("Error getting home folder: " + e.getMessage(), e);
        }
    }

    /** Get the public user for the organisation */
    public User getPublicUser() throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/users/public");
            return request.get(User.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error getting public user: " + e.getMessage(), e);
        }
    }

    /** Grant permission for an object */
    public void grantObjectPermission(ServerObject object, ServerObject principal, String permission) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/permissions/{objectid}/{principalid}/add");
            request.pathParameter("objectid", object.getId());
            request.pathParameter("principalid", principal.getId());
            request.formParameter("permission", permission);
            request.post();
        } catch (Exception e){
            throw new ConnexienceException("Error granting permission: " + e.getMessage(), e);
        }
    }
    
    /** Set the status of a workflow invocation with a message */
    @Override
    public void setWorkflowStatus(String invocationId, int status, String message) throws ConnexienceException {
        //if(parentProvider.isUseJMS() && ticket!=null){
        //    parentProvider.getJmsHelper().setWorkflowStatus(ticket, invocationId, status, message);
        //} else {
            try {
                ClientRequest request = createRequest("/workflows/invocations/{id}/setstatus");
                request.pathParameter("id", invocationId);
                request.formParameter("status", status);
                if(message!=null){
                    request.formParameter("message", message);
                } else {
                    request.formParameter("message", "");
                }
                request.post();
            } catch (Exception e){
                throw new ConnexienceException("Error setting workflow status: " + e.getMessage(), e);
            }        
        //}
    }

    

    /** Get a user by ID */
    public User getUser(String userId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/users/{id}");
            request.pathParameter("id", userId);
            return request.get(User.class).getEntity();
        } catch (Exception e) {
            throw new ConnexienceException("Error getting user: " + e.getMessage(), e);
        }
    }

    /** Change the ID of an object. Returns the number of objects changed by the request */
    public int changeObjectId(String originalId, String requestedId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/objects/{id}/idchange/{newid}");
            request.pathParameter("id", originalId);
            request.pathParameter("newid", requestedId);
            return request.post(int.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error changing object id: " + e.getMessage(), e);
        }
    }

    /** Save a dynamic service object */
    public DynamicWorkflowService saveDynamicWorkflowService(DynamicWorkflowService service) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/services/saveservice");
            request.formParameter("service", mapper.writeValueAsString(service));
            return request.post(DynamicWorkflowService.class).getEntity();
        } catch (Exception e) {
            throw new ConnexienceException("Error saving workflow service: " + e.getMessage(), e);
        }
    }

    /** Get a dynamic workflow service */
    public DynamicWorkflowService getDynamicWorkflowService(String id) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/services/{id}/getservice");
            request.pathParameter("id", id);
            return request.get(DynamicWorkflowService.class).getEntity();
        } catch (Exception e) {
            throw new ConnexienceException("Error getting workflow service: " + e.getMessage(), e);
        }
    }

    /** Save a dynamic workflow library object */
    public DynamicWorkflowLibrary saveDynamicWorkflowLibrary(DynamicWorkflowLibrary library) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/libraries/savelibrary");
            request.formParameter("library", mapper.writeValueAsString(library));
            return request.post(DynamicWorkflowLibrary.class).getEntity();
        } catch (Exception e) {
            throw new ConnexienceException("Error saving workflow library: " + e.getMessage(), e);
        }
    }

    /** Get the child folders of a container folder */
    public List<Folder> getChildFolders(String folderId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/folders/{id}/children");
            request.pathParameter("id", folderId);
            Folder[] results = request.get(Folder[].class).getEntity();
            List<Folder> folders = new ArrayList<>();
            for(int i=0;i<results.length;i++){
                folders.add(results[i]);
            }
            return folders;
        } catch (Exception e) {
            throw new ConnexienceException("Error listing child folders: " + e.getMessage(), e);
        }
    }

    /** Get the child documents of a containing folder */
    public List<DocumentRecord> getChildDocuments(String folderId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/folders/{id}/documents");
            request.pathParameter("id", folderId);
            DocumentRecord[] results = request.get(DocumentRecord[].class).getEntity();
            List<DocumentRecord> docs = new ArrayList<>();
            for(int i=0;i<results.length;i++){
                docs.add(results[i]);
            }
            return docs;
        } catch (Exception e){
            throw new ConnexienceException("Error listing child documents: " + e.getMessage(), e);
        }
    }

    /** Upload a piece of provenance data. This method just uses the provenance client
     directly to avoid making server calls */
    public void uploadProvenance(GraphOperation op) throws ConnexienceException {
        try {
            IProvenanceLogger provClient = new ProvenanceLoggerClient();
            provClient.log(op);
        } catch (Exception e){
            throw new ConnexienceException("Error uploading provenance: " + e.getMessage());
        }
    }

    /** Attach a set of metadata to an object */
    public void uploadMetadata(String objectId, MetadataCollection metaData) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/metadata/{id}");
            request.pathParameter("id", objectId);
            request.formParameter("metadata", mapper.writeValueAsString(metaData));
            request.post();
        } catch (Exception e){
            throw new ConnexienceException("Error uploading metadata: " + e.getMessage(), e);
        }
    }

    /** Get all of the metadata for an object */
    public MetadataCollection getMetadata(String objectId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/metadata/{id}");
            request.pathParameter("id", objectId);
            return request.get(MetadataCollection.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error getting metadata: " + e.getMessage(), e);
        }
    }

    /** Reset a dataset */
    public void resetDataset(String datasetId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/datasets/{id}/reset");
            request.pathParameter("id", datasetId);
            request.get();
        } catch (Exception e){
            throw new ConnexienceException("Error resetting data set: " + e.getMessage(), e);
        }
    }

    /** Update a dataset value */
    public void updateDatasetItem(String datasetId, String itemName, JSONContainer data) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/datasets/{id}/items/{name}/json");
            request.pathParameter("id", datasetId);
            request.pathParameter("name", itemName);
            request.formParameter("objectData", mapper.writeValueAsString(data));
            request.post();
        } catch (Exception e){
            throw new ConnexienceException("Error updating data set: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateDatasetItem(String datasetId, String itemName, long rowId, JSONContainer data) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/datasets/{id}/items/{name}/rows/{rowid}/json");
            request.pathParameter("id", datasetId);
            request.pathParameter("name", itemName);
            request.pathParameter("rowid", rowId);
            request.formParameter("objectData", mapper.writeValueAsString(data));
            request.post();
        } catch (Exception e){
            throw new ConnexienceException("Error updating data set: " + e.getMessage(), e);
        }
    }

    /** Update a dataset item with a number */
    public void updateDatasetItem(String datasetId, String itemName, Number data) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/datasets/{id}/items/{name}/number");
            request.pathParameter("id", datasetId);
            request.pathParameter("name", itemName);
            request.formParameter("objectData", mapper.writeValueAsString(data));
            request.post();
        } catch (Exception e){
            throw new ConnexienceException("Error updating dataset: " + e.getMessage(), e);
        }
    }

    /** Query a dataset item */
    public JSONContainer queryDatasetItem(String datasetId, String itemName) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/datasets/{id}/items/{name}/contents");
            request.pathParameter("id", datasetId);
            request.pathParameter("name", itemName);
            return request.get(JSONContainer.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error querying dataset: " + e.getMessage(), e);
        }
    }

    /** Query a dataset item */
    public JSONContainer queryDatasetItem(DatasetQuery query) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/dataqueries");
            request.formParameter("queryData", mapper.writeValueAsString(query));
            return request.post(JSONContainer.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error querying dataset: " + e.getMessage(), e);
        }
    }

    /** Get a the outgoing links from a document */
    public List<DocumentRecord> getDocumentLinks(String id) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/documents/{id}/links");
            request.pathParameter("id", id);
            List<DocumentRecord> docsList = new ArrayList<>();
            DocumentRecord[] docs =  request.get(DocumentRecord[].class).getEntity();
            Collections.addAll(docsList, docs);
            return docsList;
        } catch (Exception e) {
            throw new ConnexienceException("Error getting document: " + e.getMessage(), e);
        }
    }

    /** Add a link from the source document to the target document */
    public Link addDocumentLink(String sourceObjectId, String targetObjectId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/documents/{id}/links");
            request.pathParameter("id", sourceObjectId);
            request.formParameter("targetDocumentId", targetObjectId);
            return request.post(Link.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error creating Document Link: " + e.getMessage(), e);
        }
    }
    @Override
    public void workflowTerminatedByEngine(String invocationId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflows/invocations/{id}/engineterminated");
            request.pathParameter("id", invocationId);
            request.post();
        } catch (Exception e) {
            throw new ConnexienceException("Error logging workflow terminated by engine: " + e.getMessage(), e);
        }
    }    

    @Override
    public WorkflowLock getWorkflowLock(long lockId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/workflow/locks/{id}");
            request.pathParameter("id", lockId);
            return request.get(WorkflowLock.class).getEntity();
        } catch(Exception e){
            throw new ConnexienceException("Error getting workflow lock: " + e.getMessage(), e);
        }
    }

    @Override
    public Folder getDeploymentFolder(Integer deploymentId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/deployments/{deploymentId}/datafolder");
            request.pathParameter("deploymentId", deploymentId);
            return request.get(Folder.class).getEntity();            
        } catch (Exception e){
            throw new ConnexienceException("Error getting deployment folder: " + e.getMessage(), e);
        }
    }

    @Override
    public Integer getDeploymentId(String studyCode, String loggerSerialNumber) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/studydeployments/{studycode}/{loggerserialnumber}");
            request.pathParameter("studycode", studyCode);
            request.pathParameter("loggerserialnumber", loggerSerialNumber);
            return request.get(Integer.class).getEntity();                 
        } catch (Exception e){
            throw new ConnexienceException("Error getting deployment id: " + e.getMessage(), e);
        }
    }

    @Override
    public void addDataToDeployment(Integer deploymentId, String documentRecordId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/deployments/{deploymentId}/data");
            request.pathParameter("deploymentId", deploymentId);
            request.formParameter("documentRecordId", documentRecordId);
            request.post();            
        } catch (Exception e){
            throw new ConnexienceException("Error adding data to deployment: " + e.getMessage(), e);
        }
    }

    @Override
    public Dataset saveDataset(Dataset ds) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/datasets");
            request.formParameter("datasetData", mapper.writeValueAsString(ds));
            return request.post(Dataset.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error saving dataset: " + e.getMessage(), e);
        }
    }

    @Override
    public DatasetItem saveDatasetItem(DatasetItem item) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/datasetitems");
            request.formParameter("datasetItemData", mapper.writeValueAsString(item));
            return request.post(DatasetItem.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error saving dataset item: " + e.getMessage(), e);
        }
    }

    @Override
    public User createAccount(String firstName, String surname, String logon, String password) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/newusers");   
            request.formParameter("firstName", firstName);
            request.formParameter("surname", surname);
            request.formParameter("logon", logon);
            request.formParameter("password", password);
            return request.post(User.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error creating account: " + e.getMessage(), e);
        }
    }

    @Override
    public Group createGroup(String groupName, boolean ownerApprovalRequired, boolean nonMembersView) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/newgroups");
            request.formParameter("name", groupName);
            request.formParameter("ownerapprovalrequired", ownerApprovalRequired);
            request.formParameter("nonmembersview", nonMembersView);
            return request.post(Group.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error creating group: " + e.getMessage());
        }
    }

    @Override
    public Ticket createTicketForUser(String userId) throws ConnexienceException {
        try {
             ClientRequest request = createRequest("/usertickets/{userid}");
             request.pathParameter("userid", userId);
             return request.get(Ticket.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error creating ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public DatasetItem lookupUserDatasetItemByName(String datasetName, String itemName) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/userdatasetsbyname/{name}/items/{itemname}");
            request.pathParameter("name", datasetName);
            request.pathParameter("itemname", itemName);
            return request.get(DatasetItem.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error looking up Dataset item: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DatasetItem> listDatasetItems(String datasetId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/datasets/{id}/items");
            request.pathParameter("id", datasetId);            
            DatasetItem[] results = request.get(DatasetItem[].class).getEntity();
            ArrayList<DatasetItem> items = new ArrayList<>();
            for(DatasetItem i : results){
                items.add(i);
            }
            return items;
        } catch (Exception e){
            throw new ConnexienceException("Error listing dataset items: " + e.getMessage(), e);
        }
    }

    @Override
    public Dataset lookupUserDatasetByName(String datasetName) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/userdatasetsbyname/{name}");
            request.pathParameter("name", datasetName);
            return request.get(Dataset.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error getting named Dataset: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean userHasNamedDataset(String datasetName) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/userdatasetownership/{name}");
            request.pathParameter("name", datasetName);
            return request.get(boolean.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error checking if user owns dataset: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Group getGroupByName(String name) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/groupsbyname/{name}");
            request.pathParameter("name", name);
            return request.get(Group.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error getting group by name: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void addUserToGroup(String groupId, String userId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/groups/{id}/membership");
            request.pathParameter("id", groupId);
            request.formParameter("userId", userId);
            request.post();
        } catch (Exception e){
            throw new ConnexienceException("Error adding user to group: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void removeUserFromGroup(String groupId, String userId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/groups/{id}/membership/{userId}");
            request.pathParameter("id", groupId);
            request.pathParameter("userId", userId);
            request.delete();
        } catch (Exception e){
            throw new ConnexienceException("Error removing user from group: " + e.getMessage(), e);
        }        
    }      
    
    @Override
    public ServerObject getServerObject(String objectId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/serverobjects/{id}");
            request.pathParameter("id", objectId);
            return request.get(ServerObject.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error getting ServerObject: " + e.getMessage(), e);
        }
    }
    
    @Override
    public WorkflowProject getProject(int projectId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/projects/{id}");
            request.pathParameter("id", projectId);
            return request.get(WorkflowProject.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error getting project: " + e.getMessage(), e);
        }
    }    

    @Override
    public WorkflowFilesystemScanner getScanner(long scannerId) throws ConnexienceException {
        try {
            ClientRequest request = createRequest("/scanners/{id}");
            request.pathParameter("id", scannerId);
            return request.get(WorkflowFilesystemScanner.class).getEntity();
        } catch (Exception e){
            throw new ConnexienceException("Error getting scanner: " + e.getMessage(), e);
        }
    }
    
    
}