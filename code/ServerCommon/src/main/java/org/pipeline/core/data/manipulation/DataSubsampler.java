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

package org.pipeline.core.data.manipulation;

import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;

/**
 * This class subsamples a set of data at every n'th row
 * @author hugo
 */
public class DataSubsampler {
    /** Data to subsample */
    private Data rawData;
        
    /** Subsampled data */
    private Data subsampledData;
    
    /** Remaining data */
    private Data remainingData;
    
    /** Subsample interval */
    private int interval = 2;
    
    /** Creates a new instance of DataSubsampler */
    public DataSubsampler(Data rawData) {
        this.rawData = rawData;
    }
    
    /** Set the interval */
    public void setInterval(int interval){
        if(interval>0){
            this.interval = interval;
        }
    }
    
    /** Get the interval */
    public int getInterval(){
        return interval;
    }
    
    /** Get the subsampled data */
    public Data sampleData() throws DataException {
        subsampledData = rawData.getEmptyCopy();
        remainingData = rawData.getEmptyCopy();
        
        int cols = rawData.getColumns();
        int rows = rawData.getLargestRows();
        int count = 0;
        
        for(int i=0;i<rows;i++){
            count++;
            
            // Sampled row
            if(count>=interval){
                subsampledData.appendRows(rawData.getRowSubset(i, i, true), true);
                count = 0;
            } else {
                remainingData.appendRows(rawData.getRowSubset(i, i, true), true);
            }
        }
        return subsampledData;
    }
    
    /** Get the remaining data */
    public Data getRemainingData(){
        return remainingData;
    }
    
    /** Get the subsampled data */
    public Data getSubsampledData(){
        return subsampledData;
    }
}
