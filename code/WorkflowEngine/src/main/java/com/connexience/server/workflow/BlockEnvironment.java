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
package com.connexience.server.workflow;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.workflow.cloud.library.CloudWorkflowServiceLibraryItem;
import com.connexience.server.workflow.cloud.library.types.BinaryLibrary;
import com.connexience.server.workflow.cloud.services.CloudDataProcessorService;
import com.connexience.server.workflow.service.DataProcessorException;
import com.connexience.server.workflow.xmlstorage.StringListWrapper;
import com.connexience.server.workflow.xmlstorage.StringPairListWrapper;
import java.io.File;
import java.util.Date;

/**
 * This class provides an interface to the environment in which the block is
 * executing. It provides mechanisms to get and set input / output data and
 * log messages to the workflow execution environment.
 * @author hugo
 */
public final class BlockEnvironment {
    /** Actual DataProcessorBlock that is providing the execution environment */
    private final CloudDataProcessorService executionService;

    /** Current completion percentage */
    private int percentComplete = 0;
    
    
    public BlockEnvironment(CloudDataProcessorService executionService) {
        this.executionService = executionService;
    }
        
    /** Get a block property as a String */
    public final String getStringProperty(String name, String defaultValue) {
        return executionService.getEditableProperties().stringValue(name, defaultValue);
    }
    
    /** Get a block property as a double */
    public final double getDoubleProperty(String name, double defaultValue){
        return executionService.getEditableProperties().doubleValue(name, defaultValue);
    }
    
    /** Get a block property as a long */
    public final long getLongProperty(String name, long defaultValue){
        return executionService.getEditableProperties().longValue(name, defaultValue);
    }
    
    /** Get a block property as an integer */
    public final int getIntProperty(String name, int defaultValue){
        return executionService.getEditableProperties().intValue(name, defaultValue);
    }
    
    /** Get a block property as a Date */
    public final Date getDateProperty(String name, Date defaultValue) {
        return executionService.getEditableProperties().dateValue(name, defaultValue);
    }
    
    /** Get a block property as a boolean */
    public final boolean getBooleanProperty(String name, boolean defaultValue){
        return executionService.getEditableProperties().booleanValue(name, defaultValue);
    }
    
    /** Get a block property as an array of Strings */
    public final String[] getStringListProperty(String name) throws DataProcessorException {
        if(executionService.getEditableProperties().containsName(name)){
            try {
                StringListWrapper sl = (StringListWrapper)executionService.getEditableProperties().xmlStorableValue(name);
                return sl.toStringArray();
            } catch (Exception e){
                throw new DataProcessorException(name + " is not a StringListWrapper: " + e.getMessage(), e);
                        
            }
        } else {
            throw new DataProcessorException("No such property: " + name);
        }
    }
    
    /** Get a block property as a 2D array of Strings */
    public final String[][] getStringMatrixProperty(String name) throws DataProcessorException {
        if(executionService.getEditableProperties().containsName(name)){
            try {
                StringPairListWrapper sl = (StringPairListWrapper)executionService.getEditableProperties().xmlStorableValue(name);
                return sl.toStringArray();
            } catch (Exception e){
                throw new DataProcessorException(name + " is not a StringPainListWrapper: " + e.getMessage(), e);           
            }
        } else {
            throw new DataProcessorException("No such property: " + name);
        }        
    }
    
    /** Get a block property as a DocumentRecord */
    public final DocumentRecord getDocumentProperty(String name) throws ConnexienceException {
        if(executionService.getEditableProperties().containsName(name)){
            try {
                return (DocumentRecord)executionService.getEditableProperties().xmlStorableValue(name);
            } catch (Exception e){
                throw new ConnexienceException("Property: " + name + " is not a DocumentRecord", e);
            }
        } else {
            throw new ConnexienceException("No such document property: " + name + " exists");
        }
    }
    
