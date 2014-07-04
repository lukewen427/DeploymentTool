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
package com.connexience.server.workflow.service;

import java.io.*;

/**
 * This class is used to describe a data processor service that is added to
 * a service endpoint. It contains details of the service class, whether
 * it is a wrapped pipeline block etc.
 * @author nhgh
 */
public class DataProcessorDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	/** Class of the processor to instantiate */
    private Class<?> processorClass;

    /** Routine name that this service can processs */
    private String serviceRoutine;

    /** Is it a pipeline block */
    private boolean pipelineBlock = false;

    /** Pipeline block class */
    private Class<?> pipelineBlockClass;

    /** Construct with standard processor class */
    public DataProcessorDefinition(String serviceRoutine, Class<?> procesorClass){
        this.serviceRoutine = serviceRoutine;
        this.processorClass = procesorClass;
        pipelineBlock = false;
    }

    public boolean isPipelineBlock() {
        return pipelineBlock;
    }

    public Class<?> getPipelineBlockClass() {
        return pipelineBlockClass;
    }

    public DataProcessorDefinition setPipelineBlockClass(Class<?> pipelineBlockClass) {
        this.pipelineBlockClass = pipelineBlockClass;
        pipelineBlock = true;
        return this;
    }

    public Class<?> getProcessorClass() {
        return processorClass;
    }

    public void setProcessorClass(Class<?> processorClass) {
        this.processorClass = processorClass;
    }

    public String getServiceRoutine() {
        return serviceRoutine;
    }

    public void setServiceRoutine(String serviceRoutine) {
        this.serviceRoutine = serviceRoutine;
    }

    /** Instantiate the data processor service defined by this object */
    public DataProcessorService instantiateService() throws DataProcessorException {
        // Instantiate a standard service
        try {
            DataProcessorService service = (DataProcessorService)processorClass.newInstance();
            service.setRegisteredName(serviceRoutine);
            return service;
        } catch (Exception e){
            throw new DataProcessorException("Cannot instantiate data processor service: " + e.getMessage());
        }
    }
}