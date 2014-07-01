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
package com.connexience.server.api.external.jaxrs.v1;

import java.util.List;

import com.connexience.api.model.*;
import com.connexience.api.model.json.JSONObject;
import com.connexience.server.ConnexienceException;
import com.connexience.server.api.external.helpers.StorageHelper;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.security.Ticket;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * This class provides the REST endpoint for the publically accessible storage
 * service.
 * @author hugo
 */
@Path("/public/rest/v1/storage")
public class RestStorageService implements StorageInterface {
    @Context SecurityContext secContext;
    
    /** Create a ticket for the current security context */
    private Ticket getTicket() throws ConnexienceException {
        return EJBLocator.lookupTicketBean().createWebTicket(secContext.getUserPrincipal().getName());
    }
    
    @POST
    @Path("/folders/{id}")
    @Produces("application/json")
    @Consumes("text/plain")
    @Override
    public EscFolder createChildFolder(@PathParam(value="id")String id, String name) throws Exception {
        return new StorageHelper(getTicket()).createChildFolder(id, name);
    }

    @POST
    @Path("/folders/{id}/documents/create")
    @Produces("application/json")
    @Consumes("text/plain") 
    @Override
    public EscDocument createDocumentInFolder(@PathParam(value="id")String id, String name) throws Exception {
        return new StorageHelper(getTicket()).createDocumentInFolder(id, name);
    }

    @GET
    @Path("/currentuser")
    @Produces("application/json")
    @Override
    public EscUser currentUser() throws Exception {
        return new StorageHelper(getTicket()).currentUser();
    }

    @GET
    @Path("/folders/{id}/documents")
    @Produces("application/json")
    @Override
    public EscDocument[] folderDocuments(@PathParam(value="id")String id) throws Exception {
        return new StorageHelper(getTicket()).folderDocuments(id);
    }

    @GET
    @Path("/documents/{id}/related")
    @Produces("application/json")
    @Override
    public EscDocument[] getRelatedDocuments(@PathParam(value="id")String id) throws Exception {
        return new StorageHelper(getTicket()).getRelatedDocuments(id);
    }
    
    @GET
    @Path("/documents/{id}")
    @Produces("application/json")
    @Override
    public EscDocument getDocument(@PathParam(value="id")String id) throws Exception {
        return new StorageHelper(getTicket()).getDocument(id);
    }

    @GET
    @Path("/folders/{id}")
    @Produces("application/json")
    @Override
    public EscFolder getFolder(@PathParam(value="id")String id) throws Exception {
        return new StorageHelper(getTicket()).getFolder(id);
    }

    @GET
    @Path("/specialfolders/home")
    @Produces("application/json")
    @Override
    public EscFolder homeFolder() throws Exception {
        return new StorageHelper(getTicket()).homeFolder();
    }

    @GET
    @Path("/folders/{id}/children")
    @Produces("application/json")
    @Override
    public EscFolder[] listChildFolders(@PathParam(value="id")String id) throws Exception {
        return new StorageHelper(getTicket()).listChildFolders(id);
    }

    @GET
    @Path("/documents/{id}/versions")
    @Produces("application/json")
    @Override
    public EscDocumentVersion[] listDocumentVersions(@PathParam(value="id")String id) throws Exception {
        return new StorageHelper(getTicket()).listDocumentVersions(id);
    }
    
    @GET
    @Path("/projects")
    @Produces("application/json")
    @Override
    public EscProject[] listProjects() throws Exception {
        return new StorageHelper(getTicket()).listProjects();
    }
    
    @GET
    @Path("/projects/{id}")
    @Produces("application/json")
    @Override
    public EscProject getProject(@PathParam(value="id")String id) throws Exception {
        return new StorageHelper(getTicket()).getProject(id);
    }
    
    @POST
    @Path("/documents/{id}")
    @Produces("application/json")
    @Consumes("application/json")
    @Override
    public EscDocument updateDocument(EscDocument document) throws Exception {
        return new StorageHelper(getTicket()).updateDocument(document);
    }
    
    @POST
    @Path("/ptdocuments")
    @Produces("application/json")
    @Consumes("text/plain")
    public EscDocument updateDocument(String jsonData) throws Exception {
        JSONObject json = new JSONObject(jsonData);
        EscDocument doc = new EscDocument(json);
        return new StorageHelper(getTicket()).updateDocument(doc);
    }

