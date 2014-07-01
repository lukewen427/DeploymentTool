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

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * This class represents a column of data within a set of Data
 * @author  hugo (modified by Dominic 30/1/2014)
 */
public abstract class Column implements XmlStorable, Serializable {
    static final long serialVersionUID = -9175440052040488079L;
    
    /** Type of data in this column */
    private Class<?> dataType = null;
    
    /** Vector of data in this column */
    protected Vector columnData = null;
    
    /** Column name */
    protected String name = "";
    
    /** Parent data collection */
    private Data parentData = null;
    
    /** Additional column properties */
    private XmlDataStore properties;
    
    /** Creates a new instance of Column */
    public Column(Class<?> dataType) {
        columnData = new Vector();
        this.dataType = dataType;
    }
    
    /** Create an empty set of properties */
    public void createEmptyProperties(){
        this.properties = new XmlDataStore("DataProperties");
    }
    
    /** Get the properties */
    public XmlDataStore getProperties(){
        if(properties==null){
            createEmptyProperties();
        }
        return this.properties;
    }
    
    /** Set the properties */
    public void setProperties(XmlDataStore properties){
        this.properties = properties;
    }
    
    /** Shift the data up by a number of rows */
    public void shiftDataUp(int rows) throws IndexOutOfBoundsException {
        int size = columnData.size();
        if(rows<=size){
            for(int i=0;i<(size - rows);i++){
                columnData.setElementAt(columnData.elementAt(i + rows), i);
            }
        
            // Blank off the last rows
            for(int i=0;i<rows;i++){
                columnData.removeElementAt(columnData.size() - 1);
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
    
    /** Trim to a specific size */
    public void trimToSize(int rows){
        if(rows<columnData.size()){
            while(columnData.size()>rows){
                columnData.removeElementAt(columnData.size() - 1);
            }
        }
    }

    /** Pad this data column to a specified size */
    public void padToSize(int rows){
        if(rows>columnData.size()){
            while(columnData.size()<rows){
                columnData.add(MissingValue.get());
            }
        }
    }
    
    /** Creates a new instance of Column with a name */
    public Column(Class<?> dataType, String name) {
        columnData = new Vector();
        this.dataType = dataType;
        this.name = name;
    }

    /** Creates a new instance of Column with a pre-sepecifed size */
    public Column(Class<?> dataType, int initialSize) throws DataException {
        columnData = new Vector(initialSize);
        this.dataType = dataType;
        nullifyData();
    }
    
    /** Set the parent data collection */
    protected void setParentData(Data parentData){
        this.parentData = parentData;
    }
    
    /** Is this column read only */
    public boolean isReadOnly(){
        if(parentData!=null){
            return parentData.isReadOnly();
        } else {
            return false;
        }
    }
       
    /** Get the parent data collection */
    public Data getParentData() throws DataException {
        return parentData;
    }
    
    /** Set the column name */
    public void setName(String name) throws DataException {
        if(!isReadOnly()){
            this.name = name;
        } else {
            throw new DataException("Data is read-only");
        }
    }
    
    /** Get the column name */
    public String getName(){
        return name;
    }
    
    /** Return Meta-Data describing this column */
    public ColumnMetaData getMetaData(){
        if(this instanceof NumericalColumn){
            return new ColumnMetaData(name, dataType, getClass(), true, getRows());
        } else {
            return new ColumnMetaData(name, dataType, getClass(), false, getRows());
        }
    }
    
    /** Return the column data type */
    public Class<?> getDataType(){
        return dataType;
    }
    
    /** Set an empty size for this column and nullify all the data */
    public void nullifyToSize(int size) throws DataException, IndexOutOfBoundsException {
        if(!isReadOnly()){
            if(size>=0){
                columnData.clear();
                columnData.setSize(size);
                nullifyData();
            } else {
                throw new IndexOutOfBoundsException();
            }
        } else {
            throw new DataException("Data is read-only");
        }
    }
    
    /** Set all values to null in the Vector */
    public void nullifyData() throws DataException {
        if(!isReadOnly()){
            if(columnData!=null){
                int size = columnData.size();
                for(int i=0;i<size;i++){
                    columnData.set(i, MissingValue.get());
                }
            }
        } else {
            throw new DataException("Data is read-only");
        }
    }
    
    /** Fill a range with missing values */
    public void nullifyRange(int start, int end) throws DataException, IndexOutOfBoundsException {
        if(!isReadOnly()){
            if(start>=0 && start<columnData.size() && end>=0 && end<columnData.size()){
                for(int i=start;i<end;i++){
                    columnData.set(i, MissingValue.get());
                }
            } else {
                throw new IndexOutOfBoundsException();
            }
        } else {
            throw new DataException("Data is read-only");
        }
    }
    
    /** Set a specific value as an object */
    public void setObjectValue(int index, Object value) throws IndexOutOfBoundsException, DataException {
        if(!isReadOnly()){
            if(index>=0 && index<columnData.size()){
                if(value==null){
                    columnData.set(index, MissingValue.get());
                } else if(value.getClass().equals(dataType) || value == MissingValue.get() || value instanceof MissingValue){
                    columnData.set(index, value);
                } else {
                    throw new DataException("Invalid class type :" + value.getClass().getName());
                }
            }
        } else {
            throw new DataException("Data is read-only");
        }
    }
    
    /** Insert an object value. If the index is greater than the length of the column and
     * the padMissing flag is set to true, the column is padded with missing values up to
     * the insertion point */
    public void insertObjectValue(int index, Object value, boolean padMissing) throws DataException, IndexOutOfBoundsException {
        if(!isReadOnly()){
            if(index>=0){
                if(value.getClass().equals(dataType) || value == MissingValue.get() || value instanceof MissingValue){
                    if(index<columnData.size()){
                        // Ok to insert
                        columnData.insertElementAt(value, index);
                        
                    } else {
                        if(padMissing){
                            // Need to pad with missing values
                            int currentLength = columnData.size();
                            for(int i=currentLength;i<index;i++){
                                appendObjectValue(MissingValue.get());
                            }
                            appendObjectValue(value);
                            
                        } else {
                            throw new IndexOutOfBoundsException();
                        }
                    }
                    
                } else {
                    throw new DataException("Invalid class type :" + value.getClass().getName());
                }
            }
        } else {
            throw new DataException("Data is read-only");
        }        
    }
    
    /** Does this data set have a set of properties */
    public boolean hasProperties(){
        if(properties!=null){
            return true;
        } else {
            return false;
        }
    }
    
    /** Returns the columndata as List. 
     * This passes back a reference to the columndata List NOT a copy of it.
     * 
     * @return A List of column data 
     */
    public List<?> getList() {
        return this.columnData;   
    }
    
    /** Get a value */
    public Object getObjectValue(int index) throws IndexOutOfBoundsException {
        return columnData.elementAt(index);
    }
    
    /** Append a value */
    public void appendObjectValue(Object value) throws DataException {
        if(!isReadOnly()){
            if(dataType.isAssignableFrom(value.getClass()) || value == MissingValue.get() || value instanceof MissingValue){
                columnData.addElement(value);
            } else {
                throw new DataException("Invalid class type :" + value.getClass().getName());
            }
        } else {
            throw new DataException("Data is read-only");
        }
    }
    
    /** Return the number of rows in the column */
    public int getRows(){
        return columnData.size();
    }
    
    /** Return the number of rows that are not missing values. This method should
     * be called once and the result stored in a variable if it's being used in a 
     * loop, as it's quite an expensive check */
    public int getNonMissingRows(){
        int count = 0;
        int size = columnData.size();
        for (int i = 0; i < size; i++) {
            Object value = columnData.elementAt(i);
            if(value != MissingValue.get() && !(value instanceof MissingValue)){
                count++;
            }
        }
        return count;
    }

    /** Get a subset of the data. If padMissing is set to true, rows greater
     * than the size of this column will be padded with missing values */
    public Column getSubset(int startPos, int endPos, boolean padMissing) throws IndexOutOfBoundsException, DataException {
        Column col = getEmptyCopy();
        if(startPos<0){
            throw new IndexOutOfBoundsException();
        }
        
        int size = getRows();
        
        if(padMissing){
            if(startPos==endPos){
                if(startPos<size){
                    col.appendObjectValue(copyObjectValue(startPos));
                } else {
                    col.appendObjectValue(MissingValue.get());
                }
                
            } else {
                for(int i=startPos;i<endPos;i++){
                    if(i<size){
                        col.appendObjectValue(copyObjectValue(i));
                    } else {
                        col.appendObjectValue(MissingValue.get());
                    }
                }
            }
            
        } else {
            if(endPos>=size){
                throw new IndexOutOfBoundsException();
            } else {
                if(startPos==endPos){
                    col.appendObjectValue(copyObjectValue(startPos));
                } else {
                    for(int i=startPos;i<endPos;i++){
                        col.appendObjectValue(copyObjectValue(i));
                    }
                }
            }            
        }
        return col;        
    }
    
    /** Is a value a missing value */
    public boolean isMissing(int index) throws IndexOutOfBoundsException
    {
        Object value = columnData.elementAt(index);
        return value == MissingValue.get() || value instanceof MissingValue;
    }


    /** Locate a specific object value in this column. The row index is returned, 
     * or -1 if the value is not present. TODO: probably need to investigate
     * indexing columns for efficiency */
    public int locateValue(Object value) {
        try {
            int size = this.getRows();
            for(int i=0;i<size;i++){
                if(!isMissing(i)){
                    if(getObjectValue(i).equals(value)){
                        // Found row
                        return i;
                    }
                }
            }
            
            // Nothing found
            return -1;
            
        } catch (Exception e){
            return -1;
        }
    }

    /** Remove a specific value from this column. This is not the same as setting a value to
     * Missing as it shifts subsequent values up one place */
    public void removeValue(int index) throws IndexOutOfBoundsException {
        if(index>=0 && index<columnData.size()){
             columnData.removeElementAt(index);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
    
    /** Get a copy of this column */
    public Column getCopy() throws DataException {
        Column copyColumn = getEmptyCopy();
        int rows = getRows();
        for(int i=0;i<rows;i++){
            copyColumn.appendObjectValue(copyObjectValue(i));
        }
        return copyColumn;        
    }
    
    /** Get a value as a String */
    public String getStringValue(int index) throws IndexOutOfBoundsException {
        return getObjectValue(index).toString();
    }

    /**
     * Recreate this object from an XmlDataStore
     */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        name = store.stringValue("Name", "");
        int size = store.intValue("Size", 0);
        String value;
        columnData.clear();
        
        for(int i=0;i<size;i++){
            value = store.stringValue("R" + i, "");
            if(value.trim().equals(MissingValue.MISSING_VALUE_REPRESENTATION)){
                // Missing
                try {
                    appendObjectValue(MissingValue.get());
                } catch (Exception e){
                    throw new XmlStorageException("Error adding missing value");
                }
            } else {
                // Non-missing
                try {
                    appendStringValue(value);
                } catch (Exception e){
                    throw new XmlStorageException("Error adding value: " + value);
                }
            }
        }
        
        if(store.containsName("Properties")){
            properties = store.xmlDataStoreValue("Properties"); 
        } else {
            properties =  null;
        }        
    }

    /**
     * Save this object to an XmlDataStore. This uses String values to load
     * and save data. Some subclasses may need to override this behavior.
     */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("Column");
        store.add("Name", name);
        int size = columnData.size();
        store.add("Size", size);
        for(int i=0;i<size;i++){
            if(this.isMissing(i)){
                store.add("R" + i, MissingValue.MISSING_VALUE_REPRESENTATION);
            } else {
                try {
                    store.add("R" + i, getStringValue(i));
                } catch (Exception e){
                    store.add("R" + i, MissingValue.MISSING_VALUE_REPRESENTATION);
                }
            }
        }
        
        if(properties!=null){
            store.add("Properties", properties);
        }        
        return store;
    }
    
    /** Find the location of the first non-missing value. Returns -1 if there are no
     * non-missing values */
    public int findFirstNonMissingIndex(){
        for (int i = 0; i < columnData.size(); i++) {
            Object value = columnData.get(i);
            if (value != MissingValue.get() && value != null && !(value instanceof MissingValue)) {
                return i;
            }
        }
        return -1;
    }
    
    /** Get the first non-missing object value */
    public Object getFirstNonMissingValue() throws DataException {
        for(int i=0;i<columnData.size();i++){
            if(!isMissing(i)){
                return columnData.get(i);
            }
        }
        return null;
    }
    
    /** Get the value of a row in a format for CxD writing. This will normally
     * default to the standard getStringValue, some columns will override
     * this method to return a differently formatted string */
     public String getCxDFormatValue(int row) throws DataException {
         return getStringValue(row);
     }
    
     /** Append a row in CxD string format. Typically this will default to the 
      * standard appendStringValue method, but some column types will need
      * something different */
     public void appendCxDFormatValue(String value) throws DataException {
         appendStringValue(value);
     }
     
    /** Add a value as a String */
    public abstract void appendStringValue(String value) throws DataException;
        
    /** Copy an object value from column */
    public abstract Object copyObjectValue(int index) throws IndexOutOfBoundsException;
    
    /** Get an empty copy of this column */
    public abstract Column getEmptyCopy();
}
