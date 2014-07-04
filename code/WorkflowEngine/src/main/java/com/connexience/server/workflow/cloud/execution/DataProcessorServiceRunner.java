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
package com.connexience.server.workflow.cloud.execution;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.util.DivertingOutputStream;
import com.connexience.server.util.DivertingInputStream;
import com.connexience.server.workflow.cloud.services.CloudDataProcessorService;
import com.connexience.server.workflow.service.*;
import com.connexience.server.model.security.*;
import com.connexience.server.model.workflow.WorkflowInvocationFolder;
import com.connexience.server.util.SerializationUtils;
import com.connexience.server.workflow.WorkflowBlock;
import com.connexience.server.workflow.blocks.processor.DataProcessorBlock;
import com.connexience.server.workflow.cloud.library.CloudWorkflowServiceLibraryItem;
import com.connexience.server.workflow.cloud.services.SimplifiedJavaService;
import com.connexience.server.workflow.util.XmlSerializationUtils;
import org.pipeline.core.xmlstorage.*;

import java.io.*;
import java.net.*;
import java.util.*;
import org.pipeline.core.xmlstorage.io.XmlFileIO;
import org.pipeline.core.xmlstorage.xmldatatypes.XmlDoubleDataObject;
import org.pipeline.core.xmlstorage.xmldatatypes.XmlIntegerDataObject;
import org.pipeline.core.xmlstorage.xmldatatypes.XmlLongDataObject;
import org.pipeline.core.xmlstorage.xmldatatypes.XmlStorableDataObject;

/**
 * This class provides the standard wrapper that executes a data processor
 * service in a standalone JVM. It takes in details of the service message, api
 * details, ticket etc and launches the data processor service with a response
 * message handler that writes the response message to file.
 * @author nhgh
 */
public class DataProcessorServiceRunner {
    // Exit code

    /** Exited ok */
    public static final int EXIT_OK = 0;

    /** Problem with the call arguments */
    public static final int CALL_ARGUMENT_ERROR = 1;

    /** Error setting up the service */
    public static final int SETUP_ERROR = 2;

    /** Invocation ID */
    private String invocationId;

    /** Context ID */
    private String contextId;

    /** Working directory for this service */
    private File workingDir;

    /** Service to use */
    private DataProcessorService service;

    /** Call message */
    private DataProcessorCallMessage message;

    /** Destination for response messages */
    private CloudDataProcessorResponseMessageFileHandler responseDestination;

    /** Heartbeat sending thread */
    //private HeartbeatSendThread heartbeatThread;
    
    /** Output flusher thread */
    private OutputFlusherThread flusherThread;
   
    /** Server for IO debugging connections */
    private ServerSocketListenerThread ioServerThread;
    
    /** Initial system.err stream */
    private PrintStream initialErrStream;
    
    /** Initial system.out stream */
    private PrintStream initialOutStream;
    
    /** Initial system.in stream */
    private InputStream initialInStream;
    
    /** Input stream dumper */
    DivertingInputStream inStreamDiverter;
    
    /** Output stream dumper */
    DivertingOutputStream outStreamDiverter;
    
    /** Error stream dumper */
    DivertingOutputStream errStreamDiverter;
    
    /** Security ticket for service */
    private Ticket ticket = null;
    
    /** Classloader for when running In-VM */
    private ClassLoader inVmClassLoader = null;
    
    public DataProcessorServiceRunner(String invocationsDir, String invocationId, String contextId){
        redirectIOStreams();
        this.invocationId = invocationId;
        this.contextId = contextId;
        workingDir = new File(invocationsDir + File.separator + invocationId);
    }

    public DataProcessorServiceRunner() {
        redirectIOStreams();
    }

    public void setInVmClassLoader(ClassLoader inVmClassLoader) {
        this.inVmClassLoader = inVmClassLoader;
    }

    public String getContextId() {
        return contextId;
        
    }

