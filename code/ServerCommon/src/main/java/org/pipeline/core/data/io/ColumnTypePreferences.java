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

package org.pipeline.core.data.io;

import org.pipeline.core.data.Column;

/**
 * This class stores a set of preferred data types for columns. It is used
 * when importing data so that the user can specify the data types of
 * columns if they cannot automatically be determined. N.B. This is currently
 * a placeholder, which doesn't do anything.
 * @author hugo
 */
public class ColumnTypePreferences {
    
    /** Creates a new instance of ColumnTypePreferences */
    public ColumnTypePreferences() {
    }
    
    /** Get the number of preferred column types */
    public int getPreferenceCount(){
        return 0;
    }
    
    /** Create the relevant type of column */
    public Column createColumn(int column){
        return null;
    }
}
