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

import org.pipeline.core.data.Column;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.NumericalColumn;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class extracts all the numerical columns from a set of data.
 * @author hugo
 */
public class NumericalColumnExtractor {
    /** Data to be manipulated */
    private Data data = null;
    
    /** Creates a new instance of NumericalColumnExtractor */
    public NumericalColumnExtractor(Data data) {
        this.data = data;
    }
    
    /** Extract without copying */
    public Vector extractColumns() {
        Vector cols = new Vector();
        for(int i=0;i<data.getColumns();i++){
            try {
                if(data.column(i) instanceof NumericalColumn){
                    cols.addElement(data.column(i));
                }
            } catch (IndexOutOfBoundsException e){
            }
        }
        return cols;
    }

    /** Extract the non-numerical columns without copying */
    public Vector extractNonNumericalColumns(){
        Vector cols = new Vector();
        for(int i=0;i<data.getColumns();i++){
            try {
                if(!(data.column(i) instanceof NumericalColumn)){
                    cols.addElement(data.column(i));
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return cols;

    }
    
    /** Find the shortest numerical column */
    public int getShortestNumericalColumnLength(){
        Vector columns = extractColumns();
        if(columns.size()>0){
            Enumeration e = columns.elements();
            Column c;
            int shortestLength = Integer.MAX_VALUE;
            
            while(e.hasMoreElements()){
                c = (Column)e.nextElement();
                if(c.getRows()<shortestLength){
                    shortestLength = c.getRows();
                }
            }
            
            if(shortestLength!=Integer.MAX_VALUE){
                return shortestLength;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /** Find the shortest numerical column */
    public int getShortestNonNumericalColumnLength(){
        Vector columns = extractNonNumericalColumns();
        if(columns.size()>0){
            Enumeration e = columns.elements();
            Column c;
            int shortestLength = Integer.MAX_VALUE;

            while(e.hasMoreElements()){
                c = (Column)e.nextElement();
                if(c.getRows()<shortestLength){
                    shortestLength = c.getRows();
                }
            }

            if(shortestLength!=Integer.MAX_VALUE){
                return shortestLength;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /** Extract with copying */
    public Data copyColumns() throws DataException {
        Data d = new Data();
        for(int i=0;i<data.getColumns();i++){
            try {
                if(data.column(i) instanceof NumericalColumn){
                    d.addColumn(data.column(i).getCopy());
                }
            } catch (IndexOutOfBoundsException e){
            }
        }        
        return d;
    }
}