    public void setWorkingDir(File workingDir){
        this.workingDir = workingDir;
    }
    
    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getInvocationId() {
        return invocationId;
    }

    public void setInvocationId(String invocationId) {
        this.invocationId = invocationId;
    }

    /** Redirect the IO Streams */
    private void redirectIOStreams(){
        initialErrStream = System.err;
        initialInStream = System.in;
        initialOutStream = System.out;
        
        inStreamDiverter = new DivertingInputStream(initialInStream);
        System.setIn(inStreamDiverter);
        
        errStreamDiverter = new DivertingOutputStream(initialErrStream);
        System.setErr(new PrintStream(errStreamDiverter));
        
        outStreamDiverter = new DivertingOutputStream(initialOutStream);
        System.setOut(new PrintStream(outStreamDiverter));
    }
    
    /** Reset the IO stream redirects */
    private void undirectIOStreams()
    {
        try {
            inStreamDiverter.close();
        } catch (Exception e){
        }
        System.setIn(initialInStream);

        try {
            outStreamDiverter.close();
        } catch (Exception e){
        }
        System.setOut(initialOutStream);

        try {
            errStreamDiverter.close();
        } catch (Exception e){
        }
        System.setErr(initialErrStream);
    }
    
    /** Setup and execute the runner process */
    public int setupAndExecute(){
        // Setup the response message destination
        try {
            createMessageResponseDestination();
        } catch (Exception e){
            notifyError("Cannot create message response destination", e.getMessage());
            return SETUP_ERROR;
        }

        // Load the call message file
        try {
            loadCallMessage();
        } catch (Exception e){
            notifyError("Cannot load call message", e.getMessage());
            return SETUP_ERROR;
        }

        // Start the heartbeat thread
        /*
        String id = message.getInvocationId() + ":" + message.getContextId();
        heartbeatThread = new HeartbeatSendThread(5000, id);
        heartbeatThread.start();
        */
        
        /** Start the flusher thread */
        /*
        flusherThread = new OutputFlusherThread(5000);
        flusherThread.start();
          */      
        
        // Create the data processor class
        try {
            createDataProcessorService();
        } catch (Exception e){
            notifyError("Cannot create processor object", e.getMessage());
            return SETUP_ERROR;
        }

        // Load the library into the data processor service
        try {
             loadLibraryItem();
        } catch (Exception e){
            notifyError("Cannot load library data into service", e.getMessage());
            return SETUP_ERROR;
        }

        // Extract Provenance data from the library and call message
        if(service instanceof CloudDataProcessorService){
            CloudWorkflowServiceLibraryItem item = ((CloudDataProcessorService)service).getLibraryItem();
            if(item!=null){
                // Set the provenance data
                XmlDataStore provenance = new XmlDataStore();
                provenance.add("ServiceID", item.getZipDocumentRecord().getId());
                provenance.add("VersionID", item.getZipDocumentVersion().getId());
                provenance.add("VersionNumber", item.getZipDocumentVersion().getVersionNumber());
                provenance.add("ServiceName", item.getZipDocumentRecord().getName());
                provenance.add("WorkflowID", message.getWorkflowId());
                provenance.add("InvocationID", message.getInvocationId());
                provenance.add("ObjectType", "service");
                provenance.add("BlockUUID", message.getContextId());
                service.setProvenanceData(provenance);
            }
        }

        // Setup the data processor class so that it is ready to run
        try {
            setupDataProcessorService();
        } catch (Exception e){
            notifyError("Cannot setup data processor object: " + e.getMessage(), e.getMessage());
            return SETUP_ERROR;
        }
        
        /** Start a server socket */
        try {
            createIOServerThread();
        } catch (Exception e){
            notifyError("Cannot create IO server socket", e.getMessage());
        }
        
        // Run the service
        runService();
        
        // Reset the stream direction 
        undirectIOStreams();
        
        return EXIT_OK;
    }
    
