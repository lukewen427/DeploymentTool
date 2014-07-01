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

import org.pipeline.core.data.DataException;
import org.pipeline.core.data.NumericalColumn;
/**
 * This class calculates the correlation coefficient between two columns
 * @author hugo
 */
public class CorrelationCalculator {
    /** First column for calculation */
    NumericalColumn xColumn;
    
    /** Second column for calculation */
    NumericalColumn yColumn;

    public CorrelationCalculator(NumericalColumn xColumn, NumericalColumn yColumn) {
        this.xColumn = xColumn;
        this.yColumn = yColumn;
    }
    
    public double doubleValue() throws DataException {
        if(xColumn.getRows()==yColumn.getRows()){
            double sumX = 0;
            double sumY = 0;
            double sumXY = 0;
            double sumXSquared = 0;
            double sumYSquared = 0;
            double x;
            double y;
            
            int rows = xColumn.getRows();
            int n = 0;
            
            for(int i=0;i<rows;i++){
                if(!xColumn.isMissing(i) && !yColumn.isMissing(i)){
                    x = xColumn.getDoubleValue(i);
                    y = yColumn.getDoubleValue(i);
                    
                    sumX+=x;
                    sumY+=y;
                    
                    sumXY+=(x*y);
                    sumXSquared+=Math.pow(x, 2);
                    sumYSquared+=Math.pow(y, 2);
                    n++;
                }
            }
            
            if(n>0){
                double numerator = (n * sumXY) - (sumX * sumY);
                double denominator = ((n * sumXSquared) - Math.pow(sumX, 2)) * ((n * sumYSquared) - Math.pow(sumY, 2));
                return numerator / Math.sqrt(denominator);
            } else {
                throw new DataException("No valid data found");
            }
            
        } else {
            throw new DataException("Row counts do not match");
        }
    }
}