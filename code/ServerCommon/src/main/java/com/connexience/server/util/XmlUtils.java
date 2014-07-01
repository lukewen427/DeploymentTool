/**
 * e-Science Central
 * Copyright (C) 2008-2013 School of Computing Science, Newcastle University
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package com.connexience.server.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * This class provides some XML utilities for serializing / deserialising objects
 * and collections of objects using the java beans XML serialization code
 * @author nhgh
 */
public class XmlUtils {

    private static final XmlUtils INSTANCE = new XmlUtils();
    private static boolean externalClassLoader = false;
    private static ClassLoader externalLoader;

    /** Set the class loader correctly */
    public static void setupClassLoader(ClassLoader loader) {
        externalLoader = loader;
        externalClassLoader = true;
    }

    /** Ensure that the current thread has a classloader associated with it */
    private static void checkClassloader() {
        if (externalClassLoader) {
            Thread.currentThread().setContextClassLoader(externalLoader);
        } else {
            if (Thread.currentThread().getContextClassLoader() == null) {
                Thread.currentThread().setContextClassLoader(INSTANCE.getClass().getClassLoader());
            }
        }
    }

    /** Serialize an object to an XML String */
    public static String toXml(Object object) {
        checkClassloader();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(stream);
        encoder.writeObject(object);
        encoder.close();
        return stream.toString();
    }

    /** Recreate an object from an XML String */
    public static Object fromXml(String xmlData) {
        checkClassloader();
        ByteArrayInputStream stream = new ByteArrayInputStream(xmlData.getBytes());
        XMLDecoder decoder = new XMLDecoder(stream);
        Object object = decoder.readObject();
        return object;
    }

    /** Save a set of objects to an array of strings */
    public static String[] toXml(List objects) {
        checkClassloader();
        int count = objects.size();
        String[] data = new String[count];
        for (int i = 0; i < count; i++) {
            data[i] = toXml(objects.get(i));
        }
        return data;
    }

    /** Get a list of objects from a set of XML strings */
    public static List listFromXml(String[] xmlData) {
        checkClassloader();
        ArrayList list = new ArrayList();
        for (int i = 0; i < xmlData.length; i++) {
            list.add(fromXml(xmlData[i]));
        }
        return list;
    }

    /** Write a DOM document to an OutputStream */
    public static void writeXmlDocumentToStream(Document document, OutputStream stream) throws Exception {
        Source source = new DOMSource(document);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        StreamResult result = new StreamResult(stream);
        transformer.transform(source, result);
        stream.flush();
    }
    
    /** Read an XML document from a file */
    public static Document readXmlDocumentFromFile(File file) throws Exception {
        if(file.exists()){
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(file);
                return readXmlDocumentFromStream(stream);
            } catch (Exception e){
                throw e;
            } finally {
                if(stream!=null){
                    try {stream.close();}catch(Exception e){}
                }
            }
        } else {
            throw new Exception("FileNotFound: " + file.getPath());
        }
    }

    /** Read an XML document from an InputStream */
    public static Document readXmlDocumentFromStream(InputStream stream) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setCoalescing(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(stream);
        return doc;
    }

    /** Read an XML document from a String */
    public static Document readXmlDocumentFromString(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setCoalescing(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(xml.getBytes()));
        return doc;
    }
    
    /** Write a DOM document to a string */
    public static String xmlDocumentToString(Document document) {
        try {
            Source source = new DOMSource(document);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Extract all of the child nodes as a Properties object from a node in an XML document */
    public static Properties extractChildNodes(Document document) {
        try {
            Properties props = new Properties();
            Element top = document.getDocumentElement();
            NodeList children = top.getChildNodes();
            Node child;
            String name;
            String value;
            for(int i=0;i<children.getLength();i++){
                child = children.item(i);
                name = child.getNodeName();
                value = child.getTextContent().trim();
                props.setProperty(name, value);
            }
            return props;
        } catch (Exception e){
            return new Properties();
        }
    }
}
