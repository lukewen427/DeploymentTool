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
package com.connexience.server.model.datasets.system;

import com.connexience.server.model.datasets.Dataset;
import com.connexience.server.model.datasets.DatasetItem;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a dataset that is provided by the system and does not reside in the
 * database.
 * @author hugo
 */
public class SystemDataset extends Dataset {
    /** List of items */
    protected ArrayList<SystemDatasetItem> items = new ArrayList<>();
    
    public SystemDataset(String id) {
        super();
        this.setId(id);
    }
    
    /** Add an item */
    public void addItem(SystemDatasetItem item){
        item.setDatasetId(getId());
        items.add(item);
    }
    
    /** List the items */
    public List<SystemDatasetItem> listItems(){
        return items;
    }
    
    /** Get a dataset item by name */
    public SystemDatasetItem getItem(String name){
        for(SystemDatasetItem i : items){
            if(i.getName().equals(name)){
                return i;
            }
        }
        return null;
    }
}