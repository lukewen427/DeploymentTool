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

package org.pipeline.gui.xmlstorage.xmldatatypes;

import org.pipeline.core.xmlstorage.XmlDataObject;
import org.pipeline.core.xmlstorage.xmldatatypes.XmlBooleanDataObject;
import org.pipeline.gui.xmlstorage.XmlDataObjectEditor;
import org.pipeline.gui.xmlstorage.XmlDataStoreEditor;

/**
 * This panel edits a boolean data object
 * @author  hugo
 */
public class XmlBooleanDataObjectEditor extends javax.swing.JPanel implements XmlDataObjectEditor {
    /** Object being edited */
    private XmlBooleanDataObject booleanObject = null;
    
    /** Width of caption */
    private int captionWidth = 0;
    
    /** Creates new form XmlBooleanDataObjectEditor */
    public XmlBooleanDataObjectEditor() {
        initComponents();
    }

    /** Set the object */
    public void setObject(XmlDataObject object) {
        booleanObject = (XmlBooleanDataObject)object;
        valueBox.setText(booleanObject.getName());
        valueBox.setSelected(booleanObject.booleanValue());
        setBorder(new javax.swing.border.TitledBorder(booleanObject.getDescription()));        
    }

    /** Caption width is ignored */
    public void setCaptionWidth(int captionWidth) {
        this.captionWidth = captionWidth;
        resizeComponents();        
    }

    /** Resize all the components */
    private void resizeComponents(){
        //leftPanel.setPreferredSize(new java.awt.Dimension(captionWidth, 5));       
    }
    
    /** Update the value */
    public void updateValue() {
        if(booleanObject!=null){
            booleanObject.setBooleanValue(valueBox.isSelected());
        }
    }
    
    /** Reset the edited value back to its original value */
    public void resetValue() {
        valueBox.setSelected(booleanObject.booleanValue());
    }
    
    /** Set the parent editor window */
    public void setParent(XmlDataStoreEditor parent){
    }

    /** The helper data for this object has changed */
    public void helperDataChanged() {
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
        leftPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        valueBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        setBorder(new javax.swing.border.TitledBorder("A Boolean Value"));
        add(jPanel1, java.awt.BorderLayout.NORTH);

        add(jPanel2, java.awt.BorderLayout.SOUTH);

        add(leftPanel, java.awt.BorderLayout.WEST);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel4.add(valueBox, java.awt.BorderLayout.CENTER);

        add(jPanel4, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JCheckBox valueBox;
    // End of variables declaration//GEN-END:variables
    
}
