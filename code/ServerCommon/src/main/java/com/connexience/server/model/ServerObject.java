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
package com.connexience.server.model;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.io.Serializable;
import java.util.Date;

/**
 * This is the base class for objects stored within the server.
 *
 * @author hugo
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ServerObject implements Serializable, Comparable<ServerObject>, XmlStorable {
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


    /**
     * Database ID
     */
    private String id;
    /**
     * Id of the object that contains this one if any
     */
    private String containerId;
    /**
     * Organisation that this object belongs to
     */
    private String organisationId;
    /**
     * Object name
     */
    private String name;
    /**
     * Organisation description
     */
    private String description;
    /**
     * ID of the user that created the object
     */
    private String creatorId;
    /**
     * Date the object was created
     */
    private long timeInMillis;

    private String objectType;

    /**
    * The project which created this object
    */
    private String projectId;

    /**
     * Creates a new instance of ServerObject
     */
    public ServerObject() {
        timeInMillis = new Date().getTime();
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("ServerObject");
        store.add("ID", id);
        store.add("CreatorID", creatorId);
        store.add("Description", description);
        store.add("Name", name);
        store.add("ObjectType", objectType);
        store.add("OrganisationID", organisationId);
        store.add("TimeInMillis", timeInMillis);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        id = store.stringValue("ID", null);
        creatorId = store.stringValue("CreatorID", null);
        description = store.stringValue("Description", null);
        name = store.stringValue("Name", null);
        objectType = store.stringValue("ObjectType", null);
        organisationId = store.stringValue("OrganisationID", null);
        timeInMillis = store.longValue("TimeInMillis", 0);
    }

    
    /**
     * Populate an object with fields from this one
     */
    public void populateCopy(ServerObject copy) {
        copy.setContainerId(containerId);
        copy.setCreatorId(creatorId);
        copy.setDescription(description);
        copy.setName(name);
        copy.setObjectType(objectType);
        copy.setOrganisationId(organisationId);
    }

    /**
     * toString displays object name
     */
    public String toString() {
        return name;
    }

    /**
     * Get the object unique identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Set the object unique idendifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the object name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the object name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the id of the container object
     */
    public String getContainerId() {
        return containerId;
    }

    /**
     * Set the id of the container object
     */
    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    /**
     * Get the id of the organisation containing this object
     */
    public String getOrganisationId() {
        return organisationId;
    }

    /**
     * Set the id of the organisation containing this object
     */
    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    /**
     * Set the description of this object
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the description of this object
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the ID of the user that created this object
     */
    public String getCreatorId() {
        return creatorId;
    }

    /**
     * Set the ID of the user that created this object
     */
    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    @JsonIgnore
    public java.util.Date getCreationDate() {
        return new java.util.Date(getTimeInMillis());
    }

    public void setCreationTime(java.util.Date dateCreated) {
        setTimeInMillis(dateCreated.getTime());
    }

    public int compareTo(ServerObject o) {
        return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getProjectId()
    {
      return projectId;
    }

    public void setProjectId(String projectId)
    {
      this.projectId = projectId;
    }

  /**
     * Override the equals method to check Id
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerObject)) {
            return false;
        }

        ServerObject that = (ServerObject) o;

        if (!id.equals(that.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @JsonIgnore
    public String getDisplayName() {
        String n = StringEscapeUtils.escapeHtml(this.getName());
        return n;
    }
}