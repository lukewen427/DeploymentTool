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
 * This class represents a single workflow parameter
 * @author hugo
 */
@XmlType
public class EscWorkflowParameter implements JsonSerializable {
    private String blockName;
    private String name;
    private String value;

    public EscWorkflowParameter(String blockName, String name, String value) {
        this.blockName = blockName;
        this.name = name;
        this.value = value;
    }

    public EscWorkflowParameter() {
    }
    
    public EscWorkflowParameter(JSONObject json) {
        parseJsonObject(json);
    }
    
    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public JSONObject toJsonObject() {
        return new JSONObject(this);
    }

    @Override
    public final void parseJsonObject(JSONObject json) {
        blockName = json.getString("blockName", null);
        name = json.getString("name", null);
        value = json.getString("value", null);
    }
}