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
 * Classes that implement this interface can be stored to an XmlDataStore object. Note
 * the user is responsible for implementing the code within the storeObject and
 * retrieveObject methods
 * @author  hugo
 */
public interface XmlStorable {
    /** Save this object to an XmlDataStore */
    public XmlDataStore storeObject() throws XmlStorageException;
    
    /** Recreate this object from an XmlDataStore */
    public void recreateObject(XmlDataStore store) throws XmlStorageException;
}
