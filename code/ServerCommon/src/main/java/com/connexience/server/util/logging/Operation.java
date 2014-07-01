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

/**
 * Created by IntelliJ IDEA.
 * User: TempAdmin
 * Date: 31-Dec-2009
 * Time: 19:51:53
 * To change this template use File | Settings | File Templates.
 */
public enum Operation
{
  REGISTER,
  LOGIN, LOGOUT,
  RUN_WORKFLOW, READ_DATA, WRITE_DATA,
  MAKE_FRIEND, MAKE_GROUP, JOIN_GROUP,
  NEW_SERVICE, NEW_WORKFLOW,
  NO_LONGER_FRIEND, LEAVE_GROUP,
  SHARE_DATA, SHARE_DATA_WITH_GROUP,
  UNSHARE_DATA, UNSHARE_DATA_WIH_GROUP,
  SHARE_WORKFLOW, SHARE_WORKFLOW_WITH_GROUP,
  UNSHARE_WORKFLOW, UNSHARE_WORKFLOW_WITH_GROUP,
  SHARE_SERVICE, SHARE_SERVICE_WITH_GROUP,
  UNSHARE_SERVICE, UNSHARE_SERVICE_WITH_GROUP,
  DO_NOTHING
}
