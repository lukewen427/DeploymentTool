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
package com.connexience.server.ejb.datasets;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.datasets.Dataset;
import com.connexience.server.model.datasets.DatasetItem;
import com.connexience.server.model.datasets.DatasetQuery;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.util.JSONContainer;

import javax.ejb.Remote;
import java.util.List;

/**
 * This interface defines the behaviour of the datasets management bean
 * @author hugo
 */
@Remote
public interface DatasetsRemote {
    /** List datasets owned by a user */
    public List listDatasets(Ticket ticket, boolean includeSystem) throws ConnexienceException;
    
    /** Get a dataset owned by a user by name */
    public Dataset getUserDatasetByName(Ticket ticket, String name) throws ConnexienceException;
    
    /** Get a dataset owned by a user by name and user id */
    public Dataset getUserDatasetByName(Ticket ticket, String userId, String datasetName) throws ConnexienceException;
    
    /** Get a datasets by ID */
    public Dataset getDataset(Ticket ticket, String id) throws ConnexienceException;
    
    /** Save a datasets */
    public Dataset saveDataset(Ticket ticket, Dataset dashboard) throws ConnexienceException;
    
    /** Delete a datasets */
    public void removeDataset(Ticket ticket, String id) throws ConnexienceException;
    
    /** Remove a datasets item */
    public void removeDatasetItem(Ticket ticket, long id) throws ConnexienceException;
    
    /** Reset a datasets */
    public Dataset resetDataset(Ticket ticket, String id) throws ConnexienceException;
    
    /** Reset a single item in a data set */
    public void resetDatasetItem(Ticket ticket, long id) throws ConnexienceException;
            
    /** Get the items for a datasets */
    public List getDatasetItems(Ticket ticket, String id) throws ConnexienceException;
    
    /** Get an aggregated list of dataset items for a set of datasets */
    public List aggregateDatasetItems(Ticket ticket, List<String>datasetIds) throws ConnexienceException;
    
    /** Get the size of a multi row item */
    public int getMultipleValueItemSize(Ticket ticket, String datasetId, String name) throws ConnexienceException;
    
    /** Query a multiple item value between a range */
    public JSONContainer queryMultipleValueItem(Ticket ticket, String datasetId, String name, int startIndex, int maxResults) throws ConnexienceException;
    
    /** Query a multiple item value between a range */
    public JSONContainer queryMultipleValueItem(Ticket ticket, String datasetId, String name, int startIndex, int maxResults, String[] keys) throws ConnexienceException;

    /** Do a query on an item */
    public JSONContainer performQuery(Ticket ticket, DatasetQuery query) throws ConnexienceException;
    
    /** Get a named item for a datasets */
    public DatasetItem getDatasetItem(Ticket ticket, String datasetId, String name) throws ConnexienceException;
    
    /** Get an item by ID for a dashboard */
    public DatasetItem getDatasetItem(Ticket ticket, long id) throws ConnexienceException;
    
    /** Save a dashboard item */
    public DatasetItem saveDatasetItem(Ticket ticket, DatasetItem item) throws ConnexienceException;
    
    /** Set a dashboard item value */
    public DatasetItem setDatasetItemValue(Ticket ticket, long itemId, Object value) throws ConnexienceException;
    
    /** Update a dashboard item with a value */
    public DatasetItem updateDatasetItemWithValue(Ticket ticket, long itemId, Object value) throws ConnexienceException;
    
    /** Update an existing multi-row item */
    public Object updateExistingMultipleValueItemRow(Ticket ticket, String datasetId, String name, long rowId, Object value) throws ConnexienceException;
    
    /** Remove an existing value from a multi-row item */
    public void removeExistingMultipleValueItemRow(Ticket ticket, String datasetId, String itemName, long rowId) throws ConnexienceException;
    
    /** Get a multiple value row froma multi-row item */
    public JSONContainer getMultipleValueDataRow(Ticket ticket, String datasetId, String itemName, long rowId) throws ConnexienceException;
    
    /** Do a quick update without checking security */
    public void quickUpdateDatasetItemWithValue(Ticket ticket, DatasetItem item, Object value) throws ConnexienceException;
    
    /** Get value(s) for a dashboard item */
    public Object getDatasetItemValue(Ticket ticket, String dashboardId, String name) throws ConnexienceException;
    
    /** Get values for a dashboard item */
    public Object getDatasetItemValue(Ticket ticket, DatasetItem item) throws ConnexienceException;
}