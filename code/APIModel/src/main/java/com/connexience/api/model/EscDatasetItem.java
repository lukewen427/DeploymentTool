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
 * This class contains a simplified representation of an item that is contained
 * within a Dataset.
 * @author hugo
 */
@XmlType
public class EscDatasetItem implements JsonSerializable {
    /** The basic format of the item */
    public enum DATASET_ITEM_TYPE {
        MULTI_ROW, SINGLE_ROW
    }
    
    /** The strategy that will be applied to updates */
    public enum DATASET_ITEM_UPDATE_STRATEGY {
        REPLACE, MINIMUM, MAXIMUM, AVERAGE, SUM
    }
    
    private long id;
    private String name;
    private String datasetId;
    private DATASET_ITEM_TYPE itemType = DATASET_ITEM_TYPE.MULTI_ROW;
    private DATASET_ITEM_UPDATE_STRATEGY updateStrategy = DATASET_ITEM_UPDATE_STRATEGY.REPLACE;

    public EscDatasetItem() {
    }

    public EscDatasetItem(JSONObject json){
        parseJsonObject(json);
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItemType(DATASET_ITEM_TYPE itemType) {
        this.itemType = itemType;
    }

    public DATASET_ITEM_TYPE getItemType() {
        return itemType;
    }

    public void setUpdateStrategy(DATASET_ITEM_UPDATE_STRATEGY updateStrategy) {
        this.updateStrategy = updateStrategy;
    }

    public DATASET_ITEM_UPDATE_STRATEGY getUpdateStrategy() {
        return updateStrategy;
    }

    @Override
    public final void parseJsonObject(JSONObject json) {
        id = json.getLong("id", 0);
        name = json.getString("name", null);
        datasetId = json.getString("datasetId", null);
        String typeString = json.getString("itemType", null);
        if("multirow".equals(typeString) || "MULTI_ROW".equals(typeString)){
            itemType = DATASET_ITEM_TYPE.MULTI_ROW;

        } else if("singlerow".equals(typeString) || "SINGLE_ROW".equals(typeString)){
            itemType = DATASET_ITEM_TYPE.SINGLE_ROW;
            
        } else {
            itemType = DATASET_ITEM_TYPE.MULTI_ROW;
        }
        
        String updateString = json.getString("updateStrategy", null);
        if("average".equals(updateString)){
            updateStrategy = DATASET_ITEM_UPDATE_STRATEGY.AVERAGE;
            
        } else if("maximum".equals(updateString)){
            updateStrategy = DATASET_ITEM_UPDATE_STRATEGY.MAXIMUM;
                    
        } else if("minimum".equals(updateString)){
            updateStrategy = DATASET_ITEM_UPDATE_STRATEGY.MINIMUM;
            
        } else if("replace".equals(updateString)){
            updateStrategy = DATASET_ITEM_UPDATE_STRATEGY.REPLACE;
            
        } else if("sum".equals(updateString)){
            updateStrategy = DATASET_ITEM_UPDATE_STRATEGY.SUM;
            
        } else {
            updateStrategy = DATASET_ITEM_UPDATE_STRATEGY.REPLACE;
        }       
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("datasetId", datasetId);
        
        if(itemType==DATASET_ITEM_TYPE.MULTI_ROW){
            json.put("itemType", "MULTI_ROW");
            
        } else if(itemType==DATASET_ITEM_TYPE.SINGLE_ROW){
            json.put("itemType", "SINGLE_ROW");
            
        } else {
            json.put("itemType", "MULTI_ROW");
        }
        
        if(updateStrategy==DATASET_ITEM_UPDATE_STRATEGY.AVERAGE){
            json.put("updateStrategy","AVERAGE");
            
        } else if(updateStrategy==DATASET_ITEM_UPDATE_STRATEGY.MAXIMUM){
            json.put("updateStrategy","MAXIMUM");
            
        } else if(updateStrategy==DATASET_ITEM_UPDATE_STRATEGY.MINIMUM){
            json.put("updateStrategy","MINIMUM");
            
        } else if(updateStrategy==DATASET_ITEM_UPDATE_STRATEGY.SUM){
            json.put("updateStrategy", "SUM");
            
        } else if(updateStrategy==DATASET_ITEM_UPDATE_STRATEGY.REPLACE){
            json.put("updateStrategy","REPLACE");
            
        } else {
            json.put("updateStrategy","REPLACE");
        }  
        
        return json;
    }
}