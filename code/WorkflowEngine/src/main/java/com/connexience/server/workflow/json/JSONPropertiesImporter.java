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
package com.connexience.server.workflow.json;

import com.connexience.server.model.ServerObject;
import com.connexience.server.model.datasets.DatasetQuery;
import com.connexience.server.model.datasets.DatasetQueryFactory;
import com.connexience.server.workflow.xmlstorage.StringListWrapper;
import com.connexience.server.workflow.xmlstorage.StringPairListWrapper;
import com.connexience.server.model.document.*;
import com.connexience.server.model.folder.*;
import com.connexience.server.workflow.types.WorkflowProject;
import com.connexience.server.workflow.xmlstorage.DatasetItemWrapper;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.xmldatatypes.*;
import org.json.*;


/**
 * This class reads an XmlDataStore from a JSON representation
 * @author nhgh
 */
public class JSONPropertiesImporter {
    /** Properties JSON object */
    private JSONObject propertiesJson;

    public JSONPropertiesImporter(JSONObject propertiesJson) {
        this.propertiesJson = propertiesJson;
    }

    /** Parse the properties and create an XmlDataStore */
    public XmlDataStore createXmlDataStore() throws Exception {
        JSONObject propertyJson;
        XmlDataObject property;
        XmlDataStore loadedProperties = new XmlDataStore();

        int propertyCount = propertiesJson.getInt("propertyCount");
        JSONArray propertyArray = propertiesJson.getJSONArray("propertyArray");

        for(int i=0;i<propertyCount;i++){
            propertyJson = propertyArray.getJSONObject(i);
            try {
                if(propertyJson.has("name") && propertyJson.has("description") && propertyJson.has("type")){
                    property = createProperty(propertyJson);
                    if(property!=null){
                        loadedProperties.add(property);
                    }
                }
            } catch (XmlStorageException xmlse){
                System.out.println("Storage error: " + xmlse.getMessage());
            }
        }

        return loadedProperties;
    }

    /** Merge an existing property collection with the properties contained
     * in this importer */
    public void mergeXmlDataStore(XmlDataStore existingProperties) throws Exception {
        existingProperties.copyProperties(createXmlDataStore());
    }

