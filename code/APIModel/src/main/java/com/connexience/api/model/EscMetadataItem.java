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

/**
 * This class defines a single piece of metadata
 * @author hugo
 */
public class EscMetadataItem implements JsonSerializable {
    public enum METADATA_TYPE {
        BOOLEAN,DATE,NUMERICAL,TEXT
    }
    
    private METADATA_TYPE metadataType = METADATA_TYPE.TEXT;
    private String name;
    private String category;
    private String stringValue;
    private String objectId;
    private long id;
    
    public EscMetadataItem() {
    }

    public EscMetadataItem(JSONObject json) {
        parseJsonObject(json);
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    
    public METADATA_TYPE getMetadataType() {
        return metadataType;
    }

    public void setMetadataType(METADATA_TYPE metadataType) {
        this.metadataType = metadataType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("category", category);
        json.put("stringValue", stringValue);
        json.put("objectId", objectId);
        json.put("id", id);
        switch(metadataType){
            case BOOLEAN:
                json.put("type", "boolean");
                break;
                
            case DATE:
                json.put("type", "date");
                break;
                
            case NUMERICAL:
                json.put("type", "numerical");
                break;
                
            case TEXT:
                json.put("type", "text");
                break;
                
            default:
                json.put("type", "text");
                
        }
        return json;
    }

    @Override
    public void parseJsonObject(JSONObject json) {
        this.name = json.getString("name", null);
        this.category = json.getString("category", null);
        this.stringValue = json.getString("stringValue", null);
        String typeString = json.getString("metadataType", null);
        id = json.getLong("id", 0);
        objectId = json.getString("objectId", null);
        if("text".equalsIgnoreCase(typeString)){
            metadataType = METADATA_TYPE.TEXT;
            
        } else if("boolean".equalsIgnoreCase(typeString)){
            metadataType = METADATA_TYPE.BOOLEAN;
            
        } else if("date".equalsIgnoreCase(typeString)){
            metadataType = METADATA_TYPE.DATE;
            
        } else if("numerical".equalsIgnoreCase("typeString")){
            metadataType = METADATA_TYPE.NUMERICAL;
            
        } else {
            metadataType = METADATA_TYPE.TEXT;
        }
    }
}