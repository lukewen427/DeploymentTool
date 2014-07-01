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

package org.pipeline.core.util.block;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataMetaData;
import org.pipeline.core.data.manipulation.ColumnPicker;
import org.pipeline.core.drawing.gui.DefaultBlockEditor;
import org.pipeline.gui.data.manipulation.ColumnPickerCollectionEditor;
import org.pipeline.gui.data.manipulation.ColumnPickerCollectionEditorListener;

import javax.swing.*;
import java.awt.*;

/**
 * This class provides a standard block editor that works with a 
 * generic column picker collection block.
 * @author hugo
 */
public class ColumnPickerCollectionBlockEditor extends DefaultBlockEditor implements ColumnPickerConfigPanelListener, ColumnPickerCollectionEditorListener {
    /** Column picker config container */
    private JPanel pickerContainer;
    
    /** Column picker editor */
    private ColumnPickerCollectionEditor collectionEditor;
    
    /** Specific configuration panel for the picker */
    private ColumnPickerBlockConfigPanel configPanel;
    
    /** Preview panel if there is one */
    private ColumnPickerBlockPreviewPanel previewPanel;
    
    /** Split pane if a preview is required */
    private JSplitPane splitPane;
    
    /** Creates a new instance of ColumnPickerCollectionBlockEditor */
    public ColumnPickerCollectionBlockEditor() {
    }

    /** Update the block with the edit results */
    public void finishEdit() {
        if(configPanel!=null){
            configPanel.removeColumnPickerConfigPanelListener(this);
        }
        collectionEditor.removeColumnPickerCollectionEditorListener(this);
    }

    /** Create the GUI components */
    public void createComponents() {
        setLayout(new BorderLayout());
        ColumnPickerCollectionBlock block = (ColumnPickerCollectionBlock)getBlock();
        
        // Set of the picker config panel which is common to both
        // types of editor layout
        pickerContainer = new JPanel();
        pickerContainer.setLayout(new BorderLayout());
        collectionEditor = new ColumnPickerCollectionEditor(block.getColumnPickerClass());
        collectionEditor.setPickers(block.getPickers());
        collectionEditor.addColumnPickerCollectionEditorListener(this);
        pickerContainer.add(collectionEditor, BorderLayout.CENTER);
        configPanel = createConfigPanel();
        if(configPanel!=null){
            pickerContainer.add((JPanel)configPanel, BorderLayout.SOUTH);
            configPanel.addColumnPickerConfigPanelListener(this);
        }
        
        // Create the preview layout if required, otherwise use the standard
        // layout
        if(block.isPreviewRequired()){
            // Set up a previewed layout
            splitPane = new JSplitPane();
            splitPane.setLeftComponent(pickerContainer);
            
            previewPanel = createPreviewPanel();
            if(previewPanel!=null){
                splitPane.setRightComponent((JPanel)previewPanel);
            }
            add(splitPane, BorderLayout.CENTER);
            splitPane.setDividerLocation(0.4);
        } else {
            // Create a plain non-previewed layout
            add(pickerContainer, BorderLayout.CENTER);
        }
        blockExecuted();
    }

    /** Create the preview panel */
    private ColumnPickerBlockPreviewPanel createPreviewPanel(){
        try {
            ColumnPickerCollectionBlock block = (ColumnPickerCollectionBlock)getBlock();
            Object obj = block.getPreviewPanelClass().newInstance();
            if(obj instanceof JPanel && obj instanceof ColumnPickerBlockPreviewPanel){
                return (ColumnPickerBlockPreviewPanel)obj;
            } else {
                System.out.println("Invalid column picker preview panel class");
                return null;
            }
        } catch (Exception e){
            System.out.println("Cannot create preview panel");
            return null;
        }
    }
    
    /** Create the picker config panel */
    private ColumnPickerBlockConfigPanel createConfigPanel(){
        try {
            ColumnPickerCollectionBlock block = (ColumnPickerCollectionBlock)getBlock();
            Object obj = block.getPickerConfigPanelClass().newInstance();
            if(obj instanceof JPanel && obj instanceof ColumnPickerBlockConfigPanel){
                return (ColumnPickerBlockConfigPanel)obj;
            } else {
                System.out.println("Invalid column picker config panel class");
                return null;
            }
            
        } catch (Exception e){
            System.out.println("Cannot create config panel");
            return null;
        }
    }
    
    /** The block has been executed */
    public void blockExecuted() {
        try {
            ColumnPickerCollectionBlock block = (ColumnPickerCollectionBlock)getBlock();
            DataMetaData metaData = block.getInputData().getMetaData();
            collectionEditor.setMetaData(metaData);
            if(collectionEditor.getSelectedPicker()!=null){
                displayPreview(collectionEditor.getSelectedPicker());
            } else {
                clearPreview();
            }
        } catch (Exception e){
            e.printStackTrace();
        }        
    }
    
    /** Clear the preview display */
    private void clearPreview(){
        if(previewPanel!=null){
            previewPanel.clearDisplay();;
        }
    }

    /** Plot results for a specific picker */
    private void displayPreview(ColumnPicker picker){
        if(picker!=null){
            ColumnPickerCollectionBlock block = (ColumnPickerCollectionBlock)getBlock();

            // Update the preview panel is there is one
            if(previewPanel!=null){
                // Use the picker to create the new columns
                try {
                    Data data = block.getInputData();
                    Column[] results = picker.pickColumns(data);
                    Column original = picker.getColumnReference(data);
                    previewPanel.displayResults(picker, original, results);
                } catch (Exception e){
                    previewPanel.clearDisplay();
                }            
            }  
        } else {
            if(previewPanel!=null){
                previewPanel.clearDisplay();
            }
        }
    }
    
    /** Picker selection has changed */
    public void pickerSelected(ColumnPicker picker) {
        // If there is a config panel, tell it that a new picker has been selected
        if(configPanel!=null){
            configPanel.setPicker(picker);
        }
        
        displayPreview(picker);
    }
    
    /** Update a picker */
    public void updatePicker(ColumnPicker picker){
        ColumnPickerCollectionBlock block = (ColumnPickerCollectionBlock)getBlock();
        
        if(configPanel!=null){
            configPanel.updatePicker(picker);
        }
        
        try {
            block.runPickers(block.getInputData());
        } catch (Exception e){
            e.printStackTrace();
        }
        
        displayPreview(picker);
    }
    
    /** A picker has been edited */
    public void configPanelPickerUpdated(ColumnPicker picker) {
        collectionEditor.updateSelectedPicker();
        
        ColumnPickerCollectionBlock block = (ColumnPickerCollectionBlock)getBlock();
        try {
            block.runPickers(block.getInputData());
        } catch (Exception e){
            e.printStackTrace();
        }
        
        displayPreview(picker);        
    }
}
