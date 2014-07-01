package com.connexience.server.model.project;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: nsjw7 Date: 03/06/2013 Time: 09:35
 */
@Entity
@Table(name = "conversionworkflows")
public class ConversionWorkflow implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esc_int_generator")
	@GenericGenerator(name = "esc_int_generator", strategy = "com.connexience.server.ejb.IntegerSequenceGenerator")
	private Integer id;

	private String workflowDocumentId;

	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "conversionWorkflows")
	private Collection<FileType> fileTypes = new ArrayList<>();

	protected ConversionWorkflow()
	{
	}

	public ConversionWorkflow(final String workflowDocumentId)
	{
		this.workflowDocumentId = workflowDocumentId;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getWorkflowDocumentId()
	{
		return workflowDocumentId;
	}

	public void setWorkflowDocumentId(String escId)
	{
		this.workflowDocumentId = escId;
	}

	public Collection<FileType> getFileTypes()
	{
		return fileTypes;
	}

	public void addFileType(final FileType fileType)
	{
		if (!fileTypes.contains(fileType))
		{
			fileTypes.add(fileType);
		}

		if (!fileType.getConversionWorkflows().contains(this))
		{
			fileType.getConversionWorkflows().add(this);
		}
	}

	public void removeFileType(final FileType fileType)
	{
		fileTypes.remove(fileType);
		fileType.getConversionWorkflows().remove(this);
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof ConversionWorkflow))
		{
			return false;
		}

		final ConversionWorkflow that = (ConversionWorkflow) o;

		if (!workflowDocumentId.equals(that.workflowDocumentId))
		{
			return false;
		}
		if (id != null ? !id.equals(that.id) : that.id != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + workflowDocumentId.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
			.append("id", id)
			.append("workflowDocumentId", workflowDocumentId)
				//			.append("fileTypes", fileTypes)
			.toString();
	}
}
