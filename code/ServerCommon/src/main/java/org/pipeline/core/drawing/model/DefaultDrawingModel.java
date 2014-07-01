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

package org.pipeline.core.drawing.model;

import org.pipeline.core.drawing.*;
import org.pipeline.core.drawing.layout.BlockModelPosition;
import org.pipeline.core.drawing.layout.ConnectionModelPosition;
import org.pipeline.core.drawing.layout.DrawingLayout;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlSignable;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.pipeline.core.xmlstorage.security.XmlDataStoreSignatureHelper;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This is the default implementation of a drawing.
 *
 * @author  hugo
 *
 * TODO Add support for retrieving a list of connections similar to blocks()
 *      needed for creating LinkNodes for loaded drawings
 */
public class DefaultDrawingModel implements Serializable, DrawingModel, XmlStorable, XmlSignable {
    /**
     * Blocks contained in this drawing 
     */
    private Vector blocks = new Vector();
    
    /** 
     * Selected blocks 
     */
    private Vector selectedBlocks = new Vector();
    
    /** 
     * Listeners
     */
    private Vector listeners = new Vector();
    
    /**
     * Layout data for the blocks in this drawing
     */
    private DrawingLayout layout = new DrawingLayout(this);
    
    /**
     * Current drawing state
     */
    private int currentState = DRAWING_IDLE;
    
    /**
     * Signature information
     */
    private XmlDataStoreSignatureHelper signatureData;

    /**
     * Auxiliary data for this drawing. This is used to store other data and properties
     * that a workflow engine may need in order to execute this drawing. For example,
     * the online workflow editor allows external files to be referenced and downloaded
     * before a workflow executes. These are stored in here.
     */
    private XmlDataStore auxiliaryData = new XmlDataStore();

    /**
     * Creates a new instance of DefaultDrawingModel
     */
    public DefaultDrawingModel() {
        signatureData = new XmlDataStoreSignatureHelper(this);
    }
    
    /**
     * Get the signature data
     */
    public XmlDataStoreSignatureHelper getSignatureData() {
        return signatureData;
    }
    
    /**
     * Notify selection of a block
     */
    private void notifyObjectSelected(DrawingObject object){
        Enumeration l = listeners.elements();
        DrawingModelEvent e = new DrawingModelEvent(this, object);
        while(l.hasMoreElements()){
            ((DrawingModelListener)l.nextElement()).objectSelected(e);
        }
    }
    
    /**
     * Notify unselection of a block
     */
    private void notifyObjectUnSelected(DrawingObject object){
        Enumeration l = listeners.elements();
        DrawingModelEvent e = new DrawingModelEvent(this, object);
        while(l.hasMoreElements()){
            ((DrawingModelListener)l.nextElement()).objectUnSelected(e);
        }
    }
    
    /**
     * Notify addition of a block
     */
    private void notifyBlockAdded(BlockModel block){
        Enumeration l = listeners.elements();
        DrawingModelEvent e = new DrawingModelEvent(this, block);
        while(l.hasMoreElements()){
            ((DrawingModelListener)l.nextElement()).blockAdded(e);
        }
        
    }
    
    /**
     * Notify removal of a block
     */
    private void notifyBlockRemoved(BlockModel block){
        Enumeration l = listeners.elements();
        DrawingModelEvent e = new DrawingModelEvent(this, block);
        while(l.hasMoreElements()){
            ((DrawingModelListener)l.nextElement()).blockRemoved(e);
        }        
    }
    
    /**
     * Notify removal of a connection
     */
    private void notifyConnectionRemoved(ConnectionModel connection){
        Enumeration l = listeners.elements();
        DrawingModelEvent e = new DrawingModelEvent(this, connection);
        while(l.hasMoreElements()){
            ((DrawingModelListener)l.nextElement()).connectionRemoved(e);
        }        
    }

