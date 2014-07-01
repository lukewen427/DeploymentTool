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
import org.pipeline.core.data.ColumnMetaData;
import org.pipeline.core.data.DataMetaData;

/**
 * This class writes a DataMetaData object to a JSON object
 * @author hugo
 */
public class JsonMetaDataExporter {
    /** Metadata to export */
    private DataMetaData metaData;

    public JsonMetaDataExporter(DataMetaData metaData) {
        this.metaData = metaData;
    }
    
    public JSONObject toJson() throws DataExportException {
        try {
            JSONObject mdJson = new JSONObject();
            JSONArray columns = new JSONArray();
            JSONObject columnJson;
            ColumnMetaData col;
            for(int i=0;i<metaData.getColumns();i++){
                col = metaData.column(i);
                columnJson = new JSONObject();
                columnJson.put("name", col.getName());
                columnJson.put("type", col.getColumnTypeId());
                columns.put(columnJson);
            }
            mdJson.put("columns", columns);
            return mdJson;
        } catch (Exception e){
            throw new DataExportException("Error creating metadata json: " + e.getMessage(), e);
        }
    }
}