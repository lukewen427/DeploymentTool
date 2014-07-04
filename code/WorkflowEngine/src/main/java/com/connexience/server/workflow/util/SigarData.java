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
import org.hyperic.sigar.*;
import org.apache.log4j.*;
/**
 * This class can provide information about the configuration of the local machine.
 * It is used when sending provenance data so we can include machine attributes
 * when building performance data.
 * @author hugo
 */
public class SigarData {
    private static Logger logger = Logger.getLogger(SigarData.class);
    
    /** Singleton instance */
    public static SigarData SYSTEM_DATA = new SigarData();

    /** Average CPU speed */
    private double averageCpuSpeed = 0;
    
    /** CPU Count */
    private int cpuCount = 0;
    
    /** Physical RAM */
    private long physicalRam = 0;
    
    /** System Architecture */
    private String architecture = "";
    
    /** Operating system name */
    private String operatingSystem = "";
    
    /** CPU Vendor */
    private String cpuVendor = "";
    
    /** CPU Model */
    private String cpuModel = "";
    
    /** CPU Cache size */
    private int averageCpuCacheSize = 0;
    
    /** Local sigar object */
    private Sigar s;
    
    /** Has this data been initialised */
    private boolean available = false;
    
    public SigarData() {
    }
    
    public void initialise(){
        logger.debug("Attempting to gather system data");
        try {
            s = new Sigar();
            
            SysInfo sys = new SysInfo();
            sys.gather(s);
            architecture = sys.getArch();
            operatingSystem = sys.getName();
            physicalRam = s.getMem().getTotal();

            CpuInfo[] cpus = s.getCpuInfoList();
            if(cpus.length>0){
                cpuCount = cpus.length;
                int totalSpeed = 0;
                int totalCache = 0;
                for(int i=0;i<cpus.length;i++){
                    totalSpeed+=cpus[i].getMhz();
                    totalCache+=cpus[i].getCacheSize();
                }
                averageCpuSpeed = (int)((double)totalSpeed / (double)cpuCount);
                averageCpuCacheSize = (int)((double)totalCache / (double)cpuCount);
                cpuVendor = cpus[0].getVendor();
                cpuModel = cpus[0].getModel();
                available = true;
                
                logInfo();
            } else {
                logger.error("No CPU information returned");
                available = false;
            }
            
        } catch (Exception e){
            logger.error("Error initialising system data info: " + e.getMessage());
            available = false;
        }
    }
    
    public void logInfo(){
        if(available){
            logger.debug("Operating System: " + getOperatingSystem());
            logger.debug("Architecture: " + getArchitecture());
            logger.debug("RAM: " + getPhysicalRam());
            logger.debug("CPUs: " + getCpuCount());
            logger.debug("Mean CPU speed: " + getAverageCpuSpeed());
            logger.debug("Mean CPU cache: " + getAverageCpuCacheSize());
            logger.debug("CPU Vendor: " + getCpuVendor());
            logger.debug("CPU Model: " + getCpuModel());
            
        } else {
            logger.debug("System info not available");
        }
    }

    public long getOwnPID(){
        if(available){
            return s.getPid();
        } else {
            return -1;
        }
    }
    
    public long getProcessMemory(long pid){
        if(available){
            try {
                return s.getProcMem(pid).getSize();
            } catch (Exception e){
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    public ProcMem getProcMem(long pid){
        if(available){
            try {
                return s.getProcMem(pid);
            } catch (Exception e){
                return null;
            }
        } else {
            return null;
        }
    }
    
    public boolean processRunning(long pid){
        if(available){
            ProcMem mem = getProcMem(pid);
            if(mem!=null){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public CpuPerc getCPUUsage() throws Exception {
        return s.getCpuPerc();
    }
    
    public long getFreeRam() throws Exception {
        return s.getMem().getActualFree();
    }
    
    public long getRamUsed() throws Exception {
        return s.getMem().getActualUsed();
    }
    
    public int getAverageCpuCacheSize() {
        return averageCpuCacheSize;
    }

    public String getCpuModel() {
        return cpuModel;
    }

    public String getCpuVendor() {
        return cpuVendor;
    }

    public String getArchitecture() {
        return architecture;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public long getPhysicalRam() {
        return physicalRam;
    }
    
    public double getAverageCpuSpeed() {
        return averageCpuSpeed;
    }

    public int getCpuCount() {
        return cpuCount;
    }

    public boolean isAvailable() {
        return available;
    }
    
    public Sigar getSigarObject(){
        return s;
    }
    
    public static void main(String[] args){
        SigarData.SYSTEM_DATA.initialise();
        SigarData.SYSTEM_DATA.logInfo();
    }
}
