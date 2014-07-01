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

import com.connexience.api.model.json.JSONArray;
import com.connexience.api.model.json.JSONObject;
import com.connexience.api.model.json.JsonSerializable;

import javax.xml.bind.annotation.XmlType;

/**
 * This class contains a list of workflow parameters
 * @author hugo
 */
@XmlType
public class EscWorkflowParameterList implements JsonSerializable {
    private EscWorkflowParameter[] values;

    public EscWorkflowParameterList() {
    }

    public EscWorkflowParameterList(JSONObject json) {
        parseJsonObject(json);
    }
    
    public EscWorkflowParameter[] getValues() {
        return values;
    }

    public void addParameter(String blockName, String parameterName, String parameterValue){
        if(values==null){
            values = new EscWorkflowParameter[1];
            values[0] = new EscWorkflowParameter(blockName, parameterName, parameterValue);
        } else {
            EscWorkflowParameter[] newValues = new EscWorkflowParameter[values.length + 1];
            for(int i=0;i<values.length;i++){
                newValues[i] = values[i];
            }
            newValues[newValues.length - 1] = new EscWorkflowParameter(blockName, parameterName, parameterValue);
            values = newValues;
        }
    }
    
    public void setValues(EscWorkflowParameter[] values) {
        this.values = values;
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        JSONArray valuesJson = new JSONArray();
        for(int i=0;i<values.length;i++){
            valuesJson.put(values[i].toJsonObject());
        }
        json.put("values", values);
        return json;
    }

    @Override
    public final void parseJsonObject(JSONObject json) {
        JSONArray valuesJson = json.getJSONArray("values");
        values = new EscWorkflowParameter[valuesJson.length()];
        for(int i=0;i<valuesJson.length();i++){
            values[i] = new EscWorkflowParameter(valuesJson.getJSONObject(i));
        }
    }
}