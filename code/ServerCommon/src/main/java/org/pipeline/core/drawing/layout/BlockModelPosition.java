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
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;
// </editor-fold>

/** 
 * This class locates a block within a drawing.
 * @author  hugo
 */
public class BlockModelPosition extends ModelPosition implements XmlStorable { 
    /** Default block width */
    public static final int DEFAULT_BLOCK_WIDTH = 60;
    
    /** Default block height */
    public static final int DEFAULT_BLOCK_HEIGHT = 60;
    
    /** Default port size */
    public static final int DEFAULT_PORT_SIZE = 8;
    
    /** Top of block */
    private int top = 0;
    
    /** Left of block */
    private int left = 0;
    
    /** Width of block */
    private int width = DEFAULT_BLOCK_WIDTH;
    
    /** Height of block */
    private int height = DEFAULT_BLOCK_HEIGHT;
    
    /** Creates a new instance of BlockModelPosition */
    public BlockModelPosition(){
    }
    
    /** Get top */
    public int getTop(){
        return DrawingLayout.constrainValue(top);
    }
    
    /** Set top */
    public void setTop(int top){
        this.top = top;
        notifyLayoutChange();
    }
    
    /** Get left */
    public int getLeft(){
        return DrawingLayout.constrainValue(left);
    }
    
    /** Set left */
    public void setLeft(int left){
        this.left = left;
        notifyLayoutChange();
    }
    
    /** Get width */
    public int getWidth(){
        return width;
    }
    
    /** Set width */
    public void setWidth(int width){
        this.width = width;
        notifyLayoutChange();
    }
    
    /** Get height */
    public int getHeight(){
        return height;
    }
    
    /** Set height */
    public void setHeight(int height){
        this.height = height;
        notifyLayoutChange();
    }
    
    /** Is a point within the object */
    public boolean pointWithinObject(int x, int y){
        if(x>=left && x<=(left + width) && y>=top && y<=(top + height)){
            return true;
        } else {
            return false;
        }
    }
    
    /** Is this object within bounds */
    public boolean objectWithinBounds(int boundsX, int boundsY, int boundsWidth, int boundsHeight){
        if((boundsX<=left && boundsX + boundsWidth>=left) &&
           (boundsX<=(left+width) && (boundsX + boundsWidth)>=(left+width)) &&
           (boundsY<=top && (boundsY+boundsHeight)>=top) &&
           (boundsY<=(top+height) && (boundsY + boundsHeight)>=(top + height))){
            return true;
        } else {
            return false;
        }
    }    
    
    /** Move by a specified offset */
    public void moveBy(int xOffset, int yOffset){
        left = left + xOffset;
        top = top + yOffset;
        notifyLayoutChange();
    }    

    /** Recreate from an XmlDataStore */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        top = xmlDataStore.intValue("Top", 0);
        left = xmlDataStore.intValue("Left", 0);
        width = xmlDataStore.intValue("Width", 50);
        height = xmlDataStore.intValue("Height", 50);
    }

    /** Save to an XmlDataStore */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("BlockPosition");
        store.add("Top", top);
        store.add("Left", left);
        store.add("Width", width);
        store.add("Height", height);
        return store;
    }

    /** Get the maximum y co-ordinate */
    public int getMaximumY() {
        return top + height;
    }

    /** Get the maximum x co-ordinate */
    public int getMaximumX() {
        return left + width;
    }
}
