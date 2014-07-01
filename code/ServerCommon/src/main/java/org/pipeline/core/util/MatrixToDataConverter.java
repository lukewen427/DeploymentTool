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

package org.pipeline.core.util;

import org.pipeline.core.data.Data;
import org.pipeline.core.data.columns.DoubleColumn;
import org.pipeline.core.matrix.Matrix;

/**
 * This classs converts a matrix to a set of data. Use sparingly, it copies
 * data and isn't that efficient.
 * @author hugo
 */
public class MatrixToDataConverter {
    
    /** Matrix to convert */
    private Matrix matrix;
    
    /** Creates a new instance of MatrixToDataConterter */
    public MatrixToDataConverter(Matrix matrix) {
        this.matrix = matrix;
    }
    
    /** Convert to a set of data */
    public Data toData() {
        try {
            Data data = new Data();
            int rows = matrix.getRowDimension();
            int cols = matrix.getColumnDimension();
            
            DoubleColumn c;
            
            for(int i=0;i<cols;i++){
                c = new DoubleColumn(rows);
                c.setName("c" + i);
                for(int j=0;j<rows;j++){
                    c.appendDoubleValue(matrix.get(j, i));
                }
                data.addColumn(c);
            }
            
            return data;
            
        } catch (Exception e){
            return new Data();
        }
    }
}
