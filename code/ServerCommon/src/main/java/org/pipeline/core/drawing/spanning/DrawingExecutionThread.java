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

package org.pipeline.core.drawing.spanning;

// <editor-fold defaultstate="collapsed" desc=" Imports ">

import org.pipeline.core.drawing.BlockExecutionException;
import org.pipeline.core.drawing.BlockExecutionReport;
import org.pipeline.core.drawing.BlockModel;
import org.pipeline.core.drawing.OutputPortModel;
import org.pipeline.core.xmlstorage.XmlSignable;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.pipeline.core.xmlstorage.security.XmlDataStoreSignatureHelper;

import java.util.Enumeration;
import java.util.Vector;
// </editor-fold>

/**
 * This is the thread that is responsible for executing a drawing
 * @author hugo
 */
public class DrawingExecutionThread extends Thread {
    // Thread exit status flags
    
    /** Execution has not been started */
    public static final int EXECUTION_NOT_STARTED = 0;
    
    /** Thread has not exited */
    public static final int EXECUTION_IN_PROGRESS = 1;
    
    /** Thread finished OK */
    public static final int EXECUTION_OK = 2;
    
    /** Thread had an error during execution */
    public static final int EXECUTION_ERROR = 3;
    
    /** Thread was terminated */
    public static final int EXECUTION_TERMINATED = 4;
    
    /** Thread execution status */
    private int status = EXECUTION_NOT_STARTED;
    
    /** Parent drawing processor */
    private DrawingExecutionProcessor parent = null;
    
    /** Block list to execute */
    private Vector executionList;
    
    /** Creates a new instance of DrawingExecutionThread */
    public DrawingExecutionThread(DrawingExecutionProcessor parent, Vector executionList) {
        this.parent = parent;
        this.executionList = executionList;
    }
        
    /** Get the thread status */
    public synchronized int getStatus(){
        return status;
    }
    
    /** Set the status */
    protected synchronized void setStatus(int status){
        this.status = status;
    }
    
    /** Run the thread */
    public void run(){
        status = EXECUTION_IN_PROGRESS;
        Enumeration e = executionList.elements();
        BlockModel block;
        BlockExecutionReport report = null;
        boolean failed = false;
        parent.clearExecutionReports(executionList);
        parent.notifyDrawingExecutionStarted();
        Enumeration outputs;
        XmlDataStoreSignatureHelper sig;
        boolean breakFlag = false;
        
        while(e.hasMoreElements() && breakFlag==false){
            block = (BlockModel)e.nextElement();
            try {
                parent.notifyBlockExecutionStarted(block);
                // Check for signing
                if(block instanceof XmlSignable){
                    sig = ((XmlSignable)block).getSignatureData();
                    if(sig.objectSigned()){
                        try {
                            if(sig.verifyObject()){
                                report = block.execute();
                            } else {
                                report = new BlockExecutionReport(block, BlockExecutionReport.BLOCK_FAILED_SIGNATURE_TEST);
                            }
                            
                        } catch (XmlStorageException se){
                            report = new BlockExecutionReport(block, BlockExecutionReport.BLOCK_FAILED_SIGNATURE_TEST);
                        }
                        
                    } else {
                        report = block.execute();                
                    }
                } else {
                    report = block.execute();                
                }
                parent.addExecutionReport(report);
                        
                // Update the editor if there is one
                if(block.getEditor()!=null){
                    failed = true;
                    javax.swing.SwingUtilities.invokeLater(new BlockEditorUpdater(block));
                }
                
                // Break execution if the block asked for it
                if(report.getExecutionStatus()==BlockExecutionReport.STOP_HERE){
                    breakFlag = true;
                }
                
            } catch (BlockExecutionException ex){
                if(block!=null){
                    if(parent.getExecutionReport(block)==null){
                        parent.addExecutionReport(new BlockExecutionReport(block, BlockExecutionReport.INTERNAL_ERROR, ex.getLocalizedMessage()));
                    }
                }
                System.out.println(ex.getLocalizedMessage());
                
            } finally {
                // Tidy up
                if(report!=null && report.getExecutionStatus()!=BlockExecutionReport.NO_ERRORS){
                    System.out.println(report.getAdditionalMessage());
                    outputs = block.outputs();
                    while(outputs.hasMoreElements()){
                        ((OutputPortModel)outputs.nextElement()).clearData();
                    }
                }                
                parent.notifyBlockExecutionFinished(block);
            }
        }
        
        // Set the correct status
        if(failed==true){
            status = EXECUTION_ERROR;
        } else {
            status = EXECUTION_OK;
        }

        parent.executionThreadFinished();

    }
}
