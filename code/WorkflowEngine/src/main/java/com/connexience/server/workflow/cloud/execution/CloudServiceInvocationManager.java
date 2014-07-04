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


import com.connexience.server.workflow.cloud.rmi.ExternalProcessStartNotifer;
import com.connexience.server.workflow.engine.cloud.*;
import com.connexience.server.workflow.service.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

//import java.io.*;
//import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.*;

/**
 * This class manages the various service invocations that are being executed
 * by the workflow engine.
 * @author hugo
 */
public class CloudServiceInvocationManager extends UnicastRemoteObject implements CloudDataProcessorMessageDestination, ExternalProcessStartNotifer {
    static Logger logger = Logger.getLogger(CloudServiceInvocationManager.class);
    
    /** Parent workflow engine */
    private CloudWorkflowExecutionEngine parent;

    /** List of service invocations */
    private ConcurrentHashMap<String,CloudServiceInvocation> serviceInvocations = new ConcurrentHashMap<>();

    /** Queue of service invocations waiting to be run. */
    private ConcurrentLinkedQueue<DataProcessorCallMessage> serviceQueue = new ConcurrentLinkedQueue<>();

    /** 
     * A sorted set of debug ports used by running blocks across all 
     * invocations.
     */
    private TreeSet<Integer> debugPortsInUse = new TreeSet<>();

    /**
     * A map from the service invocation to the debug port map to ease cleaning
     * the debugPortsInUse set. 
     */
    private HashMap<CloudServiceInvocation, Integer> debugPortMap = new HashMap<>();

    /** Working directory for library files. This defaults to user.home, but can be
     * changed if the basic jar files are somewhere else */
    private String workingDir = System.getProperty("user.dir");

    /** Are workflows executed in process */
    private boolean inProcessExecution = false;

    /** Maximum number of concurrent service invocations */
    private int maxConcurrentInvocations = 4;

    /** Flag to see if queue is being checked */
    private AtomicBoolean checkingQueue = new AtomicBoolean(false);
    
    /** Queue checking thread */
    private QueueCheckThread queueCheckThread;
    
    /** Invocation timeout checking thread */
    private TimeoutCheckThread timeoutCheckThread;
    
    /** Heartbeat message receiving thread */
    //private HeartbeatSocketThread heartbeatSocketThread;
    
    /** Heartbeat message checking thread */
    //private HeartbeatCheckThread heartbeatCheckThread;
    
    /** Interval to check for service timeouts */
    private int timeoutCheckInterval = 1000;
    
    /** Interval to check for service heartbeats */
    //private int heartbeatCheckInterval = 2000;
    
    /** Timeout for heartbeats */
    //private int heartbeatTimeoutInterval = 60000;
    
    public CloudServiceInvocationManager(CloudWorkflowExecutionEngine parent, int maxConcurrentInvocations) throws RemoteException {
        this.parent = parent;
        this.maxConcurrentInvocations = maxConcurrentInvocations;
        queueCheckThread = new QueueCheckThread(this);
        queueCheckThread.start();
        
        /*
        try {
            heartbeatSocketThread = new HeartbeatSocketThread();
            heartbeatSocketThread.start();
        } catch (Exception e){
            logger.error("Error starting invocation heartbeat thread: " + e.getMessage(), e);
        }
        
        heartbeatCheckThread = new HeartbeatCheckThread();
        heartbeatCheckThread.start();
         * 
         */
        
        timeoutCheckThread = new TimeoutCheckThread();
        timeoutCheckThread.start();
    }
    
    /** Process a call message */
    @Override
    public boolean postCallMessage(DataProcessorCallMessage message) throws DataProcessorException {
        logger.debug("Service message received. InvocationID=" + message.getInvocationId() + " BlockID=" + message.getContextId());
        serviceQueue.add(message);
        processQueue();
        return true;
    }

    /** Get the working directory */
    public String getWorkingDir(){
        return workingDir;
    }

    /** Set the working directory. This is the top level directory of the engine
     * and contains the required .jar files in the /lib subdirecory */
    public void setWorkingDir(String workingDir){
        this.workingDir = workingDir;
    }
    
    /** Get the parent workflow engine */
    public CloudWorkflowExecutionEngine getParentEngine(){
        return parent;
    }

