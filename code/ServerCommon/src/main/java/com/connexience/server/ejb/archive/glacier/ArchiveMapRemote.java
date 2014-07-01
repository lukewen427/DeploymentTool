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
package com.connexience.server.ejb.archive.glacier;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.archive.GlacierArchiveStore;
import com.connexience.server.model.archive.glacier.ArchiveMap;
import com.connexience.server.model.document.DocumentRecord;

import javax.ejb.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * General Archive related business interface enterprise bean.
 * (Original intent "This is the business interface for ArchiveMapBean enterprise bean.", need to be refactorised
 * to a number of beans)
 */
@Remote
public interface ArchiveMapRemote {
    public ArchiveMap createArchiveMapAsUploading(String documentRecordId, String documentVersionId, String dataStoreId, String archiveStoreId, String uploadId)
        throws RemoteException, ConnexienceException; 

    public ArchiveMap updateArchiveMapToReuploaded(ArchiveMap archiveMap, String uploadId)
        throws RemoteException, ConnexienceException;

    public ArchiveMap updateArchiveMapToUploaded(ArchiveMap archiveMap, String archiveId, String fullChecksum)
        throws RemoteException, ConnexienceException; 

    public ArchiveMap updateArchiveMapToDownloading(ArchiveMap archiveMap, String downloadId)
        throws RemoteException, ConnexienceException; 

    public ArchiveMap updateArchiveMapToDownloaded(ArchiveMap archiveMap)
        throws RemoteException, ConnexienceException; 

    public void deleteArchiveMap(ArchiveMap archiveMap)
        throws RemoteException, ConnexienceException; 

    public List<ArchiveMap> getArchivesForDocumentRecord(String documentRecordId)
        throws RemoteException, ConnexienceException; 

    public ArchiveMap getArchiveMapForDocumentRecordAndDocumentVersion(String documentRecordId, String documentVersionId)
        throws RemoteException, ConnexienceException; 

    public ArchiveMap getArchiveMapForDownload(String downloadId)
        throws RemoteException, ConnexienceException;

    public List<GlacierArchiveStore> getGlacierArchiveStores()
        throws RemoteException, ConnexienceException;

    public DocumentRecord checkAndUpdateDocumentRecordIfUploadComplete(DocumentRecord documentRecord)
        throws RemoteException, ConnexienceException;

    public DocumentRecord checkAndUpdateDocumentRecordIfDownloadComplete(DocumentRecord documentRecord)
        throws RemoteException, ConnexienceException;

    public DocumentRecord updateDocumentRecordToArchivingFailed(DocumentRecord documentRecord)
        throws RemoteException, ConnexienceException; 

    public List<DocumentRecord> getOrphanUnarchivingDocumentRecord(String archiveStoreId)
        throws RemoteException, ConnexienceException;

    public DocumentRecord updateDocumentRecordToUnarchivingFailed(DocumentRecord documentRecord)
        throws RemoteException, ConnexienceException;
}
