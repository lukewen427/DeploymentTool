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

import org.pipeline.core.drawing.layout.DrawingLayout;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This interface defines the funcionality for a Drawing. The DefaultDrawingModel
 * object in the model package provides the standard implementation of a Drawing,
 * but some applications may choose to provide their own Drawing implementation.
 * @author  hugo
 */
public interface DrawingModel {
    // State constants
    /** Drawing idle */
    public static final int DRAWING_IDLE = 0;
    
    /** Drawing running */
    public static final int DRAWING_RUNNING = 1;
    
    /** Get the current drawing state */
    public int getState();
    
    /** Set the current drawing state */
    public void setState(int currentState);
    
    /** Add a block */
    public void addBlock(BlockModel block) throws DrawingException;
    
    /** Remove a block */
    public void removeBlock(BlockModel block) throws DrawingException;
    
    /** Get an Enumeration of blocks */
    public Enumeration blocks();
    
    /** Get an Enumeration of all the drawing objects */
    public Enumeration objects();
    
    /** Link two ports together */
    public void connectPorts(PortModel source, PortModel destination) throws DrawingException;
    
    /** Unlink a port. This will unlink both ends of the link and destroy
     * the link itself */
    public void disconnectPort(PortModel port) throws DrawingException;
    
    /** Add a block to the selected block list */
    public void addObjectSelection(DrawingObject object) throws DrawingException;
    
    /** Get the selected block */
    public Enumeration getSelectedObjects();
    
    /** Return the number of selected object */
    public int getSelectedObjectCount();
    
    /** Is a block selected */
    public boolean isObjectSelected(DrawingObject object);
    
    /** Unselect all blocks */
    public void clearObjectSelection();
    
    /** Remove a block from the selected block list */
    public void removeObjectSelection(DrawingObject object) throws DrawingException;
    
    /** Add a listener */
    public void addDrawingModelListener(DrawingModelListener listener);
    
    /** Remove a listener */
    public void removeDrawingModelListener(DrawingModelListener listener);
    
    /** Get the layout data */
    public DrawingLayout getDrawingLayout();
    
    /** Request an re-draw */
    public void redrawRequest();
    
    /** Request editing of a block */
    public void editRequest(BlockModel block);
    
    /** Request execution from a block */
    public void executeRequest(BlockModel block) throws DrawingException;
    
    /** Request execution from a block whilst within an existing execution thread. This is used by streaming and 
     * shouldn't be used by user blocks */
    public void inThreadExecuteRequest(BlockModel block) throws DrawingException;
    
    /** Request propagation of a signal through the drawing from a source block */
    public void signalRequest(BlockModel block, DrawingSignal signal);
    
    /** Get a block given its guid */
    public BlockModel getBlock(String blockGUID) throws DrawingException;
    
    /** Delete a collection of drawing objects */
    public void deleteDrawingObjects(Vector objects) throws DrawingException;
    
    /** Get a count of the blocks */
    public int getBlockCount();

    /** Get a block by name if it exists */
    public BlockModel getBlockByName(String blockName);
}
