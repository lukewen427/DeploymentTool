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

import com.connexience.server.model.ServerObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.io.Serializable;
/**
 * This class contains a record of a document stored in the server. The actual
 * document data is retrieved from the data servlet using the id from this
 * record.
 * @author hugo
 */
public class DocumentRecord extends ServerObject implements Serializable {
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
    /** Unknown - Archive Status */
    public final static int UNKNOWN_ARCHIVESTATUS = -1;

    /** Unarchived - Archive Status */
    public final static int UNARCHIVED_ARCHIVESTATUS = 0;

    /** Archiving - Archive Status */
    public final static int ARCHIVING_ARCHIVESTATUS = 1;

    /** Unarchiving - Archive Status */
    public final static int UNARCHIVING_ARCHIVESTATUS = 2;

    /** Archived - Archive Status */
    public final static int ARCHIVED_ARCHIVESTATUS = 3;

    /** Archiving Error - Archive Status */
    public final static int ARCHIVING_ERROR_ARCHIVESTATUS = 4;

    /** Unarchiving Error - Archive Status */
    public final static int UNARCHIVING_ERROR_ARCHIVESTATUS = 5;

    /** Archived Error - Archive Status */
    public final static int ARCHIVED_ERROR_ARCHIVESTATUS = 6;
    
    /** ID of the document type for this record */
    private String documentTypeId;
    
    /** Does this record support versioning */
    private boolean versioned = true;
    
    /** Set the maximum number of previous versions */
    private int maxVersions = 10;
    
    /** Limit the maximum number of previous versions */
    private boolean limitVersions = false;
    
    /** Current version number */
    private int currentVersionNumber = 0;

    /** Current document size **/
    private long currentVersionSize = 0;
    
    /** Current archive status **/
    private int currentArchiveStatus = UNARCHIVED_ARCHIVESTATUS; // UNKNOWN_ARCHIVESTATUS;

    /** Creates a new instance of DocumentRecord */
    public DocumentRecord() {
        super();
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("DocumentTypeID", documentTypeId);
        store.add("Versioned", versioned);
        store.add("MaxVersions", maxVersions);
        store.add("LimitVersions", limitVersions);
        store.add("CurrentVersionNumber", currentVersionNumber);
        store.add("CurrentVersionSize", currentVersionSize);
        store.add("CurrentArchiveStatus", currentArchiveStatus);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        documentTypeId = store.stringValue("DocumentTypeID", null);
        versioned = store.booleanValue("Versioned", true);
        maxVersions = store.intValue("MaxVersions", 10);
        limitVersions = store.booleanValue("LimitVersions", false);
        currentVersionNumber = store.intValue("CurrentVersionNumber", 0);
        currentVersionSize = store.intValue("CurrentVersionSize", 0);
        currentArchiveStatus = store.intValue("CurrentArchiveStatus", UNKNOWN_ARCHIVESTATUS);
    } 
    
    /** Populate an object with fields from this one */
    public void populateCopy(DocumentRecord doc){
        super.populateCopy(doc);
        doc.setVersioned(true);
        doc.setLimitVersions(limitVersions);
        doc.setDocumentTypeId(documentTypeId);
        doc.setCurrentArchiveStatus(currentArchiveStatus);
    }
  
    /** Get the id of the document type */
    public String getDocumentTypeId() {
        return documentTypeId;
    }

    /** Set the id of the document type */
    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    /** Is this document versioned in the data store */
    public boolean isVersioned() {
        return versioned;
    }

    /** Set whether this document versioned in the data store */
    public void setVersioned(boolean versioned) {
        this.versioned = versioned;
    }

    /** Get the maximum number of versions allowed in the data store */
    public int getMaxVersions() {
        return maxVersions;
    }

    /** Set the maximum number of versions allowed in the data store */
    public void setMaxVersions(int maxVersions) {
        this.maxVersions = maxVersions;
    }

    /** Is there an upper limit on the number of versions */
    public boolean isLimitVersions() {
        return limitVersions;
    }

    /** Set whether there is an upper limit on the number of versions */
    public void setLimitVersions(boolean limitVersions) {
        this.limitVersions = limitVersions;
    }

    /** Get the current version number */
    public int getCurrentVersionNumber() {
        return currentVersionNumber;
    }

    /** Set the current version number */
    public void setCurrentVersionNumber(int currentVersionNumber) {
        this.currentVersionNumber = currentVersionNumber;
    }

    /** Get the size of the current version */
    public long getCurrentVersionSize() {
        return currentVersionSize;
    }
    /** Set the size of the current version */
    public void setCurrentVersionSize(long currentVersionSize) {
        this.currentVersionSize = currentVersionSize;
    }

    /** Get the current archive status */
    public int getCurrentArchiveStatus() {
        return currentArchiveStatus;
    }

    /** Get the current archive status */
    @JsonIgnore
    public String getCurrentArchiveStatusAsString() {
        switch (currentArchiveStatus)
        {
            case UNKNOWN_ARCHIVESTATUS:
                return "Unknown";
            case UNARCHIVED_ARCHIVESTATUS:
                return "-";
            case ARCHIVING_ARCHIVESTATUS:
                return "Archiving";
            case UNARCHIVING_ARCHIVESTATUS:
                return "Unarchiving";
            case ARCHIVED_ARCHIVESTATUS:
                return "Archived";
            case ARCHIVING_ERROR_ARCHIVESTATUS:
                return "Archiving Error";
            case UNARCHIVING_ERROR_ARCHIVESTATUS:
                return "Unarchiving Error";
            case ARCHIVED_ERROR_ARCHIVESTATUS:
                return "Archived Error";
            default:
                return "Very Unknown";
        }
    }

    /** Set the current archive status */
    public void setCurrentArchiveStatus(int currentArchiveStatus) {
        this.currentArchiveStatus = currentArchiveStatus;
    }

    /** Try and get the document extension */
    @JsonIgnore
    public String getExtension() {
        String filename = this.getName();
        int lastDotIdx = filename.lastIndexOf(".");
        if (lastDotIdx > 0 && lastDotIdx < filename.length() - 1) {
            return filename.substring(lastDotIdx + 1).trim();
        }
        return null;
    }

}