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
 * This object contains a report of the termination status when a workflow is killed.
 * When a set of related workflows are killed this object contains a tree structure
 * with details of whether or not the various workflows in the chain were killed
 * correctly. If not, then the user can take action and attempt to re-kill
 * the failed steps or delete the invocations.
 * @author hugo
 */
public class WorkflowTerminationReport implements Serializable {
    static final long serialVersionUID = -5437816691004555473L;
    
    private WorkflowTerminationItem topItem;
    
    public WorkflowTerminationReport() {
        topItem = new WorkflowTerminationItem();
    }

    public WorkflowTerminationReport(String id, String name) {
        WorkflowTerminationItem item = new WorkflowTerminationItem();
        item.setId(id);
        item.setName(name);
        this.topItem = item;
    }

    public WorkflowTerminationItem getTopItem() {
        return topItem;
    }

    public void setTopItem(WorkflowTerminationItem topItem) {
        this.topItem = topItem;
    }

    public List<WorkflowTerminationItem> getFlatList(){
        ArrayList<WorkflowTerminationItem> list = new ArrayList<>();
        topItem.addToFlatList(list);
        return list;
    }
    
    public boolean allTerminatedOk(){
        List<WorkflowTerminationItem> list = getFlatList();
        boolean terminatedOk = true;
        for(WorkflowTerminationItem i : list){
            if(!i.isTerminatedOk()){
                terminatedOk = false;
            }
        }
        return terminatedOk;
    }
}