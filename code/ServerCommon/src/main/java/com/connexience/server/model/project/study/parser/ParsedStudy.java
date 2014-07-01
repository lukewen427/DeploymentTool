package com.connexience.server.model.project.study.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsedStudy implements Serializable
{
	public Integer escID = null;

	public String name = null;

	public Map<String, ParsedGroup> groups = new HashMap<>();

	public Map<String, String> properties = new HashMap<>();

	public List<String> problems = new ArrayList<>();

	public ParsedGroup checkGroup(final String externalID, final String name)
	{
		if (!groups.containsKey(externalID))
		{
			final ParsedGroup newGroup = new ParsedGroup();

			newGroup.name = name;
			newGroup.externalID = externalID;

			groups.put(externalID, newGroup);
		}

		return groups.get(externalID);
	}
}
