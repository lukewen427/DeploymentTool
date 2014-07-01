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

package org.pipeline.core.util.block;

import org.pipeline.core.drawing.DrawingException;
import org.pipeline.core.drawing.model.DefaultBlockModel;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This class extends the basic DefaultBlockModel to provide a 
 * block that takes care of all editing functions using
 * an XmlDataStore object that user blocks can use to
 * add properties to. This means that there is no need
 * to create special block editors or write serialisation
 * code for simple blocks that just require access to basic
 * properties.
 *
 * @author hugo
 */
public abstract class SimplifiedBlockModel extends DefaultBlockModel {
   
    /** Mappings for input data ports to helper objects. This 
     * is used to make sure that column pickers get given the
     * correct DataMetaData to display column lists when they
     * are placed onto the block editor*/
    private Hashtable inputMetaDataMappings = new Hashtable();
    
    /** Creates a new instance of SimplifiedBlockModel */
    public SimplifiedBlockModel() throws DrawingException {
        super();
        setEditorClass(SimplifiedBlockModelEditor.class);
        initialiseProperties();
    }
    
    /** Add a meta-data mapping */
    public void addInputMetaDataMapping(String propertyName, String inputName){
        inputMetaDataMappings.put(propertyName, inputName);
    }
    
    /** Get an Enumeration of the meta-data mappings */
    public Enumeration getInputMetaDataMappingKeys(){
        return inputMetaDataMappings.keys();
    }
    
    /** Get the input related to a property */
    public String getMetaDataMapping(String propertyName){
        return inputMetaDataMappings.get(propertyName).toString();
    }
    
    /** Get the properties collection */
    public XmlDataStore getProperties(){
        return getEditableProperties();
    }
    
    /** Initialise the properties */
    public abstract void initialiseProperties();
    
    /** Save this block */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        return store;
    }
    
    /** Load this block */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store); 
        blockLoadFinished();
    }
    
    /** This method is called after the block has been recreated from xml storage. It
     * can be used to do any initialisation that is needed on loading */
    public void blockLoadFinished(){        
    }
}
