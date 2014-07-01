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
 * Simple workflow invocation record for soap web service
 * @author hugo
 */
@XmlType
public class EscWorkflowInvocation extends EscObject implements JsonSerializable {
    private String status;
    private String workflowId;
    private String workflowVersionId;
    private long startTimestamp;
    private long endTimestamp;
    private int percentComplete;
    private String workflowName;
    
    public EscWorkflowInvocation() {
    }

    public EscWorkflowInvocation(JSONObject json) {
        parseJsonObject(json);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkflowVersionId() {
        return workflowVersionId;
    }

    public void setWorkflowVersionId(String workflowVersionId) {
        this.workflowVersionId = workflowVersionId;
    }

    @Override
    public JSONObject toJsonObject() {
        return new JSONObject(this);
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public int getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(int percentComplete) {
        this.percentComplete = percentComplete;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }
    
    @Override
    public final void parseJsonObject(JSONObject json) {
        super.parseJsonObject(json);
        status = json.getString("status", null);
        workflowId = json.getString("workflowId", null);
        workflowVersionId = json.getString("workflowVersionId", null);
        startTimestamp = json.getLong("startTimestamp", 0);
        endTimestamp = json.getLong("endTimestamp", 0);
        percentComplete = json.getInt("percentComplete", 0);
        workflowName = json.getString("workflowName", "");
    }
    
    @Override
    public String getObjectType() {
        return getClass().getSimpleName();
    }    
}