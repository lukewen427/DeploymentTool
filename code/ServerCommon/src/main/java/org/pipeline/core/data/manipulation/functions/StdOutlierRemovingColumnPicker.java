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
import org.pipeline.core.data.maths.StdOutlierRemover;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class selects a column and removes outliers from it using the
 * n x Standard deviation criteria.
 * @author hugo
 */
public class StdOutlierRemovingColumnPicker extends ColumnPicker {
    /** Outler removing calculator */
    private StdOutlierRemover filter = new StdOutlierRemover();
    
    /** Creates a new instance of StdOutlierRemovingColumnPicker */
    public StdOutlierRemovingColumnPicker() {
        super();
        setLimitColumnTypes(true);
        addSupportedColumnClass(DoubleColumn.class);
        addSupportedColumnClass(IntegerColumn.class);        
        setCopyData(false);
    }
    
    /** Pick and scale the column */
    public Column pickColumn(Data data) throws IndexOutOfBoundsException, DataException {
        Column c = super.pickColumn(data);
        if(c instanceof NumericalColumn){
            NumericalColumn raw = (NumericalColumn)c;
            NumericalColumn result = filter.screenColumn(raw);
            return (Column)result;
        } else {
            throw new DataException("Filters can only operate on numerical columns");
        }
    }    
    
    /** Set the standard deviation tolerance */
    public double getStdTolerance(){
        return filter.getStd();
    }
    
    /** Get the standard deviation tolerance */
    public void setStdTolerance(double std){
        filter.setStd(std);
    }
    
    /** Get the missing value index column */
    public IntegerColumn getOutlierIndexColumn(){
        return filter.getOutlierIndex();
    }
    
    /** Get the original raw data column */
    public NumericalColumn getRawDataColumn(){
        return filter.getOriginalColumn();
    }
    
    /** Get the screened column */
    public NumericalColumn getScreenedColumn(){
        return filter.getScreenedColumn();
    }
    
    /** Recreate from storage */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        super.recreateObject(xmlDataStore);
        filter.setStd(xmlDataStore.doubleValue("StdTolerance", 3));
    }

    /** Save to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("StdTolerance", filter.getStd());
        return store;
    }        
}
