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

import com.connexience.server.model.ServerObject;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.workflow.cloud.services.CloudDataProcessorService;
import com.connexience.server.workflow.engine.datatypes.FileWrapper;
import com.connexience.server.workflow.engine.datatypes.LinkWrapper;
import com.connexience.server.workflow.engine.datatypes.ObjectWrapper;
import com.connexience.server.workflow.engine.datatypes.PropertiesWrapper;
import com.connexience.server.workflow.service.DataProcessorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.pipeline.core.data.Data;
import org.pipeline.core.xmlstorage.XmlDataStore;

/**
 * This class represents all of the outputs from a block
 * @author hugo
 */
public class BlockOutputs {
    /** Actual DataProcessorBlock that is providing the execution environment */
    private final CloudDataProcessorService executionService;

    public BlockOutputs(CloudDataProcessorService executionService) {
        this.executionService = executionService;
    }
    
    
    /** Set an output as a Data set */
    public final void setOutputDataSet(String outputName, Data outputData) throws DataProcessorException {
        executionService.setOutputDataSet(outputName, outputData);
    }
    
    /** Set a single file on an output */
    public final void setOutputFile(String outputName, File file) throws DataProcessorException {
        File workingDir = executionService.getWorkingDirectory();
        try {
            FileWrapper wrapper = new FileWrapper(workingDir);
            wrapper.addFile(file);
            executionService.setOutputData(outputName, wrapper);
        } catch (IOException ioe){
            throw new DataProcessorException("Error setting files for output: " + outputName, ioe);
        }        
    }
    
    /** Send a set of propertires to a properties-wrapper output */
    public final void setOutputProperties(String outputName, Map<String, Object> properties) throws DataProcessorException {
        XmlDataStore store = new XmlDataStore();
        for(String key : properties.keySet()){
            try {
                store.add(key, properties.get(key));
            } catch (Exception e){
                throw new DataProcessorException("Cannot add property: " + key + ": " + e.getMessage(), e);
            }
        }
        executionService.setOutputData(outputName, new PropertiesWrapper(store));
    }
    
    /** Add a set of files to a file-wrapper output */
    public final void setOutputFiles(String outputName, List<File> files) throws DataProcessorException {
        File workingDir = executionService.getWorkingDirectory();
        try {
            FileWrapper wrapper = new FileWrapper(workingDir);
            for(File f : files){
                wrapper.addFile(f);
            }
            executionService.setOutputData(outputName, wrapper);
        } catch (IOException ioe){
            throw new DataProcessorException("Error setting files for output: " + outputName, ioe);
        }
    }
    
    /** Set an Object as an output */
    public final void setOutputObject(String outputName, Object value) throws DataProcessorException {
        ObjectWrapper wrapper = new ObjectWrapper(value);
        executionService.setOutputData(outputName, wrapper);
    }    
    
    /** Set an output as a set of ServerObjects */
    public final void setOutputLinks(String outputName, List<ServerObject> links) throws DataProcessorException {
        LinkWrapper wrapper = new LinkWrapper();
        for(ServerObject o : links){
            if(o instanceof Folder){
                wrapper.addFolder((Folder)o);
            } else if(o instanceof DocumentRecord){
                try {
                    wrapper.addDocument((DocumentRecord)o, executionService.createApiLink().getLatestVersion(o.getId()));
                } catch(Exception e){
                    throw new DataProcessorException("Error getting latest version data for document: " + o.getId());
                }
            }
        }
        executionService.setOutputData(outputName, wrapper);
    }
    
    /** Set an output as a single server object */
    public final void setOutputLink(String outputName, ServerObject link) throws DataProcessorException {
        ArrayList<ServerObject>links = new ArrayList<>();
        links.add(link);
        setOutputLinks(outputName, links);
    }
}