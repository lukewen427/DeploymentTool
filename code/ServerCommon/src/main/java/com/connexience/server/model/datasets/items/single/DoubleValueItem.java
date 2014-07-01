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
package com.connexience.server.model.datasets.items.single;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.datasets.DatasetCatalog;
import com.connexience.server.model.datasets.DatasetCatalogItem;
import com.connexience.server.model.datasets.items.SingleValueItem;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONObject;

/**
 * This dashboard item contains a single numerical value
 * @author hugo
 */
public class DoubleValueItem extends SingleValueItem {
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


    /** Current value of this numerical item */
    private double doubleValue = 0;
    
    /** Initial value for this item */
    private double initialDoubleValue = 0;
    
    @Override
    public void reset() {
        this.doubleValue = initialDoubleValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public double getInitialDoubleValue() {
        return initialDoubleValue;
    }

    public void setInitialDoubleValue(double initialDoubleValue) {
        this.initialDoubleValue = initialDoubleValue;
    }

    @Override
    public Object getObjectValue() {
        return doubleValue;
    }
    
    @Override
    public void setObjectValue(Object value) throws ConnexienceException {
        if(value instanceof Number){
            doubleValue = ((Number)value).doubleValue();
        } else {
            throw new ConnexienceException("Cannot set value for dashboard item: " + getName() + " value is not a number");
        }
    }

    @Override
    public void updateWithObjectValue(Object value) throws ConnexienceException {
        if(value instanceof Number){
            if(getUpdateStrategy().equals(UPDATE_CALCULATES_AVERAGE)){
                doubleValue = ((doubleValue * (double)getUpdateCount()) + ((Number)value).doubleValue()) / ((double)getUpdateCount() + 1.0);
                
            } else if(getUpdateStrategy().equals(UPDATE_CALCULATES_MAXIMUM)){
                if(((Number)value).doubleValue()>doubleValue){
                    doubleValue = ((Number)value).doubleValue();
                }
                
            } else if(getUpdateStrategy().equals(UPDATE_CALCULATES_MINIMUM)){
                if(((Number)value).doubleValue()<doubleValue){
                    doubleValue = ((Number)value).doubleValue();
                }
                
            } else if(getUpdateStrategy().equals(UPDATE_CALCULATES_SUM)){
                doubleValue = doubleValue + ((Number)value).doubleValue();
                
            } else {
                // Replace
                doubleValue = ((Number)value).doubleValue();
            }
            
            // Increment the update count
            setUpdateCount(getUpdateCount() + 1);
        } else {
            throw new ConnexienceException("Cannot update value for dashboard item: " + getName() + " value is not a number");
        }
    }

    @Override
    @JsonIgnore
    public String getTypeLabel() {
        return "Double";
    }

    @Override
    public void register() {
        DatasetCatalog.register(new DatasetCatalogItem(getClass(), "double-value-item", getTypeLabel(), "Single numerical value"));
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("InitialValue", initialDoubleValue);
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
        initialDoubleValue = json.getDouble("InitialValue");
    }
}