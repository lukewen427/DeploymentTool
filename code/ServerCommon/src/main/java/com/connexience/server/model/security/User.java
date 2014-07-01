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
import org.apache.commons.lang.StringEscapeUtils;

/**
 * This class represents a User within the system
 *
 * @author hugo
 */
public class User extends ServerObject
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
    private static final long serialVersionUID = 2L;


    /**
     * First name
     */
    private String firstName;

    /**
     * Surname
     */
    private String surname;

    /**
     * Default group for this user when they log on
     */
    private String defaultGroupId;

    /**
     * Default storage folder for this user
     */
    private String homeFolderId = "";

    /**
     * Default web folder for this User
     * */
    private String webFolderId = "";

    /**
     * Inbox for the user
     * */
    private String inboxFolderId;

    /**
     * Sent messages folder for the user
     * */
    private String sentMessagesFolderId;

    /**
     * External objects folder ID for the user. These are objects that external
     * applications have registered as objects that have their access control
     * managed by this system
     */
    private String externalObjectsFolderId;

    /*
     * The profile Id for this user.  Contains more information on the user
     * */
    private String profileId = "";

    /**
     * The folder containing this users workflow runs */
    private String workflowFolderId;

    /**
     * User key used by the API to make calls as this user
     */
    private String hashKey = null;

    /**
     * The default storage location for this user.  Could be null indicating
     * user's home folder or a project's data folder
     */
    private String defaultStorageFolderId;

    /** The default projectId for this user */
    private String defaultProjectId;

    /** Is this user a protected one - Root User or Public User? */
    private boolean protectedUser = false;

    // =========================================================================
    // Contact and various user details
    // =========================================================================

    /** Telephone number */

    /** Telephone ext */

    /** E-Mail addresss */


    /**
     * Creates a new instance of User
     */
    public User()
    {
        super();
    }

    /*
     * Get the profile Id for this user
     * */
    public String getProfileId()
    {
        return profileId;
    }
    /*
     * Set the profile Id for this user 
     * */
    public void setProfileId(String profileId)
    {
        this.profileId = profileId;
    }

    /**
     * Get the home folder id for this User
     */
    public String getHomeFolderId()
    {
        return homeFolderId;
    }

    /**
     * Set the home folder id for this User
     */
    public void setHomeFolderId(String homeFolderId)
    {
        this.homeFolderId = homeFolderId;
    }

    /*
     * Get the web folder for this User.
     * */
    public String getWebFolderId()
    {
        return webFolderId;
    }

    /*
     * Set the web folder for this user
     * */
    public void setWebFolderId(String webFolderId)
    {
        this.webFolderId = webFolderId;
    }

    /**
     * Get the first name of this user
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * Get the first name of this user
     */
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    /**
     * Get the surname of this user
     */
    public String getSurname()
    {
        return surname;
    }

    /**
     * Set the surname of this user
     */
    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    /**
     * Get the id of the default group for this user. This is the first group assigned during login
     */
    public String getDefaultGroupId()
    {
        return defaultGroupId;
    }

    /**
     * Get the id of the default group for this user. This is the first group assigned during login
     */
    public void setDefaultGroupId(String defaultGroupId)
    {
        this.defaultGroupId = defaultGroupId;
    }

    public String getWorkflowFolderId()
    {
        return workflowFolderId;
    }

    public void setWorkflowFolderId(String workflowFolderId)
    {
        this.workflowFolderId = workflowFolderId;
    }

    public String getInboxFolderId()
    {
        return inboxFolderId;
    }

    public void setInboxFolderId(String inboxFolderId)
    {
        this.inboxFolderId = inboxFolderId;
    }

    public String getSentMessagesFolderId()
    {
        return sentMessagesFolderId;
    }

    public void setSentMessagesFolderId(String sentMessagesFolderId)
    {
        this.sentMessagesFolderId = sentMessagesFolderId;
    }


    public String getDisplayName()
    {
        return StringEscapeUtils.escapeHtml(this.getFirstName() + " " + this.getSurname());
    }

    public String getExternalObjectsFolderId() {
        return externalObjectsFolderId;
    }

    public void setExternalObjectsFolderId(String externalObjectsFolderId) {
        this.externalObjectsFolderId = externalObjectsFolderId;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public String getDefaultStorageFolderId() {
        return defaultStorageFolderId;
    }

    public void setDefaultStorageFolderId(String defaultStorageFolderId) {
        this.defaultStorageFolderId = defaultStorageFolderId;
    }

    public String getDefaultProjectId() {
        return defaultProjectId;
    }

    public void setDefaultProjectId(String defaultProjectId) {
        this.defaultProjectId = defaultProjectId;
    }

    public boolean isProtectedUser() {
        return protectedUser;
    }

    public void setProtectedUser(boolean protectedUser) {
        this.protectedUser = protectedUser;
    }
}