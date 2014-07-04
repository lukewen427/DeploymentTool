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

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.document.*;
import com.connexience.server.model.logging.graph.WorkflowDataReadOperation;
import com.connexience.server.util.provenance.ProvenanceLoggerClient;
import com.connexience.server.rmi.IProvenanceLogger;


import java.io.*;
import java.util.Date;

import org.pipeline.core.xmlstorage.XmlDataStore;

/**
 * This class attempts multiple times to download a file if there are any IO / 
 * Socket errors
 * @author hugo
 */
public abstract class Downloader {
    protected API parent;
    protected DocumentRecord document;
    protected String versionId = null;
    protected int versionNumber = -1;
    protected OutputStream stream;
    
   
    public Downloader(){
        
    }

    public void setDocument(DocumentRecord document) {
        this.document = document;
    }

    public void setParent(API parent) {
        this.parent = parent;
    }

    public void setStream(OutputStream stream) {
        this.stream = stream;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }
       
    /** Log data read by workflow */
    public void logDataRead(){
        XmlDataStore provenance = parent.getProvenanceProperties();
        if(provenance!=null){
            WorkflowDataReadOperation op = new WorkflowDataReadOperation();
            op.setBlockUUID(provenance.stringValue("BlockUUID", null));
            op.setVersionId(versionId);
            op.setVersionNumber(Integer.toString(versionNumber));
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
    
    public abstract boolean download() throws ConnexienceException;
    
    public abstract InputStream getInputStream() throws ConnexienceException;
}
