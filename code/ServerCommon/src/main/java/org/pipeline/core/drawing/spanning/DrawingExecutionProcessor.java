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

import org.pipeline.core.drawing.*;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/**
 * This class manages execution of an entire drawing from any point or collection
 * of points. It deals with the streaming execution and managing parallel links
 * and loop detection.
 * @author  hugo
 */
public class DrawingExecutionProcessor implements DrawingModelListener {
    /** Drawing model being executed */
    private DrawingState drawingState = null;

    /** Current drawing execution thread if there is one */
    private DrawingExecutionThread executionThread = null;
    
    /** Execution reports for all of the blocks */
    private Hashtable executionReports = new Hashtable();
    
    /** Listeners */
    private Vector listeners = new Vector();
    
    /** Creates a new instance of DrawingExecutionProcessor */
    public DrawingExecutionProcessor(DrawingModel drawing) {
        drawingState = new DrawingState(drawing);
        drawing.addDrawingModelListener(this);
    }
    
    /** Add a listener */
    public void addDrawingExecutionListener(DrawingExecutionListener l){
        if(!listeners.contains(l)){
            listeners.addElement(l);
        }
    }
    
    /** Remove a listener */
    public void removeDrawingExecutionListener(DrawingExecutionListener l){
        if(listeners.contains(l)){
            listeners.removeElement(l);
        }
    }
    
    /** Notify listeners that execution of a drawing has started */
    protected void notifyDrawingExecutionStarted(){
        Enumeration e = listeners.elements();
        while(e.hasMoreElements()){
            ((DrawingExecutionListener)e.nextElement()).drawingExecutionStarted(drawingState.getDrawingModel());
        }
    }
    
    /** Notify listeners that execution of a drawing has finished */
    protected void notifyDrawingExecutionFinished(){
        Enumeration e = listeners.elements();
        while(e.hasMoreElements()){
            ((DrawingExecutionListener)e.nextElement()).drawingExecutionFinished(drawingState.getDrawingModel());
        }        
    }
    
    /** Notify listeners of a change in the execution status of a block */
    protected void notifyBlockExecutionStarted(BlockModel block){
        Enumeration e = listeners.elements();
        while(e.hasMoreElements()){
            ((DrawingExecutionListener)e.nextElement()).blockExecutionStarted(block);
        }        
    }
    
    /** Execution thread finished */
    protected void executionThreadFinished(){
        executionThread = null;
        drawingState.getDrawingModel().setState(DrawingModel.DRAWING_IDLE);
        notifyDrawingExecutionFinished();
    }
    
    /** Execution of a block has finished */
    public void notifyBlockExecutionFinished(BlockModel block){
        Enumeration e = listeners.elements();
        while(e.hasMoreElements()){
            ((DrawingExecutionListener)e.nextElement()).blockExecutionFinished(block);
        }  
    }
    
    /** Clear the execution report list */
    public void clearExecutionReports(){
        executionReports.clear();
    }
    
    /** Clear selected items from the execution report list */
    public void clearExecutionReports(Vector itemsToDelete){
        Enumeration e = itemsToDelete.elements();
        while(e.hasMoreElements()){
            executionReports.remove(e.nextElement());
        }
    }
    
    /** Add an entry to the execution report list */
    public void addExecutionReport(BlockExecutionReport report){
        if(!executionReports.containsKey(report.getBlock())){
            executionReports.put(report.getBlock(), report);
        }
    }
    
    /** Get an execution report for a block */
    public BlockExecutionReport getExecutionReport(BlockModel block){
        if(executionReports.containsKey(block)){
            return (BlockExecutionReport)executionReports.get(block);
        } else {
            return null;
        }
    }
    
    /** Get the execution report list */
    public Hashtable getExecutionReports(){
        return executionReports;
    }
    
    /** Were there any errors in the last execution */
    public boolean errorsPresent(){
        Enumeration e = executionReports.elements();
        int status;
        while(e.hasMoreElements()){
            status = ((BlockExecutionReport)e.nextElement()).getExecutionStatus();
            if(status!=BlockExecutionReport.NO_ERRORS && status!=BlockExecutionReport.BLOCK_NOT_EXECUTED_YET){
                return true;
            }
        }
        return false;
    }
    
