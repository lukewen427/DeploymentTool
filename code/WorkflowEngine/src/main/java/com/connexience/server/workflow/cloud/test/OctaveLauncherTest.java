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
package com.connexience.server.workflow.cloud.test;

import com.connexience.server.workflow.cloud.services.*;
import com.connexience.server.workflow.engine.datatypes.FileWrapper;
import com.connexience.server.workflow.service.*;

import org.pipeline.core.data.*;
import org.pipeline.core.data.io.*;

import java.io.*;
import java.util.*;

/**
 * Tests octave launching
 * @author nhgh
 */
public class OctaveLauncherTest {

    public static void main(String[] args0){
        OctaveDataProcessorService service = null;
        try {
            service = new OctaveDataProcessorService();

            DataProcessorCallMessage msg = new DataProcessorCallMessage();
            msg.setMaxStdOutBufferSize(10000);
            
            service.setCallMessage(msg);
            
            service.getEditableProperties().add("Param1", "Parameter 1");
            service.getEditableProperties().add("Param2", 45.0);
            service.getEditableProperties().add("Param3", 3);
            service.getEditableProperties().add("Param4", new File("/Users/nhgh"));
            
            service.executionAboutToStart();
            service.sendOctaveCommand("pwd", true);
            
            DelimitedTextDataImporter importer = new DelimitedTextDataImporter();
            importer.setImportColumnNames(false);
            importer.setDataStartRow(15);
            Data data = importer.importFile(new File("/Users/nhgh/data.csv"));
            service.assignData("weetabix", data);
            Data retrieved = service.retrieveData("weetabix");

            FileWrapper wrapper = new FileWrapper();
            wrapper.addFile("test.dat");
            wrapper.addFile("workspace.mat");
            service.assignFileList("workspace", wrapper);
            ArrayList<String> files = service.retrieveFileList("workspace");
            for(int i=0;i<files.size();i++){
                System.out.println("File(" + i + "):" + files.get(i));
            }
            service.execute();
            
            Thread.sleep(2000);
            

            
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {service.allDataProcessed();}catch(Exception e){e.printStackTrace();}
        }
    }
}
