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
import java.util.Hashtable;

/**
 * THis class represents a single item of content in the database.
 * @author hugo
 */
public class ContentItem implements Serializable {
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


    /** Label for this item */
    private String label;
    
    /** Source ID for this item */
    private String sourceId;
    
    /** List of attributes for this item. These are stored as a Hashtable
     * object and contain all of the data related to this item */
    private Hashtable attributes;

    /** Get the display label for this content item */
    public String getLabel() {
        return label;
    }

    /** Set the display label for this content item */
    public void setLabel(String label) {
        this.label = label;
    }

    /** Set the ID of the content source that provided this item */
    public String getSourceId() {
        return sourceId;
    }

    /** Get the ID of the content source that provided this item */
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    /** Get the attributes that represent the data contained in this content item */
    public Hashtable getAttributes() {
        return attributes;
    }

    /** Set the attributes that represent the data contained in this content item */
    public void setAttributes(Hashtable attributes) {
        this.attributes = attributes;
    }
    
    /** Set an attribute */
    public void setAttribute(String name, Object value){
        attributes.put(name, value);
    }
}
