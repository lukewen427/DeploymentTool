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
import com.connexience.server.model.security.Ticket;

import javax.ejb.Local;

/**
 * This is the business interface for ObjectInfo enterprise bean.
 */
@Local
public interface ObjectInfoLocal {
    /**
     * Get the name of an object
     */
    java.lang.String getObjectName(Ticket ticket, String objectId) throws ConnexienceException;

    /**
     * Get the names of a collection of objects
     */
    java.lang.String[] getObjectNames(Ticket ticket, String[] objectIds) throws ConnexienceException;

    /**
     * Does an organisation contain a specific object
     */
    boolean containsObject(Ticket ticket, String objectId) throws ConnexienceException;
    
}
