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
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;

/**
 * This data type stores an XmlDocument in an XmlDataStore
 * and allows it to be read as a DOM document or as a 
 * plain String.
 * @author hugo
 */
public class XmlXmlDataObject extends XmlDataObject {
    static final long serialVersionUID = 3633257435391883797L;
    
    Document xmlDocument = null;
    
    /** Creates a new instance of XmlXmlDataObject */
    public XmlXmlDataObject() {
        super();
    }
    
    /** Creates a new instance of XmlXmlDataObject with a name */
    public XmlXmlDataObject(String name){
        super(name);
    }
    
    /** Creates a new instance of XmlXmlDataObject with a name and a value */
    public XmlXmlDataObject(String name, String value){
        super(name);
        try {
            setStringValue(value);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /** Creates a new instance of XmlXmlDataObject with a name and document value */
    public XmlXmlDataObject(String name, Document xmlDocument){
        super(name);
        this.xmlDocument = xmlDocument;
    }
    
    public void setValue(Object objectValue) throws XmlStorageException {
        if(objectValue instanceof Document){
            xmlDocument = (Document)objectValue;
            
        } else if(objectValue instanceof String){
            setStringValue((String)objectValue);
            
        } else {
            throw new XmlStorageException("Invalid data type for XmlXmlDataObject");
        }
    }

    /** Construct this object from an XmlElement */
    public void buildFromXmlElement(Element xmlElement) throws XmlStorageException {
        try {
            NodeList list = xmlElement.getChildNodes();
            if(list.getLength()==1){
                String value = list.item(0).getNodeValue();
                if(value.equals("_NULL_XML_DOCUMENT_")){
                    xmlDocument = null;
                } else {
                    setStringValue(value);
                }
            } else {
                throw new XmlStorageException("Incorrect formatting in XmlDocument CDATA section");
            }
            
        } catch (Exception e){
            throw new XmlStorageException("Cannot recreate XmlDocument from XML data");
        }
    }

    /** Save this object to an XmlElement */
    public void appendToXmlElement(Document outputXmlDocument, Element xmlElement, boolean includeDescription) throws XmlStorageException {
        try{
            Element element = getBasicXmlElement(outputXmlDocument, includeDescription);
            if(xmlDocument!=null){
                element.appendChild(outputXmlDocument.createTextNode(stringValue()));
            } else {
                element.appendChild(outputXmlDocument.createTextNode("_NULL_XML_DOCUMENT_"));
            }
            xmlElement.appendChild(element);
            
        } catch (Exception e){
            throw new XmlStorageException("Error adding XmlStringDataObject to XML document: " + e.getMessage());
        }        
    }

    /** Get the value as an object */
    public Object getValue() {
        return xmlDocument;
    }

    /** Get the type label */
    public String getTypeLabel() {
        return "XMLDocument";
    }

    /** Get a copy of this document */
    public XmlDataObject getCopy() {
        XmlDataObject copy = new XmlXmlDataObject(getName(), xmlDocument);
        copy.setDescription(getDescription());
        copy.setCategory(getCategory());
        copy.setOptions(getOptions());
        copy.setExposedProperty(isExposedProperty());
        copy.setExposedName(getExposedName());   
        return copy;
    }
    
    /** Parse a String value */
    public static XmlDataObject parseString(String strValue) throws XmlStorageException {
        XmlXmlDataObject newObject = new XmlXmlDataObject();
        newObject.setStringValue(strValue);
        return newObject;        
    }
    
    /** Does this property support string parsing */
    public static boolean canParseString(){
        return true;
    }    
    
    /** Get the string value of this data object */
    public String stringValue() throws XmlStorageException {
        try{
            if(xmlDocument!=null){
                // Write XML to a string
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer t = tf.newTransformer();
                DOMSource source = new DOMSource(xmlDocument);
                StreamResult result = new StreamResult(stream);
                t.transform(source, result);                
                stream.flush();
                return stream.toString();                
                
            } else {
                throw new XmlStorageException("No XML document available");
            }
            
        } catch (Exception e){
            throw new XmlStorageException("Cannot output Xml document to a String: " + e.getMessage());
        }
    }
    
    /** Get the Xml document value of this data object */
    public Document xmlDocumentValue(){
        return xmlDocument;
    }
    
    /** Set the value of this data object as a String */
    public void setStringValue(String value) throws XmlStorageException {
        try {
            DocumentBuilderFactory dbf = null;
            DocumentBuilder db = null;
            Document doc = null;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            PrintWriter printStream = new PrintWriter(outStream);
            printStream.print(value);
            printStream.flush();
            printStream.close();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outStream.toByteArray());
            
            dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setNamespaceAware(true);
            dbf.setCoalescing(true);
            db = dbf.newDocumentBuilder();
            xmlDocument = db.parse(inputStream);                
            
        } catch (Exception e){
            xmlDocument = null;
            throw new XmlStorageException("Cannot parse String XmlDocument: " + e.getMessage());
        }
    }
    
    /** Set the Xml document value of this data object */
    public void setDocumentValue(Document xmlDocument){
        this.xmlDocument = xmlDocument;
    }
    
    /** Read data from an XML File */
    public void readXmlFromFile(File file) throws XmlStorageException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setCoalescing(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            xmlDocument = db.parse(file);         
        } catch (Exception e){
            throw new XmlStorageException("Error importing file: " + e.getMessage());
        }
    }
    
    /** Override the toString method */
    public String toString(){
        if(xmlDocument!=null){
            return "XML Document";
        } else {
            return "No data";
        }
    }
    
    @Override
    public Object getDefaultValue() throws XmlStorageException {
        throw new XmlStorageException("Default value not supported for: " + getClass().getSimpleName());
    }   

    @Override
    public boolean containsNonDefaultValue() {
        if(xmlDocument!=null){
            return true;
        } else {
            return false;
        }
    }
}
