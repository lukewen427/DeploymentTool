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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains details about the termination of a workflow
 * @author hugo
 */
public class WorkflowTerminationItem implements Serializable {
    static final long serialVersionUID = -6835779057021353483L;
    
    private String id;
    private String name;
    private boolean terminatedOk = false;
    private String message = "";
    private boolean subWorkflowsProcessingError = false;
    private String subWorkflowProcessingMessage = "";
    private ArrayList<WorkflowTerminationItem> children = new ArrayList<>();

    public WorkflowTerminationItem(String id, String name, boolean terminatedOk, String message) {
        this.id = id;
        this.name = name;
        this.terminatedOk = terminatedOk;
        this.message = message;
    }

    public WorkflowTerminationItem() {
    }


    public WorkflowTerminationItem createChild(){
        WorkflowTerminationItem child = new WorkflowTerminationItem();
        children.add(child);
        return child;
    }        

    public void addChild(WorkflowTerminationItem child){
        children.add(child);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<WorkflowTerminationItem> getChildren() {
        return children;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTerminatedOk() {
        return terminatedOk;
    }

    public void setTerminatedOk(boolean terminatedOk) {
        this.terminatedOk = terminatedOk;
    }

    public String getSubWorkflowProcessingMessage() {
        return subWorkflowProcessingMessage;
    }

    public boolean isSubWorkflowsProcessingError() {
        return subWorkflowsProcessingError;
    }

    public void setSubWorkflowProcessingMessage(String subWorkflowProcessingMessage) {
        this.subWorkflowProcessingMessage = subWorkflowProcessingMessage;
    }

    public void setSubWorkflowsProcessingError(boolean subWorkflowsProcessingError) {
        this.subWorkflowsProcessingError = subWorkflowsProcessingError;
    }
    
    public void addToFlatList(List<WorkflowTerminationItem> list) {
        list.add(this);
        for(WorkflowTerminationItem item : children){
            item.addToFlatList(list);
        }
    }
}
