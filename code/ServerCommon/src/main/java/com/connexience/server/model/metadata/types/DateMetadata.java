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

import java.text.DateFormat;
import java.util.Date;
/**
 * This class represents a date that can be attached as metadata to an object
 * @author hugo
 */
public class DateMetadata extends MetadataItem{
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


    private Date dateValue = new Date();

    public DateMetadata() {
    }

    public DateMetadata(DateMetadata item) {
        super(item);
        dateValue = item.getDateValue();
    }

    public DateMetadata(String category, String name, Date value) {
        super();
        setCategory(category);
        setName(name);
        this.dateValue = value;
    }
    
    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }
    

    @Override
    public String toString() {
        return "DATE: " + getName() + "=" + DateFormat.getInstance().format(dateValue);
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("DateValue", dateValue);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        dateValue = store.dateValue("DateValue", new Date());
    }
    
    @Override
    @JsonIgnore
    public MetadataItem getCopy() {
        return new DateMetadata(this);
    }    

    @Override
    @JsonIgnore
    public String getStringValue() {
        return DateFormat.getDateTimeInstance().format(dateValue);
    }
}