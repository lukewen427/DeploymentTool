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
package com.connexience.server.model.metadata;

import com.connexience.server.util.JSONEditable;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class holds a list of metadata query items that are used to build a full query
 * @author hugo
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class MetadataQuery implements Serializable, JSONEditable {
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


    /** List of query items */
    @JsonIgnore
    private ArrayList<MetadataQueryItem> items = new ArrayList<>();
    
    /** Add a query item */
    public void addItem(MetadataQueryItem item){
        items.add(item);
    }
    
    /** Get the number of query items */
    @JsonIgnore
    public int getSize(){
        return items.size();
    }
    
    /** Get a specific query item */
    @JsonIgnore
    public MetadataQueryItem getItem(int index){
        return items.get(index);
    }    
    
    /** Get the query items array list */
    public ArrayList<MetadataQueryItem> getQueryItems(){
        return items;
    }
    
    /** Set the query items array list */
    public void setQueryItems(ArrayList<MetadataQueryItem> items){
        this.items = items;
    }
    
    @JsonIgnore
    public Iterator<MetadataQueryItem> items(){
        return items.iterator();
    }
    
    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = new JSONObject();
        JSONArray itemArray = new JSONArray();
        JSONObject itemJson;
        for(MetadataQueryItem i : items){
            itemJson = i.toJson();
            itemArray.put(itemJson);
        }
        json.put("items", itemArray);
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        items.clear();
        JSONObject itemJson;
        JSONArray itemArray = json.getJSONArray("items");
        MetadataQueryItem item;
        for(int i=0;i<itemArray.length();i++){
            itemJson = itemArray.getJSONObject(i);
            item = (MetadataQueryItem)Class.forName(itemJson.getString("_className")).newInstance();
            item.readJson(itemJson);
            items.add(item);
        }
    }    
}