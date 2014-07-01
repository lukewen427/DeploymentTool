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
import java.util.ArrayList;
import java.util.Date;
/**
 * This class contains information regarding the status of a workflow engine.
 * @author hugo
 */
public class WorkflowEngineStatusData implements Serializable {
    static final long serialVersionUID = 5144727020994760291L;
    
    /** Size of the working disk */
    private long diskSize;
    
    /** Free space on the working disk */
    private long freeSpace;
    
    /** Workflow capacity */
    private int workflowCapacity;
    
    /** Number of running workflows */
    private int workflowCount;
    
    /** Is the queue connected */
    private boolean jmsConnected;
    
    /** Total workflows executed */
    private long totalWorkflowsStarted;
    
    /** Total workflows succeeded */
    private long totalWorkflowsSucceeded;
    
    /** Total workflows failed */
    private long totalWorkflowsFailed;
    
    /** Engine start date */
    private Date engineStartTime;

    /** Date of this status update */
    private Date statusDate = new Date();
    
    /** List of invocations */
    private ArrayList<WorkflowInvocationRecord> invocations = new ArrayList<>();
    
    public WorkflowEngineStatusData() {
    }

    public long getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(long diskSize) {
        this.diskSize = diskSize;
    }

    public Date getEngineStartTime() {
        return engineStartTime;
    }

    public void setEngineStartTime(Date engineStartTime) {
        this.engineStartTime = engineStartTime;
    }

    public long getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(long freeSpace) {
        this.freeSpace = freeSpace;
    }

    public boolean isJmsConnected() {
        return jmsConnected;
    }

    public void setJmsConnected(boolean jmsConnected) {
        this.jmsConnected = jmsConnected;
    }

    public long getTotalWorkflowsStarted() {
        return totalWorkflowsStarted;
    }

    public void setTotalWorkflowsStarted(long totalWorkflowsStarted) {
        this.totalWorkflowsStarted = totalWorkflowsStarted;
    }

    public int getWorkflowCapacity() {
        return workflowCapacity;
    }

    public void setWorkflowCapacity(int workflowCapacity) {
        this.workflowCapacity = workflowCapacity;
    }

    public int getWorkflowCount() {
        return workflowCount;
    }

    public void setWorkflowCount(int workflowCount) {
        this.workflowCount = workflowCount;
    }

    public void setTotalWorkflowsFailed(long totalWorkflowsFailed) {
        this.totalWorkflowsFailed = totalWorkflowsFailed;
    }

    public long getTotalWorkflowsFailed() {
        return totalWorkflowsFailed;
    }

    public void setTotalWorkflowsSucceeded(long totalWorkflowsSucceeded) {
        this.totalWorkflowsSucceeded = totalWorkflowsSucceeded;
    }

    public long getTotalWorkflowsSucceeded() {
        return totalWorkflowsSucceeded;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public ArrayList<WorkflowInvocationRecord> getInvocations() {
        return invocations;
    }

    public void setInvocations(ArrayList<WorkflowInvocationRecord> invocations) {
        this.invocations = invocations;
    }
    
}