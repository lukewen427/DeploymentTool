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

import org.pipeline.core.drawing.ConnectionModel;
import org.pipeline.core.drawing.ConnectionModelRenderer;
import org.pipeline.core.drawing.DrawingException;
import org.pipeline.core.drawing.layout.ConnectionModelPosition;
import org.pipeline.core.drawing.layout.DrawingLayout;

import java.awt.*;
import java.util.Vector;
// </editor-fold>

/**
 * This class provides the default drawing for a LinkModel on a display.
 * @author  hugo
 */
public class DefaultConnectionRenderer implements ConnectionModelRenderer {
    /** Connection to be rendered */
    private ConnectionModel connection = null;
    
    /** Selected handle size */
    private int handleSize = 3;

    /** Drawing render settings */
    private DrawingRenderSettings settings;

    /** Creates a new instance of DefaultLinkView */
    public DefaultConnectionRenderer(DrawingRenderSettings settings) {
        this.settings = settings;
    }

    /** Set the drawing render settings */
    public void setSettings(DrawingRenderSettings settings){
        this.settings = settings;
    }
    
    /** Draw the connection */
    private void drawConnection(Graphics2D g, DrawingLayout layout, boolean selected) throws DrawingException {
        if(connection!=null){           
            ConnectionModelPosition pos = layout.getLocationData(connection);
            Vector points = pos.getAllPoints();
            int size = points.size();
            if(size>1){
                Point p1;
                Point p2;
                
                for(int i=1;i<size;i++){
                    p1 = (Point)points.elementAt(i-1);
                    p2 = (Point)points.elementAt(i);
                    
                    if(selected){
                        g.setColor(settings.getSelectedLinkColor());
                    } else {
                        g.setColor(settings.getLinkColor());
                    }
                    g.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
                    
                    if(selected){
                        g.fillRect((int)p2.getX() - handleSize, (int)p2.getY() - handleSize, handleSize * 2, handleSize * 2);
                    }
                }   
            }
        }
    }
    
    // =========================================================================
    // ConnectionModelRenderer implementation
    // =========================================================================
    
    /** Set the connection to be rendered */
    public void setConnection(ConnectionModel connection) {
        this.connection = connection;
    }

    /** Return the connection stored by this renderer */
    public ConnectionModel getConnection() {
        return connection;
    }

    /** Render the connection onto a display */
    public void render(Graphics2D display, DrawingLayout layout, boolean selected) throws DrawingException {
        drawConnection(display, layout, selected);
    }
}
