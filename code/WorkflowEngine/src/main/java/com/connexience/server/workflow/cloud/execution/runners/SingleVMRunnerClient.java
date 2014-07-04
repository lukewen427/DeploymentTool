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
package com.connexience.server.workflow.cloud.execution.runners;

import com.connexience.server.util.RegistryUtil;
import com.connexience.server.workflow.cloud.execution.runners.server.SingleVMServer;
import com.connexience.server.workflow.cloud.execution.runners.server.SingleVMServiceInstance;
import com.connexience.server.workflow.service.DataProcessorCallMessage;
import com.connexience.server.workflow.service.DataProcessorException;


/**
 * This class can connect to a remote service runner that executes all of the 
 * blocks from a workflow in a single VM.
 * @author hugo
 */
public class SingleVMRunnerClient extends AbstractRunner {
    /** Link to the remote VM */
    private SingleVMServiceInstance instance;
    
    public SingleVMRunnerClient(DataProcessorCallMessage message) throws DataProcessorException {
        super(message);
    }

    private SingleVMServer lookupServer() throws Exception {
        try {
            SingleVMServer server = (SingleVMServer)RegistryUtil.lookup("localhost", "INVOCATION_SERVER_" + message.getInvocationId());
            return server;
        } catch (Exception e){
            throw new Exception("Cannot connect to invocation server: " + e.getMessage());
        }
    }
    
    @Override
    public void start() throws Exception {
        SingleVMServer server = lookupServer();
        if(server!=null){
            instance = server.createServiceInstance(message);
            instance.setup(baseDir, invocationDirectory, classpathList);
        } else {
            throw new Exception("No connection to server process");
        }
    }

    @Override
    public int waitFor() throws Exception {
        if(instance!=null){
            return instance.runService();
        } else {
            throw new Exception("No server available");
        }
    }

    @Override
    public void startDumpers() throws Exception {
        if(instance!=null){
            instance.startDumpers();
            stdErrFile = instance.getStdErrFile();
            stdOutFile = instance.getStdOutFile();
        }
    }

    @Override
    public void stopDumpers() throws Exception {
        if(instance!=null){
            instance.stopDumpers();
        }
    }

    @Override
    public void kill() {
        if(instance!=null){
            try {
                instance.kill();
            } catch (Exception e){
                
            }
        }
    }

    @Override
    public void addExternalPID(long pid) {
        try {
            instance.addExternalPID(pid);
        } catch (Exception e){
        }
    }

    @Override
    public long getMaximumMemorySize() throws Exception{
        SingleVMServer server = lookupServer();
        if(server!=null){
            return server.getMaximumMemorySize() + instance.getMaximumMemorySize();
        } else {
            throw new Exception("Cannot locate server VM");
        }
    }

    @Override
    public long getMaximumResidentMemory() throws Exception {
        SingleVMServer server = lookupServer();
        if(server!=null){
            return server.getMaximumResidentMemory()+ instance.getMaximumResidentMemory();
        } else {
            throw new Exception("Cannot locate server VM");
        }
    }

    @Override
    public int getProcessCount() throws Exception {
        return instance.getProcessCount();
    }

    @Override
    public void stopMonitoring() throws Exception {
        instance.stopMonitoring();
    }
}
