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
import java.util.Vector;

/**
 * This class provides the default implementation of an output port.
 * @author  hugo
 */
public class DefaultOutputPortModel extends DefaultPortModel implements Serializable, OutputPortModel {
    /** Transfer data held in this port */
    private PortDataSource dataSource = new DefaultMemoryPortDataSource();
    
    /** Creates a new instance of DefaultOutputPortModel */
    public DefaultOutputPortModel(String name, int location, int offset, BlockModel parent) {
        super(name, location, offset, parent);
        setMaxConnections(-1);
    }
    
    /** Set data */
    public void setData(TransferData data){
        dataSource.setData(this, data);
    }
    
    /** Get the data */
    public TransferData getData() throws DrawingException {
        if(dataSource.containsData(this)){
            TransferData data = dataSource.getData(this);
            if(data!=null){
                return data;
            } else {
                throw new DrawingException("Output port has no data");
            }
        } else {
            throw new DrawingException("Output port has no data");
        }
    }
    
    /** Clear data */
    public void clearData(){
        dataSource.clearData(this);
    }
    
    /** Get the port type */
    public int getType(){
        return OUTPUT_PORT;
    }    
    
    /** Remove a connection */
    public void removeConnection(ConnectionModel connection) {
        Vector connections = getConnections();
        if(connections.contains(connection)){
            connections.removeElement(connection);
            connection.getDestinationPort().removeConnection(connection);
            
            // Delete the layout data
            getParentBlock().getParentDrawing().getDrawingLayout().removeLocationData(connection);            
        }
    }    
    
    /** Return the data source for this port */
    public PortDataSource getDataSource() {
        return dataSource;
    }

    /** Set the data source for this port */
    public void setDataSource(PortDataSource dataSource) {
        this.dataSource = dataSource;
    }    
}
