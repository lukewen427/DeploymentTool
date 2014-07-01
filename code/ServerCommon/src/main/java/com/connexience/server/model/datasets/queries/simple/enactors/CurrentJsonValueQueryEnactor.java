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
package com.connexience.server.model.datasets.queries.simple.enactors;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.datasets.DatasetQueryEnactor;
import com.connexience.server.model.datasets.DatasetsUtils;
import com.connexience.server.model.datasets.items.single.SingleJsonRowItem;
import com.connexience.server.model.datasets.system.SimpleSystemDatasetItem;
import com.connexience.server.util.JSONContainer;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This enactor just gets the current value of a single json value row
 * @author hugo
 */
public class CurrentJsonValueQueryEnactor extends DatasetQueryEnactor {

    public CurrentJsonValueQueryEnactor() {
        connectionType = CONNECTION_TYPE.NO_CONNECTION;
    }

    @Override
    public JSONContainer performQuery() throws ConnexienceException {
        if(item instanceof SingleJsonRowItem){
            String value = ((SingleJsonRowItem)item).getStringValue();
            if(value!=null && !value.isEmpty()){
                try {
                    JSONObject result = new JSONObject();
                    JSONArray data = new JSONArray();
                    
                    // Filter keys
                    if(query.getKeyArray()!=null && query.getKeyArray().length>0){
                        data.put(DatasetsUtils.filterJson(new JSONObject(value), query.getKeyArray()));
                    } else {
                        data.put(new JSONObject(value));
                    }                    
                    result.put("data", data);
                    return new JSONContainer(result);
                } catch (Exception e){
                    throw new ConnexienceException("Error performing query: " + e.getMessage(), e);
                }
            } else {
                try {
                    JSONObject result = new JSONObject();
                    JSONArray data = new JSONArray();
                    data.put(new JSONObject());
                    result.put("data", data);
                    return new JSONContainer(result);
                } catch (Exception e){
                    throw new ConnexienceException("Error performing query: " + e.getMessage(), e);
                }
            }
            
        } else {
            throw new ConnexienceException("Unsupported item type");
        }
    }
    
    
}