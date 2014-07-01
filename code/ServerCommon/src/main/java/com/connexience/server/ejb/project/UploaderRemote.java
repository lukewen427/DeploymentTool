package com.connexience.server.ejb.project;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.project.Uploader;

import javax.ejb.Remote;

import java.util.Collection;

@Remote
public interface UploaderRemote
{
	/**
	 * Core method for retrieving a specific Uploader object
	 */
	Uploader getUploader(Ticket ticket, Integer uploaderId) throws ConnexienceException;

	/**
	 * Core method for saving/updating an Uploader object
	 */
	Uploader saveUploader(Ticket ticket, Uploader uploader) throws ConnexienceException;

	/**
	 * Core method for removing an Uploader object
	 */
	void deleteUploader(Ticket ticket, Integer uploaderId) throws ConnexienceException;

	/**
	 * Core method for retrieving the total number of Uploader objects
	 */
	Integer getUploaderCount(Ticket ticket);

	/**
	 * Core method for retrieving all available Uploader objects
	 */
	Collection<Uploader> getUploaders(Ticket ticket, Integer start, Integer maxResults);

	/**
	 * Convenience method for creating and saving a new Uploader object with a specified username and password.
	 */
	Uploader createUploader(Ticket ticket, String username, String password) throws ConnexienceException;

	/**
	 * A Convenience method to both register a new Uploader and add it to a Study object
	 */
	Uploader createAddUploader(Ticket ticket, Integer projectId, String username, String password) throws ConnexienceException;

	/**
	 * Convenience method for adding an Uploader object to a Study object
	 */
	Uploader addUploader(Ticket ticket, Integer projectId, Integer uploaderId) throws ConnexienceException;

	/**
	 * Convenience method for removing an Uploader object from a Study object
	 */
	Uploader removeUploader(Ticket ticket, Integer projectId, Integer uploaderId) throws ConnexienceException;

	/**
	 * Core method to authenticate a given username and password as return the associated Uploader object if successful
	 */
	Uploader authenticateUploader(String username, String password) throws ConnexienceException;

	/**
	 * Change an uploader's password
	 */
	Uploader setPassword(Ticket ticket, Integer uploaderId, String password) throws ConnexienceException;
}
