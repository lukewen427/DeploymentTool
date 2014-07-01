/* =================================================================
 *                     conneXience Data Pipeline
 * =================================================================
 *
 * Copyright 2006 Hugo Hiden and Adrian Conlin
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.pipeline.core.data.manipulation;

import org.pipeline.core.data.DataException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class contains a set of column references that can be used to construct
 * a ColumnPickerCollection. This collection can be set to pick based on column
 * name or column index.
 * @author hugo
 */
public class ColumnReferenceCollection implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<ColumnReference> references = new ArrayList<>();

    public ColumnReferenceCollection() {
    }
    
    public void addReference(ColumnReference reference){
        references.add(reference);
    }
    
    public ColumnPickerCollection createIndexBasedPickers() throws DataException {
        ColumnPickerCollection pickers = new ColumnPickerCollection();
        for(ColumnReference r : references){
            pickers.addColumnPicker(new ColumnPicker("#" + r.getIndex()));
        }
        return pickers;
    }
    
    public ColumnPickerCollection createNameBasedPickers() throws DataException {
        ColumnPickerCollection pickers = new ColumnPickerCollection();
        for(ColumnReference r : references){
            pickers.addColumnPicker(new ColumnPicker(r.getName()));
        }
        return pickers;
    }
}