    /** Get a block property as a Folder */
    public final Folder getFolderProperty(String name) throws ConnexienceException {
        if(executionService.getEditableProperties().containsName(name)){
            try {
                return (Folder)executionService.getEditableProperties().xmlStorableValue(name);
            } catch (Exception e){
                throw new ConnexienceException("Property: " + name + " is not a Folder", e);
            }
        } else {
            throw new ConnexienceException("No such folder property: " + name + " exists");
        }
    }
        
    /** Get a block property as a ServerObject */
    public final ServerObject getServerObjectProperty(String name) throws ConnexienceException {
        if(executionService.getEditableProperties().containsName(name)){
            try {
                return (ServerObject)executionService.getEditableProperties().xmlStorableValue(name);
            } catch (Exception e){
                throw new ConnexienceException("Property: " + name + " is not a ServerObject", e);
            }
        } else {
            throw new ConnexienceException("No such ServerObject property: " + name + " exists");
        }
    }    
    
    /** Get the workflow invocation directory */
    public final File getWorkingDirectory() throws ConnexienceException {
        try {
            return executionService.getWorkingDirectory();
        } catch (Exception e){
            throw new ConnexienceException("Cannot access working directory: " + e.getMessage(), e);
        }
    }
    
    /** Get the underlying block */
    public CloudDataProcessorService getExecutionService(){
        return executionService;
    }
    
    /** Get the unique ID of the block in the workflow */
    public String getBlockContextId(){
        return executionService.getCallMessage().getContextId();
    }
    
    /** Download a file from the server to the invocation directory */
    public File downloadFile(DocumentRecord document) throws Exception {
        DocumentRecord serverDoc = executionService.createApiLink().getDocument(document.getId());
        File outputFile = new File(getWorkingDirectory(), serverDoc.getName());
        executionService.createApiLink().downloadToFile(document, outputFile);
        return outputFile;
    }
    
    /** Upload a file to the invocation directory on the server */
    public DocumentVersion uploadFile(File fileToUpload) throws Exception {
        DocumentRecord newDoc = executionService.createApiLink().createDocument(executionService.getInvocationFolder(), fileToUpload.getName());
        return executionService.createApiLink().uploadFile(newDoc, fileToUpload);
    }
    
    /** Get the workflow invocation directory on the server */
    public Folder getServerInvocationFolder() throws Exception{
        return executionService.getInvocationFolder();
    }
    
    /** Send a percent complete message */
    public void setProgress(long total, long current) throws Exception {
        int percent = (int)(((double)current / (double)total) * 100.0);
        if(percent!=percentComplete){
            percentComplete = percent;
            executionService.createApiLink().setCurrentBlockStreamingProcessAsync(executionService.getCallMessage().getInvocationId(), executionService.getCallMessage().getContextId(), 100, percent);
        }
    }
    
    /** Get the installed directory of a dependency */
    public File getDependencyDirectory(String name) throws Exception {
        CloudWorkflowServiceLibraryItem item = executionService.getDependencyItem(name);
        if(item!=null){
            return item.getUnpackedDir();
        } else {
            throw new Exception("No such dependency: " + name);
        }
    }
    
    /** Get a property from a dependency */
    public String getDependencyProperty(String dependencyName, String propertyName, String defaultValue) throws Exception {
        CloudWorkflowServiceLibraryItem item = executionService.getDependencyItem(dependencyName);
        if(item!=null){
            return item.getWrapper().getProperty(propertyName, defaultValue);
        } else {
            throw new Exception("No such dependency: " + dependencyName);
        }
    }
    
    /** Get an executable command path from a dependency */
    public String getDependencyCommand(String dependencyName, String commandName) throws Exception {
        CloudWorkflowServiceLibraryItem item = executionService.getDependencyItem(dependencyName);
        if(item!=null){
            if(item.getWrapper() instanceof BinaryLibrary){
                return ((BinaryLibrary)item.getWrapper()).getExecutable(commandName).getRelativeCmd();
            } else {
                throw new Exception(dependencyName + " is not a binary library");
            }
        } else {
            throw new Exception("No such dependency: " + dependencyName);
        }        
    }
}