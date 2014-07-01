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

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author  hugo
 */
public class XmlDataStoreEditorTable extends javax.swing.JPanel implements TableModel {
    /** Properties being edited */
    private XmlDataStore properties;
    
    /** List of property names */
    private Vector<String> propertyNames;
    
    /** Table listeners */
    private Vector<TableModelListener> tableListeners = new Vector<>();

    /** XmlDataObject editor */
    private XmlDataObjectEditor dataObjectEditor;
    
    /** Data store listeners */
    private Vector<XmlDataStoreEditListener> dataStoreListeners = new Vector<>();
    
    /** Should the properties be sorted into ascending order */
    private boolean sortIntoAscendingOrder = true;
    
    /** List of editor types for XmlStorableObjects */
    private Hashtable<String,ExternalXmlDataObjectEditor> externalEditors = new Hashtable<>();

    /** Is this editor operating in readonly mode */
    private boolean readOnly = false;

    /** Creates new form XmlDataStoreTableEditor */
    public XmlDataStoreEditorTable() {
        initComponents();
        
        dataObjectEditor = new XmlDataObjectEditor(this);
        propertiesTable.setDefaultEditor(XmlDataObject.class, dataObjectEditor);
        propertiesTable.setDefaultRenderer(XmlDataObject.class, new XmlDataObjectRenderer());
        
        propertiesTable.setModel(this);
        
        // Listen for table selection events
        propertiesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                selectionChanged();
            }
        });
        setupAddRemoveButtons();
    }

    /** Set whether this editor is running readonly */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /** Get whether this editor is running readonly */
    public boolean getReadOnly(){
        return readOnly;
    }
    
    /** Set the edited properties */
    public void setProperties(XmlDataStore properties) {
        this.properties = properties;
        setupAddRemoveButtons();
        if(properties!=null){
            this.propertyNames = properties.getNames();
            
            // Sort into correct order
            if(sortIntoAscendingOrder){
                sortAscending();
            } else {
                sortDescending();
            }                  
        } else {
            propertyNames = null;
            notifyTableChange();
        }
    }

    /** Show / hide the add remove buttons */
    private void setupAddRemoveButtons(){
        if(properties!=null){
            if(properties.getAllowAddRemove()){
                addProperty.setVisible(true);
                deleteProperty.setVisible(true);
            } else {
                addProperty.setVisible(false);
                deleteProperty.setVisible(false);
            }
        } else {
            addProperty.setVisible(false);
            deleteProperty.setVisible(false);
        }
    }
    
    /** Notify a change in the table data */
    private void notifyTableChange(){
        Iterator<TableModelListener>i = tableListeners.iterator();
        while(i.hasNext()){
            i.next().tableChanged(new TableModelEvent(this));
        }
    }
        
            
    /** Selected value has changed */
    private void selectionChanged(){
        dataObjectEditor.terminateInPlaceEdits();
        
        // Display selected value
        if(propertiesTable.getSelectedRow()!=-1){
            int selected = propertiesTable.getSelectedRow();
            if(selected>=0 && selected<propertyNames.size()){
                try {
                    XmlDataObject value = properties.get(propertyNames.get(selected));
                    nameLabel.setText(value.getName());
                    if(value.getDescription()!=null && !(value.getDescription().trim().equals(""))){
                        descriptionField.setText(value.getDescription());
                    } else {
                        descriptionField.setText(value.getName());
                    }
                } catch (Exception e){
                    nameLabel.setText("");
                    descriptionField.setText("");
                }
            } else {
                
            }
            
        }
    }
    
    /** Is there an external editor for a specified class */
    public boolean externalEditorExists(String className){
        return externalEditors.containsKey(className);
    }
    
    /** Get the external editor for a specified class */
    public ExternalXmlDataObjectEditor getExternalEditor(String className){
        return externalEditors.get(className);
    }
    
    /** Register an external editor for a specifie class */
    public void addExternalEditor(String className, ExternalXmlDataObjectEditor editor){
        externalEditors.put(className, editor);
    }
    
    /** Editing has finished */
    public void editCompleted(){
        notifyDataStoreChange();
    }
    
    /** Add an XmlDataStoreEditListener */
    public void addXmlDataStoreEditListener(XmlDataStoreEditListener listener){
        dataStoreListeners.add(listener);
    }
    
    /** Remove an XmlDataStoreEditListener */
    public void removeXmlDataStoreEditListener(XmlDataStoreEditListener listener){
        dataStoreListeners.remove(listener);
    }

    /** Remove all the listeners from the xml data store */
    public void clearXmlDataStoreEditListeners(){
        dataStoreListeners.clear();
    }

    /** Notify listeners that the data store has changed */
    private void notifyDataStoreChange(){
        Iterator<XmlDataStoreEditListener> i = dataStoreListeners.iterator();
        while(i.hasNext()){
            i.next().dataStoreChanged(new XmlDataStoreEditEvent(properties));
        }        
    }
    
    /** Sort into ascending order */
    private void sortAscending(){
        if(propertyNames!=null){
            propertiesTable.removeEditor();
            dataObjectEditor.terminateInPlaceEdits();
            Collections.sort(propertyNames);
            notifyTableChange();
            propertiesTable.updateUI();        
        }
    }
    
    /** Sort into descending order */
    private void sortDescending() {
        if(propertyNames!=null){
            propertiesTable.removeEditor();
            dataObjectEditor.terminateInPlaceEdits();
            Collections.sort(propertyNames);
            Collections.reverse(propertyNames);
            notifyTableChange();
            propertiesTable.updateUI();        
        }   
    }
        
    // =========================================================================
    // TableModel methods
    // =========================================================================
    
    /** Add a listener */
    public void addTableModelListener(javax.swing.event.TableModelListener l) {
        tableListeners.add(l);
    }    
    
    /** Get class of table column. First column is name, second is XmlDataObject */
    public Class getColumnClass(int columnIndex) {
        if(columnIndex==0){
            return String.class;
        } else {
            return XmlDataObject.class;
        }    
    }    
    
    /** Always two columns// Nothing gets displayed for this  in this table */
    public int getColumnCount() {
        return 2;
    }// Nothing gets displayed for this 
    
    /** First column is "Name", second is "Value" */
    public String getColumnName(int columnIndex) {
        if(columnIndex==0){
            return "Name";
        } else {
            return "Value";
        }
    }
    
    /** Return number of properties */
    public int getRowCount() {
        if(properties!=null){
            return properties.size();
        } else {
            return 0;
        }
    }
    
    /** Return a specific cell value */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(properties!=null){
            try{
                String strName = propertyNames.elementAt(rowIndex);
                XmlDataObject value = properties.get(strName);
                switch(columnIndex){
                    case 0:
                        return strName;
                    case 1:
                        return value;
                    default:
                        return null;
                }
                
            } catch (Exception e){
                return null;
            }
            
        } else {
            return null;
        }
    }
    
    /** Is a particular cell editable. Only cells in the second column are
     * editable in this table */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(!readOnly){
            if(columnIndex==0){
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    
    /** Remove a listener */
    public void removeTableModelListener(javax.swing.event.TableModelListener l) {
        tableListeners.remove(l);
    }
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        ascendingButton = new javax.swing.JButton();
        descendingButton = new javax.swing.JButton();
        addProperty = new javax.swing.JButton();
        deleteProperty = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionField = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        propertiesTable = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        ascendingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/pipeline/gui/xmlstorage/resource/Up16.gif"))); // NOI18N
        ascendingButton.setBorderPainted(false);
        ascendingButton.setFocusable(false);
        ascendingButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ascendingButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ascendingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ascendingButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(ascendingButton);

        descendingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/pipeline/gui/xmlstorage/resource/Down16.gif"))); // NOI18N
        descendingButton.setBorderPainted(false);
        descendingButton.setFocusable(false);
        descendingButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        descendingButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        descendingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descendingButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(descendingButton);

        addProperty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/pipeline/gui/xmlstorage/resource/Add16.gif"))); // NOI18N
        addProperty.setBorderPainted(false);
        addProperty.setFocusable(false);
        addProperty.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addProperty.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPropertyActionPerformed(evt);
            }
        });
        jToolBar1.add(addProperty);

        deleteProperty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/pipeline/gui/xmlstorage/resource/Delete16.gif"))); // NOI18N
        deleteProperty.setBorderPainted(false);
        deleteProperty.setFocusable(false);
        deleteProperty.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteProperty.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        deleteProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletePropertyActionPerformed(evt);
            }
        });
        jToolBar1.add(deleteProperty);

        add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new java.awt.BorderLayout(0, 10));

        nameLabel.setBackground(new java.awt.Color(255, 255, 255));
        nameLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        nameLabel.setText("Name");
        nameLabel.setOpaque(true);
        jPanel1.add(nameLabel, java.awt.BorderLayout.NORTH);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        descriptionField.setEditable(false);
        descriptionField.setColumns(20);
        descriptionField.setLineWrap(true);
        descriptionField.setRows(5);
        descriptionField.setWrapStyleWord(true);
        descriptionField.setBorder(null);
        jScrollPane1.setViewportView(descriptionField);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        propertiesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        propertiesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(propertiesTable);

        add(jScrollPane2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void ascendingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ascendingButtonActionPerformed
        sortAscending();
        sortIntoAscendingOrder = true;

}//GEN-LAST:event_ascendingButtonActionPerformed

