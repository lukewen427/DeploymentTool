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
import org.pipeline.core.data.columns.DoubleColumn;
import org.pipeline.core.data.columns.StringColumn;

import java.util.Enumeration;
/**
 * This class transposes a set of data
 * @author hugo
 */
public class DataTransposer {
    /** Column to extract and use for column names in the transposed data */
    private ColumnPicker namesPicker;
    
    /** Should a column be used as column names */
    private boolean extractNamesColumn = false;
    
    /** Should the original column names be added as a new column */
    private boolean includeOriginalNames = true;
    
    /** Data to transpose */
    private Data rawData;
    
    /** Transposed data */
    private Data transposedData;

    /** Name for the column names column */
    private String columnNamesColumnName = "Column Names";
    
    /** Creates a new instance of DataTransposer */
    public DataTransposer(Data rawData) {
        this.rawData = rawData;
    }
    

    /** Transpose the data */
    public Data transpose() throws DataException {
        try {
            namesPicker.setCopyData(false);
            Data data;
            Column namesColumn = null;

            if(extractNamesColumn){
                data = new Data();
                namesColumn = namesPicker.getColumnReference(rawData);
                for(int i=0;i<rawData.getColumns();i++){
                    if(!rawData.column(i).equals(namesColumn)){
                        data.addColumn(rawData.column(i));
                    }
                }
                
            } else {
                data = rawData;
            }
            
            // Create the correct number of columns in the transposed data
            transposedData = new Data();
            int columns = data.getLargestRows();
            int rows = data.getColumns();
            
            // Make a text column with the original column names and add it as the first column
            StringColumn oldNames = new StringColumn();
            oldNames.setName(columnNamesColumnName);
            Enumeration e = data.columns();
            while(e.hasMoreElements()){
                oldNames.appendStringValue(((Column)e.nextElement()).getName());
            }
            
            // Test to see if all of the data is numerical
            if(data.isAllNumerical()){
                // Can create DoubleColumns for the data
                DoubleColumn newColumn;
                
                // Create columns
                for(int i=0;i<columns;i++){
                    newColumn = new DoubleColumn();
                    
                    if(extractNamesColumn==true && namesColumn!=null){
                        if(i<namesColumn.getRows() && !namesColumn.isMissing(i)){
                            newColumn.setName(namesColumn.getStringValue(i));
                        } else {
                            newColumn.setName("Column " + i);
                        }
                    } else {
                        newColumn.setName("Column " + i);
                    }
                    transposedData.addColumn(newColumn);
                }
                
                // Do the transposing
                DoubleColumn column;
                for(int i=0;i<transposedData.getColumns();i++){
                    column = (DoubleColumn)transposedData.column(i);
                    for(int j=0;j<rows;j++){
                        //column.appendObjectValue(data.column(j).copyObjectValue(i));
                        if(!data.column(j).isMissing(i)){
                            column.appendDoubleValue(((NumericalColumn)data.column(j)).getDoubleValue(i));
                        } else {
                            column.appendObjectValue(MissingValue.get());
                        }
                    }
                }                
                
            } else {
                // Everything gets done as text
                StringColumn newColumn;
                
                // Create columns
                for(int i=0;i<columns;i++){
                    newColumn = new StringColumn();
                    
                    if(extractNamesColumn==true && namesColumn!=null){
                        if(i<namesColumn.getRows() && !namesColumn.isMissing(i)){
                            newColumn.setName(namesColumn.getStringValue(i));
                        } else {
                            newColumn.setName("Column " + i);
                        }
                    } else {
                        newColumn.setName("Column " + i);
                    }
                    transposedData.addColumn(newColumn);
                }
                
                // Do the transposing
                Column column;
                for(int i=0;i<transposedData.getColumns();i++){
                    column = (Column)transposedData.column(i);
                    for(int j=0;j<rows;j++){
                        if(!data.column(j).isMissing(i)){
                            column.appendStringValue(data.column(j).getStringValue(i));
                        } else {
                            column.appendObjectValue(MissingValue.get());
                        }
                    }
                }
            }
            
            if(includeOriginalNames){
                transposedData.insertColumn(0, oldNames);
            }
            
            return transposedData;
            
        } catch (Exception e){
            throw new DataException("Cannot transpose data: " + e.getLocalizedMessage());
        }
    }

    /** Get the column names picker */
    public ColumnPicker getNamesPicker() {
        return namesPicker;
    }

    /** Set the column names picker */
    public void setNamesPicker(ColumnPicker namesPicker) {
        this.namesPicker = namesPicker;
    }

    /** Get whether to extract a names column */
    public boolean getExtractNamesColumn() {
        return extractNamesColumn;
    }

    /** Set whether to extract a names column */
    public void setExtractNamesColumn(boolean extractNamesColumn) {
        this.extractNamesColumn = extractNamesColumn;
    }

    /** Are the original column names included in the transposed data */
    public boolean getIncludeOriginalNames() {
        return includeOriginalNames;
    }

    /** Set whether the original column names included in the transposed data */
    public void setIncludeOriginalNames(boolean includeOriginalNames) {
        this.includeOriginalNames = includeOriginalNames;
    }

    public void setColumnNamesColumnName(String columnNamesColumnName) {
        this.columnNamesColumnName = columnNamesColumnName;
    }

    public String getColumnNamesColumnName() {
        return columnNamesColumnName;
    }


}
