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
package com.connexience.server.api.external.helpers;

import com.connexience.api.model.*;
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.datasets.Dataset;
import com.connexience.server.model.datasets.DatasetConstants;
import com.connexience.server.model.datasets.DatasetItem;
import com.connexience.server.model.datasets.items.MultipleValueItem;
import com.connexience.server.model.datasets.items.SingleValueItem;
import com.connexience.server.model.datasets.items.multiple.JsonMultipleValueItem;
import com.connexience.server.model.datasets.items.single.SingleJsonRowItem;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.metadata.MetadataItem;
import com.connexience.server.model.metadata.types.BooleanMetadata;
import com.connexience.server.model.metadata.types.DateMetadata;
import com.connexience.server.model.metadata.types.NumericalMetadata;
import com.connexience.server.model.metadata.types.TextMetadata;
import com.connexience.server.model.project.Project;
import com.connexience.server.model.security.User;
import com.connexience.server.model.workflow.WorkflowDocument;
import com.connexience.server.model.workflow.WorkflowInvocationFolder;
import com.connexience.server.model.workflow.WorkflowParameter;
import com.connexience.server.model.workflow.WorkflowParameterList;
/**
 * This class creates EscXX objects from the standard database model objects
 * @author hugo
 */
public class EscObjectFactory {
    private static void populateEscObject(EscObject obj, ServerObject serverObj){
        obj.setContainerId(serverObj.getContainerId());
        obj.setCreatorId(serverObj.getCreatorId());
        obj.setDescription(serverObj.getDescription());
        obj.setId(serverObj.getId());
        obj.setName(serverObj.getName());
        obj.setProjectId(serverObj.getProjectId());
        obj.setCreationTime(serverObj.getTimeInMillis());
    }
    
    public static EscDocument createEscDocument(DocumentRecord doc){
        EscDocument escDoc = new EscDocument();
        populateEscObject(escDoc, doc);
        escDoc.setCurrentVersionSize(doc.getCurrentVersionSize());
        escDoc.setCurrentVersionNumber(doc.getCurrentVersionNumber());
        escDoc.setDownloadPath("/data/" + doc.getId() + "/latest");
        escDoc.setUploadPath("/data/" + doc.getId());
        return escDoc;
    }
    
    public static EscDocumentVersion createEscDocumentVersion(DocumentVersion v){
        EscDocumentVersion escVersion = new EscDocumentVersion();
        escVersion.setId(v.getId());
        escVersion.setDocumentRecordId(v.getDocumentRecordId());
        escVersion.setComments(v.getComments());
        escVersion.setUserId(v.getUserId());
        escVersion.setVersionNumber(v.getVersionNumber());
        escVersion.setSize(v.getSize());
        escVersion.setTimestamp(v.getTimestamp());
        escVersion.setDownloadPath("/data/" + v.getDocumentRecordId()+ "/" + v.getId());
        escVersion.setMd5(v.getMd5());
        return escVersion;
    }
    
    public static EscFolder createEscFolder(Folder f){
        EscFolder escFolder = new EscFolder();
        populateEscObject(escFolder, f);
        return escFolder;
    }
    
    public static EscProject createEscProject(Project p){
        EscProject escProject = new EscProject();
        escProject.setId(Long.toString(p.getId()));
        escProject.setName(p.getName());
        escProject.setDescription(p.getDescription());
        escProject.setDataFolderId(p.getDataFolderId());
        escProject.setWorkflowFolderId(p.getWorkflowFolderId());
        escProject.setCreatorId(p.getOwnerId());
        return escProject;
    }
    
    public static EscUser createEscUser(User u){
        EscUser escUser = new EscUser();
        escUser.setId(u.getId());
        escUser.setName(u.getName());
        escUser.setFirstName(u.getFirstName());
        escUser.setSurname(u.getSurname());
        return escUser;
    }
    
    public static EscWorkflow createEscWorkflow(WorkflowDocument wf){
        EscWorkflow escWorkflow = new EscWorkflow();
        populateEscObject(escWorkflow, wf);
        escWorkflow.setCurrentVersionNumber(wf.getCurrentVersionNumber());
        escWorkflow.setCurrentVersionSize(wf.getCurrentVersionSize());
        return escWorkflow;
    }
    
