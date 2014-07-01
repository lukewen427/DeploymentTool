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
package com.connexience.server.ejb.directory;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.security.Group;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.WebTicket;
import com.connexience.server.model.social.profile.GroupProfile;

import javax.ejb.Remote;
import java.util.HashMap;
import java.util.List;

/**
 * This is the business interface for GroupDirectory enterprise bean.
 */
@Remote
public interface GroupDirectoryRemote
{
  /**
   * Save a group
   */
  com.connexience.server.model.security.Group saveGroup(Ticket ticket, Group group) throws ConnexienceException;

   /**
   * Get a group by Id
   */
  com.connexience.server.model.security.Group getGroup(Ticket ticket, String groupId) throws ConnexienceException;

  /**
   * Get a group by name
   */
  com.connexience.server.model.security.Group getGroupByName(Ticket ticket, String groupName) throws ConnexienceException;
  
  /**
   * Add a user to a group
   */
  void addUserToGroup(Ticket ticket, String userId, String groupId) throws ConnexienceException;

  /**
   * Remove a user from a group
   */
  void removeUserFromGroup(Ticket ticket, String userId, String groupId) throws ConnexienceException;

  /**
   * List all of the users that are part of a specific group
   */
  List listGroupMembers(Ticket ticket, String groupId) throws ConnexienceException;

  /**
   * Search for groups
   */
  List searchForGroups(Ticket ticket, String searchText) throws ConnexienceException;

  /**
   * Remove a group
   */
  void removeGroup(Ticket ticket, String groupId) throws ConnexienceException;

  /*
  * Get the number of members of a group
  * */
  Long numberOfGroupMembers(Ticket ticket, String groupId) throws ConnexienceException;

  /*
  * Save the profile of a group
  * */
  public GroupProfile saveGroupProfile(Ticket ticket, GroupProfile groupProfile, String groupId) throws ConnexienceException;

  /** List groups - left for backwards compatibility with AxisWSDirectory */
  List listGroups(Ticket ticket) throws ConnexienceException;

  /**
   * List all of the users that are part of a specific group
   */
  List listGroupMembers(Ticket ticket, String groupId, int start, int numResults) throws ConnexienceException;

  /**
   * Get the number of members ina group
   */
  int getNumberOfGroupMembers(Ticket ticket, String groupId) throws ConnexienceException;
  
  /**
   * Get some basic statistics about a group. Includes number of shared files, events and members
   */
  public HashMap getGroupStatistics(Ticket ticket, String groupId) throws ConnexienceException;

  public Boolean changeDefaultStorageFolder(WebTicket ticket, String folderId) throws ConnexienceException ;

    /** List groups - left for backwards compatibility with AxisWSDirectory */
    List listNonProtectedGroups(Ticket ticket) throws ConnexienceException;
}
