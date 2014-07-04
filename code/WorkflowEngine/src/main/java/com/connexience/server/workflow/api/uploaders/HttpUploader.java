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
package com.connexience.server.workflow.api.uploaders;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.workflow.api.Uploader;
import com.connexience.server.workflow.util.ZipUtils;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.apache.log4j.Logger;


/**
 * This class uploads data via an http post to the main server.
 * @author hugo
 */
public class HttpUploader extends Uploader {
    private static Logger logger = Logger.getLogger(HttpUploader.class);

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
            if (!dir.mkdirs()) {
                logger.warn("Setting temporary-file directory has failed: cannot create directory '" + path + "'");
            }
        }
        if (!dir.canWrite()) {
            logger.warn("Setting temporary-file directory has failed: write access denied to path '" + path + "'");
            dir = null;
        }

        if (dir == null) {
            logger.warn("Using default temporary-file directory: " + (temporaryDir == null ? "system dependent": temporaryDir.getPath()));
        } else {
            logger.debug("HttpUploader is using temporary-file directory: '" + dir + "'");
            temporaryDir = dir;
        }
    }


    public static String getTemporaryDir() {
        return temporaryDir.getPath();
    }


    private void copyStreamToTempFile() throws IOException {
        tempFile = File.createTempFile("upload-", ".dat", temporaryDir);
        logger.debug("copyStreamToTempFile: Creating temporary file: " + tempFile);
        FileOutputStream outStream = new FileOutputStream(tempFile);
        ZipUtils.copyInputStream(stream, outStream);
        outStream.flush();
        outStream.close();
    }
    
    private URL createUrl() throws ConnexienceException {
        try {
            URL downloadUrl = new URL("http://" + parent.getHostName() + ":" + parent.getHttpPort() + parent.getServerContext() + "/data");
            return downloadUrl;
        } catch (Exception e){
            throw new ConnexienceException("Cannot create download URL: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean upload() throws ConnexienceException {
        logger.debug("Attempting to upload: " + document.getName());
        URL uploadUrl = createUrl();
        try {
            copyStreamToTempFile();
        } catch (IOException e){
            throw new ConnexienceException("Error copying data to temporary file: " + e.getMessage(), e);
        } finally {
            try {
                stream.close();
            } catch (Exception e){}
        }
        
        try {
            int count = 0;
            double waitTime = initialRetryInterval;
            for(count = 1;count<=retryCount;count++){
                if(!attemptUpload(uploadUrl)){
                    logger.debug("Recoverable upload error. Waiting for: " + waitTime);
                    try {
                        Thread.sleep((int)waitTime);
                    }  catch (Exception e){}
                    waitTime = waitTime * retryMultiplier;
                    if(waitTime>=maxWaitInterval){
                        waitTime = maxWaitInterval;
                    }
                } else {
                    logger.debug("Upload succeeded after: " + count + " attempts");
                    logDataWrite();
                    return true;
                }
            }
            return false;        
        } catch (ConnexienceException e){
            logger.error("Upload exception: " + e.getMessage(), e);
            throw e;
        } finally {
            removeTempFile();
        }        
    }
    
    private boolean attemptUpload(URL uploadUrl) throws ConnexienceException {
        InputStream sourceStream = null;
        
        try {
            sourceStream = new FileInputStream(tempFile);
            HttpURLConnection connection = (HttpURLConnection)uploadUrl.openConnection();
            connection.setDoOutput(true);
            connection.addRequestProperty("documentid", document.getId());
            connection.setChunkedStreamingMode(32768);
            
            // Send in the class name for the uploaded document - services, workflows and libraries are treated differently
            connection.addRequestProperty("classname", document.getClass().getName());

            // Send in a serialized ticket
            if(parent.getTicket()!=null){
                connection.addRequestProperty("userid", parent.getTicket().getUserId());
            }
            
            byte[] buffer = new byte[4096];
            int len;
            
            BufferedOutputStream outStream = new BufferedOutputStream(connection.getOutputStream());
            while((len=sourceStream.read(buffer))>0){
                outStream.write(buffer, 0, len);
            }
            outStream.flush();
            outStream.close();
            sourceStream.close();

            if(connection.getResponseCode()==500){
                // Upload problem
                return false;
            }
            
            InputStream inStream = connection.getInputStream();  
            try {
                uploadedDocumentVersion = parent.getMapper().readValue(inStream, DocumentVersion.class);
                return true; 
            } catch (Exception e){
                throw new ConnexienceException("Error uploading data: " + e.getMessage(), e);
            }
                
            
        } catch (SocketTimeoutException stoe){
            logger.debug("Socket timeout exception in uploader. Recoverable");
            return false;
            
        } catch (SocketException e){
            // Plain socket exception
            logger.debug("Socket exception in uploader. Recoverable");
            return false;
            
        } catch (IOException ioe){
            // Plain IO Error
            logger.debug("IO exception in uploader. Recoverable");
            return false;

        } finally {
            if(sourceStream!=null){
                try {
                    sourceStream.close();
                } catch (Exception e){}
            }
        }
    }
    
    private void removeTempFile(){
        if(!tempFile.delete()){
            tempFile.deleteOnExit();
        }
    }    
}
