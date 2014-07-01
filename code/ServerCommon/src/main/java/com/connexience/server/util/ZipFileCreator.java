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
package com.connexience.server.util;

import com.connexience.server.ConnexienceException;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.security.Ticket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This class creates a zip file for a set of documents / folders
 * <p/>
 * @author hugo
 */
public class ZipFileCreator {

    private ArrayList<String> topLevelItems = new ArrayList<>();
    ArrayList<ObjectZipper> topLevelZippers = new ArrayList<>();
    private Ticket ticket;
    private File tempFile = null;
    private ZipOutputStream zipStream = null;

    public ZipFileCreator(Ticket ticket) {
        this.ticket = ticket;
    }

    public void addTopLevelItem(String id) {
        topLevelItems.add(id);
    }

    public DocumentRecord compressData(String destinationFolderId, String zipFileName) throws ConnexienceException {
        try {
            prepareTemporaryFile();

            DocumentRecord doc = StorageUtils.getOrCreateDocumentRecord(ticket, destinationFolderId, zipFileName);
            StorageUtils.upload(ticket, tempFile, doc, "Compressed data generated by file browser");
            return doc;
        } catch (Exception e) {
            throw new ConnexienceException("Error storing zip file: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    public File compressDataToFile() throws ConnexienceException {
        try {
            prepareTemporaryFile();

            return tempFile;
        } catch (Exception e) {
            throw new ConnexienceException("Error serving zip file: " + e.getMessage());
        }
    }

    public void prepareTemporaryFile() throws ConnexienceException {
        begin();
        tempFile = null;

        try {
            tempFile = File.createTempFile("archive", "zip");
        } catch (Exception e) {
            throw new ConnexienceException("Error creating archive temporary file: " + e.getMessage(), e);
        }

        try {
            setupStreams();
        } catch (Exception e) {
            throw new ConnexienceException("Error setting up zip processes: " + e.getMessage(), e);
        }

        doZip(tempFile);

        try {
            zipStream.flush();
            zipStream.finish();
            zipStream.close();
        } catch (Exception e) {
            throw new ConnexienceException("Error storing zip file: " + e.getMessage());
        }
    }

    public File getTempFile() {
        return tempFile;
    }

    private void begin() throws ConnexienceException {
        // Add all of the top level items
        topLevelZippers.clear();
        for (String id : topLevelItems) {
            topLevelZippers.add(createZipperForId(id, ""));
        }
    }

    public void cleanup() {
        if (tempFile.exists() && !tempFile.delete()) {
            tempFile.deleteOnExit();
        }
    }

    private void setupStreams() throws IOException {
        try {
            zipStream = new ZipOutputStream(new FileOutputStream(tempFile));
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    private void doZip(File tempFile) throws ConnexienceException {
        for (ObjectZipper zipper : topLevelZippers) {
            zipper.addToZip();
        }
    }

    private ObjectZipper createZipperForId(String id, String path) throws ConnexienceException {
        ServerObject object = EJBLocator.lookupObjectDirectoryBean().getServerObject(ticket, id, ServerObject.class);
        if (object instanceof Folder) {
            return new FolderZipper((Folder) object, path);
        } else if (object instanceof DocumentRecord) {
            return new DocumentRecordZipper((DocumentRecord) object, path);
        } else {
            return null;
        }
    }

    private abstract class ObjectZipper {

        private String path;

        public ObjectZipper(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public abstract void addToZip() throws ConnexienceException;
    }

    private class DocumentRecordZipper extends ObjectZipper {

        private DocumentRecord doc;

        public DocumentRecordZipper(DocumentRecord doc, String path) {
            super(path);
            this.doc = doc;
        }

        public void addToZip() throws ConnexienceException {
            try {
                DocumentVersion version = EJBLocator.lookupStorageBean().getLatestVersion(ticket, doc.getId());
                ZipEntry entry = new ZipEntry(getPath() + "/" + doc.getName());
                zipStream.putNextEntry(entry);
                StorageUtils.downloadFileToOutputStream(ticket, doc, version, zipStream);
                zipStream.closeEntry();
            } catch (IOException ioe) {
                throw new ConnexienceException("IO error adding file to zip archive: " + ioe.getMessage(), ioe);
            } catch (ConnexienceException e) {
                throw new ConnexienceException("Error adding file to zip archive: " + e.getMessage(), e);
            }
        }
    }

    private class FolderZipper extends ObjectZipper {

        private Folder folder;
        private ArrayList<DocumentRecordZipper> documents = new ArrayList<>();
        private ArrayList<FolderZipper> subdirectories = new ArrayList<>();

        public FolderZipper(Folder folder, String path) throws ConnexienceException {
            super(path + "/" + folder.getName());
            this.folder = folder;
            addItems();
        }

        private void addItems() throws ConnexienceException {
            // Add all the contents
            List contents = EJBLocator.lookupStorageBean().getAllFolderContents(ticket, folder.getId());
            for (int i = 0; i < contents.size(); i++) {
                if (contents.get(i) instanceof DocumentRecord) {
                    DocumentRecordZipper drz = new DocumentRecordZipper((DocumentRecord) contents.get(i), getPath());
                    documents.add(drz);

                } else if (contents.get(i) instanceof Folder) {
                    Folder f = (Folder) contents.get(i);
                    if (!f.getId().equals(folder.getId())) {
                        FolderZipper fz = new FolderZipper(f, getPath());
                        subdirectories.add(fz);
                    }
                }
            }
        }

        @Override
        public void addToZip() throws ConnexienceException {
            for (DocumentRecordZipper docZipper : documents) {
                docZipper.addToZip();
            }

            for (FolderZipper fz : subdirectories) {
                fz.addToZip();
            }
        }
    }
}
