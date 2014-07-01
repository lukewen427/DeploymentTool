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

package org.pipeline.core.drawing.gui;

// <editor-fold defaultstate="collapsed" desc=" Imports ">

import org.pipeline.core.drawing.BlockModel;
import org.pipeline.core.drawing.BlockModelEditor;
import org.pipeline.core.drawing.BlockModelEditorListener;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;
// </editor-fold>

/**
 * This class provides the default editor panel for a block. It extends JPanel,
 * so it needs to be placed onto another window to be useful. Any user components
 * can be added to the North, East and West of this panel. The South is used for 
 * the OK button and should not be modified.
 * @author  hugo
 */
public abstract class DefaultBlockEditor extends JPanel implements BlockModelEditor {
    /** Block being edited */
    private BlockModel block = null;
        
    /** Title */
    private String editorCaption = "Block Editor";
    
    /** Listeners */
    private Vector listeners = new Vector();
    
    /** Creates a new instance of DefaultBlockEditor */
    public DefaultBlockEditor() {
        super();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(640, 480));
    }
    
    /** Set the editor caption */
    public void setEditorCaption(String editorCaption){
        this.editorCaption = editorCaption;
    }
    
    /** Notify listeners that the editor wants to close */
    private void notifyEditorCloseRequest(){
        Enumeration e = listeners.elements();
        while(e.hasMoreElements()){
            ((BlockModelEditorListener)e.nextElement()).editorCloseRequest(this);
        }
    }
    
    // =========================================================================
    // BlockModelEditor implementation
    // =========================================================================
    /** Get the title caption */
    public String getEditorCaption() {
        return editorCaption;
    }
    
    /**
     * Refresh the GUI. This will be called whenever a BlockModel is executed. 
     */
    public abstract void blockExecuted();

    /**
     * Return a reference to the BlockModel being edited 
     */
    public BlockModel getBlock() {
        return block;
    }

    /**
     * Set the block being edited 
     */
    public void setBlock(BlockModel block) {
        this.block = block;
    }

    /**
     * Create the GUI. This means that the editor has been placed onto its
     * holder and has had its size initialised. The setBlock method will have
     * already been called and the getBlock() call will return a valid BlockModel
     * reference. 
     */
    public abstract void createComponents();
    
    /** Finish the editing process. This is called by the block itself and should not
     * be used as a way for user code to close the editor window. This should be done
     * by calling the releaseEditor() method on the block being edited. This method is
     * also responsible for closing and cleaning up the editor */
    public abstract void finishEdit();

    /** Add a listener */
    public void addBlockModelEditorListener(BlockModelEditorListener listener) {
        if(!listeners.contains(listener)){
            listeners.addElement(listener);
        }
    }

    /** Remove a listener */
    public void removeBlockModelEditorListener(BlockModelEditorListener listener) {
        if(listeners.contains(listener)){
            listeners.removeElement(listener);
        }
    }
    
    /** Forcibly close the editor component. This is typically called when a block
     * is deleted with its editor open */
    public void closeEditor() {
        notifyEditorCloseRequest();
    }
}
