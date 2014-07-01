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

package org.pipeline.core.drawing.spanning;

// <editor-fold defaultstate="collapsed" desc=" Imports ">
import org.pipeline.core.drawing.BlockModel;
// </editor-fold>

/**
 * This thread is started to update a drawing editor on a separate thread. It
 * can be started using the Swing.invokeLater style method call.
 * @author hugo
 */
public class BlockEditorUpdater implements Runnable {
    /** Block that will have its editor window updated */
    private BlockModel block = null;
    
    /** Creates a new instance of BlockEditorUpdater */
    public BlockEditorUpdater(BlockModel block) {
        this.block = block;
    }
    
    /** Run this thread */
    public void run(){
        if(block!=null && block.getEditor()!=null){
            block.getEditor().blockExecuted();
        }
    }
}
