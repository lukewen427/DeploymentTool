package com.connexience.server.model.logging.performance;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "properties")
public class Property implements Serializable {
    public enum PropertyType {
        GATHERED, SPECIFIED
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @Basic
    private String name;
    
    @Basic
    private Double value;
    
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    public Property() {
    }

    public Property(String name, PropertyType propertyType, Double value) {
        this.name = name;
        this.value = value;
        this.propertyType = propertyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    @Override
    public String toString() {
        return "Property{"
                + "value=" + value
                + ", name='" + name + '\''
                + '}';
    }
}
