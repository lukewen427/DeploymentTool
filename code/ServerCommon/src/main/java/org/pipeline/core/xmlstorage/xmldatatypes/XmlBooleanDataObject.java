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

package org.pipeline.core.xmlstorage.xmldatatypes;

import org.pipeline.core.xmlstorage.XmlDataObject;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class allows the storage of a single boolean value within an
 * XmlDataStore store.
 * @author  hugo
 */
public class XmlBooleanDataObject extends XmlDataObject {
    static final long serialVersionUID = 3844411992576043970L;
    
    /** Value of this parameter */
    private boolean value = false;
    
    /** Default value of this parameter */
    private boolean defaultValue = false;
    
    /** Creates a new instance of XmlBooleanDataObject */
    public XmlBooleanDataObject() {
        super();
        defaultSupported = true;
    }
    
    /** Creates a new instance of XmlBooleanDataObject */
    public XmlBooleanDataObject(String name) {
        super(name);
        defaultSupported = true;
    }
    
    /** Creates a new instance of XmlBooleanDataObject */
    public XmlBooleanDataObject(String name, boolean value) {
        super(name);
        this.value = value;
        defaultSupported = true;
    }
        
    /** Save to an XML element */
    public void appendToXmlElement(Document xmlDocument, Element xmlElement, boolean includeDescription) throws XmlStorageException {
        try{
            Element element = getBasicXmlElement(xmlDocument, includeDescription);
            if(value==true){
                element.setAttribute("Value", "true");
            } else {
                element.setAttribute("Value", "false");
            }
            
            if(defaultPresent){
                if(defaultValue==true){
                    element.setAttribute("DefaultValue", "true");
                } else {
                    element.setAttribute("DefaultValue", "false");
                }
            }
            
            xmlElement.appendChild(element);
        } catch (Exception e){
            throw new XmlStorageException("Error adding XmlStringDataObject to XML document: " + e.getMessage());
        }        
    }
    
    /** Recreate from an XML element */
    public void buildFromXmlElement(Element xmlElement) throws XmlStorageException {
        try{
            // Set basic properties. This will throw an Exception if the
            // element is incorrectly formed
            setBasicPropertiesFromXmlElement(xmlElement);
            
            // Set the string value
            String strValue = xmlElement.getAttribute("Value");
            if(strValue!=null){
                if(strValue.equalsIgnoreCase("true")){
                    value = true;
                } else {
                    value = false;
                }
            } else {
                throw new XmlStorageException("Missing Value attribute in XML tag");
            }
            
            strValue = xmlElement.getAttribute("DefaultValue");
            if(strValue!=null && !strValue.isEmpty()){
                if(strValue.equalsIgnoreCase("true")){
                    defaultValue = true;
                } else {
                    defaultValue = false;
                }
                defaultPresent = true;
            } else {
                defaultValue = false;
                defaultPresent = false;
            }
            
        } catch (Exception e){
            throw new XmlStorageException("Error recreating XmlStringDataObject: " + e.getMessage());
        }        
    }
    
    /** Return an exact copy of this object */
    public XmlDataObject getCopy() {
        XmlBooleanDataObject copy = new XmlBooleanDataObject(getName(), value);
        copy.setDescription(getDescription());
        copy.setCategory(getCategory());
        copy.setOptions(getOptions());
        copy.setExposedProperty(isExposedProperty());
        copy.setExposedName(getExposedName());
        if(defaultPresent){
            copy.setDefaultBooleanValue(defaultValue);
        }
        return copy;        
    }
    
    /** Return type label */
    public String getTypeLabel() {
        return "Boolean";
    }
    
    /** Get value as an object */
    public Object getValue() {
        return value;
    }

    @Override
    public Object getDefaultValue() throws XmlStorageException {
        return defaultValue;
    }
    
    /** Set value as an object */
    public void setValue(Object objectValue) throws XmlStorageException {
        if(objectValue instanceof Boolean){
            value = ((Boolean)objectValue).booleanValue();
        } else {
            throw new XmlStorageException("XmlBooleanDataObject requires a Boolean object in setValue");
        }
    }
    
    public void setDefaultAsString(String defaultValue) throws XmlStorageException {
        this.defaultValue = Boolean.parseBoolean(defaultValue);
        defaultPresent = true;
    }

    /** Set the boolean value of this object */
    public void setBooleanValue(boolean value){
        this.value = value;
    }
    
    /** Set the boolean default value */
    public void setDefaultBooleanValue(boolean defaultValue){
        this.defaultValue = defaultValue;
        defaultPresent = true;
    }
    
    /** Get the boolean value of this object */
    public boolean booleanValue(){
        return value;
    }
    
    /** Get the boolean default */
    public boolean getDefaultBooleanValue(){
        return defaultValue;
    }
    
    /** Can this value parse a string */
    public static boolean canParseString() {
        return true;
    }
    
    /** Parse a string */
    public static XmlDataObject parseString(String strValue) throws XmlStorageException {
        XmlBooleanDataObject obj = new XmlBooleanDataObject();
        
        if(strValue.equalsIgnoreCase("yes") || strValue.equalsIgnoreCase("true")){
            obj.setBooleanValue(true);
        } else {
            obj.setBooleanValue(false);
        }
        return obj;
    }

    @Override
    public boolean containsNonDefaultValue() {
        if(defaultPresent){
            if(value!=defaultValue){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
