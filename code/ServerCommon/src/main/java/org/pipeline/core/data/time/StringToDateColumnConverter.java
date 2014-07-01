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

package org.pipeline.core.data.time;

import org.pipeline.core.data.DataException;
import org.pipeline.core.data.columns.DateColumn;
import org.pipeline.core.data.columns.StringColumn;

import java.text.SimpleDateFormat;

/**
 * This column takes a text column and converts it to a date
 * type column.
 * @author hugo
 */
public class StringToDateColumnConverter {
    /** Original String column */
    private StringColumn original;
    
    /** Newly created Date column */
    private DateColumn dateColumn;
    
    /** Parse format */
    private SimpleDateFormat format = new SimpleDateFormat();

    /** Text pattern */
    private String pattern = null;
    
    /** Creates a new instance of StringToDateColumnConverter */
    public StringToDateColumnConverter(StringColumn original) {
        this.original = original;
        
    }
    
    /** Set the format. The format complies with the standard
     * java date formatting rules */
    public void setDateFormat(String pattern) throws IllegalArgumentException {
        try {
            format.applyPattern(pattern);
            this.pattern = pattern;
        } catch (NullPointerException e){
        }
    }
    
    /** Convert to a DateColumn */
    public DateColumn convertToDateColumn() throws DataException {
        dateColumn = new DateColumn(original.getRows());
        dateColumn.setName(original.getName());
        
        
        
        return dateColumn;
    }
    
    /** Get the date column */
    public DateColumn getDateColumn(){
        if(dateColumn==null){
            try {
                dateColumn = convertToDateColumn();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return dateColumn;
    }
}
