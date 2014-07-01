package com.connexience.server.model.logging.graph;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: nsjw7
 * Date: 10/09/2013
 * Time: 09:56
 * To change this template use File | Settings | File Templates.
 */
public class GraphOperationProperty implements Serializable{

    private static final long serialVersionUID = 1L;

    private long id;

    private String name;

    private String stringValue;

    public GraphOperationProperty() {
    }

    public GraphOperationProperty(String name, String stringValue) {
        this.name = name;
        this.stringValue = stringValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
