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
package com.connexience.server.model.logging.performance.model;

import com.connexience.server.model.logging.performance.PerformanceModel;
import com.connexience.server.model.logging.performance.PerformanceModelParameter;
import com.connexience.server.model.logging.performance.PerformanceModelPrediction;
import com.connexience.server.model.logging.performance.PerformanceModelResults;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.json.JSONObject;
import org.pipeline.core.data.Data;

/**
 * This class represents execution as a fixed time with no parameters.
 * @author hugo
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "FIXEDTIME")
public class FixedTimeModel extends PerformanceModel {
    @Override
    public List<PerformanceModelPrediction> getPredictions(Data xData) throws Exception {
        double meanValue = modelParameters[0];
        double standardDeviation = modelParameters[1];
        ArrayList<PerformanceModelPrediction> results = new ArrayList<>();
        if(xData.getLargestRows()>0){
            for(int i=0;i<xData.getLargestRows();i++){
                results.add(new PerformanceModelPrediction(meanValue, meanValue + (2 * standardDeviation), meanValue - (2 * standardDeviation)));
            }
        } else {
            // Add a single value if there is no data
            results.add(new PerformanceModelPrediction(meanValue, meanValue + (2 * standardDeviation), meanValue - (2 * standardDeviation)));
        }
        
        return results;
    }
    
    /** Set the standard deviation of the data */
    public void setStandardDeviation(double standardDeviation) {
        if(modelParameters==null || modelParameters.length!=2){
            modelParameters = new double[2];
        }
        modelParameters[1] = standardDeviation;
    }

    /** Set the mean value of the data */
    public void setMeanValue(double meanValue) {
        if(modelParameters==null || modelParameters.length!=2){
            modelParameters = new double[2];
        }        
        modelParameters[0] = meanValue;
    }

    @Override
    public PerformanceModelParameter[] getModelParameterSummary() {
        PerformanceModelParameter[] results = new PerformanceModelParameter[2];
        results[0] = new PerformanceModelParameter("MeanValue", PerformanceModelParameter.MODEL_PARAMETER, modelParameters[0]);
        results[1] = new PerformanceModelParameter("StandardDeviation", PerformanceModelParameter.MODEL_PARAMETER, modelParameters[1]);
        return results;
    }
}