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
package com.connexience.server.workflow.util;

import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.io.*;
import java.io.*;
/**
 * This class provides utility methods for serializing and deserializing objects via
 * the XmlDataStore system
 * @author hugo
 */
public class XmlSerializationUtils {
    /** Serialize an object to an output stream */
    public static void xmlDataStoreSerialize(OutputStream outStream, XmlStorable object) throws XmlStorageException {
        try {
            XmlDataStore store = new XmlDataStore();
            store.add("StoredObject", object);
            XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(store);
            writer.write(outStream);

        } catch (Exception e){
            throw new XmlStorageException("Error writing object: " + e.getMessage(), e);
        } finally {
            try {
                outStream.flush();
            } catch (IOException ioe){
                System.out.println("Error saving file: " + ioe.getMessage());
            }
        }        
    }
    
   /** Deserialize an object from an XML file */
    public static void xmlDataStoreSerialize(File inFile, XmlStorable object) throws XmlStorageException{
        FileOutputStream outStream = null;

        try {
            outStream = new FileOutputStream(inFile);
            XmlDataStore store = new XmlDataStore();
            store.add("StoredObject", object);
            XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(store);
            writer.write(outStream);

        } catch (FileNotFoundException e){
            throw new XmlStorageException("Error opening file: " + e.getMessage());
        } finally {
            try {
                outStream.flush();
                outStream.close();
            } catch (IOException ioe){
                System.out.println("Error saving file: " + ioe.getMessage());
            }
        }
    }

   /** Deserialize an object from an XML file */
    public static Object xmlDataStoreDeserialize(File inFile) throws XmlStorageException{
        FileInputStream inStream = null;

        try {
            inStream = new FileInputStream(inFile);
            XmlDataStoreStreamReader reader = new XmlDataStoreStreamReader(inStream);
            XmlDataStore containerData = reader.read();
            return containerData.xmlStorableValue("StoredObject");

        } catch (FileNotFoundException e){
            throw new XmlStorageException("Error opening file: " + e.getMessage(), e);
        } finally {
        }
    }
    
    /** Deserialize an object from an input stream */
    public static Object xmlDataStoreDeserialize(InputStream inStream) throws XmlStorageException {
        try {
            XmlDataStoreStreamReader reader = new XmlDataStoreStreamReader(inStream);
            XmlDataStore containerData = reader.read();
            return containerData.xmlStorableValue("StoredObject");

        } catch (Exception e){
            throw new XmlStorageException("Error opening file: " + e.getMessage(), e);
        } finally {
        }        
    }
}