    /** Main entry point */
    public static void main(String[] args){
        if(args.length==3){
            String invocationsDir = args[0];
            String invocationId = args[1];
            String contextId = args[2];
            
            DataProcessorServiceRunner runner = new DataProcessorServiceRunner(invocationsDir, invocationId, contextId);
            int code = runner.setupAndExecute();

            // Remove the lock file
            File flag = new File(new File(invocationsDir + File.separator + invocationId), contextId + "-running.flag");
            flag.delete();
            System.exit(code);

        } else {
            System.out.println("Argument count error");
            System.exit(CALL_ARGUMENT_ERROR);
        }

    }

    /** Write an error message to System.out */
    private static void notifyError(String error, String details){
        System.out.println(error + ": " + details);
    }

    /** Create the message response destination */
    public void createMessageResponseDestination(){
        responseDestination = new CloudDataProcessorResponseMessageFileHandler(workingDir);
    }
    

    /** Load the call message */
    public void loadCallMessage() throws Exception {
        File messageFile = new File(workingDir, contextId + "-message.msg");
        message = (DataProcessorCallMessage)XmlSerializationUtils.xmlDataStoreDeserialize(messageFile);
        ticket = (Ticket)SerializationUtils.deserialize(message.getTicketData());        
    }

    /** Try and load the library file */
    public void loadLibraryItem() throws Exception {
        if(service instanceof CloudDataProcessorService){
            File libraryItemFile = new File(workingDir, contextId + "-library.dat");
            CloudWorkflowServiceLibraryItem libItem = (CloudWorkflowServiceLibraryItem)XmlSerializationUtils.xmlDataStoreDeserialize(libraryItemFile);
            ((CloudDataProcessorService)service).setLibraryItem(libItem);
        }
    }

    /** Create the DataProcessor class */
    public void createDataProcessorService() throws Exception {
        String className = message.getServiceRoutine();
        
        Class<?> serviceClass;
        if(inVmClassLoader!=null){
           serviceClass = inVmClassLoader.loadClass(className);
        } else {
           serviceClass = Class.forName(className);
        }
        if(serviceClass!=null){
            Object processorObject = serviceClass.newInstance();
            if(processorObject instanceof DataProcessorService){
                // Standard data processor object
                service = (DataProcessorService)processorObject;
                
            } else if(processorObject instanceof WorkflowBlock){
                // Simplified block that needs to be wrapped up as a SimplifiedJaveaService
                service = new SimplifiedJavaService((WorkflowBlock)processorObject);
                
            }else {
                throw new Exception("Wrong service class");
            }
        }
    }
    
    /** Create the server socket for IO connections */
    public void createIOServerThread() throws Exception {
        ioServerThread = new ServerSocketListenerThread();
        ioServerThread.start();
    }
    
    /** Set up the data processor service in the same way that the existing
     * servlet does */
    public void setupDataProcessorService() throws Exception {
        if(service!=null){
            service.setResponseDestination(responseDestination);
            service.setTicket(ticket);
            service.setCallMessage(message);
            processMessageProperties();
        } else {
            throw new Exception("Service has not been created");
        }
    }

