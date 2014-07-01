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

/**
 * Contains miscellaneous utility methods that are used by the XmlAutoPersist code.
 * @author  hugo
 */
public abstract class XmlAutoPersistUtilities {
    /** Is an object XmlAutoStorable */
    public static boolean isAutoStorable(Object object){
        try {
            XmlAutoStorable o = (XmlAutoStorable)object;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
