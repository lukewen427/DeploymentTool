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
import com.connexience.server.workflow.xmlstorage.*;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.xmldatatypes.*;
import com.connexience.server.model.document.*;
import com.connexience.server.model.folder.*;
import com.connexience.server.util.JSONProject;
import com.connexience.server.workflow.types.WorkflowProject;
import org.json.*;
import java.util.*;

/**
 * This class exports an XmlDataStore to a JSON object
 * @author nhgh
 */
public class JSONPropertiesExporter {
    /** Properties to export */
    private XmlDataStore properties;

    public JSONPropertiesExporter(XmlDataStore properties) {
        this.properties = properties;
    }

    /** Export to JSON */
    public JSONObject createPropertiesJson() throws Exception {

        JSONObject json = new JSONObject();
        JSONArray propertyArray = new JSONArray();
        JSONObject propertyJson;


        Vector n = properties.getNames();
        Collections.sort(n);
        Enumeration names = n.elements();
        String name;
        int count = 0;

        XmlDataObject value;
        while(names.hasMoreElements()){
            name = names.nextElement().toString();
            value = properties.get(name);

            propertyJson = new JSONObject();
            propertyJson.put("name", name);
            propertyJson.put("type", value.getTypeLabel());
            propertyJson.put("description", value.getDescription());
            propertyJson.put("value", JSONStringHelper.escapeString(properties.getPropertyString(name)));
            propertyJson.put("category", value.getCategory());
            if(value.isDefaultSupported()){
                propertyJson.put("defaultSupported", true);
                propertyJson.put("defaultPresent", value.isDefaultPresent());
                // No need to escape '\' because it'll be escaped before transmission to the network
                propertyJson.put("defaultValue", JSONStringHelper.escapeString(properties.getDefaultPropertyString(name)));
            } else {
                propertyJson.put("defaultSupported", false);
            }

            // Set the exposed attributes
            if(value.isExposedProperty()){
                propertyJson.put("exposedProperty", true);
            } else {
                propertyJson.put("exposedProperty", false);
            }
            propertyJson.put("exposedName", value.getExposedName());

            if(value.hasOptions()){
                JSONArray optionsJson = new JSONArray();
                String[] options = value.getOptions();
                for(String s : options){
                    optionsJson.put(s);
                }
                propertyJson.put("options", optionsJson);
            }

            if(value.getTypeLabel().equalsIgnoreCase("XmlStorable")){

                String className = ((XmlStorableDataObject)value).getClassName();
                if(className.equals("com.connexience.server.workflow.xmlstorage.DocumentRecordWrapper") || className.equals("com.connexience.server.model.document.DocumentRecord")){
                    propertyJson.put("type", "Document");
                    propertyJson.put("jsonValue", createDocumentJsonObject((DocumentRecord)((XmlStorableDataObject)value).getValue()));

                } else if(className.equals("com.connexience.server.workflow.xmlstorage.FolderWrapper") || className.equals("com.connexience.server.model.folder.Folder")){
                    propertyJson.put("type", "Folder");
                    propertyJson.put("jsonValue", createFolderJsonObject((Folder)((XmlStorableDataObject)value).getValue()));

                } else if(className.equals("com.connexience.server.workflow.xmlstorage.StringListWrapper")){
                    propertyJson.put("type", "StringList");
                    propertyJson.put("jsonValue", createStringListObject((StringListWrapper)((XmlStorableDataObject)value).getValue()));
                    
                } else if(className.equals("com.connexience.server.workflow.xmlstorage.StringPairListWrapper")){
                    propertyJson.put("type", "TwoColumnList");
                    propertyJson.put("jsonValue", createStringPairListObject((StringPairListWrapper)((XmlStorableDataObject)value).getValue()));

                } else if(className.equals("com.connexience.server.workflow.xmlstorage.DatasetItemWrapper")){
                    propertyJson.put("type", "DatasetItem");
                    propertyJson.put("jsonValue", createDatasetItemObject(((DatasetItemWrapper)((XmlStorableDataObject)value).getValue())));
                    
                } else if(DatasetQueryFactory.containsClassName(className)){
                    // This is a dataset query property
                    propertyJson.put("type", "DatasetQuery");
                    propertyJson.put("jsonValue", (((DatasetQuery)((XmlStorableDataObject)value).getValue())).toJson());
                    
                } else if(className.equals("com.connexience.server.workflow.types.WorkflowProject")) {
                    // Project property 
                    propertyJson.put("type", "Project");
                    propertyJson.put("jsonValue", (((WorkflowProject)((XmlStorableDataObject)value).getValue())).toJson());

                } else {
                    // Generic server object class
                    Object storableValue = ((XmlStorableDataObject)value).getValue();
                    if(storableValue instanceof ServerObject){
                        propertyJson.put("type", "ServerObject");
                        propertyJson.put("className", ((XmlStorableDataObject)value).getExpectedClassname());
                        propertyJson.put("jsonValue", createServerObjectJson((ServerObject)((XmlStorableDataObject)value).getValue()));
                        
                    } else if(storableValue instanceof DatasetQuery){
                        propertyJson.put("type", "DatasetQuery");
                        propertyJson.put("jsonValue", (((DatasetQuery)((XmlStorableDataObject)value).getValue())).toJson());
                    
                    } else {
                        throw new Exception("Cannot export object: " + className);
                    }
                }

            }
            propertyArray.put(propertyJson);
            count++;

        }
        json.put("propertyCount", count);
        json.put("propertyArray", propertyArray);

        return json;


    }

