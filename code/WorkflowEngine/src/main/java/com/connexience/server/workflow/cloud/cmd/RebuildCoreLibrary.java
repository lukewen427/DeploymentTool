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
package com.connexience.server.workflow.cloud.cmd;


import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.security.Permission;
import com.connexience.server.model.security.User;
import com.connexience.server.model.document.*;
import com.connexience.server.model.workflow.DynamicWorkflowLibrary;
import com.connexience.server.workflow.api.*;
import com.connexience.server.workflow.cloud.library.*;
import com.connexience.server.workflow.util.*;
import com.connexience.server.workflow.api.ApiProvider;
import com.connexience.server.workflow.rpc.*;

import java.io.*;
import java.util.*;


/**
 * This class uses the workflow API and standard API to rebuild the core
 * workflow library using the .jar files in the lib directory of the 
 * workflow server.
 * @author hugo
 */
public class RebuildCoreLibrary implements ServiceLibraryContainer {
    Properties buildProperties;
    String url = "http://localhost:8080/WorkflowServer";
    String username = "";
    String password = "";
    String newFilename = "corelibrary";
    ApiProvider apiProvider;
    API api = null;
    DynamicWorkflowLibrary coreLibrary;
    ServiceLibrary library;
    private File librarySourceDir;
    ArrayList<String> fileNames = new ArrayList<>();

    public RebuildCoreLibrary(String sourceDirName) {
        librarySourceDir = new File(sourceDirName);
    }
    
