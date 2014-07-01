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
package com.connexience.server.model.datasets.queries.system;

import com.connexience.server.model.datasets.DatasetQuery;
import com.connexience.server.model.datasets.queries.system.enactors.ProjectDatasetItemQueryEnactor;
import com.connexience.server.model.datasets.system.ProjectSystemDatasetItem;
import com.connexience.server.model.project.Project;
import com.connexience.server.util.JSONProject;
import org.json.JSONException;
import org.json.JSONObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This query is targetted at a project dataset item
 * @author hugo
 */
public class ProjectDatasetItemQuery extends DatasetQuery {
    private int projectId = 0;
    private String projectName = "";
            
    public ProjectDatasetItemQuery() {
        supportedClass = ProjectSystemDatasetItem.class;
        enactorClass = ProjectDatasetItemQueryEnactor.class;
        label = "Project Data";
    }

    /** Get the project item */
    public JSONProject getProjectJson() throws JSONException {
        return new JSONProject("com.connexience.server.model.project.Project", projectId, projectName);
        
    }
    
    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("ProjectID", projectId);
        store.add("ProjectName", projectName);
        return store;
    }
    
    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store); 
        projectId = store.intValue("ProjectID", 0);
        projectName = store.stringValue("ProjectName", "");
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("Project", getProjectJson());
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
        if(json.has("Project")){
            JSONProject projectJson = new JSONProject(json.get("Project").toString());
            projectId = projectJson.getProjectId();
            projectName = projectJson.getProjectName();
        }
    }
}