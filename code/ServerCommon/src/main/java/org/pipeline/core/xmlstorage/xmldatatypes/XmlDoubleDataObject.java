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
 * This class allows a double precision number to be stored in an XmlDataStore
 * object
 * @author  hugo
 */
public class XmlDoubleDataObject extends XmlDataObject {
    static final long serialVersionUID = 2401299601083558585L;
    
    /** Value of this data object */
    private double value;
    
    /** Default double value */
    private double defaultValue;
    
    /** Creates a new instance of XmlDoubleDataObject */
    public XmlDoubleDataObject() {
        super();
        defaultSupported = true;
        value = 0;
        defaultValue = 0;
        defaultPresent = false;
    }
    
    /** Creates a new instance of XmlDoubleDataObject */
    public XmlDoubleDataObject(String name) {
        super(name);
        value = 0;
        defaultSupported = true;
        defaultValue = 0;
        defaultPresent = false;
    }
    
    /** Creates a new instance of XmlDoubleDataObject */
    public XmlDoubleDataObject(String name, double value) {
        super(name);
        this.value = value;
        defaultSupported = true;
        defaultValue = 0;
        defaultPresent = false;
    }
    
    /** Save to XML document */
    public void appendToXmlElement(Document xmlDocument, Element xmlElement, boolean includeDescription) throws XmlStorageException {
        try{
            Element element = getBasicXmlElement(xmlDocument, includeDescription);
            element.setAttribute("Value", Double.toString(value));
            if(defaultPresent){
                element.setAttribute("DefaultValue", Double.toString(defaultValue));
            }
            xmlElement.appendChild(element);
            
        } catch (Exception e){
            throw new XmlStorageException("Error adding XmlDoubleDataObject to XML document: " + e.getMessage());
        }               
    }
    
    /** Retrieve from XML document */
    public void buildFromXmlElement(Element xmlElement) throws XmlStorageException {
        try{
            // Set basic properties. This will throw an Exception if the
            // element is incorrectly formed
            setBasicPropertiesFromXmlElement(xmlElement);
            
            // Extract red, green and blue values
            value = Double.parseDouble(xmlElement.getAttribute("Value"));
            
            String strValue = xmlElement.getAttribute("DefaultValue");
            if(strValue!=null && !strValue.isEmpty()){
                defaultValue = Double.parseDouble(strValue);
                defaultPresent = true;
            } else {
                defaultPresent = false;
            }
            
        } catch (Exception e){
            throw new XmlStorageException("Error recreating XmlDoubleDataObject: " + e.getMessage());
        }              
    }
    
    /** Return an exact copy */
    public XmlDataObject getCopy() {
        XmlDoubleDataObject copy = new XmlDoubleDataObject(getName(), value);
        copy.setDescription(getDescription());
        copy.setCategory(getCategory());
        copy.setOptions(getOptions());
        copy.setExposedProperty(isExposedProperty());
        copy.setExposedName(getExposedName());   
        if(defaultPresent){
            
        }
        return copy;          
    }
    
    /** Get the data type label */
    public String getTypeLabel() {
        return "Double";
    }
    
    /** Return the value as an object */
    public Object getValue() {
        return new Double(value);
    }
    
    /** Set value as an object. In this case, any Number type will do */
    public void setValue(Object objectValue) throws XmlStorageException {
        if(objectValue instanceof Number){
            value = ((Number)objectValue).doubleValue();
        } else {
            throw new XmlStorageException("XmlBooleanDataObject requires a Number object in setValue");
        }        
    }
    
    public void setDefaultAsString(String defaultValue) throws XmlStorageException {
        this.defaultValue = Double.parseDouble(defaultValue);
        defaultPresent = true;
    }
        
    /** Get the double value */
    public double doubleValue(){
        return value;
    }
        
    /** Set the value as a double */
    public void setDoubleValue(double value){
        this.value = value;
    }
    
    public void setDefaultDoubleValue(double defaultValue){
        this.defaultValue = defaultValue;
        this.defaultPresent = true;
    }
    
    public double getDefaultDoubleValue(){
        return defaultValue;
    }
    
    /** Does this object support string parsing */
    public static boolean canParseString() {
        return true;
    }
    
    /** Parse a string value */
    public static XmlDataObject parseString(String stringValue) throws XmlStorageException {
        try{
            double newValue = Double.parseDouble(stringValue);
            XmlDoubleDataObject newObject = new XmlDoubleDataObject();
            newObject.setDoubleValue(newValue);
            return newObject;
            
        } catch (Exception e){
            throw new XmlStorageException("Cannot parse double: " + e.getMessage());
        }
    }

    @Override
    public Object getDefaultValue() throws XmlStorageException {
        return defaultValue;
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
