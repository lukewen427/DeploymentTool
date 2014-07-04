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
import com.connexience.server.workflow.service.DataProcessorCallMessage;
import com.connexience.server.workflow.util.*;
import java.io.File;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.w3c.dom.*;
import java.util.*;


/**
 * This class represents a workflow service written in R
 * @author hugo
 */
public class RServiceLibrary extends LibraryWrapper {
    /** List of additional CRAN packages to install */
    private ArrayList<String> cranPackageList = new ArrayList<>();

    public RServiceLibrary() {
    }

    public RServiceLibrary(CloudWorkflowServiceLibraryItem libraryItem) {
        super(libraryItem);
    }

    @Override
    public void setupWrapper(Document doc, LibraryPreparationReport report) throws Exception {
        // Parse the library.xml file to build up a list of executables that this
        // wrapper contains
        Element top = doc.getDocumentElement();
        NodeList children = top.getChildNodes();
        NodeList cranPackages;
        Node child;
        cranPackageList.clear();

        for(int i=0;i<children.getLength();i++){
            child = children.item(i);
            if(child.getNodeName().equalsIgnoreCase("cran-install")){
                cranPackages = child.getChildNodes();
                for(int j=0;j<cranPackages.getLength();j++){

                    if(cranPackages.item(j).getNodeName().equalsIgnoreCase("package")){
                        cranPackageList.add(cranPackages.item(j).getTextContent());
                    }
                }
            }
        }
    }

    /** Get the number of CRAN packages */
    public int getCranPackageCount(){
        return cranPackageList.size();
    }

    /** Get a CRAN package by index */
    public String getCranPackage(int index){
        return cranPackageList.get(index);
    }
    
    @Override
    public void performLibrarySpecificSetup() throws Exception {
        super.performLibrarySpecificSetup();

    }

    @Override
    public void prepareInvocationDirectory(File invocationDir, DataProcessorCallMessage message) throws Exception {
        super.prepareInvocationDirectory(invocationDir, message);

        File invocationContextDir = new File(invocationDir, message.getContextId());
        if(!invocationContextDir.exists()){
            invocationContextDir.mkdir();
        }

        File rFilesDir = new File(invocationContextDir, "rfiles");
        if(!rFilesDir.exists()){
            rFilesDir.mkdir();
        }

        // Copy scripts
        if(getLibraryItem().containsFile("/scripts")){
            File scriptsDir = getLibraryItem().getFile("/scripts");
            if(scriptsDir.isDirectory()){
                ZipUtils.copyDirTree(scriptsDir, rFilesDir);
            }
        }

        // Copy the init.m file
        File initR = getLibraryItem().getFile("/init.r");
        if(initR.exists()){
            ZipUtils.copyFileToDirectory(initR, invocationDir);
        }

        // Copy the main.m file
        File mainR = getLibraryItem().getFile("/main.r");
        if(mainR.exists()){
            ZipUtils.copyFileToDirectory(mainR, invocationDir);
        }
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        for(int i=0;i<cranPackageList.size();i++){
            store.add("CRANPackage" + i, cranPackageList.get(i));
        }
        store.add("CRANPackageCount", cranPackageList.size());
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        int count = store.intValue("CRANPackageCount", 0);
        cranPackageList.clear();
        for(int i=0;i<count;i++){
            cranPackageList.add(store.stringValue("CRANPackage" + i, ""));
        }
    }

}