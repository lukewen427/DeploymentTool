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

import com.connexience.server.ConnexienceException;
import com.connexience.server.ejb.util.WorkflowEJBLocator;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.workflow.WorkflowInvocationFolder;
import com.connexience.server.util.WorkflowUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class contains a list of debug client references
 * @author hugo
 */
public class WorkflowDebugClientList implements Runnable {
    private HashMap<String,IWorkflowDebugClient> clients = new HashMap<>();
    private Thread checkThread;
    
    public WorkflowDebugClientList() {
        checkThread = new Thread(this);
        checkThread.setDaemon(true);
        checkThread.start();
    }
    
    /** Open a debug client to a workflow invocation */
    public IWorkflowDebugClient getDebugConnection(Ticket ticket, String invocationId, String contextId) throws ConnexienceException {
        IWorkflowDebugClient debugger = null;
        WorkflowInvocationFolder invocation = WorkflowEJBLocator.lookupWorkflowManagementBean().getInvocationFolder(ticket, invocationId);
        
        if(!clients.containsKey(invocationId + "-" + contextId)){
            if(invocation!=null){
                if(invocation.getInvocationStatus()==WorkflowInvocationFolder.INVOCATION_WAITING_FOR_DEBUGGER){
                    IWorkflowEngineControl control = null;
                    try {
                        control = WorkflowUtils.connectToWorkflowEngine(ticket, invocation);
                    } catch (Exception e){
                        throw new ConnexienceException("Error connecting to workflow engine: " + e.getMessage(), e);
                    }


                    try {
                        debugger = control.openDebugger(invocationId, contextId);
                    } catch (Exception e){
                        throw new ConnexienceException("Error opening debug connection", e);
                    }
                    clients.put(invocationId + "-" + contextId, debugger);
                    return debugger;
                } else {
                    throw new ConnexienceException("Only workflows that are waiting for a debugger can be debugged");
                }
            } else {
                throw new ConnexienceException("No such invocation");
            }
        } else {
            debugger = clients.get(invocationId + "-" + contextId);
        }
        
        if(debugger!=null){
            if(invocation.getInvocationStatus()==WorkflowInvocationFolder.INVOCATION_WAITING_FOR_DEBUGGER){
                return debugger;
            } else {
                removeDebugConnection(ticket, invocationId, contextId);
                throw new ConnexienceException("Workflow invocation finished");
            }
        } else {
            throw new ConnexienceException("Cannot obtain debugger");
        }
    }
    
    /** Remove a debugger */
    public void removeDebugConnection(Ticket ticket, String invocationId, String contextId) throws ConnexienceException {
        if(clients.containsKey(invocationId + "-" + contextId)){
            IWorkflowDebugClient client = null;
            try {
                client = clients.get(invocationId + "-" + contextId);
                client.close();
            } catch (Exception e){
                throw new ConnexienceException("Error closing debug connection: " + e.getMessage(), e);
            } finally {
                client = null;
                clients.remove(invocationId + "-" + contextId);
            }
        }
    }

    @Override
    public void run() {
        boolean run = true;
        while(run){
            Iterator<IWorkflowDebugClient> i = clients.values().iterator();
            ArrayList<IWorkflowDebugClient> clientsToRemove = new ArrayList<>();
            IWorkflowDebugClient client;
            while(i.hasNext()){
                client = i.next();
                try {
                    if(!client.isConnected()){
                        clientsToRemove.add(client);
                    }
                } catch (Exception e){
                    clientsToRemove.add(client);
                }
            }
            
            for(IWorkflowDebugClient c : clientsToRemove){
                clients.values().remove(c);
            }
            
            try {
                Thread.sleep(10000);
            } catch(InterruptedException e){
                run = false;
            }
        }
    }
    
    
}