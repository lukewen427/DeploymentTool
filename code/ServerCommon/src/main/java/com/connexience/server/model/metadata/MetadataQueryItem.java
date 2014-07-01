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
package com.connexience.server.model.metadata;

import com.connexience.server.util.JSONEditable;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * This class can create a chunk of HQL to build a bigger query
 * @author hugo
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class MetadataQueryItem implements Serializable, JSONEditable {
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


    /** Name of the piece of metadata */
    protected String name = "";

    /** Should the name part of the query be case sensitive */
    protected boolean caseSensitiveName = false;
    
    /** Position counter for creating named parameters */
    protected int positionCounter = 0;

    /** Should the category name be included */
    private boolean categoryIncluded = false;
    
    /** Category search text */
    private String categorySearchText = "";
    
    /** Is the category search text case insensitive */
    private boolean caseSensitiveCategoryName = false;
    
    /** Item label */
    private String label = "Search Item";
    
    public int getPositionCounter() {
        return positionCounter;
    }

    public void setPositionCounter(int positionCounter) {
        this.positionCounter = positionCounter;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getLowercaseName(){
        return name.toLowerCase();
    }
    
    public void setLowercaseName(String lcn){
        
    }

    public void setCaseSensitiveName(boolean caseSensitiveName) {
        this.caseSensitiveName = caseSensitiveName;
    }

    public boolean isCaseSensitiveName() {
        return caseSensitiveName;
    }
    
    protected String createQueryRoot(){
        StringBuilder queryBuilder = new StringBuilder();
        
        // Add name
        if(caseSensitiveName){
            queryBuilder.append("name = '" + name + "'");
        } else {
            queryBuilder.append("lower(name) = '" + name.toLowerCase() + "'");
        }
        
        // Add category if required
        if(categoryIncluded){
            if(caseSensitiveCategoryName){
                queryBuilder.append(" and category='" + categorySearchText + "'");
            } else {
                queryBuilder.append(" and lower(category)='" + categorySearchText.toLowerCase() + "'");
            }
        }
        
        return queryBuilder.toString();
    }
    
    /** Create a named parameter tag */
    public synchronized String createNamedParameterTag(){
        String tag = "p" + positionCounter;
        positionCounter++;
        return tag;
    }
    
    public boolean isCategoryIncluded() {
        return categoryIncluded;
    }

    public void setCategoryIncluded(boolean categoryIncluded) {
        this.categoryIncluded = categoryIncluded;
    }

    public void setCategory(String categorySearchText) {
        this.categorySearchText = categorySearchText;
    }

    public String getCategory() {
        return categorySearchText;
    }

    public boolean isCaseSensitiveCategoryName() {
        return caseSensitiveCategoryName;
    }

    public void setCaseSensitiveCategoryName(boolean caseSensitiveCategoryName) {
        this.caseSensitiveCategoryName = caseSensitiveCategoryName;
    }
    
    public abstract String createQuery();
    
    @JsonIgnore
    public abstract Object[][] getParameters();
    
    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = new JSONObject();
        json.put("Category", categorySearchText);
        json.put("IncludeCategory", categoryIncluded);
        json.put("CaseSensitiveCategory", caseSensitiveCategoryName);
        json.put("ItemName", name);
        json.put("CaseSensitiveItemName", caseSensitiveName);
        json.put("Label", label);
        return json;
    }
    
    @Override
    public void readJson(JSONObject json) throws Exception {
        categorySearchText = json.getString("Category");
        categoryIncluded = json.getBoolean("IncludeCategory");
        caseSensitiveCategoryName = json.getBoolean("CaseSensitiveCategory");
        name = json.getString("ItemName");
        caseSensitiveName = json.getBoolean("CaseSensitiveItemName");
        label = json.getString("Label");
    }
}