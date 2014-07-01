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
import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.MissingValue;
import org.pipeline.core.data.columns.StringColumn;

import java.util.ArrayList;

/**
 * This class splits a column into a set of text columns based on a piece of 
 * delimiting text.
 * @author nhgh
 */
public class TextColumnSplitter {
    /** Delimiter text */
    private String delimiter = ",";
    
    /** Creates a new instance of TextColumnSplitter */
    public TextColumnSplitter() {
    }
    
    /** Get the delimiter text */
    public String getDelimiter(){
        return delimiter;
    }
    
    /** Set the delimiter text */
    public void setDelimiter(String delimiter){
        this.delimiter = delimiter;
    }    
    
    /** Split the column into a set of data */
    public Column[] splitColumn(Column column) throws IndexOutOfBoundsException, DataException {
        Data columnSet = new Data();
        
        // Get the base column
        ArrayList<String> values;
        StringColumn appendColumn;
        
        // Parse the individual rows
        for(int i=0;i<column.getRows();i++){
            if(!column.isMissing(i)){
                values = splitDelimitedTextLine(column.getStringValue(i));

                for(int j=0;j<values.size();j++){
                    appendColumn = getColumn(j, columnSet, column);
                    appendColumn.appendStringValue(values.get(j));
                }
                
                // Make sure that any remaining blank columns are padded
                if(values.size()<columnSet.getColumns()){
                    for(int j=values.size();j<columnSet.getColumns();j++){
                        columnSet.column(j).appendObjectValue(MissingValue.get());
                    }
                }
            } else {
                // Missing - add empty row
                columnSet.addEmptyRows(1);
            }
        }
        return columnSet.getColumnAray();
    }
    
    /** Get a StringColumn to add data to. This creates a new column if required */
    private StringColumn getColumn(int index, Data columnSet, Column originalColumn) throws DataException {
        if(index < columnSet.getColumns()){
            // OK to return a column
            return (StringColumn)columnSet.column(index);
        } else {
            // Create a new column
            StringColumn column = new StringColumn(originalColumn.getName() + "_" + index);
            
            int rows = columnSet.getSmallestRows();
            if(rows>0){
                for(int i=0;i<rows - 1;i++){
                    column.appendObjectValue(MissingValue.get());
                }
            }
            columnSet.addColumn(column);
            return (StringColumn)columnSet.column(index);
        }
    }
    
    /** Split a delimited text string */
    private ArrayList<String> splitDelimitedTextLine(String line) throws DataException {
        boolean withinQuotes = false;
        String temporaryDelimiter = null;
        int pos1 = 0;
        int pos2 = 0;
        String remains;
        ArrayList<String> fragments = new ArrayList<>();
        
        if (delimiter!=null && !delimiter.equalsIgnoreCase("")) {
            while (true) {
                if (withinQuotes) {
                    delimiter = temporaryDelimiter;
                    
                    pos2 = line.indexOf(delimiter, pos1);
                    if (pos2 == -1) {
                        break;
                    }
                    
                    pos1 = pos2 + delimiter.length ();
                    withinQuotes = false;
                }
                
                if (line.length() > pos1 && line.charAt(pos1) == '"') {
                    pos1++;
                    withinQuotes = true;
                    temporaryDelimiter = delimiter;
                    delimiter = "\"";
                }

                // Grab the next token
                pos2 = line.indexOf(delimiter, pos1);

                if (pos2!=-1) {
                    // Add a standard fragment to the fragments vector
                    fragments.add(line.substring(pos1, pos2).trim());
                    pos1 = pos2 + delimiter.length();

                } else {
                    // Handle the last value differently
                    remains = line.substring(pos1, line.length()).trim();
                    if (!remains.equalsIgnoreCase("")) {
                        fragments.add(remains);
                        
                    } else {
                        fragments.add("");
                    }

                // Finished this line of data
                break;
                }
            }
        }
        return fragments;
    }    
    
}
