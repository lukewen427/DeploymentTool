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
package com.connexience.server.model.scanner.filesystems;

import com.connexience.server.ConnexienceException;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.scanner.RemoteFilesystemObject;
import com.connexience.server.model.scanner.RemoteFilesystemScanner;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.util.StorageUtils;
import com.connexience.server.util.ZipUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jboss.logging.Logger;
import org.json.JSONObject;

/**
 * This class can scan a local filesystem and upload any new files into the system
 * @author hugo
 */
public class DiskScanner extends RemoteFilesystemScanner {
    Logger logger = Logger.getLogger(DiskScanner.class);
    
    /** Local directory to upload */
    private String folderPath = "/home";

    /** Import subdirectory */
    private String importSubdirectory = "import";
    
    /** Export subdirectory */
    private String exportSubdirectory = "export";
    
    /** Should imports and exports be separated */
    private boolean importExportSeparationEnabled = false;
    
    public DiskScanner() {
        setAutoscanEnabled(true);
        setScanInterval(10);
        setSettlingPassNeeded(true);
        setTypeName("Directory");
    }
    
    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("FolderPath", folderPath);
        json.put("ImportSubdirectory", importSubdirectory);
        json.put("ExportSubdirectory", exportSubdirectory);
        json.put("SeparateImportExportDirectories", importExportSeparationEnabled);
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
        if(json.has("FolderPath")){
            folderPath = json.getString("FolderPath");
        }
        
        if(json.has("ImportSubdirectory")){
            importSubdirectory = json.getString("ImportSubdirectory");
        } else {
            importSubdirectory = "";
        }
        
        if(json.has("ExportSubdirectory")){
            exportSubdirectory = json.getString("ExportSubdirectory");
        } else {
            exportSubdirectory = "";
        }
        
