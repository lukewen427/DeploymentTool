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
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.MissingValue;
import org.pipeline.core.data.columns.DoubleColumn;
import org.pipeline.core.data.columns.IntegerColumn;
import org.pipeline.core.data.columns.StringColumn;

import java.io.*;
import java.util.Vector;

/**
 * This class imports a delimited text file into a data set
 * @author hugo
 */
public class DelimitedTextDataImporter {
    // Delimiter types
    
    /** Comma separated */
    public static final int COMMA_DELIMITED = 0;
    
    /** Tab separated */
    public static final int TAB_DELIMITED = 1;
    
    /** Any ammount of blank space separated */
    public static final int WHITESPACE_DELIMITED=2;
    
    /** Semi-colon */
    public static final int SEMI_COLON_DELIMITED = 3;
    /** Custom character separated */
    
    public static final int CUSTOM_DELIMITED = 4;
    
    /** Delimiter type */
    protected int delimiterType = COMMA_DELIMITED;
    
    /** Delimiter character */
    protected String delimiterString = "";
    
    /** Import column names */
    protected boolean importColumnNames = true;
    
    /** Name row */
    protected int nameRow = 1;
    
    /** Data start row */
    protected int dataStartRow = 2;
    
    /** Data end row */
    protected int dataEndRow = 0;
    
    /** Carry on importing rows until the file ends */
    protected boolean limitRows = false;
    
    /** Subsample interval */
    protected int sampleInterval = 1;
    
    /** Subsample data */
    protected boolean subsample = false;
    
    /** Replace parse errors with missing data. Enabled by default */
    protected boolean errorsAsMissing = true;
    
    /** Column type preferences */
    protected ColumnTypePreferences preferences = null;
    
    /** Use quoted values to contain strings when importing data */
    protected boolean useEnclosingQuotes = true;
    
    /** Collection of imported titles */
    protected Vector importedNames = null;
    
    /** File to import */
    protected String fileName = "";
    
    /** Data being imported */
    protected Data data = null;
    
    /** Force all imported data to be text format */
    private boolean forceTextImport = false;
    
    /** Creates a new instance of DelimitedTextDataImporter */
    public DelimitedTextDataImporter() {

    }
    
    /** Copy settings from another DelimitedTextImporter */
    public void copySettings(DelimitedTextDataImporter importer){
        setDataEndRow(importer.getDataEndRow());
        setDataStartRow(importer.getDataStartRow());
        setDelimiterString(importer.getDelimiterString());
        setDelimiterType(importer.getDelimiterType());
        setErrorsAsMissing(importer.isErrorsAsMissing());
        setFileName(importer.getFileName());
        setImportColumnNames(importer.isImportColumnNames());
        setLimitRows(importer.isLimitRows());
        setNameRow(importer.getNameRow());
        setSampleInterval(importer.getSampleInterval());
        setSubsample(importer.isSubsample());
        setUseEnclosingQuotes(importer.isUseEnclosingQuotes());
        setForceTextImport(importer.isForceTextImport());
    }
    
