package com.connexience.server.model.project.study;

import com.connexience.server.model.project.Project;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@DiscriminatorValue("STUDY")
@NamedQueries({
	// All studies whose visibility is public
	@NamedQuery(name = "Study.public", query = "SELECT s FROM Study s WHERE s.privateProject = false ORDER BY s.id ASC"),
	@NamedQuery(name = "Study.public.count", query = "SELECT COUNT(*) FROM Study s WHERE s.privateProject = false"),

	// All studies whose associated groups are in the specified list
	@NamedQuery(name = "Study.member", query = "SELECT s FROM Study s WHERE s.adminGroupId IN :groupMemberships OR s.membersGroupId IN :groupMemberships"),
	@NamedQuery(name = "Study.member.count", query = "SELECT COUNT(*) FROM Study s WHERE s.adminGroupId IN :groupMemberships OR s.membersGroupId IN :groupMemberships"),

	// All studies whose associated groups are in the specified list or are public
	@NamedQuery(name = "Study.visible", query = "SELECT s FROM Study s WHERE s.adminGroupId IN :groupMemberships OR s.membersGroupId IN :groupMemberships OR s.privateProject = false"),
	@NamedQuery(name = "Study.visible.count", query = "SELECT COUNT(*) FROM Study s WHERE s.adminGroupId IN :groupMemberships OR s.membersGroupId IN :groupMemberships OR s.privateProject = false")
})
public class Study extends Project implements Serializable
{
	private Integer phase;

	private Date startDate;

	private Date endDate;

	@OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE)
	private List<SubjectGroup> subjectGroups = new ArrayList<>();

	@OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE)
	private List<LoggerDeployment> loggerDeployments = new ArrayList<>();

	protected Study()
	{
	}

	public Study(final String name, final String ownerId)
	{
		super(name, ownerId);
	}

	public Date getStartDate()
	{
		return startDate;
	}

	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	public Date getEndDate()
	{
		return endDate;
	}

	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}

	public Integer getPhase()
	{
		return phase;
	}

	public void setPhase(final Integer phase)
	{
		this.phase = phase;
	}

	public List<SubjectGroup> getSubjectGroups()
	{
		return subjectGroups;
	}

	public void addSubjectGroup(final SubjectGroup subjectGroup)
	{
		if (!subjectGroups.contains(subjectGroup))
		{
			subjectGroups.add(subjectGroup);
		}

		subjectGroup.setStudy(this);
	}

	public void removeSubjectGroup(final SubjectGroup subjectGroup)
	{
		subjectGroups.remove(subjectGroup);
		subjectGroup.setStudy(null);
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

		loggerDeployment.setStudy(this);
	}

	public void removeLoggerDeployment(final LoggerDeployment loggerDeployment)
	{
		loggerDeployments.remove(loggerDeployment);
		loggerDeployment.setStudy(null);
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof Study))
		{
			return false;
		}

		final Study study = (Study) o;

		if (endDate != null ? !endDate.equals(study.endDate) : study.endDate != null)
		{
			return false;
		}
		if (phase != null ? !phase.equals(study.phase) : study.phase != null)
		{
			return false;
		}
		if (startDate != null ? !startDate.equals(study.startDate) : study.startDate != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = phase != null ? phase.hashCode() : 0;
		result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
		result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "Study{" +
			"phase=" + phase +
			", startDate=" + startDate +
			", endDate=" + endDate +
			"} " + super.toString();
	}
}
