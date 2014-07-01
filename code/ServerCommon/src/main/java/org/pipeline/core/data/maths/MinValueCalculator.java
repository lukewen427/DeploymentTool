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
 * Calculates the minimum value of a numerical column
 * @author hugo
 */
public class MinValueCalculator {
    /** Column to calculate */
    private Column column;
    
    /** Index of the minimum value */
    private int minValueIndex = 0;
    
    /** Creates a new instance of MinValueCalculator */
    public MinValueCalculator(NumericalColumn column) {
        this.column = (Column)column;
    }
    
    /** Calculate the minimum value */
    public double doubleValue(){
        int size = column.getRows();
        int count = 0;
        double min = Double.MAX_VALUE;
        double v;
        NumericalColumn numberCol = (NumericalColumn)column;
        
        for(int i=0;i<size;i++){
            if(!column.isMissing(i)){
                try {
                    v = numberCol.getDoubleValue(i);

                    if(v<min){
                        min = v;
                        minValueIndex = i;
                    }
                    count++;
                } catch (Exception e){
                    // Thrown by missing
                }
            }
        }
        
        if(count>0){
            return min;        
        } else {
            return Double.NaN;
        }
    }
    
    /** Get the index of the minimum value */
    public int getIndexOfMinimumValue(){
        return minValueIndex;
    }
    
    /** Return the minimum value as an integer */
    public int intValue(){
        return (int)doubleValue();
    }
    
    /** Return the minimum value as a long */
    public long longValue(){
        return (long)doubleValue();
    }    
}
