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
 * Classes that implement this interface can be persisted automatically
 * using introspection and the xmlstorage package. All objects contained
 * within this object must also be XmlStorable or XmlAutoStorable.
 *
 * This interface has no methods, but classes that implement it have
 * to adhere to the following rules:
 *
 * 1) There must be a blank constructor
 * 2) All the instance variables must be supported by the xmlstorage
 *    package, either natively, or using user added xmldatatypes,
 *    or implement either the XmlStorage or XmlAutoStorable
 *    interfaces.
 *
 * @author  hugo
 */
public interface XmlAutoStorable {
}
