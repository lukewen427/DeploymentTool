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
package com.connexience.api.model.scripting;

import com.connexience.api.DatasetClient;
import com.connexience.api.StorageClient;
import com.connexience.api.WorkflowClient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * This class allows a javascript program to operate the API using a set of credentials.
 * It can be called from the command line to enable programs that cannot communicate
 * directly with the API to make use of e-sc.
 * @author hugo
 */
public class APIScriptExecutor {
    /** Arguments string list */
    private String[] scriptArguments;
    
    /** Credentials file */
    private File apiProperties;
    
    /** File containing the script to run */
    private File scriptFile;
    
    /** Storage API for script */
    private StorageClient storageApi;
    
    /** Dataset API for script */
    private DatasetClient datasetApi;
    
    /** Workflow API for script */
    private WorkflowClient workflowApi;
    
    /** Script execution environment */
    ScriptEngine jsEngine = null;
    
    /** Lines of script */
    private String script;

    public APIScriptExecutor(File scriptFile, File apiProperties, String[] arguments) throws Exception {
        this.scriptFile = scriptFile;
        this.apiProperties = apiProperties;
        this.scriptArguments = arguments;
        setupJSInterpreter();
        loadScript();
        replaceScriptParameters();
        executeScript();
    }
    
    /**
     * Execute the script
     */
    private void executeScript() throws Exception {
        jsEngine.eval(script);
    }
    
    /**
     * Replace parameters
     */
    private void replaceScriptParameters(){
        for(int i=0;i<scriptArguments.length;i++){
            script = script.replace("${" + (i + 1) + "}", scriptArguments[i]);
        }
    }
    
    /**
     * Load the script 
     */
    private void loadScript() throws Exception {
        if(scriptFile.exists()){
            BufferedReader reader = null;
            try {
                StringBuilder scriptBuilder = new StringBuilder();
                reader = new BufferedReader(new FileReader(scriptFile));
                String line;
                while((line=reader.readLine())!=null){
                    scriptBuilder.append(line);
                }
                script = scriptBuilder.toString();
            } catch (Exception e){
                throw e;
            } finally {
                if(reader!=null){
                    reader.close();
                }
            }
        } else {
            throw new Exception("Script file does not exist");
        }
    }
    
    /**
     * Setup the script interpreter
     */
    private void setupJSInterpreter() throws Exception {
        // Set up the javascript engine
        ScriptEngineManager mgr = new ScriptEngineManager();
        jsEngine = mgr.getEngineByName("JavaScript");

        storageApi = new StorageClient(apiProperties);
        datasetApi = new DatasetClient(apiProperties);
        workflowApi = new WorkflowClient(apiProperties);
        jsEngine.put("storageApi", storageApi);
        jsEngine.put("dataApi", datasetApi);
        jsEngine.put("workflowApi", workflowApi);
    }    
    
    public static  void  main(String[] args){
        if(args.length>=2){
            try {
                File props = new File(args[1]);
                File scr = new File(args[0]);
                String[] extraArgs;
                
                if(args.length>2){
                    extraArgs = new String[args.length - 2];
                    int count = 0;
                    for(int i=2;i<args.length;i++){
                        extraArgs[count] = args[i];
                        count++;
                    }
                } else {
                    extraArgs = new String[0];
                }
                APIScriptExecutor executor = new APIScriptExecutor(scr, props, args);
                
            } catch (Exception e){
                System.out.println("Error executing script: " + e.getMessage());
                e.printStackTrace();
                System.exit(2);
            }
        } else {
            System.out.println("Not enough arguments");
            System.exit(1);
        }
        System.exit(0);
        
    }
}
