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
 * This class provides a single column first order filter.
 * @author hugo
 */
public class FirstOrderColumnFilter {
    /** Original data */
    private NumericalColumn originalColumn;
    
    /** Filtered data */
    private NumericalColumn filteredColumn;
    
    /** Filter parameter */
    private double alpha = 0.7;
    
    /** Creates a new instance of FirstOrderColumnFilter */
    public FirstOrderColumnFilter() {
    }

    /** Get the filter alpha */
    public double getAlpha(){
        return alpha;
    }
            
    /** Set the filter alpha */
    public void setAlpha(double alpha){
        if(alpha>=0 && alpha<=1){
            this.alpha = alpha;
        }
    }
    
    /** Filter a column */
    public NumericalColumn filterData(NumericalColumn originalColumn) throws DataException, IndexOutOfBoundsException {
        this.originalColumn = originalColumn;
        Column c = (Column)originalColumn;
        filteredColumn = (NumericalColumn)((Column)originalColumn).getEmptyCopy();
        
        if(c instanceof NumericalColumn){
            int rows = c.getRows();
            
            if(rows>1){
                boolean foundFirst = false;
                double last = 0;
                double next = 0;
                
                for(int i=0;i<rows;i++){
                    if(!c.isMissing(i)){
                        if(!foundFirst){
                            // This is the first value
                            foundFirst = true;
                            filteredColumn.appendDoubleValue(originalColumn.getDoubleValue(i));
                            last = originalColumn.getDoubleValue(i);
                            
                        } else {
                            // Normal value
                            next = (alpha * last) + ((1-alpha)*originalColumn.getDoubleValue(i));
                            filteredColumn.appendDoubleValue(next);
                            last = next;  
                        }
                    }
                }
                
            } else {
                filteredColumn.appendDoubleValue(originalColumn.getDoubleValue(0));
            }
            
            return filteredColumn;
            
        } else {
            throw new DataException("Filters can only operate on numerical columns");
        }                
    }
    
    /** Get the original column */
    public NumericalColumn getOriginalColumn(){
        return originalColumn;
    }
    
    /** Get the filtered column */
    public NumericalColumn getFilteredColumn(){
        return filteredColumn;
    }
}
