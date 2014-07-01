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
import org.pipeline.core.drawing.layout.PortPositionCalculator;
import org.pipeline.core.drawing.model.DefaultDrawingModel;
import org.pipeline.core.drawing.spanning.DrawingExecutionProcessor;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class renders a drawing onto a 2D Graphics context. It contains code from
 * the original DefaultDrawingView so that workflows can be painted onto an Image
 * on the server without needing a Swing JPanel.
 * @author nhgh
 */
public class DefaultDrawingRenderer implements DrawingModelListener {
    /** Drawing being displayed */
    private DrawingModel drawing = null;

    /** List of renderers for each block in the drawing */
    private Hashtable renderers = new Hashtable();

    /** Selection rectangle */
    private Rectangle selectionRect = null;

    /** First point of linking line */
    private Point linkStart = null;

    /** Second point of linking line */
    private Point linkEnd = null;

    /** Port where the currently drawn link starts from */
    private PortModel linkStartPort = null;

    /** Execution processor for this drawing if there is one */
    private DrawingExecutionProcessor executer = null;

    /** Minimum label box width */
    private int minimumLabelBoxWidth = 50;

    /** Closes end port for connecting */
    private PortModel candidateEndPort = null;

    /** Executed OK icon */
    private static final Image CONNECTION_YES_ICON = new javax.swing.ImageIcon(DefaultDrawingView.class.getResource("/org/pipeline/core/drawing/resource/Tick16.gif")).getImage();

    /** Execution error icon */
    private static final Image CONNECTION_NO_ICON = new javax.swing.ImageIcon(DefaultDrawingView.class.getResource("/org/pipeline/core/drawing/resource/Cross16.gif")).getImage();

    /** Should the execution order be displayed */
    private boolean displayExecutionOrder = true;

    /** Current execution order */
    private Vector executionOrder = null;

    /** Drawing execution reports. These are used if there is no drawing execution processor in place */
    private Hashtable executionReports = null;

    /** Alternative drawing font */
    private Font alternativeFont = null;

    /** Currently running block. This is used in the online workflow results browser to signify that a block is still running */
    private BlockModel currentBlock = null;

    /** Rendering settings */
    private DrawingRenderSettings settings = new DrawingRenderSettings();

    /** Creates a new instance of DefaultDrawingView */
    public DefaultDrawingRenderer(DrawingModel drawing) {
        this.drawing = drawing;
        settings.setDrawing(drawing);
        drawing.addDrawingModelListener(this);
    }

    /** Set the currently executing block */
    public void setCurrentBlock(BlockModel currentBlock){
        this.currentBlock = currentBlock;
    }

    /** Clear the currently executing block */
    public void clearCurrentBlock(){
        currentBlock = null;
    }

    /** Set the alternative drawing font */
    public void setAlternativeFont(Font alternativeFont){
        this.alternativeFont = alternativeFont;
    }

    /** Set the drawing execution processor */
    public void setExecutionProcessor(DrawingExecutionProcessor executer){
        this.executer = executer;
        if(executer!=null){
            executionOrder = executer.buildExecutionListFromAllSourceBlocks();
        } else {
            executionOrder = null;
        }
    }

    /** Set the execution reports */
    public void setExecutionReports(Hashtable executionReports){
        this.executionReports = executionReports;
    }

    /** Does this view have an execution processor */
    public boolean hasExecutionProcessor(){
        if(executer!=null){
            return true;
        } else {
            return false;
        }
    }

    /** Set the location of the selection rectangle */
    public void setSelectionRectangle(int x, int y, int width, int height){
        selectionRect = new Rectangle(x, y, width, height);
    }

    /** Clear the selection rectangle */
    public void clearSelectionRectangle(){
        selectionRect = null;
    }

    /** Get the selection rectangle */
    public Rectangle getSelectionRectangle(){
        return selectionRect;
    }

    /** Set the link start */
    public void setLinkStart(Point linkStart){
        this.linkStart = linkStart;
    }

    /** Set the link end */
    public void setLinkEnd(Point linkEnd){
        this.linkEnd = linkEnd;
    }

    /** Link source port */
    public void setLinkStartPort(PortModel linkStartPort){
        this.linkStartPort = linkStartPort;
    }

    /** Set the end port that might be linked to */
    public void setCandidateEndPort(PortModel candidateEndPort) {
        this.candidateEndPort = candidateEndPort;
    }

    /** Clear the link */
    public void clearLink(){
        linkEnd = null;
        linkStart = null;
        candidateEndPort = null;
    }

    /** Draw all the blocks in the drawing */
    public void renderDrawing(Graphics g, int width, int height){
        Graphics2D g2 = (Graphics2D)g;
        settings.setVisibleRect(null);
        g2.setClip(null);
        g2.setColor(settings.getBackgroundColor());
        g2.fillRect(0, 0, width, height);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);

        // Use the alternative font if it has been specified
        if(alternativeFont!=null){
            g2.setFont(alternativeFont);
        }

        // Paint all the blocks
        Enumeration blocks = drawing.blocks();
        BlockModel block;
        BlockModelRenderer renderer;
        BlockExecutionReport status;
        BlockModelPosition pos;
        String order;
        Composite oldComposite;

