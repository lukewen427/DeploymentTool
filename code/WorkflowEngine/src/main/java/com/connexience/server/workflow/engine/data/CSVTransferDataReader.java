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
import com.connexience.server.workflow.util.*;
import org.pipeline.core.xmlstorage.*;

import java.util.*;
import java.io.*;

/**
 * This class loads transfer data in CSV format. It stores column names and
 * types at the top of the file then basic delimited rows of data.
 *
 * Data is in the form:
 *
 * #DataType for column 1: date-column, integer-column, string-column, double-column :Name for column1
 *
 * i.e.
 * #double-column:Flow Rate(g/sec)
 * #date-column:Dose time
 * #string-column:Comments
 * #integer-column:Sample number
 *
 * Then the rest of the data is standard csv delimited text
 *
 * @author nhgh
 */
public class CSVTransferDataReader {
    /** List of empty columns to use as column definitions */
    private Vector<Column> columnTemplates = new Vector<>();

    /** Template for the index column */
    private Column indexTemplate = null;
    
    /** Stream used for file reading */
    private BufferedReader reader = null;

    /** Input stream used for file reading */
    private InstrumentedInputStream inStream = null;

    /** Should data be read in chunks. If this is set each time the read method is
     * called the reader only reads in the specified number of chunks and behaves as
     * if the file has ended. A subsequent read will read the next set of data and
     * also appear as if the file has ended. This allows data to be streamed
     * through blocks. */
    private boolean inChunkMode = false;

    /** Chunk size in rows to read at each attempt */
    private int chunkSize = 10000;

    /** Is the reader in use */
    private boolean inUse = false;

    /** Number of data columns */
    private int columns = 0;

    /** Has the end of the file been reached */
    private boolean atEndOfFile = false;

    /** Total number of rows loaded */
    private long totalRowsRead = 0;

    /** Does the data contain an index column */
    private boolean containsIndex = true;

    /** Does the data contain an internal index column */
    private boolean hasInternalIndex = false;

    /** Additional data properties */
    private XmlDataStore dataProperties = null;
    
    /** Create a reader with a data file */
    public CSVTransferDataReader(File dataFile) throws DataException {
        try {
            inStream = new InstrumentedInputStream(new FileInputStream(dataFile), null);
            reader = new BufferedReader(new InputStreamReader(inStream), 2048);
            initialize();
        } catch (DataException de){
            throw de;
        } catch (Exception e){
            throw new DataException("Error opening CSV reader: " + e.getMessage());
        }
    }

    /** Createa a reader with an input stream */
    public CSVTransferDataReader(InputStream stream) throws DataException {
        inStream = new InstrumentedInputStream(stream, null);
        reader = new BufferedReader(new InputStreamReader(inStream), 2048);
        initialize();
    }

    /** Get the total number of bytes read by the input stream */
    public long getTotalBytesRead(){
        if(inStream!=null){
            return inStream.getBytesTransferred();
        } else {
            return 0;
        }
    }
    
    /** Set whether this reader is in chunk mode or not */
    public void setInChunkMode(boolean inChunkMode){
        this.inChunkMode = inChunkMode;
    }

    /** Get whether this reader is in chunk mode or not */
    public boolean isInChunkMode(){
        return inChunkMode;
    }

    /** Has the end of the file been reached */
    public boolean isAtEndOfFile(){
        return atEndOfFile;
    }

    /** Set the chunk size for this reader */
    public void setChunkSize(int chunkSize){
        this.chunkSize = chunkSize;
    }

    /** Get the chunk size for this reader */
    public int getChunkSize(){
        return chunkSize;
    }

    /** Get the total number of rows read */
    public long getTotalRowsRead(){
        return totalRowsRead;
    }
    
