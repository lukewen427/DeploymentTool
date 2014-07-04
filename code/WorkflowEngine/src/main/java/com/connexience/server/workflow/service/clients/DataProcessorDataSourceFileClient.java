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
import com.connexience.server.workflow.engine.*;
import com.connexience.server.workflow.util.ZipUtils;
import java.io.*;

/**
 * This class provides file based access to the global data
 * store for local services that can mount the data store
 * directory via NFS or SAMBA.
 * @author hugo
 */
public class DataProcessorDataSourceFileClient implements DataProcessorDataSource
{
    /** Base directory file */
    private File dir;


    /** Construct with a base directory */
    public DataProcessorDataSourceFileClient(String baseDirectory)
    {
        dir = new File(baseDirectory);
    }


    /** Get an Input data stream connected to a data source resource */
    public InputStream getInputDataStream(String invocationId, String contextId, String inputName) throws DataProcessorException {
        File dataFile = new File(dir + File.separator + invocationId + File.separator + inputName + "-" + contextId + ".dat");
        if(dataFile.exists()){
            try {
                return new FileInputStream(dataFile);
            } catch (Exception e){
                throw new DataProcessorException("IOException opening data file: " + e.getMessage());
            }
        } else {
            throw new DataProcessorException("Cannot locate data file for input: " + inputName);
        }
    }

    @Override
    public long getInputDataLength(String invocationId, String contextId, String inputName) throws DataProcessorException {
        File dataFile = new File(dir + File.separator + invocationId + File.separator + inputName + "-" + contextId + ".dat");
        if(dataFile.exists()){
            return dataFile.length();
        } else {
            throw new DataProcessorException("Cannot locate data file for input: " + inputName);
        }
    }

    @Override
    public long getOutputDataLength(String invocationId, String contextId, String outputName) throws DataProcessorException {
        File dataFile = new File(dir + File.separator + invocationId + File.separator + outputName + "-" + contextId + ".dat");
        if(dataFile.exists()){
            return dataFile.length();
        } else {
            throw new DataProcessorException("Cannot locate data file for output: " + outputName);
        }
    }

    /** Get an Output data stream connected to a data output resource */
    public OutputStream getOutputDataStream(String invocationId, String contextId, String outputName, String dataType) throws DataProcessorException {
        File dataFile = new File(dir + File.separator + invocationId + File.separator + outputName + "-" + contextId + ".dat");
        File classNameFile = new File(dir + File.separator + invocationId + File.separator + outputName + "-" + contextId + "-class-name.txt");
        
        // Write the class name 
        try {
            if(classNameFile.exists()){
                classNameFile.delete();
            }
            FileOutputStream stream = new FileOutputStream(classNameFile);
            PrintWriter writer = new PrintWriter(stream);
            writer.println(DataTypes.getDataType(dataType).getDataClass().getName());
            writer.flush();
            stream.flush();
            stream.getFD().sync();
            writer.close();
        } catch (Exception e){
            throw new DataProcessorException("Cannot save data type information: " + e.getMessage());
        }
        
        
        // Get an output stream to the save file
        try {
            if(dataFile.exists()){
                dataFile.delete();
            }
            return new FileOutputStream(dataFile);
        } catch (Exception e){
            throw new DataProcessorException("Cannot open file output stream: " + e.getMessage());
        }
    }

    @Override
    public void setOutputHash(String invocationId, String contextId, String outputName, String hashValue) throws DataProcessorException {
        try {
            File md5File = new File(dir + File.separator + invocationId + File.separator + outputName + "-" + contextId + ".md5");
            ZipUtils.writeSingleLineFile(md5File, hashValue);
        } catch (Exception e){
            throw new DataProcessorException("Error writing MD5 for output: " + outputName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String getOutputHash(String invocationId, String contextId, String outputName) throws DataProcessorException {
        try {
            File md5File = new File(dir + File.separator + invocationId + File.separator + outputName + "-" + contextId + ".md5");
            if(md5File.exists()){
                return ZipUtils.readFirstLineOfFile(md5File);
            } else {
                return null;
            }
        } catch (Exception e){
            throw new DataProcessorException("Error reading MD5 for output: " + outputName + ": " + e.getMessage(), e);
        }
    }
    
    /** This data source allows reading / writing to the filesystem */
    public boolean allowsFileSystemAccess() {
        return true;
    }

    /** Get the working directory for a specific invocation */
    public String getStorageDirectory(String invocationId) throws DataProcessorException {
        return dir + File.separator + invocationId;
    }
}