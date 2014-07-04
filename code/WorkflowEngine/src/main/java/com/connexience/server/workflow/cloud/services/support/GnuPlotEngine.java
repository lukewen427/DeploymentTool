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
package com.connexience.server.workflow.cloud.services.support;

import com.connexience.server.util.CommandRunner;
import com.connexience.server.workflow.cloud.library.CloudWorkflowServiceLibraryItem;
import com.connexience.server.workflow.cloud.library.LibraryWrapper;
import com.connexience.server.workflow.cloud.library.types.BinaryLibrary;
import com.connexience.server.workflow.cloud.services.CloudDataProcessorService;
import com.connexience.server.workflow.service.DataProcessorCallMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.pipeline.core.data.ColumnMetaData;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataMetaData;
import org.pipeline.core.data.io.CSVDataExporter;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.replacement.PropertyReplacementManager;

/**
 * This class provides a driver for GNUPlot that can be linked to a workflow
 * block. The purpose of this is to support the standard GnuplotBlock, but also
 * to provide other Java blocks with a mechanism for generating plots as part of
 * their operation.
 *
 * @author hugo
 */
public class GnuPlotEngine {

    /**
     * Javascript engine to use for processing data
     */
    ScriptEngine jsEngine = null;
    /**
     * Data processor service that this engine is attached to
     */
    private CloudDataProcessorService service;
    /**
     * Name of the plot file in the service library
     */
    private String plotFileName;
    /**
     * Name of the init.js file in the service library
     */
    
    private String initFileName;
    /** 
     * Name of the gnuplot library to call 
     */
    private String gnuplotLibraryName = "gnuplot-bin";

    /**
     * Name of the gnuplot command definition 
     */
    private String gnuplotCommandName = "gnuplot";
    
    /**
     * Name of the produced file. This is passed as a property, the plot commands
     * are under no obligation to use this, but they should...
     */
    private String resultFileName = "chart.jpg";
    
    /**
     * File containing generated commands
     */
    private File commandFile;
    
    /** Data Hashmap */
    private HashMap<String, Data> dataSets;
    
    /** Additional properties */
    private XmlDataStore additionalParameters = new XmlDataStore();
    
    public GnuPlotEngine(CloudDataProcessorService service, String plotFileName, String initFileName, String resultFileName) throws Exception {
        this.service = service;
        this.plotFileName = plotFileName;
        this.initFileName = initFileName;
        this.resultFileName = resultFileName;
        this.dataSets = service.getInputDataSetMap();
        setupJSInterpreter();
    }

    public GnuPlotEngine(CloudDataProcessorService service, String plotFileName, String resultFileName) throws Exception {
        this.service = service;
        this.resultFileName = resultFileName;
        this.plotFileName = plotFileName;
        this.dataSets = service.getInputDataSetMap();
        setupJSInterpreter();
    }

    /**
     * Setup the script interpreter
     */
    private void setupJSInterpreter() throws Exception {
        // Set up the javascript engine
        ScriptEngineManager mgr = new ScriptEngineManager();
        jsEngine = mgr.getEngineByName("JavaScript");
        jsEngine.put("block", service);
        jsEngine.put("datasets", dataSets);
    }

    /**
     * Setup the parameter replacers for scripts
     */
    private PropertyReplacementManager createParameterReplacer() {
        // Global property replacements
        XmlDataStore properties = (XmlDataStore)service.getEditableProperties().getCopy();
        
        // Make sure that the result file name is passed in
        if(resultFileName!=null){
            properties.add("Filename", resultFileName);
        }
        
        // Merge in additional parameters
        try {
            properties.mergeProperties(additionalParameters);
        } catch (Exception e){
        }
        
        return new PropertyReplacementManager(properties);
    }

    /**
     * Find the first numerical input connection
     */
    private String findFirstDataInput(){
        String[] names = service.getCallMessage().getDataSources();
        for (int i=0;i<names.length;i++) {
            if(service.getCallMessage().getDataSourceTypes()[i].equals("data-wrapper")){
                return names[i];
            }
        }
        return null;
    }
    