    /** Populate the properties with corrent document versions and folders and extract the properties
     * that can be used for the performance log */
    public void processMessageProperties() throws Exception {
        DataProcessorBlock blk = new DataProcessorBlock();
        XmlDataStore defaultProperties = blk.getEditableProperties();
        
        XmlDataStore properties = message.getProperties();
        XmlDataStore performaceProperties = new XmlDataStore("Performance");
        
        Enumeration i = properties.elements();
        XmlDataObject property;
        
        while(i.hasMoreElements()){
            property = (XmlDataObject)i.nextElement();
            if(!defaultProperties.containsName(property.getName())){
                if(property instanceof XmlDoubleDataObject){
                    // Numerical
                    performaceProperties.add(property.getName(), ((XmlDoubleDataObject)property).doubleValue());

                } else if(property instanceof XmlIntegerDataObject){
                    // Numerical 
                    performaceProperties.add(property.getName(), (double)((XmlIntegerDataObject)property).intValue());

                } else if(property instanceof XmlLongDataObject){
                    // Numerical
                    performaceProperties.add(property.getName(), (double)((XmlLongDataObject)property).longValue());
                    
                } else if(property instanceof XmlStorableDataObject){
                    // Could be anything - need to check it
                    XmlStorableDataObject sto = (XmlStorableDataObject)property;
                    if(sto.getClassName().contains("com.connexience.server")){
                        // Potentially one of ours that needs to be examined
                        Object obj = sto.getValue();
                        if(obj instanceof DocumentRecord){
                            // This is a document record, populate it
                            DocumentRecord original = (DocumentRecord)obj;
                            try {
                                DocumentRecord fetched = service.createApiLink().getDocument(original.getId());
                                if(fetched!=null){
                                    sto.setValue(fetched);
                                    performaceProperties.add(sto.getName(), (double)fetched.getCurrentVersionSize());
                                } else {
                                    performaceProperties.add(sto.getName(), (double)0);
                                }
                            } catch (Exception e){
                                System.out.println("Error loading DocumentRecord: " + sto.getName() + ": " + e.getMessage());
                            }
                        }
                    }
                }
            }
        }
        
        // Save these to the invocation directory
        XmlFileIO writer = new XmlFileIO(performaceProperties);
        writer.writeFile(new File(workingDir, message.getContextId() + "-performance-data.xml"));        
    }
    
