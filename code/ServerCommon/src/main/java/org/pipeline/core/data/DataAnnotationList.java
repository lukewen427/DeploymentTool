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
package org.pipeline.core.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.util.ArrayList;

/**
 * This class contains a set of data annotation objects
 * @author hugo
 */
public class DataAnnotationList implements XmlStorable {
    /** Annotation list */
    private ArrayList<DataAnnotation> annotations = new ArrayList<>();

    public DataAnnotationList() {
    }

    public DataAnnotationList(JSONObject json) throws JSONException {
        parseJson(json);
    }
    
    public int getSize(){
        return annotations.size();
    }
    
    public DataAnnotation getAnnotation(int index){
        return annotations.get(index);
    }
    
    public void addAnnotation(DataAnnotation annotation){
        annotations.add(annotation);
    }
    
    public void removeAnnotation(DataAnnotation annotation){
        annotations.remove(annotation);
    }
    
    public void removeAnnotation(int index){
        annotations.remove(index);
    }
    
    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("DataAnnotationList");
        store.add("AnnotationCount", annotations.size());
        for(int i=0;i<annotations.size();i++){
            store.add("Annotation" + i, annotations.get(i));
        }
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        annotations.clear();
        int size = store.intValue("AnnotationCount", 0);
        for(int i=0;i<size;i++){
            annotations.add((DataAnnotation)store.xmlStorableValue("Annotation" + i));
        }
    }
    
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        JSONArray annotationsJson = new JSONArray();
        for(int i=0;i<annotations.size();i++){
            annotationsJson.put(annotations.get(i).toJson());
        }
        json.put("items", annotationsJson);
        return json;
    }
    
    public void parseJson(JSONObject json) throws JSONException {
        JSONArray annotationsJson = json.getJSONArray("items");
        annotations.clear();
        for(int i=0;i<annotationsJson.length();i++){
            annotations.add(new DataAnnotation(annotationsJson.getJSONObject(i)));
        }
    }
}