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

import org.pipeline.core.xmlstorage.xmldatatypes.XmlStorableDataObject;

import javax.swing.*;
import java.awt.*;

/**
 * This object provides an external editor for an XmlStorableDataObject.
 * 
 * @author nhgh
 */
public abstract class ExternalXmlDataObjectEditor {
    
    /** Create an editor dialog box */
    public ExternalXmlDataObjectDialog getEditorDialog(Frame parent, XmlStorableDataObject dataObject){
        XmlDataObjectEditorPanel editor = getEditorPanel();
        editor.setObject(dataObject);
        ExternalXmlDataObjectDialog dialog = new ExternalXmlDataObjectDialog(parent, true, editor);
        return dialog;
    }
    
    /** Get an editor panel */
    public abstract XmlDataObjectEditorPanel getEditorPanel();
        
    /** Editor panel interface */
    public abstract class XmlDataObjectEditorPanel extends JPanel {

        /** Object being edited */
        private XmlStorableDataObject dataObject;
        
        /** Set the object */
        public void setObject(XmlStorableDataObject dataObject) {
            this.dataObject = dataObject;
        }
        
        /** Get the object being edited */
        public XmlStorableDataObject getObject(){
            return dataObject;
        }
        
        /** Update the edited object */
        public abstract void updateObject();

        /** Cancel the edit */
        public abstract void terminateEdit();
    }
}