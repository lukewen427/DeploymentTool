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
package com.connexience.api;

import com.connexience.api.model.DatasetInterface;
import com.connexience.api.model.EscDataset;
import com.connexience.api.model.EscDatasetItem;
import com.connexience.api.model.EscDatasetKeyList;
import com.connexience.api.model.json.JSONArray;
import com.connexience.api.model.json.JSONObject;
import com.connexience.api.model.net.GenericClient;
import java.io.File;

/**
 * This class provides the REST client for the Dataset API.
 * @author hugo
 */
public class DatasetClient extends GenericClient implements DatasetInterface {

    public DatasetClient(String hostname, int port, boolean secure, String username, String password) {
        super(hostname, port, secure, "/api/public/rest/v1/dataset", username, password);
    }

    public DatasetClient() throws Exception {
        super("/api/public/rest/v1/dataset");
    }
    
    public DatasetClient(File apiProperties) throws Exception {
        super("/api/public/rest/v1/dataset", apiProperties);
    }

    public DatasetClient(GenericClient existingClient) throws Exception {
        existingClient.configureClient(this);
        this.setUrlBase("/api/public/rest/v1/dataset");
    }
    
    @Override
    public EscDataset[] listDatasets() throws Exception {
        JSONArray results = retrieveJsonArray("/list");
        EscDataset[] datasets = new EscDataset[results.length()];
        for(int i=0;i<datasets.length;i++){
            datasets[i] = new EscDataset(results.getJSONObject(i));
        }
        return datasets;
    }

    @Override
    public EscDataset getNamedDataset(String name) throws Exception {
        return new EscDataset(retrieveJson("/usersetsbyname/" + name));
    }

    @Override
    public EscDataset getDataset(String id) throws Exception {
        return new EscDataset(retrieveJson("/sets/" + id));
    }
    
    @Override
    public void resetDataset(String id) throws Exception {
        retrieveString("/sets/" + id + "/reset");
    }

    @Override
    public EscDatasetItem[] listDatasetContents(String id) throws Exception {
        JSONArray results = retrieveJsonArray("/sets/" + id + "/items");
        EscDatasetItem[] items = new EscDatasetItem[results.length()];
        for(int i=0;i<items.length;i++){
            items[i] = new EscDatasetItem(results.getJSONObject(i));
        }
        return items;
    }

    @Override
    public EscDatasetItem addItemToDataset(String id, EscDatasetItem item) throws Exception {
        JSONObject json = postJsonRetrieveJson("/sets/" + id + "/items", item.toJsonObject());
        EscDatasetItem returnedItem = new EscDatasetItem(json);
        return returnedItem;
    }

    @Override
    public void deleteItemFromDataset(String datasetId, String itemName) throws Exception {
        deleteResource("/sets/" + datasetId + "/items/" + itemName);
    }

    @Override
    public void removeMultipleValueDatasetItemRow(String datasetId, String itemName, long rowId) throws Exception {
        deleteResource("/sets/" + datasetId + "/items/" + itemName + "/row/" + rowId);
    }

    @Override
    public String updateMultipleValueDatasetItem(String datasetId, String itemName, long rowId, String jsonData) throws Exception {
        return postTextRetrieveText("/sets/" + datasetId + "/items/" + itemName + "/row/" + rowId, jsonData);
    }

    public String updateMultipleValueDatasetItem(String datasetId, String itemName, JSONObject json) throws Exception {
        if(json.has("_id")){
            long rowId = json.getLong("_id");
            return updateMultipleValueDatasetItem(datasetId, itemName, rowId, json.toString());
        } else {
            throw new Exception("JSON data does not contain a row id field ('_id')");
        }
    }
    
    @Override
    public String appendToMultipleValueDatasetItem(String datasetId, String itemName, String jsonData) throws Exception {
        return postTextRetrieveText("/sets/" + datasetId + "/items/" + itemName + "/json", jsonData);
    }

    public void appendToMultipleValueDatasetItem(String datasetId, String itemName, JSONObject json) throws Exception {
        appendToMultipleValueDatasetItem(datasetId, itemName, json.toString());
    }

    @Override
    public String updateSingleValueDatasetItem(String datasetId, String itemName, String jsonData) throws Exception {
        return postJsonRetrieveText("/sets/" + datasetId + "/singleitems/" + itemName + "/row", new JSONObject(jsonData));
    }
    
    public JSONObject querySingleValueDatasetItemAsJson(String datasetId, String itemName) throws Exception {
        String result = querySingleValueDatasetItemAsString(datasetId, itemName);
        if(result.isEmpty()){
            return new JSONObject();
        } else {
            return new JSONObject(result);
        }
    }

    @Override
    public String querySingleValueDatasetItemAsString(String datasetId, String itemName) throws Exception {
        return retrieveString("/sets/" + datasetId + "/singleitems/" + itemName + "/row");
    }

    @Override
    public Integer getMultipleValueDatasetItemSize(String datasetId, String itemName) throws Exception {
        return Integer.parseInt(retrieveString("/sets/" + datasetId + "/items/" + itemName + "/size"));
    }

    @Override
    public String queryMultipleValueDatasetItemAsString(String datasetId, String itemName, int startRow, int maxResults) throws Exception {
        return retrieveString("/sets/" + datasetId + "/items/" + itemName + "/rows/" + startRow + "/" + maxResults);
    }

    @Override
    public String getMultipleValueDatasetItemRowAsString(String datasetId, String itemName, long rowId) throws Exception {
        return retrieveString("/sets/" + datasetId + "/items/" + itemName + "/row/" + rowId);
    }
    
    public JSONArray queryMultipleValueDatasetItemAsJson(String datasetId, String itemName, int startRow, int maxResults) throws Exception {
        JSONObject json = new JSONObject(queryMultipleValueDatasetItemAsString(datasetId, itemName, startRow, maxResults));
        return json.getJSONArray("data");
    }
    
    @Override
    public String queryMultipleValueDatasetItemAsString(String datasetId, String itemName, int startRow, int maxResults, EscDatasetKeyList keys) throws Exception {
        JSONObject json = postJsonRetrieveJson("/sets/" + datasetId + "/items/" + itemName + "/rows/" + startRow + "/" + maxResults, keys.toJsonObject());
        return json.toString();
    }
    
    public JSONArray queryMultipleValueDatasetItemAsJson(String datasetId, String itemName, int startRow, int maxResults, EscDatasetKeyList keys) throws Exception {
        JSONObject json = new JSONObject(queryMultipleValueDatasetItemAsString(datasetId, itemName, startRow, maxResults, keys));
        return json.getJSONArray("data");
    }

    @Override
    public EscDataset createDataset(String name) throws Exception {
        return new EscDataset(postTextRetrieveJson("/new", name));
    }

    @Override
    public void deleteDataset(String id) throws Exception {
        deleteResource("/sets/" + id);
    }

    @Override
    public EscDataset updateDataset(EscDataset ds) throws Exception {
        return new EscDataset(postJsonRetrieveJson("/sets/" + ds.getId(), ds.toJsonObject()));
    }

    @Override
    public EscDatasetItem[] aggregateDatasetItems(String[] datasetIds) throws Exception {
        JSONArray idList = new JSONArray();
        for(String s : datasetIds){
            idList.put(s);
        }
        JSONArray results = postJsonArrayRetrieveJsonArray("/aggregatedsets", idList);
        EscDatasetItem[] items = new EscDatasetItem[results.length()];
        for(int i=0;i<items.length;i++){
            items[i] = new EscDatasetItem(results.getJSONObject(i));
        }
        return items;        
    }
}