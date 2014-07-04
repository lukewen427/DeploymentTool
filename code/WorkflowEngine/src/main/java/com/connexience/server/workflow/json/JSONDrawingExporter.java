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
package com.connexience.server.workflow.json;

import com.connexience.server.workflow.blocks.processor.*;
import org.pipeline.core.drawing.*;
import org.pipeline.core.drawing.model.*;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.drawing.layout.*;

import org.json.*;
import java.util.*;

/**
 * This class writes a drawing containing DataProcessorBlocks to a JSON object
 * suitable for use in the JavaScript workflow editor.
 * @author nhgh
 */
public class JSONDrawingExporter {
    /** Drawing being exported */
    private DrawingModel drawing = null;

    /** Document ID for the exported drawing */
    private String documentId;

    /** Version ID for the exported drawing */
    private String versionId;

    /** Version number of the exported drawing */
    private int versionNumber;

    /** Document name for the exported drawing */
    private String name;

    /** Document description */
    private String description;

    /** Does the drawing support an external data connection */
    private boolean externalDataSupported = false;

    /** Name of the externally connected block */
    private String externalBlockName = "";

    /** Should the invocation be deleted if successful */
    private boolean deletedOnSuccess = false;
    
    /** Only upload output data for failed blocks */
    private boolean onlyFailedOutputsUploaded = false;
    
    /** Does this workflow operate in single VM mode */
    private boolean singleVmMode = true;
    
    /** Should this workflow be marked as an externally callable service */
    private boolean externalService = false;
    
    public JSONDrawingExporter(DrawingModel drawing) {
        this.drawing = drawing;

    }

    public JSONObject saveToJson() throws Exception {
        JSONObject json = new JSONObject();
        JSONObject blocksJson = new JSONObject();
        JSONObject connectionsJson = new JSONObject();

        Enumeration blocks = drawing.blocks();
        BlockModel block;
        Enumeration outputs;
        Enumeration connections;
        ConnectionModel connection;
        OutputPortModel output;


        DataProcessorBlock dpBlock;
        int count = 0;
        int connectionCount = 0;

        // Write the extra drawing properties
        if(drawing instanceof DefaultDrawingModel){
            XmlDataStore drawingProperties = ((DefaultDrawingModel)drawing).getAuxiliaryData();
            json.put("properties", new JSONPropertiesExporter(drawingProperties).createPropertiesJson());
        }

        // Drawing details
        json.put("documentId", documentId);
        json.put("versionId", versionId);
        json.put("versionNumber", versionNumber);
        json.put("name", name);
        json.put("description", description);
        json.put("externalDataSupported", externalDataSupported);
        json.put("externalBlockName", externalBlockName);
        json.put("deletedOnSuccess", deletedOnSuccess);
        json.put("onlyFailedOutputsUploaded", onlyFailedOutputsUploaded);
        json.put("singleVMMode", singleVmMode);
        json.put("externalService", externalService);
        
        // Write the blocks
        JSONArray blockArray = new JSONArray();
        JSONArray connectionArray = new JSONArray();

        while(blocks.hasMoreElements()){
            block = (BlockModel)blocks.nextElement();
            if(block instanceof DataProcessorBlock){
                dpBlock = (DataProcessorBlock)block;

                blockArray.put(createJsonDataProcessorBlockObject(dpBlock));
                count++;

                // Add the output connections to the connections table
                outputs = dpBlock.outputs();
                while(outputs.hasMoreElements()){
                    output = (OutputPortModel)outputs.nextElement();
                    connections = output.connections();
                    while(connections.hasMoreElements()){
                        connection  = (ConnectionModel)connections.nextElement();
                        connectionArray.put(createConnectionObject(connection));
                        connectionCount++;
                    }

                }
            }
        }
        
        blocksJson.put("blockCount", count);
        blocksJson.put("blockArray", blockArray);
        connectionsJson.put("connectionArray", connectionArray);
        connectionsJson.put("connectionCount", connectionCount);

        json.put("blocks", blocksJson);
        json.put("connections", connectionsJson);
        return json;
    }
    
    /** Create a JSON object for a connection */
    private JSONObject createConnectionObject(ConnectionModel connection) throws Exception {
        JSONObject json = new JSONObject();
        json.put("sourceBlockGuid", connection.getSourcePort().getParentBlock().getBlockGUID());
        json.put("sourcePortName", connection.getSourcePort().getName());
        json.put("destinationBlockGuid", connection.getDestinationPort().getParentBlock().getBlockGUID());
        json.put("destinationPortName", connection.getDestinationPort().getName());

        // Add location data
        ConnectionModelPosition pos = connection.getSourcePort().getParentBlock().getParentDrawing().getDrawingLayout().getLocationData(connection);
        if(pos!=null){
            JSONArray xJson = new JSONArray();
            JSONArray yJson = new JSONArray();
            for(int i=0;i<pos.getPointCount();i++){
                xJson.put(pos.getPoint(i).x);
                yJson.put(pos.getPoint(i).y);
            }
            json.put("pointCount", pos.getPointCount());
            json.put("xPoints", xJson);
            json.put("yPoints", yJson);
        }

        return json;

    }


