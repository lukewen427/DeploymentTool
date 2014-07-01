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

import org.pipeline.core.drawing.*;
import org.pipeline.core.drawing.layout.BlockModelPosition;
import org.pipeline.core.drawing.layout.DrawingLayout;
import org.pipeline.core.drawing.layout.PortPositionCalculator;

import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This class provides a small renderer for trivial connection
 * blocks that don't need to be full sized.
 * @author hugo
 */
public class MiniBlockRenderer implements BlockModelRenderer {
    /** Executed OK icon */
    private static final Image OK_ICON = new javax.swing.ImageIcon(DefaultBlockRenderer.class.getResource("/org/pipeline/core/drawing/resource/Tick16.gif")).getImage();
    
    /** Execution error icon */
    private static final Image ERROR_ICON = new javax.swing.ImageIcon(DefaultBlockRenderer.class.getResource("/org/pipeline/core/drawing/resource/Cross16.gif")).getImage();
    
    /** No execution status icon */
    private static final Image WARNING_ICON = new javax.swing.ImageIcon(DefaultBlockRenderer.class.getResource("/org/pipeline/core/drawing/resource/Exclaim16.gif")).getImage();
    
    /** Block being drawn */
    private BlockModel block = null;
    
    /** Drawing view panel containing this renderer */
    private DefaultDrawingView view = null;

    /** Rendering settings */
    private DrawingRenderSettings settings = null;

    /** Port size */
    private int portSize = BlockModelPosition.DEFAULT_PORT_SIZE;
    
    /** Selection handle size */
    private int selectionSize = 5;
    
    /** Output connection renderers */
    private Vector outputRenderers = new Vector();

    /** Arc size for edges */
    private int arcRadius = 5;
    
    /** Title bar height */
    private int titleHeight = 15;
    
    /** Footer bar height */
    private int footerHeight = 10;
    
    /** Creates a new instance of MiniBlockRenderer */
    public MiniBlockRenderer() {
    }

    /** Set the drawing view */
    public void setRenderSettings(DrawingRenderSettings settings){
        this.settings = settings;

        for(int i=0;i<outputRenderers.size();i++){
           ((DefaultConnectionRenderer)outputRenderers.get(i)).setSettings(settings);
        }
    }

    /** Set the drawing view */
    public void setDrawingView(Object view){
        this.view = (DefaultDrawingView)view;
    }
    
    /** Draw the bock */
    public void drawBlock(Graphics2D g, BlockModelPosition position, DrawingLayout layout, BlockExecutionReport status){
        if(view!=null && block!=null){
            int top = position.getTop();
            int left = position.getLeft();
            int width = position.getWidth();
            int height = position.getHeight();
            
           
            // Draw shadow
            g.setColor(Color.LIGHT_GRAY);
            Composite oldComposite = g.getComposite();
            AlphaComposite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
            g.setComposite(c);            
            g.fillRoundRect(left + 5, top + 5, width - 2, height - 2, arcRadius, arcRadius);
            g.setComposite(oldComposite);
            
            // Draw the IOs
            drawIOs(g, position);
            
            // Block inside background
            g.setColor(Color.WHITE);
            g.fillRoundRect(left + 1, top + 1, width - 1, height - 1, arcRadius, arcRadius);

            // Draw selection handles
            if(view.getDrawing().isObjectSelected(block)){
                g.setColor(Color.DARK_GRAY);
                g.fillRect(left - selectionSize, top - selectionSize, selectionSize, selectionSize);
                g.fillRect(left + width, top - selectionSize, selectionSize, selectionSize);
                g.fillRect(left - selectionSize, top + height, selectionSize, selectionSize);
                g.fillRect(left + width, top + height, selectionSize, selectionSize);
            }
     
            // Draw the connections
            Enumeration links = outputRenderers.elements();
            ConnectionModelRenderer renderer;
            while(links.hasMoreElements()){
                try {
                    renderer = (ConnectionModelRenderer)links.nextElement();
                    renderer.render(g, layout, view.getDrawing().isObjectSelected(renderer.getConnection()));
                } catch (Exception e){
                    e.printStackTrace();
                }
            } 
            
            // Outline
            g.setColor(Color.BLACK);
            g.drawRoundRect(left, top, width, height, arcRadius, arcRadius);               
        }
    }
        
