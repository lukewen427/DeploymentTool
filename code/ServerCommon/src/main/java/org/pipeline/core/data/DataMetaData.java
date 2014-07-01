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
 * This class contains information about a set of Data.
 * @author  hugo
 */
public class DataMetaData implements XmlStorable {
    ColumnMetaData[] columns;
    
    /** Create an empty instance */
    public DataMetaData(){
        
    }
    
    /** Creates a new instance of DataMetaData */
    public DataMetaData(Data data) {
        int cols = data.getColumns();
        columns = new ColumnMetaData[data.getColumns()];
        for(int i=0;i<cols;i++){
            columns[i]=data.column(i).getMetaData();
        }
    }
    
    /** Construct from another set of meta-data */
    public DataMetaData(DataMetaData metaData){
        int cols = metaData.getColumns();
        columns = new ColumnMetaData[cols];
        for(int i=0;i<cols;i++){
            columns[i]=metaData.column(i).getCopy();
        }
    }
    
    /** Get the column count */
    public int getColumns(){
        return columns.length;
    }
    
    /** Get all of the columns */
    public ColumnMetaData[] columnArray(){
        return columns;
    }
    
    /** Get meta-data for a column */
    public ColumnMetaData column(int index){
        if(index>=0 && index<columns.length){
            return columns[index];
        } else {
            return null;
        }
    }
    
    /** Find the index of a column given a name */
    public int findColumnIndex(String name){
        for(int i=0;i<columns.length;i++){
            if(columns[i].getName().equals(name)){
                return i;
            }
        }
        return -1;
    }

    /** Does another set of metadata match this one */
    public boolean metaDataMatches(DataMetaData metaData){
        if(metaData.getColumns()==getColumns()){
            int cols = metaData.getColumns();
            ColumnMetaData column1;
            ColumnMetaData column2;

            for(int i=0;i<cols;i++){
                column1 = column(i);
                column2 = metaData.column(i);

                if((!column1.getColumnTypeId().equals(column2.getColumnTypeId())) && (!column1.getName().equals(column2.getName()))){
                    return false;
                }
            }

            return true;
            
        } else {
            // Wrong number of columns
            return false;
        }
    }

    /** Save to an XmlDataStore */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("DataMetaData");
        store.add("ColumnCount", columns.length);
        for(int i=0;i<columns.length;i++){
            store.add("Column" + i, columns[i]);
        }
        return store;
    }

    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        int size = store.intValue("ColumnCount", 0);
        columns = new ColumnMetaData[size];
        for(int i=0;i<size;i++){
            columns[i] = (ColumnMetaData)store.xmlStorableValue("Column" + i);
        }
    }
}
