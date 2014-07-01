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
 * This Exception is thrown by classes in the Drawing package.
 * @author  hugo
 */
public class DrawingException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>DrawingException</code> without detail message.
     */
    public DrawingException() {
    }
    
    
    /**
     * Constructs an instance of <code>DrawingException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DrawingException(String msg) {
        super(msg);
    }

    public DrawingException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    
}
