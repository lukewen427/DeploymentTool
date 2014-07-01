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

package org.pipeline.core.data;

import java.io.Serializable;

/**
 * This class represents a missing value in a data column
 * @author hugo
 */
public class MissingValue implements Serializable
{
    static final long serialVersionUID = -153603249070293003L;
    
    /** Missing value message */
    public final static String MISSING_VALUE_TEXT = "MISSING";
    
    /** Text that is used to represent missing values in internal code */
    public final static String MISSING_VALUE_REPRESENTATION = "_XX_MISSING_XX_";

    /** Message for missing value */
    public static final String MISSING_VALUE_MESSAGE="[MISSING]";


    /**
     * Creates a new instance of MissingValue.
     * 
     * <p>Please DO NOT USE this constructor.
     * The preferred way to get an instance of <code>MissingValue</code> is 
     * using static {@link #get()}.
     * </p>
     * 
     * TODO: Make this constructor private at the next deep update of the system.
     * Once the constructor is made private, search through the code for text: 
     * <code> instanceof MissingValue</code> and remove it.
     * For more details contact Jacek.
     */
    public MissingValue()
    { }


    /** The preferred way to get an instance of MissingValue. */
    public static MissingValue get()
    {
        return _missingValue;
    }


    /** Override toString method */
    public String toString()
    {
        return MISSING_VALUE_TEXT;
    }


    private static final MissingValue _missingValue = new MissingValue();
}
