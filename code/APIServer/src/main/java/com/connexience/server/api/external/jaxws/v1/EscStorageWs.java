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
package com.connexience.server.api.external.jaxws.v1;

import java.util.List;

import com.connexience.api.model.*;
import com.connexience.server.ConnexienceException;
import com.connexience.server.api.external.helpers.StorageHelper;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.security.Ticket;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * This class provides an Http BASIC Authentication secured SOAP service to 
 * access e-SC data.
 * @author hugo
 */
@WebService(serviceName = "storagev1")
public class EscStorageWs implements StorageInterface {
    @Resource WebServiceContext jaxWsContext;
    
    private Ticket getTicket() throws ConnexienceException {    
        return EJBLocator.lookupTicketBean().createWebTicket(jaxWsContext.getUserPrincipal().getName());
    }    
    
    /**
     * Get the current user
     */
    @WebMethod(operationName = "currentUser")
    @Override
    public EscUser currentUser() throws Exception {
        return new StorageHelper(getTicket()).currentUser();
    }
    
    /**
     * Get the home folder
     */
    @WebMethod(operationName = "homeFolder")
    @Override
    public EscFolder homeFolder() throws Exception {
        return new StorageHelper(getTicket()).homeFolder();
    }
    
    /** 
     * Get the documents in a folder
     */
    @WebMethod(operationName = "folderDocuments")
    @Override
    public EscDocument[] folderDocuments(@WebParam(name="folderId")String id) throws Exception {
        return new StorageHelper(getTicket()).folderDocuments(id);
    }

    /**
     * Get linked documents
     */
    @WebMethod(operationName = "getRelatedDocuments")
    @Override
    public EscDocument[] getRelatedDocuments(@WebParam(name="id")String id) throws Exception {
        return new StorageHelper(getTicket()).getRelatedDocuments(id);
    }
    
    /**
     * Get the sub directories of a folder
     */
    @WebMethod(operationName = "listChildFolders")
    @Override
    public EscFolder[] listChildFolders(@WebParam(name="folderId")String id) throws Exception {
        return new StorageHelper(getTicket()).listChildFolders(id);
    }
    
    /**
     * Get or create a new document record in a folder 
     */
    @WebMethod(operationName = "createDocumentInFolder")
    @Override
    public EscDocument createDocumentInFolder(@WebParam(name="folderId")String id, @WebParam(name="documentName")String name) throws Exception {
        return new StorageHelper(getTicket()).createDocumentInFolder(id, name);
    }
    
    /**
     * Get a folder by ID
     */
    @WebMethod(operationName = "getFolder")
    @Override
    public EscFolder getFolder(@WebParam(name="folderId")String id) throws Exception {
        return new StorageHelper(getTicket()).getFolder(id);
    }
    
    /** 
     * Get a document by ID
     */
    @WebMethod(operationName = "getDocument")
    @Override
    public EscDocument getDocument(@WebParam(name="documentId")String id) throws Exception {
        return new StorageHelper(getTicket()).getDocument(id);
    }
    
    /**
     * Get all of the versions of a document
     */
    @WebMethod(operationName = "listDocumentVersions")
    @Override
    public EscDocumentVersion[] listDocumentVersions(@WebParam(name="documentId")String id) throws Exception{
        return new StorageHelper(getTicket()).listDocumentVersions(id);
    }
    
    /**
     * Get all of the versions of a document
     */
    @WebMethod(operationName = "listProjects")
    @Override
    public EscProject[] listProjects() throws Exception {
        return new StorageHelper(getTicket()).listProjects();
    }

    @WebMethod(operationName = "getProject")
    @Override
    public EscProject getProject(@WebParam(name="id")String id) throws Exception {
        return new StorageHelper(getTicket()).getProject(id);
    }
    
    /**
     * Update a document
     */
    @WebMethod(operationName = "updateDocument")
    @Override
    public EscDocument updateDocument(@WebParam(name="document")EscDocument document) throws Exception {
        return new StorageHelper(getTicket()).updateDocument(document);
    }
    
    /**
     * Create a folder inside a parent
     */
    @WebMethod(operationName = "createChildFolder")
    @Override
    public EscFolder createChildFolder(@WebParam(name="parentFolderId")String id, @WebParam(name="folderName")String name) throws Exception {
        return new StorageHelper(getTicket()).createChildFolder(id, name);
    }
    
    /**
     * Update a folder
     */
    @WebMethod(operationName = "updateFolder")
    @Override
    public EscFolder updateFolder(@WebParam(name="folder")EscFolder folder) throws Exception {
        return new StorageHelper(getTicket()).updateFolder(folder);
    }

    @WebMethod(operationName = "deleteDocument")
    @Override
    public void deleteDocument(String documentId) throws Exception {
        new StorageHelper(getTicket()).deleteDocument(documentId);
    }

    @WebMethod(operationName = "deleteFolder")
    @Override
    public void deleteFolder(String folderId) throws Exception {
        new StorageHelper(getTicket()).deleteFolder(folderId);
    }

    @WebMethod(operationName = "getLatestDocumentVersion")
    @Override
    public EscDocumentVersion getLatestDocumentVersion(String documentId) throws Exception {
        return new StorageHelper(getTicket()).getLatestDocumentVersion(documentId);
    }

    @WebMethod(operationName = "deleteDocumentVersion")
    @Override
    public void deleteDocumentVersion(String documentId, String versionId) throws Exception {
        new StorageHelper(getTicket()).deleteDocumentVersion(documentId, versionId);
    }
    
    @WebMethod(operationName = "getDocumentVersion")
    @Override
    public EscDocumentVersion getDocumentVersion(String id) throws Exception {
        return new StorageHelper(getTicket()).getDocumentVersion(id);
    }
    
    @WebMethod(operationName = "getDocumentMetadata")
    @Override
    public EscMetadataItem[] getDocumentMetadata(String id) throws Exception {
        return new StorageHelper(getTicket()).getDocumentMetadata(id);
    }

    @WebMethod(operationName = "addMetadataToDocument")
    @Override
    public EscMetadataItem addMetadataToDocument(String id, EscMetadataItem metadataItem) throws Exception {
        return new StorageHelper(getTicket()).addMetadataToDocument(id, metadataItem);
    }

    @WebMethod(operationName = "getTimestamp")
    @Override
    public long getTimestamp() throws Exception {
        return new StorageHelper(getTicket()).getTimestamp();
    }

    @WebMethod(operationName = "uploadBlock")
    @Override
    public String uploadBlock(String documentId, String versionId, Integer blockId, byte[] blockContent) throws Exception {
        return new StorageHelper(getTicket()).uploadBlock(documentId, versionId, blockId, blockContent);
    }

    @WebMethod(operationName = "commitBlockList")
    @Override
    public EscDocumentVersion commitBlockList(String documentId, String versionId, List<Integer> blockList) throws Exception {
        return new StorageHelper(getTicket()).commitBlockList(documentId, versionId, blockList);
    }

    @WebMethod(operationName = "getBlockList")
    @Override
    public List<Integer> getBlockList(String documentId, String versionId) throws Exception {
        return new StorageHelper(getTicket()).getBlockList(documentId, versionId);
    }
}
