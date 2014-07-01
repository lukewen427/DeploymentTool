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

import org.pipeline.core.data.Column;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.NumericalColumn;
import org.pipeline.core.data.manipulation.NumericalColumnExtractor;
import org.pipeline.core.matrix.Matrix;

import java.util.Vector;
/**
 * This class converts Data objects to Matrix objects by extracting the largest
 * possible matrix of data missing out all of the non numerical columns. Missing
 * values in the Data object will be converted to NaNs in the matrix. Optionally, 
 * rows that have any missing values in them can be ignored, giving a shorter matrix.
 * @author hugo
 */
public class DataToMatrixConverter {
    /** Data being converted */
    private Data data = null;
    
    /** Creates a new instance of DataToMatrixConverter */
    public DataToMatrixConverter(Data data) {
        this.data = data;
    }
    
    /** Convert to matrix */
    public Matrix toMatrix() throws DataException, IndexOutOfBoundsException {
        NumericalColumnExtractor extractor = new NumericalColumnExtractor(data);
        
        Vector columns = extractor.extractColumns();
        int rows = extractor.getShortestNumericalColumnLength();
        int cols = columns.size();
        Matrix m = new Matrix(rows, cols);
        NumericalColumn numerical;
        Column c;
        
        for(int i=0;i<cols;i++){
            numerical = (NumericalColumn)columns.elementAt(i);
            c = (Column)columns.elementAt(i);
            
            for(int j=0;j<rows;j++){
                try {
                    if(!c.isMissing(j)){
                        m.set(j, i, numerical.getDoubleValue(j));
                    } else {
                        m.set(j, i, Double.NaN);
                    }
                } catch (Exception e){
                    m.set(j, i, Double.NaN);
                }   
            }
        }
        
        return m;
    }
    
    /** Convert to a double[][] array */
    public double[][]toDoubleArray() throws DataException, IndexOutOfBoundsException {
        return toMatrix().getArray();
    }
}
