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

package org.pipeline.core.data.sql;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.MissingValue;
import org.pipeline.core.data.columns.DateColumn;
import org.pipeline.core.data.columns.DoubleColumn;
import org.pipeline.core.data.columns.IntegerColumn;
import org.pipeline.core.data.columns.StringColumn;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Vector;

/**
 * This class converts a SQL ResultSet into a Data object
 * so that it can be used by Pipeline components
 * @author hugo
 */
public class ResultSetConverter {
    /** ResultSet for this converter to convert */
    private ResultSet results;
    
    /** Creates a new instance of ResultSetConverter */
    public ResultSetConverter(ResultSet results) {
        this.results = results;
    }
    
    /** Creates a new instance of ResultSetConverter */
    public ResultSetConverter() {
        results = null;
    }    
    
    /** Convert a ResultSet to a Data Object */
    public Data convertToData(ResultSet r) throws DataException {
        this.results = r;
        return convertToData();
    }
    
    /** Convert to a Data object */
    public Data convertToData() throws DataException {
        try {
            ResultSetMetaData metaData = results.getMetaData();
            Data data = new Data();
            int type;
            String name;
            Column col;
            Vector usedColumns = new Vector();
            Vector columnTypes = new Vector();
            
            // Create the data
            int columns = metaData.getColumnCount();
            for(int i=1;i<=columns;i++){
                type = metaData.getColumnType(i);
                name = metaData.getColumnName(i);
                
                // Create correct column type
                switch(type){
                    case Types.BIGINT:
                    case Types.INTEGER:
                    case Types.TINYINT:
                        // Integer column
                        usedColumns.addElement(new Integer(i));
                        columnTypes.addElement(new Integer(type));
                        data.addColumn(new IntegerColumn(name));
                        break;
                        
                    case Types.DECIMAL:
                    case Types.DOUBLE:
                    case Types.FLOAT:
                    case Types.REAL:
                        // Double column
                        usedColumns.addElement(new Integer(i));
                        columnTypes.addElement(new Integer(type));
                        data.addColumn(new DoubleColumn(name));
                        break;
                        
                    case Types.DATE:
                    case Types.TIME:
                    case Types.TIMESTAMP:
                        // Time column
                        usedColumns.addElement(new Integer(i));
                        columnTypes.addElement(new Integer(type));
                        data.addColumn(new DateColumn(name));
                        break;
                        
                    case Types.CHAR:
                    case Types.CLOB:
                    case Types.LONGVARCHAR:
                    case Types.VARCHAR:
                        // Text column
                        usedColumns.addElement(new Integer(i));
                        columnTypes.addElement(new Integer(type));
                        data.addColumn(new StringColumn(name));
                        break;
                        
                    default:
                        break;
                            
                }
            }
            
            // Populate the data set from the Results Set
            int size = usedColumns.size();
            int colIndex;
            long length;
            
            while(results.next()){
                for (int i=0;i<size;i++){
                    colIndex = ((Integer)usedColumns.elementAt(i)).intValue();
                    type = ((Integer)columnTypes.elementAt(i)).intValue();
                    col = data.column(i);
                    
                    switch(type){
                        case Types.BIGINT:
                        case Types.INTEGER:
                        case Types.TINYINT:
                            // Integer column
                            long r = results.getLong(colIndex);
                            if(!results.wasNull()){
                                ((IntegerColumn)col).appendLongValue(r);
                            } else {
                                col.appendObjectValue(MissingValue.get());
                            }
                            break;
                            
                        case Types.DECIMAL:
                        case Types.DOUBLE:
                        case Types.FLOAT:
                        case Types.REAL:
                            // Double column
                            double d = results.getDouble(colIndex);
                            if(!results.wasNull()){
                                ((DoubleColumn)col).appendDoubleValue(d);
                            } else {
                                col.appendObjectValue(MissingValue.get());
                            }
                            break;
                            
                        case Types.DATE:
                        case Types.TIME:
                        case Types.TIMESTAMP:
                            // Date column
                            java.util.Date dt = new java.util.Date(results.getDate(colIndex).getTime());
                            if(dt!=null){
                                ((DateColumn)col).appendDateValue(dt);
                            } else {
                                col.appendObjectValue(MissingValue.get());
                            }
                            break;
                            
                        case Types.CHAR:
                        case Types.LONGVARCHAR:
                        case Types.VARCHAR:
                            // Text column
                            String s = results.getString(colIndex);
                            if(s!=null){
                                ((StringColumn)col).appendStringValue(s);
                            } else {
                                col.appendObjectValue(MissingValue.get());
                            }
                            break;
                            
                        case Types.CLOB:
                            length = results.getClob(colIndex).length();
                            if(!results.wasNull()){
                                 ((StringColumn)col).appendStringValue(results.getClob(colIndex).getSubString(1, (int)length));
                            } else {
                                col.appendObjectValue(MissingValue.get());
                            }
                            break;
                            
                        default:
                            break;
                    }
                }
            }
            
            return data;
        } catch (Exception e){
            throw new DataException(MissingValue.MISSING_VALUE_MESSAGE +": " + e.getMessage());
        }
        
    }
}
