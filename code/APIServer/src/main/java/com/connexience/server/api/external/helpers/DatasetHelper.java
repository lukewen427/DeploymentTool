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
package com.connexience.server.api.external.helpers;

import com.connexience.api.model.DatasetInterface;
import com.connexience.api.model.EscDataset;
import com.connexience.api.model.EscDatasetItem;
import com.connexience.api.model.EscDatasetKeyList;
import com.connexience.api.model.json.JSONObject;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.datasets.Dataset;
import com.connexience.server.model.datasets.DatasetItem;
import com.connexience.server.model.datasets.items.MultipleValueItem;
import com.connexience.server.model.datasets.items.SingleValueItem;
import com.connexience.server.model.datasets.items.multiple.JsonMultipleValueItem;
import com.connexience.server.model.datasets.items.single.SingleJsonRowItem;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.util.JSONContainer;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a helper that handles the backend for both the REST 
 * and SOAP dataset services.
 * @author hugo
 */
public class DatasetHelper implements DatasetInterface {
    private Ticket t;

    public DatasetHelper(Ticket t) {
        this.t = t;
    }
     
    @Override
    public EscDataset[] listDatasets() throws Exception {
        List datasets = EJBLocator.lookupDatasetsBean().listDatasets(t, false);
        EscDataset[] results = new EscDataset[datasets.size()];
        for(int i=0;i<datasets.size();i++){
            results[i] = EscObjectFactory.createEscDataset((Dataset)datasets.get(i));
        }
        return results;
    }

    @Override
    public EscDataset getNamedDataset(String name) throws Exception {
        return EscObjectFactory.createEscDataset(EJBLocator.lookupDatasetsBean().getUserDatasetByName(t, name));
    }

    @Override
    public EscDataset getDataset(String id) throws Exception {
        return EscObjectFactory.createEscDataset(EJBLocator.lookupDatasetsBean().getDataset(t, id));
    }
    
    @Override
    public void resetDataset(String id) throws Exception {
        EJBLocator.lookupDatasetsBean().resetDataset(t, id);
    }

    @Override
    public EscDatasetItem[] listDatasetContents(String id) throws Exception {
        List items = EJBLocator.lookupDatasetsBean().getDatasetItems(t, id);
        EscDatasetItem[] results = new EscDatasetItem[items.size()];
                
        for(int i=0;i<items.size();i++){
            results[i] = EscObjectFactory.createEscDatasetItem((DatasetItem)items.get(i));
        }
        return results;
    }

    @Override
    public EscDatasetItem addItemToDataset(String id, EscDatasetItem item) throws Exception {
        Dataset ds = EJBLocator.lookupDatasetsBean().getDataset(t, id);
        if(ds!=null){
            DatasetItem incomingItem = EscObjectFactory.createDatasetItem(item);
            incomingItem.setDatasetId(id);
            incomingItem.setId(-1);
            
            // Only add the item if a pre-existing one doesn't exist
            DatasetItem existingItem = EJBLocator.lookupDatasetsBean().getDatasetItem(t, id, item.getName());
            if(existingItem==null){
                // Just add - no existing item
                DatasetItem savedItem = EJBLocator.lookupDatasetsBean().saveDatasetItem(t, incomingItem);
                return EscObjectFactory.createEscDatasetItem(savedItem);                 
                
            } else {
                if(EscObjectFactory.datasetItemsMatch(existingItem, incomingItem)){
                    return EscObjectFactory.createEscDatasetItem(existingItem);
                } else {
                    throw new Exception("A different item with the same name already exists");
                }
            }
        } else {
            throw new Exception("No such dataset: " + id);
        }
    }

    @Override
    public void deleteItemFromDataset(String datasetId, String itemName) throws Exception {
        DatasetItem item = EJBLocator.lookupDatasetsBean().getDatasetItem(t, datasetId, itemName);
        if(item!=null){
            EJBLocator.lookupDatasetsBean().removeDatasetItem(t, item.getId());
        } else {
            throw new Exception("No such item: " + itemName);     
        }
    }

    @Override
    public String appendToMultipleValueDatasetItem(String datasetId, String itemName, String jsonData) throws Exception {
        DatasetItem item = EJBLocator.lookupDatasetsBean().getDatasetItem(t, datasetId, itemName);
        if(item!=null){
            if(item instanceof JsonMultipleValueItem){
                EJBLocator.lookupDatasetsBean().updateDatasetItemWithValue(t, item.getId(), jsonData);
                return "OK";
            } else {
                throw new Exception("Item: " + itemName + " is not a multi-row JSON item");
            }
        } else {
            throw new Exception("No such item: " + itemName + " in dataset: " + datasetId);
        }
    }

    @Override
    public String updateSingleValueDatasetItem(String datasetId, String itemName, String jsonData) throws Exception {
        DatasetItem item = EJBLocator.lookupDatasetsBean().getDatasetItem(t, datasetId, itemName);
        if(item!=null){
            if(item instanceof SingleJsonRowItem){
                EJBLocator.lookupDatasetsBean().updateDatasetItemWithValue(t, item.getId(), jsonData);
                return "OK";
            } else {
                throw new Exception("Item: " + itemName + " is not a single row JSON item");
            }
        } else {
            throw new Exception("No such item: " + itemName + " in dataset: " + datasetId);
        }
    }

