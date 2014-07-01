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
package com.connexience.server.ejb.scanner;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.scanner.RemoteFilesystemObject;
import com.connexience.server.model.scanner.RemoteFilesystemScanner;
import com.connexience.server.model.security.Ticket;
import java.util.List;

/**
 * This interface defines the scanner bean that can periodically scan remote
 * filesystems for files to upload.
 * @author hugo
 */
public interface ScannerRemote {
    /** Save a scanner */
    public RemoteFilesystemScanner saveScanner(Ticket ticket, RemoteFilesystemScanner scanner) throws ConnexienceException;
    
    /** Remove a scanner */
    public void removeScanner(Ticket ticket, long id) throws ConnexienceException;
    
    /** Remove all of the stored state for a scanner. This removes all of the stored filesystem objects */
    public void resetScanner(Ticket ticket, long id) throws ConnexienceException;
    
    /** List all of the scanners */
    public List listScanners(Ticket ticket) throws ConnexienceException;
    
    /** List the scanners for a user */
    public List listScannersForTicket(Ticket ticket) throws ConnexienceException;
    
    /** Get a scanner by ID */
    public RemoteFilesystemScanner getScanner(Ticket ticket, long id) throws ConnexienceException;
    
    /** Get all of the file state objects for a scanner */
    public List listFilesystemObjects(Ticket ticket, long scannerId) throws ConnexienceException;
    
    /** Save a file state object */
    public RemoteFilesystemObject saveFilesystemObject(Ticket ticket, RemoteFilesystemObject fsObject) throws ConnexienceException;
    
    /** Save a file state object without checking security */
    public RemoteFilesystemObject saveFileSystemObjectWithoutSecurity(RemoteFilesystemObject fsObject) throws ConnexienceException;
    
    /** Get a remote filesystem object without doing a security check */
    public RemoteFilesystemObject getFileSystemObjectWithoutSecurity(long fsObjectId) throws ConnexienceException;
    
    /** Remove a remote filesystem object without doing a security check */
    public void removeFileSystemObjectWithoutSecurity(long fsObjectId) throws ConnexienceException;
    
    /** Remove a file state object */
    public void removeFilesystemObject(Ticket ticket, long id) throws ConnexienceException;
    
    /** Send an upload message */
    public void sendUploadMessage(Ticket ticket, long fsObjectId) throws ConnexienceException;
    
    /** Resend all of the upload messages */
    public void resendAllUploadMessages() throws ConnexienceException;
    
    /** Force an update of a scanner */
    public void scanForChanges(RemoteFilesystemScanner scanner) throws ConnexienceException;
}   