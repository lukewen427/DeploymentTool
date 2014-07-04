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
import com.connexience.server.workflow.cloud.services.CloudDataProcessorService;
import com.connexience.server.workflow.engine.datatypes.FileWrapper;
import com.connexience.server.workflow.engine.datatypes.LinkWrapper;
import com.connexience.server.workflow.engine.datatypes.ObjectWrapper;
import com.connexience.server.workflow.engine.datatypes.PropertiesWrapper;
import com.connexience.server.workflow.service.DataProcessorException;
import java.awt.geom.Arc2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.pipeline.core.data.Data;
import org.pipeline.core.drawing.TransferData;

/**
 * This class represents all of the inputs to a block
 * @author hugo
 */
public class BlockInputs {
    /** Actual DataProcessorBlock that is providing the execution environment */
    private final CloudDataProcessorService executionService;

    public BlockInputs(CloudDataProcessorService executionService) {
        this.executionService = executionService;
    }
    
    /** Get an input as a data wrapper */
    public final Data getInputDataSet(String inputName) throws DataProcessorException {
        return executionService.getInputDataSet(inputName);
    }
    
    /** Get an input as a file wrapper */
    public final List<File> getInputFiles(String inputName) throws DataProcessorException {
        TransferData td = executionService.getInputData(inputName);
        if(td instanceof FileWrapper){
            FileWrapper wrapper = (FileWrapper)executionService.getInputData(inputName);
            ArrayList<File> results = new ArrayList<>();
            Iterator<File> i = wrapper.iterator();
            while(i.hasNext()){
                results.add(i.next());
            }
            return results;
        } else {
            throw new DataProcessorException("Input: " + inputName + " is not a file-wrapper");
        }
    }
    
    /** Get an input as an object wrapper */
    public final Object getInputObject(String inputName) throws DataProcessorException {
        TransferData td = executionService.getInputData(inputName);
        if(td instanceof ObjectWrapper){
            ObjectWrapper wrapper = (ObjectWrapper)td;
            return wrapper.getPayload();
        } else {
            throw new DataProcessorException("Input: " + inputName + " is not an object-wrapper");
        }
    }
    
    /** Get an input as a properties wrapper */
    public final Map<String,Object> getPropertiesInput(String inputName) throws DataProcessorException {
        TransferData td = executionService.getInputData(inputName);
        if(td instanceof PropertiesWrapper){
            PropertiesWrapper p = (PropertiesWrapper)td;
            HashMap<String, Object> results = new HashMap<>();
            // FIXME: This code is missing extraction of key, values from the wrapper
            return results;
        } else {
            throw new DataProcessorException("Input: " + inputName + " is not a properties-wrapper");
        }
    }    
    
    public final List<ServerObject> getReferencesInput(String inputName) throws DataProcessorException {
        TransferData td = executionService.getInputData(inputName);
        if(td instanceof LinkWrapper){
            LinkWrapper w = (LinkWrapper)td;
            List<ServerObject> results = new ArrayList<ServerObject>();
            for(int i=0;i<w.size();i++){
                results.add(w.getItem(i).getServerObject());
            }
            return results;
        } else {
            throw new DataProcessorException("Input: " + inputName + " is not a list of esc objects");
        }
    }
}
