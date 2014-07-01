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
package com.connexience.server.model.datasets.queries.system.enactors;

import com.connexience.server.ConnexienceException;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.datasets.DatasetQueryEnactor;
import com.connexience.server.model.datasets.DatasetsUtils;
import com.connexience.server.model.datasets.queries.system.ProjectDatasetItemQuery;
import com.connexience.server.model.datasets.system.ProjectSystemDatasetItem;
import com.connexience.server.model.datasets.system.SimpleSystemDatasetItem;
import com.connexience.server.model.project.Project;
import com.connexience.server.util.JSONContainer;
import com.connexience.server.util.JSONProject;
import javax.ejb.EJBLocalHome;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class can query a specific project for data
 * @author hugo
 */
public class ProjectDatasetItemQueryEnactor extends DatasetQueryEnactor {

    public ProjectDatasetItemQueryEnactor() {
        connectionType = CONNECTION_TYPE.NO_CONNECTION;
    }
    
    @Override
    public JSONContainer performQuery() throws ConnexienceException {
        if(item instanceof ProjectSystemDatasetItem){
            try {
                JSONObject value;
                JSONProject p = ((ProjectDatasetItemQuery)query).getProjectJson();
                Project project = EJBLocator.lookupProjectsBean().getProject(ticket, p.getProjectId());
                
                if(query.getKeyArray()!=null && query.getKeyArray().length>0){
                    value = DatasetsUtils.filterJson(((ProjectSystemDatasetItem)item).getValue(ticket, project), query.getKeyArray());
                } else {
                    value = ((ProjectSystemDatasetItem)item).getValue(ticket, project);
                }
                JSONArray data = new JSONArray();
                data.put(value);
                
                JSONObject result = new JSONObject();
                result.put("data", data);
                result.put("metadata", ((ProjectSystemDatasetItem)item).getMetadata(ticket, project));
                
                return new JSONContainer(result);                
            } catch (Exception e){
                throw new ConnexienceException("Error getting system data item value: " + e.getMessage(), e);
            }
            
        } else {
            throw new ConnexienceException("Unsupported project data item");
        }
    }
}