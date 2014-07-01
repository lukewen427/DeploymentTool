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
package com.connexience.server.model.datasets.queries.index;

import com.connexience.server.model.datasets.DatasetQuery;
import com.connexience.server.model.datasets.items.multiple.JsonMultipleValueItem;
import com.connexience.server.model.datasets.queries.index.enactors.IndexRangeQueryEnactor;
import org.json.JSONObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class querys a range of multple value data by index
 * @author hugo
 */
public class IndexRangeQuery extends DatasetQuery {
    int startRow = 0;
    int numberOfRows = 100;
    
    public IndexRangeQuery() {
        label = "Set of rows";
        supportedClass = JsonMultipleValueItem.class;
        enactorClass = IndexRangeQueryEnactor.class;        
    }

    public int getStartRow() {
        return startRow;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("StartRow", startRow);
        store.add("NumberOfRows", numberOfRows);
        return store;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
        if(json.has("StartRow")){
            startRow = json.getInt("StartRow");
        }
        if(json.has("NumberOfRows")){
            numberOfRows = json.getInt("NumberOfRows");
        }
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store); 
        startRow = store.intValue("StartRow", 0);
        numberOfRows = store.intValue("NumberOfRows", 100);
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("StartRow", startRow);
        json.put("NumberOfRows", numberOfRows);
        return json;
    }
}
