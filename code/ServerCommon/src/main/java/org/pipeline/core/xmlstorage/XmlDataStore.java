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

import org.pipeline.core.xmlstorage.xmldatatypes.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class provides a store of Named parameters that can be saved
 * to an XML file for long term persistence. 
 * @author  hugo
 */
public class XmlDataStore extends XmlDataObject implements Serializable {
    static final long serialVersionUID = -8867699731903190836L;
    
    /** List of properties stored in here */
    private Hashtable dataObjects = new Hashtable();
    
    /** Allows properties to be added / removed */
    private boolean allowAddRemove = false;
    
    /** Signature data for this data store */
    private byte[] signatureData;
    
    /** Has this object got signature data */
    private boolean signed = false;

    /** Automatically add properties that don't exist */
    private boolean autoAddProperties = false;

    /** Creates a new instance of XmlDataStore */
    public XmlDataStore() {
        super();
        defaultSupported = false;
    }
    
    /** Creates new instance with a name */
    public XmlDataStore(String name){
        super(name);
        defaultSupported = false;
    }
    
    /** Creates a new XmlDataStore from an existing one */
    public XmlDataStore(String name, XmlDataStore xmlDataStore){
        super(name);
    }

    /** Get whether this store automatically adds properties that aren't found */
    public boolean getAutoAddProperties(){
        return autoAddProperties;
    }

    /** Set whether this store automatically adds properties that aren't found */
    public void setAutoAddProperties(boolean autoAddProperties){
        this.autoAddProperties = autoAddProperties;
    }
    
    /** Set the signature data */
    public void setSignatureData(byte[] signatureData){
        this.signatureData = signatureData;        
        signed = true;
    }
    
    /** Get the signature data */
    public byte[] getSignatureData() throws XmlStorageException {
        if(signed){
            return signatureData;
        } else {
            throw new XmlStorageException("Object not signed");
        }               
    }
    
    /** Is this object signed */
    public boolean isSigned(){
        return signed;
    }
    
    /** Set whether to allow properties to be added or removed by the editor.
     * NB, this has no effect on adding / removing by code */
    public void setAllowAddRemove(boolean allowAddRemove){
        this.allowAddRemove = allowAddRemove;
    }
    
    /** Get whether or not to allow add remove */
    public boolean getAllowAddRemove(){
        return allowAddRemove;
    }
    
    /** Return value as an object */
    public Object getValue() {
        return this;
    }

    @Override
    public Object getDefaultValue() throws XmlStorageException {
        throw new XmlStorageException("Default value not supported for: " + getClass().getSimpleName());
    }
    
    
    /** Set value as an object */
    public void setValue(Object objectValue) throws XmlStorageException {
        if(objectValue instanceof XmlDataStore){
            // COPY in existing values
            
        } else {
            // Incorrect type
            throw new XmlStorageException("XmlDataStore requires an XmlDataStore in setValue(Object objectValue)");
        }
    }
    
    /** Return the type label */
    public String getTypeLabel() {
        return "XmlDataStore";
    }
    
    /** Add to an XML document */
    public void appendToXmlElement(Document xmlDocument, Element xmlElement, boolean includeDescription) throws XmlStorageException {
        // Create an Element to store everything in
        Element destinationElement = getBasicXmlElement(xmlDocument, includeDescription);
        
        // Add all the contents
        Enumeration contents = dataObjects.elements();
        while(contents.hasMoreElements()){
            ((XmlDataObject)contents.nextElement()).appendToXmlElement(xmlDocument, destinationElement, includeDescription);
        }
        
        // Add the security data if there is any
        if(signed){
            destinationElement.setAttribute("Signature", Base64.encodeBytes(signatureData));
        }

        // Add the auto add properties attribute
        if(autoAddProperties){
            destinationElement.setAttribute("AutoAdd", "true");
        } else {
            destinationElement.setAttribute("AutoAdd", "false");
        }
        
        xmlElement.appendChild(destinationElement);
    }
    
