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
package com.connexience.server.workflow.cloud.library;

import com.connexience.server.workflow.cloud.library.types.*;
import com.connexience.server.model.document.*;
import com.connexience.server.workflow.util.*;
import org.pipeline.core.xmlstorage.*;

import com.connexience.server.workflow.service.DataProcessorCallMessage;
import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

/**
 * This file contains a reference to a raw CloudWorkflowServiceLibraryItem
 * and knows how to deal with the data contained in it depending on the library
 * type defined in the library.xml file in the raw data.
 * @author nhgh
 */
public abstract class LibraryWrapper implements Serializable, XmlStorable 
{
    /**
     * Class version UID.
     * 
     * Please increment this value whenever your changes may cause 
     * incompatibility with the previous version of this class. If unsure, ask 
     * one of the core development team or read:
     *   http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     *   http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 1L;

    /** Raw library item */
    private CloudWorkflowServiceLibraryItem libraryItem;

    /** Library properties read from the library xml file */
    private Properties props = new Properties();

    /** Has the library been relocated to a different location */
    private boolean relocated = false;

    /** Is this library due for relocation */
    private boolean dueToRelocate = false;

    /** Relocated library top level */
    private File relocatedDir = null;

    public LibraryWrapper() {
    }
    
    public LibraryWrapper(CloudWorkflowServiceLibraryItem libraryItem) {
        this.libraryItem = libraryItem;
    }

    public CloudWorkflowServiceLibraryItem getLibraryItem(){
        return libraryItem;
    }

    public void setLibraryItem(CloudWorkflowServiceLibraryItem libraryItem){
        this.libraryItem = libraryItem;
    }

