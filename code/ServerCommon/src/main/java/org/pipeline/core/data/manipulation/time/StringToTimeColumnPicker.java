/**
 * e-Science Central
 * Copyright (C) 2008-2013 School of Computing Science, Newcastle University
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package org.pipeline.core.data.manipulation.time;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.MissingValue;
import org.pipeline.core.data.columns.DateColumn;
import org.pipeline.core.data.columns.StringColumn;
import org.pipeline.core.data.manipulation.ColumnPicker;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class provides a column picker that converts a String column into
 * a Date column
 * @author hugo
 */
public class StringToTimeColumnPicker extends ColumnPicker {
    /** Text of the date format to use for parsing text columns */
    String dateFormat = "";
    
    /** Creates a new instance of StringToTimeColumnPicker */
    public StringToTimeColumnPicker() {
        super();
        setLimitColumnTypes(true);
        addSupportedColumnClass(StringColumn.class);        
        setCopyData(false);
    }
    
    /** Pick and convert the column */
    public Column pickColumn(Data data) throws IndexOutOfBoundsException, DataException {
        Column c = super.pickColumn(data);
        SimpleDateFormat format = null;
        try {
            format = new SimpleDateFormat(dateFormat);
        } catch (Exception e){
            throw new DataException("Invalid data format: " + e.getLocalizedMessage());
        }
        
        if(c instanceof StringColumn) {
            DateColumn newColumn = new DateColumn(c.getName());
            int size = c.getRows();
            String textValue;
            Date dateValue;
            
            for(int i=0;i<size;i++){
                try {
                    if(!c.isMissing(i)){
                        textValue = c.getStringValue(i);
                        dateValue = format.parse(textValue);
                        newColumn.appendDateValue(dateValue);
                    } else {
                        newColumn.appendObjectValue(MissingValue.get());
                    }
                    
                } catch (Exception e){
                    newColumn.appendObjectValue(MissingValue.get());
                }
            }
            
            return newColumn;
            
        } else {
            throw new DataException("Date parsing can only be applied to text columns");
        }
        
    }
    
    /** Get the date format text */
    public String getDateFormat(){
        return dateFormat;
    }
    
    /** Set the date format text */
    public void setDateFormat(String dateFormat){
        this.dateFormat = dateFormat;
    }
    
    /** Recreate from storage */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        super.recreateObject(xmlDataStore);
        dateFormat = xmlDataStore.stringValue("DateFormat", "");
    }
    
    /** Save to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("DateFormat", dateFormat);
        return store;
    }        
}
