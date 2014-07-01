package com.connexience.server.ejb.project;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.project.study.LoggerData;
import com.connexience.server.model.project.study.Study;
import com.connexience.server.model.security.Ticket;

import javax.ejb.Remote;
import java.util.Collection;
import java.util.List;

@Remote
public interface StudyRemote
{
	/**
	 * Retrieve a Study by ID, fails if private and not in member/admin group
	 */
	Study getStudy(Ticket ticket, Integer studyId) throws ConnexienceException;

	/**
	 * Save a Study object to the DB, will create or update the Study as needed
	 */
	Study saveStudy(Ticket ticket, Study study) throws ConnexienceException;

	/**
	 * Delete a Study and it's associasted elements from the system.
	 */
	void deleteStudy(Ticket ticket, Integer studyId) throws ConnexienceException;

	/**
	 * Delete a Study and optionally all of its associated files and folders
	 */
	void deleteStudy(Ticket ticket, Integer studyId, Boolean deleteFiles) throws ConnexienceException;

	/**
	 * Get the number of public Studies
	 */
	Long getPublicStudyCount(Ticket ticket) throws ConnexienceException;

	/**
	 * Retrieve all public Studies
	 */
	List<Study> getPublicStudies(Ticket ticket, Integer start, Integer maxResults) throws ConnexienceException;

	/**
	 * Get the number of studies for which the ticket is in the Member or Admin groups
	 */
	Long getMemberStudyCount(Ticket ticket) throws ConnexienceException;

	/**
	 * Retrieve all Studies for which the ticket is in the Member or Admin groups
	 */
	List<Study> getMemberStudies(Ticket ticket, Integer start, Integer maxResults) throws ConnexienceException;

	/**
	 * Get the number of Studies which are public, or for which the ticket is in the Member or Admin groups
	 */
	Long getVisibleStudyCount(Ticket ticket) throws ConnexienceException;

	/**
	 * Retrieve all Studies which are public or for which the ticket holder is in the Member or Admin groups
	 */
	List<Study> getVisibleStudies(Ticket ticket, Integer start, Integer maxResults) throws ConnexienceException;

	/**
	 * Lookup Studies by their external ID
	 */
	List<Study> getStudiesByExternalId(Ticket ticket, String externalId) throws ConnexienceException;

	/**
	 * Associate an eSC DocumentRecord ID with a specific Subject Group. It will do this by attaching data to the first
	 * suitable Logger Deployment it finds.
	 */
	LoggerData addDataToGroup(Ticket ticket, Integer subjectGroupId, String documentRecordId) throws ConnexienceException;

	/**
	 * Associate an eSC DocumentRecord with a specific Logger Deployment
	 */
	LoggerData addDataToDeployment(Ticket ticket, Integer loggerDeploymentId, String documentRecordId) throws ConnexienceException;

	/**
	 * Get all LoggerData associated with a Study. DO NOT USE IN eSC PROPER.
	 */
	Collection<LoggerData> getStudyData(Ticket ticket, Integer studyId, Integer start, Integer maxResults) throws ConnexienceException;
}
