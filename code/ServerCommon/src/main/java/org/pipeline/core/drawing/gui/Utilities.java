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

import java.awt.*;
import java.awt.geom.Rectangle2D;


/**
 * This class provides utility methods for drawing blocks, ports etc
 * @author  hugo
 */
public abstract class Utilities {
    /** Create left pointing non-streaming port shape */
    public static Shape createLeftArrow(int x, int y, int dimension){
        Polygon p = new Polygon();
        p.addPoint(x + dimension, y - (int)(dimension / 2));
        p.addPoint(x, y);
        p.addPoint(x + dimension, y + (int)(dimension / 2));
        return p;
    }
    
    /** Create right pointing non-streaming port shape */
    public static Shape createRightArrow(int x, int y, int dimension){
        Polygon p = new Polygon();
        p.addPoint(x - dimension, y - (int)(dimension / 2));
        p.addPoint(x, y);
        p.addPoint(x - dimension, y + (int)(dimension / 2));
        return p;
    }
    
    /** Create upwards pointing non-streaming port shape */
    public static Shape createUpArrow(int x, int y,  int dimension){
        Polygon p = new Polygon();
        p.addPoint(x, y);
        p.addPoint(x + (int)(dimension / 2), y + dimension);
        p.addPoint(x - (int)(dimension / 2), y + dimension);
        return p;
    }
    
    /** Create downwards pointing non-streaming port shape */
    public static Shape createDownArrow(int x, int y, int dimension){
        Polygon p = new Polygon();
        p.addPoint(x, y);
        p.addPoint(x - (int)(dimension / 2), y - dimension);
        p.addPoint(x + (int)(dimension / 2), y - dimension);
        return p;
    }
    
    /** Calculate the title location point */
    public static Point getCentredTextLocation(Graphics2D g, Font font, String text, int top, int left, int width, int height){
        FontMetrics metric = g.getFontMetrics(font);
        int textHeight = (int)metric.getStringBounds(text, g).getHeight();
        int topPos = top + height - (int)((height - textHeight) / 2) - (int)(textHeight/5);
        int textWidth = (int)metric.getStringBounds(text, g).getWidth();
        int diff = width - textWidth;
        if(diff<=0){
            // Align at left
            return new Point(left, topPos);
        } else {
            // Centre
            return new Point(left + (int)(diff / 2), topPos);
        }
    }    
    
    /** Get Bounding box for some text */
    public static Rectangle2D getTextBounds(Graphics2D g, Font font, String text){
        FontMetrics metric = g.getFontMetrics(font);
        return metric.getStringBounds(text, g);
    }
}
