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
 * This interface defines the functionality of a Port that is used
 * as an I/O point on a block.
 * @author  hugo
 */
public interface PortModel {   
    // Static typedefs
    
    /** Port located on the left of the block */
    public static final int LEFT_OF_BLOCK = 0;
    
    /** Port located on the top of the block */
    public static final int TOP_OF_BLOCK = 1;
    
    /** Port located on the right of the block */
    public static final int RIGHT_OF_BLOCK = 2;
    
    /** Port located on the bottom of the block */
    public static final int BOTTOM_OF_BLOCK = 3;
    
    /** Execution is not in progress on this port */
    public static final int PORT_IDLE = 0;
    
    /** Execution is in progress on this port */
    public static final int PORT_RUNNING = 1;
    
    /** Input port */
    public static final int INPUT_PORT = 0;
    
    /** Output port */
    public static final int OUTPUT_PORT = 1;
    
    /** Does the port support streaming */
    public boolean streamingPort();
    
    /** Get the status of the port */
    public int getStatus();
    
    /** Location of port on the block */
    public int getPortLocation();
    
    /** Distance along the edge of the block */
    public int getPortOffset();
    
    /** Maximum number of supported connections */
    public int getMaximumConnection();
    
    /** Add a connection */
    public void addConnection(ConnectionModel connection) throws DrawingException;
    
    /** Remove a connection */
    public void removeConnection(ConnectionModel connection);
    
    /** Remove all connections */
    public void removeAllConnections();
    
    /** Get the block containing this port */
    public BlockModel getParentBlock();
    
    /** Get the supported data types */
    public Enumeration supportedDataTypes();
    
    /** Is a data type supported */
    public boolean dataTypeSupported(DataType type);
    
    /** Can this port connect to the specified port */
    public boolean canConnectToPort(PortModel port);
    
    /** Get the name of this port */
    public String getName();
    
    /** Get the port type. Input port or output port */
    public int getType();
    
    /** Get all the connections */
    public Enumeration connections();
    
    /** Get the data held in this port */
    public TransferData getData() throws DrawingException;
    
    /** Can this port accept another connection */
    public boolean canAcceptAdditionalConnection();
    
    /** Get the port data source. The data source is the backend data storage. Typically this is
     * in memory, but may actually be anywhere */
     public PortDataSource getDataSource();
     
    /** Set the port data source. The data source is the backend data storage. Typically this is
     * in memory, but may actually be anywhere */
     public void setDataSource(PortDataSource dataSource);
}
