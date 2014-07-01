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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This is a simple class that contains a list of metadata so that all the
 * metadata for an object can be managed easily.
 * @author hugo
 */
public class MetadataCollection implements Serializable, XmlStorable {
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


    public enum DuplicatePolicy {
        DUPLICATES_OVERWRITTEN,
        DUPLICATES_IGNORED,
        DUPLICATES_REMOVED
    }

    /** Metadata items */
    private ArrayList<MetadataItem> items = new ArrayList<>();

    /** Hashmap of items */
    private HashMap<String,MetadataItem> dictionary = new HashMap<>();
    
    /** Object ID */
    private String objectId;
    
    /** User ID */
    private String userId;
    
    public MetadataCollection() {
    }
    
    public MetadataCollection(MetadataItem[] initialItems){
        for(int i=0;i<initialItems.length;i++){
            items.add(initialItems[i]);
            dictionary.put(initialItems[i].getCategory() + "." + initialItems[i].getName(), initialItems[i]);
        }
    }

    public MetadataCollection(List<?> initialItems){
        for(int i=0;i<initialItems.size();i++){
            if(initialItems.get(i) instanceof MetadataItem){
                items.add((MetadataItem)initialItems.get(i));
                dictionary.put(((MetadataItem)initialItems.get(i)).getCategory() + "." + ((MetadataItem)initialItems.get(i)).getName(), ((MetadataItem)initialItems.get(i)));
            }
        }
    }
    
    @JsonIgnore
    public MetadataCollection getCopy(){
        MetadataCollection mdc = new MetadataCollection();
        mdc.setObjectId(objectId);
        mdc.setUserId(userId);
        for(MetadataItem i : items){
            mdc.add(i.getCopy());
        }
        return mdc;
    }

    /** Find a piece of metadata */
    public MetadataItem find(String name){
        for(MetadataItem i : items){
            if(i.getName().equals(name)){
                return i;
            }
        }
        return null;
    }
    
    /** Find a piece of metadata */
    public MetadataItem find(String category, String name) {
        for(MetadataItem i : items){
            if(i.getName().equals(name) && i.getCategory().equals(category)){
                return i;
            }
        }
        return null;
    }
    
    public void setObjectId(String objectId) {
        this.objectId = objectId;
        for(MetadataItem i : items){
            i.setObjectId(objectId);
        }
    }

    public String getObjectId() {
        return objectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        for(MetadataItem i : items){
            i.setUserId(userId);
        }
    }
    
    public int size(){
        return items.size();
    }
    
    public void add(MetadataItem item){
        item.setObjectId(objectId);
        item.setUserId(userId);
        items.add(item);
        dictionary.put(item.getCategory() + "." + item.getName(), item);
    }
    
    @JsonIgnore
    public MetadataItem get(int index){
        return items.get(index);
    }
    
    public ArrayList<MetadataItem> getItems(){
        return items;
    }
    
    public void setItems(ArrayList<MetadataItem> items){
        this.items = items;
    }
    
    public void debugPrint(){
        for(MetadataItem i : items){
            System.out.println(i);
        }
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("MetadataCollection");
        store.add("ObjectID", objectId);
        store.add("UserID", userId);
        store.add("Size", items.size());
        for(int i=0;i<items.size();i++){
            store.add("Item" + i, items.get(i));
        }
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        int size = store.intValue("Size", 0);
        items.clear();
        for(int i=0;i<size;i++){
            items.add((MetadataItem)store.xmlStorableValue("Item" + i));
        }
        setObjectId(store.stringValue("ObjectID", null));
        setUserId(store.stringValue("UserID", null));
    }
    
    /** Merge a set of metadata into this one */
    public void merge(MetadataCollection mdc, DuplicatePolicy duplicates) {
        MetadataItem item;
        for(int i=0;i<mdc.size();i++){
            item = mdc.get(i);
            if(!(dictionary.containsKey(item.getCategory() + "." + item.getName()))){
                // No existing item
                add(item.getCopy());
            } else {
                // Existing item - what to do
                if(duplicates==DuplicatePolicy.DUPLICATES_OVERWRITTEN){
                    // Overwrite
                    MetadataItem itemToRemove = dictionary.get(item.getCategory() + "." + item.getName());
                    items.remove(itemToRemove);
                    dictionary.remove(itemToRemove);
                    add(item.getCopy());
                } else if(duplicates==DuplicatePolicy.DUPLICATES_REMOVED){
                    // Remove copy
                    MetadataItem itemToRemove = dictionary.get(item.getCategory() + "." + item.getName());
                    items.remove(itemToRemove);
                    dictionary.remove(itemToRemove);                    
                }
            }
        }
    }
    
    /** Remove all non matching categories from this list */
    public void removeNonMatchingCategories(String[] categoriesToKeep) {
        
    }
}