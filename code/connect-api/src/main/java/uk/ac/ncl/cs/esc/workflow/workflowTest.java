package uk.ac.ncl.cs.esc.workflow;

import java.util.HashMap;
import java.util.Iterator;

import org.pipeline.core.drawing.model.DefaultDrawingModel;
import org.pipeline.core.xmlstorage.XmlDataStore;


import com.connexience.server.workflow.api.API;
import com.connexience.server.workflow.json.JSONDrawingExporter;
import com.connexience.server.workflow.service.DataProcessorServiceDefinition;

import uk.ac.ncl.cs.esc.connection.cloudConnection;
import uk.ac.ncl.cs.esc.connection.connection;

public class workflowTest {
	public static void main(String [] args) throws Exception{
		cloudConnection test=new cloudConnection();
		connection con=test.creatCon("cloud0");
		API api= con.getAPI();
		WorkflowInfo workflow= new workflowInfoIm(con);
		 HashMap<String, String>blocks=workflow.Blocklist("877");
		 Iterator<String> blockKey=blocks.keySet().iterator();
	
	
	}
}
