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

import com.connexience.server.ConnexienceException;

/**
 * This class represents an item in the dashboard catalog.
 * @author hugo
 */
public class DatasetCatalogItem {
    private Class<?> itemClass;
    private String id;
    private String label;
    private String description;

    public DatasetCatalogItem(Class<?> itemClass, String id, String label, String description) {
        this.itemClass = itemClass;
        this.id = id;
        this.label = label;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public Class<?> getItemClass() {
        return itemClass;
    }

    public String getLabel() {
        return label;
    }

    public DatasetItem createItem(String name, String dashboardId) throws ConnexienceException {
        try {
            DatasetItem item = (DatasetItem)itemClass.newInstance();
            item.setName(name);
            item.setDatasetId(dashboardId);
            return item;
        } catch (Exception e){
            throw new ConnexienceException("Error creating dashboard item: " + e.getMessage(), e);
        }
    }
}