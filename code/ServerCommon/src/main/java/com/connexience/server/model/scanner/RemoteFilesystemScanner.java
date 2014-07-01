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
package com.connexience.server.model.scanner;

import com.connexience.server.ConnexienceException;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.ejb.util.WorkflowEJBLocator;
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.workflow.WorkflowInvocationMessage;
import com.connexience.server.util.JSONEditable;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;

/**
 * This class can scan a remote filesystem for documents and upload them into
 * the local store
 * @author hugo
 */
public class RemoteFilesystemScanner implements JSONEditable, Serializable {
    /** Database ID */
    private long id;
    
    /** User ID */
    private String userId;
            
    /** ID of the target folder to upload files to */
    private String targetFolderId;
    
    /** ID of the workflow to run on each uploaded file */
    private String workflowId;
    
    /** IS this scanner set to automatically run a workflow for each file */
    private boolean automaticWorkflowEnabled = false;
    
    /** Last time the scan was run */
    private Date lastScanTime;

    /** Scan interval in seconds */
    private int scanInterval = 3600;
    
    /** Should this scanner automatically scan the remote folder */
    private boolean autoscanEnabled = false;
    
    /** Type name of scanner */
    private String typeName = "";

    /** Is this scanner enabled */
    private boolean enabled = false;
    
    /** Are transferred files automatically removed from the local store once they
     * have been accepted. */
    private boolean deleteUploadedFiles = false;
    
    /** Is a settling pass needed */
    private boolean settlingPassNeeded = false;

    /** Is this scanner a study scanner */
    private boolean studyScanner = false;
    
    /** ID of the study */
    private long studyId;

    public RemoteFilesystemScanner() {
    }

    
    public boolean isStudyScanner() {
        return studyScanner;
    }

    public void setStudyScanner(boolean studyScanner) {
        this.studyScanner = studyScanner;
    }

    public long getStudyId() {
        return studyId;
    }

    public void setStudyId(long studyId) {
        this.studyId = studyId;
    }
    
    
    public void setSettlingPassNeeded(boolean settlingPassNeeded) {
        this.settlingPassNeeded = settlingPassNeeded;
    }

    public boolean isSettlingPassNeeded() {
        return settlingPassNeeded;
    }
    
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
    public String getTargetFolderId() {
        return targetFolderId;
    }

    public void setDeleteUploadedFiles(boolean deleteUploadedFiles) {
        this.deleteUploadedFiles = deleteUploadedFiles;
    }

    public boolean isDeleteUploadedFiles() {
        return deleteUploadedFiles;
    }

    public void setTargetFolderId(String targetFolderId) {
        this.targetFolderId = targetFolderId;
    }

    public boolean isAutomaticWorkflowEnabled() {
        return automaticWorkflowEnabled;
    }

    public void setAutomaticWorkflowEnabled(boolean automaticWorkflowEnabled) {
        this.automaticWorkflowEnabled = automaticWorkflowEnabled;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public void setAutoscanEnabled(boolean autoscanEnabled) {
        this.autoscanEnabled = autoscanEnabled;
    }

    public boolean isAutoscanEnabled() {
        return autoscanEnabled;
    }

    public Date getLastScanTime() {
        return lastScanTime;
    }

    public void setLastScanTime(Date lastScanTime) {
        this.lastScanTime = lastScanTime;
    }

    public int getScanInterval() {
        return scanInterval;
    }

    public void setScanInterval(int scanInterval) {
        this.scanInterval = scanInterval;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean dueForUpdate(){
        if(lastScanTime==null){
            return true;
        } else {
            if(System.currentTimeMillis() > (lastScanTime.getTime() + (1000 * scanInterval))){
                return true;
            } else {
                return false;
            }
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    /** Create a map of the currently known about files */
    protected HashMap<String, RemoteFilesystemObject>createFilesystemMap(Ticket ticket) throws ConnexienceException {
        List serverFiles = EJBLocator.lookupScannerBean().listFilesystemObjects(ticket, getId());
        HashMap<String, RemoteFilesystemObject> map = new HashMap<>();
        RemoteFilesystemObject fsObj;
        for(Object o : serverFiles){
            fsObj = (RemoteFilesystemObject)o;
            map.put(fsObj.getRemotePath(), fsObj);
        }
        return map;
    }
    
    @Override
    public void readJson(JSONObject json) throws Exception {
        if(json.has("ScanInterval")){
            scanInterval = json.getInt("ScanInterval");
            if(scanInterval<1){
                scanInterval = 1;
            }
        }
        
        if(json.has("DeleteUploadedFiles")){
            deleteUploadedFiles = json.getBoolean("DeleteUploadedFiles");
        }
        
        if(json.has("ExecuteWorkflow")){
            automaticWorkflowEnabled = json.getBoolean("ExecuteWorkflow");
        };
        
        if(json.has("WorkflowID")){
            workflowId = json.getString("WorkflowID");
        }
        
        if(json.has("AutoScanEnabled")){
            autoscanEnabled = json.getBoolean("AutoScanEnabled");
        }
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = new JSONObject();
        json.put("ScanInterval", scanInterval);
        json.put("DeleteUploadedFiles", deleteUploadedFiles);
        json.put("ExecuteWorkflow", automaticWorkflowEnabled);
        json.put("WorkflowID", workflowId);
        json.put("AutoScanEnabled", autoscanEnabled);
        return json;
    }
    
    /** Perform a scan and then force upload of all files that aren't uploaded */
    public void forceUpload(Ticket ticket) throws ConnexienceException {
        scanForChanges(ticket);
        if(settlingPassNeeded){
            List fsObjects = EJBLocator.lookupScannerBean().listFilesystemObjects(ticket, id);
            RemoteFilesystemObject fsObject;
            for(Object o : fsObjects){
                fsObject = (RemoteFilesystemObject)o;
                if(!fsObject.getStatus().equals(RemoteFilesystemObject.UPLOADED) && fsObject.getStatus().equals(RemoteFilesystemObject.QUEUED)){
                    String documentId = importRemoteFile(ticket, fsObject);

                    // Remove local file if needed
                    if(deleteUploadedFiles){
                        removeRemoteFile(ticket, fsObject);
                        EJBLocator.lookupScannerBean().removeFileSystemObjectWithoutSecurity(fsObject.getId());
                    }
                    
                    // Execute the workflow on the file
                    if(documentId!=null && !documentId.isEmpty() && isAutomaticWorkflowEnabled() && getWorkflowId()!=null && !getWorkflowId().isEmpty()){
                        WorkflowInvocationMessage invocationMsg = new WorkflowInvocationMessage(ticket, getWorkflowId());
                        invocationMsg.setTargetFileId(documentId);
                        fsObject.setInvocationId(WorkflowEJBLocator.lookupWorkflowEnactmentBean().startWorkflow(ticket, invocationMsg));
                        fsObject = EJBLocator.lookupScannerBean().saveFileSystemObjectWithoutSecurity(fsObject);
                    }                    
                }
            }
        }
    }
    
    public void scanForChanges(Ticket ticket) throws ConnexienceException {
        throw new ConnexienceException("Not implemented");
    }
    
    public String importRemoteFile(Ticket ticket, RemoteFilesystemObject file) throws ConnexienceException {
        throw new ConnexienceException("Not implemented");
    }
    
    public void removeRemoteFile(Ticket ticket, RemoteFilesystemObject file) throws ConnexienceException {
        throw new ConnexienceException("Not implemented");
    }
}