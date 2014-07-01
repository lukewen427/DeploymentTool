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
import com.connexience.server.model.datasets.system.ProjectSystemDatasetItem;
import com.connexience.server.model.datasets.system.SystemDataset;
import com.connexience.server.model.project.Project;
import com.connexience.server.model.project.study.LoggerDeployment;
import com.connexience.server.model.project.study.Study;
import com.connexience.server.model.project.study.Subject;
import com.connexience.server.model.project.study.SubjectGroup;
import com.connexience.server.model.security.Ticket;
import java.util.Collection;
import org.json.JSONObject;

/**
 * Objects that extend this object can target projects to gather data
 * @author hugo
 */
public class ProjectDataset extends SystemDataset {

    public ProjectDataset() {
        super("core-datasets-project-stats");
        setName("Project Statistics");
        addItem(new ProjectSummary());
    }

    public class ProjectSummary extends ProjectSystemDatasetItem {

        public ProjectSummary() {
            setName("Summary");
        }

        @Override
        public JSONObject getMetadata(Ticket ticket, Project project) throws Exception {
            JSONObject metadata = new JSONObject();
            JSONObject descriptions = new JSONObject();
            descriptions.put("activeLoggers", "$ Active loggers");
            descriptions.put("allLoggers", "$ Configured loggers");
            descriptions.put("adminUsers", "$ Project administrators");
            descriptions.put("subjects", "$ Study participants");
            descriptions.put("members", "$ Project members");
            descriptions.put("name" ,"Project name: $");
            metadata.put("descriptions", descriptions);
            return metadata;            
        }

        @Override
        public JSONObject getValue(Ticket ticket, Project project) throws Exception {
            JSONObject value = new JSONObject();
            value.put("name", project.getName());
            if(project instanceof Study){
                Study s = (Study) project;
                
                // Number of loggers - active deployments are marked active or not
                Collection<LoggerDeployment> activeDeployments = EJBLocator.lookupLoggersBean().getLoggerDeploymentsByStudy(ticket, project.getId(), true);
                value.put("activeLoggers", activeDeployments.size());
                
                Collection<LoggerDeployment> allDeployments = EJBLocator.lookupLoggersBean().getLoggerDeploymentsByStudy(ticket, project.getId());
                value.put("allLoggers", allDeployments.size());
                
                // Get number of project members using the members group ID
                String adminGroupId = project.getAdminGroupId();
                value.put("adminUsers", EJBLocator.lookupGroupDirectoryBean().getNumberOfGroupMembers(ticket, adminGroupId));
                
                // Number of study participants
                Collection<Subject> subjects = EJBLocator.lookupSubjectsBean().getSubjects(ticket, s.getId());
                value.put("subjects", subjects.size());
                
                // Get number of project admins using group ID
                String memberGroupId = project.getMembersGroupId();
                value.put("members", EJBLocator.lookupGroupDirectoryBean().getNumberOfGroupMembers(ticket, memberGroupId));
                
                // Logger data gives you the number of files from Study bean
                int fileCount = 0;
                for(LoggerDeployment d : activeDeployments){
                    fileCount+=d.getLoggerData().size();
                }
                value.put("loggerFiles", fileCount);
                
            }
            return value;
        }

        @Override
        public String getTypeLabel() {
            return "Summary";
        }
    }
}