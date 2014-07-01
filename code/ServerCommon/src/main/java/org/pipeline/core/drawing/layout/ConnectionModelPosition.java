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

import org.pipeline.core.drawing.ConnectionModel;
import org.pipeline.core.drawing.DrawingException;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Enumeration;
import java.util.Vector;
// </editor-fold>

/** 
 * This class contains position data for points within a connection model.
 * @author  hugo
 */
public class ConnectionModelPosition extends ModelPosition implements XmlStorable {
    /** Vector of Points along the connection */
    private Vector pointList = new Vector();
       
    /** Hit distance to consider a point within the bounds of a connection */
    private int hitDistance = 5;
    
    /** Creates a new instance of ConnectionModelPosition */
    public ConnectionModelPosition() {
    }
    
    /** Is a point within the object. This is done by considering a rectangle around the 
     * x,y co-ordintae and checking whether a line drawn between two segments of the 
     * line tracing this connection intersects with this circle */
    public boolean pointWithinObject(int x, int y){
        Rectangle2D rect = new Rectangle2D.Double(x - hitDistance, y - hitDistance, hitDistance * 2, hitDistance * 2);
        Line2D line;
        Vector allPoints = getAllPoints();
        Point p1;
        Point p2;
        int size = allPoints.size();
        
        if(size>1){
            for(int i=1;i<size;i++){
                p1 = (Point)allPoints.elementAt(i-1);
                p2 = (Point)allPoints.elementAt(i);
                if(lineCrossesRectangle(p1, p2, rect)){
                    return true;
                }
            }
            
            // No intersections
            return false;
            
        } else {
            return false;
        }
    }
    
    /** Toggle a connection bend at a point within this connection. If there is no
     * bend near to the co-ordinates, one is added. If there is a bend near, it is
     * deleted */
    public void toggleConnectionBend(int x, int y){
        if(pointWithinObject(x, y)){
            try {
                Point point = new Point(DrawingLayout.constrainValue(x), DrawingLayout.constrainValue(y));
                Rectangle2D rect = new Rectangle2D.Double(x - hitDistance, y - hitDistance, hitDistance * 2, hitDistance * 2);                
                ConnectionModel connection = (ConnectionModel)getDrawingObject();
                Point startPoint = PortPositionCalculator.locatePortCentre(connection.getSourcePort(), getParentLayout().getLocationData(connection.getSourcePort().getParentBlock()));
                Point endPoint = PortPositionCalculator.locatePortCentre(connection.getDestinationPort(), getParentLayout().getLocationData(connection.getDestinationPort().getParentBlock()));                

                // Put all the points into one vector
                Vector allPoints = new Vector();
                allPoints.addElement(startPoint);
                allPoints.addAll(pointList);
                allPoints.addElement(endPoint);

                // Is there already a point
                int index = findPointIndex(pointList, point);
                if(index!=-1){
                    // There is already a point, delete it
                    pointList.removeElementAt(index);
                    
                } else {
                    // Find out where the x,y point lies in this list so that the
                    // new point can be inserted in the right place
                    Point location = findPoint(allPoints, point);
                    if(location!=null){
                        if(location.equals(endPoint)){
                            // Just befor end
                            pointList.addElement(point);
                            
                        } else {
                            index = pointList.indexOf(location);
                            if(index!=-1){
                                pointList.add(index, point);
                            }
                        }
                    }

                }
                
            } catch (DrawingException e){
                System.out.println(e.getMessage());
            }
        }
        notifyLayoutChange();
    }
    
    /** Find the Point within a list of points that is immediately adjacent to a specified point */
    private Point findPoint(Vector points, Point point){
        Rectangle2D rect = new Rectangle2D.Double(point.getX() - hitDistance, point.getY() - hitDistance, hitDistance * 2, hitDistance * 2);
        Line2D line;
        
        if(points.size()>2){
            Point left;
            Point right;
            
            for(int i=1;i<points.size();i++){
                left = (Point)points.elementAt(i - 1);
                right = (Point)points.elementAt(i);
                line = new Line2D.Double(left, right);
                if(line.intersects(rect)){
                    return right;
                }
            }
            
        } else if(points.size()==2){
            return (Point)points.elementAt(1);
        } else {
            return null;
        }
        
        return null;
    }
    
    /** Find the index of the closest point from a Vector of points to a specified point */
    private int findPointIndex(Vector points, Point point){
        Rectangle2D rect = new Rectangle2D.Double(point.getX() - hitDistance, point.getY() - hitDistance, hitDistance * 2, hitDistance * 2);
        Point testPoint;
        
        for(int i=0;i<points.size();i++){
            testPoint = (Point)points.elementAt(i);
            if(rect.contains(testPoint)){
                return i;
            }
        }
        
        return -1;
    }
    
