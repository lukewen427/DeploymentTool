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
import com.connexience.server.model.messages.Message;
import com.connexience.server.model.messages.TextMessage;
import com.connexience.server.model.notifcations.messages.NotificationMessage;
import com.connexience.server.model.security.Ticket;

import javax.ejb.Remote;
import java.util.Collection;
import java.util.List;

/**
 * Author: Simon
 * Date: Jul 17, 2009
 */
@Remote
public interface MessageRemote
{
  /**
   * Add a message to a users sent messages folder
   * */
  TextMessage addSentTextMessageToFolder(Ticket ticket, String receiverIds, String threadId, String title, String text) throws ConnexienceException;

  /**
   * Get the number of unread messages
   */
  Long getNumberOfUnreadMessages(Ticket ticket) throws ConnexienceException;

  /**
   * Get the messages in a thread
   */
  List<Message> getMessageThread(Ticket ticket, String threadId, int start, int maxResults) throws ConnexienceException;

  /**
   * Create a message from this user to a list of other users
   */
  TextMessage createTextMessage(Ticket ticket, String thisRecipeint, String allRecipients, String threadId, String title, String text) throws ConnexienceException;

  /**
   * Mark a message as read
   * */
  void markThreadAsRead(Ticket ticket, String threadId) throws ConnexienceException;

  Long getNumberOfMessagesInThread(Ticket ticket, String threadId) throws ConnexienceException;

  Long getNumberOfTextMessgesInFolder(Ticket ticket, String folderId) throws ConnexienceException;

  Long getNumberOfUnreadTextMessgesInFolder(Ticket ticket, String folderId) throws ConnexienceException;

  /**
   * Get the contents of the folder
   */
  Collection<Message> getMessages(Ticket ticket, String userId, String folderId, int start, int maxResults) throws ConnexienceException;

  /**
   * Get the TextMessages in this folder
  */
  Collection<Message> getTextMessages(Ticket ticket, String userId, String folderId, int start, int maxResults) throws ConnexienceException;

  void addNotificationMessageToInbox(Ticket ticket, String userId, Message message) throws ConnexienceException;

  void markNotificationMessageRead(Ticket ticket, NotificationMessage message) throws ConnexienceException;

  /**
   * Get the number of Messages the user has.  Threads will only be counted once
   * @param ticket Ticket for the user
   * @param userId user id of the user
   * @param folderId folder to use as the container
   * @return number of messages
   * @throws com.connexience.server.ConnexienceException something went wrong...
   */
  int getNumberOfMessages(Ticket ticket, String userId, String folderId) throws ConnexienceException;
}
