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
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.storage.DataStore;
import com.connexience.server.model.storage.migration.DataStoreMigration;
import com.connexience.server.model.storage.migration.DocumentVersionMigration;

import javax.ejb.Remote;
import java.util.List;

/**
 * This interface defines an EJB that can migrate data from one data store to 
 * another.
 * @author hugo
 */
@Remote
public interface DataStoreMigrationRemote {
    /** List all of the current migrations */
    public List listMigrations(Ticket ticket) throws ConnexienceException;
    
    /** Create a new migration */
    public DataStoreMigration createMigration(Ticket ticket, DataStore newStore) throws ConnexienceException;
    
    /** Start a migration */
    public void startMigration(Ticket ticket, long migrationId) throws ConnexienceException;
    
    /** Stop a migration */
    public void stopMigration(Ticket ticket, long migrationId) throws ConnexienceException;
    
    /** Get a migration by id */
    public DataStoreMigration getMigration(Ticket ticket, long migrationId) throws ConnexienceException;
    
    /** Save a migration */
    public DataStoreMigration saveMigration(Ticket ticket, DataStoreMigration migration) throws ConnexienceException;
    
    /** Add files not yet in a migration */
    public int addNewMigrationDocuments(Ticket ticket, long migrationId) throws ConnexienceException;
    
    /** Get the next file to be migrated from a migration */
    public boolean sendJMSMessageForNextDocumentInMigration(Ticket ticket, long migrationId) throws ConnexienceException;
    
    /** Delete a migration */
    public void deleteMigration(Ticket ticket, long migrationId) throws ConnexienceException;
    
    /** Delete all of the documents for a migration */
    public void deleteDocumentsForMigration(Ticket ticket, long migrationId) throws ConnexienceException;
    
    /** Get the number of files in a migration */
    public int getNumberOfDocumentsInMigration(Ticket ticket, long migrationId) throws ConnexienceException;
    
    /** Get the number of files in a migration with a certain status */
    public int getNumberOfDocumentsInMigrationWithStatus(Ticket ticket, long migrationId, int status) throws ConnexienceException;
    
    /** Get the list of failed file migrations from a migration */
    public List getFailedDocumentsInMigration(Ticket ticket, long migrationId) throws ConnexienceException;
    
    /** Retry all of the failed files from a migration */
    public void retryFailedDocumentsForMigration(Ticket ticket, long migrationId) throws ConnexienceException;
    
    /** Reset any migration documents that were in the middle of copying or in the JMS queue */
    public void resetInProgressDocumentMigrations(Ticket ticket, long migrationId) throws ConnexienceException;
    
    /** Finish a migration and potentially switch the organisation to the new data store */
    public void finishMigration(Ticket ticket, long migrationId) throws ConnexienceException;
    
    /** Get a record for an individual document version migration */
    public DocumentVersionMigration getDocumentVersionMigration(Ticket ticket, long documentVersionMigrationId) throws ConnexienceException;
    
    /** Save the status of a document version migration */
    public DocumentVersionMigration saveDocumentVersionMigration(Ticket ticket, DocumentVersionMigration dvm) throws ConnexienceException;
 
    /** Get the number of unmigrated documents for a migration */
    public int getNumberOfUnmigratedDocuments(Ticket ticket, long migrationId) throws ConnexienceException;
}