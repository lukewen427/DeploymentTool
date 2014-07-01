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

/**
 * This interface defines functionlity for numerical columns. It is used so
 * that things don't necessarily have to only work with DoubleColumns when
 * numerical stuff is needed.
 * @author hugo
 */
public interface NumericalColumn {
    /** Minimum value */
    public double minValue();
    
    /** Maximum value */
    public double maxValue();
    
    /** Get values as a double[] array */
    public double[] getDoubleArray();
    
    /** Get value as a double */
    public double getDoubleValue(int index) throws IndexOutOfBoundsException, DataException;
    
    /** Set value as a double */
    public void setDoubleValue(int index, double value) throws DataException, IndexOutOfBoundsException;

    /** Get a value as a long */
    public long getLongValue(int index) throws IndexOutOfBoundsException, DataException;

    /** Set a value as a long */
    public void setLongValue(int index, long value) throws IndexOutOfBoundsException, DataException;

    /** Append a double value */
    public void appendDoubleValue(double value) throws DataException;

    /** Append a long value */
    public void appendLongValue(long value) throws DataException;
    
    /** Get the number of rows */
    public int getRows();
    
    /** Is a value missing */
    public boolean isMissing(int index) throws IndexOutOfBoundsException;
    
    /** Get the column name */
    public String getName();
}