    /** Find a dependency for this service. This only searches by name in the directly
     * declared list of dependencies. */
    public CloudWorkflowServiceLibraryItem getDependencyItem(String name) {
        if(libraryItem!=null){
            Iterator<CloudWorkflowServiceLibraryItem> i = libraryItem.resolvedDependencies();
            CloudWorkflowServiceLibraryItem item;
            while(i.hasNext()){
                item = i.next();
                if(item.getLibraryName().equals(name)){
                    return item;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    /** Create the correct type of wrapper to support a library.xml file */
    public static LibraryWrapper createWrapper(String libraryType, CloudWorkflowServiceLibraryItem item){
        if(libraryType.equals("JavaService")){
            return new JavaServiceLibrary(item);

        } else if(libraryType.equals("JavaLibrary")){
            return new JavaJarLibrary(item);

        } else if(libraryType.equals("BinaryLibrary")){
            return new BinaryLibrary(item);

        } else if(libraryType.equals("OctaveService")){
            return new OctaveServiceLibrary(item);

        } else if(libraryType.equals("GnuplotService")){
            return new GnuplotServiceLibrary(item);

        } else if(libraryType.equals("RService")){
            return new RServiceLibrary(item);
            
        } else if(libraryType.equals("JavaScriptService")){
            return new JavaScriptServiceLibrary(item);
            
        }
        
        return null;
    }

    /** Has this library been relocated */
    public boolean isRelocated(){
        return relocated;
    }

    /** Set whether this library is due to relocate */
    public void setDueToRelocate(boolean dueToRelocate){
        this.dueToRelocate = dueToRelocate;
    }

    /** Get whether this library is due to relocate */
    public boolean isDueToRelocate(){
        return dueToRelocate;
    }
    
    /** Set this library to be relocated */
    public void setRelocated(boolean relocated){
        this.relocated = relocated;
    }

    /** Get the relocated directory */
    public File getRelocatedDir(){
        return relocatedDir;
    }

    /** Set the relocated directory */
    public void setRelocatedDir(File relocatedDir){
        this.relocatedDir = relocatedDir;
    }
    
    /** Get a property */
    public String getProperty(String name){
        return props.getProperty(name);
    }
    
    /** Get a property. This method returns a default value if the property does not exist */
    public String getProperty(String name, String defaultValue){
        if(props.containsKey(name)){
            return props.getProperty(name);
        } else {
            return defaultValue;
        }
    }

    /** Parse a properties element */
    private void parseProperties(Node propertiesNode){
        props.clear();

        NodeList children = propertiesNode.getChildNodes();
        Node child;
        NamedNodeMap attributes;
        String name;
        String value;

        for(int i=0;i<children.getLength();i++){
            child = children.item(i);
            if(child.getNodeName().equalsIgnoreCase("property")){
                attributes = child.getAttributes();
                name = attributes.getNamedItem("name").getTextContent().trim();
                value = attributes.getNamedItem("value").getTextContent().trim();
                props.setProperty(name, value);
            }
        }
    }

    /** Parse a library xml file */
    public void parseLibraryXml(File xmlFile, LibraryPreparationReport report) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        FileInputStream stream = new FileInputStream(xmlFile);
        Document doc = builder.parse(stream);

        // Parse the properties
        Element topElement = doc.getDocumentElement();
        NodeList children = topElement.getChildNodes();
        Node child;

        for(int i=0;i<children.getLength();i++){
            child = children.item(i);
            if(child.getNodeName().equalsIgnoreCase("properties")){
                // Parse the properties
                parseProperties(child);
                
            } else if(child.getNodeName().equalsIgnoreCase("relocate")){
                // Relocate this library to another directory
                String locateDir = child.getTextContent().trim();
                setDueToRelocate(true);
                setRelocatedDir(new File(locateDir));
            }

        }

        // Relocate if needed. Only relocates if this is a runtime engine
        if(isDueToRelocate() && libraryItem.getParent().isDevelopmentEngine()==false){
            relocateTo(getRelocatedDir(), true);
        }

        // Set up the actual wrapper using the XML file
        setupWrapper(doc, report);
    }

    /** Relocate this library to a new location */
    protected void relocateTo(File targetDir, boolean removeOriginal) throws IOException {
        if(!targetDir.exists()){
            targetDir.mkdirs();
        }
        
        ZipUtils.copyDirTree(getLibraryItem().getOriginalUnpackedDir(), targetDir);
        relocatedDir = targetDir;
        relocated = true;
        dueToRelocate = false;
        if(removeOriginal){
            ZipUtils.removeDirectory(getLibraryItem().getOriginalUnpackedDir());
        }

        // Wait for a while
        // TODO: Why??
        try {
            Thread.sleep(5000);
        } catch (Exception e){}
    }

    /** Prepare the invocation directory if needed */
    public void prepareInvocationDirectory(File invocationDir, DataProcessorCallMessage message) throws Exception {

    }

    /** Do any additional setup prior to use */
    public void performLibrarySpecificSetup() throws Exception {
        
    }

    /** Set up specific properties from an XML document */
    public abstract void setupWrapper(Document doc, LibraryPreparationReport report) throws Exception;

    public boolean fileExists(String name) {
        if(libraryItem!=null){
            File f = new File(libraryItem.getUnpackedDir(), name);
            return f.exists();
        } else {
            return false;
        }
    }

    public File getContentsDir() {
        if(libraryItem!=null){
            return libraryItem.getUnpackedDir();
        } else {
            return null;
        }
    }

    public DocumentRecord getDocument() {
        if(libraryItem!=null){
            return libraryItem.getZipDocumentRecord();
        } else {
            return null;
        }
    }

    public File getFile(String fileName) {
        if(libraryItem!=null){
            return new File(libraryItem.getUnpackedDir(), fileName);
        } else {
            return null;
        }
    }

    public ServiceLibrary getServiceLibrary() {
        if(libraryItem!=null){
            return libraryItem.getParent();
        } else {
            return null;
        }
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("LibraryWrapper");
        store.add("DueToRelocate", dueToRelocate);
        store.add("Relocated", relocated);

        if(relocatedDir!=null){
            store.add("RelocatedDir", relocatedDir);
        }

        store.add("PropertyCount", props.size());
        Enumeration<?> keys = props.keys();
        String name;
        String value;
        int count = 0;

        while(keys.hasMoreElements()){
            name = keys.nextElement().toString();
            value = props.getProperty(name);
            store.add("Property" + count + "Name", name);
            store.add("Property" + count + "Value", value);
            count++;
        }

        
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        dueToRelocate = store.booleanValue("DueToRelocate", false);
        relocated = store.booleanValue("Relocated", false);
        if(store.containsName("RelocatedDir")){
            relocatedDir = store.fileValue("RelocatedDir");
        } else {
            relocatedDir = null;
        }

        props.clear();
        int propertyCount = store.intValue("PropertyCount", 0);
        String name;
        String value;

        for(int i=0;i<propertyCount;i++){
            name = store.stringValue("Property" + i + "Name", null);
            value = store.stringValue("Property" + i + "Value", null);
            if(name!=null && value!=null){
                props.setProperty(name, value);
            }
        }
    }
}