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
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 * This class is used to serialise matrices using the xmlstorage package. It 
 * provides a matrix with an XmlStorable interface so that it can be easily
 * saved.
 * @author hugo
 */
public class MatrixSerializer implements XmlStorable {
    /** Matrix to be saved or reloaded */
    private Matrix matrix = null;

    /** Creates a new instance of MatrixSerializer */
    public MatrixSerializer() {
    }
    
    /** Creates a new instance of MatrixSerializer */
    public MatrixSerializer(Matrix matrix) {
        this.matrix = matrix;
    }

    /** Get the matrix */
    public Matrix getMatrix(){
        return matrix;
    }
    
    /** Recreate a matrix */
    public void recreateObject(XmlDataStore xmlDataStore) throws XmlStorageException {
        int rows = xmlDataStore.intValue("Rows", 0);

        String row;
        MatrixCSVReader reader = new MatrixCSVReader();
        
        try {
            for(int i=0;i<rows;i++){
                reader.appendDataLine(xmlDataStore.stringValue("Row" + i, ""));
            }
            matrix = reader.getMatrix();
        } catch (Exception e){
            e.printStackTrace();
            throw new XmlStorageException("Cannot parse matrix data");
        }
    }

    /** Save a matrix to XML */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("Matrix");
        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();
        StringBuffer rowText;
        store.add("Rows" , rows);
        
        // Save each row as a comma delimited string
        for(int i=0;i<rows;i++){
            rowText = new StringBuffer();
            
            for(int j=0;j<cols;j++){
                rowText.append(matrix.get(i, j));
                if(j<(cols-1) && cols>1){
                    rowText.append(",");
                }
            }
            store.add("Row" + i, rowText.toString());
        }
       
        return store;
    }
}
