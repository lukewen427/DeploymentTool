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

import org.pipeline.core.xmlstorage.XmlDataObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.xmldatatypes.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This object is used to edit XmlDataObjects in a table
 * @author  hugo
 */
public class XmlDataObjectEditor extends DefaultCellEditor {   
    /** Parent editor window */
    private XmlDataStoreEditorTable parent = null;
    
    /** Editor - value hashtable mapping */
    private Hashtable valueMappings = new Hashtable();
    
    /** Creates a new instance of XmlDataObjectEditor */
    public XmlDataObjectEditor(XmlDataStoreEditorTable parent) {
        super(new JTextField());
        this.parent = parent;
        setClickCountToStart(1);
    }
    
    /** An edit has completed */
    private void notifyEditCompleted(){
        if(parent!=null){
            parent.editCompleted();
        } else {
            System.out.println("null parent in XmlDataObjectEditor");
        }
    }
       
    /** Terminate all in place edits */
    public void terminateInPlaceEdits(){
        Enumeration editors = valueMappings.keys();
        
        Object editorObject;
        XmlDataObject editedValue;
        
        while(editors.hasMoreElements()){
            editorObject = editors.nextElement();
            editedValue = (XmlDataObject)valueMappings.get(editorObject);
            
            if(editedValue instanceof XmlDoubleDataObject && editorObject instanceof JTextField){
                updateDouble((JTextField)editorObject);
                fireEditingStopped();
                
            } else if(editedValue instanceof XmlIntegerDataObject && editorObject instanceof JTextField){
                updateInt((JTextField)editorObject);
                fireEditingStopped();
                
            } else if(editedValue instanceof XmlLongDataObject && editorObject instanceof JTextField){
                updateLong((JTextField)editorObject);
                fireEditingStopped();
                
            } else if(editedValue instanceof XmlStringDataObject && editorObject instanceof JTextField){
                updateString((JTextField)editorObject);
                fireEditingStopped();
                
            } else if(editedValue instanceof XmlBooleanDataObject && editorObject instanceof  JComboBox){
                updateBoolean((JComboBox)editorObject);
                fireEditingStopped();
                
            } else {
                if(valueMappings.containsKey(editorObject)){
                    valueMappings.remove(editorObject);
                }
            }
        }
    }
    
