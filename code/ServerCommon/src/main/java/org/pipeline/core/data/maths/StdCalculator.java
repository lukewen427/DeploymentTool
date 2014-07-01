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
 * Calculates the standard deviation of a column
 * @author hugo
 */
public class StdCalculator {
    /** Column to calculate */
    private Column column;
    
    /** Creates a new instance of StdCalculator */
    public StdCalculator(NumericalColumn column) {
        this.column = (Column)column;
    }
    
    /** Calculate the standard deviation */
    public double doubleValue(){
        double ssq = 0;
        NumericalColumn numberCol = (NumericalColumn)column;
        double mean = new MeanValueCalculator(numberCol).doubleValue();
        int count = column.getNonMissingRows();
        int size = column.getRows();
        
        if(!Double.isNaN(mean)){
            if(count > 1){
                for(int i=0;i<size;i++){
                    if(!column.isMissing(i)){
                        try {
                            ssq = ssq + Math.pow(numberCol.getDoubleValue(i) - mean, 2);
                        } catch (Exception e){
                            // Thrown by missing
                        }
                    }
                }
                return Math.sqrt(ssq / (count - 1));
                
            } else {
                // Only one row
                return 0;
            }
            
        } else {
            return Double.NaN;
        }
    }
}
