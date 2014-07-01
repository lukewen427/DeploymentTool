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
package org.pipeline.core.util.block;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.manipulation.ColumnPicker;
import org.pipeline.gui.data.table.DataTableModel;

import javax.swing.*;
import java.awt.*;

/**
 * This class provides a generic tabular preview of a column picker block.
 * @author hugo
 */
public class GenericTablePreviewPanel extends JPanel implements ColumnPickerBlockPreviewPanel {
    /** Title caption */
    private JLabel title;
    
    /** Scroller for the table */
    private JScrollPane scroller;
    
    /** Data table */
    private JTable dataTable;
    
    /** Constructor */
    public GenericTablePreviewPanel(){
        setLayout(new BorderLayout());
        title = new JLabel("Results");
        add(title, BorderLayout.NORTH);
        
        scroller = new JScrollPane();
        add(scroller, BorderLayout.CENTER);
    }
    /** Clear the display */
    public void clearDisplay() {
        dataTable = new JTable();
        scroller.setViewportView(dataTable);
        scroller.updateUI();
    }

    /** Display the data */
    public void displayResults(ColumnPicker picker, Column originalColumn, Column[] pickerResults) {
        Data displayData = new Data();
        try {
            Column originalCopy = originalColumn.getCopy();
            originalCopy.setName("Original Data");
            displayData.addColumn(originalCopy);
            
            displayData.addColumns(pickerResults);
            dataTable = new JTable(new DataTableModel(displayData));
            scroller.setViewportView(dataTable);
            scroller.updateUI();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}