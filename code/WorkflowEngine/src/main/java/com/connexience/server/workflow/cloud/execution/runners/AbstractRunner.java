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

import com.connexience.server.workflow.service.DataProcessorCallMessage;
import com.connexience.server.workflow.service.DataProcessorException;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Base class for a service runner.
 * @author hugo
 */
public abstract class AbstractRunner {    
    /** Classpath list */
    protected ArrayList<URL> classpathList;
    
    /** Invoction message */
    protected DataProcessorCallMessage message;
    
    /** Base directory */
    protected String baseDir;
    
    /** Max VM size */
    protected int maxVmSize = 256;
    
    /** Perm size */
    protected int permSize = 512;
    
    /** Has this been killed */
    protected boolean killed = false;
    
    /** Is debugging allowed */
    protected boolean debugAllowed = true;
    
    /** Standard output file */
    protected File stdOutFile;
    
    /** Standard error file */
    protected File stdErrFile;
    
    /** Debugging port */
    protected int debugPort = 5005;
    
    public AbstractRunner(DataProcessorCallMessage message) throws DataProcessorException {
        this.message = message;
    }

    /** Start the process running */
    public abstract void start() throws Exception;
    
    /** Wait for the process to finish */
    public abstract int waitFor() throws Exception;

    /** Start the output stream dumpers */
    public abstract void startDumpers() throws Exception;
    
    /** Stop dumping the std out and std err streams */
    public abstract void stopDumpers() throws Exception;
    
    /** Kill this process */
    public abstract void kill();
    
    public abstract void addExternalPID(long pid) throws Exception;
    
    public abstract long getMaximumMemorySize() throws Exception;
    
    public abstract long getMaximumResidentMemory() throws Exception;
    
    public abstract int getProcessCount() throws Exception;

    public abstract void stopMonitoring() throws Exception;

    /** Invocation directory */
    protected File invocationDirectory;
    
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setClasspathList(ArrayList<URL> classpathList) {
        this.classpathList = classpathList;
    }

    public ArrayList<URL> getClasspathList() {
        return classpathList;
    }

    public void setDebugAllowed(boolean debugAllowed) {
        this.debugAllowed = debugAllowed;
    }

    public boolean isDebugAllowed() {
        return debugAllowed;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    public boolean isKilled() {
        return killed;
    }

    public void setMaxVmSize(int maxVmSize) {
        this.maxVmSize = maxVmSize;
    }

    public int getMaxVmSize() {
        return maxVmSize;
    }

    public void setMessage(DataProcessorCallMessage message) {
        this.message = message;
    }

    public DataProcessorCallMessage getMessage() {
        return message;
    }

    public void setPermSize(int permSize) {
        this.permSize = permSize;
    }

    public int getPermSize() {
        return permSize;
    }
    
    /** Get the standard error file */
    public File getStdErrFile() {
        return stdErrFile;
    }

    /** Get the standard out file */
    public File getStdOutFile() {
        return stdOutFile;
    }

    public void setDebugPort(int debugPort) {
        this.debugPort = debugPort;
    }

    public int getDebugPort() {
        return debugPort;
    }

    public void setInvocationDirectory(File invocationDirectory) {
        this.invocationDirectory = invocationDirectory;
    }

    public File getInvocationDirectory() {
        return invocationDirectory;
    }
}