    /** Draw the block input and outputs */
    private void drawIOs(Graphics2D g, BlockModelPosition position){
        PortModel port;
        Point pos;
        Shape s;
        Shape s2;
        int x;
        int y;
        
        g.setColor(Color.BLACK);
        
        // Get the inputs
        Enumeration inputs = block.inputs();
        while(inputs.hasMoreElements()){
            port = (PortModel)inputs.nextElement();
            pos = PortPositionCalculator.locatePort(port, position);
            
            switch(port.getPortLocation()){
                case PortModel.LEFT_OF_BLOCK:
                    x = /*portSize + */(int)pos.getX();
                    y = (int)pos.getY();
                    s = Utilities.createRightArrow(x, y, portSize);
                    if(port.streamingPort()){
                        s2 = Utilities.createRightArrow(x + portSize, y, portSize);
                    } else {
                        s2 = null;
                    }
                    break;
                    
                case PortModel.TOP_OF_BLOCK:
                    x = (int)pos.getX();
                    y = (int)pos.getY() + portSize - portSize;
                    s = Utilities.createDownArrow(x, y, portSize);
                    if(port.streamingPort()){
                        s2 = Utilities.createDownArrow(x, y + portSize, portSize);
                    } else {
                        s2 = null;
                    }                    
                    break;
                    
                case PortModel.RIGHT_OF_BLOCK:
                    x = (int)pos.getX() /*- portSize*/;
                    y = (int)pos.getY();
                    s = Utilities.createLeftArrow(x, y, portSize);
                    if(port.streamingPort()){
                        s2 = Utilities.createRightArrow(x - portSize, y, portSize);
                    } else {
                        s2 = null;
                    }                    
                    break;
                    
                case PortModel.BOTTOM_OF_BLOCK:
                    x = (int)pos.getX();
                    y = (int)pos.getY() /*- portSize*/;
                    s = Utilities.createUpArrow(x, y, portSize);
                    if(port.streamingPort()){
                        s2 = Utilities.createDownArrow(x, y - portSize, portSize);
                    } else {
                        s2 = null;
                    }                    
                    break;
                    
                default:
                    x = (int)pos.getX();
                    y = (int)pos.getY();
                    s = Utilities.createRightArrow(x, y, portSize);  
                    s2 = null;
            }
            g.fill(s);
            if(s2!=null){
                g.fill(s2);
            }
            
        }
        
        // Get the outputs
        Enumeration outputs = block.outputs();
        while(outputs.hasMoreElements()){
            port = (PortModel)outputs.nextElement();
            pos = PortPositionCalculator.locatePort(port, position);
            x = (int)pos.getX();
            y = (int)pos.getY();
            
            
            switch(port.getPortLocation()){
                case PortModel.LEFT_OF_BLOCK:
                    g.fillOval((int)pos.getX() - portSize, (int)pos.getY() - (int)(portSize / 2), portSize, portSize);
                    break;
                    
                case PortModel.TOP_OF_BLOCK:
                    g.fillOval((int)pos.getX() - (int)(portSize / 2), y - portSize, portSize, portSize);
                    break;
                    
                case PortModel.RIGHT_OF_BLOCK:
                    g.fillOval((int)pos.getX(), (int)pos.getY() - (int)(portSize / 2), portSize, portSize);
                    break;
                    
                case PortModel.BOTTOM_OF_BLOCK:
                    g.fillOval((int)pos.getX() - (int)(portSize / 2), y, portSize, portSize);
                    break;
                    
                default:
                    s = Utilities.createRightArrow(x, y, portSize);
            }
        }
    }
    
    /** Update the output renderers */
    private void updateOutputRenderers(){
        if(block!=null){
            outputRenderers.clear();
            Enumeration outputs = block.outputs();
            OutputPortModel port;
            ConnectionModel connection;
            Enumeration connections;
            DefaultConnectionRenderer renderer;
            
            while(outputs.hasMoreElements()){
                port = (OutputPortModel)outputs.nextElement();
                connections = port.connections();
                while(connections.hasMoreElements()){
                    connection = (ConnectionModel)connections.nextElement();
                    renderer = new DefaultConnectionRenderer(settings);
                    renderer.setConnection(connection);
                    outputRenderers.add(renderer);
                }
            }
        }
    }
        
    
    // =========================================================================
    // BlockModelRenderer implementation
    // =========================================================================

    /** Return the block being rendered */
    public BlockModel getBlock() {
        return block;
    }

    /** Set the block being rendered */
    public void setBlock(BlockModel block) {
        this.block = block;
        updateOutputRenderers();
    }

    /** Render the block onto a drawing area */
    public void render(Graphics2D display, DrawingLayout layout, BlockExecutionReport status) throws DrawingException {
        BlockModelPosition position = layout.getLocationData(getBlock());
        drawBlock(display, position, layout, status);
    }    
}
