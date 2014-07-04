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
package com.connexience.server.workflow.cloud.services;

/**
 * This class implements a basic R data processor service that does not attempt
 * to stream data through R, instead it just passes it as single chunks
 * @author hugo
 */
import com.connexience.server.workflow.cloud.execution.InputStreamDumper;
import com.connexience.server.workflow.cloud.library.*;
import com.connexience.server.workflow.cloud.library.types.*;
import com.connexience.server.workflow.service.*;
import com.connexience.server.workflow.engine.datatypes.FileWrapper;
import com.connexience.server.workflow.engine.DataTypes;

import org.pipeline.core.data.*;
import org.pipeline.core.data.manipulation.*;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.xmldatatypes.*;
import org.pipeline.core.drawing.TransferData;

import java.io.*;
import java.util.*;
import java.text.*;
import org.pipeline.core.data.io.DelimitedTextDataImporter;

/**
 * This class provides a service that can run an 'R' script on a downloaded
 * R interpretor.
 * @author nhgh
 */
public class NonStreamingRDataProcessorService extends CloudDataProcessorService {
    /** Actual R process */
    private Process rProcess;

    /** Name of the octave binary library dependency */
    private String rLibraryName = "r-bin";

    /** Name of the octave command within the binary library */
    private String rCommandName = "R";

    /** Name of the properties variable */
    private String propertiesVariableName = "properties";

    /** Command file generated to send commands to R */
    private File commandFile = null;
    
    /** File to store stdout of the process */
    private File stdOutFile = null;
    
    /** File to store stderr of the process */
    private File stdErrFile = null;
    
    /** Print writer to send commands to octave */
    private PrintWriter commandWriter;

    /** Size of the output buffer */
    private int bufferSize = 4096;
    
    /** Mapping between outputs and data files in the invocation directory */
    private HashMap<String, File> outputFileMap = new HashMap<>();

    @Override
    public void executionAboutToStart() throws Exception {
        super.executionAboutToStart();
        
        // Create a temporary command file
        commandFile = createTempFile(getCallMessage().getContextId() + "-commands.r");
        commandWriter = new PrintWriter(new FileWriter(commandFile));
        
        // Install any required CRAN packages
        installCRANDependencies();
        
        // Assign properties to the octave runtime and make sure it is in the correct
        // working directory
        setupProcess();
        assignProperties();
        assignNonStreamingData();
        
        // Source all of the scripts files
        File contextDir = new File(getWorkingDirectory(), getCallMessage().getContextId());
        File rScriptsDir= new File(contextDir, "rfiles");
        File[] scripts = rScriptsDir.listFiles();
        for(int i=0;i<scripts.length;i++){
            sendRCommand("source('" + getForwardslashPath(scripts[i]) + "');");
        }        
        
        // Run the init.r file
        CloudWorkflowServiceLibraryItem libItem = getLibraryItem();
        if(libItem.getFile("/init.r").exists()){
            if(containsCommands(libItem.getFile("/init.r"))){
                sendRCommand("source('init.r');");
            } else {
                System.out.println("init.r contains no commands - ignoring");
            }
        }        
        
    }

    @Override
    public void allDataProcessed() throws Exception {
        super.allDataProcessed();
        sendRCommand("quit(save='no')");
        
        // Now execute the actual R process using the command data
        commandWriter.flush();
        commandWriter.close();
        executeR(commandFile);
        
        // Try and retrieve all of the output data
        String[] outputNames = getCallMessage().getDataOutputs();
        String[] outputTypes = getCallMessage().getDataOutputTypes();

        for(int i=0;i<outputNames.length;i++){
            if(outputTypes[i].equalsIgnoreCase(DataTypes.DATA_WRAPPER_TYPE.getName())){
                setOutputDataSet(outputNames[i], retrieveData(outputNames[i]));
            } else if(outputTypes[i].equalsIgnoreCase(DataTypes.FILE_WRAPPER_TYPE.getName())){
                setOutputData(outputNames[i], retrieveFileList(outputNames[i]));
            }
        }        
        
        // Set the command output data
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream(bufferSize * 2);
        
        // Standard output
        FileInputStream inStream = null;
        int size;
        
        if(stdOutFile.exists()){
            try {
                inStream = new FileInputStream(stdOutFile);
                size = Math.min((int)stdOutFile.length(), bufferSize);
                int value;
                for(int i=0;i<size;i++){
                    value = inStream.read();
                    if(value!=-1){
                        outBuffer.write(inStream.read());
                    }
                }
                outBuffer.flush();
            } catch (Exception e){
                System.out.println("Error copying standard output stream: " + e.getMessage());
            } finally {
                try {
                    inStream.close();
                } catch (Exception e){}
            }
                    
        }
        
        // Standard error
        if(stdErrFile.exists()){
            try {
                inStream = new FileInputStream(stdOutFile);
                size = Math.min((int)stdOutFile.length(), bufferSize);
                int value;
                for(int i=0;i<size;i++){
                    value = inStream.read();
                    if(value!=-1){
                        outBuffer.write(inStream.read());
                    }
                }
                outBuffer.flush();
                           
            } catch (Exception e){
                
            } finally {
                try {
                    inStream.close(); 
                } catch (Exception e){}
            }
        }
        outBuffer.flush();
        outBuffer.close();
        setCommandOutputData(outBuffer.toString());
    }

