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
package com.connexience.server.model.datasets.queries.temporal;

import com.connexience.server.model.datasets.DatasetConstants;
import com.connexience.server.model.datasets.DatasetQuery;
import com.connexience.server.model.datasets.items.multiple.JsonMultipleValueItem;
import com.connexience.server.model.datasets.queries.temporal.enactors.LatestByRelativeTimeQueryEnactor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class queries data using a relative time. Queries such as
 * "Data from last 10 minutes" can be expressed using this query.
 * @author hugo
 */
public class LatestByRelativeTimeQuery extends DatasetQuery implements DatasetConstants {
    /** Unit of time for this query */
    private String timeUnit = DAY;
    
    /** Number of time units */
    private int numberOfUnits = 1;
    
    public LatestByRelativeTimeQuery() {
        label = "Values from relative time";
        enactorClass = LatestByRelativeTimeQueryEnactor.class;
        supportedClass = JsonMultipleValueItem.class;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public int getNumberOfUnits() {
        return numberOfUnits;
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("TimeUnit", timeUnit);
        store.add("NumberOfUnits", numberOfUnits);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        timeUnit = store.stringValue("TimeUnit", DAY);
        numberOfUnits = store.intValue("NumberOfUnits", 1);
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("TimeUnit", timeUnit);
        json.put("NumberOfUnits", numberOfUnits);
        
        // Add options
        JSONArray options = new JSONArray();
        options.put(YEAR);
        options.put(WEEK);
        options.put(DAY);
        options.put(HOUR);
        options.put(MINUTE);
        options.put(SECOND);
        JSONObject optionsJson = new JSONObject();
        optionsJson.put("TimeUnit", options);
        json.put("_options", optionsJson);        
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
        if(json.has("TimeUnit")){
            timeUnit = json.getString("TimeUnit");
        }
        
        if(json.has("NumberOfUnits")){
            numberOfUnits = json.getInt("NumberOfUnits");
        }
    }
}