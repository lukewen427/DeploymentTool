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
package com.connexience.server.workflow.util;
import java.lang.reflect.*;
import java.util.*;
import org.apache.log4j.*;
import org.hyperic.sigar.*;

/**
 * This class periodically polls a process to see how much memory it is using
 * @author hugo
 */
public class ProcessMemoryMonitor extends Thread {
    private static Logger logger = Logger.getLogger(ProcessMemoryMonitor.class);
    
    /** Process being monitored */
    private ArrayList<Long> processes = new ArrayList<>();

    /** Maximum memory seen */
    private long maximumMemorySize = 0;
    
    /** Maximum resident memory */
    private long maximumResidentMemory = 0;
    
    /** All processes exited */
    private volatile boolean allProcessesExited = false;
    
    /** Stop flag */
    private volatile boolean stopFlag = false;
    
    /** Sample interval */
    private volatile long sampleInterval = 1000;
    
    public ProcessMemoryMonitor(long sampleInterval) {
        this.sampleInterval = sampleInterval;
    }
    
    public void addProcess(long pid){
        ProcMem mem = SigarData.SYSTEM_DATA.getProcMem(pid);
        if(mem!=null){
            processes.add(pid);
        }
        sample();
    }
    
    public synchronized void removeProcess(long pid){
        processes.remove(pid);
    }
    
    public static long extractPid(Process p){
        try {
            Class c = p.getClass();
            Field f = c.getDeclaredField("pid");
            f.setAccessible(true);
            int pid = f.getInt(p);
            return pid;
        } catch (Exception e){
            return -1;
        }
    }
    
    public synchronized void sample(){
        long sizeSum = 0;
        long residentSum = 0;
        boolean exitCheck = true;
        for(long pid : processes){
            ProcMem mem = SigarData.SYSTEM_DATA.getProcMem(pid);
            if(mem!=null){
                exitCheck = false;
                residentSum+=mem.getResident();
                sizeSum+=mem.getSize();
            }
        }
        allProcessesExited = exitCheck;
        maximumMemorySize = sizeSum;
        maximumResidentMemory = residentSum;
    }
    
    public void run(){
        while(stopFlag==false && allProcessesExited==false){
            sample();
            try {
                Thread.sleep(sampleInterval);
            } catch (InterruptedException e){}
        }
        
        if(allProcessesExited){
            logger.debug("All processes exited");
        }
    }

    public long getMaximumMemorySize() {
        return maximumMemorySize;
    }

    public long getMaximumResidentMemory() {
        return maximumResidentMemory;
    }
    
    public int getProcessCount(){
        return processes.size();
    }
    
    public long getOwnPID(){
        return SigarData.SYSTEM_DATA.getOwnPID();
    }
    
    public void stopMonitoring(){
        stopFlag = true;
        this.interrupt();
    }
    
    public static void main(String[] args){
        try {
            SigarData.SYSTEM_DATA.initialise();
            System.out.println(SigarData.SYSTEM_DATA.getProcMem(3263).getSize());
            System.out.println(SigarData.SYSTEM_DATA.getProcMem(0000).getSize());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
