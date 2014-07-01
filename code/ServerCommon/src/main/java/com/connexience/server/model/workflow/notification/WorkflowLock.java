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
package com.connexience.server.model.workflow.notification;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.io.Serializable;
import java.util.Date;

/**
 * This class provides a representation of a workflow lock that can wait
 * for the completion of multiple workflows. The lock contains details of
 * the workflow and invocation runing, the engine containing the workflow
 * and various status parameters for the child worklflows.
 * @author hugo
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class WorkflowLock implements Serializable, XmlStorable {
    /**
     * Class version UID.
     * 
     * Please increment this value whenever your changes may cause 
     * incompatibility with the previous version of this class. If unsure, ask 
     * one of the core development team or read:
     *   http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     *   http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 4L;


    public static String LOCK_FILLING = "filling";
    public static String LOCK_WAITING = "waiting";
    public static String LOCK_FINISHED = "finished";
    public static String LOCK_ERROR = "error";

    /** Database ID */
    private long id;

    /** Comments for this lock */
    private String comments = "";

    /** Invocation ID for the running workflow */
    private String invocationId;

    /** Folder id of the invocation */
    private String invocationFolderId;

    /** Context ID of the waiting block */
    private String contextId;

    /** ID of the engine running the top level workflow that is waiting for this lock */
    private String engineId;

    /** Status of this lock */
    private String status = LOCK_FILLING;

    /** User the created this lock */
    private String userId;

    /** Name of the parent workflow */
    private String workflowName = "";
    
    /** Time of the last status change */
    private Date lastChangeTime = new Date();
            
    /** Allow some of the subworkflows to fail and still let the workflow holding
     * this lock continue. */
    private boolean allowFailedSubworkflows = false;
    
    /** Should the lock pause if there are any failed subworkflows */
    private boolean pauseOnFailedSubworkflows = false;
    
    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("WorkflowLock");
        store.add("ID", id);
        store.add("Comments", comments);
        store.add("InvocationID", invocationId);
        store.add("InvocationFolderID", invocationFolderId);
        store.add("ContextID", contextId);
        store.add("EngineID", engineId);
        store.add("Status", status);
        store.add("UserID", userId);
        store.add("LastChangeTime", lastChangeTime);
        store.add("AllowFailedSubworkflows", allowFailedSubworkflows);
        store.add("PauseOnFailedSubworkflows", pauseOnFailedSubworkflows);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        id = store.longValue("ID", 0);
        comments = store.stringValue("Comments", null);
        invocationId = store.stringValue("InvocationID", null);
        invocationFolderId = store.stringValue("InvocationFolderID", null);
        contextId = store.stringValue("ContextID", null);
        engineId = store.stringValue("EngineID", null);
        status = store.stringValue("Status", LOCK_FILLING);
        userId = store.stringValue("UserID", null);
        lastChangeTime = store.dateValue("LastChangeTime", new Date());
        allowFailedSubworkflows = store.booleanValue("AllowFailedSubworkflows", false);
        pauseOnFailedSubworkflows = store.booleanValue("PauseOnFailedSubworkflows", false);
    }

    public void setPauseOnFailedSubworkflows(boolean pauseOnFailedSubworkflows) {
        this.pauseOnFailedSubworkflows = pauseOnFailedSubworkflows;
    }

    public boolean isPauseOnFailedSubworkflows() {
        return pauseOnFailedSubworkflows;
    }

    
    public void setAllowFailedSubworkflows(boolean allowFailedSubworkflows) {
        this.allowFailedSubworkflows = allowFailedSubworkflows;
    }

    public boolean isAllowFailedSubworkflows() {
        return allowFailedSubworkflows;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getEngineId() {
        return engineId;
    }

    public void setEngineId(String engineId) {
        this.engineId = engineId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInvocationId() {
        return invocationId;
    }

    public void setInvocationId(String invocationId) {
        this.invocationId = invocationId;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getInvocationFolderId() {
        return invocationFolderId;
    }

    public void setInvocationFolderId(String invocationFolderId) {
        this.invocationFolderId = invocationFolderId;
    }

    public Date getLastChangeTime() {
        return lastChangeTime;
    }

    public void setLastChangeTime(Date lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }
    
    public long getLastChangeMillis(){
        return lastChangeTime.getTime();
    }
    
    public void setLastChangeMillis(long lastChangeMillis){
        lastChangeTime = new Date(lastChangeMillis);
    }
}