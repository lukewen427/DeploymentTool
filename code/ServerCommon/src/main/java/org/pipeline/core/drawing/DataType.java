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

package org.pipeline.core.drawing;

/**
 * This class describes a data type that can be passed between blocks.
 * @author  hugo
 */
public class DataType {
    /** Name */
    private String name = "";
    
    /** Description */
    private String description = "";
    
    /** Class of the actual data that will be transferred */
    private Class dataClass = null;
    
    /** Creates a new instance of DataType */
    public DataType(String name, String description, Class dataClass) {
        this.name = name;
        this.description = description;
        this.dataClass = dataClass;
    }
    
    /** Get the name of this data type */
    public String getName(){
        return name;
    }
    
    /** Get the description of this data type */
    public String getDescrption(){
        return description;
    }
    
    /** Get the class of this data type */
    public Class getDataClass(){
        return dataClass;
    }
}
