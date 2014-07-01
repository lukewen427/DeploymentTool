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
package com.connexience.server.model.storage;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.document.UncommittedVersion;
import com.connexience.server.util.DigestBuilder;
import com.connexience.server.util.StorageUtils;
import com.microsoft.windowsazure.services.blob.client.*;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import org.codehaus.jackson.annotate.JsonIgnore;
//import org.json.JSONObject;
//import org.pipeline.core.xmlstorage.XmlDataStore;
//import org.pipeline.core.xmlstorage.XmlStorageException;


/**
 * This class provides a windows Azure blob store storage driver for e-SC.
 * @author hugo
 */
public class AzureBlobStore extends DataStore {
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(AzureBlobStore.class);
    /** Name of the storage account to use */
    private String accountName = "";

    /** Key of the storage account */
    private String accountKey = "";

    public AzureBlobStore() {
        directAccessSupported = true;
        bulkDeleteSupported = true;
        spaceReportingSupported = false;
        sizeLimited = false;
        chunkedUploadSupported = true;
    }

    /** Get the container reference */
    @JsonIgnore
    private CloudBlobContainer getContainer(String orgId) throws ConnexienceException {
        // Setup the account
        CloudStorageAccount account = null;
        try {
            String storageConnectionString = "DefaultEndpointsProtocol=http;AccountName=" + accountName + ";AccountKey=" + accountKey;        

            /** Account to use */
            account = CloudStorageAccount.parse(storageConnectionString);
        } catch (Exception e){
            throw new ConnexienceException("Error setting up cloud storage account: " + e.getMessage(), e);
        }

        // Setup the storage container
        if(account!=null){
            CloudBlobClient client = account.createCloudBlobClient();
            try {
                CloudBlobContainer container = client.getContainerReference("org-" + orgId);
                container.createIfNotExist();
                return container;            
            } catch(Exception e){
                throw new ConnexienceException("Error accessing blob container: " + e.getMessage(), e);
            }
        } else {
            throw new ConnexienceException("No cloud storage account created");
        }
    }

