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
package com.connexience.server.workflow.test;

import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.workflow.api.ApiProvider;
import com.connexience.server.workflow.api.*;
import com.connexience.server.model.security.*;
import com.connexience.server.model.folder.*;
import com.connexience.server.model.workflow.WorkflowInvocationFolder;
import com.connexience.server.workflow.service.DataProcessorServiceDefinition;

/**
 * Test setting up and calling REST version of the API
 * @author hugo
 */
public class APITest {
    public static void main(String[] args) {
        try {
            ApiProvider apiProvider = new ApiProvider();
            apiProvider.setHostName("localhost");
            apiProvider.setHttpPort(8080);
            apiProvider.setServerContext("/workflow");
            
            API api = apiProvider.createApi();
            api.authenticate("h.g.hiden@ncl.ac.uk", "V1an1W");
            Ticket t = api.getTicket();
            System.out.println("Authenticated: " + t.getUserId());
            
            String documentId = "ff80808132a074c60132a5cc65b90010";
            DocumentRecord doc = api.getDocument(documentId);
            System.out.println("Got doc: " + doc.getName());
            
            String invocationId = "8a8e007035e239c60135e254c551000c";
            WorkflowInvocationFolder inv = api.getWorkflowInvocation(invocationId);
            System.out.println("Got invocation: " + inv.getName());
                    
            inv.setDescription("Modified at: " + System.currentTimeMillis());
            System.out.println("Modified: " + inv.getDescription());
            api.saveWorkflowInvocation(inv);
            
            WorkflowInvocationFolder inv2 = api.getWorkflowInvocation(invocationId);
            System.out.println("Modified: " + inv2.getDescription());
            
            api.setCurrentBlockAsync(invocationId, "B" + System.currentTimeMillis(), 0);
            inv2 = api.getWorkflowInvocation(invocationId);
            System.out.println("ContetID: " + inv2.getCurrentBlockId());
            
            api.setCurrentBlockStreamingProcessAsync(invocationId, inv2.getCurrentBlockId(), 1000000, 40);
            
            api.logWorkflowDequeuedAsync(invocationId);
            api.logWorkflowExecutionStartedAsync(invocationId);
            api.logWorkflowCompleteAsync(invocationId, "Completed OK");
            
            User u = api.getUser(t.getUserId());
            System.out.println("Home folder of: " + u.getName() + ": " + u.getHomeFolderId());
            
            Folder f = new Folder();
            f.setContainerId(u.getHomeFolderId());
            f.setName("Test Folder");
            f = api.saveFolder(f);
            System.out.println("Saved folder: " + f.getId() + ": " + f.getOrganisationId() + ": " + f.getCreatorId());
            
            DataProcessorServiceDefinition def = api.getService("blocks-core-io-csvimport");
            System.out.println("Service definition: " + def.getCategory() + ": " + def.getName() + ": " + def.getServiceBackend() + ": " + def.getServiceRoutine());
            
            System.out.println("Latest version of: blocks-core-io-importfile: " + api.getLatestVersionId("blocks-core-io-importfile"));
            
            System.out.println("CORE library id: " + api.getDynamicWorkflowLibraryByName("core").getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
