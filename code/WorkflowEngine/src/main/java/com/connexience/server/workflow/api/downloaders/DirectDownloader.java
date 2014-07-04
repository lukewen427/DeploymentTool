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
package com.connexience.server.workflow.api.downloaders;
import com.connexience.server.workflow.api.*;
import com.connexience.server.ConnexienceException;
import com.connexience.server.model.document.*;
import com.connexience.server.model.storage.DataStore;
import com.connexience.server.workflow.util.ZipUtils;
import java.io.InputStream;
import org.apache.log4j.Logger;

/**
 * This downloader makes use of the DataStore object supplied by the server to make
 * a direct connection with the storage infrastructure.
 * @author hugo
 */
public class DirectDownloader extends Downloader {
    private static Logger logger = Logger.getLogger(DirectDownloader.class);
    
    @Override
    public boolean download() throws ConnexienceException {
        InputStream inStream = null;
        try {
            DataStore ds = parent.getDataStore();
            DocumentVersion version = createDocumentVersion();
            inStream = ds.getInputStream(document, version);
            ZipUtils.copyInputStream(inStream, stream);
            
            logDataRead();
            
            return true;
        } catch (Exception e){
            throw new ConnexienceException("Error downloading data using DirectDownloader: " + e.getMessage(), e);
        } finally {
            if(inStream!=null){
                try {inStream.close();}catch(Exception e){}
            }
        }
    }


    @Override
    public InputStream getInputStream() throws ConnexienceException {
        try {
            DataStore ds = parent.getDataStore();
            InputStream stream = ds.getInputStream(document, createDocumentVersion());
            
            if(stream!=null){
                logDataRead();
                return stream;
            } else {
                throw new ConnexienceException("No input stream returned");
            }
        } catch (Exception e){
            throw new ConnexienceException("Error downloading data using DirectDownloader: " + e.getMessage(), e);
        }
    }
    
    private DocumentVersion createDocumentVersion() throws Exception {
        DocumentVersion docVersion = new DocumentVersion();
        docVersion.setDocumentRecordId(document.getId());
        
        if(versionId==null && versionNumber==-1){
            // No version or number
            logger.debug("Fetching latest version from server");
            versionId = parent.getLatestVersionId(document.getId());
            versionNumber = document.getCurrentVersionNumber();
            docVersion.setId(versionId);

        } else if(versionId!=null){
            // Actual version
            docVersion.setId(versionId);
            versionNumber = document.getCurrentVersionNumber();

        } else if(versionId==null && versionNumber!=-1){
            // Version number
            logger.debug("Fetching version from server");
            docVersion = parent.getDocumentVersion(document, versionNumber);
            versionId = docVersion.getId();
            versionNumber = docVersion.getVersionNumber();
        }        
        
        return docVersion;
    }
}