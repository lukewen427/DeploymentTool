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
package com.connexience.server.util;

import com.connexience.server.ConnexienceException;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.ejb.util.WorkflowEJBLocator;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentType;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.workflow.DynamicWorkflowLibrary;
import com.connexience.server.model.workflow.DynamicWorkflowService;
import com.connexience.server.model.workflow.WorkflowDocument;

/**
 * This class contains some utilities for dealing with dynamic workflow services
 * @author nhgh
 */
public class ServiceUtils {
    /** Create a new service library or get an existing one */
    public static DynamicWorkflowLibrary getOrCreateDynamicWorkflowLibrary(Ticket ticket, String folderId, String fileName) throws ConnexienceException {
        DocumentRecord existingDoc = EJBLocator.lookupStorageBean().getNamedDocumentRecord(ticket, folderId, fileName);
        if (existingDoc instanceof DynamicWorkflowLibrary) {
            return (DynamicWorkflowLibrary)existingDoc;

        } else {
            DynamicWorkflowLibrary library = new DynamicWorkflowLibrary();
            library.setName(fileName);
            library.setContainerId(folderId);

            // Try and find the document type
            String extension = getExtension(fileName);
            if (extension != null) {
                DocumentType type = EJBLocator.lookupStorageBean().getDocumentTypeByExtension(ticket, extension);
                if (type != null) {
                    library.setDocumentTypeId(type.getId());
                }
            }
            library = WorkflowEJBLocator.lookupWorkflowManagementBean().saveDynamicWorkflowLibrary(ticket, library);
            return library;
        }
    }


    /** Create a new service or get an existing one */
    public static DynamicWorkflowService getOrCreateDynamicWorkflowService(Ticket ticket, String folderId, String fileName) throws ConnexienceException {
        DocumentRecord existingDoc = EJBLocator.lookupStorageBean().getNamedDocumentRecord(ticket, folderId, fileName);
        if (existingDoc instanceof DynamicWorkflowService) {
            return (DynamicWorkflowService)existingDoc;

        } else {
            DynamicWorkflowService service = new DynamicWorkflowService();
            service.setName(fileName);
            service.setContainerId(folderId);

            // Try and find the document type
            String extension = getExtension(fileName);
            if (extension != null) {
                DocumentType type = EJBLocator.lookupStorageBean().getDocumentTypeByExtension(ticket, extension);
                if (type != null) {
                    service.setDocumentTypeId(type.getId());
                }
            }
            service = WorkflowEJBLocator.lookupWorkflowManagementBean().saveDynamicWorkflowService(ticket, service);
            return service;
        }
    }

    /** Create a new workflow document or get an existing one */
    public static WorkflowDocument getOrCreateWorkflowDocument(Ticket ticket, String folderId, String fileName) throws ConnexienceException {
        DocumentRecord existingDoc = EJBLocator.lookupStorageBean().getNamedDocumentRecord(ticket, folderId, fileName);
        if(existingDoc instanceof WorkflowDocument){
            return (WorkflowDocument)existingDoc;

        } else {
            WorkflowDocument doc = new WorkflowDocument();
            doc.setName(fileName);
            doc.setContainerId(folderId);
            doc = WorkflowEJBLocator.lookupWorkflowManagementBean().saveWorkflowDocument(ticket, doc);
            return doc;
        }
    }

    /** Process uploaded service data to extract the service.xml file */
    public static void processServiceXml(Ticket ticket, DynamicWorkflowService service) throws ConnexienceException {

    }
    
    /**
     * Get the file name extension
     */
    private static String getExtension(String fileName) {
        int lastDotIdx = fileName.lastIndexOf(".");
        if (lastDotIdx > 0 && lastDotIdx < fileName.length() - 1) {
            return fileName.substring(lastDotIdx + 1).trim();
        }
        return null;
    }
}
