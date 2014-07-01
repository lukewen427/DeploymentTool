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

package org.pipeline.core.data.io;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.MissingValue;

import java.io.*;

/**
 * This class exports a Data object in CSV format
 * @author hugo
 */
public class CSVDataExporter {
    /** Data to export */
    private Data data;
    
    /** Current file writer */
    private PrintWriter currentWriter = null;
    
    /** Is a file currently open */
    private boolean fileOpen = false;
    
    /** Has the header been written to the open file yet */
    private boolean headerWritten = false;
    
    /** Write out column names */
    private boolean includeNames = true;

    /** Delimiting character */
    private String delimiter = ",";

    /** Should a row index be included */
    private boolean rowIndexIncluded = false;

    /** Should all output be quoted */
    private boolean quoteAllOutput = false;
   
    public boolean isRowIndexIncluded() {
        return rowIndexIncluded;
    }

    public void setRowIndexIncluded(boolean rowIndexIncluded) {
        this.rowIndexIncluded = rowIndexIncluded;
    }

    public int getRowIndexValue() {
        return rowIndexValue;
    }

    public void setRowIndexValue(int rowIndexValue) {
        this.rowIndexValue = rowIndexValue;
    }

    /** Start value of the row index */
    private int rowIndexValue = 0;

    /** Creates a new CSVDataExporter without a file or data set */
    public CSVDataExporter(){

    }
    
    /** Creates a new instance of CSVDataExporter */
    public CSVDataExporter(Data data) {
        this.data = data;
    }

    /** Returns the delimiting text */
    public String getDelimiter(){
        return delimiter;
    }

    /** Sets the delimiting text */
    public void setDelimiter(String delimiter){
        this.delimiter = delimiter;
    }

    public boolean isQuoteAllOutput() {
        return quoteAllOutput;
    }

    public void setQuoteAllOutput(boolean quoteAllOutput) {
        this.quoteAllOutput = quoteAllOutput;
    }
    
    /** Open a stream */
    public void openStream(OutputStream stream) throws DataExportException {
        if(!fileOpen){
            try {
                currentWriter = new PrintWriter(new OutputStreamWriter(stream));
                fileOpen = true;
                headerWritten = false;
                
            } catch (Exception e){
                currentWriter = null;
                fileOpen = false;
                headerWritten = false;
            }
            
        } else {
            throw new DataExportException("Error exporting data: File already open");
        }        
    }
    
    /** Open a file */
    public void openFile(File file) throws DataExportException {
        if(!fileOpen){
            try {
                if(!file.exists()){
                    file.createNewFile();
                } else {
                    file.delete();
                    file.createNewFile();
                }
                
                currentWriter = new PrintWriter(file);
                fileOpen = true;
                headerWritten = false;
                
            } catch (Exception e){
                currentWriter = null;
                fileOpen = false;
                headerWritten = false;
            }
            
        } else {
            throw new DataExportException("Error exporting data: File already open");
        }
               
    }
    
    /** Close the current file */
    public void closeFile(){
        if(fileOpen){
            try {
                if(currentWriter!=null){
                    currentWriter.flush();
                    currentWriter.close();
                    currentWriter = null;
               }
            } catch (Exception e){
                e.printStackTrace();
            }
            fileOpen = false;
            headerWritten = false;
        }
    }
    
