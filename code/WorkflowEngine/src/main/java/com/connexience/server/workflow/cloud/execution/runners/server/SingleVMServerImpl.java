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

import com.connexience.server.util.RegistryUtil;
import com.connexience.server.workflow.service.DataProcessorCallMessage;
import com.connexience.server.workflow.service.DataProcessorException;
import com.connexience.server.workflow.util.ProcessMemoryMonitor;
import com.connexience.server.workflow.util.SigarData;
import com.connexience.server.workflow.util.ZipUtils;
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.apache.log4j.Logger;

/**
 * This class runs as an external process that can host multiple service
 * invocations for a VM
 * @author hugo
 */
public class SingleVMServerImpl extends UnicastRemoteObject implements SingleVMServer
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


    Logger logger = Logger.getLogger(SingleVMServerImpl.class);
    private int rmiPort;
    private String invocationId;
    private String path;
    private ProcessMemoryMonitor monitor;
    
    @Override
    public SingleVMServiceInstance createServiceInstance(DataProcessorCallMessage message) throws RemoteException, DataProcessorException {
        return new SingleVMServiceInstanceImpl(message, monitor);
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
    public void terminate() throws RemoteException {
        System.exit(0);
    }

    public SingleVMServerImpl(int rmiPort, String invocationId, String path) throws RemoteException {
        this.rmiPort = rmiPort;
        this.invocationId = invocationId;
        this.path = path;
        logger.debug("Created RMI Server for InvocationID: " + invocationId);

        try {
            SigarData.SYSTEM_DATA.initialise();
        } catch (Exception e){
            logger.error("Error initialising sigar: " + e.getMessage());
        }
        
        monitor = new ProcessMemoryMonitor(250);
        monitor.addProcess(SigarData.SYSTEM_DATA.getOwnPID());
        monitor.start();
    }
    
    public void register() {
        try {
            logger.debug("Registering RMI server for InvocationID: " +invocationId + " to resgistry");
            RegistryUtil.registerToRegistry("INVOCATION_SERVER_" + invocationId, this, rmiPort, false);
            logger.debug("Registered RMI server for InvocationID: " + invocationId);            
            
            // Create a file
            File serverStartedFile = new File(path, "rmi-server.txt");
            ZipUtils.writeSingleLineFile(serverStartedFile, "INVOCATION_SERVER_" + invocationId);
        }
            catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void unregister(){
        
    }
    
    public static void main(String[] args){
        // First Argument is the RMI Registry port, second is the invocation ID
        if(args.length==3){
             int registryPort = Integer.parseInt(args[0]);
             String invocationId = args[1];
             String path = args[2];
             
            try {
                SingleVMServerImpl server = new SingleVMServerImpl(registryPort, invocationId, path);
                server.register();
            } catch (Exception e){
                System.exit(2);
            }
        } else {
            System.exit(1);
        }

    }
}
