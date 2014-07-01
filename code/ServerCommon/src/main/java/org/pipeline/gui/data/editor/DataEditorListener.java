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

package org.pipeline.gui.data.editor;

import org.pipeline.core.data.Data;

/**
 * Classes that implement this interface listen to events from the data editor
 * @author hugo
 */
public interface DataEditorListener {
    /** A column has been added or removed */
    public void columnsChanged(Data data);
    
    /** A row has been added or removed */
    public void rowsChanged(Data data);
    
    /** A value in the data has changed */
    public void dataChanged(Data data);
}
