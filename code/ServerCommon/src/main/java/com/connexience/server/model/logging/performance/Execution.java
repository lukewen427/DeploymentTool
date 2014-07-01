
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

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "executions")
@NamedQueries({
        @NamedQuery(name="Execution.distinctServiceId" , query="SELECT DISTINCT e.serviceId FROM Execution  e"),
        @NamedQuery(name="Execution.listVersionsOfService", query="SELECT DISTINCT e.versionId FROM Execution e WHERE e.serviceId=:serviceId"),
        @NamedQuery(name="Execution.listDurationsForAllVersionsOfService", query="SELECT e.duration FROM Execution e WHERE e.serviceId=:serviceId"),
        @NamedQuery(name="Execution.listDurationsForSpecificVersionOfService", query="SELECT e.duration FROM Execution e WHERE e.serviceId=:serviceId AND e.versionId=:versionId"),
        @NamedQuery(name="Execution.listDurationsForAllVersionsOfAllServices", query="SELECT e.duration from Execution e"),
        @NamedQuery(name="Execution.listMemoryForAllVersionsOfService", query="SELECT e.maxResidentMemory FROM Execution e WHERE e.serviceId=:serviceId"),
        @NamedQuery(name="Execution.listMemoryForSpecificVersionOfService", query="SELECT e.maxResidentMemory FROM Execution e WHERE e.serviceId=:serviceId AND e.versionId=:versionId"),
        @NamedQuery(name="Execution.listMemoryForAllServices", query="SELECT e.maxResidentMemory FROM Execution e"),
        @NamedQuery(name="Execution.findOrderedByName" , query="SELECT e FROM Execution e ORDER BY e.serviceName"),
        @NamedQuery(name="Execution.listServiceExecutions", query="SELECT e FROM Execution e where e.serviceId=:serviceId AND e.versionId=:versionId"),
        @NamedQuery(name="Execution.listExecutionsForServiceInInvocation", query="SELECT e from Execution e where e.serviceId=:serviceId AND e.versionId=:versionId AND e.invocationId=:invocationId"),
        @NamedQuery(name="Execution.getAverageDurationForAllVersionsOfService", query="SELECT AVG(e.duration) FROM Execution e WHERE e.serviceId=:serviceId"),
        @NamedQuery(name="Execution.getDurationStatsForAllVersionsOfService", query="SELECT MIN(e.duration), AVG(e.duration), MAX(e.duration) FROM Execution e WHERE e.serviceId=:serviceId"),
        @NamedQuery(name="Execution.getAverageDurationForAllVersionsOfAllServices", query="SELECT MIN(e.duration), AVG(e.duration), MAX(e.duration) FROM Execution e")
})
public class Execution implements Serializable{


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Basic
    private String serviceId;

    @Basic
    private String serviceName;

    @Basic
    private String versionId;

    @Basic
    private int versionNum;

    @Basic
    private String invocationId;

    @Basic
    private long startTime;

    @Basic
    private long endTime;

    @Basic
    private long duration;

    @Basic
    private String blockUUID;

    @Basic
    private String workflowId;

    @Basic
    private String workflowVersionId;

