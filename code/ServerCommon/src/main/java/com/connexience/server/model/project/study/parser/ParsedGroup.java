package com.connexience.server.model.project.study.parser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class description here.
 *
 * @author ndjm8
 */
public class ParsedGroup implements Serializable
{
	public Integer escID = null;

	public String externalID = null;

	public String name = null;

	public ParsedGroup parent = null;

	public Map<String, ParsedGroup> children = new HashMap<>();

	public Map<String, ParsedSubject> subjects = new HashMap<>();

	public Map<String, ParsedLoggerDeployment> deployments = new HashMap<>();

	public ParsedGroup checkGroup(final String externalID, final String name)
	{
		if (!children.containsKey(externalID))
		{
			final ParsedGroup group = new ParsedGroup();

			group.parent = this;
			group.name = name;
			group.externalID = externalID;

			children.put(externalID, group);
		}

		return children.get(externalID);
	}

	public ParsedSubject checkSubject(final String externalID)
	{
		if (!subjects.containsKey(externalID))
		{
			final ParsedSubject subject = new ParsedSubject();

			subject.externalID = externalID;

			subjects.put(externalID, subject);
		}

		return subjects.get(externalID);
	}

	public ParsedLoggerDeployment checkDeployment(final String loggerID, final String configName)
	{
		if (!deployments.containsKey(loggerID))
		{
			final ParsedLoggerDeployment logger = new ParsedLoggerDeployment();
			logger.loggerID = loggerID;
			logger.config = configName;

			deployments.put(loggerID, logger);
		}

		return deployments.get(loggerID);
	}
}