    @Override
    public void execute() throws Exception {
        /** Octave location */
        DataProcessorCallMessage message = getCallMessage();

        try {
            // Pass in the input data files as values
            String[] inputNames = message.getDataSources();
            TransferData inputDataObject;

            // Copy the input data to the interpreter
            String[] inputModes = message.getDataSourceModes();
            for(int i=0;i<inputNames.length;i++){
                inputDataObject = getInputData(inputNames[i]);
                if(inputDataObject instanceof com.connexience.server.workflow.engine.datatypes.DataWrapper && inputModes[i].equals(DataProcessorIODefinition.STREAMING_CONNECTION)){
                    // Streaming data
                    throw new Exception("Streaming data is not allowed");
                }
            }

            // Execute the code
            sendRCommand("source('main.r');");

            // Retrieve the outputs
            String[] outputNames = message.getDataOutputs();
            String[] outputTypes = message.getDataOutputTypes();

            for(int i=0;i<outputNames.length;i++){
                if(outputTypes[i].equalsIgnoreCase(DataTypes.DATA_WRAPPER_TYPE.getName())){
                    exportData(outputNames[i]);
                } else if(outputTypes[i].equalsIgnoreCase(DataTypes.FILE_WRAPPER_TYPE.getName())){
                    exportFileList(outputNames[i]);
                }
            }

        } catch (Exception e){
            throw e;
        }
    }

    /** Assign the non-streaming data connections at the start of the execution */
    private void assignNonStreamingData() throws Exception {
        // Pass in non-streamed data inputs
        DataProcessorCallMessage message = getCallMessage();
        String[] inputNames = message.getDataSources();
        String[] inputModes = message.getDataSourceModes();
        TransferData inputDataObject;
        Data data;

        for(int i=0;i<inputNames.length;i++){
            inputDataObject = getInputData(inputNames[i]);
            if(inputDataObject instanceof com.connexience.server.workflow.engine.datatypes.DataWrapper && inputModes[i].equals(DataProcessorIODefinition.NON_STREAMING_CONNECTION)){
                // Pass in a frame of data
                data = getInputDataSet(inputNames[i]);
                assignData(inputNames[i], data);

            } else if (inputDataObject instanceof FileWrapper){
                // Pass in a set of file references
                assignFileList(inputNames[i], (FileWrapper)inputDataObject);
            }
        }
    }

    /** Does a file contain valid octave commands */
    public boolean containsCommands(File file) throws Exception {
        LineNumberReader reader = null;
        try {
            if(file.exists()){
                reader = new LineNumberReader(new FileReader(file));
                String line;
                while((line=reader.readLine())!=null){
                    line = line.trim();
                    if(!line.startsWith("#") && !line.startsWith("%")){
                        return true;
                    }
                }
                return false;
            } else {
                return false;
            }
        } catch (Exception e){
            throw e;
        } finally {
            reader.close();
        }
    }

