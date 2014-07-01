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

package org.pipeline.core.drawing.spanning;

// <editor-fold defaultstate="collapsed" desc=" Imports ">
import org.pipeline.core.drawing.BlockModel;
import org.pipeline.core.drawing.DrawingModel;
// </editor-fold>

/**
 * Classes that implement this interface listen to drawing execution
 * events coming from the DrawingExecutionProcessor.
 * @author hugo
 */
public interface DrawingExecutionListener {
    /** Drawing Execution has started */
    public void drawingExecutionStarted(DrawingModel drawing);
    
    /** Drawing Execution has finished */
    public void drawingExecutionFinished(DrawingModel drawing);
    
    /** A block is about to be executed */
    public void blockExecutionStarted(BlockModel block);
    
    /** Block has finished executing */
    public void blockExecutionFinished(BlockModel block);
}