private void descendingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descendingButtonActionPerformed
        sortDescending();
        sortIntoAscendingOrder = false;
}//GEN-LAST:event_descendingButtonActionPerformed

private void addPropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPropertyActionPerformed
        if(properties!=null && properties.getAllowAddRemove()){
            XmlDataStoreAddFieldDialog dialog;
            //dialog = new XmlDataStoreAddFieldDialog(WindowManager.getDefault().getMainWindow(), true);
            dialog = new XmlDataStoreAddFieldDialog(null, true);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if(dialog.getAccepted()){
                try {
                    XmlDataObject obj = dialog.createXmlDataObject();
                    properties.add(obj);
                    propertyNames = properties.getNames();
                    if(sortIntoAscendingOrder){
                        sortAscending();
                    } else {
                        sortDescending();
                    }
                    
                } catch (Exception e){
                    // TODO: Error dialog
                }
            } 
        }
}//GEN-LAST:event_addPropertyActionPerformed

private void deletePropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePropertyActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_deletePropertyActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addProperty;
    private javax.swing.JButton ascendingButton;
    private javax.swing.JButton deleteProperty;
    private javax.swing.JButton descendingButton;
    private javax.swing.JTextArea descriptionField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTable propertiesTable;
    // End of variables declaration//GEN-END:variables

}
