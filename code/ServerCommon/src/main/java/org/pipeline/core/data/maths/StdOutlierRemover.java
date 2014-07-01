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
import org.pipeline.core.data.columns.IntegerColumn;

/**
 * This class screens a column for outliers using the n x standard deviation
 * criteria.
 * @author hugo
 */
public class StdOutlierRemover {
    /** Original column */
    private NumericalColumn originalColumn = null;
    
    /** Screened column */
    private NumericalColumn screenedColumn = null;
    
    /** Standard deviation to screen for */
    private double std = 3;
    
    /** Column mean */
    private double columnMean = 0;
    
    /** Column standard deviation */
    private double columnStd = 0;
    
    /** Index of missing values found */
    private IntegerColumn outlierIndex;
    
    /** Creates a new instance of StdOutlierRemover */
    public StdOutlierRemover(NumericalColumn originalColumn) {
        this.originalColumn = originalColumn;
    }
    
    /** Creates a new instance of StdOutlierRemover */
    public StdOutlierRemover() {
    }

    /** Set the standard deviation to screen for */
    public void setStd(double std){
        this.std = std;
    }
    
    /** Get the standard deviation to screen for */
    public double getStd(){
        return std;
    }
    
    /** Screen the column already present in this filter */
    public NumericalColumn screenColumn() throws DataException, IndexOutOfBoundsException {
        return screenColumn(originalColumn);
    }
    
    /** Screen the column */
    public NumericalColumn screenColumn(NumericalColumn columnToScreen) throws DataException, IndexOutOfBoundsException {
        this.originalColumn = columnToScreen;
        screenedColumn = (NumericalColumn)((Column)columnToScreen).getEmptyCopy();
        columnMean = new MeanValueCalculator(columnToScreen).doubleValue();
        columnStd = new StdCalculator(columnToScreen).doubleValue();
        outlierIndex = new IntegerColumn();
        int size = ((Column)columnToScreen).getRows();
        double value;
        double replacement;
        double lastNonMissing = columnMean;
        
        // Screen data
        for(int i=0;i<size;i++){
            if(!((Column)columnToScreen).isMissing(i)){
                value = columnToScreen.getDoubleValue(i);
                if(Math.abs(value - columnMean) > (std * columnStd)){
                    // Outlier
                    outlierIndex.appendIntValue(i);
                    
                    // Replace with the last non-missing value
                    screenedColumn.appendDoubleValue(lastNonMissing);
                    
                } else {
                    // Not an outlier
                    lastNonMissing = value;
                    screenedColumn.appendDoubleValue(value);
                }
                
            } else {
                // Missing value
                ((Column)screenedColumn).appendObjectValue(MissingValue.get());
            }
        }
        return screenedColumn;
    }
    
    /** Get the original column */
    public NumericalColumn getOriginalColumn(){
        return originalColumn;
    }
        
    /** Get the screened column */
    public NumericalColumn getScreenedColumn(){
        return screenedColumn;
    }
    
    /** Return the outlier index column */
    public IntegerColumn getOutlierIndex(){
        return outlierIndex;
    }
}
