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
import org.pipeline.core.drawing.ConnectionModel;
import org.pipeline.core.drawing.PortModel;

import java.util.Enumeration;
import java.util.Vector;
import org.pipeline.core.drawing.InputPortModel;
// </editor-fold>

/**
 * This class keeps track of the state of a Port during the calculation of a 
 * Drawing span.
 * @author  hugo
 */
public class PortState {
    // Port state constants
    
    /** Port has no data */
    public static final int PORT_HAS_NO_DATA = 0;
    
    /** Port has data */
    public static final int PORT_HAS_DATA = 1;
    
    /** Port being monitored */
    private PortModel port = null;
       
    /** Current port state */
    private int currentState = PORT_HAS_NO_DATA;
    
    /** Block state containing this port state */
    private BlockState parentBlockState = null;
    
    /** Creates a new instance of PortState */
    public PortState(PortModel port, BlockState parentBlockState) {
        this.port = port;
        this.parentBlockState = parentBlockState;
    }
    
    /** Get the current state */
    public int getCurrentState(){
        return currentState;
    }
    
    /** Set the current state */
    public void setCurrentState(int currentState){
        this.currentState = currentState;
    }
    
    /** Reset the current state */
    public void reset(){
        currentState = PORT_HAS_NO_DATA;
    }
    
    /** Reset along a path */
    public void resetAlongPath(Vector path){
        if(!path.contains(this)){
            reset();
            path.addElement(this);
            
            // If this is an output port, reset
            // all the blocks connected to it
            BlockState blockState;
            if(port.getType()==PortModel.OUTPUT_PORT){
                Enumeration e = findConnectedPorts().elements();
                PortModel connectedPort;
                PortState connectedState;
                BlockModel connectedBlock;
                
                while(e.hasMoreElements()){
                    connectedPort = (PortModel)e.nextElement();
                    connectedBlock = connectedPort.getParentBlock();
                    blockState = parentBlockState.getParentDrawingState().findBlockState(connectedBlock);
                    blockState.getInputState(connectedPort).reset();
                    blockState.resetAlongPath(path);
                }
                
            } else {
                // Reset the parent block
                parentBlockState.resetAlongPath(path);
            }
        }
    }
    
    public void addToExecutionListAlongPath(Vector executionList, Vector path){
        if(port!=null && !path.contains(this)){
            path.addElement(this);
            BlockState blockState;
            setCurrentState(PortState.PORT_HAS_DATA);
            
            if(port.getType()==PortModel.INPUT_PORT){
                // This is an input port. Try and add the parent block to
                // the execution list
                parentBlockState.addToExecutionListAlongPath(executionList, path);
                
            } else {
                // This is an output port. Find the connected ports
                // and continue down this path
                PortModel connectedPort;
                BlockModel connectedBlock;
                PortState connectedPortState;
                
                Enumeration e = findConnectedPorts().elements();
                while(e.hasMoreElements()){
                    connectedPort = (PortModel)e.nextElement();
                    connectedBlock = connectedPort.getParentBlock();
                    blockState = parentBlockState.getParentDrawingState().findBlockState(connectedBlock);
                    connectedPortState = blockState.getInputState(connectedPort);
                    connectedPortState.addToExecutionListAlongPath(executionList, path);
                }
            }
        }
    }
    
    /** Find the connected port */
    public Vector findConnectedPorts(){
        Vector connectedPorts = new Vector();
        if(port!=null){
            Enumeration e = port.connections();
            ConnectionModel cm;
            while(e.hasMoreElements()){
                cm = (ConnectionModel)e.nextElement();
                if(port.getType()==PortModel.INPUT_PORT){
                    // Get the port that supplies data to this port. There should
                    // only be one
                    connectedPorts.addElement(cm.getSourcePort());
                } else {
                    // Get the destination (InputPorts) connected to this port
                    connectedPorts.addElement(cm.getDestinationPort());
                }
            }
        }
        return connectedPorts;
    }
    
    /** Get the actual port */
    public PortModel getPort(){
        return port;
    }
    
    /** Is the underlying port optional */
    public boolean isOptionalPort(){
        if(port instanceof InputPortModel){
            return ((InputPortModel)port).isOptional();
        } else {
            return false;
        }
    }
}
