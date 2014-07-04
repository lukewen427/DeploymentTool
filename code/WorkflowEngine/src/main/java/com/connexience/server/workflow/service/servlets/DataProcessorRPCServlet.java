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
package com.connexience.server.workflow.service.servlets;

import com.connexience.server.workflow.rpc.*;
import com.connexience.server.workflow.service.*;
import com.connexience.server.util.*;

import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;

/**
 * This servlet can contains a number of data processor services that can be invoked
 * by the workflow to operate upon data. It uses the servlet POST method to obtain
 * an input DataProcessorCallMessage that contains details of the service that
 * should be invoked and the data to return. This servlet extends the basic RPC 
 * servlet and routes data processor call messages to the appropriate listeners.
 * @author hugo
 */
public class DataProcessorRPCServlet extends RPCServlet implements CallHandler, DataProcessorResponseMessageHandler {
	private static final long serialVersionUID = 1L;

	/** List of service routines */
    private Hashtable<String,DataProcessorDefinition> serviceList = new Hashtable<>();
    
    /** Global handler that takes all requests. This is used for the interpreted services */
    private Class<?> globalProcessorClass = null;
    
    /** Handler for data processor messages */
    public DataProcessorRPCServlet() {
        super();        
        addHandler(this);
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    /** Set the global processor. If present this intercepts all call messages */
    public void setGlobalProcessorClass(Class<?> globalProcessorClass){
        this.globalProcessorClass = globalProcessorClass;
    }
    
    /** Get the global processor. If present this intercepts all call messages */
    public Class<?> getGlobalProcessorClass(){
        return globalProcessorClass;
    }
        
    /** Handle a CallObject */
    public void processCallMessage(CallObject call) throws Exception {
        final DataProcessorCallMessage msg = (DataProcessorCallMessage)call.getCallArguments().xmlStorableValue("CallMessage");
        final DataProcessorResponseMessageHandler responseHandler = this;
        
        // Find the correct thread pool for running the service in
        //String threadPoolName = PropertyUtils.getSystemProperty("Workflow", "thread.pool", "thread-pool-1");
        
        String routineName = msg.getServiceRoutine();
        if(routineName.equalsIgnoreCase("_list")){
            // List all of the defined services as Xml Strings
            listServices(call);

        } else if(serviceList.containsKey(routineName) || (globalProcessorClass!=null)){
            
            // Find the correct service
            DataProcessorService tempService;
            if(globalProcessorClass!=null){
                tempService = instantiateGlobalService();
            } else {
                tempService = instantiateService(routineName);
            }
            
            final DataProcessorService service = tempService;

            //WorkManager manager = WorkManagerFactory.getWorkManager(threadPoolName);

            Thread runThread = new Thread() {
                /** Server wants work to stop */
                public void release() {
                    DataProcessorResponseMessage response = new DataProcessorResponseMessage();
                    response.setContextId(msg.getContextId());
                    response.setInvocationId(msg.getInvocationId());
                    response.setStatus(DataProcessorResponseMessage.SERVICE_EXECUTION_ERROR);
                    response.setStatusMessage("Server terminated processing");
                    try {
                        responseHandler.sendResponseMessage(response);
                    } catch (Exception e){
                        System.out.println("Error sending response message: " + e.getMessage());
                    }
                }


                /** Run the job */
                public void run() {
                    service.setCallMessage(msg);
                    service.setResponseDestination(responseHandler);
                    boolean iosClosed = false;
                    Exception executeException = null;

                    int chunkSize = msg.getProperties().intValue("StreamingChunkSize", 1000);

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

                        // If there was an execute exception, throw it now that everything has been cleaned up
                        if(executeException!=null){
                            throw executeException;
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
                }
            };

            
            runThread.start();
            //manager.scheduleWork(callWork);

            
        } else {
            throw new Exception("No such service: " + routineName);
        }
    }

    /** Send a response message back to the calling server */
    public void sendResponseMessage(DataProcessorResponseMessage message) throws DataProcessorException {
        RPCClient client = new RPCClient(PropertyUtils.getSystemProperty("Workflow", "response.url", "http://inkspot-as1:8080/esc/WorkflowServlet"));
        CallInvocationListener listener = new CallInvocationListener(){

            public void callFailed(CallObject call) {
                System.out.println("Could not send response message");
            }

            public void callSucceeded(CallObject call) {
            }
        };
        CallObject call = new CallObject("processResponse");
        try {
            call.getCallArguments().add("ResponseMessage", message);
        } catch (Exception e){
            throw new DataProcessorException("Error adding response to RPC message");
        }
        
        try {
            client.asyncCall(call, listener);
        } catch (Exception e){
            throw new DataProcessorException("Error sending response message: " + e.getMessage());
            
        }
    }
    
    /** Add a data processor service */
    public void addDataProcessorService(DataProcessorDefinition definition){
        serviceList.put(definition.getServiceRoutine(), definition);
    }

    /** Instantiate a data processor service from its class definition */
    private DataProcessorService instantiateService(String routineName) throws DataProcessorException {
        if(serviceList.containsKey(routineName)){
            return serviceList.get(routineName).instantiateService();
        } else {
            throw new DataProcessorException("Service: " + routineName + " is not registered at this endpoint");
        }
    }

    /** Instantiate the global processor service */
    private DataProcessorService instantiateGlobalService() throws DataProcessorException {
        if(globalProcessorClass!=null){
            try {
                return (DataProcessorService)globalProcessorClass.newInstance();
            } catch (Exception e){
                throw new DataProcessorException("Error instantiating global processor: " + e.getMessage());
            }
        } else {
            throw new DataProcessorException("No global processor has been defined");
        }
    }

    /** Produce a service list */
    private void listServices(CallObject call) throws DataProcessorException {
        call.setStatus(CallObject.CALL_EXECUTED_OK);
        call.setStatusMessage("");

        Enumeration<String> serviceNames = serviceList.keys();
        DataProcessorService service;
        String serviceName;
        String serviceXml;
        int serviceCount = 0;

        while(serviceNames.hasMoreElements()){
            serviceName = serviceNames.nextElement();
            service = instantiateService(serviceName);
            serviceXml = service.getServiceXml();
            if(serviceXml!=null){
                call.getReturnArguments().add("Service" + serviceCount, serviceXml);
                serviceCount++;
            }
        }
        call.getReturnArguments().add("ServiceCount", serviceCount);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            // For an empty request, list the service names
            if(request.getParameter("NAME")==null){
                Enumeration<String> services = serviceList.keys();
                while(services.hasMoreElements()){
                    out.println(services.nextElement());
                }
            } else {
                // If there is a name parameter, list the actual services
                String name = request.getParameter("NAME");
                if(serviceList.containsKey(name)){
                    DataProcessorService service = instantiateService(name);
                    String serviceXml = service.getServiceXml();
                    out.println(serviceXml);
                }
            }

        } catch (Exception e){
            throw new ServletException("Error listing services: " + e.getMessage());
        } finally {
            out.close();
        }
    }
}
