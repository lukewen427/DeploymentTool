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

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.datasets.Dataset;
import com.connexience.server.model.datasets.DatasetQuery;
import com.connexience.server.model.datasets.items.MultipleValueItem;
import com.connexience.server.model.datasets.items.multiple.JsonMultipleValueItem;
import com.connexience.server.model.datasets.items.multiple.store.JsonDataRow;
import com.connexience.server.util.JSONContainer;
import java.util.Date;
import java.util.Iterator;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/** 
 * Utility methods for accessing dashboard data
 * @author hugo
 */
public class DatasetsUtils {
    /** Get the size of a multiple value data item */
    public static int getMultipleValueDataSize(Session session, MultipleValueItem item) throws ConnexienceException {
        if(item instanceof JsonMultipleValueItem){
            return getJsonMultipleValueDataSize(session, (JsonMultipleValueItem)item);
        } else {
            throw new ConnexienceException("Unsupported multiple value type");
        }
    }
    
    /** Get a filtered subset of data from a multiple value item */
    public static Object getMultipleValueData(Session session, MultipleValueItem item, int startIndex, int maxResults, String[] keys) throws ConnexienceException {
        if(item instanceof JsonMultipleValueItem){
            return getJsonMultipleValueData(session, (JsonMultipleValueItem)item, startIndex, maxResults, keys);
        } else {
            throw new ConnexienceException("Unsupported multiple value type");
        }          
    }
    
    /** Get a subset of the data from a multiple value item */
    public static Object getMultipleValueData(Session session, MultipleValueItem item, int startIndex, int maxResults) throws ConnexienceException {
        if(item instanceof JsonMultipleValueItem){
            return getJsonMultipleValueData(session, (JsonMultipleValueItem)item, startIndex, maxResults, null);
        } else {
            throw new ConnexienceException("Unsupported multiple value type");
        }        
    }
    
    /** Get a multiple value row */
    public static Object getMultipleValueDataRow(Session session, MultipleValueItem item, long rowIndex) throws ConnexienceException {
        if(item instanceof JsonMultipleValueItem){
            return getJsonMultipleValueDataRow(session, (JsonMultipleValueItem)item, rowIndex);
        } else {
            throw new ConnexienceException("Unsupported multiple value type");
        }          
    }
    
    /** Get all the data from a multiple value item */
    public static Object getMultipleValueData(Session session, MultipleValueItem item) throws ConnexienceException {
        if(item instanceof JsonMultipleValueItem){
            return getJsonMultipleValueData(session, (JsonMultipleValueItem)item, -1, -1, null);
        } else {
            throw new ConnexienceException("Unsupported multiple value type");
        }
    }
    
    /** Append data to a multiple value item */
    public static void appendMultipleValueData(Session session, MultipleValueItem item, Object data) throws ConnexienceException {
      if(item instanceof JsonMultipleValueItem){
            appendJsonMultipleValueData(session, (JsonMultipleValueItem)item, data);
        } else {
            throw new ConnexienceException("Unsupported multiple value type");
        }
    }
    
    /** Update a multiple value item */
    public static void updateMultipleValueData(Session session, MultipleValueItem item, long rowId, Object data) throws ConnexienceException {
      if(item instanceof JsonMultipleValueItem){
            updateMultipleValueData(session, (JsonMultipleValueItem)item, rowId, data);
        } else {
            throw new ConnexienceException("Unsupported multiple value type");
        }        
    }
    
    /** Remove a row from a multiple value item */
    public static void removeMultipleValueDataRow(Session session, MultipleValueItem item, long rowId) throws ConnexienceException {
      if(item instanceof JsonMultipleValueItem){
            removeMultipleValueDataRow(session, (JsonMultipleValueItem)item, rowId);
        } else {
            throw new ConnexienceException("Unsupported multiple value type");
        }           
    }
    
    
    /** Remove all values for a multiple value data item */
    public static void removeMultipleValueData(Session session, MultipleValueItem item) throws ConnexienceException {
        if(item instanceof JsonMultipleValueItem){
            removeJsonMultipleValueData(session, (JsonMultipleValueItem)item);
        } else {
            throw new ConnexienceException("Unsupported multiple value type");
        }
    }
    
    /** Remove all multiple value data items from a dataset */
    public static void removeAllMultipleValueData(Session session, Dataset ds) throws ConnexienceException {
        // Get all of the data items
        Query q = session.createQuery("from MultipleValueItem as mv where mv.datasetId=:datasetid");
        q.setString("datasetid", ds.getId());
        List results = q.list();
        
        // Remove all of the associated data
        MultipleValueItem item;
        for(int i=0;i<results.size();i++){
            item = (MultipleValueItem)results.get(i);
            removeMultipleValueData(session, item);
        }
    }
    
