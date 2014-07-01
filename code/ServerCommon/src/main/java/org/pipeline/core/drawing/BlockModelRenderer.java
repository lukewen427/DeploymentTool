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

import org.pipeline.core.drawing.layout.DrawingLayout;

import java.awt.*;
/**
 * This interface defines behaviour of a class that renders a block onto some
 * form of drawing surface.
 * @author  hugo
 */
public interface BlockModelRenderer {
    /** Set the block */
    public void setBlock(BlockModel block);
    
    /** Get the block */
    public BlockModel getBlock();
    
    /** Re-render the block onto an Object. This will be cast to the appropriate
     * type by the renderer. This method also passes a DrawingLayout that is used\
     * to lookup the position to render the block at */
    public void render(Graphics2D display, DrawingLayout layout, BlockExecutionReport status) throws DrawingException;
    
    /** Set the drawing view */
    public void setDrawingView(Object drawingView);
}
