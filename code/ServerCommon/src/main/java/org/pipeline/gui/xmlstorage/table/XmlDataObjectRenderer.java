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
package org.pipeline.gui.xmlstorage.table;

import org.pipeline.core.xmlstorage.XmlDataObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.xmldatatypes.*;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.DateFormat;

/**
 * This object renders XmlDataObjects in a table
 * @author  hugo
 */
public class XmlDataObjectRenderer extends JLabel implements TableCellRenderer {
    // Constants for display. TODO integrate these with system properties
    /** Color for text display */
    public static final Color TEXT_COLOR = Color.GREEN;
    
    /** Color for integer / long display */
    public static final Color INTEGER_COLOR = Color.BLUE;
    
    /** Color for double display */
    public static final Color DOUBLE_COLOR = Color.RED;
    
    /** Color for boolean display */
    public static final Color BOOLEAN_COLOR = Color.DARK_GRAY;
    
    /** Normal font */
    public static final Font NORMAL_FONT = new Font("SanSerif", Font.PLAIN, 12);
    
    /** General color */
    public static final Color OTHER_COLOR = Color.BLACK;
    
    /** Date formatter */
    public static final DateFormat DATE_FORMATTER = DateFormat.getDateTimeInstance();
    
    /** Creates a new instance of XmlDataObjectRenderer */
    public XmlDataObjectRenderer() {
        super();
        setOpaque(true);
    }
    
    /** Return a renderer component for a specific cell */
    public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        // Set up according to what is being edited
        if(value instanceof XmlBooleanDataObject){
            // Set up for boolean true / false display
            setBackground(table.getBackground());
            setFont(NORMAL_FONT);
            setForeground(BOOLEAN_COLOR);
            if(((XmlBooleanDataObject)value).booleanValue()==true){
                setText("Yes");
            } else {
                setText("No");
            }
            
        } else if(value instanceof XmlColorDataObject){
            // Set for Color display
            setBackground(((XmlColorDataObject)value).colorValue());
            setText("");
            
        } else if(value instanceof XmlDateDataObject){
            // Set up for date display
            setBackground(table.getBackground());
            setFont(NORMAL_FONT);
            setForeground(OTHER_COLOR);
            setText(DATE_FORMATTER.format(((XmlDateDataObject)value).dateValue()));
            
        } else if(value instanceof XmlDoubleDataObject){
            // Set up for double display
            setBackground(table.getBackground());
            setForeground(DOUBLE_COLOR);
            setFont(NORMAL_FONT);
            setText(Double.toString(((XmlDoubleDataObject)value).doubleValue()));            
        
        } else if(value instanceof XmlFontDataObject){setBackground(table.getBackground());
            // Set up for font display
            setBackground(table.getBackground());
            setForeground(OTHER_COLOR);
            String strName = ((XmlFontDataObject)value).fontValue().getName();
            int iStyle = ((XmlFontDataObject)value).fontValue().getStyle();
            setFont(new Font(strName, iStyle, 12));
            setText(strName);
            
        } else if(value instanceof XmlIntegerDataObject){
            // Set up for integer disply
            setBackground(table.getBackground());
            setForeground(INTEGER_COLOR);
            setFont(NORMAL_FONT);
            setText(Integer.toString(((XmlIntegerDataObject)value).intValue()));
            
        } else if(value instanceof XmlLongDataObject){
            // Set up for long disply
            setBackground(table.getBackground());
            setForeground(INTEGER_COLOR);
            setFont(NORMAL_FONT);
            setText(Long.toString(((XmlLongDataObject)value).longValue()));
            
        } else if(value instanceof XmlStorableDataObject){
            // Nothing gets displayed for this 
            setBackground(table.getBackground());
            setForeground(OTHER_COLOR);
            setFont(NORMAL_FONT);
            setText(value.toString());
            
        } else if(value instanceof XmlStringDataObject){
            // Set up for String display
            setBackground(table.getBackground());
            setForeground(OTHER_COLOR);
            setFont(NORMAL_FONT);
            setText(((XmlStringDataObject)value).stringValue());
            
        } else if(value instanceof XmlDataStore){
            // Nothing gets displayed for this 
            setBackground(table.getBackground());
            setForeground(OTHER_COLOR);
            setFont(NORMAL_FONT);
            setText("");
            
        } else {
            // Customise the renderer using the actual object
            if(value!=null){
                ((XmlDataObject)value).cusomizeRenderer(this);
            }
        }
        
        // Return the customised renderer
        return this;
    }
}