    /** Start the R process running */
    public void executeR(File cmdFile) throws Exception {
        // Get the octave binary library
        CloudWorkflowServiceLibraryItem rLibrary = getDependencyItem(rLibraryName);

        if(rLibrary!=null){
            LibraryWrapper wrapper = rLibrary.getWrapper();

            if(wrapper instanceof BinaryLibrary){
                BinaryLibrary octaveLib = (BinaryLibrary)wrapper;
                BinaryLibrary.Executable rCmd = octaveLib.getExecutable(rCommandName);
                if(rCmd!=null){

                    File rBin;
                    // Get the right command if it is absolute
                    if(rCmd.isAbsolute()){
                        rBin= new File(rCmd.getRelativeCmd());
                    } else {
                        rBin = rLibrary.getFile(rCmd.getRelativeCmd());
                    }

                    // Start R
                    /*
                    File relocatedDir = getLibraryWrapper().getRelocatedDir();
                    */

                    //String R_HOME = "R_HOME=/workflow/static/r-2.11.1/lib/R";
                    //String R_HOME = "R_HOME=" + getRHomeDir().getPath();
                    //String LD_LIBRARY_PATH = "LD_LIBRARY_PATH=";
                    //String[] env = new String[]{R_HOME};

                    String[] cmdArgs = new String[] { 
                            rBin.getPath(),
                            "--no-save",
                            "--vanilla",
                            "--slave",
                            "--file=" + getForwardslashPath(cmdFile) };
                    rProcess = Runtime.getRuntime().exec(cmdArgs);

                    // Start monitoring the process
                    notifyExternalProcessStart(rProcess);
                    
                    // Connect the IO streams
                    stdErrFile = createTempFile(getCallMessage().getContextId() + "-stderr.out");
                    FileOutputStream stdErrStream = new FileOutputStream(stdErrFile);
                    // Create and start a dumper thread
                    new InputStreamDumper(rProcess.getErrorStream(), stdErrStream, bufferSize);
                    
                    stdOutFile = createTempFile(getCallMessage().getContextId() + "-stdout.out");
                    FileOutputStream stdOutputStream = new FileOutputStream(stdOutFile);
                    // Create and start a dumper thread
                    new InputStreamDumper(rProcess.getInputStream(), stdOutputStream, bufferSize);

                    int exitCode = rProcess.waitFor();
                    
                    try {
                        stdOutputStream.close();
                    } catch (Exception e){}
                    
                    try {
                        stdErrStream.close();
                    } catch (Exception e){}                    
                    
                    if(exitCode!=0){
                        throw new Exception("Non-zero exit code from R");
                    }
                }
            }
        }
    }
    
    /** Send a command to the octave process */
    public synchronized void sendRCommand(String command) throws Exception {
        commandWriter.println(command);
        commandWriter.flush();
    }

    /** Kill the external R process */
    public void terminateR(){
        try {
            commandWriter.flush();
            commandWriter.close();
        } catch (Exception e){
            System.out.println("Error closing command writer: " + e.getMessage());
        }

        try {rProcess.getInputStream().close();} catch (Exception e){}
        try {rProcess.getErrorStream().close();} catch (Exception e){}
        try {rProcess.getOutputStream().close();} catch (Exception e){}
        
        try {
            rProcess.destroy();
        } catch (Exception e){
            System.out.println("Error stopping R: " + e.getMessage());
        }
    }


    /**
     * Returns the file path constructed with forwardslashes irrespective of the operating
     * system running the code. 
     * On Windows, File.getPath() returns file's path with backslashes which R doesn't
     * like and, so getting the path must be done through this method.
     *  
     * @param path
     * @return
     */
    private String getForwardslashPath(File path) {
        String p = path.toURI().getPath();

        // On Windows File.toURI().getPath() returns '/C:/...' and we need to get rid of the leading '/'
        // in this very special case
        if (p.length() > 2 && p.charAt(0) == '/' && p.charAt(2) == ':') {
            return p.substring(1);
        }

        return p;
    }
    
    /** Setup the R process */
    private void setupProcess() throws Exception {
        // Set the working directory
        sendRCommand("setwd('" + getForwardslashPath(getWorkingDirectory()) + "');");
    }

