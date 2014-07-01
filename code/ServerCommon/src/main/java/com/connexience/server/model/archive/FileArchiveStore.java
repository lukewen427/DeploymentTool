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
package com.connexience.server.model.archive;

import com.connexience.server.ConnexienceException;

/**
 * This class acts as an archive store that stores everything on disk. The archive store is organised
 * as follows:
 *
 *  Each organisation has its own folder
 *  DocumentRecords are in sub-folders determined by the last two digits of the id
 *  Each DocumentRecord is a folder, which contains all of the versions
 *
 * @author swheater
 */
public class FileArchiveStore extends ArchiveStore {
    /** Directory for storage */
    private String directory;
    
    /** Creates a new instance of FileArchiveStore */
    public FileArchiveStore() {
    }

    /** Start Archiving of DocumentRecord */
    public void startArchiving(String documentId, String dataStoreId) throws ConnexienceException {
        try {
            // Message to worker
            System.out.println("**** FileArchiveStore: Archiving to '" + directory + "'");
            throw new UnsupportedOperationException();
        } catch (Exception exception){
            throw new ConnexienceException("Error starting File archiving: " + exception.getMessage());
        }
    }

    /** Start Archiving of DocumentRecord */
    @Override
    public void startUnarchiving(String documentId, String dataStoreId) throws ConnexienceException {
        try {
            // Message worker
            System.out.println("**** FileArchiveStore: Unarchiving from '" + directory + "'");
            throw new UnsupportedOperationException();
        } catch (Exception exception){
            throw new ConnexienceException("Error starting File unarchiving: " + exception.getMessage());
        }
    }

    /** Set the Directory */
    public String getDirectory() {
        return directory;
    }

    /** Get the Directory */
    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