    /**
     * Send a re-draw request
     */
    private void notifyRedrawRequest(){
        Enumeration l = listeners.elements();
        DrawingModelEvent e = new DrawingModelEvent(this, null);
        while(l.hasMoreElements()){
            ((DrawingModelListener)l.nextElement()).redrawRequested(e);
        }                
    }
    
    /**
     * Send a block edit request
     */
    private void notifyEditRequest(BlockModel block) {
        Enumeration l = listeners.elements();
        DrawingModelEvent e = new DrawingModelEvent(this, block);
        while(l.hasMoreElements()){
            ((DrawingModelListener)l.nextElement()).editRequest(e);
        }                
    }
    
    /** Send a block execution request */
    private void notifyExecuteRequest(BlockModel block) throws DrawingException {
        Enumeration l = listeners.elements();
        DrawingModelEvent e = new DrawingModelEvent(this, block);
        while(l.hasMoreElements()){
            ((DrawingModelListener)l.nextElement()).executeRequest(e);
        }                        
    }
    
    /** Send an inThread block execution request */
    private void notifyInThreadExecuteRequest(BlockModel block) throws DrawingException {
        Enumeration l = listeners.elements();
        DrawingModelEvent e = new DrawingModelEvent(this, block);
        while(l.hasMoreElements()){
            ((DrawingModelListener)l.nextElement()).inThreadExecuteRequest(e);
        }                        
    }
        
    /**
     * Notify the connection of two ports
     */
    private void notifyBlockConnection(ConnectionModel connection){
        Enumeration l = listeners.elements();
        DrawingModelEvent e = new DrawingModelEvent(this, connection);
        while(l.hasMoreElements()){
            ((DrawingModelListener)l.nextElement()).connectionAdded(e);
        }                
    }
    
    /**
     * Notify a signal request to listeners
     */
    private void notifySignalRequest(BlockModel block, DrawingSignal signal){
        Enumeration l = listeners.elements();
        DrawingModelEvent e = new DrawingModelEvent(this, block, signal);
        while(l.hasMoreElements()){
            ((DrawingModelListener)l.nextElement()).signalRequested(e);
        }
    }
    
    // =========================================================================
    // DrawingModel implementation
    // =========================================================================
    
    /**
     * Set the current drawing state
     */
    public void setState(int currentState){
        this.currentState = currentState;
    }
    
    /**
     * Get the current drawing state
     */
    public int getState() {
        return currentState;
    }    
    
