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

package org.pipeline.core.drawing.model;

import org.pipeline.core.drawing.*;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This class provides a default implementation of the PortModel interface.
 * @author  hugo
 */
public abstract class DefaultPortModel implements Serializable, PortModel {
    /** Port name */
    private String name = "";
    
    /** Location */
    private int location = PortModel.LEFT_OF_BLOCK;
    
    /** Offset */
    private int offset = 50;
    
    /** Parent block */
    private BlockModel parent = null;
    
    /** Maximum number of connections */
    private int maxConnections = -1;
    
    /** Connections */
    private Vector connections = new Vector();
    
    /** Data-types supported */
    private Vector dataTypes = new Vector();
    
    /** Port status */
    private int status = PORT_IDLE;
    
    /** Is this a streaming port */
    private boolean streamable = false;
    
    /** Does this input enforce data types */
    private boolean dataTypeRestricted = true;
    
    /** Creates a new instance of DefaultPortModel */
    public DefaultPortModel(String name, int location, int offset, BlockModel parent) {
        this.name = name;
        this.location = location;
        this.offset = offset;
        this.parent = parent;
    }
    
    /** Set whether or not to restrict data types */
    public void setDataTypeRestricted(boolean dataTypeRestricted){
        this.dataTypeRestricted = dataTypeRestricted;
    }
    
    /** Does this port restrict data types */
    public boolean isDataTypeRestricted(){
        return dataTypeRestricted;
    }
    
    /** Set the port location */
    public void setLocation(int location){
        this.location = location;
    }
    
    /** Set the port offset */
    public void setOffset(int offset){
        if(offset>=0 && offset<=100){
            this.offset = offset;
        }
    }
    
    /** Set maximum number of connections */
    public void setMaxConnections(int maxConnections){
        this.maxConnections = maxConnections;
    }

    /** Add a supported data type */
    public void addDataType(DataType type){
        if(!dataTypes.contains(type)){
            dataTypes.add(type);
        }
    }
    
    /** Clear out data types */
    public void clearDataTypes(){
        dataTypes.clear();
    }
    
    /** Set whether this port is streamable */
    public void setStreamable(boolean streamable){
        this.streamable = streamable;
    }
    
    /** Get the connections Vector */
    public Vector getConnections(){
        return connections;
    }
    
    // =========================================================================
    // PortModel implementation
    // =========================================================================
    
    /**
     * Get the name of this port 
     */
    public String getName() {
        return name;
    }

    /**
     * Can this port connect to the specified port 
     */
    public boolean canConnectToPort(PortModel port) {       
        // Check the maximum connection count on the target port
        Enumeration portTypes = port.supportedDataTypes();
        boolean status = true;
        
        // See if any of the supported types are contained here
        if(dataTypeRestricted){
            while(portTypes.hasMoreElements()){
                if(!dataTypes.contains(portTypes.nextElement())){
                    status = false;
                }
            } 
        }
        
        // Check input - output compatibility
        if(this instanceof InputPortModel && port instanceof InputPortModel){
            status = false;
        } 
        
        if(this instanceof OutputPortModel && port instanceof OutputPortModel){
            status = false;
        }
        
        // Check connection counts
        if(this.canAcceptAdditionalConnection()==false){
            status = false;
        }
        
        if(port.canAcceptAdditionalConnection()==false){
            status = false;
        }
        
        // Nothing found
        return status;
    }

    /**
     * Location of port on the block 
     */
    public int getPortLocation() {
        return location;
    }

    /**
     * Is a data type supported 
     */
    public boolean dataTypeSupported(DataType type) {
        if(dataTypeRestricted){
            return dataTypes.contains(type);
        } else {
            return true;
        }
    }

    /**
     * Add a connection 
     */
    public void addConnection(ConnectionModel connection) throws DrawingException {
        if(!connections.contains(connection)){
            connections.add(connection);
        } else {
            throw new DrawingException("Connection already present");
        }
    }

    /**
     * Get the supported data types 
     */
    public java.util.Enumeration supportedDataTypes() {
        return dataTypes.elements();
    }

    /**
     * Maximum number of supported connections 
     */
    public int getMaximumConnection() {
        return maxConnections;
    }

    /**
     * Distance along the edge of the block 
     */
    public int getPortOffset() {
        return offset;
    }

    /**
     * Remove a connection 
     */
    public abstract void removeConnection(ConnectionModel connection);

    /** Remove all port connections */
    public void removeAllConnections(){
        Enumeration e = connections.elements();
        while(e.hasMoreElements()){
            removeConnection((ConnectionModel)e.nextElement());
        }
    }
    
    /**
     * Get the block containing this port 
     */
    public BlockModel getParentBlock() {
        return parent;
    }

    /** Return the status of this port */
    public int getStatus() {
        return status;
    } 

    /**
     * Does the port support streaming 
     */
    public boolean streamingPort() {
        return streamable;
    }
    
    /**
     * Get the port type
     */
    public int getType(){
        return INPUT_PORT;
    }
    
    /** Get the connections */
    public Enumeration connections(){
        return connections.elements();
    }
    
    /** Can this port accept another connection */
    public boolean canAcceptAdditionalConnection() {
        // Check the maximum connection count on this port
        if(maxConnections!=-1){
            // Need to check, otherwise infinite
            if(connections.size() + 1 > maxConnections) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
