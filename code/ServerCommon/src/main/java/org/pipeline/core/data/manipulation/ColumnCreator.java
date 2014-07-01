/* =================================================================
 *                     conneXience Data Pipeline
 * =================================================================
 *
 * Copyright 2006 Hugo Hiden and Adrian Conlin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.pipeline.core.data.manipulation;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.ColumnFactory;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class is used to create a column of data. It forms the basis for
 * objects that create data without necessarily picking columns.
 * @author hugo
 */
public abstract class ColumnCreator implements XmlStorable {
    /** Name for the new column */
    private String name = "";
    
    /** Data type for new column */
    private String columnTypeId = "double-column";
    
    /** Creates a new instance of ColumnCreator */
    public ColumnCreator(String columnTypeId) {
        this.columnTypeId = columnTypeId;
    }

    /** Create the empty column */
    protected Column createEmptyColumn() throws DataException {
        Column col = ColumnFactory.createColumn(columnTypeId);
        col.setName(name);
        return col;
    }
    
    /** Recreate this object from a store */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        name = store.stringValue("Name", "");
        columnTypeId = store.stringValue(columnTypeId, "");
    }

    /** Save this object to a store */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("ColumnCreator");
        store.add("Name", name);
        store.add("ColumnTypeID", columnTypeId);
        return store;
    }
    
    /** Create the column of data */
    public Column createColumn() throws DataException {
        return createEmptyColumn();
    }
    
    /** Create the column based on an existing set of data */
    public Column createColumn(Data data) throws DataException {
        return createEmptyColumn();
    }
}
