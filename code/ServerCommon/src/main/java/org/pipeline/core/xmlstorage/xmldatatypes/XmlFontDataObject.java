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
 * This class allows storage of a Font within an XmlDataStore object
 * @author  hugo
 */
public class XmlFontDataObject extends XmlDataObject {
    static final long serialVersionUID = -1978584977114428750L;
    
    /** Font value of this XmlDataObject */
    private Font value = new Font("SanSerif", Font.PLAIN, 10);
    
    /** Creates a new instance of XmlFontDataObject */
    public XmlFontDataObject() {
        super();
    }
    
    /** Creates a new instance of XmlFontDataObject */
    public XmlFontDataObject(String name) {
        super(name);
    }
    
    /** Creates a new instance of XmlFontDataObject */
    public XmlFontDataObject(String name, Font value) {
        super(name);
        setFontValue(value);
    }    
    
    /** Save to XML document */
    public void appendToXmlElement(Document xmlDocument, Element xmlElement, boolean includeDescription) throws XmlStorageException {
        try{
            Element element = getBasicXmlElement(xmlDocument, includeDescription);
            element.setAttribute("FontName", value.getName());
            element.setAttribute("Style", Integer.toString(value.getStyle()));
            element.setAttribute("Size", Integer.toString(value.getSize()));
            xmlElement.appendChild(element);
            
        } catch (Exception e){
            throw new XmlStorageException("Error adding XmlFontDataObject to XML document: " + e.getMessage());
        }               
    }
    
    /** Create from XML document */
    public void buildFromXmlElement(Element xmlElement) throws XmlStorageException {
        try{
            // Set basic properties. This will throw an Exception if the
            // element is incorrectly formed
            setBasicPropertiesFromXmlElement(xmlElement);
            
            // Extract red, green and blue values
            String name = xmlElement.getAttribute("FontName");
            int style = Integer.parseInt(xmlElement.getAttribute("Style"));
            int size = Integer.parseInt(xmlElement.getAttribute("Size"));
            value = new Font(name, style, size);
            
        } catch (Exception e){
            throw new XmlStorageException("Error recreating XmlDoubleDataObject: " + e.getMessage());
        }               
    }
    
    /** Return an exact copy of this object */
    public XmlDataObject getCopy() {
        XmlDataObject copy = new XmlFontDataObject(getName(), new Font(value.getName(), value.getStyle(), value.getSize()));
        copy.setDescription(getDescription());
        copy.setCategory(getCategory());
        copy.setOptions(getOptions());
        copy.setExposedProperty(isExposedProperty());
        copy.setExposedName(getExposedName());     
        return copy;         
    }
    
    /** Return data type label */
    public String getTypeLabel() {
        return "Font";
    }
    
    /** Return object version of the value */
    public Object getValue() {
        return value;
    }
    
    /** Set the value as an object */
    public void setValue(Object objectValue) throws XmlStorageException {
        if(objectValue instanceof Font){
            if(objectValue.getClass().getName().equals("javax.swing.plaf.FontUIResource")){
                Font of = (Font)objectValue;
                value = new Font(of.getName(), of.getStyle(), of.getSize());
                
            } else {
                value = (Font)objectValue;
            }
            
        } else {
            throw new XmlStorageException("XmlBooleanDataObject requires a Number object in setValue");
        }          
    }
    
    /** Set the value as a Font */
    public void setFontValue(Font font){
        // Treat javax.swing.plaf.FontUIResources differently
        if(font.getClass().getName().equals("javax.swing.plaf.FontUIResource")){
            value = new Font(font.getName(), font.getStyle(), font.getSize());
        } else {
            value = font;
        }
    }
    
    /** Get the Font value */
    public Font fontValue(){
        return value;
    }
    
    /** Does this object support string parsing */
    public  static boolean canParseString() {
        return false;
    }
    
    /** Parse a string value */
    public static XmlDataObject parseString(String strValue) throws XmlStorageException {
        Font newFont = new Font("SanSerif", Font.PLAIN, 10);
        XmlFontDataObject newValue = new XmlFontDataObject();
        newValue.setFontValue(newFont);
        return newValue;
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
