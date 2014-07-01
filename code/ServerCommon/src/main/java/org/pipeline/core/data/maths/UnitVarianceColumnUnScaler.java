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
 * This class unscales a unit variance column.
 * @author hugo
 */
public class UnitVarianceColumnUnScaler {
    /** Column to be unscaled */
    private Column scaledColumn;
    
    /** Mean of unscaled data */
    private double mean;
    
    /** Standard deviation of unscaled data */
    private double std;
    
    /** Creates a new instance of UnitVarianceColumnUnScaler */
    public UnitVarianceColumnUnScaler(NumericalColumn column, double mean, double std) {
        this.scaledColumn = (Column)column;
        this.mean = mean;
        this.std = std;
    }
    
    /** Get the unscaled column */
    public DoubleColumn unscaledColumn() throws DataException {
        DoubleColumn unscaledColumn = new DoubleColumn();
        unscaledColumn.setName(scaledColumn.getName());
        NumericalColumn numberColumn = (NumericalColumn)scaledColumn;
        double value;
        int size = scaledColumn.getRows();
        for(int i=0;i<size;i++){
            if(!scaledColumn.isMissing(i)){
                value = numberColumn.getDoubleValue(i);
                unscaledColumn.appendDoubleValue((value * std) + mean);
            } else {
                unscaledColumn.appendObjectValue(MissingValue.get());
            }
        }
        return unscaledColumn;
    }
}
