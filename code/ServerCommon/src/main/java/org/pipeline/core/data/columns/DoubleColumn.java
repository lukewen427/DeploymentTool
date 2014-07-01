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
 * Double precision column of data
 * @author  hugo
 */
public class DoubleColumn extends Column implements NumericalColumn, Serializable  {
    static final long serialVersionUID = -7796981154318676373L;
    
    /** Creates a new instance of DoubleColumn */
    public DoubleColumn() {
        super(Double.class);
    }
    
    /** Creates a new instance of DoubleColumn */
    public DoubleColumn(String name) {
        super(Double.class, name);
    }
    
    /** Creates a new instance of DoubleColumn with an initial size */
    public DoubleColumn(int size) throws DataException {
        super(Double.class, size);
    }
    
    /** Creates a new instance of a DoubleColumn with an initial size and initial values */
    public DoubleColumn(int size, double initialValue) throws DataException {
        super(Double.class, size);
        for(int i=0;i<size;i++){
            setDoubleValue(i, initialValue);
        }
    }

    /**
     * Creates a new instance of a DoubleColumn with the data from a double array
     * @param data array to initinialise the DoubleColumn with
     * @throws DataException
     */
    public DoubleColumn(double[] data) throws DataException {
        super(Double.class, data.length);
        for (double aData : data) {
            appendDoubleValue(aData);
        }
    }



    /** Get a double value */
    public double getDoubleValue(int index) throws IndexOutOfBoundsException, DataException {
        if(!isMissing(index)){
            return ((Double)getObjectValue(index)).doubleValue();
        } else {
            throw new DataException("Value is missing");
        }
    }
    
    /** Append a double value */
    public void appendDoubleValue(double value) throws DataException {
        appendObjectValue(new Double(value));
    }
    
    /** Set a double value */
    public void setDoubleValue(int index, double value) throws DataException, IndexOutOfBoundsException {
        setObjectValue(index, new Double(value));
    }

    @Override
    public void setLongValue(int index, long value) throws IndexOutOfBoundsException, DataException {
        setObjectValue(index, new Double((double)value));
    }

    @Override
    public long getLongValue(int index) throws IndexOutOfBoundsException, DataException {
        return (long)getDoubleValue(index);
    }

    @Override
    public void appendLongValue(long value) throws DataException {
        appendObjectValue(new Double((double)value));
    }

    /** Add a value as a String */
    public void appendStringValue(String value) throws DataException {
        try {
            if(!value.equals(MissingValue.MISSING_VALUE_TEXT) && !value.trim().equalsIgnoreCase("")){
                appendObjectValue(new Double(value));
            } else {
                appendObjectValue(MissingValue.get());
            }
        } catch (Exception e){
            throw new DataException("Error parsing value :" + value);
        }
    }
    
    /** Get all the values as an array of doubles */
    public double[] getDoubleArray(){
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
    
    /** Get the maximum value of the column */
    public double maxValue() throws IndexOutOfBoundsException {
        return new MaxValueCalculator(this).doubleValue();
    }
    
    /** Get the minimum value of the column */
    public double minValue(){
        return new MinValueCalculator(this).doubleValue();   
    }    

    /** Get an empty copy of this column */
    public Column getEmptyCopy() {
        DoubleColumn col = new DoubleColumn();
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
            try {
                return new Double(getDoubleValue(index));
            } catch (Exception e){
                return new Double(Double.NaN);
            }
        }
    }
}