    /** Execute the service */
    public void runService() {
        boolean iosClosed = false;
        Exception executeException = null;
        boolean reportProgress;
        long totalBytesToStream = 0;
        long bytesStreamed = 0;

        int chunkSize = message.getProperties().intValue("StreamingChunkSize", 1000);
        int reportInterval = message.getProperties().intValue("ProgressUpdateInterval", 30);
        long lastReportTime = System.currentTimeMillis();
        long nextReportTime = lastReportTime + (reportInterval * 1000);

        try {
            // Load the global properties into this service
            service.loadGlobalProperties();
            
            // Set the streaming parameters of the service
            service.streamInChunksOf(chunkSize);

            // Set up any data sets so that they can be read in chunks if necessary
            service.initialiseIOs();

            // Propagate metadata through service
            service.propagateMetadata();
            
            // Should the service report streaming progress
            if(service.containsStreamedInput()){
                totalBytesToStream = service.getTotalBytesToStream();
                if(totalBytesToStream>0){
                    reportProgress = true;
                } else {
                    reportProgress = false;
                }
            } else {
                reportProgress = false;
            }

            // Set up the sever to expect progress reports
            /*
            if(wfApiLink!=null){
                if(reportProgress){
                    try {
                        wfApiLink.setCurrentBlockStreamingProgress(invocationId, contextId, totalBytesToStream, 0);
                    } catch (Exception e){
                        System.out.println("Error reporting initial progress: " + e.getMessage());
                    }
                } else {
                    try {
                        wfApiLink.setCurrentBlockStreamingProgress(invocationId, contextId, 0, 0);
                    } catch (Exception e){
                        System.out.println("Error reporting initial progress: " + e.getMessage());
                    }
                }
            }
             */
            
            // Tell the service that the execution process is about to start
            service.executionAboutToStart();

            // Wait for a connection
            boolean externalIOsConnected = false;
            if(service instanceof CloudDataProcessorService){
                CloudDataProcessorService svc = (CloudDataProcessorService)service;
                if(svc.isExternalIOConnectionSupported()){
                    DataProcessorCallMessage msg = svc.getCallMessage();
                    if(msg.getProperties().booleanValue("WaitForDebugConnection", false)){
                        svc.setErrorHandlingDeferred(true);
                        try {
                            service.createApiLink().setWorkflowStatus(invocationId, WorkflowInvocationFolder.INVOCATION_WAITING_FOR_DEBUGGER);
                        } catch(Exception e){
                            System.out.println("Error setting workflow status to waiting for debugger");
                        }
                        svc.prepareForDebugging();
                        externalIOsConnected = ioServerThread.waitForClient(msg.getProperties().intValue("DebugConnectionTimeout", 120));
                    }
                }
            }
            
            // Run the service
            try {
                if(externalIOsConnected){
                    ArrayList<String> commands = new ArrayList<>();
                    commands.add("_++EXEC");
                    commands.add("_++QUIT");
                    commands.add("_++TERM");
                    String receivedCommand = ioServerThread.waitForCommand(commands);
                    if(receivedCommand!=null){
                        if(receivedCommand.equals("_++QUIT")){
                            // Quit the debugger
                            ioServerThread.sendMessageToAllClients("Debugger session exited");
                            ioServerThread.closeAllClients();

                        } else if(receivedCommand.equals("_++TERM")){
                            // Stop the workflow
                            ioServerThread.sendMessageToAllClients("Block execution terminated by debugger");
                            ioServerThread.closeAllClients();
                            throw new Exception("Debugger terminated block execution");
                            
                        } else if(receivedCommand.equals("_++EXEC")){
                            // Executing the block code
                            ioServerThread.sendMessageToAllClients("Executing block code...");
                        }
                    }       
                }                
                
                while(!service.isStreamingFinished()){
                    service.execute();
                    if(reportProgress){
                        bytesStreamed = service.getTotalBytesStreamed();
                        if(System.currentTimeMillis()>=nextReportTime){
                            try {
                                service.createApiLink().setCurrentBlockStreamingProcessAsync(invocationId, contextId, totalBytesToStream, bytesStreamed);
                            } catch (Exception e){
                                System.out.println("Error notifying streaming progress: " + e.getMessage());
                            }
                            lastReportTime = System.currentTimeMillis();
                            nextReportTime = lastReportTime + (reportInterval * 1000);
                        }
                        
                    }
                }
            } catch (Exception e){
                executeException = e;
            }

            // Wait for all listeners to quit
            if(externalIOsConnected){
                ioServerThread.sendMessageToAllClients("Block execution code finished");
                ioServerThread.waitForClientsToDisconnect();
                if(service instanceof CloudDataProcessorService){
                    ((CloudDataProcessorService)service).setErrorHandlingDeferred(false);
                    ((CloudDataProcessorService)service).undoDebugPreparations();
                }
                try {
                    service.createApiLink().setWorkflowStatus(invocationId, WorkflowInvocationFolder.INVOCATION_RUNNING);                    
                } catch(Exception e){
                    System.out.println("Error setting workflow status to running");
                }          
            }
            
            // Tell the service that all of the data has been processed
            service.allDataProcessed();
            
            // Close all the service IOs if possible
            service.closeIOs();
            iosClosed = true;

            // Save the global properties
            service.saveGlobalProperties();
            
            boolean persistData = message.getProperties().booleanValue("persistData", false);
            if(persistData)
            {
              service.persistDataToServer();
            }

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
            
            // Send an Ok message if execution gets here
            service.sendResponseMessage();

        } catch (Exception e){
            service.sendErrorResponseMessage(e.getMessage());
        } catch (Throwable t){
            service.sendErrorResponseMessage("Runtime error: " + t.getMessage());
        } finally {
            if(!iosClosed){
                service.closeIOs();
            }
        }
        
        if(flusherThread!=null){
            flusherThread.setStopFlag(true);
        }
    }
    
