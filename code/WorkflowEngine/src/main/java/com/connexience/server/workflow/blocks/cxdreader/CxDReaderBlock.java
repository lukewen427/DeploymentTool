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
package com.connexience.server.workflow.blocks.cxdreader;

import com.connexience.server.workflow.engine.*;

import org.pipeline.core.drawing.*;
import org.pipeline.core.drawing.model.*;
import org.pipeline.core.data.*;
import org.pipeline.core.data.cxd.*;

import java.io.*;

/**
 * This block just loads the specified CxD File into memory. It
 * is a temporary block that will have no purpose when the proper
 * data link blocks are created.
 * @author hugo
 */
public class CxDReaderBlock extends DefaultBlockModel {
	private static final long serialVersionUID = 1L;

	public CxDReaderBlock() throws DrawingException {
        super();
        getEditableProperties().add("FileName", "/Users/hugo/data.cxd", "Name of CxD file to import");
        
        DefaultOutputPortModel output = new DefaultOutputPortModel("imported-data", PortModel.RIGHT_OF_BLOCK, 50, this);
        output.addDataType(DataTypes.DATA_WRAPPER_TYPE);
        addOutputPort(output);
    }

    /** Import CxD File */
    @Override
    public BlockExecutionReport execute() throws BlockExecutionException {
        Data outputData = null;
        File dataFile = new File(getEditableProperties().stringValue("FileName", ""));
        if(dataFile.exists()){
            try {
                CxdFile file = new CxdFile(dataFile);
                outputData = file.load();
                DrawingDataUtilities.setOutputData(getOutput("imported-data"), outputData);
            } catch (Exception e){
                e.printStackTrace();
                return new BlockExecutionReport(this, BlockExecutionReport.INTERNAL_ERROR, "Error loading data: " + e.getMessage());
            }
        } else {
            return new BlockExecutionReport(this, BlockExecutionReport.INTERNAL_ERROR, "Data file does not exist");
        }
        return new BlockExecutionReport(this);
        
    }

    
}
