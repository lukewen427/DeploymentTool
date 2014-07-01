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

import org.pipeline.core.xmlstorage.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;
/**
 * This class will store an XmlStorage object in an XmlDataStore. The
 * process of storing this class will call the storeObject method, and
 * recreating it will call the recreateObject method. NB the object
 * referenced by this XmlDataObject will only be recreated during
 * a getValue() or an xmlStorableValue() call. The actual data
 * will be stored here as an XmlDataStore.
 *
 * This class also supports the storage of signed objects. If
 * the object being saved supports the XmlSignable interface, the
 * signature data will be saved as an attribute of this object
 * along with a binary representation of the certificate so that
 * the signature can be verified later.
 * @author  hugo
 */
public class XmlStorableDataObject extends XmlDataObject {
    static final long serialVersionUID = -4936787317797877067L;

    /** Class name to recreate */
    private String className = "";
    
    /** Expected class name. This is used by the workflow editor to specify which type of
     * object is expected to be here. This is not enforced, but it is stored here
     * so that it is available to the editor */
    private String expectedClassname = "";
    
    /** Data to recreate object from */
    private XmlDataStore classData;
    
    /** Stored object */
    private transient XmlStorable storedObject = null;
    
    /** Creates a new instance of XmlStorableDataObject */
    public XmlStorableDataObject() {
    }
    
    /** Creates a new instance of XmlStorableDataObject */
    public XmlStorableDataObject(String name) {
        super(name);
        classData = null;
    }

    /** Creates a new instance of XmlStorableDataObject */
    public XmlStorableDataObject(String name, XmlDataStore classData, String className, String expectedClassName) {
        super(name);
        this.classData = (XmlDataStore)classData.getCopy();
        this.className = className;
        this.expectedClassname = expectedClassName;
    }

    /** Creates a new instance of XmlStorableDataObject */
    public XmlStorableDataObject(String name, XmlStorable storedObject) throws XmlStorageException {
        super(name);
        this.storedObject = storedObject;
        persistObject(storedObject);
    }

    public void appendToXmlElement(Document xmlDocument, Element xmlElement, boolean includeDescription) throws XmlStorageException {
        try {
            Element element = getBasicXmlElement(xmlDocument, includeDescription);
            element.setAttribute("ClassName", className);
            element.setAttribute("ExpectedClassName", expectedClassname);
            
            // Is the object signed
            if(storedObject instanceof XmlSignable){
                ((XmlSignable)storedObject).getSignatureData().appendToXmlElement(element);
            }
            
            // Append the XmlDataStore to this element
            Element dataStore = xmlDocument.createElement("ClassData");
            classData.appendToXmlElement(xmlDocument, dataStore, includeDescription);
            element.appendChild(dataStore);
            xmlElement.appendChild(element);
            
        } catch (Exception e) {
            throw new XmlStorageException("Error adding XmlStorableDataObject to XML document: " + e.getMessage());
        }               
    }
    
