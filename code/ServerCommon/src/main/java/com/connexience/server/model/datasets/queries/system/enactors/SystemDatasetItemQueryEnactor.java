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
package com.connexience.server.model.datasets.queries.system.enactors;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.datasets.DatasetQueryEnactor;
import com.connexience.server.model.datasets.DatasetsUtils;
import com.connexience.server.model.datasets.system.SimpleSystemDatasetItem;
import com.connexience.server.util.JSONContainer;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The enactor for querying a system dataset item
 * @author hugo
 */
public class SystemDatasetItemQueryEnactor extends DatasetQueryEnactor {

    public SystemDatasetItemQueryEnactor() {
        connectionType = CONNECTION_TYPE.NO_CONNECTION;
    }

    @Override
    public JSONContainer performQuery() throws ConnexienceException {
        if(item instanceof SimpleSystemDatasetItem){
            try {
                JSONObject value;
                
                if(query.getKeyArray()!=null && query.getKeyArray().length>0){
                    value = DatasetsUtils.filterJson(((SimpleSystemDatasetItem)item).getValue(ticket), query.getKeyArray());
                } else {
                    value = ((SimpleSystemDatasetItem)item).getValue(ticket);
                }
                JSONArray data = new JSONArray();
                data.put(value);
                
                JSONObject result = new JSONObject();
                result.put("data", data);
                result.put("metadata", ((SimpleSystemDatasetItem)item).getMetadata(ticket));
                
                return new JSONContainer(result);                
            } catch (Exception e){
                throw new ConnexienceException("Error getting system data item value: " + e.getMessage(), e);
            }
            
        } else {
            throw new ConnexienceException("Unsupported system data item");
        }
    }
    
    
}
