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
package com.connexience.server.workflow.cloud.execution.runners.server;

import com.connexience.server.workflow.service.DataProcessorCallMessage;
import com.connexience.server.workflow.service.DataProcessorException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface defines a VM that can host multiple invocations for a single
 * workflow
 * @author hugo
 */
public interface SingleVMServer extends Remote {
    public SingleVMServiceInstance createServiceInstance(DataProcessorCallMessage message) throws RemoteException, DataProcessorException;
    public abstract long getMaximumMemorySize() throws RemoteException;
    public abstract long getMaximumResidentMemory() throws RemoteException;
    public void terminate() throws RemoteException;
}
