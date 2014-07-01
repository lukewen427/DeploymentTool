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

package org.pipeline.core.data;

import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class contains Meta-Data describing a Column of Data within a set of Data.
 * @author  hugo
 */
public class ColumnMetaData implements XmlStorable {
    /** Name of column */
    private String name;
    
    /** Number of rows in column */
    private int rows;
        
    /** Data type of the column */
    private Class dataType;
    
    /** Column representation class */
    private Class columnClass;
    
    /** Is the number of rows known */
    private boolean rowSizeKnown = true;
            
    /** Type info ID */
    private String columnTypeId;

    /** Is this a numerical column */
    private boolean numerical = false;

    /** Empty constructor for XmlStorage loading */
    public ColumnMetaData(){
        
    }
    
    /** Creates a new instance of ColumnMetaData */
    public ColumnMetaData(String name, Class dataType, Class columnClass, boolean numerical, int rows) {
        rowSizeKnown = true;
        this.dataType = dataType;
        this.rows = rows;
        this.name = name;
        this.columnClass = columnClass;
        this.columnTypeId = ColumnFactory.getColumnTypeInfo(columnClass).getId();
        this.numerical = numerical;
    }
    
    /** Creates a new instance of ColumnMetaData */
    public ColumnMetaData(String name, Class dataType, Class columnClass, boolean numerical){
        rowSizeKnown = false;
        this.dataType = dataType;
        this.name = name;
        this.columnTypeId = ColumnFactory.getColumnTypeInfo(dataType).getId();
        this.numerical = numerical;
    }
    
    /** Get the column class */
    public Class getColumnClass(){
        return columnClass;
    }
    
    /** Get a copy of this ColumnMetaData object */
    public ColumnMetaData getCopy(){
        if(isRowSizeKnown()){
            return new ColumnMetaData(getName(), getDataType(), getColumnClass(), numerical, getRows());
            
        } else {
            return new ColumnMetaData(getName(), getDataType(), getColumnClass(), numerical);
        }
    }

    /** Is this column numerical */
    public boolean isNumerical(){
        return numerical;
    }
    
    /** Get the column name */
    public String getName() {
        return name;
    }

    /** Get the row count */
    public int getRows() {
        return rows;
    }

    /** Get the data type */
    public Class getDataType() {
        return dataType;
    }

    /** Is the length of the column known */
    public boolean isRowSizeKnown() {
        return rowSizeKnown;
    }
    
    /** Override toString method to return column name */
    public String toString(){
        return name;
    }
    
    /** Get the column type ID */
    public String getColumnTypeId(){
        return this.columnTypeId;
    }

    /** Save to an xml data store */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("ColumnMetaData");
        store.add("Name", name);
        store.add("Rows", rows);
        store.add("RowsKnown", rowSizeKnown);
        store.add("ColumnTypeID", columnTypeId);
        return store;
    }

    /** Read from an xml data store */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        name = store.stringValue("Name", "");
        rows = store.intValue("Rows", 0);
        rowSizeKnown = store.booleanValue("RowsKnown", false);
        columnTypeId = store.stringValue("ColumnTypeID", "");
    }
}