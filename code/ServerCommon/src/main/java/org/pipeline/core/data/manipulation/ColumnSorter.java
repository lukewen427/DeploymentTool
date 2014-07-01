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
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.columns.IntegerColumn;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This class sorts a column into order. It returns a Integer column with the
 * original orders in it.
 * @author hugo
 */
public class ColumnSorter {
    /** Original column */
    private Column originalColumn;
    
    /** Sorted data */
    private Vector sortedData;
    
    /** Index mapping back to original order */
    private IntegerColumn indexColumn;
    
    /** Sort data into descending order */
    private boolean descending = false;
    
    /** Creates a new instance of ColumnSorter */
    public ColumnSorter(Column originalColumn) {
        this.originalColumn = originalColumn;
    }
    
    /** Get the sorted data */
    public Column sort() throws DataException {
        try {
            sortedData = new Vector();

            // Copy original data into sorted vector
            int size = originalColumn.getRows();
            for(int i=0;i<size;i++){
                sortedData.addElement(new SortHolder(i, originalColumn.copyObjectValue(i)));
            }

            // Sort the data
            Collections.sort(sortedData);
            
            // Sort into descending if necessary
            if(descending){
                Collections.reverse(sortedData);
            }
            
            // Copy this into the new column
            Column sorted = originalColumn.getEmptyCopy();
            indexColumn = new IntegerColumn(sortedData.size());
            indexColumn.setName("Index");

            Enumeration e = sortedData.elements();
            SortHolder holder;
            
            while(e.hasMoreElements()){
                holder = (SortHolder)e.nextElement();
                sorted.appendObjectValue(holder.getValue());
                indexColumn.appendIntValue(holder.getIndex());
            }
            
            return sorted;
        } catch (Exception ex){
            throw new DataException("Error sorting data: " + ex.getMessage());
        }
    }       
    
    /** Get the index column */
    public IntegerColumn getIndexColumn(){
        return indexColumn;
    }
    
    /** Class to hold a value */
    private class SortHolder implements Comparable {
        /** Value */
        private Object value;
        
        /** Index */
        private int index;
        
        /** Constructor */
        public SortHolder(int index, Object value){
            this.index = index;
            this.value = value;
        }
        
        /** Get the object value */
        public Object getValue(){
            return value;
        }
        
        /** Get the index */
        public int getIndex(){
            return index;
        }
        
        /** Do the actual compare */
        public int compareTo(Object o) {
            if(o instanceof SortHolder){
                return ((Comparable)value).compareTo(((SortHolder)o).getValue());
            } else {
                throw new ClassCastException();
            }
        }
    }

    /** Does this class sort into descending order */
    public boolean isDescending() {
        return descending;
    }

    /** Set whether the data is sorted into descending order */
    public void setDescending(boolean descending) {
        this.descending = descending;
    }
}
