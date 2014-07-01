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

package org.pipeline.core.matrix.xmlstorage;

import org.pipeline.core.matrix.Matrix;

import java.util.Vector;

/**
 * This class imports a delimited text file into a data set
 * @author hugo
 */
public class MatrixCSVReader {      
    /** Vector of imported rows */
    private Vector importedRows = new Vector();
    
    /** Is this the first line */
    private boolean firstRow = true;
    
    /** Expected number of columns */
    private int expectedCols = 0;
    
    /**
     * Creates a new instance of MatrixCSVReader 
     */
    public MatrixCSVReader() {

    }
    
    /** Reset the reader */
    public void reset(){
        importedRows.clear();
        firstRow = true;
    }
    
    /** Get the matrix */
    public Matrix getMatrix(){
        int rows = importedRows.size();
        Matrix m = new Matrix(rows, expectedCols);
        for(int i=0;i<rows;i++){
            m.setMatrix(i, i, 0, expectedCols - 1, (Matrix)importedRows.elementAt(i));
        }
        return m;
    }
     
    /** Process a line of text */
    public void appendDataLine(String line) throws MatrixReadException {
        Vector importedFields = splitDelimitedTextLine(line);
        int cols = importedFields.size();
        if(firstRow){
            expectedCols = cols;
            
        } else {
            // Check size
            if(cols!=expectedCols){
                // TODO: Internationalise
                throw new MatrixReadException("Matrix rows must have equal numbers of columns");
            }
        }
            
        Matrix row = new Matrix(1, cols);
        for(int i=0;i<cols;i++){
            try {
                row.set(0, i, Double.parseDouble(importedFields.elementAt(i).toString()));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        importedRows.addElement(row);
    }
    
    /** Split a line of text into a vector of delimited values */
    private Vector splitDelimitedTextLine(String line) throws MatrixReadException {
        boolean withinQuotes = false;
        String temporaryDelimiter = null;
        int pos1 = 0;
        int pos2 = 0;
        String remains;
        Vector fragments = new Vector();
        

        while (true) {
            // Grab the next token
            pos2 = line.indexOf(",", pos1);

            if (pos2!=-1) {
                // Add a standard fragment to the fragments vector
                fragments.addElement(line.substring(pos1, pos2).trim());
                pos1 = pos2 + 1;

            } else {
                // Handle the last value differently
                remains = line.substring(pos1, line.length()).trim();
                if (!remains.equalsIgnoreCase("")) {
                    fragments.addElement(remains);

                } else {
                    fragments.addElement("");
                }

            // Finished this line of data
            break;
            }
        }

        return fragments;
    }
    
}
