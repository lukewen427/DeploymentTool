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
import com.connexience.server.model.workflow.WorkflowInvocationFolder;
import com.connexience.server.model.workflow.notification.WorkflowLock;
import com.connexience.server.model.workflow.notification.WorkflowLockMember;

import javax.ejb.Remote;
import java.util.List;

/**
 * This interface defines the functionality of the workflow locking bean that
 * allows workflows to kick off subworkflows and then wait for completion.
 * @author hugo
 */
@Remote
public interface WorkflowLockRemote {
    /** Create a workflow lock */
    public WorkflowLock createWorkflowLock(Ticket ticket, WorkflowInvocationFolder parentWorkflow) throws ConnexienceException;

    /** Save changes to a workflow lock */
    public WorkflowLock saveWorkflowLock(Ticket ticket, WorkflowLock lock) throws ConnexienceException;

    /** Remove a workflow lock */
    public void removeWorkflowLock(Ticket ticket, long lockId) throws ConnexienceException;

    /** Get a workflow lock by id */
    public WorkflowLock getLock(Ticket ticket, long lockId) throws ConnexienceException;
    
    /** Attach a workflow invocation to a lock */
    public WorkflowLockMember attachInvocationToLock(Ticket ticket, long lockId, WorkflowInvocationFolder invocation) throws ConnexienceException;

    /** Update the status of a lock member */
    public void updateLockMember(Ticket ticket, WorkflowInvocationFolder invocation) throws ConnexienceException;

    /** Check to see if a lock can be released */
    public boolean isLockFinished(Ticket ticket, long lockId) throws ConnexienceException;

    /** How many failed runs are there in a lock */
    public int getNumberOfFailedInvocationsInLock(Ticket ticket, long lockId) throws ConnexienceException;

    /** How many invocations are left in a lock */
    public int getNumberOfRemainingInvocationsInLock(Ticket ticket, long lockId) throws ConnexienceException;
    
    /** Attempt to notify the lock holder that a lock has completed */
    public void notifyLockHolderOfCompletion(Ticket ticket, long lockId) throws ConnexienceException;

    /** Send notification messages for all finished locks */
    public void notifyAllFinishedLockHolders(Ticket ticket) throws ConnexienceException;
    
    /** Get a list of the members of a workflow lock */
    public List getLockMembers(Ticket ticket, long lockId) throws ConnexienceException;

    /** Get a list of all locks */
    public List listAllLocks(Ticket ticket) throws ConnexienceException;

    /** Get a list of locks for a user */
    public List listUserLocks(Ticket ticket, String userId) throws ConnexienceException;

    /** Get a list of locks for an invocation */
    public List listInvocationLocks(Ticket ticket, String invocationId) throws ConnexienceException;
    
    /** Check to see if all of the invocations in a lock have completed. If they have, notify
     * the holder. If a lock is set to pause on errors, this method will not deliver a completion
     * message for a completed workflow that has any failed subworkflows unless the forceDelivery
     * flag is set to true. */
    public void notifyLockHolderIfComplete(Ticket ticket, long lockId, boolean forceDelivery) throws ConnexienceException;
    
    /** Is a workflow invocation a lock member */
    public boolean isInvocationLockMember(Ticket ticket, String invocationId) throws ConnexienceException;
}