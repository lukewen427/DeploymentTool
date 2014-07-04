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

import com.connexience.server.workflow.cloud.library.*;
import com.connexience.server.workflow.api.*;

import java.util.*;
import java.io.*;
import org.apache.log4j.*;

/**
 * This class manages the download of dependent library items to the workflow
 * engine. 
 * @author nhgh
 */
public class DownloadManager {
    static Logger logger = Logger.getLogger(DownloadManager.class.getName());
    
    /** Currently active downloads */
    private ArrayList<LibraryDownload> currentDownloads = new ArrayList<>();

    /** Parent cloud workflow engine */
    private ServiceLibrary parent;

    /** Construct with a link to the parent workflow engine */
    public DownloadManager(ServiceLibrary parent){
        this.parent = parent;
    }
    
    /** Start a new download, or attach a listener to an existing download if there
     * is already one in progress */
    public synchronized void startDownload(API apiLink, String documentId, String versionId, LibraryDownloadListener listener, LibraryPreparationReport report) {
        logger.debug("Starting library download for: " + documentId + " v " + versionId);
        LibraryDownload existing = findDownload(documentId, versionId);
        if(existing!=null){
            // Attach to existing download
            logger.debug("Already downloading: " + documentId + " v " + versionId + " attaching to existing download");
            report.addMessage(LibraryPreparationReport.DOWNLOAD_MANAGER_MESSAGE, "Download already in progress");
            existing.addLibraryDownloadListener(listener);

        } else {
            // Create a new download
            logger.debug("Starting new download for: " + documentId + " v " + versionId);
            report.addMessage(LibraryPreparationReport.DOWNLOAD_MANAGER_MESSAGE, "Starting download process");
            LibraryDownload download = new LibraryDownload(this, apiLink, documentId, versionId, report);
            download.addLibraryDownloadListener(listener);
            currentDownloads.add(download);
            download.startDownload();
        }
    }

    /** Get the parent library */
    public ServiceLibrary getParentLibrary(){
        return parent;
    }
    
    /** Get the download library top level directory */
    public File getLibraryDirectory(){
        return parent.getLibraryDirectory();
    }

    /** A download is complete */
    public synchronized void downloadComplete(LibraryDownload download){
        logger.debug("Download complete for: " + download.getDocumentId() + " v " + download.getVersionId());
        currentDownloads.remove(download);
    }

    /** Find a download if one exists */
    public LibraryDownload findDownload(String documentId, String versionId){
        for(int i=0;i<currentDownloads.size();i++){
            if(currentDownloads.get(i).getDocumentId().equals(documentId) && currentDownloads.get(i).getVersionId().equals(versionId)){
                return currentDownloads.get(i);
            }
        }
        return null;
    }
}
