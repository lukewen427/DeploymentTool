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

package org.pipeline.core.drawing.layout;

import org.pipeline.core.drawing.InputPortModel;
import org.pipeline.core.drawing.OutputPortModel;
import org.pipeline.core.drawing.PortModel;

import java.awt.*;

/**
 * This class calculates the position of block IO ports.
 * @author  hugo
 */
public abstract class PortPositionCalculator {
    /** Get the centre of the port hit point */
    public static Point locatePortCentre(PortModel port, BlockModelPosition position){
        int x;
        int y;
        
        switch(port.getPortLocation()){
            case PortModel.LEFT_OF_BLOCK:
                x = position.getLeft() - getPortCentrePointDistanceFromBlockEdge(port);
                y = position.getTop() + getPortCentrePoint(port, position);
                break;
                
            case PortModel.TOP_OF_BLOCK:
                x = position.getLeft() + getPortCentrePoint(port, position);
                y = position.getTop() - getPortCentrePointDistanceFromBlockEdge(port);
                break;
                
            case PortModel.RIGHT_OF_BLOCK:
                x = position.getLeft() + position.getWidth() + getPortCentrePointDistanceFromBlockEdge(port);
                y = position.getTop() + getPortCentrePoint(port, position);
                break;
                
            case PortModel.BOTTOM_OF_BLOCK:
                x = position.getLeft() + getPortCentrePoint(port, position);
                y = position.getTop() + position.getHeight() + getPortCentrePointDistanceFromBlockEdge(port);
                break;
                
            default:
                x = position.getLeft();
                y = position.getTop();
        }
        
        return new Point(x, y);
    }
    
    /** Calculate the co-ordinates of a port */
    public static Point locatePort(PortModel port, BlockModelPosition position){
        int x;
        int y;
        
        switch(port.getPortLocation()){
            case PortModel.LEFT_OF_BLOCK:
                x = position.getLeft();
                y = position.getTop() + getPortCentrePoint(port, position);
                break;
                
            case PortModel.TOP_OF_BLOCK:
                x = position.getLeft() + getPortCentrePoint(port, position);
                y = position.getTop();
                break;
                
            case PortModel.RIGHT_OF_BLOCK:
                x = position.getLeft() + position.getWidth();
                y = position.getTop() + getPortCentrePoint(port, position);
                break;
                
            case PortModel.BOTTOM_OF_BLOCK:
                x = position.getLeft() + getPortCentrePoint(port, position);
                y = position.getTop() + position.getHeight();
                break;
                
            default:
                x = position.getLeft();
                y = position.getTop();
        }
        
        return new Point(x, y);        
    }    
   
    /** Get the distance of the centre point of a port from the edge of the block */
    public static int getPortCentrePointDistanceFromBlockEdge(PortModel port){
        if(port instanceof InputPortModel){
            return BlockModelPosition.DEFAULT_PORT_SIZE;
        } else if(port instanceof OutputPortModel){
            return BlockModelPosition.DEFAULT_PORT_SIZE / 2;
        } else {
            return 0;
        }
    }
    
    /** Location the centre point of a port */
    public static int getPortCentrePoint(PortModel port, BlockModelPosition position){
        if(port.getPortLocation()==PortModel.TOP_OF_BLOCK || port.getPortLocation()==PortModel.BOTTOM_OF_BLOCK){
            return (int)(position.getWidth() * ((double)port.getPortOffset() / 100.0));
        } else {
            return (int)(position.getHeight() * ((double)port.getPortOffset() / 100.0));
        }
    }    
    
    /** Return the distance from a point */
    public static int distanceFromPoint(int x, int y, Point point){
        double distance = Math.sqrt(Math.pow((x - point.getX()), 2) + Math.pow((y - point.getY()), 2));
        return (int)distance;
    }
}
