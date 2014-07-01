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

import com.connexience.server.model.Membership;

import java.io.Serializable;

/**
 * This object represents a Users membership within a group
 * @author hugo
 */
public class GroupMembership implements Membership, Serializable {
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


    /** Membership id */
    private long id;
    
    /** Group id */
    private String groupId;
    
    /** User id */
    private String userId;
    
    /** Creates a new instance of GroupMembership */
    public GroupMembership() {
    }

    /** Get this object id */
    public long getId() {
        return id;
    }

    /** Set this object id */
    public void setId(long id) {
        this.id = id;
    }

    /** Get the id of the group that this membership refers to */
    public String getGroupId() {
        return groupId;
    }

    /** Set the id of the group that this membership refers to */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /** Get the id of the user that this membership refers to */
    public String getUserId() {
        return userId;
    }

    /** Set the id of the user that this membership refers to */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** Get the object that the principal is a member of */
    public String getMemberContainerId() {
        return groupId;
    }

    /**
     * Get the principal
     */
    public String getPrincipalId() {
        return userId;
    }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof GroupMembership)) return false;

    GroupMembership that = (GroupMembership) o;

    if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
    if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = groupId != null ? groupId.hashCode() : 0;
    result = 31 * result + (userId != null ? userId.hashCode() : 0);
    return result;
  }
}