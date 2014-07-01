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
import org.pipeline.core.drawing.gui.DefaultBlockRenderer;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.security.XmlDataStoreSignatureHelper;
import org.pipeline.core.xmlstorage.security.XmlDataStoreSignatureListener;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
/**
 * This is the default implementation of a drawing block. This 
 * class cannot be instantiated itself and must be subclassed
 * by user blocks.
 * @author  hugo
 */
public abstract class DefaultBlockModel implements Serializable, BlockModel, XmlStorable, XmlSignable, XmlDataStoreSignatureListener {
    /** Block inputs */
    private Hashtable inputs = new Hashtable();
    
    /** Block outputs */
    private Hashtable outputs = new Hashtable();
    
    /** Does this block support input streaming */
    private boolean supportsInputStreaming = false;
    
    /** Does this block support output streaming */
    private boolean supportsOutputStreaming = true;
    
    /** Block editor class */
    private Class editorClass = null;
    
    /** Block renderer class */
    private Class rendererClass = null;
    
    /** Class editing the block if there is one */
    private BlockModelEditor editor = null;
    
    /** Block unique identifier */
    private String guid;
        
    /** Signature information */
    private XmlDataStoreSignatureHelper signatureData;
    
    /** Parent drawing */
    private DrawingModel parentDrawing;
    
    /** Editable properties for this block */
    private XmlDataStore editableProperties;

    /** Creates a new instance of DefaultBlockModel */
    public DefaultBlockModel() throws DrawingException {
        guid = new RandomGUID().toString();
        signatureData = new XmlDataStoreSignatureHelper(this);
        signatureData.setAlwaysRecalculate(false);
        signatureData.addXmlDataStoreSignatureListener(this);
        rendererClass = DefaultBlockRenderer.class;
        editableProperties = new XmlDataStore();
        editableProperties.add("Name", "", "Name of the block");
        editableProperties.add("Label", "", "Text for block title bar");
        editableProperties.add("Caption", "", "Text to be displayed beneath block");
    }

    /** Get the editable properties for this block */
    public XmlDataStore getEditableProperties(){
        return editableProperties;
    }
    
