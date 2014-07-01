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

package org.pipeline.core.data.cxd;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.MissingValue;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
 * This class provides a <Row> tag handler for a SAX xml reader that loads 
 * data for a column from a CxD zip archive.
 * @author hugo
 */
public class SaxColumnRowHandler extends DefaultHandler {
    /** Column being loaded */
    private Column column;

    /** Enforce loading range */
    private boolean enforceRange = false;
    
    /** Start index for loading */
    private int startIndex = 0;
    
    /** End index for loading */
    private int endIndex = Integer.MAX_VALUE;
    
    /** Load monitor for load notifications */
    ColumnReadMonitor monitor;
    
    /** Construct with a column */
    public SaxColumnRowHandler(Column column, ColumnReadMonitor monitor) {
        this.column = column;
        this.monitor = monitor;
    }

    /** Construct with a column and range enforcing */
    public SaxColumnRowHandler(Column column, int startIndex, int endIndex) {
        this.column = column;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.enforceRange = true;
    }

    /** Beginning of a Row element. Append to the Column set */
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            if(qName.equals("Row")){
                boolean missing;
                String missingStr;

                // Check missing status
                missingStr = attributes.getValue("missing");
                if(missingStr==null || missingStr.equalsIgnoreCase("true")){
                    missing = true;
                } else {
                    missing = false;
                }

                // Row number
                int row = Integer.parseInt(attributes.getValue("index"));
                
                // Check for range if enforceRange is set
                if(enforceRange==false || (row>startIndex && row<endIndex && enforceRange==true)){
                    // Data
                    String value = attributes.getValue("value");
                    if(value!=null && missing==false){
                        column.appendCxDFormatValue(value);
                    } else {
                        column.appendObjectValue(MissingValue.get());
                    }
                }
            }
        } catch (Exception e){
            throw new SAXException("Error reading data: " + e.getMessage());
        }
    }
    
    /** Get the data column */
    public Column getColumn(){
        return column;
    }
}