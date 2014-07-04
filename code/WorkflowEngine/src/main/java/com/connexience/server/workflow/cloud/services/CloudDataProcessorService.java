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
package com.connexience.server.workflow.cloud.services;

import com.connexience.server.workflow.cloud.library.*;
import com.connexience.server.workflow.service.*;
import com.connexience.server.model.workflow.WorkflowInvocationFolder;
import com.connexience.server.workflow.util.XmlSerializationUtils;

import java.io.*;
import java.util.*;


/**
 * This data processor service extends the standard service to provide access to
 * cloud specific functionality such as the dependency library.
 * @author nhgh
 */
public abstract class CloudDataProcessorService extends DataProcessorService {
    /** Library item containing pointer to this services library and all of the dependencies */
    private CloudWorkflowServiceLibraryItem libraryItem;

    /** Does this service support an external connection */
    private boolean externalIOConnectionSupported = false;
    
    /** Should the service defer error handling. This is used when an external
     * debugger is attached and we do not want syntax errors etc killing the process */
    private boolean errorHandlingDeferred = false;
    
    /** Set the library item */
    public void setLibraryItem(CloudWorkflowServiceLibraryItem libraryItem) {
        this.libraryItem = libraryItem;
    }

    /** Send a command to the external process */
    public void sendCommand(String cmd) throws Exception {
        
    }
    
    /** Get the library item */
    public CloudWorkflowServiceLibraryItem getLibraryItem(){
        return libraryItem;
    }

    /** Prepare the service for debugging. This is overridden by the service to do any
     * service specific preparations such as changing error handling etc */
    public void prepareForDebugging(){
        
    }
    
    /** Remove the debug preparations */
    public void undoDebugPreparations(){
        
    }
    
    public void setErrorHandlingDeferred(boolean errorHandlingDeferred) {
        this.errorHandlingDeferred = errorHandlingDeferred;
    }

    public boolean isErrorHandlingDeferred() {
        return errorHandlingDeferred;
    }

    public void setExternalIOConnectionSupported(boolean externalIOConnectionSupported) {
        this.externalIOConnectionSupported = externalIOConnectionSupported;
    }

    public boolean isExternalIOConnectionSupported() {
        return externalIOConnectionSupported;
    }

    /** Get the library wrapper object that is specific to the type of service */
    public LibraryWrapper getLibraryWrapper(){
        if(libraryItem!=null){
            return libraryItem.getWrapper();
        } else {
            return null;
        }
    }
    
    /** Load the library definition file */
    public void loadLibraryItem(File libraryItemFile) throws Exception {
        if(libraryItemFile.exists()){
            libraryItem = (CloudWorkflowServiceLibraryItem)XmlSerializationUtils.xmlDataStoreDeserialize(libraryItemFile);
        }
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

    public WorkflowInvocationFolder getInvocationFolder() throws Exception {
        return createApiLink().getWorkflowInvocation(getCallMessage().getInvocationId());
    }

    /** Get the wrapper for a dependency item */
    public LibraryWrapper getDependencyWrapper(String name){
        CloudWorkflowServiceLibraryItem dependencyItem = getDependencyItem(name);
        if(dependencyItem!=null){
            return dependencyItem.getWrapper();
        } else {
            return null;
        }
    }
}