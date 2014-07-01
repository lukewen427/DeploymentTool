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

import java.util.Enumeration;
import java.util.Vector;

/**
 * This panel configures a column picker object that can extract columns from 
 * a set of Data based either on name or column index.
 * @author  hugo
 */
public class ColumnPickerConfigPanel extends javax.swing.JPanel {
    /** Column picker being configured */
    private ColumnPicker picker = null;
    
    /** Data set if there is one available */
    private DataMetaData metaData = null;
    
    /** Listeners */
    private Vector listeners = new Vector();
    
    /** Creates new form ColumnPickerConfigPanel */
    public ColumnPickerConfigPanel() {
        initComponents();
        
        setPicker(null);
    }
    
    /** Add a listener */
    public void addColumnPickerConfigListener(ColumnPickerConfigListener listener){
        if(!listeners.contains(listener)){
            listeners.addElement(listener);
        }
    }
    
    /** Remove a listener */
    public void removeColumnPickerConfigListener(ColumnPickerConfigListener listener){
        if(listeners.contains(listener)){
            listeners.removeElement(listener);
        }
    }
    
    /** Notify an update */
    private void notifyPickerUpdated(){
        Enumeration e = listeners.elements();
        while(e.hasMoreElements()){
            ((ColumnPickerConfigListener)e.nextElement()).pickerUpdated(picker);
        }
    }
    
    /** Set the meta-data */
    public void setMetaData(DataMetaData metaData){
        this.metaData = metaData;
        listColumnNames();
        configureGui();
    }
    
    /** List the column names */
    private void listColumnNames(){
        if(metaData!=null && picker!=null){
            javax.swing.DefaultComboBoxModel model = new javax.swing.DefaultComboBoxModel();
            for(int i=0;i<metaData.getColumns();i++){
                if(picker.isLimitColumnTypes()){
                    if(picker.isColumnClassSupported(metaData.column(i).getColumnClass())){
                        model.addElement(metaData.column(i));
                    }
                } else {
                    model.addElement(metaData.column(i));
                }
            }
            nameCombo.setModel(model);
            
        } else {
            nameCombo.setModel(new javax.swing.DefaultComboBoxModel());
        }               
        
    }
    
    /** Configure the GUI */
    private void configureGui(){
        if(picker!=null){
            pickByNameOption.setEnabled(true);
            pickByIndexOption.setEnabled(true);
            
            if(picker.getPickType()==ColumnPicker.PICK_BY_NAME){
                pickByNameOption.setSelected(true);
                pickByIndexOption.setSelected(false);
                nameCombo.setEnabled(true);
                indexField.setEnabled(false);
                
                if(metaData!=null){
                    if(metaData.column(metaData.findColumnIndex(picker.getColumnName()))!=null){
                        nameCombo.setSelectedItem(metaData.column(metaData.findColumnIndex(picker.getColumnName())));
                    } else {
                        nameCombo.setSelectedItem(0);
                    }
                    nameCombo.updateUI();
                }
                
                indexField.setText("");

            } else {
                pickByNameOption.setSelected(false);
                pickByIndexOption.setSelected(true);
                nameCombo.setEnabled(false);
                indexField.setEnabled(true);
                nameCombo.setSelectedItem(null);
                indexField.setText(Integer.toString(picker.getColumnIndex()));
            }
        } else {
            nameCombo.setSelectedItem(null);
            nameCombo.setEnabled(false);
            indexField.setText("");
            indexField.setEnabled(false);
            pickByNameOption.setEnabled(false);
            pickByIndexOption.setEnabled(false);
        }
    }
    
    /** Update the picker */
    public void updatePicker(){
        if(picker!=null){
            
            try {
                picker.setColumnIndex(Integer.parseInt(indexField.getText().trim()));
            } catch (Exception e){
            }
            if(nameCombo.getSelectedIndex()!=-1 && nameCombo.getSelectedItem()!=null && metaData!=null){
                int index = metaData.findColumnIndex(nameCombo.getSelectedItem().toString().trim());
                picker.setColumnName(metaData.column(index).getName());
            }
            
            if(pickByNameOption.isSelected()){
                picker.setPickType(ColumnPicker.PICK_BY_NAME);                
            } else {
                picker.setPickType(ColumnPicker.PICK_BY_NUMBER);
            }
            configureGui();
        }
        notifyPickerUpdated();
    }
        
    /** Set the picker */
    public void setPicker(ColumnPicker picker){
        this.picker = picker;
        listColumnNames();
        configureGui();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        optionButtons = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        nameCombo = new javax.swing.JComboBox();
        pickByNameOption = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        pickByIndexOption = new javax.swing.JRadioButton();
        indexField = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridLayout(2, 0, 0, 4));

        jPanel2.setLayout(new java.awt.BorderLayout(4, 0));

        nameCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameComboActionPerformed(evt);
            }
        });

        jPanel2.add(nameCombo, java.awt.BorderLayout.CENTER);

        optionButtons.add(pickByNameOption);
        pickByNameOption.setText("Select by name");
        pickByNameOption.setOpaque(false);
        pickByNameOption.setPreferredSize(new java.awt.Dimension(200, 3));
        pickByNameOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pickByNameOptionActionPerformed(evt);
            }
        });

        jPanel2.add(pickByNameOption, java.awt.BorderLayout.WEST);

        jPanel1.add(jPanel2);

        jPanel3.setLayout(new java.awt.BorderLayout(4, 0));

        optionButtons.add(pickByIndexOption);
        pickByIndexOption.setText("Select by index");
        pickByIndexOption.setPreferredSize(new java.awt.Dimension(200, 23));
        pickByIndexOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pickByIndexOptionActionPerformed(evt);
            }
        });

        jPanel3.add(pickByIndexOption, java.awt.BorderLayout.WEST);

        indexField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexFieldActionPerformed(evt);
            }
        });
        indexField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                indexFieldFocusLost(evt);
            }
        });
        indexField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                indexFieldKeyPressed(evt);
            }
        });

        jPanel3.add(indexField, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel3);

        add(jPanel1, java.awt.BorderLayout.NORTH);

    }// </editor-fold>//GEN-END:initComponents

    private void indexFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_indexFieldKeyPressed
        if(evt.getKeyCode()==java.awt.event.KeyEvent.VK_ENTER){
            updatePicker();
        }
    }//GEN-LAST:event_indexFieldKeyPressed

    private void indexFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_indexFieldFocusLost
        updatePicker();
    }//GEN-LAST:event_indexFieldFocusLost

    private void indexFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexFieldActionPerformed

    }//GEN-LAST:event_indexFieldActionPerformed

    private void nameComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameComboActionPerformed
        updatePicker();
    }//GEN-LAST:event_nameComboActionPerformed

    private void pickByIndexOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pickByIndexOptionActionPerformed
        updatePicker();
    }//GEN-LAST:event_pickByIndexOptionActionPerformed

    private void pickByNameOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pickByNameOptionActionPerformed
        updatePicker();
    }//GEN-LAST:event_pickByNameOptionActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField indexField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JComboBox nameCombo;
    private javax.swing.ButtonGroup optionButtons;
    private javax.swing.JRadioButton pickByIndexOption;
    private javax.swing.JRadioButton pickByNameOption;
    // End of variables declaration//GEN-END:variables
    
}
