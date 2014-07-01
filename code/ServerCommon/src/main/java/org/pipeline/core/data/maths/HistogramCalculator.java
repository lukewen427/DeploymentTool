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
package org.pipeline.core.data.maths;

import java.util.ArrayList;
import java.util.List;
import org.pipeline.core.data.Column;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.NumericalColumn;
import org.pipeline.core.data.NumericalRange;
import org.pipeline.core.data.columns.DoubleColumn;

/**
 * This class calculates a histogram for a column of numerical data. It returns
 * a Data object with the first three columns the min,centre.max of the range and 
 * this fourth column the number of hits in each range
 * @author hugo
 */
public class HistogramCalculator {
    /** Column to calculate the histogram on */
    private NumericalColumn column;

    /** Number of bins */
    private int binCount = 10;
    
    /** List of ranges */
    private List<NumericalRange> ranges = new ArrayList<>();
    
    public HistogramCalculator(NumericalColumn column) {
        this.column = column;
    }

    public void setBinCount(int binCount) {
        this.binCount = binCount;
    }

    public int getBinCount() {
        return binCount;
    }
    
    /** Produce a histogram */
    public Data getHistogramData() throws DataException {
        buildBins();
        int count;
        double binStart;
        double binEnd;
        double binCentre;
        Data histogram = new Data();
        histogram.addColumn(new DoubleColumn("LowerBound"));
        histogram.addColumn(new DoubleColumn("MidPoint"));
        histogram.addColumn(new DoubleColumn("UpperBound"));
        histogram.addColumn(new DoubleColumn("HitCount"));
        
        int pos = 0;
        for(NumericalRange r : ranges){
            count = r.countHits(column);
        
            binStart = r.getLowerBound();
            binEnd = r.getUpperBound();
            binCentre = binStart + ((binEnd - binStart) / 2.0);
        
            ((DoubleColumn)histogram.column(0)).appendDoubleValue(binStart);
            ((DoubleColumn)histogram.column(1)).appendDoubleValue(binCentre);
            ((DoubleColumn)histogram.column(2)).appendDoubleValue(binEnd);
            ((DoubleColumn)histogram.column(3)).appendDoubleValue(count);
        }
        
        return histogram;
    }
    
    /** Build the bins */
    private void buildBins() throws DataException {
        if(((Column)column).getNonMissingRows() > 0){
            MinValueCalculator minCalc = new MinValueCalculator(column);
            MaxValueCalculator maxCalc = new MaxValueCalculator(column);

            double minValue = minCalc.doubleValue();
            double maxValue = maxCalc.doubleValue();
            
            double span = maxValue - minValue;
            double binWidth = span / (double)binCount;
            double startPos = minValue;
            ranges.clear();;
            NumericalRange r;
            for(int i=0;i<binCount;i++){
                r = new NumericalRange();
                r.setLowerBound(startPos);
                r.setUpperBound(startPos + binWidth);
                startPos+=binWidth;
                ranges.add(r);
            }
        } else {
            throw new DataException("Column contains no non-missing rows");
        }
    }
}
