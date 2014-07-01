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
import org.pipeline.core.data.columns.DoubleColumn;
import org.pipeline.core.data.columns.IntegerColumn;
import org.pipeline.core.data.columns.StringColumn;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * This class provides a table of data that is coloured depending on data
 * type.
 * @author nhgh
 */
public class ColoredDataTable extends JPanel implements TableModel {
    /** Table model listeners */
    private ArrayList<TableModelListener> listeners = new ArrayList<>();
    
    /** Data set */
    private Data data;
    
    /** Table of data */
    private JTable dataTable;
    
    /** Data table scroller */
    private JScrollPane dataScroller;

    /** Is the data in this table editable */
    private boolean editable = false;

    /** Listeners for this table */
    private ArrayList<DataTableEditListener> editListeners = new ArrayList<>();

    /** Creates a new instance of ColoredDataTable */
    public ColoredDataTable(Data data) {
        this.data = data;
        initComponents();
        setData(data);
    }

    public void terminateEdit(){
        dataTable.removeEditor();
    }
    
    public ColoredDataTable(){
        data = null;
        initComponents();
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }


    /** Initialise the window components */
    private void initComponents(){
        setLayout(new BorderLayout());
        dataScroller = new JScrollPane();
        dataTable = new JTable();

        dataTable.setDefaultRenderer(Double.class, new DoubleTableRenderer());
        dataTable.setDefaultEditor(Double.class, new DoubleTableEditor(this));

        dataTable.setDefaultRenderer(String.class, new TextTableRenderer());
        dataTable.setDefaultEditor(String.class, new TextTableEditor(this));

        dataTable.setModel(this);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        dataTable.getTableHeader().setReorderingAllowed(false);

        dataTable.setColumnSelectionAllowed(true);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        dataScroller.setViewportView(dataTable);
        dataScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(dataScroller, BorderLayout.CENTER);
    }

    /** Add a data editor listener */
    public void addDataTableEditListener(DataTableEditListener l){
        this.editListeners.add(l);
    }

    /** Remove an edit listener */
    public void removeDataTableEditListener(DataTableEditListener l){
        this.editListeners.remove(l);
    }
    
    /** Set the data for this table */
    public void setData(Data data){
        dataTable.setModel(new DefaultTableModel());
        this.data = data;
        dataTable.setModel(this);
        notifyDataChange();
        for(int i=0;i<data.getColumns();i++){
            dataTable.getColumnModel().getColumn(i).setMaxWidth(200);
            dataTable.getColumnModel().getColumn(i).setMinWidth(75);
            dataTable.getColumnModel().getColumn(i).setWidth(75);
        }
    }

    /** Get the current row */
    public int getCurrentRow(){
        return dataTable.getSelectedRow();
    }

    /** Get the current column */
    public int getCurrentColumn(){
        return dataTable.getSelectedColumn();
    }

    /** Notify edit listeners that the data has been edited */
    public void notifyDataEdit(){
        for(int i=0;i<editListeners.size();i++){
            editListeners.get(i).dataChanged(data);
        }
    }

    /** Notify a structure change */
    public void notifyColumnsChanged(){
        Iterator<TableModelListener> e = listeners.iterator();
        TableModelEvent evt = new TableModelEvent(this, TableModelEvent.HEADER_ROW);
        while(e.hasNext()){
            e.next().tableChanged(evt);
        }
    }

    /** Notify listeners of data change */
    public void notifyDataChange(){
        Iterator<TableModelListener> e = listeners.iterator();
        TableModelEvent evt = new TableModelEvent(this);
        while(e.hasNext()){
            e.next().tableChanged(evt);
        }
    }
    
