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
package com.connexience.server.model.datasets.system.sources;

import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.ejb.util.WorkflowEJBLocator;
import com.connexience.server.model.datasets.system.SimpleSystemDatasetItem;
import com.connexience.server.model.datasets.system.SystemDataset;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.quota.UserQuota;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.User;
import com.connexience.server.model.workflow.DynamicWorkflowService;
import com.connexience.server.model.workflow.WorkflowDocument;
import com.connexience.server.model.workflow.WorkflowInvocationFolder;
import com.connexience.server.util.JSONDate;
import org.json.JSONObject;

/**
 * This data set provides general user statistics
 * @author hugo
 */
public class UserStatsDataset extends SystemDataset {

    public UserStatsDataset() {
        super("core-datasets-userstats");
        setName("User Statistics");
        addItem(new UserStatsItem());
        addItem(new FileQuotaItem());
        addItem(new WorkflowStatusItem());
    }
    
    public class WorkflowStatusItem extends SimpleSystemDatasetItem {

        public WorkflowStatusItem() {
            setName("WorkflowStatus");
        }
        
        @Override
        public JSONObject getMetadata(Ticket ticket) throws Exception {
            JSONObject metadata = new JSONObject();
            JSONObject descriptions = new JSONObject();
            descriptions.put("Running", "$ executing workflows");
            descriptions.put("Queued", "$ queued workflows");
            descriptions.put("Completed", "$ completed workflows");
            descriptions.put("Failed", "$ failed workflows");
            metadata.put("descriptions", descriptions);
            
            JSONObject icons = new JSONObject();
            metadata.put("icons", icons);
            return metadata;
        }

        @Override
        public JSONObject getValue(Ticket ticket) throws Exception {
            int succeeded = WorkflowEJBLocator.lookupWorkflowManagementBean().getAllInvocationFoldersForUser(ticket, WorkflowInvocationFolder.INVOCATION_FINISHED_OK, false).size();
            int queued = WorkflowEJBLocator.lookupWorkflowManagementBean().getAllInvocationFoldersForUser(ticket, WorkflowInvocationFolder.INVOCATION_WAITING, false).size();
            int running = WorkflowEJBLocator.lookupWorkflowManagementBean().getAllInvocationFoldersForUser(ticket, WorkflowInvocationFolder.INVOCATION_RUNNING, false).size();
            int failed = WorkflowEJBLocator.lookupWorkflowManagementBean().getAllInvocationFoldersForUser(ticket, WorkflowInvocationFolder.INVOCATION_FINISHED_WITH_ERRORS, false).size();
            JSONObject value = new JSONObject();
            value.put("Running", running);
            value.put("Queued", queued);
            value.put("Completed", succeeded);
            value.put("Failed", failed);
            return value;
        }
    }
    
    public class FileQuotaItem extends SimpleSystemDatasetItem {

        public FileQuotaItem() {
            setName("FileQuota");
        }

        @Override
        public JSONObject getMetadata(Ticket ticket) throws Exception {
            JSONObject metadata = new JSONObject();
            JSONObject descriptions = new JSONObject();
            descriptions.put("Used", "Total storage used $ bytes");
            descriptions.put("Available", "Available storage $ bytes");
            metadata.put("descriptions", descriptions);
            
            JSONObject icons = new JSONObject();
            metadata.put("icons", icons);
            return metadata;
        }

        @Override
        public JSONObject getValue(Ticket ticket) throws Exception {
            JSONObject value = new JSONObject();
            if(EJBLocator.lookupQuotaBean().userHasStorageQuota(ticket, ticket.getUserId())){
                UserQuota quota = EJBLocator.lookupQuotaBean().getOrCreateUserQuota(ticket, ticket.getUserId());
                long used = EJBLocator.lookupQuotaBean().getStorageQuotaUsed(ticket, ticket.getUserId());
                
                long available = quota.getStorageQuota() - used;
                if(available<0){
                    available = 0;
                }
                
                value.put("Available", available);
                value.put("Used", used);
 
            } else {
                long used = EJBLocator.lookupQuotaBean().getStorageQuotaUsed(ticket, ticket.getUserId());
                value.put("Available", used);
                value.put("Used", used);
            }
            return value;
        }
    }
    
    public class UserStatsItem extends SimpleSystemDatasetItem {

        public UserStatsItem() {
            setName("GeneralData");
        }
        
        @Override
        public JSONObject getValue(Ticket ticket) throws Exception {
            JSONObject value = new JSONObject();

            User user = EJBLocator.lookupUserDirectoryBean().getUser(ticket, ticket.getUserId());
            value.put("friends", EJBLocator.lookupUserDirectoryBean().getNumberOfFriends(ticket, user));
            value.put("groups", EJBLocator.lookupUserDirectoryBean().listGroupMembershipForUser(ticket, ticket.getUserId()).size() -1); //remove the 'user' group
            value.put("workflows", EJBLocator.lookupObjectDirectoryBean().getNumberOfOwnedObjects(ticket, ticket.getUserId(), WorkflowDocument.class));
            value.put("workflowBlocks", EJBLocator.lookupObjectDirectoryBean().getNumberOfOwnedObjects(ticket, ticket.getUserId(), DynamicWorkflowService.class));
            value.put("files", EJBLocator.lookupObjectDirectoryBean().getNumberOfOwnedObjects(ticket, ticket.getUserId(), DocumentRecord.class));
            value.put("sharedFiles", EJBLocator.lookupObjectDirectoryBean().getNumberOfSharedFiles(ticket, ticket.getUserId()));
            value.put("publicFiles", EJBLocator.lookupObjectDirectoryBean().getNumberOfPublicObjects(ticket, ticket.getUserId()));
            
            return value;
        }

        @Override
        public JSONObject getMetadata(Ticket ticket) throws Exception {
            JSONObject metadata = new JSONObject();
            // Descriptions 
            JSONObject descriptions = new JSONObject();
            descriptions.put("friends", "$ Connections");
            descriptions.put("groups", "Member of $ groups");
            descriptions.put("workflows", "$ Workflows");
            descriptions.put("workflowBlocks", "Developed $ workflow blocks");
            descriptions.put("files", "$ files");
            descriptions.put("sharedFiles", "$ shared files");
            descriptions.put("publicFiles", "$ public files");
            metadata.put("descriptions", descriptions);
            
            // Icons
            JSONObject icons = new JSONObject();
            icons.put("friends", "users");
            icons.put("groups", "users");
            icons.put("workflows", "cog");
            icons.put("workflowBlocks", "cog");
            icons.put("files", "file");
            icons.put("sharedFiles", "file");
            icons.put("publicFiles", "file");
            metadata.put("icons", icons);
            return metadata;
        }
        
        
        
    }
}
