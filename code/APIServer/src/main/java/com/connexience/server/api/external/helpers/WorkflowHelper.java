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
package com.connexience.server.api.external.helpers;

import com.connexience.api.model.EscWorkflow;
import com.connexience.api.model.EscWorkflowInvocation;
import com.connexience.api.model.EscWorkflowParameterList;
import com.connexience.api.model.WorkflowInterface;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.ejb.util.WorkflowEJBLocator;
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.workflow.*;
import com.connexience.server.util.SerializationUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * This class provides a helper that supports the various workflow services.
 * @author hugo
 */
public class WorkflowHelper implements WorkflowInterface {
    Ticket t;

    public WorkflowHelper(Ticket t) {
        this.t = t;
    }

    @Override
    public EscWorkflow[] listWorkflows() throws Exception {
        List workflows = WorkflowEJBLocator.lookupWorkflowManagementBean().listWorkflows(t);
        EscWorkflow[] results = new EscWorkflow[workflows.size()];
        for(int i=0;i<workflows.size();i++){
            results[i] = EscObjectFactory.createEscWorkflow((WorkflowDocument)workflows.get(i));
        }
        return results;
    }

    @Override
    public EscWorkflowInvocation[] listInvocationsRelatedToDocument(String documentId) throws Exception {
        DocumentRecord source = EJBLocator.lookupStorageBean().getDocumentRecord(t, documentId);
        if(source!=null){
            Collection<ServerObject> results = EJBLocator.lookupLinkBean().getLinkedSourceObjects(t, source);
            ArrayList<EscWorkflowInvocation> wfs = new ArrayList<>();
            for(ServerObject o : results){
                if(o instanceof WorkflowInvocationFolder){
                    wfs.add(EscObjectFactory.createEscWorkflowInvocation( (WorkflowInvocationFolder)o, EJBLocator.lookupObjectInfoBean().getObjectName(t, ((WorkflowInvocationFolder)o).getWorkflowId())));
                }
            }
            int count = 0;
            EscWorkflowInvocation[] related = new EscWorkflowInvocation[wfs.size()];
            for(EscWorkflowInvocation d : wfs){
                related[count] = d;
                count++;
            }
            return related;
        } else {
            return new EscWorkflowInvocation[0];
        }
    }

    
    @Override
    public EscWorkflow[] listCallableWorkflows() throws Exception {
        List workflows = WorkflowEJBLocator.lookupWorkflowManagementBean().listExternallyCallableWorkflows(t);
        EscWorkflow[] results = new EscWorkflow[workflows.size()];
        for(int i=0;i<workflows.size();i++){
            results[i] = EscObjectFactory.createEscWorkflow((WorkflowDocument)workflows.get(i));
        }
        return results;
    }

    @Override
    public HashMap<String, String> listCallableWorkflowParameters(String workflowId) throws Exception {
        WorkflowDocument workflow = WorkflowEJBLocator.lookupWorkflowManagementBean().getWorkflowDocument(t, workflowId);
        if(workflow.isExternalService()){
            HashMap<String, Class> paramClasses = WorkflowEJBLocator.lookupWorkflowManagementBean().getExternallyCallableWorkflowParameters(t, workflowId);
            HashMap<String, String> results = new HashMap<>();
            Class value;
            for(String key : paramClasses.keySet()){
                value = paramClasses.get(key);
                if(com.connexience.server.model.ServerObject.class.isAssignableFrom(value)){
                    // Server objects get referred to by ID
                    results.put(key, String.class.getName());
                } else {
                    if(value.equals(com.connexience.server.workflow.xmlstorage.StringListWrapper.class)){
                        // String array
                        results.put(key, ArrayList.class.getName());
                    
                    } else if(value.equals(com.connexience.server.workflow.xmlstorage.StringPairListWrapper.class)){
                        // Hashmap
                        results.put(key, HashMap.class.getName());
                        
                    } else {
                        // Simple type
                        results.put(key, paramClasses.get(key).getName());
                    }
                }
                
            }
            return results;
        } else {
            throw new Exception("Workflow: " + workflowId + " is not marked as externally callable");
        }
    }
    
    
    @Override
    public EscWorkflow getWorkflow(String workflowId) throws Exception {
        return EscObjectFactory.createEscWorkflow(WorkflowEJBLocator.lookupWorkflowManagementBean().getWorkflowDocument(t, workflowId));
    }

    @Override
    public void deleteWorkflow(String workflowId) throws Exception {
        WorkflowEJBLocator.lookupWorkflowManagementBean().deleteWorkflowDocument(t, workflowId);
    }

    @Override
    public EscWorkflowInvocation executeWorkflow(String workflowId) throws Exception {
        WorkflowInvocationMessage msg = new WorkflowInvocationMessage(t, workflowId);
        String invocationId = WorkflowEJBLocator.lookupWorkflowEnactmentBean().startWorkflow(t, msg);
        return EscObjectFactory.createEscWorkflowInvocation(WorkflowEJBLocator.lookupWorkflowManagementBean().getInvocationFolder(t, invocationId), EJBLocator.lookupObjectInfoBean().getObjectName(t, workflowId));
    }

