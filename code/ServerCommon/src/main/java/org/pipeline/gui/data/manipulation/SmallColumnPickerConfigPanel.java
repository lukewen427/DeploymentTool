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
 * This panel provides a smaller column picker configuration panel
 * that is better suited to small windows
 * @author  hugo
 */
public class SmallColumnPickerConfigPanel extends javax.swing.JPanel {
    /** Column picker being configured */
    private ColumnPicker picker = null;
    
    /** Data set if there is one available */
    private DataMetaData metaData = null;
    
    /** Listeners */
    private Vector listeners = new Vector();
    
    /** Creates new form SmallColumnPickerConfigPanel */
    public SmallColumnPickerConfigPanel() {
        initComponents();
        
        // Set up the selection type values
        javax.swing.DefaultComboBoxModel model = new javax.swing.DefaultComboBoxModel();
        model.addElement("By index");
        model.addElement("By name");
        selectionType.setModel(model);        
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
    }
    
    /** Configure the GUI */
    public void configureGui(){
        if(picker!=null){
            columnID.setEnabled(true);
            selectionType.setEnabled(true);            

            if(picker.getPickType()==ColumnPicker.PICK_BY_NUMBER){
                // Pick by number
                listColumnNumbers();
                selectionType.setSelectedIndex(0);
                columnID.setSelectedItem(Integer.toString(picker.getColumnIndex()));
                columnID.updateUI();
                
            } else {
                // List the names
                listColumnNames();
                selectionType.setSelectedIndex(1);
                
                if(metaData!=null){
                    int index = metaData.findColumnIndex(picker.getColumnName());
                    if(index!=-1){
                        columnID.setSelectedIndex(index);
                        columnID.updateUI();
                    } else {
                        columnID.setSelectedItem(picker.getColumnName());
                        columnID.updateUI();
                    }
                } else {
                    columnID.setSelectedItem(picker.getColumnName());
                    columnID.updateUI();
                }
            }
            
        } else {
            columnID.setEnabled(false);
            selectionType.setEnabled(false);            
        }
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
            
            columnID.setModel(model);
        } else {
            columnID.setModel(new javax.swing.DefaultComboBoxModel());
        }                        
    }

    /** List the column numbers */
    private void listColumnNumbers(){
        if(metaData!=null){
            javax.swing.DefaultComboBoxModel model = new javax.swing.DefaultComboBoxModel();

            for(int i=0;i<metaData.getColumns();i++){
                if(picker.isLimitColumnTypes()){
                    if(picker.isColumnClassSupported(metaData.column(i).getColumnClass())){
                        model.addElement(Integer.toString(i));
                    }
                } else {
                    model.addElement(Integer.toString(i));
                }            
            }
            columnID.setModel(model);
            
        } else {
            columnID.setModel(new javax.swing.DefaultComboBoxModel());
        }
    }
    
    /** Update the picker */
    public void updatePicker(){
        if(picker!=null){
            // Set the pick type
            if(selectionType.getSelectedIndex()==0){
                // Pick by name
                picker.setPickType(ColumnPicker.PICK_BY_NUMBER);
                
                // Set the column id
                try {
                    picker.setColumnIndex(Integer.parseInt(columnID.getSelectedItem().toString().trim()));
                } catch (Exception e){
                }                
                
            } else {
                // Pick by name
                picker.setPickType(ColumnPicker.PICK_BY_NAME);
                if(columnID.getSelectedItem()!=null){
                    picker.setColumnName(columnID.getSelectedItem().toString().trim());
                }
            }            
        }
    }
    
    /** Set the picker */
    public void setPicker(ColumnPicker picker){
        this.picker = picker;
        configureGui();
    }    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        columnID = new javax.swing.JComboBox();
        selectionType = new javax.swing.JComboBox();

        setLayout(new java.awt.BorderLayout(10, 0));

        jLabel1.setText("Select Column");
        add(jLabel1, java.awt.BorderLayout.WEST);

        jPanel1.setLayout(new java.awt.GridLayout(1, 2, 10, 0));

        columnID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                columnIDActionPerformed(evt);
            }
        });

        jPanel1.add(columnID);

        selectionType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectionTypeActionPerformed(evt);
            }
        });

        jPanel1.add(selectionType);

        add(jPanel1, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents

    private void columnIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_columnIDActionPerformed
        if(picker.getPickType()==ColumnPicker.PICK_BY_NAME){
            if(columnID.getSelectedIndex()!=-1 && columnID.getSelectedItem()!=null && metaData!=null){
                int index = metaData.findColumnIndex(columnID.getSelectedItem().toString().trim());
                if(index!=-1){
                    picker.setColumnName(metaData.column(index).getName());
                }
            }        
        } else {
            // Set the column id
            try {
                picker.setColumnIndex(Integer.parseInt(columnID.getSelectedItem().toString().trim()));
            } catch (Exception e){
            }                                            
        }
        notifyPickerUpdated();
    }//GEN-LAST:event_columnIDActionPerformed

    private void selectionTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectionTypeActionPerformed
        if(picker!=null){
            // Set the pick type
            if(selectionType.getSelectedIndex()==0){
                picker.setPickType(ColumnPicker.PICK_BY_NUMBER);
            } else if(selectionType.getSelectedIndex()==1){
                picker.setPickType(ColumnPicker.PICK_BY_NAME);
            }
            configureGui();
            notifyPickerUpdated();
        }
    }//GEN-LAST:event_selectionTypeActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox columnID;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox selectionType;
    // End of variables declaration//GEN-END:variables
    
}
