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
package com.connexience.server.model.document;

import com.connexience.server.model.ServerObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.io.Serializable;

/**
 * This class represents a document type that is recognised by the database. It contains
 * details of the data format and MIME type, which is used to give client applications
 * hints as to which viewer to use.
 * @author nhgh
 */
public class DocumentType extends ServerObject implements Serializable {
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


    /** Binary data format */
    public static final int BINARY_DATA = 0;
    
    /** XML text format */
    public static final int XML_DATA = 1;
    
    /** Plain row-by-row text file */
    public static final int PLAIN_TEXT = 2;
    
    /** Format type */
    private int formatType = BINARY_DATA;
    
    /** MIME type of the document data */
    private String mimeType = "application/octet-stream";
    
    /** File extension. This is specified without the '.'. */
    private String extension = "txt";
    
    /** Creates a new instance of DocumentType */
    public DocumentType() {
    }

    /** Create with data */
    public DocumentType(String mimeType, String extension){
        this.mimeType = mimeType;
        this.extension = extension;
        this.setName(this.extension + " document");
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("MimeType", mimeType);
        store.add("Extension", extension);
        store.add("FormatType", formatType);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        mimeType = store.stringValue("MimeType", "application/octet-stream");
        extension = store.stringValue("Extension", "txt");
        formatType = store.intValue("FormatType", BINARY_DATA);
    }
    
    
    /** Get the basic data format type */
    public int getFormatType() {
        return formatType;
    }

    /** Set the basic data format type */
    public void setFormatType(int formatType) {
        this.formatType = formatType;
    }

    /** Get the mime type which is used as a viewer hint */
    public String getMimeType() {
        return mimeType;
    }

    /** Set the mime type which will be used as a viewer hint */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /** Get the file extension */
    public String getExtension() {
        return extension;
    }

    /** Set the file extension. This should be specified without the '.' */
    public void setExtension(String extension) {
        this.extension = extension;
    }
}