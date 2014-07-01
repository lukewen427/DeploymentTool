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
import org.pipeline.core.data.NumericalColumn;
import org.pipeline.core.data.maths.MaxValueCalculator;
import org.pipeline.core.data.maths.MinValueCalculator;

import java.io.Serializable;
import org.pipeline.core.xmlstorage.XmlDataStore;

/**
 * This class provides a column that can contain Integer numbers.
 * @author hugo
 */
public class IntegerColumn extends Column implements NumericalColumn, Serializable {
    static final long serialVersionUID = 7304601599431050448L;
    
    /** Creates a new instance of IntegerColumn */
    public IntegerColumn() {
        super(Long.class);
    }
    
    /** Creates a new instance of IntegerColumn */
    public IntegerColumn(String name) {
        super(Long.class, name);
    }
        
    /** Creates a new instance of IntegerColumn */
    public IntegerColumn(int size) throws DataException {
        super(Long.class, size);
    }    
    
    /** Creates a new instance of IntegerColumn with a size and initial value */
    public IntegerColumn(int size, int initialValue) throws DataException {
        super(Long.class, size);
        for(int i=0;i<size;i++){
            setIntValue(i, initialValue);
        }
    }

    /** Get an Integer value */
    public int intValue(int index) throws IndexOutOfBoundsException, DataException {
        return getIntValue(index);
    }
    
    /** Get a long value */
    public long longValue(int index) throws IndexOutOfBoundsException, DataException {
        return getLongValue(index);
    }
    
    /** Get an Integer value */
    public int getIntValue(int index) throws IndexOutOfBoundsException, DataException {
        if(!isMissing(index)){
            return ((Long)getObjectValue(index)).intValue();
        } else {
            throw new DataException("Value is missing");
        }        
    }
    
    /** Get a Long value */
    public long getLongValue(int index) throws IndexOutOfBoundsException, DataException {
        if(!isMissing(index)){
            return ((Long)getObjectValue(index)).longValue();
        } else {
            throw new DataException("Value is missing");
        }        
    }
    
    /** Set an Integer value */
    public void setIntValue(int index, int value) throws IndexOutOfBoundsException, DataException {
        setObjectValue(index, new Long(value));
    }

    /** Set a long value */
    public void setLongValue(int index, long value) throws IndexOutOfBoundsException, DataException {
        setObjectValue(index, new Long(value));
    }
    
    /** Set a Long value */
    public void setIntValue(int index, long value) throws IndexOutOfBoundsException, DataException {
        setObjectValue(index, new Long(value));
    }
    
    /** Append an Integer value */
    public void appendIntValue(int value) throws DataException {
        appendObjectValue(new Long(value));
    }

    /** Append a long value */
    public void appendLongValue(long value) throws DataException {
        appendObjectValue(new Long(value));
    }
    
    /** Add a value as a String */
    public void appendStringValue(String value) throws DataException {
        try {
            if(!value.equals(MissingValue.MISSING_VALUE_TEXT)){
                // Parse as a double first, then cast down to
                // a long.
                double dvalue = Double.parseDouble(value);

                appendObjectValue(new Long(Math.round(dvalue)));
            } else {
                appendObjectValue(MissingValue.get());
            }            
        } catch (Exception e){
            throw new DataException("Error parsing value :" + value);
        }        
    }
    
    /** Copy an object value from column */
    public Object copyObjectValue(int index) throws IndexOutOfBoundsException {
        if(index>=getRows()){
            throw new IndexOutOfBoundsException();
        } 
        
        Object value = getObjectValue(index);
        if(value == MissingValue.get() || value instanceof MissingValue){
            return MissingValue.get();
        } else {
            try {
                return new Long(getLongValue(index));
            } catch (Exception e){
                return MissingValue.get();
            }
        }
    }
    
    /** Get an empty copy of this column */
    public Column getEmptyCopy() {
        IntegerColumn col = new IntegerColumn();
        try {
            col.setName(getName());
            col.setProperties((XmlDataStore)getProperties().getCopy());
        } catch (Exception e){
            // Ignore because the only exception will come from a read-only column
            // which this isn#t
        }
        return col;
    }

    // ==================================================================
    // Implementation of NumericalColumn interface
    // ==================================================================
    /** Append a double value */
    public void appendDoubleValue(double value) throws DataException {
        appendIntValue((int)value);
    }

    /** Get value as a double */
    public double getDoubleValue(int index) throws IndexOutOfBoundsException, DataException {
        return (double)getIntValue(index);
    }

    /** Set value as a double */
    public void setDoubleValue(int index, double value) throws DataException, IndexOutOfBoundsException {
        setIntValue(index, (int)value);
    }

    /** Minimum value */
    public double minValue() {
        return new MinValueCalculator(this).doubleValue();
    }

    /** Maximum value */
    public double maxValue() {
        return new MaxValueCalculator(this).doubleValue();
    }

    /** Get values as a double[] array */
    public double[] getDoubleArray() {
        int size = getRows();
        double[] data = new double[size];
        for(int i=0;i<size;i++){
            try {
                data[i]=getDoubleValue(i);
            } catch (Exception e){
                data[i]=Double.NaN;
            }
        }
        return data;        
    }
}
