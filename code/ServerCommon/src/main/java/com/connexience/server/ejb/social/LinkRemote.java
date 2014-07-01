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
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.social.Link;

import javax.ejb.Remote;
import java.util.Collection;

/**
 * Author: Simon
 * Date: 15-Jul-2008
 */
@Remote
public interface LinkRemote
{

  /*
  * Get all of the objects that are linked to from the source object.  Returns a collection of ServerObjects rather than ids
  **/
  Collection<ServerObject> getLinkedSourceObjects(Ticket ticket, ServerObject sourceObject) throws ConnexienceException;

  /**
   * Get all of the objects of a particular type that are linked to from the source object.
   */
  Collection getLinkedSourceObjects(Ticket ticket, ServerObject sourceObject, Class type) throws ConnexienceException;

  /*
  * Get all of the objects that link to a particular object
  **/
  Collection getLinkedSinkObjects(Ticket ticket, ServerObject sinkObject) throws ConnexienceException;

  /**
   * Get all of the objects of a particular type that are linked to the source object.
   */
  Collection getLinkedSinkObjects(Ticket ticket, ServerObject sinkObject, Class type) throws ConnexienceException;

  /**
   * Get a collection of links that emanate from the source object.  Links contain the id of the sinkObject
   */
  Collection getSourceLinks(Ticket ticket, ServerObject sourceObject) throws ConnexienceException;

  /**
   * Get a collection of links that link to the object
  */
  Collection getSinkLinks(Ticket ticket, ServerObject sinkObject) throws ConnexienceException;

  /**
  * Get a link, if one exists between the source and the sink object
  * */
  Link getLink(Ticket ticket, ServerObject sourceObject, ServerObject sinkObject) throws ConnexienceException;

  /**
  * Get a Link from its id
  * */
  Link getLink(Ticket ticket, String id) throws ConnexienceException;

  void removeLink(Ticket ticket, String id) throws ConnexienceException;

  /**
   * Remove all links that an object is the sink of
   */
  void removeSinkLinks(Ticket ticket, ServerObject sinkObject) throws ConnexienceException;

  /**
  * Remove all links that an object is the source of.
  * Must own the link (and implicitly the object otherwise the link couldn't have been created
  * */
  void removeSourceLinks(Ticket ticket, ServerObject sourceObject) throws ConnexienceException;

  /**
   * Create a link between two objects in the social networking system.  The caller must be the owner of the sourceObject
   * It is not possible to link the same object to itself
   */
  Link createLink(Ticket ticket, ServerObject sourceObject, ServerObject sinkObject) throws ConnexienceException;

//  Link createServerDocumentLink(Ticket ticket, ServerObject sourceObject, String sinkObjectId) throws ConnexienceException;
//
//  public boolean removeServerObjectLink(Ticket ticket, String proxyObjectId) throws ConnexienceException;

  /**
   * Get all of the objects of a particular type that are linked to from the source object that we have permission to read.
   */
  Long getNumberOfLinkedSourceObjects(Ticket ticket, ServerObject sourceObject, Class type) throws ConnexienceException;

  /**
   * Get all of the objects of a particular type that are linked to from the source object that we have permission to read.
   */
  Collection getLinkedSourceObjects(Ticket ticket, ServerObject sourceObject, Class type, int start, int maxResults) throws ConnexienceException;

//  boolean removeServerDocumentProxy(Ticket ticket, String serverId) throws ConnexienceException;
//
//  ProxyServerObject getServerDocumentProxy(Ticket ticket, String serverId) throws ConnexienceException;
}
