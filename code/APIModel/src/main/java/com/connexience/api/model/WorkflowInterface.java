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
package com.connexience.api.model;

import java.util.HashMap;

/**
 * This interface describes the functionality of the externally accessible
 * workflow management API.
 * @author hugo
 */
public interface WorkflowInterface {
    /**
     * List workflows
     */
    EscWorkflow[] listWorkflows() throws Exception;
    
    /**
     * List all of the workflow invocations related to a document
     */
    EscWorkflowInvocation[] listInvocationsRelatedToDocument(String documentId) throws Exception;
    
    /**
     * List all of the workflows that can be called as webservices
     */
    EscWorkflow[] listCallableWorkflows() throws Exception;
    
    /**
     * List of parameters required by a callable workflow
     */
    HashMap<String, String> listCallableWorkflowParameters(String workflowId) throws Exception;
    
    /**
     * Get a workflow by ID
     */
    EscWorkflow getWorkflow(String workflowId) throws Exception;
    
    /**
     * Delete a workflow
     */
    void deleteWorkflow(String workflowId) throws Exception;
    
    /**
     * Execute a workflow
     */
    EscWorkflowInvocation executeWorkflow(String workflowId) throws Exception;
    
    /**
     * Execute a version of a workflow
     */
    EscWorkflowInvocation executeWorkflow(String workflowId, String versionId) throws Exception;
    
    /** 
     * Execute a workflow on a document
     */
    EscWorkflowInvocation executeWorkflowOnDocument(String workflowId, String documentId) throws Exception;
    
    /** 
     * Execute a version of a workflow on a document
     */
    EscWorkflowInvocation executeWorkflowOnDocument(String workflowId, String versionId, String documentId) throws Exception;
    
    /**
     * Execute a workflow with a list of parameters
     */
    EscWorkflowInvocation executeWorkflowWithParameters(String workflowId, EscWorkflowParameterList parameters) throws Exception;
    
    /** Execute a version of a workflow with a list of parameters */
    EscWorkflowInvocation executeWorkflowWithParameters(String workflowId, String versionId, EscWorkflowParameterList parameters) throws Exception;
    
    /**
     * List the invocations of a workflow
     */
    EscWorkflowInvocation[] listInvocationsOfWorkflow(String workflowId) throws Exception;
    
    /**
     * Get an invocation by ID
     */        
    EscWorkflowInvocation getInvocation(String invocationId) throws Exception;
    
    /**
     * Terminate an invocation
     */
    EscWorkflowInvocation terminateInvocation(String invocationId) throws Exception;
}