    /** Create contents from an XML document Element */
    public void buildFromXmlElement(Element xmlElement) throws XmlStorageException {
        // Extract the basic data from this element
        setBasicPropertiesFromXmlElement(xmlElement);
        
        // Get the signature data if it exists
        String sigText = xmlElement.getAttribute("Signature");
        if(sigText!=null && !sigText.isEmpty()){
            signatureData = Base64.decode(sigText);
            signed = true;
        } else {
            signed = false;
        }

        // Get the autoadd properties attribute if present
        String autoAdd = xmlElement.getAttribute("AutoAdd");
        if(autoAdd!=null){
            if(autoAdd.equalsIgnoreCase("true")){
                autoAddProperties = true;
            } else {
                autoAddProperties = false;
            }
        } else {
            autoAddProperties = false;
        }

        // Work through all of the Child elements
        Element currentElement;
        String typeName;
        XmlDataObject dataObject;
        NodeList childNodes = xmlElement.getChildNodes();
        for(int i=0;i<childNodes.getLength();i++){
            currentElement = (Element)childNodes.item(i);
            
            // Create a serialiser object from the data
            // type name
            typeName = currentElement.getAttribute("Type");
            if(typeName!=null){
                // Create serialiser
                dataObject = XmlDataObjectFactory.createDataObjectFromLabel(typeName);
                
                // Populate using this element
                dataObject.buildFromXmlElement(currentElement);
                
                // Add this to the data object hashtable
                add(dataObject);
                
            } else {
                throw new XmlStorageException("No type name defined for: " + currentElement.getTagName());
            }
        }
    }
    
    /** Get a copy of this object */
    public XmlDataObject getCopy() {
        XmlDataStore copy = new XmlDataStore(getName());
        
        // TODO: Add copies of each individual data object
        Enumeration objects = dataObjects.elements();
        while(objects.hasMoreElements()){
            copy.add(((XmlDataObject)objects.nextElement()).getCopy());
        }
        copy.setOptions(getOptions());
        return copy;
    }
    
    /** Does this object contain a parameter name */
    public boolean containsName(String name){
        return dataObjects.containsKey(name);
    }
    
    /** Return an Enumeration containing all the elements in this data store */
    public Enumeration elements(){
        return dataObjects.elements();
    }
    
    /** Return a serialiser object by name */
    public XmlDataObject getSerialiser(String objectName) throws XmlStorageException {
        if(dataObjects.containsKey(objectName)){
             return (XmlDataObject)dataObjects.get(objectName);
        } else {
            throw new XmlStorageException(objectName + " not found");
        }
    }
    
    /** Return a Vector containing the names of the properties in this store */
    public Vector getNames(){
        Vector names = new Vector();
        Enumeration objects = dataObjects.elements();
        while(objects.hasMoreElements()){
            names.add(((XmlDataObject)objects.nextElement()).getName());
        }
        return names;
    }
    
    /** Return the number of data objects in this store */
    public int size(){
        return dataObjects.size();
    }
    
    /** Clear everything from this data store */
    public void clear(){
        Enumeration elements = dataObjects.elements();
        XmlDataObject obj;
        while(elements.hasMoreElements()){
            obj = (XmlDataObject)elements.nextElement();
            if(obj instanceof XmlDataStore){
                ((XmlDataStore)obj).clear();
            }
        }
        dataObjects.clear();
    }
    
    /** Erase an existing name from the store if it exists */
    private void eraseExistingName(String name){
        if(dataObjects.containsKey(name)){
            dataObjects.remove(name);
        }
    }
    
    /** Does a specific property exist */
    public boolean propertyExists(String name) {
        return dataObjects.containsKey(name);
    }
    
    /** Get a property by name */
    public XmlDataObject get(String name) throws XmlStorageException {
        if(propertyExists(name)){
            return (XmlDataObject)dataObjects.get(name);
        } else {
            throw new XmlStorageException(name + " does not exist");
        }        
    }
    
    /** Add an XmlDataObject to this store */
    public void add(XmlDataObject dataObject) {
        eraseExistingName(dataObject.getName());
        dataObjects.put(dataObject.getName(), dataObject);
    }
        
    /** Return an object value */
    public Object objectValue(String name) throws XmlStorageException {
        return get(name).getValue();
    }
    
    /** Remove a value */
    public void remove(String name){
        if(containsName(name)){
            dataObjects.remove(name);
        }
    }   
    
    /** Do a Debugging printout */
    public void debugPrint(PrintWriter writer, int depth) {
        
        Enumeration e = dataObjects.elements();
        XmlDataObject object;
        StringBuffer indent = new StringBuffer();

        for(int i=0;i<depth;i++){
            indent.append(" ");
        }

        writer.println(indent.toString() + "{ XMLDataStore: " + getName());
        while(e.hasMoreElements()){
            object = (XmlDataObject)e.nextElement();
            if(object instanceof XmlDataStore){
                ((XmlDataStore)object).debugPrint(writer, depth + 1);
            } else if(object instanceof XmlStorableDataObject){
                writer.println(indent.toString() + "[ XmlStorableObject");
                ((XmlStorableDataObject)object).debugPrint(writer, depth + 1);
                writer.println(indent.toString() + "]");
            } else {
                writer.println(indent.toString() + "  " + object.toString());
            }
        }
        writer.println(indent.toString() + "}");
    }
    
