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

import com.connexience.server.workflow.cloud.execution.InputStreamDumper;
import com.connexience.server.workflow.service.DataProcessorCallMessage;
import com.connexience.server.workflow.service.DataProcessorException;
import com.connexience.server.workflow.util.ProcessMemoryMonitor;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * This class executes a service as a standalone program with a JVM that exists
 * for the life of the service invocation then terminates. This maps to the
 * original way of running workflow blocks.
 * @author hugo
 */
public class OneShotJVMRunner extends AbstractRunner {
    private static Logger logger = Logger.getLogger(OneShotJVMRunner.class);    
    Process externalProcess = null;
    InputStreamDumper inDumper = null;
    InputStreamDumper stdErrDumper = null;
    FileOutputStream errStore = null;
    FileOutputStream inStore = null;
    private ProcessMemoryMonitor monitor;
    
    
    public OneShotJVMRunner(DataProcessorCallMessage message) throws DataProcessorException {
        super(message);
    }
    
    @Override
    public void start() throws Exception {
        monitor = new ProcessMemoryMonitor(250);
        ArrayList<String> cmdArgs = new ArrayList<>();

        // Basic java executable
        String javaHome;
        if(message.isRunAsDifferentUser()){
            cmdArgs.add("/usr/bin/sudo");
            cmdArgs.add("-u");
            cmdArgs.add(message.getSystemUsername());
            javaHome = System.getProperty("java.home");
            cmdArgs.add(javaHome + File.separator + "bin" + File.separator + "java");

        } else {
            javaHome = System.getProperty("java.home");
            cmdArgs.add(javaHome + File.separator + "bin" + File.separator + "java");
        }

        cmdArgs.add("-Djava.awt.headless=true");
        cmdArgs.add("-Dlog4j.configuration=enginelogging.properties");

        // Start frozen in debug mode if necessary
        if (message.isDebugEnabled() && debugAllowed) {
            logger.info("DEBUG PORT: " + debugPort);
            cmdArgs.add(String.format(
                    "-agentlib:jdwp=transport=dt_socket,server=y,address=%d,suspend=%s", 
                    debugPort,
                    message.isDebugSuspended() ? "y" : "n"));
            // Additionally, enabled debugging turns on assertions
            cmdArgs.add("-ea");
        }

        cmdArgs.add("-cp");
        StringBuilder cpb = new StringBuilder();
        URL entry;

        for (int i = 0; i < classpathList.size(); i++) {
            entry = classpathList.get(i);
            if (i > 0) {
                cpb.append(File.pathSeparator);
            }
            cpb.append(entry.getPath());
        }
        cmdArgs.add(cpb.toString());
        cmdArgs.add("-Xss1M");
        cmdArgs.add("-Xmx" + maxVmSize + "M");
        cmdArgs.add("-XX:MaxPermSize=" + permSize + "M");

        // Classname of the service runner
        cmdArgs.add("com.connexience.server.workflow.cloud.execution.DataProcessorServiceRunner");

        // Command line options
        cmdArgs.add(baseDir);
        cmdArgs.add(message.getInvocationId());
        cmdArgs.add(message.getContextId());
        externalProcess = Runtime.getRuntime().exec(cmdArgs.toArray(new String[0]), null, invocationDirectory);
        
        // Start memory monitoring
        monitor.addProcess(ProcessMemoryMonitor.extractPid(externalProcess));
        monitor.start();        
    }

    @Override
    public int waitFor() throws Exception {
        externalProcess.waitFor();
        return externalProcess.exitValue();
    }

    @Override
    public void startDumpers() throws Exception {
        stdErrFile = new File(invocationDirectory, "stderr-" + message.getContextId());
        errStore = new FileOutputStream(stdErrFile);
        stdErrDumper = new InputStreamDumper(externalProcess.getErrorStream(), errStore, message.getStdOutBufferSize());
        logger.debug("Standard error reader attached. InvocationID=" + message.getInvocationId() + " BlockID=" + message.getContextId());

        stdOutFile = new File(invocationDirectory, "stdout-" + message.getContextId());
        inStore = new FileOutputStream(stdOutFile);
        inDumper = new InputStreamDumper(externalProcess.getInputStream(), inStore, message.getStdOutBufferSize());
        logger.debug("Standard output reader attached. InvocationID=" + message.getInvocationId() + " BlockID=" + message.getContextId());

    }

    @Override
    public void stopDumpers() throws Exception {
        if(stdErrDumper!=null){
            stdErrDumper.stop();
            errStore.flush();
            errStore.close();
        }
        
        if(inDumper!=null){
            inDumper.stop();
            inStore.flush();
            inStore.close();
        }
    }

    @Override
    public void kill() {
        if(externalProcess!=null){
            externalProcess.destroy();
        }
    }

    @Override
    public int getProcessCount() {
        return monitor.getProcessCount();
    }

    @Override
    public long getMaximumMemorySize() {
        return monitor.getMaximumMemorySize();
    }

    @Override
    public long getMaximumResidentMemory() {
        return monitor.getMaximumResidentMemory();
    }

    @Override
    public void stopMonitoring() {
        monitor.stopMonitoring();
    }

    @Override
    public void addExternalPID(long pid) {
        monitor.addProcess(pid);
    }
}