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
package com.connexience.server.model.storage;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.document.UncommittedVersion;
import com.connexience.server.util.Base64;
import com.connexience.server.util.StorageUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.BadRequestException;
import org.json.JSONObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class acts as a data store that stores everything on disk. The file
 * store is organised as follows:
 *
 * Each organisation has its own folder DocumentRecords are in sub-folders
 * determinied by the last two digits of the id Each DocumentRecord is a folder,
 * which conains all of the versions
 *
 * @author nhgh
 */
public class FileDataStore extends DataStore {

    /**
     * Class version UID.
     *
     * Please increment this value whenever your changes may cause
     * incompatibility with the previous version of this class. If unsure, ask
     * one of the core development team or read:
     * http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html and
     * http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 1L;

    private static final Logger _Logger = Logger.getLogger(FileDataStore.class);
    private static final String _CommittedFileName = ".committed";

    private static final String _CommittingSuffix = ".committing";
    private static final String _CleanningSuffix = ".cleanning";

    /**
     * Directory for storage
     */
    private String directory = "";

    /**
     * Creates a new instance of FileDataStore
     */
    public FileDataStore() {
        bulkDeleteSupported = true;
        sizeLimited = true;
        spaceReportingSupported = true;
        chunkedUploadSupported = true;
    }

    @Override
    @JsonIgnore
    public long getAvailableStoreSize() throws ConnexienceException {
        File baseDir = new File(directory);
        return baseDir.getFreeSpace();
    }

    @Override
    @JsonIgnore
    public long getTotalStoreSize() throws ConnexienceException {
        File baseDir = new File(directory);
        return baseDir.getTotalSpace();
    }

