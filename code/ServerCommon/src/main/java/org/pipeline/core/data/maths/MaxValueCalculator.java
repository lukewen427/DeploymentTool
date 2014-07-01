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
import org.pipeline.core.data.NumericalColumn;

/**
 * This class calculates the maximum value of a NumericalColumn
 * @author hugo
 */
public class MaxValueCalculator {
    /** Column to calculate */
    private Column column;
    
    /** Index of maximum value */
    private int maxValueIndex = 0;
    
    /** Creates a new instance of MaxValueCalculator */
    public MaxValueCalculator(NumericalColumn column) {
        this.column = (Column)column;
    }
    
    /** Calculate the maximum value */
    public double doubleValue(){
        int size = column.getRows();
        double max = Double.MIN_VALUE;
        int count = 0;
        double v;
        NumericalColumn numberCol = (NumericalColumn)column;
        
        for(int i=0;i<size;i++){
            if(!column.isMissing(i)){
                try {
                    v = numberCol.getDoubleValue(i);
                } catch (Exception e){
                    v = Double.NaN;
                }
                if(v>max){
                    max = v;
                    maxValueIndex = i;
                }
                count++;
            }
        }
        
        if(count>0){
            return max;        
        } else {
            return Double.NaN;
        }
    }
    
    /** Get the index of the maximum value */
    public int getIndexOfMaximumValue(){
        return maxValueIndex;
    }
    
    /** Return the maximum value as an integer */
    public int intValue(){
        return (int)doubleValue();
    }
    
    /** Return the maximum value as a long */
    public long longValue(){
        return (long)doubleValue();
    }
}
