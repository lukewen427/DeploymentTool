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
package com.connexience.server.ejb.certificate;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.security.Ticket;

import javax.ejb.Remote;

/**
 * This is the business interface for CertificateAccess enterprise bean.
 */
@Remote
public interface CertificateAccessRemote {
    /**
     * Get the signing certificate relating to a server object if one exists
     */
    java.security.cert.X509Certificate getCertificate(Ticket ticket, String objectId) throws ConnexienceException;

    /** Get a certificate for a User */
    java.security.cert.X509Certificate getUserCertificate(String userId) throws ConnexienceException;

    /**
     * Verify whether a certificate matches the database certificate for a server object
     */
    boolean certificateBelongsToObject(Ticket ticket, String objectId, java.security.cert.X509Certificate userCert) throws ConnexienceException;

    /**
     * Verify whether the MD5 hash of a certificate matches the database version
     */
    boolean validateCertificateMD5Hash(Ticket ticket, String objectId, byte[] md5Hash) throws ConnexienceException;

    /**
     * Get the KeyStore data for an object
     */
    byte[] getKeyStoreData(Ticket ticket, String objectId) throws ConnexienceException;

	byte[] getOrganisationKey() throws ConnexienceException;
}
