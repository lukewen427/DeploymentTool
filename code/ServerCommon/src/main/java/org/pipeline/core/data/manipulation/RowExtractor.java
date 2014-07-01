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

import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
/**
 * This class extracts rows of data from a data set
 * @author nhgh
 */
public class RowExtractor {
    /** Data to extract rows from */
    private Data data;

    public RowExtractor(Data data) {
        this.data = data;
    }

    /** Extract data */
    public Data extract(int[] rowIndices) throws DataException, IndexOutOfBoundsException {
        Data results = data.getEmptyCopy();
        for(int i=0;i<rowIndices.length;i++){
            results.appendRows(extract(rowIndices[i]), true);
        }
        return results;
    }
    
    /** Extract data */
    public Data extract(Integer[] rowIndices) throws DataException, IndexOutOfBoundsException {
        Data results = data.getEmptyCopy();
        for(int i=0;i<rowIndices.length;i++){
            results.appendRows(extract(rowIndices[i]), true);
        }
        return results;
    }    

    /** Extract a single row */
    public Data extract(int rowIndex) throws DataException, IndexOutOfBoundsException {
        if(rowIndex>=0 && rowIndex<data.getSmallestRows()){
            Data results = data.getEmptyCopy();
            for(int i=0;i<data.getColumns();i++){
                results.column(i).appendObjectValue(data.column(i).copyObjectValue(rowIndex));
            }
            return results;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
}