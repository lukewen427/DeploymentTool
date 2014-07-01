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
package com.connexience.server.api.external.jaxrs.v1;

import com.connexience.api.model.DatasetInterface;
import com.connexience.api.model.EscDataset;
import com.connexience.api.model.EscDatasetItem;
import com.connexience.api.model.EscDatasetKeyList;
import com.connexience.api.model.json.JSONObject;
import com.connexience.server.ConnexienceException;
import com.connexience.server.api.external.helpers.DatasetHelper;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.security.Ticket;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * This class provides a service that allows access to EscDatasets
 * @author hugo
 */
@Path("/public/rest/v1/dataset")
public class RestDatasetService implements DatasetInterface {
    @Context SecurityContext secContext;
    
    /** Create a ticket for the current security context */
    private Ticket getTicket() throws ConnexienceException {
        return EJBLocator.lookupTicketBean().createWebTicket(secContext.getUserPrincipal().getName());
    }
    
    @GET
    @Path("/list")
    @Produces("application/json")
    @Override
    public EscDataset[] listDatasets() throws Exception {
        return new DatasetHelper(getTicket()).listDatasets();
    }

    @GET
    @Path("/sets/{id}/reset")
    @Produces("text/plain")
    @Override
    public void resetDataset(@PathParam(value="id")String id) throws Exception {
        new DatasetHelper(getTicket()).resetDataset(id);
    }

    @GET
    @Path("/sets/{id}/items")
    @Produces("application/json")
    @Override
    public EscDatasetItem[] listDatasetContents(@PathParam(value="id")String id) throws Exception {
        return new DatasetHelper(getTicket()).listDatasetContents(id);
    }
    
    @GET
    @Path("/usersetsbyname/{name}")
    @Produces("application/json")
    @Override
    public EscDataset getNamedDataset(@PathParam(value="name")String name) throws Exception {
        return new DatasetHelper(getTicket()).getNamedDataset(name);
    }

    @GET
    @Path("/sets/{name}")
    @Produces("application/id")
    @Override    
    public EscDataset getDataset(@PathParam(value="id")String id) throws Exception {
        return new DatasetHelper(getTicket()).getDataset(id);
    }
    
    @POST
    @Path("/sets/{id}/items")
    @Produces("application/json")
    @Consumes("application/json")
    @Override
    public EscDatasetItem addItemToDataset(@PathParam(value="id")String id, EscDatasetItem item) throws Exception {
        return new DatasetHelper((getTicket())).addItemToDataset(id, item);
    }

    @POST
    @Path("/sets/{id}/ptitems")
    @Produces("application/json")
    @Consumes("text/plain")
    public EscDatasetItem addItemToDataset(@PathParam(value="id")String id, String itemText) throws Exception {
        JSONObject json = new JSONObject(itemText);
        EscDatasetItem item = new EscDatasetItem(json);
        return new DatasetHelper((getTicket())).addItemToDataset(id, item);
    }
    
    @DELETE
    @Path("/sets/{id}/items/{itemname}")
    @Override
    public void deleteItemFromDataset(@PathParam(value="id")String datasetId, @PathParam(value="itemname")String itemName) throws Exception {
        new DatasetHelper(getTicket()).deleteItemFromDataset(datasetId, itemName);
    }
    
    @POST
    @Path("/deletedatasetitem/{id}")
    @Consumes("text/plain")
    @Produces("application/json")
    public void deleteItemFromDatasetUsingPOST(@PathParam(value="id")String datasetId, String itemName) throws Exception {
        new DatasetHelper(getTicket()).deleteItemFromDataset(datasetId, itemName);
    }
    
    @POST
    @Path("/sets/{id}/items/{itemname}/json")
    @Consumes("text/plain")
    @Produces("text/plain")
    @Override
    public String appendToMultipleValueDatasetItem(@PathParam(value="id")String datasetId, @PathParam(value="itemname")String itemName, String jsonData) throws Exception {
        return new DatasetHelper(getTicket()).appendToMultipleValueDatasetItem(datasetId, itemName, jsonData);
    }

    @POST
    @Path("/sets/{id}/items/{itemname}/row/{rowId}")
    @Override
    public String updateMultipleValueDatasetItem(@PathParam(value="id")String datasetId, @PathParam(value="itemname")String itemName, @PathParam(value="rowId")long rowId, String jsonData) throws Exception {
        return new DatasetHelper(getTicket()).updateMultipleValueDatasetItem(datasetId, itemName, rowId, jsonData);
    }
    
