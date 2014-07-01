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
package com.connexience.server.model.scanner.filesystems;

import com.connexience.server.ConnexienceException;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.scanner.RemoteFilesystemObject;
import com.connexience.server.model.scanner.RemoteFilesystemScanner;
import com.connexience.server.model.security.StoredCredentials;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.credentials.AzureCredentials;
import com.connexience.server.util.JSONServerObject;
import com.connexience.server.util.PathSplitter;
import com.connexience.server.util.StorageUtils;
import com.connexience.server.util.ZipUtils;
import com.microsoft.windowsazure.services.blob.client.*;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;
import java.io.InputStream;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * This class can scan an Azure blob store container for changes and import files
 * into the local system.
 * @author hugo
 */
public class AzureScanner extends RemoteFilesystemScanner {
    private static Logger logger = Logger.getLogger(AzureScanner.class);

    /** ID of credentials to use for this scanner */
    private String credentialsId = "";
    
    /** Name of the Azure container to scan */
    private String containerName = "container";

    /** Suffix for incoming data */
    private String importedDataContainerSuffix = "imported";
    
    /** Suffix for exported data */
    private String exportedDataContainerSuffix = "exported";
    
    /** Should separate import and export buckets be created */
    private boolean importExportContainerSeparationEnabled = false;
    
    /** Container */
    private transient CloudBlobContainer container;
    
    public AzureScanner() {
        setAutoscanEnabled(true);
        setTypeName("Azure");
    }
    
    @Override
    public void scanForChanges(Ticket ticket) throws ConnexienceException {
        // Get the upload state from the database and put them into hashmap
        HashMap<String, RemoteFilesystemObject> map = createFilesystemMap(ticket);
        RemoteFilesystemObject fsObj;
        
        CloudBlobContainer c = getContainer(ticket);
        CloudBlockBlob blob;
        
        Iterable<ListBlobItem> items = c.listBlobs();
        for(ListBlobItem i : items){

            try {
                blob = c.getBlockBlobReference(i.getUri().toString());
                if(!map.containsKey(blob.getName())){
                    // New entry needed
                    fsObj = new RemoteFilesystemObject();
                    fsObj.setRemotePath(blob.getName());
                    PathSplitter splitter = new PathSplitter(blob.getName());
                    fsObj.setName(splitter.getLastItem());
                    fsObj.setScannerId(getId());
                    fsObj.setCurrentSize(blob.getProperties().getLength());
                    fsObj.setStatus(RemoteFilesystemObject.WAITING);
                    fsObj.setStable(true);
                    fsObj.setStatusMessage("");
                    fsObj = EJBLocator.lookupScannerBean().saveFileSystemObjectWithoutSecurity(fsObj);
                    
                    fsObj.setStatus(RemoteFilesystemObject.QUEUED);
                    fsObj.setStatusMessage("");
                    fsObj = EJBLocator.lookupScannerBean().saveFileSystemObjectWithoutSecurity(fsObj);

                    // Send to the uploader
                    EJBLocator.lookupScannerBean().sendUploadMessage(ticket, fsObj.getId());
                } else {
                    fsObj = map.get(blob.getName());
                    // Queue for upload if waiting
                    if(fsObj.getStatus().equals(RemoteFilesystemObject.WAITING)){
                        fsObj.setStatus(RemoteFilesystemObject.QUEUED);
                        fsObj.setStatusMessage("");
                        fsObj = EJBLocator.lookupScannerBean().saveFileSystemObjectWithoutSecurity(fsObj);                    
                        EJBLocator.lookupScannerBean().sendUploadMessage(ticket, fsObj.getId());
                    }
                }
                
            } catch (Exception e){
                logger.error("Error processing blob: " + e.getMessage());
            }
        }
    }
    
    @Override
    public String importRemoteFile(Ticket ticket, RemoteFilesystemObject fsObject) throws ConnexienceException {
        InputStream inStream = null;
        try {
            CloudBlobContainer c = getContainer(ticket);
            String blobPath = fsObject.getRemotePath();
            CloudBlockBlob blob = c.getBlockBlobReference(blobPath);

            //Set the blob to check MD5 checksums during download
            BlobRequestOptions options = new BlobRequestOptions();
            options.setUseTransactionalContentMD5(true);
            options.setStoreBlobContentMD5(true);

            if(blob!=null){
                logger.info("Uploading to path: " + blobPath);
                Folder targetFolder = EJBLocator.lookupStorageBean().getFolder(ticket, getTargetFolderId());                
                DocumentRecord doc = StorageUtils.getOrCreateDocumentRecordAtPath(ticket, targetFolder, blobPath);
                inStream = blob.openInputStream(null, options, null);
                DocumentVersion v = StorageUtils.upload(ticket, inStream, doc, "Uploaded by scanner");
                fsObject.setStatus(RemoteFilesystemObject.UPLOADED);
                fsObject.setStatusMessage("");
                fsObject.setLocalFileId(doc.getId());
                fsObject.setCurrentSize(v.getSize());
                fsObject.setStable(true);
                fsObject = EJBLocator.lookupScannerBean().saveFileSystemObjectWithoutSecurity(fsObject);        
                return doc.getId();
            } else {
                throw new ConnexienceException("No such blob");
            }
            
        } catch (Exception e){
            fsObject.setStatus(RemoteFilesystemObject.UPLOAD_ERROR);
            fsObject.setStatusMessage(e.getMessage());
            try {
                fsObject = (RemoteFilesystemObject)EJBLocator.lookupScannerBean().saveFileSystemObjectWithoutSecurity(fsObject);
            } catch(Exception ex){}
            
            throw new ConnexienceException("Error importing file: " + e.getMessage());
            
        } finally {
            try {
                inStream.close();
            } catch (Exception ex){}
        }
    }

