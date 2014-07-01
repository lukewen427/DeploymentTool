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

package org.pipeline.gui.xmlstorage;

import org.pipeline.core.xmlstorage.XmlDataObject;

/**
 * Classes that implement this interface can act as XmlDataObject editors
 * @author hugo
 */
public interface XmlDataObjectEditor {
    /** Set the object being edited */
    public void setObject(XmlDataObject object);
    
    /** Update the object */
    public void updateValue();
 
    /** Reset the edited value back to its original value */
    public void resetValue();
    
    /** Set the width of the caption */
    public void setCaptionWidth(int captionWidth);
    
    /** Set the parent editor window */
    public void setParent(XmlDataStoreEditor parent);
    
    /** The helper data for this object has changed */
    public void helperDataChanged();
}
