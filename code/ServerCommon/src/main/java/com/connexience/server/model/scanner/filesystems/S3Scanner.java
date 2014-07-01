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
import com.connexience.server.model.scanner.RemoteFilesystemObject;
import com.connexience.server.model.scanner.RemoteFilesystemScanner;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.security.StoredCredentials;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.credentials.AmazonCredentials;
import com.connexience.server.util.JSONServerObject;
import com.connexience.server.util.PathSplitter;
import com.connexience.server.util.StorageUtils;

import java.util.HashMap;
import java.io.InputStream;
import org.apache.log4j.Logger;

import org.jets3t.service.S3Service;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.json.JSONObject;

/**
 * This class scans an Amazon S3 bucket for data to upload into the system
 * @author hugo
 */
public class S3Scanner extends RemoteFilesystemScanner {
    private static Logger logger = Logger.getLogger(S3Scanner.class);
    /** ID of credentials to use for this scanner */
    private String credentialsId = "";
    
    /** Organisation Bucket Name */
    private String bucketName = "bucket";

    /** Suffix for incoming data */
    private String importedDataBucketSuffix = "imported";
    
    /** Suffix for exported data */
    private String exportedDataBucketSuffix = "exported";
    
    /** Should separate import and export buckets be created */
    private boolean importExportBucketSeparationEnabled = false;
    
    public S3Scanner() {
        setAutoscanEnabled(true);
        setTypeName("AmazonS3");
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    public void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId;
    }
    
    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getImportedDataBucketSuffix() {
        return importedDataBucketSuffix;
    }

    public void setImportedDataBucketSuffix(String importedDataBucketSuffix) {
        this.importedDataBucketSuffix = importedDataBucketSuffix;
    }

    public String getExportedDataBucketSuffix() {
        return exportedDataBucketSuffix;
    }

    public boolean isImportExportBucketSeparationEnabled() {
        return importExportBucketSeparationEnabled;
    }

    public void setImportExportBucketSeparationEnabled(boolean importExportBucketSeparationEnabled) {
        this.importExportBucketSeparationEnabled = importExportBucketSeparationEnabled;
    }
    
    public void setExportedDataBucketSuffix(String exportedDataBucketSuffix) {
        this.exportedDataBucketSuffix = exportedDataBucketSuffix;
    }
    
    @Override
    public void scanForChanges(Ticket ticket) throws ConnexienceException {
        HashMap<String, RemoteFilesystemObject> map = createFilesystemMap(ticket);
        RemoteFilesystemObject fsObj;
        
        S3Service service = getS3Service(ticket);
        S3Bucket bucket = null;
        String compositeBucketName;
        if(importExportBucketSeparationEnabled){
            compositeBucketName = bucketName + "-" + importedDataBucketSuffix;
        } else {
            compositeBucketName = bucketName;
        }
                
        try {
            bucket = service.getOrCreateBucket(compositeBucketName);
        } catch (Exception e){
            throw new ConnexienceException("Error getting bucket: " + compositeBucketName + ": " + e.getMessage(), e);
        }
        
        if(bucket!=null){
            try {
                S3Object[] contents = service.listObjects(compositeBucketName);
                for(S3Object o : contents){
                    if(!map.containsKey(o.getName())){
                        // New entry needed
                        fsObj = new RemoteFilesystemObject();
                        fsObj.setRemotePath(o.getName());
                        PathSplitter splitter = new PathSplitter(o.getName());
                        fsObj.setName(splitter.getLastItem());
                        fsObj.setScannerId(getId());
                        fsObj.setCurrentSize(o.getContentLength());
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
                        fsObj = map.get(o.getName());
                        // Queue for upload if waiting
                        if(fsObj.getStatus().equals(RemoteFilesystemObject.WAITING)){
                            fsObj.setStatus(RemoteFilesystemObject.QUEUED);
                            fsObj.setStatusMessage("");
                            fsObj = EJBLocator.lookupScannerBean().saveFileSystemObjectWithoutSecurity(fsObj);                    
                            EJBLocator.lookupScannerBean().sendUploadMessage(ticket, fsObj.getId());
                        }                        
                    }
                }
                
            } catch (Exception e){
                throw new ConnexienceException("Error processing bucket: " + e.getMessage(), e);
            }
            
        } else {
            throw new ConnexienceException("No bucket");
        }
    }
    
