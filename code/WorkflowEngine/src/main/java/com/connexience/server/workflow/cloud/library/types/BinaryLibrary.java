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
import com.connexience.server.workflow.cloud.library.installer.*;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import org.w3c.dom.*;
import java.io.*;
import java.lang.ProcessBuilder.Redirect;
import java.util.*;

/**
 * This wrapper provides a mechanism for executing files from a binary wrapper
 * library
 * @author nhgh
 */
public class BinaryLibrary extends LibraryWrapper {
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


    /** List of executables */
    private Hashtable<String,Executable> executables = new Hashtable<>();

    /** List of packages */
    private ArrayList<Package> installerPackages = new ArrayList<>();
    
    /** Operating system string */
    private String osName = Installer.getOsName();
    
    /** Package manager string */
    private String packageManagerName = Installer.getPackageManagerName();
    
    public BinaryLibrary(){
    }

    public BinaryLibrary(CloudWorkflowServiceLibraryItem libraryItem) {
        super(libraryItem);
    }

    @Override
    public void setupWrapper(Document doc, LibraryPreparationReport report) throws Exception {
        // Parse the library.xml file to build up a list of executables that this
        // wrapper contains
        Element top = doc.getDocumentElement();
        NodeList children = top.getChildNodes();
        NodeList commands;
        NodeList packages;
        NodeList executableNodes;
        NodeList postinstallNodes;
        Executable exec;
        Package pkg;
        NamedNodeMap attributeMap;
        Node child;
        Node execNode;
        executables.clear();
        ArrayList<Command> postInstallCommands = new ArrayList<>();

        for(int i=0;i<children.getLength();i++){
            child = children.item(i);
            attributeMap = child.getAttributes();
            if(child.getNodeName().equalsIgnoreCase("commands")){
                // Check to see if it is the correct OS
                if(attributeMap.getNamedItem("os")==null ||(attributeMap.getNamedItem("os")!=null && osName!=null && osName.equals(attributeMap.getNamedItem("os").getTextContent()))){
                    // If no OS is specified, process the commands otherwise check the OS matches what the installer thinks the OS is
                    commands = child.getChildNodes();
                    for(int j=0;j<commands.getLength();j++){

                        if(commands.item(j).getNodeName().equalsIgnoreCase("command")){
                            exec = parseExecutableNode(commands.item(j));
                            if(exec!=null){
                                executables.put(exec.getName(), exec);
                            }
                        }
                    }
                }
                
            } else if (child.getNodeName().equalsIgnoreCase("executables")){
                executableNodes = child.getChildNodes();
                for(int j=0;j<executableNodes.getLength();j++){
                    execNode = executableNodes.item(j);
                    if(execNode.getNodeName().equalsIgnoreCase("dir")){
                        // Make all files in a directory executable
                        chmodDir((Element)execNode);
                    } else if(execNode.getNodeName().equalsIgnoreCase("file")){
                        // Make a file executable
                        chmodFile((Element)execNode);
                    }
                }
                
            } else if(child.getNodeName().equalsIgnoreCase("postinstall")){
                // Parse post-install shell commands
                postinstallNodes = child.getChildNodes();
                for(int j=0;j<postinstallNodes.getLength();j++){
                    execNode = postinstallNodes.item(j);
                    if(execNode.getNodeName().equalsIgnoreCase("exec")) {
                        Boolean stopOnError = true;
                        Node n = execNode.getAttributes().getNamedItem("stopOnError");
                        if (n != null && !Boolean.parseBoolean(n.getTextContent())) {
                            stopOnError = false;
                        }
                        postInstallCommands.add(new Command(execNode.getTextContent().split("\\s+"), stopOnError));
                    }
                }
            } else if(child.getNodeName().equalsIgnoreCase("packages")){
                // Check to see if it is the correct installer
                if(attributeMap.getNamedItem("installer")!=null && packageManagerName!=null && packageManagerName.equals(attributeMap.getNamedItem("installer").getTextContent())){
                    packages = child.getChildNodes();
                    for(int j=0;j<packages.getLength();j++){

                        if(packages.item(j).getNodeName().equalsIgnoreCase("package")){
                            pkg = parsePackageNode(packages.item(j));
                            if(pkg!=null){
                                installerPackages.add(pkg);
                            }
                        }
                    }
                }
            }
        }

        // Install the required packages
        if(packageManagerName!=null && installerPackages.size()>0){
            PackageManager pkgMgr = Installer.createManager(packageManagerName);
            InstallResult result;
            
            for(int i=0;i<installerPackages.size();i++){
                pkg = installerPackages.get(i);
                result = null;
                if(!pkgMgr.isInstalled(pkg.getName(), report)){
                    if(pkg.getArgs()!=null){
                        try {
                            result = pkgMgr.installPackage(pkg.getName(), pkg.getArgs(), report);
                        } catch (Exception e){
                            throw new Exception("Error installing package: " + pkg.getName() + " using " + packageManagerName, e);
                        }
                    } else {
                        try {
                            result = pkgMgr.installPackage(pkg.getName(), "", report);
                        } catch (Exception e){
                            throw new Exception("Error installing package: " + pkg.getName() + " using " + packageManagerName, e);
                        }
                    }
                    if(result!=null && result.getStatus()!=InstallResult.InstallStatus.INSTALLED_OK){
                        throw new Exception("Could not install package: " + pkg.getName() + " using " + packageManagerName);
                    }
                }
            }
        }

        // Execute the setup files
        for (Command cmd : postInstallCommands) {
            execProcess(cmd);
        }
    }