    public static EscWorkflowInvocation createEscWorkflowInvocation(WorkflowInvocationFolder f, String workflowName){
        EscWorkflowInvocation escWorkflowInvocation = new EscWorkflowInvocation();
        populateEscObject(escWorkflowInvocation, f);
        escWorkflowInvocation.setWorkflowName(workflowName);
        escWorkflowInvocation.setWorkflowId(f.getWorkflowId());
        escWorkflowInvocation.setWorkflowVersionId(f.getVersionId());
        escWorkflowInvocation.setPercentComplete(f.getPercentComplete());
        if(f.getExecutionStartTime()!=null){
            escWorkflowInvocation.setStartTimestamp(f.getExecutionStartTime().getTime());
        }
        if(f.getExecutionEndTime()!=null){
            escWorkflowInvocation.setEndTimestamp(f.getExecutionEndTime().getTime());
        }
        switch(f.getInvocationStatus()){
            case WorkflowInvocationFolder.INVOCATION_FINISHED_OK:
                escWorkflowInvocation.setStatus("Finished");
                break;
                
            case WorkflowInvocationFolder.INVOCATION_FINISHED_WITH_ERRORS:
                escWorkflowInvocation.setStatus("ExecutionError");
                break;
                
            case WorkflowInvocationFolder.INVOCATION_RUNNING:
                escWorkflowInvocation.setStatus("Running");
                break;
                
            case WorkflowInvocationFolder.INVOCATION_WAITING:
                escWorkflowInvocation.setStatus("Queued");
                break;
                
            case WorkflowInvocationFolder.INVOCATION_WAITING_FOR_DEBUGGER:
                escWorkflowInvocation.setStatus("Debugging");
                break;
                
            default:
                escWorkflowInvocation.setStatus("Unknown");
                
        }        
        return escWorkflowInvocation;   
    }
    
    public static WorkflowParameter createEscWorkflowParameter(EscWorkflowParameter escWorkflowParameter){
        WorkflowParameter p = new WorkflowParameter();
        p.setBlockName(escWorkflowParameter.getBlockName());
        p.setName(escWorkflowParameter.getName());
        p.setValue(escWorkflowParameter.getValue());
        return p;
    }
    
    public static WorkflowParameterList createEscWorkflowParameterList(EscWorkflowParameterList escWorkflowParameterList){
        WorkflowParameterList p = new WorkflowParameterList();
        EscWorkflowParameter[] values = escWorkflowParameterList.getValues();
        for(int i=0;i<values.length;i++){
            p.add(values[i].getBlockName(), values[i].getName(), values[i].getValue());
        }
        return p;
    }
    
    public static EscMetadataItem createMetadataItem(MetadataItem md){
        EscMetadataItem escMetadataItem = new EscMetadataItem();
        escMetadataItem.setObjectId(md.getObjectId());
        escMetadataItem.setName(md.getName());
        escMetadataItem.setCategory(md.getCategory());
        escMetadataItem.setStringValue(md.getStringValue());
        escMetadataItem.setId(md.getId());
        
        if(md instanceof TextMetadata){
            escMetadataItem.setMetadataType(EscMetadataItem.METADATA_TYPE.TEXT);
            
        } else if(md instanceof BooleanMetadata){
            escMetadataItem.setMetadataType(EscMetadataItem.METADATA_TYPE.BOOLEAN);
            
        } else if(md instanceof NumericalMetadata){
            escMetadataItem.setMetadataType(EscMetadataItem.METADATA_TYPE.NUMERICAL);
            
        } else if(md instanceof DateMetadata){
            escMetadataItem.setMetadataType(EscMetadataItem.METADATA_TYPE.DATE);
            
        } else {
            escMetadataItem.setMetadataType(EscMetadataItem.METADATA_TYPE.TEXT);
            
        }
        
        return escMetadataItem;
    }
    
    public static EscDataset createEscDataset(Dataset ds){
        EscDataset escDataset = new EscDataset();
        populateEscObject(escDataset, ds);
        return escDataset;
    }
    
    public static DatasetItem createDatasetItem(EscDatasetItem escDatasetItem){
        DatasetItem item = null;
        if(escDatasetItem.getItemType()== EscDatasetItem.DATASET_ITEM_TYPE.MULTI_ROW){
            item = new JsonMultipleValueItem();

        } else if(escDatasetItem.getItemType()== EscDatasetItem.DATASET_ITEM_TYPE.SINGLE_ROW){
            item = new SingleJsonRowItem();
            
        } else {
            item = new JsonMultipleValueItem();
        }
        
        if(escDatasetItem.getId()!=-1){
            item.setId(escDatasetItem.getId());
        } else {
            item.setId(0);
        }
        
        item.setDatasetId(escDatasetItem.getDatasetId());
        item.setName(escDatasetItem.getName());
        
        if(item instanceof SingleValueItem){
            SingleValueItem svi = (SingleValueItem)item;
            switch(escDatasetItem.getUpdateStrategy()){
                case AVERAGE:
                    svi.setUpdateStrategy(DatasetItem.UPDATE_CALCULATES_AVERAGE);
                    break;
                    
                case MAXIMUM:
                    svi.setUpdateStrategy(DatasetItem.UPDATE_CALCULATES_MAXIMUM);
                    break;
                    
                case MINIMUM:
                    svi.setUpdateStrategy(DatasetItem.UPDATE_CALCULATES_MINIMUM);
                    break;
                    
                case REPLACE:
                    svi.setUpdateStrategy(DatasetItem.UPDATE_REPLACES_VALUES);
                    break;
                    
                case SUM:
                    svi.setUpdateStrategy(DatasetItem.UPDATE_CALCULATES_SUM);
                    break;
                    
                default:
                    svi.setUpdateStrategy(DatasetItem.UPDATE_REPLACES_VALUES);
                    break;
            }
        }
        return item;
    }
    
