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

package org.pipeline.core.data.manipulation.functions;

import org.pipeline.core.data.*;
import org.pipeline.core.data.manipulation.ColumnPicker;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class provides a column picker that can rename and change the data
 * type of a column.
 * @author nhgh
 */
public class DataTypeChangerColumnPicker extends ColumnPicker {
    /** New data type id of this column */
    private String newTypeId = "string-column";

    /** New name of this column */
    private String newName = "New Column";

    public DataTypeChangerColumnPicker() {
        setCopyData(false);
        setLimitColumnTypes(false);
    }

    @Override
    public Column pickColumn(Data data) throws IndexOutOfBoundsException, DataException {
        Column originalCol = super.pickColumn(data);
        Column newColumn = ColumnFactory.createColumn(newTypeId);
        newColumn.setName(newName);

        int size = originalCol.getRows();
        for(int i=0;i<size;i++){
            if(!originalCol.isMissing(i)){
                newColumn.appendStringValue(originalCol.getStringValue(i));
            } else {
                newColumn.appendObjectValue(MissingValue.get());
            }
        }
        return newColumn;
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("NewTypeID", newTypeId);
        store.add("NewName", newName);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        super.recreateObject(xmlDataStore);
        newTypeId = xmlDataStore.stringValue("NewTypeID", "");
        newName = xmlDataStore.stringValue("NewName", "");
    }

    /**
     * @return the newTypeId
     */
    public String getNewTypeId() {
        return newTypeId;
    }

    /**
     * @param newTypeId the newTypeId to set
     */
    public void setNewTypeId(String newTypeId) {
        this.newTypeId = newTypeId;
    }

    /**
     * @return the newName
     */
    public String getNewName() {
        return newName;
    }

    /**
     * @param newName the newName to set
     */
    public void setNewName(String newName) {
        this.newName = newName;
    }

    
}