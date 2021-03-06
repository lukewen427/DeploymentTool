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

package org.pipeline.core.drawing.model;

import org.pipeline.core.drawing.DrawingException;
import org.pipeline.core.drawing.PortDataSource;
import org.pipeline.core.drawing.PortModel;
import org.pipeline.core.drawing.TransferData;

/**
 * This data source keeps a reference to data in memory.
 * @author hugo
 */
public class DefaultMemoryPortDataSource implements PortDataSource {
    /** Data object */
    private TransferData data = null;
    
    public TransferData getData(PortModel port) throws DrawingException {
        return data;
    }

    public void clearData(PortModel port) {
        data = null;
    }

    public void setData(PortModel port, TransferData data) {
        this.data = data;
    }

    public boolean containsData(PortModel port) throws DrawingException {
        if(data!=null){
            return true;
        } else {
            return false;
        }
    }
    
    
}