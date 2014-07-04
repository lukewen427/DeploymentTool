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

import com.connexience.server.*;
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.datasets.Dataset;
import com.connexience.server.model.datasets.DatasetItem;
import com.connexience.server.model.datasets.DatasetQuery;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.metadata.MetadataCollection;
import com.connexience.server.model.security.Group;
import com.connexience.server.model.security.User;
import com.connexience.server.model.social.Link;
import com.connexience.server.model.workflow.WorkflowDocument;
import com.connexience.server.model.workflow.WorkflowInvocationFolder;
import com.connexience.server.model.workflow.WorkflowParameterList;
import com.connexience.server.model.workflow.notification.WorkflowLock;
import com.connexience.server.util.JSONContainer;
import com.connexience.server.workflow.api.*;
import com.connexience.server.workflow.cloud.CloudWorkflowEngine;
import com.connexience.server.workflow.cloud.library.CloudWorkflowServiceLibraryItem;
import com.connexience.server.workflow.cloud.library.LibraryCallback;
import com.connexience.server.workflow.cloud.library.LibraryPreparationReport;
import com.connexience.server.workflow.cloud.library.ServiceLibrary;
import com.connexience.server.workflow.cloud.library.installer.SystemManager;
import com.connexience.server.workflow.cloud.library.installer.SystemManagerFactory;
import com.connexience.server.workflow.engine.WorkflowEngine;
import com.connexience.server.workflow.types.WorkflowFilesystemScanner;
import com.connexience.server.workflow.types.WorkflowProject;
import java.io.File;

import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.util.List;

/**
 * This is the server side of the APIBroker system.
 * @author hugo
 */
public class APIBrokerImpl extends UnicastRemoteObject implements APIBroker {
    private static final long serialVersionUID = 1L;

    /** API Object */
    private API api = null;

    /** Container object */
    private APIBrokerContainer container = null;
    
    /** Top level workflow engine */
    private WorkflowEngine engine = null;
    
    public APIBrokerImpl(APIBrokerContainer container, API api, WorkflowEngine engine) throws RemoteException {
        this.api = api;
        this.container = container;
        this.engine = engine;
    }

    public API getApiLink(){
        return api;
    }
    
    @Override
    public void release() throws RemoteException {
        container.releaseApiBroker(this);
    }

    @Override
    public DocumentRecord getDocument(String id) throws RemoteException, ConnexienceException {
        return api.getDocument(id);
    }

    @Override
    public DocumentVersion getLatestVersion(String documentId) throws RemoteException, ConnexienceException {
        return api.getLatestVersion(documentId);
    }

    @Override
    public WorkflowDocument getWorkflow(String id) throws RemoteException, ConnexienceException {
        return api.getWorkflow(id);
    }
    
    @Override
    public Folder getFolder(String id) throws RemoteException, ConnexienceException {
        return api.getFolder(id);
    }

    @Override
    public void deleteDocument(String id) throws RemoteException, ConnexienceException {
        api.deleteDocument(id);
    }

    @Override
    public void deleteFolder(String id) throws RemoteException, ConnexienceException {
        api.deleteFolderAsync(id);
    }
    
    @Override
    public User getPublicUser() throws RemoteException, ConnexienceException {
        return api.getPublicUser();
    }
    
    @Override
    public void setWorkflowStatus(String invocationId, int status) throws RemoteException, ConnexienceException {
        api.setWorkflowStatus(invocationId, status, "");
    }

    @Override
    public void setCurrentBlockStreamingProcessAsync(String invocationId, String contextId, long totalBytesToStream, long bytesStreamed) throws RemoteException, ConnexienceException {
        api.setCurrentBlockStreamingProcessAsync(invocationId, contextId, totalBytesToStream, bytesStreamed);
    }

    @Override
    public List<WorkflowDocument> listWorkflows() throws RemoteException, ConnexienceException {
        return api.listWorkflows();
    }

    @Override
    public List<WorkflowInvocationFolder> listWorkflowInvocations(String workflowId) throws RemoteException, ConnexienceException {
        return api.listWorkflowInvocations(workflowId);
    }

