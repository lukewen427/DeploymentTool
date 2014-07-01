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
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;

/**
 * This class verifies the signature data in an XmlDataStore object.
 * @author hugo
 */
public class XmlDataStoreVerifier {
    /** Store to be verified */
    private XmlDataStore store;
    
    /** Creates a new instance of XmlDataStoreVerifier */
    public XmlDataStoreVerifier(XmlDataStore store) {
        this.store = store;
    }
    
    /** Check the signature */
    public boolean validate(X509Certificate certificate) throws XmlStorageException {
        try {
            if(store.isSigned()){
                // Get the byte data from the XmlDataStore
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                PrintStream p = new PrintStream(byteStream);
                XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(store);
                writer.write(p);
                p.flush();                    
                byte[] objectData = byteStream.toByteArray();                    

                // Set up the signature and verify object data
                byte[] signatureData = store.getSignatureData();
                PublicKey key = certificate.getPublicKey();
                Signature signature = Signature.getInstance("SHA1withDSA");
                signature.initVerify(key);
                signature.update(objectData);
                return signature.verify(signatureData);
                                
            } else {            
                return false;
            }
            
        } catch (XmlStorageException de){
            throw de;
            
        } catch (Exception e){
            throw new XmlStorageException(e.getLocalizedMessage());
        }
        
    }
}
