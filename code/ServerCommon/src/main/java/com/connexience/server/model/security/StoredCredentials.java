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
package com.connexience.server.model.security;

import com.connexience.server.model.ServerObject;
import com.connexience.server.model.security.credentials.AmazonCredentials;
import com.connexience.server.model.security.credentials.AzureCredentials;
import com.connexience.server.util.JSONEditable;
import java.util.ArrayList;
import org.json.JSONObject;

/**
 * This class represents a set of stored credentials that can be used to access
 * a certain resource
 * @author hugo
 */
public abstract class StoredCredentials extends ServerObject implements JSONEditable {
    /** List the credential types */
    public static ArrayList<String> listTypes(){
        ArrayList<String> types = new ArrayList<>();
        types.add("Amazon");
        types.add("WindowsAzure");
        return types;
    }
    
    /** Create credentials with a certain type */
    public static StoredCredentials createCredentials(String credentialsType){
        switch(credentialsType){
            case "Amazon":
                return new AmazonCredentials();
            case "WindowsAzure":
                return new AzureCredentials();
            default:
                return null;
        }
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = new JSONObject();
        json.put("Name", getName());
        json.put("Description", getDescription());
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        if(json.has("Name")){
            setName(json.getString("Name").trim());
        }
        
        if(json.has("Description")){
            setDescription(json.getString("Description").trim());
        }
    }

    
    public abstract String getCredentialType();
}