    /**
     * Read a record from an InputStream
     */
    public DocumentVersion readFromStream(DocumentRecord document, DocumentVersion record, InputStream stream) throws ConnexienceException {
        assertWritable();
        try {
            BufferedInputStream inStream = new BufferedInputStream(stream);
            File documentDir = getDocumentRecordDirectory(document);

            File outFile = new File(documentDir, record.getId());
            if (outFile.exists() && outFile.isDirectory()) {
                // The case when someone overwrites a document previously
                // uploaded as chunks. 
                StorageUtils.delete(outFile, false, false);
            }

            BufferedOutputStream outStream = null;
            FileOutputStream fileStream = null;
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                fileStream = new FileOutputStream(outFile);
                outStream = new BufferedOutputStream(fileStream);

                byte[] buffer = new byte[16384];
                int len;
                long fileLen = 0L;
                while ((len = inStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, len);
                    md.update(buffer, 0, len);
                    fileLen += len;
                }

                inStream.close();
                outStream.flush();
                fileStream.flush();
                record.setMd5(Base64.encodeBytes(md.digest()));
                record.setSize(fileLen);
                return record;
            } finally {
                try {
                    fileStream.close();
                    outStream.close();
                } catch (Exception ex) {
                    _Logger.error("Error flushing stream: " + ex.getMessage());
                }

            }
        } catch (Exception e) {
            throw new ConnexienceException(e.getMessage(), e);
        }
    }

    /**
     * Read a record from a File
     */
    public DocumentVersion readFromFile(DocumentRecord document, DocumentVersion record, File file) throws ConnexienceException {
        assertWritable();
        try {
            return readFromStream(document, record, new FileInputStream(file));
        } catch (FileNotFoundException x) {
            throw new ConnexienceException(x.getMessage(), x);
        }
    }

    /**
     * Remove a record. This only deletes the actual DocumentVersion unless
     * there are no versions left, in which case the document directory is also
     * deleted
     */
    public void removeRecord(DocumentRecord document, DocumentVersion record) throws ConnexienceException {
        assertWritable();
        try {
            File documentDir = getDocumentRecordDirectory(document);
            File versionFile = new File(documentDir, record.getId());
            if (versionFile.exists()) {
                StorageUtils.delete(versionFile, true, false);
            }

            // Any more versions left
            File[] children = documentDir.listFiles();
            if (children.length == 0) {
                documentDir.delete();
            }
        } catch (Exception e) {
            throw new ConnexienceException("Error removing file: " + e.getMessage(), e);
        }
    }

    public void bulkDelete(String organisationId, ArrayList<String> documentIds) throws ConnexienceException {
        assertWritable();
        for (String documentId : documentIds) {
            try {
                File documentDir = getDocumentRecordDirectory(organisationId, documentId);
                StorageUtils.delete(documentDir, true, false);
            } catch (Exception e) {
                throw new ConnexienceException("Error removing document record: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public long getRecordSize(DocumentRecord document, DocumentVersion record) throws ConnexienceException {
        try {
            File documentDir = getDocumentRecordDirectory(document);
            File versionFile = new File(documentDir + File.separator + record.getId());

            if (versionFile.exists()) {
                if (versionFile.isDirectory()) {
                    // This is the case when the document was uploaded with 
                    // chunks. But report size only after blocks has been 
                    // committed.
                    File committed = new File(versionFile, _CommittedFileName);
                    if (committed.exists()) {
                        BufferedReader reader = null;
                        try {
                            reader = new BufferedReader(new FileReader(committed));
                            String line;
                            // Read the header
                            while ((line = reader.readLine()) != null) {
                                if (line.startsWith("length=")) {
                                    return Long.parseLong(line.substring("length=".length()));
                                }
                            }
                        } finally {
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (IOException x) {
                                    // Log and ignore the exception
                                    _Logger.warn("Exception when closing the reader", x);
                                }
                            }
                        }
                    }
                    return 0;
                } else {
                    return versionFile.length();
                }
            } else {
                throw new Exception("No such document version");
            }
        } catch (Exception e) {
            throw new ConnexienceException(e.getMessage(), e);
        }
    }

    /**
     * Get an InputStream that can be used to read the contents of the document
     */
    public InputStream getInputStream(DocumentRecord document, DocumentVersion version) throws ConnexienceException {
        try {
            File documentDir = getDocumentRecordDirectory(document);
            File versionFile = new File(documentDir + File.separator + version.getId());
            InputStream inStream;

            if (versionFile.exists() && versionFile.isDirectory()) {
                // This is the case when the document was uploaded with 
                // chunks. But report size only after blocks has been 
                // committed.
                File committed = new File(versionFile, _CommittedFileName);
                if (!committed.exists()) {
                    throw new ConnexienceException("Document %s, version %s has not yet been committed.", document.getId(), version.getId());
                }

                ArrayList<InputStream> blockStreamArray = new ArrayList<>();
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(committed));
                    String line;
                    // Read and skip the header
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("length=")) {
                            break;
                        }
                    }
                    while ((line = reader.readLine()) != null) {
                        blockStreamArray.add(new FileInputStream(new File(versionFile, line)));
                    }
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException x) {
                            // Log and ignore the exception
                            _Logger.warn("Exception when closing the reader.", x);
                        }
                    }
                }
                inStream = new SequenceInputStream(Collections.enumeration(blockStreamArray));
            } else {
                inStream = new FileInputStream(versionFile);
            }

            return new BufferedInputStream(inStream);
        } catch (ConnexienceException x) {
            throw x;
        } catch (Exception e) {
            throw new ConnexienceException(e.getMessage(), e);
        }
    }

    /**
     * Write a record to an OutputStream.
     */
    public void writeToStream(DocumentRecord document, DocumentVersion record, OutputStream stream) throws ConnexienceException {
        writeToStream(document, record, stream, -1);
    }

    /**
     * Write a record to an OutputStream with a size limit.
     */
    public void writeToStream(DocumentRecord document, DocumentVersion record, OutputStream stream, long sizeLimit) throws ConnexienceException {
        InputStream inStream = null;
        try {
            inStream = getInputStream(document, record);

            byte[] buffer = new byte[16384];
            int len = inStream.read(buffer);

            if (sizeLimit > 0) {
                long totalBytes = 0L;
                while (len != -1 && totalBytes < sizeLimit) {
                    stream.write(buffer, 0, len);
                    len = inStream.read(buffer);
                    totalBytes += len;
                }
            } else {
                while (len != -1) {
                    stream.write(buffer, 0, len);
                    len = inStream.read(buffer);
                }
            }
            stream.flush();
        } catch (Exception e) {
            throw new ConnexienceException(e.getMessage(), e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException x) {
                    // Log and ignore the exception
                    _Logger.warn("Exception when closing the input stream", x);
                }
            }
        }
    }

    /**
     * Set the directory
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    /**
     * Get the directory
     */
    public String getDirectory() {
        return directory;
    }

    // =========================================================================
    // Code to manage the allocation of directories to organisations and id
    // subdirectories
    // =========================================================================
    /**
     * Get a directory for a DocumentRecord. This uses the following semantics
     * for building a directory path:
     *
     * root_directory/ORGANISAION_ID/LAST_2_IDDIGITS/DOCUMENTID/
     *
     * This directory is then the repository for all versions of that file.
     */
    private File getDocumentRecordDirectory(String organisationId, String documentId) throws IOException {
        File orgDir = getOrganisationDirectory(organisationId);
        String id = documentId;
        String subdir = id.substring(id.length() - 2);
        File recordDirectory = new File(orgDir.getPath() + File.separator + subdir + File.separator + id);
        if (recordDirectory.exists() && recordDirectory.isDirectory()) {
            return recordDirectory;
        } else {
            if (!recordDirectory.exists()) {
                recordDirectory.mkdirs();
                return recordDirectory;
            } else {
                throw new IOException("Cannot create document record directory");
            }
        }
    }

    private File getDocumentRecordDirectory(DocumentRecord record) throws IOException {
        if (record.getId() != null && record.getOrganisationId() != null) {
            return getDocumentRecordDirectory(record.getOrganisationId(), record.getId());
        } else {
            if (record.getId() == null) {
                throw new IOException("Document does not have an ID");
            } else {
                throw new IOException("Document has not been assigned to an organisation");
            }
        }
    }

    /**
     * Get the directory for an organisation. This will create the directory if
     * it doesn't already exist
     */
    private File getOrganisationDirectory(String organisationID) throws IOException {
        File dir = new File(directory + File.separator + organisationID);
        if (dir.exists() && dir.isDirectory()) {
            return dir;
        } else {
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    return dir;
                } else {
                    throw new IOException("Cannot create organisation storage directory");
                }
            } else {
                throw new IOException("A file with the same ID as the organisation is preventing the creation of the organisation storage directory");
            }
        }
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("StorageDirectory", directory);
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
        directory = json.getString("StorageDirectory");
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("Directory", directory);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        directory = store.stringValue("Directory", "");
    }

    @Override
    public void uploadBlock(DocumentRecord document, UncommittedVersion record, int blockId, byte[] blockContent) throws ConnexienceException {
        try {
            File blockDir = new File(getDocumentRecordDirectory(document), record.getId());
            if (!blockDir.exists()) {
                blockDir.mkdirs();
            }
            File blockFile = new File(blockDir, Integer.toString(blockId));

            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(blockFile);
                outStream.write(blockContent);
            } finally {
                if (outStream != null) {
                    outStream.close();
                }
            }
        } catch (Exception x) {
            throw new ConnexienceException(x.getMessage(), x);
        }
    }

    ;


    @Override
    public DocumentVersion commitBlockList(DocumentRecord document, UncommittedVersion version, List<Integer> blockList, DocumentVersion committedVersion) throws ConnexienceException {
        //File lockFileName = null;
        //FileOutputStream lockFile = null;
        //FileLock lock = null;

        try {
            File blockDir = new File(getDocumentRecordDirectory(document), version.getId());
            if (!blockDir.exists()) {
                throw new ConnexienceException(String.format("No blocks uploaded for document %s, version %s", document.getId(), version.getId()));
            }
            if (!blockDir.isDirectory()) {
                throw new ConnexienceException("Missing directory for uncommitted version %s", version.getId());
            }

            File committingDir = new File(blockDir.getPath() + _CommittingSuffix);
            if (!committingDir.mkdir()) {
                _Logger.warn(
                        String.format("The uncommitted document version %s seems to be already committed... in directory %s",
                                version.getId(),
                                committingDir.getPath()));
                throw new ConnexienceException("Concurrent commit detected ");
            }

            if (!blockDir.renameTo(committingDir)) {
                _Logger.error("Can't rename the uncommitted version storage directory: " + blockDir);
                throw new ConnexienceException("Internal error: commit failed; see server logs for details; uncommitted document version " + version.getId());
            }

            // First, loop through the file list to remove any unneeded 
            // blocks.
            for (File blockFile : committingDir.listFiles()) {
                Integer blockNo = Integer.parseInt(blockFile.getName());
                // Check whether the block file is on the committed block 
                // list...
                if (blockList.indexOf(blockNo) < 0) {
                    // ... and delete it if it's not.
                    if (!blockFile.delete()) {
                        blockFile.deleteOnExit();
                    }
                }
            }

            // Now, loop through the block list and create the 
            // '.committed' file.
            File committed = new File(committingDir, _CommittedFileName);
            StringBuilder sb = new StringBuilder();
            long length = 0L;

            for (Integer blockNo : blockList) {
                File block = new File(committingDir, Integer.toString(blockNo));
                if (!block.exists()) {
                    throw new BadRequestException(String.format("Invalid block id %d. Block does not exist", blockNo));
                }
                length += block.length();
                sb.append(blockNo);
                sb.append('\n');
            }

            // Write the contents to the committed file
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(committed);
                writer.format("length=%d\n", length);
                writer.write(sb.toString());
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
            committedVersion.setSize(length);

            // At the very end move the newly committed version under the committedVersion.id
            File committedBlockDir = new File(getDocumentRecordDirectory(document), committedVersion.getId());
            if (committedBlockDir.exists()) {
                throw new ConnexienceException("Internal error: the directory for the new version exists already:" + committedBlockDir);
            }

            if (!committingDir.renameTo(committedBlockDir)) {
                throw new ConnexienceException(
                        "Internal error: can't rename directory uncommitted (%s) -> committed (%s).",
                        committingDir.toString(),
                        committedBlockDir.toString());
            }
            return committedVersion;
        } catch (Exception x) {
            throw new ConnexienceException(x.getMessage(), x);
        }
    }

    @Override
    public List<Integer> getBlockList(DocumentRecord document, String versionId) throws ConnexienceException {
        try {
            File blockDir = new File(getDocumentRecordDirectory(document), versionId);
            if (!blockDir.exists()) {
                throw new ConnexienceException("No document exists; documentId = %s, versionId = %s", document.getId(), versionId);
            }

            ArrayList<Integer> blockList = new ArrayList<>();
            if (!blockDir.isDirectory()) {
                // Empty list -- a special case when a user asked about block 
                // list of a document uploaded in a standard/non-chunked way.
                return blockList;
            }

            File committed = new File(blockDir, _CommittedFileName);
            if (committed.exists()) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(committed));
                    String line;
                    // Read and skip the header
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("length=")) {
                            break;
                        }
                    }
                    // Read the committed block list
                    while ((line = reader.readLine()) != null) {
                        blockList.add(Integer.parseInt(line));
                    }
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException x) {
                            // Log and ignore the exception
                            _Logger.warn("Exception when closing the reader.", x);
                        }
                    }
                }

            } else {
                // The actual order of blocks is unknown until commit, 
                // so just listing the blocks
                for (File blockFile : blockDir.listFiles()) {
                    blockList.add(Integer.parseInt(blockFile.getName()));
                }
            }

            return blockList;
        } catch (IOException x) {
            throw new ConnexienceException(x.getMessage(), x);
        }
    }

    public boolean cleanupVersionIfUncommitted(DocumentRecord document, UncommittedVersion version) throws ConnexienceException {
        try {
            File blockDir = new File(getDocumentRecordDirectory(document), version.getId());
            if (!blockDir.exists()) {
                throw new ConnexienceException("No document exists; documentId = %s, versionId = %s", document.getId(), version.getId());
            }

            if (!blockDir.isDirectory()) {
                // No directory means that the version must actually be committed?
                // Or perhaps something wrong with the whole cleanup mechanism.
                _Logger.warn(String.format("Missing directory for uncommitted version %s", version.getId()));
                return false;
            }

            // Let's try moving the uncommitted block dir to a special cleanning state
            // so it stops upload and commitBlock immediately
            File cleanningDir = new File(blockDir.getPath() + _CleanningSuffix);
            if (!cleanningDir.mkdir()) {
                // Multiple messages for the same version mean that something is wrong.
                _Logger.warn(String.format(
                        "The uncommitted document version %s seems to be already cleaned... in directory %s",
                        version.getId(),
                        cleanningDir.getPath()));
                return false;
            }

            if (!blockDir.renameTo(cleanningDir)) {
                _Logger.error("Can't rename the uncommitted version storage directory: " + blockDir);
                throw new ConnexienceException("Can't clean up uncommitted document version " + version.getId());
            }

            return StorageUtils.delete(cleanningDir, true, true);
        } catch (IOException x) {
            throw new ConnexienceException(x.getMessage(), x);
        }
    }
}