    public static void main(String[] args){
        String libSourceDir; // = System.getProperty("user.home") + File.separator + "inkspot/code/webflow/WorkflowCloud/lib";
        if(args.length==1){
            System.out.println("Library source directory specified: " + args[0]);
            libSourceDir = args[0];
        } else {
            System.out.println("Working in: " + System.getProperty("user.dir"));
            libSourceDir = System.getProperty("user.dir") + File.separator + "lib";
        }
        System.out.println("Using source library directory of: " + libSourceDir);
        
        RebuildCoreLibrary builder = new RebuildCoreLibrary(libSourceDir);
        try {
            System.out.println("Preparing local service library");
            builder.createServiceLibrary();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        try {
            System.out.println("Loading corelibrary properties file");
            builder.readPropertiesFile();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        
        try {
            System.out.println("Authenticating");
            builder.authenticate();
            System.out.println("Signed in OK");
        } catch (Exception e){
            e.printStackTrace();
        }
        
        try {
            System.out.println("Fetching core library");
            builder.fetchCoreLibrary();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }


    public void readPropertiesFile() throws IOException {
         File propertiesDir = new File(System.getProperty("user.home") + File.separator + ".inkspot");
         if(!propertiesDir.isDirectory()){
             propertiesDir.mkdirs();
         }
         
         File propertiesFile = new File(propertiesDir, "corelibrary.properties");
         if(!propertiesFile.exists()){
             ZipUtils.copyStreamToFile(getClass().getResourceAsStream("/corelibrary/corelibrary.properties"), propertiesFile);
             System.out.println("Created a new default properties file: " + propertiesFile.getPath());
             System.exit(1);
         }
         
         buildProperties = new Properties();
         FileInputStream inStream = new FileInputStream(propertiesFile);
         buildProperties.load(inStream);
         inStream.close();
         

         url = buildProperties.getProperty("url", "http://localhost:8080");
         username = buildProperties.getProperty("username", "");
         password = buildProperties.getProperty("password", "");
         newFilename = buildProperties.getProperty("uploadfilename", "corelibrary");
         // Get the file names
         Enumeration<?> names = buildProperties.propertyNames();
         String name;
         while(names.hasMoreElements()){
             name = (String)names.nextElement();
             if(name.startsWith("file")){
                 fileNames.add(buildProperties.getProperty(name));
             }
         }

         System.out.println("Server URI: " + url);
    }
    
    public void createServiceLibrary() throws Exception {
        File libraryDir = getLibraryDirectory();
        if(!libraryDir.exists()){
            libraryDir.mkdirs();
        }
        library = new ServiceLibrary(this);
        library.flushLibrary();
    }
    
    public void authenticate() throws Exception {
        try {
            apiProvider = new ApiProvider();
            apiProvider.setUseRmi(false);
            apiProvider.setURL(url);
            api = apiProvider.createApi();
            if(!api.authenticate(username, password)){
                throw new Exception("Error authenticating");
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void fetchCoreLibrary() throws Exception {
        try {
            coreLibrary = api.getDynamicWorkflowLibraryByName("core");
        } catch (Exception e){
            coreLibrary = null;
        }
        LibraryPreparationReport report = new LibraryPreparationReport();
        
        if(coreLibrary!=null){
            System.out.println("Core library ID: " + coreLibrary.getId());
            LibraryCallback cb = new LibraryCallback() {

                @Override
                public void libraryReady(CloudWorkflowServiceLibraryItem library, LibraryPreparationReport report) {
                    System.out.println("Library downloaded. Refreshing contents");
                    try {
                        refreshLibrary(library);
                    } catch (Exception e){
                        e.printStackTrace();
                        System.exit(1);
                    } finally {
                        System.out.print("Terminating...");
                        terminateApi();
                        System.out.println(" Done.");
                    }
                }

                @Override
                public void libraryPreparationFailed(String message, LibraryPreparationReport report) {
                    System.out.println("Could not download service library: " + message);
                    System.out.print("Terminating...");
                    terminateApi();
                    System.out.println(" Done.");
                }
            };
            library.prepareDependency(api, "core", cb, report, false);

        } else {
            // Need to create the core library
            System.out.println("No library found. Creating a new core library");
            createLibrary();
            System.out.print("Terminating...");
            terminateApi();
            System.out.println(" Done.");
        }
    }

    
    @Override
    public File getLibraryDirectory() {
        return new File(System.getProperty("user.home") + File.separator + ".inkspot" + File.separator + "corelibrary");
    }

    private void terminateApi(){
        if(api!=null){
            try {
                api.terminate();
            } catch (Exception e){
                System.out.println("Error terminating API: " + e.getMessage());
            }
        }
    }
    private void refreshLibrary(CloudWorkflowServiceLibraryItem item) throws Exception {
        // Find the library directory
        File libDir = item.getFile("/lib");
        if(libDir.isDirectory()){
            // Get a list of files
            File[] contents = libDir.listFiles();
            File sourceFile;
            for(int i=0;i<contents.length;i++){
                sourceFile = new File(librarySourceDir, contents[i].getName());
                if(sourceFile.exists()){
                    System.out.println("Copying file: " + sourceFile.getPath());
                    ZipUtils.copyFile(sourceFile, contents[i]);
                } else {
                    System.out.println("Cannot find source file: " + sourceFile.getPath());
                    System.exit(1);
                }
            }
            
            // Now recompress the library
            ZipUtils.zip(item.getUnpackedDir(), item.getZipFile());
            
            // Upload this file back to the server
            DocumentRecord libDoc = item.getWrapper().getDocument();
            if(libDoc!=null){
                FileInputStream fis = new FileInputStream(item.getZipFile());
                api.upload(item.getZipDocumentRecord(), fis);
                fis.close();
            }
        } else {
            throw new Exception("Library does not contain a /lib directory");
        }
    }
    
    // Nothing exists, create the correct library structure
    private void createLibrary() throws Exception {
        // Need a working directory
        String dirName = new RandomGUID().toString();
        
        File workingDir = new File(library.getLibraryDirectory(), dirName);
        if(!workingDir.exists()){
            workingDir.mkdirs();
        }
        
        // Copy in the xml files
        File libraryXml = new File(workingDir, "library.xml");
        ZipUtils.copyStreamToFile(getClass().getResourceAsStream("/corelibrary/library.xml"), libraryXml);
        
        File dependenciesXml = new File(workingDir, "dependencies.xml");
        ZipUtils.copyStreamToFile(getClass().getResourceAsStream("/corelibrary/dependencies.xml"), dependenciesXml);
        
        // Create the lib directory
        File libDir = new File(workingDir, "lib");
        if(!libDir.exists()){
            libDir.mkdirs();
        }        
        
        // Copy in all of the .jar files
        File sourceFile;
        File targetFile;
        
        for(int i=0;i<fileNames.size();i++){
            sourceFile = new File(librarySourceDir, fileNames.get(i));
            targetFile = new File(libDir, fileNames.get(i));
            ZipUtils.copyFile(sourceFile, targetFile);
        }
        
        // Compress the directory
        File zipFile = new File(library.getLibraryDirectory(), workingDir.getName() + ".zip");
        ZipUtils.zip(workingDir, zipFile);
        
        // Now upload the core library file
        DynamicWorkflowLibrary libFile = new DynamicWorkflowLibrary();
        libFile.setLibraryName("core");
        libFile.setName(newFilename);
        Folder home = api.getHomeFolder(api.getTicket().getUserId());
        libFile = (DynamicWorkflowLibrary)api.saveDocument(home, libFile);
        
        FileInputStream inStream = new FileInputStream(zipFile);
        api.upload(libFile, inStream);
        inStream.close();
        
        // Make the core library public
        User publicUser = api.getPublicUser();
        api.grantObjectPermission(libFile, publicUser, Permission.READ_PERMISSION);
    }
}