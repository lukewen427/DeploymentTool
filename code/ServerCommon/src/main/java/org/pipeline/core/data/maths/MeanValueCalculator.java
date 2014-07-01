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
import org.pipeline.core.data.NumericalColumn;

/**
 * Calculates the average value of a numerical column
 *
 * @author hugo
 */
public class MeanValueCalculator {

    /**
     * Column to calculate
     */
    private Column column;

    /**
     * Creates a new instance of MeanValueCalculator
     */
    public MeanValueCalculator(NumericalColumn column) {
        this.column = (Column) column;
    }

    /**
     * Calculate mean of a NumericalColumn and return it as a double value.
     * 
     * Missing values are excluded from the calculation.
     *
     * @return the mean of the column values
     *
     * Fixed to remove precision drift, remove unnecessary divides, loops and calls to isMissing() (dom 25/2/14)
     *
     */
    public double doubleValue() {

        int count = 0;

        double runSum = 0.0, currVal;
        NumericalColumn numberCol = (NumericalColumn) column;

        int size = column.getRows();

        for (int i = 0; i < size; i++) {

            try {
                currVal = numberCol.getDoubleValue(i);
                runSum = runSum + currVal;
                count++;
            } catch (DataException e) {
                //thrown if i val is 'missing' 
            }
        }
     
            return (runSum / count);
       
    }

    /**
     * Integer value
     */
    public int intValue() {
        return (int) doubleValue();
    }

    /**
     * Long value
     */
    public long longValue() {
        return (long) doubleValue();
    }
}
