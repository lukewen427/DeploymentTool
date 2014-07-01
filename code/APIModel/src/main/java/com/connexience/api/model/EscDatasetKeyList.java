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
 * This class holds a list of String keys for a query
 * @author hugo
 */
@XmlType
public class EscDatasetKeyList implements JsonSerializable {
    /** List of keys */
    private String[] keys;

    public EscDatasetKeyList() {
    }

    public EscDatasetKeyList(JSONObject json) {
        parseJsonObject(json);
    }
    
    public void addKey(String key){
        if(keys==null){
            keys = new String[1];
            keys[0] = key;
        } else {
            String[] newValues = new String[keys.length + 1];
            for(int i=0;i<keys.length;i++){
                newValues[i] = keys[i];
            }
            newValues[newValues.length - 1] = key;
            keys = newValues;
        }
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }
        
    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        JSONArray keysJson = new JSONArray();
        for(int i=0;i<keys.length;i++){
            keysJson.put(keys[i]);
        }
        json.put("keys", keys);
        return json;
    }

    @Override
    public final void parseJsonObject(JSONObject json) {
        JSONArray keysJson = json.getJSONArray("keys");
        keys = new String[keysJson.length()];
        for(int i=0;i<keysJson.length();i++){
            keys[i] = keysJson.getString(i);
        }
    }    
}