    /**
     * Gets the first unused port starting from the given seed. The port is not 
     * to be meant free OS-wide but just not used by any other service 
     * invocations.
     * 
     * @param seed - a port to start looking from for free ports
     * @return a port unused by any other service invocations 
     */
    public Integer getFreeDebugPort(CloudServiceInvocation i, Integer seed) {
        synchronized (this) {
            // Get the first unused port
            while (debugPortsInUse.contains(seed))
                seed++;
            debugPortsInUse.add(seed);
            debugPortMap.put(i, seed);
        }
        return seed;
    }

    /**
     * Releases port reservation made by <code>getFreeDebugPort</code>. 
     * It is ok to call this method even if the given service invocation 
     * do not use debug port at all.
     * 
     * @param i - service invocation for which the release is attempted
     */
    private void freeDebugPort(CloudServiceInvocation i) {
        synchronized (this) {
            Integer port = debugPortMap.get(i);
            if (port != null) {
                debugPortMap.remove(i);
                debugPortsInUse.remove(port);
            }
        }
    }

    /** Should the services be run in the same process as the manager */
    public boolean isInProcessExecution(){
        return inProcessExecution;
    }

    /** Set whether services should be run in the same process as the manager */
    public void setInProcessExecution(boolean inProcessExecution){
        this.inProcessExecution = inProcessExecution;
    }

    
    /** Ping a service invocation by id */
    /* private void pingServiceInvocation(String id){
        if(serviceInvocations.contains(id)){
            CloudServiceInvocation invocation = serviceInvocations.get(id);
            invocation.updateLastPingTime();
        }
    } */
    
    /** Process a response message */
    @Override
    public boolean postResponseMessage(DataProcessorResponseMessage message) throws DataProcessorException {
        String id = message.getInvocationId() + ":" + message.getContextId();
        CloudServiceInvocation inv;
        
        try {
            // Remove the service invocation
            inv = serviceInvocations.remove(id);
            freeDebugPort(inv);

            // Delegate back to the parent to progress the workflow
            boolean result = parent.postResponseMessage(message);
            
            // Process the next element in the queue
            processQueue();
            
            return result;
            
        } catch (DataProcessorException e){
            // Make sure the invocation has been removed from the queue
            if(serviceInvocations.containsKey(id)){
                inv = serviceInvocations.remove(id);
                freeDebugPort(inv);
                processQueue();
            }
            throw e;
        }
    }

    /** Kill an invocation */
    @Override
    public void terminate(DataProcessorCallMessage message) throws DataProcessorException {
        logger.debug("Terminating invocation. InvocationID=" + message.getInvocationId());
        String id = message.getInvocationId() + ":" + message.getContextId();

        // First check to see if the message is in the service queue
        if(serviceQueue.contains(message)){
            // Remove it if present
            serviceQueue.remove(message);
        }

        // Check to see if the service is runnng
        if(serviceInvocations.containsKey(id)){
            CloudServiceInvocation invocation = serviceInvocations.get(id);
            invocation.kill();
        }
    }

    @Override
    public void processStarted(String invocationId, String contextId, long pid) throws RemoteException {
        logger.debug("External process started for: " + invocationId + " at " + contextId + " of PID=" + pid);
        String id = invocationId + ":" + contextId;
        if(serviceInvocations.containsKey(id)){
            CloudServiceInvocation invocation = serviceInvocations.get(id);
            if(invocation!=null){
                invocation.addExternalPID(pid);
            }
        }
    }

    /** Process the next item on the queue if there is an available slot */
    private synchronized void processQueue(){
        checkingQueue.set(true);
        if(serviceInvocations.size()<maxConcurrentInvocations){
            // There is an available invocation space
            
            if(serviceQueue.peek()!=null){
                // There is a job waiting
                try {
                    DataProcessorCallMessage message = serviceQueue.poll();
                    CloudServiceInvocation serviceInvocation = new CloudServiceInvocation(this, message.getInvocationId(), message, this, parent.getServiceLibrary());
                    String id = message.getInvocationId() + ":" + message.getContextId();
                    serviceInvocations.put(id, serviceInvocation);
                    serviceInvocation.start();

                } catch (Exception e){
                    logger.error("Error removing message from queue", e);
                }
            }
        }
        checkingQueue.set(false);
    }
        
    /** Sum the resident memory for all of the services */
    public synchronized long getTotalMaximumResidentMemory(){
        long sum = 0;
        Enumeration<CloudServiceInvocation> i = serviceInvocations.elements();
        while(i.hasMoreElements()){
            sum+=i.nextElement().getMaximumResidentMemory();
        }

        return sum;
    }
    
