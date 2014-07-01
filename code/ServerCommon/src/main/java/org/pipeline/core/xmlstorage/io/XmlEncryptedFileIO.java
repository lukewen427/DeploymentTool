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

package org.pipeline.core.xmlstorage.io;

import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Key;

/**
 * This class allows XmlDataStores to be saved and retrieved from encrypted
 * files using specified public and private keys. 
 * @author hugo
 */
public class XmlEncryptedFileIO extends XmlDataStoreReadWriter {
    /** File to store to */
    private File file;
    
    /** Encryption / decryption key */
    private Key cryptKey;
    
    /** Creates a new instance of XmlEncryptedFileIO */
    public XmlEncryptedFileIO(File file, Key cryptKey){
        this.file = file;
        this.cryptKey = cryptKey;
    }
    
    /** Creates a new instance of XmlEncryptedFileIO */
    public XmlEncryptedFileIO(XmlDataStore dataStore, Key cryptKey) throws XmlStorageException {
        super(dataStore);
        this.cryptKey = cryptKey;
    }
    
    /** Store the XmlDataStore to a file */
    public void writeFile(File outputFile) throws XmlStorageException {
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, cryptKey);
            
            FileOutputStream fileStream = new FileOutputStream(outputFile);
            CipherOutputStream stream = new CipherOutputStream(fileStream, cipher);
            XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(getDataStore());
            writer.write(stream);
            stream.flush();
            stream.close();
            fileStream.close();
            
        } catch (Exception e){
            throw new XmlStorageException("Cannot encrypt file: " + e.getMessage());
        }
    }
    
    public XmlDataStore readFile(File inputFile) throws XmlStorageException {
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, cryptKey);
            
            return new XmlDataStore();
            
        } catch (Exception e){
            throw new XmlStorageException("Cannot encrypt file: " + e.getMessage());
        }        
    }
}
