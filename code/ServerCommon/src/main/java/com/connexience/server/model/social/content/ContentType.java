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

import java.io.Serializable;

/**
 * This class represents a type of content that can be stored in the system. It
 * contains a MIME type, icon and description. 
 * @author hugo
 */
public class ContentType implements Serializable {
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


    /** Database id of this content type */
    private String id;
    
    /** Label for this content type */
    private String label;
    
    /** Mimetype text describing this content */
    private String mimeType;
    
    /** Icon to display on the web pages for this content type */
    private String iconName;

    /** Organisation ID for this content type */
    private String organisationId;
    
    /** Get the database ID */
    public String getId() {
        return id;
    }

    /** Set the database ID */
    public void setId(String id) {
        this.id = id;
    }

    /** Get the ID of the organisation containing this content type */
    public String getOrganisationId(){
        return organisationId;
    }
    
    /** Set the ID of the organisation containing this content type */
    public void setOrganisationId(String organisationId){
        this.organisationId = organisationId;
    }
    
    /** Get the display label */
    public String getLabel() {
        return label;
    }

    /** Set the display label */
    public void setLabel(String label) {
        this.label = label;
    }

    /** Get the data type contained represented by this content type */
    public String getMimeType() {
        return mimeType;
    }

    /** Set the data type contained represented by this content type */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /** Get the icon displayed on the web pages for this content type */
    public String getIconName() {
        return iconName;
    }

    /** Set the icon displayed on the web pages for this content type */
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}