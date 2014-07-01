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

package org.pipeline.core.drawing;

/**
 * This interface defines the behaviour of an object that is used to edit
 * a BlockModel. Typically, this will be the panel that is displayed when
 * a user double clicks on a block.
 * @author  hugo
 */
public interface BlockModelEditor {
    /** Set the block being edited */
    public void setBlock(BlockModel block);
    
    /** Add a listener */
    public void addBlockModelEditorListener(BlockModelEditorListener listener);
    
    /** Remove a listener */
    public void removeBlockModelEditorListener(BlockModelEditorListener listener);
    
    /** Forcibly close the editor component. This is typically called when a block
     * is deleted with its editor open */
    public void closeEditor();
    
    /** Create the GUI. This means that the editor has been placed onto its
     * holder and has had its size initialised. The setBlock method will have
     * already been called and the getBlock() call will return a valid BlockModel
     * reference. */
    public void createComponents();
    
    /** Refresh the GUI. This will be called whenever a BlockModel is executed. */
    public void blockExecuted();
    
    /** Return a reference to the BlockModel being edited */
    public BlockModel getBlock();
    
    /** Finish the editing process and request that the editor closes. It shoud be
     * overridden by editor classes so that they can save their state to the block
     * when closing */
    public void finishEdit();
    
    /** Get the title caption */
    public String getEditorCaption();
}
