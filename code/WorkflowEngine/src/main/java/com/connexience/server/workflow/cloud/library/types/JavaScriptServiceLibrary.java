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

import com.connexience.server.workflow.cloud.library.*;
import com.connexience.server.workflow.cloud.execution.*;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import org.w3c.dom.*;
import java.io.*;
import java.util.*;
import org.pipeline.core.xmlstorage.XmlStorable;

/**
 * This class provides a library for a javascript service
 * @author hugo
 */
public class JavaScriptServiceLibrary extends LibraryWrapper implements XmlStorable {
    /** List of the core scripts to eval */
    private ArrayList<String> coreScripts = new ArrayList<>();
    
    /** List of the user scripts to eval */
    private ArrayList<String> userScripts = new ArrayList<>();
    
    /** Base URL of the scripts source */
    private String baseUrl = "";
    
    public JavaScriptServiceLibrary() {
    }
    
    public JavaScriptServiceLibrary(CloudWorkflowServiceLibraryItem libraryItem){
        super(libraryItem);
    }

    @Override
    public void setupWrapper(Document doc, LibraryPreparationReport report) throws Exception {
        JavaScriptServiceLibraryParser parser = new JavaScriptServiceLibraryParser(doc);
        parser.parse();
        coreScripts = parser.getCoreScripts();
        userScripts = parser.getUserScripts();
        baseUrl = parser.getBaseUrl();
    }
    
    public ArrayList<String> getCoreScripts(){
        return coreScripts;
    }

    public ArrayList<String> getUserScripts(){
        return userScripts;
    }
    
    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("CoreScriptCount", coreScripts.size());
        for(int i=0;i<coreScripts.size();i++){
            store.add("CoreScript" + i, coreScripts.get(i));
        }
        
        store.add("UserScriptCount", userScripts.size());
        for(int i=0;i<userScripts.size();i++){
            store.add("UserScript" + i, userScripts.get(i));
        }
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        coreScripts = new ArrayList<>();
        userScripts = new ArrayList<>();
        
        int size = store.intValue("CoreScriptCount", 0);
        for(int i=0;i<size;i++){
            coreScripts.add(store.stringValue("CoreScript" + i, null));
        }
        
        size = store.intValue("UserScriptCount", 0);
        for(int i=0;i<size;i++){
            userScripts.add(store.stringValue("UserScript" + i, null));
        }
    }
}