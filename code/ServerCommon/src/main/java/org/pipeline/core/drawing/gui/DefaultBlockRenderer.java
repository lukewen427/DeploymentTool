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
import org.pipeline.core.drawing.model.DefaultBlockModel;
import org.pipeline.core.xmlstorage.XmlSignable;
import org.pipeline.core.xmlstorage.security.XmlDataStoreSignatureHelper;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This class provides a default view of a BlockModel on a display.
 * @author  hugo
 */
public class DefaultBlockRenderer implements BlockModelRenderer {
    /** Executed OK icon */
    
    public static Image OK_ICON = new javax.swing.ImageIcon(DefaultBlockRenderer.class.getResource("/org/pipeline/core/drawing/resource/Tick16.gif")).getImage();
    
    /** Executed OK icon */
    public static Image WAIT_ICON = new javax.swing.ImageIcon(DefaultBlockRenderer.class.getResource("/org/pipeline/core/drawing/resource/hourglass.png")).getImage();
    
    /** Execution error icon */
    public static Image ERROR_ICON = new javax.swing.ImageIcon(DefaultBlockRenderer.class.getResource("/org/pipeline/core/drawing/resource/Cross16.gif")).getImage();
    
    /** No execution status icon */
    public static Image WARNING_ICON = new javax.swing.ImageIcon(DefaultBlockRenderer.class.getResource("/org/pipeline/core/drawing/resource/Exclaim16.gif")).getImage();
    
    /** Signed OK icon */
    public static Image SIGNED_OK_ICON = new javax.swing.ImageIcon(DefaultBlockRenderer.class.getResource("/org/pipeline/core/drawing/resource/User.gif")).getImage();
    
    /** Signed OK icon */
    public static Image BAD_SIGNATURE_ICON = new javax.swing.ImageIcon(DefaultBlockRenderer.class.getResource("/org/pipeline/core/drawing/resource/BadSignature.gif")).getImage();



    /** Block being drawn */
    private BlockModel block = null;
    
    /** Drawing view panel containing this renderer */
    private Object drawingView = null;

    /** Rendering settings */
    private DrawingRenderSettings settings = null;


    /** Port size */
    private int portSize = BlockModelPosition.DEFAULT_PORT_SIZE;
    
    /** Selection handle size */
    private int selectionSize = 5;
    
    /** Output connection renderers */
    private Vector outputRenderers = new Vector();

    /** Arc size for edges */
    public static int ARC_RADIUS = 5;
    
    /** Title bar height */
    private int titleHeight = 15;
    
    /** Footer bar height */
    private int footerHeight = 10;
    
    /** Creates a new instance of DefaultBlockView */
    public DefaultBlockRenderer() {
    }

    @Override
    public void setDrawingView(Object drawingView) {
        this.drawingView = drawingView;
    }


    /** Set the drawing view */
    public void setRenderSettings(DrawingRenderSettings settings){
        this.settings = settings;

        for(int i=0;i<outputRenderers.size();i++){
           ((DefaultConnectionRenderer)outputRenderers.get(i)).setSettings(settings);
        }
    }
    
