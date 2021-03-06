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
package com.connexience.server.model.metadata.queries;

import com.connexience.server.model.metadata.MetadataQueryItem;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONObject;

/**
 * Boolean query
 * @author hugo
 */
public class BooleanMetadataQueryItem extends MetadataQueryItem {
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


    /** Value being searched for */
    private boolean targetValue = true;

    public BooleanMetadataQueryItem() {
        setLabel("True/False");
    }
    
    @Override
    public String createQuery(){
        return " objectid in (select objectid from metadata where (" + createQueryRoot() + " and booleanvalue=:" + createNamedParameterTag() + ") ";
    }

    @Override
    @JsonIgnore
    public Object[][] getParameters() {
        Object[][] params = new Object[1][2];
        params[0][0] = createNamedParameterTag();
        params[0][1] = targetValue;
        return params;
    }
    
    public void setTargetValue(boolean targetValue) {
        this.targetValue = targetValue;
    }

    public boolean isTargetValue() {
        return targetValue;
    }
    
    @JsonIgnore
    public boolean getTargetValue(){
        return targetValue;
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("TargetValue", targetValue);
        json.put("_className", this.getClass().getName());
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
        this.targetValue = json.getBoolean("TargetValue");
    }
}