    /** Initialise the reader, open the file and load the data types */
    public final void initialize() throws DataException {
        // Read the header
        columnTemplates.clear();
        indexTemplate = null;
        
        int headerRowCount = 0;
        XmlDataStore properties = new XmlDataStore();
        try {
            String line;
            boolean headerFinished = false;
            HashMap<String, XmlDataStore> propertyMap = new HashMap<>();
            
            while(!headerFinished && (line=reader.readLine())!=null){
                if(!(line.trim().equals(""))){
                    if(line.startsWith("#")){
                        if(!line.equals("#HEADEREND")){
                            // This is a header line
                            headerRowCount++;
                            columnTemplates.add(parseDescriptionLine(line));
                        } else {
                            headerFinished = true;
                        }
                    } else if(line.startsWith("$") && headerFinished==false){
                        // This is the index data
                        if(line.equals("$NOINDEX")){
                            indexTemplate = null;
                            hasInternalIndex = false;
                        } else {
                            indexTemplate = parseDescriptionLine(line);
                            hasInternalIndex = true;
                        }
                        
                    } else if(line.startsWith("@") && headerFinished==false){
                        // Line with a property in it
                        properties.add(parsePropertyLine(line));
                         
                    } else if(line.startsWith("?") && headerFinished==false){
                        // Column property line
                        addColumnPropertyLineToColumnProperties(line, propertyMap);
                    }
                }
            }

            // The header should have been finished by now
            if(!headerFinished){
                throw new DataException("Header reading did not complete");
            }

            // Do the header rows match the numner of template columns
            if(headerRowCount!=columnTemplates.size()){
                throw new DataException("Error reading header - number of header rows do not match the number of columns");
            }
            columns = headerRowCount;

            // Keep properties if any exist
            if(properties.size()>0){
                dataProperties = properties;
            } else {
                dataProperties = null;
            }
            
            // Add the properties to the column templates
            for(int i=0;i<columnTemplates.size();i++){
                if(propertyMap.containsKey(Integer.toString(i))){
                    columnTemplates.get(i).setProperties(propertyMap.get(Integer.toString(i)));
                }
            }
            
            // Reader is now ready to go
            inUse = true;
            atEndOfFile = false;
            totalRowsRead = 0;

        } catch(FileNotFoundException e){
            throw new DataException("Cannot find data file");
        } catch(IOException ioe){
            throw new DataException("IO error reading data file");
        }
    }

    /** Close the reader and finish reading */
    public void close(){
        if(inUse){
            try {
               reader.close();
            } catch (Exception e){
                e.printStackTrace();
            }

            try {
                inStream.close();

            } catch (Exception e){
                e.printStackTrace();
            }
            inUse = false;
        }
    }
    
    /** Parse a column property line */
    private void addColumnPropertyLineToColumnProperties(String line, HashMap<String, XmlDataStore>propertyMap) throws DataException {
        try {
            int pos1 = line.indexOf(":");
            int pos2 = line.indexOf(":", pos1 + 1);       
            int pos3 = line.indexOf(":", pos2 + 1);
            XmlDataStore store;
            
            if(pos1!=-1 && pos2!=-1 && pos3!=-1){
                String colIndex = line.substring(1, pos1);
                String propertyName = line.substring(pos1 + 1, pos2);
                String propertyType = line.substring(pos2 + 1, pos3);
                String value = line.substring(pos3 + 1, line.length());

                XmlDataObject property = XmlDataObjectFactory.createDataObject(propertyType, value, propertyName);
                if(!propertyMap.containsKey(colIndex)){
                    store = new XmlDataStore(colIndex);
                    propertyMap.put(colIndex, store);
                } else {
                    store = propertyMap.get(colIndex);
                }
                store.add(property);
                
            } else {
                throw new Exception("Incorrectly formatted property line");
            }        
            
        } catch (Exception e){
            throw new DataException("Error loading column property: " + e.getMessage());
        }
    }
    
    /** Parse a property line */
    private XmlDataObject parsePropertyLine(String line) throws DataException {
        try {
            int pos1 = line.indexOf(":");
            int pos2 = line.indexOf(":", pos1 + 1);

            if(pos1!=-1 && pos2!=-1){
                String name = line.substring(1, pos1);
                String type = line.substring(pos1 + 1, pos2);
                String value = line.substring(pos2 + 1, line.length());
                
                XmlDataObject property = XmlDataObjectFactory.createDataObject(type, value, name);
                return property;
            } else {
                throw new Exception("Incorrectly formatted property line");
            }
        } catch (Exception e){
            throw new DataException("Error loading data property value: " + e.getMessage(), e);
        }
    }

    /** Parse a description line */
    private Column parseDescriptionLine(String line) throws DataException {
        Column template = null;
        if(line.startsWith("#")){
            // Find the ":" character
            int pos = line.indexOf(":");
            String type = line.substring(1, pos);
            String name = line.substring(pos + 1, line.length());
            template = ColumnFactory.createColumn(type);
            template.setName(name);
            return template;
        } else if (line.startsWith("$")){
            // Find the ":" character
            int pos = line.indexOf(":");
            String type = line.substring(1, pos);
            template = ColumnFactory.createColumn(type);
            template.setName("Index");
            return template;
        } else {
            throw new DataException("Description line does not start with a '# or $'");
        }
    }

