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
import org.pipeline.core.data.MissingValue;
import org.pipeline.core.data.NumericalColumn;
import org.pipeline.core.data.columns.DoubleColumn;

/**
 * This class calculates the RMS error between two numerical columns.
 * @author hugo
 */
public class ErrorCalculator {
    /** First data column */
    private NumericalColumn column1;
    
    /** Second data column */
    private NumericalColumn column2;
    
    /** Squared error column */
    private DoubleColumn rmsColumn;
    
    /** RMS Error value */
    private double rmsValue;
    
    /** Construct with two NumericalColumns */
    public ErrorCalculator(NumericalColumn column1, NumericalColumn column2) {
        this.column1 = column1;
        this.column2 = column2;
    }

    /** Calculate the errors */
    public DoubleColumn calculate() throws DataException, IndexOutOfBoundsException {
        int rows = Math.min(column1.getRows(), column2.getRows());
        rmsColumn = new DoubleColumn("SqErr_" + column1.getName());

        // Calculate squared errors
        for(int j=0;j<rows;j++){
            if(!column1.isMissing(j) && !column2.isMissing(j)){
                rmsColumn.appendDoubleValue(Math.sqrt(Math.pow(column1.getDoubleValue(j) - column2.getDoubleValue(j), 2)));
            } else {
                rmsColumn.appendObjectValue(MissingValue.get());
            }
        }

        // Calculate the rms error
        double mean = new MeanValueCalculator(rmsColumn).doubleValue();
        rmsValue = Math.sqrt(mean);
        return rmsColumn;
    }
    
    /** Get the RMS value */
    public double getRmsValue(){
        return rmsValue;
    }
}
