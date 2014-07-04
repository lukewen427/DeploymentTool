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

import com.connexience.server.workflow.cloud.library.types.*;
import com.connexience.server.workflow.service.DataProcessorException;

import org.pipeline.core.data.*;

import javax.script.*;
import java.util.*;
import java.io.*;

/**
 * This class provides a data processor service that can execute JavaScript blocks
 * @author hugo
 */
public class JavaScriptDataProcessorService extends CloudDataProcessorService {
    /** Javascript engine to use for processing data */
    ScriptEngine jsEngine = null;

    public JavaScriptDataProcessorService() {
        setExternalIOConnectionSupported(true);
    }

    @Override
    public void sendCommand(String cmd) throws Exception {
        if(jsEngine!=null){
            try {
                System.out.println("> " + cmd);
                System.out.println(jsEngine.eval(cmd));
            } catch (Exception e){
                System.out.println("ERR: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void executionAboutToStart() throws Exception {
        super.executionAboutToStart();
        ScriptEngineManager mgr = new ScriptEngineManager();
        jsEngine = mgr.getEngineByName("JavaScript");
        
        // Eval in all of the scripts. Scripts are included in the library.xml
        // file if the order in which they are evaluated is significant
        if(getLibraryWrapper() instanceof JavaScriptServiceLibrary){
            JavaScriptServiceLibrary library = (JavaScriptServiceLibrary)getLibraryWrapper();
            String scriptUrl;
            File scriptFile;
            FileReader reader;
            
            // Eval the user scripts - first scripts listed in the library.xml are evaluated
            ArrayList<File> libraryXmlScripts = new ArrayList<>();  // List of scripts that have already been evaluated
            for(int i=0;i<library.getUserScripts().size();i++){
                scriptUrl = library.getUserScripts().get(i);
                scriptFile = library.getLibraryItem().getFile(File.separator + scriptUrl);
                if(scriptFile.exists()){
                    reader = null;
                    try {
                        reader = new FileReader(scriptFile);
                        jsEngine.eval(reader);
                        libraryXmlScripts.add(scriptFile);
                    } catch (ScriptException se){
                        se.printStackTrace();
                        throw new Exception(se.getMessage() + " on line: " + se.getLineNumber());
                    } catch (Exception e){
                        throw new Exception("Error processing script file: " + scriptFile, e);
                    } finally {
                        if(reader!=null){
                            try {
                                reader.close();
                            } catch (Exception e){}
                        }
                    }
                } else {
                    throw new Exception("Cannot locate required script: " + scriptFile.getPath());
                }
            }
            
            
            // Next evaluate all of the scripts in the /scripts folder that haven't been evaluated as part of the library.xml file
            if(getLibraryItem().containsFile("/script")){
                File scriptsDir = getLibraryItem().getFile("/scripts");
                if(scriptsDir.isDirectory()){
                    File[] contents = scriptsDir.listFiles();
                    for(int i=0;i<contents.length;i++){
                        if(!libraryXmlScripts.contains(contents[i])){
                            if(contents[i].getName().endsWith(".js")){
                                reader = null;
                                try {
                                    reader = new FileReader(contents[i]);
                                    jsEngine.eval(reader);
                                } catch (ScriptException se){
                                    se.printStackTrace();
                                    throw new Exception(se.getMessage() + " on line: " + se.getLineNumber());
                                } catch (Exception e){
                                    throw new Exception("Error processing script file: " + contents[i], e);
                                } finally {
                                    if(reader!=null){
                                        try {
                                            reader.close();
                                        } catch (Exception e){}
                                    }
                                }                             
                            }
                        }
                    }
                }
            }
            
            // Now add the context variables that map to this block and the API object
            jsEngine.put("block", this);
            jsEngine.put("message", this.getCallMessage());
            
            // Now evaluate the main script
            File mainScript = getLibraryItem().getFile("/main.js");
            if(mainScript.exists()){
                reader = null;
                try {
                    reader = new FileReader(mainScript);
                    jsEngine.eval(reader);
                } catch (ScriptException se){
                    se.printStackTrace();
                    throw new Exception(se.getMessage() + " on line: " + se.getLineNumber());
                } catch (Exception e){
                    throw new Exception("Error processing script file: " + mainScript, e);
                } finally {
                    if(reader!=null){
                        try {
                            reader.close();
                        } catch (Exception e){}
                    }
                }                   
            } else {
                throw new Exception("Cannot find main.js block entry point");
            }
            
            // Run the init script
            try {
                jsEngine.eval("executionAboutToStart()");
            } catch (ScriptException se){
                se.printStackTrace();
                throw se;
            }                  
            
        } else {
            throw new Exception("Cannot access library wrapper data");
        }
        
    }

    public void setOutputDataSetObject(String outputName, Object data) throws DataProcessorException {
        if(data instanceof Data){
            super.setOutputDataSet(outputName, (Data)data);
        } else {
            throw new DataProcessorException("Object is not a valid data set");
        }
    }

    @Override
    public void allDataProcessed() throws Exception {
        super.allDataProcessed();
        try {
            jsEngine.eval("allDataProcessed()");
        } catch (ScriptException se){
            se.printStackTrace();
            throw se;
        }            
    }

    @Override
    public void execute() throws Exception {
        try {
            jsEngine.eval("execute()");
        } catch (ScriptException se){
            se.printStackTrace();
            throw se;
        }
    }
   
}