    /** Write a Data set to the current file */
    public void appendData(Data data) throws DataExportException {
        if(fileOpen && currentWriter!=null){
            StringBuilder rowBuffer;
            Column col;
            int columns = data.getColumns();
            int rows = data.getLargestRows();
            try {
                // Write header rows if needed
                if(!headerWritten){
                    // Write out names row
                    if(includeNames){
                        rowBuffer = new StringBuilder();
                        if(rowIndexIncluded){
                            rowBuffer.append("Index,");
                        }
                        for(int i=0;i<columns;i++){
                            rowBuffer.append(escapeCSVValue(data.column(i).getName(), false));
                            rowBuffer.append(delimiter);
                        }
                        // Remove the last delimiter
                        if (rowBuffer.length() > 0) {
                            rowBuffer.setLength(rowBuffer.length() - 1);
                        }
                        currentWriter.println(rowBuffer.toString());
                    }
                    headerWritten = true;
                }

                // Write out data
                for(int i=0;i<rows;i++){
                    rowBuffer = new StringBuilder();

                    // Include a row index if needed
                    if(rowIndexIncluded){
                        rowBuffer.append(rowIndexValue);
                        rowBuffer.append(delimiter);
                        rowIndexValue++;
                    }

                    for(int j=0;j<columns;j++){
                        col = data.column(j);
                        if (i < col.getRows() && !col.isMissing(i)) {
                            rowBuffer.append(escapeCSVValue(col.getStringValue(i), quoteAllOutput));
                        } else {
                            rowBuffer.append(MissingValue.MISSING_VALUE_TEXT);
                        }
                        rowBuffer.append(delimiter);
                    }
                    // Remove the last delimiter
                    if (rowBuffer.length() > 0) {
                        rowBuffer.setLength(rowBuffer.length() - 1);
                    }
                    currentWriter.println(rowBuffer.toString());
                }
            } catch (Exception ex){
                throw new DataExportException("Error exporting data: " + ex.getMessage());
            }
        } else {
            throw new DataExportException("Error exporting data: File not open");
        }
    }
    
    /** Write to a file */
    public void writeFile(File file) throws DataExportException {
        // Create the file if it doesn't exist
        try {
            if(!file.exists()){
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
        } catch (IOException ex){
            throw new DataExportException("Error exporting data: " + ex.getMessage());
        }
        
        PrintWriter writer = null;
        
        try {
            writer = new PrintWriter(file);
            writeToPrintWriter(writer);
            
        } catch (Exception ex){
            throw new DataExportException("Error exporting data: " + ex.getMessage());
        } finally {
            try {writer.flush();}catch(Exception e){}
            try {writer.close();}catch(Exception e){}
        }
    }

    /** Write the data to a PrintWriter */
    public void writeToPrintWriter(PrintWriter writer) throws Exception {
        StringBuilder rowBuffer;
        Column col;
        int columns = data.getColumns();
        int rows = data.getLargestRows();

        // Write out names row
        if(includeNames){
            rowBuffer = new StringBuilder();
            for(int i=0;i<columns;i++){
                rowBuffer.append(escapeCSVValue(data.column(i).getName(), quoteAllOutput));
                rowBuffer.append(delimiter);
            }
            // Remove the last delimiter
            if (rowBuffer.length() > 0) {
                rowBuffer.setLength(rowBuffer.length() - 1);
            }
            writer.println(rowBuffer.toString());
        }

        // Write out data
        for(int i=0;i<rows;i++){
            rowBuffer = new StringBuilder();

            // Include a row index if needed
            if(rowIndexIncluded){
                rowBuffer.append(rowIndexValue);
                rowBuffer.append(delimiter);
                rowIndexValue++;
            }

            for(int j=0;j<columns;j++){
                col = data.column(j);
                if (i < col.getRows() && !col.isMissing(i)) {
                    // Within bounds
                    rowBuffer.append(escapeCSVValue(col.getStringValue(i), quoteAllOutput));
                } else {
                    // Beyond column end
                    rowBuffer.append(MissingValue.MISSING_VALUE_TEXT);
                }
                rowBuffer.append(delimiter);
            }
            // Remove the last delimiter
            if (rowBuffer.length() > 0) {
                rowBuffer.setLength(rowBuffer.length() - 1);
            }
            writer.println(rowBuffer.toString());
        }
        writer.flush();
    }

    /** Are names included in the export */
    public boolean isIncludeNames() {
        return includeNames;
    }

    /** Set whether to include a names row in the export */
    public void setIncludeNames(boolean includeNames) {
        this.includeNames = includeNames;
    }


    /**
     * This operation performs escaping of CSV values according to RFC4180.
     */
    private static String escapeCSVValue(String value, boolean force)
    {
        if (force || value.indexOf('"') >= 0) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }

        if (value.indexOf(",") >= 0 || value.indexOf("\n") >= 0) {
            return '"' + value + '"';
        }

        return value;
    }
}
