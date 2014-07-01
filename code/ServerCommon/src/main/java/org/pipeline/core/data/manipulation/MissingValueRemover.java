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
package org.pipeline.core.data.manipulation;

import org.pipeline.core.data.*;

/**
 * This class removes rows that contain any missing values from a Data set
 * @author hugo
 */
public class MissingValueRemover {
    Data input;

    private boolean filterNaN = true;
    private boolean filterInf = true;
   
    public MissingValueRemover(Data input) {
        this.input = input;
    }
    
    public Data removeMissingRows() throws DataException {
        Data output = input.getEmptyCopy();        
        boolean missingFound;
        int rows = input.getSmallestRows();
        int cols = input.getColumns();
        Column col;
        double value;
        
        for(int i=0;i<rows;i++){
            missingFound = false;
            for(int j=0;j<cols;j++){
                col = input.column(j);
                if(col instanceof NumericalColumn){
                    if(!col.isMissing(i)){
                        // Other checks for numerical data
                        if(filterNaN || filterInf){
                            value = ((NumericalColumn)col).getDoubleValue(i);
                            if(filterNaN && Double.isNaN(value)){
                                missingFound = true;
                            }

                            if(filterInf && Double.isInfinite(value)){
                                missingFound = true;
                            }
                        }
                    } else {
                        missingFound = true;
                    }
                    
                } else {
                    // Just check for missing values
                    if(col.isMissing(i)){
                        missingFound = true;
                    }
                }
            }
            
            // Add row if no missings found
            if(missingFound==false){
                for(int j=0;j<cols;j++){
                    output.column(j).appendObjectValue(input.column(j).copyObjectValue(i));
                }         
            }               
        }
        return output;
    }
     
}
