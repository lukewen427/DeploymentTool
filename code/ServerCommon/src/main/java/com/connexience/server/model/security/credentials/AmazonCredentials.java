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
package com.connexience.server.model.security.credentials;

import com.connexience.server.model.security.StoredCredentials;
import org.json.JSONObject;

/**
 * This class stores a set of credentials for Amazon WebServices.
 * @author hugo
 */
public class AmazonCredentials extends StoredCredentials {
    private String accessKey = "";
    private String accessKeyId = "";
    
    @Override
    public String getCredentialType() {
        return "Amazon";
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }
    
    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
        if(json.has("AccessKeyID")){
            accessKeyId = json.getString("AccessKeyID");
        }
        if(json.has("AccessKey")){
            accessKey = json.getString("AccessKey");
        }
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("AccessKeyID", accessKeyId);
        json.put("AccessKey", accessKey);
        return json;
    }    
}