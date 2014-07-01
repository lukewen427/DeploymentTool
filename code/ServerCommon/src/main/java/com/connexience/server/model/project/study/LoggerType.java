package com.connexience.server.model.project.study;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: nsjw7 Date: 11/07/2013 Time: 14:09
 */
@Entity
@Table(name = "loggertypes", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"name", "manufacturer"})
})
public class LoggerType implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esc_int_generator")
	@GenericGenerator(name = "esc_int_generator", strategy = "com.connexience.server.ejb.IntegerSequenceGenerator")
	private Integer id;

	private String name;

	private String manufacturer;

	private boolean physicalDevice = true;

	@OneToMany(mappedBy = "loggerType", cascade = CascadeType.REMOVE)
	private List<Sensor> sensors = new ArrayList<>();

	@OneToMany(mappedBy = "loggerType", cascade = CascadeType.REMOVE)
	private List<Logger> loggers = new ArrayList<>();

	@OneToMany(mappedBy = "loggerType", cascade = CascadeType.REMOVE)
	private List<LoggerConfiguration> loggerConfigurations = new ArrayList<>();

	protected LoggerType()
	{
	}

	public LoggerType(final String manufacturer, final String name)
	{
		this.manufacturer = manufacturer;
		this.name = name;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getManufacturer()
	{
		return manufacturer;
	}

	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	public boolean isPhysicalDevice()
	{
		return physicalDevice;
	}

	public void setPhysicalDevice(boolean physicalDevice)
	{
		this.physicalDevice = physicalDevice;
	}

	public List<Sensor> getSensors()
	{
		return sensors;
	}

	public void addSensor(final Sensor sensor)
	{
		sensor.setLoggerType(this);
	}

	public void removeSensor(final Sensor sensor)
	{
		sensor.setLoggerType(null);
	}

	public List<Logger> getLoggers()
	{
		return loggers;
	}

	public List<LoggerConfiguration> getLoggerConfigurations()
	{
		return loggerConfigurations;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof LoggerType))
		{
			return false;
		}

		final LoggerType that = (LoggerType) o;

		if (physicalDevice != that.physicalDevice)
		{
			return false;
		}
		if (id != null ? !id.equals(that.id) : that.id != null)
		{
			return false;
		}
		if (!manufacturer.equals(that.manufacturer))
		{
			return false;
		}
		if (!name.equals(that.name))
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
		result = 31 * result + manufacturer.hashCode();
		result = 31 * result + (physicalDevice ? 1 : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
			.append("id", id)
			.append("name", name)
			.append("manufacturer", manufacturer)
			.append("physicalDevice", physicalDevice)
			.toString();
	}
}
