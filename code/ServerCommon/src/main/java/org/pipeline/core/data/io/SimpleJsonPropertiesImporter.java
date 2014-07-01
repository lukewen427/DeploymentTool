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
import org.pipeline.core.xmlstorage.XmlDataObjectFactory;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class imports a properties object from a JSON structure
 * @author hugo
 */
public class SimpleJsonPropertiesImporter {
    /** JSON to import */
    private JSONObject json;

    public SimpleJsonPropertiesImporter(JSONObject json) {
        this.json = json;
    }
    
    
    public XmlDataStore parseJson() throws XmlStorageException, JSONException {
        XmlDataStore store = new XmlDataStore();
        JSONArray propertyArray = json.getJSONArray("properties");
        
        JSONObject propertyJson;
        XmlDataObject property;
        
        for(int i=0;i<propertyArray.length();i++){
            propertyJson = propertyArray.getJSONObject(i);
            property = XmlDataObjectFactory.createDataObject(propertyJson.getString("type"), propertyJson.getString("type"), propertyJson.getString("value"));
            property.setDescription(propertyJson.getString("description"));
            store.add(property);
        }
        
        return store;
    }
}