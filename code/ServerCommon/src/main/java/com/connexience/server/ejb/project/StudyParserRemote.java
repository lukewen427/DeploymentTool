package com.connexience.server.ejb.project;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.project.study.parser.ParsedStudy;
import com.connexience.server.model.security.Ticket;

import javax.ejb.Remote;

import java.util.List;

/**
 * Remote EJB interface for parsing flat files (typically XLSX) and producing Study, Subject Group, Subject, Logger and
 * Logger Deployment objects.
 */
@Remote
public interface StudyParserRemote
{
	/**
	 * Parse a spreadsheet into a ParsedStudy object, to be persisted by <code>persistParsedStudy()</code>.
	 */
	ParsedStudy parseStudyStructure(Ticket ticket, String escFileId, Integer studyId, String loggerColumnName) throws ConnexienceException;

	/**
	 * Persist a given ParsedStudy object
	 */
	List<String> persistParsedStudy(final Ticket ticket, final ParsedStudy parsedStudy) throws ConnexienceException;

	/**
	 * Parse a spreadsheet to create one or more Logger instances of a specified LoggerType
	 */
	List<String> parseLoggerDefinitions(Ticket ticket, String escFileId, Integer loggerTypeId, String loggerIdColumnName) throws ConnexienceException;

	/**
	 * Parse a spreadsheet to setup additional properties for logger deployments within a study
	 */
	List<String> parseLoggerProperties(Ticket ticket, String escFileId, Integer studyId) throws ConnexienceException;

	/**
	 * Parse a spreadsheet to setup additional properties for subjects within a study
	 */
	List<String> parseSubjectProperties(Ticket ticket, String escFileId, Integer studyId) throws ConnexienceException;
}
