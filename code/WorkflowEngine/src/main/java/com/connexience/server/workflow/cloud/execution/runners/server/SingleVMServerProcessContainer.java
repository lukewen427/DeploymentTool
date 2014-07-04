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

import com.connexience.server.workflow.cloud.execution.InputStreamDumper;
import java.io.File;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * This class can contain a VM server process for a workflow invocation
 * @author hugo
 */
public class SingleVMServerProcessContainer extends Thread {
    private Logger logger = Logger.getLogger(SingleVMServerProcessContainer.class);
    private int rmiPort;
    private String invocationId;
    private Process serverProcess = null;
    private boolean runAsDifferentUser = false;
    private String userName = "";
    private int permGenSize = 128;
    private int maxVMSize = 512;
    private int exitCode = 0;
    private boolean running = false;
    private File workingDir;

    private boolean debuggingEnabled = false;
    private boolean debuggingSuspended = true;


    public SingleVMServerProcessContainer(int rmiPort, String invocationId, File workingDir) {
        this.rmiPort = rmiPort;
        this.invocationId = invocationId;
        this.workingDir = workingDir;
    }
    
    public SingleVMServerProcessContainer(int rmiPort, String invocationId, File workingDir, String userName){
        runAsDifferentUser = true;
        this.userName = userName;
        this.rmiPort = rmiPort;
        this.invocationId = invocationId;
        this.workingDir = workingDir;
    }

    public void setMaxVMSize(int maxVMSize) {
        this.maxVMSize = maxVMSize;
    }

    public void setPermGenSize(int permGenSize) {
        this.permGenSize = permGenSize;
    }

    public void setDebuggingEnabled(boolean debuggingEnabled) {
        this.debuggingEnabled = debuggingEnabled;
    }

    public void setDebuggingSuspended(boolean debuggingSuspended) {
        this.debuggingSuspended = debuggingSuspended;
    }

    public int getExitCode() {
        return exitCode;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        try {
            // Are we executing as somebody else
            ArrayList<String> cmdArgs = new ArrayList<>();
            String javaHome;
            if(runAsDifferentUser){
                cmdArgs.add("/usr/bin/sudo");
                cmdArgs.add("-u");
                cmdArgs.add(userName);
                javaHome = System.getProperty("java.home");
                cmdArgs.add(javaHome + File.separator + "bin" + File.separator + "java");

            } else {
                javaHome = System.getProperty("java.home");
                cmdArgs.add(javaHome + File.separator + "bin" + File.separator + "java");
            }
        
            String libraryPath = System.getProperty("java.library.path");
            logger.debug("Library path for external VM server: " + libraryPath);
            
            cmdArgs.add("-Djava.awt.headless=true");
            cmdArgs.add("-Dlog4j.configuration=enginelogging.properties");	
            cmdArgs.add("-Djava.library.path=" + libraryPath);
            
            // Memory details
            cmdArgs.add("-Xss1M");
            cmdArgs.add("-Xmx" + maxVMSize + "M");
            cmdArgs.add("-XX:MaxPermSize=" + permGenSize + "M"); 
            
            // Debugging details
            if (debuggingEnabled) {
                cmdArgs.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=" + (debuggingSuspended ? "y" : "n"));
            }

            // Classpath
            String classpath = System.getProperty("java.class.path");
            cmdArgs.add("-cp");
            cmdArgs.add(classpath);
            
            // Class to run
            cmdArgs.add("com.connexience.server.workflow.cloud.execution.runners.server.SingleVMServerImpl");
            
            // Invocation details
            cmdArgs.add(Integer.toString(rmiPort));
            cmdArgs.add(invocationId);
            cmdArgs.add(workingDir.getPath());
           
            logger.debug("Starting server for InvocationID:" + invocationId);
            serverProcess = Runtime.getRuntime().exec(cmdArgs.toArray(new String[cmdArgs.size()]), null, workingDir);
            InputStreamDumper stdOutDumper = new InputStreamDumper(serverProcess.getInputStream(), System.out);
            InputStreamDumper stdErrDumper = new InputStreamDumper(serverProcess.getErrorStream(), System.err);
            running = true;
            exitCode = serverProcess.waitFor();
            running = false;
            logger.debug("Server processes for InvocationID: " + invocationId + " finished with exit code: " + exitCode);
            
        } catch (Exception e){
            
        }
    }
    
    public void stopServer(){
        if(running && serverProcess!=null){
            serverProcess.destroy();
        }
    }
    
    /** Wait for the server to register itself with RMI */
    public boolean waitForRMIRegistration(int timeout){
        int interval = 100;
        long endTime = System.currentTimeMillis() + (1000 * timeout);
        File f = new File(workingDir, "rmi-server.txt");
        while(System.currentTimeMillis()<endTime){
            if(f.exists()){
                return true;
            }
            try {
                Thread.sleep(interval);
            } catch (Exception e){}
        }
        
        logger.error("Could not contact RMI server after: " + timeout + " seconds");
        return false;
    }
}