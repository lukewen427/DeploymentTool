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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * User: nsjw7 Date: 03/06/2013 Time: 09:40
 */
@Entity
@Table(name = "loggerdata")
@NamedQueries({
	@NamedQuery(name = "LoggerData.byEscId", query = "SELECT d FROM LoggerData d WHERE d.documentRecordId = :escId"),
	@NamedQuery(name = "LoggerData.byStudy", query = "SELECT d FROM LoggerData d WHERE d.loggerDeployment.study.id = :studyId ")
})
public class LoggerData implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esc_int_generator")
	@GenericGenerator(name = "esc_int_generator", strategy = "com.connexience.server.ejb.IntegerSequenceGenerator")
	private Integer id;

	private String documentRecordId;

	@ManyToOne(fetch = FetchType.EAGER)
	private FileType fileType;

	@ManyToOne(fetch = FetchType.EAGER)
	private LoggerDeployment loggerDeployment;

	protected LoggerData()
	{
	}

	public LoggerData(final String documentRecordId)
	{
		this.documentRecordId = documentRecordId;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getDocumentRecordId()
	{
		return documentRecordId;
	}

	public void setDocumentRecordId(String escId)
	{
		this.documentRecordId = escId;
	}

	public FileType getFileType()
	{
		return fileType;
	}

	public void setFileType(final FileType fileType)
	{
		this.fileType = fileType;
	}

	public LoggerDeployment getLoggerDeployment()
	{
		return loggerDeployment;
	}

	public void setLoggerDeployment(LoggerDeployment loggerDeployment)
	{
		// un-associate, if required
		if (this.loggerDeployment != null && this.loggerDeployment.getLoggerData().contains(this))
		{
			this.loggerDeployment.getLoggerData().remove(this);
		}

		this.loggerDeployment = loggerDeployment;

		// re-associate, if required
		if (loggerDeployment != null && !loggerDeployment.getLoggerData().contains(this))
		{
			loggerDeployment.getLoggerData().add(this);
		}
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof LoggerData))
		{
			return false;
		}

		final LoggerData that = (LoggerData) o;

		if (!documentRecordId.equals(that.documentRecordId))
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
		result = 31 * result + documentRecordId.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
			.append("id", id)
			.append("documentRecordId", documentRecordId)
			.append("fileType", fileType)
			.append("loggerDeployment", loggerDeployment)
			.toString();
	}
}
