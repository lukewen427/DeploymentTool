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
 * This operation is triggered whenever a user writes a piece of
 * data.
 *
 * @author hugo
 */
public class ServiceSaveOperation extends GraphOperation
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
   * ID of the service
   */
  String serviceId;

  /**
   * Id of the version
   */
  String versionId;

  /**
   * Version number of the service
   */
  int versionNum;

  /**
   * Name of the service
   */
  String name;

  public ServiceSaveOperation()
  {
  }

  public ServiceSaveOperation(String serviceId, String versionId, int versionNum, String name, String userId, Date timestamp)
  {
    super();
    this.serviceId = serviceId;
    this.versionId = versionId;
    this.versionNum = versionNum;
    this.name = name;
    setUserId(userId);
    setTimestamp(timestamp);
  }

  public String getServiceId()
  {
    return serviceId;
  }

  public void setServiceId(String serviceId)
  {
    this.serviceId = serviceId;
  }

  public String getVersionId()
  {
    return versionId;
  }

  public void setVersionId(String versionId)
  {
    this.versionId = versionId;
  }

  public int getVersionNum()
  {
    return versionNum;
  }

  public void setVersionNum(int versionNum)
  {
    this.versionNum = versionNum;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }
}