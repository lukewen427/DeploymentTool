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
package com.connexience.server.ejb.properties;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.properties.PropertyGroup;
import com.connexience.server.model.properties.PropertyItem;
import com.connexience.server.model.security.Ticket;

import javax.ejb.Remote;
import java.util.List;

/**
 * This interface defines the functionality of the property access bean.
 * @author nhgh
 */
@Remote
public interface PropertiesRemote
{
    /** Get the properties groups for a specific object */
    public List getObjectPropertyGroups(Ticket ticket, String objectId) throws ConnexienceException;
    
    /** Get a specific property group */
    public PropertyGroup getPropertyGroup(Ticket ticket, long propertyGroupId) throws ConnexienceException;
    
    /** Get a named property group for a specific object */
    public PropertyGroup getPropertyGroup(Ticket ticket, String objectId, String propertyGroupName) throws ConnexienceException;
    
    /** Save a property group */
    public PropertyGroup savePropertyGroup(Ticket ticket, PropertyGroup propertyGroup) throws ConnexienceException;
    
    /** Remove a property group */
    public void removePropertyGroup(Ticket ticket, long propertyGroupId) throws ConnexienceException;
    
    /** Get a named system property in the form of PropertyGroupName, PropertyName */
    public PropertyItem getSystemProperty(Ticket ticket, String propertyGroupName, String propertyName) throws ConnexienceException;
    
    /** Set a named system property */
    public void setSystemProperty(Ticket ticket, String propertyGroupName, String propertyName, String propertyValue) throws ConnexienceException;
    
    /** Get a property */
    public PropertyItem getProperty(Ticket ticket, String objectId, String propertyGroupName, String propertyName) throws ConnexienceException;
    
    /** Set a property */
    public void setProperty(Ticket ticket, String objectId, String propertyGroupName, String propertyName, String propertyValue) throws ConnexienceException;
    
    /** Remove a property */
    public void removeProperty(Ticket ticket, long propertyId) throws ConnexienceException;

    /** Remove a property from a group by name */
    public void removeProperty(Ticket ticket, String objectId, String propertyGroupName, String propertyName) throws ConnexienceException;

    /** List the system property groups */
    public List getSystemPropertyGroups(Ticket ticket) throws ConnexienceException;
}