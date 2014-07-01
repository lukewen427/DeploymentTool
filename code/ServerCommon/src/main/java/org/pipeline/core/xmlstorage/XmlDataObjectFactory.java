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

import java.awt.*;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class is used to produce new XmlDataObject when required
 * @author  hugo
 */
public class XmlDataObjectFactory {
    /** Built in type labels */
    private static final String[] typeLabels = {"String",
                                                                    "Boolean",
                                                                    "Color",
                                                                    "Date",
                                                                    "Double",
                                                                    "Font",
                                                                    "PlafFont", 
                                                                    "Integer",
                                                                    "Long",
                                                                    "XmlDataStore",
                                                                    "XmlStorable",
                                                                    "XmlAutoStorable",
                                                                    "XMLDocument",
                                                                    "ByteArray",
                                                                    "File"};
                                                                    
                                                                    
    /** Built in type classes */
    private static final Class[] typeClasses = {    String.class,
                                                                   Boolean.class,
                                                                   Color.class,
                                                                   Date.class,
                                                                   Double.class,
                                                                   Font.class,
                                                                   javax.swing.plaf.FontUIResource.class,
                                                                   Integer.class,
                                                                   Long.class,
                                                                   XmlDataStore.class,
                                                                   XmlStorable.class,
                                                                   XmlAutoStorable.class,
                                                                   Document.class,
                                                                   byte[].class,
                                                                   File.class};
                                                                   
    
    /** Built in serializer classes */
    private static final Class[] serializerClasses = {XmlStringDataObject.class,
                                                                    XmlBooleanDataObject.class,
                                                                    XmlColorDataObject.class,
                                                                    XmlDateDataObject.class,
                                                                    XmlDoubleDataObject.class,
                                                                    XmlFontDataObject.class,
                                                                    XmlFontDataObject.class,
                                                                    XmlIntegerDataObject.class,
                                                                    XmlLongDataObject.class,
                                                                    XmlDataStore.class,
                                                                    XmlStorableDataObject.class,
                                                                    XmlAutoStorableDataObject.class,
                                                                    XmlXmlDataObject.class,
                                                                    XmlByteArrayDataObject.class,
                                                                    XmlFileDataObject.class};
                                                                    
    
    /** List of recognised data types */
    private static IntialisedHashtable recognisedTypes = new IntialisedHashtable(typeClasses, serializerClasses);
    
    /** List of recognised types indexed by label */
    private static IntialisedHashtable labelledTypes = new IntialisedHashtable(typeLabels, serializerClasses);
            
    /** Is a specific data type persistable */
    public static boolean isDataTypeRecognised(Class dataType) {
            return recognisedTypes.containsKey(dataType);
    }
    
    /** Register an additional type of XmlDataObject */
    public static void registerDataType(Class dataType, String label, Class xmlSerialiser) throws XmlStorageException {
        if(!recognisedTypes.containsKey(dataType) && !labelledTypes.containsKey(label)){
            recognisedTypes.put(dataType, xmlSerialiser);
            labelledTypes.put(label, xmlSerialiser);
        } else {
            throw new XmlStorageException("Data type is already registered: " + dataType.getName());
        }
    }
    
    /** Unregister an additional type of XmlDataObject */
    public static void unregisterDataType(Class dataType, String label) {
        if(recognisedTypes.containsKey(dataType) && labelledTypes.containsKey(label)){            
            labelledTypes.remove(label);
            recognisedTypes.remove(dataType);
        }
    }
    
    /** Create a new XmlDataObject using a String type, String value to parse and a name */
    public static XmlDataObject createDataObject(String type, String value, String name) throws XmlStorageException {
        Class typeClass = getClassFromLabel(type);

        // Work out if the object can parse a boolean
        try{
            if(canDataObjectParseString(type)){
                Class[] paramTypes = new Class[]{String.class};
                Object[] params = new Object[]{value};
                
                Method parseMethod = typeClass.getMethod("parseString", paramTypes);
                XmlDataObject obj = (XmlDataObject)parseMethod.invoke(null, params);
                obj.setName(name);
                return obj;
                
            } else {
                throw new XmlStorageException(type + " cannot parse a string value");
            }
        } catch(Exception e){ 
            throw new XmlStorageException(e.getMessage());
        }
    }
    
    /** Create a new XmlDataObject using a String type, String value to parse and a name */
    public static XmlDataObject createDataObject(String type, String value, String name, String defaultValue) throws XmlStorageException {
        Class typeClass = getClassFromLabel(type);

        // Work out if the object can parse a boolean
        try{
            if(canDataObjectParseString(type)){
                Class[] paramTypes = new Class[]{String.class};
                Object[] params = new Object[]{value};
                
                Method parseMethod = typeClass.getMethod("parseString", paramTypes);
                XmlDataObject obj = (XmlDataObject)parseMethod.invoke(null, params);
                obj.setName(name);
                
                obj.setDefaultAsString(defaultValue);
                return obj;
                
            } else {
                throw new XmlStorageException(type + " cannot parse a string value");
            }
        } catch(Exception e){ 
            throw new XmlStorageException(e.getMessage());
        }
    }
    
