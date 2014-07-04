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
package com.connexience.server.workflow.cloud.rmi;

import com.connexience.server.model.security.*;
import com.connexience.server.model.workflow.control.*;
import com.connexience.server.util.StorageUtils;
import com.connexience.server.workflow.engine.*;
import com.connexience.server.workflow.cloud.execution.*;
import com.connexience.server.workflow.util.ZipUtils;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.io.*;
import org.apache.log4j.*;

/**
 * This class provides a control connection to the cloud workflow engine
 * @author nhgh
 */
public class WorkflowEngineControlImpl extends UnicastRemoteObject implements IWorkflowEngineControl
{
    /**
     * Class version UID.
     * 
     * Please increment this value whenever your changes may cause 
     * incompatibility with the previous version of this class. If unsure, ask 
     * one of the core development team or read:
     *   http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     *   http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 1L;


    private static Logger logger = Logger.getLogger(WorkflowEngineControlImpl.class);
    
    /** Workflow engine object */
    private CloudWorkflowExecutionEngine engine;

    /** Ticket of the user controlling this connection */
    //private Ticket ticket;

    public WorkflowEngineControlImpl(CloudWorkflowExecutionEngine engine, Ticket ticket) throws RemoteException {
        this.engine = engine;
        //this.ticket = ticket;
    }

    public WorkflowEngineControlImpl(CloudWorkflowExecutionEngine engine) throws RemoteException {
        this.engine = engine;
        //this.ticket = null;
    }

    /** List the invocations running in the engine */
    public ArrayList<WorkflowInvocationRecord> getRunningInvocations() throws RemoteException {
        ArrayList<WorkflowInvocationRecord> invocations = new ArrayList<>();

        Enumeration<WorkflowInvocation> i = engine.getExecutionEngine().listInvocations();
        WorkflowInvocationRecord record;
        WorkflowInvocation invocation;

        while(i.hasMoreElements()){
            invocation = i.nextElement();
            record = new WorkflowInvocationRecord();
            record.setInvocationId(invocation.getInvocationId());
            record.setStartTime(invocation.getStartTime());
            record.setRunning(invocation.isRunning());
            record.setPid(invocation.getPid());
            record.setWorkflowName(invocation.getWorkflowName());
            invocations.add(record);
        }
        return invocations;
    }

    public WorkflowInvocationRecord getInvocation(String invocationId) throws RemoteException {
        WorkflowInvocation invocation = engine.getExecutionEngine().getInvocation(invocationId);
        if(invocation!=null){
            WorkflowInvocationRecord record = new WorkflowInvocationRecord();
            record.setInvocationId(invocation.getInvocationId());
            record.setStartTime(invocation.getStartTime());
            record.setRunning(invocation.isRunning());
            record.setPid(invocation.getPid());
            record.setContextId(invocation.getCurrentItem().getMessage().getContextId());
            record.setWorkflowName(invocation.getWorkflowName());
            
            // Try and find the debug port of the invocation
            GlobalDataSource ds = invocation.getDataSource().getParent();
            if(ds.allowsFileSystemAccess()){
                try {
                    String invocationDir = ds.getStorageDirectory(invocationId);
                    String contextId = invocation.getCurrentContextId();
                    File invocationPortFile = new File(invocationDir, contextId + "-port.dat");
                    int portNumber = Integer.parseInt(ZipUtils.readFirstLineOfFile(invocationPortFile));
                    record.setCurrentBlockDebugPort(portNumber);
                   
                } catch (Exception e){
                    record.setCurrentBlockDebugPort(-1);
                }
                
            } else {
                record.setCurrentBlockDebugPort(-1);
            }
            
            
            return record;
        } else {
            return null;
        }
    }

    
    /** Shutdown the workflow engine */
    public void shutdown() throws RemoteException {
        engine.shutdown();
    }

    public void terminateInvocation(String invocationId) throws RemoteException {
        engine.getExecutionEngine().killInvocation(invocationId);
    }

    public void terminateInvocation(long pid) throws RemoteException {
        WorkflowInvocation invocation = engine.getExecutionEngine().findInvocationByPid(pid);
        if(invocation!=null){
            invocation.kill();
        }
    }

    @Override
    public void flushLibrary() throws RemoteException {
        try {
            engine.getServiceLibrary().flushLibrary();
        } catch (Exception e){
            throw new RemoteException("Error flushing library: " + e.getMessage());
        }
    }

    @Override
    public WorkflowEngineStatusData getStatusData() throws RemoteException {
        WorkflowEngineStatusData data = engine.getEngineStatus();
        data.setInvocations(getRunningInvocations());
        return data;
    }

    @Override
    public void connectJms() throws RemoteException {
       engine.getJmsListener().getAttacherThread().attach();
    }

    @Override
    public void disconnectJms() throws RemoteException {
        engine.getJmsListener().getAttacherThread().detach();
    }

    @Override
    public IWorkflowDebugClient openDebugger(String invocationId, String contextId) throws RemoteException {
        try {
            WorkflowInvocationRecord invocation = getInvocation(invocationId);
            if(invocation.getContextId().equals(contextId)){
                if(invocation.getCurrentBlockDebugPort()!=-1){
                    WorkflowDebugClientImpl debugger = new WorkflowDebugClientImpl(invocation);
                    logger.debug("Opened DEBUG connction. InvocationID=" + invocationId + " BlockID=" + contextId);
                    return debugger;
                } else {
                    throw new RemoteException("Current workflow step does not support debugging");
                }
            } else {
                throw new RemoteException("Specified context does not match the current workflow execution context");
            }
        } catch (RemoteException re){
            logger.error("Error opening debug connection", re);
            throw re;
        }
    }

    @Override
    public byte[] fetchStdOut(String invocationId, String contextId) throws RemoteException {
        WorkflowInvocation invocation = engine.getExecutionEngine().getInvocation(invocationId);
        if(invocation!=null){
            InvocationDataSource ds = invocation.getDataSource();
            File invocationDir = ds.getStorageDir();
            if(invocationDir.exists()){
                File stdOutFile = new File(invocationDir, "stdout-" + contextId);
                if(stdOutFile.exists()){
                    try {
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        ZipUtils.copyFileToOutputStream(stdOutFile, buffer);
                        buffer.flush();
                        return buffer.toByteArray();
                    } catch (IOException ioe){
                        return new String("IO Error reading std out file: " + ioe.getMessage()).getBytes();
                    }                    
                } else {
                    return new String("Output file does not exist").getBytes();
                }
                
            } else {
                return new String("Cannot find invocation directory").getBytes();
            }
        } else {
            return new String("No such invocation").getBytes();
        }
    }
}