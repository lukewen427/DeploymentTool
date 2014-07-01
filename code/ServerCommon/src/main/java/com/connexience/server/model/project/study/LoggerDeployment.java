package com.connexience.server.model.project.study;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: nsjw7 Date: 03/06/2013 Time: 09:35
 */
@Entity
@Table(name = "loggerdeployments")
public class LoggerDeployment implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esc_int_generator")
	@GenericGenerator(name = "esc_int_generator", strategy = "com.connexience.server.ejb.IntegerSequenceGenerator")
	private Integer id;

	private String dataFolderId;

	private boolean active = false;

	@ManyToOne
	private Study study;

	@ManyToOne
	private Logger logger;

	@ManyToOne
	private SubjectGroup subjectGroup;

	@ManyToOne
	private LoggerConfiguration loggerConfiguration;

	@OneToMany(mappedBy = "loggerDeployment", cascade = CascadeType.REMOVE)
	private List<LoggerData> loggerData = new ArrayList<>();

	@ElementCollection(fetch = FetchType.EAGER)
	private Map<String, String> additionalProperties = new HashMap<>();

	protected LoggerDeployment()
	{
	}

	public LoggerDeployment(final Logger logger, final LoggerConfiguration loggerConfiguration, final SubjectGroup subjectGroup)
	{
		this.study = subjectGroup.getStudy();
		this.logger = logger;
		this.loggerConfiguration = loggerConfiguration;
		this.subjectGroup = subjectGroup;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof LoggerDeployment))
		{
			return false;
		}

		final LoggerDeployment that = (LoggerDeployment) o;

		if (active != that.active)
		{
			return false;
		}
		if (dataFolderId != null ? !dataFolderId.equals(that.dataFolderId) : that.dataFolderId != null)
		{
			return false;
		}
		if (id != null ? !id.equals(that.id) : that.id != null)
		{
			return false;
		}
		if (!logger.equals(that.logger))
		{
			return false;
		}
		if (!loggerConfiguration.equals(that.loggerConfiguration))
		{
			return false;
		}
		if (!study.equals(that.study))
		{
			return false;
		}
		if (!subjectGroup.equals(that.subjectGroup))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (dataFolderId != null ? dataFolderId.hashCode() : 0);
		result = 31 * result + (active ? 1 : 0);
		result = 31 * result + study.hashCode();
		result = 31 * result + logger.hashCode();
		result = 31 * result + subjectGroup.hashCode();
		result = 31 * result + loggerConfiguration.hashCode();
		return result;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getDataFolderId()
	{
		return dataFolderId;
	}

	public void setDataFolderId(String dataFolderId)
	{
		this.dataFolderId = dataFolderId;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public Map<String, String> getAdditionalProperties()
	{
		return additionalProperties;
	}

	public void setAdditionalProperties(Map<String, String> additionalProperties)
	{
		this.additionalProperties = additionalProperties;
	}

	public String getAdditionalProperty(String key)
	{
		return additionalProperties.get(key);
	}

	public String putAdditionalProperty(String key, String value)
	{
		return additionalProperties.put(key, value);
	}

	public String removeAdditionalProperty(String key)
	{
		return additionalProperties.remove(key);
	}

	public Study getStudy()
	{
		return study;
	}

	public void setStudy(Study study)
	{
		// un-associate, if required
		if ((this.study != null) && this.study.getLoggerDeployments().contains(this))
		{
			this.study.getLoggerDeployments().remove(this);
		}

		this.study = study;

		// re-associate, if required
		if ((study != null) && !study.getLoggerDeployments().contains(this))
		{
			study.getLoggerDeployments().add(this);
		}
	}

	public LoggerConfiguration getLoggerConfiguration()
	{
		return loggerConfiguration;
	}

	public void setLoggerConfiguration(final LoggerConfiguration loggerConfiguration)
	{
		if (this.loggerConfiguration != null && this.loggerConfiguration.getLoggerDeployments().contains(this))
		{
			this.loggerConfiguration.getLoggerDeployments().remove(this);
		}

		this.loggerConfiguration = loggerConfiguration;

		if (loggerConfiguration != null && !loggerConfiguration.getLoggerDeployments().contains(this))
		{
			loggerConfiguration.getLoggerDeployments().add(this);
		}
	}

	public Logger getLogger()
	{
		return logger;
	}

	public void setLogger(final Logger logger)
	{
		if (this.logger != null && this.logger.getLoggerDeployments().contains(this))
		{
			this.logger.getLoggerDeployments().remove(this);
		}

		this.logger = logger;

		if (logger != null && !logger.getLoggerDeployments().contains(this))
		{
			logger.getLoggerDeployments().add(this);
		}
	}

	public SubjectGroup getSubjectGroup()
	{
		return subjectGroup;
	}

	public void setSubjectGroup(final SubjectGroup subjectGroup)
	{
		if (this.subjectGroup != null && this.subjectGroup.getLoggerDeployments().contains(this))
		{
			this.subjectGroup.getLoggerDeployments().remove(this);
		}

		this.subjectGroup = subjectGroup;

		if (subjectGroup != null && !subjectGroup.getLoggerDeployments().contains(this))
		{
			subjectGroup.getLoggerDeployments().add(this);
		}
	}

	public List<LoggerData> getLoggerData()
	{
		return loggerData;
	}

	public void addLoggerData(final LoggerData loggerDatum)
	{
		loggerDatum.setLoggerDeployment(this);
	}

	public void removeLoggerData(final LoggerData loggerDatum)
	{
		loggerDatum.setLoggerDeployment(null);
	}

	public void addAllLoggerData(final List<LoggerData> loggerData)
	{
		for (final LoggerData loggerDatum : loggerData)
		{
			addLoggerData(loggerDatum);
		}
	}

	public void removeAllLoggerData(final List<LoggerData> loggerData)
	{
		for (final LoggerData loggerDatum : loggerData)
		{
			removeLoggerData(loggerDatum);
		}
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
			.append("id", id)
			.append("dataFolderId", dataFolderId)
			.append("active", active)
			.append("study", study)
			.append("additionalProperties", additionalProperties)
			.toString();
	}
}
