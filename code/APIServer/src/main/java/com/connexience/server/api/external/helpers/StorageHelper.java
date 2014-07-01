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
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.document.UncommittedVersion;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.metadata.MetadataItem;
import com.connexience.server.model.metadata.types.BooleanMetadata;
import com.connexience.server.model.metadata.types.DateMetadata;
import com.connexience.server.model.metadata.types.NumericalMetadata;
import com.connexience.server.model.metadata.types.TextMetadata;
import com.connexience.server.model.project.Project;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.User;
import com.connexience.server.util.StorageUtils;

import javax.jws.WebParam;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class provides a helper to support the various storage services
 * @author hugo
 */
public class StorageHelper implements StorageInterface {
    Ticket t;

    public StorageHelper(Ticket t) {
        this.t = t;
    }
    
    /**
     * Get the current user
     */
    @Override
    public EscUser currentUser() throws Exception {
        User user = EJBLocator.lookupUserDirectoryBean().getUser(t, t.getUserId());
        return EscObjectFactory.createEscUser(user);
    }
    
    /**
     * Get the home folder
     */
    @Override
    public EscFolder homeFolder() throws Exception {
        Folder f = EJBLocator.lookupStorageBean().getHomeFolder(t, t.getUserId());
        return EscObjectFactory.createEscFolder(f);
    }

    /** 
     * Get the documents in a folder
     */
    @Override
    public EscDocument[] folderDocuments(@WebParam(name="folderId")String id) throws Exception {
        List docs = EJBLocator.lookupStorageBean().getFolderDocumentRecords(t, id);
        EscDocument[] results = new EscDocument[docs.size()];
        int count = 0;
        for(Object o : docs){
            results[count] = EscObjectFactory.createEscDocument((DocumentRecord)o);
            count++;
        }
        return results;
    }

    @Override
    public EscDocument[] getRelatedDocuments(String id) throws Exception {
        DocumentRecord source = EJBLocator.lookupStorageBean().getDocumentRecord(t, id);
        if(source!=null){
            Collection<ServerObject> results = EJBLocator.lookupLinkBean().getLinkedSourceObjects(t, source);
            ArrayList<EscDocument> docs = new ArrayList<>();
            
            for(ServerObject o : results){
                if(o instanceof DocumentRecord){
                    docs.add(EscObjectFactory.createEscDocument((DocumentRecord)o));
                }
            }
            int count = 0;
            EscDocument[] related = new EscDocument[docs.size()];
            for(EscDocument d : docs){
                related[count] = d;
                count++;
            }
            return related;
        } else {
            return new EscDocument[0];
        }
    }

    /**
     * Get the sub directories of a folder
     */
    @Override
    public EscFolder[] listChildFolders(@WebParam(name="folderId")String id) throws Exception {
        List folders = EJBLocator.lookupStorageBean().getChildFolders(t, id);
        EscFolder[] results = new EscFolder[folders.size()];
        for(int i = 0;i<folders.size();i++){
            results[i] = EscObjectFactory.createEscFolder((Folder)folders.get(i));
        }
        return results;
    }
    
    /**
     * Get or create a new document record in a folder 
     */
    @Override
    public EscDocument createDocumentInFolder(@WebParam(name="folderId")String id, @WebParam(name="documentName")String name) throws Exception {
        return EscObjectFactory.createEscDocument(StorageUtils.getOrCreateDocumentRecord(t, id, name));
    }
    
    /**
     * Get a folder by ID
     */
    @Override
    public EscFolder getFolder(@WebParam(name="folderId")String id) throws Exception {
        return EscObjectFactory.createEscFolder(EJBLocator.lookupStorageBean().getFolder(t, id));
    }
    
    /** 
     * Get a document by ID
     */
    @Override
    public EscDocument getDocument(@WebParam(name="documentId")String id) throws Exception {
        return EscObjectFactory.createEscDocument(EJBLocator.lookupStorageBean().getDocumentRecord(t, id));
    }
    
    /**
     * Get all of the versions of a document
     */
    @Override
    public EscDocumentVersion[] listDocumentVersions(@WebParam(name="documentId")String id) throws Exception{
        List versions = EJBLocator.lookupStorageBean().listVersions(t, id);
        EscDocumentVersion[] results = new EscDocumentVersion[versions.size()];
        for(int i=0;i<versions.size();i++){
            results[i] = EscObjectFactory.createEscDocumentVersion((DocumentVersion)versions.get(i));
        }
        return results;
    }

