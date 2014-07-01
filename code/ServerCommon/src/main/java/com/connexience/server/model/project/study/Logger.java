package com.connexience.server.model.project.study;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import java.util.List;

/**
 * User: nsjw7 Date: 03/06/2013 Time: 09:35
 */
@Entity
@Table(name = "loggers")
public class Logger implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esc_int_generator")
	@GenericGenerator(name = "esc_int_generator", strategy = "com.connexience.server.ejb.IntegerSequenceGenerator")
	private Integer id;

	@Column(unique = true)
	private String serialNumber;

	private String location;

	@ManyToOne(fetch = FetchType.EAGER)
	private LoggerType loggerType;

	// remove any deployment if this logger is deleted
	@OneToMany(mappedBy = "logger", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<LoggerDeployment> loggerDeployments = new ArrayList<>();

	protected Logger()
	{
	}

	public Logger(final String serialNumber)
	{
		this.serialNumber = serialNumber;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getSerialNumber()
	{
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber)
	{
		this.serialNumber = serialNumber;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public LoggerType getLoggerType()
	{
		return loggerType;
	}

	public void setLoggerType(final LoggerType loggerType)
	{
		// unassociate, if required
		if (this.loggerType != null && this.loggerType.getLoggers().contains(this))
		{
			this.loggerType.getLoggers().remove(this);
		}

		this.loggerType = loggerType;

		// add this sensor to the new loggerType
		if ((loggerType != null) && !loggerType.getLoggers().contains(this))
		{
			loggerType.getLoggers().add(this);
		}
	}

	public List<LoggerDeployment> getLoggerDeployments()
	{
		return loggerDeployments;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof Logger))
		{
			return false;
		}

		final Logger logger = (Logger) o;

		if (id != null ? !id.equals(logger.id) : logger.id != null)
		{
			return false;
		}
		if (location != null ? !location.equals(logger.location) : logger.location != null)
		{
			return false;
		}
		if (!serialNumber.equals(logger.serialNumber))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + serialNumber.hashCode();
		result = 31 * result + (location != null ? location.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
			.append("id", id)
			.append("serialNumber", serialNumber)
			.append("location", location)
			.append("loggerType", loggerType)
				//			.append("loggerDeployment", loggerDeployment)
			.toString();
	}
}
