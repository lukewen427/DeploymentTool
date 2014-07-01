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

package org.pipeline.core.data.text;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.MissingValue;
import org.pipeline.core.data.columns.StringColumn;

/**
 * This class joins two columns of data together to produce a new text column
 * @author nhgh
 */
public class TextColumnJoiner {
    /** Left hand column */
    private Column leftColumn;

    /** Right hand column */
    private Column rightColumn;

    public TextColumnJoiner(Column leftColumn, Column rightColumn) {
        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
    }

    public StringColumn joinColumns(boolean includeDelimiter, String delimiterText, String newName) throws DataException, IndexOutOfBoundsException {
        StringColumn results = new StringColumn(newName);
        int rows;
        if(leftColumn.getRows()<rightColumn.getRows()){
            rows = leftColumn.getRows();
        } else {
            rows = rightColumn.getRows();
        }
        String value;
        for(int i=0;i<rows;i++){

            if(leftColumn.isMissing(i)==false && rightColumn.isMissing(i)==false){
                if(includeDelimiter){
                    value = leftColumn.getStringValue(i) + delimiterText + rightColumn.getStringValue(i);
                } else {
                    value = leftColumn.getStringValue(i) + rightColumn.getStringValue(i);
                }
                results.appendStringValue(value);
            } else {
                results.appendObjectValue(MissingValue.get());
            }
        }
        return results;
    }
}
