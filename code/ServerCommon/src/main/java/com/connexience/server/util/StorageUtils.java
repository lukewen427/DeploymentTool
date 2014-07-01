/**
 * e-Science Central Copyright (C) 2008-2013 School of Computing Science,
 * Newcastle University
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation at: http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package com.connexience.server.util;

import com.connexience.server.ConnexienceException;
import com.connexience.server.ConnexienceSecurityException;
import com.connexience.server.ejb.storage.StorageRemote;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.ejb.util.WorkflowEJBLocator;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentType;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.document.UncommittedVersion;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.logging.graph.UserReadOperation;
import com.connexience.server.model.logging.graph.UserWriteOperation;
import com.connexience.server.model.security.Permission;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.User;
import com.connexience.server.model.storage.DataStore;
import com.connexience.server.model.workflow.DynamicWorkflowService;
import com.connexience.server.model.workflow.WorkflowDocument;
import com.connexience.server.rmi.IProvenanceLogger;
import com.connexience.server.util.provenance.ProvenanceLoggerClient;
import org.jboss.logging.Logger;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides utility methods for managing storage and retrieval of
 * data using upload and download reservations.
 *
 * @author nhgh
 */
public abstract class StorageUtils {

    private static final Logger _Logger = Logger.getLogger(StorageUtils.class);

    /**
     * Cache of document types
     */
    private static final ConcurrentHashMap<String, DocumentType> documentTypeCache = new ConcurrentHashMap<>();

    /**
     * Keystore password
     */
    private static char[] password = new String("sT0r3pa33worD").toCharArray();

    /**
     * Download a file to a specified directory
     */
    public static void downloadFileToDirectory(Ticket ticket, DocumentRecord record, DocumentVersion version, File targetDirectory) throws ConnexienceException {

        // Make sure there is a version. If not, get the latest one
        if (version == null) {
            version = EJBLocator.lookupStorageBean().getLatestVersion(ticket, record.getId());
        }

        // Check that the file represents a directory
        if (!targetDirectory.isDirectory()) {
            throw new ConnexienceException("Target is not a directory");
        }

        DataStore store = EJBLocator.lookupStorageBean().getOrganisationDataStore(ticket, ticket.getOrganisationId());

        // Get data if the store exists
        if (store != null) {
            try {
                File targetFile = new File(targetDirectory, record.getName());
                FileOutputStream stream = new FileOutputStream(targetFile);

                //log the provenance
                String userName = EJBLocator.lookupUserDirectoryBean().getUserName(ticket, ticket.getUserId());
                Long timestamp = System.currentTimeMillis();
                UserReadOperation read = new UserReadOperation(record.getId(), version.getId(), record.getName(), String.valueOf(version.getVersionNumber()), ticket.getUserId(), new Date(timestamp), userName);
                read.setProjectId(ticket.getDefaultProjectId());
                IProvenanceLogger provClient = new ProvenanceLoggerClient();
                provClient.log(read);

                store.writeToStream(record, version, stream);
                stream.flush();
                stream.close();
            } catch (Exception e) {
                throw new ConnexienceException("Error writing data to target file: " + e.getMessage());
            }

        } else {
            throw new ConnexienceException("Could not locate data store for document: " + record.getName());
        }
    }

    /**
     * Get an InputStream connected to a document record
     */
    public static InputStream getInputStream(Ticket ticket, DocumentRecord record, DocumentVersion version) throws ConnexienceException {

        // Make sure there is a version. If not, get the latest one
        if (version == null) {
            version = EJBLocator.lookupStorageBean().getLatestVersion(ticket, record.getId());
        }

        DataStore store = EJBLocator.lookupStorageBean().getOrganisationDataStore(ticket, ticket.getOrganisationId());

        // Get data if the store exists
        if (store != null) {

            //log the provenance
            String userName = EJBLocator.lookupUserDirectoryBean().getUserName(ticket, ticket.getUserId());
            Long timestamp = System.currentTimeMillis();
            UserReadOperation read = new UserReadOperation(record.getId(), version.getId(), record.getName(), String.valueOf(version.getVersionNumber()), ticket.getUserId(), new Date(timestamp), userName);
            read.setProjectId(ticket.getDefaultProjectId());
            IProvenanceLogger provClient = new ProvenanceLoggerClient();
            provClient.log(read);

            return store.getInputStream(record, version);
        } else {
            throw new ConnexienceException("Could not locate data store for document: " + record.getName());
        }
    }

    /**
     * Get some server data into a byte array. Send in a null document version
     * to get the latest file version
     */
    public static byte[] download(Ticket ticket, DocumentRecord record, DocumentVersion version) throws ConnexienceException {

        // Make sure there is a version. If not, get the latest one
        if (version == null) {
            version = EJBLocator.lookupStorageBean().getLatestVersion(ticket, record.getId());
        }

        DataStore store = EJBLocator.lookupStorageBean().getOrganisationDataStore(ticket, ticket.getOrganisationId());

        // Get data if the store exists
        if (store != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            store.writeToStream(record, version, stream);

            //log the provenance
            String userName = EJBLocator.lookupUserDirectoryBean().getUserName(ticket, ticket.getUserId());
            Long timestamp = System.currentTimeMillis();
            UserReadOperation read = new UserReadOperation(record.getId(), version.getId(), record.getName(), String.valueOf(version.getVersionNumber()), ticket.getUserId(), new Date(timestamp), userName);
            read.setProjectId(ticket.getDefaultProjectId());
            IProvenanceLogger provClient = new ProvenanceLoggerClient();
            provClient.log(read);

            return stream.toByteArray();
        } else {
            throw new ConnexienceException("Could not locate data store for document: " + record.getName());
        }
    }

