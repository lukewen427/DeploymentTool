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

package org.pipeline.gui.data.io;

import org.pipeline.core.data.Data;
import org.pipeline.core.data.io.DelimitedTextDataImporter;
import org.pipeline.gui.data.table.DataTableModel;

import java.io.File;

/**
 * This panel provides a simple import panel for loading data in
 * a variety of delimited text files
 * @author  hugo
 */
public class DelimitedTextImportPanel extends javax.swing.JPanel {
    /** Selected file */
    private File selectedFile = null;
    
    /** Imported data */
    private Data importedData = null;
    
    /** Data to table adapter model */
    private DataTableModel model = null;
    
    /** Has a file been selected */
    private boolean fileSelected = false;
    
    /** Was the data imported correctly */
    private boolean dataImported = false;
    
    /** Data importer for loading data */
    private DelimitedTextDataImporter importer = null;
    
    /** Creates new form DelimitedTextImportPanel with an existing data set and importer */
    public DelimitedTextImportPanel(DelimitedTextDataImporter importer) {
        initComponents();
        
        this.importer = importer;
        model = new DataTableModel(importedData);
        dataTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        if(!importer.getFileName().trim().equals("")){
            selectedFile = new File(importer.getFileName().trim());
        } else {
            selectedFile = null;
        }
        configureWindow();
    }
    
    /** Creates new form DelimitedTextImportPanel */
    public DelimitedTextImportPanel() {
        initComponents();
        importedData = new Data();
        importer = new DelimitedTextDataImporter();
        configureWindow();
    }
    
    /** Get the importer object */
    public DelimitedTextDataImporter getImporter(){
        return importer;
    }
    
    /** Configure the window with values taken from the data importer */
    private void configureWindow(){
        if(selectedFile==null){
            importButton.setEnabled(false);
            fileNameLabel.setText("None");
            fileSelected = false;
        } else {
            importButton.setEnabled(true);
            fileNameLabel.setText(selectedFile.getName());
            fileSelected = true;
        } 
        
        // Set up combo box items
        javax.swing.DefaultComboBoxModel comboModel = new javax.swing.DefaultComboBoxModel(new String[]{",",";","TAB", "SPACE"});
        delimiterCombo.setModel(comboModel);
        
        startRowField.setText(Integer.toString(importer.getDataStartRow()));
        endRowField.setText(Integer.toString(importer.getDataEndRow()));
        columnNameRowField.setText(Integer.toString(importer.getNameRow()));
        subsampleField.setText(Integer.toString(importer.getSampleInterval()));
        
        switch(importer.getDelimiterType()){
            case DelimitedTextDataImporter.COMMA_DELIMITED:
                delimiterCombo.setSelectedItem(",");
                break;
                
            case DelimitedTextDataImporter.SEMI_COLON_DELIMITED:
                delimiterCombo.setSelectedItem(";");
                break;
                
            case DelimitedTextDataImporter.TAB_DELIMITED:
                delimiterCombo.setSelectedItem("TAB");
                break;
                
            case DelimitedTextDataImporter.WHITESPACE_DELIMITED:
                delimiterCombo.setSelectedItem("SPACE");
                break;
                
            case DelimitedTextDataImporter.CUSTOM_DELIMITED:
            default:
                delimiterCombo.setSelectedItem(importer.getDelimiterString());  
        }
        
        importNamesCheck.setSelected(importer.isImportColumnNames());
        subsampleCheck.setSelected(importer.isSubsample());
        limitSizeCheck.setSelected(importer.isLimitRows());
        forceTextCheck.setSelected(importer.isForceTextImport());
    }
    
