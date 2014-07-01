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
package com.connexience.server.model.datasets;

/**
 * This interface defines a few constants for data sets
 * @author hugo
 */
public interface DatasetConstants {
    // Constants for data accumulation in single value items
    
    /** Updating a value replaces its data */
    public static final String UPDATE_REPLACES_VALUES = "replace";
    
    /** Updating a value accumulates a maximum value */
    public static final String UPDATE_CALCULATES_MAXIMUM = "maximum";
    
    /** Updating a value accumulates a minimum value */
    public static final String UPDATE_CALCULATES_MINIMUM = "minimum";
    
    /** Updating a value accumulates a mean value */
    public static final String UPDATE_CALCULATES_AVERAGE = "average";
    
    /** Updating a value accumulates a summation */
    public static final String UPDATE_CALCULATES_SUM = "sum";
    
    /** Constant for one day */
    public static final String DAY = "Day";
    
    /** Constant for one hour */
    public static final String HOUR = "Hour";
    
    /** Constant for one minute */
    public static final String MINUTE = "Minute";
    
    /** Constant for one second */
    public static final String SECOND = "Second";
    
    /** Constant for one week */
    public static final String WEEK = "Week";
    
    /** Constant for one year */
    public static final String YEAR = "Year";
    
    /** Connection type enum */
    public enum CONNECTION_TYPE {
        NO_CONNECTION, HIBERNATE_CONNECTION, JDBC_CONNECTION
    }
}