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

import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class represents a range of numerical data
 * @author nhgh
 */
public class NumericalRange implements XmlStorable {
    /** Upper bound of the range */
    private double upperBound = 1.0;

    /** Lower bound of the range */
    private double lowerBound = -1.0;

    /** Set the upper bound */
    public void setUpperBound(double upperBound){
        this.upperBound = upperBound;
    }

    /** Get the upper bound */
    public double getUpperBound(){
        return upperBound;
    }

    /** Set the lower bound */
    public void setLowerBound(double lowerBound){
        this.lowerBound = lowerBound;
    }

    /** Get the lower bound */
    public double getLowerBound(){
        return lowerBound;
    }

    /** Does a value lie within this range */
    public boolean isWithinRange(double value){
        if(value>=lowerBound && value<=upperBound){
            return true;
        } else {
            return false;
        }
    }

    /** Count the number of hits for this range given a column of numerical data */
    public int countHits(NumericalColumn column) throws DataException {
        int count = 0;
        int size = column.getRows();
        for(int i=0;i<size;i++){
            if(!column.isMissing(i)){
                if(isWithinRange(column.getDoubleValue(i))){
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return lowerBound + " <= x <= " + upperBound;
    }
        
    /** Save to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("NumericalRange");
        store.add("UpperBound", upperBound);
        store.add("LowerBound", lowerBound);
        return store;
    }

    /** Load from storage */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        upperBound = store.doubleValue("UpperBound", 1.0);
        lowerBound = store.doubleValue("LowerBound", -1.0);
    }
}
