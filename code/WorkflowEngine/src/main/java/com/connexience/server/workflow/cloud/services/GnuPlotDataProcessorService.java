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

import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.workflow.cloud.services.support.GnuPlotEngine;
import java.io.File;
import java.util.HashMap;
import org.pipeline.core.data.Data;



/**
 * This class knows how to operate a GNU plot instance. It has a preamble script
 * which sets the plot options and then a data passing loop which passes the
 * data columns into gnuplot via the stdin connection of the process.
 * @author nhgh
 */
public class GnuPlotDataProcessorService extends CloudDataProcessorService {
    private GnuPlotEngine engine;
    
    /** Start gnuplot and send the preamble file */
    @Override
    public void executionAboutToStart() throws Exception {
        
    }

    /** Finish the plot and stop gnuplot */
    @Override
    public void allDataProcessed() throws Exception {

    }

    /** Pass a chunk of data to gnuplot */
    @Override
    public void execute() throws Exception {
        engine = new GnuPlotEngine(this, "initplot.txt", "init.js", getEditableProperties().stringValue("Filename", "chart.jpg"));
        File plotFile = engine.createPlot();
        
        if(plotFile.exists()){
             DocumentRecord document = new DocumentRecord();
             document.setName(plotFile.getName());
             document.setDescription(getEditableProperties().stringValue("Comments", "GNUPlot generated graph"));
             document = createApiLink().saveDocument(getInvocationFolder(), document);
             createApiLink().uploadFile(document, plotFile);
        } else {
            throw new Exception("Output file: " + plotFile.getName() + " does not exist in workflow folder");
        }
        
        if(getEditableProperties().booleanValue("UploadCommands", false)==true){
            DocumentRecord cmdDocument = new DocumentRecord();
            cmdDocument.setName(getCallMessage().getContextId() + "-gnuplot.txt");
            cmdDocument.setDescription("GNUPlot command file");
            cmdDocument = createApiLink().saveDocument(getInvocationFolder(), cmdDocument);
            createApiLink().uploadFile(cmdDocument, engine.getCommandFile());
        }
    }
}