    /** Send in the properties data */
    private void assignProperties() throws Exception {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        XmlDataStore properties = getCallMessage().getProperties();

        String name;
        XmlDataObject property;

        StringBuffer namesVariable = new StringBuffer();
        StringBuffer valuesVariable = new StringBuffer();
        StringBuffer typesVariable = new StringBuffer();
        boolean first =true;
        boolean containsData = false;
        String prefix;

        Enumeration<?> names = properties.getNames().elements();

        namesVariable.append("names<-c(");
        valuesVariable.append("values<-c(");
        typesVariable.append("types<-c(");

        while(names.hasMoreElements()){
            name = names.nextElement().toString();
            property = properties.get(name);

            if(property instanceof XmlStringDataObject){
                if(first){
                    first = false;
                    prefix = "";
                } else {
                    prefix = ",";
                }
                namesVariable.append(prefix + "'" + name + "'");
                valuesVariable.append(prefix + "'" + ((XmlStringDataObject)property).stringValue() + "'");
                typesVariable.append(prefix + "'text'");
                containsData = true;

            } else if(property instanceof XmlDoubleDataObject){
                if(first){
                    first = false;
                    prefix = "";
                } else {
                    prefix = ",";
                }
                namesVariable.append(prefix + "'" + name + "'");
                valuesVariable.append(prefix + "'" + ((XmlDoubleDataObject)property).doubleValue() + "'");
                typesVariable.append(prefix + "'double'");
                containsData = true;

            } else if(property instanceof XmlIntegerDataObject){
                if(first){
                    first = false;
                    prefix = "";
                } else {
                    prefix = ",";
                }
                namesVariable.append(prefix + "'" + name + "'");
                valuesVariable.append(prefix + "'" + ((XmlIntegerDataObject)property).intValue() + "'");
                typesVariable.append(prefix + "'integer'");
                containsData = true;

            } else if(property instanceof XmlLongDataObject){
                if(first){
                    first = false;
                    prefix = "";
                } else {
                    prefix = ",";
                }
                namesVariable.append(prefix + "'" + name + "'");
                valuesVariable.append(prefix + "'" + ((XmlLongDataObject)property).longValue() + "'");
                typesVariable.append(prefix + "'long'");
                containsData = true;
                
            } else if(property instanceof XmlBooleanDataObject){
                if(first){
                    first = false;
                    prefix = "";
                } else {
                    prefix = ",";
                }
                namesVariable.append(prefix + "'" + name + "'");
                valuesVariable.append(prefix + "'" + ((XmlBooleanDataObject)property).booleanValue() + "'");
                typesVariable.append(prefix + "'boolean'");
                containsData = true;

            } else if(property instanceof XmlDateDataObject){
                if(first){
                    first = false;
                    prefix = "";
                } else {
                    prefix = ",";
                }
                namesVariable.append(prefix + "'" + name + "'");
                valuesVariable.append(prefix + "'" + format.format(((XmlDateDataObject)property).dateValue()) + "'");
                typesVariable.append(prefix + "'date'");
                containsData = true;

            } else if(property instanceof XmlFileDataObject){
                if(first){
                    first = false;
                    prefix = "";
                } else {
                    prefix = ",";
                }
                namesVariable.append(prefix + "'" + name + "'");
                valuesVariable.append(prefix + "'" + ((XmlFileDataObject)property).fileValue().getPath() + "'");
                typesVariable.append(prefix + "'file'");
                containsData = true;

            }
        }

        namesVariable.append(");");
        valuesVariable.append(");");
        typesVariable.append(");");
        if(containsData){
            sendRCommand(namesVariable.toString());
            sendRCommand(valuesVariable.toString());
            sendRCommand(typesVariable.toString());
            sendRCommand(propertiesVariableName + "<-data.frame(name=names,value=values,type=types);");
            sendRCommand("rm(names)");
            sendRCommand("rm(values)");
            sendRCommand("rm(types)");
        }
    }

    /** Assign a set of input data to the process */
    public void assignData(String name, Data data) throws Exception {
        NumericalColumnExtractor extractor = new NumericalColumnExtractor(data);
        Vector<?> numerical = extractor.extractColumns();

        // Numerical data
        if(numerical.size()>0){
            assignNumerical(name, numerical, extractor.getShortestNumericalColumnLength());
        }


        // Text data
        Vector<?> remaining = extractor.extractNonNumericalColumns();
        if(remaining.size()>0){
            assignNonNumerical(name, remaining, extractor.getShortestNonNumericalColumnLength());
        }

    }

    /** Assign a set of numerical data to the process */
    public void assignNumerical(String name, Vector<?> columns, int rows) throws Exception {
        sendRCommand(name + "<-matrix(nrow=0, ncol=" + columns.size() + ");");
        StringBuffer row;
        int cols = columns.size();
        NumericalColumn col;
        String value;

        for(int i=0;i<rows;i++){
            row = new StringBuffer();
            row = new StringBuffer(name + "<-rbind(" + name + ", c(");
            for(int j=0;j<cols;j++){
                col = (NumericalColumn)columns.get(j);
                if(!col.isMissing(i)){
                    value = Double.toString(col.getDoubleValue(i));
                } else {
                    value = "NA";
                }

                if(j>0){
                    row.append("," + value);
                } else {
                    row.append(value);
                }
            }
            row.append("));");
            sendRCommand(row.toString());
        }

        sendRCommand("");
    }

