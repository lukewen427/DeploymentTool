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

import com.connexience.server.workflow.WorkflowBlock;
import com.connexience.server.workflow.BlockEnvironment;
import com.connexience.server.workflow.BlockInputs;
import com.connexience.server.workflow.BlockOutputs;
import com.connexience.server.workflow.service.DataProcessorException;

/**
 * This class provides an alternate Java data processor service that is designed
 * to make it easier for people writing workflow blocks to interact with the 
 * workflow engine. It removes access to many of the methods of DataProcessorService
 * that are not required for basic blocks. 
 * @author hugo
 */
public class SimplifiedJavaService extends CloudDataProcessorService {
    /** Block implementation providing the service */
    private WorkflowBlock blockImplementation;

    /** Workflow environment object for blocks to access the engine */
    BlockEnvironment env = new BlockEnvironment(this);
    
    /** Block inputs collection */
    BlockInputs inputs = new BlockInputs(this);
    
    /** Block outputs collection */
    BlockOutputs outputs = new BlockOutputs(this);
    
    public SimplifiedJavaService(WorkflowBlock blockImplementation) {
        this.blockImplementation = blockImplementation;
    }
    
    @Override
    public void execute() throws Exception {
        if(blockImplementation!=null){
            blockImplementation.execute(env, inputs, outputs);
        } else {
            throw new Exception("WorkflowBlock not initialised");
        }
    }

    @Override
    public void executionAboutToStart() throws Exception {
        if(blockImplementation!=null){
            blockImplementation.preExecute(env);
        } else {
            throw new Exception("WorkflowBlock not initialised");
        }
    }

    @Override
    public void allDataProcessed() throws Exception {
        if(blockImplementation!=null){
            blockImplementation.postExecute(env);
        } else {
            throw new Exception("WorkflowBlock not initialised");
        }
    }
}
