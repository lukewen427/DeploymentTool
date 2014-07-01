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
 * This class holds quality results of a performance model. It contains things
 * like RMS errors, correlation etc. It is not stored in the database, but can
 * be generated from a PredictionModel
 * @author hugo
 */
@Entity
@Table(name = "modelresults")
@NamedQueries({
    @NamedQuery(name = "PerformanceModelResults.listServiceIdsWithModel", query="SELECT DISTINCT r.serviceId FROM PerformanceModelResults r"),
    @NamedQuery(name = "PerformanceModelResults.listServiceVersionsWithModel", query="SELECT r FROM PerformanceModelResults r WHERE r.serviceId=:serviceId")
})
public class PerformanceModelResults implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;    
    private int trainingPoints;
    private int testingPoints;
    private double trainingRMSError;
    private double testingRMSError;
    private String serviceId;
    private String serviceName;
    private String versionId;
    private long modelId;
    private String modelType;
    private int versionNumber;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date buildDate;

    public PerformanceModelResults() {
    }

    public PerformanceModelResults(PerformanceModelResults exisiting){
        this.trainingPoints = exisiting.getTrainingPoints();
        this.testingPoints = exisiting.getTestingPoints();
    }

    public int getTrainingPoints() {
        return trainingPoints;
    }

    public void setTrainingPoints(int trainingPoints) {
        this.trainingPoints = trainingPoints;
    }

    public int getTestingPoints() {
        return testingPoints;
    }

    public void setTestingPoints(int testingPoints) {
        this.testingPoints = testingPoints;
    }

    public Date getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(Date buildDate) {
        this.buildDate = buildDate;
    }

    public double getTestingRMSError() {
        return testingRMSError;
    }

    public void setTestingRMSError(double testingRMSError) {
        this.testingRMSError = testingRMSError;
    }

    public double getTrainingRMSError() {
        return trainingRMSError;
    }

    public void setTrainingRMSError(double trainingRMSError) {
        this.trainingRMSError = trainingRMSError;
    }

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

    public long getModelId() {
        return modelId;
    }

    public void setModelId(long modelId) {
        this.modelId = modelId;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }
}