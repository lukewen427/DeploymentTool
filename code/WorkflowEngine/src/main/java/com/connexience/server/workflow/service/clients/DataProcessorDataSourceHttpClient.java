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
package com.connexience.server.workflow.service.clients;

import com.connexience.server.workflow.service.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

import java.io.*;

/**
 * This clas provides a basic http GET/POST client
 * for the DataSourceServlet. It implements the
 * DataProcessorDataSource interface so that remote
 * services can use it to access workflow data.
 * @author hugo
 */
public class DataProcessorDataSourceHttpClient implements DataProcessorDataSource {
    /** Base URL of the data source servlet */
    private String baseUrl;
    
    /** Create a client with a base URL */
    public DataProcessorDataSourceHttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    /** Get hold of a piece of input data */
    public InputStream getInputDataStream(String invocationId, String contextId, String inputName) throws DataProcessorException {
        HttpClient client = new HttpClient();
        
        GetMethod method = new GetMethod(baseUrl);
        method.getParams().setParameter("invocationid", invocationId);
        method.getParams().setParameter("contextid", contextId);
        method.getParams().setParameter("inputname", inputName);
        try {
            client.executeMethod(method);
            return method.getResponseBodyAsStream();
        } catch (Exception e){
            throw new DataProcessorException("Error getting data stream: " + e.getMessage());
        }
    }

    @Override
    public long getInputDataLength(String invocationId, String contextId, String inputName) throws DataProcessorException {
        throw new UnsupportedOperationException("Not supported for HttpDataSources");
    }


    /** Get a stream suitable for writing output data to */
    public OutputStream getOutputDataStream(String invocationId, String contextId, String outputName, String dataType) throws DataProcessorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setOutputHash(String invocationId, String contextId, String outputName, String hashValue) throws DataProcessorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getOutputHash(String invocationId, String contextId, String outputName) throws DataProcessorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getOutputDataLength(String invocationId, String contextId, String outputName) throws DataProcessorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public boolean allowsFileSystemAccess() {
        return false;
    }

    public String getStorageDirectory(String invocationId) throws DataProcessorException {
        return null;
    }
}
