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
package com.connexience.api;

import com.connexience.api.misc.IProgressInfo;
import com.connexience.api.model.*;
import com.connexience.api.model.json.JSONArray;
import com.connexience.api.model.net.GenericClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides a client for the REST storage service.
 * @author hugo
 */
public class StorageClient extends GenericClient implements StorageInterface
{

    public StorageClient(String hostname, int port, boolean secure, String username, String password) {
        super(hostname, port, secure, "/api/public/rest/v1/storage", username, password);
    }

    public StorageClient() throws Exception {
        super("/api/public/rest/v1/storage");
    }
    
    public StorageClient(GenericClient existingClient) throws Exception {
        existingClient.configureClient(this);
        this.setUrlBase("/api/public/rest/v1/storage");
    }
    
    public StorageClient(File apiProperties) throws Exception {
        super("/api/public/rest/v1/storage", apiProperties);
    }
    
    /** Creates a new client and checks that the user is valid */
    public static StorageClient createAuthenticatedClient(String hostname, int port, boolean secure, String username, String password) throws Exception {
        StorageClient c = new StorageClient(hostname, port, secure, username, password);
        try {
            long timestamp = c.getTimestamp();
            return c;
        } catch (Exception e){
            return null;
        }
    }
    
    @Override
    public EscFolder createChildFolder(String id, String name) throws Exception {
        return new EscFolder(postTextRetrieveJson("/folders/" + id, name));
    }

    @Override
    public EscDocument createDocumentInFolder(String id, String name) throws Exception {
        return new EscDocument(postTextRetrieveJson("/folders/" + id + "/documents/create", name));
    }
    //public EscDocument createDocumentInFolder(EscFolder parent, String name) throws Exception
    //{
    //    Map<String, String> props = new HashMap<String, String>(); 
    //    if(parent.getProjectId()!=null){
    //        props.put("ProjectID", parent.getProjectId());
    //    }
    //    return new EscDocument(postTextRetrieveJson("/folders/" + parent.getId() + "/documents/create", name, props));
    //}

    @Override
    public EscUser currentUser() throws Exception {
        return new EscUser(retrieveJson("/currentuser"));
    }

    @Override
    public EscDocument[] folderDocuments(String id) throws Exception {
        JSONArray results = retrieveJsonArray("/folders/" + id +"/documents");
        EscDocument[] docs = new EscDocument[results.length()];
        for(int i=0;i<results.length();i++){
            docs[i] = new EscDocument(results.getJSONObject(i));
        }
        return docs;
    }

    @Override
    public EscDocument[] getRelatedDocuments(String id) throws Exception {
        JSONArray results = retrieveJsonArray("/documents/" + id +"/related");
        EscDocument[] docs = new EscDocument[results.length()];
        for(int i=0;i<results.length();i++){
            docs[i] = new EscDocument(results.getJSONObject(i));
        }
        return docs;
    }

    @Override
    public EscDocument getDocument(String id) throws Exception {
        return new EscDocument(retrieveJson("/documents/" + id));
    }

    @Override
    public EscFolder getFolder(String id) throws Exception {
        return new EscFolder(retrieveJson("/folders/" + id));
    }

    @Override
    public EscFolder homeFolder() throws Exception {
        return new EscFolder(retrieveJson("/specialfolders/home"));
    }

    @Override
    public EscFolder[] listChildFolders(String id) throws Exception {
        JSONArray results = retrieveJsonArray("/folders/" + id + "/children");
        EscFolder[] folders = new EscFolder[results.length()];
        for(int i=0;i<results.length();i++){
            folders[i] = new EscFolder(results.getJSONObject(i));
        }
        return folders;
    }

    @Override
    public EscDocumentVersion[] listDocumentVersions(String id) throws Exception {
        JSONArray results = retrieveJsonArray("/documents/" + id + "/versions");
        EscDocumentVersion[] versions = new EscDocumentVersion[results.length()];
        for(int i=0;i<results.length();i++){
            versions[i] = new EscDocumentVersion(results.getJSONObject(i));
        }
        return versions;
    }

