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
package com.connexience.server.api.external.jaxws.v1;

import com.connexience.api.model.DatasetInterface;
import com.connexience.api.model.EscDataset;
import com.connexience.api.model.EscDatasetItem;
import com.connexience.api.model.EscDatasetKeyList;
import com.connexience.server.ConnexienceException;
import com.connexience.server.api.external.helpers.DatasetHelper;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.security.Ticket;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * This clas provides the SOAP version of the Dataset API
 * @author hugo
 */
@WebService(serviceName = "datasetv1")
public class EscDatasetWs implements DatasetInterface {
    @Resource WebServiceContext jaxWsContext;
    
    private Ticket getTicket() throws ConnexienceException {    
        return EJBLocator.lookupTicketBean().createWebTicket(jaxWsContext.getUserPrincipal().getName());
    }    
    
    @WebMethod(operationName = "listDatasets")
    @Override
    public EscDataset[] listDatasets() throws Exception {
        return new DatasetHelper(getTicket()).listDatasets();
    }

    @WebMethod(operationName="getNamedDataset")
    @Override
    public EscDataset getNamedDataset(String name) throws Exception {
        return new DatasetHelper(getTicket()).getNamedDataset(name);
    }

    @WebMethod(operationName="getDataset")
    @Override
    public EscDataset getDataset(String id) throws Exception {
        return new DatasetHelper(getTicket()).getDataset(id);
    }
    
    @WebMethod(operationName="createDataset")
    @Override
    public EscDataset createDataset(String name) throws Exception {
        return new DatasetHelper(getTicket()).createDataset(name);
    }

    @WebMethod(operationName="deleteDataset")
    @Override
    public void deleteDataset(String id) throws Exception {
        new DatasetHelper(getTicket()).deleteDataset(id);
    }

    @WebMethod(operationName="updateDataet")
    @Override
    public EscDataset updateDataset(EscDataset ds) throws Exception {
        return new DatasetHelper(getTicket()).updateDataset(ds);
    }

    @WebMethod(operationName="resetDataset")
    @Override
    public void resetDataset(String id) throws Exception {
        new DatasetHelper(getTicket()).resetDataset(id);
    }

    @WebMethod(operationName = "listDatasetContents")
    @Override
    public EscDatasetItem[] listDatasetContents(String id) throws Exception {
        return new DatasetHelper(getTicket()).listDatasetContents(id);
    }

    @WebMethod(operationName = "addItemToDataset")
    @Override
    public EscDatasetItem addItemToDataset(String id, EscDatasetItem item) throws Exception {
        return new DatasetHelper(getTicket()).addItemToDataset(id, item);
    }

    @WebMethod(operationName = "updateMultipleValueDatasetItem")
    @Override
    public String updateMultipleValueDatasetItem(String datasetId, String itemName, long rowId, String jsonData) throws Exception {
        return new DatasetHelper(getTicket()).updateMultipleValueDatasetItem(datasetId, itemName, rowId, jsonData);
    }
    
    @WebMethod(operationName = "deleteItemFromDataset")
    @Override
    public void deleteItemFromDataset(String datasetId, String itemName) throws Exception {
        new DatasetHelper(getTicket()).deleteItemFromDataset(datasetId, itemName);
    }

    @WebMethod(operationName = "appendToMultipleValueDatasetItem")
    @Override
    public String appendToMultipleValueDatasetItem(String datasetId, String itemName, String jsonData) throws Exception {
        return new DatasetHelper(getTicket()).appendToMultipleValueDatasetItem(datasetId, itemName, jsonData);
    }

    @WebMethod(operationName = "updateSingleValueDatasetItem")
    @Override
    public String updateSingleValueDatasetItem(String datasetId, String itemName, String jsonData) throws Exception {
        return new DatasetHelper(getTicket()).updateSingleValueDatasetItem(datasetId, itemName, jsonData);
    }

    @WebMethod(operationName = "querySingleValueDatasetItemAsString")
    @Override
    public String querySingleValueDatasetItemAsString(String datasetId, String itemName) throws Exception {
        return new DatasetHelper(getTicket()).querySingleValueDatasetItemAsString(datasetId, itemName);
    }

    @WebMethod(operationName = "getMultipleValueDatasetItemSize")
    @Override
    public Integer getMultipleValueDatasetItemSize(String datasetId, String itemName) throws Exception {
        return new DatasetHelper(getTicket()).getMultipleValueDatasetItemSize(datasetId, itemName);
    }

    @WebMethod(operationName = "queryMultipleValueDatasetItemAsString")
    @Override
    public String queryMultipleValueDatasetItemAsString(String datasetId, String itemName, int startRow, int maxResults) throws Exception {
        return new DatasetHelper(getTicket()).queryMultipleValueDatasetItemAsString(datasetId, itemName, startRow, maxResults);
    }

    @WebMethod(operationName = "queryMultipleValueDatasetItemAsStringWithFilters")
    @Override
    public String queryMultipleValueDatasetItemAsString(String datasetId, String itemName, int startRow, int maxResults, EscDatasetKeyList keys) throws Exception {
        return new DatasetHelper(getTicket()).queryMultipleValueDatasetItemAsString(datasetId, itemName, startRow, maxResults, keys);
    }

    @WebMethod(operationName = "aggregateDatasetItems")
    @Override
    public EscDatasetItem[] aggregateDatasetItems(String[] datasetIds) throws Exception {
        return new DatasetHelper(getTicket()).aggregateDatasetItems(datasetIds);
    }

    @WebMethod(operationName="removeMultipleValueDatasetItemRow")
    @Override
    public void removeMultipleValueDatasetItemRow(String datasetId, String itemName, long rowId) throws Exception {
        new DatasetHelper(getTicket()).removeMultipleValueDatasetItemRow(datasetId, itemName, rowId);
    }

    @WebMethod(operationName="getMultipleValueDatasetItemRowAsString")
    @Override
    public String getMultipleValueDatasetItemRowAsString(String datasetId, String itemName, long rowId) throws Exception {
        return new DatasetHelper(getTicket()).getMultipleValueDatasetItemRowAsString(datasetId, itemName, rowId);
    }
}