    /** Get the size of a multiple json value item */
    public static int getJsonMultipleValueDataSize(Session session, JsonMultipleValueItem item) throws ConnexienceException {
        SQLQuery q = session.createSQLQuery("select count(id) from datasetjson where itemid=:itemid");
        q.setLong("itemid", item.getId());
        //q.addEntity(Long.class);
        return ((Number)q.uniqueResult()).intValue();
    }
    
    /** Remove all values for a JSON multiple value data item */
    private static void removeJsonMultipleValueData(Session session, JsonMultipleValueItem item) throws ConnexienceException {
        Query q = session.createSQLQuery("delete from datasetjson where itemid=:itemid");
        q.setLong("itemid", item.getId());
        q.executeUpdate();
    }
    
    private static void removeMultipleValueDataRow(Session session, JsonMultipleValueItem item, long rowId) throws ConnexienceException {
        Query q = session.createSQLQuery("delete from datasetjson where id=:rowid");
        q.setLong("rowid", rowId);
        q.executeUpdate();
    }
    
    /** Update some JSON data in a JsonMultipleValueData object */
    private static void updateMultipleValueData(Session session, JsonMultipleValueItem item, long rowId, Object data) throws ConnexienceException {
        Query q = session.createQuery("from JsonDataRow as obj where obj.id=:rowId and obj.itemId=:itemId");
        q.setLong("rowId", rowId);
        q.setLong("itemId", item.getId());
        List results = q.list();
        if(results.size()==1){
            JsonDataRow row = (JsonDataRow)results.get(0);
            
            if(data instanceof JSONObject){
                // JSON object has been sent
                try {
                    JSONObject js = (JSONObject)data;
                    JSONObject updateJson = new JSONObject();
                    Iterator keys = js.keys();
                    String key;
                    while(keys.hasNext()){
                        key = (String)keys.next().toString();
                        if(!key.startsWith("_")){
                            updateJson.put(key, js.get(key));
                        }
                    }
                    row.setStringValue(updateJson.toString());
                    
                } catch (Exception e){
                    throw new ConnexienceException("Error creating JSON data row for Json data: " + e.getMessage(), e);
                }

            } else if(data instanceof String){
                // Plain text sent
                try {
                    JSONObject js = new JSONObject((String)data);
                    JSONObject updateJson = new JSONObject();
                    Iterator keys = js.keys();
                    String key;
                    while(keys.hasNext()){
                        key = (String)keys.next().toString();
                        if(!key.startsWith("_")){
                            updateJson.put(key, js.get(key));
                        }
                    }
                    row.setStringValue(updateJson.toString());                    

                } catch (Exception e){
                    throw new ConnexienceException("Error creating JSON data row for String data: " + e.getMessage(), e);
                }

            } else if(data instanceof JSONContainer){
                // JSONContainer data
                try {
                    JSONObject js = new JSONObject(((JSONContainer)data).getStringData());
                    JSONObject updateJson = new JSONObject();
                    Iterator keys = js.keys();
                    String key;
                    while(keys.hasNext()){
                        key = (String)keys.next().toString();
                        if(!key.startsWith("_")){
                            updateJson.put(key, js.get(key));
                        }
                    }
                    row.setStringValue(updateJson.toString());                    

                } catch (Exception e){
                    throw new ConnexienceException("Error creating JSON data row for JsonContainer data: " + e.getMessage(), e);
                }
            }
            
            if(row!=null){
                row.setCollectionTime(new Date());
                session.update(row);
            } else {
                throw new ConnexienceException("Error saving JSON data row: Unsupported data");
            }        
        } else {
            throw new ConnexienceException("No such row: " + rowId + " for item: " + item.getName());
        }
    }
    
    /** Append some JSON data to a JsonMultipleValueData object */
    private static void appendJsonMultipleValueData(Session session, JsonMultipleValueItem item, Object data) throws ConnexienceException {
        JsonDataRow row = null;
        if(data instanceof JSONObject){
            // JSON object has been sent
            try {
                row = new JsonDataRow(item, (JSONObject)data);
            } catch (Exception e){
                throw new ConnexienceException("Error creating JSON data row for Json data: " + e.getMessage(), e);
            }
            
        } else if(data instanceof String){
            // Plain text sent
            row = new JsonDataRow(item, (String)data);
            
        } else if(data instanceof JSONContainer){
            // JSONContainer data
            row = new JsonDataRow(item, ((JSONContainer)data).getStringData());
            
        }
        if(row!=null){
            session.save(row);
        } else {
            throw new ConnexienceException("Error saving JSON data row: Unsupported data");
        }
    }
    
