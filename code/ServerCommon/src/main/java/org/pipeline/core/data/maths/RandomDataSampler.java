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
import org.pipeline.core.data.*;
import org.pipeline.core.data.manipulation.RowExtractor;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class takes a random sample of rows from a data set. It can be configured
 * to take a configurable number of random rows in terms of a percentage from the
 * input data set.
 * @author hugo
 */
public class RandomDataSampler implements XmlStorable {
    /** List of sampled rows */
    private ArrayList<Integer> sampledRows = new ArrayList<>();
    
    /** List of remaining rows */
    private ArrayList<Integer> remainingRows = new ArrayList<>();
    
    /** Data to be sampled */
    private Data data;
    
    /** Number of rows to sample */
    private int numberOfSampledRows = 10;
    
    /** Should the sample be done as a percentage */
    private boolean percentageSample = true;

    public RandomDataSampler() {
    }

    public RandomDataSampler(Data data) {
        this.data = data;
    }

    public void reset(){
        sampledRows.clear();
        remainingRows.clear();
        for(int i=0;i<data.getLargestRows();i++){
            remainingRows.add(i);
        }
    }
    
    public Data sample() throws DataException {
        reset();
        int actualSampleRows;
        if(percentageSample){
            actualSampleRows = (int)(((double)numberOfSampledRows / 100.0) * (double)data.getLargestRows());
        } else {
            actualSampleRows = numberOfSampledRows;
        }
        
        if(actualSampleRows>data.getLargestRows()){
            actualSampleRows = data.getLargestRows();
        }
        
        int sampledCount = 0;
        int iterationLimit = data.getLargestRows() * 20;
        int dataRows = data.getLargestRows();
        int iterationCounter = 0;
        boolean found;
        int sampledRow;
        boolean stopFlag = false;
        while(sampledCount<actualSampleRows && stopFlag==false){
            found = false;
            iterationCounter = 0;
            while(found==false && stopFlag==false){
                sampledRow = (int)(Math.random() * dataRows);
                if(!sampledRows.contains(sampledRow)){
                    // This row has not yet been sampled
                    sampledRows.add(sampledRow);
                    remainingRows.remove(new Integer(sampledRow));
                    found = true;
                    sampledCount++;
                } else {
                    iterationCounter++;
                }
                
                if(iterationCounter>iterationLimit){
                    stopFlag = true;
                }
            }
        }
            
        RowExtractor extractor = new RowExtractor(data);
        Integer[] results = sampledRows.toArray(new Integer[sampledRows.size()]);
        return extractor.extract(results);
    }
    
    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("RandomDataSampler");
        store.add("NumberOfSampledRows", numberOfSampledRows);
        store.add("PercentageSample", percentageSample);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        numberOfSampledRows = store.intValue("NumberOfSampledRows", 10);
        percentageSample = store.booleanValue("PercentageSample", true);
    }

    public ArrayList<Integer> getRemainingRows() {
        return remainingRows;
    }

    public ArrayList<Integer> getSampledRows() {
        return sampledRows;
    }
    
    public Data getRemainingData() throws DataException {
        Integer[] rows = remainingRows.toArray(new Integer[remainingRows.size()]);
        RowExtractor extractor = new RowExtractor(data);
        return extractor.extract(rows);
    }

    public int getNumberOfSampledRows() {
        return numberOfSampledRows;
    }

    public void setPercentageSample(boolean percentageSample) {
        this.percentageSample = percentageSample;
    }

    public boolean isPercentageSample() {
        return percentageSample;
    }

    public void setNumberOfSampledRows(int numberOfSampledRows) {
        this.numberOfSampledRows = numberOfSampledRows;
    }
}