    /** Create a JSON object for a block */
    private JSONObject createJsonDataProcessorBlockObject(DataProcessorBlock block) throws Exception {
        JSONObject json = new JSONObject();

        // Basic properties
        json.put("guid", block.getBlockGUID());
        json.put("caption", block.getCaption());
        json.put("label", block.getLabel());
        json.put("name", block.getName());
        json.put("serviceId", block.getServiceId());
        json.put("latestVersion", block.getUsesLatest());
        json.put("versionId", block.getVersionId());
        json.put("versionNumber", block.getVersionNumber());
        json.put("dynamicService", block.isDynamicService());
        json.put("description", block.getDescription());
        json.put("idempotent", block.isIdempotent());
        json.put("deterministic", block.isDeterministic());

        BlockModelPosition pos = block.getParentDrawing().getDrawingLayout().getLocationData(block);
        if(pos!=null){
            JSONObject locationJson = new JSONObject();
            locationJson.put("top", pos.getTop());
            locationJson.put("left", pos.getLeft());
            locationJson.put("width", pos.getWidth());
            locationJson.put("height", pos.getHeight());
            json.put("location", locationJson);
        }

        // Block editable properties
        XmlDataStore properties = block.getEditableProperties();
        StandardPropertiesCategoriser categoriser = new StandardPropertiesCategoriser();
        categoriser.replaceCategories(properties);
        json.put("properties", new JSONPropertiesExporter(properties).createPropertiesJson());
        
        // Block inputs
        json.put("inputs", createJsonInputs(block));

        // Block outputs
        json.put("outputs", createJsonOutputs(block));
        
        return json;

    }
    
    /** Create the input list for a block */
    private JSONObject createJsonInputs(BlockModel block) throws Exception {
        JSONObject json = new JSONObject();
        JSONObject inputJson;
        JSONArray inputArray = new JSONArray();
        JSONArray typeArray;

        Enumeration inputs = block.inputs();
        InputPortModel input;
        int count = 0;
        DataType dataType;
        Enumeration supportedTypes;
        int typeCount;

        while(inputs.hasMoreElements()){
            input = (InputPortModel)inputs.nextElement();
            inputJson = new JSONObject();
            inputJson.put("name", input.getName());
            inputJson.put("location", input.getPortLocation());
            inputJson.put("offset", input.getPortOffset());
            inputJson.put("streamable", input.streamingPort());
            inputJson.put("optional", input.isOptional());
            supportedTypes = input.supportedDataTypes();
            typeArray = new JSONArray();
            typeCount = 0;

            while(supportedTypes.hasMoreElements()){
                dataType = (DataType)supportedTypes.nextElement();
                typeArray.put(dataType.getName());
                typeCount++;
            }
            inputJson.put("typeCount", typeCount);
            inputJson.put("typeArray", typeArray);
            
            inputArray.put(inputJson);
            count++;

        }
        json.put("inputCount", count);
        json.put("inputArray", inputArray);
        return json;
    }

    /** Create the input list for a block */
    private JSONObject createJsonOutputs(BlockModel block) throws Exception {
        JSONObject json = new JSONObject();
        JSONObject outputJson;
        JSONArray outputArray = new JSONArray();
        JSONArray typeArray;

        Enumeration outputs = block.outputs();
        OutputPortModel output;
        int count = 0;
        DataType dataType;
        Enumeration supportedTypes;
        int typeCount;

        while(outputs.hasMoreElements()){
            output = (OutputPortModel)outputs.nextElement();
            outputJson = new JSONObject();
            outputJson.put("name", output.getName());
            outputJson.put("location", output.getPortLocation());
            outputJson.put("offset", output.getPortOffset());

            supportedTypes = output.supportedDataTypes();
            typeArray = new JSONArray();
            typeCount = 0;

            while(supportedTypes.hasMoreElements()){
                dataType = (DataType)supportedTypes.nextElement();
                typeArray.put(dataType.getName());
                typeCount++;
            }
            outputJson.put("typeCount", typeCount);
            outputJson.put("typeArray", typeArray);

            outputArray.put(outputJson);
            count++;

        }
        json.put("outputArray", outputArray);
        json.put("outputCount", count);
        return json;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public DrawingModel getDrawing() {
        return drawing;
    }

    public void setDrawing(DrawingModel drawing) {
        this.drawing = drawing;
    }

    public String getExternalBlockName() {
        return externalBlockName;
    }

    public void setExternalBlockName(String externalBlockName) {
        this.externalBlockName = externalBlockName;
    }

    public boolean isExternalDataSupported() {
        return externalDataSupported;
    }

    public void setExternalDataSupported(boolean externalDataSupported) {
        this.externalDataSupported = externalDataSupported;
    }

    public boolean isSingleVmMode() {
        return singleVmMode;
    }

    public void setSingleVmMode(boolean singleVmMode) {
        this.singleVmMode = singleVmMode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public void setDeletedOnSuccess(boolean deletedOnSuccess) {
        this.deletedOnSuccess = deletedOnSuccess;
    }

    public boolean isDeletedOnSuccess() {
        return deletedOnSuccess;
    }

    public void setOnlyFailedOutputsUploaded(boolean onlyFailedOutputsUploaded) {
        this.onlyFailedOutputsUploaded = onlyFailedOutputsUploaded;
    }

    public boolean isOnlyFailedOutputsUploaded() {
        return onlyFailedOutputsUploaded;
    }
    
    public boolean isExternalService(){
        return externalService;
    }

    public void setExternalService(boolean externalService) {
        this.externalService = externalService;
    }    
}
