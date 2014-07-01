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
package org.pipeline.core.xmlstorage.prefs;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.security.KeyData;
import com.connexience.server.util.SignatureUtils;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.io.XmlDataStoreByteArrayIO;
import org.pipeline.core.xmlstorage.io.XmlDataStoreStreamReader;
import org.pipeline.core.xmlstorage.io.XmlDataStoreStreamWriter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * This class manages all of the preferences for the desktop application.
 *
 * @author hugo
 */
public abstract class PreferenceManager {

    private static Logger logger = Logger.getLogger(PreferenceManager.class);
    /**
     * Editable properties
     */
    private static Hashtable<String, XmlDataStore> editableProperties = new Hashtable<>();

    /**
     * Non-editable properties
     */
    private static Hashtable<String, XmlDataStore> systemProperties = new Hashtable<>();

    /**
     * Storage directory
     */
    private static File storageDir;

    /**
     * Preference file name
     */
    private static String fileName;

    /**
     * Private key for signing
     */
    private static PrivateKey privateKey = null;

    /**
     * Certificate for validating
     */
    private static X509Certificate certificate = null;

    /**
     * UserID extracted from the certificate
     */
    private static String certificateOwnerId = null;

    /**
     * Have the properties already been loaded
     */
    private static boolean loadPerformed = false;

    /** Config server URL */
    private static URL networkConfigServerUrl;
    
    /** Have these preferences been loaded from a network */
    private static boolean networkStore = false;
    
    /** Client IP address */
    private static String networkClientIP;
    
    /** Network config folder name */
    private static String networkConfigFolder = "preferences";
    
    /** Network preferences file name */
    private static String networkFileName = "config.xml";
    
    
    /**
     * Get the storage directory
     */
    public static File getStorageDirectory() {
        return storageDir;
    }

    public static boolean isLoadPerformed() {
        return loadPerformed;
    }

