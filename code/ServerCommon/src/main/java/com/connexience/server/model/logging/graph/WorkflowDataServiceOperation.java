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
package com.connexience.server.model.logging.graph;

import java.util.Date;

/**
 * This operation occurs whenever a service is invoked in a workflow.
 *
 * @author hugo
 */
public class WorkflowDataServiceOperation extends WorkflowGraphOperation {
    /**
     * Class version UID.
     * <p/>
     * Please increment this value whenever your changes may cause
     * incompatibility with the previous version of this class. If unsure, ask
     * one of the core development team or read:
     * http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     * http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 1L;


    /**
     * Serialized call properties. This is used to extract the
     * properties sent to the service when it was called
     */
    private byte[] propertiesData;

    /**
     * ID of the service document
     */
    private String serviceId;

    /**
     * Version ID of the service document
     */
    private String versionId;

    /**
     * Version nunmber of the service document
     */
    private int versionNum;

    /**
     * Dependency IDs
     */
    private String[] dependencyIds;

    /**
     * Dependency version IDs
     */
    private String[] dependencyVersionIds;

    /**
     * Dependency names
     */
    private String[] dependencyNames;

    /**
     * Service end time
     */
    private Date endTimestamp;

    /**
     * Name of the service document
     */
    private String serviceName;

    /**
     * Context UUID of the block running this service
     */
    private String blockUUID;

    /**
     * VersionId of the workflow that contains this service
     */
    private String workflowVersionId;

    /**
     * Is the service idempotent
     */
    private boolean idempotent = true;

    /**
     * Is the service deterministic
     */
    private boolean deterministic = true;

    /**
     * The amount of data this service consumed
     */
    private long dataConsumedSize = 0;

    /**
     * The amount of data thsi service produced
     */
    private long dataProducedSize = 0;

    /**
     * Id of the workflow containing this service
     */
    private String workflowId = "";

    /**
     * IP of the machine running this workflow
     */
    private String workflowEngineIp = "";

    /**
     * Number of services running at the same time as this one
     */
    private int concurrentServiceInvocations = 0;

    /**
     * Performance details
     */
    private double averageCpuSpeed = 0;
    private String cpuVendor = "";
    private String cpuModel = "";
    private int averageCpuCacheSize = 0;
    private long physicalRam = 0;
    private String operatingSystem = "";
    private int cpuCount = 0;
    private String architecture = "";


    public String[] getDependencyIds() {
        return dependencyIds;
    }

    public void setDependencyIds(String[] dependencyIds) {
        this.dependencyIds = dependencyIds;
    }

    public String[] getDependencyNames() {
        return dependencyNames;
    }

    public void setDependencyNames(String[] dependencyNames) {
        this.dependencyNames = dependencyNames;
    }

    public String[] getDependencyVersionIds() {
        return dependencyVersionIds;
    }

    public void setDependencyVersionIds(String[] dependencyVersionIds) {
        this.dependencyVersionIds = dependencyVersionIds;
    }

    public byte[] getPropertiesData() {
        return propertiesData;
    }

    public void setPropertiesData(byte[] propertiesData) {
        this.propertiesData = propertiesData;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public Date getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Date endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getBlockUUID() {
        return blockUUID;
    }

    public void setBlockUUID(String blockUUID) {
        this.blockUUID = blockUUID;
    }

    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    public String getWorkflowVersionId() {
        return workflowVersionId;
    }

    public void setWorkflowVersionId(String workflowVersionId) {
        this.workflowVersionId = workflowVersionId;
    }

    public boolean isIdempotent() {
        return idempotent;
    }

    public void setIdempotent(boolean idempotent) {
        this.idempotent = idempotent;
    }

    public boolean isDeterministic() {
        return deterministic;
    }

    public void setDeterministic(boolean deterministic) {
        this.deterministic = deterministic;
    }

    public long getDataConsumedSize() {
        return dataConsumedSize;
    }

    public void setDataConsumedSize(long dataConsumedSize) {
        this.dataConsumedSize = dataConsumedSize;
    }

    public long getDataProducedSize() {
        return dataProducedSize;
    }

    public void setDataProducedSize(long dataProducedSize) {
        this.dataProducedSize = dataProducedSize;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
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
}
