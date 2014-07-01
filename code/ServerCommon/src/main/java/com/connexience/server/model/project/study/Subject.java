package com.connexience.server.model.project.study;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: nsjw7 Date: 11/07/2013 Time: 11:48
 */
@Entity
@Table(name = "subjects")
public class Subject implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esc_int_generator")
	@GenericGenerator(name = "esc_int_generator", strategy = "com.connexience.server.ejb.IntegerSequenceGenerator")
	private Integer id;

	private String externalId;

	@ManyToOne(fetch = FetchType.EAGER)
	private SubjectGroup subjectGroup;

	@ElementCollection(fetch = FetchType.EAGER)
	private Map<String, String> additionalProperties = new HashMap<>();

	protected Subject()
	{
	}

	public Subject(final String externalId)
	{
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

	public String getExternalId()
	{
		return externalId;
	}

	public void setExternalId(String readableId)
	{
		this.externalId = readableId;
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

	public SubjectGroup getSubjectGroup()
	{
		return subjectGroup;
	}

	public void setSubjectGroup(final SubjectGroup subjectGroup)
	{
		// un-associate, if required
		if (this.subjectGroup != null && this.subjectGroup.getSubjects().contains(this))
		{
			this.subjectGroup.getSubjects().remove(this);
		}

		this.subjectGroup = subjectGroup;

		// re-associate, if required
		if (subjectGroup != null && !subjectGroup.getSubjects().contains(this))
		{
			subjectGroup.getSubjects().add(this);
		}
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof Subject))
		{
			return false;
		}

		final Subject subject = (Subject) o;

		if (id != null ? !id.equals(subject.id) : subject.id != null)
		{
			return false;
		}
		if (!externalId.equals(subject.externalId))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + externalId.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
			.append("id", id)
			.append("readableId", externalId)
			.append("subjectGroup", subjectGroup)
			.append("additionalProperties", additionalProperties)
			.toString();
	}
}