    @Override
    public EscProject[] listProjects() throws Exception {
        JSONArray results = retrieveJsonArray("/projects");
        EscProject[] projects = new EscProject[results.length()];
        for(int i=0;i<results.length();i++){
            projects[i] = new EscProject(results.getJSONObject(i));
        }
        return projects;
    }

    @Override
    public EscProject getProject(String id) throws Exception {
        return new EscProject(retrieveJson("/projects/" + id));
    }
    
    @Override
    public EscDocument updateDocument(EscDocument document) throws Exception {
        if(document.getId()!=null){
            return new EscDocument(postJsonRetrieveJson("/documents/" + document.getId(), document.toJsonObject()));
        } else {
            throw new Exception("Document has no ID");
        }
    }

    @Override
    public EscFolder updateFolder(EscFolder folder) throws Exception {
        if(folder.getId()!=null){
            return new EscFolder(postJsonRetrieveJson("/folders/" + folder.getId(), folder.toJsonObject()));
        } else {
            throw new Exception("Folder has no ID");
        }
    }

    @Override
    public EscDocumentVersion getLatestDocumentVersion(String documentId) throws Exception {
        return new EscDocumentVersion(retrieveJson("/documents/" + documentId + "/versions/latest"));
    }

    @Override
    public void deleteDocumentVersion(String documentId, String versionId) throws Exception {
        deleteResource("/documents/" + documentId + "/versions/" + versionId);
    }

    
    @Override
    public void deleteDocument(String documentId) throws Exception {
        int response = deleteResource("/documents/" + documentId);
        if(response!=HttpURLConnection.HTTP_OK && response!=HttpURLConnection.HTTP_NO_CONTENT){
            throw new Exception("HTTP Error: " + response);
        }
    }

    @Override
    public void deleteFolder(String folderId) throws Exception {
        int response = deleteResource("/folders/" + folderId);
        if(response!=HttpURLConnection.HTTP_OK && response!=HttpURLConnection.HTTP_NO_CONTENT){
            throw new Exception("HTTP Error: " + response);
        }
    }

    @Override
    public EscMetadataItem[] getDocumentMetadata(String id) throws Exception {
        JSONArray json = retrieveJsonArray("/documents/" + id + "/metadata");
        EscMetadataItem[] metadata = new EscMetadataItem[json.length()];
        if(json.length()>0){
            for(int i=0;i<json.length();i++){
                metadata[i] = new EscMetadataItem(json.getJSONObject(i));
            }
        }
        return metadata;
    }

    @Override
    public EscMetadataItem addMetadataToDocument(String id, EscMetadataItem metadataItem) throws Exception {
        return new EscMetadataItem(postJsonRetrieveJson("/documents/" + id + "/metadata", metadataItem.toJsonObject()));
    }

    @Override
    public EscDocumentVersion getDocumentVersion(String id) throws Exception {
        return new EscDocumentVersion(retrieveJson("/documentversions/" + id));
    }

    @Override
    public long getTimestamp() throws Exception {
        return Long.parseLong(retrieveString("/timestamp"));
    }


    @Override
    public String uploadBlock(String documentId, String versionId, Integer blockId, byte[] blockContent) throws Exception
    {
        String url = String.format("/documents/%s/%s/%d", documentId, versionId, blockId); 
        URLConnection connection = createConnection(url);
        
        // Set the project ID if needed
        //if(document.getProjectId()!=null){
        //    connection.setRequestProperty("ProjectID", document.getProjectId());
        //}
        
        // Set the streaming mode to work with larger streams
        if (connection instanceof HttpURLConnection) {
            ((HttpURLConnection) connection).setFixedLengthStreamingMode(blockContent.length);
        }
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        //connection.setRequestProperty("Content-Length", Integer.toString(blockContent.length));
        //if (callback != null) {
        //    callback.reportStart(contentLength);
        //}
        try (OutputStream outStream = connection.getOutputStream()) {
            outStream.write(blockContent);
            outStream.close();
        }

        try (BufferedReader inReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return inReader.readLine();
        }
    }

