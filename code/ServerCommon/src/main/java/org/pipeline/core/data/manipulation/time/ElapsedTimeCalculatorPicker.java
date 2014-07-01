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

package org.pipeline.core.data.manipulation.time;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.MissingValue;
import org.pipeline.core.data.columns.DateColumn;
import org.pipeline.core.data.columns.DoubleColumn;
import org.pipeline.core.data.manipulation.ColumnPicker;
import org.pipeline.core.data.time.TimeConstants;
import org.pipeline.core.data.time.TimeFunctions;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.util.Date;
/**
 * This column picker converts a date column to an elapsed time column
 * containing time in specified units by subtracting either the first
 * value, or a specified value from all of the data.
 * @author nhgh
 */
public class ElapsedTimeCalculatorPicker extends ColumnPicker implements TimeConstants {   
    /** Time units for converted data */
    private int units = SECONDS;
    
    /** Creates a new instance of ElapsedTimeCalculatorPicker */
    public ElapsedTimeCalculatorPicker() {
        super();
        setCopyData(false);
        setLimitColumnTypes(true);
        addSupportedColumnClass(DateColumn.class);
    }

    /** Find the first non-missing value and use it for the base time */
    public Date findBaseTime(DateColumn dateCol) throws DataException {
        int index = dateCol.findFirstNonMissingIndex();
        if(index!=-1){
            return dateCol.getDateValue(index);
        } else {
            throw new DataException("No date values where found in the specified column");
        }
    }
    
    /** Do the calculations */
    public Column pickColumn(Data data) throws IndexOutOfBoundsException, DataException {
        Column col = super.pickColumn(data);
        if(col instanceof DateColumn){
            DateColumn dateCol = (DateColumn)col;
            DoubleColumn elapsedTime = new DoubleColumn();
            elapsedTime.setName(dateCol.getName());
            int size = dateCol.getRows();            
            Date baseTime = findBaseTime(dateCol);
            for(int i=0;i<size;i++){
                try {
                    if(!dateCol.isMissing(i)){
                        elapsedTime.appendDoubleValue(TimeFunctions.calculateDifference(dateCol.dateValue(i), baseTime, units));
                    } else {
                        elapsedTime.appendObjectValue(MissingValue.get());
                    }
                    
                } catch (Exception e){
                    elapsedTime.appendObjectValue(MissingValue.get());
                }
            }
            
            return elapsedTime;
        } else {
            throw new DataException("Elapsed time calculation can only operate on date columns");
        }
    }
    
    /** Set the units */
    public void setUnits(int units){
        this.units = units;
    }
    
    /** Get the units */
    public int getUnits(){
        return units;
    }
    
    /** Recreate from storage */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        super.recreateObject(xmlDataStore);
        units = xmlDataStore.intValue("Units", SECONDS);
    }
    
    /** Save to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("Units", units);
        return store;
    }            
}