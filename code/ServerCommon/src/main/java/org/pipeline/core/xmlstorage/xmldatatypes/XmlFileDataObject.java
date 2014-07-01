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

import java.io.File;

/**
 * This class allows a java.io.File object to be stored to 
 * an XmlDataStore
 * @author hugo
 */
public class XmlFileDataObject extends XmlDataObject {
    static final long serialVersionUID = -3452476539684968846L;
    
    /** File stored */
    private File value;
    
    /** Is the value a directory instead of a File */
    private boolean directory = false;
    
    /** Creates a new instance of XmlFileDataObject */
    public XmlFileDataObject() {
        super();
    }
    
    /** Create with a name */
    public XmlFileDataObject(String name) {
        super(name);
    }
    
    /** Create with a value */
    public XmlFileDataObject(String name, File value){
        super(name);
        this.value = value;
    }
    
    /** Set whether this object is a directory */
    public void setDirectory(boolean directory){
        this.directory = directory;
    }
    
    /** Does this object represent a directory */
    public boolean isDirectory(){
        return directory;
    }
    
    /** Save to an XML element */
    public void appendToXmlElement(Document xmlDocument, Element xmlElement, boolean includeDescription) throws XmlStorageException {
        try{
            Element element = getBasicXmlElement(xmlDocument, includeDescription);
            
            if(value!=null){
                element.setAttribute("Value", value.getPath());
            } else {
                element.setAttribute("Value", "NONE");
            }
            
            xmlElement.appendChild(element);
        } catch (Exception e){
            throw new XmlStorageException("Error adding XmlFileDataObject to XML document: " + e.getMessage());
        }        
    }    
    
    /** Recreate from an XML element */
    public void buildFromXmlElement(Element xmlElement) throws XmlStorageException {
        try{
            // Set basic properties. This will throw an Exception if the
            // element is incorrectly formed
            setBasicPropertiesFromXmlElement(xmlElement);
            
            // Create the file
            if(!xmlElement.getAttribute("Value").equals("NONE")){
                value = new File(xmlElement.getAttribute("Value"));
            } else {
                value = null;
            }
            
            // Is this a directory
            if(xmlElement.getAttribute("Directory").equalsIgnoreCase("true")){
                directory = true;
            } else {
                directory = false;
            }
            
        } catch (Exception e){
            throw new XmlStorageException("Error recreating XmlFileDataObject: " + e.getMessage());
        }        
    }    
    
    /** Return an exact copy of this object */
    public XmlDataObject getCopy() {
        XmlDataObject copy = new XmlFileDataObject(getName(), value);
        copy.setDescription(getDescription());
        copy.setCategory(getCategory());
        copy.setOptions(getOptions());
        ((XmlFileDataObject)copy).setDirectory(directory);
        copy.setExposedProperty(isExposedProperty());
        copy.setExposedName(getExposedName());     
        return copy;        
    }    
    
    /** Return type label */
    public String getTypeLabel() {
        return "File";
    }    
    
    /** Get value as an object */
    public Object getValue() {
        return value;
    }    
    
    /** Set value as an object */
    public void setValue(Object objectValue) throws XmlStorageException {
        if(objectValue instanceof File){
            value = (File)objectValue;
        } else {
            throw new XmlStorageException("XmlFileDataObject requires a File object in setValue");
        }
    }    
    
    /** Set the value as a File */
    public void setFileValue(File value){
        this.value = value;
    }
    
    /** Get the value as a file */
    public File fileValue(){
        return value;
    }
    
    /** Can this value parse a string */
    public static boolean canParseString() {
        return true;
    }
    
    /** Parse a string */
    public static XmlDataObject parseString(String strValue) throws XmlStorageException {
        XmlFileDataObject obj = new XmlFileDataObject();
        obj.setFileValue(new File(strValue));
        return obj;
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
