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
package com.connexience.server.model.workflow.control;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This interface represents a control instance to a workflow engine
 * @author nhgh
 */
public interface IWorkflowEngineControl extends Remote {
    /** Get the running invocations for the logged on user */
    public ArrayList<WorkflowInvocationRecord> getRunningInvocations() throws RemoteException;

    /** Get a workflow invocation record */
    public WorkflowInvocationRecord getInvocation(String invocationId) throws RemoteException;
    
    /** Get the current status of the engine */
    public WorkflowEngineStatusData getStatusData() throws RemoteException;
    
    /** Shutdown the workflow engine */
    public void shutdown() throws RemoteException;

    /** Terminate an invocation */
    public void terminateInvocation(String invocationId) throws RemoteException;

    /** Flush the workflow library */
    public void flushLibrary() throws RemoteException;

    /** Disconnect the JMS queue */
    public void disconnectJms() throws RemoteException;
    
    /** Connect the JMS queue */
    public void connectJms() throws RemoteException;
    
    /** Open a debug client to a workflow invocation */
    public IWorkflowDebugClient openDebugger(String invocationId, String contextId) throws RemoteException;
    
    /** Fetch the standard output for an invocation */
    public byte[] fetchStdOut(String invocationId, String contextId) throws RemoteException;
}