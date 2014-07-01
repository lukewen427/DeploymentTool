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

package org.pipeline.core.util.swing;
import javax.swing.*;

/**
 * This class implements a Runnable interface and updates the text of a JLabel
 * @author hugo
 */
public class LabelUpdater implements Runnable {
    /** Label to update */
    private JLabel label;
    
    /** Text to set */
    private String text;
    
    /** Creates a new instance of LabelUpdater */
    public LabelUpdater(JLabel label, String text) {
        this.label = label;
        this.text = text;
    }
    
    /** Do the update */
    public void run(){
        label.setText(text);
    }
}
