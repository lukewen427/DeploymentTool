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
package com.connexience.server.util.logging;

import java.util.Date;

/**
 * Author: Simon
 * Date: Jan 14, 2010
 */
public interface ILogEventFactory
{
  ILogEvent newRegisteration(Date timestamp, String userId);

  ILogEvent newLogin(Date timestamp, String userId);

  ILogEvent newRunWorkflow(Date timestamp, String userId, String workflowId);

  ILogEvent newMakeFriends(Date timestamp, String user1, String user2);

  ILogEvent newMakeGroup(Date timestamp, String userId, String groupId);

  ILogEvent newCreateService(Date timestamp, String userId, String serviceId);

  ILogEvent newCreateWorkflow(Date timestamp, String userId, String workflowId);

  ILogEvent newReadData(Date timestamp, String userId, String dataId);

  ILogEvent newReadDataByWorkflow(Date timestamp, String userId, String dataId, String workflowId);

  ILogEvent newWriteData(Date timestamp, String userId, String dataId);

  ILogEvent newWriteDataByWorkflow(Date timestamp, String userId, String dataId, String workflowId);

  ILogEvent newJoinGroup(Date timestamp, String userId, String groupId);

  ILogEvent newPermission(Date timestamp, String granterId, String objectId, String granteeId);
}
