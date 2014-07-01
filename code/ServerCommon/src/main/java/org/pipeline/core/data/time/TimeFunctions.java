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

package org.pipeline.core.data.time;

import javax.swing.*;
import java.util.Date;

/**
 * This class contains various time conversion functions
 * @author hugo
 */
public abstract class TimeFunctions {
    
    /** Convert a time value */
    public static long convert(long value, int sourceUnits, int targetUnits){
        return (value * TimeConstants.MULTIPLERS[sourceUnits]) / TimeConstants.MULTIPLERS[targetUnits];
    }
    
    /** Convert a value to milliseconds */
    public static long convertToMilliseconds(double value, int units){
        return (long)(value * (double)TimeConstants.MULTIPLERS[units]);
    }
        
    /** Calculate the difference between two dates as a time value */
    public static double calculateDifference(Date time1, Date time2, int targetUnits){ 
        double millisecondDiff = time1.getTime() - time2.getTime();
        return millisecondDiff / (double)TimeConstants.MULTIPLERS[targetUnits];
    }
    
    /** Get the label for a specific unit */
    public static String getLabel(int units){
        return TimeConstants.UNITS[units];
    }
    
    /** Get a ComboBoxModel for the supported time units */
    public static DefaultComboBoxModel getTimeUnitsComboModel(){
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for(int i=0;i<TimeConstants.UNIT_COUNT;i++){
            model.addElement(getLabel(i));
        }
        return model;
    }
}
