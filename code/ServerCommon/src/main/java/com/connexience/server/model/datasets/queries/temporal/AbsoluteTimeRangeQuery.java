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

import com.connexience.server.model.datasets.DatasetQuery;
import com.connexience.server.model.datasets.items.multiple.JsonMultipleValueItem;
import com.connexience.server.model.datasets.queries.temporal.enactors.AbsoluteTimeRangeQueryEnactor;
import com.connexience.server.util.JSONDate;
import java.util.Date;
import org.json.JSONObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This query extracts a time range from multiple value data
 * @author hugo
 */
public class AbsoluteTimeRangeQuery extends DatasetQuery {
    private Date startDate = new Date();
    private Date endDate = new Date();

    public AbsoluteTimeRangeQuery() {
        supportedClass = JsonMultipleValueItem.class;
        enactorClass = AbsoluteTimeRangeQueryEnactor.class;
        label = "Absolute time range";
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("StartDate", startDate);
        store.add("EndDate", endDate);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        startDate = store.dateValue("StartDate", new Date());
        endDate = store.dateValue("EndDate", new Date());
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("StartDate", new JSONDate(startDate));
        json.put("EndDate", new JSONDate(endDate));
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
        
        if(json.has("StartDate")){
            startDate = JSONDate.createDate(json.getJSONObject("StartDate"));
        }
        
        if(json.has("EndDate")){
            endDate = JSONDate.createDate(json.getJSONObject("EndDate"));
        }
    }
}