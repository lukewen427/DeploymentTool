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
import com.connexience.server.model.social.TagCloudElement;

import javax.ejb.Remote;
import java.util.List;
import java.util.Vector;

/**
 * Author: Simon
 * Date: Jun 11, 2009
 */
@Remote
public interface SearchRemote
{
  List tagSearch(Ticket ticket, String searchText, int start, int maxResults) throws ConnexienceException;

  Integer countTagSearch(Ticket ticket, String searchText) throws ConnexienceException;

  /*
 * Method to get the top n recently created documents
 * */
  List getRecentlyAccessedDocuments(Ticket ticket, int start, int maxResults) throws ConnexienceException;

  List<TagCloudElement> getVisibleTags(Ticket ticket, int start, int maxResults) throws ConnexienceException;

  List freeTextSearch(Ticket ticket, String searchText, Vector<String> advancedOptions, int start, int maxResults) throws ConnexienceException;

  /* FREE TEXT SEARCHING METHODS      */
  /************************************/

  int countFreeTextSearch(Ticket ticket, String searchText, Vector<String> advancedOptions) throws ConnexienceException;
}
