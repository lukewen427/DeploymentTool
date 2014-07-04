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
import com.connexience.server.util.SerializationUtils;
import com.connexience.server.workflow.engine.HashableTransferObject;
import com.connexience.server.workflow.util.ZipUtils;
import org.pipeline.core.drawing.*;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.pipeline.core.xmlstorage.Base64;

/**
 * This class provides a transfer data object which wraps up a serialized
 * java object.
 *
 * @author nhgh
 */
public class ObjectWrapper implements TransferData, StorableTransferData, HashableTransferObject {
    /**
     * Object being stored
     */
    private Object storedObject = null;

    /**
     * Byte array storing content of the object
     */
    private byte[] objectData = null;

    /**
     * MD5 hash value
     */
    String hashValue = null;

    /**
     * Construct an empty object wrapper
     */
    public ObjectWrapper() {

    }

    /**
     * Construct with a serializble object
     */
    public ObjectWrapper(Object storedObject) {
        this.storedObject = storedObject;
    }

    /**
     * Construct with some object data
     */
    public ObjectWrapper(byte[] objectData) {
        this.objectData = objectData;
    }

    /**
     * Get a copy of the saved object
     */
    public TransferData getCopy() throws DrawingException {
        try {
            if (storedObject instanceof Serializable) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ObjectOutputStream objWriter = new ObjectOutputStream(stream);
                objWriter.writeObject(storedObject);
                objWriter.flush();
                objWriter.close();
                ByteArrayInputStream inStream = new ByteArrayInputStream(stream.toByteArray());
                ObjectInputStream objReader = new ObjectInputStream(inStream);
                Object newObj = objReader.readObject();
                return new ObjectWrapper(newObj);
            } else {
                throw new DrawingException("Cannot serialize object");
            }

        } catch (Throwable t) {
            throw new DrawingException("Error copying data: " + t.getMessage());
        }
    }

    /**
     * Get the contained object
     */
    public Object getPayload() {
        if (storedObject != null) {
            return storedObject;
        } else {
            try {
                storedObject = SerializationUtils.deserialize(objectData);
                return storedObject;
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Load the object from an input stream
     */
    public void loadFromInputStream(InputStream inStream) throws DrawingException {
        try {
            ByteArrayOutputStream store = new ByteArrayOutputStream();
            ZipUtils.copyInputStream(inStream, store);
            this.objectData = store.toByteArray();
        } catch (Exception e) {
            throw new DrawingException("Cannot load object from stream: " + e.getMessage());
        }
    }

    /**
     * Save the object to an output stream
     */
    public void saveToOutputStream(OutputStream outStream) throws DrawingException {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            DigestOutputStream digestStream = new DigestOutputStream(outStream, md5);

            if (storedObject != null && objectData == null) {
                // Write object

                ObjectOutputStream writer = new ObjectOutputStream(digestStream);
                writer.writeObject(storedObject);
                writer.flush();
                writer.close();

            } else if (objectData != null) {
                // Directly write data
                ByteArrayInputStream source = new ByteArrayInputStream(objectData);
                ZipUtils.copyInputStream(source, digestStream);
            }

            digestStream.flush();
            digestStream.close();
            hashValue = com.connexience.server.util.Base64.encodeBytes(digestStream.getMessageDigest().digest());
        } catch (Exception e) {
            throw new DrawingException("Error saving object to stream: " + e.getMessage());
        }
    }

    @Override
    public String getHash() throws DrawingException {

        if (hashValue != null) {
            return hashValue;
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
            try {
                //calculate the MD5 hash of the object
                out = new ObjectOutputStream(bos);
                out.writeObject(storedObject);
                objectData = bos.toByteArray();

                return new String(MessageDigest.getInstance("MD5").digest(objectData));
            } catch (Exception e) {
                throw new DrawingException("Cannot create MD5 hash");
            } finally {
                try {
                    if (out != null) out.close();
                    bos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }


    /**
     * Get the raw object data
     */
    public byte[] getObjectData() {
        return objectData;
    }
}