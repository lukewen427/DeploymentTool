package com.connexience.server.model.project;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "uploaders", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"username"})
})
@NamedQueries({
	@NamedQuery(name = "Uploader.public.count", query = "SELECT COUNT(*) FROM Uploader u"),
	@NamedQuery(name = "Uploader.public", query = "SELECT u FROM Uploader u")
})
public class Uploader implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esc_int_generator")
	@GenericGenerator(name = "esc_int_generator", strategy = "com.connexience.server.ejb.IntegerSequenceGenerator")
	private Integer id;

	private String username;

	private String hashedPassword;

	@ManyToMany(mappedBy = "uploaders", fetch = FetchType.EAGER)
	private Collection<Project> projects = new ArrayList<>();

	protected Uploader()
	{
	}

	public Uploader(final String username, final String hashedPassword)
	{
		this.username = username;
		this.hashedPassword = hashedPassword;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getHashedPassword()
	{
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword)
	{
		this.hashedPassword = hashedPassword;
	}

	public Collection<Project> getProjects()
	{
		return projects;
	}

	public void addStudy(final Project project)
	{
		if (!projects.contains(project))
		{
			projects.add(project);
		}

		if (!project.getUploaders().contains(this))
		{
			project.getUploaders().add(this);
		}
	}

	public void removeStudy(final Project project)
	{
		projects.remove(project);
		project.getUploaders().remove(this);
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof Uploader))
		{
			return false;
		}

		final Uploader that = (Uploader) o;

		if (!hashedPassword.equals(that.hashedPassword))
		{
			return false;
		}
		if (id != null ? !id.equals(that.id) : that.id != null)
		{
			return false;
		}
		if (!username.equals(that.username))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + username.hashCode();
		result = 31 * result + hashedPassword.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		return "Uploader{" +
			"id=" + id +
			", username='" + username + '\'' +
			", hashedPassword='" + hashedPassword + '\'' +
			", projects=" + projects +
			"} " + super.toString();
	}
}