    @Override
    public WorkflowInvocationFolder getWorkflowInvocation(String invocationId) throws RemoteException, ConnexienceException {
        return api.getWorkflowInvocation(invocationId);
    }

    @Override
    public WorkflowInvocationFolder executeWorkflow(WorkflowDocument workflow, WorkflowParameterList parameters, long lockId, String folderName) throws RemoteException, ConnexienceException {
        return api.executeWorkflow(workflow, parameters, lockId, folderName);
    }

    @Override
    public WorkflowLock createWorkflowLock(String invocationId, String contextId, boolean allowFailedSubworkflows, boolean pauseOnFailures) throws RemoteException, ConnexienceException {
        return api.createWorkflowLock(invocationId, contextId, allowFailedSubworkflows, pauseOnFailures);
    }

    @Override
    public void attachInvocationToLock(String invocationId, long lockId) throws RemoteException ,ConnexienceException
    {
        api.attachInvocationToLock(invocationId, lockId);
    };

    @Override
    public void removeWorkflowLock(long lockId) throws RemoteException, ConnexienceException {
        api.removeWorkflowLock(lockId);
    }

    @Override
    public void setWorkflowLockStatus(long lockId, String status) throws RemoteException, ConnexienceException {
        api.setWorkflowLockStatus(lockId, status);
    }

    @Override
    public DocumentRecord saveDocument(Folder parent, DocumentRecord doc) throws RemoteException, ConnexienceException {
        return api.saveDocument(parent, doc);
    }

    @Override
    public Folder saveFolder(Folder folder) throws RemoteException, ConnexienceException {
        return api.saveFolder(folder);
    }
    
    @Override
    public void grantObjectPermission(ServerObject obj, User principal, String permission) throws RemoteException, ConnexienceException {
        api.grantObjectPermission(obj, principal, permission);
    }

    @Override
    public void grantObjectPermission(ServerObject obj, Group principal, String permission) throws RemoteException, ConnexienceException {
        api.grantObjectPermission(obj, principal, permission);
    }
    
