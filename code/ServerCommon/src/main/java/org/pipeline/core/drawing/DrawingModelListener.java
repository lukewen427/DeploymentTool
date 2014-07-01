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
 * Classes that implement this interface DrawingModelListenernts from a DrawingModel.
 * @author  hugo
 */
public interface DrawingModelListener {
    /** A block has been selected */
    public void objectSelected(DrawingModelEvent e);
    
    /** A block has been unselected */
    public void objectUnSelected(DrawingModelEvent e);
    
    /** Request editing for a block */
    public void editRequest(DrawingModelEvent e);
    
    /** Request execution from a block */
    public void executeRequest(DrawingModelEvent e) throws DrawingException;
    
    /** Request execution from a block in an exising Thread */
    public void inThreadExecuteRequest(DrawingModelEvent e) throws DrawingException;
    
    /** A block has been added */
    public void blockAdded(DrawingModelEvent e);
    
    /** A block has been removed */
    public void blockRemoved(DrawingModelEvent e);  
    
    /** A connection has been added */
    public void connectionAdded(DrawingModelEvent e);
    
    /** A connection has been removed */
    public void connectionRemoved(DrawingModelEvent e);
    
    /** Drawing wants a re-draw */
    public void redrawRequested(DrawingModelEvent e);
    
    /** Drawing wants to send a signal */
    public void signalRequested(DrawingModelEvent e);
}
