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

package org.pipeline.core.data.source;

import org.pipeline.core.data.Data;

/**
 * This interface extends the simple data source object to provide a multiple
 * output data source, which could be a drawing.
 * @author nhgh
 */
public interface MultiDataSource extends DataSource {
    /** Get the names of the outputs */
    public String[] getOutputNames();
    
    /** Get a named data output */
    public Data getData(String name);
    
    /** Is a named set of data valid */
    public boolean dataValid(String name);
}
