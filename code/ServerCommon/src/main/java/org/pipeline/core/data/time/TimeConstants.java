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

/**
 * This interface provides a number of time related constants that are
 * used by the time processing functions
 * @author hugo
 */
public interface TimeConstants {
    /** Units are milliseconds */
    public static final int MILLISECONDS = 0;
    
    /** Units are seconds */
    public static final int SECONDS = 1;
    
    /** Units are minutes */
    public static final int MINUTES = 2;
    
    /** Units are hours */
    public static final int HOURS = 3;
    
    /** Units are days */
    public static final int DAYS = 4;
    
    /** Units are weeks */
    public static final int WEEKS = 5;
    
    /** Number of units supported */
    public static final int UNIT_COUNT = 6;
    
    /** Multipliers */
    public static final long[] MULTIPLERS = {1,         // Milliseconds
                                             1000,      // Seconds
                                             60000,     // Minutes
                                             3600000,   // Hours
                                             86400000,  // Days
                                             604800000};// Weeks

    /** String keys for labels */
    public static final String[] KEYS = {"MILLISECONDS_LABEL", "SECONDS_LABEL", "MINUTES_LABEL", "HOURS_LABEL", "DAYS_LABEL", "WEEKS_LABEL"};

    /** Units for labels */
    public static final String[] UNITS = {"Milliseconds", "Seconds", "Minutes" ,"Hours", "Days", "Weeks"};
}
