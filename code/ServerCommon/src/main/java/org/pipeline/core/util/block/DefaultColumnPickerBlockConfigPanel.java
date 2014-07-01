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

package org.pipeline.core.util.block;

import org.pipeline.core.data.manipulation.ColumnPicker;

import javax.swing.*;
import java.util.Vector;

/**
 * This panel provides the basic functions of a config panel for a column 
 * picker used by a ColumnPickerBlock. It handles listeners etc and can
 * be extended to provide the desired editing functionaliy.
 * @author hugo
 */
public abstract class DefaultColumnPickerBlockConfigPanel extends JPanel implements ColumnPickerBlockConfigPanel {
    /** Listeners */
    private Vector<ColumnPickerConfigPanelListener> listeners = new Vector<>();

    /** Currently selected picker */
    private ColumnPicker _picker = null;
    
    /** Add a listener to this panel */
    public void addColumnPickerConfigPanelListener(ColumnPickerConfigPanelListener listener) {
        listeners.add(listener);
    }

    /** Remove a config panel listener from this panel */
    public void removeColumnPickerConfigPanelListener(ColumnPickerConfigPanelListener listener) {
        listeners.remove(listener);
    }
    
    /** Notify a change in the configuration of the selected picker */
    protected void notifyPickerUpdated(){
        if(_picker!=null){
            for(int i=0;i<listeners.size();i++){
                listeners.get(i).configPanelPickerUpdated(_picker);
            }
        }
    }

    /** Get the currently selected picker */
    public ColumnPicker getPicker(){
        return _picker;
    }
    
    /** Set the currently selected picker */
    public void setPicker(ColumnPicker picker) {
        this._picker = picker;
        pickerChanged();
    }

    /** Update the selected picker with the new settings */
    public void updatePicker(ColumnPicker picker) {
        if(picker!=null){
            if(picker.equals(this._picker)){
                updatePicker();
            }
        }
    }
    
    /** Save changes to the current picker */
    public abstract void updatePicker();
    
    /** Picker has changed or been set */
    public abstract void pickerChanged();
}
