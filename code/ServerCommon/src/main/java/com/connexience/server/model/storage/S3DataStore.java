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
import org.jets3t.service.S3Service;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.json.JSONObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a connexience data store that uses Amazon S3 for its back end storage
 * @author hugo
 */
public class S3DataStore extends DataStore {
    /**
     * Class version UID.
     * 
     * Please increment this value whenever your changes may cause 
     * incompatibility with the previous version of this class. If unsure, ask 
     * one of the core development team or read:
     *   http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     *   http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 1L;


    /** Amazon Access Key ID */
    private String accessKeyId = "";
    
    /** Amazon Secret Key ID */
    private String accessKey = "";
    
    /** Organisation Bucket Name */
    private String organisationBucket = "";
    
    /** Location in Europe */
    public static final String EUROPE = S3Bucket.LOCATION_EUROPE;
    
    /** Location in USA */
    public static final String USA = S3Bucket.LOCATION_US;

    public S3DataStore(){
        sizeLimited = false;
        directAccessSupported = true;
        bulkDeleteSupported = false;
        spaceReportingSupported = false;
    }

    /** Get an InputStream that can be used to read the contents of the document */
    public InputStream getInputStream(DocumentRecord document, DocumentVersion version) throws ConnexienceException {
        S3Service service = getS3Service();
        
        // Find the relevant object
        try {
            S3Object fileObject = service.getObject(organisationBucket, version.getId(), null, null, null, null, null, null);
            InputStream inStream = fileObject.getDataInputStream();
            return inStream;
            
        } catch (Exception e){
            throw new ConnexienceException("Error getting S3 data stream: " + e.getMessage());
        }

    }
    
    /** Read a record from a File */
    public DocumentVersion readFromFile(DocumentRecord document, DocumentVersion record, File file) throws ConnexienceException {
        assertWritable();
        
        // Get the S3 Service
        S3Service service = getS3Service();
        
        // Create an object to store the version
        S3Object fileObject = new S3Object(record.getId());
        fileObject.setContentLength(file.length());
        fileObject.setDataInputFile(file);
        record.setSize(file.length());
        
        try {
            service.putObject(organisationBucket, fileObject);
            record.setMd5(DigestBuilder.calculateMD5(file));
        } catch (Exception e){
            throw new ConnexienceException("Error uploading data to S3: " + e.getMessage());
        }
        return record;
    }
    
    /** Read a record from an InputStream */
    public DocumentVersion readFromStream(DocumentRecord document, DocumentVersion record, InputStream stream) throws ConnexienceException {
        assertWritable();
        
        // Get the S3 Service
        S3Service service = getS3Service();

        // Copy the data from the stream to a temporary file
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("esc", "dat");
            StorageUtils.copyStreamToFile(stream, tmpFile);
        } catch (Exception e){
            throw new ConnexienceException("Error creating temporary file: " + e.getMessage(), e);
        }

        // Create an object to store the version
        S3Object fileObject = new S3Object(record.getId());
        fileObject.setContentLength(tmpFile.length());
        fileObject.setDataInputFile(tmpFile);
        