    /** Get an editor component */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean bIsSelected, int iRow, int iColumn) {
        // Create an appropriate editor component       
        if(value instanceof XmlBooleanDataObject){
            // Combo box for Yes / No choice
            JComboBox yesNoCombo = new JComboBox(new String[]{"Yes","No"});
            if(((XmlBooleanDataObject)value).booleanValue()==true){
                yesNoCombo.setSelectedIndex(0);
            } else {
                yesNoCombo.setSelectedIndex(1);
            }
            
            // Add a mapping to the hashtable
            valueMappings.put(yesNoCombo, value);
            
            yesNoCombo.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    updateBoolean((JComboBox)e.getSource());
                }
            });
            
            yesNoCombo.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent arg0) {
                }

                public void focusLost(FocusEvent e) {
                    updateBoolean((JComboBox)e.getSource());
                }
            });
            return yesNoCombo;
            
        } else if(value instanceof XmlColorDataObject){
            // Show a color chooser dialog
            Color newColor = JColorChooser.showDialog(SwingUtilities.getRoot((JComponent)table), "Select colour", ((XmlColorDataObject)value).colorValue());
            if(newColor!=null){
                ((XmlColorDataObject)value).setColorValue(newColor);
                notifyEditCompleted();
            }
            fireEditingStopped();
            return null;
            
        } else if(value instanceof XmlDateDataObject){
            // Show a date chooser dialog
            /*
            Date initialDate = ((XmlDateDataObject)value).dateValue();
            TimeDateChooserDialog dialog = new TimeDateChooserDialog(initialDate, true);
            dialog.setVisible(true);
            if(dialog.dialogAccepted()){
                ((XmlDateDataObject)value).setDateValue(dialog.getDate());
                notifyEditCompleted();
            }
             */
            fireEditingStopped();
            return null;
            
        } else if(value instanceof XmlDoubleDataObject){
            // Set up a JTextField to edit a double value
            JTextField doubleEditor = new JTextField(Double.toString(((XmlDoubleDataObject)value).doubleValue()));
            doubleEditor.setBorder(BorderFactory.createEmptyBorder());
            doubleEditor.selectAll();
            
            // Add a mapping to the hashtable
            valueMappings.put(doubleEditor, value);

            // Add an action listener
            doubleEditor.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    updateDouble((JTextField)e.getSource());
                    fireEditingStopped();
                }
            });            
            
            // Add a focus listener
            doubleEditor.addFocusListener(new FocusListener(){
                public void focusLost(FocusEvent e){
                    updateDouble((JTextField)e.getSource());
                }
                
                public void focusGained(FocusEvent e) {
                }                    
            });
            
            return doubleEditor;
        
        } else if(value instanceof XmlFontDataObject){
            // Set up for font display
            return null;
            
        } else if(value instanceof XmlIntegerDataObject){
            // Set up a JTextField to edit an integer value
            JTextField integerEditor = new JTextField(Integer.toString(((XmlIntegerDataObject)value).intValue()));
            integerEditor.setBorder(BorderFactory.createEmptyBorder());
            integerEditor.selectAll();
            
            // Add a mapping to the hashtable
            valueMappings.put(integerEditor, value);
            
            // Add an action listener
            integerEditor.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    updateInt((JTextField)e.getSource());
                    fireEditingStopped();
                }
            });
            
            // Add a focus listener
            integerEditor.addFocusListener(new FocusListener(){
                public void focusLost(FocusEvent e){
                    updateInt((JTextField)e.getSource());
                }
                public void focusGained(FocusEvent e) {
                }
            });            
            
            return integerEditor;

        } else if(value instanceof XmlLongDataObject){
            // Set up a JTextField to edit a long value
            JTextField longEditor = new JTextField(Long.toString(((XmlLongDataObject)value).longValue()));
            longEditor.setBorder(BorderFactory.createEmptyBorder());
            longEditor.selectAll();
            
            // Add a mapping to the hashtable
            valueMappings.put(longEditor, value);
            
            // Add an action listener
            longEditor.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    updateLong((JTextField)e.getSource());
                    fireEditingStopped();
                }
            });
            
            // Add a focus listener
            longEditor.addFocusListener(new FocusListener(){
                public void focusLost(FocusEvent e){
                    updateLong((JTextField)e.getSource());
                }
                public void focusGained(FocusEvent e) {
                }
            });            
            
            return longEditor;
            
        } else if(value instanceof XmlStorableDataObject){
            // Check to see if there is an external editor
            XmlStorableDataObject obj = (XmlStorableDataObject)value;
            String className = obj.getClassName();
            if(parent.externalEditorExists(className)){
                ExternalXmlDataObjectEditor externalEditor = parent.getExternalEditor(className);
                try {
                    ExternalXmlDataObjectDialog dialog = externalEditor.getEditorDialog(null, obj);
                    dialog.setVisible(true);
                    if(dialog.isDialogAccepted()){
                        dialog.getEditorPanel().updateObject();
                        notifyEditCompleted();
                    }
                    
                } catch (Exception e){
                    // TODO: Error dialoig
                }
                fireEditingStopped();
            }
            return null;
            
        } else if(value instanceof XmlStringDataObject){
            // Set up a JTextField to edit a string
            JTextField stringEditor = new JTextField(((XmlStringDataObject)value).stringValue());
            stringEditor.setBorder(BorderFactory.createEmptyBorder());
            stringEditor.selectAll();
            
            // Add a mapping to the hashtable
            valueMappings.put(stringEditor, value);
            
            // Add an action listener
            stringEditor.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    updateString((JTextField)e.getSource());
                    fireEditingStopped();
                }
            });
            
            // Add a focus listener
            stringEditor.addFocusListener(new FocusListener(){
                public void focusLost(FocusEvent e){
                    updateString((JTextField)e.getSource());
                }
                public void focusGained(FocusEvent e) {
                }
            });            
            
            return stringEditor;
            
        } else if(value instanceof XmlDataStore){
            // Nothing gets displayed for this 
            return null;
            
        } else {
            return null;
        }
    }
    
    /** Edit a boolean */
    private void updateBoolean(JComboBox editor){
        if(valueMappings.containsKey(editor)){
            // Edit boolean value
            XmlBooleanDataObject value = (XmlBooleanDataObject)valueMappings.get(editor);
            valueMappings.remove(editor);
            
            String strResult = editor.getSelectedItem().toString();
            if(strResult.equalsIgnoreCase("yes")){
                value.setBooleanValue(true);
            } else {
                value.setBooleanValue(false);
            }
            fireEditingStopped();
            notifyEditCompleted();
        }
    }
    
    /** Update a double value */
    private void updateDouble(JTextField editor){
        if(valueMappings.containsKey(editor)){
            // Edit double value
            XmlDoubleDataObject value = (XmlDoubleDataObject)valueMappings.get(editor);
            valueMappings.remove(editor);
            
            try{
                double dNewValue = Double.parseDouble(editor.getText());
                value.setDoubleValue(dNewValue);                
            } catch (Exception e){
            }
            notifyEditCompleted();
        }
    }
    
    /** Update an integer value */
    private void updateInt(JTextField editor){
        if(valueMappings.containsKey(editor)){
            // Edit integer value
            XmlIntegerDataObject value = (XmlIntegerDataObject)valueMappings.get(editor);
            valueMappings.remove(editor);
            
            try{
                int iNewValue = Integer.parseInt(editor.getText());
                value.setIntValue(iNewValue);                
            } catch (Exception e){
            } 
            notifyEditCompleted();
        }        
    }
    
    /** Update a long value */
    private void updateLong(JTextField editor){
        if(valueMappings.containsKey(editor)){
            // Edit long value
            XmlLongDataObject value = (XmlLongDataObject)valueMappings.get(editor);
            valueMappings.remove(editor);
            
            try{
                long lNewValue = Long.parseLong(editor.getText());
                value.setLongValue(lNewValue);                
            } catch (Exception e){
            } 
            notifyEditCompleted();
        }        
    }    
    
    /** Update a String value */
    private void updateString(JTextField editor){
        if(valueMappings.containsKey(editor)){
            // Edit String value
            XmlStringDataObject value = (XmlStringDataObject)valueMappings.get(editor);
            valueMappings.remove(editor);
            value.setStringValue(editor.getText());
            notifyEditCompleted();
        }
    }
}
