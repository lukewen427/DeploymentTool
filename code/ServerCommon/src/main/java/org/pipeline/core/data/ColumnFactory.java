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

package org.pipeline.core.data;

import org.pipeline.core.data.columns.DateColumn;
import org.pipeline.core.data.columns.DoubleColumn;
import org.pipeline.core.data.columns.IntegerColumn;
import org.pipeline.core.data.columns.StringColumn;

import java.awt.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * This class can create columns for specific classes of data and
 * is used so that the data handling routines can be extended with
 * additional column types.
 * @author hugo
 */
public abstract class ColumnFactory
{
    /** Column types */
    private static HashMap<String, ColumnTypeInfo> columnTypes = new HashMap<>();
    
    /** Column class type list */
    private static HashMap<Class<?>, ColumnTypeInfo> columnClassTypes = new HashMap<>();
    
    /** Register default column types */
    static {
        registerColumnType(new ColumnTypeInfo("date-column", "Date", Date.class, DateColumn.class));
        registerColumnType(new ColumnTypeInfo("double-column", "Double", Double.class, DoubleColumn.class));
        registerColumnType(new ColumnTypeInfo("integer-column", "Integer", Long.class, IntegerColumn.class));
        registerColumnType(new ColumnTypeInfo("string-column", "Text", String.class, StringColumn.class, Color.WHITE, Color.BLUE));
    }      
    
    /** Register a column type */
    public static void registerColumnType(ColumnTypeInfo type)
    {
        if (!columnTypes.containsKey(type.getId())) {
            columnTypes.put(type.getId(), type);
            columnClassTypes.put(type.getColumnRepresentationClass(), type);
        }
    }


    // This method has been removed to avoid synchronization and locking 
    // (via Hashtable). Without this method columnTypes and columnClassTypes 
    // become read-only and may be accessed without synchronization at all.
    ///** Remove a column type */
    //public static void unregisterColumnType(String id){
    //    try {
    //        ColumnTypeInfo type = getColumnType(id);
    //        columnClassTypes.remove(type.getColumnRepresentationClass());
    //    } catch (Exception e){
    //    }
    //    columnTypes.remove(id);
    //}


    /** Get a column type */
    public static ColumnTypeInfo getColumnType(String typeId)
    {
        return columnTypes.get(typeId);
    }
    
    /** Get the column type info for a specified representation class */
    public static ColumnTypeInfo getColumnTypeInfo(Class<?> representationClass)
    {
        return columnClassTypes.get(representationClass);
    }
    
    /** Get the column type info for the specified column */
    public static ColumnTypeInfo getColumnTypeInfo(Column column) throws DataException
    {
    	ColumnTypeInfo i = columnClassTypes.get(column.getClass());
    	if (i == null) {
    		throw new DataException("Cannot identify column type of: " + column.getName());
    	}
    	return i;
    }
    
    /** Get the column type info for the specified column */
    public static ColumnTypeInfo getColumnTypeInfo(ColumnMetaData metaData) throws DataException
    {
    	ColumnTypeInfo i = columnClassTypes.get(metaData.getDataType());
    	if (i == null) {
            throw new DataException("Cannot identify column type of: " + metaData.getName());
        }
    	return i;
    }
    
    /** Create a column */
    public static Column createColumn(String id) throws DataException
    {
    	ColumnTypeInfo info = columnTypes.get(id);
    	if (info == null) {
    		throw new DataException("Column type: " + id + " does not exist");
    	}

    	try {
    		Object col = info.getColumnRepresentationClass().newInstance();
    		return (Column)col;
    	} catch (Exception e) {
    		throw new DataException("Cannot create column: " + e.getMessage());
    	}
    }
    
    /** List the column types */
    public static Collection<ColumnTypeInfo> getColumnTypes()
    {
        return columnTypes.values();
    }
    
    /** Create an empty set of data from a set of DataMetaData */
    public static Data createEmptyData(DataMetaData metaData) throws DataException
    {
        Data data = new Data();
        ColumnMetaData[] columns = metaData.columnArray();
        Column column;
        
        // Create the new empty columns
        for(int i=0;i<columns.length;i++){
            column = createColumn(columns[i].getColumnTypeId());
            column.setName(columns[i].getName());
            data.addColumn(column);
        }
        
        return data;
    }

    /** Convert a column from one type to another */
    public static Column convert(Column source, String newType) throws DataException
    {
        Column target = createColumn(newType);
        for(int i=0;i<source.getRows();i++){
            if(!source.isMissing(i)){
                target.appendStringValue(source.getStringValue(i));
            } else {
                target.appendObjectValue(MissingValue.get());
            }
        }
        return target;
    }

    /** Change the data type of a column in a data set */
    public static void changeColumnType(Data data, int index, String newType) throws DataException
    {
        Column newColumn = convert(data.column(index), newType);
        data.replaceColumn(index, newColumn);
    }
}
