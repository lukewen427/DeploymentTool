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
package com.connexience.server.model.logging.graph;

import java.util.Date;

/**
 * This operation is triggered whenever a workflow service writes a piece of
 * data.
 *
 * @author hugo
 */
public class UserReadOperation extends GraphOperation
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
   * ID of the document that was read
   */
  String documentId;

  /**
   * Version ID of the document that was written
   */
  String versionId;

  /**
   * Name of the document being written
   */
  String documentName;

  /**
   * Version number of the document being written
   */
  String versionNumber;

  /** The name of the user who downloaded this - needed to update the user object if
   * it doesn't already have a name set for the user */
  String username;


  public UserReadOperation()
  {
  }

  public UserReadOperation(String documentId, String versionId, String documentName, String versionNumber, String userId, Date timestamp, String username)
  {
    super();
    this.documentId = documentId;
    this.versionId = versionId;
    this.documentName = documentName;
    this.versionNumber = versionNumber;
    setUserId(userId);
    setTimestamp(timestamp);
    setUsername(username);
  }

  public String getDocumentId()
  {
    return documentId;
  }

  public void setDocumentId(String documentId)
  {
    this.documentId = documentId;
  }

  public String getVersionId()
  {
    return versionId;
  }

  public void setVersionId(String versionId)
  {
    this.versionId = versionId;
  }

  public String getDocumentName()
  {
    return documentName;
  }

  public void setDocumentName(String documentName)
  {
    this.documentName = documentName;
  }

  public String getVersionNumber()
  {
    return versionNumber;
  }

  public void setVersionNumber(String versionNumber)
  {
    this.versionNumber = versionNumber;
  }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}