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
import com.connexience.server.workflow.types.WorkflowFilesystemScanner;
import com.connexience.server.workflow.types.WorkflowProject;
import java.util.Date;
import java.util.List;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** 
 * This interface defines the server side of the workflow manager RMI service.
 * @author hugo 
 */
public interface RMIService extends Remote {
    /** Sign in with a username and password */
    Ticket authenticate(String username, String password) throws ConnexienceException, RemoteException;

    /** Change the ID of an object. Returns the number of objects changed by the request */
    int changeObjectId(String originalId, String requestedId) throws ConnexienceException, RemoteException;

    /** Create the next version of a document */
    DocumentVersion createNextVersion(String documentId) throws ConnexienceException, RemoteException;

    /** Create a lock for a workflow */
    WorkflowLock createWorkflowLock(String invocationId, String contextId, boolean allowFailedSubworkflows, boolean pauseOnFailures) throws ConnexienceException, RemoteException;

    /** Attach an running workflow invocation to a workflow lock */
    void attachInvocationToLock(String invocationId, long lockId) throws ConnexienceException, RemoteException;

    /** Delete a folder asynchronously */
    void deleteFolder(String folderId) throws ConnexienceException, RemoteException;

    /** Delete a document */
    void deleteDocument(String documentId) throws ConnexienceException, RemoteException;
    
    /** Execute a workflow */
    WorkflowInvocationFolder executeWorkflow(String workflowId, WorkflowParameterList parameters, String parentInvocationId, long lockId, String folderName) throws ConnexienceException, RemoteException;

    /** Get the child documents of a containing folder */
    DocumentRecord[] getChildDocuments(String folderId) throws ConnexienceException, RemoteException;

    /** Get the child folders of a container folder */
    Folder[] getChildFolders(String folderId) throws ConnexienceException, RemoteException;

    /** Get a document record by ID */
    DocumentRecord getDocument(String id) throws ConnexienceException, RemoteException;

    /** Get a version of a document by number */
    DocumentVersion getDocumentVersion(String documentId, int versionNumber) throws ConnexienceException, RemoteException;

    /** Get versions of a document */
    DocumentVersion[] getDocumentVersions(String documentId) throws ConnexienceException, RemoteException;

    /** Get a dynamic workflow library by name */
    DynamicWorkflowLibrary getDynamicWorkflowLibraryByName(String libraryName) throws ConnexienceException, RemoteException;

    /** Get a dynamic workflow service */
    DynamicWorkflowService getDynamicWorkflowService(String id) throws ConnexienceException, RemoteException;

    /** Get a folder by ID */
    Folder getFolder(String folderId) throws ConnexienceException, RemoteException;

    /** Get the home folder for a user */
    Folder getHomeFolder(String userId) throws ConnexienceException, RemoteException;

    /** Get the latest version of a document */
    DocumentVersion getLatestVersion(String documentId) throws ConnexienceException, RemoteException;

    /** Get the ID of the latest version of a document */
    String getLatestVersionId(String documentId) throws ConnexienceException, RemoteException;

    /** Get all of the metadata for an object */
    MetadataCollection getMetadata(String objectId) throws ConnexienceException, RemoteException;

    /** Get a named subdirectory */
    Folder getNamedSubdirectory(String id, String name) throws ConnexienceException, RemoteException;

    /** Get / create a document in a folder */
    DocumentRecord getOrCreateDocumentRecord(String parentFolderId, String name) throws ConnexienceException, RemoteException;

    /** Get the public user for the organisation */
    User getPublicUser() throws ConnexienceException, RemoteException;

    /** Get the time from the server */
    Date getServerTime() throws ConnexienceException, RemoteException;

    /** Get a service definition object. This gets the XML from the server and parses it here */
    String getServiceXml(String serviceId) throws ConnexienceException, RemoteException;

    /** Get a service definition object. This gets the XML from the server and parses it here */
    String getServiceXml(String serviceId, String versionId) throws ConnexienceException, RemoteException;

    /** Get a user by ID */
    User getUser(String userId) throws ConnexienceException, RemoteException;

    /** Get a workflow document by ID */
    WorkflowDocument getWorkflow(String id) throws ConnexienceException, RemoteException;

    /** Get a workflow invocation */
    WorkflowInvocationFolder getWorkflowInvocation(String invocationId) throws ConnexienceException, RemoteException;

    /** List all invocations of a workflow */
    List<WorkflowInvocationFolder> listWorkflowInvocations(String workflowId) throws ConnexienceException, RemoteException;

    /** Grant permission for an object */
    void grantObjectPermission(String objectId, String principalId, String permission) throws ConnexienceException, RemoteException;

    /** List all of the users workflows */
    WorkflowDocument[] listWorkflows() throws ConnexienceException, RemoteException;

    /** Get the organisation data store */
    DataStore loadDataStore() throws ConnexienceException, RemoteException;

    /** Log the completion of a workflow asynchronously */
    void logWorkflowComplete(String invocationId, String status) throws ConnexienceException, RemoteException;

    /** Log dequeue of a workflow */
    void logWorkflowDequeued(String invocationId) throws ConnexienceException, RemoteException;

    /** Log start of a workflow */
    void logWorkflowExecutionStarted(String invocationId) throws ConnexienceException, RemoteException;

    /** Notify shutdown of an engine */
    void notifyEngineShutdown(String hostId) throws ConnexienceException, RemoteException;

    /** Notify startup of an engine */
    void notifyEngineStartup(String hostId) throws ConnexienceException, RemoteException;

    /** Query a dataset item */
    JSONContainer queryDatasetItem(String datasetId, String itemName) throws ConnexienceException, RemoteException;

