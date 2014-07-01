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
 * This class contains information regarding an Event that has been created
 * by a DrawingModel.
 * @author  hugo
 */
public class DrawingModelEvent {
    /** Drawing causing the Event */
    private DrawingModel drawing = null;
    
    /** Block triggering the Event if there was one */
    private DrawingObject obj = null;
    
    /** Signal if there is one */
    private DrawingSignal signal = null;
    
    /** Creates a new instance of DrawingModelEvent */
    public DrawingModelEvent(DrawingModel drawing, DrawingObject obj) {
        this.obj = obj;
        this.drawing = drawing;
    }
    
    /** Creates a new instance of DrawingModelEvent */
    public DrawingModelEvent(DrawingModel drawing, DrawingObject obj, DrawingSignal signal) {
        this.obj = obj;
        this.drawing = drawing;
        this.signal = signal;
    }
    
    /** Get the drawing */
    public DrawingObject getObject(){
        return obj;
    }
    
    /** Get the block */
    public DrawingModel getDrawing(){
        return drawing;
    }
    
    /** Get the drawing signal */
    public DrawingSignal getSignal(){
        return signal;
    }
}
