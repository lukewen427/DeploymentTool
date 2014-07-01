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

import org.pipeline.core.data.Column;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.NumericalColumn;
import org.pipeline.core.data.columns.DoubleColumn;
import org.pipeline.core.data.columns.IntegerColumn;
import org.pipeline.core.data.manipulation.ColumnPicker;
import org.pipeline.core.data.maths.FirstOrderColumnFilter;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class provides a column selector that does first order
 * filtering on a selected column.
 * @author hugo
 */
public class FirstOrderFilterColumnPicker extends ColumnPicker {
    /** Column filter */
    private FirstOrderColumnFilter filter = new FirstOrderColumnFilter();
            
    /** Creates a new instance of FirstOrderFilterColumnPicker */
    public FirstOrderFilterColumnPicker() {
        super();
        setLimitColumnTypes(true);
        addSupportedColumnClass(DoubleColumn.class);
        addSupportedColumnClass(IntegerColumn.class);
        setCopyData(false);
    }
    
    /** Get the filter alpha */
    public double getAlpha(){
        return filter.getAlpha();
    }
            
    /** Set the filter alpha */
    public void setAlpha(double alpha){
        if(alpha>=0 && alpha<=1){
            filter.setAlpha(alpha);
        }
    }
    
    /** Override the toString method */
    public String toString(){
        return super.toString() + " - Alpha: " + filter.getAlpha();
    }
    
    /** Pick and scale the column */
    public Column pickColumn(Data data) throws IndexOutOfBoundsException, DataException {
        Column c = super.pickColumn(data);
        if(c instanceof NumericalColumn){
            NumericalColumn raw = (NumericalColumn)c;
            return (Column)filter.filterData(raw);

        } else {
            throw new DataException("Filters can only operate on numerical columns");
        }
    }

    /** Recreate from storage */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        super.recreateObject(xmlDataStore);
        filter.setAlpha(xmlDataStore.doubleValue("Alpha", filter.getAlpha()));
    }

    /** Get the filtered data */
    public NumericalColumn getFilteredColumn(){
        return filter.getFilteredColumn();
    }
    
    /** Get the original data */
    public NumericalColumn getOriginalColumn(){
        return filter.getOriginalColumn();
    }
    
    /** Save to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("Alpha", filter.getAlpha());
        return store;
    }    
}
