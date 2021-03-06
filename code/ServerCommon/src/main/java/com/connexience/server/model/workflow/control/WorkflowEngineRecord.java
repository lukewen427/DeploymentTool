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
package com.connexience.server.model.workflow.control;

import java.io.Serializable;

/**
 * This class represents a workflow engine that has been seen at least once by
 * the system. It can either point to a direct IP address or to a workflow engine
 * ID if it is located within the sevice host
 * @author hugo
 */
public class WorkflowEngineRecord implements Serializable {
    /**
     * Class version UID.
     * 
     * Please increment this value whenever your changes may cause 
     * incompatibility with the previous version of this class. If unsure, ask 
     * one of the core development team or read:
     *   http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     *   http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 1L;


    /** Database ID of the engine */
    private long id;
    
    /** ID of the engine */
    private String engineId;
    
    public WorkflowEngineRecord() {
    }

    public void setEngineId(String engineId) {
        this.engineId = engineId;
    }

    public String getEngineId() {
        return engineId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public boolean isStandalone(){
        if(engineId!=null){
            if(engineId.startsWith("IP:")){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    
    public String getIPAddress(){
        // Engine is running standalone
        int index = engineId.indexOf(":");
        if(index!=-1){
            return engineId.substring(index + 1);
        } else {
            return "UnknownIP";
        }        
    }
}
