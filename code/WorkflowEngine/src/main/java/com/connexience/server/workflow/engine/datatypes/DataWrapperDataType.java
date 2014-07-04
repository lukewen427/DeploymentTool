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

package com.connexience.server.workflow.engine.datatypes;
import org.pipeline.core.drawing.*;

/**
 * This class describes the data type for a standard set of Data that
 * gets passed between blocks
 * @author hugo
 */
public class DataWrapperDataType extends DataType {
    
    /** Creates a new instance of DataWrapperDataType */
    public DataWrapperDataType() {
        super("data-wrapper", "Standard set of data", DataWrapper.class);
    }
}
