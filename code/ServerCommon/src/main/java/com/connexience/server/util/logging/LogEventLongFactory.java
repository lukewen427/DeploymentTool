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
public class LogEventLongFactory implements ILogEventFactory
{
   private LogEventLongManager manager = new LogEventLongManager();

  public ILogEvent newRegisteration(Date timestamp, String userId)
  {
    Long luserId = manager.addUser(userId);
    return new LogEventLongs(timestamp.getTime(), luserId, Operation.REGISTER.toString());
  }

  public ILogEvent newLogin(Date timestamp, String userId)
  {

    return new LogEventLongs(timestamp.getTime(), manager.addUser(userId), Operation.LOGIN.toString());
  }

  public ILogEvent newRunWorkflow(Date timestamp, String userId, String workflowId)
  {
    return new LogEventLongs(timestamp.getTime(), manager.addUser(userId), Operation.RUN_WORKFLOW.toString(), manager.addWorkflow(workflowId));
  }

  public ILogEvent newMakeFriends(Date timestamp, String user1, String user2)
  {
    return new LogEventLongs(timestamp.getTime(), manager.addUser(user1), Operation.MAKE_FRIEND.toString(), manager.addUser(user2));
  }

  public ILogEvent newMakeGroup(Date timestamp, String userId, String groupId)
  {
    return new LogEventLongs(timestamp.getTime(), manager.addUser(userId), Operation.MAKE_GROUP.toString(), manager.addGroup(groupId));
  }

  public ILogEvent newCreateService(Date timestamp, String userId, String serviceId)
  {
    return new LogEventLongs(timestamp.getTime(), manager.addUser(userId), Operation.NEW_SERVICE.toString(), manager.addService(serviceId));
  }

  public ILogEvent newCreateWorkflow(Date timestamp, String userId, String workflowId)
  {
    return new LogEventLongs(timestamp.getTime(), manager.addUser(userId), Operation.NEW_WORKFLOW.toString(), manager.addWorkflow(workflowId));
  }

  public ILogEvent newReadData(Date timestamp, String userId, String dataId)
  {
    return new LogEventLongs(timestamp.getTime(), manager.addUser(userId), Operation.READ_DATA.toString(), manager.addData(dataId));
  }

  public ILogEvent newReadDataByWorkflow(Date timestamp, String userId, String dataId, String workflowId)
  {
    return new LogEventLongs(timestamp.getTime(), manager.addUser(userId), Operation.READ_DATA.toString(), manager.addData(dataId), manager.addWorkflow(workflowId));
  }

  public ILogEvent newWriteData(Date timestamp, String userId, String dataId)
  {
    return new LogEventLongs(timestamp.getTime(), manager.addUser(userId), Operation.WRITE_DATA.toString(), manager.addData(dataId));
  }

  public ILogEvent newWriteDataByWorkflow(Date timestamp, String userId, String dataId, String workflowId)
  {
    return new LogEventLongs(timestamp.getTime(), manager.addUser(userId), Operation.WRITE_DATA.toString(), manager.addData(dataId), manager.addWorkflow(workflowId));
  }

  public ILogEvent newJoinGroup(Date timestamp, String userId, String groupId)
  {
    return new LogEventLongs(timestamp.getTime(), manager.addUser(userId), Operation.JOIN_GROUP.toString(), manager.addGroup(groupId));
  }

  public ILogEvent newPermission(Date timestamp, String granterId, String dataId, String granteeId)
  {
    return new LogEventLongs(timestamp.getTime(), manager.addUser(granterId), Operation.SHARE_DATA.toString(), manager.addData(dataId), manager.addUser(granteeId));
  }

  //todo: writing a new version - need separate event type as writebyworkflow uses same number of params?

}