    @Override
    public EscDocumentVersion commitBlockList(String documentId, String versionId, List<Integer> blockList) throws Exception
    {
        try {
            String url = String.format("/documents/%s/%s/blocklist", documentId, versionId);
            return new EscDocumentVersion(postJsonArrayRetrieveJson(url, new JSONArray(blockList)));
        } finally {
        }
    }

    @Override
    public List<Integer> getBlockList(String documentId, String versionId) throws Exception
    {
        String url = String.format("/documents/%s/%s/blocklist", documentId, versionId);
        JSONArray array = retrieveJsonArray(url);
        System.out.println(array);
        ArrayList<Integer> output = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            output.add(array.getInt(i));
        }
        return output;
    }


    /**
     * Get an input stream for a document
     */
    public InputStream openInputStream(EscDocument document) throws Exception {
        return openInputStream(document.getDownloadPath());
    }
    
    /**
     * Download the contents of a document to an output stream
     */    
    public void download(EscDocument document, OutputStream stream) throws Exception {
        downloadUrlToOutputStream(document.getDownloadPath(), stream);
    }
    
    /**
     * Download the contents of a document to a local file
     */
    public void download(EscDocument document, File localFile) throws Exception {
        downloadUrlToFile(document.getDownloadPath(), localFile);
    }
    
    /** 
     * Download a document version to an output stream
     */
    public void download(EscDocumentVersion version, OutputStream stream) throws Exception {
        downloadUrlToOutputStream(version.getDownloadPath(), stream);
    }
    
    /**
     * Download a document version to an output stream
     */
    public void download(EscDocumentVersion version, File localFile) throws Exception {
        downloadUrlToFile(version.getDownloadPath(), localFile);
    }


    /**
     * Upload a file to a server on the folder
     */
    public EscDocumentVersion upload(EscFolder parent, File localFile) throws Exception
    {
        return upload(parent, localFile, null);
    }

    public EscDocumentVersion upload(EscFolder parent, File localFile, IProgressInfo callback) throws Exception
    {
        EscDocument document = createDocumentInFolder(parent.getId(), localFile.getName());
        // FIXME: How about project ids?
        // FIXME: How about overwrite protection?
        try (FileInputStream inStream = new FileInputStream(localFile)) {
            return upload(document, inStream, localFile.length(), callback);
        } 
    }

    /**
     * Upload from a local file 
     */
    public EscDocumentVersion upload(EscDocument document, File localFile) throws Exception
    {
        return upload(document, localFile, null);
    }

    public EscDocumentVersion upload(EscDocument document, File localFile, IProgressInfo callback) throws Exception
    {
        try (FileInputStream inStream = new FileInputStream(localFile)) {
            return upload(document, inStream, localFile.length(), callback);
        } catch (Exception e) {
            throw new Exception("Error doing upload: " + e.getMessage(), e);
        }
    }

    /**
     * Upload from an input stream
     */
    public EscDocumentVersion upload(EscDocument document, InputStream stream, long contentLength) throws Exception
    {
        return upload(document, stream, contentLength, null);
    }

    /**
     * Upload from an input stream
     */
    public EscDocumentVersion upload(EscDocument document, InputStream stream, long contentLength, IProgressInfo callback) throws Exception
    {
        final String documentId = document.getId();

        if (callback != null) {
            callback.reportBegin(contentLength);
        }

        if (contentLength <= 4 * 1024 * 1024) {
            URLConnection connection = createConnection("/data/" + documentId);
            // Set the project ID if needed
            // FIXME: Discuss how project ids should be handled
            if(document.getProjectId()!=null){
                connection.setRequestProperty("ProjectID", document.getProjectId());
            }
            // Set the streaming mode to work with larger streams
            if (connection instanceof HttpURLConnection) {
                ((HttpURLConnection) connection).setFixedLengthStreamingMode((int)contentLength);
            }
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("Content-Length", Long.toString(contentLength));
            OutputStream outStream = connection.getOutputStream();
            copyInputStream(stream, outStream, callback);
            stream.close();
            outStream.close();

            InputStream resultsStream = connection.getInputStream();
            BufferedReader inReader = new BufferedReader(new InputStreamReader(resultsStream));
            String versionId = inReader.readLine();
            return getDocumentVersion(versionId);
        } else {
            long currentLength = 0;
            try {
                // For a larger file, upload it in blocks
                int chunkSize;
                ArrayList<Integer> blockList = new ArrayList<>();
                byte[] buffer = new byte[2 * 1024 * 1024];
                // create a new file version
                String versionId = "new";
                int blockId = 0;
                while ((chunkSize = stream.read(buffer)) > 0) {
                    if (chunkSize < buffer.length) {
                        versionId = uploadBlock(documentId, versionId, blockId, Arrays.copyOf(buffer, chunkSize));
                    } else {
                        versionId = uploadBlock(documentId, versionId, blockId, buffer);
                    }
                    currentLength += chunkSize;
                    blockList.add(blockId);
                    blockId++;
                    if (callback != null) {
                        callback.reportProgress(currentLength);
                    }
                }
                return commitBlockList(documentId, versionId, blockList);
            } finally {
                if (callback != null) {
                    callback.reportEnd(currentLength);
                }
            }
        }
    }

    /**
     * Upload a file to a server on the folder
     */
    /*
    public EscDocumentVersion upload(EscFolder parent, File localFile, IProgressInfo callback) throws Exception {
        EscDocument doc = createDocumentInFolder(parent.getId(), localFile.getName());
        final String documentId = doc.getId();

        if (localFile.length() <= 4 * 1024 * 1024) {
            // For small files do simple upload
            URLConnection connection = createConnection("/data/" + documentId);
            
            // Set the project ID if needed
            if(parent.getProjectId()!=null){
                connection.setRequestProperty("ProjectID", parent.getProjectId());
            }
            // Set the streaming mode to work with larger files
            if (connection instanceof HttpURLConnection) {
                ((HttpURLConnection) connection).setFixedLengthStreamingMode((int)localFile.length());
            }
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("Content-Length", Long.toString(localFile.length()));
            try (OutputStream outStream = connection.getOutputStream(); 
                 FileInputStream inStream = new FileInputStream(localFile)) {
                if (callback != null) {
                    callback.reportStart(localFile.length());
                }
                copyInputStream(inStream, outStream, callback);
            }
            InputStream resultsStream = connection.getInputStream();
            BufferedReader inReader = new BufferedReader(new InputStreamReader(resultsStream));
            String versionId = inReader.readLine();
            return getDocumentVersion(versionId);
        } else {
            // For a larger file, upload it in blocks
            int chunkSize;
            ArrayList<Integer> blockList = new ArrayList<Integer>();
            byte[] buffer = new byte[2 * 1024 * 1024];
            // create a new file version
            String versionId = "new";
            int blockId = 0;
            try (FileInputStream inStream = new FileInputStream(localFile)) {
                while ((chunkSize = inStream.read(buffer)) > 0) {
                    if (chunkSize < buffer.length) {
                        versionId = uploadBlock(documentId, versionId, blockId, Arrays.copyOf(buffer, chunkSize));
                    } else {
                        versionId = uploadBlock(documentId, versionId, blockId, buffer);
                    }
                    blockList.add(blockId);
                    blockId++;
                }
                return commitBlockList(documentId, versionId, blockList);
            }
        }
    }
    */
}