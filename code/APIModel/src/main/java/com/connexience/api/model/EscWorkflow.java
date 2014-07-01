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
 * Simple Workflow representation for SOAP web service
 * @author hugo
 */
@XmlType
public class EscWorkflow extends EscObject implements JsonSerializable {
    private long currentVersionSize;
    private int currentVersionNumber;
    
    public EscWorkflow() {
    }
    
    public EscWorkflow(JSONObject json) {
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

    @Override
    public JSONObject toJsonObject() {
        return new JSONObject(this);
    }

    @Override
    public final void parseJsonObject(JSONObject json) {
        super.parseJsonObject(json);
        currentVersionNumber = json.getInt("currentVersionNumber", 0);
        currentVersionSize = json.getLong("currentVersionSize", 0);
    }
    
    @Override
    public String getObjectType() {
        return getClass().getSimpleName();
    }    
}
