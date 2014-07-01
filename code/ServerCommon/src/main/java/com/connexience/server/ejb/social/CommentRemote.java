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
import com.connexience.server.model.social.Comment;

import javax.ejb.Remote;
import java.util.List;

/**
 * This interface defines a bean that can manage comments attached to server objects.
 * @author hugo
 */
@Remote
public interface CommentRemote {
  /**
   * Create a comment for a server object
   */
  Comment createComment(Ticket ticket, String objectId, String text) throws ConnexienceException;

  /**
   * Get the comments attached to a server object
   */
  List getComments(Ticket ticket, String objectId) throws ConnexienceException;

  /**
   * Update the details of a comment - throws an exception if the comment does not exist
   */
  Comment updateComment(Ticket ticket, String commentId, String text) throws ConnexienceException;

  /**
   * Delete a comment
   */
  void deleteComment(Ticket ticket, String commentId) throws ConnexienceException;

  /**
   * Get a comment
   */
  Comment getComment(Ticket ticket, String commentId) throws ConnexienceException;
  
  /**
   * Create a comment on a server object
   */
  Comment createComment(Ticket ticket, String objectId, String text, String authorName) throws ConnexienceException;

  /**
   * Get the number of comments for an object
   */
  Long getNumberOfComments(Ticket ticket, String objectId);
}