    @Basic
    private int exitCode;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name="consumed_join_table",
            joinColumns = @JoinColumn( name="exec_id"),
            inverseJoinColumns = @JoinColumn( name="port_id")
    )
    private Set<Port> portDataConsumed = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name="produced_join_table",
            joinColumns = @JoinColumn( name="exec_id"),
            inverseJoinColumns = @JoinColumn( name="port_id")
    )
    private Set<Port>  portDataProduced = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Property>  properties = new HashSet<>();


    @Basic
    private String workflowEngineIp = "";
    @Basic
    private int concurrentServiceInvocations = 0;
    @Basic
    private double averageCpuSpeed = 0;
    @Basic
    private String cpuVendor = "";
    @Basic
    private String cpuModel = "";
    @Basic
    private int averageCpuCacheSize = 0;
    @Basic
    private long physicalRam = 0;
    @Basic
    private long maxMemorySize = 0;
    @Basic
    private long maxResidentMemory = 0;
    @Basic
    private String operatingSystem = "";
    @Basic
    private int cpuCount = 0;
    @Basic
    private String architecture = "";


    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    public String getInvocationId() {
        return invocationId;
    }

    public void setInvocationId(String invocationId) {
        this.invocationId = invocationId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getBlockUUID() {
        return blockUUID;
    }

    public void setBlockUUID(String blockUUID) {
        this.blockUUID = blockUUID;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkflowVersionId() {
        return workflowVersionId;
    }

    public void setWorkflowVersionId(String workflowVersionId) {
        this.workflowVersionId = workflowVersionId;
    }

    public Set<Port> getPortDataConsumed() {
        return portDataConsumed;
    }

    public void setPortDataConsumed(Set<Port> portDataConsumed) {
        this.portDataConsumed = portDataConsumed;
    }

    public String getWorkflowEngineIp() {
        return workflowEngineIp;
    }

    public void setWorkflowEngineIp(String workflowEngineIp) {
        this.workflowEngineIp = workflowEngineIp;
    }

    public int getConcurrentServiceInvocations() {
        return concurrentServiceInvocations;
    }

    public void setConcurrentServiceInvocations(int concurrentServiceInvocations) {
        this.concurrentServiceInvocations = concurrentServiceInvocations;
    }

    public double getAverageCpuSpeed() {
        return averageCpuSpeed;
    }

    public void setAverageCpuSpeed(double averageCpuSpeed) {
        this.averageCpuSpeed = averageCpuSpeed;
    }

    public String getCpuVendor() {
        return cpuVendor;
    }

    public void setCpuVendor(String cpuVendor) {
        this.cpuVendor = cpuVendor;
    }

    public String getCpuModel() {
        return cpuModel;
    }

    public void setCpuModel(String cpuModel) {
        this.cpuModel = cpuModel;
    }

    public int getAverageCpuCacheSize() {
        return averageCpuCacheSize;
    }

    public void setAverageCpuCacheSize(int averageCpuCacheSize) {
        this.averageCpuCacheSize = averageCpuCacheSize;
    }

    public long getPhysicalRam() {
        return physicalRam;
    }

    public void setPhysicalRam(long physicalRam) {
        this.physicalRam = physicalRam;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public int getCpuCount() {
        return cpuCount;
    }

    public void setCpuCount(int cpuCount) {
        this.cpuCount = cpuCount;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public Set<Port> getPortDataProduced() {
        return portDataProduced;
    }

    public void setPortDataProduced(Set<Port> portDataProduced) {
        this.portDataProduced = portDataProduced;
    }

    public Set<Property> getProperties() {
        return properties;
    }

    public void setProperties(Set<Property> properties) {
        this.properties = properties;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean addInputPort(Port port) {
        return portDataConsumed.add(port);
    }

    public boolean addOutputPort(Port port) {
        return portDataProduced.add(port);
    }

    public boolean addProperty(Property property) {
        return properties.add(property);
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    @Override
    public String toString() {
        return "Execution{" +
                "serviceId='" + serviceId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", versionNum=" + versionNum +
                ", duration=" + duration +
                ", portDataProduced=" + portDataProduced +
                ", portDataConsumed=" + portDataConsumed +
                ", properties=" + properties +
                '}';
    }

    public int getNumInputs()
    {
        return getPortDataConsumed().size();
    }

    public int getNumOutputs()
    {
        return getPortDataProduced().size();
    }

    public void setMaxMemorySize(long maxMemorySize) {
        this.maxMemorySize = maxMemorySize;
    }

    public long getMaxMemorySize() {
        return maxMemorySize;
    }

    public void setMaxResidentMemory(long maxResidentMemory) {
        this.maxResidentMemory = maxResidentMemory;
    }

    public long getMaxResidentMemory() {
        return maxResidentMemory;
    }
}