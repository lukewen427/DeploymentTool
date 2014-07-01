package com.connexience.server.model.project;

import com.connexience.server.model.project.study.Sensor;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: nsjw7 Date: 03/06/2013 Time: 09:35
 */
@Entity
@Table(name = "filetypes")
public class FileType implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esc_int_generator")
	@GenericGenerator(name = "esc_int_generator", strategy = "com.connexience.server.ejb.IntegerSequenceGenerator")
	private Integer id;

	private String name;

	private String description;

	@ManyToMany(fetch = FetchType.EAGER)
	private List<ConversionWorkflow> conversionWorkflows = new ArrayList<>();

	@OneToMany(mappedBy = "fileType")
	private List<Sensor> sensors = new ArrayList<>();

	protected FileType()
	{
	}

	public FileType(final String name)
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

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Collection<ConversionWorkflow> getConversionWorkflows()
	{
		return conversionWorkflows;
	}

	public void setConversionWorkflows(final List<ConversionWorkflow> workflows)
	{
		for (final ConversionWorkflow conversionWorkflow : conversionWorkflows)
		{
			conversionWorkflow.getFileTypes().remove(this);
		}

		this.conversionWorkflows = workflows;

		for (final ConversionWorkflow conversionWorkflow : conversionWorkflows)
		{
			conversionWorkflow.getFileTypes().add(this);
		}
	}

	public void addConversionWorkflow(final ConversionWorkflow conversionWorkflow)
	{
		if (!conversionWorkflows.contains(conversionWorkflow))
		{
			conversionWorkflows.add(conversionWorkflow);
		}

		if (!conversionWorkflow.getFileTypes().contains(this))
		{
			conversionWorkflow.getFileTypes().add(this);
		}
	}

	public void removeConversionWorkflow(final ConversionWorkflow conversionWorkflow)
	{
		conversionWorkflows.remove(conversionWorkflow);
		conversionWorkflow.getFileTypes().remove(this);
	}

	public Collection<Sensor> getSensors()
	{
		return sensors;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof FileType))
		{
			return false;
		}

		final FileType fileType = (FileType) o;

		if (description != null ? !description.equals(fileType.description) : fileType.description != null)
		{
			return false;
		}
		if (id != null ? !id.equals(fileType.id) : fileType.id != null)
		{
			return false;
		}
		if (!name.equals(fileType.name))
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
		return result;
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
			.append("id", id)
			.append("name", name)
			.append("description", description)
				//			.append("conversionWorkflows", conversionWorkflows)
				//			.append("sensors", sensors)
			.toString();
	}
}
