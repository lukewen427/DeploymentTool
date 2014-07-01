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

import org.pipeline.core.data.Data;

import javax.swing.*;
import java.awt.*;


/**
 * This class provides a table that can show a set of data
 * @author hugo
 */
public class DataTable extends JPanel {
    /** Data being displayed */
    private Data data = null;
        
    /** Table scroll pane */
    private JScrollPane tableScroller;
    
    /** Minimum column width */
    private int minimumWidth = 100;
    
    /** Current JTable */
    private JTable table = null;
    
    /** Create with no data */
    public DataTable(){
        setLayout(new BorderLayout());
        tableScroller = new JScrollPane();
        tableScroller.setViewportView(new JTable());
        add(tableScroller, BorderLayout.CENTER);
    }
    
    /** Create with data */
    public DataTable(Data data){
        setLayout(new BorderLayout());
        tableScroller = new JScrollPane();
        add(tableScroller, BorderLayout.CENTER);
        setData(data);
    }
    
    /** Set the data to display */
    public void setData(Data data){
        this.data = data;
        if(data!=null){
            DataTableModel model = new DataTableModel(data);
            table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setModel(new DataTableModel(data));

            // Set preferred column widths
            int size = table.getColumnModel().getColumnCount();
            for(int i=0;i<size;i++){
                table.getColumnModel().getColumn(i).setMinWidth(minimumWidth);
            }         
            tableScroller.setViewportView(table);   
        } else {
            table = null;
            tableScroller.setViewportView(new JTable());
        }
    }
    
    /** Get the selected row. This method returns -1 if no row is selected */
    public int getSelectedRow(){
        if(table!=null){
            return table.getSelectedRow();
        } else {
            return -1;
        }
    }
}
