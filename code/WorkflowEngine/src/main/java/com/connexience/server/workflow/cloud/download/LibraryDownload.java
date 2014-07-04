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
package com.connexience.server.workflow.cloud.download;

import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.workflow.cloud.library.*;
import com.connexience.server.workflow.api.*;
import java.util.*;
import java.io.*;
import org.apache.log4j.*;

/**
 * This class manages the download and unpacking of a single library
 * @author nhgh
 */
public class LibraryDownload implements Runnable {
    static Logger logger = Logger.getLogger(LibraryDownload.class);
    
    /** ID of the document on the server */
    private String documentId;

    /** Version number to download */
    private String versionId;

    /** Parent download manager */
    private DownloadManager parent;

    /** Download listeners */
    private ArrayList<LibraryDownloadListener> listeners = new ArrayList<>();

    /** Is the download active */
    private boolean downloadActive = false;

    /** API link for downloading */
    private API apiLink;

    /** Downloaded library item object */
    private CloudWorkflowServiceLibraryItem libraryItem = null;

    /** Report for storing messages */
    private LibraryPreparationReport report = null;
    
    /** Create with parent download manager and ids */
    public LibraryDownload(DownloadManager parent, API apiLink, String documentId, String versionId, LibraryPreparationReport report){
        this.parent = parent;
        this.documentId = documentId;
        this.versionId = versionId;
        this.report = report;
        this.apiLink = apiLink;
    }

    /** Add a listener to this download */
    public void addLibraryDownloadListener(LibraryDownloadListener listener){
        listeners.add(listener);
    }

    /** Remove a listener from this download */
    public void removeLibraryDownloadListener(LibraryDownloadListener listener){
        listeners.remove(listener);
    }
    
    /** Get the document ID */
    public String getDocumentId() {
        return documentId;
    }

    /** Get the version id */
    public String getVersionId(){
        return versionId;
    }

    /** Is the download currently active */
    public synchronized boolean isDownloadActive(){
        return downloadActive;
    }

    /** Set whether the download is active */
    public void setDownloadActive(boolean downloadActive){
        this.downloadActive = downloadActive;
    }

    /** Start the download process in a separate thread */
    public void startDownload(){
        logger.debug("Starting download for: " + documentId + " v " + versionId);
        if(!isDownloadActive()){
            setDownloadActive(true);
            new Thread(this).start();
        }
    }

    /** Notify a download error */
    private void finishWithError(String message){
        logger.debug("Download for: " + documentId + " v " + versionId + " had error: " + message);
        setDownloadActive(false);
        for(int i=0;i<listeners.size();i++){
            listeners.get(i).downloadError(message);
        }
        parent.downloadComplete(this);
    }

    /** Notify that download completed OK */
    private void finishOk(){
        logger.debug("Download for: " + documentId + " v " + versionId + " finished ok");
        setDownloadActive(false);
        parent.downloadComplete(this);
        for(int i=0;i<listeners.size();i++){
            listeners.get(i).downloadComplete(libraryItem);
        }
    }

    /** Perform the actual download */
    public void run(){
        logger.debug("Starting download thread for: " + documentId + " v " + versionId);
        try {
            DocumentRecord doc = apiLink.getDocument(documentId);
            if(doc!=null){
                List<DocumentVersion> versions = apiLink.getDocumentVersions(doc);

                DocumentVersion version = null;
                for(int i=0;i<versions.size();i++){
                    if(versions.get(i).getId().equals(versionId)){
                        version = versions.get(i);
                    }
                }

                // Download the data
                if(version!=null){
                    File downloadFile = new File(parent.getLibraryDirectory(), documentId + "-" + versionId + ".zip");
                    FileOutputStream outStream = new FileOutputStream(downloadFile);
                    apiLink.download(doc, versionId, outStream);
                    outStream.flush();
                    outStream.close();

                    // Create the library item
                    libraryItem = new CloudWorkflowServiceLibraryItem(parent.getParentLibrary(), doc, version);
                    libraryItem.extractFiles(report);

                    // Compile the library if needed
                    libraryItem.compile();
                    
                    finishOk();
                } else {
                    logger.error("Cannot locate version: " + versionId);
                    finishWithError("Cannot locate service version id: " + versionId);
                }
            } else {
                logger.error("Cannot locate document: " + documentId);
                finishWithError("Cannot locate service id: " + documentId);
            }
            
        } catch (Exception e){
            logger.error("Exception in download thread", e);
            finishWithError(e.getMessage());
        }
    }
}