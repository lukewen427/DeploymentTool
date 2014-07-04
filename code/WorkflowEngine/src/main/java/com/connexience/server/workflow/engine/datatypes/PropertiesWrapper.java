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
package com.connexience.server.workflow.engine.datatypes;
import java.security.DigestOutputStream;
import com.connexience.server.workflow.engine.HashableTransferObject;

import java.io.*;
import java.security.MessageDigest;

import org.pipeline.core.drawing.*;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.io.*;

/**
 * This data wrapper allows a set of name-value pairs to be passed
 * between workflow blocks (services).
 * @author hugo
 */
public class PropertiesWrapper implements TransferData, StorableTransferData, HashableTransferObject {
    /** Properties being transferred */
    private XmlDataStore properties;

    /** MD5 hash value */
    private String hashValue = null;
    
    public PropertiesWrapper(){
        properties = new XmlDataStore("Properties");
    }
    
    /** Construct with properties */
    public PropertiesWrapper(XmlDataStore properties) {
        this.properties = properties;
    }
   
    /** Get the properties payload */
    public Object getPayload() {
        return properties;
    }

    /** Get a copy of this transfer data */
    public TransferData getCopy() throws DrawingException {
        PropertiesWrapper wrapper = new PropertiesWrapper((XmlDataStore)properties.getCopy());
        return wrapper;
    }

    public void loadFromInputStream(InputStream stream) throws DrawingException {
        try {
            XmlDataStoreStreamReader reader = new XmlDataStoreStreamReader(stream);
            this.properties = reader.read();
        } catch (Exception e){
            throw new DrawingException("Error loading property data: " + e.getMessage(), e);
        }
    }

    public void saveToOutputStream(OutputStream stream) throws DrawingException {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            DigestOutputStream digestStream = new DigestOutputStream(stream, md5);
            XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(properties);
            writer.write(digestStream);
            stream.flush();
            digestStream.flush();
            digestStream.close();
            hashValue = com.connexience.server.util.Base64.encodeBytes(digestStream.getMessageDigest().digest());
        } catch (Exception e){
            throw new DrawingException("Error saving property data: " + e.getMessage());
        }
    }    
    
    public XmlDataStore properties() {
        return properties;
    }

    @Override
    public String getHash() throws DrawingException {
        if (hashValue != null) {
            return hashValue;
        } else {
            try {
                //calculate the MD5 hash of the object
                XmlDataStoreByteArrayIO writer = new XmlDataStoreByteArrayIO(properties);
                byte[] objectData = writer.toByteArray();

                return new String(MessageDigest.getInstance("MD5").digest(objectData));
            } catch (Exception e) {
                throw new DrawingException("Cannot create MD5 hash");
            }
        }
    }
    
    
}