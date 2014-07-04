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
package com.connexience.server.workflow.test;

import com.connexience.server.workflow.blocks.processor.*;
import com.connexience.server.workflow.service.*;
import com.connexience.server.workflow.engine.*;
import com.connexience.server.util.RandomGUID;
import com.connexience.server.workflow.engine.data.CSVTransferDataWriter;
import com.connexience.server.workflow.util.ZipUtils;


import org.pipeline.core.data.*;
import org.pipeline.core.data.io.*;
import java.io.*;


/**
 * This class provides a test harness for a workflow block that loads the service.xml
 * file in order to construct a sensibly formed invocation message and provide stub
 * data sources.
 * @author hugo
 */
public class BlockTester {
    /** Name of the service.xml file to parse */
    private String serviceXmlFile = "service.xml";
    
    /** ServiceXML definition object */
    private DataProcessorServiceDefinition def;

    /** Block to assist with the test */
    private DataProcessorBlock block;
    
    /** Call message for block */
    private DataProcessorCallMessage message;
    
    /** Global data store */
    private GlobalDataSource globalSource;
    
    /** Data processor service object */
    private DataProcessorService service;
    
    /** Invocation directory */
    private File invocationDir;
    
    public BlockTester() {
    }
    
    public void setup() throws Exception {
        createGlobalDataSource();
        loadServiceXml();
        createBlock();
        createCallMessage();
        createInvocationDirectory();
        copySourceData();
        createDataProcessorService();
    }
    
    public void execute() throws Exception {
        boolean iosClosed = false;
        Exception executeException = null;

        int chunkSize = message.getProperties().intValue("StreamingChunkSize", 1000);

        try {
            // Set the streaming parameters of the service
            service.streamInChunksOf(chunkSize);

            // Set up any data sets so that they can be read in chunks if necessary
            service.initialiseIOs();
            
            // Tell the service that the execution process is about to start
            service.executionAboutToStart();
                    
            // Run the service
            try {
                              
                while(!service.isStreamingFinished()){
                    service.execute();
                }
            } catch (Exception e){
                executeException = e;
            }
            
            // Tell the service that all of the data has been processed
            service.allDataProcessed();
            
            // Close all the service IOs if possible
            service.closeIOs();
            iosClosed = true;

            // Call the tidyup method
            Exception tidyException = null;
            try {
                service.preCloseTidyup();
            } catch (Exception e){
                tidyException = e;
            }

            // If there was an execute exception, throw it now that everything has been cleaned up
            if(executeException!=null){
                throw executeException;
            }

            if(tidyException!=null){
                throw tidyException;
            }
            
        } catch (Exception e){
            throw e;
        } catch (Throwable t){
            service.sendErrorResponseMessage("Runtime error: " + t.getMessage());
        } finally {
            if(!iosClosed){
                service.closeIOs();
            }
        }
        
    }
    
    public void createGlobalDataSource() throws Exception {
        File inkspotDir = new File(System.getProperty("user.home") + System.getProperty("file.separator") + ".inkspot");
        if(!inkspotDir.exists()){
            inkspotDir.mkdir();
        }
        
        File testDataStore = new File(inkspotDir, "testData");
        if(!testDataStore.exists()){
            testDataStore.mkdir();
        }
        
        globalSource = new GlobalDataSource(testDataStore.getPath());
        
    }
    
    public void createInvocationDirectory(){
        File baseDir = new File(globalSource.getBaseDirectory());
        invocationDir = new File(baseDir, message.getInvocationId());
        if(!invocationDir.exists()){
            invocationDir.mkdir();
        }
    }
    
    public void deleteInvocationDirectory() throws Exception {
        ZipUtils.removeDirectory(invocationDir);
    }
    
    public void loadServiceXml() throws Exception {
        def = new DataProcessorServiceDefinition();
        InputStream xmlStream = getClass().getResourceAsStream("/service.xml");
        def.loadXmlStream(xmlStream);
    }
    
    public void createBlock() throws Exception {
        block = new DataProcessorBlock();
        block.setUnconnectedInputErrorsIgnored(true);
        block.setServiceDefinition(def);   
        block.setGlobalDataStore(globalSource);
        block.setInvocationId(new RandomGUID().toString());
        block.initialiseForService();
    }
    
