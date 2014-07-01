/**
 * e-Science Central
 * Copyright (C) 2008-2013 School of Computing Science, Newcastle University
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package com.connexience.server.model.social.profile;

import java.io.Serializable;

/**
 * This class represents the profile of a single user within the social networking
 * system.
 * <p/>
 * It is linked from the User object and provides mroe details
 *
 * @author Simon
 */
public class UserProfile implements Serializable
{
    /**
     * Class version UID.
     * 
     * Please increment this value whenever your changes may cause 
     * incompatibility with the previous version of this class. If unsure, ask 
     * one of the core development team or read:
     *   http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     *   http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 1L;


    private String id;

    private String website;

    private String emailAddress;

    private String text = "";

    private String defaultDomain = "";

    public UserProfile()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }


    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getDefaultDomain()
    {
        return defaultDomain;
    }

    public void setDefaultDomain(String defaultDomain)
    {
        this.defaultDomain = defaultDomain;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof UserProfile)) return false;

        UserProfile that = (UserProfile) o;

        if (emailAddress != null ? !emailAddress.equals(that.emailAddress) : that.emailAddress != null) return false;
        if (website != null ? !website.equals(that.website) : that.website != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = website != null ? website.hashCode() : 0;
        result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
        return result;
    }
}
