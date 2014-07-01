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
import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.util.Vector;

/** 
 * This class is used to select a column from a set of Data based
 * upon a selection of either name or column number.
 * @author hugo
 */
public class ColumnPicker implements XmlStorable {
    /** Pick a column by number */
    public static final int PICK_BY_NUMBER = 0;
    
    /** Pick a column by name */
    public static final int PICK_BY_NAME = 1;
    
    /** Pick type */
    private int pickType = PICK_BY_NUMBER;
    
    /** Name of column to pick */
    private String columnName = "";
    
    /** Index of column to pick */
    private int columnIndex = 0;
    
    /** Is this picker limited to a certain column type */
    private boolean limitColumnTypes = false;
    
    /** Vector of column types */
    private Vector supportedColumnTypes = new Vector();
    
    /** Does this picker extract multiple columns */
    private boolean multiColumnPicker = false;

    /** Is this picker case sensitive */
    private boolean caseSensitive = true;

    /** Should this picker copy the data when it picks a column. By default
     * this is true, but pickers that subclass this basic picker should set
     * this to false and copy the column during processing. The actual column
     * should NEVER be returned and a copy MUST ALWAYS be returned by the
     * processing. */
    private boolean copyData = true;
     
    /** Creates a new instance of ColumnPicker */
    public ColumnPicker() {
    }
    
    /** Creates a new instance of ColumnPicker by parsing a selection string. Strings
     * are treated as if they are named unles the string starts with a '#'*/
    public ColumnPicker(String definition) throws DataException {
        configure(definition);
    }

    /** Configure this picker using a definition string */
    public void configure(String definition) throws DataException {
        if(!definition.equalsIgnoreCase("")){
            if(definition.startsWith("#")){
                // Numerical picker
                pickType = ColumnPicker.PICK_BY_NUMBER;
                try {
                    columnIndex = Integer.parseInt(definition.substring(1, definition.length()));
                } catch (Exception e){
                    throw new DataException("Cannot parse column index: " + e.getMessage());
                }

            } else {
                // Name picker
                pickType = ColumnPicker.PICK_BY_NAME;
                columnName = definition;
            }
        }
    }

    /** Write settings to a string */
    public String toStringDefinition(){
        if(pickType==ColumnPicker.PICK_BY_NAME){
            return columnName;
        } else {
            return "#" + columnIndex;
        }
    }
    
    /** Is this picker a multi-column picker */
    public boolean isMultiColumnPicker(){
        return multiColumnPicker;
    }
    
    /** Set whether this picker is a multi-column picker */
    public void setMultiColumnPicker(boolean multiColumnPicker){
        this.multiColumnPicker = multiColumnPicker;
    }
    
    /** Add a supported column class */
    public void addSupportedColumnClass(Class columnClass){
        supportedColumnTypes.addElement(columnClass);
    }
    
    /** Clear the supported column classes */
    public void clearSupportedColumnClasses(){
        supportedColumnTypes.clear();
    }
    
    /** Is a column class supported */
    public boolean isColumnClassSupported(Class columnClass){
        return supportedColumnTypes.contains(columnClass);
    }
    
    /** Set whether this picker copies data */
    public void setCopyData(boolean copyData){
        this.copyData = copyData;
    }
    
    /** Set whether to limit data types */
    public void setLimitColumnTypes(boolean limitColumnTypes){
        this.limitColumnTypes = limitColumnTypes;
    }
    
    /** Are the allowed data types restricted */
    public boolean isLimitColumnTypes(){
        return limitColumnTypes;
    }
    
    /** Get whether this picker copies data */
    public boolean getCopyData(){
        return copyData;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public Column[] pickColumns(Data data, boolean performCopy)throws IndexOutOfBoundsException, DataException {
        if(!multiColumnPicker){
            // Pick the column in the standard way
            Column[] results = new Column[1];
            results[0] = pickColumn(data, performCopy);
            return results;
        } else {
            throw new DataException("pickColumns not implemented for multi-column picker");
        }        
    }
    
    /** Pick an array of columns from a set of data */
    public Column[] pickColumns(Data data) throws IndexOutOfBoundsException, DataException {
        if(!multiColumnPicker){
            // Pick the column in the standard way
            Column[] results = new Column[1];
            results[0] = pickColumn(data);
            return results;
        } else {
            throw new DataException("pickColumns not implemented for multi-column picker");
        }
    }
    
    /** Pick a column with a copy flag */
    public Column pickColumn(Data data, boolean performCopy) throws IndexOutOfBoundsException, DataException {
        Column column = null;
        switch(pickType){
            case PICK_BY_NAME:
                if(caseSensitive){
                    column = data.column(columnName);
                } else {
                    column = data.columnCaseInsentitive(columnName);
                }
                break;
                
            case PICK_BY_NUMBER:
            default:
                column = data.column(columnIndex);

        }
        
        // Copy data if required
        if(performCopy){
            return column.getCopy();
        } else {
            return column;
        }        
    }
    
    /** Pick a column from a set of data */
    public Column pickColumn(Data data) throws IndexOutOfBoundsException, DataException {
        return pickColumn(data, copyData);
    }
    
    /** Pick a column reference from a set of data. This does not copy the data, and the
     * column used should probably not be used for anything else */
    public Column getColumnReference(Data data) throws IndexOutOfBoundsException, DataException {
        Column column = null;
        switch(pickType){
            case PICK_BY_NAME:
                if(caseSensitive){
                    column = data.column(columnName);
                } else {
                    column = data.columnCaseInsentitive(columnName);
                }
                break;
                
            case PICK_BY_NUMBER:
            default:
                column = data.column(columnIndex);

        }        
        return column;
    }
    
    /** Get the pick type */
    public int getPickType() {
        return pickType;
    }
    
    /** Set the pick type */
    public void setPickType(int pickType) {
        this.pickType = pickType;
    }

    /** Get the column name */
    public String getColumnName() {
        return columnName;
    }

    /** Set the column name */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /** Get the column number */
    public int getColumnIndex() {
        return columnIndex;
    }

    /** Set the column number */
    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }
    
    /** Override toString method */
    public String toString(){
        switch(pickType){
            case PICK_BY_NAME:
                return "Column named: " + columnName;
                
            case PICK_BY_NUMBER:
            default:
                return "Column number: " + columnIndex;
        }
        
    }

    /** Recreate from storage */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        pickType = xmlDataStore.intValue("PickType", PICK_BY_NUMBER);
        columnName = xmlDataStore.stringValue("ColumnName", "");
        columnIndex = xmlDataStore.intValue("ColumnIndex", 0);
        caseSensitive = xmlDataStore.booleanValue("CaseSensitive", true);
    }

    /** Save to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("ColumnSelection");
        store.add("PickType", pickType);
        store.add("ColumnName", columnName);
        store.add("ColumnIndex", columnIndex);
        store.add("CaseSensitive", caseSensitive);
        return store;
    }
}
