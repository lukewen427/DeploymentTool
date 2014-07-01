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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents a list of parameters that can be sent to a workflow
 * @author hugo
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class WorkflowParameterList implements Serializable {
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


    private ArrayList<WorkflowParameter> parameters = new ArrayList<>();
    public void addParameter(WorkflowParameter parameter) {
        parameters.add(parameter);
    }

    public void add(WorkflowParameter parameter) {
        parameters.add(parameter);
    }

    public ArrayList<WorkflowParameter> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<WorkflowParameter> parameters) {
        this.parameters = parameters;
    }

    @JsonIgnore
    public WorkflowParameter getParameter(int index) {
        return parameters.get(index);
    }

    /** Automatically add a parameter */
    public void add(String blockName, String parameterName, String parameterValue) {
        WorkflowParameter parameter = new WorkflowParameter();
        parameter.setBlockName(blockName);
        parameter.setName(parameterName);
        parameter.setValue(parameterValue);
        add(parameter);
    }

    /** Automatically add a parameter */
    public void add(String parameterId, String parameterValue){
        WorkflowParameter parameter = new WorkflowParameter();
        int index = parameterId.indexOf(".");
        if(index!=-1){
            parameter.setBlockName(parameterId.substring(0, index));
            parameter.setName(parameterId.substring(index+1, parameterId.length()));
        } else {
            parameter.setBlockName("InvalidParameterID");
            parameter.setName("InvalidParameterID");

        }
        parameter.setValue(parameterValue);
        add(parameter);
    }
    
    /** Get the size of this list */
    public int size(){
        return parameters.size();
    }
}
