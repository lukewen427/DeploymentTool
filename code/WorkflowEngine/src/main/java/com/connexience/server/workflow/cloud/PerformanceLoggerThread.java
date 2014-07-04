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
package com.connexience.server.workflow.cloud;

import com.connexience.server.model.logging.performance.WorkflowEngineStats;
import com.connexience.server.model.workflow.control.WorkflowEngineStatusData;
import com.connexience.server.util.provenance.PerformanceLoggerClient;
import com.connexience.server.workflow.util.SigarData;
import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import org.apache.log4j.*;
import org.hyperic.sigar.CpuPerc;


/**
 * This class provides a Thread that can log global performance metrics for the
 * workflow engine. 
 * @author hugo
 */
public class PerformanceLoggerThread extends Thread {
    private static Logger logger = Logger.getLogger(PerformanceLoggerThread.class);
    
    private volatile boolean runFlag = true;
    private long lastSendTime = System.currentTimeMillis();;
    private long currentMaximumMemory = 0;
    private long currentMaximumResidentMemory = 0;
    private double filteredCurrentMaximumResidentMemory = 0;
    private double filteredCurrentMaximumMemory = 0;
    private double filteredTotalCpu = 0;
    private double filteredUserCpu = 0;
    private double filteredStolenCpu = 0;
    private double filteredFreeRam = 0;
    private double filteredUsedRam = 0;
    
    public double filterConstant = 0.8;
    public boolean alwaysSend = false;
    public int sampleRate = 500;
    public int sendInterval = 4000;
    
    private CloudWorkflowEngine parentEngine;
    private PerformanceLoggerClient client;
    
    public PerformanceLoggerThread(CloudWorkflowEngine parentEngine) {
        this.parentEngine = parentEngine;
        client = new PerformanceLoggerClient();
        setDaemon(true);
    }
    
    @Override
    public void run() {
        while(runFlag){
            sample();
            // Check engine isn't idle
            if(parentEngine.getExecutionEngine().getJobQueueSize()>0 || alwaysSend){
                if(System.currentTimeMillis()>=(lastSendTime + sendInterval)){
                    send();
                    lastSendTime = System.currentTimeMillis();
                }
            }
            
            try {
                Thread.sleep(sampleRate);
            } catch (InterruptedException ie){
                runFlag = false;
            }
        }
    }
    
    public void terminate(){
        this.interrupt();
        runFlag = false;
    }
    
    private synchronized void sample(){
        currentMaximumResidentMemory = parentEngine.getExecutionEngine().getInvocationManager().getTotalMaximumResidentMemory();
        filteredCurrentMaximumResidentMemory = (filterConstant * filteredCurrentMaximumResidentMemory) + ((1 - filterConstant) * (double)currentMaximumResidentMemory);
        
        currentMaximumMemory = parentEngine.getExecutionEngine().getInvocationManager().getTotalMaximumMemory();
        filteredCurrentMaximumMemory = (filterConstant * filteredCurrentMaximumMemory) + ((1 - filterConstant) * (double)currentMaximumMemory);
        
        try {
            CpuPerc p = SigarData.SYSTEM_DATA.getCPUUsage();
            
            filteredTotalCpu = (filterConstant * filteredTotalCpu) + ((1 - filterConstant) * p.getCombined());
            filteredStolenCpu = (filterConstant * filteredStolenCpu) + ((1 - filterConstant) * p.getStolen());
            filteredUserCpu = (filterConstant * filteredUserCpu) + ((1 - filterConstant) * p.getUser());
        } catch (Exception e){
            logger.error("Error gathering CPU data: " + e.getMessage());
        }
        
        try {
            filteredFreeRam = (filterConstant * filteredFreeRam) + ((1 - filterConstant) * (double)SigarData.SYSTEM_DATA.getFreeRam());
            filteredUsedRam = (filterConstant * filteredUsedRam) + ((1 - filterConstant) * (double)SigarData.SYSTEM_DATA.getRamUsed());
        } catch (Exception e){
            logger.error("Error gathering global memory data: " + e.getMessage());
        }
        
    }
    
    private synchronized void send(){
        WorkflowEngineStats sample = new WorkflowEngineStats();
        sample.setFilteredMaximumMemory(filteredCurrentMaximumMemory);
        sample.setFilteredMaximumResidentMemory(filteredCurrentMaximumResidentMemory);
        sample.setFilteredStolenCpu(filteredStolenCpu);
        sample.setFilteredTotalCpu(filteredTotalCpu);
        sample.setFilteredUserCpu(filteredUserCpu);
        sample.setFilteredFreeRam(filteredFreeRam);
        sample.setFilteredUsedRam(filteredUsedRam);
        sample.setObservationTime(new Date());
        sample.setIpAddress(parentEngine.getServerIp());
        
        WorkflowEngineStatusData statusData = parentEngine.getExecutionEngine().getEngineStatus();
        sample.setFreeDiskSpace(statusData.getFreeSpace());
        sample.setRunningWorkflowCount(statusData.getWorkflowCount());

        if(parentEngine.getExecutionEngine().getJobQueueSize()>0){
            sample.setIdle(false);
        } else {
            sample.setIdle(true);
        }
        client.log(sample);
    }
}