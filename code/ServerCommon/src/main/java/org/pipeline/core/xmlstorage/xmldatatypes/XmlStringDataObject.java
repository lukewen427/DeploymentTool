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
 * String XmlData storage object
 * @author  hugo
 */
public class XmlStringDataObject extends XmlDataObject {
    static final long serialVersionUID = 4253711272607163914L;
    
    /** Value of this string */
    private String value;

    /** Default value of this string */
    private String defaultValue;
    
    /** Creates a new instance of XmlStringDataObject */
    public XmlStringDataObject() {
        super();
        value = "";
        defaultValue = "";
        defaultPresent = false;
        defaultSupported = true;
    }
    
    /** Creates a new instance of XmlStringDataObject */
    public XmlStringDataObject(String name) {
        super(name);
        value = "";
        defaultValue = "";
        defaultPresent = false;
        defaultSupported = true;        
    }
    
    /** Creates a new instance of XmlStringDataObject with a String value */
    public XmlStringDataObject(String name, String value) {
        super(name);
        this.value = value;
        defaultValue = "";
        defaultPresent = false;
        defaultSupported = true;        
    }
    
    /** Get value as an object */
    public Object getValue() {
        return value;
    }

    @Override
    public Object getDefaultValue() throws XmlStorageException {
        return defaultValue;
    }
    
    /** Get the value as a string */
    public String stringValue() {
        return value;
    }
    
    public void setDefaultAsString(String defaultValue) throws XmlStorageException {
        this.defaultValue = defaultValue;
        defaultPresent = true;
    }
        
    public String getDefaultStringValue(){
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue){
        this.defaultValue = defaultValue;
        this.defaultPresent = true;
    }
    
    /** Set the value as a string */
    public void setStringValue(String value){
        this.value = value;
    }
    
    /** Set the value as an object */
    public void setValue(Object objectValue) throws XmlStorageException {
        if(objectValue instanceof String){
            value = (String)objectValue;
        } else {
            value = objectValue.toString();
        }
    }
    
    /** Return a label describing the data type of this parameter. This is used for
     * writing to the XML document. It must match the name used to register this type
     * with the XmlDataObjectFactory otherwise everything will break */
    public String getTypeLabel() {
        return "String";
    }
    
    /** Save the contents of this parameter as a child of an xmlElement within 
     * an xmlDocument */
    public void appendToXmlElement(Document xmlDocument, Element xmlElement, boolean includeDescription) throws XmlStorageException {
        try{
            Element element = getBasicXmlElement(xmlDocument, includeDescription);
            element.setAttribute("Value", value);
            if(defaultPresent){
                element.setAttribute("DefaultValue", defaultValue);
            }
            xmlElement.appendChild(element);
        } catch (Exception e){
            throw new XmlStorageException("Error adding XmlStringDataObject to XML document: " + e.getMessage());
        }
    }
    
    /** Build this parameter from an xmlElement */
    public void buildFromXmlElement(Element xmlElement) throws XmlStorageException {
        try{
            // Set basic properties. This will throw an Exception if the
            // element is incorrectly formed
            setBasicPropertiesFromXmlElement(xmlElement);
            
            // Set the string value
            value = xmlElement.getAttribute("Value");
            
            String strValue = xmlElement.getAttribute("DefaultValue");
            if(strValue!=null && !strValue.isEmpty()){
                defaultValue = strValue;
                defaultPresent = true;
            } else {
                defaultValue = "";
                defaultPresent = false;
            }
            
        } catch (Exception e){
            throw new XmlStorageException("Error recreating XmlStringDataObject: " + e.getMessage());
        }
    }
    
    /** Return a copy of this object */
    public XmlDataObject getCopy() {
        XmlStringDataObject copy = new XmlStringDataObject(getName(), value);
        copy.setDescription(getDescription());
        copy.setCategory(getCategory());
        copy.setOptions(getOptions());
        copy.setExposedProperty(isExposedProperty());
        copy.setExposedName(getExposedName());
        if(defaultPresent){
            copy.setDefaultValue(defaultValue);
        }
        return copy;
    }
    
    /** Can this object parse a string */
    public static boolean canParseString() {
        return true;
    }
    
    /** Parse a string */
    public static XmlDataObject parseString(String value) throws XmlStorageException {
        XmlStringDataObject newObject = new XmlStringDataObject();
        newObject.setStringValue(value);
        return newObject;
    }

    @Override
    public boolean containsNonDefaultValue() {
        if(defaultPresent){
            if(defaultValue!=null && !defaultValue.equals(value)){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    
    
}
