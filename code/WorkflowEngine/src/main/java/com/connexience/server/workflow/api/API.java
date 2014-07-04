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
package com.connexience.server.workflow.api;

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
import com.connexience.server.util.JSONContainer;
import com.connexience.server.workflow.service.DataProcessorServiceDefinition;
import com.connexience.server.workflow.types.WorkflowFilesystemScanner;
import com.connexience.server.workflow.types.WorkflowProject;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.pipeline.core.xmlstorage.XmlDataStore;

/**
 *
 * @author hugo
 */
public interface API {

    /** Sign in with a username and password */
    boolean authenticate(String username, String password) throws ConnexienceException;

    /** Change the ID of an object. Returns the number of objects changed by the request */
    int changeObjectId(String originalId, String requestedId) throws ConnexienceException;

    /** Create the next version of a document */
    DocumentVersion createNextVersion(DocumentRecord document) throws ConnexienceException;

    /** Create a lock for a workflow */
    WorkflowLock createWorkflowLock(String invocationId, String contextId, boolean allowFailedSubworkflows, boolean pauseOnFailures) throws ConnexienceException;

    /** Attach a running workflow invocation to a workflow lock */
    void attachInvocationToLock(String invocationId, long lockId) throws ConnexienceException;

    /** Delete a folder asynchronously */
    void deleteFolderAsync(String folderId) throws ConnexienceException;

    /** Delete a document */
    void deleteDocument(String documentId) throws ConnexienceException;
    
    /** Download to an output stream */
    void download(DocumentRecord document, OutputStream stream) throws ConnexienceException;

    /** Download to an output stream */
    void download(DocumentRecord document, String versionId, OutputStream stream) throws ConnexienceException;

    /** Execute a workflow */
    WorkflowInvocationFolder executeWorkflow(WorkflowDocument workflow, WorkflowParameterList parameters, long lockId, String folderName) throws ConnexienceException;

    /** Get the child documents of a containing folder */
    List<DocumentRecord> getChildDocuments(String folderId) throws ConnexienceException;

    /** Get the child folders of a container folder */
    List<Folder> getChildFolders(String folderId) throws ConnexienceException;

    DataStore getDataStore() throws ConnexienceException;

    /** Get a document record by ID */
    DocumentRecord getDocument(String id) throws ConnexienceException;

    /** Get a version of a document by number */
    DocumentVersion getDocumentVersion(DocumentRecord document, int versionNumber) throws ConnexienceException;

    /** Get versions of a document */
    List<DocumentVersion> getDocumentVersions(DocumentRecord document) throws ConnexienceException;

    /** Get a dynamic workflow library by name */
    DynamicWorkflowLibrary getDynamicWorkflowLibraryByName(String libraryName) throws ConnexienceException;

    /** Get a dynamic workflow service */
    DynamicWorkflowService getDynamicWorkflowService(String id) throws ConnexienceException;

    /** Get a folder by ID */
    Folder getFolder(String folderId) throws ConnexienceException;

    /** Get the home folder for a user */
    Folder getHomeFolder(String userId) throws ConnexienceException;

    String getHostName();

    int getHttpPort();
    
    void setHostName(String hostName);
    
    void setHttpPort(int httpPort);

    /** Get the latest version of a document */
    DocumentVersion getLatestVersion(String documentId) throws ConnexienceException;

    /** Get the ID of the latest version of a document */
    String getLatestVersionId(String documentId) throws ConnexienceException;

    ObjectMapper getMapper();

    /** Get all of the metadata for an object */
    MetadataCollection getMetadata(String objectId) throws ConnexienceException;

    /** Get a named subdirectory */
    Folder getNamedSubdirectory(String id, String name) throws ConnexienceException;

    /** Get / create a document in a folder */
    DocumentRecord getOrCreateDocumentRecord(String parentFolderId, String name) throws ConnexienceException;

    XmlDataStore getProvenanceProperties();

    /** Get the public user for the organisation */
    User getPublicUser() throws ConnexienceException;

    String getServerContext();

    /** Get the time from the server */
    Date getServerTime() throws ConnexienceException;

    /** Get a service definition object. This gets the XML from the server and parses it here */
    DataProcessorServiceDefinition getService(String serviceId) throws ConnexienceException;

    /** Get a service definition object. This gets the XML from the server and parses it here */
    DataProcessorServiceDefinition getService(String serviceId, String versionId) throws ConnexienceException;

    Ticket getTicket();

    /** Get a user by ID */
    User getUser(String userId) throws ConnexienceException;

    /** Get a workflow document by ID */
    WorkflowDocument getWorkflow(String id) throws ConnexienceException;

    /** Get a workflow invocation */
    WorkflowInvocationFolder getWorkflowInvocation(String invocationId) throws ConnexienceException;

    /** Grant permission for an object */
    void grantObjectPermission(ServerObject object, ServerObject principal, String permission) throws ConnexienceException;

    /** List all of the users workflows */
    List<WorkflowDocument> listWorkflows() throws ConnexienceException;

    /** List all invocations for the given workflow */
    List<WorkflowInvocationFolder> listWorkflowInvocations(String workflowId) throws ConnexienceException;

    /** Get the organisation data store */
    DataStore loadDataStore() throws ConnexienceException;

    /** Log the completion of a workflow asynchronously */
    void logWorkflowCompleteAsync(String invocationId, String status) throws ConnexienceException;

    /** Log dequeue of a workflow */
    void logWorkflowDequeuedAsync(String invocationId) throws ConnexienceException;

    /** Log start of a workflow */
    void logWorkflowExecutionStartedAsync(String invocationId) throws ConnexienceException;

    /** Notify shutdown of an engine */
    void notifyEngineShutdownAsync(String hostId) throws ConnexienceException;

