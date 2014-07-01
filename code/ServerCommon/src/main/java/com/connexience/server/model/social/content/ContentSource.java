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
package com.connexience.server.model.social.content;

import com.connexience.server.ConnexienceException;

import javax.naming.InitialContext;
import java.io.Serializable;

/**
 * This class represents a source of specific content items.
 * @author hugo
 */
public class ContentSource implements Serializable {
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


    /** Database ID of this source */
    private String id;
    
    /** Organisation containing this source */
    private String organisationId;
    
    /** MIME type that this content source can provide */
    private String mimeType;
    
    /** Display label for this content source */
    private String label;
    
    /** Icon name that gets mapped to an actual icon in the web pages */
    private String iconName;
    
    /** Description text for the web GUI */
    private String description;
    
    /** JNDI Name of the EJB that provides the back end source */
    private String sourceJndiName;

    /** Get the database id of this source */
    public String getId() {
        return id;
    }

    /** Set the database id of this source */
    public void setId(String id) {
        this.id = id;
    }

    /** Get the ID of the organisation containing this data source */
    public String getOrganisationId(){
        return organisationId;
    }
    
    /** Set the ID of the organisation containing this data source */
    public void setOrganisationId(String organisationId){
        this.organisationId = organisationId;
    }
    
    /** Get the data type label that this source provides */
    public String getMimeType() {
        return mimeType;
    }

    /** Set the data type label that this source provides */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /** Get the title label that gets displayed on the web pages */
    public String getLabel() {
        return label;
    }

    /** Set the title label that gets displayed on the web pages */
    public void setLabel(String label) {
        this.label = label;
    }

    /** Get the base name of the icon for this source. This gets mapped to an
     * actual icon in the web / gui. */
    public String getIconName() {
        return iconName;
    }

    /** Set the base name of the icon for this source. This gets mapped to an
     * actual icon in the web / gui. */
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    /** Get the descriptive text for this source */
    public String getDescription() {
        return description;
    }

    /** Set the descriptive text for this source */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Get the name of the back end EJB bean that will be used to retrieve data from this source */
    public String getSourceJndiName() {
        return sourceJndiName;
    }

    /** Set the name of the back end EJB bean that will be used to retrieve data from this source */
    public void setSourceJndiName(String sourceJndiName) {
        this.sourceJndiName = sourceJndiName;
    }
    
    /** Get hold of the fetcher for this content source */
    public ContentFetcher getFetcher() throws ConnexienceException {
        try {
            InitialContext ic = new InitialContext();
            return (ContentFetcher)ic.lookup(sourceJndiName);
        } catch (Exception e){
            throw new ConnexienceException("Cannot get content fetcher: " + e.getMessage());
        }
    }
}
