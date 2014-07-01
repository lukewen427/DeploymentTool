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

import org.pipeline.core.data.manipulation.ColumnPicker;
import org.pipeline.core.xmlstorage.XmlDataObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.xmldatatypes.*;
import org.pipeline.gui.xmlstorage.xmldatatypes.*;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This class provides an editor for an XmlDataStore
 * @author  hugo
 */
public class XmlDataStoreEditor extends javax.swing.JPanel {
    /** Store being edited */
    private XmlDataStore store = null;
    
    /** Hashtable of editors */
    private Hashtable editors = new Hashtable();
    
    /** Hashtable of helper objects for the various data objects. These are indexed by name
     * in the same way that the editors are. */
    private Hashtable helperObjects = new Hashtable();
    
    /** Creates new form XmlDataStoreEditor */
    public XmlDataStoreEditor() {
        initComponents();
    }
    
    /** Set the store */
    public void setStore(XmlDataStore store){
        this.store = store;
        createEditors();
    }
    
    /** Create the editors */
    private void createEditors(){
        if(store!=null){
            editors.clear();
            javax.swing.JPanel viewerPanel = new javax.swing.JPanel();
            viewerPanel.setLayout(new java.awt.BorderLayout());
            javax.swing.JPanel topPanel = viewerPanel;
                    
            Enumeration values = store.elements();
            XmlDataObject value;
            javax.swing.JPanel newPanel = null;
            javax.swing.JPanel editorPanel = null;
            XmlDataObjectEditor editor;
            
            while(values.hasMoreElements()){
                value = (XmlDataObject)values.nextElement();
                
                // Create the correct editor component
                newPanel = new javax.swing.JPanel();
                newPanel.setLayout(new java.awt.BorderLayout());
                
                if(value instanceof XmlStringDataObject){
                    editorPanel = new XmlStringDataObjectEditor();
                    
                } else if(value instanceof XmlBooleanDataObject){
                    editorPanel = new XmlBooleanDataObjectEditor();
                    
                } else if(value instanceof XmlColorDataObject){
                    editorPanel = new XmlColorDataObjectEditor();
                    
                } else if(value instanceof XmlDateDataObject){
                    editorPanel = new XmlDateDataObjectEditor();
                    
                } else if(value instanceof XmlDoubleDataObject){
                    editorPanel = new XmlNumericalDataObjectEditor();
                            
                } else if(value instanceof XmlLongDataObject){
                    editorPanel = new XmlNumericalDataObjectEditor();
                    
                } else if(value instanceof XmlIntegerDataObject){
                    editorPanel = new XmlNumericalDataObjectEditor();
                 
                } else if(value instanceof XmlXmlDataObject){
                    editorPanel = new XmlXmlDataObjectEditor();
                    
                } else if(value instanceof XmlFileDataObject){
                    editorPanel = new XmlFileDataObjectEditor();
                    
                } else if(value instanceof XmlStorableDataObject){
                    // Check stored object to see if an editor can be created for it
                    XmlStorableDataObject xmlStorable = (XmlStorableDataObject)value;
                    if(xmlStorable.getValue() instanceof ColumnPicker){
                        // Column picker object
                        editorPanel = new XmlColumnPickerDataObjectEditor();
                    }
                }
                
                if(editorPanel!=null){
                    ((XmlDataObjectEditor)editorPanel).setParent(this);
                    ((XmlDataObjectEditor)editorPanel).setObject(value);
                    newPanel.add(editorPanel, java.awt.BorderLayout.CENTER);                    
                    editors.put(value.getName(), editorPanel);
                }
                
                // Add to panel 
                if(newPanel!=null){
                    ((XmlDataObjectEditor)editorPanel).setCaptionWidth(100);
                    viewerPanel.add(newPanel, java.awt.BorderLayout.NORTH);
                    viewerPanel = newPanel;
                }
            }
            contentsScroller.setViewportView(topPanel);
        } else {
            editors.clear();
            contentsScroller.setViewportView(new javax.swing.JPanel());
        } 
    }
    
    /** Get the total height of the editors */
    public int getTotalEditorHeight(){
        Enumeration e = editors.elements();
        int size = 0;
        javax.swing.JPanel panel;
        
        while(e.hasMoreElements()){
            panel = (javax.swing.JPanel)e.nextElement();
            size = size + (int)panel.getPreferredSize().getHeight();            
        }
        return size;
    }
    
    /** Add a helper object for a named value */
    public void setHelperObject(String name, Object helper){
        if(helperObjects.containsKey(name)){
            helperObjects.remove(name);
        }
        
        helperObjects.put(name, helper);
        if(editors.containsKey(name)){
            ((XmlDataObjectEditor)editors.get(name)).helperDataChanged();
        }
    }
    
    /** Set all of the caption widths */
    public void setCaptionWidths(int width){
        Enumeration e = editors.elements();
        XmlDataObjectEditor editor;
        
        while(e.hasMoreElements()){        
            editor = (XmlDataObjectEditor)e.nextElement();
            editor.setCaptionWidth(width);
        }        
    }
    
    /** Get a helper object for a named value */
    public Object getHelperObject(String name){
        if(helperObjects.containsKey(name)){
            return helperObjects.get(name);
        } else {
            return null;
        }
    }
    
    /** Reset all values */
    public void resetValues(){
        Enumeration e = editors.elements();
        while(e.hasMoreElements()){
            ((XmlDataObjectEditor)e.nextElement()).resetValue();
        }
    }
    
    /** Save all changes */
    public void updateValues(){
        Enumeration e = editors.elements();
        while(e.hasMoreElements()){
            ((XmlDataObjectEditor)e.nextElement()).updateValue();
        }
    }    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        contentsScroller = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());

        add(contentsScroller, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    /** Save changes */
    /** Reset all values */    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane contentsScroller;
    // End of variables declaration//GEN-END:variables
    
}
