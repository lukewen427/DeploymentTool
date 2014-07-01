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
package com.connexience.server.model.metadata;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.io.Serializable;

/**
 * This is the base class for a single piece of metadata
 * @author hugo
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class MetadataItem implements Serializable, XmlStorable {
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


    /** Metadata database ID */
    private long id;
    
    /** ID of the object that this metadata refers to */
    private String objectId;
    
    /** Name of this metadata item */
    private String name;
    
    /** ID of the user that created the metadata */
    private String userId;

    /** Category name of the metadata */
    private String category;

    public MetadataItem() {
    }

    public MetadataItem(MetadataItem item) {
        objectId = item.getObjectId();
        name = item.getName();
        userId = item.getUserId();
        category = item.getCategory();
    }
    
    /** Populate the basic properties from another metadata item */
    public void populateBasicProperties(MetadataItem item){
        objectId = item.getObjectId();
        name = item.getName();
        userId = item.getUserId();
        category = item.getCategory();
    }
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("MetadataItem");
        store.add("ID", id);
        store.add("Category", category);
        store.add("Name", name);
        store.add("ObjectID", objectId);
        store.add("UserID", userId);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        id = store.longValue("ID", 0);
        category = store.stringValue("Category", null);
        name = store.stringValue("Name", null);
        objectId = store.stringValue("ObjectID", null);
        userId = store.stringValue("UserID", null);
    }
    
    @JsonIgnore
    public abstract MetadataItem getCopy();
    
    @JsonIgnore
    public abstract String getStringValue();
}