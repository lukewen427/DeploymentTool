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
package com.connexience.server.model.organisation;

import com.connexience.server.model.Membership;

import java.io.Serializable;

/**
 * This class represents a membership of an organisation
 * @author hugo
 */
public class OrganisationMembership implements Membership, Serializable {
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


    /** Object is a user */
    public static final int USER_MEMBERSHIP = 0;
    
    /** Object is a group */
    public static final int GROUP_MEMBERSHIP = 1;
    
    /** Membership id */
    private long id;
    
    /** Organisation id */
    private String organisationId;
    
    /** Id of principal belonging to organisation */
    private String principalId;
    
    /** Object type */
    private int objectType;
    
    /** Creates a new instance of OrganisationMembership */
    public OrganisationMembership() {
    }

    /** Get the membership id */
    public long getId() {
        return id;
    }
    
    /** Set the membership id */
    public void setId(long id) {
        this.id = id;
    }

    /** Get the organisation id */
    public String getOrganisationId() {
        return organisationId;
    }

    /** Get the organisation id */
    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    /** Get the id of the user */
    public String getPrincipalId() {
        return principalId;
    }

    /** Set the id of the user */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /** Get the object that the principal is a member of */
    public String getMemberContainerId() {
        return organisationId;
    }

    /** Get the type of object this membership applies to */
    public int getObjectType() {
        return objectType;
    }

    /** Set the type of object this membership applies to */
    public void setObjectType(int objectType) {
        this.objectType = objectType;
    }
}