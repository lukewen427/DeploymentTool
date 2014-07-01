/**
 * e-Science Central Copyright (C) 2008-2013 School of Computing Science,
 * Newcastle University
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation at: http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package com.connexience.server.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

/**
 * This class provides basic serialization and deserialization utility methods.
 *
 * @author hugo
 */
public class SerializationUtils {

    /**
     * Serialize an object to a byte array
     */
    public static byte[] serialize(Serializable object) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(buffer);
        stream.writeObject(object);
        stream.flush();
        stream.close();
        return buffer.toByteArray();
    }

    /**
     * Serialize to an output stream
     */
    public static void serialize(Serializable object, OutputStream outStream) throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(outStream);
        stream.writeObject(object);
        stream.flush();
        stream.close();
        outStream.flush();
    }

    /**
     * Deserialize from an input stream
     */
    public static Object deserialize(InputStream inStream) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(inStream);
        Object obj = stream.readObject();
        stream.close();
        return obj;
    }

    /**
     * Deserialize an object from a byte array
     */
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream buffer = new ByteArrayInputStream(data);
        ObjectInputStream stream = new ObjectInputStream(buffer);
        Object obj = stream.readObject();
        stream.close();
        return obj;
    }
    
    /** 
     * Deserialize from a byte array and class loader 
     */
    public static Object deserialize(byte[] data, ClassLoader cl) throws IOException, ClassNotFoundException {
        ByteArrayInputStream buffer = new ByteArrayInputStream(data);
        MyObjectInputStream stream = new MyObjectInputStream(buffer, cl);
        Object obj = stream.readObject();
        stream.close();
        return obj;
    }
    
    /**
     * Write an object to a file
     */
    public static void serialize(Serializable object, File outFile) throws IOException {
        FileOutputStream fileStream = new FileOutputStream(outFile);
        ObjectOutputStream objStream = new ObjectOutputStream(fileStream);
        objStream.writeObject(object);
        objStream.flush();
        fileStream.flush();
        fileStream.getFD().sync();
        objStream.close();
        fileStream.close();
    }

    /**
     * Read an object from file
     */
    public static Object deserialize(File inFile) throws ClassNotFoundException, IOException {
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(inFile));
        Object obj = stream.readObject();
        stream.close();
        return obj;
    }

    /**
     * Serialize an object to an XML file
     */
    public static void XmlSerialize(Serializable object, File outFile) throws IOException {
        FileOutputStream fileStream = new FileOutputStream(outFile);
        XMLEncoder encoder = new XMLEncoder(fileStream);
        encoder.writeObject(object);
        encoder.flush();
        fileStream.flush();
        fileStream.getFD().sync();
        encoder.close();
        fileStream.close();
    }

    /**
     * Deserialize an object from an XML file
     */
    public static Object XmlDeserialize(File inFile) throws ClassNotFoundException, IOException {
        FileInputStream inStream = null;
        XMLDecoder decoder = null;
        try {
            inStream = new FileInputStream(inFile);
            decoder = new XMLDecoder(inStream);
            Object result = decoder.readObject();
            return result;
        } finally {
            if (decoder != null) {
                decoder.close();
            }
            if (inStream != null) {
                inStream.close();
            }
        }
    }

    /**
     * Extended object input stream to deal with a class loader
     */
    public static class MyObjectInputStream extends ObjectInputStream {
        private ClassLoader cl;
        
        public MyObjectInputStream(InputStream in, ClassLoader cl) throws IOException {
            super(in);
            this.cl = cl;
        }

        @Override
        public Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            Class c = cl.loadClass(desc.getName());
            return c;
        }
    }


}