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

/**
 * This class represents a file on a remote filesystem. 
 * @author hugo
 */
public class RemoteFilesystemObject {
    public static final String UPLOADING = "uploading";
    public static final String QUEUED = "queued";
    public static final String UPLOADED = "uploaded";
    public static final String UPLOAD_ERROR = "error";
    public static final String WAITING = "waiting";
    
    /** Database ID */
    private long id;
    
    /** Status message */
    private String statusMessage = "";

    /** Upload status */
    private String status = WAITING;

    /** ID of the scanner */
    private long scannerId;
    
    /** ID of the local file */
    private String localFileId;
    
    /** Path or ID on remote system */
    private String remotePath;
    
    /** File name */
    private String name;
    
    /** Current size */
    private long currentSize = -1;
    
    /** Is the size stable */
    private boolean stable = false;
    
    /** Invocation ID of the workflow that processed this file */
    private String invocationId = null;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public String getLocalFileId() {
        return localFileId;
    }

    public void setLocalFileId(String localFileId) {
        this.localFileId = localFileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public long getScannerId() {
        return scannerId;
    }

    public void setScannerId(long scannerId) {
        this.scannerId = scannerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStable(boolean stable) {
        this.stable = stable;
    }

    public boolean isStable() {
        return stable;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getInvocationId() {
        return invocationId;
    }

    public void setInvocationId(String invocationId) {
        this.invocationId = invocationId;
    }
}
