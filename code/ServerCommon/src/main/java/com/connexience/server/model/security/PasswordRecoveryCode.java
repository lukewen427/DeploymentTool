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

import java.util.Date;

/**
 * This holds a code which can be used to reset a user password.  The code will expire after a certain amount of time
 * User: nsjw7
 * Date: 05/11/2012
 * Time: 09:34
 */
public class PasswordRecoveryCode {

    private long id;

    //Default expiry time
    public static final long DEFAULT_EXPIRY = 1000 * 60 * 60 * 12;

    private String userId;

    private String code;

    private Date expiry;

    public PasswordRecoveryCode() {
    }

    public PasswordRecoveryCode(String userId, String code) {
        this.userId = userId;
        this.code = code;
        long expiry = new Date().getTime() + DEFAULT_EXPIRY;
        this.expiry = new Date(expiry);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    @Override
    public String toString() {
        return "PasswordRecoveryCode{" +
                "userId='" + userId + '\'' +
                ", code='" + code + '\'' +
                ", expiry=" + expiry +
                '}';
    }
}
