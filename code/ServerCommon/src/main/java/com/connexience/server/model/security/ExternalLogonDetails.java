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

import java.io.Serializable;
import java.util.UUID;

/**
 * Author: Simon
 * Date: Mar 24, 2010
 */
public class ExternalLogonDetails implements Serializable
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
     * The id of this object in the database
     */
    private long id;

    /**
     * The user id that this external id is for
     */
    private String userId;

    /**
     * The id provided by the external site such as an OpenId
     */
    private String externalUserId;

    /**
     * A temporary id that is created and put in a form for the user to fill in their name
     * or confirm linking to an existing account.  Deleted when User account created or linked.
     */
    private String temporaryId;

    /**
     * A string identifying the provider for these external logon details
     */
    private String provider;

    public ExternalLogonDetails()
    {
    }

    public ExternalLogonDetails(String userId, String externalUserId)
    {
        this.userId = userId;
        this.externalUserId = externalUserId;
        this.temporaryId = UUID.randomUUID().toString();
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getExternalUserId()
    {
        return externalUserId;
    }

    public void setExternalUserId(String externalUserId)
    {
        this.externalUserId = externalUserId;
    }

    public String getTemporaryId()
    {
        return temporaryId;
    }

    public void setTemporaryId(String temporaryId)
    {
        this.temporaryId = temporaryId;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(final String provider)
    {
        this.provider = provider;
    }
}
