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
import org.pipeline.core.data.columns.DoubleColumn;
import org.pipeline.core.data.columns.IntegerColumn;
import org.pipeline.core.data.manipulation.ColumnPicker;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This picker selects a column and scales it 0-1 it before returning a new column.
 * @author hugo
 */
public class ScalingColumnPicker extends ColumnPicker {
    /** Minimum scale value */
    private double minScaled = 0;
    
    /** Maximum scale value */
    private double maxScaled = 1;
    
    /**
     * Creates a new instance of ScalingColumnPicker 
     */
    public ScalingColumnPicker() {
        super();
        setLimitColumnTypes(true);
        addSupportedColumnClass(DoubleColumn.class);
        addSupportedColumnClass(IntegerColumn.class);        
        setCopyData(false);
    }
    
    /** Pick and normalise the column */
    public Column pickColumn(Data data) throws IndexOutOfBoundsException, DataException {
        Column c = super.pickColumn(data);
        if(c instanceof DoubleColumn){
            DoubleColumn doubleColumn = (DoubleColumn)c;
            double max = doubleColumn.maxValue();
            double min = doubleColumn.minValue();
            double v;
            double sv;
            
            int size = doubleColumn.getRows();
            DoubleColumn scaled = new DoubleColumn();
            scaled.setName(doubleColumn.getName());
            double diff = max - min;
            
            if(diff>0){
                for(int i=0;i<size;i++){
                    sv = (doubleColumn.getDoubleValue(i) - min) / diff;
                    v = (sv + minScaled) * maxScaled;
                    scaled.appendDoubleValue(v);
                }
                return scaled;
                
            } else {
                throw new DataException("Scaling column picker is unable to scale column");
            }
            
        } else {
            throw new DataException("Scaling column picker requires a numerical column");
        }
    }
    
    /** Get the minimum scaled value */
    public double getMinScaled() {
        return minScaled;
    }

    /** Set the minimum scaled value */
    public void setMinScaled(double minScaled) {
        this.minScaled = minScaled;
    }

    /** Get the maximum scaled value */
    public double getMaxScaled() {
        return maxScaled;
    }

    /** Set the maximum scaled value */
    public void setMaxScaled(double maxScaled) {
        this.maxScaled = maxScaled;
    }
    
    /** Recreate from storage */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        super.recreateObject(xmlDataStore);
        maxScaled = xmlDataStore.doubleValue("MaxScaled", 1);
        minScaled = xmlDataStore.doubleValue("MinScaled", 0);
    }

    /** Save to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("MaxScaled", maxScaled);
        store.add("MinScaled", minScaled);
        return store;
    }    
}
