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
package com.connexience.server.workflow.api;

import com.connexience.server.*;
import com.connexience.server.model.document.*;
import com.connexience.server.model.logging.graph.WorkflowDataWriteOperation;
import com.connexience.server.model.workflow.DynamicWorkflowLibrary;
import com.connexience.server.model.workflow.DynamicWorkflowService;
import com.connexience.server.model.workflow.WorkflowDocument;
import com.connexience.server.util.provenance.ProvenanceLoggerClient;
import com.connexience.server.rmi.IProvenanceLogger;

import java.io.*;
import java.util.*;
import org.pipeline.core.xmlstorage.XmlDataStore;
/**
 * This class uploads data and retries if it fails.
 * @author hugo
 */
public abstract class Uploader {
    protected InputStream stream;
    protected DocumentRecord document;
    protected DocumentVersion uploadedDocumentVersion = null;
    protected API parent;    
    
    public Uploader() {

    }
    
    /** Log the fact that some data has been written by the API */
    public void logDataWrite(){
        XmlDataStore provenance = parent.getProvenanceProperties();
        if(provenance!=null && !(document instanceof DynamicWorkflowLibrary) && !(document instanceof DynamicWorkflowService) && !(document instanceof WorkflowDocument)){
            WorkflowDataWriteOperation op = new WorkflowDataWriteOperation();
            op.setBlockUUID(provenance.stringValue("BlockUUID", null));
            op.setVersionId(uploadedDocumentVersion.getId());
            op.setVersionNumber(Integer.toString(uploadedDocumentVersion.getVersionNumber()));
            op.setDocumentName(document.getName());
            op.setDocumentId(document.getId());
            op.setInvocationId(provenance.stringValue("InvocationID", null));
            op.setTimestamp(new Date());
            op.setUserId(parent.getTicket().getUserId());            
            op.setProjectId(parent.getTicket().getDefaultProjectId());
            IProvenanceLogger provClient = new ProvenanceLoggerClient();
            provClient.log(op);                
        }
    }
    
    public void setParent(API parent){
        this.parent = parent;
    }
    
    public void setDocument(DocumentRecord document){
        this.document = document;
    }

    public void setStream(InputStream stream){
        this.stream = stream;
    }
    
    public DocumentVersion getUploadedDocumentVersion(){
        return uploadedDocumentVersion;
    }
            
    public abstract boolean upload() throws ConnexienceException;
}
