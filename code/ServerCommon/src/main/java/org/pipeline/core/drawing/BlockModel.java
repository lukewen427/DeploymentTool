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

import java.util.Enumeration;


/**
 * This interface defines the behaviour of a single block on a drawing. The
 * class DefaultBlock implements this interface and is probably the class
 * that should be extended for most blocks.
 * @author  hugo
 */
public interface BlockModel extends DrawingObject {
    /** Get input count */
    public int getInputCount();
    
    /** Get output count */
    public int getOutputCount();
    
    /** Get an Enumeration of inputs */
    public Enumeration inputs();
    
    /** Get an Enumeration of outputs */
    public Enumeration outputs();
    
    /** Get a named input */
    public InputPortModel getInput(String inputName) throws DrawingException;
    
    /** Get a named output */
    public OutputPortModel getOutput(String outputName) throws DrawingException;
    
    /** Execute this block */
    public BlockExecutionReport execute() throws BlockExecutionException;
        
    /** Execute with meta-data instead of real data. Some blocks may not support this */
    //public void executeWithMetaData() throws BlockExecutionException;
    
    /** Does this block support input streaming */
    public boolean supportsInputStreaming();
    
    /** Does this block support output streaming */
    public boolean supportsOutputStreaming();    

    /** Create an editor */
    public BlockModelEditor createEditor();
    
    /** Get the open editor if there is one */
    public BlockModelEditor getEditor();
    
    /** Release the editor */
    public void releaseEditor();
    
    /** Create a renderer */
    public BlockModelRenderer createRenderer(); 
    
    /** Get the blocks unique string identifier */
    public String getBlockGUID();
    
    /** Can this block be edited */
    public boolean isEditable();
    
    /** Set the parent drawing */
    public void setParentDrawing(DrawingModel parentDrawing);
            
    /** Get the parent drawing */
    public DrawingModel getParentDrawing();
    
    /** Get the block name */
    public String getName();
    
    /** Process a DrawingSignal */
    public void processSignal(DrawingSignal signal);
}
