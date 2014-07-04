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
package com.connexience.server.workflow.api.downloaders;

import com.connexience.server.workflow.api.*;

import com.connexience.server.workflow.util.ZipUtils;
import com.connexience.server.*;
import com.connexience.server.model.document.DocumentVersion;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * This class contacts the main server via http to download files.
 * @author hugo
 */
public class HttpDownloader extends Downloader {
    private static Logger logger = Logger.getLogger(HttpDownloader.class);
    
    private static File temporaryDir;

    File tempFile;
    int retryCount = 20;
    int initialRetryInterval = 1000;
    int maxWaitInterval = 10000;
    double retryMultiplier = 2;


    public static void setTemporaryDir(String path) {
        if (path == null) {
            temporaryDir = null;
        }

        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                logger.warn("Setting temporary directory has failed: cannot create directory '" + path + "'");
            }
        }
        if (!dir.canWrite()) {
            logger.warn("Setting temporary directory has failed: write access denied to path '" + path + "'");
            dir = null;
        }

        if (dir == null) {
            logger.warn("Using default temporary-file directory: " + (temporaryDir == null ? "system dependent": temporaryDir.getPath()));
        } else {
            logger.debug("HttpDownloader is using temporary-file directory: '" + dir + "'");
            temporaryDir = dir;
        }
    }


    public static String getTemporaryDir() {
        return temporaryDir.getPath();
    }


    public HttpDownloader() {
    }
     
    private URL createUrl() throws ConnexienceException {
        try {
            URL downloadUrl = new URL("http://" + parent.getHostName() + ":" + parent.getHttpPort() + parent.getServerContext() + "/data");
            return downloadUrl;
        } catch (Exception e){
            throw new ConnexienceException("Cannot create download URL: " + e.getMessage(), e);
        }
    }
    
    public boolean download() throws ConnexienceException {
        logger.debug("Attempting to download: " + document.getName());
        try {
            URL url = createUrl();
            
            int count = 0;
            double waitTime = initialRetryInterval;
            for(count = 1;count<=retryCount;count++){
                if(!attemptDownload(url)){
                    logger.debug("Recoverable download error. Waiting for: " + waitTime);
                    try {
                        Thread.sleep((int)waitTime);
                    }  catch (Exception e){}
                    waitTime = waitTime * retryMultiplier;
                    if(waitTime>=maxWaitInterval){
                        waitTime = maxWaitInterval;
                    }
                } else {                    
                    logger.debug("Download succeeded after: " + count + " attempts");
                    if(tempFile!=null && tempFile.exists()){
                        try {
                            ZipUtils.copyFileToOutputStream(tempFile, stream);
                            
                            // Download OK
                            logDataRead();
                            return true;
                        } catch (Exception e){
                            throw new ConnexienceException("Error copying data from temporary file: " + e.getMessage());
                        }
                    } else {
                        throw new ConnexienceException("No temporary file");
                    }
                    

                }
            }
            return false;        
        } catch (ConnexienceException e){
            logger.error("Download exception: " + e.getMessage(), e);
            throw e;
        } finally {
            removeTempFile();
        }        
    }
    
    public InputStream getInputStream() throws ConnexienceException {
        try {
            HttpURLConnection connection = createConnection(createUrl());

            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                InputStream inStream = connection.getInputStream();
                logDataRead();
                //return new DownloaderInputStream(this, inStream);
                if (inStream instanceof BufferedInputStream) {
                    return inStream;
                } else {
                    return new BufferedInputStream(inStream);
                }
            } else {
                throw new ConnexienceException("Server returned error code: " + connection.getResponseCode() + " downloading file: " + connection.getResponseMessage());
            }        
            
        } catch (SocketTimeoutException stoe){
            throw new ConnexienceException("Socket timeout", stoe);
            
        } catch (SocketException se){
            throw new ConnexienceException("Socket exception", se);
            
        } catch (IOException ioe){
            throw new ConnexienceException("IOException", ioe);
            
        }       
    }
    
    private boolean attemptDownload(URL url) throws ConnexienceException {
        FileOutputStream fileStream = null;
        try {
            // Create a temp file
            if(tempFile!=null && tempFile.exists()){
                tempFile.delete();
            }
            tempFile = File.createTempFile("download-", ".dat", temporaryDir);
            fileStream = new FileOutputStream(tempFile);
            
            HttpURLConnection connection = createConnection(url);
            
            byte[] buffer = new byte[4096];
            int len;

            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                BufferedInputStream inStream = new BufferedInputStream(connection.getInputStream());
                while((len=inStream.read(buffer))>0){
                    fileStream.write(buffer, 0, len);
                }
                inStream.close();
                fileStream.flush();
                fileStream.close();
                
                // Check the content length matches the file size
                if(connection.getContentLength()==tempFile.length()){
                    return true;
                } else {
                    logger.debug("Expected file size did not match actual file size. CL=" + connection.getContentLength() + " FL=" + tempFile.length() + ". Recoverable");
                    return false;
                }
            } else {
                throw new ConnexienceException("Server returned error code: " + connection.getResponseCode() + " downloading file: " + connection.getResponseMessage());
            }        
            
        } catch (SocketTimeoutException stoe){
            logger.debug("Socket timeout exception. Recoverable");
            return false;
            
        } catch (SocketException se){
            logger.debug("Socket exception downloading. Revoverable");
            return false;
            
        } catch (IOException ioe){
            logger.debug("IOError downloading. Recoverable");
            return false;
        } finally {
            if(fileStream!=null){
                try {fileStream.flush();}catch (Exception e){}
                try {fileStream.close();}catch (Exception e){}
            }
        }
    }
    
    private HttpURLConnection createConnection(URL url) throws SocketTimeoutException, SocketException, IOException, ConnexienceException {
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        if(versionId==null && versionNumber==-1){
            // No version or number
            connection.addRequestProperty("documentid", document.getId());
            connection.addRequestProperty("type", "document");                

            // Latest version
            versionId = parent.getLatestVersionId(document.getId());
            versionNumber = document.getCurrentVersionNumber();
            connection.addRequestProperty("versionid", versionId);
            
        } else if(versionId!=null){
            // Actual version
            connection.addRequestProperty("documentid", document.getId());
            connection.addRequestProperty("type", "document");
            connection.addRequestProperty("versionid", versionId);
            
            
        } else if(versionId==null && versionNumber!=-1){
            // Version number
            connection.addRequestProperty("documentid", document.getId());
            connection.addRequestProperty("type", "document");
            
            // Get the version ID 
            DocumentVersion version = parent.getDocumentVersion(document, versionNumber);
            versionId = version.getId();
            connection.addRequestProperty("versionid", version.getId());
        }

        // Send in a serialized ticket
        if(parent.getTicket()!=null){
            connection.addRequestProperty("userid", parent.getTicket().getUserId());
        }
        
        // If the client is not using session security, send in some provenance
        // data if it exists
        if(parent.getProvenanceProperties()!=null){
            connection.addRequestProperty("invocationid", parent.getProvenanceProperties().stringValue("InvocationID", ""));
            connection.addRequestProperty("blockuuid", parent.getProvenanceProperties().stringValue("BlockUUID", ""));
        }
        return connection;
    }
    
    private void removeTempFile(){
        if(tempFile!=null && tempFile.exists()){
            if(!tempFile.delete()){
                tempFile.deleteOnExit();
            }
        }
    }
}
