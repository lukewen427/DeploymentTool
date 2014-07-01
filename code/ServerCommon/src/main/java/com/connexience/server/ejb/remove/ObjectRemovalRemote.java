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
package com.connexience.server.ejb.remove;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.dashboard.Dashboard;
import com.connexience.server.model.datasets.Dataset;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentType;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.messages.Message;
import com.connexience.server.model.properties.PropertyGroup;
import com.connexience.server.model.scanner.RemoteFilesystemScanner;
import com.connexience.server.model.security.Group;
import com.connexience.server.model.security.StoredCredentials;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.User;
import com.connexience.server.model.social.Comment;
import com.connexience.server.model.social.Event;
import com.connexience.server.model.social.Tag;
import com.connexience.server.model.storage.migration.DataStoreMigration;
import com.connexience.server.model.workflow.DynamicWorkflowService;
import com.connexience.server.model.workflow.WorkflowFolderTrigger;
import org.hibernate.Session;

import javax.ejb.Remote;

/**
 * This is the business interface for ObjectRemoval enterprise bean.
 */
@Remote
public interface ObjectRemovalRemote
{

  /*
   * Utility method to remove a server object.
   *
   * Throws an exception if passed an object that there is not a method to remove in this bean
   * */
   public void remove(Ticket ticket, ServerObject so) throws ConnexienceException;

   /**
    * Remove a dynamic workflow service
    */
   public void remove(DynamicWorkflowService service) throws ConnexienceException;

  /**
   * Remove a document version
   */
  void remove(DocumentVersion version) throws ConnexienceException;

  /**
   * Remove an entire DocumentRecord
   */
  void remove(DocumentRecord doc) throws ConnexienceException;

  /**
   * Remove a filesystem scanner
   */
  void remove(RemoteFilesystemScanner scanner) throws ConnexienceException;
  
  /**
   * Remove a group and all of the associated memberships and permissions
   */
  void remove(Group group) throws ConnexienceException;

  /**
   * Remove a user from the system
   */
  void remove(User user) throws ConnexienceException;
          
  /**
   * Remove a document version with a session
   */
  void remove(DocumentVersion version, Session session) throws ConnexienceException;

  /**
   * Remove a Folder
   */
  void remove(Ticket ticket, Folder folder) throws ConnexienceException;

  /**
     * Remove a Folder
     */
  void removeWorkflowFolder(Ticket ticket, User user) throws ConnexienceException;
  /**
   * Remove a DocumentType
   */
  void remove(DocumentType docType) throws ConnexienceException;


  /**
   * Remove a properties group
   */
  void remove(PropertyGroup properties) throws ConnexienceException;

  /**
   * Remove a Comment
   */
  void remove(Comment comment) throws ConnexienceException;

  /**
   * Remove a Event
   */
  void remove(Event event) throws ConnexienceException;


  /*
 * Remove a single tag from a server object
 * */
  void remove(String serverObjectId, Tag tag) throws ConnexienceException;

  /**
   * Remove all of the tags for an item
   */
  void removeTags(Session session, String serverObjectId);


  void remove(Message m) throws ConnexienceException;
  
  void remove(WorkflowFolderTrigger trigger) throws ConnexienceException;
  
  void remove(Dataset dataset) throws ConnexienceException;
  
  void remove(Dashboard dashboard) throws ConnexienceException;
  
  void remove(DataStoreMigration migration) throws ConnexienceException;
  
  void remove(StoredCredentials credentials) throws ConnexienceException;
}
