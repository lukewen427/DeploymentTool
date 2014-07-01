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
package com.connexience.server.model.workflow;

import com.connexience.server.model.folder.Folder;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.util.Date;

/**
 * This class extends the basic folder record to provide a store for all of the
 * outputs of a specific workflow invocation.
 * @author nhgh
 */
public class WorkflowInvocationFolder extends Folder {
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


    /** Workflow is waiting */
    public static final int INVOCATION_WAITING = 0;

    /** Workflow is executing */
    public static final int INVOCATION_RUNNING = 1;

    /** Workflow has finished with no errors */
    public static final int INVOCATION_FINISHED_OK = 2;

    /** Workflow has finished but has errors */
    public static final int INVOCATION_FINISHED_WITH_ERRORS = 3;

    /** Workflow is waiting for a debugger on the current block */
    public static final int INVOCATION_WAITING_FOR_DEBUGGER = 4;

    /** Message when workflow succeeded */
    public static final String OK_MESSAGE = "Executed OK";

    /** Message when workflow failed */
    public static final String FAILED_MESSAGE = "Execution FAILED";

    /** Message when workflow killed */
    public static final String KILLED_MESSAGE = "Execution KILLED";

    private static String[] statusStrings = new String[] {
        "Waiting", "Running", "Finished Ok", "Finished with errors", "Waiting for a debugger"
    };

    /** Returns the status string corresponding to the given status id */ 
    public static String statusToString(int invocationStatus) {
        if (invocationStatus < 0 || invocationStatus > 4)
            throw new IllegalArgumentException("status must be in range [0, 4] inclusive.");
        return statusStrings[invocationStatus];
    }


    /** Invocation ID. This links into the workflow database */
    private String invocationId;

    /** ID of the workflow document associated with this invocation */
    private String workflowId;

    /** Version ID of the workflow document associated with this invocation */
    private String versionId;

    /** Timestamp of the invocation */
    private Date invocationDate = new java.util.Date();

    /** Time this invocation was started */
    private Date queuedTime = null;

    /** Time this invocation was taken from the queue */
    private Date dequeuedTime = null;

    /** Time this invocation was started */
    private Date executionStartTime = null;

    /** Time this invocation was completed */
    private Date executionEndTime = null;

    /** Status of execution */
    private int invocationStatus = INVOCATION_WAITING;

    /** ID of the block currently being executed */
    private String currentBlockId;

    /** Is the streaming progress known for the current block */
    private boolean streamingProgressKnown = false;

    /** Total number of bytes to stream */
    private long totalBytesToStream = 0;

    /** Total number of bytes streamed */
    private long bytesStreamed = 0;

    /** Percent complete */
    private int percentComplete = 0;
    
    /** ID of the engine running the workflow */
    private String engineId;

    /** Status message */
    private String message;

    public WorkflowInvocationFolder(){
        super();
    }

    /** Reset the data in this folder to its default un-run state */
    public void reset(){
        setInvocationStatus(WorkflowInvocationFolder.INVOCATION_WAITING);
        setMessage(null);
        setCurrentBlockId(null);
        setEngineId(null);
        setStreamingProgressKnown(false);
        setTotalBytesToStream(0);
        setBytesStreamed(0);      
        setInvocationDate(new java.util.Date());
        setQueuedTime(null);
        setDequeuedTime(null);
        setExecutionStartTime(null);
        setExecutionEndTime(null);
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("InvocationID", invocationId);
        store.add("WorkflowID", workflowId);
        store.add("VersionID", versionId);
        store.add("InvocationDate", invocationDate);
        store.add("QueuedTime", queuedTime);
        store.add("DequeuedTime", dequeuedTime);
        store.add("ExecutionStartTime", executionStartTime);
        store.add("ExecutionEndTime", executionEndTime);
        store.add("InvocationStatus", invocationStatus);
        store.add("CurrentBlockID", currentBlockId);
        store.add("StreamingProgressKnown", streamingProgressKnown);
        store.add("TotalBytesToStream", totalBytesToStream);
        store.add("BytesStreamed", bytesStreamed);
        store.add("EngineID", engineId);
        store.add("Message", message);
        store.add("PercentComplete", percentComplete);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        invocationId = store.stringValue("InvocationID", null);
        workflowId = store.stringValue("WorkflowID", null);
        versionId = store.stringValue("VersionID", null);
        invocationDate = store.dateValue("InvocationDate", null);
        queuedTime = store.dateValue("QueuedTime", null);
        dequeuedTime = store.dateValue("DequeuedTime", null);
        executionStartTime = store.dateValue("ExecutionStartTime", null);
        executionEndTime = store.dateValue("ExecutionEndTime", null);
        invocationStatus = store.intValue("InvocationStatus", INVOCATION_WAITING);
        currentBlockId = store.stringValue("CurrentBlockID", null);
        streamingProgressKnown = store.booleanValue("StreamingProgressKnows", false);
        totalBytesToStream = store.longValue("TotalBytesToStream", 0);
        bytesStreamed = store.longValue("BytesStreamed", 0);
        engineId = store.stringValue("EngineID", null);
        message = store.stringValue("Message", null);
        percentComplete = store.intValue("PercentComplete", 0);
    }

