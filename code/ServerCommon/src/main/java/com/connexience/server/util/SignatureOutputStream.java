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
package com.connexience.server.util;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;

/**
 * This class signs all of the data in an output stream
 * @author nhgh
 */
public class SignatureOutputStream extends OutputStream {
    /** Signature object */
    private Signature sig;
    
    /** Signature data */
    private byte[] signatureData = new byte[0];
    
    /** Create with a private key */
    public SignatureOutputStream(PrivateKey key, String algorithm) throws NoSuchAlgorithmException, InvalidKeyException {
        sig = Signature.getInstance(algorithm);
        sig.initSign(key);
    }
    
    @Override
    public void write(int arg0) throws IOException {
        try {
            sig.update((byte)arg0);
        } catch (Exception e){
            throw new IOException("Signature error: " + e.getMessage());
        }
    }

    @Override
    public void write(byte[] arg0) throws IOException {
        try {
            sig.update(arg0);
        } catch (Exception e){
            throw new IOException("Signature error: " + e.getMessage());
        }
    }

    @Override
    public void write(byte[] arg0, int arg1, int arg2) throws IOException {
        try {
            sig.update(arg0, arg1, arg2);
        } catch (Exception e){
            throw new IOException("Signature error: " + e.getMessage());
        }
    }

    /** Get the signature */
    public byte[] getSignatureData(){
        return signatureData;
    }

    /** Calculate the signature on the flush method */
    @Override
    public void flush() throws IOException {
        try {
            signatureData = sig.sign();
        } catch (Exception e){
            throw new IOException("Signature error: " + e.getMessage());
        }
    }
}