/* =================================================================
 *                     conneXience Data Pipeline
 * =================================================================
 *
 * Copyright 2006 Hugo Hiden and Adrian Conlin
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.pipeline.core.drawing;

import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.io.Serializable;

/**
 * This class describes the execution result for a block. It
 * contains details of whether the block executed successfully,
 * how long it took to execute and any error messages produced.
 * @author hugo
 */
public class BlockExecutionReport implements Serializable, XmlStorable {
    // Status flags
    
    /** Block hasn't executed yet */
    public static final int BLOCK_NOT_EXECUTED_YET = 0;
    
    /** Block executed with no problems */
    public static final int NO_ERRORS = 1;
    
    /** Block couldn't get all its input data */
    public static final int INPUT_DATA_ERROR = 2;
    
    /** Block had an internal error */
    public static final int INTERNAL_ERROR = 3;
    
    /** Not executed because of upstream errors */
    public static final int UPSTREAM_ERRORS = 4;
    
    /** Signature error */
    public static final int BLOCK_FAILED_SIGNATURE_TEST = 5;
    
    /** Cannot set output data */
    public static final int OUTPUT_DATA_ERROR = 6;
    
    /** Execution should be stopped here */
    public static final int STOP_HERE = 7;
    
    /** Execution status */
    private int executionStatus = NO_ERRORS;
    
    /** Block that this execution report is attached to */
    private transient BlockModel block;
    
    /** ID of the block that this report refers to */
    private String blockGuid;
    
    /** Additional message text. This is in addition to the standard message
     * that depends on the status */
    private String additionalMessage;

    /** Text captured from the system.out if present */
    private String commandOutput = null;

    /** Is this report used for storing command output if there was any */
    private boolean commandOutputStored = false;

    /** Creates an empty BlockExecutionReport */
    public BlockExecutionReport(){
        
    }
    
    /** Creates a new instance of BlockExecutionReport */
    public BlockExecutionReport(BlockModel block) {
        executionStatus = NO_ERRORS;
        this.block = block;
        this.blockGuid = block.getBlockGUID();
        additionalMessage = "";
    }
    
    /** Creates a new instance of BlockExecutionReport */
    public BlockExecutionReport(BlockModel block, int executionStatus) {
        this.block = block;
        this.blockGuid = block.getBlockGUID();
        this.executionStatus = executionStatus;
        additionalMessage = "";
    }
    
    /** Creates a new instance of BlockExecutionReport */
    public BlockExecutionReport(BlockModel block, int executionStatus, String additionalMessage) {
        this.block = block;
        this.blockGuid = block.getBlockGUID();
        this.executionStatus = executionStatus;
        this.additionalMessage = additionalMessage;
    }

    /** Does this report contain and commandOutput data */
    public boolean containsCommandOutput(){
        if(commandOutput!=null && !(commandOutput.trim().equalsIgnoreCase(""))){
            return true;
        } else {
            return false;
        }
    }

    /** Get the command output data */
    public String getCommandOutput(){
        return commandOutput;
    }

    /** Set the command output data */
    public void setCommandOutput(String commandOutput){
        this.commandOutput = commandOutput;
    }
    
    /** Get the message text */
    public String getMessage(){
        switch(executionStatus){
            case BLOCK_NOT_EXECUTED_YET:
                return "Block not executed";
                
            case NO_ERRORS:
                return "Executed OK";
                
            case INPUT_DATA_ERROR:
                return "Cannot access block input data";
                
            case INTERNAL_ERROR:
                return "Internal calculation error";
                
            case UPSTREAM_ERRORS:
                return "Block not executed because of upstream errors";
                
            case BLOCK_FAILED_SIGNATURE_TEST:
                return "Block signature not valid";
                
            case OUTPUT_DATA_ERROR:
                return "Cannot set block output data";
                
            case STOP_HERE:
                return "Block stopped execution";
                
            default:
                return "Undefined message";
        }        
    }
    
    /** Get the GUID of the block that this report refers to */
    public String getBlockGuid(){
        return blockGuid;
    }
    
    /** Get the block that this report refers to */
    public BlockModel getBlock(){
        return block;
    }
    
    /** Get the additional message */
    public String getAdditionalMessage(){
        return additionalMessage;
    }

    /** Set the additional message */
    public void setAdditionalMessage(String additionalMessage){
        this.additionalMessage = additionalMessage;
    }
    
    /** Get the execution status */
    public int getExecutionStatus(){
        return executionStatus;
    }

    /** Set the execution status */
    public void setExecutionStatus(int executionStatus){
        this.executionStatus = executionStatus;
    }

    /** Set whether this block would be used for command output storage if there was any */
    public void setCommandOutputStored(boolean commandOutputStored) {
        this.commandOutputStored = commandOutputStored;
    }

    /** Get whether this block would be used for command output storage if there was any */
    public boolean isCommandOutputStored() {
        return commandOutputStored;
    }
    
    /** Save state */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("BlockExecutionReport");
        store.add("ExecutionStatus", executionStatus);
        store.add("AdditionalMessage", additionalMessage);
        store.add("BlockGUID", blockGuid);
        store.add("CommandOutput", commandOutput);
        store.add("CommandOutputStored", commandOutputStored);
        return store;
    }

    /** Restore state */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        executionStatus = store.intValue("ExecutionStatus", BLOCK_NOT_EXECUTED_YET);
        additionalMessage = store.stringValue("AdditionalMessage", "");
        blockGuid = store.stringValue("BlockGUID", "");
        commandOutput = store.stringValue("CommandOutput", "");
        commandOutputStored = store.booleanValue("CommandOutputStored", false);
    }
}
