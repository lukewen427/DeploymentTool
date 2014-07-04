/**
 * e-Science Central Copyright (C) 2008-2013 School of Computing Science,
 * Newcastle University
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation at: http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package com.connexience.server.workflow.cloud.services;

import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.workflow.cloud.execution.DataProcessorServiceRunner;
import com.connexience.server.workflow.cloud.library.*;
import com.connexience.server.workflow.cloud.library.types.*;
import com.connexience.server.workflow.service.*;
import com.connexience.server.workflow.engine.datatypes.DataWrapper;
import com.connexience.server.workflow.engine.datatypes.FileWrapper;
import com.connexience.server.workflow.engine.DataTypes;
import org.pipeline.core.data.*;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.xmldatatypes.*;
import org.pipeline.core.data.io.DelimitedTextDataImporter;
import org.pipeline.core.drawing.TransferData;

import java.io.*;
import java.util.*;
import java.text.*;

/**
 * This class provides a service that can run an 'R' script on a downloaded R
 * interpretor.
 *
 * @author nhgh
 */
public class RDataProcessorService extends CloudDataProcessorService {

    /**
     * Actual R process
     */
    private Process rProcess;
    /**
     * Name of the octave binary library dependency
     */
    private String rLibraryName = "r-bin";
    /**
     * Name of the octave command within the binary library
     */
    private String rCommandName = "R";
    /**
     * Name of the properties variable
     */
    private String propertiesVariableName = "properties";
    /**
     * Input stream storage
     */
    private ByteArrayOutputStream outputStore;
    /**
     * Error stream storage
     */
    private ByteArrayOutputStream errorStore;
    /**
     * Print writer to send commands to octave
     */
    private PrintWriter commandWriter;
    /**
     * Is this class waiting for some output from Octave
     */
    private volatile boolean waitingForOutput = false;
    /**
     * Text to be used for the "done" marker
     */
    private String doneMarkerText = "+-=_CONNEXIENCE_DONE_MARKER_=-+";
    /**
     * Output stream dumper
     */
    private Dumper dd1;
    /**
     * Error stream dumper
     */
    private ErrorDumper er1;
    /**
     * Size of the output buffer
     */
    private int bufferSize = 4096;
    /**
     * R watchdog thread
     */
    private RWatchdog rWatcher = null;

    public RDataProcessorService() {
        setExternalIOConnectionSupported(true);
    }

    @Override
    public void executionAboutToStart() throws Exception {

        super.executionAboutToStart();
        bufferSize = getCallMessage().getStdOutBufferSize();
        startR();

        // Assign properties to the octave runtime and make sure it is in the correct
        // working directory
        setupProcess();
        assignProperties();
        assignNonStreamingData();

        // Install any required CRAN packages
        installCRANDependencies();

        // Source all of the scripts files
        File contextDir = new File(getWorkingDirectory(), getCallMessage().getContextId());
        File rScriptsDir = new File(contextDir, "rfiles");
        File[] scripts = rScriptsDir.listFiles();
        for (int i = 0; i < scripts.length; i++) {
            sendRCommand("source('" + getForwardslashPath(scripts[i]) + "');", true);
        }

        // Run the init.r file
        CloudWorkflowServiceLibraryItem libItem = getLibraryItem();
        if (libItem.getFile("/init.r").exists()) {
            if (containsCommands(libItem.getFile("/init.r"))) {
                sendRCommand("source('init.r');", true);
            } else {
                System.out.println("init.r contains no commands - ignoring");
            }
        }

    }

