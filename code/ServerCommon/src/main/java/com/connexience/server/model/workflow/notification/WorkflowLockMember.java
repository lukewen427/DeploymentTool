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

import com.connexience.server.model.workflow.WorkflowInvocationFolder;

import java.io.Serializable;

/**
 * This class represents a member of a workflow invocation lock.
 * @author hugo
 */
public class WorkflowLockMember implements Serializable {
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
    private static final long serialVersionUID = 1L;


    /** Database ID */
    private long id;

    /** ID of the parent lock */
    private long lockId;

    /** ID of the invocation */
    private String invocationId;

    /** ID of the invocation folder */
    private String invocationFolderId;

    /** Invocation status */
    private int invocationStatus = WorkflowInvocationFolder.INVOCATION_WAITING;

    /** ID of the engine running this workflow */
    private String engineId;

    /** Start time of the workflow */
    private long startTime;

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

    public int getInvocationStatus() {
        return invocationStatus;
    }

    public void setInvocationStatus(int invocationStatus) {
        this.invocationStatus = invocationStatus;
    }

    public long getLockId() {
        return lockId;
    }

    public void setLockId(long lockId) {
        this.lockId = lockId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getInvocationFolderId() {
        return invocationFolderId;
    }

    public void setInvocationFolderId(String invocationFolderId) {
        this.invocationFolderId = invocationFolderId;
    }
}