    @Override
    public String importRemoteFile(Ticket ticket, RemoteFilesystemObject fsObject) throws ConnexienceException {
        InputStream inStream = null;
        try {
            S3Service service = getS3Service(ticket);
            String compositeBucketName;
            if(importExportBucketSeparationEnabled){
                compositeBucketName = bucketName + "-" + importedDataBucketSuffix;
            } else {
                compositeBucketName = bucketName;
            }
        
            String blobPath = fsObject.getRemotePath();
            S3Object o = service.getObject(compositeBucketName, blobPath);

            if(o!=null){
                logger.info("Uploading to path: " + blobPath);
                Folder targetFolder = EJBLocator.lookupStorageBean().getFolder(ticket, getTargetFolderId());                
                DocumentRecord doc = StorageUtils.getOrCreateDocumentRecordAtPath(ticket, targetFolder, blobPath);
                inStream = o.getDataInputStream();
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
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        JSONServerObject credentialsJson = new JSONServerObject(AmazonCredentials.class);
        credentialsJson.put("id", credentialsId);
        
        json.put("Credentials", credentialsJson);
        json.put("BucketName", bucketName);
        json.put("ImportBucketSuffix", importedDataBucketSuffix);
        json.put("ExportBucketSuffix", exportedDataBucketSuffix);
        json.put("SeparateImportExportBuckets", importExportBucketSeparationEnabled);
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);

        if(json.has("Credentials")){
            credentialsId = json.getJSONObject("Credentials").getString("id");
        }
        
        if(json.has("BucketName")){
            bucketName = json.getString("BucketName");
        }
        
        if(json.has("ImportBucketSuffix")){
            importedDataBucketSuffix = json.getString("ImportBucketSuffix");
        } else {
            importedDataBucketSuffix = "";
        }
        
        if(json.has("ExportBucketSuffix")){
            exportedDataBucketSuffix = json.getString("ExportBucketSuffix");
        } else {
            exportedDataBucketSuffix = "";
        }
        
        if(json.has("SeparateImportExportBuckets")){
            importExportBucketSeparationEnabled = json.getBoolean("SeparateImportExportBuckets");
        } else {
            importExportBucketSeparationEnabled = false;
        }
    }
    
    /** Create an S3 service object configured with the access keys */
    private S3Service getS3Service(Ticket ticket) throws ConnexienceException {
        try {
            StoredCredentials c = EJBLocator.lookupCredentialsDirectoryBean().getCredentials(ticket, credentialsId);
            if(c instanceof AmazonCredentials){
                AmazonCredentials ac = (AmazonCredentials)c;
                AWSCredentials myCredentials =  new AWSCredentials(ac.getAccessKeyId(), ac.getAccessKey());
                return new RestS3Service(myCredentials);            

            } else {
                throw new Exception("No suitable credentials for scanner");
            }            
        } catch (Exception e){
            throw new ConnexienceException("Error connecting to Amazon S3: " + e.getMessage());
        }
    }    

    @Override
    public void removeRemoteFile(Ticket ticket, RemoteFilesystemObject fsObject) throws ConnexienceException {
        try {
            S3Service service = getS3Service(ticket);
            String compositeBucketName;
            if(importExportBucketSeparationEnabled){
                compositeBucketName = bucketName + "-" + importedDataBucketSuffix;
            } else {
                compositeBucketName = bucketName;
            }
            service.deleteObject(compositeBucketName, fsObject.getRemotePath());
        } catch (Exception e){
            throw new ConnexienceException("Error removing remote file from S3: " + e.getMessage(), e);
        }
    }
}
