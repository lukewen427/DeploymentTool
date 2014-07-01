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

@Entity
@Table(name = "loggerconfigurations")
public class LoggerConfiguration implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esc_int_generator")
	@GenericGenerator(name = "esc_int_generator", strategy = "com.connexience.server.ejb.IntegerSequenceGenerator")
	private Integer id;

	// readable name for the configuration
	private String name;

	// description of the configuration
	private String description;

	// frequency the logger samples at (Hz)
	private Integer sampleFrequency;

	// code defining the application of this configuration
	private String application;

	// minimum firmware version for this configuration
	private String minimumFirmwareVersion;

	// location of minimally required firmware
	private String firmwareLocation;

	@ManyToOne(fetch = FetchType.EAGER)
	private LoggerType loggerType;

	@OneToMany(mappedBy = "loggerConfiguration", cascade = CascadeType.REMOVE)
	private List<LoggerDeployment> loggerDeployments = new ArrayList<>();

	@ElementCollection(fetch = FetchType.EAGER)
	private Map<String, String> additionalProperties = new HashMap<>();

	protected LoggerConfiguration()
	{
	}

	public LoggerConfiguration(final String name)
	{
		this.name = name;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(final Integer id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	public Integer getSampleFrequency()
	{
		return sampleFrequency;
	}

	public void setSampleFrequency(final Integer sampleFrequency)
	{
		// TODO: valid values check? (u specific 1,2,4,5,10,20)

		this.sampleFrequency = sampleFrequency;
	}

	public String getApplication()
	{
		return application;
	}

	public void setApplication(final String application)
	{
		this.application = application;
	}

	public String getMinimumFirmwareVersion()
	{
		return minimumFirmwareVersion;
	}

	public void setMinimumFirmwareVersion(final String minimumFirmwareVersion)
	{
		this.minimumFirmwareVersion = minimumFirmwareVersion;
	}

	public String getFirmwareLocation()
	{
		return firmwareLocation;
	}

	public void setFirmwareLocation(final String firmwareLocation)
	{
		this.firmwareLocation = firmwareLocation;
	}

	public LoggerType getLoggerType()
	{
		return loggerType;
	}

	public void setLoggerType(final LoggerType loggerType)
	{
		if (this.loggerType != null && this.loggerType.getLoggerConfigurations().contains(this))
		{
			this.loggerType.getLoggerConfigurations().remove(this);
		}

		this.loggerType = loggerType;

		if (loggerType != null && !loggerType.getLoggerConfigurations().contains(this))
		{
			loggerType.getLoggerConfigurations().add(this);
		}
	}

	public List<LoggerDeployment> getLoggerDeployments()
	{
		return loggerDeployments;
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

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof LoggerConfiguration))
		{
			return false;
		}

		final LoggerConfiguration that = (LoggerConfiguration) o;

		if (application != null ? !application.equals(that.application) : that.application != null)
		{
			return false;
		}
		if (description != null ? !description.equals(that.description) : that.description != null)
		{
			return false;
		}
		if (firmwareLocation != null ? !firmwareLocation.equals(that.firmwareLocation) : that.firmwareLocation != null)
		{
			return false;
		}
		if (id != null ? !id.equals(that.id) : that.id != null)
		{
			return false;
		}
		if (minimumFirmwareVersion != null ? !minimumFirmwareVersion.equals(that.minimumFirmwareVersion) : that.minimumFirmwareVersion != null)
		{
			return false;
		}
		if (!name.equals(that.name))
		{
			return false;
		}
		if (sampleFrequency != null ? !sampleFrequency.equals(that.sampleFrequency) : that.sampleFrequency != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + name.hashCode();
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (sampleFrequency != null ? sampleFrequency.hashCode() : 0);
		result = 31 * result + (application != null ? application.hashCode() : 0);
		result = 31 * result + (minimumFirmwareVersion != null ? minimumFirmwareVersion.hashCode() : 0);
		result = 31 * result + (firmwareLocation != null ? firmwareLocation.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
			.append("id", id)
			.append("name", name)
			.append("description", description)
			.append("sampleFrequency", sampleFrequency)
			.append("application", application)
			.append("minimumFirmwareVersion", minimumFirmwareVersion)
			.append("firmwareLocation", firmwareLocation)
			.append("loggerType", loggerType)
			.toString();
	}
}
