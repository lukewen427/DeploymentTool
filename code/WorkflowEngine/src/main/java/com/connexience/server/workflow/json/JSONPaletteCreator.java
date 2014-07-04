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
package com.connexience.server.workflow.json;

import com.connexience.server.model.security.*;
import com.connexience.server.model.workflow.*;
import com.connexience.server.ejb.util.*;
import org.json.*;

import java.util.*;

/**
 * This class creates a JSON object representing the drawing tools palette.
 *
 * @author nhgh
 */
public class JSONPaletteCreator {
	/**
	 * Security ticket for user generating the palette
	 */
	private Ticket ticket;

	public JSONPaletteCreator(Ticket ticket) {
		this.ticket = ticket;
	}

	/**
	 * Generate the pallete
	 */
	@Deprecated
	public JSONObject getPalette() throws Exception {
		Hashtable<String, JSONObject> paletteMap = new Hashtable<>();

		JSONObject categoryJson;
		JSONObject serviceJson;
		JSONArray serviceArray;

		String category;
		DynamicWorkflowService service;

		List<?> services = WorkflowEJBLocator.lookupWorkflowManagementBean().listDynamicWorkflowServices(ticket);
		for (int i = 0; i < services.size(); i++) {
			service = (DynamicWorkflowService) services.get(i);
			category = service.getCategory();
			if (category == null) {
				category = "My Services";
			}

			// Get or create the category and service array
			if (paletteMap.containsKey(category)) {
				categoryJson = paletteMap.get(category);
				serviceArray = categoryJson.getJSONArray("serviceArray");
			} else {
				categoryJson = new JSONObject();
				serviceArray = new JSONArray();
				categoryJson.put("name", category);
				categoryJson.put("serviceArray", serviceArray);
				paletteMap.put(category, categoryJson);
			}

			serviceJson = new JSONObject();
			serviceJson.put("category", service.getCategory());
			serviceJson.put("name", service.getName());
			serviceJson.put("description", service.getDescription());
			serviceJson.put("serviceId", service.getId());
			serviceJson.put("creatorId", service.getCreatorId());
			if (service.getProjectFileId() != null && !service.getProjectFileId().isEmpty()) {
				serviceJson.put("projectFileId", service.getProjectFileId());
			}
			serviceArray.put(serviceJson);
		}

		JSONObject paletteJson = new JSONObject();
		JSONArray categoryArray = new JSONArray();
		Enumeration<JSONObject> categories = paletteMap.elements();

		List<JSONObject> catList = Collections.list(categories);
		Collections.sort(catList, new Comparator<JSONObject>() {
							 @Override
							 public int compare(JSONObject o1, JSONObject o2) {
								 try {
									 return (o1.get("name").toString().compareTo(o2.get("name").toString()));
								 } catch (JSONException e) {
									 e.printStackTrace();
									 return 0;
								 }
							 }
						 }
		);

		int count = 0;
		for (JSONObject catJson : catList) {
			categoryArray.put(catJson);
			count++;
		}

		paletteJson.put("categoryArray", categoryArray);
		paletteJson.put("categoryCount", count);
		return paletteJson;
	}

	public JSONArray getHierarchicalPalette() throws Exception {
		JSONArray root = new JSONArray();

		List<?> services = WorkflowEJBLocator.lookupWorkflowManagementBean().listDynamicWorkflowServices(ticket);

		for (Object serviceAsObject : services) {
			final DynamicWorkflowService service = (DynamicWorkflowService) serviceAsObject;

			final String rawCategories = service.getCategory() == null ? "MISSING" : service.getCategory();
			final String[] categories = rawCategories.split(";");

			for (final String category : categories) {
				final String[] categoryPath = category.split("\\.");

				JSONObject parent = findOrCreateChild(root, categoryPath[0]);

				// Traverse down the way if we need
				for (int i = 1; i < categoryPath.length; i++) {
					parent = findOrCreateChild(parent.optJSONArray("children"), categoryPath[i]);
				}

				JSONObject block = new JSONObject();
				block.put("name", service.getName());
				block.put("category", rawCategories);
				block.put("description", service.getDescription());
				block.put("serviceId", service.getId());
				block.put("creatorId", service.getCreatorId());

				if (service.getProjectFileId() != null && !service.getProjectFileId().isEmpty()) {
					block.put("projectFileId", service.getProjectFileId());
				}

				parent.optJSONArray("blocks").put(block);
			}
		}

		return root;
	}

	// Find or Create a child object
	private JSONObject findOrCreateChild(final JSONArray list, final String needleName) throws JSONException {
		for (int i = 0; i < list.length(); i++) {
			if (list.optJSONObject(i) != null) {
				final JSONObject child = list.optJSONObject(i);

				if (needleName.equals(child.optString("name"))) {
					return list.optJSONObject(i);
				}
			}
		}

		// If we're here, we found no child, create one.
		final JSONObject child = new JSONObject();
		child.put("name", needleName);
		child.put("children", new JSONArray());
		child.put("blocks", new JSONArray());

		list.put(child);

		return child;
	}
}
