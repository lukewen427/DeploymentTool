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
package org.pipeline.core.data.cxd;

import org.pipeline.core.data.Column;

/**
 * This class monitors the load process of a set of data
 * @author hugo
 */
public class ColumnReadMonitor {
    /** Number of columns */
    private int columns;
    
    /** Set of flags to indicate when all of the columns have finished */
    private boolean[] finishedFlags;
    
    /** Set of rows which is updated as data is read */
    
    /** Set the number of columns */
    public void setColumns(int columns){
        this.columns = columns;
        finishedFlags = new boolean[columns];
        for(int i=0;i<columns;i++){
            finishedFlags[i] = false;
        }
    }
    
    /** Have all the columns finished loading */
    public synchronized boolean allColumnsFinished(){
        for(int i=0;i<columns;i++){
            if(finishedFlags[i]==false){
                return false;
            }
        }
        return true;
    }
    
    /** Set a flag specifying that a column has been completely read */
    public void setColumnFinished(int index){
        finishedFlags[index] = true;
    }
    
    /** A new row has been read for a column */
    public void rowRead(int index, Column col) {

    }
}
