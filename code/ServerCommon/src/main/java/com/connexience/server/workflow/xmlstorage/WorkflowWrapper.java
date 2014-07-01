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
package com.connexience.server.workflow.xmlstorage;

import org.pipeline.core.xmlstorage.*;
import java.io.*;

/**
 * This class wraps a WorkflowDocument and the associated workflow object
 * and any additional properties into an XmlStorable object.
 * @author nhgh
 */
public class WorkflowWrapper implements XmlStorable, Serializable {
	private static final long serialVersionUID = 1L;

	/** Old static workflow engine */
    public static final String STATIC_ENGINE = "static";

    /** Updated dynamic cloud engine */
    public static final String DYNAMIC_ENGINE ="dynamic";

    /** Workflow document data */
    private XmlDataStore drawingData = null;
    
    /** Document ID */
    private String id;
    
    /** Document Name */
    private String name;
    
    /** DocumentDescription */
    private String description;
    
    /** Parent ID */
    private String containerId;
    
    /** OrganisationID */
    private String organisationId;

    /** ID of the document creator */
    private String creatorId;
    
    /** Does this workflow support an external data source */
    private boolean externalDataSupported = false;
    
    /** Name of the block to send the external data to */
    private String externalDataBlockName = "";

    /** Is this workflow readonly. This is set when the workflow is loaded */
    private boolean readOnly = false;

    /** Does this wrapper contain a workflow image */
    private boolean imagePresent = false;

    /** Workflow image as a byte array */
    private byte[] workflowImageData = new byte[0];

    /** Workflow engine type to execute this workflow on */
    private String engineType = STATIC_ENGINE;

    /** Should the invocation be deleted if successful */
    private boolean deletedOnSuccess = false;
    
    /** Only upload output data for failed blocks */
    private boolean onlyFailedOutputsUploaded = false;
    
    /** Size of the current version */
    private long currentVersionSize = 0;
    
    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public boolean isImagePresent() {
        return imagePresent;
    }

    public byte[] getWorkflowImageData() {
        return workflowImageData;
    }

    public void setWorkflowImageData(byte[] workflowImageData) {
        this.workflowImageData = workflowImageData;
        if(workflowImageData.length>0){
            imagePresent = true;
        } else {
            imagePresent = false;
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }
    
    /** Get the creator id */
    public String getCreatorId(){
        return creatorId;
    }
    
    /** Set the creator id */
    public void setCreatorId(String creatorId){
        this.creatorId = creatorId;
    }
    
    /** Set the drawing data */
    public void setDrawingData(XmlDataStore drawingData){
        this.drawingData = drawingData;
    }
    
    /** Get the drawing data */
    public XmlDataStore getDrawingData(){
        return drawingData;
    }

    /** Does this wrapper contain a valid drawing */
    public boolean containsDrawingData(){
        if(drawingData!=null){
            return true;
        } else {
            return false;
        }
    }
    
    /** Get the the name of the block that has external data sent to it */
    public String getExternalDataBlockName() {
        return externalDataBlockName;
    }

    /** Set the the name of the block that has external data sent to it */
    public void setExternalDataBlockName(String externalDataBlockName) {
        this.externalDataBlockName = externalDataBlockName;
    }

    /** Does this workflow allow data to be sent to a block */
    public boolean isExternalDataSupported() {
        return externalDataSupported;
    }

    /** Set whether this workflow allows data to be sent to a block */
    public void setExternalDataSupported(boolean externalDataSupported) {
        this.externalDataSupported = externalDataSupported;
    }

    public boolean isReadOnly(){
        return readOnly;
    }

    public void setReadOnly(boolean readOnly){
        this.readOnly = readOnly;
    }

    /** Get the workflow engine type */
    public String getEngineType(){
        return engineType;
    }

    /** Set the workflow engine type */
    public void setEngineType(String engineType){
        this.engineType = engineType;
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

    public long getCurrentVersionSize() {
        return currentVersionSize;
    }

    public void setCurrentVersionSize(long currentVersionSize) {
        this.currentVersionSize = currentVersionSize;
    }
   
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("WorkflowWrapper");
        store.add("ID", id);
        store.add("Name", name);
        store.add("Description", description);
        store.add("ContainerID", containerId);
        store.add("OrganisationID", organisationId);
        store.add("CreatorID", creatorId);
        store.add("ExternalDataBlockName", externalDataBlockName);
        store.add("ExternalDataSupported", externalDataSupported);
        store.add("ReadOnly", readOnly);
        store.add("EngineType", engineType);
        store.add("DeletedOnSuccess", deletedOnSuccess);
        store.add("OnlyFailedOutputsUploaded", onlyFailedOutputsUploaded);
        store.add("CurrentVersionSize", currentVersionSize);
        if(workflowImageData!=null && workflowImageData.length>0){
            store.add("ImagePresent", true);
            store.add("WorkflowImageData", workflowImageData);
        } else {
            store.add("ImagePresent", false);
            store.add("WorkflowImageData", new byte[0]);
        }

        if(drawingData!=null){
            store.add("DrawingData", drawingData);
        }
        return store;
        
    }

    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        id = store.stringValue("ID", null);
        name = store.stringValue("Name", "");
        description = store.stringValue("Description", "");
        containerId = store.stringValue("ContainerID", null);
        organisationId = store.stringValue("OrganisationID", null);
        creatorId = store.stringValue("CreatorID", null);
        externalDataBlockName = store.stringValue("ExternalDataBlockName", "");
        externalDataSupported = store.booleanValue("ExternalDataSupported", false);
        readOnly = store.booleanValue("ReadOnly", false);
        engineType = store.stringValue("EngineType", STATIC_ENGINE);
        deletedOnSuccess = store.booleanValue("DeletedOnSuccess", false);
        onlyFailedOutputsUploaded = store.booleanValue("OnlyFailedOutputsUploaded", false);
        currentVersionSize = store.longValue("CurrentVersionSize", 0);
        if(store.containsName("DrawingData")){
            drawingData = store.xmlDataStoreValue("DrawingData");
        }

        if(store.booleanValue("ImagePresent", false)){
            workflowImageData = store.byteArrayValue("WorkflowImageData");
            imagePresent = true;
        } else {
            workflowImageData = new byte[0];
            imagePresent = false;
        }
    }
}