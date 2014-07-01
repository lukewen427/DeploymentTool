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

import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.folder.Folder;

import java.util.Hashtable;

/**
 * This is a utility class for the XmlStorage package. It allows classes to register
 * themselves as storable. This allows them to be re-created without having to have
 * a dependency to their package.
 * @author hugo
 */
public abstract class XmlStorage {
    /** List of classes */
    private static Hashtable objectTable;
    
    static {
        XmlStorage.objectTable = new Hashtable();
        XmlStorage.overrideClass("com.connexience.server.workflow.xmlstorage.FolderWrapper", Folder.class);
        XmlStorage.overrideClass("com.connexience.server.workflow.xmlstorage.DocumentRecordWrapper", DocumentRecord.class);
        XmlStorage.registerClass(com.connexience.server.model.document.DocumentVersion.class);
        XmlStorage.registerClass(com.connexience.server.model.workflow.WorkflowDocument.class);
        XmlStorage.registerClass(com.connexience.server.model.workflow.DynamicWorkflowLibrary.class);
        XmlStorage.registerClass(com.connexience.server.model.workflow.DynamicWorkflowService.class);
        XmlStorage.registerClass(com.connexience.server.model.workflow.WorkflowInvocationFolder.class);
    }
    
    /** Override a class */
    public static void overrideClass(String name, Class c){
        if(!objectTable.containsKey(name)){
            objectTable.put(name, c);
        }
    }
    
    /** Register a class */
    public static boolean registerClass(Class c){
        if(!objectTable.containsKey(c.getName())){
            objectTable.put(c.getName(), c);
        }
        return true;
    }
        
    
    /** Unregsister a class */
    public static boolean unregisterClass(Class c){
        objectTable.remove(c.getName());
        return true;
    }
    
    /** Unregister a class */
    public static boolean unregisterClass(String name){
        objectTable.remove(name);
        return true;
    }
    
    /** Does this store contain a class */
    public static boolean containsClass(String name){
        return objectTable.containsKey(name);
    }
    
    /** Instantiate a class */
    public static Object instantiate(String name) throws XmlStorageException {
        if(objectTable.containsKey(name)){
            try {
                Class c = (Class)objectTable.get(name);
                return c.newInstance();
                
            } catch (Exception e){
                throw new XmlStorageException("Error creating class: " + e.getMessage());
            }
        } else {
            throw new XmlStorageException("Cannot find class: " + name);
        }
        
    }
}
