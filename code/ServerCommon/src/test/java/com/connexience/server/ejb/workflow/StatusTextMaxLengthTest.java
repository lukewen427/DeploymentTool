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

import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.workflow.WorkflowServiceLog;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class StatusTextMaxLengthTest
{
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		_ticket = new Ticket();

		// Please DO edit these values before running the test
		//_ticket.setUserId("8a8816153af5ce3e013af5d16c11002c");
		//_invocationId = "8a8816153af5ce3e013af5d9c4000109";

		_wfManagementRemote = lookupTheBean();
	}

	@Test
	public void testShort() throws Exception
	{
		if (_invocationId.equals(FakeInvocationId)) {
			fail("Please configure test before running.");
		}
		String statusMessage = "A short status message";

		_wfManagementRemote.updateServiceLog(_ticket, _invocationId, _contextId, "Some output data", "Some status text", statusMessage);
		WorkflowServiceLog log = _wfManagementRemote.getServiceLog(_ticket, _invocationId, _contextId);
		assertEquals(statusMessage, log.getStatusMessage());
	}
	
	@Test
	public void testLong() throws Exception
	{
		if (_invocationId.equals(FakeInvocationId)) {
			fail("Please configure test before running.");
		}

		String statusMessage;
		String statusMessage100 = "It's a very long status message. It's a very long status message. It's a very long status message.. ";
		String trimmedText;
		statusMessage = statusMessage100;
		statusMessage += statusMessage;
		statusMessage += statusMessage;
		statusMessage += statusMessage;
		statusMessage += statusMessage100;
		statusMessage += statusMessage100;
		statusMessage += statusMessage100;

		trimmedText = statusMessage.substring(0, 1020) + "..."; 

		_wfManagementRemote.updateServiceLog(_ticket, _invocationId, _contextId, "Some output data", "Some status text", statusMessage);
		WorkflowServiceLog log = _wfManagementRemote.getServiceLog(_ticket, _invocationId, _contextId);
		assertEquals(trimmedText, log.getStatusMessage());
	}


	// Do NOT edit any of these values
	// To setup test use method setUpBeforeClass.
	private final static String FakeInvocationId = "xxxxxxxxxxxx";
	private static Ticket _ticket;
	private static String _invocationId = FakeInvocationId;
    private static WorkflowManagementRemote _wfManagementRemote;
    private static String _contextId = "fake context id";
    

	private static WorkflowManagementRemote lookupTheBean() throws Exception
	{
		Properties props = new Properties();
		props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		//props.put(Context.INITIAL_CONTEXT_FACTORY, org.jboss.naming.remote.client.InitialContextFactory.class.getName());
		//props.put(Context.PROVIDER_URL, "remote://localhost:4447");
		//props.put(Context.SECURITY_PRINCIPAL, "testuser");
		//props.put(Context.SECURITY_CREDENTIALS, "testpassword");
		//props.put("jboss.naming.client.ejb.context", true);

		Context c = new InitialContext(props);
		return (WorkflowManagementRemote) c.lookup("ejb:inkspot/server-beans//WorkflowManagementBean!" + WorkflowManagementRemote.class.getName());
		//return (WorkflowManagementRemote) c.lookup("ejb:inkspot/server-beans/WorkflowManagementBean!com.connexience.server.ejb.workflow.WorkflowManagementRemote");
	}
}
