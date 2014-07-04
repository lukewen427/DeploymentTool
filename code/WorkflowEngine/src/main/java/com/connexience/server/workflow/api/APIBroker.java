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
import com.connexience.server.model.document.*;
import com.connexience.server.*;
import com.connexience.server.model.*;
import com.connexience.server.model.datasets.Dataset;
import com.connexience.server.model.datasets.DatasetItem;
import com.connexience.server.model.datasets.DatasetQuery;
import com.connexience.server.model.metadata.MetadataCollection;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.security.Group;
import com.connexience.server.model.social.Link;
import com.connexience.server.model.workflow.WorkflowDocument;
import com.connexience.server.model.workflow.WorkflowInvocationFolder;
import com.connexience.server.model.workflow.WorkflowParameterList;
import com.connexience.server.model.workflow.notification.WorkflowLock;
import com.connexience.server.model.security.User;
import com.connexience.server.util.JSONContainer;
import com.connexience.server.workflow.cloud.library.CloudWorkflowServiceLibraryItem;
import com.connexience.server.workflow.types.WorkflowFilesystemScanner;
import com.connexience.server.workflow.types.WorkflowProject;

import java.rmi.*;
import java.io.*;
import java.util.*;

/**
 * This interface defines a broker that allows blocks limited access to the 
 * server API without including a massive classpath.
 * @author hugo
 */
public interface APIBroker extends Remote {
    /** Release this broker so that the server can tidy up resources */
    public void release() throws RemoteException;

    /** Get a document record by ID */
    public DocumentRecord getDocument(String id) throws RemoteException, ConnexienceException;

    /** Get the latest version of a document */
    public DocumentVersion getLatestVersion(String documentId) throws RemoteException, ConnexienceException;

    /** Get a workflow by ID */
    public WorkflowDocument getWorkflow(String id) throws RemoteException, ConnexienceException;

    /** Delete a folder by ID */
    public void deleteFolder(String id) throws RemoteException, ConnexienceException;

    /** Delete a document by ID */
    public void deleteDocument(String id) throws RemoteException, ConnexienceException;

    /** Get a Folder by ID */
    public Folder getFolder(String id) throws RemoteException, ConnexienceException;

    /** Get the documents contained in a folder */
    public List<DocumentRecord> getFolderDocuments(Folder folder) throws RemoteException, ConnexienceException;
    /** Get the public user */
    public User getPublicUser() throws RemoteException, ConnexienceException;

    /** Set the status of a workflow */
    public void setWorkflowStatus(String invocationId, int status) throws RemoteException, ConnexienceException;

    /** Set the current streaming progress of a block */
    public void setCurrentBlockStreamingProcessAsync(String invocationId, String contextId, long totalBytesToStream, long bytesStreamed) throws RemoteException, ConnexienceException;

    /** Remove a workflow lock */
    public void removeWorkflowLock(long lockId) throws RemoteException, ConnexienceException;

    /** Set the status of a workflow lock */
    public void setWorkflowLockStatus(long lockId, String status) throws RemoteException, ConnexienceException;

    public List<WorkflowDocument> listWorkflows() throws RemoteException, ConnexienceException;

    /** List all invocations for the given workflow */
    List<WorkflowInvocationFolder> listWorkflowInvocations(String workflowId) throws RemoteException, ConnexienceException;

    /** Create a workflow lock */
    public WorkflowLock createWorkflowLock(String invocationId, String contextId, boolean allowFailedSubworkflows, boolean pauseOnFailures) throws RemoteException, ConnexienceException;

    /** Execute a workflow */
    public WorkflowInvocationFolder executeWorkflow(WorkflowDocument workflow, WorkflowParameterList parameters, long lockId, String folderName) throws RemoteException, ConnexienceException;

    /** Attach an workflow invocation to a workflow lock */
    public void attachInvocationToLock(String invocationId, long lockId) throws RemoteException, ConnexienceException;

    /** Get an invocation */
    public WorkflowInvocationFolder getWorkflowInvocation(String invocationId) throws RemoteException, ConnexienceException;

    /** Save a document */
    public DocumentRecord saveDocument(Folder parent, DocumentRecord doc) throws RemoteException, ConnexienceException;

    /** Save a folder */
    public Folder saveFolder(Folder folder) throws RemoteException, ConnexienceException;

    /** Upload a file */
    public DocumentVersion uploadFile(DocumentRecord doc, File file) throws RemoteException, ConnexienceException;

