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

import org.json.JSONArray;
import org.json.JSONObject;
import org.pipeline.core.data.Column;
import org.pipeline.core.data.ColumnFactory;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.MissingValue;

/**
 * Create a set of data from a JSON representation
 * @author hugo
 */
public class JsonDataImporter {
    /** JSON Object being imported */
    private JSONObject dataJson;

    public JsonDataImporter(JSONObject dataJson) {
        this.dataJson = dataJson;
    }

    public Data toData() throws DataImportException {
        try {
            Data data = new Data();
            int columnCount = dataJson.getInt("columns");
            int rowCount = dataJson.getInt("rowCount");
            JSONArray columnNames = dataJson.getJSONArray("columnNames");
            JSONArray columnTypes = dataJson.getJSONArray("columnTypes");
            JSONArray rows = dataJson.getJSONArray("rows");
            JSONArray rowJson;
            Column column;
            String value;

            // Create the columns
            for(int i=0;i<columnCount;i++){
                column = ColumnFactory.createColumn(columnTypes.getString(i));
                column.setName(columnNames.getString(i));
                data.addColumn(column);
            }

            // Enter the data
            for(int i=0;i<rowCount;i++){
                rowJson = rows.getJSONArray(i);
                for(int j=0;j<rowJson.length();j++){
                    value = rowJson.getString(j);
                    if(!value.equals(MissingValue.MISSING_VALUE_REPRESENTATION)){
                        data.column(j).appendStringValue(value);
                    } else {
                        data.column(j).appendObjectValue(MissingValue.get());
                    }
                }
            }

            // Load the index data if there is any
            if(dataJson.has("indexColumn")){
                JSONObject indexColumnJson = dataJson.getJSONObject("indexColumn");
                String columnType = indexColumnJson.getString("columnType");
                String name = indexColumnJson.getString("name");
                Column indexColumn = ColumnFactory.createColumn(columnType);
                indexColumn.setName(name);
                JSONArray indexDataJson = indexColumnJson.getJSONArray("data");
                for(int i=0;i<indexDataJson.length();i++){
                    value = indexDataJson.getString(i);
                    if(!value.equals(MissingValue.MISSING_VALUE_REPRESENTATION)){
                        indexColumn.appendCxDFormatValue(value);
                    } else {
                        indexColumn.appendObjectValue(MissingValue.get());
                    }
                }
                data.setIndexColumn(indexColumn);
            }
            
            // Load the annotations if there are any
            if(dataJson.has("hasAnnotations") && dataJson.getBoolean("hasAnnotations")==true){
                data.getAnnotations().parseJson(dataJson.getJSONObject("annotations"));
            }
            
            // Load the properties if there are any
            if(dataJson.has("hasProperties") && dataJson.getBoolean("hasProperties")==true){
                data.setProperties(new SimpleJsonPropertiesImporter(dataJson.getJSONObject("properties")).parseJson());
            }
            return data;
        } catch (Exception e){
            throw new DataImportException("Error parsing JSON: " + e.getMessage(), e);
        }
    }
}