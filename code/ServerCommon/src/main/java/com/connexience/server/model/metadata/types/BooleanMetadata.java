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
package com.connexience.server.model.metadata.types;

import com.connexience.server.model.metadata.MetadataItem;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class represents a single true / false piece of metadata that can be attached 
 * to an object.
 * @author hugo
 */
public class BooleanMetadata extends MetadataItem {
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


    private boolean booleanValue = true;

    public BooleanMetadata() {
    }

    public BooleanMetadata(BooleanMetadata item) {
        super(item);
        booleanValue = item.isBooleanValue();
    }

    public BooleanMetadata(String category, String name, boolean value) {
        super();
        setCategory(category);
        setName(name);
        this.booleanValue = value;
    }
    
    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }
    
    

    @Override
    public String toString() {
        return "BOOLEAN: " + getName() + "=" + booleanValue;
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("BooleanValue", booleanValue);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        booleanValue = store.booleanValue("BooleanValue", true);
    }

    @Override
    @JsonIgnore
    public MetadataItem getCopy() {
        return new BooleanMetadata(this);
    }

    @Override
    @JsonIgnore
    public String getStringValue() {
        return Boolean.toString(booleanValue);
    }
}