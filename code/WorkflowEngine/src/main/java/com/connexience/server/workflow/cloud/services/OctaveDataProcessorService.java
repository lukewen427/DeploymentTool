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

import com.connexience.server.workflow.service.*;
import com.connexience.server.workflow.engine.datatypes.FileWrapper;
import com.connexience.server.workflow.engine.DataTypes;

import com.connexience.server.workflow.cloud.library.*;
import com.connexience.server.workflow.cloud.library.types.*;
import com.connexience.server.workflow.util.ZipUtils;

import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.xmldatatypes.*;
import org.pipeline.core.data.*;
import org.pipeline.core.data.manipulation.*;
import org.pipeline.core.data.io.*;
import org.pipeline.core.drawing.TransferData;

import java.util.*;
import java.io.*;

/**
 * This service knows how to call Octave services via the JOPAS interface
 * classes in WorkflowCore.
 *
 * @author nhgh
 */
public class OctaveDataProcessorService extends CloudDataProcessorService {

    /**
     * Octave executable process
     */
    private Process octaveProcess;
    /**
     * Name of the octave binary library dependency
     */
    private String octaveLibraryName = "octave-bin";
    /**
     * Name of the gnuplot binary library
     */
    private String gnuplotLibraryName = "gnuplot-bin";
    /**
     * Name of the gnuplot command
     */
    private String gnuplotCommandName = "gnuplot";
    /**
     * Name of the octave command within the binary library
     */
    private String octaveCommandName = "octave";
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
     * Octave output dumper
     */
    private Dumper dd1;
    /**
     * Octave error dumper
     */
    private ErrorDumper er1;

    /**
     * Text to be used for the "done" marker
     */
    private String doneMarkerText = "+-=_CONNEXIENCE_DONE_MARKER_=-+";

    public OctaveDataProcessorService() {
        setExternalIOConnectionSupported(true);
    }

    @Override
    public void executionAboutToStart() throws Exception {
        super.executionAboutToStart();

        // Get the octave library name from a property if there is one
        octaveLibraryName = getEditableProperties().stringValue("OctaveLibraryName", "octave-bin");

        // Get the octave binary library
        CloudWorkflowServiceLibraryItem octaveLibrary = getDependencyItem(octaveLibraryName);
        CloudWorkflowServiceLibraryItem libItem = getLibraryItem();

        if (octaveLibrary == null) {
            throw new Exception("Octave library dependency not found");
        }

        LibraryWrapper wrapper = octaveLibrary.getWrapper();

        if (!(wrapper instanceof BinaryLibrary)) {
            throw new Exception("Octave library is not a binary library");
        }

        BinaryLibrary octaveLib = (BinaryLibrary) wrapper;
        BinaryLibrary.Executable octaveCmd = octaveLib.getExecutable(octaveCommandName);
        if (octaveCmd == null) {
            throw new Exception("Octave executable command is not defined in the library");
        }

        File octaveBin = null;

        // Get the right command if it is absolute
        if (octaveCmd.isAbsolute()) {
            octaveBin = new File(octaveCmd.getRelativeCmd());
        } else {
            octaveBin = octaveLibrary.getFile(octaveCmd.getRelativeCmd());
        }

        if (!octaveBin.exists()) {
            System.out.println("Octave executable: " + octaveBin.getPath() + " does not exist. Assuming it is on the path");
        }

        int bufferSize = getCallMessage().getStdOutBufferSize();

        outputStore = new ByteArrayOutputStream(bufferSize);
        errorStore = new ByteArrayOutputStream(bufferSize);

        // Start octave
        String initialCmd = "PS1('" + doneMarkerText + "\\n');";
        File initFile = new File(getWorkingDirectory(), getCallMessage().getContextId() + "_init.m");
        ZipUtils.writeSingleLineFile(initFile, initialCmd);
        
        String[] cmdArgs = new String[] {
                octaveBin.getPath(),
                "--interactive",
                "--persist",
                initFile.getPath() };
        octaveProcess = Runtime.getRuntime().exec(cmdArgs);
        
        Thread.sleep(100); // Get rid of this on Linux

        // Connect the IO streams
        commandWriter = new PrintWriter(octaveProcess.getOutputStream());
        dd1 = new Dumper(octaveProcess.getInputStream(), System.out, doneMarkerText);
        dd1.start();
        er1 = new ErrorDumper(octaveProcess.getErrorStream(), System.out, dd1);
        er1.start();

        // Start monitoring the octave process
        notifyExternalProcessStart(octaveProcess);
        
        // Assign properties to the octave runtime and make sure it is in the correct
        // working directory
        setupProcess();
        setupGnuPlot();
        assignProperties();

        // Assign the non-streaming data
        assignNonStreamingData();

        // Run the init.m file
        if (libItem.getFile("/init.m").exists()) {
            if (containsCommands(libItem.getFile("/init.m"))) {
                sendOctaveCommand("init;", true);
            }
        }
    }



