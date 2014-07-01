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
package com.connexience.server.model.datasets;


import com.connexience.server.util.JSONEditable;
import java.io.Serializable;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class defines a simple data set query that can be edited on the website
 * and set as a workflow block parameter.
 * @author hugo
 */
public class DatasetQuery implements Serializable, XmlStorable, JSONEditable, DatasetConstants {
    /** ID of the dataset */
    private String datasetId;
    
    /** Name of the item */
    private String itemName;
    
    /** List of values to return */
    private ArrayList<String> keys = new ArrayList<>();
    
    /** Supported dataset item class */
    protected Class supportedClass;
    
    /** Class that performs the query */
    protected Class enactorClass;
    
    /** Label that gets displayed in the UI */
    protected String label;
    
    public void addKey(String key){
        keys.add(key);
    }
    
    public ArrayList<String> getKeys(){
        return keys;
    }
    
    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Class getSupportedClass() {
        return supportedClass;
    }

    public Class getEnactorClass() {
        return enactorClass;
    }
    
    public boolean isItemSupported(DatasetItem item){
        if(supportedClass.isAssignableFrom(item.getClass())){
            return true;
        } else {
            return false;
        }
        /*
        if(item.getClass().equals(supportedClass)){
            return true;
        } else {
            return false;
        }
        */
    }

    public String[] getKeyArray(){
        if(keys.size()>0){
            String[] results = new String[keys.size()];
            keys.toArray(results);
            return results;
        } else {
            return null;
        }
    }
    
    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        datasetId = store.stringValue("DatasetID", null);
        itemName = store.stringValue("ItemName", null);
        keys.clear();
        int count = store.intValue("KeyCount", 0);
        for(int i=0;i<count;i++){
            keys.add(store.stringValue("Key" + i, null));
        }
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("DataSetQuery");
        store.add("DatasetID", datasetId);
        store.add("ItemName", itemName);
        store.add("KeyCount", keys.size());
        for(int i=0;i<keys.size();i++){
            store.add("Key" + i, keys.get(i));
        }
        return store;
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = new JSONObject();
        JSONObject hidden = new JSONObject();
        hidden.put("datasetId", datasetId);
        hidden.put("itemName", itemName);
        JSONArray keysJson = new JSONArray();
        for(int i=0;i<keys.size();i++){
            keysJson.put(keys.get(i));
        }
        hidden.put("keys", keysJson);
        json.put("_hidden", hidden);
        json.put("_className", getClass().getName());
        json.put("_label", label);
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        if(json.has("_hidden")){
            JSONObject hidden = json.getJSONObject("_hidden");
            if(hidden.has("datasetId")){
                datasetId = hidden.getString("datasetId");
            } else {
                datasetId = null;
            }

            if(hidden.has("itemName")){
                itemName = hidden.getString("itemName");
            } else {
                itemName = null;
            }

            keys.clear();
            if(hidden.has("keys")){
                JSONArray keysJson = hidden.getJSONArray("keys");
                for(int i=0;i<keysJson.length();i++){
                    keys.add(keysJson.getString(i));
                }
            }
        }
    }
}