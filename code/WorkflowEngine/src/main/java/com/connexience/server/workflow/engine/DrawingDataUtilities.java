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

package com.connexience.server.workflow.engine;

import com.connexience.server.workflow.engine.datatypes.*;
import org.pipeline.core.data.*;
import org.pipeline.core.drawing.*;
        
/**
 * This class provides utility methods for passing data in and out of blocks
 * wrapped up as TransferData objects
 * @author hugo
 */
public abstract class DrawingDataUtilities {
    /** Get a set of Data from an input port. A DrawingException is thrown if
     * the data is not of the correct type */
     public static Data getInputData(InputPortModel input) throws DrawingException {
         TransferData transfer = input.getData();
         if(transfer!=null){
             Object payload = transfer.getPayload();
             if(payload instanceof Data){
                 return (Data)payload;
             } else {
                 throw new DrawingException("No data transferred on input port");
             }

         } else {
             throw new DrawingException("No data available");
         }
     }
     
     /** Pass a set of data to an output port. This method wraps the data up in a DataWrapper
      * object so that it can be passed around the drawing. The data is always made read-only
      * when it is set as an output. This is so that a single copy can be passed to downstream
      * blocks without danger of changes being made which will corrupt the data for other blocks. */
     public static void setOutputData(OutputPortModel output, Data data) {
         data.setReadOnly(true);
         DataWrapper wrapper = new DataWrapper(data);
         output.setData(wrapper);
     }
}