    /** Split a line of text into a vector of delimited values */
    private ArrayList<String> splitDelimitedTextLine(String line) {
        boolean withinQuotes = false;
        String temporaryDelimiter = null;
        int pos1 = 0;
        int pos2 = 0;
        String remains;
        ArrayList<String> fragments = new ArrayList<>();

        String delimiter = ",";

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

                if (line.length() > pos1 && line.charAt(pos1) == '"') {
                    pos1++;
                    withinQuotes = true;
                    temporaryDelimiter = delimiter;
                    delimiter = "\"";
                }

                // Grab the next token
                pos2 = line.indexOf(delimiter, pos1);

                if (pos2!=-1) {
                    // Add a standard fragment to the fragments vector
                    fragments.add(line.substring(pos1, pos2).trim());
                    pos1 = pos2 + delimiter.length();

                } else {
                    // Handle the last value differently
                    remains = line.substring(pos1, line.length()).trim();
                    if (!remains.equalsIgnoreCase("")) {
                        fragments.add(remains);

                    } else {
                        fragments.add("");
                    }

                // Finished this line of data
                break;
                }
            }
        return fragments;

    }

    /** Pass out an emprty set of data with the correct column layout. This is
     * used when streaming in parallel and one input has finished */
    public Data getEmptyData() throws DataException {
        Data data = new Data();

        // Create the columns using the templates
        for(Column template : columnTemplates){
            data.addColumn(template.getEmptyCopy());
        }
        return data;
    }

    /** Load the data / next chunk of data into a data set */
    public synchronized Data read() throws DataException {
        if(inUse){
            Data data = new Data();

            Column newCol;
            
            // Create the columns using the templates
            for(Column template : columnTemplates){
                newCol = template.getEmptyCopy();
                data.addColumn(newCol);
            }

            // Setup the index column
            if(indexTemplate!=null){
                data.setIndexColumn(indexTemplate.getEmptyCopy());
            }
            
            // Read the next set of lines
            boolean finishRead = false;
            String fragment;
            String line;
            int linesRead = 0;
            ArrayList<String>fragments;
            int startPoint;
            int expectedSize;
            if(containsIndex && hasInternalIndex){
                startPoint = 2;
                expectedSize = columns + 2;
                
            } else if(containsIndex && hasInternalIndex==false){
                startPoint = 1;
                expectedSize = columns + 1;
                
            } else if(hasInternalIndex && containsIndex==false){
                startPoint = 1;
                expectedSize = columns + 1;
                
            } else {
                startPoint = 0;
                expectedSize = columns;
            }
            
            
            
            while(!finishRead){
                try {
                    line = reader.readLine();
                    if(line!=null){
                        if(!line.trim().equals("")){
                            fragments = splitDelimitedTextLine(line);
                            if(fragments.size()==expectedSize){
                                // Add index
                                if(hasInternalIndex && containsIndex && data.hasIndexColumn()){
                                    data.getIndexColumn().appendCxDFormatValue(fragments.get(1));
                                } else if(hasInternalIndex && containsIndex==false && data.hasIndexColumn()){
                                    data.getIndexColumn().appendCxDFormatValue(fragments.get(0));
                                }
                                
                                for(int i=startPoint;i<fragments.size();i++){
                                    fragment = fragments.get(i);
                                    if(!fragment.equals(MissingValue.MISSING_VALUE_REPRESENTATION)){
                                        data.column(i - startPoint).appendCxDFormatValue(fragments.get(i));
                                    } else {
                                        data.column(i - startPoint).appendObjectValue(MissingValue.get());
                                    }
                                }
                            } else {
                                throw new DataException("Error reading line: " + line + " incorrect number of columns");
                            }
                        }
                    } else {
                        // END OF FILE
                        finishRead = true;
                        atEndOfFile = true;
                        close();
                    }
                } catch (IOException ioe){
                    throw new DataException("Error loading data: " + ioe.getMessage());
                }

                linesRead++;
                if(!atEndOfFile){
                    totalRowsRead++;
                }

                // Check for end of read condition
                if(linesRead>=chunkSize && inChunkMode){
                    finishRead = true;
                }
            }

            // Copy in properties if there are any
            if(dataProperties!=null){
                try {
                    data.setProperties(((XmlDataStore)dataProperties.getCopy()));
                } catch(Exception ex){
                    throw new DataException("Error copying data properties: " + ex.getMessage(), ex);
                }      
            }
            return data;
            
        } else {
            throw new DataException("Data reader has not been initialized");
        }
    }
}