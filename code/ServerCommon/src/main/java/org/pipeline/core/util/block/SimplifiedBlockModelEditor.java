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

import org.pipeline.core.data.DataMetaData;
import org.pipeline.core.drawing.gui.DefaultBlockEditor;
import org.pipeline.core.util.DrawingDataUtilities;
import org.pipeline.gui.xmlstorage.XmlDataStoreEditor;

import java.awt.*;
import java.util.Enumeration;

/**
 * This class provides the editor panel for a SimplifiedBlock model. 
 * It just places a collection of editors on a panel for each
 * item in the blocks getProperties() object.
 * @author hugo
 */
public class SimplifiedBlockModelEditor extends DefaultBlockEditor {
    /** Property editor */
    private XmlDataStoreEditor editor;
    
    /** Maximum permissable height */
    private int maxHeight = 500;
    
    /** Creates a new instance of SimplifiedBlockModelEditor */
    public SimplifiedBlockModelEditor() {
        super();
        setPreferredSize(new java.awt.Dimension(400, 400));
    }
    
    /** Update the block */
    public void finishEdit(){
        editor.updateValues();
    }
    
    /** Block has been executed */
    public void blockExecuted(){
        // Add any required meta-data to the editor
        if(getBlock()!=null){
            SimplifiedBlockModel block = (SimplifiedBlockModel)getBlock();
            Enumeration e = block.getInputMetaDataMappingKeys();
            String propertyName;
            String inputName;
            DataMetaData metaData;
            
            while(e.hasMoreElements()){
                propertyName = e.nextElement().toString();
                inputName = block.getMetaDataMapping(propertyName);
                try {
                    metaData = DrawingDataUtilities.getInputData(block.getInput(inputName)).getMetaData();
                    editor.setHelperObject(propertyName, metaData);
                } catch (Exception ex){
                    // TODO: Send to correct logger
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
    
    /** Create the GUI components */
    public void createComponents(){
        editor = new XmlDataStoreEditor();
        if(getBlock()!=null){
            editor.setStore(((SimplifiedBlockModel)getBlock()).getProperties());
        }
        int size = editor.getTotalEditorHeight();
        if(size<maxHeight){
            setPreferredSize(new java.awt.Dimension(400, size + 20));
        } else {
            setPreferredSize(new java.awt.Dimension(400, maxHeight + 20));
        }
        add(editor, BorderLayout.CENTER);
        blockExecuted();
    }
}
