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
package com.connexience.api.model;

import java.util.List;


/**
 * This interface defines the standard storage service. It is implemented by
 * both the REST and SOAP services, and a REST client is provided.
 * @author hugo
 */
public interface StorageInterface {

    /**
     * Create a folder inside a parent
     */
    EscFolder createChildFolder(String id, String name) throws Exception;

    /**
     * Get or create a new document record in a folder
     */
    EscDocument createDocumentInFolder(String id, String name) throws Exception;

    /**
     * Get the current user
     */
    EscUser currentUser() throws Exception;

    /**
     * Get the documents in a folder
     */
    EscDocument[] folderDocuments(String id) throws Exception;

    /**
     * Get all of the documents related to a specified document
     */
    EscDocument[] getRelatedDocuments(String id) throws Exception;
    
    /**
     * Get a document by ID
     */
    EscDocument getDocument(String id) throws Exception;

    /**
     * Get a folder by ID
     */
    EscFolder getFolder(String id) throws Exception;

    /**
     * Get the home folder
     */
    EscFolder homeFolder() throws Exception;

    /**
     * Get the sub directories of a folder
     */
    EscFolder[] listChildFolders(String id) throws Exception;

    /**
     * Get all of the versions of a document
     */
    EscDocumentVersion[] listDocumentVersions(String id) throws Exception;

    /** Get a specific document version */
    EscDocumentVersion getDocumentVersion(String id) throws Exception;
    
    /**
     * List the projects the user is a member of
     */
    EscProject[] listProjects() throws Exception;
    
    /**
     * Get a project by ID
     */
    EscProject getProject(String id) throws Exception;
    
    /**
     * Update a document
     */
    EscDocument updateDocument(EscDocument document) throws Exception;

    /**
     * Update a folder
     */
    EscFolder updateFolder(EscFolder folder) throws Exception;
    
    /**
     * Get the latest version of a document
     */
    EscDocumentVersion getLatestDocumentVersion(String documentId) throws Exception;
    
    /**
     * Delete a version of a document 
     */
    void deleteDocumentVersion(String documentId, String versionId) throws Exception;
    
    /**
     * Delete a document
     */
    void deleteDocument(String documentId) throws Exception;
    
    /**
     * Delete a folder
     */
    void deleteFolder(String folderId) throws Exception;
    
    /**
     * Get the metadata for a document
     */
    EscMetadataItem[] getDocumentMetadata(String id) throws Exception;
    
    /**
     * Add a piece of metadata to a document
     */
    EscMetadataItem addMetadataToDocument(String id, EscMetadataItem metadataItem) throws Exception;
    
    /**
     * Get the server time
     */
    long getTimestamp() throws Exception;

    /**
     * Upload a block of data to the server
     */
    String uploadBlock(String documentId, String versionId, Integer blockId, byte[] blockContent) throws Exception;

    /**
     * Commit the data blocks for a document
     */
    EscDocumentVersion commitBlockList(String documentId, String versionId, List<Integer> blockList) throws Exception;

    /**
     * Get a list of block IDs for a document version
     */
    List<Integer> getBlockList(String documentId, String versionId) throws Exception;
}
