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
import java.util.Arrays;

/**
 * This class provides a ticket that is created when using the web pages.
 *
 * @author nhgh
 */
public class WebTicket extends Ticket implements Serializable {
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
    private static final long serialVersionUID = -5702427820076897159L;

    /**
     * List of Group IDs for the user
     */
    private String[] groupIds;


    /**
     * Creates a new instance of WebTicket
     */
    public WebTicket() {
        super();
        setStorable(true);
    }

    /**
     * Get the group ids
     */
    public String[] getGroupIds() {
        return groupIds;
    }

    /**
     * Set the group ids
     */
    public void setGroupIds(String[] groupIds) {
        this.groupIds = groupIds;
    }

    @Override
    public String toString() {
        return "WebTicket{" +
                "groupIds=" + Arrays.toString(groupIds) +
                '}' + super.toString();
    }
}
