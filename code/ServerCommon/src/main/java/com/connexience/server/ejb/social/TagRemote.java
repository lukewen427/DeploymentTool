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
import com.connexience.server.model.social.Tag;

import javax.ejb.Remote;
import java.util.Collection;

/**
 * Author: Simon
 * Date: Jun 3, 2009
 *
 * This bean deals with adding and removing tags from objects
 */
@Remote
public interface TagRemote
{
  /*
 * Add a com.connexience.server.social.tag to a server object
 * */
  Tag addTag(Ticket ticket, ServerObject so, String tag) throws ConnexienceException;

  /*
 * Get all of the tags for a server object
 * */
  Collection getTags(Ticket ticket, ServerObject so) throws ConnexienceException;

  /*
 * Get a com.connexience.server.social.tag from an id
 * */
  Tag getTag(Ticket ticket, String tagId) throws ConnexienceException;

  /*
 * Remove a com.connexience.server.social.tag from a server object
 * */
  void removeTag(Ticket ticket, ServerObject so, Tag tag) throws ConnexienceException;
}
