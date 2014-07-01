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
package com.connexience.server.ejb.ticket;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.security.ExternalLogonDetails;
import com.connexience.server.model.security.RememberMeLogin;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.WebTicket;

import javax.ejb.Remote;
import java.util.List;

/**
 * This is the business interface for Ticket enterprise bean.
 */
@Remote
public interface TicketRemote {
    /**
     * Create a ticket for a username and password
     */
    com.connexience.server.model.security.Ticket acquireTicket(String username, String password) throws ConnexienceException;

    /**
     * Create a ticket with specified groups
     */
    com.connexience.server.model.security.Ticket acquireTicket(String username, String password, List groupIds) throws ConnexienceException;

    /**
     * List the principals that a ticket has registered for
     */
    List listTicketPrincipals(String ticketId) throws ConnexienceException;

    /**
     * Does a ticket have a group associated with it
     */
    boolean ticketHasGroup(Ticket ticket, String groupId) throws ConnexienceException;

    /** Get the actual ticket */
    public Ticket getTicket(String ticketId) throws ConnexienceException;

    /**
     * List the groups that a ticket is currently associated with
     */
    List listTicketGroups(Ticket ticket) throws ConnexienceException;

    /**
     * List the group ids associated with a ticket as a String[] array
     */
    String[] listTicketGroupIds(Ticket ticket) throws ConnexienceException;

    /**
     * List the IDs of all of the ticket principals
     */
    String[] listTicketPrincipalIds(Ticket ticket) throws ConnexienceException;

    /**
     * Create a ticket for a username. This is used by the web pages, as the user has already
     * logged on using their browser
     */
    com.connexience.server.model.security.WebTicket createWebTicket(String username) throws ConnexienceException;

    /** Create a web ticket with a username and password */
    com.connexience.server.model.security.WebTicket createWebTicket(String username, String password) throws ConnexienceException;

    /**
     * Create a ticket for a user given a database id. This is used by the social networking
     * webservice.
     */
    com.connexience.server.model.security.WebTicket createWebTicketForDatabaseId(String id) throws ConnexienceException;
    
    /** Create a public ticket */
    com.connexience.server.model.security.WebTicket createPublicWebTicket() throws ConnexienceException;

  /**
   * Allow an admin user to change users.  Returns a ticket with the other users credentials.
 */
  WebTicket switchUsers(Ticket ticket, String otherUserId) throws ConnexienceException;

  /**
   * Set a value that allows a user to login automatically
   */
  String addRememberMe(Ticket ticket) throws ConnexienceException;

  /**
  * Set a value that allows a user to login automatically
  */
  RememberMeLogin checkRememberMe(String cookieId) throws ConnexienceException;

  /**
     * Set a value that allows a user to login automatically
     */
  void deleteRememberMe(Ticket ticket, String cookieId) throws ConnexienceException;

  ExternalLogonDetails getExternalLogon(String externalUserId) throws ConnexienceException;
  
  ExternalLogonDetails getExternalLogon(String externalUserId, String provider) throws ConnexienceException;

  ExternalLogonDetails addExternalLogon(String userId, String externalUserId) throws ConnexienceException;
  
  ExternalLogonDetails addExternalLogon(String userId, String externalUserId, String provider) throws ConnexienceException;
}

