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

/**
 * This class provides an editing handle that can be used to move and re-size
 * drawing objects.
 * @author  hugo
 */
public class EditHandle {
    /** Top left handle */
    public static final int TOP_LEFT_HANDLE = 0;
    
    /** Top right handle */
    public static final int TOP_RIGHT_HANDLE = 1;
    
    /** Bottom left handle */
    public static final int BOTTOM_LEFT_HANDLE = 2;
    
    /** Bottom right handle */
    public static final int BOTTOM_RIGHT_HANDLE = 3;
    
    /** Free standing handle */
    public static final int FREE_STANDING_HANDLE = 4;
    
    /** Handle type */
    private int handleType = TOP_LEFT_HANDLE;
    
    /** Creates a new instance of EditHandle */
    public EditHandle() {
    }
}
