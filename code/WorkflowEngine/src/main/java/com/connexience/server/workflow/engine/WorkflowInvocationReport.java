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
package com.connexience.server.workflow.engine;

import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.drawing.model.*;

import java.util.*;

/**
 * This class holds a report of a finished workflow invocation. It contains
 * a copy of the drawing data and the execution reports generated by the drawing.
 * @author hugo
 */
public class WorkflowInvocationReport implements XmlStorable {
    /** Drawing data object */
    private XmlDataStore drawingData;
    
    /** Workflow reports */
    private Hashtable executionReports;
    
    /** Empty constructor */
    public WorkflowInvocationReport() {
    }
    
    /** Create from a WorkflowInvocation object */
    public WorkflowInvocationReport(WorkflowInvocation invocation) throws WorkflowInvocationException {
        try {
            drawingData = ((DefaultDrawingModel)invocation.getDrawing()).storeObject();
            executionReports = invocation.getExecutionReports();
        } catch (Exception e){
            throw new WorkflowInvocationException("Error creating invocation report: " + e.getMessage());
        }
    }
    
    /** Get the drawing data */
    public XmlDataStore getDrawingData(){
        return drawingData;
    }
    
    /** Get the list of execution reports */
    public Hashtable getExecutionReports(){
        return executionReports;
    }

    /** Store this object */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("WorkflowInvocationReport");
        
        return store;
    }

    /** Recreate this object from storage */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {

    }    
}