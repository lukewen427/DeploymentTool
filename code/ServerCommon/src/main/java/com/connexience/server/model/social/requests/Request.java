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
package com.connexience.server.model.social.requests;

import com.connexience.server.model.messages.Message;

import java.io.Serializable;

/**
 * This class represents the superclass for all requests, e.g. join group, make friends.
 *
 * Author: Simon
 * Date: Jun 22, 2009
 */
public class Request extends Message implements Serializable
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
     * Request is waiting for acknowledgement
     */
    public static final int REQUEST_PENDING = 0;
    /**
     * Request has been accepted
     */
    public static final int REQUEST_ACCEPTED = 1;
    /**
     * Request has been rejected
     */
    public static final int REQUEST_REJECTED = 2;

    /**
     * Request status
     */
    protected int status = REQUEST_PENDING;

    public Request()
    {
        super();
    }

    /**
     * Get the current status of this request
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * Set the current status of this request
     */
    public void setStatus(int status)
    {
        this.status = status;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;

        FriendRequest request = (FriendRequest) o;

        if (getStatus() != request.getStatus()) return false;
        if (getSenderId() != null ? !getSenderId().equals(request.getSenderId()) : request.getSenderId() != null)
            return false;
        if (getRecipientId() != null ? !getRecipientId().equals(request.getRecipientId()) : request.getRecipientId() != null)
            return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = getSenderId() != null ? getSenderId().hashCode() : 0;
        result = 31 * result + (getRecipientId() != null ? getRecipientId().hashCode() : 0);
        result = 31 * result + getStatus();
        return result;
    }

}