    /**
     * Add a listener 
     */
    public void addDrawingModelListener(DrawingModelListener listener) {
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    /**
     * Unselect all objects 
     */
    public void clearObjectSelection() {
        selectedBlocks.clear();
        notifyObjectUnSelected(null);
    }

    /**
     * Remove a listener 
     */
    public void removeDrawingModelListener(DrawingModelListener listener) {
        if(listeners.contains(listener)){
            listeners.remove(listener);
        }
    }

    /**
     * Link two ports together 
     */
    public void connectPorts(PortModel source, PortModel destination) throws DrawingException {
        if(source.canConnectToPort(destination) && source.canAcceptAdditionalConnection() && destination.canAcceptAdditionalConnection()){
            if(source.getType()==PortModel.INPUT_PORT && destination.getType()==PortModel.OUTPUT_PORT){
                // Connect
                DefaultConnectionModel connection = new DefaultConnectionModel((OutputPortModel)destination, (InputPortModel)source);
                source.addConnection(connection);
                destination.addConnection(connection);
                ConnectionModelPosition pos = new ConnectionModelPosition();
                layout.addLocationData(connection, pos);
                notifyBlockConnection(connection);

            } else if (source.getType()==PortModel.OUTPUT_PORT && destination.getType()==PortModel.INPUT_PORT){
                // Connect
                DefaultConnectionModel connection = new DefaultConnectionModel((OutputPortModel)source, (InputPortModel)destination);
                source.addConnection(connection);
                destination.addConnection(connection);
                ConnectionModelPosition pos = new ConnectionModelPosition();
                layout.addLocationData(connection, pos);
                notifyBlockConnection(connection);

            } else {
                // Incompatible port types
                throw new DrawingException("Can only link an Output port to an Input port");
            }
        } else {
            throw new DrawingException("Incompatible ports");
        }
    }

    /**
     * Get an Enumeration of blocks 
     */
    public Enumeration blocks() {
        return blocks.elements();
    }

    /** Get an Enumeration of objects */
    public Enumeration objects(){
        Vector objects = new Vector();
        Enumeration blockList = blocks();
        BlockModel block;
        Enumeration outputList;
        ConnectionModel connection;
        Enumeration connectionList;
        OutputPortModel port;
        
        while(blockList.hasMoreElements()){
            block = (BlockModel)blockList.nextElement();
            objects.addElement(block);
            
            outputList = block.outputs();
            while(outputList.hasMoreElements()){
                port = (OutputPortModel)outputList.nextElement();
                connectionList = port.connections();
                while(connectionList.hasMoreElements()){
                    objects.addElement(connectionList.nextElement());
                }
            }
        }
        return objects.elements();
    }
    
    /**
     * Unlink a port. This will unlink both ends of the link and destroy
     * the link itself 
     */
    public void disconnectPort(PortModel port) throws DrawingException {
        ConnectionModel connection;
        Enumeration connections = port.connections();
        while (connections.hasMoreElements()) {
            connection = (ConnectionModel)connections.nextElement();
            connection.getSourcePort().removeConnection(connection);
            connection.getDestinationPort().removeConnection(connection);
            layout.removeLocationData(connection);
            notifyConnectionRemoved(connection);
        }
    }

    /**
     * Add an object to the selected block list 
     */
    public void addObjectSelection(DrawingObject object) throws DrawingException {
        if(!selectedBlocks.contains(object)){
            selectedBlocks.add(object);
            notifyObjectSelected(object);
        }
    }

    /**
     * Remove a block from the selected block list 
     */
    public void removeObjectSelection(DrawingObject object) throws DrawingException {
        if(selectedBlocks.contains(object)){
            selectedBlocks.remove(object);
            notifyObjectUnSelected(object);
        }
    }

    /**
     * Add a block 
     */
    public void addBlock(BlockModel block) throws DrawingException {
        if(!blocks.contains(block)){
            blocks.add(block);
            block.setParentDrawing(this);
            notifyBlockAdded(block);
        }
    }

    /**
     * Remove a block 
     */
    public void removeBlock(BlockModel block) throws DrawingException {
        if(blocks.contains(block)){
            blocks.remove(block);
            notifyBlockRemoved(block);
        }
    }

    /**
     * Get the selected ojects
     */
    public Enumeration getSelectedObjects() {
        return selectedBlocks.elements();
    }   
    
    /**
     * Get the drawing layout
     */
    public DrawingLayout getDrawingLayout(){
        return layout;
    }
    
    /**
     * Is an object selected
     */
    public boolean isObjectSelected(DrawingObject object){
        return selectedBlocks.contains(object);
    }

    /**
     * Request a re-draw
     */
    public void redrawRequest(){
        notifyRedrawRequest();
    }
    
    /**
     * Request that a block be edited
     */
    public void editRequest(BlockModel block) {
        notifyEditRequest(block);
    }

    /** Request that the drawing be executed from a block */
    public void executeRequest(BlockModel block) throws DrawingException {
        notifyExecuteRequest(block);
    }
    
    /** Request propagation of a signal through the drawing from a source block */
    public void signalRequest(BlockModel block, DrawingSignal signal){
        notifySignalRequest(block, signal);
    }   
    
    /**
     * Get a block given its guid
     */
    public BlockModel getBlock(String blockGUID) {
        BlockModel block;
        Enumeration e = blocks.elements();
        while(e.hasMoreElements()){
            block = (BlockModel)e.nextElement();
            if(block.getBlockGUID().equals(blockGUID)){
                return block;
            }
        }
        return null;
    }

    /** Get a block by name if it exists */
    public BlockModel getBlockByName(String blockName) {
        BlockModel block;
        Enumeration e = blocks.elements();
        while(e.hasMoreElements()){
            block = (BlockModel)e.nextElement();
            if(block.getName().equals(blockName)){
                return block;
            }
        }
        return null;
    }

    /**
     * Return the number of selected object
     */
    public int getSelectedObjectCount() {
        return selectedBlocks.size();
    }
     
    /**
     * Delete a collection of drawing objects
     */
    public void deleteDrawingObjects(Vector objects) throws DrawingException {
        Enumeration e = objects.elements();
        DrawingObject obj;
        ConnectionModel connection;
        BlockModel block;
        PortModel port;
        
        while(e.hasMoreElements()){
            obj = (DrawingObject)e.nextElement();
            
            if(obj instanceof ConnectionModel){
                // Delete a connection
                connection = (ConnectionModel)obj;
                connection.getSourcePort().removeConnection(connection);
                connection.getDestinationPort().removeConnection(connection);
                notifyConnectionRemoved(connection);
                layout.removeLocationData(obj);
                
            } else if(obj instanceof BlockModel){
                // Delete a block
                block = (BlockModel)obj;
                
                // Close the editor if there is one
                if(block.getEditor()!=null){
                    block.getEditor().closeEditor();
                }
                
                // Delete all the input connections
                Enumeration ports = block.inputs();
                Enumeration connections;
                
                while(ports.hasMoreElements()){
                    port = (PortModel)ports.nextElement();
                    disconnectPort(port);
                }
                
                // Delete all the output connections
                ports = block.outputs();
                while(ports.hasMoreElements()){
                    port = (PortModel)ports.nextElement();
                    disconnectPort(port);
                }
                blocks.removeElement(block);
                layout.removeLocationData(block);
                notifyBlockRemoved(block);
            }
            
        }
        redrawRequest();
    }
    
    /** Delete everything from the drawing */
    public void clearDrawing() throws DrawingException {
        layout.clear();
        blocks.clear();
        layout.notifyLayoutChange();
        notifyRedrawRequest();
    }
    
    /**
     * Recreate from a store
     */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        blocks.clear();
        layout.clear();
        int blockCount = xmlDataStore.intValue("BlockCount", 0);
        
        BlockModel block;
        BlockModelPosition blockPos;

        // Auxiliary data
        if(xmlDataStore.containsName("AuxiliaryData")){
            auxiliaryData = xmlDataStore.xmlDataStoreValue("AuxiliaryData");
        } else {
            auxiliaryData = new XmlDataStore();
        }

        // Load all of the blocks and locations
        for(int i=0;i<blockCount;i++){
            // Get the block
            block = (BlockModel)xmlDataStore.xmlStorableValue("Block" + i);
            
            // Get the location data
            blockPos = (BlockModelPosition)xmlDataStore.xmlStorableValue("Block" + i + "Position");
            
            // Add to drawing
            blocks.add(block);
            block.setParentDrawing(this);
            layout.addLocationData(block, blockPos);
            
        }
        
        // Connect all of the blocks
        int linkCount = xmlDataStore.intValue("LinkCount", 0);
        String sourceID;
        String targetID;
        String sourceName;
        String targetName;
        BlockModel targetBlock;
        BlockModel sourceBlock;     
        OutputPortModel sourcePort;
        InputPortModel targetPort;
        ConnectionModelPosition connectionPos;
        DefaultConnectionModel connection;
        
        for(int i=0;i<linkCount;i++){
            sourceID = xmlDataStore.stringValue("Connection" + i + "SourceBlock", "");
            targetID = xmlDataStore.stringValue("Connection" + i + "TargetBlock", "");
            sourceName = xmlDataStore.stringValue("Connection" + i + "SourcePort", "");
            targetName = xmlDataStore.stringValue("Connection" + i + "TargetPort", "");
            
            sourceBlock = getBlock(sourceID);
            targetBlock = getBlock(targetID);
            if(sourceBlock!=null && targetBlock!=null){
                try {
                    sourcePort = sourceBlock.getOutput(sourceName);
                    targetPort = targetBlock.getInput(targetName);
                    
                    // Check compaitibility
                    //if(sourcePort.canConnectToPort(targetPort)){
                        connection = new DefaultConnectionModel(sourcePort, targetPort);
                        sourcePort.addConnection(connection);
                        targetPort.addConnection(connection);
                        
                        // Load position
                        connectionPos = (ConnectionModelPosition)xmlDataStore.xmlStorableValue("Connection" + i + "Position");
                        layout.addLocationData(connection, connectionPos);
                    //}
                    
                } catch (DrawingException de){
                    System.out.println("Load error: " + de);
                    // TODO: Internationalise
                    throw new XmlStorageException("Error loading document - " + de);
                }
            }
        }       
    }

