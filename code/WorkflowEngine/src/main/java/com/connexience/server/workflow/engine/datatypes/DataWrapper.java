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

package com.connexience.server.workflow.engine.datatypes;

import java.security.DigestOutputStream;
import com.connexience.server.util.DivertingOutputStream;
import com.connexience.server.workflow.engine.HashableTransferObject;
import com.connexience.server.workflow.engine.StreamableTransferData;
import java.io.*;
import org.pipeline.core.data.*;
import com.connexience.server.workflow.engine.data.*;
import java.security.MessageDigest;
import org.pipeline.core.drawing.*;
import org.pipeline.core.xmlstorage.Base64;

/**
 * This class wraps up a Data object in a TransferData interface.
 * @author hugo
 */
public class DataWrapper implements TransferData, StreamableTransferData, HashableTransferObject {
    /** Data being passed around */
    private Data data = null;

    /** Reader object for this wrapper */
    CSVTransferDataReader reader;

    /** Writer object for this wrapper */
    CSVTransferDataWriter writer;

    /** Is this object reading or writing */
    private boolean inWriteMode = false;

    /** Is this object in use */
    private boolean inUse = false;

    /** Is this object in streaming mode */
    private boolean streaming = false;

    /** Chunk size for streaming */
    private int chunkSize = 1000;

    /** Last chunk of data read */
    private Data lastChunk = null;

    /** Total number of bytes to read */
    private long totalBytesToRead = 0;

    /** Are the total number of bytes to read known */
    private boolean totalBytesToReadKnown = false;

    /** MD5 calculating stream */
    private DigestOutputStream hashStream;
    
    /** Create an empty data wrapper. This is used when creating empty objects
     * befire they are populated with data */
    public DataWrapper(){
    }
    
    /** Creates a new instance of DataWrapper from a Data object */
    public DataWrapper(Data data) {
        this.data = data;            
    }

    /** Set whether this object is in streaming mode */
    public void setStreaming(boolean streaming) {
        this.streaming = streaming;
        if(reader!=null){
           reader.setInChunkMode(streaming);
        }
    }

    /** Set the streaming chunk size */
    public void setChunkSize(int chunkSize){
        this.chunkSize = chunkSize;
        if(reader!=null){
            reader.setChunkSize(chunkSize);
        }
    }

    /** Get the data payload */
    public Object getPayload() {
        if(isFinished()){
            // Return nothing if the input is finished
            return null;

        } else if(!inWriteMode){
            if(streaming){
                // Read a chunk of data
                try {
                    lastChunk = reader.read();
                    return lastChunk;
                } catch (Exception e){
                    System.out.println("Error loading chunk: " + e.getMessage());
                    return null;
                }
            } else {
                // If there is no data, read the data as one chunk. Otherwise, just return the
                // last loaded chunk. This is so that a block with a streaming and non-streaming
                // input can load its non streamed input multiple times.
                if(lastChunk==null){
                    try {
                        lastChunk = reader.read();
                        return lastChunk;
                    } catch (Exception e){
                        System.out.println("Error loading non-streamed chunk: " + e.getMessage());
                        return null;
                    }
                } else {
                    return lastChunk;
                }
            }
            
        } else {
            return null;
        }
    }

    /** Write a chunk of data */
    public void writeChunk(Data dataChunk) throws DataException {
        if(inUse && inWriteMode) {
            writer.write(dataChunk);
        }
    }

    /** Read a chunk of data */
    public Data readChunk() throws DataException {
        if(isFinished()){
            // Return nothing if the input is finished
            return null;
        } else if(inUse && inWriteMode==false){
            if(streaming){
                // Read a chunk of data
                if(!reader.isAtEndOfFile()){
                    return reader.read();
                } else {
                    return null;
                }
            } else {
                // If there is no data, read the data as one chunk. Otherwise, just return the
                // last loaded chunk. This is so that a block with a streaming and non-streaming
                // input can load its non streamed input multiple times.
                if(lastChunk==null){
                    lastChunk = reader.read();
                    return lastChunk;
                } else {
                    return lastChunk;
                }
            }
        } else {
            return null;
        }
    }

    /** Get a copy of this data */
    public TransferData getCopy() throws DrawingException {
        try {
            DataWrapper wrapper = new DataWrapper(data.getCopy());
            return wrapper;
            
        } catch (Exception e){
            // TODO: Internationalise
            throw new DrawingException("Cannot create data copy");
        }
    }

    /** Begin reading from an input stream */
    public void beginReading(InputStream stream) throws DrawingException {
        if(!inUse){
            inUse = true;
            inWriteMode = false;
            try {
                reader = new CSVTransferDataReader(stream);
                reader.setInChunkMode(streaming);
                reader.setChunkSize(chunkSize);
                
            } catch (Exception e){
                throw new DrawingException("Error opening data reader: " + e.getMessage());
            }

            reader.setInChunkMode(streaming);

        } else {
            throw new DrawingException("Data transfer object is already in use");
        }
    }

    /** Start writing data */
    public void beginWriting(OutputStream stream) throws DrawingException {
        if(!inUse){
            inUse = true;
            inWriteMode = true;
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                hashStream = new DigestOutputStream(stream, md5);
                writer = new CSVTransferDataWriter(hashStream);

                // Write the data if needed
                if(data!=null){
                    writer.write(data);
                    close();
                }
            } catch (Exception e){
                throw new DrawingException("Error opening data writer: " + e.getMessage());
            }
        } else {
            throw new DrawingException("Data transfer object is already in use");
        }
    }

    /** Is the reading / writing process complete */
    public boolean isFinished() {
        if(inUse){
            if(inWriteMode){
                return false;
            } else {
                return reader.isAtEndOfFile();
            }
        } else {
            return true;
        }
    }

    /** Close the reader / writer */
    public void close() throws DrawingException {
        if(inUse){
            if(inWriteMode){
                writer.close();
                inUse = false;
                try {
                    hashStream.flush();
                    hashStream.close();
                } catch (Exception e){
                    throw new DrawingException("Error closing hash output stream: " + e.getMessage(), e);
                }
                
            } else {
                reader.close();
                inUse = false;
            }
        }
    }

    /** Get an empty set of data from the reader if possible */
    public Data getEmptyData() throws DrawingException {
        if(!inWriteMode){
            if(reader!=null){
                try {
                    return reader.getEmptyData();
                } catch (DataException de){
                    throw new DrawingException("Error creating empty data set: " + de.getMessage());
                }
            } else {
                throw new DrawingException("Cannot create empty data set: data reader has not been created");
            }
        } else {
            throw new DrawingException("Cannot create empty data set from an output wrapper");
        }

    }

    public void setTotalBytesToRead(long bytesToRead) {
        this.totalBytesToRead = bytesToRead;
    }

    public long getTotalBytesToRead() {
        return totalBytesToRead;
    }

    public void setTotalBytesKnown(boolean totalBytesKnown) {
        this.totalBytesToReadKnown = totalBytesKnown;
    }

    public boolean isTotalBytesKnown() {
        return this.totalBytesToReadKnown;
    }

    @Override
    public long getActualBytesRead() {
        if(inUse && streaming && !inWriteMode){
            return reader.getTotalBytesRead();
        } else {
            return 0;
        }
    }

    @Override
    public String getHash() throws DrawingException {
        if(hashStream!=null){
            return com.connexience.server.util.Base64.encodeBytes(hashStream.getMessageDigest().digest());
        } else {
            return null;
        }
    }
}