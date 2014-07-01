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
package com.connexience.server.model.document;

import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.io.Serializable;
import java.util.Date;

/**
 * This class represents a single version of a file in the repository. This class
 * contains the ID of the document record that it relates to, its version number,
 * the ID of the underlying data file in the storage system, the id of the User 
 * that modified the data, a timestamp and a signature stored as a byte array
 * which can be verified using the certificate of the user that saved the file.
 * @author nhgh
 */
public class DocumentVersion implements Comparable<DocumentVersion>, Serializable, XmlStorable {    
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
    private static final long serialVersionUID = 2L;


    /** Database ID of the version. This the same as the ID of the stored file */
    private String id;
    
    /** ID of the document record this file belongs to */
    private String documentRecordId;
    
    /** Version number of the file */
    private int versionNumber;
    
    /** ID of the User that created the file */
    private String userId;
    
    /** Timestamp for the file */
    private long timestamp;
    
    /** Size of data in bytes */
    private long size = 0;

    /** Comments entered during upload */
    private String comments;
    
    /** MD5 Hash of the file */
    private String md5 = null;

    /** Creates a new instance of DocumentVersion */
    public DocumentVersion() {
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("DocumentVersion");
        store.add("ID", id);
        store.add("DocumentRecordID", documentRecordId);
        store.add("VersionNumber", versionNumber);
        store.add("UserID", userId);
        store.add("Timestamp", timestamp);
        store.add("Size", size);
        store.add("Comments", comments);
        if(md5!=null && !md5.isEmpty()){
            store.add("MD5", md5);
        }
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        id = store.stringValue("ID", null);
        documentRecordId = store.stringValue("DocumentRecordID", null);
        versionNumber = store.intValue("VersionNumber", 0);
        userId = store.stringValue("UserID", null);
        timestamp = store.longValue("Timestamp", 0);
        size = store.longValue("Size", 0);
        comments = store.stringValue("Comments", null);
        if(store.containsName("MD5")){
            md5 = store.stringValue("MD5", null);
        } else {
            md5 = null;
        }
    }
    
    
    /** Set the upload comments for this version */
    public void setComments(String comments){
        this.comments = comments;
    }
    
    /** Set the upload comments for this version */
    public String getComments(){
        return comments;
    }
    
    /** Get the size in bytes */
    public long getSize(){
        return size;
    }
    
    /** Set the size in bytes */
    public void setSize(long size){
        this.size = size;
    }
    
    /** Get the database id of this version */
    public String getId() {
        return id;
    }

    /** Set the database id of this version */
    public void setId(String id) {
        this.id = id;
    }

    /** Get the id of the associated DocumentRecord */
    public String getDocumentRecordId() {
        return documentRecordId;
    }

    /** Set the id of the associated DocumentRecord */
    public void setDocumentRecordId(String documentRecordId) {
        this.documentRecordId = documentRecordId;
    }

    /** Get the version number of this document */
    public int getVersionNumber() {
        return versionNumber;
    }

    /** Set the version number of this document */
    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    /** Get the ID of the user that created this version */
    public String getUserId() {
        return userId;
    }

    /** Set the ID of the user that created this version */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** Get the modification timestamp of the data */
    public Date getTimestampDate() {
        return new java.util.Date(timestamp);
    }

    /** Set the modification timestamp of the data */
    public void setTimestampDate(Date timestamp) {
        this.timestamp = timestamp.getTime();
    }

    /** Get the timestamp as a Long */
    public long getTimestamp(){
        return timestamp;
    }
    
    /** Set the timestamp as a Long */
    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }

    /** Get the MD5 hash value of the record */
    public String getMd5(){
        return md5;
    }
    
    /** Set the MD5 hash value of the record */
    public void setMd5(String md5){
        this.md5 = md5;
    }
    
    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(DocumentVersion o) {
        int version = ((DocumentVersion)o).getVersionNumber();
        if(version>getVersionNumber()){
            return -1;
        } else if(version<getVersionNumber()){
            return 1;
        } else {
            return 0;
        }
    }
}