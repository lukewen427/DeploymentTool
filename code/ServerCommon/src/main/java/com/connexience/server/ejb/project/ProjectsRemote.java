package com.connexience.server.ejb.project;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.project.Project;
import com.connexience.server.model.security.Ticket;

import javax.ejb.Remote;

import java.util.List;

@Remote
public interface ProjectsRemote
{
	/**
	 * Retrieve a specific Project object (throws exception if the ID cannot be found).
	 */
	Project getProject(Ticket ticket, Integer projectId) throws ConnexienceException;

	/**
	 * Search for a Project using text, will be matched against name, description or external ID
	 */
	List<Project> searchProjects(Ticket ticket, String searchTerm) throws ConnexienceException;

	/**
	 * Save a Project object into the Database (can create or save a Project object).
	 */
	Project saveProject(Ticket ticket, Project project) throws ConnexienceException;

	/**
	 * Delete a specified Project from the system.
	 */
	void deleteProject(Ticket ticket, Integer projectId) throws ConnexienceException;

	void deleteProject(Ticket ticket, Integer projectId, Boolean deleteFiles) throws ConnexienceException;

	/**
	 * Get the number of public Studies
	 */
	Integer getPublicProjectCount(Ticket ticket) throws ConnexienceException;

	/**
	 * Retrieve all public Studies
	 */
	List<Project> getPublicProjects(Ticket ticket, Integer start, Integer maxResults) throws ConnexienceException;

	/**
	 * Get the number of studies for which the ticket is in the Member or Admin groups
	 */
	Integer getMemberProjectCount(Ticket ticket) throws ConnexienceException;

	/**
	 * Retrieve all Studies for which the ticket is in the Member or Admin groups
	 */
	List<Project> getMemberProjects(Ticket ticket, Integer start, Integer maxResults) throws ConnexienceException;

	/**
	 * Get the number of Studies which are public, or for which the ticket is in the Member or Admin groups
	 */
	Integer getVisibleProjectCount(Ticket ticket) throws ConnexienceException;

	/**
	 * Retrieve all Studies which are public or for which the ticket holder is in the Member or Admin groups
	 */
	List<Project> getVisibleProjects(Ticket ticket, Integer start, Integer maxResults) throws ConnexienceException;

	/**
	 * Lookup a Project by its externalID field
	 */
	List<Project> getProjectsByExternalId(Ticket ticket, String externalId) throws ConnexienceException;

	/**
	 * Convenience method for creating and saving a new Project object owned by the Ticket holder
	 */
	Project createProject(Ticket ticket, String projectName) throws ConnexienceException;

	/**
	 * Convenience method for creating and saving a new Project object with a specified owner
	 */
	Project createProject(Ticket ticket, String projectName, String escOwnerId) throws ConnexienceException;

	/**
	 * Convenience method to add an eSC User ID to the Admin group of a Project
	 */
	void addProjectAdmin(Ticket ticket, Integer projectId, String userId) throws ConnexienceException;

	/**
	 * Convenience method to remove an eSC User ID from the Admin group of a Project
	 */
	void removeProjectAdmin(Ticket ticket, Integer projectId, String userId) throws ConnexienceException;

	/**
	 * Convenience method to add an eSC User ID to the Member group of a Project
	 */
	void addProjectMember(Ticket ticket, Integer projectId, String userId) throws ConnexienceException;

	/**
	 * Convenience method to remove an eSC User ID from the Member group of a Project
	 */
	void removeProjectMember(Ticket ticket, Integer projectId, String userId) throws ConnexienceException;

	/**
	 * Convenience method to check if the ticket holder is in the Member or Admin group of a Project
	 */
	public boolean isProjectMember(Ticket ticket, Project project) throws ConnexienceException;

	/**
	 * Convenience method to check if the ticket holder is in the Admin group of a Project
	 */
	public boolean isProjectAdmin(Ticket ticket, Project project) throws ConnexienceException;
}
