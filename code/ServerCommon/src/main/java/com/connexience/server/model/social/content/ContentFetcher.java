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
package com.connexience.server.model.social.content;

import com.connexience.server.ConnexienceException;

import javax.ejb.Remote;
import java.util.List;

/**
 * This interface defines an EJB that can fetch a certain type of content from
 * a data source. The actual configuration is done at the EJB level, this interface
 * just defines high level access to content.
 * @author hugo
 */
@Remote
public interface ContentFetcher {
    /** Is a specific content type supported */
    public boolean typeSupported(ContentType type);
    
    /** Perform a query */
    public List executeQuery(ContentQuery query, int maxResults, int offset) throws ConnexienceException;
}