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
 * This interface defines an object that can act as a source of data
 * set objects. It is used mainly for workspace objects that can
 * supply data sets such as database connections.
 * @author hugo
 */
public interface DataSource {
    /** Add a data source listener */
    public void addDataSourceListener(DataSourceListener listener);
    
    /** Remove a data source listener */
    public void removeDataSourceListener(DataSourceListener listener);
    
    /** Is the current data valid */
    public boolean dataValid();
    
    /** Get the current data */
    public Data getData();
}
