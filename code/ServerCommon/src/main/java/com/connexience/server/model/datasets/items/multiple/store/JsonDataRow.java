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
package com.connexience.server.model.datasets.items.multiple.store;

import com.connexience.server.model.datasets.items.multiple.JsonMultipleValueItem;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

/**
 * This class represents a row of data for a JsonMultipleValue item
 * @author hugo
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JsonDataRow {
    /** Database ID */  
    private long id;
    
    /** Parent multiple value item */
    private long itemId;
    
    /** JSON data string */
    private String stringValue;

    /** Collection date */
    private Date collectionTime;
    
    public JsonDataRow() {
    }

    public JsonDataRow(JsonMultipleValueItem item, String stringValue) {
        this.itemId = item.getId();
        this.stringValue = stringValue;
        collectionTime = new Date();
    }

    public JsonDataRow(JsonMultipleValueItem item, JSONObject json) throws JSONException, IOException {
        this.itemId = item.getId();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        json.write(writer);
        writer.flush();
        writer.close();
        this.stringValue = new String(stream.toByteArray());
        collectionTime = new Date();
    }
    
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
    
    /** Get the JSON Object */
    @JsonIgnore
    public JSONObject getJson() throws JSONException {
        return new JSONObject(stringValue);
    }    

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getItemId() {
        return itemId;
    }

    public Date getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(Date collectionTime) {
        this.collectionTime = collectionTime;
    }

}