    /** Notify startup of an engine */
    void notifyEngineStartupAsync(String hostId) throws ConnexienceException;

    /** Query a dataset item */
    JSONContainer queryDatasetItem(String datasetId, String itemName) throws ConnexienceException;

    /** Query a dataset item using a query object */
    JSONContainer queryDatasetItem(DatasetQuery query) throws ConnexienceException;
    
    /** Ask for a resend of a lock */
    void refreshLockStatusAsync(long lockId) throws ConnexienceException;

    /** Remove a workflow lock */
    void removeWorkflowLock(long lockId) throws ConnexienceException;

    /** Reset a dataset */
    void resetDataset(String datasetId) throws ConnexienceException;

    /** Save a document to a folder */
    DocumentRecord saveDocument(Folder parent, DocumentRecord doc) throws ConnexienceException;

    /** Save a dynamic workflow library object */
    DynamicWorkflowLibrary saveDynamicWorkflowLibrary(DynamicWorkflowLibrary library) throws ConnexienceException;

    /** Save a dynamic service object */
    DynamicWorkflowService saveDynamicWorkflowService(DynamicWorkflowService service) throws ConnexienceException;

    /** Save a folder */
    Folder saveFolder(Folder f) throws ConnexienceException;

    /** Save a workflow invocation synchronously */
    WorkflowInvocationFolder saveWorkflowInvocation(WorkflowInvocationFolder invocation) throws ConnexienceException;

    /** Set the current block asynchonously */
    void setCurrentBlockAsync(String invocationId, String contextId, int percentComplete) throws ConnexienceException;

    /** Set the streaming progress of a block */
    void setCurrentBlockStreamingProcessAsync(String invocationId, String contextId, long totalBytesToStream, long bytesStreamed) throws ConnexienceException;

    void setDataStore(DataStore dataStore);

    /** Set the engine ID for an invocation */
    void setInvocationEngineId(String invocationId, String engineId) throws ConnexienceException;

    void setProvenanceProperties(XmlDataStore provenanceProperties);

    void setServerContext(String serverContext);

    void setTicket(Ticket ticket);

    /** Set the status of a lock */
    void setWorkflowLockStatus(long lockId, String status) throws ConnexienceException;

    /** Set the status of a workflow invocation */
    void setWorkflowStatus(String invocationId, int status, String message) throws ConnexienceException;
    
    /** Terminate the API. This closes the InitialContext etc */
    void terminate();

    /** Update an existing row within a dataset with JSON */
    void updateDatasetItem(String datasetId, String itemName, long rowId, JSONContainer data) throws ConnexienceException;
    
    /** Update a dataset value */
    void updateDatasetItem(String datasetId, String itemName, JSONContainer data) throws ConnexienceException;

    /** Update a dataset item with a number */
    void updateDatasetItem(String datasetId, String itemName, Number data) throws ConnexienceException;

    /** Update a document version */
    DocumentVersion updateDocumentVersion(DocumentVersion version) throws ConnexienceException;

    /** Update the service log for a block */
    void updateServiceLogAsync(String invocationId, String contextId, String outputData, String statusText, String statusMessage) throws ConnexienceException;

    /** Update the log message for a service */
    void updateServiceLogMessageAsync(String invocationId, String contextId, String statusText, String statusMessage) throws ConnexienceException;

    /** Upload a stream to a document */
    DocumentVersion upload(DocumentRecord document, InputStream stream) throws ConnexienceException;

    /** Attach a set of metadata to an object */
    void uploadMetadata(String objectId, MetadataCollection metaData) throws ConnexienceException;

    /** Upload a piece of provenance data. This method just uses the provenance client
    directly to avoid making server calls */
    void uploadProvenance(GraphOperation op) throws ConnexienceException;

    List<DocumentRecord> getDocumentLinks(String id) throws ConnexienceException;

    Link addDocumentLink(String sourceObjectId, String targetObjectId) throws ConnexienceException;

    void workflowTerminatedByEngine(String invocationId) throws ConnexienceException;
    
    WorkflowLock getWorkflowLock(long lockId) throws ConnexienceException;
    
    Integer getDeploymentId(String studyCode, String loggerSerialNumber) throws ConnexienceException;
    
    Folder getDeploymentFolder(Integer deploymentId) throws ConnexienceException;
    
    void addDataToDeployment(Integer deploymentId, String documentRecordId) throws ConnexienceException;
    
    Dataset saveDataset(Dataset ds) throws ConnexienceException;
    
    DatasetItem saveDatasetItem(DatasetItem item) throws ConnexienceException;
    
    User createAccount(String firstName, String surname, String logon, String password) throws ConnexienceException;
    
    Group createGroup(String groupName, boolean ownerApprovalRequired, boolean nonMembersView) throws ConnexienceException;
    
    Ticket createTicketForUser(String userId) throws ConnexienceException;
    
    public DatasetItem lookupUserDatasetItemByName(String datasetName, String itemName) throws ConnexienceException;
    
    public boolean userHasNamedDataset(String datasetName) throws ConnexienceException;
    
    public Dataset lookupUserDatasetByName(String datasetName) throws ConnexienceException;
    
    public List<DatasetItem>listDatasetItems(String datasetId) throws ConnexienceException;
    
    public Group getGroupByName(String name) throws ConnexienceException;
    
    public void addUserToGroup(String groupId, String userId) throws ConnexienceException;
    
    public void removeUserFromGroup(String groupId, String userId) throws ConnexienceException;
    
    public ServerObject getServerObject(String objectId) throws ConnexienceException;
    
    public WorkflowProject getProject(int projectId) throws ConnexienceException;
    
    public WorkflowFilesystemScanner getScanner(long scannerId) throws ConnexienceException;
}
