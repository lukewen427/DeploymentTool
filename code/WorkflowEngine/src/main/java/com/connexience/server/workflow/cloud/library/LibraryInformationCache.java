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
package com.connexience.server.workflow.cloud.library;

import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.workflow.api.API;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.*;

/**
 * This class caches service and library documents and the latest version id
 * of library and service files to reduce the number of API calls. A JMS message
 * is sent whenever a library or service changes which removes that item from
 * the cache.
 * @author hugo
 */
public class LibraryInformationCache {
    private static Logger logger = Logger.getLogger(LibraryInformationCache.class);
    
    /** Cache of latest versions for a given document ID */
    private ConcurrentHashMap<String, String> latestVersionCache = new ConcurrentHashMap<>();
    
    /** Cache of libraries by name */
    private ConcurrentHashMap<String, DocumentRecord> libraryCache = new ConcurrentHashMap<>();
    
    /** Cache of services by ID */
    private ConcurrentHashMap<String, DocumentRecord> serviceCache = new ConcurrentHashMap<>();
    
    /** Is this cache enabled */
    private boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    /** Get a service by id. This method checks the cache to see if it is there first */
    public synchronized DocumentRecord getServiceById(String serviceId, API api) throws Exception {
        if (enabled && serviceCache.containsKey(serviceId)) {
            return serviceCache.get(serviceId);
        } else {
            // Need to fetch from server
            logger.debug("Cache miss for service info: " + serviceId);
            DocumentRecord serviceDoc = api.getDocument(serviceId);
            if (serviceDoc != null) {
                serviceCache.put(serviceId, serviceDoc);
                return serviceDoc;
            } else {
                return null;
            }
        }
    }

    /** Get a workflow library by name */
    public synchronized DocumentRecord getLibraryByName(String libraryName, API api) throws Exception {
        if (enabled && libraryCache.containsKey(libraryName)) {
            return libraryCache.get(libraryName);
        } else {
            // Need to fetch from server
            logger.debug("Cache miss for library: " + libraryName);
            DocumentRecord libraryDoc = api.getDynamicWorkflowLibraryByName(libraryName);
            if (libraryDoc != null) {
                libraryCache.put(libraryName, libraryDoc);
                return libraryDoc;
            } else {
                return null;
            }
        }
    }

    /** Get the latest version of a document */
    public synchronized String getLatestDocumentVersionId(DocumentRecord doc, API api) throws Exception {
        if (enabled && latestVersionCache.containsKey(doc.getId())) {
            return latestVersionCache.get(doc.getId());
        } else {
            // Need to fetch latest document from the server
            logger.debug("Cache miss for latest document id for: " + doc.getId());
            String latestVersionId = api.getLatestVersionId(doc.getId());
            if(latestVersionId!=null){
                latestVersionCache.put(doc.getId(), latestVersionId);
                return latestVersionId;
            } else {
                return null;
            }
        }
    }
    
    /** Evict a library */
    public synchronized void evictLibraryByName(String libraryName){
        libraryCache.remove(libraryName);
        logger.debug("Removed library: " + libraryName + " from information cache");
    }
    
    /** Evict a latest version */
    public synchronized void evictLatestVersionId(String documentId){
        latestVersionCache.remove(documentId);
        logger.debug("Removed latest version information for: " + documentId + " from information cache");
    }
    
    /** Exict a service */
    public synchronized void evictService(String serviceId){
        serviceCache.remove(serviceId);
        logger.debug("Removed service: " + serviceId + " from information cache");
    }
    
    /** Evict everything from the cache */
    public synchronized void clearCache(){
        serviceCache.clear();
        libraryCache.clear();
        latestVersionCache.clear();
        logger.debug("Information cache cleared");
    }
}