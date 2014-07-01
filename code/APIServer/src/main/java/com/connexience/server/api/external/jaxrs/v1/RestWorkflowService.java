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
package com.connexience.server.api.external.jaxrs.v1;

import com.connexience.api.model.EscWorkflow;
import com.connexience.api.model.EscWorkflowInvocation;
import com.connexience.api.model.EscWorkflowParameterList;
import com.connexience.api.model.WorkflowInterface;
import com.connexience.server.ConnexienceException;
import com.connexience.server.api.external.helpers.WorkflowHelper;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.security.Ticket;
import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * This class provides the REST endpoint for the publically accessible workflow
 * service.
 * @author hugo
 */
@Path("/public/rest/v1/workflow")
public class RestWorkflowService implements WorkflowInterface {
    @Context SecurityContext secContext;
    
    /** Create a ticket for the current security context */
    private Ticket getTicket() throws ConnexienceException {
        return EJBLocator.lookupTicketBean().createWebTicket(secContext.getUserPrincipal().getName());
    }

    @GET
    @Path("/workflows")
    @Produces("application/json")
    @Override
    public EscWorkflow[] listWorkflows() throws Exception {
        return new WorkflowHelper(getTicket()).listWorkflows();
    }

    @GET
    @Path("/relatedinvocations/{documentId}")
    @Produces("application/json")
    @Override
    public EscWorkflowInvocation[] listInvocationsRelatedToDocument(@PathParam(value="documentId")String documentId) throws Exception {
        return new WorkflowHelper(getTicket()).listInvocationsRelatedToDocument(documentId);
    }

    @GET
    @Path("/callableworkflows")
    @Produces("application/json")
    @Override
    public EscWorkflow[] listCallableWorkflows() throws Exception {
        return new WorkflowHelper(getTicket()).listCallableWorkflows();
    }

    @GET
    @Path("/callableworkflows/{id}/parameters")
    @Produces("application/json")
    @Override
    public HashMap<String, String> listCallableWorkflowParameters(@PathParam(value="id")String workflowId) throws Exception {
        return new WorkflowHelper(getTicket()).listCallableWorkflowParameters(workflowId);
    }
    
    @GET
    @Path("/workflows/{id}")
    @Produces("application/json")
    @Override
    public EscWorkflow getWorkflow(@PathParam(value="id")String workflowId) throws Exception {
        return new WorkflowHelper(getTicket()).getWorkflow(workflowId);
    }

    @DELETE
    @Path("/workflows/{id}")
    @Override
    public void deleteWorkflow(@PathParam(value="id")String workflowId) throws Exception {
        new WorkflowHelper(getTicket()).deleteWorkflow(workflowId);
    }

    @POST
    @Path("/deleteworkflow")
    @Consumes("text/plain")
    public void deleteWorkflowUsingPOST(@FormParam(value="id")String workflowId) throws Exception {
        new WorkflowHelper(getTicket()).deleteWorkflow(workflowId);
    }
    
    @POST
    @Path("/workflows/{id}/invoke")
    @Produces("application/json")
    @Override
    public EscWorkflowInvocation executeWorkflow(@PathParam(value="id")String workflowId) throws Exception {
        return new WorkflowHelper(getTicket()).executeWorkflow(workflowId);
    }

    @POST
    @Path("/workflows/{id}/{versionid}/invoke")
    @Produces("application/json")
    @Override
    public EscWorkflowInvocation executeWorkflow(@PathParam(value="id")String workflowId, @PathParam(value="versionid")String versionId) throws Exception {
        return new WorkflowHelper(getTicket()).executeWorkflow(workflowId, versionId);
    }

    @POST
    @Path("/workflows/{id}/docinvoke")
    @Produces("application/json")
    @Consumes("text/plain")
    @Override
    public EscWorkflowInvocation executeWorkflowOnDocument(@PathParam(value="id")String workflowId, String documentId) throws Exception {
        return new WorkflowHelper(getTicket()).executeWorkflowOnDocument(workflowId, documentId);
    }

    @POST
    @Path("/workflows/{id}/{versionid}/docinvoke")
    @Produces("application/json")
    @Consumes("text/plain")
    @Override
    public EscWorkflowInvocation executeWorkflowOnDocument(@PathParam(value="id")String workflowId, @PathParam(value="versionid")String versionId, String documentId) throws Exception {
        return new WorkflowHelper(getTicket()).executeWorkflowOnDocument(workflowId, versionId, documentId);
    }

    @POST
    @Path("/workflows/{id}/paraminvoke")
    @Consumes("application/json")
    @Produces("application/json")
    @Override
    public EscWorkflowInvocation executeWorkflowWithParameters(@PathParam(value="id")String workflowId, EscWorkflowParameterList parameters) throws Exception {
        return new WorkflowHelper(getTicket()).executeWorkflowWithParameters(workflowId, parameters);
    }
    
    @POST
    @Path("/workflows/{id}/{versionid}/paraminvoke")
    @Consumes("application/json")
    @Produces("application/json")
    @Override
    public EscWorkflowInvocation executeWorkflowWithParameters(@PathParam(value="id")String workflowId, @PathParam(value="versionid")String versionId, EscWorkflowParameterList parameters) throws Exception {
        return new WorkflowHelper(getTicket()).executeWorkflowWithParameters(workflowId, versionId, parameters);
    }

    @GET
    @Path("/workflows/{id}/invocations")
    @Produces("application/json")
    @Override
    public EscWorkflowInvocation[] listInvocationsOfWorkflow(@PathParam(value="id")String workflowId) throws Exception {
        return new WorkflowHelper(getTicket()).listInvocationsOfWorkflow(workflowId);
    }

    @GET
    @Path("/invocations/{id}")
    @Produces("application/json")
    @Override
    public EscWorkflowInvocation getInvocation(@PathParam(value="id")String invocationId) throws Exception {
        return new WorkflowHelper(getTicket()).getInvocation(invocationId);
    }

    @POST
    @Path("/invocations/{id}/terminate")
    @Produces("application/json")
    @Override
    public EscWorkflowInvocation terminateInvocation(@PathParam(value="id")String invocationId) throws Exception {
        return new WorkflowHelper(getTicket()).terminateInvocation(invocationId);
    }
}
