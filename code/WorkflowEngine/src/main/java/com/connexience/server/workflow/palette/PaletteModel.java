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
package com.connexience.server.workflow.palette;

import org.pipeline.core.xmlstorage.*;
import java.util.*;

/**
 * This class provides a representation of a palette of blocks.
 * @author hugo
 */
public class PaletteModel implements XmlStorable {
    /** Palette categories */
    private Vector<PaletteCategory> categories = new Vector<>();
    
    /** Title of this palette */
    private String title = "Services";
    
    /** Does a category exist */
    public boolean hasCategory(String category){
        for(int i=0;i<categories.size();i++){
            if(categories.get(i).getName().equals(category)){
                return true;
            }
        }
        return false;
    }
    
    /** Set the palette title */
    public void setTitle(String title){
        this.title = title;
    }
    
    /** Get the palette title */
    public String getTitle(){
        return title;
    }
    
    /** Override the toString method */
    @Override
    public String toString(){
        return title;
    }
    
    /** Get a category */
    public PaletteCategory getCategory(String category){
        for(int i=0;i<categories.size();i++){
            if(categories.get(i).getName().equals(category)){
                return categories.get(i);
            }
        }
        return null;
    }
    
    /** Return the number of categories */
    public int getCategoryCount(){
        return categories.size();
    }
    
    /** Get a category by index */
    public PaletteCategory getCategory(int index){
        return categories.get(index);
    }
    
    /** Get the position of a category within this model */
    public int getPositionOfCategory(PaletteCategory category) {
        return categories.indexOf(category);
    }
    
    /** Add an item */
    public void addItem(PaletteItem item){
        if(hasCategory(item.getCategory())){
            // Add to existing category
            getCategory(item.getCategory()).addItem(item);
            
        } else {
            // Create new category
            PaletteCategory category = new PaletteCategory();
            category.setName(item.getCategory());
            category.addItem(item);
            categories.add(category);
        }
    }
    /** Save the contents of this palette to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("PaletteModel");
        store.add("CategoryCount", categories.size());
        for(int i=0;i<categories.size();i++){
            store.add("Category" + i, categories.get(i));
        }
        return store;
    }

    /** Load the palette from storage */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        int count = store.intValue("CategoryCount", 0);
        categories.clear();
        for(int i=0;i<count;i++){
            categories.add((PaletteCategory)store.xmlStorableValue("Category" + i));
        }
    }
}