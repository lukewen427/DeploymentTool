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
package org.pipeline.core.xmlstorage.replacement;

import org.pipeline.core.xmlstorage.XmlDataObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

/**
 * This class replaces the categories in a data store with one of the alternatives
 * contained in this class.
 * @author hugo
 */
public class CategoryReplacer {
    /** Replacement map */
    private HashMap<String,String> replacements = new HashMap<>();
    
    /** Should the replacer only fix properties with a null category */
    private boolean onlyNullReplaced = false;
    
    /** Default category for properties that are not categorised */
    private String defaultCategory = "";
    
    /** Are empty categories replaced with the default value */
    private boolean automaticDefaultReplacement = false;
    
    public void addReplacement(String propertyName, String newCategory){
        replacements.put(propertyName, newCategory);
    }

    public void setOnlyNullReplaced(boolean onlyNullReplaced) {
        this.onlyNullReplaced = onlyNullReplaced;
    }

    public boolean isOnlyNullReplaced() {
        return onlyNullReplaced;
    }

    public void setAutomaticDefaultReplacement(boolean automaticDefaultReplacement) {
        this.automaticDefaultReplacement = automaticDefaultReplacement;
    }

    public boolean isAutomaticDefaultReplacement() {
        return automaticDefaultReplacement;
    }

    public void setDefaultCategory(String defaultCategory) {
        this.defaultCategory = defaultCategory;
    }

    public String getDefaultCategory() {
        return defaultCategory;
    }
    
    /** Assign a set of replacements for all of the names in a data store */
    public void addReplacementForAllProperties(XmlDataStore store, String category){
        Enumeration e = store.elements();
        XmlDataObject obj;
        
        while(e.hasMoreElements()){
            obj = (XmlDataObject)e.nextElement();
            if(obj.getCategory()==null || obj.getCategory().isEmpty()){
                addReplacement(obj.getName(), category);
            }
        }
    }
    
    public void replaceCategories(XmlDataStore store) throws XmlStorageException {
        Vector names = store.getNames();
        Enumeration e = names.elements();
        String name;
        XmlDataObject value;
        
        while(e.hasMoreElements()){
            name = e.nextElement().toString();
            value = store.get(name);
            
            if(onlyNullReplaced){
                if(value.getCategory()==null || value.getCategory().isEmpty()){
                    if(replacements.containsKey(name)){
                        value.setCategory(replacements.get(name));
                    }
                }
            } else {
                if(replacements.containsKey(name)){
                    value.setCategory(replacements.get(name));
                }                
            }
            
            // Is the category still null and are we replacing nulls with the default
            if(automaticDefaultReplacement && (value.getCategory()==null || value.getCategory().isEmpty())){
                value.setCategory(defaultCategory);
            }
        }
    }
}