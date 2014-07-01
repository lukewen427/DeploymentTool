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

import com.connexience.server.model.datasets.queries.simple.CurrentJsonValueQuery;
import com.connexience.server.model.datasets.queries.index.IndexRangeQuery;
import com.connexience.server.model.datasets.queries.index.LatestByIndexQuery;
import com.connexience.server.model.datasets.queries.system.ProjectDatasetItemQuery;
import com.connexience.server.model.datasets.queries.system.SystemDatasetItemQuery;
import com.connexience.server.model.datasets.queries.temporal.AbsoluteTimeRangeQuery;
import com.connexience.server.model.datasets.queries.temporal.LatestByAbsoluteTimeQuery;
import com.connexience.server.model.datasets.queries.temporal.LatestByRelativeTimeQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * This class provides a factory that can create data set queries based on 
 * an ID.
 * @author hugo
 */
public class DatasetQueryFactory {
    private static final HashMap<String, Class> queryCatalog = new HashMap<>();
    
    static {
        queryCatalog.put("current-json", CurrentJsonValueQuery.class);
        queryCatalog.put("latest-by-index", LatestByIndexQuery.class);
        queryCatalog.put("index-range", IndexRangeQuery.class);
        queryCatalog.put("latest-by-realtive-time", LatestByRelativeTimeQuery.class);
        queryCatalog.put("latest-by-absolute-time", LatestByAbsoluteTimeQuery.class);
        queryCatalog.put("absolute-time-range", AbsoluteTimeRangeQuery.class);
        queryCatalog.put("system-dataset-item-value", SystemDatasetItemQuery.class);
        queryCatalog.put("system-dataset-project-query", ProjectDatasetItemQuery.class);
    }
    
    /** List the query IDs */
    public static ArrayList<String> listIds(){
        ArrayList<String> result = new ArrayList<>();
        Set<String> keys = queryCatalog.keySet();
        for(String s : keys){
            result.add(s);
        }
        return result;
    }
    
    /** Create a query */
    public static DatasetQuery createQuery(String id){ 
        if(queryCatalog.containsKey(id)){
            try {
                return (DatasetQuery)queryCatalog.get(id).newInstance();
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
    
    /** Does this factory contain a class with a specified name */
    public static boolean containsClassName(String className){
        Collection<Class> items = queryCatalog.values();
        for(Class c : items){
            if(c.getName().equals(className)){
                return true;
            }
        }
        return false;
    }
    
    /** Create a query */
    public static DatasetQuery createQueryByClassname(String className){
        if(containsClassName(className)){
            try {
                return (DatasetQuery)Class.forName(className).newInstance();
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }            
        } else {
            return null;
        }
    }
    
    /** Create a set of queries that match a specified item */
    public static ArrayList<DatasetQuery> createQueryTemplatesForItem(DatasetItem item){
        DatasetQuery q;
        ArrayList<DatasetQuery> results = new ArrayList<>();
        for(Class c : queryCatalog.values()){
            q = createQueryByClassname(c.getName());
            if(q.isItemSupported(item)){
                q.setDatasetId(item.getDatasetId());
                q.setItemName(item.getName());
                results.add(q);
            }
        }
        return results;
    }
    
    /** Create an enactor for a query */
    public static DatasetQueryEnactor createEnactorForQuery(DatasetQuery query){
        try {
            return (DatasetQueryEnactor)query.getEnactorClass().newInstance();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
