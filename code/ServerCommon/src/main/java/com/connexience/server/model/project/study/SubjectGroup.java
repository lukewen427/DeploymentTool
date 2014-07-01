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
 * User: nsjw7 Date: 11/07/2013 Time: 11:48
 */
@Entity
@Table(name = "subjectgroups")
public class SubjectGroup implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esc_int_generator")
	@GenericGenerator(name = "esc_int_generator", strategy = "com.connexience.server.ejb.IntegerSequenceGenerator")
	private Integer id;

	private String displayName;

	private String externalId;

	private String dataFolderId;

	@ManyToOne(fetch = FetchType.EAGER)
	private Study study;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	private SubjectGroup parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	private List<SubjectGroup> children = new ArrayList<>();

	@OneToMany(mappedBy = "subjectGroup", cascade = CascadeType.REMOVE)
	private List<LoggerDeployment> loggerDeployments = new ArrayList<>();

	@OneToMany(mappedBy = "subjectGroup", cascade = CascadeType.REMOVE)
	private List<Subject> subjects = new ArrayList<>();

	@ElementCollection(fetch = FetchType.EAGER)
	private Map<String, String> additionalProperties = new HashMap<>();

	protected SubjectGroup()
	{
	}

	public SubjectGroup(final String displayName, final String externalId)
	{
		this.displayName = displayName;
		this.externalId = externalId;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getExternalId()
	{
		return externalId;
	}

	public void setExternalId(String readableId)
	{
		this.externalId = readableId;
	}

	public String getDataFolderId()
	{
		return dataFolderId;
	}

	public void setDataFolderId(String dataFolderId)
	{
		this.dataFolderId = dataFolderId;
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
		if (this.study != null && this.study.getSubjectGroups().contains(this))
		{
			this.study.getSubjectGroups().remove(this);
		}

		this.study = study;

		if (!study.getSubjectGroups().contains(this))
		{
			study.getSubjectGroups().add(this);
		}
	}

	public SubjectGroup getParent()
	{
		return parent;
	}

	public void setParent(SubjectGroup parent)
	{
		// un-associate, if required
		if (this.parent != null && this.parent.getChildren().contains(this))
		{
			this.parent.getChildren().remove(this);
		}

		this.parent = parent;

		// re-associate, if required
		if (parent != null && !parent.getChildren().contains(this))
		{
			parent.getChildren().add(this);
		}
	}

	public List<SubjectGroup> getChildren()
	{
		return children;
	}

	public void addChild(final SubjectGroup child)
	{
		if (!children.contains(this))
		{
			children.add(child);
		}

		child.setParent(this);
	}

	public void removeChild(final SubjectGroup child)
	{
		children.remove(child);
		child.setParent(null);
	}

	public List<Subject> getSubjects()
	{
		return subjects;
	}

	public void addSubject(final Subject subject)
	{
		if (!subjects.contains(subject))
		{
			subjects.add(subject);
		}

		subject.setSubjectGroup(this);
	}

	public void removeSubject(final Subject subject)
	{
		subjects.remove(subject);
		subject.setSubjectGroup(null);
	}

	public List<LoggerDeployment> getLoggerDeployments()
	{
		return loggerDeployments;
	}

	public void addLoggerDeployment(final LoggerDeployment loggerDeployment)
	{
		if (!loggerDeployments.contains(loggerDeployment))
		{
			loggerDeployments.add(loggerDeployment);
		}

		loggerDeployment.setSubjectGroup(this);
	}

	public void removeLoggerDeployment(final LoggerDeployment loggerDeployment)
	{
		loggerDeployments.remove(loggerDeployment);
		loggerDeployment.setSubjectGroup(null);
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof SubjectGroup))
		{
			return false;
		}

		final SubjectGroup that = (SubjectGroup) o;

		if (dataFolderId != null ? !dataFolderId.equals(that.dataFolderId) : that.dataFolderId != null)
		{
			return false;
		}
		if (!displayName.equals(that.displayName))
		{
			return false;
		}
		if (id != null ? !id.equals(that.id) : that.id != null)
		{
			return false;
		}
		if (parent != null ? !parent.equals(that.parent) : that.parent != null)
		{
			return false;
		}
		if (externalId != null ? !externalId.equals(that.externalId) : that.externalId != null)
		{
			return false;
		}
		if (study != null ? !study.equals(that.study) : that.study != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + displayName.hashCode();
		result = 31 * result + (externalId != null ? externalId.hashCode() : 0);
		result = 31 * result + (dataFolderId != null ? dataFolderId.hashCode() : 0);
		result = 31 * result + (study != null ? study.hashCode() : 0);
		result = 31 * result + (parent != null ? parent.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
			.append("id", id)
			.append("displayName", displayName)
			.append("readableId", externalId)
			.append("dataFolderId", dataFolderId)
			.append("study", study)
			.append("parent", parent)
			.append("additionalProperties", additionalProperties)
			.toString();
	}
}
