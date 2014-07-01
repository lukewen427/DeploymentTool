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
package com.connexience.server.model.storage.migration;

import java.io.Serializable;

/**
 * This class represents a document version that is being migrated from one
 * storage driver to another. One of these is created for each version to
 * be copied and then an EJB moves these one by one into the new data store
 * @author hugo
 */
public class DocumentVersionMigration implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Status flags for copying
    public static final int WAITING_FOR_COPY = 0;
    public static final int COPYING = 1;
    public static final int COPY_DONE = 2;
    public static final int COPY_FAILED = 3;
    public static final int IN_JMS_QUEUE = 4;
    
    /** Database ID */
    private long id;
    
    /** ID of the migration */
    private long migrationId;
    
    /** Document version being moved */
    private String versionId;
    
    /** ID of the document being moved */
    private String documentID;
    
    /** Organisation ID */
    private String organisationId;
    
    /** Current copying status */
    private int copyStatus = WAITING_FOR_COPY;

    /** Message if there was a copy error */
    private String copyErrorMessage = "";
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMigrationId() {
        return migrationId;
    }

    public void setMigrationId(long migrationId) {
        this.migrationId = migrationId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    public int getCopyStatus() {
        return copyStatus;
    }

    public void setCopyStatus(int copyStatus) {
        this.copyStatus = copyStatus;
    }

    public String getCopyErrorMessage() {
        return copyErrorMessage;
    }

    public void setCopyErrorMessage(String copyErrorMessage) {
        this.copyErrorMessage = copyErrorMessage;
    }
}