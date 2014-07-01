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
package com.connexience.server.model.datasets.items;

import com.connexience.server.model.datasets.DatasetItem;

/**
 * This item is used to pass a dataset id and name around between workflow blocks. It
 * represents a reference to a dataset item and doesn't do anything by itself.
 * @author hugo
 */
public class DummyDatasetItem extends DatasetItem {

    @Override
    public boolean isMultipleItem() {
        return false;
    }

    @Override
    public String getTypeLabel() {
        return "DummyItem";
    }

    @Override
    public void register() {
        
    }
    
}
