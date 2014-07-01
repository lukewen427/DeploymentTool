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

import org.pipeline.core.drawing.ConnectionModel;
import org.pipeline.core.drawing.InputPortModel;
import org.pipeline.core.drawing.OutputPortModel;

import java.io.Serializable;

/**
 * This class provide the default implementation of a ConnectionModel.
 * @author  hugo
 */
public class DefaultConnectionModel implements Serializable, ConnectionModel {
    /** Source port */
    private OutputPortModel sourcePort;
    
    /** Destination port */
    private InputPortModel destinationPort;
    
    /** Creates a new instance of DefaultConnectionModel */
    public DefaultConnectionModel(OutputPortModel sourcePort, InputPortModel destinationPort) {
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
    }

    /** Override toString method */
    public String toString(){
        if(sourcePort.getParentBlock()!=null && destinationPort.getParentBlock()!=null){
            String inLabel = sourcePort.getParentBlock().toString();
            String outLabel = destinationPort.getParentBlock().toString();
            return inLabel + " --> " + outLabel;
        } else {
            return "Link";
        }
    }
    
    // =========================================================================
    // ConnectionModel implementation
    // =========================================================================
    
    /** Source port */
    public OutputPortModel getSourcePort() {
        return sourcePort;
    }

    /** Destination port */
    public InputPortModel getDestinationPort() {
        return destinationPort;
    }
}