    /** Assign a list of files from a filewrapper */
    private void assignFileList(String name, FileWrapper wrapper) throws Exception {
        StringBuffer fileList = new StringBuffer();
        fileList.append(name + "<-c(");
        for(int i=0;i<wrapper.getFileCount();i++){
            fileList.append("\"");
            fileList.append(wrapper.getFile(i));
            fileList.append("\"");
            if(i<wrapper.getFileCount() - 1){
                // Need a ,
                fileList.append(",");
            }
        }
        fileList.append(");");
        sendRCommand(fileList.toString());
    }
    
    /** Send in the non-numerical columns of a data set */
    private void assignNonNumerical(String name, Vector<?> columns, int rows) throws Exception {
    	name += "_text";
    	sendRCommand(name + "<-matrix(nrow=0, ncol=" + columns.size() + ");");
        StringBuffer row;
        int cols = columns.size();
        Column col;
        String value;


        for(int i=0;i<rows;i++){
            row = new StringBuffer();
            row = new StringBuffer(name + "<-rbind(" + name + ", c(");
            for(int j=0;j<cols;j++){
                col = (Column)columns.get(j);
                if(!col.isMissing(i)){
                    value = col.getStringValue(i);
                } else {
                    value = "NA";
                }

                if(j>0){
                    row.append(",'" + value + "'");
                } else {
                    row.append("'" + value + "'");
                }
            }
            row.append("));");
            sendRCommand(row.toString());
        }

        sendRCommand("");
    }

    /** Export a set of data for an output */
    public void exportData(String name) throws Exception {

        // Retrieve the numerical data
        exportNumericalData(name);
        exportNonNumericalData(name);
    }
    
    /** Retrieve a set of data for and output */
    public Data retrieveData(String name) throws Exception {
        Data results = new Data();
        File tempFile;
        
        // Numerical data 
        if(outputFileMap.containsKey(name + "-numerical")){
            tempFile = outputFileMap.get(name + "-numerical");
            if(tempFile.exists()){
                DelimitedTextDataImporter importer = new DelimitedTextDataImporter();
                Data numerical = importer.importFile(tempFile);
                results.joinData(numerical, true);
            }            
        }
        
        // Non-numerical data
        if(outputFileMap.containsKey(name + "-non-numerical")){
            tempFile = outputFileMap.get(name + "-non-numerical");
            if(tempFile.exists()){
                DelimitedTextDataImporter importer = new DelimitedTextDataImporter();
                Data nonumerical = importer.importFile(tempFile);
                results.joinData(nonumerical, true);
            }            
        }
        
        return results;
    }

    /** Export a list of files from the R process */
    private void exportFileList(String name) throws Exception {
        File tempFile = this.createTempFile("tmp" + name);
        if(tempFile.exists()){
            tempFile.delete();
        }

        StringBuffer cmd = new StringBuffer();
        cmd.append("if(exists('" + name + "')){write.table(" + name + ",file=\"");
        cmd.append(getForwardslashPath(tempFile));
        cmd.append("\" ,quote=FALSE,sep=',', col.names=FALSE,row.names=FALSE);}");
        sendRCommand(cmd.toString());
        
        outputFileMap.put(name, tempFile);  // Store the file as an output
    }
    
    /** Retrieve a list of files from the R process */
    private FileWrapper retrieveFileList(String name) throws Exception {
        if(outputFileMap.containsKey(name)){
            File tempFile = outputFileMap.get(name);
            if(tempFile.exists()){
                FileWrapper files = new FileWrapper();
                BufferedReader reader = new BufferedReader(new FileReader(tempFile));
                try {
                	String line;
                	while((line=reader.readLine())!=null){
                		files.addFile(line);
                	}
                	return files;
                } finally {
                	reader.close();
                }
            } else {
                throw new Exception("Block did not write output file list: " + name);
            }            
        } else {
            throw new Exception("Block did not write an output file list for output: " + name);
        }
    }

