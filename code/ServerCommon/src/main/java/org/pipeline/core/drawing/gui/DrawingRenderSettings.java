/**
 * e-Science Central
 * Copyright (C) 2008-2013 School of Computing Science, Newcastle University
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package org.pipeline.core.drawing.gui;

import org.pipeline.core.drawing.DrawingModel;

import java.awt.*;

/**
 * This class contains all of the drawing rendering attributes.
 * @author nhgh
 */
public class DrawingRenderSettings {
    /** Shadow color for a block */
    private Color blockShadowColor = Color.LIGHT_GRAY;

    /** Fill color for an unselected block */
    private Color unselectedBlockFillColor = Color.WHITE;

    /** Fill color for a selected block */
    private Color selectedBlockFillColor = Color.WHITE;

    /** Color for block selection handles */
    private Color selectedHandleColor = Color.BLACK;

    /** Border color of a block */
    private Color blockBorderColor = Color.BLACK;

    /** Header color of a block */
    private Color unselectedHeaderColor = Color.LIGHT_GRAY;

    /** Selected header color of a block */
    private Color selectedHeaderColor = Color.LIGHT_GRAY;

    /** Title color for a selected block */
    private Color selectedTitleColor = Color.BLACK;

    /** Title color for an unselected block */
    private Color unselectedTitleColor = Color.BLACK;

    /** Block label color */
    private Color blockLabelColor = Color.BLACK;

    /** Standard link color */
    private Color linkColor = Color.BLACK;

    /** Selected link color */
    private Color selectedLinkColor = Color.BLUE;

    /** Color for drawing a link */
    private Color drawingLinkColor = Color.BLACK;

    /** Should block shadows be painted */
    private boolean shadowsEnabled = true;

    /** Selection rectagle color */
    private Color selectionRectangleColor = Color.LIGHT_GRAY;

    /** Color of the selection box border */
    private Color selectionRectangleBorderColor = new Color(0, 0, 128);

    /** Port color for blocks */
    private Color blockPortColor = Color.BLACK;

    /** Drawing blackground color */
    private Color backgroundColor = Color.WHITE;
    
    /** Visible rectangle */
    private Rectangle visibleRect = null;

    /** Drawing being rendered */
    private DrawingModel drawing = null;

    public DrawingModel getDrawing() {
        return drawing;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setDrawing(DrawingModel drawing) {
        this.drawing = drawing;
    }

    public Rectangle getVisibleRect() {
        return visibleRect;
    }

    public void setVisibleRect(Rectangle visibleRect) {
        this.visibleRect = visibleRect;
    }

    
    public Color getUnselectedHeaderColor() {
        return unselectedHeaderColor;
    }

    public void setUnselectedHeaderColor(Color unselectedHeaderColor) {
        this.unselectedHeaderColor = unselectedHeaderColor;
    }

    public Color getUnselectedTitleColor() {
        return unselectedTitleColor;
    }

    public void setUnselectedTitleColor(Color unselectedTitleColor) {
        this.unselectedTitleColor = unselectedTitleColor;
    }

    public Color getBlockBorderColor() {
        return blockBorderColor;
    }

    public void setBlockBorderColor(Color blockBorderColor) {
        this.blockBorderColor = blockBorderColor;
    }

    public Color getSelectedHandleColor() {
        return selectedHandleColor;
    }

    public void setSelectedHandleColor(Color selectedHandleColor) {
        this.selectedHandleColor = selectedHandleColor;
    }

    public Color getSelectedBlockFillColor() {
        return selectedBlockFillColor;
    }

    public void setSelectedBlockFillColor(Color selectedBlockFillColor) {
        this.selectedBlockFillColor = selectedBlockFillColor;
    }

    public Color getUnselectedBlockFillColor() {
        return unselectedBlockFillColor;
    }

    public void setUnselectedBlockFillColor(Color unselectedBlockFillColor) {
        this.unselectedBlockFillColor = unselectedBlockFillColor;
    }

    public Color getBlockShadowColor() {
        return blockShadowColor;
    }

    public void setBlockShadowColor(Color blockShadowColor) {
        this.blockShadowColor = blockShadowColor;
    }

    public Color getSelectionRectangleBorderColor() {
        return selectionRectangleBorderColor;
    }

    public void setSelectionRectangleBorderColor(Color selectionRectangleBorderColor) {
        this.selectionRectangleBorderColor = selectionRectangleBorderColor;
    }

    public Color getBlockPortColor() {
        return blockPortColor;
    }

    public void setBlockPortColor(Color blockPortColor) {
        this.blockPortColor = blockPortColor;
    }

    public Color getSelectionRectangleColor() {
        return selectionRectangleColor;
    }

    public void setSelectionRectangleColor(Color selectionRectangleColor) {
        this.selectionRectangleColor = selectionRectangleColor;
    }

    public boolean isShadowsEnabled() {
        return shadowsEnabled;
    }

    public void setShadowsEnabled(boolean shadowsEnabled) {
        this.shadowsEnabled = shadowsEnabled;
    }

    public Color getLinkColor() {
        return linkColor;
    }

    public void setLinkColor(Color linkColor) {
        this.linkColor = linkColor;
    }

    public Color getDrawingLinkColor() {
        return drawingLinkColor;
    }

    public void setDrawingLinkColor(Color drawingLinkColor) {
        this.drawingLinkColor = drawingLinkColor;
    }

    public Color getSelectedLinkColor() {
        return selectedLinkColor;
    }

    public void setSelectedLinkColor(Color selectedLinkColor) {
        this.selectedLinkColor = selectedLinkColor;
    }

    public Color getBlockLabelColor() {
        return blockLabelColor;
    }

    public void setBlockLabelColor(Color blockLabelColor) {
        this.blockLabelColor = blockLabelColor;
    }

    public Color getSelectedHeaderColor() {
        return selectedHeaderColor;
    }

    public void setSelectedHeaderColor(Color selectedHeaderColor) {
        this.selectedHeaderColor = selectedHeaderColor;
    }

    public Color getSelectedTitleColor() {
        return selectedTitleColor;
    }

    public void setSelectedTitleColor(Color selectedTitleColor) {
        this.selectedTitleColor = selectedTitleColor;
    }
}