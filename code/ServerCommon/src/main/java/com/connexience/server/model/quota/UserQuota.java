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
package com.connexience.server.model.quota;

import java.io.Serializable;

/**
 * This class defines the various quota data assigned to a user
 * @author hugo
 */
public class UserQuota implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** Database ID */
    private long id;
    
    /** User ID for this quota object */
    private String userId;
    
    /** Is the storage quota enforced */
    private boolean storageQuotaEnforced = false;
    
    /** Total storage quota in bytes */
    private long storageQuota = 1000000000;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStorageQuota() {
        return storageQuota;
    }

    public void setStorageQuota(long storageQuota) {
        this.storageQuota = storageQuota;
    }

    public boolean isStorageQuotaEnforced() {
        return storageQuotaEnforced;
    }

    public void setStorageQuotaEnforced(boolean storageQuotaEnforced) {
        this.storageQuotaEnforced = storageQuotaEnforced;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}