    @Override
    public String querySingleValueDatasetItemAsString(String datasetId, String itemName) throws Exception {
        DatasetItem item = EJBLocator.lookupDatasetsBean().getDatasetItem(t, datasetId, itemName);
        if(item instanceof SingleValueItem){
            SingleValueItem svi = (SingleValueItem)item;
            Object value = svi.getObjectValue();
            if(value!=null){
                if(item instanceof SingleJsonRowItem){
                    // JSON value
                    return value.toString();
                } else {
                    // Numerical value
                    JSONObject json = new JSONObject();
                    json.put("value", value);
                    return json.toString();
                }
            } else {
                return "";
            }
        } else {
            throw new Exception("Item: " + itemName + " is not a single value item");
        }
    }

    @Override
    public Integer getMultipleValueDatasetItemSize(String datasetId, String itemName) throws Exception {
        return EJBLocator.lookupDatasetsBean().getMultipleValueItemSize(t, datasetId, itemName);
    }
    
    @Override
    public String queryMultipleValueDatasetItemAsString(String datasetId, String itemName, int startRow, int maxResults) throws Exception {
        DatasetItem item = EJBLocator.lookupDatasetsBean().getDatasetItem(t, datasetId, itemName);
        if(item instanceof MultipleValueItem){
            JSONContainer c = EJBLocator.lookupDatasetsBean().queryMultipleValueItem(t, datasetId, itemName, startRow, maxResults);
            return c.toString();
        } else {
            throw new Exception("Item: " + itemName + " is not a multiple value item");
        }
    }    

    @Override
    public String queryMultipleValueDatasetItemAsString(String datasetId, String itemName, int startRow, int maxResults, EscDatasetKeyList keys) throws Exception {
        DatasetItem item = EJBLocator.lookupDatasetsBean().getDatasetItem(t, datasetId, itemName);
        if(item instanceof MultipleValueItem){
            JSONContainer c = EJBLocator.lookupDatasetsBean().queryMultipleValueItem(t, datasetId, itemName, startRow, maxResults, keys.getKeys());
            return c.toString();
        } else {
            throw new Exception("Item: " + itemName + " is not a multiple value item");
        }
    }

    @Override
    public EscDataset createDataset(String name) throws Exception {
        Dataset ds = new Dataset();
        ds.setName(name);
        ds.setProjectId(t.getDefaultProjectId());
        ds.setDescription("Dataset created by API");
        ds = EJBLocator.lookupDatasetsBean().saveDataset(t, ds);
        return EscObjectFactory.createEscDataset(ds);
    }

    @Override
    public void deleteDataset(String id) throws Exception {
        EJBLocator.lookupDatasetsBean().removeDataset(t, id);
    }

    @Override
    public EscDataset updateDataset(EscDataset ds) throws Exception {
        Dataset existing = EJBLocator.lookupDatasetsBean().getDataset(t, ds.getId());
        if(existing!=null){
            existing.setName(ds.getName());
            existing.setDescription(ds.getDescription());
            existing = EJBLocator.lookupDatasetsBean().saveDataset(t, existing);
            return EscObjectFactory.createEscDataset(existing);
        } else {
            throw new Exception("No such dataset");
        
        }
    }

    @Override
    public String updateMultipleValueDatasetItem(String datasetId, String itemName, long rowId, String jsonData) throws Exception {
        DatasetItem item = EJBLocator.lookupDatasetsBean().getDatasetItem(t, datasetId, itemName);
        if(item!=null){
            if(item instanceof JsonMultipleValueItem){
                EJBLocator.lookupDatasetsBean().updateExistingMultipleValueItemRow(t, datasetId, itemName, rowId, jsonData);
                return "OK";
            } else {
                throw new Exception("Item: " + itemName + " is not a multi-row JSON item");
            }
        } else {
            throw new Exception("No such item: " + itemName + " in dataset: " + datasetId);
        }
    }    

    @Override
    public EscDatasetItem[] aggregateDatasetItems(String[] datasetIds) throws Exception {
        ArrayList<String> ids = new ArrayList<String>();
        for(String s : datasetIds){
            ids.add(s);
        }
        
        List items = EJBLocator.lookupDatasetsBean().aggregateDatasetItems(t, ids);
        EscDatasetItem[] results = new EscDatasetItem[items.size()];
                
        for(int i=0;i<items.size();i++){
            results[i] = EscObjectFactory.createEscDatasetItem((DatasetItem)items.get(i));
        }
        return results;        
    }

    @Override
    public void removeMultipleValueDatasetItemRow(String datasetId, String itemName, long rowId) throws Exception {
        EJBLocator.lookupDatasetsBean().removeExistingMultipleValueItemRow(t, datasetId, itemName, rowId);
    }

    @Override
    public String getMultipleValueDatasetItemRowAsString(String datasetId, String itemName, long rowId) throws Exception {
        return EJBLocator.lookupDatasetsBean().getMultipleValueDataRow(t, datasetId, itemName, rowId).getStringData();
    }
}