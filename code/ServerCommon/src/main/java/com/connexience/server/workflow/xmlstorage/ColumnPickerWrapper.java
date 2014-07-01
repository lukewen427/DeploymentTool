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
package com.connexience.server.workflow.xmlstorage;

import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.data.manipulation.*;

/**
 * This class contains a data structure that represents a column picker object.
 * It allows data to be selected based on a column index or column name value.
 * @author hugo
 */
public class ColumnPickerWrapper implements XmlStorable {
    /** Pick a column by name */
    private boolean pickByName = false;

    /** Name to pick */
    private String columnName = "";

    /** Case sensitive */
    private boolean caseSensitive = true;

    /** Column index */
    private int columnIndex = 1;

    /** Is this picker 1-based */
    private boolean oneBased = true;

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isOneBased() {
        return oneBased;
    }

    public void setOneBased(boolean oneBased) {
        this.oneBased = oneBased;
    }

    public boolean isPickByName() {
        return pickByName;
    }

    public void setPickByName(boolean pickByName) {
        this.pickByName = pickByName;
    }

    /** Create a column picker object from this wrapper */
    public ColumnPicker createPicker(){
        ColumnPicker picker = new ColumnPicker();

        if(pickByName){
            picker.setPickType(ColumnPicker.PICK_BY_NAME);
            picker.setColumnName(columnName);
        } else {
            picker.setPickType(ColumnPicker.PICK_BY_NUMBER);
            if(oneBased){
                picker.setColumnIndex(columnIndex - 1);
            } else {
                picker.setColumnIndex(columnIndex);
            }
        }
        
        return picker;
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("ColumnPickerWrapper");
        store.add("PickByName", pickByName);
        store.add("CaseSensitive", caseSensitive);
        store.add("OneBased", oneBased);
        store.add("ColumnName", columnName);
        store.add("ColumnIndex", columnIndex);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        pickByName = store.booleanValue("PickByName", false);
        caseSensitive = store.booleanValue("CaseSensitive", true);
        oneBased = store.booleanValue("OneBased", true);
        columnName = store.stringValue("ColumnName", "");
        columnIndex = store.intValue("ColumnIndex", 1);
    }
}