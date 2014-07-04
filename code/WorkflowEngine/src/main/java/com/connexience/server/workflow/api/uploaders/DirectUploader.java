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
import com.connexience.server.workflow.api.*;
import com.connexience.server.model.document.*;
import com.connexience.server.model.storage.DataStore;


/**
 * This class uploads data directly to supported DataStore objects obtained
 * from the server.
 * @author hugo
 */
public class DirectUploader extends Uploader {

    @Override
    public boolean upload() throws ConnexienceException {
        try {
            // Create the next version for the document
            DocumentVersion v = parent.createNextVersion(document);

            // Upload the data to the data store
            DataStore ds = parent.getDataStore();
            v = ds.readFromStream(document, v, stream);
            v.setUserId(parent.getTicket().getUserId());
            
            // Update the document version
            uploadedDocumentVersion = parent.updateDocumentVersion(v);
            logDataWrite();
            return true;
        } catch (Exception e){
            throw new ConnexienceException("Error uploading data directly to datastore: " + e.getMessage(), e);
        }
    }
}