    /**
     * Save to a store
     */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("Drawing");
        
        // Save the blocks and all of their output links
        int linkCount = 0;
        DefaultBlockModel block;
        BlockModel targetBlock;
        InputPortModel targetPort;
        Enumeration portEnum;
        OutputPortModel port;
        ConnectionModel connection;
        Enumeration connectionEnum;
        BlockModelPosition blockPos;
        ConnectionModelPosition connectionPos;

        // Save auxiliary data
        if(auxiliaryData!=null){
            store.add("AuxiliaryData", auxiliaryData);
        } else {
            auxiliaryData = new XmlDataStore();
            store.add("AuxiliaryData", auxiliaryData);
        }
        
        store.add("BlockCount", blocks.size());
        for(int i=0;i<blocks.size();i++){
            block = (DefaultBlockModel)blocks.elementAt(i);
            
            // Save the block
            store.add("Block" + i, block);
            
            // Save the layout data for the block
            try {
                blockPos = layout.getLocationData(block);
                store.add("Block" + i + "Position", blockPos);
            } catch (DrawingException e){
                // No location data
                store.add("Block" + i + "Position", new BlockModelPosition());
            }
            
            // Save all of the output links
            portEnum = block.outputs();
            while(portEnum.hasMoreElements()){
                port = (OutputPortModel)portEnum.nextElement();
                connectionEnum = port.connections();
                
                while(connectionEnum.hasMoreElements()){
                    connection = (ConnectionModel)connectionEnum.nextElement();
                    
                    // Save source info
                    store.add("Connection" + linkCount + "SourceBlock", block.getBlockGUID());
                    store.add("Connection" + linkCount + "SourcePort", port.getName());
                    
                    // Save target info
                    targetPort = connection.getDestinationPort();
                    targetBlock = targetPort.getParentBlock();
                    store.add("Connection" + linkCount + "TargetBlock", targetBlock.getBlockGUID());
                    store.add("Connection" + linkCount + "TargetPort", targetPort.getName());
                    
                    // Save the connection layout data
                    try {
                        connectionPos = layout.getLocationData(connection);
                        store.add("Connection" + linkCount + "Position", connectionPos);
                    } catch (DrawingException e){
                        store.add("Connection" + linkCount + "Position", new ConnectionModelPosition());
                    }
                    
                    linkCount++;
                }
                
            }
            store.add("LinkCount", linkCount);
        }
        
        return store;
    }
    
    /**
     * Get a count of the blocks
     */
    public int getBlockCount() {
        return blocks.size();
    }

    /**
     * Request execution from a block whilst within an existing execution thread. This is used by streaming and 
     * shouldn't be used by user blocks
     */
    public void inThreadExecuteRequest(BlockModel block) throws DrawingException {
        notifyInThreadExecuteRequest(block);
    }

    /** Get the auxiliary data store */
    public XmlDataStore getAuxiliaryData(){
        return auxiliaryData;
    }
}