        try {
            service.putObject(organisationBucket, fileObject);
            record.setSize(fileObject.getContentLength());
            record.setMd5(DigestBuilder.calculateMD5(tmpFile));
            return record;
        } catch (Exception e){
            throw new ConnexienceException("Error uploading data to S3: " + e.getMessage());
        } finally {
            if (tmpFile != null) {
                if (!tmpFile.delete()) {
                    tmpFile.deleteOnExit();
                }
            }
        }
    }

    @Override
    public long getRecordSize(DocumentRecord document, DocumentVersion record) throws ConnexienceException {
        // Get the S3 Service
        S3Service service = getS3Service();

        // Find the relevant object
        try {
            S3Object fileObject = service.getObject(organisationBucket, record.getId(), null, null, null, null, null, null);
            return fileObject.getContentLength();
        } catch (Exception e){
            throw new ConnexienceException("Error getting S3 data: " + e.getMessage());
        }
    }
    
    
    /** Write a record to an OutputStream */
    public void writeToStream(DocumentRecord document, DocumentVersion record, OutputStream stream) throws ConnexienceException {
        // Get the S3 Service
        S3Service service = getS3Service();

        // Find the relevant object
        try {
            S3Object fileObject = service.getObject(organisationBucket, record.getId(), null, null, null, null, null, null);
            
            InputStream inStream = fileObject.getDataInputStream();
            byte[] buffer = new byte[4096];
            int len = inStream.read(buffer);
            while(len!=-1){
                stream.write(buffer, 0, len);
                len = inStream.read(buffer);
            }
            stream.flush();
            inStream.close();
            
        } catch (Exception e){
            throw new ConnexienceException("Error getting S3 data: " + e.getMessage());
        }
        
    }
    
    /** Write a record to an OutputStream */
    public void writeToStream(DocumentRecord document, DocumentVersion record, OutputStream stream, long sizeLimit) throws ConnexienceException {
        // Get the S3 Service
        S3Service service = getS3Service();

        // Find the relevant object
        try {
            S3Object fileObject = service.getObject(organisationBucket, record.getId(), null, null, null, null, null, null);
            
            InputStream inStream = fileObject.getDataInputStream();
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
            throw new ConnexienceException("Error getting S3 data: " + e.getMessage());
        }
        
    }
    
    /** Remove a record */
    public void removeRecord(DocumentRecord document, DocumentVersion record) throws ConnexienceException {
        assertWritable();
        
        // Get the S3 Service
        S3Service service = getS3Service();
        try {
            service.deleteObject(organisationBucket, record.getId());
        } catch (Exception e){
            throw new ConnexienceException("Error deleting S3 data: " + e.getMessage());
        }
    }

    @Override
    public void bulkDelete(String organisationId, ArrayList<String> documentIds) throws ConnexienceException {
        assertWritable();
        throw new UnsupportedOperationException("Not supported yet.");
    }   
    

    /** Create an S3 service object configured with the access keys */
    private S3Service getS3Service() throws ConnexienceException {
        try {
            AWSCredentials myCredentials =  new AWSCredentials(accessKeyId, accessKey);
            return new RestS3Service(myCredentials);            
        } catch (Exception e){
            throw new ConnexienceException("Error connecting to Amazon S3: " + e.getMessage());
        }
    }
    
    /** Try and create a new S3 Bucket. A ConnexienceException is thrown if this
     * cannot be done, whilst the bucket name will be returned as a string if it
     * can be done.*/
    public String createBucket(String bucketName, String location) throws ConnexienceException {
        S3Service service = getS3Service();
        try {
            S3Bucket bucket = service.createBucket(bucketName, location);
            if(bucket!=null){
                return bucket.getName();
            } else {
                throw new ConnexienceException("Cannot create bucket: " + bucketName);
            }
        } catch (Exception e){
            throw new ConnexienceException("Cannot create bucket: " + e.getMessage());
        }
    } 
    
    /** Set the Amazon ID of the access Key */
    public String getAccessKeyId() {
        return accessKeyId;
    }
    
    /** Get the Amazon ID of the access Key */
    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    /** Get the private access Key */
    public String getAccessKey() {
        return accessKey;
    }

    /** Set the private access Key */
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    /** Get the name of the Amazon bucket that stores the organisation data */
    public String getOrganisationBucket() {
        return organisationBucket;
    }

    /** Set the name of the Amazon bucket that stores the organisation data */
    public void setOrganisationBucket(String organisationBucket) {
        this.organisationBucket = organisationBucket;
    }
    
    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("AccessKeyID", accessKeyId);
        json.put("AccessKey", accessKey);
        json.put("BucketName", organisationBucket);
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
        accessKey = json.getString("AccessKey");
        accessKeyId = json.getString("AccessKeyID");
        organisationBucket = json.getString("BucketName");
    }    

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("AccessKey", accessKey);
        store.add("AccessKeyID", accessKeyId);
        store.add("OrganisationBucket", organisationBucket);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        accessKey = store.stringValue("AccessKey", "");
        accessKeyId = store.stringValue("AccessKeyID", "");
        organisationBucket = store.stringValue("OrganisationBucket", "");
    }


    @Override
    public void uploadBlock(DocumentRecord document, UncommittedVersion version, int blockId, byte[] blockContent)
    throws ConnexienceException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocumentVersion commitBlockList(DocumentRecord document, UncommittedVersion version, List<Integer> blockList, DocumentVersion versionToCommit)
    throws ConnexienceException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Integer> getBlockList(DocumentRecord document, String versionId)
    throws ConnexienceException
    {
        throw new UnsupportedOperationException();
    }
}
