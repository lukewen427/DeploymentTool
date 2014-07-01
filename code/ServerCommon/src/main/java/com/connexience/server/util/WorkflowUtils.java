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
package com.connexience.server.util;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.workflow.WorkflowInvocationFolder;
import com.connexience.server.model.workflow.control.IWorkflowEngine;
import com.connexience.server.model.workflow.control.IWorkflowEngineControl;
import com.connexience.server.model.workflow.control.WorkflowEngineRecord;
import org.apache.log4j.Logger;

/**
 * Utilites to deal with workflows
 * @author hugo
 */
public class WorkflowUtils {
    private static Logger logger = Logger.getLogger(WorkflowUtils.class);
    
    public static IWorkflowEngineControl connectToWorkflowEngine(Ticket ticket, WorkflowEngineRecord engine) throws Exception {
        return connectToWorkflowEngine(ticket, engine.getEngineId());
    }
    
    public static IWorkflowEngineControl connectToWorkflowEngine(Ticket ticket, WorkflowInvocationFolder invocation) throws Exception {
        return connectToWorkflowEngine(ticket, invocation.getEngineId());
    }
    
    /** Connect to a remote invocation. This method checks the host id property of the invocation. If it starts with IP: it is a direct connection. Otherwise the
     * ID will be looked up in the service database */
    public static IWorkflowEngineControl connectToWorkflowEngine(Ticket ticket, String engineId) throws Exception {
        if(engineId!=null){
            if(engineId.startsWith("IP:")){
                // Engine is running standalone
                int index = engineId.indexOf(":");
                if(index!=-1){
                    String engineIp = engineId.substring(index + 1);
                    IWorkflowEngine engine = (IWorkflowEngine)RegistryUtil.lookup(engineIp, "CloudWorkflowEngine");
                    return engine.openControlConnection();
                } else {
                    throw new Exception("Error parsing workflow engine IP address from engine ID");
                }
            } else {
                // Engine is running on a service host
                throw new ConnexienceException("Service Host Machines no longer supported");
            }

        } else {
            throw new ConnexienceException("Cannot identify machine hosting workflow");
        }        
    }    
}