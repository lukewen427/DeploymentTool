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

package org.pipeline.gui.data.table;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.MissingValue;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.util.Enumeration;
import java.util.Vector;
/**
 * This class provides a TableModel for a set of Data
 * @author  hugo
 */
public class DataTableModel extends DefaultTableModel {
    /** Data set */
    private Data data = null;
    
    /** Is the data editable */
    private boolean editable = false;
    
    /** Listeners */
    private Vector listeners = new Vector();
    
    /** Edit event listeners */
    private Vector editListeners = new Vector();
    
    /** Creates a new instance of DataTableModel */
    public DataTableModel(Data data) {
        super();
        this.data = data;
    }
    
    /** Is this data editable */
    public boolean isEditable(){
        return editable;
    }
    
    /** Set whether this data is editable */
    public void setEditable(boolean editable){
        this.editable = editable;
    }
    
    /** Notify listeners of data change */
    public void notifyDataChange(){
        Enumeration e = listeners.elements();
        TableModelEvent evt = new TableModelEvent(this);
        while(e.hasMoreElements()){
            ((TableModelListener)e.nextElement()).tableChanged(evt);
        }
    }
    
    /** Add an edit listener */
    public void addDataTableEditListener(DataTableEditListener listener){
        if(!editListeners.contains(listener)){
            editListeners.addElement(listener);
        }
    }
    
    /** Remove an edit listener */
    public void removeDataTableEditListener(DataTableEditListener listener){
        if(editListeners.contains(listener)){
            editListeners.removeElement(listener);
        }
    }
    
    /** Notify an edit change */
    private void notifyEdit(){
        Enumeration e = editListeners.elements();
        while(e.hasMoreElements()){
            ((DataTableEditListener)e.nextElement()).dataChanged(data);
        }
    }
    
    // =========================================================================
    // TableModel implementation
    // =========================================================================

    /**
     * Sets the value in the cell at <code>columnIndex</code> and
     * <code>rowIndex</code> to <code>aValue</code>.
     * 
     * @param	aValue		 the new value
     * @param	rowIndex	 the row whose value is to be changed
     * @param	columnIndex 	 the column whose value is to be changed
     * @see #getValueAt
     * @see #isCellEditable
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(editable && data!=null){

            try {
                if(columnIndex<data.getColumns()){
                    Column c = data.column(columnIndex);
                    if(rowIndex<c.getRows()){
                        c.setObjectValue(rowIndex, aValue);
                        notifyEdit();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }            
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     * 
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
        if(data!=null){
            return data.getLargestRows();
        } else {
            return 0;
        }
    }

    /**
     * Returns true if the cell at <code>rowIndex</code> and
     * <code>columnIndex</code>
     * is editable.  Otherwise, <code>setValueAt</code> on the cell will not
     * change the value of that cell.
     * 
     * @param	rowIndex	the row whose value to be queried
     * @param	columnIndex	the column whose value to be queried
     * @return	true if the cell is editable
     * @see #setValueAt
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return editable;
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     * 
     * @param	rowIndex	the row whose value is to be queried
     * @param	columnIndex 	the column whose value is to be queried
     * @return	the value Object at the specified cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(data!=null){
            try {
                Object value;
                
                if(columnIndex<data.getColumns()){
                    Column c = data.column(columnIndex);
                    if(rowIndex<c.getRows()){
                        value = c.getObjectValue(rowIndex);
                        if(editable){
                            if(value == MissingValue.get() || value instanceof MissingValue){
                                return null;
                            } else {
                                return value;
                            }
                        } else {
                            return value.toString();
                        }
                        
                    } else {
                        return "";
                    }

                } else {
                    return "N/A";
                }
            } catch (Exception e){
                return "";
            }
            
        } else {
            return "N/A";
        }
    }

    /**
     * Returns the name of the column at <code>columnIndex</code>.  This is used
     * to initialize the table's column header name.  Note: this name does
     * not need to be unique; two columns in a table can have the same name.
     * 
     * @param	columnIndex	the index of the column
     * @return  the name of the column
     */
    public String getColumnName(int columnIndex) {
        if(data!=null){            
            try {
                if(columnIndex>=0 && columnIndex<data.getColumns()){
                    return ((Column)data.column(columnIndex)).getName();
                } else {
                    return "N/A";
                }
            } catch (Exception e){
                return "";
            }
        } else {
            return "N/A";
        }
    }

    /**
     * Returns the most specific superclass for all the cell values 
     * in the column.  This is used by the <code>JTable</code> to set up a 
     * default renderer and editor for the column.
     * 
     * @param columnIndex  the index of the column
     * @return the common ancestor class of the object values in the model.
     */
    public Class getColumnClass(int columnIndex) {
        if(data!=null){
            if(editable){
                return data.column(columnIndex).getDataType();
            } else {
                return String.class;
            }
        } else {
            return null;
        }
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     * 
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount() {
        if(data!=null){
            return data.getColumns();
        } else {
            System.out.println("No data");
            return 0;
        }
    }

    /** Remove a listener */
    public void removeTableModelListener(TableModelListener l) {
        if(listeners.contains(l)){
            listeners.removeElement(l);
        }
    }

    /** Add a listener */
    public void addTableModelListener(TableModelListener l) {
        if(!listeners.contains(l)){
            listeners.add(l);
        }
    }
    
    /** Static utility method to set the size of all the columns */
    public static void setColumnWidths(JTable table, int width){
        // Set preferred column widths
        int size = table.getColumnModel().getColumnCount();
        for(int i=0;i<size;i++){
            table.getColumnModel().getColumn(i).setMinWidth(75);
        }                 
    }
}