    /** Ping message send thread */
    /*
    private class HeartbeatSendThread extends Thread {
        int interval;
        String id;
        
        public HeartbeatSendThread(int interval, String id) {
            super("Heartbeat Thread");
            setDaemon(true);
            this.interval = interval;
            this.id = id;
        }

        @Override
        public void run() {
            System.out.println("Started heartbeat thread");
            try {
                DatagramSocket s = new DatagramSocket();
                InetAddress address = InetAddress.getByName("localhost");
                byte[] data = id.getBytes();
                DatagramPacket p;
                
                while(true){
                    try {
                        p = new DatagramPacket(data, data.length, address, 8888);
                        s.send(p);
                    } catch (Exception e){
                        System.out.println("Error sending heartbeat packet: " + e.getMessage());
                    }
                    
                    try {
                        Thread.sleep(interval);
                    } catch (Exception e){}
                }
                
            } catch (Exception e){
                System.err.println("Error starting heartbeat thread: "+ e.getMessage());
                System.exit(SETUP_ERROR);
            }
            
        }
    }
    */

    /** Thread to flush the System.out and System.err streams */
    private class OutputFlusherThread extends Thread {
        private volatile boolean stopFlag = false;
        private int interval;
        
        public OutputFlusherThread(int interval) {
            setDaemon(true);
            this.interval = interval;
        }

        @Override
        public void run() {
            while(!stopFlag){
                try {
                    Thread.sleep(interval);
                } catch (Exception e){
                }
                
                try {
                    System.out.flush();
                } catch(Exception e){}
                
                try {
                    System.err.flush();
                } catch(Exception e){}
            }
        }

        public synchronized void setStopFlag(boolean stopFlag){
            this.stopFlag = stopFlag;
        }
    }
    
    /** Thread to handle server socket connections */
    private class ServerSocketListenerThread extends Thread {
        /** Server socket to listen for STD IO connections */
        private ServerSocket ioServer;        
        
        /** List of threads */
        private ArrayList<ServerSocketThread> threads = new ArrayList<>();
        
        private volatile boolean run = true;
        
        /** Maximum number of debug connections. Can't see any reason why this would be more than one ever */
        private int maxDebugConnections = 1;
        
        /** Commands being waited for */
        private ArrayList<String> waitCommands = new ArrayList<>();
        
        /** Is this listener waiting for a command */
        private volatile boolean waitingForCommand = false;
        
        /** Last recognised command */
        private String lastRecognisedCommand = null;
        
        public ServerSocketListenerThread() throws Exception {
            setDaemon(true);
            ioServer = new ServerSocket(0);
            
            File messageFile = new File(workingDir, contextId + "-port.dat");
            PrintWriter writer = new PrintWriter(messageFile);
            writer.println(ioServer.getLocalPort());
            writer.flush();
            writer.close();
        }

        @Override
        public void run() {
            while(run){
                try {                    
                    Socket s = ioServer.accept();
                    if(threads.size()<maxDebugConnections){
                        ServerSocketThread th = new ServerSocketThread(this, s);
                        threads.add(th);
                        th.start();
                    } else {
                        s.close();
                    }
                    
                } catch (Exception e){
                    System.out.println("Error in server socket acceptor thread: " + e.getMessage());
                }
            }
        }
        
        public void removeServerThread(ServerSocketThread th){
            threads.remove(th);
        }
        
        /** Number of connected clients */
        public int clientCount(){
            return threads.size();
        }
        
        /** Wait for a client to connect for a specified number of seconds */
        public boolean waitForClient(int timeoutInSeconds){
            long time = System.currentTimeMillis() + (timeoutInSeconds * 1000);

            while(System.currentTimeMillis()<time){
                if(clientCount()>0){
                    return true;
                }
                try {
                    Thread.sleep(100);
                } catch (Exception e){}
            }
            
            return false;
        }
        
        public boolean testCommand(String command){
            if(waitCommands.contains(command)){
                lastRecognisedCommand = command;
                waitingForCommand = false;
                return true;
            } else {
                return false;
            }
        }
        
