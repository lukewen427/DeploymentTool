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
package com.connexience.server.model.logging.performance;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * This class represents a record of a workflow engine in the performance
 * database.
 * @author hugo
 */
@Entity
@Table(name = "engines")
@NamedQueries({
        @NamedQuery(name="WorkflowEngineInstance.findEngine" , query="SELECT e FROM WorkflowEngineInstance e where e.ipAddress=:ipAddress"),
        @NamedQuery(name="WorkflowEngineInstance.listEngines", query="SELECT e FROM WorkflowEngineInstance e"),
        @NamedQuery(name="WorkflowEngineInstance.listInactiveEngines", query="SELECT e FROM WorkflowEngineInstance e where e.status=0")
})
public class WorkflowEngineInstance {
    public static final int ENGINE_RUNNING = 1;
    public static final int ENGINE_STOPPED = 0;
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id; 
    
    @Basic
    private String ipAddress;
    
    @Basic
    private int status = ENGINE_RUNNING;

    @Basic
    private long physicalRam = 0;
    
    @Basic
    private int cpuCount = 0;
    
    @Basic
    private double cpuSpeed = 0;
    
    @Basic
    private String operatingSystem = "";
    
    @Basic
    private long diskFreeSpace = 0;
    
    @Basic
    private double cpuPercentUsed = 0;
    
    @Basic
    private String architecture = "";
    
    @Basic
    private long freeRam = 0;
    
    @Basic
    private long runningWorkflowCount = 0;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date observationTime;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getObservationTime() {
        return observationTime;
    }

    public void setObservationTime(Date observationTime) {
        this.observationTime = observationTime;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public int getCpuCount() {
        return cpuCount;
    }

    public void setCpuCount(int cpuCount) {
        this.cpuCount = cpuCount;
    }

    public double getCpuSpeed() {
        return cpuSpeed;
    }

    public void setCpuSpeed(double cpuSpeed) {
        this.cpuSpeed = cpuSpeed;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }


    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public long getPhysicalRam() {
        return physicalRam;
    }

    public void setPhysicalRam(long physicalRam) {
        this.physicalRam = physicalRam;
    }


    public double getCpuPercentUsed() {
        return cpuPercentUsed;
    }

    public void setCpuPercentUsed(double cpuPercentUsed) {
        this.cpuPercentUsed = cpuPercentUsed;
    }

    public long getDiskFreeSpace() {
        return diskFreeSpace;
    }

    public void setDiskFreeSpace(long diskFreeSpace) {
        this.diskFreeSpace = diskFreeSpace;
    }

    public long getFreeRam() {
        return freeRam;
    }

    public void setFreeRam(long freeRam) {
        this.freeRam = freeRam;
    }

    public long getRunningWorkflowCount() {
        return runningWorkflowCount;
    }

    public void setRunningWorkflowCount(long runningWorkflowCount) {
        this.runningWorkflowCount = runningWorkflowCount;
    }
}