    /** Set up the data importer using the values on this GUI */
    private boolean setupImporter(){
        // Start row
        try {
            int startRow = Integer.parseInt(startRowField.getText());
            if(startRow>0){       
                importer.setDataStartRow(startRow);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Invalid start row", "Import error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e){
            javax.swing.JOptionPane.showMessageDialog(this, "Error loading start row", "Import error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // End row 
        if(limitSizeCheck.isSelected()){
            try {
                int endRow = Integer.parseInt(endRowField.getText());
                if(endRow>0){
                    importer.setDataEndRow(endRow);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Invalid end row", "Import error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e){
                javax.swing.JOptionPane.showMessageDialog(this, "Error loading end row", "Import error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return false;
            }   
        }
        
        // Label row
        if(importNamesCheck.isSelected()){
            try {
                int nameRow = Integer.parseInt(columnNameRowField.getText());
                if(nameRow>0){
                    importer.setNameRow(nameRow);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Invalid names row", "Import error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e){
                javax.swing.JOptionPane.showMessageDialog(this, "Error loading names row", "Import error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        // Sub-sample interval
        if(subsampleCheck.isSelected()){
            try {
                int subsampleInterval = Integer.parseInt(subsampleField.getText());
                if(subsampleInterval>0){
                    importer.setSampleInterval(subsampleInterval);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Invalid subsample interval", "Import error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
            } catch (NumberFormatException e){
                javax.swing.JOptionPane.showMessageDialog(this, "Error loading subsample interval", "Import error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        // Column delimiter
        String delimiter = delimiterCombo.getSelectedItem().toString();
        if(delimiter.equals(",")){
            importer.setDelimiterType(DelimitedTextDataImporter.COMMA_DELIMITED);
            
        } else if(delimiter.equals(";")){
            importer.setDelimiterType(DelimitedTextDataImporter.SEMI_COLON_DELIMITED);
            
        } else if(delimiter.equals("TAB")){
            importer.setDelimiterType(DelimitedTextDataImporter.TAB_DELIMITED);
            
        } else if(delimiter.equals("SPACE")){
            importer.setDelimiterType(DelimitedTextDataImporter.WHITESPACE_DELIMITED);
            
        } else {
            // Custom delimiter
            importer.setDelimiterType(DelimitedTextDataImporter.CUSTOM_DELIMITED);
            importer.setDelimiterString(delimiter);
        }
       
        // Import names
        importer.setImportColumnNames(importNamesCheck.isSelected());
        
        // Subsample
        importer.setSubsample(subsampleCheck.isSelected());
        
        // Limit rows
        importer.setLimitRows(limitSizeCheck.isSelected());
        
        // Force text import 
        importer.setForceTextImport(forceTextCheck.isSelected());
        return true;
    }
    
    /** Do the data import */
    private void doImport(){
        if(fileSelected){
            if(setupImporter()==true){
                try {
                    importedData = importer.importFile(selectedFile);
                    model = new DataTableModel(importedData);
                    dataTable.setModel(model);

                    // Set preferred column widths
                    int size = dataTable.getColumnModel().getColumnCount();
                    for(int i=0;i<size;i++){
                        dataTable.getColumnModel().getColumn(i).setMinWidth(75);
                    }
                    
                } catch (Exception e){
                    e.printStackTrace();
                    javax.swing.JOptionPane.showMessageDialog(this, "Import error: " + e.getMessage(), "Import error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        }        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jToolBar1 = new javax.swing.JToolBar();
        selectButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        startRowField = new javax.swing.JTextField();
        endRowField = new javax.swing.JTextField();
        columnNameRowField = new javax.swing.JTextField();
        subsampleField = new javax.swing.JTextField();
        delimiterCombo = new javax.swing.JComboBox();
        jPanel7 = new javax.swing.JPanel();
        importNamesCheck = new javax.swing.JCheckBox();
        subsampleCheck = new javax.swing.JCheckBox();
        limitSizeCheck = new javax.swing.JCheckBox();
        forceTextCheck = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        dataScroller = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        fileNameLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jToolBar1.setRollover(true);
        selectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/pipeline/gui/data/resource/Open16.gif")));
        selectButton.setText("Select file");
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });

        jToolBar1.add(selectButton);

        importButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/pipeline/gui/data/resource/Import16.gif")));
        importButton.setText("Perform import");
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });

        jToolBar1.add(importButton);

        add(jToolBar1, java.awt.BorderLayout.NORTH);

        jPanel8.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout(40, 20));

        jPanel5.setLayout(new java.awt.GridLayout(5, 0, 2, 10));

        jLabel1.setText("Data start row:");
        jPanel5.add(jLabel1);

        jLabel2.setText("Data end row:");
        jPanel5.add(jLabel2);

        jLabel3.setText("Column label row:");
        jPanel5.add(jLabel3);

        jLabel4.setText("Sub-sample interval:");
        jPanel5.add(jLabel4);

        jLabel5.setText("Column delimiter:");
        jPanel5.add(jLabel5);

        jPanel1.add(jPanel5, java.awt.BorderLayout.WEST);

        jPanel6.setLayout(new java.awt.GridLayout(5, 0, 0, 10));

        jPanel6.add(startRowField);

        jPanel6.add(endRowField);

        jPanel6.add(columnNameRowField);

        jPanel6.add(subsampleField);

        delimiterCombo.setEditable(true);
        jPanel6.add(delimiterCombo);

        jPanel1.add(jPanel6, java.awt.BorderLayout.CENTER);

        jPanel7.setLayout(new java.awt.GridLayout(4, 0));

        importNamesCheck.setText("Import column names");
        jPanel7.add(importNamesCheck);

        subsampleCheck.setText("Subsample rows during import");
        jPanel7.add(subsampleCheck);

        limitSizeCheck.setText("Limit number of rows imported");
        jPanel7.add(limitSizeCheck);

        forceTextCheck.setText("Force Text data import");
        jPanel7.add(forceTextCheck);

        jPanel1.add(jPanel7, java.awt.BorderLayout.SOUTH);

        jPanel8.add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel2.setPreferredSize(new java.awt.Dimension(458, 230));
        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        dataScroller.setViewportView(dataTable);

        jPanel2.add(dataScroller, java.awt.BorderLayout.CENTER);

        jPanel8.add(jPanel2, java.awt.BorderLayout.CENTER);

        add(jPanel8, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout(10, 0));

        jLabel6.setText("Selected file:");
        jPanel3.add(jLabel6, java.awt.BorderLayout.WEST);

        jPanel3.add(fileNameLabel, java.awt.BorderLayout.CENTER);

        add(jPanel3, java.awt.BorderLayout.SOUTH);

    }// </editor-fold>//GEN-END:initComponents

    /** Do the actual import */
    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
        doImport();
    }//GEN-LAST:event_importButtonActionPerformed

    /** Show a file dialog to select a data set */
    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.showOpenDialog(this);
        if(chooser.getSelectedFile()!=null){
            fileSelected = true;
            fileNameLabel.setText(chooser.getSelectedFile().getName());
            importButton.setEnabled(true);
            dataImported = false;                   
            selectedFile = chooser.getSelectedFile();
            doImport();
        }
    }//GEN-LAST:event_selectButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField columnNameRowField;
    private javax.swing.JScrollPane dataScroller;
    private javax.swing.JTable dataTable;
    private javax.swing.JComboBox delimiterCombo;
    private javax.swing.JTextField endRowField;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JCheckBox forceTextCheck;
    private javax.swing.JButton importButton;
    private javax.swing.JCheckBox importNamesCheck;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JCheckBox limitSizeCheck;
    private javax.swing.JButton selectButton;
    private javax.swing.JTextField startRowField;
    private javax.swing.JCheckBox subsampleCheck;
    private javax.swing.JTextField subsampleField;
    // End of variables declaration//GEN-END:variables
    
}
