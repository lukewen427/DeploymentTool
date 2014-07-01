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

package org.pipeline.core.xmlstorage.autopersist;

import org.pipeline.core.xmlstorage.XmlAutoStorable;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * Automatically persists XmlStorable objects using reflection
 * @author  hugo
 */
public class XmlAutoPersister {
    /** Object being persisted */
    private XmlAutoStorable object = null;
    
    /** Creates a new instance of XmlAutoPersister */
    public XmlAutoPersister(XmlAutoStorable object) {
        this.object = object;
    }
    
    /** Persist object */
    public XmlDataStore persist() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore();
        try {
            new XmlStorablePersister(store, this.object).persist();
        } catch (Exception e){
            throw new XmlStorageException(e.getMessage());
        }
        return store;
    }
}
