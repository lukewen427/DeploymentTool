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

import java.awt.*;

/**
 * This class provides information for a specific column type. It
 * is used to register the various column types with the ColumnFactory.
 * @author hugo
 */
public class ColumnTypeInfo {
    /** Column Type label */
    private String label;
    
    /** Column type id */
    private String id;
    
    /** Column data type class */
    private Class dataType;
    
    /** Class representing the column itself */
    private Class columnRepresentationClass;
    
    /** Foreground table text colour */
    private Color foregroundColor = Color.BLACK;
    
    /** Background table text colour */
    private Color backgroundColor = Color.WHITE;
    
    /** Creates a new instance of ColumnTypeInfo */
    public ColumnTypeInfo(String id, String label, Class dataType, Class columnRepresentationClass) {
        this.id = id;
        this.label = label;
        this.dataType = dataType;
        this.columnRepresentationClass = columnRepresentationClass;
    }

    /** Creates a new instance of ColumnTypeInfo */
    public ColumnTypeInfo(String id, String label, Class dataType, Class columnRepresentationClass, Color foregroundColor, Color backgroundColor) {
        this.id = id;
        this.label = label;
        this.dataType = dataType;
        this.columnRepresentationClass = columnRepresentationClass;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
    }
    
    /** Get the display label for this column class */
    public String getLabel() {
        return label;
    }

    /** Get the id value for this column class */
    public String getId() {
        return id;
    }

    /** Get the class of the data that this type of column can store */
    public Class getDataType() {
        return dataType;
    }

    /** Get the class of the column implementation that the ColumnFactory will create */
    public Class getColumnRepresentationClass() {
        return columnRepresentationClass;
    }
    
    /** Override the toString method to return the column label */
    public String toString(){
        return label;
    }
    
    /** Get the foreground colour for rendering */
    public Color getForegroundColor(){
        return foregroundColor;
    }
    
    /** Get the background colour for rendering */
    public Color getBackgroundColor(){
        return backgroundColor;
    }
}

