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
package com.connexience.server.workflow.service;

import com.connexience.server.model.metadata.MetadataCollection;
import org.pipeline.core.drawing.*;
import com.connexience.server.workflow.engine.*;
import java.io.*;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.io.XmlFileIO;

/**
 * This class holds a TransferData object for an input data set
 * @author nhgh
 */
public class InputTransferDataHolder {
    /** Data transfer object */
    private TransferData dataObject;

    /** Human readable name of the transfer data type */
    private String transferTypeName = "";

    /** Data source client that is used to load the data */
    private DataProcessorDataSource sourceClient;

    /** Stream being used to read data */
    private InputStream stream;

    /** Port that the transfer data is connected to */
    private String linkedPort;

    /** ID of the block that the transfer data is connected to */
    private String linkedContext;

    /** Workflow invocation ID */
    private String invocationId;

    /** Is this a streaming connection */
    private boolean streamingConnection = false;

    /** Chunk size for streaming */
    private int chunkSize = 1000;

    /** Is this input connected */
    private boolean connected = false;
    
    /** Metadata for connection */
    MetadataCollection metadata = null;
    
    /** Create an input transfer data holder */
    public InputTransferDataHolder(String transferTypeName, String linkedPort, String linkedContext, DataProcessorDataSource sourceClient, String invocationId, boolean streamingConnection, int chunkSize) throws DataProcessorException {
        this.sourceClient = sourceClient;
        this.transferTypeName = transferTypeName;
        this.linkedContext = linkedContext;
        this.linkedPort = linkedPort;
        this.invocationId = invocationId;
        this.streamingConnection = streamingConnection;
        this.chunkSize = chunkSize;
        createTransferDataObject();
    }

    /** Create the transfer object */
    private void createTransferDataObject() throws DataProcessorException {
        if(!this.linkedContext.equals("???") && ! this.linkedPort.equals("???")){
            try {
                boolean sizeKnown = false;
                long size = 0;
                if(sourceClient.allowsFileSystemAccess()){
                    size = sourceClient.getInputDataLength(invocationId, linkedContext, linkedPort);
                    sizeKnown = true;

                    // Load metadata
                    File metadataFile = new File(sourceClient.getStorageDirectory(invocationId), linkedPort + "-" + linkedContext + "-metadata.xml");
                    if(metadataFile.exists()){
                        try {
                            XmlFileIO reader = new XmlFileIO(metadataFile);
                            XmlDataStore store = reader.readFile();
                            MetadataCollection mdc = new MetadataCollection();
                            mdc.recreateObject(store);
                            metadata = mdc;
                        } catch (Exception e){
                            throw new DataProcessorException("Error loading metadata: " + e.getMessage(), e);
                        }
                    }
                }

                stream = sourceClient.getInputDataStream(invocationId, linkedContext, linkedPort);
                dataObject = DataTypes.instantiateTransferData(transferTypeName, stream);

                // Set up if streamable transfer data
                if(dataObject instanceof StreamableTransferData){
                    ((StreamableTransferData)dataObject).setStreaming(streamingConnection);
                    ((StreamableTransferData)dataObject).setChunkSize(chunkSize);
                    ((StreamableTransferData)dataObject).setTotalBytesKnown(sizeKnown);
                    ((StreamableTransferData)dataObject).setTotalBytesToRead(size);
                }
                connected = true;

            } catch (Exception e){
                throw new DataProcessorException("Cannot instantiate input data object: " + e.getMessage());
            } finally {
                // Stream should be closed if this is not a streaming connection
                if(!(dataObject instanceof StreamableTransferData) && stream!=null){
                    try {
                        stream.close();
                    } catch (Exception e){
                        System.out.println("Error closing transfer data stream: " + e.getMessage());
                    }
                }
            }
            
        } else {
            // Unconnected input
            dataObject = null;
            connected = false;
        }
    }

    /** Get the metadata for this input */
    public MetadataCollection getMetadata() {
        return metadata;
    }

    /** Does this object contain metadat */
    public boolean hasMetadata(){
        if(metadata!=null){
            return true;
        } else {
            return false;
        }
    }
        
    /** Has the data reader finished reading everything */
    public boolean isFinished(){
        if(dataObject instanceof StreamableTransferData){
            return ((StreamableTransferData)dataObject).isFinished();
        } else {
            // True if not a streamable connection
            return true;
        }
    }

    public boolean isConnected() {
        return connected;
    }
    
    /** Close the stream if this is a streamable object */
    public void close() throws DataProcessorException {
        if(dataObject instanceof StreamableTransferData){
            if(stream!=null) {
                try {
                     stream.close();
                } catch (Exception e){
                    throw new DataProcessorException("Cannot close stream for input linked to: " + linkedPort + ": " + e.getMessage());
                }
            }

        }
    }
    /** Get the transfer data object */
    public TransferData getTransferData(){
        return dataObject;
    }
}