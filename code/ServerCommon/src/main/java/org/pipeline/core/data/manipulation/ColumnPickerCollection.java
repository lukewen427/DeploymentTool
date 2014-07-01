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

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class contains a set of column picker objects so that a new set
 * of data can be constructed from existing data based on a defined selection
 * of columns.
 * @author hugo
 */
public class ColumnPickerCollection implements XmlStorable {
    /** Collection of column pickers */
    private Vector pickers = new Vector();
    
    // Data extraction options
    
    /** Only extract the selected data */
    public static final int SELECTED_ONLY = 0;
    
    /** Extract the selected and any unselected columns */
    public static final int SELECTED_AND_REMAINING = 1;
    
    /** Extract the selected columns and forward all of the additional data */
    public static final int SELECTED_AND_ALL = 2;
    
    /** Selection type flag */
    private int extractionMethod = SELECTED_ONLY;
    
    /** Creates a new instance of ColumnPickerCollection */
    public ColumnPickerCollection() {
    }

    /** Extract a new set of columns from some existing data */
    public Data extractData(Data data) throws IndexOutOfBoundsException, DataException {
        Data newData = new Data();
        Column[] column;
        Enumeration e = pickers.elements();
        while(e.hasMoreElements()){
            column = ((ColumnPicker)e.nextElement()).pickColumns(data);
            newData.addColumns(column);
        }
        
        // Decide what to add to the selected data
        switch(extractionMethod){
            case SELECTED_AND_ALL:
                for(int i=0;i<data.getColumns();i++){
                    newData.addColumn(data.column(i).getCopy());
                }
                return newData;
                
            case SELECTED_AND_REMAINING:
                Data nonSelected = extractNonSelectedData(data);
                for(int i=0;i<nonSelected.getColumns();i++){
                    newData.addColumn(nonSelected.column(i));
                }
                return newData;
                
            case SELECTED_ONLY:
            default:
                return newData;
            
        }
    }
    
    /** Set all the collected column pickers to either copy or not copy data */
    public void setPickersCopyData(boolean copyData){
        Enumeration e = pickers.elements();
        while(e.hasMoreElements()){
            ((ColumnPicker)e.nextElement()).setCopyData(copyData);
        }
    }
    
    /** Extract the non-selected data from some input data */
    public Data extractNonSelectedData(Data data) throws IndexOutOfBoundsException, DataException {
        Vector columnsToIgnore = new Vector();
        Enumeration e = pickers.elements();
        Column c;
        
        while(e.hasMoreElements()){
            c = ((ColumnPicker)e.nextElement()).getColumnReference(data);
            if(!columnsToIgnore.contains(c)){
                columnsToIgnore.addElement(c);
            }
        }
        
        Data newData = new Data();
        e = data.columns();
        while(e.hasMoreElements()){
            c = (Column)e.nextElement();
            if(!columnsToIgnore.contains(c)){
                newData.addColumn(c.getCopy());
            }
        }
        return newData;
    }
    
    /** Get all of the columns and put them into a Vector. This does not copy the data */
    public Vector extractColumnVector(Data data) throws IndexOutOfBoundsException, DataException {
        Vector columns = new Vector();
        
        Column[] column;
        Enumeration e = pickers.elements();
        while(e.hasMoreElements()){
            column = ((ColumnPicker)e.nextElement()).pickColumns(data, false);
            for(int i=0;i<column.length;i++){
                columns.addElement(column[i]);
            }
        }        
        return columns;
    }
    
    /** Add a picker */
    public void addColumnPicker(ColumnPicker picker){
        if(!pickers.contains(picker)){
            pickers.addElement(picker);
        }
    }
    
    /** Add a picker from a string definition */
    public void addColumnPicker(String definition) throws DataException {
        ColumnPicker p = new ColumnPicker(definition);
        pickers.add(p);
    }
    
    /** Remove a picker */
    public void removeColumnPicker(ColumnPicker picker){
        if(pickers.contains(picker)){
            pickers.removeElement(picker);
        }
    }
    
    /** Delete a picker */
    public void deleteColumnPicker(int index){
        pickers.removeElementAt(index);
    }
    
    /** Get a picker */
    public ColumnPicker picker(int index){
        return (ColumnPicker)pickers.elementAt(index);
    }
    
    /** Get the number of pickers */
    public int size(){
        return pickers.size();
    }
    
    /** Recreate from storage */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        int size = xmlDataStore.intValue("PickerCount", 0);
        extractionMethod = xmlDataStore.intValue("ExtractionMethod", SELECTED_ONLY);
        pickers.clear();
        for(int i=0;i<size;i++){
            addColumnPicker((ColumnPicker)xmlDataStore.xmlStorableValue("Picker" + i));
        }
    }

    /** Save to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("ColumnSelectionList");
        store.add("ExtractionMethod", extractionMethod);
        store.add("PickerCount", size());
        for(int i=0;i<size();i++){
            store.add("Picker" + i, picker(i));
        }
        return store;
    }
    
    /** Get the selection method */
    public int getExtractionMethod(){
        return extractionMethod;
    }
    
    /** Set the selection method */
    public void setExtractionMethod(int extractionMethod){
        this.extractionMethod = extractionMethod;
    }
    
    /** Create the pickers from and array of Strings. These strings specify
     * the column patterns. By default, the name of the column is used. If the
     * string starts with a '#', the index is used. */
    public void populateFromStringArray(String[] pickerDefinitions) throws DataException {
        pickers.clear();
        String definition;
        ColumnPicker picker;
        
        for(int i=0;i<pickerDefinitions.length;i++){
            definition = pickerDefinitions[i].trim();
            picker = new ColumnPicker(definition);
            addColumnPicker(picker);
        }
    }
    
    /** Store these pickers to an array of Strings */
    public String[] saveToStringArray(){
        ColumnPicker picker;
        String[] definitions = new String[pickers.size()];
        for(int i=0;i<this.pickers.size();i++){
            picker = (ColumnPicker)pickers.get(i);
            definitions[i] = picker.toStringDefinition();
        }
        return definitions;
    }
}
