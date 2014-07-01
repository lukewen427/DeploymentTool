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
package com.connexience.server.model.security;

/**
 * This class represents a logon configuration for a User
 *
 * @author hugo
 */
public class LogonDetails
{
  /**
   * ID of logon object
   */
  private long id;

  /**
   * Logon name. This is used as the database id
   */
  private String logonName;

  /**
   * User ID
   */
  private String userId;

  /**
   * Password
   */
  private String hashedPassword;


  /**
   * Creates a new instance of LogonDetails
   */
  public LogonDetails()
  {
  }

  /**
   * Get the id of this logon object
   */
  public long getId()
  {
    return id;
  }

  /**
   * Set the id of this logon object
   */
  public void setId(long id)
  {
    this.id = id;
  }

  /**
   * Get the user logon name
   */
  public String getLogonName()
  {
    return logonName;
  }

  /**
   * Set the user logon name
   */
  public void setLogonName(String logonName)
  {
    this.logonName = logonName;
  }

  /**
   * Get the database id of the user
   */
  public String getUserId()
  {
    return userId;
  }

  /**
   * Set the database id of the user
   */
  public void setUserId(String userId)
  {
    this.userId = userId;
  }


  /**
   * Get the hashed password of this logon
   */
  public String getHashedPassword()
  {
    return hashedPassword;
  }

  /**
   * Set the hashed password of this logon
   */
  public void setHashedPassword(String hashedPassword)
  {
    this.hashedPassword = hashedPassword;
  }
}
