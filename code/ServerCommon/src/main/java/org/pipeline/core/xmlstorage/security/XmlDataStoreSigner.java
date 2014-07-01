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

package org.pipeline.core.xmlstorage.security;

import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.pipeline.core.xmlstorage.io.XmlDataStoreStreamWriter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.PrivateKey;
import java.security.Signature;

/**
 * This class signs an XmlDataStore by converting it to a String and signing the
 * data. 
 * @author hugo
 */
public class XmlDataStoreSigner {
    /** Data storage object to be signed */
    private XmlDataStore store;
    
    /** Creates a new instance of XmlDataStoreSigner */
    public XmlDataStoreSigner(XmlDataStore store) {
        this.store = store;
    }
    
    /** Return a byte array containing the signature data for a button. Needs
     * to have a PrivateKey supplied in order to sign the object */
    public byte[] sign(PrivateKey key) throws XmlStorageException {
        try {
            // Save object to a byte array
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            PrintStream p = new PrintStream(byteStream);
            XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(store);
            writer.write(p);
            p.flush();                    
            byte[] objectData = byteStream.toByteArray();            
            
            // Sign the data using a private key
            Signature sig = Signature.getInstance("SHA1withDSA");
            sig.initSign(key);
            sig.update(objectData);            
            byte[] signatureData = sig.sign();
            
            // Set the data in the store
            store.setSignatureData(signatureData);
            return signatureData;
            
        } catch (Exception e){
            throw new XmlStorageException(e.getLocalizedMessage());            
        }
    }
}
