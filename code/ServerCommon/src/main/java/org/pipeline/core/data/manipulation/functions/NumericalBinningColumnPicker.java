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

package org.pipeline.core.data.manipulation.functions;

import org.pipeline.core.data.*;
import org.pipeline.core.data.columns.DoubleColumn;
import org.pipeline.core.data.columns.IntegerColumn;
import org.pipeline.core.data.manipulation.ColumnPicker;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.util.ArrayList;

/**
 * This class takes a numerical column and counts the number of times values fall
 * into one of several pre-defined ranges.
 * @author nhgh
 */
public class NumericalBinningColumnPicker extends ColumnPicker {
    /** Collection of ranges */
    private ArrayList<NumericalRange> ranges = new ArrayList<>();


    /** Construct and set supported column types */
    public NumericalBinningColumnPicker() {
        setLimitColumnTypes(true);
        addSupportedColumnClass(DoubleColumn.class);
        addSupportedColumnClass(IntegerColumn.class);
        addSupportedColumnClass(NumericalColumn.class);
        setCopyData(false);
    }

    /** Get the number of ranges */
    public int getRangeCount(){
        return ranges.size();
    }

    /** Get a range */
    public NumericalRange getRange(int index){
        return ranges.get(index);
    }

    /** Add a new range */
    public void addRange(NumericalRange range){
        ranges.add(range);
    }

    /** Delete a range */
    public void deleteRange(int index){
        ranges.remove(index);
    }

    /** Delete a range */
    public void deleteRange(NumericalRange range){
        ranges.remove(range);
    }
    
    @Override
    public Column pickColumn(Data data) throws IndexOutOfBoundsException, DataException {
        Column col = super.pickColumn(data);
        if(col instanceof NumericalColumn){
            NumericalColumn column = (NumericalColumn)col;
            IntegerColumn hitCount = new IntegerColumn("HitCount");
            for(int i=0;i<ranges.size();i++){
                hitCount.appendIntValue(ranges.get(i).countHits(column));
            }
            return hitCount;
        } else {
            throw new DataException("Numerical binning counter only supports numerical columns");
        }
    }

    @Override
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        super.recreateObject(xmlDataStore);
        ranges.clear();
        int size = xmlDataStore.intValue("RangeCount", 0);
        for(int i=0;i<size;i++){
            ranges.add((NumericalRange)xmlDataStore.xmlStorableValue("Range" + i));
        }
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("RangeCount", ranges.size());
        for(int i=0;i<ranges.size();i++){
            store.add("Range" + i, ranges.get(i));
        }
        return store;
    }
}