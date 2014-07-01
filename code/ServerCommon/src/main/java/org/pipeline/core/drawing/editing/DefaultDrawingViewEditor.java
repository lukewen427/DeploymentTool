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

package org.pipeline.core.drawing.editing;

import org.pipeline.core.drawing.*;
import org.pipeline.core.drawing.gui.DefaultDrawingView;
import org.pipeline.core.drawing.layout.BlockModelPosition;
import org.pipeline.core.drawing.layout.ConnectionModelPosition;
import org.pipeline.core.drawing.layout.DrawingLayout;
import org.pipeline.core.drawing.layout.PortPositionCalculator;

import java.awt.*;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This class provides editing capabilities for a DefaultDrawingView.
 * @author  hugo
 */
public class DefaultDrawingViewEditor implements MouseListener, MouseMotionListener, MouseWheelListener {
    /** Nothing happening */
    public static final int EDITOR_IDLE = 0;
    
    /** Dragging an object or collection of objects */
    public static final int DRAGGING_OBJECTS = 1;
    
    /** Drawing a box */
    public static final int DRAWING_BOX = 2;
    
    /** Connecting two ports together */
    public static final int CONNECTING_PORTS = 3;
    
    /** Default drawing view being edited */
    private DefaultDrawingView view = null;
       
    /** Drawing model object being edited */
    private DrawingModel drawing = null;
    
    /** Current drawing state */
    private int currentState = EDITOR_IDLE;
    
    /** X position of point where the last event started */
    private int xStart = 0;
    
    /** Y position of point where the last event started */
    private int yStart = 0;
    
    /** Old x-position store */
    private int xOld = 0;
    
    /** Old y-position store */
    private int yOld = 0;
    
    /** Start port for connecting */
    private PortModel startPort = null;
    
    /** Drop target for drawing */
    private DropTargetListener dndListener = null;

    /** Is editing enabled */
    private boolean editingEnabled = true;

    /** Creates a new instance of DefaultDrawingViewEditor */
    public DefaultDrawingViewEditor(DefaultDrawingView view) {
        this.view = view;
        if(view!=null){
            view.addMouseListener(this);
            view.addMouseMotionListener(this);
            view.addMouseWheelListener(this);
            this.drawing = view.getDrawing();
            dndListener = null;
        }
    }

    /** Creates a new instance of DefaultDrawingViewEditor */
    public DefaultDrawingViewEditor(DefaultDrawingView view, DropTargetListener dndListener) {
        this.view = view;
        if(view!=null){
            view.addMouseListener(this);
            view.addMouseMotionListener(this);
            view.addMouseWheelListener(this);
            this.drawing = view.getDrawing();
            this.dndListener = dndListener;
        }
    }
    
    /** Delete the selected objects in the drawing */
    public void deleteSelectedObjects() throws DrawingException {
        if(editingEnabled){
            Vector selected = new Vector();
            Enumeration e = drawing.getSelectedObjects();
            while(e.hasMoreElements()){
                selected.addElement(e.nextElement());
            }
            drawing.deleteDrawingObjects(selected);
            drawing.redrawRequest();
        }
    }
    
    // =========================================================================
    // MouseMotionListener implementation
    // =========================================================================
    
    /** Mouse has moved across the drawing */
    public void mouseMoved(MouseEvent e) {

    }