    @POST
    @Path("/sets/{id}/singleitems/{itemname}/row")
    @Consumes("application/json")
    @Produces("text/plain")
    @Override
    public String updateSingleValueDatasetItem(@PathParam(value="id")String datasetId, @PathParam(value="itemname")String itemName, String jsonData) throws Exception {
        return new DatasetHelper(getTicket()).updateSingleValueDatasetItem(datasetId, itemName, jsonData);
    }

    @GET
    @Path("/sets/{id}/singleitems/{itemname}/row")
    @Produces("application/json")
    @Override
    public String querySingleValueDatasetItemAsString(@PathParam(value="id")String datasetId, @PathParam(value="itemname")String itemName) throws Exception {
        return new DatasetHelper(getTicket()).querySingleValueDatasetItemAsString(datasetId, itemName);
    }

    @GET
    @Path("/sets/{id}/items/{itemname}/size")
    @Produces("text/plain")
    @Override
    public Integer getMultipleValueDatasetItemSize(@PathParam(value="id")String datasetId, @PathParam(value="itemname")String itemName) throws Exception {
        return new DatasetHelper(getTicket()).getMultipleValueDatasetItemSize(datasetId, itemName);
    }
    
    @GET
    @Path("/sets/{id}/items/{itemname}/rows/{startrow}/{maxresults}")
    @Produces("application/json")
    @Override
    public String queryMultipleValueDatasetItemAsString(@PathParam(value="id")String datasetId, @PathParam(value="itemname")String itemName, @PathParam(value="startrow")int startRow, @PathParam(value="maxresults")int maxResults) throws Exception {
        return new DatasetHelper(getTicket()).queryMultipleValueDatasetItemAsString(datasetId, itemName, startRow, maxResults);
    }    

    @POST
    @Path("/sets/{id}/items/{itemname}/rows/{startrow}/{maxresults}")
    @Consumes("application/json")
    @Produces("application/json")
    @Override
    public String queryMultipleValueDatasetItemAsString(@PathParam(value="id")String datasetId, @PathParam(value="itemname")String itemName, @PathParam(value="startrow")int startRow, @PathParam(value="maxresults")int maxResults, EscDatasetKeyList keys) throws Exception {
        String results = new DatasetHelper(getTicket()).queryMultipleValueDatasetItemAsString(datasetId, itemName, startRow, maxResults, keys);
        return results;
    }

    @POST
    @Path("/new")
    @Consumes("text/plain")
    @Produces("application/json")
    @Override
    public EscDataset createDataset(String name) throws Exception {
        return new DatasetHelper(getTicket()).createDataset(name);
    }    

    @DELETE
    @Path("/sets/{id}")
    @Override
    public void deleteDataset(@PathParam(value="id")String id) throws Exception {
        new DatasetHelper(getTicket()).deleteDataset(id);
    }
    
    @POST
    @Path("/deletedataset")
    @Consumes("text/plain")
    public void deleteDatasetUsingPOST(String id) throws Exception {
        new DatasetHelper(getTicket()).deleteDataset(id);
    }
    
    @POST
    @Path("/sets/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    @Override
    public EscDataset updateDataset(EscDataset ds) throws Exception {
        return new DatasetHelper(getTicket()).updateDataset(ds);
    }
    
    @POST
    @Path("/ptsets/{id}")
    @Consumes("text/plain")
    @Produces("application/json")
    public EscDataset updateDataset(String dsText) throws Exception {
        JSONObject json = new JSONObject(dsText);
        EscDataset ds = new EscDataset(json);
        return new DatasetHelper(getTicket()).updateDataset(ds);
    }    

    @POST
    @Path("/aggregatedsets")
    @Consumes("application/json")
    @Produces("application/json")
    @Override
    public EscDatasetItem[] aggregateDatasetItems(String[] datasetIds) throws Exception {
        return new DatasetHelper(getTicket()).aggregateDatasetItems(datasetIds);
    }

    @DELETE
    @Path("/sets/{datasetId}/items/{itemName}/row/{rowId}")
    @Override
    public void removeMultipleValueDatasetItemRow(@PathParam(value="datasetId")String datasetId, @PathParam(value="itemName")String itemName, @PathParam(value="rowId")long rowId) throws Exception {
        new DatasetHelper(getTicket()).removeMultipleValueDatasetItemRow(datasetId, itemName, rowId);
    }

    @GET
    @Path("/sets/{datasetId}/items/{itemName}/row/{rowId}")
    @Produces("application/json")
    @Override
    public String getMultipleValueDatasetItemRowAsString(@PathParam(value="datasetId")String datasetId, @PathParam(value="itemName")String itemName, @PathParam(value="rowId")long rowId) throws Exception {
        return new DatasetHelper(getTicket()).getMultipleValueDatasetItemRowAsString(datasetId, itemName, rowId);
    }
}
