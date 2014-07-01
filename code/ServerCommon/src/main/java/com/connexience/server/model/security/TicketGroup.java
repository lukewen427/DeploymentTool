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

/**
 * This class represents a group membership within a ticket.
 * @author hugo
 */
public class TicketGroup {
    /** Ticket group id */
    private long id;
    
    /** Ticket id */
    private String ticketId;
    
    /** Group id */
    private String groupId;
    
    /** Creates a new instance of TicketGroup */
    public TicketGroup() {
    }

    /** Get the database id */
    public long getId() {
        return id;
    }

    /** Set the database id */
    public void setId(long id) {
        this.id = id;
    }

    /** Get the id of the related ticket */
    public String getTicketId() {
        return ticketId;
    }

    /** Set the id of the related ticket */
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    /** Get the group id that this membership refers to */
    public String getGroupId() {
        return groupId;
    }

    /** Set the group id that this membership refers to */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
}