    @Override
    public void removeRemoteFile(Ticket ticket, RemoteFilesystemObject fsObject) throws ConnexienceException {
        try {
            CloudBlobContainer c = getContainer(ticket);
            CloudBlockBlob blob = c.getBlockBlobReference(fsObject.getRemotePath());
            if(blob!=null && blob.exists()){
                blob.delete();
            } else {
                throw new ConnexienceException("Could not access blob");
            }
        } catch (Exception e){
            throw new ConnexienceException("Error removing Azure blob: " + e.getMessage(), e);
        }
    }
    
    /** Get a connection to the relevant container */
    private CloudBlobContainer getContainer(Ticket ticket) throws ConnexienceException {
        if(container==null){
            // Setup the account
            CloudStorageAccount account = null;
            try {
                StoredCredentials c = EJBLocator.lookupCredentialsDirectoryBean().getCredentials(ticket, credentialsId);
                if(c instanceof AzureCredentials){
                    AzureCredentials ac = (AzureCredentials)c;
                    String storageConnectionString = "DefaultEndpointsProtocol=http;AccountName=" + ac.getAccountName() + ";AccountKey=" + ac.getAccountKey();        

                    /** Account to use */
                    account = CloudStorageAccount.parse(storageConnectionString);
                } else {
                    throw new Exception("No suitable credentials for scanner");
                }
            } catch (Exception e){
                throw new ConnexienceException("Error setting up cloud storage account: " + e.getMessage(), e);
            }

            // Setup the storage container
            if(account!=null){
                CloudBlobClient client = account.createCloudBlobClient();
                try {
                    if(importExportContainerSeparationEnabled){
                        container = client.getContainerReference(containerName + "-" + importedDataContainerSuffix);
                    } else {
                        container = client.getContainerReference(containerName);
                    }
                    
                    container.createIfNotExist();
                    return container;            
                } catch(Exception e){
                    container = null;
                    throw new ConnexienceException("Error accessing blob container: " + e.getMessage(), e);
                }
            } else {
                throw new ConnexienceException("No cloud storage account created");
            }
            
        } else {
            return container;
        }
    }

    public void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId;
    }
   
    public String getCredentialsId() {
        return credentialsId;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public void setImportedDataContainerSuffix(String importedDataContainerSuffix) {
        this.importedDataContainerSuffix = importedDataContainerSuffix;
    }

    public String getImportedDataContainerSuffix() {
        return importedDataContainerSuffix;
    }

    public void setExportedDataContainerSuffix(String exportedDataContainerSuffix) {
        this.exportedDataContainerSuffix = exportedDataContainerSuffix;
    }

    public String getExportedDataContainerSuffix() {
        return exportedDataContainerSuffix;
    }

    public boolean isImportExportContainerSeparationEnabled() {
        return importExportContainerSeparationEnabled;
    }

    public void setImportExportContainerSeparationEnabled(boolean importExportContainerSeparationEnabled) {
        this.importExportContainerSeparationEnabled = importExportContainerSeparationEnabled;
    }
    
    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("ContainerName", containerName);
        
        JSONServerObject credentialsJson = new JSONServerObject(AzureCredentials.class);
        credentialsJson.put("id", credentialsId);
        
        json.put("Credentials", credentialsJson);
        
        json.put("ImportContainerSuffix", importedDataContainerSuffix);
        json.put("ExportContainerSuffix", exportedDataContainerSuffix);
        json.put("SeparateImportExportContainers", importExportContainerSeparationEnabled);
        
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
        if(json.has("Credentials")){
            credentialsId = json.getJSONObject("Credentials").getString("id");
        }
        
        if(json.has("ContainerName")){
            containerName = json.getString("ContainerName").trim();
        }
        
        if(json.has("ImportContainerSuffix")){
            importedDataContainerSuffix = json.getString("ImportContainerSuffix");
        } else {
            importedDataContainerSuffix = "";
        }
        
        if(json.has("ExportContainerSuffix")){
            exportedDataContainerSuffix = json.getString("ExportContainerSuffix");
        } else {
            exportedDataContainerSuffix = "";
        }
        
        if(json.has("SeparateImportExportContainers")){
            importExportContainerSeparationEnabled = json.getBoolean("SeparateImportExportContainers");
        } else {
            importExportContainerSeparationEnabled = false;
        }
    }
}