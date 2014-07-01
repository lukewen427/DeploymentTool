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

import org.pipeline.core.xmlstorage.Base64;
import org.pipeline.core.xmlstorage.XmlDataObject;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;

/**
 * This class stores a byte[] array as a base64 string
 * that can be stored in an XmlDataStore.
 * @author hugo
 */
public class XmlByteArrayDataObject extends XmlDataObject {
    static final long serialVersionUID = 5040748945287996494L;

    /** Data stored */
    private byte[] data;

    /** Creates a new instance of XmlByteArrayDataObject */
    public XmlByteArrayDataObject() {
        super();
        defaultSupported = false;
        data = new byte[0];
    }

    /** Creates a new instance of XmlByteArrayDataObject with a name */
    public XmlByteArrayDataObject(String name) {
        super(name);
        data = new byte[0];
    }

    /** Creates a new instance of XmlByteArrayDataObject with a name and value */
    public XmlByteArrayDataObject(String name, byte[] data) {
        super(name);
        this.data = data;
    }

    /** Get a copy of this object */
    public XmlDataObject getCopy(){
        XmlDataObject copy = new XmlByteArrayDataObject(getName());
        byte[] copyData = new byte[data.length];
        for(int i=0;i<data.length;i++){
            copyData[i]=data[i];
        }
        ((XmlByteArrayDataObject)copy).setValue(copyData);
        copy.setDescription(getDescription());
        copy.setCategory(getCategory());
        copy.setOptions(getOptions());
        copy.setExposedProperty(isExposedProperty());
        copy.setExposedName(getExposedName());
        return copy;             
    }

    /** Get the data type label */
    public String getTypeLabel(){
        return "ByteArray";
    }

    /** Get the value */
    public Object getValue(){
        return data;
    }

    /** Get the value as a byte array */
    public byte[] byteArrayValue() {
        return data;
    }

    /** Set the value */
    public void setValue(Object value) throws XmlStorageException {
        if(value instanceof byte[]){
            setValue((byte[])value);
        } else {
            throw new XmlStorageException("XmlByteArrayDataObject requires byte[] in setValue");
        }
    }

    /** Set the value as a byte array */
    public void setValue(byte[] data){
        this.data = data;
    }

    /** Save the contents of this parameter as a child of an xmlElement within 
     * an xmlDocument */
    public void appendToXmlElement(Document xmlDocument, Element xmlElement, boolean includeDescription) throws XmlStorageException {
        try{
            Element element = getBasicXmlElement(xmlDocument, includeDescription);
            element.setAttribute("Value", Base64.encodeBytes(data));
            xmlElement.appendChild(element);
        } catch (Exception e){
            throw new XmlStorageException("Error adding byte array to XML document: " + e.getMessage());
        }
    }

    /** Build this parameter from an xmlElement */
    public void buildFromXmlElement(Element xmlElement) throws XmlStorageException {
        try{
            // Set basic properties. This will throw an Exception if the
            // element is incorrectly formed
            setBasicPropertiesFromXmlElement(xmlElement);

            // Set the string value
            data = Base64.decode(xmlElement.getAttribute("Value"));

        } catch (Exception e){
            throw new XmlStorageException("Error recreating XmlStringDataObject: " + e.getMessage());
        }
    }    

    /** Can this object parse a string */
    public static boolean canParseString() {
        return true;
    }

    /** Parse a string */
    public static XmlDataObject parseString(String strValue) throws XmlStorageException {
        XmlByteArrayDataObject newObject = new XmlByteArrayDataObject();
        newObject.setValue(Base64.decode(strValue));
        return newObject;
    }

    /** Get data as an InputStream */
    public InputStream getInputStream(){
        return new ByteArrayInputStream(data);
    }

    /** Load a File */
    public void loadFile(File file) throws XmlStorageException
    {
        BufferedInputStream stream = null;
        try {
            stream = new BufferedInputStream(new FileInputStream(file));
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int val;
            val = stream.read();
            while (val!=-1) {
                buffer.write(val);
                val = stream.read();
            }
            data = buffer.toByteArray();
        } catch (Exception e){
            data = new byte[0];
            throw new XmlStorageException("Error loading file: " + e.getMessage());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception x) {
                    // Ignore any exceptions on stream.close();
                }
            }
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