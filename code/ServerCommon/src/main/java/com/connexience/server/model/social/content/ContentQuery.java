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
import java.util.Iterator;

/**
 * This class defines a query for content type. It contains a hashtable of query
 * parameters that are interpreted by the specific content fetcher bean.
 * @author hugo
 */
public class ContentQuery implements Serializable {
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


    /** List of query parameters */
    Hashtable queryParameters = new Hashtable();
    
    /** Label for this query */
    private String label;
    
    public ContentQuery() {
    }

    /** Set the query label */
    public void setLabel(String label){
        this.label = label;
    }
    
    /** Get the query label */
    public String getLabel(){
        return label;
    }
    
    /** Add a query parameter */
    public void addParameter(String name, Object value){
        queryParameters.put(name, value);
    }
    
    /** Get a query parameter */
    public Object getParameter(String name){
        return queryParameters.get(name);
    }
    
    /** Does a query parameter exist */
    public boolean parameterExists(String name){
        return queryParameters.containsKey(name);
    }
    
    /** Get a list of names */
    public Iterator names() {
        return queryParameters.keySet().iterator();
    }
}