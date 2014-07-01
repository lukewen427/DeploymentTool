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
package com.connexience.server.ejb.directory;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.metadata.SearchOrder;
import com.connexience.server.model.metadata.types.OrderBy;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.User;
import com.connexience.server.model.social.profile.UserProfile;
import org.hibernate.Session;

import javax.ejb.Remote;
import java.util.Collection;
import java.util.List;

/**
 * This is the business interface for UserDirectory enterprise bean.
 */
@Remote
public interface UserDirectoryRemote {
    /**
     * List all of the groups as GroupMembership objects that a User is a member of
     */
    List listGroupMembershipForUser(Ticket ticket, String userId) throws ConnexienceException;

    /**
     * Is one of the user groups a member of an organisation
     */
    boolean isUserOrganisationMember(Ticket ticket, String userId, String organisationId) throws ConnexienceException;

    /**
     * Is a user an organisation admin member
     */
    boolean isUserOrganisationAdmin(Ticket ticket, String userId, String organisationId) throws ConnexienceException;

    /**
     * Is a user a member of a specified group
     */
    boolean isUserGroupMember(Ticket ticket, String userId, String groupId) throws ConnexienceException;

    /**
     * Save a user
     */
    User saveUser(Ticket ticket, User user) throws ConnexienceException;

    /**
     * Get a specific user
     */
    com.connexience.server.model.security.User getUser(Ticket ticket, String userId) throws ConnexienceException;

    /**
     * Set password for user
     */
    void setPassword(Ticket ticket, String userId, String logonName, String password) throws ConnexienceException;

    /**
     * Validate a logon and return the user object
     */
    com.connexience.server.model.security.User authenticateUser(String logonName, String password) throws ConnexienceException;


    /**
     * Does a user already have a logon
     */
    boolean userHasLogon(String userId) throws ConnexienceException;

    /**
     * Delete a logon for a user
     */
    void deleteUserLogon(Ticket ticket, String userId) throws ConnexienceException;

    /**
     * Delete an account
     */
    void deleteAccount(Ticket ticket, String userId) throws ConnexienceException;
    
    /**
     * Change a users password
     */
    public void changePassword(Ticket ticket, String userId, String password) throws ConnexienceException;

    /**
     * List the groups that a user is a member of
     */
    List listUserGroups(Ticket ticket, String userId) throws ConnexienceException;

    /**
     * Find a user from a logon name
     */
    com.connexience.server.model.security.User getUserFromLogonName(String logonName) throws ConnexienceException;

    /**
     * Create a user account
     */
    public User createAccount(User user, String logonName, String password, String defaultDomain) throws ConnexienceException;

    /**
     * Get the user profile for a user
     */
    UserProfile getUserProfile(Ticket ticket, String userId) throws ConnexienceException;


    UserProfile saveUserProfile(Ticket ticket, String userId, UserProfile profile) throws ConnexienceException;

    /*
   * Get the logon name for the current a user
   * */
    public String getLogonName(Ticket ticket, String userId) throws ConnexienceException;


    /**
     * Method to get a list of users who have shared content with the current user.
     */
    public List getUsersWithSharedContent(Ticket ticket) throws ConnexienceException;


    /**
     * Get the user  from the profileId
     */
    User getUserFromProfileId(Ticket ticket, String profileId) throws ConnexienceException;

    User createAccount(User user, String logonName, String password) throws ConnexienceException;

    User createAccountForExternalLogon(User user, String externalUserId, String defaultDomain) throws ConnexienceException;

    User createAccountForExternalLogon(User user, String externalUserId, String defaultDomain, String provider) throws ConnexienceException;

    /**
     * Gets a user based on their email address.  Used in the linking of accounts to external logons
     *
     * @param email email address of the user
     * @return User if one exists with a email address set in their profile, name or logon details
     * @throws com.connexience.server.ConnexienceException
     *
     */
    User getUserFromEmailAddress(String email) throws ConnexienceException;

    void setLogonName(Ticket ticket, String userId, String logonName) throws ConnexienceException;

    /**
     * Validate a logon and return the user object
     */
    User authenticateUserFromId(String userId, String password) throws ConnexienceException;

    /**
     * Get the name of a user - no security as everyone can get the name of a user
     */
    String getUserName(Ticket ticket, String userId) throws ConnexienceException;

    Collection getFriends(Ticket ticket, User user, Session session) throws ConnexienceException;

    /*
   * Method to get the content of a particular type that has been shared by a particular user.
   * */
    List getSharedContent(Ticket ticket, String userId, Class type) throws ConnexienceException;

    long getNumberOfFriends(Ticket ticket, User user) throws ConnexienceException;

    /**
     * List all of the projects that a User is a member of
     */
    public List<String> listProjectsForUser(Ticket ticket) throws ConnexienceException;

    String addPasswordRecoveryCode(Ticket adminTicket, String userId) throws ConnexienceException;

    public boolean validatePasswordRecoveryCode(String code) throws ConnexienceException;

    public boolean changePasswordFromCode(String code, String newPassword) throws ConnexienceException;

    /**
     * Search for a user using first name and surname match
     */
    List searchForUsers(Ticket ticket, String searchString, int start, int maxResults) throws ConnexienceException;

    public List searchForUsers(Ticket ticket, String searchString, OrderBy orderBy, SearchOrder ascDesc, int start, int maxResults) throws ConnexienceException;

}
