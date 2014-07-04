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

import com.connexience.server.model.document.*;
import com.connexience.server.workflow.util.*;
import com.connexience.server.workflow.service.*;
import org.pipeline.core.xmlstorage.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * This class defines a single dependency that a data processor executor depends
 * upon. To run a data processor, all of the external dependencies that it declares
 * must be met.
 * @author hugo
 */
public class CloudWorkflowServiceLibraryItem implements Serializable, XmlStorable
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

    /** This item contains a service object */
    public static final int SERVICE_ITEM = 0;

    /** This item contains a set of dependency files */
    public static final int LIBRARY_ITEM = 1;

    /** Unknown / invalid library item */
    public static final int UNKNOWN_ITEM = 2;

    /** Top level of the unpacked directory */
    protected File unpackedDir;

    /** Zip file that was downloaded from the server */
    protected File zipFile;
    
    /** DocumentRecord that contains the zipped dependency data */
    private DocumentRecord zipDocumentRecord;

    /** DocumentVersion that describes this processor version */
    private DocumentVersion zipDocumentVersion;

    /** Parent cloud library */
    private transient ServiceLibrary parent;

    /** Dependencies for this item */
    private CopyOnWriteArrayList<CloudWorkflowItemDependency> dependencyList = new CopyOnWriteArrayList<>();

    /** Library name taken from libray.xml file */
    protected String libraryName = "";

    /** Version number */
    protected int versionNumber = 0;

    /** Type of data contained in this library item */
    protected int itemType = SERVICE_ITEM;

    /** Type of library that this item represents. This is JavaLibrary / RLibrary etc etc */
    protected String libraryType;

    /** List of resolved dependencies */
    private CopyOnWriteArrayList<CloudWorkflowServiceLibraryItem> resolvedDependencies = new CopyOnWriteArrayList<>();

    /** Library specific wrapper object */
    private LibraryWrapper wrapper = null;

    /** Empty constructor */
    public CloudWorkflowServiceLibraryItem(){

    }

    /** Construct with a parent engine */
    public CloudWorkflowServiceLibraryItem(ServiceLibrary parent, DocumentRecord zipDocumentRecord, DocumentVersion zipDocumentVersion){
        this.parent = parent;
        versionNumber = zipDocumentVersion.getVersionNumber();
        setZipDocumentDetails(zipDocumentRecord, zipDocumentVersion);
    }

    @Override
    public String toString() {
        return libraryName + ":" + versionNumber;
    }

    /** Does this item have any latest version dependencies */
    public boolean hasLatestVersionDependencies(){
        for(int i=0;i<dependencyList.size();i++){
            if(dependencyList.get(i).isLatestVersion()){
                return true;
            }
        }
        return false;
    }

    /** Add a resolved dependency */
    public void addResolvedDependency(CloudWorkflowServiceLibraryItem item){
        resolvedDependencies.add(item);
    }

    /** Set the resolved dependencies */
    public void setResolvedDependencies(CopyOnWriteArrayList<CloudWorkflowServiceLibraryItem> resolved){
        this.resolvedDependencies = resolved;
    }

    /** Have all of the dependencies been resolved */
    public boolean allDependenciesResolved(){
        if(dependencyList.size()==resolvedDependencies.size()){
            return true;
        } else {
            return false;
        }
    }
    
    /** Set the parent library */
    public void setParent(ServiceLibrary parent){
        this.parent = parent;
    }

    /** Get the parent library */
    public ServiceLibrary getParent(){
        return parent;
    }
    
    /** Set up this item from a previously unpacked directory. This is used for testing
     * and development of service items - one of these objects is created pointing
     * at the development dir. */
    public void setupFromUnpackedDir(File unpackedDir, LibraryPreparationReport report) throws Exception {
        this.unpackedDir = unpackedDir;
        parseLibraryType(report);
        parseLibraryXml(report);
        parseDependenciesXml();
        if(wrapper!=null){
            wrapper.setRelocated(false);
        }
    }

    /** Get the unpacked directory of this service item. This will change if the library
     *  has been relocated by the wrapper class. This can happed for binary libraries
     * that need a fixed installation location */
    public File getUnpackedDir(){
        if(wrapper!=null){
            if(wrapper.isRelocated()){
                return wrapper.getRelocatedDir();
            } else {
                return unpackedDir;
            }
            
        } else {
            return unpackedDir;
        }
    }

    /** Get the original unpacked directory. This will be masked if the library
     * has been relocated */
    public File getOriginalUnpackedDir(){
        return unpackedDir;
    }
    
    /** Add a dependency */
    public void addDependency(CloudWorkflowItemDependency dependency){
        dependencyList.add(dependency);
    }

    /** Get the zip file that was downloaded from the server */
    public File getZipFile(){
        return zipFile;
    }
    
    /** Get the service document object */
    public DocumentRecord getZipDocumentRecord(){
        return zipDocumentRecord;
    }

    /** Get the service version object */
    public DocumentVersion getZipDocumentVersion(){
        return zipDocumentVersion;
    }

    /** Check to see if this item matches an ID and version ID set */
    public boolean matches(String serviceId, String versionId){
        if(zipDocumentRecord.getId().equals(serviceId) && zipDocumentVersion.getId().equals(versionId)){
            return true;
        } else {
            return false;
        }
    }

    /** Does this item match a dependency library name and version number */
    public boolean matches(String libraryName, int versionNumber){
        if(this.libraryName.equals(libraryName) && this.versionNumber==versionNumber){
            return true;
        } else {
            return false;
        }
    }
    
    public boolean matchesWithVersionId(String versionId){
        if(this.zipDocumentVersion.getId().equals(versionId)){
            return true;
        } else {
            return false;
        }
    }

    /** Get an iterator of all of the resolved dependency library items */
    public Iterator<CloudWorkflowServiceLibraryItem> resolvedDependencies(){
        return resolvedDependencies.iterator();
    }

    /** Get an iterator of dependencies */
    public Iterator<CloudWorkflowItemDependency> dependencies(){
        return dependencyList.iterator();
    }

    /** Set the document record and version details */
    public void setZipDocumentDetails(DocumentRecord zipDocumentRecord, DocumentVersion zipDocumentVersion) {
        this.zipDocumentRecord = zipDocumentRecord;
        this.zipDocumentVersion = zipDocumentVersion;
        zipFile = new File(parent.getLibraryDirectory(), zipDocumentRecord.getId() + "-" + zipDocumentVersion.getId() + ".zip");
    }

    /** Extract the files contained in the zip archive. Files are stored in a subdirectory
     * with the ID of the service. Each version is unzipped into its own directory
     * tree with the ID of the version. */
    public void extractFiles(LibraryPreparationReport report) throws DataProcessorException, IOException {
        if(zipFile.exists()){
            unpackedDir = null;
            File topDir = parent.getLibraryDirectory();
            
            // Check to see if a document version directory exists under
            // this top level document directory
            File versionDir = new File(topDir, zipDocumentVersion.getId());
            if(!versionDir.exists()){
                versionDir.mkdir();
            }

            // Extract the zip file to this directory
            try {
                ZipUtils.unzip(zipFile, versionDir);
            } catch (Exception e){
                throw new IOException("Error unzipping library data: " + e.getMessage());
            }
            unpackedDir = versionDir;
            
            // Remove the zip file
            if(!zipFile.delete()){
                zipFile.deleteOnExit();
            }

            // Work out what type of object this is
            parseLibraryType(report);

            // Create a list of the dependency libraries
            parseDependenciesXml();

            // Do setup on the wrapper if present
            if(wrapper!=null){
                try {
                    wrapper.performLibrarySpecificSetup();
                } catch (Exception e){
                    throw new DataProcessorException("Error performing library setup: " + e.getMessage(), e);
                }
            }
            
        } else {
            throw new DataProcessorException("Zip file does not exist");
        }
    }

    /** Recompress the files into an archive */
    public void compressFiles() throws DataProcessorException, IOException {

    }

    /** Get the number of dependencies in this library */
    public int getDependencyCount(){
        return dependencyList.size();
    }
    
    /** Compile this library if needed */
    public void compile() throws DataProcessorException {

    }

    /** Get the type of this library. This is either service or library */
    public int getItemType(){
        return itemType;
    }

    /** Get the library type. This is something like JavaService etc */
    public String getLibraryType(){
        return libraryType;
    }

    /** Get the library name */
    public String getLibraryName(){
        return libraryName;
    }

    /** Get a list of subfolders for a specified top level folder */
    public List<File> getSubfolders(String relativeTopFolder) throws IOException {
        File top = getFile(relativeTopFolder);
        ArrayList<File> results = new ArrayList<>();
        if(top.isDirectory()){
            populateSubfolderList(results, top);
        }
        return results;
    }

    /** Populate a list of subfolders and recurse into the directory */
    private void populateSubfolderList(List<File> subfolders, File directory){
        if(directory.isDirectory()){
            subfolders.add(directory);
            File[] children = directory.listFiles();
            for(int i=0;i<children.length;i++){
                if(children[i].isDirectory()){
                    populateSubfolderList(subfolders, children[i]);
                }
            }
        }

    }


    /** Add all the jar files in the /lib directory of this dependency to a classpath list */
    public synchronized void addLibraryJarsToClasspath(ArrayList<URL> classPath) throws DataProcessorException, MalformedURLException{
        // Get all of the files in the lib directory
        File[] contents;
        if(getUnpackedDir()!=null){
            File libDir = new File(getUnpackedDir(), "lib");
            URL url;
            if(libDir.exists()){
                contents = libDir.listFiles();
                for(int i=0;i<contents.length;i++){
                    if(contents[i].getName().toLowerCase().endsWith(".jar")){
                        url = contents[i].toURI().toURL();
                        if(!classPath.contains(url)){
                            classPath.add(url);
                        }
                    }
                }
            }
            
            // Add any other libraries that may be in the root directory
            contents = unpackedDir.listFiles();
            for(int i=0;i<contents.length;i++){
                if(contents[i].getName().endsWith(".jar")){
                    url = contents[i].toURI().toURL();
                    if(!classPath.contains(url)){
                        classPath.add(url);
                    }
                }
            }

            // If there is a classes directory, then add this to the classpath
            // as well.
            if(containsFile("/classes")){
                try {
                    File classesDir = getFile("/classes");
                    classPath.add(classesDir.toURI().toURL());
                } catch (Exception e){
                    throw new DataProcessorException("Cannot add /classes directory to classpath: " + e.getMessage());
                }
            }

            // Now add dependencies
            for(int i=0;i<resolvedDependencies.size();i++){
                if(resolvedDependencies.get(i)!=this){
                    resolvedDependencies.get(i).addLibraryJarsToClasspath(classPath);
                }
            }

        } else {
            throw new DataProcessorException("Data has not been unpacked sucessfully");
        }
    }

    /** Copy any files needed to the invocation directory */
    public void prepareInvocationDirectory(File invocationDir, DataProcessorCallMessage message) throws DataProcessorException {
        // Set up the directory using all the dependencies first
        Iterator<CloudWorkflowServiceLibraryItem> i = resolvedDependencies();
        while(i.hasNext()){
            i.next().prepareInvocationDirectory(invocationDir, message);
        }

        // Set up the directory
        if(wrapper!=null){
            try {
                wrapper.prepareInvocationDirectory(invocationDir, message);
            } catch (Exception e){
                throw new DataProcessorException("Error preparing invocation directory: " + e.getMessage());
            }
        }
    }

    /** Get a file from this library. The top level in the library is referred to
     * with a '/' and everything is relative from here */
    public File getFile(String relativePath) throws IOException {
        File file = new File(getUnpackedDir(), relativePath);
        if(file.exists()){
            return file;
        } else {
            throw new IOException("File: '" + relativePath +"' does not exist in the library");
        }
    }

    /** Does this library contain a specified file */
    public boolean containsFile(String relativePath) {
        File file = new File(getUnpackedDir(), relativePath);
        if(file.exists()){
            return true;
        } else {
            return false;
        }
    }

    /** Create a directory */
    public File createDirectory(String relativePath) throws IOException {
        File file = new File(getUnpackedDir(), relativePath);
        if(!file.exists()){
            file.mkdirs();
            return file;
        } else {
            if(file.isDirectory()){
                return file;
            } else {
                throw new IOException(relativePath + " already exists and is a file");
            }
        }
    }

    /** Try and find the library.xml file in the unpacked files to determine
     * what type of wrapper class to create for this raw data */
    private void parseLibraryType(LibraryPreparationReport report) throws IOException, DataProcessorException {
        if(containsFile("/library.xml")){
            // Found a library.xml file
            itemType = LIBRARY_ITEM;
            parseLibraryXml(report);

        } else {
            // Look for a service.xml file to see if this is a top
            // level service
            if(containsFile("/service.xml")){
                itemType = SERVICE_ITEM;
            } else {
                itemType = UNKNOWN_ITEM;
            }
        }
    }

    /** Parse the library.xml file */
    private void parseLibraryXml(LibraryPreparationReport report) throws DataProcessorException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            File libraryFile = getFile("/library.xml");
            FileInputStream stream = new FileInputStream(libraryFile);
            Document doc = builder.parse(stream);
            stream.close();
            
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

            // Create the wrapper object
            if(libraryType!=null){
                wrapper = LibraryWrapper.createWrapper(libraryType, this);
                if(wrapper!=null){
                    wrapper.parseLibraryXml(libraryFile, report);
                }
            } else {
                wrapper = null;
            }
        } catch (Exception e){
            // FIXME: This message is misleading in the case when an exec command 
            //        fails during library preparation.
            throw new DataProcessorException("Error parsing library.xml file: " + e.getMessage());
        }
    }

    /** Get the library wrapper object */
    public LibraryWrapper getWrapper(){
        return wrapper;
    }

    /** Parse the dependencies.xml file */
    private void parseDependenciesXml() throws DataProcessorException {
        try {
            dependencyList.clear();
            if(containsFile("/dependencies.xml")){
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                FileInputStream stream = new FileInputStream(getFile("/dependencies.xml"));
                Document doc = builder.parse(stream);
                stream.close();

                Element top = doc.getDocumentElement();
                NodeList children = top.getChildNodes();
                NodeList depChildren;
                Node depChild;
                Node child;
                CloudWorkflowItemDependency dependency;

                String name;
                int version;
                boolean latest;
                boolean runtimeOnly;

                for(int i=0;i<children.getLength();i++){
                    child = children.item(i);
                    if(child.getNodeName().equalsIgnoreCase("dependency")){
                        depChildren = child.getChildNodes();
                        dependency = new CloudWorkflowItemDependency();

                        name = null;
                        version = 0;
                        latest = true;
                        runtimeOnly = false;


                        for(int j=0;j<depChildren.getLength();j++){
                            depChild = depChildren.item(j);
                            if(depChild.getNodeName().equalsIgnoreCase("libraryname")){
                                name = depChild.getTextContent().trim();
                            } else if(depChild.getNodeName().equalsIgnoreCase("version")){
                                version = Integer.parseInt(depChild.getTextContent());
                            } else if(depChild.getNodeName().equalsIgnoreCase("uselatestversion")){
                                if(depChild.getTextContent().trim().equalsIgnoreCase("true")){
                                    latest = true;
                                } else {
                                    latest = false;
                                }
                            } else if(depChild.getNodeName().equalsIgnoreCase("runtimeonly")){
                                if(depChild.getTextContent().trim().equalsIgnoreCase("true")){
                                    runtimeOnly = true;
                                } else {
                                    runtimeOnly = false;
                                }
                            }
                        }

                        if(name!=null){
                            dependency.setLibraryName(name);
                            dependency.setLatestVersion(latest);
                            dependency.setVersionNumber(version);
                            dependency.setRuntimeOnlyDependency(runtimeOnly);
                            dependencyList.add(dependency);
                        }
                    }
                }

            }
        } catch (Exception e){
            throw new DataProcessorException("Error parsing depencendies.xml file: " + e.getMessage());
        }
    }

    /** Get the dependency chain as an XmlDataStore */
    public synchronized XmlDataStore getDependencyChainData(){
        XmlDataStore store = new XmlDataStore("DependencyChain");
        HashMap<String, CloudWorkflowServiceLibraryItem> list = getDependencyChain();
        Iterator<CloudWorkflowServiceLibraryItem> i = list.values().iterator();
        CloudWorkflowServiceLibraryItem dep;
        int count = 0;
        while(i.hasNext()){
            dep = i.next();
            store.add("Dependency" + count + "ID", dep.getZipDocumentRecord().getId());
            store.add("Dependency" + count + "VersionID", dep.getZipDocumentVersion().getId());
            store.add("Dependency" + count + "Name", dep.getZipDocumentRecord().getName());
            count++;
        }
        return store;
    }

    /** Get an XmlDataStore containing the list of dependencies */
    public synchronized HashMap<String, CloudWorkflowServiceLibraryItem> getDependencyChain(){
        HashMap<String, CloudWorkflowServiceLibraryItem> list = new HashMap<>();
        addDependenciesToChain(list);
        return list;
    }

    /** Add the dependencies to an XML data store */
    protected synchronized void addDependenciesToChain(HashMap<String, CloudWorkflowServiceLibraryItem> chain){
        Iterator<CloudWorkflowServiceLibraryItem> i = resolvedDependencies();
        CloudWorkflowServiceLibraryItem dep;
        while(i.hasNext()){
            dep = i.next();
            if(!chain.containsKey(dep.getLibraryName())){
                chain.put(dep.getLibraryName(), dep);
                dep.addDependenciesToChain(chain);
            }
        }
    }

    /** Print out the classpath */
    public void debugPrint(){
        try {
            ArrayList<URL>cpArray = new ArrayList<>();
            addLibraryJarsToClasspath(cpArray);
            for(int i=0;i<cpArray.size();i++){
                System.out.println(cpArray.get(i));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /** Get an unpopulated copy of this library suitable for dependency resolution */
    public CloudWorkflowServiceLibraryItem getUnpopulatedCopy() throws Exception {
        CloudWorkflowServiceLibraryItem copy = new CloudWorkflowServiceLibraryItem();
        copy.setParent(this.parent);
        copy.setZipDocumentDetails(zipDocumentRecord, zipDocumentVersion);
        copy.itemType = this.itemType;
        copy.libraryName = libraryName;
        copy.libraryType = libraryType;
        copy.unpackedDir = unpackedDir;
        copy.versionNumber = versionNumber;
        copy.zipFile = zipFile;
        copy.setupFromUnpackedDir(unpackedDir, new LibraryPreparationReport());
        return copy;
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("CloudWorkflowServiceLibraryItem");
        store.add("ItemType", itemType);
        store.add("LibraryName", libraryName);
        store.add("LibraryType", libraryType);
        store.add("UnpackedDir", unpackedDir);
        store.add("VersionNumber", versionNumber);
        store.add("ZipDocumentRecord", zipDocumentRecord);
        store.add("ZipDocumentVersion", zipDocumentVersion);
        store.add("ZipFile", zipFile);

        // Dependency list
        Iterator<CloudWorkflowItemDependency> depIterator = dependencyList.iterator();
        int count = 0;
        while(depIterator.hasNext()){
            store.add("Dependency" + count, depIterator.next());
            count++;
        }
        store.add("DependencyCount", count);

        // Store the resolved dependencies
        Iterator<CloudWorkflowServiceLibraryItem> resolvedDepIterator = resolvedDependencies.iterator();
        count = 0;
        while(resolvedDepIterator.hasNext()){
            store.add("ResolvedDependency" + count, resolvedDepIterator.next());
            count++;
        }
        store.add("ResolvedDependencyCount", count);

        // Save the wrapper
        if(wrapper!=null){
            store.add("Wrapper", wrapper);
        }
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        itemType = store.intValue("ItemType", SERVICE_ITEM);
        libraryName = store.stringValue("LibraryName", "");
        libraryType = store.stringValue("LibraryType", null);

        if(store.containsName("UnpackedDir")){
            unpackedDir = store.fileValue("UnpackedDir", "");
        } else {
            unpackedDir = null;
        }

        versionNumber = store.intValue("VersionNumber", 0);
        if(store.containsName("ZipDocumentRecord")){
            zipDocumentRecord = (DocumentRecord)store.xmlStorableValue("ZipDocumentRecord");
        } else {
            zipDocumentRecord = null;
        }

        if(store.containsName("ZipDocumentVersion")){
            zipDocumentVersion = (DocumentVersion)store.xmlStorableValue("ZipDocumentVersion");
        } else {
            zipDocumentVersion = null;
        }

        if(store.containsName("ZipFile")){
            zipFile = store.fileValue("ZipFile");
        } else {
            zipFile = null;
        }


        // Load dependencies
        int dependencyCount = store.intValue("DependencyCount", 0);
        dependencyList.clear();
        for(int i=0;i<dependencyCount;i++){
            dependencyList.add((CloudWorkflowItemDependency)store.xmlStorableValue("Dependency" + i));
        }

        // Load resolved dependencies
        int resolvedDependencyCount = store.intValue("ResolvedDependencyCount", 0);
        resolvedDependencies.clear();
        for(int i=0;i<resolvedDependencyCount;i++){
            resolvedDependencies.add((CloudWorkflowServiceLibraryItem)store.xmlStorableValue("ResolvedDependency" + i));
        }

        // Load the wrapper
        if(store.containsName("Wrapper")){
            wrapper = (LibraryWrapper)store.xmlStorableValue("Wrapper");
            wrapper.setLibraryItem(this);
        }
    }
}