    /** Get the status flag of this invocation */
    public int getInvocationStatus() {
        return invocationStatus;
    }

    /** Set the status flag of this invocation */
    public void setInvocationStatus(int invocationStatus) {
        this.invocationStatus = invocationStatus;
    }

    /** Get the id of the currently executing block */
    public String getCurrentBlockId() {
        return currentBlockId;
    }

    /** Set the id of the currently executing block */
    public void setCurrentBlockId(String currentBlockId) {
        this.currentBlockId = currentBlockId;
    }

    /** Get the id of the engine that is running this workflow */
    public String getEngineId() {
        return engineId;
    }

    /** Set the id of the engine that is running this workflow */
    public void setEngineId(String engineId) {
        this.engineId = engineId;
    }

    /** Get the ID of the invocation in the workflow database */
    public String getInvocationId() {
        return invocationId;
    }

    /** Set the ID of the invocation in the workflow database */
    public void setInvocationId(String invocationId) {
        this.invocationId = invocationId;
    }

    /** Get the workflow ID */
    public String getWorkflowId(){
        return workflowId;
    }

    /** Set the workflow ID */
    public void setWorkflowId(String workflowId){
        this.workflowId = workflowId;
    }

    /** Get the invocation timestamp */
    public Date getInvocationDate(){
        return invocationDate;
    }

    /** Set the invocation timestamp */
    public void setInvocationDate(Date newDate){
        if(newDate!=null){
            invocationDate = new java.util.Date(newDate.getTime());
        }
    }

    public void setBytesStreamed(long bytesStreamed) {
        this.bytesStreamed = bytesStreamed;
    }

    public long getBytesStreamed() {
        return bytesStreamed;
    }

    public boolean isStreamingProgressKnown() {
        return streamingProgressKnown;
    }

    public void setStreamingProgressKnown(boolean streamingProgressKnown) {
        this.streamingProgressKnown = streamingProgressKnown;
    }

    public void setTotalBytesToStream(long totalBytesToStream) {
        this.totalBytesToStream = totalBytesToStream;
    }

    public long getTotalBytesToStream() {
        return totalBytesToStream;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getQueuedTime() {
        return queuedTime;
    }

    public void setQueuedTime(Date queuedTime) {
        this.queuedTime = queuedTime;
    }

    public Date getDequeuedTime() {
        return dequeuedTime;
    }

    public void setDequeuedTime(Date dequeuedTime) {
        this.dequeuedTime = dequeuedTime;
    }

    public Date getExecutionStartTime() {
        return executionStartTime;
    }

    public void setExecutionStartTime(Date executionStartTime) {
        this.executionStartTime = executionStartTime;
    }

    public Date getExecutionEndTime() {
        return executionEndTime;
    }

    public void setExecutionEndTime(Date executionEndTime) {
        this.executionEndTime = executionEndTime;
    }

    public int getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(int percentComplete) {
        this.percentComplete = percentComplete;
    }
}