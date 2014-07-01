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
import org.pipeline.core.drawing.PortModel;

import java.util.Enumeration;
import java.util.Vector;
// </editor-fold>

/**
 * This track keeps track of the state of a block during the calculation of a 
 * Drawing span.
 * @author  hugo
 */
public class BlockState {
    
    /** Input port states */
    private Vector inputStates = new Vector();
    
    /** Output port states */
    private Vector outputStates = new Vector();
    
    /** Block represented */
    private BlockModel block = null;
    
    /** Parent drawing state */
    private DrawingState parentDrawingState = null;
    
    /** Creates a new instance of BlockState */
    public BlockState(BlockModel block, DrawingState parentDrawingState) {
        this.block = block;
        this.parentDrawingState = parentDrawingState;
        createIOStates();
    }
    
    /** Get the parent drawing state */
    public DrawingState getParentDrawingState(){
        return parentDrawingState;
    }
    
    /** Get the actual block managed by this block state */
    public BlockModel getBlock(){
        return block;
    }
    
    /** Reset the state of this block */
    public void reset(){

    }
    
    /** Propagate a reset signal along a path */
    public void resetAlongPath(Vector path){
        if(!path.contains(this)){
            reset();
            path.addElement(this);
            
            Enumeration e = outputStates.elements();
            while(e.hasMoreElements()){
                ((PortState)e.nextElement()).resetAlongPath(path);
            }
        }
    }
    
    /** Add to the execution list */
    public void addToExecutionListAlongPath(Vector executionList, Vector path){
        if(!executionList.contains(block) && !path.contains(this)){
            if(getInputCount()==0){
                // If this block has no inputs, then it can always be executed
                path.addElement(this);
                executionList.addElement(block);
                Enumeration e = outputStates.elements();
                while(e.hasMoreElements()){
                    ((PortState)e.nextElement()).addToExecutionListAlongPath(executionList, path);
                }
                
            } else {
                // Need to check that all of the inputs are satisfied
                if(allInputsReady()){
                    path.addElement(this);
                    executionList.addElement(block);
                    Enumeration e = outputStates.elements();
                    while(e.hasMoreElements()){
                        ((PortState)e.nextElement()).addToExecutionListAlongPath(executionList, path);
                    }                    
                }
            }
        }
    }
    
    /** Are all of the inputs in a certain state */
    public boolean allInputsReady(){
        boolean sameState = true;
        Enumeration e = inputStates.elements();
        PortState p;
        while(e.hasMoreElements()){
            p = (PortState)e.nextElement();
            if(p.isOptionalPort()){
                // If there is no connection, then fine
                if(p.findConnectedPorts().size()>0){
                    // Otherwise do the normal check
                    if(p.getCurrentState()!=PortState.PORT_HAS_DATA) {
                        sameState = false;
                    }
                }
                
            } else if(p.getCurrentState()!=PortState.PORT_HAS_DATA) {
                sameState = false;
            }
        }
        return sameState;
    }
    
    /** Set all of the inputs to a specific state */
    public void setInputsToState(int state){
        Enumeration e = inputStates.elements();
        while(e.hasMoreElements()){
            ((PortState)e.nextElement()).setCurrentState(state);
        }        
    }
    
    /** Set all of the outputs to a specifc state */
    public void setOutputsToState(int state){
        Enumeration e = outputStates.elements();
        while(e.hasMoreElements()){
            ((PortState)e.nextElement()).setCurrentState(state);
        }        
    }
    
    /** Get the number of inputs */
    public int getInputCount(){
        return inputStates.size();
    }
    
    /** Get the number of outputs */
    public int getOutputCount(){
        return outputStates.size();
    }
    
    /** Return the port state that represents an actual input port model */
    public PortState getInputState(PortModel model){
        Enumeration e = inputStates.elements();
        PortState state;
        while(e.hasMoreElements()){
            state = (PortState)e.nextElement();
            if(state.getPort().equals(model)){
                return state;
            }
        }
        return null;
    }
    
    /** Return the port state that represents an actual output port model */
    public PortState getOutputState(PortModel model){
        Enumeration e = outputStates.elements();
        PortState state;
        while(e.hasMoreElements()){
            state = (PortState)e.nextElement();
            if(state.getPort().equals(model)){
                return state;
            }
        }
        return null;
    }
    
    /** Refresh the IO states */
    public void refreshIOStates(){
        if(block!=null){
            // Inputs
            Enumeration e = block.inputs();
            PortModel port;
            while(e.hasMoreElements()){
                port = (PortModel)e.nextElement();
                if(getInputState(port)==null){
                    inputStates.addElement(new PortState(port, this));
                }
            }
            
            // Clean up any input ports that have been removed
            Vector inputPorts = new Vector();
            e = block.inputs();
            while(e.hasMoreElements()){
                inputPorts.addElement(e.nextElement());
            }
            
            e = inputStates.elements();
            PortState state;
            while(e.hasMoreElements()){
                state = (PortState)e.nextElement();
                if(!inputPorts.contains(state.getPort())){
                    inputStates.remove(state);
                }
            }
            
            // Outputs
            e = block.outputs();
            while(e.hasMoreElements()){
                port = (PortModel)e.nextElement();
                if(getOutputState(port)==null){
                    outputStates.addElement(new PortState(port, this));
                }
            }
            
            // Clean up any output ports that have been removed
            Vector outputPorts = new Vector();
            e = block.outputs();
            while(e.hasMoreElements()){
                outputPorts.addElement(e.nextElement());
            }
            
            e = outputStates.elements();
            while(e.hasMoreElements()){
                state = (PortState)e.nextElement();
                if(!outputPorts.contains(state.getPort())){
                    outputStates.remove(state);
                }
            }
        }
    }
    
    /** Create IO states */
    private void createIOStates(){
        if(block!=null){
            // Inputs
            Enumeration e = block.inputs();
            inputStates.clear();
            while(e.hasMoreElements()){
                inputStates.addElement(new PortState((PortModel)e.nextElement(), this));
            }
            
            // Outputs
            e = block.outputs();
            outputStates.clear();
            while(e.hasMoreElements()){
                outputStates.addElement(new PortState((PortModel)e.nextElement(), this));
            }
        }
    }
}
