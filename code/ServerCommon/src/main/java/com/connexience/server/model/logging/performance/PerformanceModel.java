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
import java.util.List;
import java.util.Vector;
import javax.persistence.*;
import org.json.JSONObject;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.manipulation.ColumnPicker;
import org.pipeline.core.data.manipulation.ColumnPickerCollection;
import org.apache.log4j.*;
import org.json.JSONArray;

/**
 * This is the base class for a model of performance. There are specific
 * types of model for different things of interest.
 * @author hugo
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="modelType")
@NamedQueries({
    @NamedQuery(name = "PerformanceModel.getModel", query="SELECT m FROM PerformanceModel m WHERE m.id=:id"),
    @NamedQuery(name = "PerformanceModel.getModelForService", query="SELECT m FROM PerformanceModel m WHERE m.serviceId=:serviceId AND m.versionId=:versionId"),
    @NamedQuery(name = "PerformanceModel.getAllModelsForService", query="SELECT m FROM PerformanceModel m WHERE m.serviceId=:serviceId"),
    @NamedQuery(name = "PerformanceModel.getSpecifiedModels", query="SELECT m from PerformanceModel m WHERE m.serviceId=:serviceId AND m.versionId=:versionId AND m.modelledObjectType=:modelledObjectType AND m.modelledObjectName=:objectName"),
    @NamedQuery(name = "PerformanceModel.getDurationModelsForService", query="SELECT m from PerformanceModel m WHERE m.serviceId=:serviceId AND m.versionId=:versionId AND m.modelledObjectType=:modelledObjectType")
})  

public abstract class PerformanceModel implements Serializable {
    private static Logger logger = Logger.getLogger(PerformanceModel.class);
    
    /** Database ID */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
        
    /** Modelled object type enumerator */
    public enum ModelledObjectType {
        OUTPUT_SIZE, DURATION, ARBITRARY, NONE
    }
        
    /** What type of object does this model predicted */
    @Enumerated(EnumType.STRING)
    private ModelledObjectType modelledObjectType = ModelledObjectType.DURATION;
    
    /** Name of the variable that this model can predict */
    @Basic
    private String modelledObjectName;
    
    /** Service that this model relates to */
    @Basic
    private String serviceId;
    
    /** ID of the version that this model relates to */
    @Basic
    private String versionId;
    
    @Basic
    private int versionNumber;
    
    /** Can this model be updated or does a new model have to be created */
    @Basic
    private boolean updatable = true;

    /** Training results */
    @OneToOne(orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "modelId")
    private PerformanceModelResults trainingResults;
    
    /** Model parameter data. This is stored as JSON in the database because all
     * models will have different configurations. */
    @Basic
    protected double[] modelParameters;
    
    @Column(name = "inputSelections", columnDefinition = "text")
    private String inputSelections;
    
    @Transient
    private PerformanceModelParameter[] inputSelectionList;
    
    /** Model construction timestamp. This needed to check if there is any new
     * data to add to the model */
    @Temporal(TemporalType.TIMESTAMP)
    private Date constuctionTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ColumnPickerCollection getColumnList() {
        if(inputSelectionList!=null){
            ColumnPickerCollection pickers = new ColumnPickerCollection();
            for(int i=0;i<inputSelectionList.length;i++){
                try {
                    pickers.addColumnPicker(new ColumnPicker(inputSelectionList[i].getColumnName()));
                } catch (Exception e){
                    logger.error("Error adding column picker: " + e.getMessage());
                }
            }
            return pickers;
        } else {
            return new ColumnPickerCollection();
        }
        
    }

    public void setColumnList(Vector dataColumns) {
        org.pipeline.core.data.Column col;
        inputSelectionList = new PerformanceModelParameter[dataColumns.size()];
        for(int i=0;i<dataColumns.size();i++){
            col = (org.pipeline.core.data.Column)dataColumns.get(i);
            inputSelectionList[i] = new PerformanceModelParameter(col.getName(), col.getProperties().stringValue("ParameterType", "UNKNOWN"));
        }
    }
    
    /** Get a list of the model inputs as PerformanceModelParameter objects */
    public PerformanceModelParameter[] getInputSelectionList(){
        return inputSelectionList;
    }
    
    public ModelledObjectType getModelledObjectType() {
        return modelledObjectType;
    }

    public void setModelledObjectType(ModelledObjectType modelledObjectType) {
        this.modelledObjectType = modelledObjectType;
    }

    public String getModelledObjectName() {
        return modelledObjectName;
    }

    public void setModelledObjectName(String modelledObjectName) {
        this.modelledObjectName = modelledObjectName;
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

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public double[] getModelParameters() {
        return modelParameters;
    }

    public void setModelParameters(double[] modelParameters) {
        this.modelParameters = modelParameters;
    }

    public Date getConstuctionTime() {
        return constuctionTime;
    }

    public void setConstuctionTime(Date constuctionTime) {
        this.constuctionTime = constuctionTime;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    @PrePersist
    @PreUpdate
    public void serializeJson(){
        if(inputSelectionList!=null){
            JSONArray json = new JSONArray();
            for(int i=0;i<inputSelectionList.length;i++){
                json.put(inputSelectionList[i].toJson());
            }
            inputSelections = json.toString();
        }
    }
    
    @PostLoad
    public void parseParameterJson(){
        try {
            JSONArray json = new JSONArray(inputSelections);
            inputSelectionList = new PerformanceModelParameter[json.length()];
            for(int i=0;i<json.length();i++){
                inputSelectionList[i] = new PerformanceModelParameter(json.getJSONObject(i));
            }
        } catch (Exception e){
            inputSelectionList = new PerformanceModelParameter[0];
        }
    }

    /** Get the model performance statistics */
    public PerformanceModelResults getTrainingResults() {
        return trainingResults;
    }

    public void setTrainingResults(PerformanceModelResults trainingResults) {
        this.trainingResults = trainingResults;
        this.constuctionTime = trainingResults.getBuildDate();
    }
    
    
    /** Get a prediction given a set of data */
    public abstract List<PerformanceModelPrediction> getPredictions(Data xData) throws Exception;
    
    /** Get a summary of the parameters used in this model */
    public abstract PerformanceModelParameter[] getModelParameterSummary();
}