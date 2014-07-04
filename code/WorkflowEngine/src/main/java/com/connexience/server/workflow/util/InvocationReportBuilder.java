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
package com.connexience.server.workflow.util;

import com.connexience.server.model.document.*;
import com.connexience.server.model.workflow.*;
import com.connexience.server.model.security.*;
import com.connexience.server.*;
import com.connexience.server.ejb.util.*;
import com.connexience.server.util.*;

import org.pipeline.core.drawing.*;
import org.pipeline.core.drawing.model.*;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.io.*;

import java.util.*;

/**
 * This class builds an invocation report for a workflow. It avoids the need
 * to upload report.xml into the workflow folder.
 * @author hugo
 */
public class InvocationReportBuilder {

    private String invocationId;
    private Ticket ticket;
    private WorkflowInvocationFolder folder = null;
    private DocumentRecord workflowDoc = null;
    DocumentVersion version = null;
    
    public InvocationReportBuilder(String invocationId, Ticket ticket) {
        this.invocationId = invocationId;
        this.ticket = ticket;
    }

    public XmlDataStore buildReport() throws Exception {
        folder = WorkflowEJBLocator.lookupWorkflowManagementBean().getInvocationFolder(ticket, invocationId);
        if (folder != null) {
            // Is the workflow finished
            boolean finished;
            boolean finishedOk;
            if (folder.getInvocationStatus() == WorkflowInvocationFolder.INVOCATION_FINISHED_OK || folder.getInvocationStatus() == WorkflowInvocationFolder.INVOCATION_FINISHED_WITH_ERRORS) {
                finished = true;
                if (folder.getInvocationStatus() == WorkflowInvocationFolder.INVOCATION_FINISHED_OK) {
                    finishedOk = true;
                } else {
                    finishedOk = false;
                }
            } else {
                finished = false;
                finishedOk = false;
            }

            // Get the workflow document
            DefaultDrawingModel drawing = new DefaultDrawingModel();
            workflowDoc = EJBLocator.lookupStorageBean().getDocumentRecord(ticket, folder.getWorkflowId());
 
            if (folder.getVersionId() != null) {
                version = EJBLocator.lookupStorageBean().getVersion(ticket, folder.getWorkflowId(), folder.getVersionId());
            } else {
                version = EJBLocator.lookupStorageBean().getLatestVersion(ticket, folder.getWorkflowId());
            }

            if (workflowDoc != null && version != null) {
                XmlDataStoreStreamReader reader = new XmlDataStoreStreamReader(StorageUtils.getInputStream(ticket, workflowDoc, version));
                drawing.recreateObject(reader.read());
            } else {
                throw new ConnexienceException("Invocation has no workflow data");
            }

            // Get all of the service logs from the database
            List<?> logs = WorkflowEJBLocator.lookupWorkflowManagementBean().getServiceLogs(ticket, invocationId);
            HashMap<String, WorkflowServiceLog> logMap = new HashMap<>();
            WorkflowServiceLog log;

            for (int i = 0; i < logs.size(); i++) {
                log = (WorkflowServiceLog) logs.get(i);
                logMap.put(log.getContextId(), log);
            }

            // If the workflow is finished assume any missing reports indicate success
            int count = 0;
            BlockExecutionReport report;
            BlockModel block;
            Enumeration<?> blocks = drawing.blocks();
            String blockGuid;
            XmlDataStore reportData = new XmlDataStore("Reports");

            while (blocks.hasMoreElements()) {
                block = (BlockModel) blocks.nextElement();
                blockGuid = block.getBlockGUID();
                if (logMap.containsKey(blockGuid)) {
                    // There is a report
                    report = new BlockExecutionReport(block);
                    log = logMap.get(blockGuid);
                    if (log.getStatusText().equals(WorkflowServiceLog.SERVICE_EXECUTION_ERROR)) {
                        report.setExecutionStatus(BlockExecutionReport.INTERNAL_ERROR);
                    } else if (log.getStatusText().equals(WorkflowServiceLog.SERVICE_EXECUTION_OK)) {
                        report.setExecutionStatus(BlockExecutionReport.NO_ERRORS);
                    } else if (log.getStatusText().equals(WorkflowServiceLog.SERVICE_NOT_EXECUTED_YET)) {
                        report.setExecutionStatus(BlockExecutionReport.BLOCK_NOT_EXECUTED_YET);
                    } else {
                        report.setExecutionStatus(BlockExecutionReport.BLOCK_NOT_EXECUTED_YET);
                    }
                    report.setAdditionalMessage(log.getStatusMessage());
                    reportData.add("Report" + count, report);
                    count++;

                } else {
                    // No report
                    report = new BlockExecutionReport(block);
                    if (finished) {
                        // Lack of log if the block is finished means it worked OK
                        if (finishedOk) {
                            report.setExecutionStatus(BlockExecutionReport.NO_ERRORS);
                        } else {
                            report.setExecutionStatus(BlockExecutionReport.UPSTREAM_ERRORS);
                        }
                    } else {
                        report.setExecutionStatus(BlockExecutionReport.BLOCK_NOT_EXECUTED_YET);
                    }
                    reportData.add("Report" + count, report);
                    count++;
                }
            }
            reportData.add("ReportCount", count);
            return reportData;
        } else {
            throw new ConnexienceException("Cannot locate invocation: " + invocationId);
        }
    }
    
    public WorkflowInvocationFolder getFolder(){
        return folder;
    }
    
    public DocumentRecord getWorkflowDocument(){
        return workflowDoc;
    }
            
    public DocumentVersion getVersion(){
        return version;
    }
}
