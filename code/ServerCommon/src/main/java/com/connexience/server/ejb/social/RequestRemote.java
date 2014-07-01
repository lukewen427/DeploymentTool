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
package com.connexience.server.ejb.social;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.social.requests.FriendRequest;

import javax.ejb.Remote;
import java.util.List;

/**
 * This interface defines the functionality of the Request handling EJB that deals
 * with requests for friendships, group membership, application links etc.
 *
 * @author hugo
 */
@Remote
public interface RequestRemote
{

  /**
   * Get all of the requests with a certain status sent by a user
   */
  public List listSentRequests(Ticket ticket, int status) throws ConnexienceException;

  /** Get all the requests that have been sent to a user */
//    public List listReceivedRequests(Ticket ticket, int status) throws ConnexienceException;

  /**
   * Accept a request
   */
  public void acceptRequest(Ticket ticket, String requestId) throws ConnexienceException;

  /**
   * Reject a request
   */
  public void rejectRequest(Ticket ticket, String requestId) throws ConnexienceException;

  /**
   * Return the number of outstanding requests for a user
   */
  public int outstandingRequests(Ticket ticket) throws ConnexienceException;

  /**
   * Get all the requests that have been sent to a user
   */
  List listReceivedFriendRequests(Ticket ticket, int status) throws ConnexienceException;

  /**
   * Get all the requests that have been sent to a user
   */
  List listReceivedRequests(Ticket ticket, int status) throws ConnexienceException;

  /**
   * Post a new request object. This is saved and then will be visible to the
   * target profile the next time they check
   */
  FriendRequest postFriendRequest(Ticket ticket, FriendRequest request) throws ConnexienceException;

  /*
 * Method to determine whether a user has already requested to join a group
 * */
  boolean joinGroupRequestExists(Ticket ticket, String userId, String groupId) throws ConnexienceException;

  /*
 * Method to determine whether a friend request already exists
 * */
  FriendRequest friendRequestExists(Ticket ticket, String sourceId, String sinkId) throws ConnexienceException;

  /**
   * Get all the requests that have been sent to a user
  */
  Long getNumberOfReceivedRequests(Ticket ticket, int status) throws ConnexienceException;
}
