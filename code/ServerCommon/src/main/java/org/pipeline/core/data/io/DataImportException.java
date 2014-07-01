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

package org.pipeline.core.data.io;

/**
 * This Exception is thrown by data import operations
 * @author hugo
 */
public class DataImportException extends Exception {
    
    /** Creates a new instance of DataImportException */
    public DataImportException() {
        super();
    }
    
    /** Creates a new instance of DataImportException */
    public DataImportException(String msg) {
        super(msg);
    }

    DataImportException(String msg, Throwable cause){
        super(msg, cause);
    }
}
