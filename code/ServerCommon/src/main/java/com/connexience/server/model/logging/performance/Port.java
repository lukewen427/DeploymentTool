package com.connexience.server.model.logging.performance;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ports")
@NamedQueries({
    @NamedQuery(name="Port.getPortSizeStatsForAllVersionsOfService", query="SELECT MIN(p.data), AVG(p.data), MAX(p.data) FROM Port p WHERE p.serviceId=:serviceId AND p.name=:portName"),
    @NamedQuery(name="Port.getPortSizeStatsForAllPortsOfAllServices", query="SELECT MIN(p.data), AVG(p.data), MAX(p.data) FROM Port p")
})
public class Port implements Serializable
{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Basic
    private String name;

    @Basic
    private String serviceId;
    
    @Basic
    private String versionId;
    
    @Basic
    private Long data;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
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

    @Override
    public String toString() {
        return "Port{" +
                "name='" + name + '\'' +
                ", data=" + data +
                '}';
    }
}