    /**
     * Store the last used directory
     */
    public static void storeLastDir(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            getEditablePropertyGroup("Filesystem").add("WorkingDirectory", directory);
        }
    }

    /**
     * Get the loaded preference file
     */
    public static File getLoadedPreferenceFile() {
        if (storageDir != null && fileName != null) {
            return new File(storageDir, fileName);
        } else {
            return null;
        }
    }

    /**
     * Get the last used directory
     */
    public static File getLastDir() {
        File dir = getEditablePropertyGroup("Filesystem").fileValue("WorkingDirectory", System.getProperty("user.dir"));
        if (dir.exists()) {
            return dir;
        } else {
            return new File(System.getProperty("user.home"));
        }
    }


    /**
     * Load the keystore from a directory
     */
    public static boolean loadKeystore(File directory, String keystoreName) {
        storageDir = directory;
        File keystoreFile = new File(storageDir, keystoreName);
        if (keystoreFile.exists()) {
            try {
                byte[] data = loadFile(keystoreFile);
                return loadKeystoreFromByteArray(data);
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Load the keystore from a file
     */
    public static boolean loadKeystoreFromFile(File keystoreFile) {
        return loadKeystore(keystoreFile.getParentFile(), keystoreFile.getName());
    }

    /**
     * Load the keystore from a subdirectory of the home directory
     */
    public static boolean loadKeystoreFromHomeDir(String subdirectory, String keystoreName) {
        File dir = new File(System.getProperty("user.home") + File.separator + subdirectory);
        return loadKeystore(dir, keystoreName);
    }

    /**
     * Get a file from the home directory
     */
    public static File getFileFromHomeDir(String subdirectory, String fileName) {
        return new File(new File(System.getProperty("user.home") + File.separator + subdirectory), fileName);
    }

    /**
     * Read a single line file from the home directory
     */
    public static String readSingleLineFileFromHomeDir(String subdirectory, String fileName) throws IOException {
        File f = getFileFromHomeDir(subdirectory, fileName);
        if (f.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            try {
                String line = reader.readLine();
                if (line != null) {
                    return line;
                } else {
                    throw new IOException("File: " + fileName + " contains no data");
                }
            } finally {
                reader.close();
            }
        } else {
            throw new IOException("File: " + fileName + " not found");
        }
    }

    /**
     * Create a single line file in the home directory
     */
    public static void createSingleLineFileInHomeDir(String subdirectory, String fileName, String contents) throws IOException {
        File f = getFileFromHomeDir(subdirectory, fileName);
        PrintWriter writer = new PrintWriter(f);
        writer.println(contents);
        writer.flush();
        writer.close();
    }

    /**
     * Load the keystore from a byte array
     */
    public static boolean loadKeystoreFromByteArray(byte[] fileData) {
        try {
            KeyData kd = new KeyData();
            kd.setKeyStoreData(fileData);
            privateKey = kd.getPrivateKey();
            certificate = kd.getCertificate();
            certificateOwnerId = SignatureUtils.getOwnerId(certificate);
            return true;

        } catch (Exception e) {
            System.out.println("Error loading keystore: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the private key
     */
    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * Get the certificate
     */
    public static X509Certificate getCertificate() {
        return certificate;
    }

    /**
     * Get the id of the certificate owner
     */
    public static String getCertificateOwnerId() {
        return certificateOwnerId;
    }

    /**
     * Try and find an IP address from a server
     */
    public static String getIpAddressFromConfigServer(String hostname, int port) throws ConnexienceException {
        BufferedReader reader = null;
        try {
            URL u = new URL("http://" + hostname + ":" + port + "/workflow/ipaddress");
            URLConnection c = u.openConnection();
            reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
            return reader.readLine();
        } catch (Exception e){
            throw new ConnexienceException("IP Address error: " + e.getMessage(), e);
        } finally {
            try {reader.close();}catch(Exception e){}
        }
    }
    
    /**
     * Load properties from a config server
     */
    public static boolean loadPropertiesFromConfigServer(URL baseUrl, String configDirName, String clientIP, String fileName){
        XmlDataStore specificProperties = null;
        
        // Store location data so that data can be saved
        networkConfigServerUrl = baseUrl;
        networkFileName = fileName;
        networkClientIP = clientIP;
        networkStore = true;
        networkConfigFolder = configDirName;
        
        try {
            String loadUrl = baseUrl.toString() + "/" + configDirName + "/" + clientIP + "/" + fileName;
            
            logger.debug("Attempting to load client specific properties from: " + loadUrl);
            specificProperties = loadStoreFromUrl(loadUrl);
        } catch (FileNotFoundException fnfe){
            // File does not exist
            logger.debug("Client specific config not found.");
        } catch (Exception e){
            logger.error("Exception loading config from server: " + e.getMessage());
            return false;
        }
        
        if(specificProperties!=null){
            logger.debug("Properties loaded OK");
            mergeInProperties(specificProperties);
            return true;
        } else {
            // Need to search for defaults
            String defaultsUrl = baseUrl.toString() + "/" + configDirName + "/" + fileName;
            logger.debug("Attempting to load generic settings: " + defaultsUrl);
            try {
                XmlDataStore defaultProperties = loadStoreFromUrl(defaultsUrl);
                if(defaultProperties!=null){
                    mergeInProperties(defaultProperties);
                    logger.warn("USED DEFAULT CONFIGURATION SETTINGS FROM SERVER. MAKING COPY.");
                    saveNetworkProperties();
                    return true;
                } else {
                    logger.error("No default properties loaded");
                    return false;
                }
            } catch (FileNotFoundException fnfe){
                logger.error("No default config found");
                return false;
            } catch (Exception e){
                logger.error("Exception loading default properties from config server: " + e.getMessage());
                return false;
            }
        }
    }   
    
    private static XmlDataStore loadStoreFromUrl(String url) throws FileNotFoundException, ConnexienceException {
        InputStream stream = null;
        
        try {
            URL configFileUrl = new URL(url);
            URLConnection c = configFileUrl.openConnection();
            
            if(c instanceof HttpURLConnection){
                HttpURLConnection http = (HttpURLConnection)c;
                if(http.getResponseCode()==HttpURLConnection.HTTP_OK){
                    stream = http.getInputStream();
                    XmlDataStoreStreamReader reader = new XmlDataStoreStreamReader(stream);
                    return reader.read();
                } else if(http.getResponseCode()==HttpURLConnection.HTTP_NOT_FOUND) {
                    throw new FileNotFoundException();
                } else {
                    throw new ConnexienceException("Unexpected response code: " + http.getResponseCode());
                }
            } else {
                throw new ConnexienceException("Wrong kind of server");
            }
            
        } catch (FileNotFoundException fnfe){
            throw fnfe;
        
        } catch (Exception e){
            throw new ConnexienceException("Error loading properties from server: " + e.getMessage(), e);
        }
    }
    
    /**
     * Load properties from a file
     */
    public static boolean loadPropertiesFromFile(File file) {
        String path = file.getParent();
        String name = file.getName();
        return loadProperties(new File(path), name);
    }

    /**
     * Load properties from a subdirectory in the users home directory
     */
    public static boolean loadPropertiesFromHomeDir(String subdirectory, String preferenceFileName) {
        File dir = new File(System.getProperty("user.home") + File.separator + subdirectory);
        return loadProperties(dir, preferenceFileName);
    }

    /**
     * Load all of the properties from the specified file
     */
    public static boolean loadProperties(File storageDirectory, String preferenceFileName) {
        if (!loadPerformed) {
            storageDir = storageDirectory;
            fileName = preferenceFileName;
            if (!storageDir.exists()) {
                if (!storageDir.mkdirs()) {
                    System.out.println("Cannot create property directory");
                    return false;
                }
            }

            File propertiesFile = new File(storageDir, fileName);

            if (propertiesFile.exists()) {
                FileInputStream stream = null;
                try {
                    stream = new FileInputStream(propertiesFile);
                    XmlDataStoreStreamReader reader = new XmlDataStoreStreamReader(stream);
                    XmlDataStore properties = reader.read();
                    mergeInProperties(properties);

                } catch (Exception e) {
                    System.out.println("Cannot load properties file: " + e.getMessage());
                } finally {
                    try {
                        stream.close();
                    } catch (Exception e) {
                    }
                }


                loadPerformed = true;
            } else {
                loadPerformed = false;
            }
        } else {
            logger.warn("Properties have already been loaded from: " + storageDirectory.getPath() + File.separator + fileName);
        }
        return loadPerformed;
    }

    /**
     * Merge in a new set of properties
     */
    private static void mergeInProperties(XmlDataStore properties) {
        try {
            // Take a copy of the existing properties
            Hashtable<String, XmlDataStore> systemBackup = systemProperties;
            Hashtable<String, XmlDataStore> editableBackup = editableProperties;
            systemProperties = new Hashtable<>();
            editableProperties = new Hashtable<>();

            XmlDataStore props;
            // Load the editable property groups
            Vector<?> names;
            String name;

            if (properties.containsName("EditableProperties")) {
                XmlDataStore editableProps = properties.xmlDataStoreValue("EditableProperties");
                names = editableProps.getNames();
                for (int i = 0; i < names.size(); i++) {
                    name = (String) names.get(i);
                    if (editableProps.containsName(name)) {
                        props = editableProps.xmlDataStoreValue(name);
                        props.setAllowAddRemove(true);
                        editableProperties.put(name, props);
                    }
                }
            }

            // Load the system property groups
            if (properties.containsName("SystemProperties")) {
                XmlDataStore systemProps = properties.xmlDataStoreValue("SystemProperties");
                names = systemProps.getNames();
                for (int i = 0; i < names.size(); i++) {
                    name = (String) names.get(i);
                    if (systemProps.containsName(name)) {
                        props = systemProps.xmlDataStoreValue(name);
                        props.setAllowAddRemove(true);
                        systemProperties.put(name, props);
                    }
                }
            }
            
            // Merge the backups
            XmlDataStore mergeStore;
            for (String key : systemBackup.keySet()) {
                mergeStore = systemBackup.get(key);
                if (!systemProperties.containsKey(key)) {
                    // Add group
                    systemProperties.put(key, mergeStore);
                } else {
                    // Merge properties
                    try {
                        systemProperties.get(key).mergeProperties(mergeStore);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            for (String key : editableBackup.keySet()) {
                mergeStore = editableBackup.get(key);
                if (!editableProperties.containsKey(key)) {
                    // Add group
                    editableProperties.put(key, mergeStore);
                } else {
                    // Merge properties
                    try {
                        editableBackup.get(key).mergeProperties(mergeStore);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Save the properties to file
     */
    public static void saveProperties() {
        if(!networkStore){
            // Filesystem store
            if (!storageDir.exists()) {
                if (!storageDir.mkdirs()) {
                    System.out.println("Cannot create property directory");
                    return;
                }
            }

            File propertiesFile = new File(storageDir, fileName);

            FileOutputStream stream = null;
            try {
                XmlDataStore propertyStore = getAllProperties();
                stream = new FileOutputStream(propertiesFile);
                XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(propertyStore);
                writer.setDescriptionIncluded(true);
                writer.prettyPrint(stream);

            } catch (Exception e) {
               logger.error("Error saving properties file: " + e.getMessage());
            } finally {
                try {
                    stream.flush();
                    stream.close();
                } catch (Exception e) {
                }
            }
        } else {
            // Config server store
            logger.info("Not saving properties to config server");
        }
    }

    public static void saveNetworkProperties(){
        logger.debug("Saving properties to network config server");
        OutputStream stream = null;
        try {
            String propertiesUrl = networkConfigServerUrl.toString() + "/" + networkConfigFolder + "/" + networkClientIP + "/" + networkFileName;
            URL u = new URL(propertiesUrl);
            HttpURLConnection c = (HttpURLConnection)u.openConnection();
            c.setRequestMethod("POST");
            c.setDoOutput(true);
            stream = c.getOutputStream();
            XmlDataStore propertyStore = getAllProperties();
            XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(propertyStore);
            writer.write(stream);

            InputStream inputStream = c.getInputStream();
            while(inputStream.read()!=-1){

            }
            inputStream.close();
        } catch (Exception e){
            logger.error("Error saving properties to config server: " + e.getMessage());
        } finally {
            try {
                stream.flush();
                stream.close();
            } catch (Exception e){
            }
        }        
    }
    /**
     * Print the properties to System.out
     */
    public static void debugPrint() {
        try {
            System.out.println();
            PrintWriter writer = new PrintWriter(System.out);
            getAllProperties().debugPrint(writer, 4);
            writer.flush();
            writer.close();
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all of the properties as an XmlDataStore
     */
    public static XmlDataStore getAllProperties() throws Exception {
        XmlDataStore propertyStore = new XmlDataStore("Properties");

        // Save all of the editable properties
        Enumeration<String> keys = editableProperties.keys();
        XmlDataStore editableProps = new XmlDataStore("EditableProperties");
        String key;

        while (keys.hasMoreElements()) {
            key = keys.nextElement();
            editableProps.add(key, editableProperties.get(key));
        }
        propertyStore.add("EditableProperties", editableProps);

        // Save all of the system properties
        keys = systemProperties.keys();
        XmlDataStore systemProps = new XmlDataStore("SystemProperties");
        while (keys.hasMoreElements()) {
            key = keys.nextElement();
            systemProps.add(key, systemProperties.get(key));
        }
        propertyStore.add("SystemProperties", systemProps);
        return propertyStore;
    }

    /**
     * Get all of the properties as a single XmlString
     */
    public static String getAllPropertiesAsXmlString() throws Exception {
        XmlDataStore propertyStore = getAllProperties();
        XmlDataStoreByteArrayIO writer = new XmlDataStoreByteArrayIO(propertyStore);
        return new String(writer.toByteArray());
    }

    /**
     * Get an editable property group. This creates a new group if it doesn't
     * already exist
     */
    public static XmlDataStore getEditablePropertyGroup(String name) {
        if (editableProperties.containsKey(name)) {
            return editableProperties.get(name);
        } else {
            XmlDataStore store = new XmlDataStore(name);
            store.setAutoAddProperties(true);
            editableProperties.put(name, store);
            return store;
        }
    }

    /**
     * Get an editable property group. This creates a new group if it doesn't
     * already exist
     */
    public static XmlDataStore getSystemPropertyGroup(String name) {
        if (systemProperties.containsKey(name)) {
            return systemProperties.get(name);
        } else {
            XmlDataStore store = new XmlDataStore(name);
            store.setAutoAddProperties(true);
            systemProperties.put(name, store);
            return store;
        }
    }

    /**
     * List the system property group names
     */
    public static ArrayList<String> getSystemPropertyGroupNames() {
        ArrayList<String> results = new ArrayList<>();
        Enumeration<String> i = systemProperties.keys();
        while (i.hasMoreElements()) {
            results.add(i.nextElement());
        }
        return results;
    }

    /**
     * List the editable property group names
     */
    public static ArrayList<String> getEditablePropertyGroupNames() {
        ArrayList<String> results = new ArrayList<>();
        Enumeration<String> i = editableProperties.keys();
        while (i.hasMoreElements()) {
            results.add(i.nextElement());
        }
        return results;
    }

    /**
     * Load a file into a byte array
     */
    private static byte[] loadFile(File file) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[1024];
        int bytes;
        while ((bytes = stream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytes);
        }
        outStream.flush();
        outStream.close();
        stream.close();
        return outStream.toByteArray();
    }
}
