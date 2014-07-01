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
package com.connexience.server.model.security;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * This class contains key data associated with a specific object. It holds the
 * public + private keys for an object and also an X509Certificate that is used
 * when signing. This object is typically associated with Users (for object signing)
 * and Organisations (for logon ticket generation). It is stored in a separate
 * database table so that it is never given out directly by the server. 
 *
 * @author hugo
 */
public class KeyData {
    /** Signing algorithm */
    public static final String sigAlg = "SHA1withDSA";

    /** Keystore password */
    private char[] password = new String("sT0r3pa33worD").toCharArray();

    /** Private key */
    private PrivateKey privateKey;

    /** X509Certificate */
    private X509Certificate certificate;

    /** Database ID of this key data */
    private String id;

    /** Object that this key information belongs to */
    private String objectId;

    /** Keystore containing everything */
    private KeyStore store;

    /** Keystore byte array data */
    private byte[] keystoreData;

    /** Set up the BouncyCastle security provider */
    /*
    static {
        try {
            if(Security.getProvider("BC")==null){
                Security.insertProviderAt(new BouncyCastleProvider(), 2);
                System.out.println("Added BouncyCastle security provider");
            }
        } catch (Exception e){
            System.out.println("Cannot add BouncyCastle security provider: " + e.getMessage());
        }
    } 
    */

    /** Creates a new instance of KeyData */
    public KeyData() {
    }

    /** Get the encoded certificate data */
    public byte[] getEncodedCertificate(){
        // Expand the keystore if there is no certificate present
        if(certificate==null){
            expandKeyStore();
        }
        try {
            return certificate.getEncoded();
        } catch (Exception e){
            return new byte[0];
        }
    }