    /** Query a dataset item */
    JSONContainer queryDatasetItem(DatasetQuery query) throws ConnexienceException, RemoteException;
    
    /** Ask for a resend of a lock */
    void refreshLockStatus(long lockId) throws ConnexienceException, RemoteException;

    /** Remove a workflow lock */
    void removeWorkflowLock(long lockId) throws ConnexienceException, RemoteException;

    /** Reset a dataset */
    void resetDataset(String datasetId) throws ConnexienceException, RemoteException;

    /** Save a document to a folder */
    DocumentRecord saveDocument(String parentId, DocumentRecord doc) throws ConnexienceException, RemoteException;

    /** Save a dynamic workflow library object */
    DynamicWorkflowLibrary saveDynamicWorkflowLibrary(DynamicWorkflowLibrary library) throws ConnexienceException, RemoteException;

    /** Save a dynamic service object */
    DynamicWorkflowService saveDynamicWorkflowService(DynamicWorkflowService service) throws ConnexienceException, RemoteException;

    /** Save a folder */
    Folder saveFolder(Folder f) throws ConnexienceException, RemoteException;

    /** Save a workflow invocation synchronously */
    WorkflowInvocationFolder saveWorkflowInvocation(WorkflowInvocationFolder invocation) throws ConnexienceException, RemoteException;

    /** Set the current block asynchonously */
    void setCurrentBlock(String invocationId, String contextId, int percentComplete) throws ConnexienceException, RemoteException;

    /** Set the streaming progress of a block */
    void setCurrentBlockStreamingProcess(String invocationId, String contextId, long totalBytesToStream, long bytesStreamed) throws ConnexienceException, RemoteException;

    /** Set the engine ID for an invocation */
    void setInvocationEngineId(String invocationId, String engineId) throws ConnexienceException, RemoteException;

    /** Set the status of a lock */
    void setWorkflowLockStatus(long lockId, String status) throws ConnexienceException, RemoteException;

    /** Set the status of a workflow invocation */
    void setWorkflowStatus(String invocationId, int status, String message) throws ConnexienceException, RemoteException;

    /** Terminate the API. This closes the InitialContext etc */
    void terminate() throws RemoteException;

    /** Update an existing dataset row */
    void updateDatasetItemWithJson(String datasetId, String itemName, long rowId, JSONContainer data) throws ConnexienceException, RemoteException;
    
    /** Update a dataset value */
    void updateDatasetItemWithJson(String datasetId, String itemName, JSONContainer data) throws ConnexienceException, RemoteException;

    /** Update a dataset item with a number */
    void updateDatasetItemWithNumber(String datasetId, String itemName, Number data) throws ConnexienceException, RemoteException;

    /** Update a document version */
    DocumentVersion updateDocumentVersion(DocumentVersion version) throws ConnexienceException, RemoteException;

    /** Update the service log for a block */
    void updateServiceLog(String invocationId, String contextId, String outputData, String statusText, String statusMessage) throws ConnexienceException, RemoteException;

    /** Update the log message for a service */
    void updateServiceLogMessage(String invocationId, String contextId, String statusText, String statusMessage) throws ConnexienceException, RemoteException;

    /** Attach a set of metadata to an object */
    void uploadMetadata(String objectId, MetadataCollection metaData) throws ConnexienceException, RemoteException; 
    
    /** Get the connection ticket */
    Ticket getTicket() throws RemoteException;

    public Link addDocumentLink(String sourceDocumentId, String targetDocumentId) throws ConnexienceException, RemoteException ;

    public DocumentRecord[] getDocumentLinks(String id) throws ConnexienceException, RemoteException;
    
    public void workflowTerminatedByEngine(String invocationId) throws ConnexienceException, RemoteException;
 
    public WorkflowLock getWorkflowLock(long lockId) throws ConnexienceException, RemoteException;
    
    Integer getDeploymentId(String studyCode, String loggerSerialNumber) throws ConnexienceException, RemoteException;
    
    Folder getDeploymentFolder(Integer deploymentId) throws ConnexienceException, RemoteException;
    
    void addDataToDeployment(Integer deploymentId, String documentRecordId) throws ConnexienceException, RemoteException;    
    
    Dataset saveDataset(Dataset ds) throws ConnexienceException, RemoteException;
    
    DatasetItem saveDatasetItem(DatasetItem item) throws ConnexienceException, RemoteException;
    
    User createAccount(String firstName, String surname, String logon, String password) throws ConnexienceException, RemoteException;    
    
    Group createGroup(String groupName, boolean ownerApprovalRequired, boolean nonMembersView) throws ConnexienceException, RemoteException;
    
    Ticket createTicketForUser(String userId) throws ConnexienceException, RemoteException;    
    
    DatasetItem lookupUserDatasetItemByName(String datasetName, String itemName) throws ConnexienceException, RemoteException;
    
    public boolean userHasNamedDataset(String datasetName) throws ConnexienceException, RemoteException;
    
    public Dataset lookupUserDatasetByName(String datasetName) throws ConnexienceException, RemoteException;
    
    public DatasetItem[] listDatasetItems(String datasetId) throws ConnexienceException, RemoteException;    
    
    public Group getGroupByName(String name) throws ConnexienceException, RemoteException;
    
    public void addUserToGroup(String groupId, String userId) throws ConnexienceException, RemoteException;
    
    public void removeUserFromGroup(String groupId, String userId) throws ConnexienceException, RemoteException;    
    
    public ServerObject getServerObject(String objectId) throws ConnexienceException, RemoteException;
    
    public WorkflowProject getProject(int projectId) throws ConnexienceException, RemoteException;
    
    public WorkflowFilesystemScanner getScanner(long scannerId) throws ConnexienceException, RemoteException;
}