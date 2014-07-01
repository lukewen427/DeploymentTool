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
import java.util.HashMap;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.dialect.H2Dialect;

/**
 * This message logs a change in the status of a workflow engine
 * @author hugo
 */
@Entity
@Table(name = "enginestatus")
public class WorkflowEngineStatusChange implements Serializable {
    public static final String PROPERTY_CPU_COUNT = "CpuCount";
    public static final String PROPERTY_CPU_SPEED = "CpuSpeed";
    public static final String PROPERTY_PHYSICAL_RAM = "PhysicalRAM";
    public static final String PROPERTY_ARCHITECTURE = "Architecture";
    public static final String PROPERTY_OPERATING_SYSTEM = "OperatingSystem";
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id; 
    
    @Basic
    private int status = WorkflowEngineInstance.ENGINE_RUNNING;
    
    @Basic
    private String ipAddress;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date observationTime = new Date();

    @Transient
    private HashMap<String,Object> extraProperties = new HashMap<>();
    
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

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
    
    public HashMap<String,Object> getExtraProperties(){
        return extraProperties;
    }
}