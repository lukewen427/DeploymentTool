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
package com.connexience.api.model;

/**
 * This interface defines the functions provided by the Dataset API that is used
 * to update and query Datasets stored within the server.
 * @author hugo
 */
public interface DatasetInterface {
    /** List the Datasets available to the user */
    EscDataset[] listDatasets() throws Exception; 
    
    /** Get a named dataset for the current user */
    EscDataset getNamedDataset(String name) throws Exception;
    
    /** Get a dataset by ID */
    EscDataset getDataset(String id) throws Exception;
    
    /** Create a new dataset */
    EscDataset createDataset(String name) throws Exception;

    /** Delete a dataset */
    void deleteDataset(String id) throws Exception;
    
    /** Save changes to a dataset */
    EscDataset updateDataset(EscDataset ds) throws Exception;
    
    /** Reset a Dataset to remove all of the data contained in it */
    void resetDataset(String id) throws Exception;
    
    /** List all of the items in a dataset */
    EscDatasetItem[] listDatasetContents(String id) throws Exception;
    
    /** Add an item to a dataset */
    EscDatasetItem addItemToDataset(String id, EscDatasetItem item) throws Exception;
    
    /** Delete an item from a dataset */
    void deleteItemFromDataset(String datasetId, String itemName) throws Exception;

    /** Change a piece of JSON data in a multi-row dataset item. */
    String updateMultipleValueDatasetItem(String datasetId, String itemName, long rowId, String jsonData) throws Exception;
    
    /** Remove a multiple value item row */
    void removeMultipleValueDatasetItemRow(String datasetId, String itemName, long rowId) throws Exception;
    
    /** Get a multiple value item row */
    String getMultipleValueDatasetItemRowAsString(String datasetId, String itemName, long rowId) throws Exception;
            
    /** Add some JSON data to a multi-row dataset item */
    String appendToMultipleValueDatasetItem(String datasetId, String itemName, String jsonData) throws Exception;

    /** Update a single-row dataset item with some JSON data */
    String updateSingleValueDatasetItem(String datasetId, String itemName, String jsonData) throws Exception;
    
    /** Query a single value as text */
    String querySingleValueDatasetItemAsString(String datasetId, String itemName) throws Exception;

    /** Get the number of value in a multi-value item */
    Integer getMultipleValueDatasetItemSize(String datasetId, String itemName) throws Exception;   
    
    /** Query a multi-value item using start and page size */
    String queryMultipleValueDatasetItemAsString(String datasetId, String itemName, int startRow, int maxResults) throws Exception;
    
    /** Query a multi-value item using start and page size with a list of keys to return */
    String queryMultipleValueDatasetItemAsString(String datasetId, String itemName, int startRow, int maxResults, EscDatasetKeyList keys) throws Exception;
    
    /** Aggregate a list of dataset items */
    EscDatasetItem[] aggregateDatasetItems(String[] datasetIds) throws Exception;
}