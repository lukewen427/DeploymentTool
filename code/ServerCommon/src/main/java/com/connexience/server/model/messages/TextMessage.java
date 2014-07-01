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

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Simon
 * Date: Jul 17, 2009
 * This class represents a text message sent from one person to one or many other people.  It may be in reply to another message
 */
public class TextMessage extends Message implements Serializable
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
   * The id for this thread of messages.  Will be equal to the message id for the first message in the thread*/
  private String threadId;

  /**
   * Has this message been read
   * */
  private boolean read;

  /**
   * Title of the message
   * */
  private String title;


  public TextMessage()
  {
    super();
    this.setName("Text Message");
    this.setTimestamp(new Date());
  }

  public String getThreadId()
  {
    return threadId;
  }

  public void setThreadId(String threadId)
  {
    this.threadId = threadId;
  }

  public boolean isRead()
  {
    return read;
  }

  public void setRead(boolean read)
  {
    this.read = read;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }
}
