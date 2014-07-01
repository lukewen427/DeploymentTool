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

package org.pipeline.core.matrix.table;

import org.pipeline.core.matrix.Matrix;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.Enumeration;
import java.util.Vector;
/**
 * This class wraps a matrix in a table model for viewing.
 * @author hugo
 */
public class MatrixTableModel implements TableModel {
    /** Matrix to view */
    private Matrix m;
    
    /** Is this matrix writable */
    private boolean writable = false;
    
    /** Listeners */
    private Vector listeners = new Vector();
    
    /** Creates a new instance of MatrixTableModel */
    public MatrixTableModel(Matrix m, boolean writable) {
        this.m = m;
        this.writable = writable;
    }

    /** Is the matix writable */
    public boolean isWritable() {
        return writable;
    }

    /** Set writable status */
    public void setWritable(boolean writable) {
        this.writable = writable;
    }
    
    /** Notify a change */
    public void notifiyMatrixChange(){
        Enumeration e = listeners.elements();
        while(e.hasMoreElements()){
            ((TableModelListener)e.nextElement()).tableChanged(new TableModelEvent(this));
        }
    }
    
    // ==========================================================
    // TableModel implementation
    // ==========================================================

    /** Remove listener */
    public void removeTableModelListener(TableModelListener l) {
        if(listeners.contains(l)){
            listeners.removeElement(l);
        }
    }

    /** Add listener */
    public void addTableModelListener(TableModelListener l) {
        if(!listeners.contains(l)){
            listeners.addElement(l);
        }
    }

    /** Set a value */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(writable){
            double v = ((Double)aValue).doubleValue();
            m.set(rowIndex, columnIndex, v);
        }
    }

    /** Get a column name */
    public String getColumnName(int columnIndex) {
        return "C" + columnIndex;
    }
    
    /** All double objects */
    public Class getColumnClass(int columnIndex) {
        return Double.class;
    }

    /** Is a cell editable */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return writable;
    }

    /** Get a value */
    public Object getValueAt(int rowIndex, int columnIndex) {
        return new Double(m.get(rowIndex, columnIndex));
    }

    /** Get the number of rows */
    public int getRowCount() {
        return m.getRowDimension();
    }

    /** Get the number of columns */
    public int getColumnCount() {
        return m.getColumnDimension();
    }
}
