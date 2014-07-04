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

/**
 * Users Java blocks should implement this interface in order to operate within
 * the e-Science Central workflow engine. This interface provides a simplified 
 * block setup. More complex blocks should extend the DataProcessorService classs.
 * @author hugo
 */
public interface WorkflowBlock {
    /**
     * This method is called when block execution is first started. It should be
     * used to setup any data structures that are used throughout the execution
     * lifetime of the block.
     */
    public void preExecute(BlockEnvironment env) throws Exception;
    
    /**
     * This code is used to perform the actual block operation. It may be called
     * multiple times if data is being streamed through the block. It is, however, 
     * guaranteed to be called at least once and always after the preExecute
     * method and always before the postExecute method;
     */
    public void execute(BlockEnvironment env, BlockInputs inputs, BlockOutputs outputs) throws Exception;
    
    /*
     * This code is called once when all of the data has passed through the block. 
     * It should be used to cleanup any resources that the block has made use of.
     */
    public void postExecute(BlockEnvironment env) throws Exception;
}