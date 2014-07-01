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
package com.connexience.server.model.workflow;

import com.connexience.server.model.document.DocumentRecord;

/**
 * This class extends DocumentRecord to provide a specific workflow document.
 * @author nhgh
 */
public class WorkflowDocument extends DocumentRecord {
    /**
     * Class version UID.
     * 
     * Please increment this value whenever your changes may cause 
     * incompatibility with the previous version of this class. If unsure, ask 
     * one of the core development team or read:
     *   http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     *   http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 2L;

    /** Old static workflow engine */
    public static final String STATIC_ENGINE = "static";

    /** Updated dynamic cloud engine */
    public static final String DYNAMIC_ENGINE ="dynamic";

    /** Should invocations store intermediate data */
    private boolean intermedateDataStored = false;

    /** Does this workflow support an external data source */
    private boolean externalDataSupported = false;
    
    /** Name of the block to send the external data to */
    private String externalDataBlockName = "";

    /** Workflow engine type to execute this workflow on */
    private String engineType = DYNAMIC_ENGINE;

    /** Should the invocation be deleted if successful */
    private boolean deletedOnSuccess = false;
    
    /** Only upload output data for failed blocks */
    private boolean onlyFailedOutputsUploaded = true;
    
    /** Does this workflow operate in single VM mode */
    private boolean singleVmMode = true;
    
    /** Should this workflow be marked as an externally callable service */
    private boolean externalService = false;
    
    public WorkflowDocument() {
        super();
    }

    /** Get a copy of this workflow document */
    public void populateCopy(WorkflowDocument doc){
        super.populateCopy(doc);
        doc.setDeletedOnSuccess(deletedOnSuccess);
        doc.setEngineType(engineType);
        doc.setExternalDataBlockName(externalDataBlockName);
        doc.setExternalDataSupported(externalDataSupported);
        doc.setIntermedateDataStored(intermedateDataStored);
        doc.setOnlyFailedOutputsUploaded(onlyFailedOutputsUploaded);
    }
    
    /** Is this workflow marked as an external service */
    public boolean isExternalService(){
        return externalService;
    }

    /** Set whether this workflow is marked as an external service */
    public void setExternalService(boolean externalService) {
        this.externalService = externalService;
    }
    
    public boolean isSingleVmMode(){
        return singleVmMode;
    }

    /** Set single VM mode status */
    public void setSingleVmMode(boolean singleVmMode){
        this.singleVmMode = singleVmMode;
    }

    /** Get the workflow engine type */
    public String getEngineType(){
        return engineType;
    }

    /** Set the workflow engine type */
    public void setEngineType(String engineType){
        this.engineType = engineType;
    }
    
    /** Are the inter-block results stored in the database */
    public boolean isIntermedateDataStored() {
        return intermedateDataStored;
    }

    /** Set whether inter-block results are stored in the database */
    public void setIntermedateDataStored(boolean intermedateDataStored) {
        this.intermedateDataStored = intermedateDataStored;
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

    /** Set whether or not only output data for failed blocks is uploaded */
    public void setOnlyFailedOutputsUploaded(boolean onlyFailedOutputsUploaded) {
        this.onlyFailedOutputsUploaded = onlyFailedOutputsUploaded;
    }

    /** Are only failed block outputs uploaded */
    public boolean isOnlyFailedOutputsUploaded() {
        return onlyFailedOutputsUploaded;
    }

    /** Should the invocation folder be automatically deleted if the workflow succeeds */
    public void setDeletedOnSuccess(boolean deletedOnSuccess) {
        this.deletedOnSuccess = deletedOnSuccess;
    }

    /** Is the workflow directory deleted if the workflow succeeds with no errors */
    public boolean isDeletedOnSuccess() {
        return deletedOnSuccess;
    }
}