    @Override
    @JsonIgnore
    public InputStream getInputStream(DocumentRecord document, DocumentVersion version) throws ConnexienceException {
        CloudBlobContainer container = getContainer(document.getOrganisationId());
        if(container!=null){
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(document.getId() + "/" + version.getId());
                return blob.openInputStream();
            } catch (Exception e){
                throw new ConnexienceException("Error accessing stored blob: " + e.getMessage(), e);
            }
        } else {
            throw new ConnexienceException("No container available");
        }
    }

    @Override
    public DocumentVersion readFromFile(DocumentRecord document, DocumentVersion record, File file) throws ConnexienceException {
        assertWritable();
        CloudBlobContainer container = getContainer(document.getOrganisationId());
        FileInputStream inStream = null;
        if(container!=null){
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(document.getId() + "/" + record.getId());
                
                inStream = new FileInputStream(file);
                blob.upload(inStream, file.length());
                record.setSize(file.length());
                record.setMd5(DigestBuilder.calculateMD5(file));
                return record;
            } catch (Exception e){
                throw new ConnexienceException("Error access stored blob: " + e.getMessage(), e);
            } finally {
                if(inStream!=null){
                    try {
                        inStream.close();
                    } catch (Exception e){
                        logger.error("Error closing input stream in readFromFile: " + e.getMessage());
                    }
                }
            }
        } else {
            throw new ConnexienceException("No container available");
        }
    }

    @Override
    public DocumentVersion readFromStream(DocumentRecord document, DocumentVersion record, InputStream stream) throws ConnexienceException {
        assertWritable();
        // Get the container
        CloudBlobContainer container = getContainer(document.getOrganisationId());
        FileInputStream inStream = null;
        if(container!=null){
            // Copy the data from the stream to a temporary file
            File tmpFile = null;
            try {
                tmpFile = File.createTempFile("esc", "dat");
                StorageUtils.copyStreamToFile(stream, tmpFile);
            } catch (Exception e){
                throw new ConnexienceException("Error creating temporary file: " + e.getMessage(), e);
            }

            try {
                CloudBlockBlob blob = container.getBlockBlobReference(document.getId() + "/" + record.getId());
                inStream = new FileInputStream(tmpFile);
                blob.upload(inStream, tmpFile.length());
                record.setSize(tmpFile.length());
                record.setMd5(DigestBuilder.calculateMD5(tmpFile));
                return record;
            } catch (Exception e){
                throw new ConnexienceException("Error uploading data to Blob Store: " + e.getMessage());
            } finally {
                if(inStream!=null){
                    try {
                        inStream.close();
                    } catch (Exception e){
                        logger.error("Error closing input stream in readFromStream: " + e.getMessage());
                    }
                } 

                if (tmpFile != null) {
                    if (!tmpFile.delete()) {
                        tmpFile.deleteOnExit();
                    }
                }
            }
        } else {
            throw new ConnexienceException("No container available");
        }
    }

    @Override
    public void writeToStream(DocumentRecord document, DocumentVersion record, OutputStream stream) throws ConnexienceException {
        CloudBlobContainer container = getContainer(document.getOrganisationId());
        if(container!=null){
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(document.getId() + "/" + record.getId());
                blob.download(stream);
                stream.flush();
            } catch (Exception e){
                throw new ConnexienceException("Error accessing stored blob: " + e.getMessage(), e);
            }
        } else {
            throw new ConnexienceException("No container available");
        }
    }

    @Override
    public void writeToStream(DocumentRecord document, DocumentVersion record, OutputStream stream, long sizeLimit) throws ConnexienceException {
        CloudBlobContainer container = getContainer(document.getOrganisationId());
        InputStream inStream = null;
        if(container!=null){
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(document.getId() + "/" + record.getId());
                inStream = blob.openInputStream();
                byte[] buffer = new byte[4096];
                int len = inStream.read(buffer);
                long totalBytes = 0L;
                while(len!=-1 && totalBytes<sizeLimit){
                    stream.write(buffer, 0, len);
                    len = inStream.read(buffer);
                    totalBytes = totalBytes + len;
                }
                stream.flush();
                inStream.close();
            } catch (Exception e){
                throw new ConnexienceException("Error accessing stored blob: " + e.getMessage(), e);
            } finally {
                if(inStream!=null){
                    try {
                        inStream.close();
                    } catch (Exception e){
                        logger.error("Error closing input stream in writeToStream: " + e.getMessage());
                    }
                }
            }
        } else {
            throw new ConnexienceException("No container available");
        }
    }

    @Override
    public void removeRecord(DocumentRecord document, DocumentVersion record) throws ConnexienceException {
        assertWritable();
        CloudBlobContainer container = getContainer(document.getOrganisationId());
        if(container!=null){
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(document.getId() + "/" + record.getId());
                blob.delete();
            } catch (Exception e){
                throw new ConnexienceException("Error accessing stored blob: " + e.getMessage(), e);
            }
        } else {
            throw new ConnexienceException("No container available");
        }
    }

    @Override
    @JsonIgnore
    public long getRecordSize(DocumentRecord document, DocumentVersion record) throws ConnexienceException {
        CloudBlobContainer container = getContainer(document.getOrganisationId());
        if(container!=null){
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(document.getId() + "/" + record.getId());
                return blob.getProperties().getLength();
            } catch (Exception e){
                throw new ConnexienceException("Error accessing stored blob: " + e.getMessage(), e);
            }
        } else {
            throw new ConnexienceException("No container available");
        }
    }

    @Override
    public void bulkDelete(String organisationId, ArrayList<String> documentIds) throws ConnexienceException {
        assertWritable();
        CloudBlobContainer container = getContainer(organisationId);
        if(container!=null){
            try {
                String id;
                CloudBlobDirectory dir;
                ListBlobItem item;
                for(int i=0;i<documentIds.size();i++){
                    id = documentIds.get(i);
                    try {
                        dir = container.getDirectoryReference(id + "/");
                        Iterator<ListBlobItem> contents = dir.listBlobs().iterator();
                        while(contents.hasNext()){
                            item = contents.next();
                            if(item instanceof CloudBlockBlob){
                                ((CloudBlockBlob)item).delete();
                            }
                        }

                    } catch (Exception e){
                        logger.error("Error bulk deleting: " + id + ": " + e.getMessage());
                    }
                }
            } catch (Exception e){
                throw new ConnexienceException("Error accessing stored blob: " + e.getMessage(), e);
            }
        } else {
            throw new ConnexienceException("No container available");
        }
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public String getAccountName() {
        return accountName;
    }

    public static void main(String[] args){
        try {
            AzureBlobStore bs = new AzureBlobStore();
            bs.setAccountName("inkspotunilever");
            bs.setAccountKey("+lVLhgqAfphVtkjjweaJEpbk/t51Z/bqYrQBDom1SU0jWDuVQ1PraP9EPjFv1Qjoc4rTW5PPUPIHuAabuIDclQ==");
            CloudBlobContainer container = bs.getContainer("002");
            Iterable<ListBlobItem> b = container.listBlobs("003/");
            Iterator<ListBlobItem> i = b.iterator();
            while(i.hasNext()){
                ListBlobItem t = i.next();
                System.out.println(t.getClass().getName() + t.getUri());
            }

            /*
             DocumentRecord doc = new DocumentRecord();
             doc.setOrganisationId("002");
             doc.setId("003");
             doc.setName("data.csv");

             DocumentVersion version = new DocumentVersion();
             version.setId("004");
             version.setDocumentRecordId(doc.getId());
             version.setVersionNumber(0);
             version.setTimestampDate(new Date());

             File f = new File("/Users/hugo/data.csv");
             version = bs.readFromFile(doc, version, f);
             System.out.println("Version size: " + version.getSize());
             */

            ArrayList<String> ids = new ArrayList<>();
            ids.add("003");
            bs.bulkDelete("002", ids);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("AccountName", accountName);
        json.put("AccountKey", accountKey);
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
        accountName = json.getString("AccountName");
        accountKey = json.getString("AccountKey");
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("AccountName", accountName);
        store.add("AccountKey", accountKey);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        accountName = store.stringValue("AccountName", "");
        accountKey = store.stringValue("AccountKey", "");
    }


    @Override 
    public void uploadBlock(DocumentRecord document, UncommittedVersion record, int blockId, byte[] blockContent) throws ConnexienceException
    {
        assertWritable();
        CloudBlobContainer container = getContainer(document.getOrganisationId());
        if (container != null) {
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(document.getId() + "/" + record.getId());
                // Azure requires that all block IDs for a blob need to be of the same length,
                // therefore, we take the blockId number and format it accordingly.
                blob.uploadBlock(String.format("%08x", blockId), new ByteArrayInputStream(blockContent), blockContent.length);
            } catch (Exception e){
                throw new ConnexienceException("Error accessing stored blob: " + e.getMessage(), e);
            }
        } else {
            throw new ConnexienceException("No container available");
        }
    };


    @Override
    public DocumentVersion commitBlockList(DocumentRecord document, UncommittedVersion record, List<Integer> blockList, DocumentVersion committedVersion) throws ConnexienceException
    {
        assertWritable();
        CloudBlobContainer container = getContainer(document.getOrganisationId());
        if (container != null) {
            CloudBlockBlob blob;
            try {
                blob = container.getBlockBlobReference(document.getId() + "/" + record.getId());
                ArrayList<BlockEntry> azureBlockList = new ArrayList<>();
                for (Integer b : blockList) {
                    azureBlockList.add(new BlockEntry(String.format("%08x", b), BlockSearchMode.LATEST));
                }
                blob.commitBlockList(azureBlockList);

                CloudBlockBlob committedBlob = container.getBlockBlobReference(document.getId() + "/" + committedVersion.getId());
                committedBlob.copyFromBlob(blob);
                committedBlob.downloadAttributes();
                committedVersion.setSize(committedBlob.getProperties().getLength());
                return committedVersion;
            } catch (Exception e){
                throw new ConnexienceException("Error accessing stored blob: " + e.getMessage(), e);
            }
        } else {
            throw new ConnexienceException("No container available");
        }
    }


    @Override
    public List<Integer> getBlockList(DocumentRecord document, String versionId) throws ConnexienceException
    {
        CloudBlobContainer container = getContainer(document.getOrganisationId());
        if (container != null) {
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(document.getId() + "/" + versionId);
                ArrayList<BlockEntry> azureBlockList = blob.downloadBlockList(BlockListingFilter.ALL, null, null, null);
                ArrayList<Integer> blockList = new ArrayList<>();
                for (BlockEntry e : azureBlockList) {
                    blockList.add(Integer.parseInt(e.getId(), 16));
                }
                return blockList;
            } catch (Exception e){
                throw new ConnexienceException("Error accessing stored blob: " + e.getMessage(), e);
            }
        } else {
            throw new ConnexienceException("No container available");
        }
    }
}