    /** Editable properties have been changed */
    public void editablePropertyChanged(XmlDataObject property){
        signatureData.setSignatureDirty();
        try {
            parentDrawing.executeRequest(this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /** Get the block caption */
    public String getCaption(){
        return editableProperties.stringValue("Caption", "");
    }
    
    /** Get the block name */
    public String getName(){
        return editableProperties.stringValue("Name", "");
    }
    
    /** Get the block label */
    public String getLabel(){
        return editableProperties.stringValue("Label", "");
    }

    /** Set the parent drawing */
    public void setParentDrawing(DrawingModel parentDrawing){
        this.parentDrawing = parentDrawing;
    }
    
    /** Get the parent drawing */
    public DrawingModel getParentDrawing(){
        return parentDrawing;
    }
    
    /** Get the inputs Hashtable */
    public Hashtable getInputs(){
        return inputs;
    }
    
    /** Get the outputs Hashtable */
    public Hashtable getOutputs(){
        return outputs;
    }
    
    /** Override toString() method to return label */
    public String toString(){
        return getLabel();
    }
    
    /** Set the label */
    public void setLabel(String label){
        try {
            editableProperties.get("Label").setValue(label);
        } catch (Exception e){}
    }
    
    /** Get the signature data */
    public XmlDataStoreSignatureHelper getSignatureData() {
        return signatureData;
    }
    
    /** Add a port. The actual type determines whether addInputPort or
     * addOutputPort gets called */
    public void addPort(PortModel port) throws DrawingException {
        if(port instanceof InputPortModel){
            addInputPort((InputPortModel)port);
        } else {
            addOutputPort((OutputPortModel)port);
        }
    }
    
    /** Add an input port */
    public void addInputPort(InputPortModel port) throws DrawingException {
        if(!inputs.containsKey(port.getName())){
            inputs.put(port.getName(), port);
        } else {
            throw new DrawingException("Port: " + port.getName() + " already exists");
        }
    }
    
    /** Add an output port */
    public void addOutputPort(OutputPortModel port) throws DrawingException {
        if(!outputs.containsKey(port.getName())){
            outputs.put(port.getName(), port);
        } else {
            throw new DrawingException("Port: " + port.getName() + "already exists");
        }
    }
        
    /** Is a port an input port */
    public boolean isInput(String portName){
        return inputs.containsKey(portName);
    }
    
    /** Is a port an output port */
    public boolean isOutput(String portName){
        return outputs.containsKey(portName);
    }
    
    /** Set the editor class */
    public void setEditorClass(Class editorClass){
        this.editorClass = editorClass;
    }
    
    /** Set the renderer class */
    public void setRendererClass(Class rendererClass){
        this.rendererClass = rendererClass;
    }
    
    /** Get the block editor class if there is one */
    public Class getEditorClass() {
        return editorClass;
    }

    /** Get the block renderer class if there is one */
    public Class getRendererClass() {
        return rendererClass;
    }
    
    /** Request execution of this block */
    public void requestExecution() throws DrawingException {
        if(parentDrawing!=null){
            parentDrawing.executeRequest(this);
        }
    }
    
    // =========================================================================
    // BlockModel implementation
    // =========================================================================
    
    /**
     * Does this block support input streaming 
     */
    public boolean supportsInputStreaming() {
        return supportsInputStreaming;
    }

    /**
     * Get a named output 
     */
    public OutputPortModel getOutput(String outputName) throws DrawingException {
        if(outputs.containsKey(outputName)){
            return (OutputPortModel)outputs.get(outputName);
        } else {
            throw new DrawingException("Output port: " + outputName + " does not exist");
        }
    }

    /**
     * Get a named input 
     */
    public InputPortModel getInput(String inputName) throws DrawingException {
        if(inputs.containsKey(inputName)){
            return (InputPortModel)inputs.get(inputName);
        } else {
            throw new DrawingException("Input port: " + inputName + " does not exist");
        }
    }

    /**
     * Get input count 
     */
    public int getInputCount() {
        return inputs.size();
    }

    /**
     * Get an Enumeration of outputs 
     */
    public Enumeration outputs() {
        return outputs.elements();
    }

    /**
     * Get an Enumeration of inputs 
     */
    public Enumeration inputs() {
        return inputs.elements();
    }

    /**
     * Does this block support output streaming 
     */
    public boolean supportsOutputStreaming() {
        return supportsOutputStreaming;
    }

    /**
     * Get output count 
     */
    public int getOutputCount() {
        return outputs.size();
    }    

    /** Create a new editor object */
    public BlockModelEditor createEditor(){
        if(editorClass!=null){
            if(editor==null){
                try {
                    Object obj = editorClass.newInstance();
                    if(obj instanceof BlockModelEditor){
                        editor = (BlockModelEditor)obj;
                        editor.setBlock(this);
                        editor.createComponents();
                        return editor;

                    } else {
                        System.out.println("Wrong editor class");
                        return null;
                    }

                } catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
                
            } else {
                // Return the existing editor
                return editor;
                
            }
        } else {
            return null;
        }
    }
    
    /** Create a new renderer for this block */
    public BlockModelRenderer createRenderer(){
        if(rendererClass!=null){
            try {
                Object obj = rendererClass.newInstance();
                if(obj instanceof BlockModelRenderer){
                    BlockModelRenderer renderer = (BlockModelRenderer)obj;
                    renderer.setBlock(this);
                    return renderer;
                } else {
                    System.out.println("Wrong renderer class");
                    return null;
                }
                
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
            
        } else {
            return null;
        }
    }
    
    /** Get the editor object */
    public BlockModelEditor getEditor(){
        return editor;
    }
    
    /** Release and close the editor */
    public void releaseEditor(){
        if(editor!=null){
            editor.finishEdit();
            editor = null;
        }
        signatureData.setSignatureDirty();
    }    

    /** Get the blocks unique string identifier */
    public String getBlockGUID() {
        return guid;
    }

    /** Set the unique identifier for this block */
    public void setBlockGUID(String guid){
        this.guid = guid;
    }
    
    /** Can this block be edited */
    public boolean isEditable() {
        if(editorClass!=null){
            return true;
        } else {
            return false;
        }
    }
    
    /** Process a DrawingSignal */
    public void processSignal(DrawingSignal signal){    
    }   
    
    /** Recreate from an XmlData store */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        guid = xmlDataStore.stringValue("GUID", guid);
        if(xmlDataStore.containsName("EditableProperties")){
            XmlDataStore props  = xmlDataStore.xmlDataStoreValue("EditableProperties");
            editableProperties.copyProperties(props);
        }
    }

    /** Save to an XmlData store */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("Block");
        store.add("GUID", guid);
        store.add("EditableProperties", editableProperties);
        return store;
    }

    /** Execute this block. In this class, there is no implementation, so an error message is returned */
    public BlockExecutionReport execute() throws BlockExecutionException {
        return new BlockExecutionReport(this, BlockExecutionReport.INTERNAL_ERROR, "No implementation");
    }
    
    /** Signature state has changed */
    public void signatureStateChanged(XmlDataStoreSignatureHelper xmlDataStoreSignatureHelper) {
        getParentDrawing().redrawRequest();
    }
}
