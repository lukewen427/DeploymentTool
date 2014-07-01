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

package org.pipeline.core.matrix.regression;

import org.pipeline.core.matrix.Matrix;

/**
 * This class provides a non-linear regression that takes polynomial transforms
 * of the input data and performs a simple linear regression on the results
 * @author nhgh
 */
public class NonlinearRegressionCalculator {
    /** Maximum order for the transforms */
    private int maxOrder = 2;
    
    /** Add a column of ones to the transformed data */
    private boolean appendOnes = false;
    
    /** Matrix of regression coefficients */
    private Matrix coefficients;
    
    /** X-Data */
    private Matrix x;
    
    /** Y-Data */
    private Matrix y;
    
    /** Creates a new instance of NonlinearRegressionCalculator */
    public NonlinearRegressionCalculator(Matrix x, Matrix y) {
        this.x = x;
        this.y = y;
    }
    
    /** Calculate the regression coefficients */
    public Matrix calculate() {
        Matrix data = buildPolynomialData(x, maxOrder, appendOnes);
        return new LinearRegressionCalculator(data, y).calculate();
    }
    
    /** Build a polynomial data set with a specified order with augmentation if required */
    public static Matrix buildPolynomialData(Matrix original, int order, boolean augment) {
        int cols = original.getColumnDimension();
        int rows = original.getRowDimension();
        
        // Calculate new matrix size
        int newCols = (cols * order);
        if(augment){
            newCols++;
        }
        Matrix updated = new Matrix(rows, newCols);
        
        // Augment if needed
        int count = 0;
        if(augment){
            for(int i=0;i<rows;i++){
                updated.set(i, 0, 1);
            }
            count++;
        }
        
        // Fill in the rest of the data
        for(int i=0;i<cols;i++){
            for(int j=1;i<=order;j++){
                for(int k=0;k<rows;k++){
                    updated.set(k, count, Math.pow(original.get(k, i), (double)j));
                }
                count++;
            }
        }
        
        return updated;
    }

    /** Get the maximum polynomial order for this model */
    public int getMaxOrder() {
        return maxOrder;
    }

    /** Set the maximum polynomial order for this model */
    public void setMaxOrder(int maxOrder) {
        if(maxOrder>0){
            this.maxOrder = maxOrder;
        }
    }

    /** Is a column of ones appended to the raw data */
    public boolean isAppendOnes() {
        return appendOnes;
    }

    /** Set whether to append a column of ones to the raw data */
    public void setAppendOnes(boolean appendOnes) {
        this.appendOnes = appendOnes;
    }
}