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

package org.pipeline.core.drawing.customio;

import org.pipeline.core.drawing.DrawingException;
import org.pipeline.core.drawing.InputPortModel;
import org.pipeline.core.drawing.OutputPortModel;
import org.pipeline.core.drawing.PortModel;
import org.pipeline.core.drawing.model.DefaultBlockModel;
import org.pipeline.core.drawing.model.DefaultPortModel;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This class extends the DefaultBlockModel to provide a block that can
 * have its inputs and outputs customised
 * @author hugo
 */
public abstract class CustomisableDefaultBlockModel extends DefaultBlockModel {
    /** Input definitions */
    private CustomPortDefinitionList inputList = new CustomPortDefinitionList(CustomInputDefinition.class, null, this);
    
    /** Output definitions */
    private CustomPortDefinitionList outputList = new CustomPortDefinitionList(CustomOutputDefinition.class, null, this);
    
    /** Creates a new instance of CustomisableDefaultBlockModel */
    public CustomisableDefaultBlockModel() throws DrawingException {
        super();
    }
    
    /** Get the input definition list */
    public CustomPortDefinitionList getInputDefinitions(){
        return inputList;
    }
    
    /** Get the output definition list */
    public CustomPortDefinitionList getOutputDefinitions(){
        return outputList;
    }
    
    /** Remove an output port */
    public void removeOutputPort(String portName) {
        Hashtable outputs = getOutputs();
        if(outputs.containsKey(portName)){ 
            OutputPortModel port = (OutputPortModel)outputs.get(portName); 
            port.removeAllConnections();
            getOutputs().remove(port.getName());
            getParentDrawing().redrawRequest();
        }
    }
    
    /** Remove an input port */
    public void removeInputPort(String portName){
        Hashtable inputs = getInputs();
        if(inputs.containsKey(portName)){ 
            InputPortModel port = (InputPortModel)inputs.get(portName); 
            port.removeAllConnections();
            getInputs().remove(port.getName());
            getParentDrawing().redrawRequest();
        }
    }
    
    /** Synchronise the inputs and outputs with the definitions */
    public void syncIOPorts(){
        createInputs();
        createOutputs();
        if(getParentDrawing()!=null){
            getParentDrawing().redrawRequest();
        }
    }
    
    /** Create the outputs */
    private void createOutputs(){
        // Delete ports that are not in the definition list
        Enumeration e = outputs();
        PortModel port;
        while(e.hasMoreElements()){
            port = (PortModel)e.nextElement();
            if(!outputList.namedDefinitionPresent(port.getName())){
                removeOutputPort(port.getName());
            }
        }        
        
        // Create the inputs
        int count = outputList.getDefinitionCount();

        CustomOutputDefinition outDef;
        for(int i=0;i<count;i++){
            outDef = (CustomOutputDefinition)outputList.getDefinition(i);
            if(!isOutput(outDef.getName())){
                // Need to create
                try {
                    this.addOutputPort((OutputPortModel)outDef.createPort(this));
                } catch (Exception ex){
                    ex.printStackTrace();
                    // TODO: Log somewhere better
                }
            } else {
                // Might need to update port position
                try {
                    ((DefaultPortModel)getOutput(outDef.getName())).setLocation(outDef.getLocation());
                    ((DefaultPortModel)getOutput(outDef.getName())).setOffset(outDef.getOffset());
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }        
    }
    
    /** Create the inputs */
    private void createInputs(){
        // Delete ports that are not in the definition list
        Enumeration e = inputs();
        PortModel port;
        while(e.hasMoreElements()){
            port = (PortModel)e.nextElement();
            if(!inputList.namedDefinitionPresent(port.getName())){
                removeInputPort(port.getName());
            }
        }
        
        // Create the inputs
        int count = inputList.getDefinitionCount();
        CustomInputDefinition inDef;
        for(int i=0;i<count;i++){
            inDef = (CustomInputDefinition)inputList.getDefinition(i);
            if(!isInput(inDef.getName())){
                // Need to create
                try {
                    this.addInputPort((InputPortModel)inDef.createPort(this));
                } catch (Exception ex){
                    ex.printStackTrace();
                    // TODO: Log somewhere better
                }
            } else {
                // Might need to update port position
                try {
                    ((DefaultPortModel)getInput(inDef.getName())).setLocation(inDef.getLocation());
                    ((DefaultPortModel)getInput(inDef.getName())).setOffset(inDef.getOffset());
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    
    /** Recreate from an XmlData store */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        super.recreateObject(xmlDataStore);
        inputList.recreateObject(xmlDataStore.xmlDataStoreValue("InputDefinitions"));
        outputList.recreateObject(xmlDataStore.xmlDataStoreValue("OutputDefinitions"));   
        syncIOPorts();
    }

    /** Save to an XmlData store */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("InputDefinitions", inputList.storeObject());
        store.add("OutputDefinitions", outputList.storeObject());        
        return store;
    }    
}
