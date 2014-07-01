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
package com.connexience.server.model.properties;

import com.connexience.server.util.JSONContainer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.io.Serializable;

/**
 * This class represents a single string property item that can be associated
 * with a property group
 * @author nhgh
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class PropertyItem implements Serializable {
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


    /** ID of this property item */
    private long id;
    
    /** ID of the parent group */
    private long groupId;
    
    /** Property name */
    private String name;
    
    /** Property string value */
    private String value;

    /** Property type label */
    private String type = "String";
    
    public PropertyItem() {
    }

    /** Copy an existing property item */
    public PropertyItem(PropertyItem item) {
        id = item.getId();
        groupId = item.getGroupId();
        name = item.getName();
        value = item.getValue();
        type = item.getType();
    }

    public PropertyItem copyWithoutId(){
        PropertyItem i = new PropertyItem();
        i.setGroupId(groupId);
        i.setName(name);
        i.setValue(value);
        i.setType(type);
        return i;
    }
    
    /** Get the ID of the group containing this property */
    public long getGroupId() {
        return groupId;
    }

    /** Set the ID of the group containing this property */
    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    /** Get the ID of this property */
    public long getId() {
        return id;
    }

    /** Set the ID of this property */
    public void setId(long id) {
        this.id = id;
    }

    /** Get the name of this property */
    public String getName() {
        return name;
    }

    /** Set the name of this property */
    public void setName(String name) {
        this.name = name;
    }

    /** Get the string value of this property */
    public String getValue() {
        return value;
    }

    /** Set the string value of this property */
    public void setValue(String value) {
        this.value = value;
    }
    
    @JsonIgnore
    public void setValue(double value){
        this.value = Double.toString(value);
    }
    
    @JsonIgnore
    public void setValue(long value){
        this.value = Long.toString(value);
    }
    
    @JsonIgnore
    public void setValue(boolean value){
        if(value==true){
            this.value = "true";
        } else {
            this.value = "false";
        }
    }
    
    @JsonIgnore
    public void setValue(JSONContainer value){
        this.value = value.getStringData();
    }
    
    /** Display the value as the toString */
    public String toString(){
        return name + ": " + value;
    }

    /** Get the property type label */
    public String getType() {
        return type;
    }

    /** Set the property type label */
    public void setType(String type) {
        this.type = type;
    }
    
    /** Try and get the value of this property as a double */
    @JsonIgnore
    public double doubleValue(){
        return Double.parseDouble(value);
    }
    
    @JsonIgnore
    public int intValue(){
        return Integer.parseInt(value);
    }
    
    @JsonIgnore
    public long longValue(){
        return Long.parseLong(value);
    }
    
    @JsonIgnore
    public boolean booleanValue(){
        if(value!=null && !value.isEmpty()){
            if(value.toLowerCase().trim().equals("true") || value.toLowerCase().trim().equals("1")){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    @JsonIgnore
    public JSONContainer jsonValue(){
        JSONContainer container = new JSONContainer();
        container.setStringData(value);
        return container;
    }
}