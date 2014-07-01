package com.connexience.server.model.project.study;

import com.connexience.server.model.project.FileType;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * User: nsjw7 Date: 03/06/2013 Time: 09:35
 */
@Entity
@Table(name = "sensors")
public class Sensor implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esc_int_generator")
	@GenericGenerator(name = "esc_int_generator", strategy = "com.connexience.server.ejb.IntegerSequenceGenerator")
	private Integer id;

	private String name;

	@ManyToOne(fetch = FetchType.EAGER)
	private FileType fileType;

	@ManyToOne(fetch = FetchType.EAGER)
	private LoggerType loggerType;

	protected Sensor()
	{
	}

	public Sensor(final String name)
	{
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

	public void setName(final String name)
	{
		this.name = name;
	}

	public FileType getFileType()
	{
		return fileType;
	}

	public void setFileType(final FileType fileType)
	{
		// unassociate, if required
		if ((this.fileType != null) && this.fileType.getSensors().contains(this))
		{
			this.fileType.getSensors().remove(this);
		}

		this.fileType = fileType;

		// associate with new fileType
		if ((fileType != null) && !fileType.getSensors().contains(this))
		{
			fileType.getSensors().add(this);
		}
	}

	public LoggerType getLoggerType()
	{
		return loggerType;
	}

	public void setLoggerType(final LoggerType loggerType)
	{
		// unassociate, if required
		if (this.loggerType != null && this.loggerType.getSensors().contains(this))
		{
			this.loggerType.getSensors().remove(this);
		}

		this.loggerType = loggerType;

		// add this sensor to the new loggerType
		if ((loggerType != null) && !loggerType.getSensors().contains(this))
		{
			loggerType.getSensors().add(this);
		}
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof Sensor))
		{
			return false;
		}

		final Sensor sensor = (Sensor) o;

		if (id != null ? !id.equals(sensor.id) : sensor.id != null)
		{
			return false;
		}
		if (!name.equals(sensor.name))
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
		return result;
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
			.append("id", id)
			.append("name", name)
			.append("fileType", fileType)
			.append("loggerType", loggerType)
			.toString();
	}
}
