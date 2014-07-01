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
 * This class represents a numerical (double) precision piece of metadata
 * @author hugo
 */
public class NumericalMetadata extends MetadataItem {
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


    /** Double precision representation. This is the main form, setting this
     * also updates the longValue property so that exact == queries can be
     * performed.
     */
    private double doubleValue = 0.0;

    public NumericalMetadata() {
    }

    public NumericalMetadata(NumericalMetadata item) {
        super(item);
        doubleValue = item.getDoubleValue();
    }

    public NumericalMetadata(String category, String name, double value) {
        super();
        setCategory(category);
        setName(name);
        doubleValue = value;
    }

    public NumericalMetadata(String category, String name, long value) {
        super();
        setCategory(category);
        setName(name);
        doubleValue = (double)value;
    }
    
    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }
    
    @JsonIgnore
    public long getLongValue(){
        return (long)doubleValue;
    }
    
    @JsonIgnore
    public void setLongValue(long v){
        this.doubleValue = (double)v;
    }

    @Override
    public String toString() {
        return "NUMERICAL: " + getName() + "=" + doubleValue;
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("DoubleValue", doubleValue);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        doubleValue = store.doubleValue("DoubleValue", 0.0);
    }
    
    @Override
    @JsonIgnore
    public MetadataItem getCopy() {
        return new NumericalMetadata(this);
    }    

    @Override
    @JsonIgnore
    public String getStringValue() {
        return Double.toString(doubleValue);
    }
    
    
}