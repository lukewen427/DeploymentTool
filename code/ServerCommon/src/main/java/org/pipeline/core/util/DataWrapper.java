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

package org.pipeline.core.util;

import org.pipeline.core.data.Data;
import org.pipeline.core.drawing.DataType;
import org.pipeline.core.drawing.DrawingException;
import org.pipeline.core.drawing.TransferData;

/**
 * This class wraps up a Data object in a TransferData interface.
 * @author hugo
 */
public class DataWrapper implements TransferData {
    /** Data being passed around */
    private Data data = null;
    
    /** Static data type */
    public static final DataType DATA_WRAPPER_TYPE = new DataWrapperDataType();
    
    /** Creates a new instance of DataWrapper from a Data object */
    public DataWrapper(Data data) {
        this.data = data;            
    }

    /** Get the data payload */
    public Object getPayload() {
        return data;
    }

    /** Get a copy of this data */
    public TransferData getCopy() throws DrawingException {
        try {
            DataWrapper wrapper = new DataWrapper(data.getCopy());
            return wrapper;
            
        } catch (Exception e){
            // TODO: Internationalise
            throw new DrawingException("Cannot create data copy");
        }
    }
}