        while(blocks.hasMoreElements()){
            block = (BlockModel)blocks.nextElement();

            // Create the renderer, or use the previously created one if it's there
            if(renderers.containsKey(block)){
                renderer = (BlockModelRenderer)renderers.get(block);
            } else {
                renderer = block.createRenderer();
                if(renderer!=null){
                    ((BlockModelRenderer)renderer).setDrawingView(this);
                }
                if(renderer instanceof DefaultBlockRenderer){
                    ((DefaultBlockRenderer)renderer).setRenderSettings(settings);
                }
            }

            // If the renderer was created, draw the block
            if(renderer!=null){
                try {
                    if(executer!=null){
                        renderer.render(g2, ((DefaultDrawingModel)drawing).getDrawingLayout(), executer.getExecutionReport(block));
                    } else {
                        if(executionReports!=null && executionReports.containsKey(block.getBlockGUID())){
                            // Render report if the external execution reports are present
                            renderer.render(g2, ((DefaultDrawingModel)drawing).getDrawingLayout(), (BlockExecutionReport)executionReports.get(block.getBlockGUID()));
                        } else {
                            renderer.render(g2, ((DefaultDrawingModel)drawing).getDrawingLayout(), null);
                        }
                    }
                } catch (DrawingException e){
                    e.printStackTrace();
                }
            }

            // Set the renderer to null so that it doesn't interfere with drawing the
            // next block
            renderer = null;

            // Draw the execution order
            if(displayExecutionOrder && executer!=null){
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g2.setColor(Color.BLACK);
                try {
                    int index = executionOrder.indexOf(block);
                    pos = ((DefaultDrawingModel)drawing).getDrawingLayout().getLocationData(block);
                    if(index!=-1){
                        order = Integer.toString(index + 1);
                    } else {
                        order = "--";
                    }
                    g2.drawString(order, pos.getLeft() + (pos.getWidth() / 2), pos.getTop() + pos.getHeight() - 2);

                } catch (Exception e){
                } finally {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                }
            }

        }

        // Highlight a block if there is one set
        oldComposite = g2.getComposite();
        if(currentBlock!=null){
            try {

                pos = ((DefaultDrawingModel)drawing).getDrawingLayout().getLocationData(currentBlock);
                if(pos!=null){
                    AlphaComposite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
                    g2.setComposite(c);
                    g2.setColor(Color.orange);
                    g2.fillRoundRect(pos.getLeft() - 10, pos.getTop() - 10, pos.getWidth() + 20, pos.getHeight() + 20, 5, 5);
                }

            } catch (Exception e){

            } finally {
                g2.setComposite(oldComposite);
            }

        }

        // Draw the selection rectangle if there is one
        if(selectionRect!=null){
            oldComposite = g2.getComposite();
            AlphaComposite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
            g2.setComposite(c);

            g2.setColor(settings.getSelectionRectangleColor());
            g2.fill(selectionRect);

            g2.setComposite(oldComposite);
            g2.setColor(settings.getSelectionRectangleBorderColor());
            g2.draw(selectionRect);
        }

