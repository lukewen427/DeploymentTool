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

// <editor-fold defaultstate="collapsed" desc=" Imports ">

import org.pipeline.core.drawing.*;

import java.awt.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
// </editor-fold>

/**
 * This class stores position data for every object in a Drawing.
 * @author  hugo
 */
public class DrawingLayout {
    /** Store of location data */
    private Hashtable locations = new Hashtable();
    
    /** Store of objects indexed by location */
    private Hashtable objects = new Hashtable();
    
    /** Drawing being edited */
    private DrawingModel drawing = null;
    
    /** Distance from a port to count as a hit */
    private int portHitDistance = 15;
    
    /** Grid pixel spacing */
    private static int gridSize = 5;
    
    /** DrawingLayout listeners */
    private Vector listeners = new Vector();
    
    /** Creates a new instance of DrawingLayout */
    public DrawingLayout(DrawingModel drawing) {
        this.drawing = drawing;
    }
    
    /** Add a listener */
    public void addDrawingLayoutListener(DrawingLayoutListener listener){
        if(!listeners.contains(listener)){
            listeners.addElement(listener);
        }
    }
    
    /** Remove a listener */
    public void removeDrawingLayoutListener(DrawingLayoutListener listener){
        if(listeners.contains(listener)){
            listeners.removeElement(listener);
        }
    }
    
    /** Notify a change */
    public void notifyLayoutChange(){
        Enumeration e = listeners.elements();
        while(e.hasMoreElements()){
            ((DrawingLayoutListener)e.nextElement()).layoutChanged();
        }
    }
    
    /** Constrain a value to fit within the grid */
    public static int constrainValue(int value){
        //return Math.round((float)((value / gridSize) * gridSize));
        return value;
    }
    
    /** Clear all the location data */
    public void clear(){
        locations.clear();
        objects.clear();
    }
    
    /** Add location for an object */
    public void addLocationData(DrawingObject object, ModelPosition data){
        data.setDrawingObject(object);
        data.setParentLayout(this);
        
        if(locations.containsKey(object)){
            locations.remove(object);
        }
        locations.put(object, data);
        
        if(objects.containsKey(data)){
            objects.remove(data);
        }
        objects.put(data, object);
    }
    
    /** Remove location data for an object */
    public void removeLocationData(DrawingObject object) {
        if(locations.containsKey(object)){
            if(objects.containsKey(locations.get(object))){
                objects.remove(locations.get(object));
            }
            locations.remove(object);
        }
    }
    
    /** Get the location data from an object */
    public BlockModelPosition getLocationData(BlockModel block) throws DrawingException {
        if(locations.containsKey(block)){
            return (BlockModelPosition)locations.get(block);
        } else {
            throw new DrawingException("Location data not available for block");
        }
    }
    
    /** Get location data for a connection */
    public ConnectionModelPosition getLocationData(ConnectionModel connection) throws DrawingException {
        if(locations.containsKey(connection)){
            return (ConnectionModelPosition)locations.get(connection);
        } else {
            throw new DrawingException("Location data not available for block");
        }
    }
    
    /** Return the drawing object that is underneath a particular point */
    public DrawingObject getObjectAtLocation(int x, int y){
        Enumeration l = locations.elements();
        ModelPosition p;
        while(l.hasMoreElements()){
            p = (ModelPosition)l.nextElement();
            if(p.pointWithinObject(x, y)){
                if(objects.containsKey(p)){
                    return (DrawingObject)objects.get(p);
                }
            }
        }
        // Nothing found
        return null;
    }
    
    /** Is there a selected object underneath a particular point */
    public boolean selectedObjectAtLocation(int x, int y){
        Enumeration selection = drawing.getSelectedObjects();
        ModelPosition p;
        DrawingObject obj;
        
        while(selection.hasMoreElements()){
            obj = (DrawingObject)selection.nextElement();
            if(locations.containsKey(obj)){
                p = (ModelPosition)locations.get(obj);
                if(p.pointWithinObject(x, y)){
                    return true;
                }
            }
        }
        // Nothing found
        return false;
    }
    
    /** Return a set of objects lying within a rectangle */
    public Vector getObjectsWithinRectangle(int x, int y, int width, int height){
        Vector list = new Vector();
        ModelPosition p;
        DrawingObject o;
        Enumeration objs = objects.elements();
        
        while(objs.hasMoreElements()){
            o = (DrawingObject)objs.nextElement();
            if(locations.containsKey(o)){
                p = (ModelPosition)locations.get(o);
                if(p.objectWithinBounds(x, y, width, height)){
                    list.add(o);
                }
            }
        }
        return list;
    }
    
    /** Move all the selected objects by a specified offset */
    public void moveSelectedObjects(int xOffset, int yOffset){
        Enumeration selected = drawing.getSelectedObjects();
        ModelPosition p;
        DrawingObject obj;
        
        while(selected.hasMoreElements()){
            obj = (DrawingObject)selected.nextElement();
            if(locations.containsKey(obj)){
                p = (ModelPosition)locations.get(obj);
                p.moveBy(xOffset, yOffset);
            }
            
        }
    }
    
    /** Get the port that the cursor is over */
    public PortModel getPortAtPoint(int x, int y){
        PortModel port = null;
        PortModel locatedPort = null;
        //DrawingObject obj = getObjectAtLocation(x, y);
        Point point;
        BlockModelPosition pos;
        int distance;
        int minDistance = Integer.MAX_VALUE;
        
        Enumeration blocks = drawing.blocks();
        BlockModel block;
        
        while(blocks.hasMoreElements()){
            block = (BlockModel)blocks.nextElement();
        
            if(locations.containsKey(block)){
                Enumeration inputs = block.inputs();
                pos = (BlockModelPosition)locations.get(block);
                
                // Look for an input
                while(inputs.hasMoreElements()){
                    port = (PortModel)inputs.nextElement();
                    point = PortPositionCalculator.locatePortCentre(port, pos);
                    distance = PortPositionCalculator.distanceFromPoint(x, y, point); 
                    if(distance<=minDistance){
                        locatedPort = port;
                        minDistance = distance;
                    }
                }
                
                // Look for an output
                Enumeration outputs = block.outputs();
                while(outputs.hasMoreElements()){
                    port = (PortModel)outputs.nextElement();
                    point = PortPositionCalculator.locatePortCentre(port, pos);
                    distance = PortPositionCalculator.distanceFromPoint(x, y, point); 
                    if(distance<=minDistance){
                        locatedPort = port;
                        minDistance = distance;
                    }
                }                
            }
        }
        
        // Return a port if it was in range
        if(minDistance<=portHitDistance){
            return locatedPort;
        } else {
            return null;
        }
    }
    
    /** Calculate the dimension that incorporates all of the objects in the drawing */
    public Dimension getDrawingBounds(){      
        int maxX = 0;
        int maxY = 0;
        int x;
        int y;
        
        Enumeration e = locations.elements();
        ModelPosition p;
        while(e.hasMoreElements()){
            p = (ModelPosition)e.nextElement();
            x = p.getMaximumX();
            y = p.getMaximumY();
            
            if(x>maxX){
                maxX = x;
            }
            
            if(y>maxY){
                maxY = y;
            }
        }
        return new Dimension(maxX, maxY);
    }
}
