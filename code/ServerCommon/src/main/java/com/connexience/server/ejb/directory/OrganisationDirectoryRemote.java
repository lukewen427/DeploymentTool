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
import com.connexience.server.model.metadata.SearchOrder;
import com.connexience.server.model.metadata.types.OrderBy;
import com.connexience.server.model.organisation.Organisation;
import com.connexience.server.model.security.Ticket;

import javax.ejb.Remote;
import java.util.List;

/**
 * This is the business interface for OrganisationDirectorybean enterprise bean.
 */
@Remote
public interface OrganisationDirectoryRemote {
    /**
     * List all of the organisations
     */
    List listOrganisations(Ticket ticket) throws ConnexienceException;

    /**
     * Save an organisation to the database
     */
    Organisation saveOrganisation(Ticket ticket, Organisation org) throws ConnexienceException;

    /**
     * Get an organisation by id
     */
    com.connexience.server.model.organisation.Organisation getOrganisation(Ticket ticket, String organisationId) throws ConnexienceException;

    /**
     * List all of the groups in an organisation
     */
    public List listOrganisationGroups(Ticket ticket, int start, int maxResults) throws ConnexienceException;

    /**
     * Get an organisation by name
     */
    com.connexience.server.model.organisation.Organisation getOrganisationByName(Ticket ticket, String organisationId) throws ConnexienceException;

    /**
     * Search for organisations
     */
    List searchOrganisations(Ticket ticket, String searchText) throws ConnexienceException;

    /*
    * List all of the users within an organisation, limited by the start parameter and maximum number of results to return
    **/
    public List listOrganisationUsers(Ticket ticket, int start, int maxResults) throws ConnexienceException;

    /*
    * Get the number of users within an organisation
    **/
    public int numberOfOrganisationUsers(Ticket ticket) throws ConnexienceException;

    /*
    * Get the number of groups within an organisation
    **/
    public int numberOfOrganisationGroups(Ticket ticket) throws ConnexienceException;

    /**
     * List all of the groups in an organisation that are not protected
     */
    List listOrganisationNonProtectedGroups(Ticket ticket, int start, int maxResults) throws ConnexienceException;

    /**
     * get the number of groups in an organisation
     */
    int numberOfOrganisationNonProtectedGroups(Ticket ticket) throws ConnexienceException;

    /**
     * List all of the groups in an organisation
     */
    List listOrganisationGroups(Ticket ticket, String organisationId) throws ConnexienceException;

    /**
     * Get the default organisation
     */
    public Organisation getDefaultOrganisation(Ticket ticket) throws ConnexienceException;

    /**
     * Set up a new organisation
     */
    Organisation setupNewOrganisation(String name, String storageDir,
                                      String awsAccessKey, String awsSecretKey, String awsDomainName, String awsGlacierVaultName,
                                      String adminGroup, String userGroup,
                                      String adminFirstname, String adminLastname, String adminUsername, String adminPassword,
                                      String gmailUser, String gmailPassword) throws ConnexienceException;

    /**
     * List all of the users in an organisation
     */
    List listNonProtectedOrganisationUsers(Ticket ticket, int start, int maxResults) throws ConnexienceException;

    public List listNonProtectedOrganisationUsers(Ticket ticket, OrderBy orderBy, SearchOrder ascDesc, int start, int maxResults) throws ConnexienceException;

    public void resetAccessKeyToLocalMacAddress() throws ConnexienceException;
}