    /** Sum the resident total for all of the services */
    public synchronized long getTotalMaximumMemory(){
        long sum = 0;
        Enumeration<CloudServiceInvocation> i = serviceInvocations.elements();
        while(i.hasMoreElements()){
            sum+=i.nextElement().getMaximumMemorySize();
        }
        return sum;
    }    
    
    /** Get the queue length */
    public int getInvocationQueueLength(){
        return serviceQueue.size();
    }

    /** Get the number of current service calls running */
    public int getServiceCallCount(){
        return serviceInvocations.size();
    }

    public void setMaxConcurrentInvocations(int maxConcurrentInvocations) {
        this.maxConcurrentInvocations = maxConcurrentInvocations;
    }

    public int getMaxConcurrentInvocations() {
        return maxConcurrentInvocations;
    }

    private class QueueCheckThread extends Thread {
        CloudServiceInvocationManager manager;
        public QueueCheckThread(CloudServiceInvocationManager manager) {
            super("QueueCheckingThread");
            setDaemon(true);
            this.manager = manager;
        }
        
        public void run(){
            while(true){
                if(manager.checkingQueue.get()==false){
                    manager.processQueue();
                }
                try {
                    Thread.sleep(5000);
                } catch (Exception e){
                }
            }
        }
    }

    /*
    private class HeartbeatSocketThread extends Thread {
        private DatagramSocket socket;

        public HeartbeatSocketThread() throws IOException {
            super("Heartbeat listener");
            setDaemon(true);
            socket = new DatagramSocket(8888);
            logger.debug("Created heartbeat socket thread");
        }
        
        public void run(){
            logger.debug("Started heartbeat socket thread");
            byte[] buffer;
            DatagramPacket packet;
            String message;
            
            while(true){
                buffer = new byte[128];
                packet = new DatagramPacket(buffer, buffer.length);
                
                try {
                    socket.receive(packet);
                    message = new String(buffer, 0, packet.getLength());
                    pingServiceInvocation(message);
                } catch(Exception e){
                    logger.error("Error receiving heartbeat packet: " + e.getMessage(), e);
                }
            }
        }
        
    }
    
    private class HeartbeatCheckThread extends Thread {
        public HeartbeatCheckThread() {
            super("Heartbeat checker");
            setDaemon(true);
            logger.debug("Created heartbeat checker thread");
        }
        
        public void run(){
            logger.debug("Started heartbeat checker thread");
            Enumeration<CloudServiceInvocation> invocationList;
            ArrayList<CloudServiceInvocation> invocationsToKill = new ArrayList<CloudServiceInvocation>();
            CloudServiceInvocation invocation;
            long currentTime;
            
            while(true){
                currentTime = System.currentTimeMillis();
                invocationsToKill.clear();
                invocationList = serviceInvocations.elements();
                while(invocationList.hasMoreElements()){
                    invocation = invocationList.nextElement();
                    if(invocation.getLastPingTime() < (currentTime - heartbeatTimeoutInterval)){
                        invocationsToKill.add(invocation);
                    }
                }
                
                // Kill invocations
                for(int i=0;i<invocationsToKill.size();i++){
                    invocationsToKill.get(i).kill();
                }
                
                try {
                    Thread.sleep(heartbeatCheckInterval);
                } catch (Exception e){}
            }
        }
    }
    */

    private class TimeoutCheckThread extends Thread {
        public TimeoutCheckThread() {
            super("Timeout checker");
            setDaemon(true);
            logger.debug("Created timeout checker thread");
        }
        
        public void run(){
            logger.debug("Started timeout checker thread");
            Enumeration<CloudServiceInvocation> invocationList;
            ArrayList<CloudServiceInvocation> invocationsToKill = new ArrayList<>();
            CloudServiceInvocation invocation;
            long currentTime;
            
            while(true){
                currentTime = System.currentTimeMillis();
                invocationsToKill.clear();
                invocationList = serviceInvocations.elements();
                while(invocationList.hasMoreElements()){
                    invocation = invocationList.nextElement();
                    if(invocation.isTimeoutEnforced() && currentTime > invocation.getTimeoutTime()){
                        invocationsToKill.add(invocation);
                    }
                }
                
                // Kill invocations
                for(int i=0;i<invocationsToKill.size();i++){
                    invocationsToKill.get(i).timeoutKill();
                }
                
                try {
                    Thread.sleep(timeoutCheckInterval);
                } catch (Exception e){}
            }
        }
    }    
}