    /** Download to a file */
    public void downloadToFile(DocumentRecord doc, File targetFile) throws RemoteException, ConnexienceException;

    /** Download to a file */
    public void downloadToFile(DocumentRecord doc, String versionId, File targetFile) throws RemoteException, ConnexienceException;   

    /** Grant access to an object */
    public void grantObjectPermission(ServerObject obj, User principal, String permission) throws RemoteException, ConnexienceException;

    /** Grant access to an object */
    public void grantObjectPermission(ServerObject obj, Group principal, String permission) throws RemoteException, ConnexienceException;
    
    /** Get a metadata collection */
    public MetadataCollection getMetadata(ServerObject obj) throws RemoteException, ConnexienceException;

    /** Upload some metadata */
    public void uploadMetadata(ServerObject obj, MetadataCollection metadata) throws RemoteException, ConnexienceException;

    /** Update a row in a dataset */
    public void updateDatasetItem(String datasetId, String itemName, long rowId, JSONContainer data) throws RemoteException, ConnexienceException;    
    
    /** Send data to a dashboard */
    public void updateDatasetItem(String datasetId, String itemName, JSONContainer data) throws RemoteException, ConnexienceException;    

    /** Update a dashboard item with a number */
    public void updateDatasetItem(String datasetId, String itemName, Number data) throws RemoteException, ConnexienceException;

    /** Reset a dashboard */
    public void resetDataset(String datasetId) throws RemoteException, ConnexienceException;

    /** Query a data set item for its contents */
    public JSONContainer queryDatasetItem(String datasetId, String itemName) throws RemoteException, ConnexienceException;

    /** Query a dataset item */
    public JSONContainer queryDatasetItem(DatasetQuery query) throws RemoteException, ConnexienceException;
    
    /** Get a subdirectory */
    public Folder getSubdirectory(Folder parent, String subdirectory) throws RemoteException, ConnexienceException;

    /** Create a new document */
    public DocumentRecord createDocument(Folder parent, String name) throws RemoteException, ConnexienceException;

    /** Get the child folders of a container folder */
    public List<Folder> getChildFolders(String folderId) throws RemoteException, ConnexienceException;

    /** Prepare a library for this block */
    public CloudWorkflowServiceLibraryItem prepareLibrary(String libraryName) throws RemoteException, ConnexienceException;

    public List<DocumentRecord> getDocumentLinks(String id) throws ConnexienceException, RemoteException;

    public Link addDocumentLink(String sourceObjectId, String targetObjectId) throws ConnexienceException, RemoteException;

    public Integer getDeploymentId(String studyCode, String loggerSerialNumber) throws ConnexienceException, RemoteException;
    
    public Folder getDeploymentFolder(Integer deploymentId) throws ConnexienceException, RemoteException;
    
    public void addDataToDeployment(Integer deploymentId, String documentRecordId) throws ConnexienceException, RemoteException;  
    
    public Dataset saveDataset(Dataset ds) throws ConnexienceException, RemoteException;
    
    public DatasetItem saveDatasetItem(DatasetItem item) throws ConnexienceException, RemoteException;
    
    public User createAccount(String firstName, String surname, String logon, String password) throws ConnexienceException, RemoteException;     
    
    public Group createGroup(String groupName, boolean ownerApprovalRequired, boolean nonMembersView) throws ConnexienceException, RemoteException;
    
    public DatasetItem lookupUserDatasetItemByName(String datasetName, String itemName) throws ConnexienceException, RemoteException;
    
    public User getCurrentUser() throws ConnexienceException, RemoteException;
    
    public boolean userHasNamedDataset(String datasetName) throws ConnexienceException, RemoteException;
    
    public Dataset lookupUserDatasetByName(String datasetName) throws ConnexienceException, RemoteException;
    
    public List<DatasetItem>listDatasetItems(String datasetId) throws ConnexienceException, RemoteException;    
    
    public Group getGroupByName(String name) throws ConnexienceException, RemoteException;
    
    public void addUserToGroup(String groupId, String userId) throws ConnexienceException, RemoteException;
    
    public void removeUserFromGroup(String groupId, String userId) throws ConnexienceException, RemoteException;        
    
    public ServerObject getServerObject(String objectId) throws ConnexienceException, RemoteException;
    
    public WorkflowProject getProject(int projectId) throws ConnexienceException, RemoteException;
    
    public WorkflowFilesystemScanner getScanner(long scannerId) throws ConnexienceException, RemoteException;
}