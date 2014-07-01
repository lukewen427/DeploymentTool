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
package com.connexience.api;

import com.connexience.api.model.EscWorkflow;
import com.connexience.api.model.EscWorkflowInvocation;
import com.connexience.api.model.EscWorkflowParameterList;
import com.connexience.api.model.WorkflowInterface;
import com.connexience.api.model.json.JSONArray;
import com.connexience.api.model.json.JSONObject;
import com.connexience.api.model.net.GenericClient;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class provides a client for the REST workflow service.
 * @author hugo
 */
public class WorkflowClient extends GenericClient implements WorkflowInterface {

    public WorkflowClient(String hostname, int port, boolean secure, String username, String password) {
        super(hostname, port, secure, "/api/public/rest/v1/workflow", username, password);
    }

    public WorkflowClient() throws Exception {
        super("/api/public/rest/v1/workflow");
    }
    
    public WorkflowClient(GenericClient existingClient) throws Exception {
        existingClient.configureClient(this);
        this.setUrlBase("/api/public/rest/v1/workflow");
    }
    
    public WorkflowClient(File apiProperties) throws Exception {
        super("/api/public/rest/v1/workflow", apiProperties);
    }
    
    @Override
    public EscWorkflow[] listWorkflows() throws Exception {
        JSONArray json = retrieveJsonArray("/workflows");
        EscWorkflow[] results = new EscWorkflow[json.length()];
        for(int i=0;i<json.length();i++){
            results[i] = new EscWorkflow(json.getJSONObject(i));
        }
        return results;
    }

    @Override
    public EscWorkflowInvocation[] listInvocationsRelatedToDocument(String documentId) throws Exception {
        JSONArray results = retrieveJsonArray("/relatedinvocations/" + documentId);
        EscWorkflowInvocation[] invocations = new EscWorkflowInvocation[results.length()];
        for(int i=0;i<results.length();i++){
            invocations[i] = new EscWorkflowInvocation(results.getJSONObject(i));
        }
        return invocations;        
    }

    
    @Override
    public EscWorkflow[] listCallableWorkflows() throws Exception {
        JSONArray json = retrieveJsonArray("/callableworkflows");
        EscWorkflow[] results = new EscWorkflow[json.length()];
        for(int i=0;i<json.length();i++){
            results[i] = new EscWorkflow(json.getJSONObject(i));
        }
        return results;
    }

    @Override
    public EscWorkflow getWorkflow(String workflowId) throws Exception {
        return new EscWorkflow(retrieveJson("/workflows/" + workflowId));
    }

    @Override
    public void deleteWorkflow(String workflowId) throws Exception {
        deleteResource("/workflows/" + workflowId);
    }

    @Override
    public EscWorkflowInvocation executeWorkflow(String workflowId) throws Exception {
        return new EscWorkflowInvocation(postTextRetrieveJson("/workflows/" + workflowId + "/invoke", "EXECUTE"));
    }

    @Override
    public EscWorkflowInvocation executeWorkflow(String workflowId, String versionId) throws Exception {
        return new EscWorkflowInvocation(postTextRetrieveJson("/workflows/" + workflowId + "/" + versionId + "/invoke", "EXECUTE"));
    }

    @Override
    public EscWorkflowInvocation executeWorkflowOnDocument(String workflowId, String documentId) throws Exception {
        return new EscWorkflowInvocation(postTextRetrieveJson("/workflows/" + workflowId + "/docinvoke", documentId));
    }

    @Override
    public EscWorkflowInvocation executeWorkflowOnDocument(String workflowId, String versionId, String documentId) throws Exception {
        return new EscWorkflowInvocation(postTextRetrieveJson("/workflows/" + workflowId + "/" + versionId + "/docinvoke", documentId));
    }

    @Override
    public EscWorkflowInvocation executeWorkflowWithParameters(String workflowId, EscWorkflowParameterList parameters) throws Exception {
        return new EscWorkflowInvocation(postJsonRetrieveJson("/workflows/" + workflowId + "/paraminvoke", parameters.toJsonObject()));
    }

    @Override
    public EscWorkflowInvocation executeWorkflowWithParameters(String workflowId, String versionId, EscWorkflowParameterList parameters) throws Exception {
        return new EscWorkflowInvocation(postJsonRetrieveJson("/workflows/" + workflowId + "/" + versionId + "/paraminvoke", parameters.toJsonObject()));
    }

    @Override
    public EscWorkflowInvocation[] listInvocationsOfWorkflow(String workflowId) throws Exception {
        JSONArray results = retrieveJsonArray("/workflows/" + workflowId + "/invocations");
        EscWorkflowInvocation[] invocations = new EscWorkflowInvocation[results.length()];
        for(int i=0;i<results.length();i++){
            invocations[i] = new EscWorkflowInvocation(results.getJSONObject(i));
        }
        return invocations;
    }

    @Override
    public EscWorkflowInvocation getInvocation(String invocationId) throws Exception {
        return new EscWorkflowInvocation(retrieveJson("/invocations/" + invocationId));
    }

    @Override
    public EscWorkflowInvocation terminateInvocation(String invocationId) throws Exception {
        return new EscWorkflowInvocation(postTextRetrieveJson("/invocations/" + invocationId + "/terminate", "TERMINATE"));
    }

    @Override
    public HashMap<String, String> listCallableWorkflowParameters(String workflowId) throws Exception {
        JSONObject results = retrieveJson("/callableworkflows/" + workflowId + "/parameters");
        HashMap<String, String> params = new HashMap<>();
        Iterator keys = results.keys();
        String key, value;
        while(keys.hasNext()){
            key = keys.next().toString();
            value = results.getString(key);
            params.put(key, value);
        }
        return params;
    }
}
