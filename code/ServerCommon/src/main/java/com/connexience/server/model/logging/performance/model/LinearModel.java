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
import javax.persistence.Basic;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.columns.DoubleColumn;
import org.pipeline.core.data.manipulation.ColumnPickerCollection;

/**
 * This class models execution time using a basic linear regression model
 * @author hugo
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "LINEAR")
public class LinearModel extends PerformanceModel {

    
    @Basic
    private boolean interceptIncluded = true;

    public double[] getRegressionCoefficients() {
        return getModelParameters();
    }

    public void setRegressionCoefficients(double[] regressionCoefficients) {
        setModelParameters(regressionCoefficients);
    }

    public boolean isInterceptIncluded() {
        return interceptIncluded;
    }

    public void setInterceptIncluded(boolean interceptIncluded) {
        this.interceptIncluded = interceptIncluded;
    }
    
    @Override
    public List<PerformanceModelPrediction> getPredictions(Data xData) throws Exception {
        ColumnPickerCollection pickers = getColumnList();
        ArrayList<PerformanceModelPrediction> results = new ArrayList<>();
        Data predictionData = pickers.extractData(xData);
        if(predictionData.getColumns()>0){
            // Check to see if there are the correct number of column
            if(interceptIncluded){
                if((predictionData.getColumns())!=(modelParameters.length - 1)) {
                    throw new Exception("Insufficient columns in xData");
                }
            } else {
                if(predictionData.getColumns()!=modelParameters.length){
                    throw new Exception("Insufficient columns in xData");
                }
            }
            
            
            int rows = predictionData.getLargestRows();
            int columnIndex = 0;
            
            for(int i=0;i<rows;i++){
                if(!predictionData.rowContainsMissingValues(i)){
                    double rowSum = 0;
                    int coefficientStartPoint;
                    if(interceptIncluded){
                        rowSum+=modelParameters[0];
                        coefficientStartPoint = 1;
                    } else {
                        coefficientStartPoint = 0;
                    }
                    columnIndex = 0;
                    for(int j=coefficientStartPoint;j<modelParameters.length;j++){
                        rowSum+=(modelParameters[j] * ((DoubleColumn)predictionData.column(columnIndex)).getDoubleValue(i));
                        columnIndex++;
                    }
                    results.add(new PerformanceModelPrediction(rowSum, rowSum, rowSum)); // TODO: Add confidence intervals
                } else {
                    // Add a blank prediction value
                    results.add(new PerformanceModelPrediction());
                }
            }
            return results;
        } else {
            throw new Exception("No model input columns");
        }
        
    }    
    
    @Override
    public PerformanceModelParameter[] getModelParameterSummary() {
        PerformanceModelParameter[] selections = getInputSelectionList();
        PerformanceModelParameter p;
        if(interceptIncluded){
            PerformanceModelParameter[] results = new PerformanceModelParameter[selections.length + 1];
            
            for(int i=0;i<modelParameters.length;i++){
                if(i==0){
                    p = new PerformanceModelParameter();
                    p.setColumnType(PerformanceModelParameter.MODEL_PARAMETER);
                    p.setColumnName("Intercept");
                    p.setValue(modelParameters[i]);
                    results[i] = p;
                } else {
                    p = new PerformanceModelParameter();
                    p.setColumnType(selections[i - 1].getColumnType());
                    p.setColumnName(selections[i - 1].getColumnName());
                    p.setValue(modelParameters[i]);
                    results[i] = p;
                }
            }
            return results;
            
        } else {
            PerformanceModelParameter[] results = new PerformanceModelParameter[selections.length];
            for(int i=0;i<modelParameters.length;i++){
                p = new PerformanceModelParameter();
                p.setColumnType(selections[i - 1].getColumnType());
                p.setColumnName(selections[i - 1].getColumnName());
                p.setValue(modelParameters[i]);
                results[i] = p;
            }            
            return results;
        }
    }    
}