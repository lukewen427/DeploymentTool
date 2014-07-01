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

import java.util.Hashtable;

/**
 * This object extends a Hashtable to allow it to be created
 * with a pre-initialised key-value set
 * @author  hugo
 */
public class IntialisedHashtable extends Hashtable {
    
    /** Creates a new instance of IntialisedHashtable */
    public IntialisedHashtable(Object[] keys, Object[] values){
        super();
        if(keys.length == values.length){
            for(int i=0;i<keys.length;i++){
                put(keys[i], values[i]);
            }
        }
    }
}
