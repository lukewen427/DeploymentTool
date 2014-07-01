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
package org.pipeline.core.data.io;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pipeline.core.xmlstorage.XmlDataObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.util.Enumeration;

/**
 * This class exports a set of properties as a JSON object. It only works with
 * the simple property types.
 * @author hugo
 */
public class SimpleJsonPropertiesExporter {
    /** Properties to export */
    private XmlDataStore properties;

    public SimpleJsonPropertiesExporter(XmlDataStore properties) {
        this.properties = properties;
    }
    
    public JSONObject toJson() throws JSONException, XmlStorageException {
        JSONObject json = new JSONObject();
        JSONArray propertyArray = new JSONArray();
        JSONObject propertyJson;

        Enumeration names = properties.getNames().elements();
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
            propertyJson.put("value", properties.getPropertyString(name));
            propertyArray.put(propertyJson);
            count++;

        }
        json.put("properties", propertyArray);
        json.put("objectId", "");
        json.put("groupName", "");

        return json;   
    }
}