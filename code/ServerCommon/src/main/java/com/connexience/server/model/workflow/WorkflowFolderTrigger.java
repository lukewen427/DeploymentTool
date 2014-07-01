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
package com.connexience.server.model.workflow;

import com.connexience.server.util.JSONEditable;
import com.connexience.server.util.WildcardUtils;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * This class defines a trigger for running a workflow whenever a document is uploaded
 * to a folder. 
 * @author hugo
 */
public class WorkflowFolderTrigger implements Serializable, JSONEditable {
    static final long serialVersionUID = 1701083428027625450L;
    
    /** Database id */
    private long id;
    
    /** Target folder ID */
    private String folderId;
    
    /** Is this trigger enabled */
    private boolean enabled = true;

    /** Operate on subdirectories */
    private boolean subdirectoryTrigger = false;
    
    /** Workflow to execute */
    private String workflowId;
    
    /** User that owns this trigger */
    private String ownerId;
    
    /** Should the file name be passed through a RegEx to check it matches a pattern */
    private boolean nameRegExChecked = false;
    
    /** RegEx to use to check the name */
    private String nameRegEx = "";

    /** Comments for this trigger */
    private String comments = "";

    /** Is this trigger enabled */
    public boolean isEnabled() {
        return enabled;
    }

    /** Set whether this trigger is enabled */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNameRegEx() {
        return nameRegEx;
    }

    public void setNameRegEx(String nameRegEx) {
        this.nameRegEx = nameRegEx;
    }

    public boolean isNameRegExChecked() {
        return nameRegExChecked;
    }

    public void setNameRegExChecked(boolean nameRegExChecked) {
        this.nameRegExChecked = nameRegExChecked;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isSubdirectoryTrigger() {
        return subdirectoryTrigger;
    }

    public void setSubdirectoryTrigger(boolean subdirectoryTrigger) {
        this.subdirectoryTrigger = subdirectoryTrigger;
    }
    
    public boolean nameMatches(String name){
        if(nameRegExChecked){
            return WildcardUtils.wildCardMatch(name, nameRegEx);
        } else {
            return true;
        }
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = new JSONObject();
        json.put("Enabled", enabled);
        json.put("CheckSubdirectories", subdirectoryTrigger);
        json.put("UseNameWildcard", nameRegExChecked);
        json.put("NamePattern", nameRegEx);
        json.put("Comments", comments);
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        enabled = json.getBoolean("Enabled");
        subdirectoryTrigger = json.getBoolean("CheckSubdirectories");
        nameRegExChecked = json.getBoolean("UseNameWildcard");
        nameRegEx = json.getString("NamePattern");
        comments = json.getString("Comments");
    }
}