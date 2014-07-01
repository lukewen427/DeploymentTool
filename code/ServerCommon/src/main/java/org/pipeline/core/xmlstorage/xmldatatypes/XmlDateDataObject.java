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
import org.w3c.dom.Element;

import java.util.Date;

/**
 * This class allows a Date object to be saved into an XmlDataStore object
 * @author  hugo
 */
public class XmlDateDataObject extends XmlDataObject {
    static final long serialVersionUID = -1318213293834814464L;
    
    /** Date value */
    private Date value = null;
    
    /** Creates a new instance of XmlDateDataObject */
    public XmlDateDataObject() {
        super();
    }
    
    /** Creates a new instance of XmlDateDataObject */
    public XmlDateDataObject(String name) {
        super(name);
    }
    
    /** Creates a new instance of XmlDateDataObject */
    public XmlDateDataObject(String name, Date value) {
        super(name);
        this.value = value;
    }

    public void appendToXmlElement(org.w3c.dom.Document xmlDocument, org.w3c.dom.Element xmlElement, boolean includeDescription) throws XmlStorageException {
        try{
            Element element = getBasicXmlElement(xmlDocument, includeDescription);
            element.setAttribute("Value", Long.toString(value.getTime()));
            xmlElement.appendChild(element);
            
        } catch (Exception e){
            throw new XmlStorageException("Error adding XmlDateDataObject to XML document: " + e.getMessage());
        }              
    }
    
    public void buildFromXmlElement(org.w3c.dom.Element xmlElement) throws XmlStorageException {
        try{
            // Set basic properties. This will throw an Exception if the
            // element is incorrectly formed
            setBasicPropertiesFromXmlElement(xmlElement);
            
            // Extract red, green and blue values
            value = new Date(Long.parseLong(xmlElement.getAttribute("Value")));
            
        } catch (Exception e){
            throw new XmlStorageException("Error recreating XmlDateDataObject: " + e.getMessage());
        }               
    }
    
    /** Return an exact copy of this date object */
    public XmlDataObject getCopy() {
        XmlDataObject copy = new XmlDateDataObject(getName(), new Date(value.getTime()));
        copy.setDescription(getDescription());
        copy.setCategory(getCategory());
        copy.setOptions(getOptions());
        copy.setExposedProperty(isExposedProperty());
        copy.setExposedName(getExposedName());       
        return copy;          
    }
    
    /** Get data type label */
    public String getTypeLabel() {
        return "Date";
    }
    
    /** Get value as an object */
    public Object getValue() {
        return value;
    }
    
    /** Set value as an object */
    public void setValue(Object objectValue) throws XmlStorageException {
        if(objectValue instanceof Date){
            value = (Date)objectValue;
        } else {
            throw new XmlStorageException("XmlDateDataObject requires a Date object in setValue");
        }        
    }
    
    /** Get value as a Date */
    public Date dateValue(){
        return value;
    }
    
    /** Set value as a Date */
    public void setDateValue(Date value){
        this.value = value;
    }
    
    /** Can this object parse a string */
    public static boolean canParseString() {
        return true;
    }
    
    /** Parse a string */
    public static XmlDataObject parseString(String stringValue) throws XmlStorageException {
        try {
            java.text.SimpleDateFormat f = new java.text.SimpleDateFormat();
            Date newValue = f.parse(stringValue);
            XmlDateDataObject newObject = new XmlDateDataObject();
            newObject.setDateValue(newValue);
            return newObject;
            
        } catch (Exception e){
            throw new XmlStorageException("Cannot parse date: " + e.getMessage());
        }
    }
    
    @Override
    public Object getDefaultValue() throws XmlStorageException {
        throw new XmlStorageException("Default value not supported for: " + getClass().getSimpleName());
    }    

    @Override
    public boolean containsNonDefaultValue() {
        return true;
    }
    
    
}