    /** Create a property object */
    private XmlDataObject createProperty(JSONObject propertyJson) throws Exception {
        String name = propertyJson.getString("name");
        String type = propertyJson.getString("type");
        String description = propertyJson.getString("description");

        String value = null;
        if(propertyJson.has("value")){
            value = JSONStringHelper.unescapeString(propertyJson.getString("value"));
        }
        
        String category = null;
        if(propertyJson.has("category")){
            category = propertyJson.getString("category");
        }

        JSONObject jsonValue = null;
        if(propertyJson.has("jsonValue")){
            jsonValue = propertyJson.getJSONObject("jsonValue");
        }
        
        boolean defaultPresent = false;
        String defaultValueString = "";
        if(propertyJson.has("defaultSupported")){
            if(propertyJson.getBoolean("defaultSupported")){
                // Default is possibly present
                if(propertyJson.has("defaultPresent") && propertyJson.getBoolean("defaultPresent")==true){
                    if(propertyJson.has("defaultValue")){
                        defaultValueString = JSONStringHelper.unescapeString(propertyJson.getString("defaultValue"));
                        defaultPresent = true;
                    }
                }
            }
        }
        
        XmlDataObject property = null;

        if(type.equals("Document")){
            DocumentRecord docWrapper = new DocumentRecord();
            if(jsonValue.has("name")){docWrapper.setName(jsonValue.getString("name"));}
            if(jsonValue.has("id")){docWrapper.setId(jsonValue.getString("id"));}
            property = new XmlStorableDataObject(name, docWrapper);

        } else if(type.equals("Folder")){
            Folder folderWrapper = new Folder();
            if(jsonValue.has("name")){folderWrapper.setName(jsonValue.getString("name"));}
            if(jsonValue.has("id")){folderWrapper.setId(jsonValue.getString("id"));}
            property = new XmlStorableDataObject(name, folderWrapper);
            
        } else if(type.equals("StringList")){
            StringListWrapper stringList = createStringListWrapper(jsonValue);
            property = new XmlStorableDataObject(name, stringList);
            
        } else if(type.equals("TwoColumnList")){
            StringPairListWrapper stringPairList = createStringPairListWrapper(jsonValue);
            property = new XmlStorableDataObject(name, stringPairList);

        } else if(type.equals("DatasetItem")){
            DatasetItemWrapper datasetItem = createDatasetItemWrapper(jsonValue);
            property = new XmlStorableDataObject(name, datasetItem);
            
        } else if(type.equals("ServerObject")){
            if(jsonValue.has("className")){
                try {
                    ServerObject objectWrapper = (ServerObject)Class.forName(jsonValue.getString("className")).newInstance();
                    if(jsonValue.has("id")){objectWrapper.setId(jsonValue.getString("id"));}
                    if(jsonValue.has("name")){objectWrapper.setName(jsonValue.getString("name"));}
                    property = new XmlStorableDataObject(name, objectWrapper);
                } catch (Exception e){
                    throw new Exception("Cannot create object of type: " + jsonValue.getString("className"));
                }
            } else {
                throw new Exception("Cannot import property: " + name + " no class name available in JSON data");
            }
            
        } else if(type.equals("DatasetQuery")) {
            if(jsonValue.has("_className")){
                if(DatasetQueryFactory.containsClassName(jsonValue.getString("_className"))){
                    DatasetQuery q = DatasetQueryFactory.createQueryByClassname(jsonValue.getString("_className"));
                    if(q!=null){
                        q.readJson(jsonValue);
                        property = new XmlStorableDataObject(name, q);
                    } else {
                        throw new Exception("Null DatasetQuery object returned");
                    }
                } else {
                    DatasetQuery q = new DatasetQuery();
                    q.readJson(jsonValue);
                    property = new XmlStorableDataObject(name, q);
                }
            } else {
                throw new Exception("Cannot import property: " + name + " no class name available in JSON data");
            }
            
        } else if(type.equals("Project")){
            WorkflowProject projectItem = createWorkflowProject(jsonValue);
            property = new XmlStorableDataObject(name, projectItem);
            
        } else {
            if(!defaultPresent){
                property = XmlDataObjectFactory.createDataObject(type, value, name);
            } else {
                property = XmlDataObjectFactory.createDataObject(type, value, name, defaultValueString);
            }
        }

        if(property!=null){
            property.setDescription(description);
            property.setCategory(category);
            
            if(propertyJson.has("options")){
                // Add in the options
                JSONArray optionsJson = propertyJson.getJSONArray("options");
                
                String[] options = new String[optionsJson.length()];
                for(int i=0;i<optionsJson.length();i++){
                    options[i] = optionsJson.getString(i);
                }
                property.setOptions(options);
            }

            // Set exposure details
            if(propertyJson.has("exposedProperty")){
                if(propertyJson.getBoolean("exposedProperty")){
                    property.setExposedProperty(propertyJson.getBoolean("exposedProperty"));
                } else {
                    property.setExposedProperty(false);            
                }
            } else {
                property.setExposedProperty(false);
            }       
            
            // Setup the exposed name
            if(propertyJson.has("exposedName")){
                property.setExposedName(propertyJson.getString("exposedName"));
            } else {
                property.setExposedName("");
            }
            
            return property;
        } else {
            return null;
        }
    }

    /** Create a String List object */
    private StringListWrapper createStringListWrapper(JSONObject stringListJson) throws Exception {
        StringListWrapper wrapper = new StringListWrapper();
        int size = stringListJson.getInt("valueCount");
        JSONArray values = stringListJson.getJSONArray("values");
        for(int i=0;i<size;i++){
            wrapper.add(values.getString(i));
        }
        return wrapper;
    }

    /** Create a workflow project */
    private WorkflowProject createWorkflowProject(JSONObject projectJson) throws Exception {
        WorkflowProject p = new WorkflowProject();
        p.readJson(projectJson);
        return p;
    }
    
    /** Create a dataset item wrapper */
    private DatasetItemWrapper createDatasetItemWrapper(JSONObject itemJson) throws Exception {
        DatasetItemWrapper wrapper = new DatasetItemWrapper();
        if(itemJson.has("datasetId")){
            wrapper.setDatasetId(itemJson.getString("datasetId"));
        } else {
            wrapper.setDatasetId("");
        }
        
        if(itemJson.has("itemName")){
            wrapper.setName(itemJson.getString("itemName"));
        } else {
            wrapper.setName("");
        }
        return wrapper;
    }
    
    /** Create a two column string list */
    private StringPairListWrapper createStringPairListWrapper(JSONObject stringPairListJson) throws Exception {
        StringPairListWrapper wrapper = new StringPairListWrapper();
        int size = stringPairListJson.getInt("valueCount");
        JSONArray column1 = stringPairListJson.getJSONArray("column1");
        JSONArray column2 = stringPairListJson.getJSONArray("column2");
        for(int i=0;i<size;i++){
            wrapper.add(column1.getString(i), column2.getString(i));
        }
        return wrapper;
    }
}