    /** Copy in a set of properties */
    public void copyProperties(XmlDataStore store) throws XmlStorageException {
        Enumeration e = store.elements();
        XmlDataObject property;
        XmlDataObject originalProperty;
        
        while(e.hasMoreElements()){
            property = (XmlDataObject)e.nextElement();
            if(this.containsName(property.getName())){
                originalProperty = this.get(property.getName());
                
                // Set the description if it exists
                if(property.getDescription()!=null && (!property.getDescription().equalsIgnoreCase(""))){
                    originalProperty.setDescription(property.getDescription());
                }
                
                if(originalProperty.getTypeLabel().equals(property.getTypeLabel())){
                    if(originalProperty instanceof XmlDataStore){
                        ((XmlDataStore)originalProperty).copyProperties((XmlDataStore)property);
                    } else {
                        originalProperty.setValue(property.getValue());
                    }
                } else {
                    remove(property.getName());
                    add(property);
                }
                
            } else {
                add(property.getCopy());
            }
        }
    }
    
    /** Merge in a set of properties only copying in properties that have no direct
     * match in this store */
    public void mergeProperties(XmlDataStore store) throws XmlStorageException {
        Enumeration e = store.elements();
        XmlDataObject property;
        
        while(e.hasMoreElements()){
            property = (XmlDataObject)e.nextElement();
            if(!this.containsName(property.getName())){
                add(property.getCopy());
            }
        }        
    }
    
    // =========================================================================
    // Methods to easily add built in types
    // =========================================================================
    /** Add a String to this store */
    public void add(String name, String value) {
        if(value!=null){
            try{
                XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
                dataObject.setName(name);
                add(dataObject);
            } catch (Exception e){
                e.printStackTrace();
            }         
        }
    }
    
    /** Add a String to this store */
    public void add(String name, String value, String description){
        if(value!=null){
            try{
                XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
                dataObject.setName(name);
                dataObject.setDescription(description);
                add(dataObject);
            } catch (Exception e){
                e.printStackTrace();
            }        
        }
    }
    
