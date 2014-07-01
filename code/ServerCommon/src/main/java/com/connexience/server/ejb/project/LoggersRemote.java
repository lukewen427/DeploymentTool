package com.connexience.server.ejb.project;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.project.ConversionWorkflow;
import com.connexience.server.model.project.FileType;
import com.connexience.server.model.project.study.Logger;
import com.connexience.server.model.project.study.LoggerConfiguration;
import com.connexience.server.model.project.study.LoggerData;
import com.connexience.server.model.project.study.LoggerDeployment;
import com.connexience.server.model.project.study.LoggerType;
import com.connexience.server.model.project.study.Sensor;
import com.connexience.server.model.security.Ticket;

import javax.ejb.Remote;
import java.util.Collection;
import java.util.List;

@Remote
public interface LoggersRemote
{
	/**
	 * Core method for retrieving the number of LoggerType objects
	 */
	Integer getLoggerTypeCount(Ticket ticket);

	/**
	 * List all logger types
	 */
	Collection<LoggerType> getLoggerTypes(Ticket ticket, Integer start, Integer maxResults) throws ConnexienceException;

	/**
	 * Core method for retrieving an instance
	 */
	LoggerType getLoggerType(Ticket ticket, Integer id) throws ConnexienceException;

	/**
	 * Core method for saving/updating an instance
	 */
	LoggerType saveLoggerType(Ticket ticket, LoggerType loggerType);

	/**
	 * Core method for removing an instance
	 */
	void deleteLoggerType(Ticket ticket, Integer id) throws ConnexienceException;

	/**
	 * Core method for retrieving an instance
	 */
	Sensor getSensor(Ticket ticket, Integer id) throws ConnexienceException;

	/**
	 * Core method for saving/updating an instance
	 */
	Sensor saveSensor(Ticket ticket, Sensor sensor);

	/**
	 * Core method for removing an instance
	 */
	void deleteSensor(Ticket ticket, Integer id) throws ConnexienceException;

	/**
	 * Get the number of FileType objects
	 */
	Integer getFileTypeCount(Ticket ticket);

	/**
	 * List all FileType objects
	 */
	Collection<FileType> getFileTypes(Ticket ticket, Integer start, Integer maxResults) throws ConnexienceException;

	/**
	 * Core method for retrieving an instance
	 */
	FileType getFileType(Ticket ticket, Integer id) throws ConnexienceException;

	/**
	 * Core method for saving/updating an instance
	 */
	FileType saveFileType(Ticket ticket, FileType fileType);

	/**
	 * Core method for removing an instance
	 */
	void deleteFileType(Ticket ticket, Integer id) throws ConnexienceException;

	/**
	 * Core method for retrieving an instance
	 */
	ConversionWorkflow getConversionWorkflow(Ticket ticket, Integer id) throws ConnexienceException;

	/**
	 * Core method for saving/updating an instance
	 */
	ConversionWorkflow saveConversionWorkflow(Ticket ticket, ConversionWorkflow conversionWorkflow);

	/**
	 * Core method for removing an instance
	 */
	void deleteConversionWorkflow(Ticket ticket, Integer id) throws ConnexienceException;

	/**
	 * Get the number of Logger objects
	 */
	Integer getLoggerCount(Ticket ticket);

	/**
	 * List all Logger objects
	 */
	Collection<Logger> getLoggers(Ticket ticket, Integer start, Integer maxResults) throws ConnexienceException;

	/**
	 * List all Logger objects with a specific LoggerType
	 */
	Collection<Logger> getLoggersByType(Ticket ticket, Integer loggerTypeId) throws ConnexienceException;

	/**
	 * List all Logger objects with a specific Serial
	 */
	Collection<Logger> getLoggersBySerial(Ticket ticket, String serial) throws ConnexienceException;

	/**
	 * Core method for retrieving an instance
	 */
	Logger getLogger(Ticket ticket, Integer id) throws ConnexienceException;

	/**
	 * Core method for saving/updating an instance
	 */
	Logger saveLogger(Ticket ticket, Logger logger) throws ConnexienceException;

	/**
	 * Core method for removing an instance
	 */
	void deleteLogger(Ticket ticket, Integer id) throws ConnexienceException;

	/**
	 * Get the number of LoggerConfiguration objects
	 */
	Integer getLoggerConfigurationCount(Ticket ticket);

	/**
	 * List all LoggerConfiguration objects
	 */
	Collection<LoggerConfiguration> getLoggerConfigurations(Ticket ticket, Integer start, Integer maxResults) throws ConnexienceException;

	/**
	 * List all LoggerConfiguration objects of a specific Type
	 */
	Collection<LoggerConfiguration> getLoggerConfigurationsByType(Ticket ticket, Integer loggerTypeId) throws ConnexienceException;

