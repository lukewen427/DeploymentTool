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
 * Author: Simon
 * Date: Jan 14, 2010
 */
public class LogEventLongs implements Serializable, ILogEvent
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


    private String id;
    private long timestamp;
    private long userId;
    private String operation;
    private long param ;
    private long param2;

    public LogEventLongs()
    {
    }

    public LogEventLongs(long timestamp, long userId, String operation)
    {
        this.timestamp = timestamp;
        this.userId = userId;
        this.operation = operation;
    }

    public LogEventLongs(long timestamp, long userId, String operation, long param)
    {

        this.timestamp = timestamp;
        this.userId = userId;
        this.operation = operation;
        this.param = param;
    }

    public LogEventLongs(long timestamp, long userId, String operation, long param, long param2)
    {
        this.timestamp = timestamp;
        this.userId = userId;
        this.operation = operation;
        this.param = param;
        this.param2 = param2;
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

    public long getUserId()
    {
        return userId;
    }

    public void setUserId(long userId)
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

    public long getParam()
    {
        return param;
    }

    public void setParam(long param)
    {
        this.param = param;
    }

    public long getParam2()
    {
        return param2;
    }

    public void setParam2(long param2)
    {
        this.param2 = param2;
    }
}
