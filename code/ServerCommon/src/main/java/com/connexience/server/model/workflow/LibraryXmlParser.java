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
package com.connexience.server.model.workflow;

import com.connexience.server.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * This class parses the library.xml data in an uploaded service to identify
 * library / service type
 * @author nhgh
 */
public class LibraryXmlParser {
    /** Xml data to parse */
    private String xmlData;

    /** Name of the library */
    private String libraryName;

    /** Type of library / service */
    private String libraryType;

    public LibraryXmlParser(String xmlData) {
        this.xmlData = xmlData;
    }
    
    public void parse() throws Exception {
        Document doc = XmlUtils.readXmlDocumentFromString(xmlData);

        Element top = doc.getDocumentElement();
        NodeList children = top.getChildNodes();
        Node child;

        for(int i=0;i<children.getLength();i++){
            child = children.item(i);
            if(child.getNodeName().equalsIgnoreCase("name")){
                libraryName = child.getTextContent().trim();
            } else if(child.getNodeName().equalsIgnoreCase("type")){
                libraryType = child.getTextContent().trim();
            }
        }
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryType(String libraryType) {
        this.libraryType = libraryType;
    }
   
}
