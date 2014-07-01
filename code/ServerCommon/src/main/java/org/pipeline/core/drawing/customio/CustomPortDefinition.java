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

package org.pipeline.core.drawing.customio;

import org.pipeline.core.drawing.BlockModel;
import org.pipeline.core.drawing.DataType;
import org.pipeline.core.drawing.PortModel;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class contains basic information about a customised input or
 * output for a block.
 * @author hugo
 */
public abstract class CustomPortDefinition implements XmlStorable {
    /** Port location */
    private int location = PortModel.LEFT_OF_BLOCK;
    
    /** Port offset */
    private int offset = 50;
    
    /** Port data type */
    private DataType dataType = null;
        
    /** Port name */
    private String name = "";

    /** Is this a streamable port */
    private boolean streamable = false;

    /** Data type name. This is used to reset the data types during load */
    private String dataTypeName = "";

    /** Creates a new instance of CustomPortDefinition */
    public CustomPortDefinition() {
    }

    /** Is the port streamable */
    public boolean isStreamable(){
        return streamable;
    }

    /** Set whether the port is streamable */
    public void setStreamable(boolean streable){
        this.streamable = streable;
    }

    /** Set the data type */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
    
    /** Get the data type */
    public DataType getDataType() {
        return dataType;
    }
    
    /** Get the location */
    public int getLocation() {
        return location;
    }
    
    /** Set the location */
    public void setLocation(int location) {
        this.location = location;
    }
    
    /** Get the port offset */
    public int getOffset() {
        return offset;
    }
    
    /** Set the port offset */
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    /** Get the port name */
    public String getName() {
        return name;
    }

    /** Set the port name */
    public void setName(String name) {
        this.name = name;
    }

    /** Set the data type name for this port */
    public void setDataTypeName(String dataTypeName){
        this.dataTypeName = dataTypeName;
    }

    /** Get the data type name for this port */
    public String getDataTypeName(){
        return dataTypeName;
    }
    
    /** Create a port based on this definition */
    public abstract PortModel createPort(BlockModel block);
    
    /** Get location text */
    public String getLocationText(){
        switch(location){
            case PortModel.LEFT_OF_BLOCK:
                return "left of block";
            case PortModel.TOP_OF_BLOCK:
                return "top of block";
            case PortModel.RIGHT_OF_BLOCK:
                return "right of block";
            case PortModel.BOTTOM_OF_BLOCK:
                return "bottom of block";
            default:
                return "";
        }
    }    
    
    /** Recreate from an XmlData store */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        location = xmlDataStore.intValue("Location", PortModel.LEFT_OF_BLOCK);
        offset = xmlDataStore.intValue("Offset", 50);
        name = xmlDataStore.stringValue("Name", "");
        streamable = xmlDataStore.booleanValue("Streamable", false);
        dataTypeName = xmlDataStore.stringValue("DataTypeName", "");
    }

    /** Save to an XmlData store */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("CustomIO");
        store.add("Location", location);
        store.add("Offset", offset);
        store.add("Name", name);
        store.add("Streamable", streamable);
        if(dataTypeName!=null){
            store.add("DataTypeName", dataTypeName);
        } else if(dataType!=null){
            store.add("DataTypeName", dataType.getName());
        }
        return store;
    }
}
