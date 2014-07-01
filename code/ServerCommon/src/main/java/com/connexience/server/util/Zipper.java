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
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.security.Ticket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/**
 *
 * @author hugo
 */
public class Zipper {
    ZipOutputStream zipStream;
    DocumentRecord target;
    Ticket ticket;
    boolean error = false;
    String errorMessage = "";
    String comments;
    boolean running = false;
    File tempFile;

    public Zipper(Ticket ticket, DocumentRecord target, String comments) {
        this.ticket = ticket;
        this.target = target;
        this.comments = comments;
    }

    public void setupStreams() throws IOException {
        try {
            tempFile = File.createTempFile("data", "zip");
            zipStream = new ZipOutputStream(new FileOutputStream(tempFile));
        } catch (IOException ioe){
            error = true;
            errorMessage = ioe.getMessage();
            throw ioe;
        }
    }

    public boolean hasError(){
        return error;
    }

    public boolean isRunning(){
        return running;
    }

    public DocumentVersion closeStreams() throws IOException, ConnexienceException {
        DocumentVersion version = null;
        try {
            zipStream.flush();
            zipStream.finish();
            zipStream.close();

            if(!error){
                version = StorageUtils.upload(ticket, tempFile, target, comments);
            }
            
            if(!tempFile.delete()){
                tempFile.deleteOnExit();
                System.out.println("Could not delete: " + tempFile.getName() + " deleting on exit!");
            }
        } catch (IOException ioe){
            error = true;
            errorMessage = ioe.getMessage();
            running = false;
        }
        return version;
    }

    public void appendDocumentRecord(DocumentRecord doc) throws Exception {
        try {
            if(zipStream!=null){
                DocumentVersion version = EJBLocator.lookupStorageBean().getLatestVersion(ticket, doc.getId());
                ZipEntry entry = new ZipEntry(doc.getName());
                zipStream.putNextEntry(entry);
                StorageUtils.downloadFileToOutputStream(ticket, doc, version, zipStream);
                zipStream.closeEntry();
            } else {
                throw new Exception("Not running");
            }
        } catch (Exception e){
            error = true;
            errorMessage = e.getMessage();
            throw e;
        }

    }
    
    public void appendDocumentRecord(String path, DocumentRecord doc) throws Exception {
        try {
            if(zipStream!=null){
                DocumentVersion version = EJBLocator.lookupStorageBean().getLatestVersion(ticket, doc.getId());
                ZipEntry entry = new ZipEntry(path + doc.getName());
                zipStream.putNextEntry(entry);
                StorageUtils.downloadFileToOutputStream(ticket, doc, version, zipStream);
                zipStream.closeEntry();
            } else {
                throw new Exception("Not running");
            }
        } catch (Exception e){
            error = true;
            errorMessage = e.getMessage();
            throw e;
        }

    }    

    public void appendEntry(String name) throws Exception {
        try {
            if(zipStream!=null){
                ZipEntry entry = new ZipEntry(name);
                zipStream.putNextEntry(entry);
            } else {
                throw new Exception("Not running");
            }
        } catch (Exception e){
            error = true;
            errorMessage = e.getMessage();
            throw e;
        }
    }

    public void closeEntry() throws Exception {
        try {
            if(zipStream!=null){
                zipStream.closeEntry();
            } else {
                throw new Exception("Not running");
            }
        } catch (Exception e){
            error = true;
            errorMessage = e.getMessage();
            throw e;
        }
    }
}
