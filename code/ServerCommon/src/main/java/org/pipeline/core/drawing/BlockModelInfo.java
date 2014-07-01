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

import java.awt.*;
/**
 * This class contains information regarding a certain block type. This includes
 * name, description, icon, editor class and renderer class.
 * @author  hugo
 */
public abstract class BlockModelInfo {
    /** Block class */
    private Class blockClass = null;
    
    /** Block icon */
    private Image icon = null;
    
    /** Block name. This will be used on block palettes */
    private String name = "";
    
    /** Block ID. This will be used when creating blocks */
    private String id = "";
    
    /** Block description */
    private String description = "";
    
    /** Block category text */
    private String category = "Default";
       
    /** Creates a new instance of BlockModelInfo */
    public BlockModelInfo(String id, String name, Class blockClass) {
        this.blockClass = blockClass;
        this.name = name;
        this.id = id;
    }
    
    /** Get the block id */
    public String getID(){
        return id;
    }
    
    /** Set the block name */
    public void setName(String name){
        this.name = name;
    }
    /** Get the block name */
    public String getName(){
        return name;
    }
    
    /** Get the block description */
    public String getDescription(){
        return description;
    }
    
    /** Set the block description */
    public void setDescription(String description){
        this.description = description;
    }
    
    /** Get the block icon */
    public Image getIcon(){
        return icon;
    }
    
    /** Set the block icon */
    public void setIcon(Image icon){
        this.icon = icon;
    }
    
    /** Set the block category */
    public void setCategory(String category){
        this.category = category;
    }
    
    /** Get the block category */
    public String getCategory(){
        return category;
    }
    
    /** Override toString */
    public String toString(){
        return name;
    }
    
    /** Get the block class */
    public Class getBlockClass(){
        return blockClass;
    }    
}
