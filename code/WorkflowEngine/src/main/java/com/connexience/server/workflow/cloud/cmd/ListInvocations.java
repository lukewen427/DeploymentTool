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
import java.util.*;
import java.text.*;

/**
 * This command lists the running invocations on the local server.
 * @author nhgh
 */
public class ListInvocations {
    public static void main(String[] args0){
        try {
            DateFormat format = DateFormat.getDateTimeInstance();
            IWorkflowEngine engine = (IWorkflowEngine)RegistryUtil.lookup("localhost", "CloudWorkflowEngine");
            IWorkflowEngineControl control = engine.openControlConnection();

            System.out.println("Running Invocations:");
            ArrayList<WorkflowInvocationRecord> invocations = control.getRunningInvocations();
            for(int i=0;i<invocations.size();i++){
                System.out.print(invocations.get(i).getPid() + "  ");
                System.out.print(format.format(invocations.get(i).getStartTime()));
                System.out.print("  ");
                System.out.println(invocations.get(i).getInvocationId());
            }

        } catch (Exception e){
            System.out.println("Error stopping server: " + e.getMessage());
        }
    }
}
