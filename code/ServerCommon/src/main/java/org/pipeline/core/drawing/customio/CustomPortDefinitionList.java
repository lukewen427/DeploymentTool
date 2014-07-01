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

import org.pipeline.core.drawing.DataType;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.util.Enumeration;
import java.util.Vector;
       
/**
 * This class provides a list of custom port definitions
 * @author hugo
 */
public class CustomPortDefinitionList implements XmlStorable {
    /** Class of the definition to create */
    private Class definitionClass;
    
    /** Default data type class */
    private DataType defaultDataType;
    
    /** IO definitions */
    private Vector ioDefinitions = new Vector();
    
    /** Parent block */
    private CustomisableDefaultBlockModel parent;
    
    /** Creates a new instance of CustomPortDefinitionList */
    public CustomPortDefinitionList(Class definitionClass, DataType defaultDataType, CustomisableDefaultBlockModel parent) {
        this.definitionClass = definitionClass;
        this.defaultDataType = defaultDataType;
        this.parent = parent;
    }
    
    /** Update the parent io definitions */
    public void syncParentIOs(){
        if(parent!=null){
            parent.syncIOPorts();
        }
    }
    
    /** Get the default data type */
    public DataType getDefaultDataType(){
        return defaultDataType;
    }
    
    /** Set the default data type */
    public void setDefaultDataType(DataType defaultDataType){
        this.defaultDataType = defaultDataType;
        
        // Set this in all of the existing port definitions
        Enumeration e = ioDefinitions.elements();
        while(e.hasMoreElements()){
            ((CustomPortDefinition)e.nextElement()).setDataType(defaultDataType);
        }
    }
    
    /** Is a named io definition present in the custom ios */
    public boolean namedDefinitionPresent(String name){
        Enumeration e = ioDefinitions.elements();
        CustomPortDefinition def;
        while(e.hasMoreElements()){
            def = (CustomPortDefinition)e.nextElement();
            if(def.getName().equals(name)){
                return true;
            }
        }
        return false;
    }    

    /** Get all of the definitions */
    public Enumeration definitions(){
        return ioDefinitions.elements();
    }
    
    /** Get definition count */
    public int getDefinitionCount(){
        return ioDefinitions.size();
    }
    
    /** Create a definition */
    public CustomPortDefinition createDefinition(String name){
        try {
            CustomPortDefinition def = (CustomPortDefinition)definitionClass.newInstance();
            def.setName(name);
            def.setDataType(defaultDataType);
            ioDefinitions.addElement(def);
            return def;
        } catch (Exception e){
            return null;
        }
    }
    
    /** Get a definition by name */
    public CustomPortDefinition getDefinition(String name){
        Enumeration e = ioDefinitions.elements();
        CustomPortDefinition def;
        
        while(e.hasMoreElements()){
            def = (CustomPortDefinition)e.nextElement();
            if(def.getName().equals(name)){
                return def;
            }
        }
        return null;
    }
    
    /** Get a definition */
    public CustomPortDefinition getDefinition(int index){
        return (CustomPortDefinition)ioDefinitions.elementAt(index);
    }
    
    /** Remove all definitions */
    public void removeAllDefinitions(){
        ioDefinitions.clear();
    }
    
    /** Delete a definition */
    public void removeDefinition(int index){
        ioDefinitions.removeElementAt(index);
    }
    
    /** Delete a definition */
    public void removeDefintion(CustomPortDefinition definition){
        if(ioDefinitions.contains(definition)){
            ioDefinitions.removeElement(definition);
        }
    }
    
    /** Recreate list */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        ioDefinitions.clear();
        int count = xmlDataStore.intValue("DefinitionCount", 0);
        for(int i=0;i<count;i++){
            ioDefinitions.addElement((CustomPortDefinition)xmlDataStore.xmlStorableValue("Definition" + i));
        }
        setDefaultDataType(defaultDataType);
    }

    /** Save list */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("IODefinitionList");
        store.add("DefinitionCount", ioDefinitions.size());
        for(int i=0;i<ioDefinitions.size();i++){
            store.add("Definition" + i, (CustomPortDefinition)ioDefinitions.elementAt(i));
        }
        return store;
    }
}
