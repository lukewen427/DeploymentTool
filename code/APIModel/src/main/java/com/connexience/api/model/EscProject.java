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

import com.connexience.api.model.json.JSONObject;
import com.connexience.api.model.json.JsonSerializable;
import javax.xml.bind.annotation.XmlType;

/**
 * This clas represents a project / group within ESC
 * @author hugo
 */
@XmlType
public class EscProject implements JsonSerializable {
    private String id;
    private String name;
    private String description;
    private String workflowFolderId;
    private String dataFolderId;
    private String creatorId;
    
    public EscProject() {
    }
    
    public EscProject(JSONObject json){
        parseJsonObject(json);
    }
    
    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getDataFolderId() {
        return dataFolderId;
    }

    public void setDataFolderId(String dataFolderId) {
        this.dataFolderId = dataFolderId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkflowFolderId() {
        return workflowFolderId;
    }

    public void setWorkflowFolderId(String workflowFolderId) {
        this.workflowFolderId = workflowFolderId;
    }

    @Override
    public void parseJsonObject(JSONObject json) {
        creatorId = json.getString("creatorId", null);
        id = json.getString("id", null);
        name = json.getString("name", null);
        description = json.getString("description", null);
        dataFolderId = json.getString("dataFolderId", null);
        workflowFolderId = json.getString("workflowFolderId", null);
    }

    @Override
    public JSONObject toJsonObject() {
        return new JSONObject(this);
    }
    
    
}