        if(json.has("SeparateImportExportDirectories")){
            importExportSeparationEnabled = json.getBoolean("SeparateImportExportDirectories");
        } else {
            importExportSeparationEnabled = false;
        }
    }

    public void setImportSubdirectory(String importSubdirectory) {
        this.importSubdirectory = importSubdirectory;
    }

    public String getImportSubdirectory() {
        return importSubdirectory;
    }

    public void setExportSubdirectory(String exportSubdirectory) {
        this.exportSubdirectory = exportSubdirectory;
    }

    public String getExportSubdirectory() {
        return exportSubdirectory;
    }

    public boolean isImportExportSeparationEnabled() {
        return importExportSeparationEnabled;
    }

    public void setImportExportSeparationEnabled(boolean importExportSeparationEnabled) {
        this.importExportSeparationEnabled = importExportSeparationEnabled;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFolderPath() {
        return folderPath;
    }

    @Override
    public void scanForChanges(Ticket ticket) throws ConnexienceException {
        // List the local files
        ArrayList<File> localFiles = listFiles();
        
        // Get the upload state from the database and put them into hashmap
        HashMap<String, RemoteFilesystemObject> map = createFilesystemMap(ticket);
        
        // Add any local files that do not yet exist in the database
        RemoteFilesystemObject fsObj;
        for(File f : localFiles){
            if(map.containsKey(f.getPath())){
                // Already seen this file, check size has stabilised and
                // upload if not already uploaded
                fsObj = map.get(f.getPath());
                if(RemoteFilesystemObject.WAITING.equals(fsObj.getStatus())){
                    // Is the file stable
                    if(fsObj.isStable()){
                        // Do the upload
                        fsObj.setStatus(RemoteFilesystemObject.QUEUED);
                        fsObj.setStatusMessage("");
                        fsObj = EJBLocator.lookupScannerBean().saveFileSystemObjectWithoutSecurity(fsObj);
                        
                        // Send to uploader
                        EJBLocator.lookupScannerBean().sendUploadMessage(ticket, fsObj.getId());
                        
                    } else {
                        // Check the sizes
                        if(fsObj.getCurrentSize()==-1){
                            // Not checked
                            fsObj.setCurrentSize(f.length());
                            
                        } else if(fsObj.getCurrentSize()==f.length()) {
                            // Now stable
                            fsObj.setStable(true);
                            
                        } else {
                            // Store last size
                            fsObj.setCurrentSize(f.length());
                        }
                        fsObj = EJBLocator.lookupScannerBean().saveFileSystemObjectWithoutSecurity(fsObj);
                        
                    }
                   
                }
                
            } else {
                // Need to create a map entry for the file
                fsObj = new RemoteFilesystemObject();
                fsObj.setRemotePath(f.getPath());
                fsObj.setScannerId(getId());
                fsObj.setCurrentSize(f.length());
                fsObj.setStatus(RemoteFilesystemObject.WAITING);
                fsObj.setStatusMessage("");
                fsObj.setName(f.getName());
                fsObj = EJBLocator.lookupScannerBean().saveFileSystemObjectWithoutSecurity(fsObj);
            }
        }
    }

    @Override
    public String importRemoteFile(Ticket ticket, RemoteFilesystemObject fsObject) throws ConnexienceException {
        try {
            // Set status
            fsObject.setStatus(RemoteFilesystemObject.UPLOADING);
            fsObject = EJBLocator.lookupScannerBean().saveFileSystemObjectWithoutSecurity(fsObject);
            
            File localFile = new File(fsObject.getRemotePath());
            String relativePath = ZipUtils.subtractPath(createImportPath(), localFile.getPath());
            logger.info("Uploading to path: " + relativePath);
            Folder targetFolder = EJBLocator.lookupStorageBean().getFolder(ticket, getTargetFolderId());

            DocumentRecord doc = StorageUtils.getOrCreateDocumentRecordAtPath(ticket, targetFolder, relativePath);
            DocumentVersion v = StorageUtils.upload(ticket, localFile, doc, "Uploaded by scanner");
            fsObject.setStatus(RemoteFilesystemObject.UPLOADED);
            fsObject.setStatusMessage("");
            fsObject.setLocalFileId(doc.getId());
            fsObject.setCurrentSize(v.getSize());
            fsObject.setName(localFile.getName());
            fsObject.setStable(true);
            fsObject = EJBLocator.lookupScannerBean().saveFileSystemObjectWithoutSecurity(fsObject);
            return doc.getId();
            
        } catch (Exception e){
            fsObject.setStatus(RemoteFilesystemObject.UPLOAD_ERROR);
            fsObject.setStatusMessage(e.getMessage());
            try {
                fsObject = (RemoteFilesystemObject)EJBLocator.lookupScannerBean().saveFileSystemObjectWithoutSecurity(fsObject);
            } catch(Exception ex){}
            return "";
        }
    }
    
    @Override
    public void removeRemoteFile(Ticket ticket, RemoteFilesystemObject file) throws ConnexienceException {
        File remoteFileToRemove = new File(file.getRemotePath());
        if(remoteFileToRemove.exists()){
            if(remoteFileToRemove.delete()==false){
                remoteFileToRemove.deleteOnExit();
            }
        }
    }

    private String createImportPath(){
        if(importExportSeparationEnabled){
            String path = folderPath + File.separator + importSubdirectory;
            
            // Create directory if needed
            File subdir = new File(path);
            if(!subdir.exists()){
                subdir.mkdirs();
            }
            
            return path;
        } else {
            return folderPath;
        }
        
    }
    
    private ArrayList<File> listFiles() throws ConnexienceException {
        File topLevel = new File(createImportPath());
        if(topLevel.exists() && topLevel.isDirectory()){
            ArrayList<File> results = new ArrayList<>();
            listFiles(topLevel, results);
            return results;
        } else {
            throw new ConnexienceException("The path: " + createImportPath() + " is not a directory");
        }  
    }
    
    private void listFiles(File parent, ArrayList<File>results){
        File[] contents = parent.listFiles();
        for(File f : contents){
            if(!f.getName().startsWith(".")){
                if(f.isFile()){
                    results.add(f);
                } else if(f.isDirectory()){
                    listFiles(f, results);
                }
            }
        }
    }
}