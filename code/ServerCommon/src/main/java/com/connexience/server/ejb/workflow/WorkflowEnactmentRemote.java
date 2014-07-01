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
package com.connexience.server.ejb.workflow;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.workflow.WorkflowInvocationMessage;

import javax.ejb.Remote;

/**
 * This interface defines the functionality of the workflow enactment bean. This
 * runs in its own application space and uses a different database to the
 * core server database.
 * @author nhgh
 */
@Remote
public interface WorkflowEnactmentRemote {
    /** Execute a workflow. The invocation ID is returned if the workflow started */
    public String startWorkflow(Ticket ticket, WorkflowInvocationMessage invocationMessage) throws ConnexienceException;

    /** Resubmit a workflow */
    public String resubmitWorkflow(Ticket ticket, String invocationId) throws ConnexienceException;
}