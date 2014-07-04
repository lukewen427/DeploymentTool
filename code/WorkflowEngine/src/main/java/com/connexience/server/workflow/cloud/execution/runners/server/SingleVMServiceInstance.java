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

import com.connexience.server.workflow.service.DataProcessorException;
import java.io.File;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


/**
 * This interface defines a single executing service in a remote runner
 * @author hugo
 */
public interface SingleVMServiceInstance extends Remote {
    /** Set the classpath */
    public void setup(String baseDir, File invocationDir, ArrayList<URL> classpathList) throws RemoteException, DataProcessorException;

    /** Run the service */
    public int runService() throws RemoteException, DataProcessorException;
    
    /** Start the stream dumpers */
    public void startDumpers() throws RemoteException, DataProcessorException;
    
    /** Stop capturing streams */
    public void stopDumpers() throws RemoteException, DataProcessorException;
    
    /** Get the std-out file */
    public File getStdOutFile() throws RemoteException;
    
    /** Get the std-err file */
    public File getStdErrFile() throws RemoteException;
    
    /** Kill the service. This kills the server */
    public void kill() throws RemoteException;
    
    /** Add an external PID */
    public void addExternalPID(long pid) throws RemoteException;
    
    public abstract long getMaximumMemorySize() throws RemoteException;
    
    public abstract long getMaximumResidentMemory() throws RemoteException;
    
    public abstract int getProcessCount() throws RemoteException;

    public abstract void stopMonitoring() throws RemoteException;
   
}