    /**
     * Upload a File to the server
     */
    public static DocumentVersion upload(Ticket ticket, File file, DocumentRecord document, String comments) throws ConnexienceException {
        try {

            // Get the datastore for the organisation that the user belongs to
            DataStore store = EJBLocator.lookupStorageBean().getOrganisationDataStore(ticket, ticket.getOrganisationId());
            if (store.isWriteEnabled()) {
                // Create a new version
                DocumentVersion version = EJBLocator.lookupStorageBean().createNextVersion(ticket, document.getId());

                // Update the version information back into the database
                version.setUserId(ticket.getUserId());
                version.setSize(file.length());
                version.setComments(comments);
                EJBLocator.lookupStorageBean().updateVersion(ticket, version);
                version = store.readFromStream(document, version, new FileInputStream(file));
                version = EJBLocator.lookupStorageBean().updateVersion(ticket, version);

                String userName = EJBLocator.lookupUserDirectoryBean().getUserName(ticket, ticket.getUserId());
                Long timestamp = System.currentTimeMillis();
                UserWriteOperation write = new UserWriteOperation(document.getId(), version.getId(), document.getName(), String.valueOf(version.getVersionNumber()), ticket.getUserId(), new Date(timestamp), userName);
                write.setProjectId(ticket.getDefaultProjectId());
                IProvenanceLogger provClient = new ProvenanceLoggerClient();
                provClient.log(write);

                return version;

            } else {
                throw new ConnexienceException("Storage is Read-Only");
            }
        } catch (ConnexienceException ce) {
            throw new ConnexienceException("Error uploading file: " + ce.getMessage(), ce);
        } catch (Exception e) {
            throw new ConnexienceException("Error signing byte array: " + e.getMessage());
        }
    }

    /**
     * Upload a byte[] array to the server as a file
     */
    public static DocumentVersion upload(Ticket ticket, byte[] data, DocumentRecord document, String comments) throws ConnexienceException {
        try {

            // Get the datastore for the organisation that the user belongs to
            DataStore store = EJBLocator.lookupStorageBean().getOrganisationDataStore(ticket, ticket.getOrganisationId());
            if (store.isWriteEnabled()) {
                // Create a new version
                DocumentVersion version = EJBLocator.lookupStorageBean().createNextVersion(ticket, document.getId());

                // Update the version information back into the database
                version.setUserId(ticket.getUserId());
                version.setSize(data.length);
                version.setComments(comments);
                EJBLocator.lookupStorageBean().updateVersion(ticket, version);

                version = store.readFromStream(document, version, new ByteArrayInputStream(data));
                version = EJBLocator.lookupStorageBean().updateVersion(ticket, version);

                //log the provenance
                String userName = EJBLocator.lookupUserDirectoryBean().getUserName(ticket, ticket.getUserId());
                Long timestamp = System.currentTimeMillis();
                UserWriteOperation write = new UserWriteOperation(document.getId(), version.getId(), document.getName(), String.valueOf(version.getVersionNumber()), ticket.getUserId(), new Date(timestamp), userName);
                write.setProjectId(ticket.getDefaultProjectId());
                IProvenanceLogger provClient = new ProvenanceLoggerClient();
                provClient.log(write);

                return version;
            } else {
                throw new ConnexienceException("Storage is Read-Only");
            }

        } catch (ConnexienceException ce) {
            throw new ConnexienceException("Error uploading byte array: " + ce.getMessage(), ce);
        } catch (Exception e) {
            throw new ConnexienceException("Error signing byte array: " + e.getMessage());
        }
    }

    /**
     * Upload data from an InputStream to the server
     */
    public static DocumentVersion upload(Ticket ticket, InputStream inStream, DocumentRecord document, String comments) throws ConnexienceException {
        try {

            // Get the datastore for the organisation that the user belongs to
            DataStore store = EJBLocator.lookupStorageBean().getOrganisationDataStore(ticket, ticket.getOrganisationId());
            if (store.isWriteEnabled()) {
                // Create a new version
                DocumentVersion version = EJBLocator.lookupStorageBean().createNextVersion(ticket, document.getId());

                // Update the version information back into the database
                version.setUserId(ticket.getUserId());
                version.setSize(0);
                version.setComments(comments);
                version = EJBLocator.lookupStorageBean().updateVersion(ticket, version);
                version = store.readFromStream(document, version, inStream);
                version = EJBLocator.lookupStorageBean().updateVersion(ticket, version);

                //log the provenance
                String userName = EJBLocator.lookupUserDirectoryBean().getUserName(ticket, ticket.getUserId());
                Long timestamp = System.currentTimeMillis();
                UserWriteOperation write = new UserWriteOperation(document.getId(), version.getId(), document.getName(), String.valueOf(version.getVersionNumber()), ticket.getUserId(), new Date(timestamp), userName);
                write.setProjectId(ticket.getDefaultProjectId());
                IProvenanceLogger provClient = new ProvenanceLoggerClient();
                provClient.log(write);

                return version;
            } else {
                throw new ConnexienceException("Storage is Read-Only");
            }

        } catch (ConnexienceException ce) {
            throw new ConnexienceException("Error uploading InputStream: " + ce.getMessage(), ce);
        } catch (Exception e) {
            throw new ConnexienceException("Error signing byte array: " + e.getMessage(), e);
        }
    }