    public void createCallMessage() throws Exception {
        message = block.createCallMessage();
        
        // Set the message connected inputs so that there are files
        // referenced that match the input names
        for(int i=0;i<message.getDataSources().length;i++){
            message.getDataSourceConnectionContexts()[i] = "source";
            message.getDataSourceConnections()[i] = message.getDataSources()[i];
        }
    }
    
    public void createDataProcessorService() throws Exception {
        Class<?> processorClass = Class.forName(message.getServiceRoutine());
        Object processorObject = processorClass.newInstance();
        service = (DataProcessorService)processorObject;
        service.setCallMessage(message);
        service.initialiseIOs();
    }
    
    public void copySourceData() throws Exception {
        // Copy data from all of the inputs to the invocation directory
        File sourceDir = new File(globalSource.getBaseDirectory() + File.separator + message.getInvocationId());
        InputStream inStream;
        FileOutputStream outStream;
        File targetFile;
        File classNameFile;
        String className;
        
        for(int i=0;i<message.getDataSources().length;i++){
            if(message.getDataSourceTypes()[i].equals("data-wrapper")){
                // Copy DataWrappers as CSV data transfer objects
                inStream = getClass().getResourceAsStream("/test/" + message.getDataSources()[i]);
                DelimitedTextDataImporter importer = new DelimitedTextDataImporter();
                Data inData = importer.importInputStream(inStream);
                inStream.close();
                targetFile = new File(sourceDir, message.getDataSourceConnections()[i] + "-" + message.getDataSourceConnectionContexts()[i] + ".dat");
                outStream = new FileOutputStream(targetFile);
                CSVTransferDataWriter writer = new CSVTransferDataWriter(targetFile);
                writer.initialise();
                writer.write(inData);
                writer.close();
                className = "com.connexience.server.workflow.engine.datatypes.DataWrapper";
                
            } else if(message.getDataSourceTypes()[i].equals("file-wrapper")){
                className = "com.connexience.server.workflow.engine.datatypes.FileWrapper";
                
                // Copy the file
                inStream = getClass().getResourceAsStream("/test/" + message.getDataSources()[i]);
                targetFile = new File(sourceDir, message.getDataSourceConnectionContexts()[i]);
                outStream = new FileOutputStream(targetFile);
                ZipUtils.copyInputStream(inStream, outStream);
                inStream.close();
                outStream.flush();
                outStream.close();
                
                // Create the wrapper object that lists this file
                targetFile = new File(sourceDir, message.getDataSourceConnections()[i] + "-" + message.getDataSourceConnectionContexts()[i] + ".dat");
                ZipUtils.writeSingleLineFile(targetFile, message.getDataSourceConnectionContexts()[i]);
                
            } else if(message.getDataSourceTypes()[i].equals("object-wrapper")){
                className = "com.connexience.server.workflow.engine.datatypes.ObjectWrapper";
                inStream = getClass().getResourceAsStream("/test/" + message.getDataSources()[i]);
                targetFile = new File(sourceDir, message.getDataSourceConnections()[i] + "-" + message.getDataSourceConnectionContexts()[i] + ".dat");
                outStream = new FileOutputStream(targetFile);
                ZipUtils.copyInputStream(inStream, outStream);
                inStream.close();
                outStream.flush();
                outStream.close();                
                
                
            } else if(message.getDataSourceTypes()[i].equals("properties-wrapper")){
                className = "com.connexience.server.workflow.engine.datatypes.PropertiesWrapper";
                inStream = getClass().getResourceAsStream("/test/" + message.getDataSources()[i]);
                targetFile = new File(sourceDir, message.getDataSourceConnections()[i] + "-" + message.getDataSourceConnectionContexts()[i] + ".dat");
                outStream = new FileOutputStream(targetFile);
                ZipUtils.copyInputStream(inStream, outStream);
                inStream.close();
                outStream.flush();
                outStream.close();                
            } else {
                className = "java.lang.Object";
            }
            
            // Write the class name
            ZipUtils.writeSingleLineFile(new File(sourceDir, message.getDataSourceConnections()[i] + "-" + message.getDataSourceConnectionContexts()[i] + "-class-name.txt"), className);
        }
    }
}