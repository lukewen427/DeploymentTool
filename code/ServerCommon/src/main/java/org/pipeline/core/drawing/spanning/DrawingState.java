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

import java.util.Enumeration;
import java.util.Vector;
// </editor-fold>

/**
 * This class keeps track of the state of a Drawing during the calculation of an
 * execution span.
 * @author  hugo
 */
public class DrawingState {
    /** Drawing model being processed */
    private DrawingModel drawingModel = null;

    /** Collection of states for individual blocks */
    private Vector blocks = new Vector();
    
    /** Creates a new instance of DrawingState */
    public DrawingState(DrawingModel drawingModel) {
        this.drawingModel = drawingModel;
        createBlockStates();
    }
    
    /** Create state objects for all of the drawing blocks */
    public void createBlockStates(){
        blocks.clear();
        if(drawingModel!=null){
            Enumeration e = drawingModel.blocks();
            while(e.hasMoreElements()){
                blocks.addElement(new BlockState((BlockModel)e.nextElement(), this));
            }
        }
    }
    
    /** Refresh the block states */
    public void refreshBlockStates(){
        if(drawingModel!=null){
            // Add new block states
            Enumeration e = drawingModel.blocks();
            BlockModel block;
            while(e.hasMoreElements()){
                block = (BlockModel)e.nextElement();
                if(findBlockState(block)==null){
                    blocks.addElement(new BlockState(block, this));
                } else {
                    // Refresh IO states on existing state
                    findBlockState(block).refreshIOStates();
                }
            }
            
            // Remove any block states that no longer exist in the drawing
            Vector drawingBlocks = new Vector();
            e = drawingModel.blocks();
            while(e.hasMoreElements()){
                drawingBlocks.addElement(e.nextElement());
            }
            
            e = blocks.elements();
            BlockState state;
            while(e.hasMoreElements()){
                state = (BlockState)e.nextElement();
                if(!drawingBlocks.contains(state.getBlock())){
                    blocks.remove(state);
                }
            }
        }
    }
    
    /** Find the block state that corresponds to a specific actual block */
    public BlockState findBlockState(BlockModel block){
        Enumeration e = blocks.elements();
        BlockState state;
        while(e.hasMoreElements()){
            state = (BlockState)e.nextElement();
            if(state.getBlock().equals(block)){
                return state;
            }
        }
        return null;
    }
    
    /** Reset states of all blocks */
    public void reset(){
        Enumeration e = blocks.elements();
        while(e.hasMoreElements()){
            ((BlockState)e.nextElement()).reset();
        }
    }
    
    /** Reset all inputs along a path */
    public void resetPath(BlockState startingPoint){
        
    }
    
    /** Get all of the block states */
    public Enumeration blockStates(){
        return blocks.elements();
    }
    
    /** Get the drawing model */
    public DrawingModel getDrawingModel(){
        return drawingModel;
    }
}