    /**
     * List projects the user is a member of
     */
    @Override
    public EscProject[] listProjects() throws Exception {
        List projects = EJBLocator.lookupProjectsBean().getMemberProjects(t, 0, 0);
        EscProject[] results = new EscProject[projects.size()];
        for(int i=0;i<projects.size();i++){
            results[i] = EscObjectFactory.createEscProject((Project)projects.get(i));
        } 
        return results;
    }

    /**
     * Get a project by ID
     */
    @Override
    public EscProject getProject(String id) throws Exception {
        return EscObjectFactory.createEscProject(EJBLocator.lookupProjectsBean().getProject(t, Integer.parseInt(id)));
    }
    
    /**
     * Update a document
     */
    @Override
    public EscDocument updateDocument(@WebParam(name="document")EscDocument document) throws Exception {
        DocumentRecord existing = EJBLocator.lookupStorageBean().getDocumentRecord(t, document.getId());
        if(existing!=null){
            existing.setContainerId(document.getContainerId());
            existing.setName(document.getName());
            existing.setDescription(document.getDescription());
            existing = EJBLocator.lookupStorageBean().saveDocumentRecord(t, existing);
            return EscObjectFactory.createEscDocument(existing);
        } else {
            throw new Exception("No such document");
        }
    }
    
    /**
     * Create a folder inside a parent
     */
    @Override
    public EscFolder createChildFolder(@WebParam(name="parentFolderId")String id, @WebParam(name="folderName")String name) throws Exception {
        Folder parent = EJBLocator.lookupStorageBean().getFolder(t, id);
        if(parent!=null){
            Folder child = new Folder();
            child.setName(name);
            child.setContainerId(parent.getId());
            child = EJBLocator.lookupStorageBean().addChildFolder(t, id, child);
            return EscObjectFactory.createEscFolder(child);
        } else {
            throw new Exception("No such parent folder");
        }
    }
    
    /**
     * Update a folder
     */
    @Override
    public EscFolder updateFolder(@WebParam(name="folder")EscFolder folder) throws Exception {
        Folder existing = EJBLocator.lookupStorageBean().getFolder(t, folder.getId());
        if(existing!=null){
            existing.setName(folder.getName());
            existing.setDescription(folder.getDescription());
            existing.setContainerId(folder.getContainerId());
            existing = EJBLocator.lookupStorageBean().updateFolder(t, existing);
            return EscObjectFactory.createEscFolder(existing);
        } else {
            throw new Exception("No such folder");
        }
    }


    /**
     * Get latest document version
     */    
    @Override
    public EscDocumentVersion getLatestDocumentVersion(String documentId) throws Exception {
        DocumentRecord doc = EJBLocator.lookupStorageBean().getDocumentRecord(t, documentId);
        if(doc!=null){
            DocumentVersion version = EJBLocator.lookupStorageBean().getLatestVersion(t, documentId);
            if(version!=null){
                return EscObjectFactory.createEscDocumentVersion(version);
            } else {
                throw new Exception("Document has no versions");
            }
        } else {
            throw new Exception("No such document");
        }
    }

    /**
     * Delete a folder
     */    
    @Override
    public void deleteFolder(String folderId) throws Exception {
        EJBLocator.lookupStorageBean().removeFolderTree(t, folderId);
    }
    
    /**
     * Delete a document
     */
    @Override
    public void deleteDocument(String documentId) throws Exception {
        EJBLocator.lookupStorageBean().removeDocumentRecord(t, documentId);
    }

    /** Retrieve the metadata for an object */
    @Override
    public EscMetadataItem[] getDocumentMetadata(String id) throws Exception {
        List results = EJBLocator.lookupMetaDataBean().getObjectMetadata(t, id);
        EscMetadataItem[] metadata = new EscMetadataItem[results.size()];
        for(int i=0;i<results.size();i++){
            metadata[i] = EscObjectFactory.createMetadataItem((MetadataItem)results.get(i));
        }
        return metadata;
    }

