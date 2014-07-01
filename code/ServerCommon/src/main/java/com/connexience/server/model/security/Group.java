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

import com.connexience.server.model.ServerObject;

/**
 * This class represents a group of users
 *
 * @author hugo
 */
public class Group extends ServerObject
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
     * Indicates whether this group is a special group or not.
     * The default users and admin groups are protected and will not
     * show in lists on the website
     * */
    private boolean protectedGroup = false;

    /**
     * ID of the profile for this group
     * */
    private String profileId;

    /*
     * Should the group admin (creator) have to explicitly allow people to join the group?
     * */
    private boolean adminApproveJoin = true;

    /*
     * Can the members of this group be listed by non-members?
     * */
    private boolean nonMembersList = true;

    /**
     * Folder for the DocumentRecordLinks for this group
     */
    private String dataFolder;

    /**
     * Folder for any events stored in this group
     */
    private String eventsFolder;

    /**
     * Creates a new instance of Group
     */
    public Group()
    {
        super();
    }

    public boolean isProtectedGroup()
    {
        return protectedGroup;
    }

    public void setProtectedGroup(boolean protectedGroup)
    {
        this.protectedGroup = protectedGroup;
    }

    public String getProfileId()
    {
        return profileId;
    }

    public void setProfileId(String profileId)
    {
        this.profileId = profileId;
    }

    public boolean isAdminApproveJoin()
    {
        return adminApproveJoin;
    }

    public void setAdminApproveJoin(boolean adminApproveJoin)
    {
        this.adminApproveJoin = adminApproveJoin;
    }

    public boolean isNonMembersList()
    {
        return nonMembersList;
    }

    public void setNonMembersList(boolean nonMembersList)
    {
        this.nonMembersList = nonMembersList;
    }

    public String getDataFolder()
    {
        return dataFolder;
    }

    public void setDataFolder(String dataFolder)
    {
        this.dataFolder = dataFolder;
    }

    public String getEventsFolder()
    {
        return eventsFolder;
    }

    public void setEventsFolder(String eventsFolder)
    {
        this.eventsFolder = eventsFolder;
    }
}
