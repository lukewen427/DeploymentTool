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
package com.connexience.server.workflow.engine.data;

import org.pipeline.core.data.*;
import org.pipeline.core.xmlstorage.*;

import java.util.*;
import java.io.*;

/**
 * This class provides a tool for writing intermediate data wrapper data to
 * a file. It allows a single large write or chunk based writing which is used
 * to support streaming large files through blocks.
 * @author nhgh
 */
public class CSVTransferDataWriter {
    /** Output writer */
    private PrintWriter writer = null;

    /** Output stream used for writing */
    private OutputStream stream = null;
    
    /** Has the file been opened for writing */
    private boolean inUse = false;

    /** Total number of lines written */
    private long totalRowsWritten = 0;

    /** Has the header been written to the file */
    private boolean headerWritten = false;

    /** Expected number of columns */
    private int expectedColumns = 0;

    /** Create with a file */
    public CSVTransferDataWriter(File dataFile) throws DataException {
        try {
            stream = new FileOutputStream(dataFile); 
            writer = new PrintWriter(stream);
            initialise();
        } catch (Exception e){
            throw new DataException("Error opening CSV writer: " + e.getMessage());
        }
    }

    /** Create with an output stream */
    public CSVTransferDataWriter(OutputStream stream) throws DataException {
        try {
            this.stream = stream;
            writer = new PrintWriter(stream);
            initialise();
        } catch (Exception e){
            throw new DataException("Error opening CSV writer: " + e.getMessage());
        }
    }

    /** Initialise the file for writing */
    public void initialise() {
        inUse = true;
        totalRowsWritten = 0;
        headerWritten = false;
    }

    /** Get the total number of rows written */
    public long getTotalRowsWritten(){
        return totalRowsWritten;
    }
    
    /** Write the header to the file */
    private synchronized void writeHeader(Data data) throws DataException {
        if(!headerWritten){
            Column col;
            ColumnTypeInfo info;
            XmlDataObject property;
            
            // Write any properties
            if(data.hasProperties()){
                Enumeration properties = data.getProperties().elements();
                while(properties.hasMoreElements()){
                    property = (XmlDataObject)properties.nextElement();
                    writer.println("@" + property.getName() + ":" + property.getTypeLabel() + ":" + property.getValue().toString());
                }
            }
            
            // Does the data set have and index
            if(data.hasIndexColumn()){
                writer.println("$" + ColumnFactory.getColumnTypeInfo(data.getIndexColumn()).getId() + ":Index");
            } else {
                writer.println("$NOINDEX");
            }
            
            // Write the column descriptors
            for(int i=0;i<data.getColumns();i++){
                col = data.column(i);
                info = ColumnFactory.getColumnTypeInfo(col);
                writer.println("#" + info.getId() + ":" + col.getName());
            }
            
            // Write the column properties
            for(int i=0;i<data.getColumns();i++){
                col = data.column(i);
                if(col.hasProperties()){
                    Enumeration properties = col.getProperties().elements();
                    while(properties.hasMoreElements()){
                        property = (XmlDataObject)properties.nextElement();
                        writer.println("?" + i + ":" + property.getName() + ":" + property.getTypeLabel() + ":" + property.getValue().toString());
                    }
                }
            }
            
            writer.println("#HEADEREND");
            writer.flush();
            if(stream instanceof FileOutputStream){
                try {
                    stream.flush();
                    ((FileOutputStream)stream).getFD().sync();
                } catch (Exception e){
                    throw new DataException("Error syncing output data file");
                }
            }
            headerWritten = true;
            expectedColumns = data.getColumns();
        }
    }
    
    /** Close the writer and finish writing */
    public synchronized void close(){
        try {
            writer.flush();
            if(stream instanceof FileOutputStream){
                try {
                    stream.flush();
                    ((FileOutputStream)stream).getFD().sync();
                } catch (Exception e){
                    throw new DataException("Error syncing output data file");
                }
            }
            writer.close();
            stream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        inUse = false;
    }

    /** Write some data to the file */
    public synchronized void write(Data data) throws DataException {
        if(inUse){
            writeHeader(data);  // Write the header if it hasn't already been written

            int rows = data.getLargestRows();
            int cols = data.getColumns();
            StringBuffer row;
            Column col;

            if(cols==expectedColumns){
                for(int i=0;i<rows;i++){
                    row = new StringBuffer();

                    // Append index
                    row.append(totalRowsWritten);

                    // Write index
                    if(data.hasIndexColumn()){
                        row.append(",");
                        row.append(data.getIndexColumn().getStringValue(i));
                    }
                    
                    for(int j=0;j<cols;j++){
                        col = data.column(j);
                        row.append(",");

                        if(i<col.getRows()){
                            if(!col.isMissing(i)){
                                row.append("\"" + col.getCxDFormatValue(i) + "\"");
                            } else {
                                row.append(MissingValue.MISSING_VALUE_REPRESENTATION);
                            }
                        } else {
                            row.append(MissingValue.MISSING_VALUE_REPRESENTATION);
                        }
                    }

                    writer.println(row.toString());
                    totalRowsWritten++;
                }
                writer.flush();
                
                if(stream instanceof FileOutputStream){
                    try {
                        stream.flush();
                        ((FileOutputStream)stream).getFD().sync();
                    } catch (Exception e){
                        throw new DataException("Error syncing output data file");
                    }
                }
            } else {
                throw new DataException("Wrong number of columns in appended data set");
            }
        } else {
            throw new DataException("CSV Writer has not been initialised");
        }
    }
}