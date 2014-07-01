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
 * This is the root object securing the system. It holds a username and password
 * for a user that can administer everything and also keys etc to sign tickets
 * for this user when they log on
 * @author hugo
 */
public class RootSecurityObject extends ServerObject {
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


    /** ID of the root user */
    private String rootUserId;
    
    /** Creates a new instance of RootSecurityObject */
    public RootSecurityObject() {
        super();
    }

    /** Get the root user id */
    public String getRootUserId() {
        return rootUserId;
    }

    /** Set the root username */
    public void setRootUserId(String rootUserId) {
        this.rootUserId = rootUserId;
    }
}