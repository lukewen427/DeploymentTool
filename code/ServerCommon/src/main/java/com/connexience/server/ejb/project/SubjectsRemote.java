package com.connexience.server.ejb.project;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.project.study.Subject;
import com.connexience.server.model.project.study.SubjectGroup;

import javax.ejb.Remote;
import java.util.Collection;

@Remote
public interface SubjectsRemote
{
	/**
	 * Core method for retrieving a single SubjectGroup
	 */
	SubjectGroup getSubjectGroup(Ticket ticket, Integer subjectGroupId) throws ConnexienceException;

	/**
	 * Core method for saving/updating a single SubjectGroup
	 */
	SubjectGroup saveSubjectGroup(Ticket ticket, SubjectGroup subjectGroup) throws ConnexienceException;

	/**
	 * Core method for removing a SubjectGroup (and all of its children and Subjects)
	 */
	void deleteSubjectGroup(Ticket ticket, Integer subjectGroupId) throws ConnexienceException;

	/**
	 * Core method for retrieving a single Subject
	 */
	Subject getSubject(Ticket ticket, Integer subjectId) throws ConnexienceException;

	/**
	 * Core method for saving/updating a single Subject
	 */
	Subject saveSubject(Ticket ticket, Subject subject);

	/**
	 * Core method for removing a Subject
	 */
	void deleteSubject(Ticket ticket, Integer subjectId) throws ConnexienceException;

	/**
	 * Convenience method for retrieving all SubjectGroup objects associated with a Study
	 */
	Collection<SubjectGroup> getSubjectGroups(Ticket ticket, Integer studyId) throws ConnexienceException;

	/**
	 * Creates a new SubjectGroup in the specified Study
	 */
	SubjectGroup createSubjectGroup(Ticket ticket, Integer studyId, String groupName, String readableId) throws ConnexienceException;

	/**
	 * Created a new SubjectGroup under the specified parent SubjectGroup
	 */
	SubjectGroup createChildGroup(Ticket ticket, Integer parentSubjectGroupId, String groupName, String readableId) throws ConnexienceException;

	/**
	 * Adds an specified Subject to the specified SubjectGroup
	 */
	SubjectGroup addGroupSubject(Ticket ticket, Integer parentSubjectGroupId, Integer subjectId) throws ConnexienceException;

	/**
	 * Removes the specified Subject from the specified SubjectGroup
	 */
	SubjectGroup removeGroupSubject(Ticket ticket, Integer parentSubjectGroupId, Integer subjectId) throws ConnexienceException;

	/**
	 * Convenience method to find all Subjects associated with a specific Study (instead of traversing SubjectGroups)
	 */
	Collection<Subject> getSubjects(Ticket ticket, Integer studyId) throws ConnexienceException;

	/**
	 * Creates a new Subject in the specified SubjectGroup
	 */
	Subject createSubject(Ticket ticket, Integer parentSubjectGroupId, String subjectReadableId) throws ConnexienceException;

	void deleteSubjectGroup(final Ticket ticket, final Integer subjectGroupId, final Boolean deleteFiles) throws ConnexienceException;
}
