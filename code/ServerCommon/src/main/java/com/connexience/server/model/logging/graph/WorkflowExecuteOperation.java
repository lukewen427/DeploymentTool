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
package com.connexience.server.model.logging.graph;

import java.util.Date;

/**
 * This operation is triggered whenever a user writes a piece of
 * data.
 *
 * @author simon
 */
public class WorkflowExecuteOperation extends GraphOperation {
    /**
     * Class version UID.
     * <p/>
     * Please increment this value whenever your changes may cause
     * incompatibility with the previous version of this class. If unsure, ask
     * one of the core development team or read:
     * http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     * http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 1L;


    /**
     * ID of the workflow
     */
    String workflowId;

    /**
     * ID of the version
     */
    String versionId;

    /**
     * Id of the invocation
     */
    String invocationId;

    /**
     * Name of the workflow
     */
    String name;

    /**
     * Id of the parent workflow which started this workflow
     */
    String parentWorkflowId;

    /**
     * Id of the parent invocation
     */
    String parentInvocationId;

    /**
     * VersionId of the parent workflow
     */
    String parentWorkflowVersionId;


    public WorkflowExecuteOperation() {
    }

    public WorkflowExecuteOperation(String workflowId, String versionId, String invocationId, String name, String userId, Date timestamp) {
        super();
        this.workflowId = workflowId;
        this.versionId = versionId;
        this.name = name;
        this.invocationId = invocationId;
        setUserId(userId);
        setTimestamp(timestamp);
    }

    public WorkflowExecuteOperation(String workflowId, String versionId, String invocationId, String name, String parentWorkflowId, String parentInvocationId, String parentWorkflowVersionId, String userId, Date timestamp) {
        this.workflowId = workflowId;
        this.versionId = versionId;
        this.invocationId = invocationId;
        this.name = name;
        this.parentWorkflowId = parentWorkflowId;
        this.parentInvocationId = parentInvocationId;
        this.parentWorkflowVersionId = parentWorkflowVersionId;
        setUserId(userId);
        setTimestamp(timestamp);
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentWorkflowId() {
        return parentWorkflowId;
    }

    public void setParentWorkflowId(String parentWorkflowId) {
        this.parentWorkflowId = parentWorkflowId;
    }

    public String getParentInvocationId() {
        return parentInvocationId;
    }

    public void setParentInvocationId(String parentInvocationId) {
        this.parentInvocationId = parentInvocationId;
    }

    public String getParentWorkflowVersionId() {
        return parentWorkflowVersionId;
    }

    public void setParentWorkflowVersionId(String parentWorkflowVersionId) {
        this.parentWorkflowVersionId = parentWorkflowVersionId;
    }

    public String getInvocationId() {
        return invocationId;
    }

    public void setInvocationId(String invocationId) {
        this.invocationId = invocationId;
    }
}