    /** Is this executer running */
    public synchronized boolean isRunning(){
        if(executionThread!=null){
            return true;
        } else {
            return false;
        }
    }
    
    /** Terminate the drawing execution. This call the Thread.stop method, which is deprecated. */
    public synchronized void terminateExecution(){
        if(isRunning()){
            try {
                executionThread.stop();
                executionThread.setStatus(DrawingExecutionThread.EXECUTION_TERMINATED);
                executionThreadFinished();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    /** Execute a list */
    public void executeList(Vector executionList) throws DrawingException {
        if(executionThread==null){
            if(drawingState.getDrawingModel().getState()==DrawingModel.DRAWING_IDLE){
                executionThread = new DrawingExecutionThread(this, executionList);
                drawingState.getDrawingModel().setState(DrawingModel.DRAWING_RUNNING);
                executionThread.start();
            } else {
                throw new DrawingException("Drawing is already running");
            }
            
        } else {
            throw new DrawingException("Drawing is already running");
        }
    }
    
    /** Execute a list and wait for the execution thread to finish */
    public void executeListBlocking(Vector executionList) throws DrawingException {
        if(drawingState.getDrawingModel().getState()==DrawingModel.DRAWING_IDLE){
            executionThread = new DrawingExecutionThread(this, executionList);
            executionThread.run();
            executionThread = null;
        }
        
        /*executeList(executionList);
        while(drawingState.getDrawingModel().getState()==DrawingModel.DRAWING_RUNNING){
            try {
                Thread.sleep(100);
            } catch (Exception e){
            }
        }
        if(drawingState.getDrawingModel().getState()!=DrawingModel.DRAWING_IDLE){
            throw new DrawingException("Drawing still running");
        }*/
    }
    
    /** Build an execution list using all source blocks as starting points */
    public Vector buildExecutionList(Vector startingPoints){
        // Rebuild the block states
        drawingState.refreshBlockStates();
        
        Vector execList = new Vector();
        BlockState blockState;
        Vector path;
        
        // Reset all the blocks and inputs along the paths from
        // the starting points        
        Enumeration e = startingPoints.elements();
        while(e.hasMoreElements()){
            blockState = drawingState.findBlockState((BlockModel)e.nextElement());
            if(blockState!=null){
                path = new Vector();
                blockState.resetAlongPath(path);
            }
        }
        
        // Start adding blocks to the execution list
        e = startingPoints.elements();
        while(e.hasMoreElements()){
            blockState = drawingState.findBlockState((BlockModel)e.nextElement());
            if(blockState!=null){
                path = new Vector();
                blockState.addToExecutionListAlongPath(execList, path);
            }
        }
        
        return execList;
    }
        
    /** Build an execution list from all source blocks */
    public Vector buildExecutionListFromAllSourceBlocks(){
        Vector sourceBlocks = new Vector();
        Enumeration e = drawingState.getDrawingModel().blocks();
        BlockModel block;
        while(e.hasMoreElements()){
            block = (BlockModel)e.nextElement();
            if(block.getInputCount()==0){
                sourceBlocks.addElement(block);
            }
        }
        return buildExecutionList(sourceBlocks);
    }
    
    /** Execute from all source blocks */
    public void executeFromAllSourceBlocks() throws DrawingException {
        executeList(buildExecutionListFromAllSourceBlocks());
    }
    
    /** Execute from all source blocks in a blocking fashion */
    public void blockingExecuteFromAllSourceBlocks() throws DrawingException {
        executeListBlocking(buildExecutionListFromAllSourceBlocks());
    }
    
    /** Execute from a single block */
    public void executeFromBlock(BlockModel block) throws DrawingException {
        Vector sourceBlocks = new Vector();
        sourceBlocks.addElement(block);
        Vector execList = buildExecutionList(sourceBlocks);
        executeList(execList);
    }
    
    /** Send a signal from a single block */
    public void signalFromBlock(BlockModel block, DrawingSignal signal){
        Vector sourceBlocks = new Vector();
        sourceBlocks.addElement(block);
        Vector execList = buildExecutionList(sourceBlocks);
        Enumeration e = execList.elements();
        while(e.hasMoreElements()){
            ((BlockModel)e.nextElement()).processSignal(signal);
        }
    }

    /** Send a signal to all blocks */
    public void signalAllBlocks(DrawingSignal signal){
        Enumeration e = drawingState.getDrawingModel().blocks();
        while(e.hasMoreElements()){
            ((BlockModel)e.nextElement()).processSignal(signal);
        }        
    }
    // ============================================================
    // DrawingModelListener implementation
    // ============================================================
    /** Drawing wants a re-draw */
    public void redrawRequested(DrawingModelEvent e) {
    }

    /** A block has been unselected */
    public void objectUnSelected(DrawingModelEvent e) {
    }

    /** A block has been selected */
    public void objectSelected(DrawingModelEvent e) {
    }

    /** Request editing for a block */
    public void editRequest(DrawingModelEvent e) {
    }

    /** Block wants to be executed */
    public void executeRequest(DrawingModelEvent e) throws DrawingException {
        if(e.getObject() instanceof BlockModel){
            //executeFromBlock((BlockModel)e.getObject());
            
            Vector sourceBlocks = new Vector();
            sourceBlocks.addElement((BlockModel)e.getObject());
            Vector execList = buildExecutionList(sourceBlocks);
            executeListBlocking(execList);            
        }
    }
    
    /** Block wants to be executed in an existing thread */
    public void inThreadExecuteRequest(DrawingModelEvent dme) throws DrawingException {
        if(dme.getObject() instanceof BlockModel){
            Vector sourceBlocks = new Vector();
            sourceBlocks.addElement((BlockModel)dme.getObject());
            Vector executionList = buildExecutionList(sourceBlocks);
            
            executionList.remove((BlockModel)dme.getObject());
            BlockModel block;
            BlockExecutionReport report = null;
            int i=0;
            boolean breakFlag = false;

            while(i<executionList.size() && breakFlag==false){
                block = (BlockModel)executionList.get(i);
                try {
                    report = block.execute();
                    // Update the editor if there is one
                    if(block.getEditor()!=null){
                        javax.swing.SwingUtilities.invokeLater(new BlockEditorUpdater(block));
                    }                        
                    if(report.getExecutionStatus()==BlockExecutionReport.STOP_HERE){
                        breakFlag = true;
                    }

                } catch (Exception ex){
                    ex.printStackTrace();
                }
                i++;
            }
        }
    }
    
    /** Block wants to send a signal */
    public void signalRequested(DrawingModelEvent e){
        if(e.getObject() instanceof BlockModel && e.getSignal()!=null){
            Vector sourceBlocks = new Vector();
            DrawingSignal signal =(DrawingSignal)e.getSignal();
            sourceBlocks.addElement(e.getObject());
            Vector blockList = buildExecutionList(sourceBlocks);
            for(int i=0;i<blockList.size();i++){
                ((BlockModel)blockList.elementAt(i)).processSignal(signal);
            }
        }
    }
    
    /** A connection has been removed */
    public void connectionRemoved(DrawingModelEvent e) {        
        ConnectionModel connection = (ConnectionModel)e.getObject();
        InputPortModel destination = connection.getDestinationPort();
        BlockModel destinationBlock = destination.getParentBlock();
        
        try {
            executeFromBlock(destinationBlock);
        } catch (Exception ex){
        }

        drawingState.refreshBlockStates();
    }

    /** A connection has been added */
    public void connectionAdded(DrawingModelEvent e) {
        drawingState.refreshBlockStates();
        
        // Execute along the connection - need to find
        // the block that has received the connection
        // and execute from there
        ConnectionModel connection = (ConnectionModel)e.getObject();
        BlockModel targetBlock = connection.getDestinationPort().getParentBlock();
        
        // Find the destination port and set it to contain data
        PortModel targetPort = connection.getDestinationPort();
        BlockState targetBlockState = drawingState.findBlockState(targetBlock);
        PortState targetPortState = targetBlockState.getInputState(targetPort);
        targetPortState.setCurrentState(PortState.PORT_HAS_DATA);
        
        // Do the execution
        try {
            executeFromBlock(targetBlock);
        } catch(DrawingException ex){
            ex.printStackTrace();
        }
    }

    /** A block has been removed */
    public void blockRemoved(DrawingModelEvent e) {
        drawingState.refreshBlockStates();
    }

    /** A block has been added */
    public void blockAdded(DrawingModelEvent e) {
        drawingState.refreshBlockStates();
    }


}
