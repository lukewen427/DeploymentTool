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
 * This class represents a category of blocks within a palette model
 * @author hugo
 */
public class PaletteCategory implements XmlStorable {
    /** List of items within this category */
    private Vector<PaletteItem> items = new Vector<>();
    
    /** Category name */
    private String name;
    
    /** Set the category name */
    public void setName(String name){
        this.name = name;
    }
    
    /** Get the category name */
    public String getName(){
        return name;
    }
    
    /** Add an item */
    public void addItem(PaletteItem item){
        items.add(item);
    }
    
    /** Override the toString method */
    @Override
    public String toString(){
        return name;
    }
    
    /** Get the number of items in this category */
    public int getItemCount(){
        return items.size();
    }
                    
    /** Get an item by index */
    public PaletteItem getItem(int index){
        return items.get(index);
    }
    
    /** Get the position of an item in this category */
    public int getPositionOfItem(PaletteItem item){
        return items.indexOf(item);
    }
    
    /** Save this palette category to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("PaletteCategory");
        store.add("Name", name);
        store.add("ItemCount", items.size());
        for(int i=0;i<items.size();i++){
            store.add("Item" + i, items.get(i));
        }
        return store;
    }

    /** Recreate this palette category */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        name = store.stringValue("Name", "");
        int count = store.intValue("ItemCount", 0);
        items.clear();
        for(int i=0;i<count;i++){
            items.add((PaletteItem)store.xmlStorableValue("Item" + i));
        }
    }
}