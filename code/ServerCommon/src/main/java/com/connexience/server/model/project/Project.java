package com.connexience.server.model.project;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Entity
@Inheritance
@DiscriminatorColumn(name = "projecttype")
@DiscriminatorValue("PROJECT")
@Table(name = "projects")
@NamedQueries({
	// All projects whose visibility is public
	@NamedQuery(name = "Project.public", query = "SELECT p FROM Project p WHERE p.privateProject = false"),
	@NamedQuery(name = "Project.public.count", query = "SELECT COUNT(*) FROM Project p WHERE p.privateProject = false"),

	// All projects whose associated groups are in the specified list
	@NamedQuery(name = "Project.member", query = "SELECT p FROM Project p WHERE p.adminGroupId IN :groupMemberships OR p.membersGroupId IN :groupMemberships"),
	@NamedQuery(name = "Project.member.count", query = "SELECT COUNT(*) FROM Project p WHERE p.adminGroupId IN :groupMemberships OR p.membersGroupId IN :groupMemberships"),

	// All projects whose associated groups are in the specified list or are public
	@NamedQuery(name = "Project.visible", query = "SELECT p FROM Project p WHERE p.adminGroupId IN :groupMemberships OR p.membersGroupId IN :groupMemberships OR p.privateProject = false"),
	@NamedQuery(name = "Project.visible.count", query = "SELECT COUNT(*) FROM Project p WHERE p.adminGroupId IN :groupMemberships OR p.membersGroupId IN :groupMemberships OR p.privateProject = false")
})
public class Project implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esc_int_generator")
	@GenericGenerator(name = "esc_int_generator", strategy = "com.connexience.server.ejb.IntegerSequenceGenerator")
	private Integer id;

	private String externalId;

	private String name;

	private String description;

	private String ownerId;

	private String adminGroupId;

	private String membersGroupId;

	private String dataFolderId;

	private String workflowFolderId;

	private Long remoteScannerId;

	private boolean privateProject = false;

	@ManyToMany
	private Collection<Uploader> uploaders = new ArrayList<>();

	@ElementCollection(fetch = FetchType.EAGER)
	private Map<String, String> additionalProperties = new HashMap<>();

	protected Project()
	{
	}

	public Project(final String name, final String ownerId)
	{
		this.name = name;
		this.ownerId = ownerId;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(final Integer id)
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

	public String getOwnerId()
	{
		return ownerId;
	}

	public void setOwnerId(String ownerId)
	{
		this.ownerId = ownerId;
	}

	public String getAdminGroupId()
	{
		return adminGroupId;
	}

	public void setAdminGroupId(String adminGroupId)
	{
		this.adminGroupId = adminGroupId;
	}

	public String getMembersGroupId()
	{
		return membersGroupId;
	}

	public void setMembersGroupId(String membersGroupId)
	{
		this.membersGroupId = membersGroupId;
	}

	public String getDataFolderId()
	{
		return dataFolderId;
	}

	public void setDataFolderId(String dataFolderId)
	{
		this.dataFolderId = dataFolderId;
	}

	public String getWorkflowFolderId()
	{
		return workflowFolderId;
	}

	public void setWorkflowFolderId(final String escWorkflowFolderId)
	{
		this.workflowFolderId = escWorkflowFolderId;
	}

	public boolean isPrivateProject()
	{
		return privateProject;
	}

	public void setPrivateProject(boolean privateStudy)
	{
		this.privateProject = privateStudy;
	}

	public Long getRemoteScannerId()
	{
		return remoteScannerId;
	}

	public void setRemoteScannerId(final Long remoteScannerId)
	{
		this.remoteScannerId = remoteScannerId;
	}

	public Collection<Uploader> getUploaders()
	{
		return uploaders;
	}

	public void addUploader(final Uploader uploader)
	{
		if (!uploaders.contains(uploader))
		{
			uploaders.add(uploader);
		}

		if (!uploader.getProjects().contains(this))
		{
			uploader.getProjects().add(this);
		}
	}

	public void removeUploader(final Uploader uploader)
	{
		uploaders.remove(uploader);
		uploader.getProjects().remove(this);
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

	public String setAdditionalProperty(String key, String value)
	{
		return additionalProperties.put(key, value);
	}

	public String removeAdditionalProperty(String key)
	{
		return additionalProperties.remove(key);
	}
}
