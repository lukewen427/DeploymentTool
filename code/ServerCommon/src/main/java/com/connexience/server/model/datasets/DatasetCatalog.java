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
package com.connexience.server.model.datasets;

import com.connexience.server.model.datasets.items.multiple.JsonMultipleValueItem;
import com.connexience.server.model.datasets.items.single.DoubleValueItem;
import com.connexience.server.model.datasets.items.single.SingleJsonRowItem;
import com.connexience.server.model.datasets.system.SystemDataset;
import com.connexience.server.model.datasets.system.sources.ProjectDataset;
import com.connexience.server.model.datasets.system.sources.UserStatsDataset;
import java.util.ArrayList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;


/**
 * This class maintains a catalog of available dashboard items that can be included
 * in a dashboard definition
 * @author hugo
 */
public class DatasetCatalog {
    /** Dataset items that can be added to data sets */
    private static HashMap<String, DatasetCatalogItem> catalog = new HashMap<>();
    
    /** Standard datasets that are available everywhere */
    private static HashMap<String, SystemDataset> systemDatasets = new HashMap<>();
    
    static {
        new DoubleValueItem().register();
        new JsonMultipleValueItem().register();
        new SingleJsonRowItem().register();
    }
    
    static {
        register(new UserStatsDataset());
        register(new ProjectDataset());
    }
    
    public static void register(DatasetCatalogItem item){
        catalog.put(item.getId(), item);
    }
    
    public static void register(SystemDataset dataset){
        systemDatasets.put(dataset.getId(), dataset);
    }
    
    public static Collection<DatasetCatalogItem> listNonSystemItems() {
        return catalog.values();
    }

    public static DatasetCatalogItem getItem(String id){
        if(catalog.containsKey(id)){
            return catalog.get(id);
        } else {
            return null;
        }
    }
    
    /** Get a system dataset by id */
    public static Dataset getSystemDataset(String id){
        if(systemDatasets.containsKey(id)){
            return systemDatasets.get(id);
        } else {
            return null;
        }
    }
    
    /** IS a dataset a system dataset */
    public static boolean isSystemDataset(Dataset ds){
        return systemDatasets.containsKey(ds.getId());
    }
    
    /** Does the system catalog contain an ID */
    public static boolean isSystemDataset(String id){
        return systemDatasets.containsKey(id);
    }
    
    /** List all of the system datasets */
    public static List<SystemDataset> listSystemDatasets(){
        ArrayList<SystemDataset> results = new ArrayList<>();
        for(SystemDataset ds : systemDatasets.values()){
            results.add(ds);
        }
        return results;
    }
    
}