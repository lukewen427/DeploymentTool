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
package com.connexience.server.model.security;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.io.Serializable;
import java.util.Date;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class Ticket implements Serializable {
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
    private static final long serialVersionUID = 6826210287308161730L;


    /** ID that represents the organisation for a root ticket */
    public static final String ROOT_ORGANISATION_ID = "0000000000000000000";

    /** Property of the remote IP address in the Axis Engine */
    public static final String REMOTE_IP_PROPERTY = "RemoteIP";

    /**
     * Should this ticket be stored. This is only set when the ticket is created, and is set to
     * false the first time the ticket has been stored
     */
    private boolean storable = false;

    /** ID of the logon ticket */
    private String id;

    /** User ID that this ticket refers to */
    private String userId;

    /** Organisation id */
    private String organisationId;

    /** Last access time */
    private Date lastAccessTime;

    /** Is this a super user ticket for this organisation */
    private boolean superTicket = false;

    /**
     * IP of the host that sent this ticket. This is not set in the database and is recorded
     * in the ticket handler when the ticket is received
     */
    private String remoteHost;

    /** Default storage directory.  Could be null or project files directory. */
    private String defaultStorageFolderId;

    /** Id of the default project.  Could be null */
    private String defaultProjectId;

    /** Empty constructor */
    public Ticket() {
    }

    /** Is this ticket associated with an organisation that isn't the root org */
    @JsonIgnore
    public boolean isAssociatedWithNonRootOrg() {
        if (organisationId.equals(ROOT_ORGANISATION_ID)) {
            return false;
        } else {
            return true;
        }
    }

    /** Return the ID of this ticket */
    public String getId() {
        return id;
    }

    /** Set the ID of this ticket */
    public void setId(String id) {
        this.id = id;
    }

    /** Get the user id this ticket refers to */
    public String getUserId() {
        return userId;
    }

    /** Set the user id this ticket refers to */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** Get the last access time */
    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    /** Set the last access time */
    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    /** Update the access time */
    public void updateAccessTime() {
        lastAccessTime = new Date();
    }

    /** Get the id of the organisation hosting the logon */
    public String getOrganisationId() {
        return organisationId;
    }

    /** Set the id of the organisation hosting the logon */
    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    /** Is this a superuser ticket for the organisation */
    public boolean isSuperTicket() {
        return superTicket;
    }

    /** Set whether this is a superuser ticket for the organisation */
    public void setSuperTicket(boolean superTicket) {
        this.superTicket = superTicket;
    }

    /** Get the IP address of the remote host */
    public String getRemoteHost() {
        return remoteHost;
    }

    /** Set the IP address of the remote host */
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    /** Set whether to store this ticket */
    public void setStorable(boolean storable) {
        this.storable = storable;
    }

    /** Get whether to store this ticket */
    public boolean isStorable() {
        return storable;
    }

    public String getDefaultStorageFolderId() {
        return defaultStorageFolderId;
    }

    public void setDefaultStorageFolderId(String defaultStorageFolderId) {
        this.defaultStorageFolderId = defaultStorageFolderId;
    }

    public String getDefaultProjectId() {
        return defaultProjectId;
    }

    public void setDefaultProjectId(String defaultProjectId) {
        this.defaultProjectId = defaultProjectId;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "storable=" + storable +
                ", id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", organisationId='" + organisationId + '\'' +
                ", lastAccessTime=" + lastAccessTime +
                ", superTicket=" + superTicket +
                ", remoteHost='" + remoteHost + '\'' +
                ", defaultStorageFolderId='" + defaultStorageFolderId + '\'' +
                ", defaultProjectId='" + defaultProjectId + '\'' +
                '}';
    }
}