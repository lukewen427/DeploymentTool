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

package org.pipeline.core.drawing;

/**
 * This class acts as a redirector for the data held within a PortModel. It is
 * used to enable drawings to save intermediate data to disk instead of holding
 * it in memory during long running remote invocations.
 * @author hugo
 */
public interface PortDataSource {
    /** Get some data for a specific port */
    public TransferData getData(PortModel port) throws DrawingException;
    
    /** Set the data for a specific port */
    public void setData(PortModel port, TransferData data);
    
    /** Remove the data from this store */
    public void clearData(PortModel port);
    
    /** Does this source contain data for a port */
    public boolean containsData(PortModel port) throws DrawingException;
}