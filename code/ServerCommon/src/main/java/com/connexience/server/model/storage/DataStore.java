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
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.document.UncommittedVersion;
import com.connexience.server.util.JSONEditable;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;


/**
 * This is the base class for an object that can act as a data store
 * @author nhgh
 */
public abstract class DataStore extends ServerObject implements Serializable, JSONEditable {
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


    /** Server host */
    //private String server = "localhost";
    
    /** Server port for the storage server */
    //private int port = 8080;
    
    /** Does this store support complete document history deletion */
    protected boolean bulkDeleteSupported = false;
    
    /** Is the size of this store limited */
    protected boolean sizeLimited = false;
    
    /** Can the store return the storage statistics */
    protected boolean spaceReportingSupported = false;
    
    /** Does the store support direct access */
    protected boolean directAccessSupported = false;
    
    /** Does the store support uploading documents with putBlock and commitBlockList */
    protected boolean chunkedUploadSupported = false;

    /** Is read / write enabled for this store */
    protected boolean writeEnabled = false;
    
    /** Creates a new instance of DataStore */
    public DataStore() {
    }

    public boolean isWriteEnabled() {
        return writeEnabled;
    }

    public void setWriteEnabled(boolean writeEnabled) {
        this.writeEnabled = writeEnabled;
    }

    @JsonIgnore
    public boolean isBulkDeleteSupported() {
        return bulkDeleteSupported;
    }
    
    /** Can the store report space */
    @JsonIgnore
    public boolean isSpaceReportingSupported() {
        return spaceReportingSupported;
    }
    
    /** Is space finite */
    @JsonIgnore
    public boolean isSizeLimited() {
        return sizeLimited;
    }

    /** Does this store support direct access */
    @JsonIgnore
    public boolean isDirectAccessSupported() {
        return directAccessSupported;
    }

    @JsonIgnore
    public boolean isChunkedUploadSupported() {
        return chunkedUploadSupported;
    }

    /** Get an InputStream that can be used to read the contents of the document */
    public abstract InputStream getInputStream(DocumentRecord document, DocumentVersion version) throws ConnexienceException;
    
    /** Read a record from a File */
    public abstract DocumentVersion readFromFile(DocumentRecord document, DocumentVersion record, File file) throws ConnexienceException;
    
    /** Read a record from an InputStream */
    public abstract DocumentVersion readFromStream(DocumentRecord document, DocumentVersion record, InputStream stream) throws ConnexienceException;
    
    /** Write a record to an OutputStream */
    public abstract void writeToStream(DocumentRecord document, DocumentVersion record, OutputStream stream) throws ConnexienceException;
    
    /** Write a record to an OutputStream with a size limit */
    public abstract void writeToStream(DocumentRecord document, DocumentVersion record, OutputStream stream, long sizeLimit) throws ConnexienceException;
    
    /** Remove a record */
    public abstract void removeRecord(DocumentRecord document, DocumentVersion record) throws ConnexienceException;    
    
    /** Get the size of record */
    public abstract long getRecordSize(DocumentRecord document, DocumentVersion record) throws ConnexienceException;
    
    /** Remove all versions of a document */
    public abstract void bulkDelete(String organisationId, ArrayList<String> documentIds) throws ConnexienceException;

    /** Upload a chunk of a document */ 
    public abstract void uploadBlock(DocumentRecord document, UncommittedVersion version, int blockId, byte[] blockContent) throws ConnexienceException;

    /** Commit a list of chunk that create a complete document version */
    public abstract DocumentVersion commitBlockList(DocumentRecord document, UncommittedVersion version, List<Integer> blockList, DocumentVersion versionToCommit) throws ConnexienceException;

    /** List blocks ids that the requested version consists of */ 
    public abstract List<Integer> getBlockList(DocumentRecord document, String versionId) throws ConnexienceException;

    public void assertWritable() throws ConnexienceException {
        if(!writeEnabled){
            throw new ConnexienceException("Store is Read-Only");
        }
    }
    
    /** Get the total size of the store if supported */
    @JsonIgnore
    public long getTotalStoreSize() throws ConnexienceException {
        return -1;
    }
    
    /** Get the available storage */
    @JsonIgnore
    public long getAvailableStoreSize() throws ConnexienceException {
        return -1;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = new JSONObject();
        return json;
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("WriteEnabled", writeEnabled);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        writeEnabled = store.booleanValue("WriteEnabled", false);
    }
    
    
}