        /** Wait for all of the clients to disconnect */
        public void waitForClientsToDisconnect(){
            // Also wait for the QUIT command
            lastRecognisedCommand = null;
            ArrayList<String> cmds = new ArrayList<>();
            cmds.add("_++QUIT");
            waitCommands = cmds;
            waitingForCommand = true;
            while(clientCount()>0 && waitingForCommand==true){
                try {
                    Thread.sleep(100); 
                } catch (Exception e){}
            }
        }
        
        /** Wait for one of several commands. This method returns true if one of the
         commands was accepted */
        public String waitForCommand(ArrayList<String> commands){
            waitCommands = commands;
            lastRecognisedCommand = null;
            waitingForCommand = true;
            
            while(waitingForCommand && run && threads.size()>0){
                try {
                    Thread.sleep(100);
                } catch (Exception e){}
            }
            return lastRecognisedCommand;
        }
        
        /** Close all of the cliends */
        public void closeAllClients(){
            Iterator<ServerSocketThread> i = threads.iterator();
            while(i.hasNext()){
                i.next().close();
            }
        }
        
        /** Send a message to all of the clients */
        public void sendMessageToAllClients(String message){
            for(int i=0;i<threads.size();i++){
                threads.get(i).sendMessage(message);
            }
        }
    }

    /** Thread to communicate with a single socket */
    private class ServerSocketThread extends Thread {
        /** Communications socket */ 
        private Socket s;

        /** Parent listener thread */
        private ServerSocketListenerThread parent;
        
        public ServerSocketThread(ServerSocketListenerThread parent, Socket s) {
            setDaemon(true);
            this.parent = parent;
            this.s = s;
        }

        private void setup() throws IOException {
            errStreamDiverter.addExtraStream(s.getOutputStream());
            outStreamDiverter.addExtraStream(s.getOutputStream());
        }
        
        public void close(){
            if(s!=null){
                try {
                    s.close();
                } catch (Exception e){
                    System.out.println("Error closing debug socket: " + e.getMessage());
                }
            }
        }
        
        public void sendMessage(String message){
            try {
                outStreamDiverter.write((message +"\n").getBytes());
                outStreamDiverter.flush();                    
            } catch (Exception e){
                System.out.println("Error sending message to debugger: " + e.getMessage());
            }
        }
        
        @Override
        public void run() {
            try {
                setup();
                sendMessage("Debugger connected:");
                sendMessage("-------- ----------");
                sendMessage("");
                sendMessage("Click: CLEAR to clear the debug window");
                sendMessage("       EXEC to execute the block and stay in the debugger");
                sendMessage("       QUIT to finish the debug session and execute the block");
                sendMessage("       KILL to finish the debug session and terminate the workflow");
                sendMessage("");
                
            } catch (Exception e){
                parent.removeServerThread(this);
                System.out.println("Error setting up debugger socket: " + e.getMessage());
            }
            
            String line;
            InputStream inStream = null;
            BufferedReader reader = null;
            
            try {
                inStream = s.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inStream));
            } catch (Exception e){
                parent.removeServerThread(this);
                System.out.println("Error setting up debugger socket streams: " + e.getMessage());
            }
            
            try {
                if(service instanceof CloudDataProcessorService){
                    if(reader!=null){
                        line = reader.readLine();
                        while(line!=null){
                            if(line.startsWith("_++")){
                                if(!parent.testCommand(line)){
                                    // Only send the command if it wasn't one that we were waiting for
                                    ((CloudDataProcessorService)service).sendCommand(line);
                                }

                            } else {
                                // Send anyway
                                if(!parent.testCommand(line)){
                                    ((CloudDataProcessorService)service).sendCommand(line);
                                }
                            }
                            line = reader.readLine();
                        }
                    }
                }
            } catch (Exception e){
                parent.removeServerThread(this);
                System.out.println("Error reading from debugger input stream: " + e.getMessage());
            }   
            parent.removeServerThread(this);
        }
    }    
}