    @Override
    public EscWorkflowInvocation executeWorkflow(String workflowId, String versionId) throws Exception {
        WorkflowInvocationMessage msg = new WorkflowInvocationMessage(t, workflowId, versionId);
        String invocationId = WorkflowEJBLocator.lookupWorkflowEnactmentBean().startWorkflow(t, msg);
        return EscObjectFactory.createEscWorkflowInvocation(WorkflowEJBLocator.lookupWorkflowManagementBean().getInvocationFolder(t, invocationId), EJBLocator.lookupObjectInfoBean().getObjectName(t, workflowId));
    }

    @Override
    public EscWorkflowInvocation executeWorkflowOnDocument(String workflowId, String documentId) throws Exception {
        WorkflowInvocationMessage msg = new WorkflowInvocationMessage(t, workflowId);
        msg.setTargetFileId(documentId);
        String invocationId = WorkflowEJBLocator.lookupWorkflowEnactmentBean().startWorkflow(t, msg);
        return EscObjectFactory.createEscWorkflowInvocation(WorkflowEJBLocator.lookupWorkflowManagementBean().getInvocationFolder(t, invocationId), EJBLocator.lookupObjectInfoBean().getObjectName(t, workflowId));
    }

    @Override
    public EscWorkflowInvocation executeWorkflowOnDocument(String workflowId, String versionId, String documentId) throws Exception {
        WorkflowInvocationMessage msg = new WorkflowInvocationMessage(t, workflowId, versionId);
        msg.setTargetFileId(documentId);
        String invocationId = WorkflowEJBLocator.lookupWorkflowEnactmentBean().startWorkflow(t, msg);
        return EscObjectFactory.createEscWorkflowInvocation(WorkflowEJBLocator.lookupWorkflowManagementBean().getInvocationFolder(t, invocationId), EJBLocator.lookupObjectInfoBean().getObjectName(t, workflowId));
    }

    @Override
    public EscWorkflowInvocation executeWorkflowWithParameters(String workflowId, EscWorkflowParameterList parameters) throws Exception {
        WorkflowInvocationMessage msg = new WorkflowInvocationMessage(t, workflowId);
        WorkflowParameterList params = EscObjectFactory.createEscWorkflowParameterList(parameters);
        msg.setParameterXmlData(SerializationUtils.serialize(params));
        String invocationId = WorkflowEJBLocator.lookupWorkflowEnactmentBean().startWorkflow(t, msg);
        return EscObjectFactory.createEscWorkflowInvocation(WorkflowEJBLocator.lookupWorkflowManagementBean().getInvocationFolder(t, invocationId), EJBLocator.lookupObjectInfoBean().getObjectName(t, workflowId));
    }

    @Override
    public EscWorkflowInvocation executeWorkflowWithParameters(String workflowId, String versionId, EscWorkflowParameterList parameters) throws Exception {
        WorkflowInvocationMessage msg = new WorkflowInvocationMessage(t, workflowId, versionId);
        WorkflowParameterList params = EscObjectFactory.createEscWorkflowParameterList(parameters);
        msg.setParameterXmlData(SerializationUtils.serialize(params));
        String invocationId = WorkflowEJBLocator.lookupWorkflowEnactmentBean().startWorkflow(t, msg);
        return EscObjectFactory.createEscWorkflowInvocation(WorkflowEJBLocator.lookupWorkflowManagementBean().getInvocationFolder(t, invocationId), EJBLocator.lookupObjectInfoBean().getObjectName(t, workflowId));
 
    }

    @Override
    public EscWorkflowInvocation[] listInvocationsOfWorkflow(String workflowId) throws Exception {
        List results = WorkflowEJBLocator.lookupWorkflowManagementBean().getInvocationFolders(t, workflowId);
        EscWorkflowInvocation[] invocations = new EscWorkflowInvocation[results.size()];
        String workflowName = EJBLocator.lookupObjectInfoBean().getObjectName(t, workflowId);
        for(int i=0;i<results.size();i++){
            invocations[i] = EscObjectFactory.createEscWorkflowInvocation((WorkflowInvocationFolder)results.get(i), workflowName);
        }
        return invocations;
    }

    @Override
    public EscWorkflowInvocation getInvocation(String invocationId) throws Exception {
        WorkflowInvocationFolder invocation = WorkflowEJBLocator.lookupWorkflowManagementBean().getInvocationFolder(t, invocationId);
        return EscObjectFactory.createEscWorkflowInvocation(invocation, EJBLocator.lookupObjectInfoBean().getObjectName(t, invocation.getWorkflowId()));
    }

    @Override
    public EscWorkflowInvocation terminateInvocation(String invocationId) throws Exception {
        WorkflowInvocationFolder invocation = WorkflowEJBLocator.lookupWorkflowManagementBean().getInvocationFolder(t, invocationId);
        WorkflowTerminationReport report = WorkflowEJBLocator.lookupWorkflowManagementBean().terminateInvocation(t, invocationId);
        return EscObjectFactory.createEscWorkflowInvocation(invocation, EJBLocator.lookupObjectInfoBean().getObjectName(t, invocation.getWorkflowId()));
    }
}