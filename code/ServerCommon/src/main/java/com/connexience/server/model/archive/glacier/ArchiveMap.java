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
package com.connexience.server.model.archive.glacier;

import java.io.Serializable;

/**
 * This class represents the present state of AWS Glacier Archives.
 * 
 * @author swheater
 */
public class ArchiveMap implements Serializable {
    public static final int UNKNOWN_STATUS      = -1;
    public static final int UPLOADING_STATUS    = 0;
    public static final int UPLOADED_STATUS     = 1;
    public static final int DOWNLOADING_STATUS  = 2;
    public static final int DOWNLOADED_STATUS   = 3;

    /** Id */
    private long id;

    /** Document Record Id */
    private String documentRecordId;

    /** Document Version Id */
    private String documentVersionId;

    /** DataStore Id */
    private String dataStoreId;

    /** ArchiveStore Id */
    private String archiveStoreId;

    /** Archive Id */
    private String archiveId;

    /** Upload Id */
    private String uploadId;

    /** Download Id */
    private String downloadId;

    /** SHA256 Tree Hash */
    private String sha256TreeHash;

    /** Status */
    private int status = UNKNOWN_STATUS;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDocumentRecordId() {
        return documentRecordId;
    }

    public void setDocumentRecordId(String documentRecordId) {
        this.documentRecordId = documentRecordId;
    }

    public String getDocumentVersionId() {
        return documentVersionId;
    }

    public void setDocumentVersionId(String documentVersionId) {
        this.documentVersionId = documentVersionId;
    }

    public String getDataStoreId() {
        return dataStoreId;
    }

    public void setDataStoreId(String dataStoreId) {
        this.dataStoreId = dataStoreId;
    }

    public String getArchiveStoreId() {
        return archiveStoreId;
    }

    public void setArchiveStoreId(String archiveStoreId) {
        this.archiveStoreId = archiveStoreId;
    }

    public String getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(String archiveId) {
        this.archiveId = archiveId;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public String getSHA256TreeHash() {
        return sha256TreeHash;
    }

    public void setSHA256TreeHash(String sha256TreeHash) {
        this.sha256TreeHash = sha256TreeHash;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
