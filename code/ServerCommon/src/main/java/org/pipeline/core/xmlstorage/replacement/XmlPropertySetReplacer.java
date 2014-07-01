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
package org.pipeline.core.xmlstorage.replacement;

import org.pipeline.core.xmlstorage.XmlDataObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * This file loads an Xml document containing a list of properties to set. This
 * can then be applied to multiple XmlDataStore objects to modify their contents.
 * The format of the file is:
 *
 * <properties>
 *  <set name="storename">
 *      <propertyname>NewValue</propertyname>
 *      <propertyname>NewValue</propertyname>
 *      ...
 *  </set>
 * 
 *
 * </properties>
 *
 * @author nhgh
 */
public class XmlPropertySetReplacer {
    /** List of parsed properties. These will be set as string values in the
     * XmlDataStore objects that are processed */
    private Hashtable<String,Properties> parsedProperties = new Hashtable<>();

    /** Parse an XmlInput stream */
    public void parseXmlString(InputStream stream) throws XmlStorageException {
        try {
            parsedProperties.clear();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setCoalescing(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(stream);

            Element root = doc.getDocumentElement();
            NodeList children = root.getChildNodes();
            Element setElement;
            for(int i=0;i<children.getLength();i++){
                setElement = (Element)children.item(i);
                if(setElement.getNodeName().equalsIgnoreCase("set")){
                    parseSet(setElement);
                }
            }
            
        } catch (Exception e){
            throw new XmlStorageException("Error parsing XML stream: " + e.getMessage());
        }
    }

    /** Parse a set element */
    private void parseSet(Element setElement) throws Exception {
        String setName = setElement.getAttribute("name");
        Properties properties;
        if(parsedProperties.containsKey(setName)){
            properties = parsedProperties.get(setName);
        } else {
            properties = new Properties();
            parsedProperties.put(setName, properties);
        }

        NodeList children = setElement.getChildNodes();
        Element element;
        String name;
        String value;

        for(int i=0;i<children.getLength();i++){
            element = (Element)children.item(i);
            name = element.getNodeName().trim();
            value = element.getTextContent();
            properties.put(name, value);
        }
    }

    /** Replace the properties in a property store with a named set */
    public void replace(XmlDataStore store, String setName) throws XmlStorageException {
        if(parsedProperties.containsKey(setName)){
            Properties props = parsedProperties.get(setName);
            Enumeration names = props.propertyNames();
            String name;
            String value;
            String oldValue;

            XmlDataObject dataObject;

            while(names.hasMoreElements()){
                name = names.nextElement().toString();
                if(store.containsName(name)){
                    dataObject = store.get(name);
                    if(dataObject.canParseString()){
                        oldValue = store.getPropertyString(name);
                        value = props.getProperty(name, oldValue);
                        dataObject.parseString(value);
                    }
                }
            }
        }
    }
}
