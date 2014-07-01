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
package com.connexience.server.ejb.admin;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.User;
import com.connexience.server.model.storage.DataStore;

import javax.ejb.Remote;
import java.util.List;

/**
 * Author: Simon
 * Date: Jul 16, 2009
 */
@Remote
public interface AdminRemote {
    void moveWorkflowFolders(Ticket adminTicket) throws ConnexienceException;

    void createInboxFolders(Ticket adminTicket) throws ConnexienceException;

    void moveNotes(Ticket adminTicket) throws ConnexienceException;

    void moveProfileText(Ticket adminTicket) throws ConnexienceException;

    List<User> mapUsernameToEmail(Ticket ticket) throws ConnexienceException;

    void sendEmailToAllUsers(Ticket ticket, String subject, String content, String contentType) throws ConnexienceException;

    List<User> getAllUsers(Ticket ticket) throws ConnexienceException;

    /**
     * Method to add workflow invocation to users watch proeprty
     */
    String addWorkflowFavourite(Ticket ticket, String workflowId) throws ConnexienceException;

    /* Method to remove workflow invocation to users watch proeprty
    */
    void removeWorkflowFavourite(Ticket ticket, String workflowId) throws ConnexienceException;

    void addHashKeyForUsers(Ticket adminTicket) throws ConnexienceException;

    int fixUserHomeFolderOwnser(Ticket adminTicket) throws ConnexienceException;

    void migrateStorage(Ticket adminTicket, DataStore newStore) throws ConnexienceException;

    void migrateProjects(Ticket adminTicket) throws ConnexienceException;

    void migrateLAUsers(Ticket adminTicket) throws ConnexienceException;

    void createLADatasets(Ticket adminTicket) throws ConnexienceException;

	String cahaiPatientDump(final Ticket adminTicket) throws ConnexienceException;

    void reRunAllCahaiWorkflows(Ticket adminTicket) throws ConnexienceException;

    void addReviewDateToPatient(Ticket adminTicket) throws ConnexienceException;
}