    private void terminateOctave() throws Exception {
        try {
            commandWriter.flush();
            commandWriter.println("quit");
            commandWriter.flush();
            commandWriter.close();
        } catch (Exception e) {
            System.out.println("Error closing command writer: " + e.getMessage());
        }

        // See if octave quits gracefully
        long timeoutTime = System.currentTimeMillis() + 2000;
        boolean loopBreak = false;

        while (loopBreak == false) {
            if (!isOctaveRunning()) {
                loopBreak = true;
                System.out.println("Octave processes exited correctly");
            }

            if (System.currentTimeMillis() > timeoutTime) {
                loopBreak = true;
                System.out.println("Octave process still running after 2 seconds");
            }
        }

        try {
            octaveProcess.getInputStream().close();
        } catch (Exception e) {
        }
        try {
            octaveProcess.getErrorStream().close();
        } catch (Exception e) {
        }
        try {
            octaveProcess.getOutputStream().close();
        } catch (Exception e) {
        }

        try {
            octaveProcess.destroy();
        } catch (Exception e) {
            System.out.println("Error stopping octave: " + e.getMessage());
        }
    }

    /**
     * Is the octave process running
     */
    private boolean isOctaveRunning() {
        try {
            if (octaveProcess != null) {
                octaveProcess.exitValue();
                return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Terminate octave and store the output data
     */
    @Override
    public void allDataProcessed() throws Exception {
        super.allDataProcessed();
        dd1.setFinished();
        er1.setFinished();
        terminateOctave();

        StringBuffer outputBuffer = new StringBuffer(outputStore.size() + errorStore.size());
        outputBuffer.append(new String(outputStore.toByteArray()));
        outputBuffer.append(new String(errorStore.toByteArray()));
        setCommandOutputData(outputBuffer.toString());
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
                if (inputDataObject instanceof com.connexience.server.workflow.engine.datatypes.DataWrapper && inputModes[i].equals(DataProcessorIODefinition.STREAMING_CONNECTION)) {
                    // Pass in a frame of data
                    data = getInputDataSet(inputNames[i]);
                    assignData(inputNames[i], data);

                }
            }

            // Execute the code
            sendOctaveCommand("main", true);

            // Retrieve the outputs
            String[] outputNames = message.getDataOutputs();
            String[] outputTypes = message.getDataOutputTypes();
            Data outData;

            for (int i = 0; i < outputNames.length; i++) {
                if (outputTypes[i].equalsIgnoreCase(DataTypes.DATA_WRAPPER_TYPE.getName())) {
                    outData = retrieveData(outputNames[i]);
                    if (outData != null) {
                        setOutputDataSet(outputNames[i], outData);
                    }
                } else if (outputTypes[i].equalsIgnoreCase(DataTypes.FILE_WRAPPER_TYPE.getName())) {
                    ArrayList<String> files = retrieveFileList(outputNames[i]);
                    setOutputData(outputNames[i], new FileWrapper(files));
                }
            }

        } catch (Exception e) {
            throw e;
        }


    }

    /**
     * Set up the octave process to be in the correct directory etc
     */
    private synchronized void setupProcess() throws Exception {
        sendOctaveCommand("cd " + getWorkingDirectory().getPath() + ";", true);
        sendOctaveCommand("path(path,'" + getWorkingDirectory().getPath() + File.separator + getCallMessage().getContextId() + File.separator + "mfiles');", true);
        
        File scriptDir = new File(getLibraryWrapper().getContentsDir(), "scripts");
        sendOctaveCommand("path(path,'" + scriptDir.getPath() + "');", true);
    }

    /**
     * Set up the gnuplot path if there is a gnuplot dependency
     */
    private synchronized void setupGnuPlot() throws Exception {
        // Get the octave binary library
        CloudWorkflowServiceLibraryItem gnuplotLibrary = getDependencyItem(gnuplotLibraryName);

        if (gnuplotLibrary != null) {
            LibraryWrapper wrapper = gnuplotLibrary.getWrapper();

            if (wrapper instanceof BinaryLibrary) {
                BinaryLibrary gnuplotLib = (BinaryLibrary) wrapper;
                BinaryLibrary.Executable gnuplotCmd = gnuplotLib.getExecutable(gnuplotCommandName);
                if (gnuplotCmd != null) {

                    File gnuplotBin;
                    if (gnuplotCmd.isAbsolute()) {
                        gnuplotBin = new File(gnuplotCmd.getRelativeCmd());
                    } else {
                        gnuplotBin = gnuplotLibrary.getFile(gnuplotCmd.getRelativeCmd());
                    }


                    if (gnuplotBin.exists()) {
                        // Send the gnuplot binary command
                        sendOctaveCommand("gnuplot_binary('" + gnuplotBin.getPath() + "');", true);
                    }
                }
            }
        }
    }

    /**
     * Assign all of the declared service properties as variable in the octave
     * environment
     */
    private synchronized void assignProperties() {
        XmlDataStore properties = getEditableProperties();
        
        // Add the block library directory
        properties.add("BlockLibraryPath", getLibraryItem().getUnpackedDir().getPath());
        
        String name;
        XmlDataObject value;
        String cmd;
        propertiesVariableName = properties.stringValue("PropertyVariable", propertiesVariableName);

        if (octaveProcess != null) {
            Vector<?> propertyNames = properties.getNames();
            for (int i = 0; i < propertyNames.size(); i++) {
                name = (String) propertyNames.get(i);
                try {
                    cmd = null;
                    value = properties.get(name);
                    if (value instanceof XmlStringDataObject) {
                        // String property
                        cmd = propertiesVariableName + "." + name + "='" + ((XmlStringDataObject) value).stringValue() + "';";

                    } else if (value instanceof XmlBooleanDataObject) {
                        // Boolean
                        if (((XmlBooleanDataObject) value).booleanValue() == true) {
                            cmd = propertiesVariableName + "." + name + " = 1;";
                        } else {
                            cmd = propertiesVariableName + "." + name + " = 0;";
                        }

                    } else if (value instanceof XmlDoubleDataObject) {
                        // Double
                        cmd = propertiesVariableName + "." + name + " = " + ((XmlDoubleDataObject) value).doubleValue() + ";";

                    } else if (value instanceof XmlIntegerDataObject) {
                        // Integer
                        cmd = propertiesVariableName + "." + name + " = " + ((XmlIntegerDataObject) value).intValue() + ";";

                    } else if (value instanceof XmlLongDataObject) {
                        // Long
                        cmd = propertiesVariableName + "." + name + " = " + ((XmlLongDataObject) value).longValue() + ";";

                    } else if (value instanceof XmlFileDataObject) {
                        // File reference
                        cmd = propertiesVariableName + "." + name + "='" + ((XmlFileDataObject) value).fileValue().getPath() + "';";
                    }

                    // Execute the assignment command in octave
                    if (cmd != null) {
                        sendOctaveCommand(cmd, true);
                    }

                } catch (Exception e) {

                }
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
            if (inputDataObject instanceof com.connexience.server.workflow.engine.datatypes.DataWrapper && inputModes[i].equals(DataProcessorIODefinition.NON_STREAMING_CONNECTION)) {
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
     * Wait for some output from octave
     */
    private synchronized void waitForMarker() {
        while (dd1.isWaiting()) {
            try {
                Thread.yield();
            } catch (Exception e){}
        }
    }

    public void sendCommand(String cmd) throws Exception {
        sendOctaveCommand(cmd, true);
    }
    
    /**
     * Send a command to the octave process
     */
    public synchronized void sendOctaveCommand(String command, boolean waitForResponse) throws Exception {
        if (octaveProcess != null) {

            if (waitForResponse) {
                er1.clearErrorFlag();
                dd1.setWaiting();
                commandWriter.println(command);
                commandWriter.flush();
                waitForMarker();

                // Check for error
                if (er1.isErrorFlagSet()) {
                    throw new Exception("Octave interpretor error: " + er1.getErrorText());
                }
            } else {
                commandWriter.println(command);
                commandWriter.flush();
            }

        } else {
            throw new Exception("Error sending command. Octave not running");
        }
    }

    /**
     * Assign some input data (or a chunk of input data) as a matrix within
     * Octave. Data is assigned as data structures with the name of the input
     * and sections within the structure representing different aspects of the
     * data: name.numerical = the numerical columns in the data name.text = the
     * text columns in the data name.numericalNames = a list of numerical column
     * names name.textNames = a list of text column names name.textTypes = a
     * list of text column types
     *
     * @param name of the variable
     * @param data set to assign
     */
    public void assignData(String name, Data data) throws Exception {
        NumericalColumnExtractor extractor = new NumericalColumnExtractor(data);
        Vector<?> numerical = extractor.extractColumns();

        // Numerical data
        if (getEditableProperties().booleanValue("NativeTransfer", true)) {
            assignNumericalNative(name, numerical, extractor.getShortestNumericalColumnLength());
        } else {
            assignNumerical(name, numerical, extractor.getShortestNumericalColumnLength());
        }


        // Text data
        Vector<?> remaining = extractor.extractNonNumericalColumns();
        assignNonNumerical(name, remaining, extractor.getShortestNonNumericalColumnLength());

    }
    
    /**
     * Assign a set of numerical columns as a matrix to the process
     */
    private void assignNumerical(String name, Vector<?> columns, int rows) throws Exception {
        int cols = columns.size();
        int lastSize = 100;
        StringBuffer cmd;
        Column column;

        if (cols > 0) {
            sendOctaveCommand(name + ".numerical=[", false);
            for (int i = 0; i < rows; i++) {
                cmd = new StringBuffer(lastSize);
                for (int j = 0; j < cols; j++) {
                    column = (Column) columns.get(j);
                    if (!column.isMissing(i)) {
                        cmd.append(column.getStringValue(i) + " ");
                    } else {
                        cmd.append("NaN ");
                    }
                }

                cmd.append("; ");

                // Assign a bigger buffer next time if this
                if (cmd.length() > lastSize) {
                    lastSize = cmd.length();
                }
                sendOctaveCommand(cmd.toString(), false);
            }
            sendOctaveCommand("];", true);
        } else {
            sendOctaveCommand(name + ".numerical=[];", true);
        }

        // Send the names
        if (cols > 0) {
            StringBuffer namesCmd = new StringBuffer();
            namesCmd.append(name + ".numericalNames=[");
            namesCmd.append("'" + ((Column) columns.get(0)).getName() + "'");
            if (cols > 1) {
                for (int i = 1; i < cols; i++) {
                    namesCmd.append(";'" + ((Column) columns.get(i)).getName() + "'");
                }
            }
            namesCmd.append("];");
            sendOctaveCommand(namesCmd.toString(), true);
        } else {
            sendOctaveCommand(name + ".numericalNames=[];", true);
        }
    }

    /**
     * Assign numerical data using native octave text format
     */
    private void assignNumericalNative(String name, Vector<?> columns, int rows) throws Exception {
        int cols = columns.size();
        int lastSize = 100;
        StringBuffer cmd;
        Column column;
        File tmpFile = createTempFile(name);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(tmpFile));
            writer.println("# Created by Inkspot Workflow Runtime");
            writer.println("# name: " + "_" + name + "_");
            writer.println("# type: matrix");
            writer.println("# rows: " + rows);
            writer.println("# columns: " + cols);
            if (cols > 0) {
                for (int i = 0; i < rows; i++) {
                    cmd = new StringBuffer(lastSize);
                    for (int j = 0; j < cols; j++) {
                        column = (Column) columns.get(j);
                        if (!column.isMissing(i)) {
                            cmd.append(column.getStringValue(i) + " ");
                        } else {
                            cmd.append("NaN ");
                        }
                    }

                    // Assign a bigger buffer next time if this
                    if (cmd.length() > lastSize) {
                        lastSize = cmd.length();
                    }
                    writer.println(cmd.toString());
                }
            } else {
                writer.println("");
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        sendOctaveCommand("load " + tmpFile.getPath(), true);
        sendOctaveCommand(name + ".numerical= _" + name + "_;", true);
        sendOctaveCommand("clear _" + name + "_", true);


        // Send the names
        if (cols > 0) {
            StringBuffer namesCmd = new StringBuffer();
            namesCmd.append(name + ".numericalNames=[");
            namesCmd.append("'" + ((Column) columns.get(0)).getName() + "'");
            if (cols > 1) {
                for (int i = 1; i < cols; i++) {
                    namesCmd.append(";'" + ((Column) columns.get(i)).getName() + "'");
                }
            }
            namesCmd.append("];");
            sendOctaveCommand(namesCmd.toString(), true);
        } else {
            sendOctaveCommand(name + ".numericalNames=[];", true);
        }
    }

    /**
     * Assign non numerical columns to the process
     */
    private void assignNonNumerical(String name, Vector<?> columns, int rows) throws Exception {
        int cols = columns.size();
        Column column;

        if (cols > 0) {
            sendOctaveCommand(name + ".textCols=" + cols + ";", true);
            for (int i = 0; i < cols; i++) {
                sendOctaveCommand(name + ".textCol" + i + "=[", false);
                column = (Column) columns.get(i);

                for (int j = 0; j < rows; j++) {
                    if (j == 0) {
                        if (!column.isMissing(j)) {
                            sendOctaveCommand("'" + column.getStringValue(j) + "'", false);
                        } else {
                            sendOctaveCommand("''", false);
                        }
                    } else {
                        if (!column.isMissing(j)) {
                            sendOctaveCommand(";'" + column.getStringValue(j) + "'", false);
                        } else {
                            sendOctaveCommand(";''", false);
                        }
                    }
                }
                sendOctaveCommand("];", true);
            }
        } else {
            sendOctaveCommand(name + ".textCols=0;", true);
        }

        // Send the names
        if (cols > 0) {
            StringBuffer namesCmd = new StringBuffer();
            namesCmd.append(name + ".textNames=[");
            namesCmd.append("'" + ((Column) columns.get(0)).getName() + "'");
            if (cols > 1) {
                for (int i = 1; i < cols; i++) {
                    namesCmd.append(";'" + ((Column) columns.get(i)).getName() + "'");
                }
            }
            namesCmd.append("];");
            sendOctaveCommand(namesCmd.toString(), true);
        } else {
            sendOctaveCommand(name + ".textNames=[];", true);
        }
    }

    /**
     * Retrieve some data from the Octave process
     */
    public Data retrieveData(String name) throws Exception {
        sendOctaveCommand(name + ".numerical;", true);  // Check variable exists
        sendOctaveCommand(name + "_tmp=" + name + ".numerical;", true);
        sendOctaveCommand("save -ascii _" + name + "_transfer.dat " + name + "_tmp;", true);
        sendOctaveCommand("clear _" + name + "_tmp;", true);
        DelimitedTextDataImporter importer = new DelimitedTextDataImporter();

        File importFile = new File(getWorkingDirectory(), "_" + name + "_transfer.dat");
        if (importFile.exists()) {
            if (importFile.length() > 0) {
                importer.setDelimiterType(DelimitedTextDataImporter.WHITESPACE_DELIMITED);
                importer.setImportColumnNames(false);
                importer.setDataStartRow(1);
                return importer.importFile(importFile);
            } else {
                return null;
            }
        } else {
            throw new Exception("Output data file for output: " + name + " not found");
        }
    }

    /**
     * Assign a list of files from a file transfer data object to a data
     * structure
     */
    public synchronized void assignFileList(String name, FileWrapper wrapper) throws Exception {
        sendOctaveCommand(name + ".files=[", false);
        for (int i = 0; i < wrapper.getFileCount(); i++) {
            if (i == 0) {
                sendOctaveCommand("'" + wrapper.getFile(i) + "'", false);
            } else {
                sendOctaveCommand(";'" + wrapper.getFile(i) + "'", false);
            }
        }
        sendOctaveCommand("];", true);
    }

    /**
     * Retrieve a set of file names suitable for a file wrapper
     */
    public synchronized ArrayList<String> retrieveFileList(String name) throws Exception {
        ArrayList<String> values = new ArrayList<>();
        sendOctaveCommand(name + ".files;", true);
        sendOctaveCommand("_fid=fopen('_" + name + "_transfer.dat', 'w');", true);
        sendOctaveCommand("fdisp(_fid," + name + ".files);", true);
        sendOctaveCommand("fclose(_fid);", true);

        File importFile = new File(getWorkingDirectory(), "_" + name + "_transfer.dat");
        DelimitedTextDataImporter importer = new DelimitedTextDataImporter();

        if (importFile.exists()) {
            importer.setDelimiterType(DelimitedTextDataImporter.COMMA_DELIMITED);
            importer.setImportColumnNames(false);
            importer.setDataStartRow(1);
            Data imported = importer.importFile(importFile);

            if (imported.getColumns() == 1) {
                Column importCol = imported.column(0);
                for (int i = 0; i < importCol.getRows(); i++) {
                    if (!importCol.isMissing(i)) {
                        values.add(importCol.getStringValue(i));
                    }
                }
            } else {
                throw new Exception("Incorrect data format for output: " + name);
            }
        } else {
            throw new Exception("Output data file for output: " + name + " not found");
        }
        return values;
    }

    /**
     * This class dumps the standard output stream
     */
    private class Dumper extends Thread {

        private volatile Boolean waiting = false;
        private boolean finished = false;
        private BufferedReader reader;
        private PrintWriter writer;
        
        public Dumper(InputStream inStream, OutputStream outStream, String markerText) {
            reader = new BufferedReader(new InputStreamReader(inStream));
            writer = new PrintWriter(outStream);
        }

        @Override
        public void run() {
            String line;
            try {
                while (finished == false && (line = reader.readLine()) != null) {
                    if (!line.contains(doneMarkerText)) {
                        writer.println(line);
                        writer.flush();
                    } else {
                        matchFound();
                    }
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
                finished = true;
            }
        }

        public void setWaiting() {
            waiting = true;
        }

        public void setFinished() {
            finished = true;
        }

        public void matchFound() {
            waiting = false;
        }

        public boolean isWaiting() {
            return waiting;
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
        InputStream inStream;
        OutputStream outStream;
        volatile boolean finished = false;
        volatile boolean errorFlag = false;
        private String errorText = "";


        public ErrorDumper(InputStream inStream, OutputStream outStream, Dumper inputDumper) {
            this.inStream = inStream;
            this.reader = new BufferedReader(new InputStreamReader(inStream));
            this.outStream = outStream;
            this.writer = new PrintWriter(new OutputStreamWriter(outStream));
        }

        public void setFinished() {
            finished = true;
        }

        public boolean isErrorFlagSet() {
            return errorFlag;
        }

        public void clearErrorFlag() {
            errorText = "";
            errorFlag = false;
        }

        public String getErrorText() {
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
                            if (!isErrorHandlingDeferred()) {
                                if (trimmedLine.endsWith("error:") || trimmedLine.startsWith("error:") || trimmedLine.equals("syntax error")) {
                                    // Tell the stream monitor that there has been output on the error stream                                
                                    synchronized (this) {
                                        errorText = line;
                                        errorFlag = true;
                                    }
                                }
                            }
                        }
                        writer.println("ERR: " + line);
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
            }
        }
    }

}
