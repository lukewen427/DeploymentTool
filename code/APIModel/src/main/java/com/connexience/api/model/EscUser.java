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
package com.connexience.api.model;

import com.connexience.api.model.json.JSONObject;
import com.connexience.api.model.json.JsonSerializable;

import javax.xml.bind.annotation.XmlType;

/**
 * Simple user object for SOAP webservice
 * @author hugo
 */
@XmlType
public class EscUser implements JsonSerializable {
    private String id;
    private String name;
    private String firstName;
    private String surname;

    public EscUser() {
    }
    
    public EscUser(JSONObject json){
        parseJsonObject(json);
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public final JSONObject toJsonObject() {
        return new JSONObject(this);
    }

    @Override
    public final void parseJsonObject(JSONObject json) {
        id = json.getString("id", null);
        name = json.getString("name", null);
        firstName = json.getString("firstName", null);
        surname = json.getString("surname", null);
    }
}