    /** Draw the bock */
    public void drawBlock(Graphics2D g, BlockModelPosition position, DrawingLayout layout, BlockExecutionReport status){
        if(settings!=null && block!=null){
            int top = position.getTop();
            int left = position.getLeft();
            int width = position.getWidth();
            int height = position.getHeight();
            boolean selected = settings.getDrawing().isObjectSelected(block);
           
            // Draw shadow
            if(settings.isShadowsEnabled()){
                g.setColor(settings.getBlockShadowColor());
                Composite oldComposite = g.getComposite();
                AlphaComposite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
                g.setComposite(c);
                g.fillRoundRect(left + 5, top + 5, width - 2, height - 2, ARC_RADIUS, ARC_RADIUS);
                g.setComposite(oldComposite);
            }
                        
            // Block inside background
            if(selected){
                g.setColor(settings.getSelectedBlockFillColor());
            } else {
                g.setColor(settings.getUnselectedBlockFillColor());
            }

            g.fillRoundRect(left + 1, top + 1, width - 1, height - 1, ARC_RADIUS, ARC_RADIUS);

            // Draw the IOs
            drawIOs(g, position);
            
            // Draw selection handles
            if(selected){
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g.setColor(settings.getSelectedHandleColor());
                g.fillRect(left - selectionSize, top - selectionSize, selectionSize, selectionSize);
                g.fillRect(left + width, top - selectionSize, selectionSize, selectionSize);
                g.fillRect(left - selectionSize, top + height, selectionSize, selectionSize);
                g.fillRect(left + width, top + height, selectionSize, selectionSize);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
            
            // Outline
            g.setColor(settings.getBlockBorderColor());
            g.drawRoundRect(left, top, width, height, ARC_RADIUS, ARC_RADIUS);
            
            // Block labels if it is a default block model
            if(block instanceof DefaultBlockModel){

                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Shape oldClip = g.getClip();

                String label = ((DefaultBlockModel)block).getLabel();
                Point p;
                if(!selected){
                    // Draw the top bar for unselected
                    g.setColor(settings.getUnselectedHeaderColor());

                    g.fillRoundRect(left + 1, top + 1, width - 1, titleHeight, ARC_RADIUS, ARC_RADIUS);
                    g.fillRect(left + 1, top + ARC_RADIUS + 1, width - 1, titleHeight - ARC_RADIUS);
                    g.setColor(settings.getBlockBorderColor());
                    g.drawLine(left + 1, top + titleHeight, left + width, top + titleHeight);

                    // Label constrained to block width
                    g.setColor(settings.getUnselectedTitleColor());
                    g.clip(new Rectangle(left, top, width, titleHeight));
                    p = Utilities.getCentredTextLocation(g, g.getFont(), label, top, left, width, titleHeight);
                    g.drawString(label, (int)p.getX(), (int)p.getY());

                } else {
                    Rectangle2D labelBounds = Utilities.getTextBounds(g, g.getFont(), label);
                    int leftPos;
                    if(labelBounds.getWidth() > width){
                        //leftPos = left + (int)((width - labelBounds.getWidth()) / 2);
                        g.setClip(new Rectangle(left - ARC_RADIUS, top, (int)labelBounds.getWidth() + (2 * ARC_RADIUS), titleHeight + 1));

                        // Extended bar
                        g.setColor(settings.getSelectedHeaderColor());
                        g.fillRoundRect(left - 1, top , (int) labelBounds.getWidth() + 2 , titleHeight, ARC_RADIUS, ARC_RADIUS);
                        //g.fillRect(leftPos + 1, top + ARC_RADIUS + 1, (int)labelBounds.getWidth() - 1, titleHeight - ARC_RADIUS);

                        g.setColor(settings.getBlockBorderColor());
                        
                        g.drawRoundRect(left - 1, top , (int) labelBounds.getWidth() + 2 , titleHeight, ARC_RADIUS, ARC_RADIUS);

                        g.clip(new Rectangle(left, top, (int)labelBounds.getWidth(), titleHeight));
                        p = Utilities.getCentredTextLocation(g, g.getFont(), label, top, left, (int)labelBounds.getWidth(), titleHeight);
                    } else {
                        g.clip(new Rectangle(left, top, width, titleHeight + 1));
                        g.setColor(settings.getSelectedHeaderColor());
                        g.fillRoundRect(left + 1, top + 1, width - 1, titleHeight, ARC_RADIUS, ARC_RADIUS);
                        g.fillRect(left + 1, top + ARC_RADIUS + 1, width - 1, titleHeight - ARC_RADIUS);
                        g.setColor(settings.getBlockBorderColor());
                        g.drawLine(left + 1, top + titleHeight, left + width, top + titleHeight);

                        // Standard bar
                        p = Utilities.getCentredTextLocation(g, g.getFont(), label, top, left, width, titleHeight);
                    }

                    g.setColor(settings.getSelectedTitleColor());
                    g.drawString(label, (int)p.getX(), (int)(p.getY()));
                }


                label = ((DefaultBlockModel)block).getCaption();
                if(!label.trim().equals("")){
                    g.setColor(settings.getBlockLabelColor());
                    if(settings.getVisibleRect()!=null){
                        g.setClip(settings.getVisibleRect());
                    } else {
                        g.setClip(null);
                    }
                    
                    Rectangle2D bounds = Utilities.getTextBounds(g, g.getFont(), label);
                    int x = left + (int)((width - bounds.getWidth()) / 2);
                    int y = top + height + (int)bounds.getHeight();
                    g.drawString(label, x, y);
                }
                g.setClip(oldClip);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
                      
            // Draw the connections
            Enumeration links = outputRenderers.elements();
            ConnectionModelRenderer renderer;
            while(links.hasMoreElements()){
                try {
                    renderer = (ConnectionModelRenderer)links.nextElement();
                    renderer.render(g, layout, settings.getDrawing().isObjectSelected(renderer.getConnection()));
                } catch (Exception e){
                    e.printStackTrace();
                }
            } 
            
            // Draw execution status icon
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            if(status!=null){
                if(status.getExecutionStatus()==BlockExecutionReport.NO_ERRORS){
                    g.drawImage(OK_ICON, left + width - 18, top + height - 18, null);
                } else if(status.getExecutionStatus()==BlockExecutionReport.BLOCK_NOT_EXECUTED_YET){
                    g.drawImage(WAIT_ICON, left + width - 18, top + height - 18, null);
                } else {
                    g.drawImage(ERROR_ICON, left + width - 18, top + height - 18, null);
                }                
            }
            
            // Signature icon
            if(block instanceof XmlSignable){
                XmlDataStoreSignatureHelper sig = ((XmlSignable)block).getSignatureData();
                if(sig.objectSigned()){
                    try {
                        if(sig.verifyObject()){
                            g.drawImage(SIGNED_OK_ICON, left + 4, top + height - 18, null);
                        } else {
                            g.drawImage(BAD_SIGNATURE_ICON, left + 4, top + height - 18, null);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }   
                }
            }
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            

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
        
        g.setColor(settings.getBlockPortColor());
        
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
                        s2 = Utilities.createLeftArrow(x - portSize, y, portSize);
                    } else {
                        s2 = null;
                    }                    
                    break;
                    
                case PortModel.BOTTOM_OF_BLOCK:
                    x = (int)pos.getX();
                    y = (int)pos.getY() /*- portSize*/;
                    s = Utilities.createUpArrow(x, y, portSize);
                    if(port.streamingPort()){
                        s2 = Utilities.createUpArrow(x, y - portSize, portSize);
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
