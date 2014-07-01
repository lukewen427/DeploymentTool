/**
 * e-Science Central
 * Copyright (C) 2008-2013 School of Computing Science, Newcastle University
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package org.pipeline.core.data.io;
import org.pipeline.core.data.*;
import org.pipeline.core.data.columns.*;
import org.json.*;
/**
 * This class exports a Data set to a JSON object
 * @author hugo
 */
public class JsonDataExporter {
    /** Data set to export */
    private Data data;

    public JsonDataExporter(Data data) {
        this.data = data;
    }

    /** Write the data to a JSON object */
    public JSONObject toJson() throws DataExportException {
        try {
            JSONObject result = new JSONObject();
            JSONArray rows = new JSONArray();
            JSONArray colNames = new JSONArray();
            JSONArray colTypes = new JSONArray();
            JSONArray colProperties = new JSONArray();
            int rowCount = data.getLargestRows();
            JSONArray row;

            for(int i=0;i<rowCount;i++){
                row = new JSONArray();
                for(int j=0;j<data.getColumns();j++){
                    if(!data.column(j).isMissing(i)){
                        if(data.column(j) instanceof DoubleColumn){
                            row.put(((DoubleColumn)data.column(j)).getDoubleValue(i));
                            
                        } else if(data.column(j) instanceof IntegerColumn){
                            row.put(((IntegerColumn)data.column(j)).getLongValue(i));
                            
                        } else if(data.column(j) instanceof StringColumn){
                            row.put(((StringColumn)data.column(j)).getStringValue(i));
                             
                        } else if(data.column(j) instanceof DateColumn){
                            row.put(((DateColumn)data.column(j)).getDateValue(i));
                            
                        } else {
                            row.put(data.column(j).getStringValue(i));
                        }
                            
                    } else {
                        row.put(MissingValue.MISSING_VALUE_REPRESENTATION);
                    }
                }
                rows.put(row);
            }

            for(int i=0;i<data.getColumns();i++){
                colNames.put(data.column(i).getName());
                colTypes.put(ColumnFactory.getColumnTypeInfo(data.column(i)).getId());
                if(data.column(i).hasProperties()){
                    colProperties.put(new SimpleJsonPropertiesExporter(data.column(i).getProperties()).toJson());
                } else {
                    colProperties.put(new JSONArray());
                }
            }

            // Add the index column if there is one
            if(data.hasIndexColumn()){
                JSONObject indexJson = new JSONObject();
                indexJson.put("name", "Index");
                indexJson.put("columnType", ColumnFactory.getColumnTypeInfo(data.getIndexColumn()).getId());
                Column indexCol = data.getIndexColumn();
                JSONArray indexData = new JSONArray();
                for(int i=0;i<indexCol.getRows();i++){
                    if(!indexCol.isMissing(i)){
                        if(indexCol instanceof DoubleColumn){
                            indexData.put(((DoubleColumn)indexCol).getDoubleValue(i));
                            
                        } else if(indexCol instanceof IntegerColumn){
                            indexData.put(((IntegerColumn)indexCol).getLongValue(i));
                            
                        } else if(indexCol instanceof StringColumn){
                            indexData.put(((StringColumn)indexCol).getStringValue(i));
                            
                        } else if(indexCol instanceof DateColumn){
                            indexData.put(((DateColumn)indexCol).getDateValue(i));
                            
                        } else {
                            indexData.put(indexCol.getCxDFormatValue(i));
                        }
                    } else {
                        indexData.put(MissingValue.MISSING_VALUE_REPRESENTATION);
                    }
                }
                indexJson.put("data", indexData);
                result.put("indexColumn", indexJson);
            }
            
            result.put("columnNames", colNames);
            result.put("columnTypes", colTypes);
            result.put("columnProperties", colProperties);
            result.put("rows", rows);
            result.put("columns", data.getColumns());
            result.put("rowCount", rows.length());
            
            // Sort the annotations if there are any
            if(data.getAnnotations()!=null){
                result.put("annotations", data.getAnnotations().toJson());
                result.put("hasAnnotations", true);
            } else {
                result.put("hasAnnotations", false);
            }
            
            // Store the properties if there are any
            if(data.hasProperties()){
                result.put("properties", new SimpleJsonPropertiesExporter(data.getProperties()).toJson());
                result.put("hasProperties", true);
            } else {
                result.put("hasProperties", false);
            }
            
            return result;
        } catch (Exception e){
            throw new DataExportException("Error saving data to JSON: " + e.getMessage(), e);
        }
    }
}