    @POST
    @Path("/folders/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    @Override
    public EscFolder updateFolder(EscFolder folder) throws Exception {
        return new StorageHelper(getTicket()).updateFolder(folder);
    }
    
    @POST
    @Path("/ptfolders")
    @Consumes("text/plain")
    @Produces("application/json")
    public EscFolder updateFolder(String folderJson) throws Exception {
        JSONObject json = new JSONObject(folderJson);
        EscFolder folder = new EscFolder(json);
        return new StorageHelper(getTicket()).updateFolder(folder);
    }    

    @DELETE
    @Path("/documents/{id}/versions/{versionId}")
    public void deleteDocumentVersion(@PathParam(value="id")String documentId, @PathParam(value="versionId")String versionId) throws Exception{
        new StorageHelper(getTicket()).deleteDocumentVersion(documentId, versionId);
    }
    
    @GET
    @Path("/documents/{id}/versions/latest")
    @Produces("application/json")
    @Override
    public EscDocumentVersion getLatestDocumentVersion(@PathParam(value="id")String documentId) throws Exception {
        return new StorageHelper(getTicket()).getLatestDocumentVersion(documentId);
    }

    @GET
    @Path("/documentversions/{id}")
    @Produces("application/json")
    public EscDocumentVersion getDocumentVersion(@PathParam(value="id")String id) throws Exception {
        return new StorageHelper(getTicket()).getDocumentVersion(id);
    }
    
    @DELETE
    @Path("/documents/{id}")
    @Override
    public void deleteDocument(@PathParam(value="id")String documentId) throws Exception {
        new StorageHelper(getTicket()).deleteDocument(documentId);
    }
    
    /** This method allows a workflow to be deleted by POSTING it's ID to the 
     * /deletedocument url.
     */
    @POST
    @Path("/deletedocument")
    @Consumes("text/plain")
    public void deleteDocumentUsingPOST(String documentId) throws Exception {
        new StorageHelper(getTicket()).deleteDocument(documentId);
    }

    @DELETE
    @Path("/folders/{id}")
    @Override
    public void deleteFolder(@PathParam(value="id")String folderId) throws Exception {
        new StorageHelper(getTicket()).deleteFolder(folderId);
    }

    /**
     * This methods allows a folder to be deleted using a POST method
     */
    @POST
    @Path("/deletefolder")
    @Consumes("text/plain")
    public void deleteFolderUsingPOST(String folderId) throws Exception {
        new StorageHelper(getTicket()).deleteFolder(folderId);
    }
    
    @GET
    @Path("/documents/{id}/metadata")
    @Produces("application/json")
    @Override
    public EscMetadataItem[] getDocumentMetadata(@PathParam(value="id")String id) throws Exception {
        return new StorageHelper(getTicket()).getDocumentMetadata(id);
    }

    @POST
    @Path("/documents/{id}/metadata")
    @Produces("application/json")
    @Consumes("application/json")
    @Override
    public EscMetadataItem addMetadataToDocument(@PathParam(value="id")String id, EscMetadataItem metadataItem) throws Exception {
        return new StorageHelper(getTicket()).addMetadataToDocument(id, metadataItem);
    }
    
    @POST
    @Path("/documents/{id}/ptmetadata")
    @Produces("application/json")
    @Consumes("text/plain")
    public EscMetadataItem addMetadataToDocument(@PathParam(value="id")String id, String metadataJson) throws Exception {
        EscMetadataItem md = new EscMetadataItem(new JSONObject(metadataJson));
        return new StorageHelper(getTicket()).addMetadataToDocument(id, md);
    }
    
    @GET
    @Path("/timestamp")
    @Produces("text/plain")
    @Override
    public long getTimestamp() throws Exception {
        return new StorageHelper(getTicket()).getTimestamp();
    }

    @POST
    @Path("/documents/{documentId}/{versionId}/{blockId}")
    @Consumes("application/octet-stream")
    @Produces("application/json")
    @Override
    public String uploadBlock(@PathParam(value="documentId")String docId, @PathParam(value="versionId")String verId, @PathParam(value="blockId")Integer blockId, byte[] blockContent) throws Exception {
        return new StorageHelper(getTicket()).uploadBlock(docId, verId, blockId, blockContent);
    }

    @POST
    @Path("/documents/{documentId}/{versionId}/blocklist")
    @Consumes("application/json")
    @Produces("application/json")
    @Override
    public EscDocumentVersion commitBlockList(@PathParam(value="documentId")String docId, @PathParam(value="versionId")String verId, List<Integer> blockList) throws Exception {
        return new StorageHelper(getTicket()).commitBlockList(docId, verId, blockList);
    }

    @GET
    @Path("/documents/{documentId}/{versionId}/blocklist")
    @Produces("application/json")
    @Override
    public List<Integer> getBlockList(@PathParam(value="documentId")String docId, @PathParam(value="versionId")String verId) throws Exception {
        return new StorageHelper(getTicket()).getBlockList(docId, verId);
    }
}