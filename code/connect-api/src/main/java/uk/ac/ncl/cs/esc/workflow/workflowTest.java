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
	
	     while(blockKey.hasNext()){
			 String blockid=blockKey.next();
			 String serviceId= workflow.getBlockServiceId(blockid, "877");
			 DataProcessorServiceDefinition def = api.getService(serviceId);
			 XmlDataStore wfData=def.getServiceProperties();
			 DefaultDrawingModel drawing =new DefaultDrawingModel();
			 drawing.recreateObject(wfData);
			 JSONDrawingExporter exporter = new JSONDrawingExporter(drawing);
			 System.out.println(exporter.saveToJson());
		
		 }
	//	System.out.println(workflow.Workflowlist());
	//	test2.getWorkflowAsJsonObject("877");
	//	test2.Blocklist("877");
	//	test2.getSource("877");
		
		
	//	API api= con.getAPI();
	//	deployWF wf=new deployWFIm(api);
	}
}
