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
package com.connexience.server.model.datasets.items;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.datasets.DatasetItem;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONObject;

/**
 * Tbis class represents a dashboard item that holds a single piece of data
 * @author hugo
 */
public abstract class SingleValueItem extends DatasetItem {
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


    /** Strategy for updating this value */
    private String updateStrategy = UPDATE_REPLACES_VALUES;

    /** Number of updates this item has received */
    private int updateCount = 0;
    
    /** Wrapper to reset this object. This first sets the update count to zero
     * then performs an object specific reset */
    public void resetItem(){
        this.updateCount = 0;
        reset();
    }
    
    /** Reset this item to the default value */
    public abstract void reset();
    
    /** Set an object value */
    @JsonIgnore
    public abstract void setObjectValue(Object value) throws ConnexienceException;
    
    /** Get the object value */
    @JsonIgnore
    public abstract Object getObjectValue();
            
    /** Update with an object value */
    public abstract void updateWithObjectValue(Object value) throws ConnexienceException;

    @Override
    @JsonIgnore
    public boolean isMultipleItem() {
        return false;
    }

    public String getUpdateStrategy() {
        return updateStrategy;
    }

    public void setUpdateStrategy(String updateStrategy) {
        this.updateStrategy = updateStrategy;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("UpdateStrategy", updateStrategy);
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        updateStrategy = json.getString("UpdateStrategy");
        super.readJson(json);
    }
}