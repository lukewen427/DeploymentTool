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
package com.connexience.server.ejb.social;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.social.Event;
import com.connexience.server.model.social.event.GroupEvent;

import javax.ejb.Remote;
import java.util.Date;
import java.util.List;

@Remote
/**
 * This interface defines the behaviour of the event management bean
 * @author hugo
 */
public interface EventRemote {
    /** Add an event for a group */
    public GroupEvent saveGroupEvent(Ticket ticket, GroupEvent event) throws ConnexienceException;

    /** List all of the events for a group */
    public List listGroupEvents(Ticket ticket, String groupId) throws ConnexienceException;

    /** List all of the events for a group in a certain timeframe */
    public List listGroupEvents(Ticket ticket, String groupId, Date startDate, Date endDate) throws ConnexienceException;
    
    /** Remove an event for a group */
    public void deleteEvent(Ticket ticket, Event event) throws ConnexienceException;

    /** Get a group event by ID */
    public GroupEvent getGroupEvent(Ticket ticket, String id) throws ConnexienceException;
}