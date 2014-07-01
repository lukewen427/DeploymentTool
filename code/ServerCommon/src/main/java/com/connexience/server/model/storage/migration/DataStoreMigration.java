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

import com.connexience.server.model.storage.DataStore;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.pipeline.core.xmlstorage.io.XmlDataStoreByteArrayIO;

import java.io.Serializable;

/**
 * This class represents a migration that is in progress. It contains details
 * of the type of data store being moved to and it's configuration properties.
 * @author hugo
 */
public class DataStoreMigration implements Serializable {
    public static final int MIGRATION_STOPPED = 0;
    public static final int MIGRATION_RUNNING = 1;
    public static final int MIGRATION_FINISHED = 2;
    
    /** Database ID */
    private long id;
    
    /** Text representing the data store that will receive the data */
    private String storeXml;

    /** Datastore object */
    private transient DataStore targetStore;
    
    /** Owner of this migration */
    private String userId;
    
    /** Status of this migration */
    private int status = MIGRATION_STOPPED;
    
    public DataStoreMigration() {
    }
    
    /** Set the data store to be moved to */
    @JsonIgnore
    public void setTargetDataStore(DataStore targetStore) throws XmlStorageException {
        this.targetStore = targetStore;
        XmlDataStore storeData = new XmlDataStore("DataStoreXML");
        storeData.add("Store", this.targetStore);
        XmlDataStoreByteArrayIO writer = new XmlDataStoreByteArrayIO(storeData);
        storeXml = new String(writer.toByteArray());
    }
    
    /** Get the target data store */
    @JsonIgnore
    public DataStore getTargetDataStore() throws XmlStorageException {
        if(this.targetStore!=null){
            return targetStore;
        } else {
            return reinstantiateStore();
        }
    }
    
    /** Recreate the store object */
    public DataStore reinstantiateStore() throws XmlStorageException {
        XmlDataStoreByteArrayIO reader = new XmlDataStoreByteArrayIO(storeXml.getBytes());
        XmlDataStore store = reader.toXmlDataStore();
        DataStore result = (DataStore)store.xmlStorableValue("Store");
        targetStore = result;
        return targetStore;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStoreXml() {
        return storeXml;
    }

    public void setStoreXml(String storeXml) {
        this.storeXml = storeXml;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}