    /** Import data from a Stream */
    public Data importInputStream(InputStream stream) throws DataImportException, DataException {
        data = new Data();
        data.setIndexColumn(new IntegerColumn("Index"));
        IntegerColumn indexColumn = (IntegerColumn)data.getIndexColumn();
        try {
            importedNames = null;    
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(new BufferedInputStream(stream)));
            String rawLine = reader.readLine();
            String line;
            int count = 0;
            int indexCount = 0;
            int importedRowCount = 0;
            int expectedColNo = 0;

            while(rawLine!=null){
                line = rawLine.trim();
                count++;

                // Only process line if it is non-blank
                if(!line.equalsIgnoreCase("")){
                    // Work out what to do with the line
                    if(count==getNameRow() && isImportColumnNames()==true){
                        // This line contains the column names
                        importedNames = processTitleLine(line);

                    } else if(count>=getDataStartRow() && (count<=getDataEndRow() || isLimitRows()==false)){
                        // Process line of text
                        expectedColNo = processDataLine(line, importedRowCount, data, importedNames, expectedColNo);
                        indexColumn.appendIntValue(indexCount);
                        importedRowCount++;
                    }
                    
                    indexCount++;
                }

                // Get next line
                rawLine = reader.readLine();
            }
            reader.close();
        } catch (IOException e){
            throw new DataImportException("Error inmporting file :" + e.getLocalizedMessage());
        }
        return data;
    }
    
    /** Import from a File */
    public Data importFile(File file) throws DataImportException, DataException {
        FileInputStream stream = null;
        if(file.exists()){
            // Set up import
            importedNames = null;    
            fileName = file.getAbsolutePath();
            try {
                stream = new FileInputStream(file);
                return importInputStream(stream);
            } catch (FileNotFoundException e){
                throw new DataImportException("File not found");
            } finally {
                if(stream!=null){
                    try{stream.close();}catch(Exception ex){}
                }
            }
        } else {
            throw new DataImportException("File not found");
        }
    }
    
    /** Get the imported data */
    public Data getData(){
        return data;
    }
    
    /** Process the title line of a data file */
    protected Vector processTitleLine(String line) throws DataImportException {
        return splitDelimitedTextLine(line);
    }

    /**
     * Process a line of text
     * 
     * @param line
     * @param rowNumber
     * @param dataSet
     * @param names
     * @param expectedColNo
     * @return the number of columns in the data set. This value may be bigger 
     * than the expectedColNo if the row of data is longer than what has been read before. 
     * @throws DataImportException
     * @throws DataException
     */
    protected int processDataLine(String line, int rowNumber, Data dataSet, Vector names, int expectedColNo) throws DataImportException, DataException
    {
        Vector<?> importedFields = splitDelimitedTextLine(line);
        int cols = importedFields.size();
        for (int i = 0; i < cols; i++) {
            processTextValue(importedFields.elementAt(i).toString(), i, rowNumber, dataSet, names);
        }

        // If needed, add null for missing columns
        for(int i = cols; i < expectedColNo; i++) {
            processTextValue(null, i, rowNumber, dataSet, names);
        }

        return Math.max(cols, expectedColNo);
    }
    
    /** Split a line of text into a vector of delimited values */
    protected Vector splitDelimitedTextLine(String line) throws DataImportException {
        boolean withinQuotes = false;
        String temporaryDelimiter = null;
        int pos1 = 0;
        int pos2 = 0;
        String remains;
        Vector fragments = new Vector();
        
        String delimiter = getActualDelimiterString();
        if (delimiter!=null && !delimiter.equalsIgnoreCase("")) {
            while (true) {
                if (withinQuotes) {
                    delimiter = temporaryDelimiter;
                    
                    pos2 = line.indexOf(delimiter, pos1);
                    if (pos2 == -1) {
                        break;
                    }
                    
                    pos1 = pos2 + delimiter.length ();
                    withinQuotes = false;
                }
                
                if (isUseEnclosingQuotes() && line.length() > pos1 && line.charAt(pos1) == '"') {
                    pos1++;
                    withinQuotes = true;
                    temporaryDelimiter = delimiter;
                    delimiter = "\"";
                }

                // Grab the next token
                pos2 = line.indexOf(delimiter, pos1);

                if (pos2!=-1) {
                    // Add a standard fragment to the fragments vector
                    fragments.addElement(line.substring(pos1, pos2).trim());
                    pos1 = pos2 + delimiter.length();

                } else {
                    // Handle the last value differently
                    remains = line.substring(pos1, line.length()).trim();
                    if (!remains.equalsIgnoreCase("")) {
                        fragments.addElement(remains);
                        
                    } else {
                        fragments.addElement("");
                    }

                // Finished this line of data
                break;
                }
            }
        }
        return fragments;
    }
    
    /** Get actual delimiter string value */
    public String getActualDelimiterString(){
        switch(getDelimiterType()){
            case COMMA_DELIMITED:
                return ",";
                
            case TAB_DELIMITED:
                return "\t";
                
            case WHITESPACE_DELIMITED:
                return " ";
                
            case SEMI_COLON_DELIMITED:
                return ";";
                        
            case CUSTOM_DELIMITED:
            default:
                return getDelimiterString();
        }
    }
    
    /** Process a single text value */
    protected void processTextValue(String value, int colNumber, int rowNumber, Data dataSet, Vector names) throws DataImportException, DataException {
        Column column;
                
        // Column already exists
        if(colNumber<dataSet.getColumns()){
            column = dataSet.column(colNumber);
            
            try {
                column.appendStringValue(value);
                
            } catch (DataException e){
                if(isErrorsAsMissing()){
                    try {
                        column.appendObjectValue(MissingValue.get());
                    } catch (Exception ex){
                        // This won't happen
                        ex.printStackTrace();
                    }
                } else {
                    throw new DataImportException("Error importing data");
                }
            }

        // Column does not exist
        } else {
            // Get the name of the new column if the names have been imported. If not, 
            // make up a name
            String columnName;
            
            if(names!=null){
                if(colNumber>=0 && colNumber<names.size()){
                    columnName = names.elementAt(colNumber).toString();
                } else {
                    columnName = "Column " + colNumber;
                }
            } else {
                columnName = "Column " + colNumber;
            }
            
            // Need to construct a new column
            if(preferences!=null && colNumber<preferences.getPreferenceCount()){
                // Create a column from the preferences
                throw new DataImportException("Column preferences not implemented yet");
                
            } else {
                // Try and work out the column type
                if(!forceTextImport){
                    try {
                        double d = Double.parseDouble(value);
                        column = new DoubleColumn(rowNumber);
                        column.setName(columnName);
                        dataSet.addColumn(column);
                        // Backfill with missing values
                        column.nullifyToSize(rowNumber);

                        ((DoubleColumn)column).appendDoubleValue(d);

                    } catch (NumberFormatException e){
                        // String column 
                        column = new StringColumn(rowNumber);
                        column.setName(columnName);
                        dataSet.addColumn(column);
                        // Backfill
                        column.nullifyToSize(rowNumber);

                        try {
                            column.appendStringValue(value);
                        } catch (Exception se){
                            // Won't happen with string
                        }

                    } catch (Exception ge){
                        ge.printStackTrace();
                        throw new DataImportException("Error importing data");
                    }
                } else {
                    // Add everything as a String
                    column = new StringColumn(rowNumber);
                    column.setName(columnName);
                    dataSet.addColumn(column);
                    // Backfill
                    column.nullifyToSize(rowNumber);

                    try {
                        column.appendStringValue(value);
                    } catch (Exception se){
                        // Won't happen with string
                    }
                }
            }
        }
    }

    /** Return the column delimiter type */
    public int getDelimiterType() {
        return delimiterType;
    }

    /** Set the column delimiter type */
    public void setDelimiterType(int delimiterType) {
        this.delimiterType = delimiterType;
    }

    /** Get the custom delimiter string */
    public String getDelimiterString() {
        return delimiterString;
    }

    
    /** Set the custom delimiter string */
    public void setDelimiterString(String delimiterString) {
        this.delimiterString = delimiterString;
    }

    public boolean isImportColumnNames() {
        return importColumnNames;
    }

    public void setImportColumnNames(boolean importColumnNames) {
        this.importColumnNames = importColumnNames;
    }

    public int getNameRow() {
        return nameRow;
    }

    public void setNameRow(int nameRow) {
        this.nameRow = nameRow;
    }

    public int getDataStartRow() {
        return dataStartRow;
    }

    public void setDataStartRow(int dataStartRow) {
        this.dataStartRow = dataStartRow;
    }

    public int getDataEndRow() {
        return dataEndRow;
    }

    public void setDataEndRow(int dataEndRow) {
        this.dataEndRow = dataEndRow;
    }

    public boolean isLimitRows() {
        return limitRows;
    }

    public void setLimitRows(boolean limitRows) {
        this.limitRows = limitRows;
    }

    public int getSampleInterval() {
        return sampleInterval;
    }

    public void setSampleInterval(int sampleInterval) {
        this.sampleInterval = sampleInterval;
    }

    public boolean isErrorsAsMissing() {
        return errorsAsMissing;
    }

    public void setErrorsAsMissing(boolean errorsAsMissing) {
        this.errorsAsMissing = errorsAsMissing;
    }

    public boolean isUseEnclosingQuotes() {
        return useEnclosingQuotes;
    }

    public void setUseEnclosingQuotes(boolean useEnclosingQuotes) {
        this.useEnclosingQuotes = useEnclosingQuotes;
    }

    public boolean isSubsample() {
        return subsample;
    }

    public void setSubsample(boolean subsample) {
        this.subsample = subsample;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isForceTextImport() {
        return forceTextImport;
    }

    public void setForceTextImport(boolean forceTextImport) {
        this.forceTextImport = forceTextImport;
    }
}
