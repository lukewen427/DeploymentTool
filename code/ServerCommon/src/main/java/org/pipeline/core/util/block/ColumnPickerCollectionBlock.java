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

import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.manipulation.ColumnPickerCollection;
import org.pipeline.core.drawing.BlockExecutionException;
import org.pipeline.core.drawing.BlockExecutionReport;
import org.pipeline.core.drawing.DrawingException;
import org.pipeline.core.drawing.model.DefaultBlockModel;
import org.pipeline.core.util.DrawingDataUtilities;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class provides a base block class that contains a set of column splitters
 * and an associated editor panel. It lets a generic column picker block be
 * created easily.
 * @author hugo
 */
public class ColumnPickerCollectionBlock extends DefaultBlockModel {
    /** Column pickers used by this block */
    private ColumnPickerCollection pickers = new ColumnPickerCollection();
    
    /** Column picker class to use in the editor */
    private Class columnPickerClass;
    
    /** Auxiliary panel class to use to configure individual pickers */
    private Class pickerConfigPanelClass;
    
    /** Does this block require a preview panel on the right of the editor */
    private boolean previewRequired = false;
    
    /** Class for the preview panel */
    private Class previewPanelClass;
    
    /** Name of the input that supplies the data */
    private String inputPortName = "input-data";
    
    /** Name of the output to send the results to */
    private String primaryOutputPortName = "output-data";
    
    /** Creates a new instance of ColumnPickerCollectionBlock */
    public ColumnPickerCollectionBlock() throws DrawingException {
        super();
        setEditorClass(ColumnPickerCollectionBlockEditor.class);
    }
    
    /** Get the column picker class */
    public Class getColumnPickerClass(){
        return columnPickerClass;
    }
    
    /** Set the column picker class */
    public void setColumnPickerClass(Class columnPickerClass){
        this.columnPickerClass = columnPickerClass;
        
    }
    
    /** Set the picker configuration panel class */
    public void setPickerConfigPanelClass(Class pickerConfigPanelClass){
        this.pickerConfigPanelClass = pickerConfigPanelClass;
    }
    
    /** Get the picker configuration panel class */
    public Class getPickerConfigPanelClass(){
        return pickerConfigPanelClass;
    }
    
    /** Get the column picker collection */
    public ColumnPickerCollection getPickers(){
        return pickers;
    }
    
    /** Get the input data from the input connection */
    public Data getInputData() throws DrawingException {
        return DrawingDataUtilities.getInputData(getInput(inputPortName));
    }
    
    /** Run the pickers with a set of data */
    protected Data runPickers(Data rawData) throws DataException, IndexOutOfBoundsException {
        Data results = pickers.extractData(rawData);
        return results;
    }
    
    /** Is a results preview required */
    public boolean isPreviewRequired() {
        return previewRequired;
    }
    
    /** Set whether a results preview is required */
    public void setPreviewRequired(boolean previewRequired) {
        this.previewRequired = previewRequired;
    }

    /** Get the class of the preview panel object */
    public Class getPreviewPanelClass() {
        return previewPanelClass;
    }

    /** Set the class of the preview panel object */
    public void setPreviewPanelClass(Class previewPanelClass) {
        this.previewPanelClass = previewPanelClass;
    }
    
    /** Save this block */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("Pickers", pickers);
        return store;
    }
    
    /** Recreate this block */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        super.recreateObject(xmlDataStore);
        pickers = (ColumnPickerCollection)xmlDataStore.xmlStorableValue("Pickers");
    }

    /** Get the name of the input port */
    public String getInputPortName() {
        return inputPortName;
    }

    /** Set the name of the input port */
    public void setInputPortName(String inputPortName) {
        this.inputPortName = inputPortName;
    }

    /** Get the name of the main output that contains the results */
    public String getPrimaryOutputPortName() {
        return primaryOutputPortName;
    }

    /** Set the name of the main output that contains the results */
    public void setPrimaryOutputPortName(String primaryOutputPortName) {
        this.primaryOutputPortName = primaryOutputPortName;
    }

    /** Execute this block */
    public BlockExecutionReport execute() throws BlockExecutionException {
        Data data = null;
        Data outputData = null;
        try {
            data = DrawingDataUtilities.getInputData(getInput(inputPortName));
        } catch (Exception e){
            return new BlockExecutionReport(this, BlockExecutionReport.INPUT_DATA_ERROR);
            
        }
        
        // Run the pickers
        try {
            outputData = runPickers(data);
        } catch (Exception e){
            return new BlockExecutionReport(this, BlockExecutionReport.INTERNAL_ERROR, e.getMessage());
        }
        
        // Set the output data
        try {
            DrawingDataUtilities.setOutputData(getOutput(primaryOutputPortName), outputData);
        } catch (Exception e){
            return new BlockExecutionReport(this, BlockExecutionReport.OUTPUT_DATA_ERROR);
        }
        return new BlockExecutionReport(this);
    }
}