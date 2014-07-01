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

import org.pipeline.core.xmlstorage.XmlDataStore;

/**
 * This class represents a signal that can be passed through a drawing. It
 * is used to signify the start of streaming or some event specific to a 
 * collection of blocks.
 * @author hugo
 */
public class DrawingSignal {
    /** Signal type */
    private String signalType;
    
    /** Signal description */
    private String signalDescription;
    
    /** Signal properties */
    private XmlDataStore properties;
    
    /** Creates a new instance of DrawingSignal */
    public DrawingSignal(String signalType, String signalDescription) {
        this.signalType = signalType;
        this.signalDescription = signalDescription;
        properties = new XmlDataStore();
    }
    
    /** Get the signal type */
    public String getSignalType(){
        return signalType;
    }
    
    /** Get the signal description */
    public String getSignalDescription(){
        return signalDescription;
    }
    
    /** Get the signal properties */
    public XmlDataStore getProperties(){
        return properties;
    }
}
