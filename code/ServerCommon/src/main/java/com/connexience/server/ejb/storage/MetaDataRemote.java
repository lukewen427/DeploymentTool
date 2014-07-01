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
package com.connexience.server.ejb.storage;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.metadata.MetadataItem;
import com.connexience.server.model.metadata.MetadataQuery;
import com.connexience.server.model.metadata.MetadataSynonym;
import com.connexience.server.model.security.Ticket;

import javax.ejb.Remote;
import java.util.List;

/**
 * Author: Simon Date: May 11, 2009
 */
@Remote
public interface MetaDataRemote {
    /** List metadata for an object */
    List getObjectMetadata(Ticket ticket, String objectId) throws ConnexienceException;
    
    /** Add a piece of metadata to an object */
    public MetadataItem addMetadata(Ticket ticket, String objectId, MetadataItem metadata) throws ConnexienceException;
    
    /** Remove a piece of metadata from an object */
    public void removeMetadata(Ticket ticket, String objectId, long metadataId) throws ConnexienceException;
    
    /** Perform a metadata search */
    public List search(Ticket ticket, MetadataQuery query) throws ConnexienceException;
   
    /** Perform a metadata search with a start point and page size */
    public List search(Ticket ticket, MetadataQuery query, int startPosition, int pageSize) throws ConnexienceException;
    
    /** Get the number of results in a search */
    public int getResultCount(Ticket ticket, MetadataQuery query) throws ConnexienceException;
    
    /** List all of the synonyms */
    public List listSynonyms(Ticket ticket) throws ConnexienceException;
    
    /** Add a synonym */
    public MetadataSynonym addSynonym(Ticket ticket, MetadataSynonym synonym) throws ConnexienceException;
    
    /** Remove a synonym */
    public void removeSynonym(Ticket ticket, long id) throws ConnexienceException;

    /** Get a piece of metadata for an object */
    public MetadataItem getObjectMetadata(Ticket ticket, String objectId, String category, String name) throws ConnexienceException;


}