    /**
     * Create a new document record, or return the existing document record for
     * a named file within a folder
     */
    public static DocumentRecord getOrCreateDocumentRecord(Ticket ticket, String folderId, String fileName) throws ConnexienceException {
        DocumentRecord existing = EJBLocator.lookupStorageBean().getNamedDocumentRecord(ticket, folderId, fileName);
        if (existing != null) {
            return existing;
        } else {
            DocumentRecord record = new DocumentRecord();
            record.setName(fileName);
            record.setContainerId(folderId);

            // Try and find the document type
            String extension = getExtension(fileName);
            if (extension != null) {
                DocumentType type = getDocumentTypeByExtention(ticket, extension);
                if (type != null) {
                    record.setDocumentTypeId(type.getId());
                }
            }
            record = EJBLocator.lookupStorageBean().saveDocumentRecord(ticket, record);
            return record;
        }
    }

    /**
     * Get a document type by extension. This method uses a cache
     */
    public static DocumentType getDocumentTypeByExtention(Ticket ticket, String extension) throws ConnexienceException {
        if (documentTypeCache.containsKey(extension)) {
            return documentTypeCache.get(extension);
        } else {
            DocumentType type = EJBLocator.lookupStorageBean().getDocumentTypeByExtension(ticket, extension);
            if (type != null) {
                documentTypeCache.put(extension, type);
            }
            return type;
        }
    }

    /**
     * Get the file name extension
     */
    public static String getExtension(String fileName) {
        int lastDotIdx = fileName.lastIndexOf(".");
        if (lastDotIdx > 0 && lastDotIdx < fileName.length() - 1) {
            return fileName.substring(lastDotIdx + 1).trim();
        }
        return null;
    }

    /**
     * Get the PrivateKey for the current user
     */
    public static PrivateKey getPrivateKey(Ticket ticket) throws ConnexienceException {
        try {
            byte[] keystoreData = EJBLocator.lookupCertificateBean().getKeyStoreData(ticket, ticket.getUserId());

            ByteArrayInputStream stream = new ByteArrayInputStream(keystoreData);
            KeyStore store = KeyStore.getInstance("JKS");
            store.load(stream, password);

            KeyStore.PrivateKeyEntry pke = (KeyStore.PrivateKeyEntry) store.getEntry("MyKey", new KeyStore.PasswordProtection(password));
            return pke.getPrivateKey();
        } catch (ConnexienceException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ConnexienceException(e.getMessage());
        }
    }

