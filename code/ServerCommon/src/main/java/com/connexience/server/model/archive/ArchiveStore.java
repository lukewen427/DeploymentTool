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
package com.connexience.server.model.archive;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.ServerObject;

import java.io.Serializable;

/**
 * This is the base class for an object that can act as an archive store
 * @author swheater
 */
public abstract class ArchiveStore extends ServerObject implements Serializable {
    /** Creates a new instance of ArchiveStore */
    public ArchiveStore() {
    }

    /** Start Archiving of DocumentRecord */
    public abstract void startArchiving(String documentId, String dataStoreId) throws ConnexienceException;

    /** Start Archiving of DocumentRecord */
    public abstract void startUnarchiving(String documentId, String dataStoreId) throws ConnexienceException;
}
