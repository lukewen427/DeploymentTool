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

import com.connexience.server.ejb.util.*;
import com.connexience.server.workflow.service.*;
import com.connexience.server.model.security.*;
import com.connexience.server.model.document.*;

import com.connexience.server.workflow.blocks.processor.DataProcessorBlock;
import org.pipeline.core.drawing.*;
import org.pipeline.core.xmlstorage.*;

import org.json.*;
import java.util.*;


/**
 * This class creates a JSON representation of a block from a DataProcessorServiceDefinition
 * xml string. It is used on the server side when a new block is dragged onto a workflow
 * that needs to be configured.
 * @author nhgh
 */
public class JSONBlockCreator {
    /** ID of the service being queried */
    private String documentId = null;

    /** Version of the service to get */
    private String versionId = null;

    /** Security ticket for getting service details */
    private Ticket ticket = null;

    public JSONBlockCreator(Ticket ticket, String documentId, String versionId) {
        this.ticket = ticket;
        this.documentId = documentId;
        this.versionId = versionId;
    }

    /** Create a service JSON object that can be used to generate a block */
    public JSONObject createBlockJson() throws Exception {
        String serviceXml;
        int versionNumber = 0;

        if(versionId!=null){
            serviceXml = WorkflowEJBLocator.lookupWorkflowManagementBean().getDynamicWorkflowServiceXML(ticket, documentId, versionId);
            DocumentVersion version = EJBLocator.lookupStorageBean().getLatestVersion(ticket, documentId);
            versionNumber = version.getVersionNumber();
        } else {
            serviceXml = WorkflowEJBLocator.lookupWorkflowManagementBean().getDynamicWorkflowServiceXML(ticket, documentId);
        }

        DataProcessorServiceDefinition def = new DataProcessorServiceDefinition();
        def.loadXmlString(serviceXml);

        JSONObject blockJson = new JSONObject();

        // Basic properties
        blockJson.put("guid", new RandomGUID().toString());
        blockJson.put("caption", "");
        blockJson.put("label", def.getName());
        blockJson.put("name", "");
        blockJson.put("serviceId", documentId);
        blockJson.put("dynamicService", true);
        blockJson.put("versionNumber", versionNumber);
        blockJson.put("description", def.getDescription());
        if(versionId!=null){
            blockJson.put("latestVersion", false);
            blockJson.put("versionId", versionId);

        } else {
            blockJson.put("latestVersion", true);
        }

        // Dummy location data
        JSONObject locationJson = new JSONObject();
        locationJson.put("top", 10);
        locationJson.put("left", 10);
        locationJson.put("width", 60);
        locationJson.put("height", 60);
        blockJson.put("location", locationJson);
        blockJson.put("idempotent", Boolean.toString(def.isIdempotent()));
        blockJson.put("deterministic", Boolean.toString(def.isDeterministic()));

        // Inputs
        Vector<DataProcessorIODefinition> inputs = def.getInputs();
        JSONObject inputsJson = new JSONObject();
        JSONArray inputArray = new JSONArray();
        JSONArray typeArray;
        JSONObject inputJson;
        DataProcessorIODefinition input;

        for(int i=0;i<inputs.size();i++){
            input = inputs.get(i);
            inputJson = new JSONObject();
            inputJson.put("name", input.getName());
            inputJson.put("location", PortModel.LEFT_OF_BLOCK);
            inputJson.put("offset", calculateOffset(inputs.size(), i));
            inputJson.put("optional", input.isOptional());
            if(input.getMode().equals(DataProcessorIODefinition.STREAMING_CONNECTION)){
                inputJson.put("streamable", true);
            } else {
                inputJson.put("streamable", false);
            }

            typeArray = new JSONArray();
            typeArray.put(input.getDataTypeName());
            inputJson.put("typeCount", 1);
            inputJson.put("typeArray", typeArray);
            inputArray.put(inputJson);
        }
        inputsJson.put("inputCount", def.getInputs().size());
        inputsJson.put("inputArray", inputArray);
        blockJson.put("inputs", inputsJson);

        // Outputs
        Vector<DataProcessorIODefinition> outputs = def.getOutputs();
        JSONObject outputsJson = new JSONObject();
        JSONArray outputArray = new JSONArray();
        JSONObject outputJson;
        DataProcessorIODefinition output;

        for(int i=0;i<outputs.size();i++){
            output = outputs.get(i);
            outputJson = new JSONObject();
            outputJson.put("name", output.getName());
            outputJson.put("location", PortModel.RIGHT_OF_BLOCK);
            outputJson.put("offset", calculateOffset(outputs.size(), i));
            outputJson.put("streamable", false);
            typeArray = new JSONArray();
            typeArray.put(output.getDataTypeName());
            outputJson.put("typeCount", 1);
            outputJson.put("typeArray", typeArray);
            outputArray.put(outputJson);
        }
        outputsJson.put("outputCount", def.getOutputs().size());
        outputsJson.put("outputArray", outputArray);
        blockJson.put("outputs", outputsJson);
        
        // Properties
        XmlDataStore properties = def.getServiceProperties();
        
        // Make sure that the properties declared in service.xml get assigned to the block
        StandardPropertiesCategoriser replacer = new StandardPropertiesCategoriser();
        replacer.addReplacementForAllProperties(properties, "Block");

        // Create a block to get the standard properties
        DataProcessorBlock dummyBlock = new DataProcessorBlock();
        properties.copyProperties(dummyBlock.getEditableProperties());
        
        replacer.replaceCategories(properties);
        blockJson.put("properties", new JSONPropertiesExporter(properties).createPropertiesJson());

        return blockJson;
    }

    /** Reposition a set of inputs or outputs */
    private int calculateOffset(int portCount, int portIndex){
        switch(portCount){
            case 0:
                return 50;

            case 1:
                return 50;

            case 2:
                if(portIndex==0){
                    return 30;
                } else {
                    return 70;
                }

            case 3:
                if(portIndex==0){
                    return 20;
                } else if(portIndex==1){
                    return 50;
                } else {
                    return 80;
                }

            case 4:
                if(portIndex==0){
                    return 20;
                } else if(portIndex==1){
                    return 40;
                } else if(portIndex==2){
                    return 60;
                } else {
                    return 80;
                }

            case 5:
                if(portIndex==0){
                    return 10;
                } else if(portIndex==1){
                    return 30;
                } else if(portIndex==2){
                    return 50;
                } else if(portIndex==3){
                    return 70;
                } else {
                    return 90;
                }

            default:
                int step = 80 / 60;
                return 10 + (portIndex * step);
        }
    }
}