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

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: TempAdmin
 * Date: 31-Dec-2009
 * Time: 21:27:38
 * To change this template use File | Settings | File Templates.
 */
public class LogEventString implements Serializable, ILogEvent
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


    private String id = "";
    private long timestamp;
    private String userId = "";
    private String operation = "";
    private String param = "";
    private String param2 = "";

    public static final String WORKFLOW_ID_UNKOWN = "Workflow id is unknown";
    public static final String GROUP_ID_UNKOWN = "Group id is unknown";
    public static final String  INVOCATION_ID_UNKOWN =  "Invocation id is unkownn";

    public String toString()
    {
        return "LogEventString: (t=" + timestamp + ",u=" + userId + ",op=" + operation + ",param=" + param + ",param2=" + param2 + ")";
    }

    public LogEventString()
    {
    }

    public LogEventString(long timestamp, String userId, String operation)
    {
        this.timestamp = timestamp;
        this.userId = userId;
        this.operation = operation;
    }

    public LogEventString(long t, String u, String o, String p)
    {
        timestamp = t;
        userId = u;
        operation = o;
        param = p;
        param2 = "";
    }

    public LogEventString(long t, String u, String o, String p, String p2)
    {
        timestamp = t;
        userId = u;
        operation = o;
        param = p;
        param2 = p2;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getOperation()
    {
        return operation;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }

    public String getParam()
    {
        return param;
    }

    public void setParam(String param)
    {
        this.param = param;
    }

    public String getParam2()
    {
        return param2;
    }

    public void setParam2(String param2)
    {
        this.param2 = param2;
    }
}
