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

/**
 * This class represents a permission for a server object
 *
 * @author hugo
 */
public class Permission implements Serializable
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
     * Read permission
     */
    public static final String READ_PERMISSION = "read";

    /**
     * Write permission
     */
    public static final String WRITE_PERMISSION = "write";

    /**
     * Administrator permission
     */
    public static final String ADMINISTRATOR_PERMISSION = "admin";

    /**
     * Owner permission
     */
    public static final String OWNER_PERMISSION = "owner";

    /**
     * Owner permission
     */
    public static final String ADD_PERMISSION = "add";

    /**
     * Owner permission
     */
    public static final String EXECUTE_PERMISSION = "execute";


    /**
     * Database id
     */
    private long id;

    /**
     * ID of the secured object
     */
    private String targetObjectId;

    /**
     * ID of principal
     */
    private String principalId;

    /* Whether or not this permission is universal.  If so, the principalId is not checked */
    private boolean universal;

    /**
     * Permission type
     */
    private String type;

    /**
     * Creates a new instance of Permission
     */
    public Permission()
    {
    }

    /**
     * Get the ID of this permission object
     */
    public long getId()
    {
        return id;
    }

    /**
     * Set the ID of this permission object
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * Get the id of the secured object
     */
    public String getTargetObjectId()
    {
        return targetObjectId;
    }

    /**
     * Set the id of the secured object
     */
    public void setTargetObjectId(String targetObjectId)
    {
        this.targetObjectId = targetObjectId;
    }

    /**
     * Get the id of the principal that this permission refers to
     */
    public String getPrincipalId()
    {
        return principalId;
    }

    /**
     * Set the id of the principal that this permission relates to
     */
    public void setPrincipalId(String principalId)
    {
        this.principalId = principalId;
    }

    /**
     * Get the permission type. Read / write / owner etc
     */
    public String getType()
    {
        return type;
    }

    /**
     * Get the permission type. Read / write / owner etc
     */
    public void setType(String type)
    {
        this.type = type;
    }

    public boolean isUniversal()
    {
        return universal;
    }

    public void setUniversal(boolean universal)
    {
        this.universal = universal;
    }

    /**
     * Override the equals method
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof Permission)
        {
            Permission p = (Permission) obj;
            if ((p.getPrincipalId().equals(getPrincipalId()) && p.getTargetObjectId().equals(getTargetObjectId()) && p.getType().equals(getType()))
                    || (p.isUniversal() && p.getTargetObjectId().equals(getTargetObjectId()) && p.getType().equals(getType())))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

}