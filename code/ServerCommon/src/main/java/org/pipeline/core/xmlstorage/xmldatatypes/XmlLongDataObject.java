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
 * This class allows a long to be stored in an XmlDataStore
 * @author  hugo
 */
public class XmlLongDataObject extends XmlDataObject {
    static final long serialVersionUID = 1440992110281473590L;
    
    /** Value of this long data object */
    private long value = 0;
    
    /** Default value of this long data object */
    private long defaultValue;
    
    /** Creates a new instance of XmlLongDataObject */
    public XmlLongDataObject() {
        super();
        defaultPresent = false;
        defaultSupported = true;
        defaultValue = 0;
    }
    
    /** Creates a new instance of XmlLongDataObject */
    public XmlLongDataObject(String name) {
        super(name);
        defaultPresent = false;
        defaultSupported = true;
        defaultValue = 0;        
    }
    
    /** Creates a new instance of XmlLongDataObject */
    public XmlLongDataObject(String name, long value) {
        super(name);
        this.value = value;
        defaultPresent = false;
        defaultSupported = true;
        defaultValue = 0;        
    }    
    
    /** Save to an XML document */
    public void appendToXmlElement(Document xmlDocument, Element xmlElement, boolean includeDescription) throws XmlStorageException {
        try{
            Element element = getBasicXmlElement(xmlDocument, includeDescription);
            element.setAttribute("Value", Long.toString(value));
            
            if(defaultPresent){
                element.setAttribute("DefaultValue", Long.toString(defaultValue));
            }
            xmlElement.appendChild(element);
            
        } catch (Exception e){
            throw new XmlStorageException("Error adding XmlLongDataObject to XML document: " + e.getMessage());
        }            
    }
    
    /** Retrieve from an XML document */
    public void buildFromXmlElement(Element xmlElement) throws XmlStorageException {
        try{
            // Set basic properties. This will throw an Exception if the
            // element is incorrectly formed
            setBasicPropertiesFromXmlElement(xmlElement);
            
            // Extract red, green and blue values
            value = Long.parseLong(xmlElement.getAttribute("Value"));
            
            String strValue = xmlElement.getAttribute("DefaultValue");
            if(strValue!=null && !strValue.isEmpty()){
                defaultValue = Long.parseLong(strValue);
                defaultPresent = true;
            } else {
                defaultPresent = false;
                defaultValue = 0;
            }
        } catch (Exception e){
            throw new XmlStorageException("Error recreating XmlLongDataObject: " + e.getMessage());
        }                         
    }
    
    /** Return an exact copy of this object */
    public XmlDataObject getCopy() {
        XmlLongDataObject copy = new XmlLongDataObject(getName(), value);
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
    
    /** Get the data type label */
    public String getTypeLabel() {
        return "Long";
    }
    
    /** Get value as an object variable */
    public Object getValue() {
        return new Long(value);
    }

    @Override
    public Object getDefaultValue() throws XmlStorageException {
        return defaultValue;
    }
    
    public long getDefaultLongValue(){
        return defaultValue;
    }
    
    public void setDefaultValue(long defaultValue){
        this.defaultValue = defaultValue;
        this.defaultPresent = true;
    }
    
    public void setDefaultAsString(String defaultValue) throws XmlStorageException {
        this.defaultValue = Long.parseLong(defaultValue);
        defaultPresent = true;
    }
    
    
    /** Set value as an object variable. Any Number object will do */
    public void setValue(Object objectValue) throws XmlStorageException {
        if(objectValue instanceof Number){
            value = ((Number)objectValue).longValue();
        } else {
            throw new XmlStorageException("Number type required for setValue in XmlLongDataObject");
        }        
    }
    
    /** Get value as a long */
    public long longValue() {
        return value;
    }
    
    /** Set value as a long */
    public void setLongValue(long value){
        this.value = value;
    }
    
    /** Does this object support string parsing */
    public static boolean canParseString() {
        return true;
    }
    
    /** Parse a string value */
    public static XmlDataObject parseString(String stringValue) throws XmlStorageException {
        try{
            long newValue = (long)Double.parseDouble(stringValue);
            XmlLongDataObject newObject = new XmlLongDataObject();
            newObject.setLongValue(newValue);
            return newObject;
            
        } catch (Exception e){
            throw new XmlStorageException("Cannot parse long: " + e.getMessage());
        }
    }

    @Override
    public boolean containsNonDefaultValue() {
        if(defaultPresent){
            if(defaultValue!=value){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
