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

import org.json.JSONObject;
import org.pipeline.core.data.Column;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.NumericalColumn;

/**
 * This class exports a set of data as a collection of JSON formatted rows
 * @author hugo
 */
public class JsonRowArrayExporter {
    private Data data;

    public JsonRowArrayExporter(Data data) {
        this.data = data;
    }
    
    public String[] toJsonRows() throws Exception {
        int rows = data.getLargestRows();
        int cols = data.getColumns();
        String[] results = new String[rows];
        JSONObject rowJson;
        String name;
        Column c;
        
        for(int i=0;i<rows;i++){
            rowJson = new JSONObject();
            for(int j=0;j<cols;j++){
                c = data.column(j);
                name = c.getName().replace(" ", "_");
                if(!c.isMissing(i)){
                    if(c instanceof NumericalColumn){
                        rowJson.put(name, ((NumericalColumn)c).getDoubleValue(i));
                    } else {
                        rowJson.put(name, c.getCxDFormatValue(i));
                    }
                    
                } else {
                    rowJson.put(name, "");
                }
            }
            results[i] = rowJson.toString();
        }
        return results;
    }
}
