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
package com.connexience.server.workflow.engine.data;
import org.pipeline.core.data.*;
import java.io.*;

/**
 * This class contains a serialized row of data
 * @author hugo
 */
public class SerializedRow implements Serializable {
    static final long serialVersionUID = -1830686151688620462L;
    
    Object[] values;

    public SerializedRow() {
    }

    public SerializedRow(Data data, int rowIndex){
        try {
            Column c;
            values = new Object[data.getColumns()];

            for(int i=0;i<data.getColumns();i++){
                c = data.column(i);
                if(rowIndex<c.getRows()){
                    values[i] = c.getObjectValue(rowIndex);
                } else {
                    values[i] = null;
                }
            }
        } catch (Exception e){
            
        }
    }

    public SerializedRow(Object[] values) {
        this.values = values;
    }

    public Object[] getValues(){
        return values;
    }

    public Object getValue(int index){
        return values[index];
    }
}