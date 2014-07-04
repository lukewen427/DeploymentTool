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
package com.connexience.server.workflow.cloud.download;

import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.io.*;
import com.connexience.server.model.document.*;
import com.connexience.server.workflow.api.*;

import java.io.*;
import org.apache.log4j.*;
/**
 * This class downloads a set of workflow data and parses into an XmlDataStore
 * if possible
 * @author nhgh
 */
public class WorkflowDataFetcher {
    static Logger logger = Logger.getLogger(WorkflowDataFetcher.class);
    /** ID of the data to download */
    private String workflowId;

    /** Configured API to use for the download */
    private API apiLink;

    /** Version of the workflow to download */
    private String versionId = null;

    /** Workflow document */
    private DocumentRecord workflowDocument;
    
    public WorkflowDataFetcher(String workflowId, API apiLink) {
        this.workflowId = workflowId;
        this.apiLink = apiLink;
    }

    public WorkflowDataFetcher(String workflowId, String versionId, API apiLink) {
        this.workflowId = workflowId;
        this.apiLink = apiLink;
        this.versionId = versionId;
    }

    /** Get the correct version of the workflow data from the server */
    public XmlDataStore download() throws XmlStorageException, DownloadException {
        API api = apiLink;
        logger.debug("Starting workflow data download for document: " + workflowId);
        try {
            workflowDocument = api.getDocument(workflowId);
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

            if(versionId!=null){
                api.download(workflowDocument, versionId, dataStream);
            } else {
                api.download(workflowDocument, dataStream);
            }
            dataStream.flush();

            XmlDataStoreByteArrayIO reader = new XmlDataStoreByteArrayIO(dataStream.toByteArray());
            return reader.toXmlDataStore();

        } catch (XmlStorageException xlmse){
            logger.error("Error parsing workflow data", xlmse);
            throw xlmse;
        } catch (Exception e){
            logger.error("Workflow data download error", e);
            throw new DownloadException("Error downloading workflow data: " + e.getMessage(), e);
        }
    }

    public DocumentRecord getWorkflowDocument() {
        return workflowDocument;
    }
}
