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
package com.connexience.server.api.external.jaxws.v1;

import com.connexience.api.model.EscWorkflow;
import com.connexience.api.model.EscWorkflowInvocation;
import com.connexience.api.model.EscWorkflowParameterList;
import com.connexience.api.model.WorkflowInterface;
import com.connexience.server.ConnexienceException;
import com.connexience.server.api.external.helpers.WorkflowHelper;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.security.Ticket;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * This class provides the SOAP endpoint for the publically accessible workflow
 * service.
 * @author hugo
 */
@WebService(serviceName = "workflowv1")
public class EscWorkflowWs implements WorkflowInterface {
    @Resource WebServiceContext jaxWsContext;
    
    private Ticket getTicket() throws ConnexienceException {    
        return EJBLocator.lookupTicketBean().createWebTicket(jaxWsContext.getUserPrincipal().getName());
    }
    
    @WebMethod(operationName = "listWorkflows")
    @Override
    public EscWorkflow[] listWorkflows() throws Exception {
        return new WorkflowHelper(getTicket()).listWorkflows();
    }

    @WebMethod(operationName = "listInvocationsRelatedToDocument")
    @Override
    public EscWorkflowInvocation[] listInvocationsRelatedToDocument(String documentId) throws Exception {
        return new WorkflowHelper(getTicket()).listInvocationsRelatedToDocument(documentId);
    }
    
    @WebMethod(operationName = "listCallableWorkflows")
    @Override
    public EscWorkflow[] listCallableWorkflows() throws Exception {
        return new WorkflowHelper(getTicket()).listCallableWorkflows();
    }

    @WebMethod(operationName = "listCallableWorkflowParameters")
    @Override
    public HashMap<String, String> listCallableWorkflowParameters(String workflowId) throws Exception {
        return new WorkflowHelper(getTicket()).listCallableWorkflowParameters(workflowId);
    }
    
    @WebMethod(operationName = "getWorkflow")
    @Override
    public EscWorkflow getWorkflow(String workflowId) throws Exception {
        return new WorkflowHelper(getTicket()).getWorkflow(workflowId);
    }

    @WebMethod(operationName = "deleteWorkflow")
    @Override
    public void deleteWorkflow(String workflowId) throws Exception {
        new WorkflowHelper(getTicket()).deleteWorkflow(workflowId);
    }

    @WebMethod(operationName = "executeWorkflow")
    @Override
    public EscWorkflowInvocation executeWorkflow(String workflowId) throws Exception {
        return new WorkflowHelper(getTicket()).executeWorkflow(workflowId);
    }

    @WebMethod(operationName = "executeWorkflowWithVersionId")
    @Override
    public EscWorkflowInvocation executeWorkflow(String workflowId, String versionId) throws Exception {
        return new WorkflowHelper(getTicket()).executeWorkflow(workflowId, versionId);
    }

    @WebMethod(operationName = "executeWorkflowOnDocument")
    @Override
    public EscWorkflowInvocation executeWorkflowOnDocument(String workflowId, String documentId) throws Exception {
        return new WorkflowHelper(getTicket()).executeWorkflowOnDocument(workflowId, documentId);
    }

    @WebMethod(operationName = "executeWorkflowOnDocumentWithVersionId")
    @Override
    public EscWorkflowInvocation executeWorkflowOnDocument(String workflowId, String versionId, String documentId) throws Exception {
        return new WorkflowHelper(getTicket()).executeWorkflowOnDocument(workflowId, versionId, documentId);
    }

    @WebMethod(operationName = "executeWorkflowWithParameters")
    @Override
    public EscWorkflowInvocation executeWorkflowWithParameters(String workflowId, EscWorkflowParameterList parameters) throws Exception {
        return new WorkflowHelper(getTicket()).executeWorkflowWithParameters(workflowId, parameters);
    }

    @WebMethod(operationName = "executeWorkflowWithParametersAndVersionId")
    @Override
    public EscWorkflowInvocation executeWorkflowWithParameters(String workflowId, String versionId, EscWorkflowParameterList parameters) throws Exception {
        return new WorkflowHelper(getTicket()).executeWorkflowWithParameters(workflowId, versionId, parameters);
    }

    @WebMethod(operationName = "listInvocationsOfWorkflow")
    @Override
    public EscWorkflowInvocation[] listInvocationsOfWorkflow(String workflowId) throws Exception {
        return new WorkflowHelper(getTicket()).listInvocationsOfWorkflow(workflowId);
    }

    @WebMethod(operationName = "getInvocation")
    @Override
    public EscWorkflowInvocation getInvocation(String workflowId) throws Exception {
        return new WorkflowHelper(getTicket()).getInvocation(workflowId);
    }

    @WebMethod(operationName = "terminateInvocation")
    @Override
    public EscWorkflowInvocation terminateInvocation(String invocationId) throws Exception {
        return new WorkflowHelper(getTicket()).terminateInvocation(invocationId);
    }
}
