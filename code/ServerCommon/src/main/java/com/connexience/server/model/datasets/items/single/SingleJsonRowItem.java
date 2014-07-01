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
import com.connexience.server.util.JSONContainer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * This class represents a single row of json data
 * @author hugo
 */
public class SingleJsonRowItem extends SingleValueItem {
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


    /** JSON Data string */
    private String jsonString = "";
    
    /** Extra data in JSON format - update counts etc */
    private String auxiliaryString = "";
    
    public String getStringValue(){
        return jsonString;
    }
    
    public void setStringValue(String value){
        jsonString = value;
    }

    public String getAuxiliaryString() {
        return auxiliaryString;
    }

    public void setAuxiliaryString(String auxiliaryString) {
        this.auxiliaryString = auxiliaryString;
    }

    @Override
    public void reset() {
        jsonString = "";
        auxiliaryString = "";
    }

    @Override
    @JsonIgnore
    public void setObjectValue(Object value) throws ConnexienceException {
        String updateJson = null;
        if(value instanceof JSONContainer){
            updateJson = ((JSONContainer)value).getStringData();
            
        } else if(value instanceof String){
            updateJson = (String)value;
            
        } else {
            throw new ConnexienceException("JsonRowItem only supports JSONContainer or String data");
        }
        
        try {
            JSONObject myObject = null;
            if(!jsonString.isEmpty()){
                myObject = new JSONObject(jsonString);
            } else {
                myObject = new JSONObject();
            }
            
            JSONObject updatedData = new JSONObject(updateJson);
            
            // Add all the items from the update into this data
            Iterator<?> keys = updatedData.keys();
            String key;
            while(keys.hasNext()){
                key = keys.next().toString();
                myObject.put(key, updatedData.get(key));
            }
            
            jsonString = myObject.toString();
        } catch (Exception e){
            throw new ConnexienceException("Error setting value in SingleRowJson: " + e.getMessage());
        }        
    }

    @Override
    @JsonIgnore
    public Object getObjectValue() {
        return new JSONContainer(jsonString);
    }

    @Override
    public void updateWithObjectValue(Object value) throws ConnexienceException {
        String updateJson = null;
        if(value instanceof JSONContainer){
            updateJson = ((JSONContainer)value).getStringData();
            
        } else if(value instanceof String){
            updateJson = (String)value;
            
        } else {
            throw new ConnexienceException("JsonRowItem only supports JSONContainer or String data");
        }
        
        if(getUpdateStrategy().equals(UPDATE_REPLACES_VALUES)){
            // Just replace data
            jsonString = updateJson;
            
        } else {
            // Do updating
            try {
                JSONObject myObject = null;
                JSONObject updateCountObject = null;

                if(jsonString!=null && !jsonString.isEmpty()){
                    myObject = new JSONObject(jsonString);
                } else {
                    myObject = new JSONObject();
                }

                if(auxiliaryString!=null && !auxiliaryString.isEmpty()){
                    updateCountObject = new JSONObject(auxiliaryString);
                } else {
                    updateCountObject = new JSONObject();
                }

                // Parse the update data
                JSONObject updatedData = new JSONObject(updateJson);

                // Add all the items from the update into this data
                Iterator<?> keys = updatedData.keys();
                String key;
                String stringValue;
                String updateCountString;
                int localUpdateCount;
                String existingStringValue;
                double doubleValue;
                double existingDoubleValue;
                boolean isNumber = false;
                boolean isExistingValueNumber = false;

                while(keys.hasNext()){
                    key = keys.next().toString();

                    // Get the text value from the JSON object
                    stringValue = updatedData.getString(key);

                    try {
                        doubleValue = Double.valueOf(stringValue);
                        isNumber = true;
                    } catch (NumberFormatException nfe){
                        doubleValue = 0;
                        isNumber = false;
                    }

                    // Increment the update count for the object being set
                    if(updateCountObject.has(key)){
                        // Retrieve the value if it there
                        localUpdateCount = Integer.valueOf(updateCountObject.getString(key));
                        localUpdateCount++;
                        updateCountObject.put(key, Integer.toString(localUpdateCount));
                    } else {
                        // Create a new value
                        localUpdateCount = 1;
                        updateCountObject.put(key, Integer.toString(localUpdateCount));
                    }

                    // Does the original data contain this value
                    if(myObject.has(key)){
                        existingStringValue = myObject.getString(key);
                        try {
                            existingDoubleValue = Double.valueOf(existingStringValue);
                            isExistingValueNumber = true;
                        } catch (NumberFormatException nfe){
                            existingDoubleValue = 0;
                            isExistingValueNumber = false;
                        }

                        // Update value according to strategy
                        if(isNumber && isExistingValueNumber){
                            if(getUpdateStrategy().equals(UPDATE_CALCULATES_AVERAGE)){
                                if(getUpdateCount()>0){
                                    double newValue = ((existingDoubleValue * (double)localUpdateCount) + doubleValue) / ((double)localUpdateCount + 1.0);
                                    myObject.put(key, newValue);
                                } else {
                                    // Set set the value as this is the first update
                                    myObject.put(key, doubleValue);
                                }

                            } else if(getUpdateStrategy().equals(UPDATE_CALCULATES_SUM)){
                                double newValue = existingDoubleValue + doubleValue;
                                myObject.put(key, newValue);

                            } else if(getUpdateStrategy().equals(UPDATE_CALCULATES_MAXIMUM)){
                                if(doubleValue>existingDoubleValue){
                                    myObject.put(key, doubleValue);
                                }

                            } else if(getUpdateStrategy().equals(UPDATE_CALCULATES_MINIMUM)){
                                if(doubleValue<existingDoubleValue){
                                    myObject.put(key, doubleValue);
                                }

                            } else if(getUpdateStrategy().equals(UPDATE_REPLACES_VALUES)){
                                myObject.put(key, doubleValue);
                            }

                        } else {
                            // Only option is to set as a string
                            myObject.put(key, stringValue);
                        }

                    } else {
                        // Set value
                        if(isNumber){
                            // Set as number
                            myObject.put(key, doubleValue);
                        } else {
                            // Set as string
                            myObject.put(key, stringValue);
                        }
                    }
                }   

                // Increment the updates
                setUpdateCount(getUpdateCount() + 1);
                auxiliaryString = updateCountObject.toString();
                jsonString = myObject.toString();

            } catch (Exception e){
                throw new ConnexienceException("Error updating value: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void register() {
        DatasetCatalog.register(new DatasetCatalogItem(getClass(), "json-row-item", getTypeLabel(), "Single row of data"));
    }

    @Override
    public String getTypeLabel() {
        return "JSONRow";
    }
}