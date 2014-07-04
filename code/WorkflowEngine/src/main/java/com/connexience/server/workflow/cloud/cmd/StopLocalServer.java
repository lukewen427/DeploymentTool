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
package com.connexience.server.workflow.cloud.cmd;

import com.connexience.server.util.*;
import com.connexience.server.model.workflow.control.*;

/**
 * This command commects to the local RMI registry and stops the locally running
 * WorkflowCloud server.
 * @author nhgh
 */
public class StopLocalServer {
    public static void main(String[] args0){
        try {
            IWorkflowEngine engine = (IWorkflowEngine)RegistryUtil.lookup("localhost", "CloudWorkflowEngine");
            IWorkflowEngineControl control = engine.openControlConnection();
            //engine.shutdown();

            control.shutdown();
        } catch (Exception e){
            System.out.println("Error stopping server: " + e.getMessage());
        }
    }
}
