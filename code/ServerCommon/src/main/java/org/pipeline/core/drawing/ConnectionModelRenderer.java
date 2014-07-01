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
 * This interface defines the behaviour of a renderer for a connection.
 * @author  hugo
 */
public interface ConnectionModelRenderer {
        /** Set the connection */
    public void setConnection(ConnectionModel connection);
    
    /** Get the block */
    public ConnectionModel getConnection();
    
    /** Re-render the connection onto an Object. This will be cast to the appropriate
     * type by the renderer. This method also gets a DrawingLayout object so
     * that the renderer can find out where to draw the connection */
    public void render(Graphics2D display, DrawingLayout layout, boolean selected) throws DrawingException;
}
