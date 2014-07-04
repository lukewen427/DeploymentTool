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
import com.connexience.server.workflow.engine.*;
import com.connexience.server.workflow.engine.datatypes.FileWrapper;
import org.pipeline.core.drawing.*;

import java.io.*;
import org.pipeline.core.xmlstorage.io.XmlFileIO;

/**
 * This class holds a set of output data associated with a data processor
 * service. In the cases where a data-wrapper is used, a stream is opened
 * and all data sent to the output is appended to the file. In other cases,
 * no transfer data is instantiated, and data is written in one shot when the
 * output is set.
 * @author nhgh
 */
public class OutputTransferDataHolder {
    /** Expected data type */
    private String transferTypeName;

    /** Output name */
    private String outputName;

    /** Invocation ID */
    private String invocationId;

    /** Block context ID */
    private String contextId;

    /** Data Transfer object being used if this is a streaming destination */
    private TransferData dataObject;

    /** Data source client that is used to save the data to */
    private DataProcessorDataSource sourceClient;

    /** Does this data holder represent a streaming data object */
    private boolean streamingObject = false;

    /** MD5 hash value */
    private String hashValue = null;
    
    /** Metadata for object */
    private MetadataCollection metadata;
    
    /** Create an OutputTransferDataHolder */
    public OutputTransferDataHolder(String transferTypeName, String outputName, String contextId, DataProcessorDataSource sourceClient, String invocationId) throws DataProcessorException {
        this.transferTypeName = transferTypeName;
        this.outputName = outputName;
        this.contextId = contextId;
        this.sourceClient = sourceClient;
        this.invocationId = invocationId;
        createTransferDataObject();
    }


    /** Create the data transfer object. This only creates an object if the data type
     * is a streamable object. Otherwise, the object is left until the workflow
     * service explicitly sets it . */
    private void createTransferDataObject() throws DataProcessorException {
        // Only create if this is a streaming transfer object
        try {
            if(DataTypes.isStreamable(transferTypeName)){
                streamingObject = true;
                try {
                    dataObject = DataTypes.instantiateTransferData(transferTypeName, sourceClient.getOutputDataStream(invocationId, contextId, outputName, transferTypeName));
                } catch (DrawingException de){
                    throw new DataProcessorException("Cannot create streaming data output holder: " + de.getMessage());
                }
            } else {
                streamingObject = false;
                dataObject = null;
            }
        } catch (DrawingException de){
            throw new DataProcessorException("Error creating data transfer object: " + de.getMessage());
        }
    }

    /** Get the transfer data object */
    public TransferData getTransferData(){
        return dataObject;
    }

    /** Is this a streaming output */
    public boolean isStreamingObject(){
        return streamingObject;
    }

    /** Set an entire data transfer object and write it to the source client stream */
    public void setDataObject(TransferData dataObject) throws DataProcessorException {
        if(!streamingObject && this.dataObject==null){

            // If this is a file-wrapper, set the working directory if possible
            if(dataObject instanceof FileWrapper && sourceClient.allowsFileSystemAccess()){
                ((FileWrapper)dataObject).setHashContext(new File(sourceClient.getStorageDirectory(invocationId)));
            }
            
            // Calculate the hash
            if(dataObject instanceof HashableTransferObject){
                try {
                    hashValue = ((HashableTransferObject)dataObject).getHash();
                    sourceClient.setOutputHash(invocationId, contextId, outputName, hashValue);
                } catch (Exception e){
                    throw new DataProcessorException("Cannot calculate transfer data MD5 hash: " + e.getMessage(), e);
                }
            }

            // Save the data after calculating the Hash as this will fix inconsistencies in the FileWrapper if possible.
            if(dataObject instanceof StorableTransferData){
                try {
                    OutputStream stream = sourceClient.getOutputDataStream(invocationId, contextId, outputName, transferTypeName);
                    ((StorableTransferData)dataObject).saveToOutputStream(stream);
                    stream.flush();
                    stream.close();
                } catch (Exception e){
                    throw new DataProcessorException("Error saving output data: " + e.getMessage());
                }
            } else {
                throw new DataProcessorException("Cannot save non-storable data objet");
            }
            
        } else {
            throw new DataProcessorException("Cannot set transfer data object in a streaming output");
        }
    }

    /** Close the data writer if this is a streaming object */
    public void close() throws DataProcessorException {
        try {
            if(streamingObject && dataObject!=null){
                ((StreamableTransferData)dataObject).close();
                
                if(dataObject instanceof HashableTransferObject){
                    hashValue = ((HashableTransferObject)dataObject).getHash();
                    sourceClient.setOutputHash(invocationId, contextId, outputName, hashValue);
                }
            }
            writeMetadata();
            
        } catch (DrawingException de){
            throw new DataProcessorException("Error closing output data object: " + de.getMessage());
        }
    }

    /** Set the metadata and save it to disk */
    private void writeMetadata() throws DataProcessorException {
        if(metadata!=null){
            if(sourceClient.allowsFileSystemAccess()){
                File metadataFile = new File(sourceClient.getStorageDirectory(invocationId), outputName + "-" + contextId + "-metadata.xml");
                try {
                    XmlFileIO writer = new XmlFileIO(metadata.storeObject());
                    writer.writeFile(metadataFile);
                } catch (Exception e){
                    throw new DataProcessorException("Error saving metadata: " + e.getMessage(), e);
                }
            } else {
                throw new DataProcessorException("Metadata not supported on non filesystem based data source");
            }
        }
    }

    public void setMetadata(MetadataCollection metadata) {
        this.metadata = metadata;
    }

    public MetadataCollection getMetadata() {
        return metadata;
    }
    
    public String getTransferTypeName(){
      return transferTypeName;
    }
}