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
import com.connexience.server.workflow.engine.DataTypes;

import org.pipeline.core.drawing.*;
import org.pipeline.core.drawing.model.*;
import org.pipeline.core.drawing.customio.*;
import org.pipeline.core.drawing.layout.*;

import org.json.*;

/**
 * This class reads a drawing containing DataProcessorBlocks from a JSON object.
 * @author nhgh
 */
public class JSONDrawingImporter {
    /** JSON Object being loaded */
    private JSONObject drawingJson;


    public JSONDrawingImporter(JSONObject drawingJson) {
        this.drawingJson = drawingJson;
    }

    /** Load the drawing */
    public synchronized DrawingModel loadDrawing() throws DrawingException {
        DefaultDrawingModel drawing = new DefaultDrawingModel();
        //BlockModel block;

        try {
            // Create the blocks
            JSONObject blocksJson = drawingJson.getJSONObject("blocks");
            int blockCount = blocksJson.getInt("blockCount");
            JSONArray blockArray = blocksJson.getJSONArray("blockArray");
            for(int i=0;i<blockCount;i++){
                createBlock(blockArray.getJSONObject(i), drawing);
            }

            // Create the connections
            JSONObject connectionsJson = drawingJson.getJSONObject("connections");
            int connectionCount = connectionsJson.getInt("connectionCount");
            JSONArray connectionArray = connectionsJson.getJSONArray("connectionArray");
            for(int i=0;i<connectionCount;i++){
                connectBlocks(connectionArray.getJSONObject(i), drawing);
            }

            // Basic drawing properties
            

        } catch (JSONException jsone){
            throw new DrawingException("Error parsing drawing JSON data: " + jsone.getMessage());
        }

        return drawing;
    }

    /** Link two blocks together */
    private void connectBlocks(JSONObject connectionJson, DrawingModel drawing) throws DrawingException, JSONException {
        String sourceGuid = connectionJson.getString("sourceBlockGuid");
        String sourcePortName = connectionJson.getString("sourcePortName");
        String destinationGuid = connectionJson.getString("destinationBlockGuid");
        String destinationPortName = connectionJson.getString("destinationPortName");

        BlockModel sourceBlock = drawing.getBlock(sourceGuid);
        BlockModel targetBlock = drawing.getBlock(destinationGuid);

        OutputPortModel sourcePort = sourceBlock.getOutput(sourcePortName);
        InputPortModel targetPort = targetBlock.getInput(destinationPortName);
        DefaultConnectionModel connection = new DefaultConnectionModel(sourcePort, targetPort);
        sourcePort.addConnection(connection);
        targetPort.addConnection(connection);

        // Add the connection points
        ConnectionModelPosition pos = new ConnectionModelPosition();
        
        int pointCount = connectionJson.getInt("pointCount");
        JSONArray xArray = connectionJson.getJSONArray("xPoints");
        JSONArray yArray = connectionJson.getJSONArray("yPoints");
        for(int i=0;i<pointCount;i++){
            pos.addPoint(xArray.getInt(i), yArray.getInt(i));
        }
        
        pos.setDrawingObject(connection);
        drawing.getDrawingLayout().addLocationData(connection, pos);
    }

    /** Create a block from a JSON object */
    private BlockModel createBlock(JSONObject blockJson, DrawingModel drawing) throws DrawingException, JSONException {
        try {
            DataProcessorBlock block = new DataProcessorBlock();

            // Basic properties
            block.setBlockGUID(blockJson.getString("guid"));
            block.getEditableProperties().add("Caption", blockJson.getString("caption"));
            block.getEditableProperties().add("Name", blockJson.getString("name"));
            block.setServiceId(blockJson.getString("serviceId"));
            block.setUsesLatest(blockJson.getBoolean("latestVersion"));
            block.setVersionId(blockJson.getString("versionId"));
            block.setVersionNumber(blockJson.getInt("versionNumber"));
            block.setDynamicService(blockJson.getBoolean("dynamicService"));
            block.setDescription(blockJson.getString("description"));
            block.setIdempotent(Boolean.valueOf(blockJson.optString("idempotent")));
            block.setDeterministic(Boolean.valueOf(blockJson.optString("deterministic")));

            // Block inputs
            JSONObject inputsJson = blockJson.getJSONObject("inputs");
            JSONArray inputArray = inputsJson.getJSONArray("inputArray");
            JSONArray dataTypeArray;
            JSONObject inputJson;

            int portCount = inputsJson.getInt("inputCount");
            int dataTypeCount;
            String typeName;

            CustomInputDefinition input;
            DataType type;

            for(int i=0;i<portCount;i++){
                inputJson = inputArray.getJSONObject(i);
                input = (CustomInputDefinition)block.getInputDefinitions().createDefinition(inputJson.getString("name"));
                input.setLocation(inputJson.getInt("location"));
                input.setOffset(inputJson.getInt("offset"));
                input.setStreamable(inputJson.getBoolean("streamable"));
                
                // Set optional status
                if(inputJson.has("optional")){
                    input.setOptional(inputJson.getBoolean("optional"));
                } else {
                    input.setOptional(false);
                }
                
                // Load the supported data types
                dataTypeCount = inputJson.getInt("typeCount");
                dataTypeArray  = inputJson.getJSONArray("typeArray");

                for(int j=0;j<dataTypeCount;j++){
                    typeName = dataTypeArray.getString(j);
                    type = DataTypes.getDataType(typeName);
                    input.setDataType(type);
                    input.setDataTypeName(typeName);

                }
                
            }

            // Block outputs
            JSONObject outputsJson = blockJson.getJSONObject("outputs");
            JSONArray outputArray = outputsJson.getJSONArray("outputArray");
            portCount = outputsJson.getInt("outputCount");

            JSONObject outputJson;
            CustomPortDefinition output;

            for(int i=0;i<portCount;i++){
                outputJson = outputArray.getJSONObject(i);
                output = block.getOutputDefinitions().createDefinition(outputJson.getString("name"));
                output.setLocation(outputJson.getInt("location"));
                output.setOffset(outputJson.getInt("offset"));
                output.setStreamable(false);

                // Load the supported data types
                dataTypeCount = outputJson.getInt("typeCount");
                dataTypeArray = outputJson.getJSONArray("typeArray");

                for(int j=0;j<dataTypeCount;j++){
                    typeName = dataTypeArray.getString(j);
                    type = DataTypes.getDataType(typeName);
                    output.setDataType(type);
                    output.setDataTypeName(typeName);
                }

            }

            // Extra properties
            JSONObject propertiesJson = blockJson.getJSONObject("properties");
            new JSONPropertiesImporter(propertiesJson).mergeXmlDataStore(block.getEditableProperties());

            // Create the layout data
            BlockModelPosition pos = new BlockModelPosition();
            JSONObject blockPos = blockJson.getJSONObject("location");
            pos.setLeft(blockPos.getInt("left"));
            pos.setTop(blockPos.getInt("top"));
            pos.setWidth(blockPos.getInt("width"));
            pos.setHeight(blockPos.getInt("height"));

            // Add to the drawing
            drawing.addBlock(block);
            drawing.getDrawingLayout().addLocationData(block, pos);

            block.syncIOPorts();
            block.resetDataTypes();
            return block;
            
        } catch (DrawingException de){
            throw de;
        } catch (JSONException jsone){
            throw jsone;
        } catch (Exception e){
            throw new DrawingException("Error parsing drawing: " + e.getMessage());
        }
    }


}
