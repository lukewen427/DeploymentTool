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

import java.awt.*;

/**
 * This class allows a Color object to be persisted in an XmlDataStore
 * @author  hugo
 */
public class XmlColorDataObject extends XmlDataObject {
    static final long serialVersionUID = 7042931178009445682L;
    
    /** Color */
    private Color value;
    
    /** Creates a new instance of XmlColorDataObject */
    public XmlColorDataObject() {
        super();
    }
    
    /** Creates a new instance of XmlColorDataObject */
    public XmlColorDataObject(String name) {
        super(name);
    }
    
    /** Creates a new instance of XmlColorDataObject */
    public XmlColorDataObject(String name, Color value) {
        super(name);
        this.value = value;
    }
    
    /** Save to an XML element */
    public void appendToXmlElement(Document xmlDocument, Element xmlElement, boolean includeDescription) throws XmlStorageException {
        try{
            Element element = getBasicXmlElement(xmlDocument, includeDescription);
            element.setAttribute("Red", Integer.toString(value.getRed()));
            element.setAttribute("Green", Integer.toString(value.getGreen()));
            element.setAttribute("Blue", Integer.toString(value.getBlue()));
            xmlElement.appendChild(element);
            
        } catch (Exception e){
            throw new XmlStorageException("Error adding XmlColorDataObject to XML document: " + e.getMessage());
        }        
    }
    
    /** Reconstruct from an XML element */
    public void buildFromXmlElement(Element xmlElement) throws XmlStorageException {
        try{
            // Set basic properties. This will throw an Exception if the
            // element is incorrectly formed
            setBasicPropertiesFromXmlElement(xmlElement);
            
            // Extract red, green and blue values
            int r = Integer.parseInt(xmlElement.getAttribute("Red"));
            int g = Integer.parseInt(xmlElement.getAttribute("Green"));
            int b = Integer.parseInt(xmlElement.getAttribute("Blue"));
            value = new Color(r, g, b);
            
        } catch (Exception e){
            throw new XmlStorageException("Error recreating XmlColorDataObject: " + e.getMessage());
        }        
    }
    
    /** Return an exact copy of this object */
    public XmlDataObject getCopy() {
        XmlDataObject copy = new XmlColorDataObject(getName(), new Color(value.getRGB()));
        copy.setDescription(getDescription());
        copy.setCategory(getCategory());
        copy.setOptions(getOptions());
        copy.setExposedProperty(isExposedProperty());
        copy.setExposedName(getExposedName());
        return copy;           
    }
    
    /** Get the data type label */
    public String getTypeLabel() {
        return "Color";
    }
    
    /** Return value as an object */
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object objectValue) throws XmlStorageException {
        if(objectValue instanceof Color){
            value = (Color)objectValue;
        } else {
            throw new XmlStorageException("XmlColorDataObject requires a Color object in setValue");
        }
    }
    
    /** Get value as a color */
    public Color colorValue(){
        return value;
    }
    
    /** Get red value of color */
    public int getRed(){
        return value.getRed();
    }
    
    /** Get green value of color */
    public int getGreen(){
        return value.getGreen();
    }
    
    /** Get blue value of color */
    public int getBlue(){
        return value.getBlue();
    }
    
    /** Set the color value */
    public void setColorValue(Color color){
        this.value = color;
    }
    
    /** Can this object parse a string */
    public static boolean canParseString() {
        return false;
    }
    
    /** Parse a string value */
    public static XmlDataObject parseString(String value) throws XmlStorageException {
        return new XmlColorDataObject();
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