    /** Execute a file and wait */
    private void execProcess(Command cmd)
    throws Exception
    {
        if (cmd.commandArray.length == 0) { 
            throw new Exception("Missing command value");
        }

        // Prepare command to execute.
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(isRelocated() ? getRelocatedDir() : getLibraryItem().getUnpackedDir());
        pb.redirectOutput(Redirect.INHERIT);
        pb.redirectError(Redirect.INHERIT);
        pb.command(cmd.commandArray);

        Process p = pb.start();
        int exitCode = p.waitFor();
        if (cmd.stopOnError && exitCode != 0) {
            throw new Exception("Command " + cmd.commandArray[0] + " returned non-zero exit code: " + exitCode);
        }
    }

    /** Parse and run a chmod node */
    private void chmodFile(Element chmodNode) throws Exception {
        String path = chmodNode.getAttribute("path");
        if(path!=null){
            File fullPath = getLibraryItem().getFile(path);
            if(fullPath.exists() && fullPath.isFile()){
                fullPath.setExecutable(true);
            }
        }
    }

    /** Parse and run a chmod node for a directory */
    private void chmodDir(Element chmodNode) throws Exception {
        String path = chmodNode.getAttribute("path");
        if(path!=null){
            File dir = getLibraryItem().getFile(path);
            if(dir.isDirectory()){
                File[] contents = dir.listFiles();
                for(int i=0;i<contents.length;i++){
                    if(contents[i].isFile()){
                        contents[i].setExecutable(true);
                    }
                }
            }
        }
    }
    
    /** Parse a package node */
    private Package parsePackageNode(Node packageNode) throws Exception {
        NodeList children = packageNode.getChildNodes();
        String name = null;
        String args = null;
        Node child;

        for(int i=0;i<children.getLength();i++){
            child = children.item(i);
            if(child.getNodeName().equalsIgnoreCase("name")){
                name = child.getTextContent();
            } else if(child.getNodeName().equalsIgnoreCase("args")){
                args = child.getTextContent();
            }
        }

        if(name!=null){
            return new Package(name, args);
        } else {
            return null;
        }        
    }
    
    /** Parse an executable node from the XML file */
    private Executable parseExecutableNode(Node executableNode) throws Exception {
        NodeList children = executableNode.getChildNodes();
        String name = null;
        String cmd = null;
        boolean absolute = false;
        Node child;

        for(int i=0;i<children.getLength();i++){
            child = children.item(i);
            if(child.getNodeName().equalsIgnoreCase("name")){
                name = child.getTextContent();
            } else if(child.getNodeName().equalsIgnoreCase("cmd")){
                cmd = child.getTextContent();
            } else if(child.getNodeName().equalsIgnoreCase("absolute")){
                if(child.getTextContent().equalsIgnoreCase("true")){
                    absolute = true;
                } else {
                    absolute = false;
                }
            }
        }

        if(name!=null && cmd!=null){
            return new Executable(name, cmd, absolute);
        } else {
            return null;
        }
    }

    /** Get an executable file by name */
    public Executable getExecutable(String name) {
        if(executables.containsKey(name)){
            return executables.get(name);
        } else {
            return null;
        }
    }

    /** Class representing an installable package */
    public class Package implements Serializable {
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

        /** Name of this package */
        private String name;
        
        /** Extra arguments for the installer */
        private String args;

        public Package(String name, String args) {
            this.name = name;
            this.args = args;
        }

        public Package() {
        }

        public String getArgs() {
            return args;
        }

        public String getName() {
            return name;
        }

        public void setArgs(String args) {
            this.args = args;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    
    private static class Command {
        String[] commandArray;
        boolean stopOnError;

        Command(String[] commandArray, boolean stopOnError)
        {
            this.commandArray = commandArray;
            this.stopOnError = stopOnError;
        }
    }

    /** Class representing an executable bit of code */
    public class Executable implements Serializable {
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


        /** Name of this executable item */
        private String name;

        /** Actual command relative to the base directory of the library */
        private String relativeCmd;

        /** Is this command an absolute file reference */
        private boolean absolute = false;

        public Executable() {
        }

        public Executable(String name, String relativeCmd, boolean absolute) {
            this.name = name;
            this.relativeCmd = relativeCmd;
            this.absolute = absolute;
        }

        public boolean isAbsolute() {
            return absolute;
        }

        public void setAbsolute(boolean absolute) {
            this.absolute = absolute;
        }

        public String getName() {
            return name;
        }

        public String getRelativeCmd() {
            return relativeCmd;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setRelativeCmd(String relativeCmd) {
            this.relativeCmd = relativeCmd;
        }
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();

        int count = 0;
        for (Executable exec : executables.values()) {
            store.add("Executable" + count + "Name", exec.getName());
            store.add("Executable" + count + "Cmd", exec.getRelativeCmd());
            store.add("Executable" + count + "Absolute", exec.isAbsolute());
            count++;
        }
        store.add("ExecutableCount", count);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        int executableCount = store.intValue("ExecutableCount", 0);
        executables.clear();
        Executable exec;
        for(int i=0;i<executableCount;i++){
            exec = new Executable();
            exec.setName(store.stringValue("Executable" + i + "Name", ""));
            exec.setRelativeCmd(store.stringValue("Executable" + i + "Cmd", ""));
            exec.setAbsolute(store.booleanValue("Executable" + i + "Absolute", false));
            executables.put(exec.getName(), exec);
        }
    }
}