    /** Add a String to this store */
    public void add(String name, String value, String description, String category){
        if(value!=null){
            try{
                XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
                dataObject.setName(name);
                dataObject.setDescription(description);
                dataObject.setCategory(category);
                add(dataObject);
            } catch (Exception e){
                e.printStackTrace();
            }        
        }
    }
    /** Add an XmlDataStore object to this store */
    public void add(String name, XmlDataStore value) {
        if(value!=null){
            try{
                eraseExistingName(name);
                value.setName(name);
                dataObjects.put(name, value);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    /** Add an XmlDataStore object to this store */
    public void add(String name, XmlDataStore value, String description) {
        if(value!=null){
            try{
                eraseExistingName(name);
                value.setName(name);
                value.setDescription(description);
                dataObjects.put(name, value);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    /** Add an XmlDataStore object to this store */
    public void add(String name, XmlDataStore value, String description, String category) {
        if(value!=null){
            try{
                eraseExistingName(name);
                value.setName(name);
                value.setDescription(description);
                value.setCategory(category);
                dataObjects.put(name, value);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }    
    
    /** Add a boolean value to this store */
    public void add(String name, boolean value) {
        try{
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            add(dataObject);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /** Add a boolean value to this store */
    public void add(String name, boolean value, String description) {
        try{
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            add(dataObject);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /** Add a boolean value to this store */
    public void add(String name, boolean value, String description, String category) {
        try{
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            dataObject.setCategory(category);
            add(dataObject);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /** Add a color value to this store */
    public void add(String name, Color color) {
        if(color!=null){
            try{
                XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(color);
                dataObject.setName(name);
                add(dataObject);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    /** Add a color value to this store */
    public void add(String name, Color color, String description) {
        if(color!=null){
            try{
                XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(color);
                dataObject.setName(name);
                dataObject.setDescription(description);
                add(dataObject);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    /** Add a color value to this store */
    public void add(String name, Color color, String description, String category) {
        if(color!=null){
            try{
                XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(color);
                dataObject.setName(name);
                dataObject.setDescription(description);
                dataObject.setCategory(category);
                add(dataObject);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }    
    
    /** Add a date to this store */
    public void add(String name, Date dateValue) {
        if(dateValue!=null){
            try{
                XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(dateValue);
                dataObject.setName(name);
                add(dataObject);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    /** Add a date to this store */
    public void add(String name, Date dateValue, String description) {
        if(dateValue!=null){
            try{
                XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(dateValue);
                dataObject.setName(name);
                dataObject.setDescription(description);
                add(dataObject);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    /** Add a date to this store */
    public void add(String name, Date dateValue, String description, String category) {
        if(dateValue!=null){
            try{
                XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(dateValue);
                dataObject.setName(name);
                dataObject.setDescription(description);
                dataObject.setCategory(category);
                add(dataObject);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }    
    
    /** Add a double to the data store */
    public void add(String name, double value) {
        try{
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            add(dataObject);
        } catch (Exception e){
        }
    }
    
    /** Add a double to the data store */
    public void add(String name, double value, String description) {
        try{
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            add(dataObject);
        } catch (Exception e){
        }
    }
    
    /** Add a double to the data store */
    public void add(String name, double value, String description, String category) {
        try{
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            dataObject.setCategory(category);
            add(dataObject);
        } catch (Exception e){
        }
    }    
    
    /** Add a Font to the data store */
    public void add(String name, Font fontValue) {
        if(fontValue!=null){
            try{
                XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(fontValue);
                dataObject.setName(name);
                add(dataObject);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    /** Add a Font to the data store */
    public void add(String name, Font fontValue, String description) {
        if(fontValue!=null){
            try{
                XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(fontValue);
                dataObject.setName(name);
                dataObject.setDescription(description);
                add(dataObject);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    /** Add a Font to the data store */
    public void add(String name, Font fontValue, String description, String category) {
        if(fontValue!=null){
            try{
                XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(fontValue);
                dataObject.setName(name);
                dataObject.setDescription(description);
                dataObject.setCategory(category);
                add(dataObject);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }    

    /** Add an int to the data store */
    public void add(String name, int value) {
        try{
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            add(dataObject);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /** Add an int to the data store */
    public void add(String name, int value, String description) {
        try{
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            add(dataObject);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
   /** Add an int to the data store */
    public void add(String name, int value, String description, String category) {
        try{
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            dataObject.setCategory(category);
            add(dataObject);
        } catch(Exception e){
            e.printStackTrace();
        }
    }    
    
    /** Add a long to the data store */
    public void add(String name, long value) {
        try{
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            add(dataObject);        
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /** Add a long to the data store */
    public void add(String name, long value, String description) {
        try{
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            add(dataObject);        
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /** Add a long to the data store */
    public void add(String name, long value, String description, String category) {
        try{
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            dataObject.setCategory(category);
            add(dataObject);        
        } catch (Exception e){
            e.printStackTrace();
        }
    }    
    
    /** Add an XmlStorable object to the data store */
    public void add(String name, XmlStorable value) throws XmlStorageException {
        if(value!=null){
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            add(dataObject);
        }
    }
    
    /** Add an XmlStorable object to the data store */
    public void add(String name, XmlStorable value, String description) throws XmlStorageException {
        if(value!=null){
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            add(dataObject);
        }
    }
    
    /** Add an XmlStorable object to the data store */
    public void add(String name, XmlStorable value, String description, String category) throws XmlStorageException {
        if(value!=null){
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            dataObject.setCategory(category);
            add(dataObject);
        }
    }    
    
    /** Add an object to the data store */
    public void add(String name, Object value) throws XmlStorageException {
        if(value!=null){
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            add(dataObject);
        }
    }
    
    /** Add an Xml document to the data store */
    public void add(String name, Document value) throws XmlStorageException {
        if(value!=null){
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            add(dataObject);
        }
    }
    
    /** Add an Xml document to the data store */
    public void add(String name, Document value, String description) throws XmlStorageException {
        if(value!=null){
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            add(dataObject);        
        }
    }
    
    /** Add an Xml document to the data store */
    public void add(String name, Document value, String description, String category) throws XmlStorageException {
        if(value!=null){
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            dataObject.setCategory(category);
            add(dataObject);        
        }
    }    
    
    /** Add a byte array to the data store */
    public void add(String name, byte[] value) throws XmlStorageException {
        if(value!=null){
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            add(dataObject);
        }
    }
    
    /** Add a byte array to the data store */
    public void add(String name, byte[] value, String description) throws XmlStorageException {
        if(value!=null){
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            add(dataObject);
        }
    }
    
    /** Add a byte array to the data store */
    public void add(String name, byte[] value, String description, String category) throws XmlStorageException {
        if(value!=null){
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            dataObject.setCategory(category);
            add(dataObject);
        }
    }    
    
    /** Add a file to the data store */
    public void add(String name, File value) {
        if(value!=null){
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            add(dataObject);
        }
    }
    
    /** Add a file to the data store */
    public void add(String name, File value, String description) {
        if(value!=null){
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            add(dataObject);
        }
    }
    
    /** Add a file to the data store */
    public void add(String name, File value, String description, String category) {
        if(value!=null){
            XmlDataObject dataObject = XmlDataObjectFactory.createDataObject(value);
            dataObject.setName(name);
            dataObject.setDescription(description);
            dataObject.setCategory(category);
            add(dataObject);
        }
    }    
    
    // =========================================================================
    // Methods to easily access built in types
    // =========================================================================
    
    /** Return an XmlDocument value */
    public Document xmlDocumentValue(String name) throws XmlStorageException {
        try {
            return ((XmlXmlDataObject)get(name)).xmlDocumentValue();
        } catch (Exception e){
            throw new XmlStorageException("Cannot get xml document: " + e.getMessage());
        }
    }
    
    /** Read a string */
    public String stringValue(String name, String defaultValue) {
        try{
            return ((XmlStringDataObject)get(name)).stringValue();
        } catch (Exception e){
            if(autoAddProperties){
                add(name, defaultValue);
            }
            return defaultValue;
        }
    }
    
    /** Get an XmlDataStore */
    public XmlDataStore xmlDataStoreValue(String name) throws XmlStorageException {
        try { 
            return (XmlDataStore)get(name);
        } catch (Exception e){
            throw new XmlStorageException("Cannot get XmlDataStore: " + e.getMessage());
        }
    }
    
    /** Get a boolean value */
    public boolean booleanValue(String name, boolean defaultValue) {
        try{
            return ((XmlBooleanDataObject)get(name)).booleanValue();
        } catch (Exception e){
            if(autoAddProperties){
                add(name, defaultValue);
            }
            return defaultValue;
        }
    }
    
    /** Get a color value */
    public Color colorValue(String name, Color defaultValue) {
        try { 
            return ((XmlColorDataObject)get(name)).colorValue();
        } catch (Exception e){
            if(autoAddProperties){
                add(name, defaultValue);
            }
            return defaultValue;
        }         
    }
    
    /** Return a date value */
    public Date dateValue(String name, Date defaultValue) {
        try {
            return ((XmlDateDataObject)get(name)).dateValue();
        } catch (Exception e){
            if(autoAddProperties){
                add(name, defaultValue);
            }
            return defaultValue;
        }
    }
    
    /** Return a double value */
    public double doubleValue(String name, double defaultValue) {
        try {
            return ((XmlDoubleDataObject)get(name)).doubleValue();
        } catch (Exception e){
            if(autoAddProperties){
                add(name, defaultValue);
            }
            return defaultValue;
        }
    }
    
    /** Return a font value */
    public Font fontValue(String name) throws XmlStorageException {
        try {
            return ((XmlFontDataObject)get(name)).fontValue();
        } catch (Exception e) {
            throw new XmlStorageException("Cannot get Font: " + e.getMessage());
        }
    }
    
    /** Return an int value */
    public int intValue(String name, int defaultValue) {
        try {
            XmlDataObject obj = get(name);
            if(obj instanceof XmlIntegerDataObject){
                return ((XmlIntegerDataObject)get(name)).intValue();
            } else if(obj instanceof XmlLongDataObject){
                return (int)((XmlLongDataObject)get(name)).longValue();
            } else {
                if(autoAddProperties){
                    add(name, defaultValue);
                }
                return defaultValue;
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /** Return a long value */
    public long longValue(String name, long defaultValue) {
        try {
            XmlDataObject obj = get(name);
            if(obj instanceof XmlIntegerDataObject){
                return (long)((XmlIntegerDataObject)get(name)).intValue();
            } else if(obj instanceof XmlLongDataObject){
                return ((XmlLongDataObject)get(name)).longValue();
            } else {
                if(autoAddProperties){
                    add(name, defaultValue);
                }
                return defaultValue;
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /** Return a byte array value */
    public byte[] byteArrayValue(String name) throws XmlStorageException {
        try {
            return ((XmlByteArrayDataObject)get(name)).byteArrayValue();
        } catch (Exception e){
            throw new XmlStorageException("Cannot get byteArrayValue: " + e.getMessage());
        }
    }
    
    /** Return a byte[] property as an InputStream */
    public InputStream inputStreamValue(String name) throws XmlStorageException {
        try {
            return ((XmlByteArrayDataObject)get(name)).getInputStream();
        } catch (Exception e){
            throw new XmlStorageException("Cannot get byteArrayValue: " + e.getMessage());
        }        
    }
    
    /** Get an xmlStorable object */
    public Object xmlStorableValue(String name) throws XmlStorageException {
        try {
            return ((XmlStorableDataObject)get(name)).getValue();
        } catch (Exception e){
            throw new XmlStorageException("Cannot get xmlStorableValue: " + e.getMessage());
        }
    }
    
    /** Get a file value */
    public File fileValue(String name) throws XmlStorageException {
        try {
            return ((XmlFileDataObject)get(name)).fileValue();
        } catch (Exception e){
            throw new XmlStorageException("Cannot get File value: " + e.getMessage());
        }
    }
    
    /** Get a file value */
    public File fileValue(String name, String defaultPath) {
        try {
            return ((XmlFileDataObject)get(name)).fileValue();
        } catch (Exception e){
            if(autoAddProperties){
                add(name, new File(defaultPath));
            }
            return new File(defaultPath);
        }
    }
    
    /** Can this object parse a string */
    public static boolean canParseString() {
        return false;
    }
    
    /**
     * Parse a string name
     */
    public static XmlDataObject parseString(String name) throws XmlStorageException {
        throw new XmlStorageException("XmlDataStore does not support parseString");
    }
    
    /** Modify an existing value using a string */
    public void modifyValue(String strName, String newValue) throws XmlStorageException {
        XmlDataObject value = get(strName);
        if(XmlDataObjectFactory.canDataObjectParseString(value.getTypeLabel())){
            add(XmlDataObjectFactory.createDataObject(value.getTypeLabel(), newValue, strName));
        } else {
            throw new XmlStorageException(strName + " cannot parse a String value");
        }  
    }

    /** Get a property value as a string. This method returns null if the property cannot
     * be represented as a string */
     public String getPropertyString(String name) throws XmlStorageException {

        XmlDataObject dataObject = get(name);

        if(dataObject instanceof XmlBooleanDataObject){
            return Boolean.toString(((XmlBooleanDataObject)dataObject).booleanValue());

        } else if(dataObject instanceof XmlDateDataObject){
            return null;

        } else if(dataObject instanceof XmlDoubleDataObject){
            return Double.toString(((XmlDoubleDataObject)dataObject).doubleValue());

        } else if(dataObject instanceof XmlFileDataObject){
            return ((XmlFileDataObject)dataObject).fileValue().getPath();

        } else if(dataObject instanceof XmlIntegerDataObject){
            return Integer.toString(((XmlIntegerDataObject)dataObject).intValue());

        } else if(dataObject instanceof XmlLongDataObject){
            return Long.toString(((XmlLongDataObject)dataObject).longValue());

        } else if(dataObject instanceof XmlStringDataObject){
            return ((XmlStringDataObject)dataObject).stringValue();
        } else {
            return null;
        }

     }
     
     /** Get the default value of a property as a string */
     public String getDefaultPropertyString(String name) throws XmlStorageException {
        XmlDataObject dataObject = get(name);

        if(dataObject instanceof XmlBooleanDataObject){
            return Boolean.toString(((XmlBooleanDataObject)dataObject).getDefaultBooleanValue());

        } else if(dataObject instanceof XmlDoubleDataObject){
            return Double.toString(((XmlDoubleDataObject)dataObject).getDefaultDoubleValue());

        } else if(dataObject instanceof XmlIntegerDataObject){
            return Integer.toString(((XmlIntegerDataObject)dataObject).getDefaultIntValue());

        } else if(dataObject instanceof XmlLongDataObject){
            return Long.toString(((XmlLongDataObject)dataObject).getDefaultLongValue());

        } else if(dataObject instanceof XmlStringDataObject){
            return ((XmlStringDataObject)dataObject).getDefaultStringValue();
            
        } else {
            return null;
        }         
     }

    @Override
    public boolean containsNonDefaultValue() {
        if(dataObjects.size()>0){
            return true;
        } else {
            return false;
        }
    }
}