    /** Remove a listener */
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    /** No cells are editable */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return editable;
    }

    /** Get the value of a cell */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(data!=null){
            try {
                Object value;
                
                if(columnIndex<data.getColumns()){
                    Column c = data.column(columnIndex);
                    if(rowIndex<c.getRows()){
                        if(!c.isMissing(rowIndex)){
                            value = c.getObjectValue(rowIndex);
                            return value;
                        } else {
                            return null;
                        }
                        
                    } else {
                        return null;
                    }

                } else {
                    return "N/A";
                }
            } catch (Exception e){
                return null;
            }
            
        } else {
            return null;
        }        
    }

    /** Get the number of table rows */
    public int getRowCount() {
        if(data!=null){
            return data.getLargestRows();
        } else {
            return 0;
        }        
    }

    /** Get the number of table columns */
    public int getColumnCount() {
        if(data!=null){
            return data.getColumns();
        } else {
            return 0;
        }        
    }

    /** Add a listener */
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    /** No values can be set */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(editable && data!=null){

            try {
                if(columnIndex<data.getColumns()){
                    Column c = data.column(columnIndex);
                    if(rowIndex<c.getRows()){
                        if(c instanceof DoubleColumn){
                            ((DoubleColumn)c).setDoubleValue(rowIndex, Double.parseDouble(aValue.toString()));
                        } else if(c instanceof IntegerColumn){
                            ((IntegerColumn)c).setLongValue(rowIndex, Long.parseLong(aValue.toString()));
                        } else if(c instanceof StringColumn){
                            ((StringColumn)c).setStringValue(rowIndex, aValue.toString());
                        } else {
                            c.setObjectValue(rowIndex, aValue);
                        }
                        notifyDataChange();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /** Get the name of a column */
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

    /** Get the class of a column */
    public Class<?> getColumnClass(int columnIndex) {
        if(data!=null){
            return data.column(columnIndex).getMetaData().getDataType();
        } else {
            return null;
        }        
    }
    
    // =====================================================================
    // Standard renderer that displays all data types
    // =====================================================================

    private class DoubleTableEditor extends DefaultCellEditor {
        JTextField field;
        ColoredDataTable parentTable;
        public DoubleTableEditor(final ColoredDataTable parentTable) {
            super(new JTextField());
            setClickCountToStart(1);
            field = (JTextField)this.getComponent();
            field.setBorder(null);
            this.parentTable = parentTable;

            addCellEditorListener(new CellEditorListener() {


                @Override
                public void editingStopped(ChangeEvent e) {
                    parentTable.notifyDataEdit();
                }

                @Override
                public void editingCanceled(ChangeEvent e) {

                }
            });
        }


    }

    private class DoubleTableRenderer extends DefaultTableCellRenderer {
        private Color oddRowColor = Color.WHITE;
        private Color evenRowColor = new Color(240, 240, 240);
        private Color foregroundColor = new Color(0,0,0);
        private NumberFormat format = NumberFormat.getNumberInstance();
        public DoubleTableRenderer() {
            setForeground(foregroundColor);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if(!isSelected){
                if((row % 2)==0){
                    setBackground(evenRowColor);
                } else {
                    setBackground(oddRowColor);
                }
                setForeground(foregroundColor);
            } else {
                setBackground(SystemColor.textHighlight);
                setForeground(SystemColor.textHighlightText);
            }
            if(value!=null){
                setText(format.format(((Double)value).doubleValue()));
            } else {
                setText(MissingValue.MISSING_VALUE_TEXT);
            }
            return this;
        }

    }

    private class TextTableEditor extends DefaultCellEditor {
        JTextField field;
        ColoredDataTable parentTable;
        public TextTableEditor(final ColoredDataTable parentTable) {
            super(new JTextField());
            setClickCountToStart(1);
            field = (JTextField)this.getComponent();
            field.setBorder(null);
            this.parentTable = parentTable;
            addCellEditorListener(new CellEditorListener() {


                @Override
                public void editingStopped(ChangeEvent e) {
                    parentTable.notifyDataEdit();
                }

                @Override
                public void editingCanceled(ChangeEvent e) {

                }
            });
        }
    }

    private class TextTableRenderer extends DefaultTableCellRenderer {
        private Color oddRowColor = Color.WHITE;
        private Color evenRowColor = new Color(240, 240, 240);
        private Color foregroundColor = new Color(0,64,0);
        public TextTableRenderer() {
            setOpaque(true);
            setForeground(foregroundColor);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if(!isSelected){
                if((row % 2)==0){
                    setBackground(evenRowColor);
                } else {
                    setBackground(oddRowColor);
                }
                setForeground(foregroundColor);
            } else {
                setBackground(SystemColor.textHighlight);
                setForeground(SystemColor.textHighlightText);
            }
            if(value!=null){
                setText((String)value);
            } else {
                setText(MissingValue.MISSING_VALUE_TEXT);
            }
            return this;
        }

    }
}