    /** Set the encoded certificate data */
    public void setEncodedCertificate(byte[] encodedCertificate){
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(encodedCertificate);
            certificate = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(stream);
        } catch (Exception e){
            System.out.println("Error loading encoded certificate: " + e.getMessage());
            certificate = null;
        }
    }

    /** Get the id of this key data */
    public String getId(){
        return id;
    }

    /** Set the id of this key data */
    public void setId(String id){
        this.id = id;
    }

    /** Get the object ID */
    public String getObjectId(){
        return objectId;
    }

    /** Set the object ID */
    public void setObjectId(String objectId){
        this.objectId = objectId;
    }

    /** Get the data as a KeyStore byte array */
    public byte[] getKeyStoreData(){
        return keystoreData;
    }

    /** Create the keystore data from the physical keystore */
    private void createKeystoreData(){
        if(store!=null){
            try {
                ByteArrayOutputStream storeStream = new ByteArrayOutputStream();
                store.store(storeStream, password);
                keystoreData = storeStream.toByteArray();
            } catch (Exception e){
                System.out.println("Error saving keystore: " + e.getMessage());
                keystoreData = new byte[0];
            }
        }
    }

    /** Set the data as a KeyStore byte array */
    public void setKeyStoreData(byte[] data){
        keystoreData = data;
    }

    /** Get the private key */
    public PrivateKey getPrivateKey(){
        if(privateKey!=null){
            return privateKey;
        } else {
            if(keystoreData!=null){
                if(expandKeyStore()){
                    return privateKey;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /** Get the X509 certificate */
    public X509Certificate getCertificate(){
        if(certificate!=null){
            return certificate;
        } else {
            if(keystoreData!=null){
                if(expandKeyStore()){
                    return certificate;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /** Get the key and certificate from the keystore. Returns true if sucessful, 
     * false if there was an error */
    private boolean expandKeyStore(){
        if(keystoreData!=null){
            try {
                ByteArrayInputStream stream = new ByteArrayInputStream(keystoreData);
                store = KeyStore.getInstance("JKS");
                store.load(stream, password);

                KeyStore.PrivateKeyEntry pke = (KeyStore.PrivateKeyEntry)store.getEntry("MyKey", new KeyStore.PasswordProtection(password));
                certificate = (X509Certificate)pke.getCertificate();
                privateKey = pke.getPrivateKey();
                return true;
            } catch (Exception e){
                privateKey = null;
                certificate = null;
                System.out.println("Cannot open keystore: " + e.getMessage());
                return false;
            }
        } else {
            return false;
        }
    }

    /** Create certificate and keys */
    public final void initialiseKeys(String userDN, String issuerDN, int validity) throws Exception
    {
        // Keytool location
        String javaHome = System.getProperty("java.home");
        ArrayList<String> keyToolArgs = new ArrayList<>();
        String keyTool = javaHome + File.separator + "bin" + File.separator + "keytool";

        File tmpKeystore = File.createTempFile("key", ".tmp");
        tmpKeystore.delete();   // Delete now, otherwise keytool will not run

        File tmpCert = File.createTempFile("cert", ".cer");

        // Construct the command line for keytool
        keyToolArgs.add(keyTool);
        keyToolArgs.add("-genkey");
        keyToolArgs.add("-alias");
        keyToolArgs.add("MyKey");
        keyToolArgs.add("-keyalg");
        keyToolArgs.add("DSA");
        keyToolArgs.add("-keysize");
        keyToolArgs.add("1024");
        keyToolArgs.add("-keystore");
        keyToolArgs.add(tmpKeystore.getPath());
        keyToolArgs.add("-sigalg");
        keyToolArgs.add("DSA");
        keyToolArgs.add("-dname");
        keyToolArgs.add(userDN);
        keyToolArgs.add("-validity");
        keyToolArgs.add(Integer.toString(validity * 365));
        keyToolArgs.add("-keypass");
        String passStr = new String(password);
        keyToolArgs.add(passStr);
        keyToolArgs.add("-storepass");
        keyToolArgs.add(passStr);
        keyToolArgs.add("-storetype");
        keyToolArgs.add("JKS");
        String[] keyToolArgsArr =  keyToolArgs.toArray(new String[0]);
        System.out.println(Arrays.toString(keyToolArgsArr));

        Process keytool = Runtime.getRuntime().exec(keyToolArgsArr);
        keytool.waitFor();

        // Export the certificate
        keyToolArgs = new ArrayList<>();
        keyToolArgs.add(keyTool);
        keyToolArgs.add("-export");
        keyToolArgs.add("-alias");
        keyToolArgs.add("MyKey");
        keyToolArgs.add("-file");
        keyToolArgs.add(tmpCert.getPath());
        keyToolArgs.add("-keystore");
        keyToolArgs.add(tmpKeystore.getPath());
        keyToolArgs.add("-storepass");
        keyToolArgs.add(passStr);

        Process export = Runtime.getRuntime().exec(keyToolArgs.toArray(new String[0]));
        export.waitFor();

        // Load the created keystore and certificate back into memory
        keystoreData = loadFile(new File(tmpKeystore.getPath()));

        ByteArrayInputStream stream = new ByteArrayInputStream(keystoreData);
        store = KeyStore.getInstance("JKS");
        store.load(stream, password);  

        KeyStore.PrivateKeyEntry pke = (KeyStore.PrivateKeyEntry)store.getEntry("MyKey", new KeyStore.PasswordProtection(password));
        certificate = (X509Certificate)pke.getCertificate();
        privateKey = pke.getPrivateKey();

        // Delete the temporary files
        if(tmpKeystore.exists()){
            if(!tmpKeystore.delete()){
                tmpKeystore.deleteOnExit();
            }
        }

        if(!tmpCert.delete()){
            tmpCert.deleteOnExit();
        }
    }

    /** Load a file into a byte array */
    private byte[] loadFile(File file) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[1024];
        int bytes;
        while((bytes=stream.read(buffer))!=-1){
            outStream.write(buffer, 0, bytes);
        }
        outStream.flush();
        outStream.close();
        stream.close();
        return outStream.toByteArray();
    }
}