    /**
     * Create the data files for the plot
     */
    private PropertyReplacementManager createDataParameterReplacer() throws Exception {
        Set<String> names = dataSets.keySet();
        XmlDataStore inputProperties = new XmlDataStore();
        inputProperties.add("Directory", service.getWorkingDirectory().toString()); // Add working directory here
        DataProcessorCallMessage msg = service.getCallMessage();
        int count = 0;
        for (String name : names) {
            if(msg.getDataSourceType(name).equals("data-wrapper")){
                System.out.println("Using data from: " + name);
                Data data = dataSets.get(name);

                System.out.println("Data size: " + data.getSmallestRows() + "," + data.getColumns());
                File dataFile = service.createTempFile(msg.getContextId() + "-gnuplot-" + name + ".dat");
                if(!dataFile.exists()){
                    // Need to create a new data file
                    CSVDataExporter exporter = new CSVDataExporter(data);
                    exporter.setDelimiter(" ");
                    exporter.setIncludeNames(false);
                    exporter.setRowIndexIncluded(true);
                    exporter.setRowIndexValue(0);


                    PrintWriter dataWriter = new PrintWriter(dataFile);
                    exporter.writeToPrintWriter(new PrintWriter(dataFile));
                    dataWriter.flush();
                    dataWriter.close();   
                    inputProperties.add(name, dataFile.getPath());
                    
                } else {
                    // Alread have data file in place
                    inputProperties.add(name, dataFile.getPath());
                }
            }
        }
        return new PropertyReplacementManager(inputProperties);
    }

    /**
     * Evaluate the init script if it exists
     */
    private void evaluateInitScript(PrintWriter writer, PropertyReplacementManager propertyReplacer, PropertyReplacementManager inputReplacer) throws Exception {
        CloudWorkflowServiceLibraryItem library = service.getLibraryItem();

        // Look for a Javascript file called init.js this will get evaluated
        // before anything else
        if(library.containsFile(File.separator + initFileName)){
            // Load and process init script contents
            LineNumberReader scriptReader = new LineNumberReader(new FileReader(library.getFile(File.separator + initFileName)));
            ArrayList<String> scriptLines = new ArrayList<>();
            String scriptLine;
            while((scriptLine=scriptReader.readLine())!=null){
                scriptLines.add(inputReplacer.replaceProperties(propertyReplacer.replaceProperties(scriptLine)));
            }
            
            evaluateScript(scriptLines, writer, propertyReplacer, inputReplacer);
        }        
    }

    /**
     * Evaluate a script
     */
    private void evaluateScript(ArrayList<String> scriptSection, PrintWriter writer, PropertyReplacementManager propertyReplacer, PropertyReplacementManager inputReplacer) throws Exception {
        // Output destination for script to write to
        ByteArrayOutputStream scriptOutputBuffer = new ByteArrayOutputStream();
        PrintWriter gnuplotWriter = new PrintWriter(scriptOutputBuffer);
        jsEngine.put("writer", gnuplotWriter);

        // Create a buffer of script contents
        ByteArrayOutputStream scriptContents = new ByteArrayOutputStream();
        PrintWriter scriptWriter = new PrintWriter(scriptContents);
        for (String s : scriptSection) {
            scriptWriter.println(s);
        }
        scriptWriter.flush();
        scriptWriter.close();
        ByteArrayInputStream inStream = new ByteArrayInputStream(scriptContents.toByteArray());

        // Evaluate this stream
        jsEngine.eval(new InputStreamReader(inStream));

        // Flush the output from the script
        gnuplotWriter.flush();
        gnuplotWriter.close();

        LineNumberReader scriptOutputReader = new LineNumberReader(new InputStreamReader(new ByteArrayInputStream(scriptOutputBuffer.toByteArray())));
        String outputLine;
        while ((outputLine = scriptOutputReader.readLine()) != null) {
            writer.println(outputLine);
        }
    }

    /** Find the Gnuplot command */
    private File locateGnuplot() throws Exception {
        // Get the executable file from the library
        CloudWorkflowServiceLibraryItem gnuplotLibrary = service.getDependencyItem(gnuplotLibraryName);
        if(gnuplotLibrary==null){
            throw new Exception("GNUPlot library not found");
        }
        LibraryWrapper wrapper = gnuplotLibrary.getWrapper();
        if(!(wrapper instanceof BinaryLibrary)){
            throw new Exception("Wrong library type for GNUPlot library");
        }
        BinaryLibrary gnuplotLib = (BinaryLibrary)wrapper;
        BinaryLibrary.Executable gnuplotCommand = gnuplotLib.getExecutable(gnuplotCommandName);
        if(gnuplotCommand==null){
            throw new Exception("GNUPlot command not defined in library");
        }
        File gnuplotBin;
        // Get the right command if it is absolute
        if(gnuplotCommand.isAbsolute()){
            gnuplotBin = new File(gnuplotCommand.getRelativeCmd());
        } else {
            gnuplotBin = gnuplotLib.getFile(gnuplotCommand.getRelativeCmd());
        }
        
        if(!gnuplotBin.exists()){
            System.out.println("GNUPLOT command not found. Assuming it is on the path");
        }
        return gnuplotBin;        
    }
    
