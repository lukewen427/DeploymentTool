package com.connexience.server.model.document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.io.Serializable;
import java.util.Date;


/**
 * This class describes a document version that has not yet been committed
 * in the database. For large documents which upload has been split into chunks
 * their document version may become valid only when all chunks have been successfully
 * uploaded and committed in the store. Once committed the version of the document is 
 * moved from this table to the document version table.
 * 
 * There are two main differences between uncommitted version and (committed) document version.
 * First, the uncommitted version has no version number nor md5 sum which are set during version commit, i.e.
 * when the version is moved from the uncommittedversions to documentversion table.
 * Second, the timestamp in the uncommitted version denotes beginning of upload and is used to
 * clean up the storage and database in the case of invalid uploads. Therefore, timestamp
 * is not copied during commit but set to the time of commit.
 * 
 * @author Jacek
 *
 */
public class UncommittedVersion implements Serializable, XmlStorable
{
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
    private static final long serialVersionUID = 3L;


    /** ID of the uncommitted version */
    private String id;

    /** ID of the document record this file belongs to */
    private String documentRecordId;

    /** ID of the User that created the file */
    private String userId;

    /** Timestamp for the file */
    private Date timestamp;

    /** Size of data in bytes */
    private long size = 0;

    /** Comments entered during upload */
    private String comments;

    /** MD5 Hash of the file */
    //private String md5 = null;

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("DocumentVersion");
        store.add("ID", id);
        store.add("DocumentRecordID", documentRecordId);
        store.add("UserID", userId);
        store.add("Timestamp", timestamp);
        store.add("Size", size);
        store.add("Comments", comments);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        id = store.stringValue("ID", null);
        documentRecordId = store.stringValue("DocumentRecordID", null);
        userId = store.stringValue("UserID", null);
        timestamp = store.dateValue("Timestamp", new Date(0L));
        size = store.longValue("Size", 0);
        comments = store.stringValue("Comments", null);
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

    /** Get the ID of the user that created this version */
    public String getUserId() {
        return userId;
    }

    /** Set the ID of the user that created this version */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** Get the modification timestamp of the data */
    public Date getTimestamp() {
        return this.timestamp;
    }

    /** Set the modification timestamp of the data */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