    /** Retrieve the text part of a data set */
    private void exportNonNumericalData(String name) throws Exception {
        // write.table(m, file="/Users/hugo/Desktop/out.txt", quote=FALSE,sep=',',col.names=FALSE,row.names=FALSE)
        File tempFile = this.createTempFile("tmp" + name + "txt");
        if(tempFile.exists()){
            tempFile.delete();
        }

        StringBuffer cmd = new StringBuffer();
        cmd.append("if(exists('" + name + "_text')){write.table(" + name + "_text,file=\"");
        cmd.append(getForwardslashPath(tempFile));
        cmd.append("\" ,quote=FALSE,sep=',', col.names=TRUE,row.names=FALSE);}");
        sendRCommand(cmd.toString());
        
        outputFileMap.put(name + "-non-numerical", tempFile);

    }

    /** Retrieve a set of data for an output */
    private void exportNumericalData(String name) throws Exception {
        // write.table(m, file="/Users/hugo/Desktop/out.txt", quote=FALSE,sep=',',col.names=FALSE,row.names=FALSE)
        File tempFile = this.createTempFile("tmp" + name);
        if(tempFile.exists()){
            tempFile.delete();
        }

        StringBuffer cmd = new StringBuffer();
        cmd.append("if(exists('" + name + "')){write.table(" + name + ",file=\"");
        cmd.append(getForwardslashPath(tempFile));
        cmd.append("\" ,quote=FALSE,sep=',', col.names=TRUE,row.names=FALSE);}");
        sendRCommand(cmd.toString());
        
        outputFileMap.put(name + "-numerical", tempFile);

    }

    /** Get the R Library directory that contains the add on packages */
    public File getRLibraryDir(){
        CloudWorkflowServiceLibraryItem rLibraryItem = getDependencyItem(rLibraryName);
        File homeDir;
        if(rLibraryItem.getWrapper().isRelocated()){
            homeDir = rLibraryItem.getWrapper().getRelocatedDir();
        } else {
            homeDir = rLibraryItem.getUnpackedDir();
        }

        File libraryDir;
        File rDir;

        // Is there a lib64 directory?
        File lib64 = new File(homeDir, "lib64");
        if(lib64.isDirectory()){
            rDir = new File(lib64, "R");
            libraryDir = new File(rDir, "library");
            return libraryDir;
        } else {
            File lib = new File(homeDir, "lib");
            rDir = new File(lib, "R");
            libraryDir = new File(rDir, "library");
            return libraryDir;
        }
    }

    /** Get the R exec directory */
    public File getRHomeDir(){
        CloudWorkflowServiceLibraryItem rLibraryItem = getDependencyItem(rLibraryName);

        File homeDir;
        if(rLibraryItem.getWrapper().isRelocated()){
            homeDir = rLibraryItem.getWrapper().getRelocatedDir();
        } else {
            homeDir = rLibraryItem.getUnpackedDir();
        }
        
        File rDir;

        // Is there a lib64 directory?
        File lib64 = new File(homeDir, "lib64");
        if(lib64.isDirectory()){
            rDir = new File(lib64, "R");
            return rDir;
        } else {
            File lib = new File(homeDir, "lib");
            rDir = new File(lib, "R");
            return rDir;
        }
    }

    /** Install any dependent CRAN packages */
    public void installCRANDependencies() throws Exception {
        RServiceLibrary lib = (RServiceLibrary)getLibraryWrapper();
        int packageCount = lib.getCranPackageCount();
        for(int i=0;i<packageCount;i++){
            installCRANPackage(lib.getCranPackage(i));
        }
    }

    /** Install a CRAN package into the environment */
    public void installCRANPackage(String name) throws Exception {
        CloudWorkflowServiceLibraryItem rLibrary = getDependencyItem(rLibraryName);
        String cranRepository = rLibrary.getWrapper().getProperty("cran-repository", "http://cran.us.r-project.org");
        String quiet = rLibrary.getWrapper().getProperty("cran-quiet-install", "true");
        String cmd;
        if(quiet.equalsIgnoreCase("true")){
            cmd="if (!\"PKG\" %in% installed.packages())install.packages(\"PKG\", repos=\"REPOS\");suppressPackageStartupMessages(library(\"PKG\"));";
        } else {
            cmd="if (!\"PKG\" %in% installed.packages())install.packages(\"PKG\", repos=\"REPOS\");library(\"PKG\");";
        }
        
        cmd = cmd.replaceAll("PKG", name);
        cmd = cmd.replaceAll("REPOS", cranRepository);
        sendRCommand(cmd);
    }
}