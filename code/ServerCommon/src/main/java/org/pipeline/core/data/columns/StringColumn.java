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

package org.pipeline.core.data.columns;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.MissingValue;

import java.io.Serializable;
import org.pipeline.core.xmlstorage.XmlDataStore;
/**
 * Column of text values
 * @author  hugo
 */
public class StringColumn extends Column implements Serializable {
    static final long serialVersionUID = 8185986185181681201L;
    
    /** Creates a new instance of StringColumn */
    public StringColumn() {
        super(String.class);
    }
    
    /** Creates a new instance of StringColumn */
    public StringColumn(String name) {
        super(String.class, name);
    }
    
    /** Creates a new instance of StringColumn with an initial size */
    public StringColumn(int size) throws DataException {
        super(String.class, size);
    }
    
    /** Creates a new instance of a DoubleColumn with an initial size and initial values */
    public StringColumn(int size, String initialValue)throws DataException {
        super(String.class, size);
        
        for(int i=0;i<size;i++){
            setStringValue(i, initialValue);
        }
    }

    /** Set a string value */
    public void setStringValue(int index, String value) throws DataException, IndexOutOfBoundsException {        
        setObjectValue(index, value);
    }
    
    /** Add a string value */
    public void appendStringValue(String value) throws DataException {
        if(value!=null){
            if(!value.equals(MissingValue.MISSING_VALUE_TEXT)){
                appendObjectValue(value);
            } else {
                appendObjectValue(MissingValue.get());
            }
        } else {
            appendObjectValue(MissingValue.get());
        }
    }
    
    /** Get an empty copy of this column */
    public Column getEmptyCopy() {
        StringColumn col = new StringColumn();
        try {
            col.setName(getName());
            col.setProperties((XmlDataStore)getProperties().getCopy());
        } catch (Exception e){
            // Ignore because the only exception will come from a read-only column
            // which this isn#t
        }
        return col;
    }
    
    /** Copy an object value from this column */
    public Object copyObjectValue(int index) throws IndexOutOfBoundsException {
        if(index>=getRows()){
            throw new IndexOutOfBoundsException();
        } 

        Object value = getObjectValue(index);
        if(value == MissingValue.get() || value instanceof MissingValue){
            return MissingValue.get();
        } else {
            return new String(getStringValue(index));
        }
    }
}