    /** Create basic JSON properties for a ServerObject */
    private JSONObject createServerObjectJson(ServerObject object) throws Exception {
        JSONObject objectJson = new JSONObject();
        objectJson.put("id", object.getId());
        objectJson.put("name", object.getName());
        objectJson.put("description", object.getDescription());
        objectJson.put("className", object.getClass().getName());
        return objectJson;
    }
    
    /** Create an auxiliary JSON object for a document */
    private JSONObject createDocumentJsonObject(DocumentRecord document) throws Exception {
        JSONObject documentJson = new JSONObject();
        documentJson.put("id", document.getId());
        documentJson.put("name", document.getName());
        documentJson.put("description", document.getDescription());
        return documentJson;
    }

    /** Create an auxiliary JSON object for a folder */
    private JSONObject createFolderJsonObject(Folder folder) throws Exception {
        JSONObject folderJson = new JSONObject();
        folderJson.put("id", folder.getId());
        folderJson.put("name", folder.getName());
        folderJson.put("description", folder.getDescription());
        return folderJson;
    }

    /** Create a String List JSON object */
    private JSONObject createStringListObject(StringListWrapper stringList) throws Exception {
        JSONObject listJson = new JSONObject();
        JSONArray values = new JSONArray();
        for(int i=0;i<stringList.getSize();i++){
            values.put(stringList.getValue(i));
        }
        listJson.put("values", values);
        listJson.put("valueCount", stringList.getSize());
        return listJson;
    }

    /** Create a two column list wrapper */
    private JSONObject createStringPairListObject(StringPairListWrapper stringList) throws Exception {
        JSONObject listJson = new JSONObject();
        JSONArray column1 = new JSONArray();
        JSONArray column2 = new JSONArray();
        for(int i=0;i<stringList.getSize();i++){
            column1.put(stringList.getValue(i, 0));
            column2.put(stringList.getValue(i, 1));
        }
        listJson.put("column1", column1);
        listJson.put("column2", column2);
        listJson.put("valueCount", stringList.getSize());
        return listJson;
    }
    
    /** Create a dataset item wrapper */
    private JSONObject createDatasetItemObject(DatasetItemWrapper item) throws Exception {
        JSONObject itemJson = new JSONProject();
        itemJson.put("datasetId", item.getDatasetId());
        itemJson.put("itemName", item.getName());
        return itemJson;
    }
}
