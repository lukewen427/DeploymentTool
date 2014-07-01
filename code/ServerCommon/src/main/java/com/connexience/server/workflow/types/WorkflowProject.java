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

package com.connexience.server.workflow.types;
import com.connexience.server.util.JSONEditable;
import java.io.Serializable;
import java.util.HashMap;
import org.json.JSONObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;
/**
 * This class provides a wrapper for a project that can be sent to the workflow 
 * engine without issues with sending JPA objects to the workflow engine.
 * @author hugo
 */
public class WorkflowProject implements Serializable, XmlStorable, JSONEditable {
    private Integer id;

    private String externalId;

    private String name;

    private String description;

    private String ownerId;

    private String adminGroupId;

    private String membersGroupId;

    private String dataFolderId;

    private String workflowFolderId;

    private Long remoteScannerId;

    private boolean privateProject = false;    

    private final HashMap<String,String> additionalProperties = new HashMap<String, String>();
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getAdminGroupId() {
        return adminGroupId;
    }

    public void setAdminGroupId(String adminGroupId) {
        this.adminGroupId = adminGroupId;
    }

    public String getMembersGroupId() {
        return membersGroupId;
    }

    public void setMembersGroupId(String membersGroupId) {
        this.membersGroupId = membersGroupId;
    }

    public String getDataFolderId() {
        return dataFolderId;
    }

    public void setDataFolderId(String dataFolderId) {
        this.dataFolderId = dataFolderId;
    }

    public String getWorkflowFolderId() {
        return workflowFolderId;
    }

    public void setWorkflowFolderId(String workflowFolderId) {
        this.workflowFolderId = workflowFolderId;
    }

    public Long getRemoteScannerId() {
        return remoteScannerId;
    }

    public void setRemoteScannerId(Long remoteScannerId) {
        this.remoteScannerId = remoteScannerId;
    }

    public boolean isPrivateProject() {
        return privateProject;
    }

    public void setPrivateProject(boolean privateProject) {
        this.privateProject = privateProject;
    }
    
    public HashMap<String, String> getAdditionalProperties(){
        return additionalProperties;
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("Project");
        store.add("ID", id);
        store.add("Description", description);
        store.add("Name", name);
        store.add("AdminGroupID", adminGroupId);
        store.add("DataFolderID", dataFolderId);
        store.add("ExternalID", externalId);
        store.add("MembersGroupID", membersGroupId);
        store.add("OwnerID", ownerId);
        store.add("PrivateProject", privateProject);
        store.add("RemoteScannerID", remoteScannerId);
        store.add("WorkflowFolderID", workflowFolderId);
        
        store.add("AdditionalPropertyCount", additionalProperties.size());
        int count = 0;
        for(String key : additionalProperties.keySet()){
            store.add("Property" + count + "Name", key);
            store.add("Property" + count + "Value", additionalProperties.get(key));
            count++;
        }
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        id = store.intValue("ID", 0);
        description = store.stringValue("Description", "");
        name = store.stringValue("Name", "");
        adminGroupId = store.stringValue("AdminGroupID", null);
        dataFolderId = store.stringValue("DataFolderID", null);
        externalId = store.stringValue("ExternalID", null);
        membersGroupId = store.stringValue("MembersGroupID", null);
        ownerId = store.stringValue("OwnerID", null);
        remoteScannerId = store.longValue("RemoteScannerID", -1);
        workflowFolderId = store.stringValue("WorkflowFolderID", null);
        
        additionalProperties.clear();
        int count = store.intValue("AdditionalPropertyCount", 0);
        String key, value;
        for(int i=0;i<count;i++){
            key = store.stringValue("Property" + i + "Name", null);
            value = store.stringValue("Property" + i + "Value", null);
            if(key!=null && value!=null){
                additionalProperties.put(key, value);
            }
        }
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = new JSONObject();
        json.put("id", id);
        if(description!=null){
            json.put("description", description);
        } else {
            json.put("description", "");
        }
        json.put("name", name);
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        if(json.has("id")){
            id = json.getInt("id");
        }
        
        if(json.has("description")){
            description = json.getString("description");
        } else {
            description = "";
        }
        
        if(json.has("name")){
            name = json.getString("name");
        } else {
            name = "";
        }
    }
}