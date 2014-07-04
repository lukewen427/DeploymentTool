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
package com.connexience.server.workflow.cloud.library.types;

import org.w3c.dom.*;
import java.util.*;
/**
 * This class parses a library.xml file for a javascript service
 * @author hugo
 */
public class JavaScriptServiceLibraryParser {
    private String baseUrl = "";
    private Document doc;
    private ArrayList<String> coreScriptList = new ArrayList<>();
    private ArrayList<String> userScriptList = new ArrayList<>();

    public JavaScriptServiceLibraryParser(Document doc) {
        this.doc = doc;
    }

    public void parse() throws Exception {
        // Parse the library.xml file to build up a list of executables that this
        // wrapper contains
        Element top = doc.getDocumentElement();
        NodeList children = top.getChildNodes();
        NodeList userScripts;
        NodeList coreScripts;
        Node child;
        Node execNode;
        String name;


        for (int i = 0; i < children.getLength(); i++) {
            child = children.item(i);
            if (child.getNodeName().equalsIgnoreCase("corescripts")) {
                // Get the base url
                baseUrl = child.getAttributes().getNamedItem("baseurl").getTextContent();
                coreScripts = child.getChildNodes();
                for (int j = 0; j < coreScripts.getLength(); j++) {

                    if (coreScripts.item(j).getNodeName().equalsIgnoreCase("script")) {
                        name = coreScripts.item(j).getTextContent();
                        coreScriptList.add(name);
                    }
                }

            } else if (child.getNodeName().equalsIgnoreCase("scripts")) {
                userScripts = child.getChildNodes();
                for (int j = 0; j < userScripts.getLength(); j++) {
                    if (userScripts.item(j).getNodeName().equalsIgnoreCase("script")) {
                        name = userScripts.item(j).getTextContent();
                        userScriptList.add(name);
                    }

                }
            }
        }
    } 
    
    public ArrayList<String> getCoreScripts(){
        return coreScriptList;
    }
    
    public ArrayList<String> getUserScripts(){
        return userScriptList;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