    public void buildFromXmlElement(Element xmlElement) throws XmlStorageException {
        try {
            // Set basic properties. This will throw an Exception if the
            // element is incorrectly formed
            setBasicPropertiesFromXmlElement(xmlElement);            
            
            // Get classname and extract class data
            className = xmlElement.getAttribute("ClassName");
            
            if(xmlElement.hasAttribute("ExpectedClasName")){
                expectedClassname = xmlElement.getAttribute("ExpectedClassName");
            } else {
                expectedClassname = "";
            }
            
            NodeList nodes = xmlElement.getChildNodes();
            if(nodes.getLength()==1){
                Element dataStore = (Element)nodes.item(0);
                if(dataStore.getChildNodes().getLength()==1){
                    Element dataStoreChild = (Element)dataStore.getChildNodes().item(0);
                    if(classData!=null){
                        classData.clear();
                        classData = null;
                    }
                    classData = new XmlDataStore();
                    classData.buildFromXmlElement(dataStoreChild);
                    instantiateObject();
                    
                    // Load the signature if the object supports it
                    if(storedObject instanceof XmlSignable){
                        ((XmlSignable)storedObject).getSignatureData().recreateFromXmlElement(xmlElement);
                    }
                    
                } else {
                    throw new XmlStorageException("Badly formatted XML document element");
                }
                
            } else {
                throw new XmlStorageException("Badly formatted XML document element");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new XmlStorageException("Error retrieving XmlStorableDataObject from XML document: " + e.getMessage());
        }
    }
    
    /** Get the class data */
    public XmlDataStore getClassData(){
        return classData;
    }
    
    /** Get the class name */
    public String getClassName(){
        return className;
    }

    /** Get the name of the class that we expect to be stored here. This is not enforced */
    public String getExpectedClassname() {
        return expectedClassname;
    }

    /** Set the name of the class that we expect to be stored here. This is not enforced */
    public void setExpectedClassname(String expectedClassname) {
        this.expectedClassname = expectedClassname;
    }
    
    /** Return an exact copy of this object */
    public XmlDataObject getCopy() {
        XmlDataObject copy = new XmlStorableDataObject(getName(), classData, className, expectedClassname);
        copy.setDescription(getDescription());
        copy.setCategory(getCategory());
        copy.setOptions(getOptions());
        copy.setExposedProperty(isExposedProperty());
        copy.setExposedName(getExposedName());  
        return copy;
    }
    
    /** Get the data type label */
    public String getTypeLabel() {
        return "XmlStorable";
    }
    
    /** Recreate and return the object value */
    public Object getValue() {
        try{
            if(storedObject!=null){
                return storedObject;
            } else {
                return instantiateObject();
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    /** Get value as an XmlStorable object */
    public XmlStorable xmlStorableValue() throws XmlStorageException {
        return (XmlStorable)getValue();      
    }
    
    /** Set value as an object */
    public void setValue(Object objectValue) throws XmlStorageException {
        if(objectValue instanceof XmlStorable){
            persistObject((XmlStorable)objectValue);
            this.storedObject = (XmlStorable)objectValue;
        } else {
            throw new XmlStorageException("setValue() with XmlStorableDataObject requires an XmlStorable object as a parameter");
        }
    }
    
    /** Set value as an XmlStorage object */
    public void setXmlStorableValue(XmlStorable storableValue) throws XmlStorageException {
        persistObject(storableValue);
        this.storedObject = storableValue;
    }

    @Override
    public String toString() {
        Object v = getValue();
        if(v!=null){
            return v.toString();
        } else {
            return "NO OBJECT";
        }
    }
    
    /** Do a debug print of the data contained in this object */
    public void debugPrint(PrintWriter writer, int depth){
        classData.debugPrint(writer, depth);
    }
    
    /** Instantiate the object referred to by this storage object. This method
     * now checks the XmlStorage object factory to see if the class has been registered.
     * If not, it tries to do a Class.forName, which will probably fail */
    private XmlStorable instantiateObject() throws XmlStorageException {
        try{
            XmlStorable newObject;
            if(XmlStorage.containsClass(className)){
                newObject = (XmlStorable)XmlStorage.instantiate(className);
            } else {   
                // Fall back to a Class.forName
                newObject = (XmlStorable)Class.forName(className).newInstance();
            }
            newObject.recreateObject(classData);
            storedObject = newObject;
            return newObject;
            
        } catch (Exception e){
            throw new XmlStorageException("Could not instantiate XmlStorage object: " + className);
        }
    }
    
    /** Re-persist the stored object */
    public void repersistObject() throws XmlStorageException {
        persistObject(storedObject);
    }
    
    /** Store an object in this class as an XmlDataStore */
    public void persistObject(XmlStorable storableValue) throws XmlStorageException {
        try {
            className = storableValue.getClass().getName();
            classData = storableValue.storeObject();            
            
        } catch (Exception e) {
            throw new XmlStorageException("Cannot add object: " + e.getMessage());
        }
    }
    
    /** Can this object parse a string */
    public static boolean canParseString() {
        return false;
    }
    
    /** Parse a string value */
    public static XmlDataObject parseString(String stringValue) throws XmlStorageException {
        throw new XmlStorageException("XmlStorableDataObject does not support parseString");        
    }
    
    @Override
    public Object getDefaultValue() throws XmlStorageException {
        throw new XmlStorageException("Default value not supported for: " + getClass().getSimpleName());
    }    

    @Override
    public boolean containsNonDefaultValue() {
        if(classData!=null){
            return true;
        } else {
            return false;
        }
    }
}