    /** Get values for a Json multiple value object */
    public static JSONContainer getJsonMultipleValueData(Session session, JsonMultipleValueItem item, int startIndex, int maxResults, String[] filterKeys) throws ConnexienceException {
        try {
            Query q = session.createQuery("from JsonDataRow as obj where obj.itemId=:itemid order by obj.id asc");
            q.setLong("itemid", item.getId());
            
            if(startIndex>=0){
                q.setFirstResult(startIndex);
            } 
            
            if(maxResults>=0){
                q.setMaxResults(maxResults);
            }
            
            List rows = q.list();
            return createResultFromList(rows, filterKeys);
        } catch (Exception ex){
            throw new ConnexienceException("Error getting JSON values: " + ex.getMessage());
        }
    }
    
    public static JSONContainer getJsonMultipleValueDataRow(Session session, JsonMultipleValueItem item, long rowId) throws ConnexienceException {
        try {
            Query q = session.createQuery("from JsonDataRow as obj where obj.id=:rowid");
            q.setLong("rowid", rowId);
            return createResultFromList(q.list(), null);
        } catch (Exception e){
            throw new ConnexienceException("Error getting JSON row: " + e.getMessage(), e);
        }
    }
    
    public static JSONContainer createResultFromList(List rows, String[] filterKeys) throws Exception {
        String jsonText;
        JSONObject json;
        JSONObject filteredJson;
        JSONArray errors = new JSONArray();
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();

        for(Object o : rows){
            jsonText = ((JsonDataRow)o).getStringValue();
            try {
                if(filterKeys==null){
                    json = new JSONObject(jsonText);
                    json.put("_id", ((JsonDataRow)o).getId());  // Database ID
                    json.put("_timestamp", ((JsonDataRow)o).getCollectionTime());
                    json.put("_timeInMillis", ((JsonDataRow)o).getCollectionTime().getTime());
                    data.put(json);
                } else {
                    json = new JSONObject(jsonText);
                    filteredJson = new JSONObject();
                    for(String k : filterKeys){
                        if(json.has(k)){
                            filteredJson.put(k, json.get(k));
                        } else {
                            filteredJson.put(k, JSONObject.NULL);
                        }
                    }

                    filteredJson.put("_id", ((JsonDataRow)o).getId());  // Database ID
                    filteredJson.put("_timestamp", ((JsonDataRow)o).getCollectionTime());
                    filteredJson.put("_timeInMillis", ((JsonDataRow)o).getCollectionTime().getTime());
                    data.put(filteredJson);
                }
            } catch (Exception e){
                // TODO: Add the error to the json data
                errors.put("Error parsing json: " + e.getMessage());
            }
        }

        if(errors.length()>0){
            result.put("_errors", errors);
        }

        result.put("data", data);
        return new JSONContainer(result);
        
    }

    public static JSONObject filterJson(JSONObject json, String[] filterKeys) throws Exception {
        
        if(filterKeys!=null){
            JSONObject result = new JSONObject();
            Object value;
            
            // Filter for keys
            for(String key : filterKeys){
                if(json.has(key)){
                    value = json.get(key);
                    if(value instanceof JSONObject){
                        result.put(key, copyJsonObject((JSONObject)value));
                    } else if(value instanceof JSONArray){
                        result.put(key, copyJsonArray((JSONArray)value));
                    } else {
                        result.put(key, value);
                    }
                }
            }
            
            // Now add all extra data. i.e. stuff that starts with an '_'
            Iterator i = json.keys();
            String key;
            
            while(i.hasNext()){
                key = i.next().toString();
                if(key.startsWith("_")){
                    value = json.get(key);
                    if(value instanceof JSONObject){
                        result.put(key, copyJsonObject((JSONObject)value));
                    } else if(value instanceof JSONArray){
                        result.put(key, copyJsonArray((JSONArray)value));
                    } else {
                        result.put(key, value);
                    }                    
                }
            }
        
            return result;
        } else {
            // Copy exact values
            return json;
        }
    }
    
    private static JSONArray copyJsonArray(JSONArray json) throws Exception {
        String jsonString = json.toString();
        JSONArray result = new JSONArray(jsonString);
        return result;
    }
    
    private static JSONObject copyJsonObject(JSONObject json) throws Exception {
        String jsonString = json.toString();
        JSONObject result = new JSONObject(jsonString);
        return result;
    }
}