    /** Mouse has been dragged across the drawing */
    public void mouseDragged(MouseEvent e) {
        if(currentState==DRAWING_BOX){
            int x1, x2, y1, y2;
            if(e.getX()<xStart){
                x1 = e.getX();
                x2 = xStart;
            } else {
                x1 = xStart;
                x2 = e.getX();
            }
            
            if(e.getY()<yStart){
                y1 = e.getY();
                y2 = yStart;
            } else {
                y1 = yStart;
                y2 = e.getY();
            }
            view.setSelectionRectangle(x1, y1, x2 - x1, y2 - y1);
            drawing.redrawRequest();
            
        } else if(currentState==DRAGGING_OBJECTS && editingEnabled){
            // Calculate movement amount
            int xOffset = e.getX() - xStart;
            int yOffset = e.getY() - yStart;
            
            // Move everything
            if(drawing.getSelectedObjectCount()==1){
                DrawingObject s = (DrawingObject)drawing.getSelectedObjects().nextElement();
                if(s instanceof BlockModel){
                    // Drag a single block
                    drawing.getDrawingLayout().moveSelectedObjects(xOffset, yOffset);
                    
                } else {
                    // Special case for dragging a connection - only one point is to
                    // be moved when doing a connection drag.
                    try {
                        ConnectionModelPosition pos = drawing.getDrawingLayout().getLocationData((ConnectionModel)s);
                        if(pos!=null){
                            pos.dragPoint(xStart, yStart, xOffset, yOffset);
                        }
                        
                    } catch (DrawingException ex){
                        System.out.println(ex.getMessage());
                    }
                }
                
            } else {
                drawing.getDrawingLayout().moveSelectedObjects(xOffset, yOffset);
            }
            
            // Reset start points
            xStart = e.getX();
            yStart = e.getY();
            
            // Repaint the drawing
            drawing.redrawRequest();
            
        } else if(currentState==CONNECTING_PORTS && editingEnabled){
            view.setLinkEnd(new Point(e.getX(), e.getY()));
            PortModel possibleEnd = drawing.getDrawingLayout().getPortAtPoint(e.getX(), e.getY());
            if(possibleEnd!=null){
                if(!possibleEnd.equals(startPort)){
                    view.setCandidateEndPort(possibleEnd);
                } else {
                    view.setCandidateEndPort(null);
                }
            } else {
                view.setCandidateEndPort(null);
            }
            drawing.redrawRequest();
        }
    }
    
    // =========================================================================
    // MouseListener implementation
    // =========================================================================
    
    /** Mouse has been clicked */
    public void mouseClicked(MouseEvent e) {
        DrawingObject o = drawing.getDrawingLayout().getObjectAtLocation(e.getX(), e.getY());
        if(e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==1){
            if(o!=null){
                try {                   
                    if(e.getModifiers()==17){
                        // Add the selected object
                        drawing.addObjectSelection(o);

                    } else {
                        // New selection
                        drawing.clearObjectSelection();
                        drawing.addObjectSelection(o);
                    }
                } catch (DrawingException ex){
                    ex.printStackTrace();
                }
            } else {
                drawing.clearObjectSelection();
            }
            
        } else if(e.getButton()==1 && e.getClickCount()==2){
            if(o instanceof BlockModel){
                drawing.editRequest((BlockModel)o);
            }
            
        } else if(e.getButton()==MouseEvent.BUTTON3){
            if(o instanceof ConnectionModel){
                try {
                    ConnectionModel connection = (ConnectionModel)o;
                    ConnectionModelPosition pos = drawing.getDrawingLayout().getLocationData(connection);
                    if(pos!=null){
                        pos.toggleConnectionBend(e.getX(), e.getY());
                        drawing.clearObjectSelection();
                        drawing.addObjectSelection(o);                        
                        drawing.redrawRequest();
                    }
                    
                } catch (DrawingException ex){
                    System.out.println(ex.getLocalizedMessage());
                }
            }
            
        }
    }
    
