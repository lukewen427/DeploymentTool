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
package com.connexience.server.model.datasets.queries.system;

import com.connexience.server.model.datasets.DatasetItem;
import com.connexience.server.model.datasets.DatasetQuery;
import com.connexience.server.model.datasets.queries.system.enactors.SystemDatasetItemQueryEnactor;
import com.connexience.server.model.datasets.system.SimpleSystemDatasetItem;
import org.json.JSONObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class does a query on a system dataset item to return the current value
 * @author hugo
 */
public class SystemDatasetItemQuery extends DatasetQuery {

    public SystemDatasetItemQuery() {
        supportedClass = SimpleSystemDatasetItem.class;
        enactorClass = SystemDatasetItemQueryEnactor.class;
        label = "Current Value";
    }
    
    @Override
    public JSONObject toJson() throws Exception {
        return super.toJson();
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        return super.storeObject();
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
    }
}
