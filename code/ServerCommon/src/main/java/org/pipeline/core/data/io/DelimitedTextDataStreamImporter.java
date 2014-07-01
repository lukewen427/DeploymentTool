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

import org.pipeline.core.data.Data;
import org.pipeline.core.data.columns.IntegerColumn;

import java.io.*;
import java.util.Vector;

/**
 * This class extends the standard DelimitedTextDataImporter and imports a large data
 * file in chunks. 
 * @author hugo
 */
public class DelimitedTextDataStreamImporter extends DelimitedTextDataImporter {
    /** Chunk size */
    private int chunkSize = 10;
    
    /** Current location */
    private int currentLocation = 0;
    
    /** Top chunk of data */
    private Data topChunk = null;
    
    /** Current chunk of data */
    private Data currentChunk = null;
    
    /** File to read */
    private File importFile = null;
    
    /** Is the file in the process of being read */
    private boolean fileReading = false;
    
    /** Is this the first chunk */
    private boolean firstChunk;
    
    /** Reader for loading data */
    private LineNumberReader fileReader = null;
    
    /** Number of lines imported */
    private int linesImported = 0;
    
    /** Has the import finished */
    private boolean finished = false;

    /** Vector of names */
    private Vector namesVector = null;

    /** Creates a new instance of DelimitedTextDataStreamImporter */
    public DelimitedTextDataStreamImporter() {
        super();
    }
    
    /** Is the import finished */
    public boolean isFinished(){
        return finished;
    }
    
    /** Set the file to read */
    public void setImportFile(File importFile){
        this.importFile = importFile;
        if(importFile!=null){
            fileName = importFile.getAbsolutePath();
            
        } else {
            fileName = "";
        }
    }
    
    /** Import the first chunk of data. This is used to get data metadata and
     * as a preview data set for configuring drawings with the correct columns
     * etc */
    public Data importTopChunk() throws DataImportException {
        resetRead();
        if(fileReading){
            topChunk = importNextChunk();
            terminateRead();
            return topChunk;
            
        } else {
            throw new DataImportException("Error importing data");
        }
    }
    
    /** Try and read the title row */
    private Vector readTitles() throws DataImportException {
        if(importColumnNames){
            LineNumberReader reader = null;
            try {
                reader = new LineNumberReader(new FileReader(new File(fileName)));
                int count = 0;
                boolean found = false;
                String rawLine = reader.readLine();
                String line;
                Vector names = null;
                
                while(!rawLine.equals("") && found==false){
                    line = rawLine.trim();
                    count++;
                    if(count==nameRow){
                        names = processTitleLine(line);
                        found = true;
                    }
                }
                reader.close();
                return names;
                
            } catch (Exception e){
                try {reader.close();}catch(Exception ex){}
                fileReading = false;
                throw new DataImportException("Error importing data");
            }
        } else {
            return null;
        }
    }

    /** Reset the read with an InputStream */
    public void resetWithInputStream(InputStream stream) throws DataImportException {
        if(fileReading==false){
            try {
                fileReader = new LineNumberReader(new InputStreamReader(stream));
                fileReading = true;
                firstChunk = true;
                linesImported = 0;
                finished = false;
                currentLocation = 0;
                String line;

                // Read data until the data is started
                if(dataStartRow>0){

                    while(currentLocation<this.dataStartRow){
                        line = fileReader.readLine();
                        if(!line.trim().equals("")){
                            currentLocation++;
                            // Process the title line
                            if(currentLocation==this.getNameRow() && this.importColumnNames){
                                namesVector = processTitleLine(line);
                            }
                        }
                    }
                }
            } catch (Exception e){
                fileReading = false;
                fileReader = null;
                firstChunk = true;
                linesImported = 0;
                finished = true;
                throw new DataImportException("Error importing data: " + e.getMessage(), e);
            }

        } else {
            fileReading = false;
            fileReader = null;
            firstChunk = true;
            linesImported = 0;
            finished = true;
            throw new DataImportException("File already open");
        }
    }

