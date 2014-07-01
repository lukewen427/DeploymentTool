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
 * Simple DocumentVersion object for SOAP web service
 * @author hugo
 */
@XmlType
public class EscDocumentVersion implements JsonSerializable {
    private String id;
    private String documentRecordId;
    private String comments;
    private String userId;
    private int versionNumber;
    private long size;
    private long timestamp;
    private String downloadPath;
    private String md5;
    
    public EscDocumentVersion() {

    }

    public EscDocumentVersion(JSONObject json){
        parseJsonObject(json);
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDocumentRecordId() {
        return documentRecordId;
    }

    public void setDocumentRecordId(String documentRecordId) {
        this.documentRecordId = documentRecordId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public JSONObject toJsonObject() {
        return new JSONObject(this);
    }

    @Override
    public void parseJsonObject(JSONObject json) {
        id = json.getString("id", null);
        comments = json.getString("comments", null);
        documentRecordId = json.getString("documentRecordId", null);
        downloadPath = json.getString("downloadPath", null);
        size = json.getLong("size", 0);
        timestamp = json.getLong("timestamp", 0);
        userId = json.getString("userId", null);
        versionNumber = json.getInt("versionNumber", 0);
        md5 = json.getString("md5", null);
    }
}