    /** Perform data replacement. This method puts all of the input columns into the plot string */
    private String expandTrendsTags(String line) throws Exception {
        if(line.contains("${Trends}")){
            // Replace with actual data tags
            String firstInput = findFirstDataInput();
            if(firstInput!=null){
                Data data = dataSets.get(firstInput);
                DataMetaData metaData = data.getMetaData();
                if(metaData!=null){
                    ColumnMetaData md;
                    StringBuilder plotCommand = new StringBuilder();
                    boolean first = true;

                    for(int i=0;i<metaData.getColumns();i++){
                        md = metaData.column(i);
                        if(md.isNumerical()){
                            if(!first){
                                plotCommand.append(",");
                            }
                            first = false;
                            plotCommand.append("'${" + firstInput + "}' using 1:" + (i + 2) + " with lines title \"" + md.getName() + "\"");
                        }
                    }
                    return line.replace("${Trends}", plotCommand.toString());

                } else {
                    return line;
                }
            } else {
                return line;
            }
        } else {
            return line;
        }
    }    
    
    /**
     * Get a reference to the additional parameters 
     */
    public XmlDataStore getAdditionalParameters() {
        return additionalParameters;
    }

    /**
     * Get the file containing the commands used to generate the graph.
     */
    public File getCommandFile() {
        return commandFile;
    }

    /**
     * Create the plot
     */
    public File createPlot() throws Exception {
        // Get some parameter replacers
        PropertyReplacementManager inputReplacer = createDataParameterReplacer();
        PropertyReplacementManager propertyReplacer = createParameterReplacer();

        // Create the commands file
        commandFile = service.createTempFile("gnuplot");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(commandFile);

            // Execute the init script if there is one
            evaluateInitScript(writer, propertyReplacer, inputReplacer);
            
            // Run the plot commands
            File initFile = service.getLibraryItem().getFile(plotFileName);
            LineNumberReader reader = new LineNumberReader(new FileReader(initFile));
            String line;
            ArrayList<String> scriptSection = null;
            while((line=reader.readLine())!=null){
                if(line.trim().startsWith("<script>") && scriptSection==null){
                    // Start of a script
                    scriptSection = new ArrayList<>();
                    
                } else if(line.trim().endsWith("</script>") && scriptSection!=null){
                    // End of a script -- execute the script
                    evaluateScript(scriptSection, writer, propertyReplacer, inputReplacer);
                    
                    scriptSection = null;
                } else {
                    if(scriptSection!=null){
                        // Add line to the script section
                        scriptSection.add(inputReplacer.replaceProperties(propertyReplacer.replaceProperties(line)));
                    } else {
                        // Try and expand the line with data if the right flag is present
                        writer.println(inputReplacer.replaceProperties(propertyReplacer.replaceProperties(expandTrendsTags(line))));    // Replace ${properties}
                    }
                }
            }
            
            reader.close();            
            
            // Send the end command
            writer.println("quit");
            writer.flush();
            writer.close();
            
            // Get hold of Gnuplot
            File gnuplotBin = locateGnuplot();
            CommandRunner executor = new CommandRunner();
            executor.run(gnuplotBin.getPath() + " < " + commandFile.getPath());
            System.out.println(executor.sysOut());
            System.err.println(executor.sysErr());
            if(executor.getExitCode()!=0){
                throw new Exception("Gnuplot execution error: " + executor.getExitCode());
            }
            
            
        } catch (Exception e) {
            throw new Exception("Error in createPlot: " + e.getMessage(), e);
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (Exception e) {
            }
        }

        // Add the block properties as replacement parameters
        if(service.getEditableProperties().containsName("Filename")){
            File results = new File(service.getWorkingDirectory(), resultFileName);
            return results;
        } else {
            // No file name set
            return null;
        }
    }
}