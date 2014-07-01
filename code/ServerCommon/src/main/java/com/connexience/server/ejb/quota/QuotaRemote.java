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
package com.connexience.server.ejb.quota;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.quota.UserQuota;
import com.connexience.server.model.security.Ticket;

import javax.ejb.Remote;

/**
 * This interface defines the behaviour of the quota management system
 * @author hugo
 */
@Remote
public interface QuotaRemote {
    /** Get the storage quota for a user. This method creates an unrestricted
     * quota object if one doesn't already exist.*/
    public UserQuota getOrCreateUserQuota(Ticket ticket, String userId) throws ConnexienceException;
    
    /** Save a storage quota object */
    public UserQuota saveUserQuota(Ticket ticket, UserQuota quota) throws ConnexienceException;
    
    /** Does a user have a storage quota */
    public boolean userHasStorageQuota(Ticket ticket, String userId) throws ConnexienceException;
    
    /** Get the amount of storage quota that a user has used */
    public long getStorageQuotaUsed(Ticket ticket, String userId) throws ConnexienceException;
    
    /** Get the storage used by a project */
    public long getStorageQuotaUsedInProject(Ticket ticket, String userId, String projectId) throws ConnexienceException;
        
    /** Get the amount of storage quota left for a user */
    public long getAvailableStorageQuota(Ticket ticket, String userId) throws ConnexienceException;
}