    /**
     * Assign the non-streaming data connections at the start of the execution
     */
    private void assignNonStreamingData() throws Exception {
        // Pass in non-streamed data inputs
        DataProcessorCallMessage message = getCallMessage();
        String[] inputNames = message.getDataSources();
        String[] inputModes = message.getDataSourceModes();
        TransferData inputDataObject;
        Data data;

        for (int i = 0; i < inputNames.length; i++) {
            inputDataObject = getInputData(inputNames[i]);
            if (inputDataObject instanceof DataWrapper && inputModes[i].equals(DataProcessorIODefinition.NON_STREAMING_CONNECTION)) {
                // Pass in a frame of data
                data = getInputDataSet(inputNames[i]);
                assignData(inputNames[i], data);

            } else if (inputDataObject instanceof FileWrapper) {
                // Pass in a set of file references
                assignFileList(inputNames[i], (FileWrapper) inputDataObject);
            }
        }
    }

    /**
     * Does a file contain valid octave commands
     */
    public boolean containsCommands(File file) throws Exception {
        LineNumberReader reader = null;
        try {
            if (file.exists()) {
                reader = new LineNumberReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.startsWith("#") && !line.startsWith("%")) {
                        return true;
                    }
                }
                return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            reader.close();
        }
    }

    /**
     * Start the R process running
     */
    public void startR() throws Exception {
        // Get the octave binary library
        CloudWorkflowServiceLibraryItem rLibrary = getDependencyItem(rLibraryName);

        if (rLibrary != null) {
            LibraryWrapper wrapper = rLibrary.getWrapper();

            if (wrapper instanceof BinaryLibrary) {
                BinaryLibrary octaveLib = (BinaryLibrary) wrapper;
                BinaryLibrary.Executable rCmd = octaveLib.getExecutable(rCommandName);
                if (rCmd != null) {

                    File rBin;
                    // Get the right command if it is absolute
                    if (rCmd.isAbsolute()) {
                        rBin = new File(rCmd.getRelativeCmd());
                    } else {
                        rBin = rLibrary.getFile(rCmd.getRelativeCmd());
                    }

                    outputStore = new ByteArrayOutputStream(bufferSize);
                    errorStore = new ByteArrayOutputStream(bufferSize);

                    // Start R
                    String[] cmdArgs = new String[] {
                            rBin.getPath(),
                            "--vanilla",
                    "--slave" };
                    rProcess = Runtime.getRuntime().exec(cmdArgs);

                    rWatcher = new RWatchdog();
                    rWatcher.start();

                    // Connect the IO streams
                    commandWriter = new PrintWriter(rProcess.getOutputStream());
                    dd1 = new Dumper(rProcess.getInputStream(), System.out);
                    dd1.start();
                    er1 = new ErrorDumper(rProcess.getErrorStream(), System.out, dd1);
                    er1.start();

                    // Notify the start of an external process
                    notifyExternalProcessStart(rProcess);                    
                }
            }
        }
    }

    @Override
    public void allDataProcessed() throws Exception {
        super.allDataProcessed();
        dd1.setFinished();
        er1.setFinished();
        terminateR();

        StringBuffer outputBuffer = new StringBuffer(outputStore.size() + errorStore.size());
        outputBuffer.append(new String(outputStore.toByteArray()));
        outputBuffer.append(new String(errorStore.toByteArray()));
        setCommandOutputData(outputBuffer.toString());
    }

    @Override
    public void prepareForDebugging() {
        super.prepareForDebugging();
        try {
            sendRCommand("options(error=dump.frames);", false);
        } catch (Exception e) {
            System.out.println("Error preparing R process for debugging: " + e.getMessage());
        }
    }

    @Override
    public void undoDebugPreparations() {
        super.undoDebugPreparations();
        try {
            sendRCommand("options(error=NULL);", false);
        } catch (Exception e) {
            System.out.println("Error undoing R process debugging preparations: " + e.getMessage());
        }
    }

    @Override
    public void sendCommand(String cmd) throws Exception {
        sendRCommand(cmd, false);
    }

    /**
     * Send a command to the octave process
     */
    private void sendRCommand(String command, boolean waitForResponse) throws Exception {
        if (rProcess == null) {
            throw new Exception("Error sending command. R not running");
        }

        if (waitForResponse) {
            er1.clearErrorFlag();
            dd1.clearExtraneousDone();
            dd1.clearWaitingFlag();
            commandWriter.println(command);
            commandWriter.flush();

            waitingForOutput = true;
            dd1.waitFor(doneMarkerText);

            // FIXME: the additional print is needed as sometimes a line may already be started
            // and then waitingForOutput would block indefinitely.
            commandWriter.println("print(''); print('" + doneMarkerText + "');");
            commandWriter.flush();

            // waitingForOutput should be set false by Dumper or ErrorDumper via signalReceived
            synchronized (this) {
                while (waitingForOutput) {
                    try {
                        this.wait(1000);
                    } catch (InterruptedException x) {
                        System.out.println("Exception in sendRCommand ignored: " + x.getMessage());
                    }
                }
            }

            // Check for error
            if (er1.isErrorFlagSet()) {
                throw new Exception("R interpretor error: " + er1.getErrorText());
            }
        } else {
            commandWriter.println(command);
            commandWriter.flush();
        }
    }

    /**
     * Kill the external R process
     */
    public void terminateR() {
        try {
            commandWriter.close();
        } catch (Exception e) {
            System.out.println("Error closing command writer: " + e.getMessage());
        }

        rWatcher.setStopFlag();
        try {
            rProcess.getInputStream().close();
        } catch (Exception e) {
            System.out.println("Error closing R standard output stream: " + e.getMessage());
        }
        try {
            rProcess.getErrorStream().close();
        } catch (Exception e) {
            System.out.println("Error closing R standard error stream: " + e.getMessage());
        }
        try {
            rProcess.getOutputStream().close();
        } catch (Exception e) {
            System.out.println("Error closing R standard input stream: " + e.getMessage());
        }

        try {
            rProcess.destroy();
        } catch (Exception e) {
            System.out.println("Error stopping R: " + e.getMessage());
        }
    }

    @Override
    public void execute() throws Exception {
        /**
         * Octave location
         */
        DataProcessorCallMessage message = getCallMessage();

        try {
            // Pass in the input data files as values
            String[] inputNames = message.getDataSources();
            Data data;
            TransferData inputDataObject;

            // Copy the input data to the interpreter
            String[] inputModes = message.getDataSourceModes();
            for (int i = 0; i < inputNames.length; i++) {
                inputDataObject = getInputData(inputNames[i]);
                if (inputDataObject instanceof DataWrapper && inputModes[i].equals(DataProcessorIODefinition.STREAMING_CONNECTION)) {
                    // Pass in a frame of data
                    data = getInputDataSet(inputNames[i]);
                    assignData(inputNames[i], data);
                }
            }

            // Execute the code
            sendRCommand("source('main.r');", true);

            // Retrieve the outputs
            String[] outputNames = message.getDataOutputs();
            String[] outputTypes = message.getDataOutputTypes();
            Data outData;

            for (int i = 0; i < outputNames.length; i++) {
                if (outputTypes[i].equalsIgnoreCase(DataTypes.DATA_WRAPPER_TYPE.getName())) {
                    outData = retrieveData(outputNames[i]);
                    setOutputDataSet(outputNames[i], outData);
                } else if (outputTypes[i].equalsIgnoreCase(DataTypes.FILE_WRAPPER_TYPE.getName())) {
                    FileWrapper files = retrieveFileList(outputNames[i]);
                    setOutputData(outputNames[i], files);
                }
            }

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Returns the file path constructed with forwardslashes irrespective of the
     * operating system running the code. On Windows, File.getPath() returns
     * file's path with backslashes which R doesn't like and, so getting the
     * path must be done through this method.
     *
     * @param path
     * @return
     */
    private static String getForwardslashPath(File path) {
        String p = path.toURI().getPath();

        // On Windows File.toURI().getPath() returns '/C:/...' and we need to get rid of the leading '/'
        // in this very special case
        if (p.length() > 2 && p.charAt(0) == '/' && p.charAt(2) == ':') {
            return p.substring(1);
        }

        return p;
    }

    /**
     * Setup the R process
     */
    private void setupProcess() throws Exception {
        // Set the working directory
        sendRCommand("options(echo=F);", false);
        sendRCommand("setwd('" + getForwardslashPath(getWorkingDirectory()) + "');", true);
    }

    /**
     * Send in the properties data
     */
    private void assignProperties() throws Exception {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        XmlDataStore properties = getCallMessage().getProperties();
        
        // Add the block library directory
        properties.add("BlockLibraryPath", getLibraryItem().getUnpackedDir().getPath());
        
        Enumeration<?> names = properties.getNames().elements();

        StringBuilder namesVariable = new StringBuilder();
        StringBuilder valuesVariable = new StringBuilder();
        StringBuilder typesVariable = new StringBuilder();

        namesVariable.append("names<-c(");
        valuesVariable.append("values<-c(");
        typesVariable.append("types<-c(");

        // setup all special properties
        namesVariable.append("'InvocationId',");
        valuesVariable.append("'" + getCallMessage().getInvocationId() + "',");
        typesVariable.append("'text',");

        while (names.hasMoreElements()) {
            String name = names.nextElement().toString();
            XmlDataObject property = properties.get(name);

            if (property instanceof XmlStringDataObject) {
                namesVariable.append("'" + name + "',");
                valuesVariable.append("'" + ((XmlStringDataObject) property).stringValue() + "',");
                typesVariable.append("'text',");
            } else if (property instanceof XmlDoubleDataObject) {
                namesVariable.append("'" + name + "',");
                valuesVariable.append("'" + ((XmlDoubleDataObject) property).doubleValue() + "',");
                typesVariable.append("'double',");
            } else if (property instanceof XmlIntegerDataObject) {
                namesVariable.append("'" + name + "',");
                valuesVariable.append("'" + ((XmlIntegerDataObject) property).intValue() + "',");
                typesVariable.append("'integer',");
            } else if (property instanceof XmlLongDataObject) {
                namesVariable.append("'" + name + "',");
                valuesVariable.append("'" + ((XmlLongDataObject) property).longValue() + "',");
                typesVariable.append("'long',");
            } else if (property instanceof XmlBooleanDataObject) {
                namesVariable.append("'" + name + "',");
                valuesVariable.append("'" + ((XmlBooleanDataObject) property).booleanValue() + "',");
                typesVariable.append("'boolean',");
            } else if (property instanceof XmlDateDataObject) {
                namesVariable.append("'" + name + "',");
                valuesVariable.append("'" + format.format(((XmlDateDataObject) property).dateValue()) + "',");
                typesVariable.append("'date',");
            } else if (property instanceof XmlStorableDataObject) {
                if (property.getValue() instanceof DocumentRecord) {
                    DocumentRecord wrapper = (DocumentRecord) property.getValue();
                    DocumentRecord document = createApiLink().getDocument(wrapper.getId());
                    File downloadFile = createTempFile(wrapper.getName());
                    createApiLink().downloadToFile(document, downloadFile);

                    namesVariable.append("'" + name + "',");
                    valuesVariable.append("'" + downloadFile.getName() + "',");
                    typesVariable.append("'file',");
                }
            }
        }

        // Get rid of the trailing comma
        if (namesVariable.length() > 0) {
            namesVariable.setLength(namesVariable.length() - 1);
        }
        if (valuesVariable.length() > 0) {
            valuesVariable.setLength(valuesVariable.length() - 1);
        }
        if (typesVariable.length() > 0) {
            typesVariable.setLength(typesVariable.length() - 1);
        }

        namesVariable.append(");");
        valuesVariable.append(");");
        typesVariable.append(");");

        sendRCommand(namesVariable.toString(), false);
        sendRCommand(valuesVariable.toString(), false);
        sendRCommand(typesVariable.toString(), false);
        sendRCommand(propertiesVariableName + "<-data.frame(value=values,type=types,stringsAsFactors=FALSE);", false);
        sendRCommand("rownames(" + propertiesVariableName + ")<-names", true);
        sendRCommand("rm(names)", false);
        sendRCommand("rm(values)", false);
        sendRCommand("rm(types)", true);

        //setup the getProperty function
        String getPropertyFunction =
                "getProperty <- function(key)\n"
                        + "{\n"
                        + "   theRow <- properties[key,];\n"
                        + "   if (theRow$type == 'double' || theRow$type == 'integer' || theRow$type == 'long') {\n"
                        + "      ret <- as.numeric(theRow$value);\n"
                        + "   } else if (theRow$type == 'boolean') {\n"
                        + "      ret <- as.logical(theRow$value);\n"
                        + "   } else {\n"
                        + "      ret <- as.character(theRow$value);\n"
                        + "    }\n"
                        + "   ret;\n"
                        + "};";
        sendRCommand(getPropertyFunction, true);

        String quietInstallFunction = "cranInstallQuiet<-function(pkgName, repository){\n"
                + "  if (!pkgName %in% installed.packages()){\n"
                + "    install.packages(pkgs=pkgName, repos=repository);\n"
                + "  }\n"
                + "  pkgCmd=paste('suppressPackageStartupMessages(library(', pkgName, '));');\n"
                + "  eval(parse(text = pkgCmd));\n"
                + "}";
        sendRCommand(quietInstallFunction, true);

        String installFunction = "cranInstall<-function(pkgName, repository){\n"
                + "  if (!pkgName %in% installed.packages()){\n"
                + "    install.packages(pkgs=pkgName, repos=repository);\n"
                + "  }\n"
                + "  pkgCmd=paste('library(', pkgName, ');');"
                + "  eval(parse(text = pkgCmd));\n"
                + "}";
        sendRCommand(installFunction, true);


        String getFilenameFunction = "getFileName = function(){\n"
                + "  rand = sample(1:1000000,1);\n"
                + "  file = paste(\"data\", rand, \".dat\", sep=\"\");\n"
                + "  while(file.exists(file))\n"
                + "  {\n"
                + "    rand = sample(1:1000000,1);\n"
                + "    file = paste(\"data\", rand ,\".dat\", sep=\"\");\n"
                + "  }\n"
                + "  file;\n"
                + "}";
        sendRCommand(getFilenameFunction, true);
    }

    /**
     * This method converts a Java string into a proper R character literal. It
     * must be used when there is a chance that a Java string may contain
     * backslash characters '\'
     */
    private String escapeString(String textLiteral) {
        // TODO: are there any other characters that need to be escaped?
        return textLiteral.replace("\\", "\\\\");
    }

    /**
     * This operation assigns data form a data input to a data.frame structure.
     */
    public void assignData(String name, Data data) throws Exception {
        int cols = data.getColumns();

        // First, check for a very special case
        if (cols == 0) {
            sendRCommand(name + " <- data.frame();", true);
            return;
        }

        // Prepare R to not interpret string values as factors
        // see http://cran.r-project.org/doc/manuals/R-intro.html#Factors for
        // more explanation
        sendRCommand(name + "_TMPxXxX <- options(stringsAsFactors = FALSE);", false);
        sendRCommand(name + " <- data.frame();", false);

        // At first try to treat all columns as numerical
        boolean[] isNumerical = new boolean[cols];
        Arrays.fill(isNumerical, true);

        StringBuffer rowCmd;
        boolean emptyRow;
        int r = 0;

        while (true) {
            emptyRow = true;
            rowCmd = new StringBuffer(name + " <- rbind(" + name + ", list(");

            for (int c = 0; c < cols; c++) {
                Column col = data.column(c);

                if (r < col.getRows() && !col.isMissing(r)) {
                    emptyRow = false;
                    if (isNumerical[c]) {
                        try {
                            rowCmd.append(Double.parseDouble(col.getStringValue(r)) + ",");
                        } catch (NumberFormatException x) {
                            isNumerical[c] = false;
                            rowCmd.append("'" + escapeString(col.getStringValue(r)) + "',");
                        }
                    } else {
                        rowCmd.append("'" + escapeString(col.getStringValue(r)) + "',");
                    }
                } else {
                    rowCmd.append("NA,");
                }
            }

            if (emptyRow) {
                break;
            }

            // Trim the last comma, close brackets and send the command to R
            // cols is > 0 so the for loop must have run at least once
            rowCmd.setLength(rowCmd.length() - 1);
            rowCmd.append("));");
            sendRCommand(rowCmd.toString(), false);

            r++;
        }

        // Now, set the column names
        rowCmd = new StringBuffer("colnames(" + name + ") <- c(");
        for (int c = 0; c < cols; c++) {
            rowCmd.append("'" + escapeString(data.column(c).getName()) + "',");
        }
        rowCmd.setLength(rowCmd.length() - 1);
        rowCmd.append(");");
        sendRCommand(rowCmd.toString(), false);

        // Clean up afterwards
        sendRCommand("options(stringsAsFactors = " + name + "_TMPxXxX$stringsAsFactors);", false);
        sendRCommand("rm(" + name + "_TMPxXxX);", true);
    }

    /**
     * Assign a list of files from a filewrapper
     */
    private void assignFileList(String name, FileWrapper wrapper) throws Exception {
        StringBuffer fileList = new StringBuffer();
        fileList.append(name + "<-c(");
        for (int i = 0; i < wrapper.getFileCount(); i++) {
            fileList.append("\"");
            fileList.append(getForwardslashPath(wrapper.getFile(i)));
            fileList.append("\"");
            if (i < wrapper.getFileCount() - 1) {
                // Need a ,
                fileList.append(",");
            }
        }
        fileList.append(");");
        sendRCommand(fileList.toString(), true);
    }

    /**
     * Retrieve a set of data for an output
     */
    public Data retrieveData(String name) throws Exception {
        File tempFile = this.createTempFile("tmp" + name);
        if (tempFile.exists()) {
            tempFile.delete();
        }

        StringBuffer cmd = new StringBuffer();
        cmd.append("if(exists('" + name + "')){write.table(" + name + ",file=\"");
        cmd.append(getForwardslashPath(tempFile));
        cmd.append("\" ,quote=FALSE,sep=',', col.names=TRUE,row.names=FALSE);}");
        sendRCommand(cmd.toString(), true);
        if (tempFile.exists()) {
            DelimitedTextDataImporter importer = new DelimitedTextDataImporter();
            Data result = importer.importFile(tempFile);
            if (!tempFile.delete()) {
                tempFile.deleteOnExit();
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * Retrieve a list of files
     */
    private FileWrapper retrieveFileList(String name) throws Exception {
        File tempFile = this.createTempFile("tmp" + name);
        if (tempFile.exists()) {
            tempFile.delete();
        }

        StringBuffer cmd = new StringBuffer();
        cmd.append("if(exists('" + name + "')){write.table(" + name + ",file=\"");
        cmd.append(getForwardslashPath(tempFile));
        cmd.append("\" ,quote=FALSE,sep=',', col.names=FALSE,row.names=FALSE);}");
        sendRCommand(cmd.toString(), true);
        if (tempFile.exists()) {
            FileWrapper files = new FileWrapper();
            BufferedReader reader = new BufferedReader(new FileReader(tempFile));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    files.addFile(line);
                }
                return files;
            } finally {
                reader.close();
            }
        } else {
            throw new Exception("Block did not write output file list: " + name);
        }

    }

    /**
     * Some output text that the process has been waiting for was received
     */
    void signalReceived() {
        synchronized (this) {
            waitingForOutput = false;
            this.notify();
        }
    }

    /**
     * Get the R Library directory that contains the add on packages
     */
    public File getRLibraryDir() {
        CloudWorkflowServiceLibraryItem rLibraryItem = getDependencyItem(rLibraryName);
        File homeDir;
        if (rLibraryItem.getWrapper().isRelocated()) {
            homeDir = rLibraryItem.getWrapper().getRelocatedDir();
        } else {
            homeDir = rLibraryItem.getUnpackedDir();
        }

        File libraryDir;
        File rDir;

        // Is there a lib64 directory?
        File lib64 = new File(homeDir, "lib64");
        if (lib64.isDirectory()) {
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

    /**
     * Get the R exec directory
     */
    public File getRHomeDir() {
        CloudWorkflowServiceLibraryItem rLibraryItem = getDependencyItem(rLibraryName);

        File homeDir;
        if (rLibraryItem.getWrapper().isRelocated()) {
            homeDir = rLibraryItem.getWrapper().getRelocatedDir();
        } else {
            homeDir = rLibraryItem.getUnpackedDir();
        }

        File rDir;

        // Is there a lib64 directory?
        File lib64 = new File(homeDir, "lib64");
        if (lib64.isDirectory()) {
            rDir = new File(lib64, "R");
            return rDir;
        } else {
            File lib = new File(homeDir, "lib");
            rDir = new File(lib, "R");
            return rDir;
        }
    }

    /**
     * Install any dependent CRAN packages
     */
    public void installCRANDependencies() throws Exception {
        RServiceLibrary lib = (RServiceLibrary) getLibraryWrapper();
        String cranRepository = lib.getProperty("cran-repository", "http://cran.us.r-project.org");
        String quiet = lib.getProperty("cran-quiet-install", "true");
        int packageCount = lib.getCranPackageCount();

        // Install CRAN packages into the environment
        if (quiet.equalsIgnoreCase("true")) {
            for (int i = 0; i < packageCount; i++) {
                String name = lib.getCranPackage(i);
                sendRCommand("cranInstallQuiet(\"" + name + "\",\"" + cranRepository + "\");", true);
            }
        } else {
            for (int i = 0; i < packageCount; i++) {
                String name = lib.getCranPackage(i);
                sendRCommand("cranInstall(\"" + name + "\",\"" + cranRepository + "\");", true);
            }
        }
    }

    /**
     * This class dumps the standard output stream
     */
    private class Dumper extends Thread {

        BufferedReader reader;
        PrintWriter writer;
        volatile boolean finished = false;
        String waitText = "";
        boolean waitingForOutput = false;
        boolean extraneousDone = false;

        public Dumper(InputStream inStream, OutputStream outStream) {
            this.reader = new BufferedReader(new InputStreamReader(inStream));
            this.writer = new PrintWriter(new OutputStreamWriter(outStream));
        }

        public void setFinished() {
            finished = true;
        }

        /**
         * Clear the extraneous done flag
         */
        public synchronized void clearExtraneousDone() {
            extraneousDone = false;
        }

        /**
         * Clear the waiting for output
         */
        public synchronized void clearWaitingFlag() {
            waitingForOutput = false;
        }

        /**
         * Is the extraneous done flag set
         */
        public synchronized boolean extraneousDoneFlagSet() {
            return extraneousDone;
        }

        /**
         * Is this object waiting
         */
        public synchronized boolean isWaitingForOutput() {
            return waitingForOutput;
        }

        /**
         * Set this dumper to wait for an output and signal the service that it
         * has arrived
         */
        public synchronized void waitFor(String text) {
            if (!waitingForOutput) {
                waitText = text;
                waitingForOutput = true;
            }
        }

        @Override
        public void run() {
            String line;

            finished = false;
            while (!finished) {
                try {
                    while (!finished && (line = reader.readLine()) != null) {
                        synchronized (this) {
                            if (waitingForOutput) {
                                if (line.startsWith("[1]") && line.contains(waitText)) {
                                    // Signal that the output is ready
                                    extraneousDone = false;
                                    waitingForOutput = false;
                                    waitText = "";
                                    signal();
                                } else {
                                    writer.println(line);
                                    writer.flush();
                                }
                            } else {
                                // Check for a "done" message and set the flag if one appears
                                if (line.contains(doneMarkerText) || line.startsWith("WARNING:")) {
                                    System.out.println("ExtraneousDone set");
                                    extraneousDone = true;
                                }

                                writer.println(line);
                                writer.flush();
                            }
                        }
                    }
                    finished = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            writer.flush();
            writer.close();
            try {
                reader.close();
            } catch (Exception e) {
            }
        }

        /**
         * There has been activity on the error stream
         */
        public synchronized void errorReceived() {
            if (waitingForOutput) {
                signal();
                waitingForOutput = false;
                waitText = "";
            }
        }

        /**
         * Signal the parent that the output has been received
         */
        private void signal() {
            signalReceived();
        }
    }

    /**
     * This class monitors and dumps the error stream. If there is an error, it
     * notifies the input stream dumper to stop waiting for a response if it was
     * waiting
     */
    private class ErrorDumper extends Thread {

        BufferedReader reader;
        PrintWriter writer;
        Dumper inputDumper;
        boolean errorFlag = false;
        String errorText = "";
        volatile boolean finished = false;

        public ErrorDumper(InputStream inStream, OutputStream outStream, Dumper inputDumper) {
            this.inputDumper = inputDumper;
            this.reader = new BufferedReader(new InputStreamReader(inStream));
            this.writer = new PrintWriter(new OutputStreamWriter(outStream));
        }

        public void setFinished() {
            finished = true;
        }

        public synchronized boolean isErrorFlagSet() {
            return errorFlag;
        }

        public synchronized void clearErrorFlag() {
            errorText = "";
            errorFlag = false;
        }

        public synchronized String getErrorText() {
            return errorText;
        }

        @Override
        public void run() {
            String line;
            String trimmedLine;

            finished = false;
            while (!finished) {
                try {
                    while (!finished && (line = reader.readLine()) != null) {
                        if (!line.equals("")) {
                            trimmedLine = line.trim().toLowerCase();
                            if (trimmedLine.endsWith("error:") || trimmedLine.startsWith("error:") || trimmedLine.equals("syntax error")) {
                                // Tell the stream monitor that there has been output on the error stream
                                synchronized (this) {
                                    errorText = line;
                                    errorFlag = true;
                                }
                                inputDumper.errorReceived();
                            }
                        }
                        writer.println(line);
                        writer.flush();
                    }
                    if (!isErrorHandlingDeferred()) {
                        finished = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            writer.flush();
            writer.close();
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This class provides an R process watchdog thread that checks to see if
     * the process is running
     */
    public class RWatchdog extends Thread {

        /**
         * Set to stop the thread
         */
        private volatile boolean stopFlag = false;

        public void run() {
            while (!stopFlag) {
                try {
                    int exitCode = rProcess.exitValue();

                    // Stopped if we get here
                    if (!stopFlag) {
                        // Should be running!
                        sendErrorResponseMessage("R Process died; exit code: " + exitCode);
                        System.exit(DataProcessorServiceRunner.CALL_ARGUMENT_ERROR);
                    }
                } catch (Exception e) {
                    // Exception so still running
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException x) {
                    System.out.println("Exception in RWatchdog ignored: " + x.getMessage());
                }
            }
        }

        /**
         * Set the stop flag
         */
        public void setStopFlag() {
            stopFlag = true;
        }
    }
}
