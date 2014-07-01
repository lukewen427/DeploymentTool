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
 * This class calculates a regression model given a set of X and Y
 * data using a pseudo inverse.
 * @author hugo
 */
public class LinearRegressionCalculator {
    /** X-Values */
    Matrix x;
    
    /** Y-Values */
    Matrix y;
    
    /** Creates a new instance of LinearRegressionCalculator */
    public LinearRegressionCalculator(Matrix x, Matrix y) {
        this.x = x;
        this.y = y;
    }
    
    /** Calculate the regression model. Parameters are calculated using:
     * B = ((X'X)^-1)X'Y */
    public Matrix calculate(){
        Matrix xTx = x.transpose().times(x);
        Matrix temp = xTx.inverse().times(x.transpose());
        Matrix B = temp.times(y);
        return B;
    }
}
