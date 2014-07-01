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
package com.connexience.server.model.messages;

import com.connexience.server.model.ServerObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Martyn
 * Date: Jul 17, 2009
 */
public class Message extends ServerObject implements Serializable
{
    /**
     * Class version UID.
     * 
     * Please increment this value whenever your changes may cause 
     * incompatibility with the previous version of this class. If unsure, ask 
     * one of the core development team or read:
     *   http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     *   http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 1L;


  /**
   * Time that this request was made
   */
  private Date timestamp;

  /**
   * Text of the message
   */
  private String message;

  /**
   * The id of the sender
   * */
  private String senderId;

  /**
   * The id of the recipient
   * */
  private String recipientId;

  public Message()
  {
    super();
    this.timestamp = new Date();
  }

  /**
   * Get the creation time of this request
   */
  public Date getTimestamp()
  {
    return timestamp;
  }

  /**
   * Set the creation time of this request
   */
  public void setTimestamp(Date timestamp)
  {
    this.timestamp = timestamp;
  }

  /**
   * Get the message that will be displayed to the target user
   */
  public String getMessage()
  {
    return message;
  }

  /**
   * Set the message that will be displayed to the target user
   */
  public void setMessage(String message)
  {
    this.message = message;
  }

  public String getSenderId()
  {
    return senderId;
  }

  public void setSenderId(String senderId)
  {
    this.senderId = senderId;
  }

  public String getRecipientId()
  {
    return recipientId;
  }

  public void setRecipientId(String recipientId)
  {
    this.recipientId = recipientId;
  }
}
