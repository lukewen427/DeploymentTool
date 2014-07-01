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

import org.pipeline.core.data.*;
import org.pipeline.core.data.columns.DateColumn;
import org.pipeline.core.data.columns.DoubleColumn;
import org.pipeline.core.data.columns.IntegerColumn;
import org.pipeline.core.data.manipulation.ColumnPicker;
import org.pipeline.core.data.time.TimeConstants;
import org.pipeline.core.data.time.TimeFunctions;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.util.Date;
/**
 * This class provides a column picker that converts an elapsed time such
 * as 0, 0.5, 1, 1.5 hours to an absolute timestamp value based upon a
 * defined start time and specified time unit.
 * @author hugo
 */
public class ElapsedTimeToTimeStampColumnPicker extends ColumnPicker implements TimeConstants {
    /** Start time to use for generating timestamps */
    private Date startDate = new Date();
    
    /** Time units */
    private int timeUnits = SECONDS;
    
    /** Creates a new instance of ElapsedTimeToTimeStampColumnPicker */
    public ElapsedTimeToTimeStampColumnPicker() {
        super();
        setCopyData(false);
        setLimitColumnTypes(true);
        addSupportedColumnClass(DoubleColumn.class);
        addSupportedColumnClass(IntegerColumn.class);
    }
    
    /** Get the start date */
    public Date getStartDate(){
        return startDate;
    }
    
    /** Set the start date */
    public void setStartDate(Date startDate){
        this.startDate = startDate;
    }
    
    /** Get the time units */
    public int getTimeUnits(){
        return timeUnits;
    }
    
    /** Set the time units */
    public void setTimeUnits(int timeUnits){
        this.timeUnits = timeUnits;
    }
    
    /** Do the calculations */
    public Column pickColumn(Data data) throws IndexOutOfBoundsException, DataException {
        Column col = super.pickColumn(data);
        if(col instanceof NumericalColumn){
            NumericalColumn numberCol = (NumericalColumn)col;
            DateColumn dateCol = new DateColumn();
            dateCol.setName(col.getName());
            int size = col.getRows();
            long elapsedMilliseconds;
            Date dateValue;
            
            for(int i=0;i<size;i++){
                if(!col.isMissing(i)){
                    elapsedMilliseconds = TimeFunctions.convertToMilliseconds(numberCol.getDoubleValue(i), timeUnits);
                    dateValue = new Date(startDate.getTime() + elapsedMilliseconds);
                    dateCol.appendDateValue(dateValue);
                } else {
                    dateCol.appendObjectValue(MissingValue.get());
                }
            }
            return dateCol;
            
        } else {
            throw new DataException("Numerical Data required");
        }
    }
    
    /** Recreate from storage */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        super.recreateObject(xmlDataStore);
        startDate = xmlDataStore.dateValue("StartDate", new Date());
        timeUnits = xmlDataStore.intValue("TimeUnits", SECONDS);
    }
    
    /** Save to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("StartDate", startDate);
        store.add("TimeUnits", timeUnits);
        return store;
    }        
}
