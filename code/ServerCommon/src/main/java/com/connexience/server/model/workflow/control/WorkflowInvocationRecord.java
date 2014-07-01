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
package com.connexience.server.model.workflow.control;

import java.io.Serializable;
import java.util.Date;

/**
 * This class represents a single invocation on the workflow engine
 * @author nhgh
 */
public class WorkflowInvocationRecord implements Serializable {
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


    /** Invocation ID */
    private String invocationId;

    /** Time this invocation was started */
    private Date startTime;

    /** Has this invocation been started */
    private boolean running;

    /** PID value as recognised by the engine that is running the workflow */
    private long pid;

    /** ContextID of the current block */
    private String contextId;
    
    /** Debug port of the current block if there is one */
    private int currentBlockDebugPort = -1;
    
    /** Workflow name */
    private String workflowName;
    
    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }
   
    public String getInvocationId() {
        return invocationId;
    }

    public void setInvocationId(String invocationId) {
        this.invocationId = invocationId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public int getCurrentBlockDebugPort() {
        return currentBlockDebugPort;
    }

    public void setCurrentBlockDebugPort(int currentBlockDebugPort) {
        this.currentBlockDebugPort = currentBlockDebugPort;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }
    
    
}