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

package org.pipeline.gui.data.editor;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.ColumnFactory;
import org.pipeline.core.data.ColumnTypeInfo;
import org.pipeline.core.data.DataException;

import javax.swing.*;

/**
 * This dialog window lists the recognised column types from the 
 * ColumnFactory and allows the user to create new columns.
 * @author  hugo
 */
public class NewColumnDialog extends javax.swing.JDialog {
    /** Has this dialog been accepted */
    boolean accepted = false;
    
    /** Creates new form NewColumnDialog */
    public NewColumnDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(300, 130);
        setResizable(false);
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (ColumnTypeInfo info : ColumnFactory.getColumnTypes()) {
            model.addElement(info);
        }
        columnType.setModel(model);
    }
    
    /** Was this dialog accepted */
    public boolean getAccepted(){
        return accepted;
    }
    
    /** Create the column */
    public Column createColumn() throws DataException {
        if(columnType.getSelectedItem()!=null){
            ColumnTypeInfo type = (ColumnTypeInfo)columnType.getSelectedItem();
            Column col = ColumnFactory.createColumn(type.getId());
            col.setName(columnName.getText().trim());
            return col;
        } else {
            throw new DataException("No column type specified");
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        columnType = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        columnName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create New Column");

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel1.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel1.add(cancelButton);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        jPanel2.setLayout(new java.awt.BorderLayout(0, 10));

        jPanel3.setLayout(new java.awt.BorderLayout(10, 0));

        jPanel3.add(columnType, java.awt.BorderLayout.CENTER);

        jLabel1.setText("Column type:");
        jLabel1.setPreferredSize(new java.awt.Dimension(120, 15));
        jPanel3.add(jLabel1, java.awt.BorderLayout.WEST);

        jPanel2.add(jPanel3, java.awt.BorderLayout.NORTH);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.BorderLayout(10, 0));
        jPanel5.add(columnName, java.awt.BorderLayout.CENTER);

        jLabel2.setText("Column name:");
        jLabel2.setPreferredSize(new java.awt.Dimension(120, 15));
        jPanel5.add(jLabel2, java.awt.BorderLayout.WEST);

        jPanel4.add(jPanel5, java.awt.BorderLayout.NORTH);

        jPanel2.add(jPanel4, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Cancel this dialog */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        accepted = false;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Accept this dialog */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        accepted = true;
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewColumnDialog(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField columnName;
    private javax.swing.JComboBox columnType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
    
}
