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
import com.connexience.server.model.security.StoredCredentials;
import com.connexience.server.model.security.Ticket;
import java.util.List;
import javax.ejb.Remote;

/**
 * This interface defines the functionality of the credentials storage framework.
 * @author hugo
 */
@Remote
public interface CredentialsDirectoryRemote {
    public List listCredentials(Ticket ticket) throws ConnexienceException;
    public StoredCredentials saveCredentials(Ticket ticket, StoredCredentials credentials) throws ConnexienceException;
    public StoredCredentials getCredentials(Ticket ticket, String id) throws ConnexienceException;
    public void removeCredentials(Ticket ticket, String id) throws ConnexienceException;
    public List listCredentialsByClass(Ticket ticket, Class credentialsClass) throws ConnexienceException;
}