        // Draw the port link if there is one
        if(linkStart!=null && linkEnd!=null && linkStartPort!=null){
            g2.setColor(settings.getDrawingLinkColor());

            // Line
            g2.drawLine((int)linkStart.getX(), (int)linkStart.getY(), (int)linkEnd.getX(), (int)linkEnd.getY());

            // Calculate box details
            Rectangle2D bounds = Utilities.getTextBounds(g2, g2.getFont(), linkStartPort.getName());
            int labelBoxHeight = (int)bounds.getHeight() + 2;
            int labelBoxWidth = (int)bounds.getWidth() + 25;

            if(labelBoxWidth<minimumLabelBoxWidth){
                labelBoxWidth = minimumLabelBoxWidth;
            }

            int boxTop = (int)linkEnd.getY() - (int)(labelBoxHeight / 2);
            int boxLeft = (int)linkEnd.getX();

            // Set the alpha blending level
            AlphaComposite c;

            if(candidateEndPort==null){
                oldComposite = g2.getComposite();
                c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
                g2.setComposite(c);

                // Draw the box
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g2.drawRect(boxLeft, boxTop, labelBoxWidth, labelBoxHeight);
                g2.setColor(Color.WHITE);
                Shape oldClip = g2.getClip();
                g2.clip(new Rectangle(boxLeft + 1, boxTop + 1, labelBoxWidth - 1, labelBoxHeight - 1));
                g2.fillRect(boxLeft + 1, boxTop + 1, labelBoxWidth, labelBoxHeight);
                g2.setColor(Color.BLACK);
                Point p = Utilities.getCentredTextLocation(g2, g2.getFont(), linkStartPort.getName(), boxTop + 1, boxLeft + 1, labelBoxWidth - 2, labelBoxHeight - 2);
                g2.drawString(linkStartPort.getName(), (int)p.getX() + 10, (int)p.getY());
                g2.setClip(oldClip);
                g2.setComposite(oldComposite);
                g2.setColor(Color.BLACK);
                g2.fillOval(linkEnd.x - 4, linkEnd.y - 4, 8, 8);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            } else {
                String statusLabel = "Connection OK: ";

                Rectangle2D nameBounds = Utilities.getTextBounds(g2, g2.getFont(), candidateEndPort.getName());
                Rectangle2D sourceBounds = Utilities.getTextBounds(g2, g2.getFont(), statusLabel);

                // Find the maximum size
                labelBoxWidth = Math.max(minimumLabelBoxWidth, (int)nameBounds.getWidth());;
                labelBoxWidth = Math.max(labelBoxWidth, (int)sourceBounds.getWidth() + 10);
                Point p = Utilities.getCentredTextLocation(g2, g2.getFont(), candidateEndPort.getName(), boxTop + 1, boxLeft + 1, labelBoxWidth, 20);

                // Add space for the icon
                labelBoxWidth+=25;
                labelBoxHeight = 100;

                oldComposite = g2.getComposite();
                c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
                g2.setComposite(c);

                g2.setColor(Color.WHITE);

                g2.fillRoundRect(boxLeft, boxTop, labelBoxWidth, 50, 5, 5);
                g2.setColor(new Color(46, 159, 217));
                g2.fillRoundRect(boxLeft, boxTop, labelBoxWidth, (int)nameBounds.getHeight() + 10, 5, 5);

                g2.setComposite(oldComposite);
                g2.setColor(Color.BLACK);
                g2.drawRoundRect(boxLeft, boxTop, labelBoxWidth, 50, 5, 5);
                g2.drawRoundRect(boxLeft, boxTop, labelBoxWidth, (int)nameBounds.getHeight() + 10, 5, 5);

                // Name
                g2.drawString(candidateEndPort.getName(), p.x, p.y);

                // Data type check
                g2.drawString(statusLabel, boxLeft + 2, p.y + 25);
                if(linkStartPort.canConnectToPort(candidateEndPort)){
                    // Draw a tick
                    g2.drawImage(CONNECTION_YES_ICON, boxLeft + labelBoxWidth - 23, p.y + 12, 16, 16, null);
                    g2.setColor(new Color(0,0,128));
                    g2.fillOval(linkEnd.x - 4, linkEnd.y - 4, 8, 8);

                } else {
                    // Draw a cross
                    g2.drawImage(CONNECTION_NO_ICON, boxLeft + labelBoxWidth - 23, p.y + 12 , 16, 16, null);
                    g2.setColor(Color.RED);
                    g2.fillOval(linkEnd.x - 4, linkEnd.y - 4, 8, 8);

                }

                // Highlight port
                try {
                    Point portPoint = PortPositionCalculator.locatePort(candidateEndPort, drawing.getDrawingLayout().getLocationData(candidateEndPort.getParentBlock()));
                    g2.drawRect(portPoint.x, portPoint.y, BlockModelPosition.DEFAULT_PORT_SIZE, BlockModelPosition.DEFAULT_PORT_SIZE);
                } catch (Exception e){
                }


            }
        }
    }

    /** Get the drawing object */
    public DrawingModel getDrawing(){
        return drawing;
    }

    // =========================================================================
    // DrawingModelListnener implementation
    // =========================================================================
    /** A block has been selected */
    public void objectSelected(DrawingModelEvent e){
    }

    /** A block has been unselected */
    public void objectUnSelected(DrawingModelEvent e){
    }

    /** Request editing for a block */
    public void editRequest(DrawingModelEvent e){

    }

    /** A block has been added */
    public void blockAdded(DrawingModelEvent e){
        if(executer!=null){
            executionOrder = executer.buildExecutionListFromAllSourceBlocks();
        }
    }

    /** A block has been removed */
    public void blockRemoved(DrawingModelEvent e) {
        if(executer!=null){
            executionOrder = executer.buildExecutionListFromAllSourceBlocks();
        }
        renderers.remove(e.getObject());
    }

    /** A connection has been added */
    public void connectionAdded(DrawingModelEvent e) {
        if(executer!=null){
            executionOrder = executer.buildExecutionListFromAllSourceBlocks();
        }
    }

    /** A connection has been removed */
    public void connectionRemoved(DrawingModelEvent e){
        if(executer!=null){
            executionOrder = executer.buildExecutionListFromAllSourceBlocks();
        }
    }

    /** Drawing wants to be re-drawn */
    public void redrawRequested(DrawingModelEvent e){
    }

    /** Block wants to be edited */
    public void editRequest(BlockModel block){

    }

    /** Block wants to be executed */
    public void executeRequest(DrawingModelEvent e){
    }

    /** Drawing wants to send a signal */
    public void signalRequested(DrawingModelEvent e){
    }

    /** Is the execution order visible */
    public boolean isDisplayExecutionOrder() {
        return displayExecutionOrder;
    }

    /** Set whether the execution order is visible */
    public void setDisplayExecutionOrder(boolean displayExecutionOrder) {
        this.displayExecutionOrder = displayExecutionOrder;
    }

    /**
     * Request execution from a block in an exising Thread
     */
    public void inThreadExecuteRequest(DrawingModelEvent e) throws DrawingException {
    }

}