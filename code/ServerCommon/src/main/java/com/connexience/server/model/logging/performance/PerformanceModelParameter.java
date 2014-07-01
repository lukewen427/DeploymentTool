/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.connexience.server.model.logging.performance;

import org.json.JSONObject;
import org.apache.log4j.*;

/**
 * This class contains a referernce to a parameter used in a performance model
 * @author hugo
 */
public class PerformanceModelParameter {
    private static Logger logger = Logger.getLogger(PerformanceModelParameter.class);
    public static final String MODEL_PARAMETER = "ModelParameter";
    public static final String INPUT_SIZE = "InputSize";
    public static final String OUTPUT_SIZE = "OutputSize";
    public static final String MACHINE_DATA = "MachineData";
    public static final String SENSOR_DATA = "SensorData";
    public static final String SERVICE_PROPERTY = "ServiceProperty";
    
    /** Name of the column */
    private String columnName;
    
    /** Type of the column */
    private String columnType;

    /** Value of this parameter */
    private double value;
    
    public PerformanceModelParameter() {
    }

    public PerformanceModelParameter(JSONObject json) {
        parseJson(json);
    }
    
    public PerformanceModelParameter(String columnName, String columnType) {
        this.columnType = columnType;
        this.columnName = columnName;
    }

    public PerformanceModelParameter(String columnName, String columnType, double value){
        this.columnName = columnName;
        this.columnType = columnType;
        this.value = value;
    }
    
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("columnType", columnType);
            json.put("columnName", columnName);
            json.put("value", value);
        } catch (Exception e){
            logger.error("JSONError: " + e.getMessage());
        }
        return json;
    }
    
    public void parseJson(JSONObject json) {
        try {
            columnName = json.getString("columnName");
            columnType = json.getString("columnType");
            if(json.has("value")){
                value = json.getDouble("value");
            } else {
                value = 0;
            }
        } catch (Exception e){
            logger.error("Exception in parseJson: " + e.getMessage());
        }
    }
}