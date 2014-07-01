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

package org.pipeline.core.xmlstorage.io;

import org.pipeline.core.xmlstorage.XmlDataObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * This class does all the basic read write operations for an XmlDataStore
 * object. It keeps track of version numbers, info tags etc etc
 * @author  $Author: hugo $
 * @version $Revision: 1.2 $
 */
public class XmlDataStoreReadWriter {
    /** Local data store */
    private XmlDataStore dataStore = null;
    
    /** Created XML document */
    private Document xmlDocument = null;
       
    /** Should the description be included when saving the document */
    private boolean descriptionIncluded = false;
    
    /** Creates a new blank instance of XmlDataStoreReadWriter */
    public XmlDataStoreReadWriter(){
    }
    
    /** Creates a new instance of XmlDataStoreReadWriter */
    public XmlDataStoreReadWriter(XmlDataStore dataStore)  throws XmlStorageException {
        this.dataStore = dataStore;
    }
 
    /** Creates a new instance of XmlDataStoreReadWriter */
    public XmlDataStoreReadWriter(Document xmlDocument) throws XmlStorageException {
        this.xmlDocument = xmlDocument;
        parseXmlDocument(xmlDocument);
    }
            
    /** Set whether to include the description */
    public void setDescriptionIncluded(boolean descriptionIncluded){
        this.descriptionIncluded = descriptionIncluded;
    }
    
    /** Should descriptions be included when saving the data */
    public boolean isDescriptionIncluded(){
        return descriptionIncluded;
    }

    /** Parse an XML document */
    public void parseXmlDocument(Document xmlDocument) throws XmlStorageException {
        try {
            // Get top level element
            Element documentElement = xmlDocument.getDocumentElement();
            // Get rid of all text elements full of whitespaces
            removeWhiteSpaceText(documentElement);
            
            if (documentElement.getTagName().equalsIgnoreCase(XmlDataObject.DOCUMENT_ROOT_NODE)) {
                // Extract the correct element
                NodeList contentsNodes = documentElement.getChildNodes();
                Element currentElement;

                for (int i = 0; i < contentsNodes.getLength() ; i++) {
                    currentElement = (Element)contentsNodes.item(i);
                    
                    // If this is the Data element, create an XmlDataStore
                    boolean dataElementFound = false;
                    if (currentElement.getTagName().equalsIgnoreCase("data")) {
                        // There should be a single child of data, which
                        // is what we use to build the XmlDataStore
                        if (dataElementFound == false) {
                            // Get the first child
                            if (currentElement.getChildNodes().getLength() == 1) {
                                Element dataElementChild = (Element)currentElement.getFirstChild();
                                dataElementFound = true;

                                // Get rid of anything lying around in here
                                if (this.dataStore!=null) {
                                    this.dataStore.clear();
                                    this.dataStore = null;
                                }

                                // Populate with data
                                this.dataStore = new XmlDataStore();
                                this.dataStore.buildFromXmlElement(dataElementChild);

                            } else {
                                // Already have a data 
                                throw new XmlStorageException("Only one XmlDataStore element permitted");
                            }
                        }
                    } else {
                        // Another top level element, treat
                        // accordingly
                    }
                }
            } else {
                throw new XmlStorageException("Not a valid document format");
            }
        } catch (Exception e){
            throw new XmlStorageException("Error parsing XML document: " + e.getMessage());
        }
    }
    
    /** Create an XML document. Subclasses can add information to this document if required */
    public Document createXmlDocument() throws XmlStorageException {
        if(this.dataStore!=null){
            try{
                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                documentFactory.setNamespaceAware(true);
                DocumentBuilder builder = documentFactory.newDocumentBuilder();
                this.xmlDocument = builder.newDocument();  
                
                Element rootElement = this.xmlDocument.createElement(XmlDataObject.DOCUMENT_ROOT_NODE);
                this.xmlDocument.setXmlStandalone(true);
                this.xmlDocument.appendChild(rootElement);

                // Create a Data element to save the data store to
                Element dataElement = this.xmlDocument.createElement("Data");
                rootElement.appendChild(dataElement);
                this.dataStore.appendToXmlElement(this.xmlDocument, dataElement, descriptionIncluded);

                return this.xmlDocument;
                
            } catch (Exception e){
                throw new XmlStorageException("Error creating XML document: " + e.getMessage());
            }   
            
        } else {
            throw new XmlStorageException("No DataStore available");
        }
    }
    
    /** Return a reference to the XML document stored in this class */
    public Document getDocument(){
        if(xmlDocument!=null){
            xmlDocument.setXmlStandalone(true);
            return xmlDocument;
        } else {
            try {
                return createXmlDocument();
            } catch (Exception e) {
                return null;
            }
        }
    }
    
    /** Return a reference to the XmlDataStore object contained in this class */
    public XmlDataStore getDataStore(){
        if(dataStore==null){
            try {
                parseXmlDocument(xmlDocument);
            } catch (Exception e){
                
            }
        }
        return this.dataStore;
    }
    
    /** Transform the XML document to a single string */
    public String toString(){
        try{
            if(this.xmlDocument!=null){
                // Write XML to a string
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer t = tf.newTransformer();
                DOMSource source = new DOMSource(this.xmlDocument);
                StreamResult result = new StreamResult(stream);
                t.transform(source, result);                
                stream.flush();
                return stream.toString();                
                
            } else {
                return super.toString();
            }
            
        } catch (Exception e){
            return super.toString();
        }
    }
    
    /** Parse a single string to form the XML document */
    public void parseString(String xml) throws XmlStorageException {
        // Try to parse XML contained in the Stream
        try {
            DocumentBuilderFactory dbf = null;
            DocumentBuilder db = null;
            Document doc = null;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintWriter print = new PrintWriter(out);
            print.print(xml);
            print.flush();
            print.close();
            ByteArrayInputStream oStream = new ByteArrayInputStream(out.toByteArray());
            
            dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setNamespaceAware(true);
            dbf.setCoalescing(true);
            db = dbf.newDocumentBuilder();
            doc = db.parse(oStream);        
            parseXmlDocument(doc);
            
        } catch (Exception e){
            throw new XmlStorageException("Error parsing XML string: " + e.getMessage());
        }
    }

    /**
     * Recursively removes all whitespace nodes in the element structure
     * except when a whitespace node is the only child of the element.
     * 
     * @param element - an XML element to be cleaned.
     */
    private void removeWhiteSpaceText(Element element) {
        Node n = element.getFirstChild();
        while (n != null) {
            Node next = n.getNextSibling();

            if (n instanceof Text) {
                if ("".equals(n.getTextContent().trim()) && element.getChildNodes().getLength() > 1) {
                    element.removeChild(n);
                }
            }
            else if (n instanceof Element) {
                removeWhiteSpaceText((Element)n);
            }

            n = next;
        }
    }
}
