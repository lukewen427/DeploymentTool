/* =================================================================
 *                     conneXience Data Pipeline
 * =================================================================
 *
 * Copyright 2006 Hugo Hiden and Adrian Conlin
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.pipeline.core.xmlstorage.xmldatatypes;

import org.pipeline.core.xmlstorage.XmlDataObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class serializes AutoPersistable objects
 * @author  hugo
 */
public class XmlAutoStorableDataObject extends XmlDataObject {
    /** Class name to recreate */
    private String className = "";
    
    /** Data store containing class data */
    //private XmlDataStore myClassData = null;
    private XmlDataStore classData = null;
    
    /** Creates a new instance of XmlAutoStorableDataObject */
    public XmlAutoStorableDataObject() {
    }
    
    /** Creates a new instance of XmlAutoStorableDataObject with a name */
    public XmlAutoStorableDataObject(String strName) {
        super(strName);
    }

    /** Creates a new instance of XmlAutoStorableDataObject with a name, class data and a class name */
    public XmlAutoStorableDataObject(String name, XmlDataStore classData, String className) {
        super(name);
        this.className = className;
        this.classData = (XmlDataStore)classData.getCopy();
    }

    /** Add contents to an XmlElement */
    public void appendToXmlElement(Document xmlDocument, Element xmlElement, boolean includeDescription) throws XmlStorageException {
    }
    
    /** Recreate from an XmlElement */
    public void buildFromXmlElement(Element xmlElement) throws XmlStorageException {
    }
    
    /** Create an exact copy of this object */
    public XmlDataObject getCopy() {
        XmlAutoStorableDataObject copy = new XmlAutoStorableDataObject();
        copy.setDescription(getDescription());
        copy.setCategory(getCategory());
        copy.setOptions(getOptions());
        return copy;
    }
    
    /** Return the type label as used in the XML document */
    public String getTypeLabel() {
        return "XmlAutoStorable";
    }
    
    /** Return the value as an object reference. This method instantiates the object using the stored class data and the class name */
    public Object getValue() {
        return null;
    }
    
    /** Set the value as an object reference. This method tries to persist the object using the autopersist framework to an XmlDataStore */
    public void setValue(Object objectValue) throws XmlStorageException {
    }

    @Override
    public Object getDefaultValue() throws XmlStorageException {
        throw new XmlStorageException("Default value not supported for: " + getClass().getSimpleName());
    }

    @Override
    public boolean containsNonDefaultValue() {
        return true;
    }
    
    
}
