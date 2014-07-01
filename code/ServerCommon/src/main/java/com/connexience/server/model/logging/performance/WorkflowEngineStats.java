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

import java.io.Serializable;
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
 * This class represents a snapshot of the summary statistics for a workflow
 * engine.
 * @author hugo
 */
@Entity
@Table(name = "enginestats")
@NamedQueries({
        @NamedQuery(name="WorkflowEngineStats.getStatsForEngine" , query="SELECT s FROM WorkflowEngineStats s where s.ipAddress=:ipAddress and s.observationTime>:startTime and s.observationTime<:endTime")
})
public class WorkflowEngineStats implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;    
    
    @Basic
    private String ipAddress;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date observationTime;
    
    @Basic
    private double filteredMaximumMemory;
    
    @Basic
    private double filteredMaximumResidentMemory;
    
    @Basic
    private double filteredTotalCpu;
    
    @Basic
    private double filteredUserCpu;
    
    @Basic
    private double filteredStolenCpu;

    @Basic
    private double filteredFreeRam;
    
    @Basic
    private double filteredUsedRam;
    
    @Basic
    private boolean idle = false;

    @Basic
    private long freeDiskSpace = 0;
    
    @Basic
    private int runningWorkflowCount = 0;
    
    
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

    public Date getObservationTime() {
        return observationTime;
    }

    public void setObservationTime(Date observationTime) {
        this.observationTime = observationTime;
    }

    public double getFilteredMaximumMemory() {
        return filteredMaximumMemory;
    }

    public void setFilteredMaximumMemory(double filteredMaximumMemory) {
        this.filteredMaximumMemory = filteredMaximumMemory;
    }

    public double getFilteredMaximumResidentMemory() {
        return filteredMaximumResidentMemory;
    }

    public void setFilteredMaximumResidentMemory(double filteredMaximumResidentMemory) {
        this.filteredMaximumResidentMemory = filteredMaximumResidentMemory;
    }

    public double getFilteredTotalCpu() {
        return filteredTotalCpu;
    }

    public void setFilteredTotalCpu(double filteredTotalCpu) {
        this.filteredTotalCpu = filteredTotalCpu;
    }

    public double getFilteredUserCpu() {
        return filteredUserCpu;
    }

    public void setFilteredUserCpu(double filteredUserCpu) {
        this.filteredUserCpu = filteredUserCpu;
    }

    public double getFilteredStolenCpu() {
        return filteredStolenCpu;
    }

    public void setFilteredStolenCpu(double filteredStolenCpu) {
        this.filteredStolenCpu = filteredStolenCpu;
    }

    public double getFilteredFreeRam() {
        return filteredFreeRam;
    }

    public double getFilteredUsedRam() {
        return filteredUsedRam;
    }

    public boolean isIdle() {
        return idle;
    }

    public void setFilteredFreeRam(double filteredFreeRam) {
        this.filteredFreeRam = filteredFreeRam;
    }

    public void setFilteredUsedRam(double filteredUsedRam) {
        this.filteredUsedRam = filteredUsedRam;
    }

    public void setIdle(boolean idle) {
        this.idle = idle;
    }

    public long getFreeDiskSpace() {
        return freeDiskSpace;
    }

    public void setFreeDiskSpace(long freeDiskSpace) {
        this.freeDiskSpace = freeDiskSpace;
    }

    public int getRunningWorkflowCount() {
        return runningWorkflowCount;
    }

    public void setRunningWorkflowCount(int runningWorkflowCount) {
        this.runningWorkflowCount = runningWorkflowCount;
    }

    @Override
    public String toString() {
        return filteredMaximumMemory + "," + filteredMaximumResidentMemory + "," + filteredStolenCpu + "," + filteredTotalCpu + "," + filteredUserCpu;
    }
    
    public void debugPrint(){
        System.out.println(toString());
    }
}