	/**
	 * Core method for retrieving an instance
	 */
	LoggerConfiguration getLoggerConfiguration(Ticket ticket, Integer id) throws ConnexienceException;

	/**
	 * Core method for saving/updating an instance
	 */
	LoggerConfiguration saveLoggerConfiguration(Ticket ticket, LoggerConfiguration loggerConfiguration);

	/**
	 * Core method for removing an instance
	 */
	void deleteLoggerConfiguration(Ticket ticket, Integer id) throws ConnexienceException;

	/**
	 * Get the number of LoggerDeployment objects
	 */
	Integer getLoggerDeploymentCount(Ticket ticket);

	/**
	 * List all LoggerDeployment objects
	 */
	Collection<LoggerDeployment> getLoggerDeployments(Ticket ticket, Integer start, Integer maxResults) throws ConnexienceException;

	/**
	 * List all LoggerDeployment objects of a specific Study
	 */
	Collection<LoggerDeployment> getLoggerDeploymentsByStudy(Ticket ticket, Integer studyId) throws ConnexienceException;

	/**
	 * Core method for retrieving an instance
	 */
	LoggerDeployment getLoggerDeployment(Ticket ticket, Integer id) throws ConnexienceException;

	/**
	 * Get a logger deployment given an study code and logger ID
	 */
	LoggerDeployment getLoggerDeployment(Ticket ticket, String studyCode, String loggerSerialNumber) throws ConnexienceException;

	/**
	 * Core method for saving/updating an instance
	 */
	LoggerDeployment saveLoggerDeployment(Ticket ticket, LoggerDeployment loggerDeployment) throws ConnexienceException;

	/**
	 * Core method for removing an instance
	 */
	void deleteLoggerDeployment(Ticket ticket, Integer id) throws ConnexienceException;

	LoggerData saveLoggerData(Ticket ticket, LoggerData loggerData) throws ConnexienceException;

	/**
	 * Convenience method to create a new FileType object
	 */
	FileType createFileType(Ticket ticket, String name) throws ConnexienceException;

	/**
	 * Convenience method to create a new ConversionWorkflow object and associate it with a FileType
	 */
	ConversionWorkflow createConversionWorkflow(Ticket ticket, Integer fileTypeId, String escWorkflowId) throws ConnexienceException;

	/**
	 * Convenience method to create a new LoggerType with a specified manufacturer and model name
	 */
	LoggerType createLoggerType(Ticket ticket, String manufacturer, String modelName) throws ConnexienceException;

	/**
	 * Convenience method to create a new Sensor with a specific name and assign it to an existing LoggerType
	 */
	Sensor createSensor(Ticket ticket, Integer loggerTypeId, String sensorName) throws ConnexienceException;

	/**
	 * Convenience method to assign a FileType to a Sensor object
	 */
	Sensor addFileTypeToSensor(Ticket ticket, Integer sensorId, Integer fileTypeId) throws ConnexienceException;

	/**
	 * Convenience method for creating new LoggerConfiguration for a specified LoggerType
	 */
	LoggerConfiguration createLoggerConfiguration(Ticket ticket, Integer loggerTypeId, String configurationName) throws ConnexienceException;

	/**
	 * Convenience method for creating a new Logger from an existing LoggerType
	 */
	Logger createLogger(Ticket ticket, Integer loggerTypeId, String serialNumber) throws ConnexienceException;

	/**
	 * Convenience method for creating a new LoggerDeployment for a given Logger and LoggerConfiguration
	 */
	LoggerDeployment createLoggerDeployment(Ticket ticket, Integer studyId, Integer loggerId, Integer loggerConfigurationId, Integer subjectGroupId) throws ConnexienceException;

	/**
	 * List all LoggerDeployment objects of a specific Study, filtered by active or not
	 */
	Collection<LoggerDeployment> getLoggerDeploymentsByStudy(Ticket ticket, Integer studyId, boolean isActive) throws ConnexienceException;

	LoggerData getLoggerData(Ticket ticket, Integer loggerDataId) throws ConnexienceException;

	/**
	 * Get all LoggerData objects associated with a specified Study ID
	 */
	List<LoggerData> getLoggerDataByStudy(Ticket ticket, Integer studyId) throws ConnexienceException;

	/**
	 * Return a list of all available (i.e. not currently deployed) loggers for a specified Logger Type ID
	 */
	List<Logger> getAvailableLoggersByType(Ticket ticket, Integer loggerTypeId) throws ConnexienceException;

	/**
	 * Undeploy all Logger Deployments from a given Study (i.e., set active = false) and return all affected deployments.
	 */
	List<LoggerDeployment> undeployLoggersByStudy(Ticket ticket, Integer studyId) throws ConnexienceException;
}