    /** Can a data object parse a string */
    public static boolean canDataObjectParseString(String type) throws XmlStorageException {
        Class typeClass = getClassFromLabel(type);
        try{
            Method canParseMethod = typeClass.getMethod("canParseString", (Class[])null);
            return ((Boolean)canParseMethod.invoke(null, (Object[])null)).booleanValue();            
            
        } catch (Exception e){
            throw new XmlStorageException(e.getMessage());
        }
    }
    
    /** Create a new XmlDataObject for a generic object */
    public static XmlDataObject createDataObject(Object value) throws XmlStorageException {
        Hashtable rt = recognisedTypes;
        if(recognisedTypes.containsKey(value.getClass())) {
            try{
                Class serialiserClass = (Class)recognisedTypes.get(value.getClass());
                XmlDataObject serialiserObject = (XmlDataObject)serialiserClass.newInstance();
                serialiserObject.setValue(value);
                return serialiserObject;
                
            } catch (Exception e) {
                throw new XmlStorageException("Error instantiating class: " + e.getMessage());
            }
            
        } else if (value instanceof XmlStorable) {
            // Special case for XmlStorable
            XmlStorableDataObject serialiserObject = new XmlStorableDataObject();
            serialiserObject.setValue((XmlStorable)value);
            return (XmlDataObject)serialiserObject;
            
        } else if(value instanceof XmlAutoStorable){
            // Special case of XmlAutoStorable
            return null;
            
        } else {
           throw new XmlStorageException("Unregistered object class: " + value.getClass().getName());
        }
    }
    
    /** Return a Vector containing all the recognised data types. NB this does not list XmlDataStores and XmlStorable */
    public static Vector listRecognisedTypes(){
        Vector recognisedTypes = new Vector();

        // Add the labelled types
        Enumeration extras = labelledTypes.keys();
        while(extras.hasMoreElements()){
            recognisedTypes.add(extras.nextElement().toString());
        }
        return recognisedTypes;
    }
    
    /** Return the Class of a recognised type using a label string */
    public static Class getClassFromLabel(String label) throws XmlStorageException {
        if(labelledTypes.containsKey(label)) {
            return (Class)labelledTypes.get(label);
        } else {
            throw new XmlStorageException("Unregistered label: " + label);
        }
    }
    
    /** Create a named empty data object from a label string and a name */
    public static XmlDataObject createNamedDataObjectFromLabel(String label, String name) throws XmlStorageException {
        XmlDataObject obj = createDataObjectFromLabel(label);
        obj.setName(name);
        return obj;
    }
    
    /** Create a new XmlDataObject using a label string */
    public static XmlDataObject createDataObjectFromLabel(String label) throws XmlStorageException {
        if(labelledTypes.containsKey(label)) {
            try{
                Class serialiserClass = (Class)labelledTypes.get(label);
                XmlDataObject serialiserObject = (XmlDataObject)serialiserClass.newInstance();
                return serialiserObject;

            } catch (Exception e) {
                throw new XmlStorageException("Error instantiating class: " + e.getMessage());
            }

        } else {
            throw new XmlStorageException("Unregistered label: " + label);
        }
    }
    
    // =========================================================================
    // Methods to create simple types, such as int, boolean, double etc
    // =========================================================================  
    
    /** Create a new XmlDataObject for a byte[] array */
    public static XmlDataObject createDataObject(byte[] value) {
        XmlByteArrayDataObject byteSerializer = new XmlByteArrayDataObject();
        byteSerializer.setValue(value);
        return byteSerializer;
    }
    
    /** Create a new XmlDataObject for a boolean */
    public static XmlDataObject createDataObject(boolean value) {
        XmlBooleanDataObject booleanSerializer = new XmlBooleanDataObject();
        booleanSerializer.setBooleanValue(value);
        return booleanSerializer;
    }
        
    /** Create a new XmlDataObject for a double */
    public static XmlDataObject createDataObject(double value) {
        XmlDoubleDataObject doubleSerializer = new XmlDoubleDataObject();
        doubleSerializer.setDoubleValue(value);
        return doubleSerializer;
    }    
    
    /** Create a new XmlDataObject for an int */
    public static XmlDataObject createDataObject(int value) {
        XmlIntegerDataObject intSerializer = new XmlIntegerDataObject();
        intSerializer.setIntValue(value);
        return intSerializer;
    }
    
    /** Create a new XmlDataObject for a long */
    public static XmlDataObject createDataObject(long value) {
        XmlLongDataObject longSerializer = new XmlLongDataObject();
        longSerializer.setLongValue(value);
        return longSerializer;
    }
    
    /** Create a new XmlDataObject for a file */
    public static XmlDataObject createDataObject(File value){
        XmlFileDataObject fileSerializer = new XmlFileDataObject();
        fileSerializer.setFileValue(value);
        return fileSerializer;
    }
    
    /** Create a new XmlDataObject for an XmlDataStore */
    public static XmlDataObject createDateObject(XmlDataStore value) {
        return value;
    }
}
