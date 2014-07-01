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
 * Simple document object for SOAP web service
 * @author hugo
 */
@XmlType
public class EscDocument extends EscObject implements JsonSerializable {
    private long currentVersionSize;
    private int currentVersionNumber;
    private String downloadPath;
    private String uploadPath;
    
    public EscDocument() {
    }
    
    public EscDocument(JSONObject json) {
        parseJsonObject(json);
    }

    public int getCurrentVersionNumber() {
        return currentVersionNumber;
    }

    public void setCurrentVersionNumber(int currentVersionNumber) {
        this.currentVersionNumber = currentVersionNumber;
    }

    public long getCurrentVersionSize() {
        return currentVersionSize;
    }

    public void setCurrentVersionSize(long currentVersionSize) {
        this.currentVersionSize = currentVersionSize;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    @Override
    public void parseJsonObject(JSONObject json) {
        super.parseJsonObject(json);
        currentVersionNumber = json.getInt("currentVersionNumber", 0);
        currentVersionSize = json.getLong("currentVersionSize", 0);
        downloadPath = json.getString("downloadPath", null);
        uploadPath = json.getString("uploadPath", null);
    }

    @Override
    public JSONObject toJsonObject() {
        return new JSONObject(this);
    }
    
    @Override
    public String getObjectType() {
        return getClass().getSimpleName();
    }    
}