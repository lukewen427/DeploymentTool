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
package com.connexience.server.ejb.util;

import com.connexience.server.ConnexienceException;
import com.connexience.server.ejb.workflow.WorkflowEnactmentRemote;
import com.connexience.server.ejb.workflow.WorkflowLockRemote;
import com.connexience.server.ejb.workflow.WorkflowManagementRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * This bean provides utility methods for looking up the various workflow
 * management and execution beans.
 *
 * @author nhgh
 */
public abstract class WorkflowEJBLocator
{
  private static WorkflowEnactmentRemote wfEnactmentRemote = null;
  private static WorkflowManagementRemote wfManagementRemote = null;
  private static WorkflowLockRemote wfLockRemote = null;

  /**
   * Get hold of a workflow management bean
   */
  public static WorkflowManagementRemote lookupWorkflowManagementBean() throws ConnexienceException
  {
    try
    {
      if (wfManagementRemote == null)
      {
        Context c = new InitialContext();
        wfManagementRemote = (WorkflowManagementRemote) c.lookup("java:global/ejb/WorkflowManagementBean");
      }
      return wfManagementRemote;
    }
    catch (NamingException ne)
    {
      throw new ConnexienceException("Cannot locate workflow management bean: " + ne.getMessage());
    }
  }

  /**
   * Get hold of a workflow execution bean
   */
  public static WorkflowEnactmentRemote lookupWorkflowEnactmentBean() throws ConnexienceException
  {
    try
    {
      if (wfEnactmentRemote == null)
      {
        Context c = new InitialContext();
        wfEnactmentRemote = (WorkflowEnactmentRemote) c.lookup("java:global/ejb/WorkflowEnactmentBean");
      }
      return wfEnactmentRemote;
    }
    catch (NamingException ne)
    {
      throw new ConnexienceException("Cannot locate workflow enactment bean: " + ne.getMessage());
    }
  }

  /**
   * Get hold of a workflow lock bean
   */
  public static WorkflowLockRemote lookupWorkflowLockBean() throws ConnexienceException
  {
    try
    {
      if (wfLockRemote == null)
      {
        Context c = new InitialContext();
        wfLockRemote = (WorkflowLockRemote) c.lookup("java:global/ejb/WorkflowLockBean");
      }
      return wfLockRemote;
    }
    catch (NamingException ne)
    {
      throw new ConnexienceException("Cannot locate workflow lock bean: " + ne.getMessage());
    }
  }
}