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
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class provides a column picker that applies a moving average
 * filter to the selected data column.
 * @author hugo
 */
public class MovingAverageFilterColumnPicker extends ColumnPicker {
    /** Moving average window length */
    private int windowLength = 10;
    
    /** Creates a new instance of MovingAverageFilterColumnPicker */
    public MovingAverageFilterColumnPicker() {
        super();
        setLimitColumnTypes(true);
        addSupportedColumnClass(DoubleColumn.class);
        addSupportedColumnClass(IntegerColumn.class);        
        setCopyData(false);        
    }

    /** Get the moving average window length */
    public int getWindowLength() {
        return windowLength;
    }

    /** Set the moving average window length */
    public void setWindowLength(int windowLength) {
        if(windowLength>0){
            this.windowLength = windowLength;
        }
    }
    
    /** Pick and scale the column */
    public Column pickColumn(Data data) throws IndexOutOfBoundsException, DataException {
        Column c = super.pickColumn(data);
        if(c instanceof NumericalColumn){
            NumericalColumn raw = (NumericalColumn)c;
            int rows = c.getRows();
            DoubleColumn filtered = new DoubleColumn();
            filtered.setName(c.getName() + "_Filtered");
            
            // Create a data window
            double[] window = new double[windowLength];
            
            
            
            return filtered;
            
        } else {
            throw new DataException("Filters can only operate on numerical columns");
        }
    }
    
    /** Shuffle the points of a vector along by one and add a new data point in position 0 */
    private void shiftWindow(double[] window, double dataPoint){
        int length = window.length;
        for(int i=length-1;i>0;i--){
            window[i]=window[i-1];
        }
        window[0]=dataPoint;
    }
    
    
    /** Recreate from storage */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        super.recreateObject(xmlDataStore);
        windowLength = xmlDataStore.intValue("WindowLength", 10);
    }

    /** Save to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("WindowLength", windowLength);
        return store;
    }        
}
