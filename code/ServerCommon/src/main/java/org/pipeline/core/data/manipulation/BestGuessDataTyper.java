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

import org.pipeline.core.data.Column;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.MissingValue;
import org.pipeline.core.data.columns.DoubleColumn;
import org.pipeline.core.data.columns.StringColumn;
import org.pipeline.core.xmlstorage.XmlDataStore;
/**
 * This class makes a best guess at data types for columns. It assumes all the
 * original columns are text columns then tries to interpret each value as a
 * Double. If the majority of the non-missing values can be interpreted, then
 * a column will be set to Double and values appended accordingly. Otherwise,
 * the column is left as a text column.
 * @author hugo
 */
public class BestGuessDataTyper {
    /** Data being re-typed */
    private Data data;

    public BestGuessDataTyper(Data data) {
        this.data = data;
    }
    
    /** Guess a column type */
    private Column guessColumn(Column original) throws DataException{
        int missingCount = 0;
        int numberCount = 0;
        int nonNumberCount = 0;
        
        int rows = original.getRows();
        for(int i=0;i<rows;i++){
            if(!original.isMissing(i)){
                if(!original.getStringValue(i).trim().equalsIgnoreCase("")){
                    try {
                        Double.parseDouble(original.getStringValue(i));
                        numberCount++;
                    } catch (Exception e){
                        nonNumberCount++;
                    }
                } else {
                    missingCount++;
                }
                
            } else {
                missingCount++;
            }
        }

        Column newColumn;
        if(numberCount>0 && numberCount>nonNumberCount){
            // Numerical column
            newColumn = new DoubleColumn(original.getName());
        } else {
            // Text column
            newColumn = new StringColumn(original.getName());
        }
        
        for(int i=0;i<rows;i++){
            if (!original.isMissing(i)) {
                try {
                    newColumn.appendStringValue(original.getStringValue(i));
                } catch (Exception e) {
                    newColumn.appendObjectValue(MissingValue.get());
                }
            } else {
                newColumn.appendObjectValue(MissingValue.get());
            }
        }

        return newColumn;        
    }
    
    /** Return a new set of data */
    public Data guess() throws DataException {
        Data newData = new Data();

        for(int i=0;i<data.getColumns();i++){
            newData.addColumn(guessColumn(data.column(i)));
        }
        
        if(data.hasIndexColumn()){
            newData.setIndexColumn(data.getIndexColumn().getCopy());
        }
        if(data.hasProperties()){
           //dps - got rid of catch all here 27/3/14
                newData.setProperties((XmlDataStore)data.getProperties().getCopy());
          
        }
        return newData;
    }
}
