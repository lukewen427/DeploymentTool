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
import org.pipeline.core.xmlstorage.XmlDataObjectFactory;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class allows the user to add a new field to an XmlDataStore
 * @author  hugo
 */
public class XmlDataStoreAddFieldDialog extends javax.swing.JDialog {
    /** Has this dialog been accepted */
    private boolean accepted = false;
    
    /** Creates new form XmlDataStoreAddFieldDialog */
    public XmlDataStoreAddFieldDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        typeChooser.setModel(new javax.swing.DefaultComboBoxModel(XmlDataObjectFactory.listRecognisedTypes()));
        setBounds(new java.awt.Rectangle(300, 170));
    }
    
    /** Was this dialog accepted */
    public boolean getAccepted(){
        return accepted;
    }
    
    /** Create an XmlDataObject */
    public XmlDataObject createXmlDataObject() throws XmlStorageException {
        String strClass = typeChooser.getSelectedItem().toString();
        String strName = propertyName.getText().trim();
        String strValue = propertyValue.getText().trim();
        
        if(strName.length()>0){
            if(XmlDataObjectFactory.canDataObjectParseString(strClass) && strValue.length()>0){
                return XmlDataObjectFactory.createDataObject(strClass, strValue, strName);
            } else {
                return XmlDataObjectFactory.createNamedDataObjectFromLabel(strClass, strName);
            }
            
        } else {
            throw new XmlStorageException("Property must have a name");
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        typeChooser = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        propertyName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        propertyValue = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Property");
        setLocationRelativeTo(this);
        setModal(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

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

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setText("Data type:");
        jLabel1.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel2.add(jLabel1);

        typeChooser.setPreferredSize(new java.awt.Dimension(120, 25));
        typeChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeChooserActionPerformed(evt);
            }
        });

        jPanel2.add(typeChooser);

        jLabel2.setText("Property name:");
        jLabel2.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel2.add(jLabel2);

        propertyName.setPreferredSize(new java.awt.Dimension(120, 20));
        jPanel2.add(propertyName);

        jLabel3.setText("Property value:");
        jLabel3.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel2.add(jLabel3);

        propertyValue.setPreferredSize(new java.awt.Dimension(120, 20));
        jPanel2.add(propertyValue);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    /** Enable relevant components */
    private void typeChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeChooserActionPerformed
        String strClass = typeChooser.getSelectedItem().toString();
        try{
            if(XmlDataObjectFactory.canDataObjectParseString(strClass)){
                propertyValue.setEnabled(true);
            } else {
                propertyValue.setEnabled(false);
            }
        } catch (Exception e){
            propertyValue.setEnabled(false);
        }
    }//GEN-LAST:event_typeChooserActionPerformed

    /** Cancel button clicked */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
        accepted = false;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Ok button clicked */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        setVisible(false);
        accepted = true;
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new XmlDataStoreAddFieldDialog(new javax.swing.JFrame(), true).show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField propertyName;
    private javax.swing.JTextField propertyValue;
    private javax.swing.JComboBox typeChooser;
    // End of variables declaration//GEN-END:variables
    
}