    public static EscDatasetItem createEscDatasetItem(DatasetItem item){
        EscDatasetItem escDatasetItem = new EscDatasetItem();
        escDatasetItem.setDatasetId(item.getDatasetId());
        escDatasetItem.setId(item.getId());
        escDatasetItem.setName(item.getName());

        // Update strategy
        if(item instanceof SingleValueItem){
            SingleValueItem svi = (SingleValueItem)item;
            if(svi.getUpdateStrategy()!=null && !svi.getUpdateStrategy().isEmpty()){
                if(DatasetConstants.UPDATE_CALCULATES_AVERAGE.equals(svi.getUpdateStrategy())){
                    escDatasetItem.setUpdateStrategy(EscDatasetItem.DATASET_ITEM_UPDATE_STRATEGY.AVERAGE);
                    
                } else if(DatasetConstants.UPDATE_CALCULATES_MAXIMUM.equals(svi.getUpdateStrategy())){
                    escDatasetItem.setUpdateStrategy(EscDatasetItem.DATASET_ITEM_UPDATE_STRATEGY.MAXIMUM);
                    
                } else if(DatasetConstants.UPDATE_CALCULATES_MINIMUM.equals(svi.getUpdateStrategy())){
                    escDatasetItem.setUpdateStrategy(EscDatasetItem.DATASET_ITEM_UPDATE_STRATEGY.MINIMUM);
                    
                } else if(DatasetConstants.UPDATE_CALCULATES_SUM.equals(svi.getUpdateStrategy())){
                    escDatasetItem.setUpdateStrategy(EscDatasetItem.DATASET_ITEM_UPDATE_STRATEGY.SUM);
                    
                } else if(DatasetConstants.UPDATE_REPLACES_VALUES.equals(svi.getUpdateStrategy())){
                    escDatasetItem.setUpdateStrategy(EscDatasetItem.DATASET_ITEM_UPDATE_STRATEGY.REPLACE);
                            
                } else {
                    escDatasetItem.setUpdateStrategy(EscDatasetItem.DATASET_ITEM_UPDATE_STRATEGY.REPLACE);
                }
                
            } else {
                escDatasetItem.setUpdateStrategy(EscDatasetItem.DATASET_ITEM_UPDATE_STRATEGY.REPLACE);
            }
            
        } else {
            // No update strategy for multirow items
            escDatasetItem.setUpdateStrategy(EscDatasetItem.DATASET_ITEM_UPDATE_STRATEGY.REPLACE);
        }
        
        if(item instanceof SingleJsonRowItem){
            escDatasetItem.setItemType(EscDatasetItem.DATASET_ITEM_TYPE.SINGLE_ROW);
            
        } else if(item instanceof SingleValueItem){
            escDatasetItem.setItemType(EscDatasetItem.DATASET_ITEM_TYPE.SINGLE_ROW);
            
        } else if(item instanceof JsonMultipleValueItem){
            escDatasetItem.setItemType(EscDatasetItem.DATASET_ITEM_TYPE.MULTI_ROW);
            
        } else if(item instanceof MultipleValueItem){
            escDatasetItem.setItemType(EscDatasetItem.DATASET_ITEM_TYPE.MULTI_ROW);
            
        } else {
            escDatasetItem.setItemType(EscDatasetItem.DATASET_ITEM_TYPE.MULTI_ROW);
        }
        
        return escDatasetItem;
    }
    
    public static boolean datasetItemsMatch(DatasetItem item1, DatasetItem item2){
        if(item1.getDatasetId().equals(item2.getDatasetId())){
            if(item1.getName().equals(item2.getName())){
                if(item1.getTypeLabel().equals(item2.getTypeLabel())){
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