    /** Mouse button has been pressed */
    public void mousePressed(MouseEvent e) {
        PortModel selectedPort;
        BlockModel block;
        DrawingObject o;
        DrawingLayout layout = drawing.getDrawingLayout();
        BlockModelPosition pos;
        Point point;
        
        if(e.getButton()==MouseEvent.BUTTON1){
            // Is there an object at the mouse co-ordinte
            if(layout.selectedObjectAtLocation(e.getX(), e.getY())){
                o = layout.getObjectAtLocation(e.getX(), e.getY());
                if(o instanceof BlockModel){
                    // Is there a port to drag from 
                    selectedPort = layout.getPortAtPoint(e.getX(), e.getY());
                    
                    if(selectedPort!=null){
                        // Get the block
                        try {
                            block = (BlockModel)o;
                            pos = (BlockModelPosition)layout.getLocationData(block);
                            point = PortPositionCalculator.locatePortCentre(selectedPort, pos);

                            // Start connecting ports
                            if(selectedPort.canAcceptAdditionalConnection() && editingEnabled){
                                currentState = CONNECTING_PORTS;
                                startPort = selectedPort;
                                view.setLinkStart(point);
                                view.setLinkStartPort(startPort);
                            } else {
                                currentState = EDITOR_IDLE;
                            }
                            
                        } catch (DrawingException ex){
                            currentState = EDITOR_IDLE;
                        }
                        
                    } else {
                        // Start an object drag
                        xStart = e.getX();
                        yStart = e.getY();
                        currentState = DRAGGING_OBJECTS;        
                    }
                } else {
                    // Start an object drag
                    xStart = e.getX();
                    yStart = e.getY();
                    currentState = DRAGGING_OBJECTS;                      
                }
                
            } else {
                // Is there an object that can be selected

                selectedPort = layout.getPortAtPoint(e.getX(), e.getY());
                if(selectedPort!=null){
                    try {
                        block = selectedPort.getParentBlock();
                        pos = (BlockModelPosition)layout.getLocationData(block);
                        point = PortPositionCalculator.locatePortCentre(selectedPort, pos);

                        // Start connecting ports
                        if(selectedPort.canAcceptAdditionalConnection() && editingEnabled){
                            currentState = CONNECTING_PORTS;
                            startPort = selectedPort;
                            view.setLinkStart(point);  
                            view.setLinkStartPort(startPort);
                        } else {
                            currentState = EDITOR_IDLE;
                        }

                    } catch (Exception ex){
                        currentState = EDITOR_IDLE;
                    }

                } else {
                
                    o = layout.getObjectAtLocation(e.getX(), e.getY());
                    if(o!=null){
                        try {
                            if(e.getModifiers()==17){
                                // Add the selected object
                                drawing.addObjectSelection(o);

                            } else {
                                // New selection
                                drawing.clearObjectSelection();
                                drawing.addObjectSelection(o);
                            }

                            // Start an object drag
                            xStart = e.getX();
                            yStart = e.getY();
                            currentState = DRAGGING_OBJECTS;

                        } catch (DrawingException ex){
                            currentState = EDITOR_IDLE;
                            drawing.clearObjectSelection();
                            ex.printStackTrace();
                        }
                    } else {
                        xStart = e.getX();
                        yStart = e.getY();
                        currentState = DRAWING_BOX;                        
                    }
                }
            }
            drawing.redrawRequest();
        }
    }

    /** Mouse has left the drawing area */
    public void mouseExited(MouseEvent e) {
    }

    /** Mouse has entered the drawing area */
    public void mouseEntered(MouseEvent e) {
    }

    /** Mouse button has been released */
    public void mouseReleased(MouseEvent e) {
        PortModel selectedPort;
        BlockModel block;
        DrawingObject o;
        DrawingLayout layout = drawing.getDrawingLayout();
        BlockModelPosition pos;
        Point point;
        
        if(currentState==DRAWING_BOX){
            // Find out what has been selected 
            Rectangle r = view.getSelectionRectangle();
            if(r!=null){
                int x = (int)r.getX();
                int y = (int)r.getY();
                int width = (int)r.getWidth();
                int height = (int)r.getHeight();
                Vector selected = drawing.getDrawingLayout().getObjectsWithinRectangle(x, y, width, height);

                // Clear the selection rectangle
                view.clearSelectionRectangle();

                // Was shift pressed? If not, clear object selection first
                if(e.getModifiers()!=MouseEvent.SHIFT_MASK){
                    drawing.clearObjectSelection();
                }
                
                // Add the selected objects
                for(int i=0;i<selected.size();i++){
                    try {
                        drawing.addObjectSelection((DrawingObject)selected.elementAt(i));
                    } catch (DrawingException ex){
                        ex.printStackTrace();
                    }
                }

                drawing.redrawRequest();
            }
            
        } else if(currentState==CONNECTING_PORTS){
            view.clearLink();
            
            // Try and find the destination port
            selectedPort = layout.getPortAtPoint(e.getX(), e.getY());

            if(selectedPort!=null){            
                if(!selectedPort.equals(startPort)){
                    try {
                        drawing.connectPorts(startPort, selectedPort);
                        
                    } catch (DrawingException ex){
                        // TODO: Localise
                        javax.swing.JOptionPane.showMessageDialog(view, ex.getLocalizedMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
            currentState = EDITOR_IDLE;
            drawing.redrawRequest();
        }
        currentState = EDITOR_IDLE;
    }

    /** Is drawing editing enabled */
    public boolean isEditingEnabled(){
        return editingEnabled;
    }

    /** Set whether drawing editing is enabled */
    public void setEditingEnabled(boolean editingEnabled){
        this.editingEnabled = editingEnabled;
    }
    
    // =========================================================================
    // MouseWheelListener implementation
    // =========================================================================
    public void mouseWheelMoved(MouseWheelEvent e) {
    }
}
