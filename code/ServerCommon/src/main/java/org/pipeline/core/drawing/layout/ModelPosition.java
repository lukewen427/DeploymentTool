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

package org.pipeline.core.drawing.layout;

// <editor-fold defaultstate="collapsed" desc=" Imports ">
import org.pipeline.core.drawing.DrawingObject;
// </editor-fold>

/**
 * Base class for an objects position data
 * @author  hugo
 */
public abstract class ModelPosition {
    /** Drawng object that this position refers to */
    private DrawingObject drawingObject = null;
    
    /** Parent drawing layout */
    private DrawingLayout parentLayout = null;
    
    /** Constructor */
    public ModelPosition(){
    }
    
    /** Notify the parent that the layout has changed */
    public void notifyLayoutChange(){
        if(parentLayout!=null){
            parentLayout.notifyLayoutChange();
        }
    }
    
    /** Get the parent drawing layout */
    public DrawingLayout getParentLayout(){
        return parentLayout;
    }
    
    /** Set the parent drawing layout */
    public void setParentLayout(DrawingLayout parentLayout){
        this.parentLayout = parentLayout;
    }
    
    /** Get the drawing object */
    public DrawingObject getDrawingObject(){
        return drawingObject;
    }
    
    /** Set the drawing object */
    public void setDrawingObject(DrawingObject drawingObject){
        this.drawingObject = drawingObject;
    }
    
    /** Is a point within the bounds of the object */
    public abstract boolean pointWithinObject(int x, int y);
    
    /** Is an object fully contained by these bounds */
    public abstract boolean objectWithinBounds(int x, int y, int width, int height);
    
    /** Move by a specified offset */
    public abstract void moveBy(int xOffset, int yOffset);
    
    /** Get the maximum x co-ordinate */
    public abstract int getMaximumX();
    
    /** Get the maximum y co-ordinate */
    public abstract int getMaximumY();
}
