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

package org.pipeline.core.xmlstorage;

/**
 * This is the general purpose Exception thrown by the xmlstorage package
 * @author  hugo
 */
public class XmlStorageException extends Exception
{
    /**
     * Creates a new instance of <code>XmlStorageException</code> without detail message.
     */
    public XmlStorageException() {
    }


    /**
     * Constructs an instance of <code>XmlStorageException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public XmlStorageException(String msg) {
        super(msg);
    }


    public XmlStorageException(Throwable cause) {
        super(cause);
    }


    public XmlStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
