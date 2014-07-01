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
 * This class provides the default implementation of an input port.
 * @author  hugo
 */
public class DefaultInputPortModel extends DefaultPortModel implements Serializable, InputPortModel {
    private boolean optional = false;
    
    /** Creates a new instance of DefaultInputPortModel */
    public DefaultInputPortModel(String name, int location, int offset, BlockModel parent) {
        super(name, location, offset, parent);
        setMaxConnections(1);
    }
    
    public DefaultInputPortModel(String name, int location, int offset, BlockModel parent, boolean optional) {
        super(name, location, offset, parent);
        this.optional = optional;
        setMaxConnections(1);
    }
        
    /** Get the port type */
    public int getType(){
        return INPUT_PORT;
    }

    @Override
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @Override
    public boolean isOptional() {
        return this.optional;
    }

    /** Get the input data */
    public TransferData getData() throws DrawingException {
        // Get the data from the associated output port
        ConnectionModel connection;
        Enumeration e = connections();
        if(e.hasMoreElements()){
            connection = (ConnectionModel)e.nextElement();
            if(connection.getSourcePort()!=null){
                return connection.getSourcePort().getData();
            } else {
                throw new DrawingException("Input port has no connections");
            }
            
        } else {
            throw new DrawingException("Input port has no connections");
        }
    }

    /** Remove a connection */
    public void removeConnection(ConnectionModel connection) {
        Vector connections = getConnections();
        if(connections.contains(connection)){
            connections.removeElement(connection);
            connection.getSourcePort().removeConnection(connection);
            
            // Delete the layout data
            getParentBlock().getParentDrawing().getDrawingLayout().removeLocationData(connection);
        }
    }

    /** Return the data source for this port */
    public PortDataSource getDataSource() {
        return null;
    }

    /** Set the data source for this port */
    public void setDataSource(PortDataSource dataSource) {
    }
}
