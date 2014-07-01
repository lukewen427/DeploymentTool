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



import org.pipeline.core.xmlstorage.Base64;
import org.pipeline.core.xmlstorage.XmlSignable;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.pipeline.core.xmlstorage.io.XmlDataStoreStreamWriter;
import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

/**
 * This class provides helper methods for objects that implement the XmlSignable
 * interface.
 * @author hugo
 */
public class XmlDataStoreSignatureHelper {
    /** Has this object been signed */
    private boolean objectSigned = false;
    /** Signature data */
    private byte[] signatureData = new byte[0];
    /** ID of user who signed the object */
    private String signingUserId;
    /** Parent object for this signature helper */
    private XmlStorable parentObject = null;
    /** Is the signature dirty */
    private boolean signatureDirty = true;
    /** Does the signature need to be recalculated every time */
    private boolean alwaysRecalculate = true;
    /** Is the signature valid */
    private boolean signatureValid = false;
    /** Listeners */
    private ArrayList<XmlDataStoreSignatureListener> listeners = new ArrayList<>();
    /** Certificate used to validate signature */
    private X509Certificate certificate = null;

    /** Creates a new instance of XmlDataStoreSignatureHelper */
    public XmlDataStoreSignatureHelper(XmlSignable parentSignableObject) {
        this.parentObject = (XmlStorable)parentSignableObject;
    }

    /** Add a listener */
    public void addXmlDataStoreSignatureListener(XmlDataStoreSignatureListener listener){
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    /** Remove a listener */
    public void removeXmlDataStoreSignatureListener(XmlDataStoreSignatureListener listener){
        listeners.remove(listener);
    }

    /** Set the validation certificate */
    public void setCertificate(X509Certificate certificate){
        this.certificate = certificate;
    }

    /** Notify a state change */
    private void notifyStateChange(){
        for(XmlDataStoreSignatureListener listener : listeners) {
            listener.signatureStateChanged(this);
        }
    }

    /** Does this object contain a signature */
    public boolean objectSigned(){
        return objectSigned;
    }

    /** Remove the signature */
    public void removeSignature(){
        signatureValid = false;
        signatureDirty = false;
        signingUserId = null;
        objectSigned = false;
        signatureData = new byte[0];
        notifyStateChange();
    }

    /** Get the parent object */
    public XmlStorable getParentObject(){
        return parentObject;
    }

    /** Get whether to always recalculate the object data when checking the signature */
    public boolean getAlwaysRecalculate(){
        return alwaysRecalculate;
    }

    /** Set whether to always recalculate the object data when checking the signature */
    public void setAlwaysRecalculate(boolean alwaysRecalculate){
        this.alwaysRecalculate = alwaysRecalculate;
    }

    /** Set the signature to be dirty */
    public void setSignatureDirty(){
        signatureDirty = true;
        notifyStateChange();
    }

    /** Append the signature and certificate data as attributes of an Element */
    public void appendToXmlElement(Element xmlElement) throws XmlStorageException {
        if(objectSigned){
            // Add all attributes
            try {
                xmlElement.setAttribute("ObjectSigned", "true");
                xmlElement.setAttribute("Signature", Base64.encodeBytes(signatureData));
                xmlElement.setAttribute("UserID", signingUserId);
            } catch (Exception e){
                throw new XmlStorageException("Error saving signature data: " + e.getMessage());
            }

        } else {
            // Just add a flag to say that the object hasn't been signed
            xmlElement.setAttribute("ObjectSigned", "false");
        }
    }

    /** Re-create the signature and certificate information from the attributes of an
     * Xml Element */
    public void recreateFromXmlElement(Element xmlElement) throws XmlStorageException {
        signatureDirty = true;
        signatureValid = false;
        String signedAttribute = xmlElement.getAttribute("ObjectSigned");
        if(signedAttribute!=null){
            if(signedAttribute.equalsIgnoreCase("true")){
                // Load signature data
                String signatureText = xmlElement.getAttribute("Signature");
                if(signatureText!=null){
                    signatureData = Base64.decode(signatureText);
                    signingUserId = xmlElement.getAttribute("UserID");
                    objectSigned = true;

                } else {
                    objectSigned = false;
                    signingUserId = null;
                    signatureData = new byte[0];
                }

            } else {
                // Not signed
                objectSigned = false;
                signingUserId = null;
                signatureData = new byte[0];
            }

        } else {
            // No signature attribute
            objectSigned = false;

        }
    }

    /** Get the certificate */
    public String getSigningUserId(){
        return signingUserId;
    }

    /** Sign the object using a PrivateKey */
    public void signObject(PrivateKey key, String userId) throws XmlStorageException {
        if(parentObject!=null){

            try {
                // Save object to a byte array
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                PrintStream p = new PrintStream(byteStream);
                XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(parentObject.storeObject());
                writer.setDescriptionIncluded(true);
                writer.write(p);
                p.flush();
                byte[] objectData = byteStream.toByteArray();

                // Sign the data using a private key
                Signature sig = Signature.getInstance("SHA1withDSA");
                sig.initSign(key);
                sig.update(objectData);
                signatureData = sig.sign();
                objectSigned = true;
                this.signingUserId = userId;
                signatureDirty = false;
                signatureValid = true;

            } catch (Exception e){
                signatureDirty = false;
                signatureValid = false;
                this.signingUserId = null;
                signatureData = new byte[0];
                objectSigned = false;
                throw new XmlStorageException(e.getLocalizedMessage());
            }
        } else {
            throw new XmlStorageException("No object to sign");
        }
        notifyStateChange();
    }

    /** Force an immediate signature revalidation */
    public void forceValidation(){
        validateSignature();
    }

    /** Check the signature and set the relevant internal flags */
    private void validateSignature() {
        try {
            if(objectSigned){
                // Get the byte data from the XmlDataStore
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                PrintStream p = new PrintStream(byteStream);
                XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(parentObject.storeObject());
                writer.setDescriptionIncluded(true);
                writer.write(p);
                p.flush();
                byte[] objectData = byteStream.toByteArray();

                // Set up the signature and verify object data
                PublicKey key = certificate.getPublicKey();
                Signature signature = Signature.getInstance("SHA1withDSA");
                signature.initVerify(key);
                signature.update(objectData);
                signatureValid = signature.verify(signatureData);
                signatureDirty = false;

            } else {
                signatureValid = false;
                signatureDirty = false;
            }

        } catch (Exception e){
            signatureValid = false;
            signatureDirty = false;
        }
        notifyStateChange();
    }

    /** Verify the signature */
    public boolean verifyObject() throws XmlStorageException {
        if(alwaysRecalculate){
            validateSignature();
            return signatureValid;
        } else {
            if(!signatureDirty){
                return signatureValid;
            } else {
                validateSignature();
                return signatureValid;
            }
        }
    }
}
