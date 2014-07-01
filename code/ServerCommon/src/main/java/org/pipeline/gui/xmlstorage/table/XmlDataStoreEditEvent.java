/**
 * e-Science Central
 * Copyright (C) 2008-2013 School of Computing Science, Newcastle University
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package org.pipeline.gui.xmlstorage.table;

import org.pipeline.core.xmlstorage.XmlDataStore;

/**
 * This event is triggered when an XmlDataStore is modified by the 
 * editor panel
 * @author  hugo
 */
public class XmlDataStoreEditEvent {
    /** Object being edited */
	private XmlDataStore store = null;
	
    /** Creates a new instance of XmlDataStoreEditEvent */
    public XmlDataStoreEditEvent(XmlDataStore store) {
        this.store = store;
    }
    
    /** Return the data store */
    public XmlDataStore getDataStore(){
        return store;
    }
}