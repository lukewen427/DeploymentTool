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

package org.pipeline.core.xmlstorage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;

/**
 * Base storage class that is stored within an XmlDataStore
 * @author  hugo
 */
public abstract class XmlDataObject implements Serializable {
    /** Parameter Name */
    private String name;

    /** Description */
    private String description = null;
    
    /** Category of this object */
    private String category = null;
    
    /** Document top level tag name */
    public static final String DOCUMENT_ROOT_NODE = "PipelineDocument";
    
    /** List of valid options if they have been set */
    private String[] options = null;
    
    /** Is this a public property */
    private boolean exposedProperty = false;
    
    /** Public name for this property */
    private String exposedName = "";
    
    /** Does this property support a default value */
    protected boolean defaultSupported = false;
    
    /** Does this property actually contain a default */
    protected boolean defaultPresent = false;
    
    /** Creates a new instance of XmlDataObject */
    public XmlDataObject() {
        name = "NoName";
    }

    /** Creates a new instance of XmlDataObject */
    public XmlDataObject(String name) {
        this.name = name;
    }

    /** Set the description */
    public void setDescription(String description){
        this.description = description;
    }
    
    /** Get the description */
    public String getDescription(){
        if(description!=null){
            return description;
        } else {
            return "";
        }
    }

    /** Does this data object have options */
    public boolean hasOptions(){
        if(this.options!=null){
            return true;
        } else {
            return false;
        }
    }
    
    /** Set the range of valid options */
    public void setOptions(String[] options){
        this.options = options;
    }
    
    /** Get the range of valid options */
    public String[] getOptions(){
        return options;
    }
    
    /** Get the property category */
    public String getCategory() {
        return category;
    }

    /** Set the property category */
    public void setCategory(String category) {
        this.category = category;
    }
    
    /** Get this parameters name */
    public String getName(){
        return name;
    }
    
    /** Set the name of this parameter */
    protected void setName(String strName){
        name = strName;
    }

    /** Get the name that will be exposed so that this property can be marked as externally settable */
    public String getExposedName() {
        return exposedName;
    }

   
    /** Set whether or not this property will be marked as exposed */
    public void setExposedProperty(boolean exposedProperty) {
        this.exposedProperty = exposedProperty;
    }

    /** Has this property been marked as an exposed property */
    public boolean isExposedProperty() {
        return exposedProperty;
    }
    
    /** Set the name that will be exposed so that this property can be marked as externally settable */
    public void setExposedName(String exposedName) {
        this.exposedName = exposedName;
    }
    
    
    /** Remove the description from this data object */
    public void flushDescription(){
        this.description = null;
    }

    @Override
    public String toString() {
        if(description!=null){
            return name + "=" + getValue().toString() + ": " + description;
        } else {
            return name + "=" + getValue().toString();
        }
    }
    
    /** Set value from an Object reference. This is used by custom XmlDataObjects
     * in the XmlDataObjectFactory */
    public abstract void setValue(Object objectValue) throws XmlStorageException;
    
    /** Return the value of this object */
    public abstract Object getValue();
    
    /** Return the default value of this object */
    public abstract Object getDefaultValue() throws XmlStorageException;
    
    /** Does this item contain a non-default value */
    public abstract boolean containsNonDefaultValue();
    
    /** Return the data type label for this object */
    public abstract String getTypeLabel();
    
    /** Write the contents of this parameter to an Element within an XML document */
    public abstract void appendToXmlElement(Document xmlDocument, Element xmlElement, boolean includeDescription) throws XmlStorageException;
    
    /** Build this parameter from an xmlElement */
    public abstract void buildFromXmlElement(Element xmlElement) throws XmlStorageException;
    
    /** Return a copy of this object */
    public abstract XmlDataObject getCopy();
    
    /** Parse a String value */
    public static XmlDataObject parseString(String value) throws XmlStorageException {
        throw new XmlStorageException("No parseString method defined");
    }
    
    public void setDefaultAsString(String defaultValue) throws XmlStorageException {
        throw new XmlStorageException("Cannot set default string value");
    }
    
    /** Does this property support string parsing */
    public static boolean canParseString(){
        return false;
    }

    public boolean isDefaultPresent() {
        return defaultPresent;
    }

    public boolean isDefaultSupported() {
        return defaultSupported;
    }
    
    /** Append the data contained in this parameter to an element of an XML document */
    public Element getBasicXmlElement(Document xmlDocument, boolean includeDescription){
        Element element = xmlDocument.createElement("Parameter");
        element.setAttribute("Name", getName());
        element.setAttribute("Type", getTypeLabel());
        
        if(category!=null){   
            element.setAttribute("Category", getCategory());
        }
        
        if(includeDescription && description!=null && (!description.equalsIgnoreCase(""))){
            element.setAttribute("Description", description);
        }
        
        if(options!=null){
            StringBuilder optionList = new StringBuilder();
            int count = 0;
            for(String o : options){
                if(count==0){
                    optionList.append(o);
                } else {
                    optionList.append(",");
                    optionList.append(o);
                }
                count++;
            }
            element.setAttribute("Options", optionList.toString());
        }
        
        // Set the exposed name
        if(exposedName!=null && !exposedName.isEmpty()){
            element.setAttribute("ExposedName", exposedName.trim());
        }
        
        if(exposedProperty==true){
            element.setAttribute("ExposedProperty", "true");
        } else {
            element.setAttribute("ExposedProperty", "false");
        }
        return element;
    }
    
    /** Set basic properties from an XmlElement */
    public void setBasicPropertiesFromXmlElement(Element xmlElement) throws XmlStorageException {
        try{
            String strType = xmlElement.getAttribute("Type");
            String strName = xmlElement.getAttribute("Name");
            String desc = xmlElement.getAttribute("Description");
            String cat = xmlElement.getAttribute("Category");
            String optionList = xmlElement.getAttribute("Options");
            String exposedNameAttribute = xmlElement.getAttribute("ExposedName");
            String exposedPropertyAttrtibute = xmlElement.getAttribute("ExposedProperty");
            
            // Load the description if it exists
            if(desc!=null && (!desc.equalsIgnoreCase(""))){
                description = desc;
            } else {
                description = null;
            }
            
            // Load the category if it exists */
            if(cat!=null && !cat.equalsIgnoreCase("")){
                category = cat;
            } else {
                category = null;
            }
            
            // Property type
            if(strType!=null && strName!=null){
                if(strType.equals(getTypeLabel())){
                    name = strName;
                } else {
                    throw new XmlStorageException("Data type mismatch");
                }
                
            } else {
                throw new XmlStorageException("Missing XML attributes");
            }
            
            // Valid options if there are any
            if(optionList!=null && !optionList.isEmpty()){
                options = optionList.split(",");
            }
            
            // Is this an exposed property
            if(exposedNameAttribute!=null && ! exposedNameAttribute.isEmpty()){
                exposedName = exposedNameAttribute.trim();
            } else {
                exposedName = "";
            }
            
            if(exposedPropertyAttrtibute!=null && exposedPropertyAttrtibute.equals("true")){
                exposedProperty = true;
            } else {
                exposedProperty = false;
            }
            
        } catch (Exception e){
            throw new XmlStorageException("Error setting basic properties from Element: " + e.getMessage());
        }
    }    
    
    /** Customise the standard renderer object to display this property */
    public void cusomizeRenderer(Object renderer){
    }
    
    /** Get the custom cell editor object */
    public Object getCustomEditor(){
        return null;
    }
}