    /**
     * Download a file to an HttpServletResponse. This method makes sure that
     * the database access worked before the output stream is committed
     */
    public static boolean downloadFileToServletResponse(Ticket ticket, DocumentRecord record, DocumentVersion version, HttpServletResponse response) throws ConnexienceException {
        InputStream inStream = getInputStream(ticket, record, version);
        byte[] buffer = new byte[4096];
        int len;
        BufferedOutputStream bufferedStream = null;
        try {
            response.setContentLength((int) version.getSize());
            bufferedStream = new BufferedOutputStream(response.getOutputStream());

            //log the provenance
            String userName = EJBLocator.lookupUserDirectoryBean().getUserName(ticket, ticket.getUserId());
            Long timestamp = System.currentTimeMillis();
            UserReadOperation read = new UserReadOperation(record.getId(), version.getId(), record.getName(), String.valueOf(version.getVersionNumber()), ticket.getUserId(), new Date(timestamp), userName);
            read.setProjectId(ticket.getDefaultProjectId());
            IProvenanceLogger provClient = new ProvenanceLoggerClient();
            provClient.log(read);

            while ((len = inStream.read(buffer)) > 0) {
                bufferedStream.write(buffer, 0, len);
            }
            return true;
        } catch (Exception e) {
            throw new ConnexienceException("Error downloading file: " + e.getMessage());
        } finally {
            try {
                inStream.close();
            } catch (Exception e) {
            }

            try {
                bufferedStream.flush();
            } catch (Exception e) {
            }

            try {
                bufferedStream.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Download a file to an output stream
     */
    public static void downloadFileToOutputStream(Ticket ticket, DocumentRecord record, DocumentVersion version, OutputStream stream) throws ConnexienceException {
        InputStream inStream = getInputStream(ticket, record, version);
        byte[] buffer = new byte[4096];
        int len;
        try {
            //log the provenance
            String userName = EJBLocator.lookupUserDirectoryBean().getUserName(ticket, ticket.getUserId());
            Long timestamp = System.currentTimeMillis();
            UserReadOperation read = new UserReadOperation(record.getId(), version.getId(), record.getName(), String.valueOf(version.getVersionNumber()), ticket.getUserId(), new Date(timestamp), userName);
            read.setProjectId(ticket.getDefaultProjectId());
            IProvenanceLogger provClient = new ProvenanceLoggerClient();
            provClient.log(read);

            while ((len = inStream.read(buffer)) > 0) {
                stream.write(buffer, 0, len);
            }
            stream.flush();
        } catch (Exception e) {
            throw new ConnexienceException("Error downloading file: " + e.getMessage());
        } finally {
            try {
                inStream.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Download data from a stream to a file
     */
    public static long copyStreamToFile(InputStream stream, File file) throws ConnexienceException {
        try {
            byte[] buffer = new byte[4096];
            FileOutputStream outStream = new FileOutputStream(file);
            long fileLen = 0;

            int len;
            while ((len = stream.read(buffer)) > 0) {
                fileLen = fileLen + len;
                outStream.write(buffer, 0, len);
            }

            outStream.flush();
            outStream.close();
            return fileLen;
        } catch (Exception e) {
            throw new ConnexienceException("Error copying stream data to temporary file: " + e.getMessage());
        }
    }

    /**
     * Merge two document records together. This copies all of the old versions
     * of one document over the other. All of the versions of sourceDocument and
     * appended to the versions of targetDocument
     */
    public static DocumentRecord mergeDocuments(Ticket ticket, DocumentRecord sourceDocument, DocumentRecord targetDocument) throws ConnexienceException {
        List versions = EJBLocator.lookupStorageBean().listVersions(ticket, sourceDocument.getId());
        DocumentVersion v;
        for (Object o : versions) {
            v = (DocumentVersion) o;
            copyDocumentData(ticket, sourceDocument, v, targetDocument);
        }
        EJBLocator.lookupStorageBean().removeDocumentRecord(ticket, sourceDocument.getId());
        return targetDocument;
    }

    /**
     * Copy data from one documentrecord to another where they are owned by the
     * same user
     */
    public static DocumentVersion copyDocumentData(Ticket ticket, DocumentRecord sourceDocument, DocumentVersion sourceVersion, DocumentRecord targetDocument) throws ConnexienceException {

        //Call the method which deals with two users but passing the same user.
        User user = EJBLocator.lookupUserDirectoryBean().getUser(ticket, ticket.getUserId());
        return copyDocumentData(ticket, user, sourceDocument, sourceVersion, targetDocument);
    }

    /**
     * Copy data from one document record to another where the records are owned
     * by different users
     */
    public static DocumentVersion copyDocumentData(Ticket ownerTicket, User newUser, DocumentRecord sourceDocument, DocumentVersion sourceVersion, DocumentRecord targetDocument) throws ConnexienceException {
        try {
            Ticket newUserTicket = EJBLocator.lookupTicketBean().createWebTicketForDatabaseId(newUser.getId());
            DataStore store = EJBLocator.lookupStorageBean().getOrganisationDataStore(ownerTicket, ownerTicket.getOrganisationId());
            if (store.isWriteEnabled()) {
                if (sourceVersion == null) {
                    sourceVersion = EJBLocator.lookupStorageBean().getLatestVersion(ownerTicket, sourceDocument.getId());
                }

                //Get the source document
                InputStream inStream = getInputStream(ownerTicket, sourceDocument, sourceVersion);

                //Set up the new DocumentVersion
                DocumentVersion targetVersion = EJBLocator.lookupStorageBean().createNextVersion(newUserTicket, targetDocument.getId());
                targetVersion.setComments(sourceVersion.getComments());
                targetVersion.setUserId(newUserTicket.getUserId());
                targetVersion = EJBLocator.lookupStorageBean().updateVersion(newUserTicket, targetVersion);

                //Copy the actual data and update the version
                targetVersion = store.readFromStream(targetDocument, targetVersion, inStream);
                targetVersion = EJBLocator.lookupStorageBean().updateVersion(newUserTicket, targetVersion);

                //log the provenance
                String userName = EJBLocator.lookupUserDirectoryBean().getUserName(ownerTicket, ownerTicket.getUserId());
                Long timestamp = System.currentTimeMillis();
                UserWriteOperation write = new UserWriteOperation(targetDocument.getId(), targetVersion.getId(), targetDocument.getName(), String.valueOf(targetVersion.getVersionNumber()), ownerTicket.getUserId(), new Date(timestamp), userName);
                write.setProjectId(ownerTicket.getDefaultProjectId());
                IProvenanceLogger provClient = new ProvenanceLoggerClient();
                provClient.log(write);

                return targetVersion;
            } else {
                throw new ConnexienceException("Storage is Read-Only");
            }
        } catch (ConnexienceException ce) {
            throw new ConnexienceException("Error copying document: " + ce.getMessage(), ce);
        } catch (Exception e) {
            throw new ConnexienceException("Error copying document data: " + e.getMessage());
        }
    }

    /**
     * Copy the contents of a folder to another folder owned by the same user
     */
    public static Folder copyFolderContents(Ticket ticket, Folder sourceFolder, Folder parentFolder, String targetFolderName) throws ConnexienceException, ConnexienceSecurityException {

        //Call the method which deals with different users.  Pass in the same user asd ignore the docIdMap and workflowList
        //which map the old IDs to new IDs
        User user = EJBLocator.lookupUserDirectoryBean().getUser(ticket, ticket.getUserId());
        return copyFolderContents(ticket, sourceFolder, user, parentFolder, targetFolderName, new HashMap<String, String>(), new ArrayList<WorkflowDocument>());
    }

    /**
     * Copy the contents of a folder from one owner to another. Replace the IDs
     * of copied documents where possible. Will replace the following:
     *
     * 1) Service IDs in workflow documents if the services are copied too 2)
     * DocumentWrapper and FolderWrapper service parameters when the source
     * document is copied.
     *
     *
     */
    public static Folder copyFolderContentsAndReplaceWorkflowIds(Ticket ticket, Folder sourceFolder, User parentFolderOwner, Folder parentFolder, String targetFolderName) throws ConnexienceException, ConnexienceSecurityException {

        //Get a ticket for the owner of the source folder
        Ticket currentUserTicket = EJBLocator.lookupTicketBean().createWebTicketForDatabaseId(parentFolderOwner.getId());

        //HM to hold the mapping of old ID -> new ID
        HashMap<String, String> docIdMap = new HashMap<>();

        //List of workflow documents that have been copied
        List<WorkflowDocument> workflowList = new ArrayList<>();

        //Copy the contents
        Folder newFolder = copyFolderContents(ticket, sourceFolder, parentFolderOwner, parentFolder, targetFolderName, docIdMap, workflowList);

        //Go through the workflows and replace the IDs
        for (WorkflowDocument workflow : workflowList) {
            WorkflowEJBLocator.lookupWorkflowManagementBean().replaceWorkflowServiceIds(currentUserTicket, workflow, docIdMap);
        }

        return newFolder;
    }

    /**
     * Copy the contents of a folder to another. Will copy subfolders too and
     * handle folders which are owned by different people. Will return a list of
     * the workflow documents copied and a Map of document IDs which have been
     * copied, old ID -> new ID
     *
     */
    private static Folder copyFolderContents(Ticket folderOwnerTicket, Folder sourceFolder, User parentFolderOwner, Folder parentFolder, String targetFolderName, HashMap<String, String> docIdMap, List<WorkflowDocument> workflowList) throws ConnexienceException, ConnexienceSecurityException {

        //Get a ticket for the owner of the new folder
        Ticket currentUserTicket = EJBLocator.lookupTicketBean().createWebTicketForDatabaseId(parentFolderOwner.getId());

        //We must be able to read the source folder
        StorageRemote storageRemote = EJBLocator.lookupStorageBean();
        if (EJBLocator.lookupAccessControlBean().canTicketAccessResource(folderOwnerTicket, sourceFolder, Permission.WRITE_PERMISSION)) {

            //Create the new folder
            Folder targetFolder = new Folder();
            targetFolder.setName(targetFolderName);
            targetFolder.setContainerId(parentFolder.getId());
            targetFolder.setPublicVisible(sourceFolder.isPublicVisible());
            targetFolder.setShortName(sourceFolder.getShortName());
            targetFolder.setCreationTime(new Date());
            targetFolder.setCreatorId(parentFolderOwner.getId());
            targetFolder.setDescription(sourceFolder.getDescription());
            targetFolder.setObjectType(sourceFolder.getObjectType());
            targetFolder.setOrganisationId(sourceFolder.getOrganisationId());
            targetFolder.setTimeInMillis(new Date().getTime());
            targetFolder = EJBLocator.lookupStorageBean().addChildFolder(currentUserTicket, parentFolder.getId(), targetFolder);

            //Create the documents
            @SuppressWarnings("unchecked")
            List<DocumentRecord> contents = storageRemote.getFolderDocumentRecords(folderOwnerTicket, sourceFolder.getId());
            for (DocumentRecord doc : contents) {
                DocumentVersion version = EJBLocator.lookupStorageBean().getLatestVersion(folderOwnerTicket, doc.getId());
                if (version != null) {

                    //Set workflow and service specific items
                    DocumentRecord duplicate;
                    if (doc instanceof WorkflowDocument) {
                        WorkflowDocument oldWfDoc = (WorkflowDocument) doc;
                        WorkflowDocument newWfDoc = new WorkflowDocument();
                        newWfDoc.setEngineType(oldWfDoc.getEngineType());
                        newWfDoc.setExternalDataBlockName(oldWfDoc.getExternalDataBlockName());
                        newWfDoc.setExternalDataSupported(oldWfDoc.isExternalDataSupported());
                        newWfDoc.setIntermedateDataStored(oldWfDoc.isIntermedateDataStored());
                        duplicate = newWfDoc;
                    } else if (doc instanceof DynamicWorkflowService) {
                        DynamicWorkflowService wfService = (DynamicWorkflowService) doc;
                        DynamicWorkflowService newWfService = new DynamicWorkflowService();
                        newWfService.setCategory(wfService.getCategory());
                        duplicate = newWfService;
                    } else {
                        //Just a DocumentRecord
                        duplicate = new DocumentRecord();
                    }

                    //Set standard properties
                    duplicate.setName(doc.getName());
                    duplicate.setContainerId(targetFolder.getId());
                    duplicate.setCreationTime(new Date());
                    duplicate.setCreatorId(parentFolderOwner.getId());
                    duplicate.setDescription(doc.getDescription());
                    duplicate.setDocumentTypeId(doc.getDocumentTypeId());
                    duplicate.setLimitVersions(doc.isLimitVersions());
                    duplicate.setMaxVersions(doc.getMaxVersions());
                    duplicate.setObjectType(doc.getObjectType());
                    duplicate.setOrganisationId(doc.getOrganisationId());
                    duplicate.setVersioned(doc.isVersioned());

                    //Save the document in the correct way for workflows and services
                    if (duplicate instanceof WorkflowDocument) {
                        duplicate = WorkflowEJBLocator.lookupWorkflowManagementBean().saveWorkflowDocument(currentUserTicket, (WorkflowDocument) duplicate);
                        workflowList.add((WorkflowDocument) duplicate);
                    } else if (duplicate instanceof DynamicWorkflowService) {
                        duplicate = WorkflowEJBLocator.lookupWorkflowManagementBean().saveDynamicWorkflowService(currentUserTicket, (DynamicWorkflowService) duplicate);
                    } else {
                        duplicate = EJBLocator.lookupStorageBean().saveDocumentRecord(currentUserTicket, duplicate);
                    }

                    //Add the new ID to the Map so that IDs can be replaced in workflows
                    docIdMap.put(doc.getId(), duplicate.getId());

                    //Copy the actual data
                    DocumentVersion newVersion = StorageUtils.copyDocumentData(currentUserTicket, doc, version, duplicate);

                    // Now do any final config
                    if (duplicate instanceof WorkflowDocument) {
                        // Get a preview picture
                        WorkflowEJBLocator.lookupWorkflowManagementBean().updateWorkflowImage(currentUserTicket, duplicate.getId());
                    } else if (duplicate instanceof DynamicWorkflowService) {
                        String newServiceCategory = ((DynamicWorkflowService) duplicate).getCategory();
                        // Parse the service data
                        WorkflowEJBLocator.lookupWorkflowManagementBean().updateServiceXml(currentUserTicket, duplicate.getId(), newVersion.getId(), newServiceCategory);
                    }

                }
            }

            //Recurse through the subfolders
            @SuppressWarnings("unchecked")
            List<Folder> subFolders = storageRemote.getChildFolders(folderOwnerTicket, sourceFolder.getId());
            for (Folder subFolder : subFolders) {
                copyFolderContents(folderOwnerTicket, subFolder, parentFolderOwner, targetFolder, subFolder.getName(), docIdMap, workflowList);
            }
        } else {
            throw new ConnexienceSecurityException("Not allowed to copy folder");
        }
        return new Folder();
    }

    /**
     * <p>
     * Uploads a chunk of a document data for the specified document. When
     * uploading a document block by block, the first call to this method should
     * leave <code>version</code> as <code>null</code> to create a new
     * uncommitted document version. Then all subsequent calls to
     * <code>uploadBlock</code> and {@link #commitBlockList} should use that
     * version id to refer to the version of the document being uploaded.</p>
     *
     * <p>
     * A special (currently unsupported) case is when <code>versionId</code>
     * refers to an already committed document version. Then the
     * <code>uploadBlock</code> creates a new, uncommitted version of the
     * document that is based on the previous version with id
     * <code>versionId</code>. Later during {@link #commitBlockList} all blocks
     * from previous version (obtained using {@link #getBlockList}) together
     * with the newly uploaded blocks need to be committed. This gives an
     * effective way to append data to the end of the document, but also a low
     * cost way to add blocks in front of the document or any other arrangement
     * of blocks.
     *
     * @param ticket
     * @param document
     * @param version may be <code>null</code> to create a new version of a
     * document and upload its first block or non-null to upload any subsequent
     * blocks.
     * @param blockId an identification number of a block. The actual order of
     * block IDs is send during commitBlockList, so there are no constraints on
     * block numbering (e.g. the IDs need not increase monotonically).
     * @param blockContent
     * @return a new document version when version is null. Otherwise an updated
     * version record.
     * @throws ConnexienceException
     */
    public static UncommittedVersion uploadBlock(Ticket ticket, DocumentRecord document, String versionId, int blockId, byte[] blockContent) throws ConnexienceException {
        // Get the datastore for the organisation that the user belongs to
        DataStore store = EJBLocator.lookupStorageBean().getOrganisationDataStore(ticket, ticket.getOrganisationId());
        if (store.isWriteEnabled()) {
            UncommittedVersion version;
            DocumentVersion existingVersion;

            if (versionId == null) {
                // Create a new version
                version = EJBLocator.lookupStorageBean().createUncommittedVersion(ticket, document.getId());
            } else {
                version = EJBLocator.lookupStorageBean().getUncommittedVersion(ticket, versionId);
                if (version == null) {
                    existingVersion = EJBLocator.lookupStorageBean().getVersion(ticket, document.getId(), versionId);
                    if (existingVersion == null) {
                        throw new ConnexienceException("Invalid version id");
                    } else {
                        // This is a special case when an existing committed version of a document could be
                        // changed by adding a new block to it.
                        throw new UnsupportedOperationException();
                    }
                }
                version.setUserId(ticket.getUserId());
                EJBLocator.lookupStorageBean().updateUncommittedVersion(ticket, version);
            }

            if (store.isChunkedUploadSupported()) {
                // FIXME: [CALA] This is a little hack but I can't find a way to 
                // register a uncommitted version in the FileDataStore without too
                // many changes.
                //if (store instanceof FileDataStore) {
                //    // Add uncommitted version irrespective of any exceptions that 
                //    // might occur later in this code. It doesn't hurt too much.
                //    EJBLocator.lookupStorageBean().addUncommittedVersion(ticket, version.getId());
                //}
                store.uploadBlock(document, version, blockId, blockContent);
            } else {
                // Add uncommitted version irrespective of any exceptions that 
                // might occur later in this code. It doesn't hurt too much.
                //EJBLocator.lookupStorageBean().addUncommittedVersion(ticket, version.getId());

                File blocksDir = new File(getBlockPath(store, document, version.getId()));
                if (!blocksDir.exists()) {
                    _Logger.warn("Data storage does not support chunked upload. The upload will incur an additional latency overhead.");
                    blocksDir.mkdirs();
                }
                File blockFile = new File(blocksDir, Integer.toString(blockId));
                OutputStream outStream = null;
                try {
                    outStream = new BufferedOutputStream(new FileOutputStream(blockFile));
                    outStream.write(blockContent);
                } catch (Exception x) {
                    throw new ConnexienceException("Internal error", x);
                } finally {
                    if (outStream != null) {
                        try {
                            outStream.close();
                        } catch (Exception x) {
                            _Logger.warn("Exception when closing the block output stream", x);
                        }
                    }
                }
            }

            // Do not record provenance for chunked uploads until commit.
            return version;
        } else {
            throw new ConnexienceException("Store is Read-Only");
        }
    }

    public static List<Integer> getBlockList(Ticket ticket, DocumentRecord document, String versionId) throws ConnexienceException {
        // Get the datastore for the organisation that the user belongs to
        DataStore store = EJBLocator.lookupStorageBean().getOrganisationDataStore(ticket, ticket.getOrganisationId());

        if (store.isChunkedUploadSupported()) {
            return store.getBlockList(document, versionId);
        } else {
            ArrayList<Integer> blockList = new ArrayList<>();
            File blockDir = new File(getBlockPath(store, document, versionId));
            if (blockDir.exists()) {
                // No specific order of blocks...
                for (String blockName : blockDir.list()) {
                    blockList.add(Integer.parseInt(blockName));
                }
            }
            return blockList;
        }
    }

    /**
     * <p>
     * Commits the list of blocks that creates a document. If this call is
     * successful, the uncommitted <code>version</code> becomes invalid and
     * cannot be used again; neither to upload new blocks nor to commit a new
     * version with possibly different selection/order of blocks.</p>
     *
     * <p>
     * To add a new block or change the order of blocks in an existing document
     * use {@link #uploadBlock} followed by {@link #commitBlockList} with a new
     * uncommitted version id.</p>
     */
    public static DocumentVersion commitBlockList(Ticket ticket, DocumentRecord document, String versionId, String comments, List<Integer> blockList) throws ConnexienceException {
        // Get the datastore for the organisation that the user belongs to
        DataStore store = EJBLocator.lookupStorageBean().getOrganisationDataStore(ticket, ticket.getOrganisationId());
        if (store.isWriteEnabled()) {
            // A basic sanity check
            UncommittedVersion uVersion = EJBLocator.lookupStorageBean().getUncommittedVersion(ticket, versionId);
            if (uVersion == null || !uVersion.getDocumentRecordId().equals(document.getId())) {
                throw new ConnexienceException("No such version");
            }

            DocumentVersion committedVersion = null;

            // Easy part: try to create and update a new document version; rollback if there's anything wrong
            UserTransaction tx = null;
            try {
                tx = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
                tx.begin();
                committedVersion = EJBLocator.lookupStorageBean().createNextVersion(ticket, document.getId());
                committedVersion.setUserId(ticket.getUserId());
                committedVersion.setComments(comments);

                // Difficult part: try to commit block list, which may involve a remote and long running call
                // to a storage service
                if (store.isChunkedUploadSupported()) {
                // Note! This might be a remote call to some cloud storage
                    // but it should be relatively quick.
                    committedVersion = store.commitBlockList(document, uVersion, blockList, committedVersion);
                    
                } else {
                    // Note! This might be a remote call to some cloud storage
                    // and it can be relatively long.
                    _Logger.warn("Data storage does not support chunked upload. The upload will incur an additional latency overhead.");
                    ArrayList<InputStream> subStreams = new ArrayList<>();
                    File blockDir = new File(getBlockPath(store, document, versionId));
                    if (!blockDir.exists()) {
                        throw new IllegalArgumentException("No blocks found. Use 'uploadBlock(...)' first.");
                    }
                    String errorId = null;
                    InputStream superStream = null;
                    try {
                        // Prepare the SequenceInputStream for all the chunks
                        for (Integer blockNo : blockList) {
                            String blockId = Integer.toString(blockNo);
                            File block = new File(blockDir, blockId);
                            if (!block.exists()) {
                                throw new IllegalArgumentException("Block not found: blockId = " + blockId);
                            }
                            errorId = blockId;
                            subStreams.add(new FileInputStream(block));
                        }
                        // Upload the chunks to the store
                        superStream = new BufferedInputStream(new SequenceInputStream(Collections.enumeration(subStreams)));
                        committedVersion = store.readFromStream(document, committedVersion, superStream);
                        // Clean up
                        delete(blockDir, false, false);
                    } catch (FileNotFoundException x) {
                        throw new ConnexienceException("Internal error: cannot read block: " + errorId, x);
                    } finally {
                        try {
                            superStream.close();
                        } catch (IOException x) {
                            // Ignore any issues with closing substreams
                        }
                    }
                }

                try {
                    committedVersion = StorageUtils.populateWithMD5(ticket, document, committedVersion);
                    committedVersion = EJBLocator.lookupStorageBean().updateVersion(ticket, committedVersion);
                    EJBLocator.lookupStorageBean().removeUncommittedVersion(ticket, versionId);
                } catch (Exception x) {
                    throw new ConnexienceException("Error updating version data: versionId = " + committedVersion.getId(), x);
                }

                // Run any triggers
                WorkflowEJBLocator.lookupWorkflowManagementBean().runTriggersForDocument(ticket, document);

                // Log the provenance
                String userName = EJBLocator.lookupUserDirectoryBean().getUserName(ticket, ticket.getUserId());
                Long timestamp = System.currentTimeMillis();
                UserWriteOperation write = new UserWriteOperation(
                        document.getId(),
                        committedVersion.getId(),
                        document.getName(),
                        String.valueOf(committedVersion.getVersionNumber()),
                        ticket.getUserId(),
                        new Date(timestamp),
                        userName);
                write.setProjectId(ticket.getDefaultProjectId());
                IProvenanceLogger provClient = new ProvenanceLoggerClient();
                provClient.log(write);

                tx.commit();
                return committedVersion;
            } catch (Exception x) {
                if (tx != null) {
                    try {
                        tx.rollback();
                    } catch (SystemException xx) {
                        throw new ConnexienceException("Error during transaction rollback when updating version data: UncommittedVersion.Id = " + versionId, xx);
                    }
                }
                throw new ConnexienceException("Error updating version data: UncommittedVersion.Id = " + versionId, x);
            }
        } else {
            throw new ConnexienceException("Store is Read-Only");
        }
    }

    public static boolean cleanupVersionIfUncommitted(Ticket ticket, DocumentRecord document, UncommittedVersion version) throws ConnexienceException {
        // Get the datastore for the organisation that the user belongs to
        DataStore store = EJBLocator.lookupStorageBean().getOrganisationDataStore(ticket, ticket.getOrganisationId());
        if (store.isWriteEnabled()) {
            if (store.isChunkedUploadSupported()) {
                // skip cleanup
                return false;
            }

            File blockDir = new File(getBlockPath(store, document, version.getId()));
            if (!blockDir.exists()) {
                _Logger.warnf("Attempt to cleanup nonexistent document version, documentId = %s, versionId = %s", document.getId(), version.getId());
                return false;
            }
            if (!blockDir.isDirectory()) {
                _Logger.warnf("Attempt to cleanup unchunked document version, documentId = %s, versionId = %s", document.getId(), version.getId());
                return false;
            }

            return delete(blockDir, false, false);
        }
        throw new ConnexienceException("Store is Read-Only");
    }

    /**
     * Delete a file or directory (recursively).
     *
     * @param f the file or directory to be deleted.
     * @param deleteOnExit if <code>true</code> and delete fails, the file is
     * registered to be deleted on JVM exit; see File.deleteOnExit for more
     * details.
     * @param subdirOnly if <code>true</code>, only files included in the
     * directory denoted by <code>f</code> are deleted. If <code>f</code> is not
     * a directory, setting <code>subdirOnly</code> will cause f not to be
     * deleted.
     * @throws IOException
     */
    public static boolean delete(File f, boolean deleteOnExit, boolean subdirOnly) {
        if (!f.exists()) {
            return false;
        }

        if (f.isDirectory()) {
            if (deleteOnExit) {
                for (File ff : f.listFiles()) {
                    if (!delete(ff, true, false)) {
                        ff.deleteOnExit();
                    }
                }
            } else {
                for (File ff : f.listFiles()) {
                    delete(ff, false, false);
                }
            }
        }

        if (!subdirOnly) {
            if (!f.delete() && deleteOnExit) {
                f.deleteOnExit();
                return false;
            }
        }

        return true;
    }

    private static String getBlockPath(DataStore store, DocumentRecord document, String versionId) {
        StringBuilder sb = new StringBuilder();

        sb.append(System.getProperty("java.io.tmpdir"));
        sb.append("/org-");
        sb.append(store.getOrganisationId());
        sb.append("/doc-");
        sb.append(document.getId());
        sb.append("/");
        sb.append(versionId);
        sb.append(".dir");

        return sb.toString();
    }

    /**
     * Get / Create a folder path and return the bottom level folder
     */
    public static Folder getOrCreateFolderPath(Ticket ticket, Folder parent, String path) throws ConnexienceException {
        PathSplitter splitter = new PathSplitter(path);
        Folder current = parent;
        Folder nextFolder;
        String folderName;

        while (splitter.hasNextElement()) {
            folderName = splitter.nextElement();
            if (folderName != null && !folderName.isEmpty()) {
                nextFolder = EJBLocator.lookupStorageBean().getNamedFolder(ticket, current.getId(), folderName);
                if (nextFolder == null) {
                    nextFolder = new Folder();
                    nextFolder.setName(folderName);
                    nextFolder.setContainerId(folderName);
                    nextFolder = EJBLocator.lookupStorageBean().addChildFolder(ticket, current.getId(), nextFolder);
                    current = nextFolder;
                } else {
                    current = nextFolder;
                }
            }
        }
        return current;
    }

    /**
     * Get / create a document record at the end of a specific path
     */
    public static DocumentRecord getOrCreateDocumentRecordAtPath(Ticket ticket, Folder parentFolder, String fullPath) throws ConnexienceException {
        PathSplitter splitter = new PathSplitter(fullPath);
        String path = splitter.buildPathWithoutLastElement();
        String fileName = splitter.getLastItem();
        Folder createdParent = StorageUtils.getOrCreateFolderPath(ticket, parentFolder, path);
        return getOrCreateDocumentRecord(ticket, createdParent.getId(), fileName);
    }

    /**
     * Calculate the MD5 for a document version
     */
    public static DocumentVersion populateWithMD5(Ticket ticket, DocumentRecord doc, DocumentVersion v) throws ConnexienceException {
        if (doc.getCurrentArchiveStatus() == DocumentRecord.UNARCHIVED_ARCHIVESTATUS) {
            InputStream stream = null;
            try {
                stream = getInputStream(ticket, doc, v);
                String md5 = DigestBuilder.calculateMD5(stream);
                v.setMd5(md5);
                return v;
            } catch (Exception e) {
                throw new ConnexienceException("Error populating DocumentVersion with MD5: " + e.getMessage(), e);
            } finally {
                try {
                    stream.close();
                } catch (Exception ex) {
                }
            }
        } else {
            return v;
        }
    }
}
