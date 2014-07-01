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

package org.pipeline.core.data.maths;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.MissingValue;
import org.pipeline.core.data.NumericalColumn;
import org.pipeline.core.data.columns.DoubleColumn;

/**
 * This class scales a column to have unit variance by subtracting its mean
 * and dividing by its standard deviation. It returns a new DoubleColumn with
 * the same name, as the input column containing the scaled values. MissingValues
 * are preserved. If any columns have a zero standard deviation, the mean values
 * will be subtracted.
 * @author hugo
 */
public class UnitVarianceColumnScaler {
    /** Column to be scaled */
    private Column column;
    
    /** Mean value */
    private double mean = 0;
    
    /** Standard deviation */
    private double std = 0;
    
    /** Creates a new instance of UnitVarianceColumnScaler */
    public UnitVarianceColumnScaler(NumericalColumn column) {
        this.column = (Column)column;
    }
    
    /** Get the scaled column */
    public DoubleColumn scaledColumn() throws DataException {
        DoubleColumn result = new DoubleColumn();
        result.setName(column.getName());
        NumericalColumn numberColumn = (NumericalColumn)column;
        
        mean = new MeanValueCalculator(numberColumn).doubleValue();
        std = new StdCalculator(numberColumn).doubleValue();
        
        // Fix the std if it is zero
        if(std==0){
            std = 1;
        }
        
        if(!Double.isNaN(mean) && !Double.isNaN(std)){
            int size = column.getRows();
            for(int i=0;i<size;i++){
                if(!column.isMissing(i)){
                    result.appendDoubleValue((numberColumn.getDoubleValue(i) - mean) / std);
                } else {
                    result.appendObjectValue(MissingValue.get());
                }
            }
            return result;
            
        } else {
            // Can only copy the column. TODO: Change this
            if(column instanceof DoubleColumn){   
                return (DoubleColumn)column.getCopy();
            } else {
                throw new DataException("Cannot scale column");
            }
        }
    }
    
    /** Get the calculated mean */
    public double getMean(){
        return mean;
    }
    
    /** Get the calculated standard deviation */
    public double getStd(){
        return std;
    }
    
    /** Scale a column using an existing set of scaling parameters */
    public static DoubleColumn scaleData(NumericalColumn unscaledColumn, double mean, double std) throws DataException, IndexOutOfBoundsException {
        // Fix the std if it is zero
        if(std==0){
            std = 1;
        }
        
        Column column = (Column)unscaledColumn;
        DoubleColumn result = new DoubleColumn();
        result.setName(column.getName());
        
        if(!Double.isNaN(mean) && !Double.isNaN(std)){
            int size = column.getRows();
            for(int i=0;i<size;i++){
                if(!column.isMissing(i)){
                    result.appendDoubleValue((unscaledColumn.getDoubleValue(i) - mean) / std);
                } else {
                    result.appendObjectValue(MissingValue.get());
                }
            }
            return result;
            
        } else {
            // Can only copy the column. TODO: Change this
            if(column instanceof DoubleColumn){   
                return (DoubleColumn)column.getCopy();
            } else {
                throw new DataException("Cannot scale column");
            }
        }        
    }
}