    /** Drag a point by a selected ammount if one exists at the specified location */
    public void dragPoint(int x, int y, int xOffset, int yOffset){
        int index = findPointIndex(pointList, new Point(x, y));
        if(index!=-1){
            Point point = (Point)pointList.elementAt(index);
            if(point!=null){
                point.setLocation(DrawingLayout.constrainValue(x + xOffset), DrawingLayout.constrainValue(y + yOffset));
            }
        }
        notifyLayoutChange();
    }
    
    /** Does a line drawn between two points intersect with a rectangle */
    private boolean lineCrossesRectangle(Point startPoint, Point endPoint, Rectangle2D rect){
        Line2D line = new Line2D.Double(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
        return rect.intersectsLine(line);
    }
    
    /** Is this object within bounds. This checks to see if both the start point and end point 
     * are within bounds. The intermediate points are ignored */
    public boolean objectWithinBounds(int x, int y, int width, int height){
        Rectangle2D rect = new Rectangle2D.Double((double)x, (double)y, (double)width, (double)height);
        ConnectionModel connection = (ConnectionModel)getDrawingObject();
        try {
            Point startPoint = PortPositionCalculator.locatePortCentre(connection.getSourcePort(), getParentLayout().getLocationData(connection.getSourcePort().getParentBlock()));
            Point endPoint = PortPositionCalculator.locatePortCentre(connection.getDestinationPort(), getParentLayout().getLocationData(connection.getDestinationPort().getParentBlock()));
            return (rect.contains(startPoint) && rect.contains(endPoint));

        } catch (DrawingException e){
            return false;
        }        
    }
    
    /** Move by a specified offset */
    public void moveBy(int xOffset, int yOffset){
        Point point;
        int newX;
        int newY;
        
        // Move all intermediate points
        for(int i=0;i<pointList.size();i++){
            point = (Point)pointList.elementAt(i);
            newX = (int)(point.getX() + xOffset);
            newY = (int)(point.getY() + yOffset);
                       
            int x = DrawingLayout.constrainValue(newX);
            int y = DrawingLayout.constrainValue(newY);

            pointList.setElementAt(new Point(x, y), i);
        }
        notifyLayoutChange();
    }        
    
    /** Get all the points. This includes the start and end points that are
     * calculated by locating the blocks at either end of the connection */
    public Vector getAllPoints(){
        try {
            Vector allPoints = new Vector();
            ConnectionModel connection = (ConnectionModel)getDrawingObject();
            Point startPoint = PortPositionCalculator.locatePortCentre(connection.getSourcePort(), getParentLayout().getLocationData(connection.getSourcePort().getParentBlock()));
            Point endPoint = PortPositionCalculator.locatePortCentre(connection.getDestinationPort(), getParentLayout().getLocationData(connection.getDestinationPort().getParentBlock()));                
             
            allPoints.addElement(startPoint);
            allPoints.addAll(pointList);
            allPoints.addElement(endPoint);
            return allPoints;
            
        } catch (DrawingException e){
            return new Vector();
        }
    }
    
    /** Constrain a point */
    private void constrainPoint(Point point){
        point.setLocation(DrawingLayout.constrainValue((int)point.getX()), DrawingLayout.constrainValue((int)point.getY()));
    }
    
    /** Return an Enumeration of points */
    public Enumeration points(){
        return pointList.elements();
    }
    
    /** Get the number of points */
    public int getPointCount(){
        return pointList.size();
    }
    
    /** Get a specific point */
    public Point getPoint(int point){
        return (Point)pointList.elementAt(point);
    }

    /** Load from a store */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        int pointCount = xmlDataStore.intValue("PointCount", 0);
        int x;
        int y;
        pointList.clear();
        
        for(int i=0;i<pointCount;i++){
            x = xmlDataStore.intValue("X" + i, 0);
            y = xmlDataStore.intValue("Y" + i, 0);
            pointList.addElement(new Point(x, y));
        }
    }

    /** Save to a store */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("ConnectionPosition");
        store.add("PointCount", getPointCount());
        for(int i=0;i<getPointCount();i++){
            store.add("X" + i, (int)getPoint(i).getX());
            store.add("Y" + i, (int)getPoint(i).getY());
        }
        return store;
    }   

    /** Get the maximum y co-ordinate */
    public int getMaximumY() {
        Vector points = getAllPoints();
        int maxY = 0;
        Point p;
        Enumeration e = points.elements();
        while(e.hasMoreElements()){
            p = (Point)e.nextElement();
            if(p.getY()>maxY){
                maxY = (int)p.getY();
            }
        }
        return maxY;
    }

    /** Get the maximum x co-ordinate */
    public int getMaximumX() {
        Vector points = getAllPoints();
        int maxX = 0;
        Point p;
        Enumeration e = points.elements();
        while(e.hasMoreElements()){
            p = (Point)e.nextElement();
            if(p.getX()>maxX){
                maxX = (int)p.getX();
            }
        }
        return maxX;        
    }

    /** Add a new point to the connection point list */
    public void addPoint(int x, int y){
        pointList.addElement(new Point(x, y));
    }
}