    /** Add some metadata to an object */
    @Override
    public EscMetadataItem addMetadataToDocument(String id, EscMetadataItem metadataItem) throws Exception {
        MetadataItem md;
        switch(metadataItem.getMetadataType()){
            case BOOLEAN:
                md = new BooleanMetadata();
                ((BooleanMetadata)md).setBooleanValue(Boolean.parseBoolean(metadataItem.getStringValue()));
                break;
                
            case DATE:
                md = new DateMetadata();
                ((DateMetadata)md).setDateValue(Date.valueOf(metadataItem.getStringValue()));
                break;
                
            case NUMERICAL:
                md = new NumericalMetadata();
                ((NumericalMetadata)md).setDoubleValue(Double.parseDouble(metadataItem.getStringValue()));
                break;
                
            case TEXT:
                md = new TextMetadata();
                ((TextMetadata)md).setTextValue(metadataItem.getStringValue());
                break;
                
            default:
                md = new TextMetadata();
                ((TextMetadata)md).setTextValue(metadataItem.getStringValue());
                break;
            
        }
        
        md.setCategory(metadataItem.getCategory());
        md.setName(metadataItem.getName());
        md.setObjectId(metadataItem.getObjectId());
        md.setUserId(t.getUserId());
        md = EJBLocator.lookupMetaDataBean().addMetadata(t, id, md);
        return EscObjectFactory.createMetadataItem(md);
    }

    @Override
    public EscDocumentVersion getDocumentVersion(String id) throws Exception {
        return EscObjectFactory.createEscDocumentVersion(EJBLocator.lookupStorageBean().getVersion(t, id));
    }

    @Override
    public long getTimestamp() throws Exception {
        return System.currentTimeMillis();
    }

    @Override
    public String uploadBlock(String documentId, String versionId, Integer blockNo, byte[] blockContent) throws Exception {
        DocumentRecord doc = EJBLocator.lookupStorageBean().getDocumentRecord(t, documentId);
        if (doc == null) {
            //throw new NotFoundException("Cannot find document: " + documentId);
            throw new Exception("Cannot find document: " + documentId);
        }

        if ("new".equals(versionId)) {
            versionId = null;
        }

        return StorageUtils.uploadBlock(t, doc, versionId, blockNo, blockContent).getId();
    }

    @Override
    public EscDocumentVersion commitBlockList(String documentId, String versionId, List<Integer> blockList) throws Exception
    {
        DocumentRecord doc = EJBLocator.lookupStorageBean().getDocumentRecord(t, documentId);
        if (doc == null) {
            //throw new NotFoundException("Cannot find document: " + documentId);
            throw new Exception("Cannot find document: " + documentId);
        }

        UncommittedVersion version = EJBLocator.lookupStorageBean().getUncommittedVersion(t, versionId);
        if (version == null) {
            //throw new NotFoundException("Cannot find document with version: " + versionId);
            throw new Exception("Cannot find document with uncommitted version: " + versionId);
        }

        return EscObjectFactory.createEscDocumentVersion(
                    StorageUtils.commitBlockList(t, doc, version.getId(), "Uploaded via commitBlockList", blockList));
    }

    @Override
    public List<Integer> getBlockList(String documentId, String versionId) throws Exception
    {
        DocumentRecord doc = EJBLocator.lookupStorageBean().getDocumentRecord(t, documentId);
        if (doc == null) {
            //throw new NotFoundException("Cannot find document: " + documentId);
            throw new Exception("Cannot find document: " + documentId);
        }

        if ("latest".equals(versionId)) {
            DocumentVersion version = EJBLocator.lookupStorageBean().getLatestVersion(t, documentId);
            if (version == null) {
                throw new Exception("Cannot find document with version: " + versionId);
            }
            versionId = version.getId();
        } else {
            DocumentVersion version = EJBLocator.lookupStorageBean().getVersion(t, documentId, versionId);
            if (version == null) {
                UncommittedVersion uVersion = EJBLocator.lookupStorageBean().getUncommittedVersion(t, versionId);
                if (uVersion == null) {
                    throw new Exception("Cannot find document with version: " + versionId);
                }
            }
        }

        return StorageUtils.getBlockList(t, doc, versionId);
    }

    @Override
    public void deleteDocumentVersion(String documentId, String versionId) throws Exception {
        EJBLocator.lookupStorageBean().removeVersion(t, documentId, versionId);
    }
    
    
}