    @Override
    public DocumentVersion uploadFile(DocumentRecord doc, File file) throws RemoteException, ConnexienceException {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
            return api.upload(doc, inStream);
        } catch (IOException ioe){
            throw new ConnexienceException("IOEror uploading file: " + ioe.getMessage(), ioe);
        } finally {
            if(inStream!=null){
                try{inStream.close();}catch(Exception e){}
            }
        }
    }

    @Override
    public void downloadToFile(DocumentRecord doc, File targetFile) throws RemoteException, ConnexienceException {
        FileOutputStream outStream = null;
        try {
            // Chmod the file if engine is running using separated workflows
            if(CloudWorkflowEngine.SINGLETON!=null && CloudWorkflowEngine.SINGLETON.getExecutionEngine().isWorkflowSeparationEnforced()){
                try {
                    SystemManager mgr = SystemManagerFactory.newInstance();
                    String engineGroup = CloudWorkflowEngine.SINGLETON.getExecutionEngine().getInvocationUserMap().getEngineGroupName();
                    mgr.setGroupOnFile(targetFile, engineGroup, false);
                    mgr.changePermissions(targetFile, true, true, false, false);
                } catch (Exception e){
                    throw new ConnexienceException("Error chmodding downloaded file: " + e.getMessage(), e);
                }
            }
            
            outStream = new FileOutputStream(targetFile);
            api.download(doc, outStream);
        } catch (IOException ioe){
            throw new ConnexienceException("IOError downloading file: " + ioe.getMessage(), ioe);
        } finally {
            try {
                if(outStream!=null){
                    outStream.flush();
                    outStream.close();
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    public void downloadToFile(DocumentRecord doc, String versionId, File targetFile) throws RemoteException, ConnexienceException {
        FileOutputStream outStream = null;        
        try {
            // Chmod the file if engine is running using separated workflows
            if(CloudWorkflowEngine.SINGLETON!=null && CloudWorkflowEngine.SINGLETON.getExecutionEngine().isWorkflowSeparationEnforced()){
                try {
                    SystemManager mgr = SystemManagerFactory.newInstance();
                    String engineGroup = CloudWorkflowEngine.SINGLETON.getExecutionEngine().getInvocationUserMap().getEngineGroupName();
                    mgr.setGroupOnFile(targetFile, engineGroup, false);
                    mgr.changePermissions(targetFile, true, true, false, false);
                } catch (Exception e){
                    throw new ConnexienceException("Error chmodding downloaded file: " + e.getMessage(), e);
                }
            }
            
            outStream = new FileOutputStream(targetFile);
            api.download(doc, versionId, outStream);
        } catch (IOException ioe){
            throw new ConnexienceException("IOError downloading file: " + ioe.getMessage(), ioe);
        } finally {
            try {
                if(outStream!=null){
                    outStream.flush();
                    outStream.close();
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    public MetadataCollection getMetadata(ServerObject obj) throws RemoteException, ConnexienceException {
        return api.getMetadata(obj.getId());
    }

    @Override
    public void uploadMetadata(ServerObject obj, MetadataCollection metadata) throws RemoteException, ConnexienceException {
        metadata.setObjectId(obj.getId());
        if(api.getTicket()!=null){
            metadata.setUserId(api.getTicket().getUserId());
        }
        api.uploadMetadata(obj.getId(), metadata);
    }

    @Override
    public List<DocumentRecord> getFolderDocuments(Folder folder) throws RemoteException, ConnexienceException {
        return api.getChildDocuments(folder.getId());
    }
    
    @Override
    public void updateDatasetItem(String dashboardId, String itemName, JSONContainer data) throws RemoteException, ConnexienceException {
        api.updateDatasetItem(dashboardId, itemName, data);
    }    

    @Override
    public void resetDataset(String dashboardId) throws RemoteException, ConnexienceException {
        api.resetDataset(dashboardId);
    }

    @Override
    public void updateDatasetItem(String datasetId, String itemName, long rowId, JSONContainer data) throws RemoteException, ConnexienceException {
        api.updateDatasetItem(datasetId, itemName, rowId, data);
    }

    @Override
    public void updateDatasetItem(String dashboardId, String itemName, Number data) throws RemoteException, ConnexienceException {
        api.updateDatasetItem(dashboardId, itemName, data);
    }

    @Override
    public JSONContainer queryDatasetItem(String datasetId, String itemName) throws RemoteException, ConnexienceException {
        return api.queryDatasetItem(datasetId, itemName);
    }

    @Override
    public JSONContainer queryDatasetItem(DatasetQuery query) throws RemoteException, ConnexienceException {
        return api.queryDatasetItem(query);
    }

    @Override
    public Folder getSubdirectory(Folder parent, String subdirectory) throws RemoteException, ConnexienceException {
        return api.getNamedSubdirectory(parent.getId(), subdirectory);
    }

    @Override
    public DocumentRecord createDocument(Folder parent, String name) throws RemoteException, ConnexienceException {
        return api.getOrCreateDocumentRecord(parent.getId(), name);
    }

    @Override
    public List<Folder> getChildFolders(String folderId) throws RemoteException, ConnexienceException {
        return api.getChildFolders(folderId);
    }

    @Override
    public CloudWorkflowServiceLibraryItem prepareLibrary(String libraryName) throws RemoteException, ConnexienceException {
        ServiceLibrary library = engine.getLibrary();
        SyncLibraryCallback cb = new SyncLibraryCallback();
        LibraryPreparationReport report = new LibraryPreparationReport();
        library.prepareDependency(api, libraryName, cb, report, true);
        boolean finished = false;
        
        while(!finished){
            try {
                Thread.sleep(1);
                finished = cb.isFinished();
            } catch (InterruptedException e){
                finished = true;
            }
        }
        if(cb.isSuceeded()){
            return cb.getLibrary();
        } else {
            return null;
        }
    }

    @Override
    public List<DocumentRecord> getDocumentLinks(String id) throws ConnexienceException, RemoteException {
        return api.getDocumentLinks(id);
    }

    @Override
    public Link addDocumentLink(String sourceObjectId, String targetObjectId) throws ConnexienceException, RemoteException {
        return api.addDocumentLink(sourceObjectId, targetObjectId);
    }

    /** Library preparation callback that can be made to wait until done */
    private class SyncLibraryCallback implements LibraryCallback {
        private volatile boolean finished = false;
        private volatile boolean suceeded = false;
        private CloudWorkflowServiceLibraryItem library = null;
        
        @Override
        public void libraryPreparationFailed(String message, LibraryPreparationReport report) {
            finished = true;
            suceeded = false;
        }

        @Override
        public void libraryReady(CloudWorkflowServiceLibraryItem library, LibraryPreparationReport report) {
            finished = true;
            suceeded = true;
            this.library = library;
        }

        public boolean isFinished() {
            return finished;
        }

        public boolean isSuceeded() {
            return suceeded;
        }
        
        public CloudWorkflowServiceLibraryItem getLibrary() {
            return library;
        }
                
    }

    @Override
    public Folder getDeploymentFolder(Integer deploymentId) throws ConnexienceException, RemoteException {
        return api.getDeploymentFolder(deploymentId);
    }

    @Override
    public Integer getDeploymentId(String studyCode, String loggerSerialNumber) throws ConnexienceException, RemoteException {
        return api.getDeploymentId(studyCode, loggerSerialNumber);
    }

    @Override
    public void addDataToDeployment(Integer deploymentId, String documentRecordId) throws ConnexienceException, RemoteException {
        api.addDataToDeployment(deploymentId, documentRecordId);
    }

    @Override
    public Dataset saveDataset(Dataset ds) throws ConnexienceException, RemoteException {
        return api.saveDataset(ds);
    }

    @Override
    public DatasetItem saveDatasetItem(DatasetItem item) throws ConnexienceException, RemoteException {
        return api.saveDatasetItem(item);
    }

    @Override
    public User createAccount(String firstName, String surname, String logon, String password) throws ConnexienceException, RemoteException {
        return api.createAccount(firstName, surname, logon, password);
    }

    @Override
    public Group createGroup(String groupName, boolean ownerApprovalRequired, boolean nonMembersView) throws ConnexienceException, RemoteException {
        return api.createGroup(groupName, ownerApprovalRequired, nonMembersView);
    }

    @Override
    public DatasetItem lookupUserDatasetItemByName(String datasetName, String itemName) throws ConnexienceException, RemoteException {
        return api.lookupUserDatasetItemByName(datasetName, itemName);
    }

    @Override
    public User getCurrentUser() throws ConnexienceException, RemoteException {
        return api.getUser(api.getTicket().getUserId());
    }

    @Override
    public Dataset lookupUserDatasetByName(String datasetName) throws ConnexienceException, RemoteException {
        return api.lookupUserDatasetByName(datasetName);
    }

    @Override
    public boolean userHasNamedDataset(String datasetName) throws ConnexienceException, RemoteException {
        return api.userHasNamedDataset(datasetName);
    }

    @Override
    public List<DatasetItem> listDatasetItems(String datasetId) throws ConnexienceException, RemoteException {
        return api.listDatasetItems(datasetId);
    }
    
    @Override
    public Group getGroupByName(String name) throws ConnexienceException, RemoteException {
        return api.getGroupByName(name);
    }
    
    @Override
    public void addUserToGroup(String groupId, String userId) throws ConnexienceException, RemoteException {
        api.addUserToGroup(groupId, userId);
    }
    
    @Override
    public void removeUserFromGroup(String groupId, String userId) throws ConnexienceException, RemoteException {
        api.removeUserFromGroup(groupId, userId);
    }    

    @Override
    public ServerObject getServerObject(String objectId) throws ConnexienceException, RemoteException {
        return api.getServerObject(objectId);
    }

    @Override
    public WorkflowProject getProject(int projectId) throws ConnexienceException, RemoteException {
        return api.getProject(projectId);
    }

    @Override
    public WorkflowFilesystemScanner getScanner(long scannerId) throws ConnexienceException, RemoteException {
        return api.getScanner(scannerId);
    }
}