    /** Reset the read to the top of the file and open a reading stream */
    public void resetRead() throws DataImportException {
        if(fileReading==false){
            try {
                importedNames = readTitles();
                fileReader = new LineNumberReader(new FileReader(new File(fileName)));
                fileReading = true;
                firstChunk = true;
                linesImported = 0;
                finished = false;
                
                // Read data until the data is started
                int count = 0;
                if(dataStartRow>1){
                    String line = fileReader.readLine();
                    count++;
                    while(count<this.dataStartRow && !line.trim().equals("")){
                        line = fileReader.readLine();
                        count++;
                    }
                }
            } catch (Exception e){
                fileReading = false;
                fileReader = null;
                firstChunk = true;
                linesImported = 0;
                finished = true;
                throw new DataImportException("Error importing data");
            }
            
        } else {
            fileReading = false;
            fileReader = null;
            firstChunk = true;
            linesImported = 0;
            finished = true;
            throw new DataImportException("File already open");
        }
    }
    
    /** Read the next chunk of data */
    public Data importNextChunk() throws DataImportException {
        if(fileReading && finished==false){
            int count = 0;
            int expectedColNo = 0;
            boolean chunkEnd = false;
            boolean firstLine = true;
            
            try {
                String rawLine;
                String line;
                
                while(count<chunkSize && chunkEnd==false){
                    rawLine = fileReader.readLine();
                    if(rawLine!=null /*&& !rawLine.equals("")*/){
                        line = rawLine.trim();

                        if(!line.equals("")){
                            if(firstLine){
                                if(firstChunk){
                                    // This is the first chunk, need to create new data set
                                    currentChunk = new Data();
                                    currentChunk.setIndexColumn(new IntegerColumn("Index"));
                                } else {
                                    // Create an empty copy
                                    currentChunk = topChunk.getEmptyCopy();
                                    currentChunk.setIndexColumn(new IntegerColumn("Index"));
                                }
                                firstLine = false;
                            }

                            expectedColNo = processDataLine(line, count, currentChunk, importedNames, expectedColNo);
                            ((IntegerColumn)currentChunk.getIndexColumn()).appendIntValue(currentLocation);
                            linesImported++;
                            currentLocation++;
                            count++;
                        }
                        
                    } else {
                        finished = true;
                        chunkEnd =true;
                    }
                }
                
                // Make this the top chunk if it's the first
                if(firstChunk){
                    firstChunk = false;
                    topChunk = currentChunk;
                }

                // Set the names if there is a names vector
                if (namesVector != null && currentChunk != null) {
                    // Take the smaller of the two column numbers...
                    int minColNo = Math.min(namesVector.size(), currentChunk.getColumns());
                    // and set the column names.
                    for(int i = 0; i < minColNo; i++) {
                        currentChunk.column(i).setName(namesVector.get(i).toString());
                    }
                }
                
                return currentChunk;
                
            } catch (Exception e){
                e.printStackTrace();
                terminateRead();
                throw new DataImportException("Error importing data");
            }
            
        } else {
            throw new DataImportException("Error importing data");
        }
    }
    
    /** Close the reading stream */
    public void terminateRead(){
        if(fileReading){
            if(fileReader!=null){
                try {
                    fileReader.close();
                } catch (Exception e){}
                fileReader = null;
            }
            fileReading = false;
            firstChunk = true;
            finished = true;
        }
    }
    
    /** Get the number of lines imported */
    public int getLinesImported(){
        return linesImported;
    }
    
    /** Get the current chunk */
    public Data getCurrentChunk(){
        return currentChunk;
    }
    
    /** Get the top chunk */
    public Data getTopChunk(){
        return topChunk;
    }
    
    /** Get the chunk size */
    public int getChunkSize(){
        return chunkSize;
    }
    
    /** Set the chunk size */
    public void setChunkSize(int chunkSize){
        if(chunkSize>0){
            this.chunkSize = chunkSize;
        }
    }
}
