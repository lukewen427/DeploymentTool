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

import com.connexience.server.workflow.cloud.execution.DataProcessorServiceRunner;
import com.connexience.server.workflow.service.DataProcessorCallMessage;
import com.connexience.server.workflow.service.DataProcessorException;
import com.connexience.server.workflow.util.ProcessMemoryMonitor;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


/**
 * This class provides an endpoint that can execute a service.
 * @author hugo
 */
public class SingleVMServiceInstanceImpl extends UnicastRemoteObject implements SingleVMServiceInstance
{
    /**
     * Class version UID.
     * 
     * Please increment this value whenever your changes may cause 
     * incompatibility with the previous version of this class. If unsure, ask 
     * one of the core development team or read:
     *   http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     *   http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 1L;

    private ArrayList<URL> classpathList = new ArrayList<>();
    private ArrayList<Long> externalProcessIds = new ArrayList<>();
    private DataProcessorCallMessage message;
    private String baseDir;
    private File invocationDir;
    private PrintStream initialErrStream;
    private PrintStream redirectedErrStream;
    private PrintStream initialOutStream; 
    private PrintStream redirectedOutStream;
    private File stdOutFile;
    private File stdErrFile;
    private ProcessMemoryMonitor monitor;
    
    public SingleVMServiceInstanceImpl(DataProcessorCallMessage message, ProcessMemoryMonitor monitor) throws RemoteException {
        super();
        this.message = message;
        this.monitor = monitor;
    }

    @Override
    public void setup(String baseDir, File invocationDir, ArrayList<URL> classpathList) throws RemoteException, DataProcessorException {
        this.classpathList = classpathList;
        this.baseDir = baseDir;
        this.invocationDir = invocationDir;
    }

    @Override
    public int runService() throws RemoteException, DataProcessorException {
        try {            
            // Create the classloader
            URLClassLoader serviceClassloader = new URLClassLoader(classpathList.toArray(new URL[classpathList.size()]));
            
            // Create the service runner
            Class runnerClass = serviceClassloader.loadClass("com.connexience.server.workflow.cloud.execution.DataProcessorServiceRunner");
            DataProcessorServiceRunner runner = (DataProcessorServiceRunner)runnerClass.newInstance();
            runner.setContextId(message.getContextId());
            runner.setInvocationId(message.getInvocationId());
            runner.setWorkingDir(new File(baseDir + File.separator + message.getInvocationId()));
            runner.setInVmClassLoader(serviceClassloader);
            
            int code = runner.setupAndExecute();

            // Remove the lock file
            File flag = new File(new File(baseDir + File.separator + message.getInvocationId()), message.getContextId() + "-running.flag");
            flag.delete();            
            monitor.sample();
            
            // Remove and PIDs that were added to the monitor
            for(long pid : externalProcessIds){
                monitor.removeProcess(pid);
            }
            
            return code;
        } catch (Exception e){
            throw new DataProcessorException("Error running service: " + e.getMessage(), e);
        }
    }

    @Override
    public void startDumpers() throws RemoteException, DataProcessorException {
        try {
            initialErrStream = System.err;
            stdErrFile = new File(invocationDir, "stderr-" + message.getContextId());
            redirectedErrStream = new PrintStream(stdErrFile);
            System.setErr(redirectedErrStream);
            
            initialOutStream = System.out;
            stdOutFile = new File(invocationDir, "stdout-" + message.getContextId());
            redirectedOutStream = new PrintStream(stdOutFile);
            System.setOut(redirectedOutStream);
            
        } catch (Exception e){
            throw new DataProcessorException("Error starting stream dumpers: " + e.getMessage(), e);
        }
    }

    @Override
    public void stopDumpers() throws RemoteException, DataProcessorException {
        try {
            redirectedErrStream.flush();
            System.setErr(initialErrStream);
            redirectedErrStream.close();
            
            redirectedOutStream.flush();
            System.setOut(initialOutStream);
            redirectedOutStream.close();
        } catch (Exception e){
            throw new DataProcessorException("Error stopping stream dumping: " + e.getMessage(), e);
        }
    }

    @Override
    public File getStdErrFile() throws RemoteException {
        return stdErrFile;
    }

    @Override
    public File getStdOutFile() throws RemoteException {
        return stdOutFile;
    }

    @Override
    public void kill() throws RemoteException {
        System.exit(0);
    }

    @Override
    public void addExternalPID(long pid) throws RemoteException {
        monitor.addProcess(pid);
        externalProcessIds.add(pid);
    }

    @Override
    public long getMaximumMemorySize() throws RemoteException {
        return monitor.getMaximumMemorySize();
    }

    @Override
    public long getMaximumResidentMemory() throws RemoteException {
        return monitor.getMaximumResidentMemory();
    }

    @Override
    public int getProcessCount() throws RemoteException {
        return monitor.getProcessCount();
    }

    @Override
    public void stopMonitoring() throws RemoteException {
        monitor.stopMonitoring();
    }   
}