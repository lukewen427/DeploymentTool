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

package org.pipeline.gui.data.manipulation;

import org.pipeline.core.data.DataMetaData;
import org.pipeline.core.data.manipulation.ColumnPicker;
import org.pipeline.core.data.manipulation.ColumnPickerCollection;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This panel lets a collection of ColumnPicker objects be configured.
 * @author  hugo
 */
public class ColumnPickerCollectionEditor extends javax.swing.JPanel implements ColumnPickerConfigListener {
    /** Picker configuration panel */
    private ColumnPickerConfigPanel configPanel = null;
    
    /** Column picker collection */
    private ColumnPickerCollection pickers = null;
    
    /** Selected column picker */
    private ColumnPicker selected = null;
        
    /** Listeners */
    private Vector listeners = new Vector();
    
    /** Class of picker to create */
    private Class pickerClass = ColumnPicker.class;
    
    /** Creates new form ColumnPickerCollectionEditor */
    public ColumnPickerCollectionEditor(Class pickerClass) {
        initComponents();


        this.pickerClass = pickerClass;
        configPanel = new ColumnPickerConfigPanel();
        configPanel.addColumnPickerConfigListener(this);
        add(configPanel, java.awt.BorderLayout.SOUTH);
        
        // Create the model for the combo box
        javax.swing.DefaultComboBoxModel model = new javax.swing.DefaultComboBoxModel();
        model.addElement("Selected only");
        model.addElement("Selected and remaining");
        model.addElement("Selected and all");
        inclusionCombo.setModel(model);
    }
    
    /** Hide the inclusion combo and label */
    public void hideInclusionDisplay(){
        inclusionCombo.setVisible(false);
        inclusionLabel.setVisible(false);
    }
    
    /** Add a listener */
    public void addColumnPickerCollectionEditorListener(ColumnPickerCollectionEditorListener l){
        if(!listeners.contains(l)){
            listeners.addElement(l);
        }
    }
    
    /** Remove a listener */
    public void removeColumnPickerCollectionEditorListener(ColumnPickerCollectionEditorListener l){
        if(listeners.contains(l)){
            listeners.removeElement(l);
        }
    }
    
    /** Notify listeners of a change in picker selection */
    private void notifyPickerSelected(ColumnPicker picker){
        Enumeration e = listeners.elements();
        while(e.hasMoreElements()){
            ((ColumnPickerCollectionEditorListener)e.nextElement()).pickerSelected(picker);
        }
    }
    
    /** Notify listeners that they need to update a picker */
    private void notifyUpdatePicker(ColumnPicker picker){
        Enumeration e = listeners.elements();
        while(e.hasMoreElements()){
            ((ColumnPickerCollectionEditorListener)e.nextElement()).updatePicker(picker);
        }
    }
    
    /** Picker has been updated */
    public void pickerUpdated(ColumnPicker picker) {
        refreshPickerList();
    }
    
    /** Refresh the list of pickers */
    private void refreshPickerList(){
        inclusionCombo.setSelectedIndex(pickers.getExtractionMethod());
        
        javax.swing.DefaultListModel model = new javax.swing.DefaultListModel();
        for(int i=0;i<pickers.size();i++){
            model.addElement(pickers.picker(i));
        }
        pickerList.setModel(model);
        
        // Set the selected picker
        if(model.contains(selected)){
            pickerList.setSelectedValue(selected, true);
        } else {
            selectPicker(null);
        }
    }
    
    /** Set the picker collection */
    public void setPickers(ColumnPickerCollection pickers){
        this.pickers = pickers;
        refreshPickerList();
    }
    
    /** Set the data meta-data so that column names can be displayed if available */
    public void setMetaData(DataMetaData metaData){
        configPanel.setMetaData(metaData);
    }
    
    /** Update the selected picker */
    public void updateSelectedPicker(){
        if(selected!=null){
            configPanel.updatePicker();
            pickerList.repaint();
            pickers.setExtractionMethod(inclusionCombo.getSelectedIndex());
            notifyUpdatePicker(selected);
        }
    }
    
    /** Select another picker */
    public void selectPicker(ColumnPicker picker){
        // Has the picker changed
        if(picker!=null){
            if(selected!=null && !selected.equals(picker)){
                // Update the selected if it has
                updateSelectedPicker();
            }
            selected = picker;
            configPanel.setPicker(selected);
            notifyPickerSelected(selected);
        } else {
            selected = null;
            updateSelectedPicker();
            notifyPickerSelected(null);
        }
    }
    
    /** Get the selected picker */
    public ColumnPicker getSelectedPicker(){
        return selected;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        pickerList = new javax.swing.JList();
        jPanel4 = new javax.swing.JPanel();
        inclusionLabel = new javax.swing.JLabel();
        inclusionCombo = new javax.swing.JComboBox();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.GridLayout(5, 1));

        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/pipeline/gui/data/resource/Add16.gif")));
        addButton.setText("Add");
        addButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        jPanel2.add(addButton);

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/pipeline/gui/data/resource/Delete16.gif")));
        deleteButton.setText("Delete");
        deleteButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        jPanel2.add(deleteButton);

        jPanel2.add(jPanel3);

        moveUpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/pipeline/gui/data/resource/Up16.gif")));
        moveUpButton.setText("Up");
        moveUpButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPanel2.add(moveUpButton);

        moveDownButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/pipeline/gui/data/resource/Down16.gif")));
        moveDownButton.setText("Down");
        moveDownButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPanel2.add(moveDownButton);

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        add(jPanel1, java.awt.BorderLayout.EAST);

        pickerList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        pickerList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pickerListMouseReleased(evt);
            }
        });

        jScrollPane1.setViewportView(pickerList);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel4.setLayout(new java.awt.BorderLayout());

        inclusionLabel.setText("Inclusion:");
        inclusionLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel4.add(inclusionLabel, java.awt.BorderLayout.WEST);

        inclusionCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inclusionComboActionPerformed(evt);
            }
        });

        jPanel4.add(inclusionCombo, java.awt.BorderLayout.CENTER);

        add(jPanel4, java.awt.BorderLayout.NORTH);

    }// </editor-fold>//GEN-END:initComponents

    /** Update inclusion method */
    private void inclusionComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inclusionComboActionPerformed
        pickers.setExtractionMethod(inclusionCombo.getSelectedIndex());
    }//GEN-LAST:event_inclusionComboActionPerformed

    /** Delete the selected picker */
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        if(selected!=null){
            pickers.removeColumnPicker(selected);
            selectPicker(null);
            refreshPickerList();
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    /** Add a new picker */
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try {
            ColumnPicker picker = (ColumnPicker)pickerClass.newInstance();
            pickers.addColumnPicker(picker);
            selectPicker(picker);
            refreshPickerList();
            
        } catch (Exception e){
            e.printStackTrace();
        }
    }//GEN-LAST:event_addButtonActionPerformed

    /** Change the selection */
    private void pickerListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pickerListMouseReleased
        if(pickerList.getSelectedValue()!=null){
            selectPicker((ColumnPicker)pickerList.getSelectedValue());
        } else {
            selectPicker(null);
        }
    }//GEN-LAST:event_pickerListMouseReleased
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JComboBox inclusionCombo;
    private javax.swing.JLabel inclusionLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JList pickerList;
    // End of variables declaration//GEN-END:variables
    
}
