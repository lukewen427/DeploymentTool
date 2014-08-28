package uk.ac.ncl.cs.esc.workflow;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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
		String workflowId="1045";
		WorkflowInfo workflow= new workflowInfoIm(con);
		HashMap<String, String>blocks=workflow.Blocklist(workflowId);
	//	System.out.println(blocks);
		ArrayList<ArrayList<String>> connection=workflow.getConnection(workflowId);
		ArrayList<String> head=getHead(connection);
		HashMap<String, String> partition=getPartition(blocks,workflowId,workflow);
	//	 System.out.println(head);
 		 System.out.println(connection);
//		eSCPartition dep=new eSCPartition(workflowId);
		
//		dep.createSCWorkflow("cloud0", partition, head,new HashMap<String, ByteArrayOutputStream>());
	
	}
	private static HashMap<String, String> getPartition(HashMap<String, String>blocks,String workflowId,WorkflowInfo workflow) throws Exception{
		HashMap<String, String> partition=new HashMap<String, String>();
		Iterator<String>blockIds=blocks.keySet().iterator();
		while(blockIds.hasNext()){
			String blockId=blockIds.next();
			String serviceId=workflow.getBlockServiceId(blockId, workflowId);
			partition.put(blockId,serviceId);
		}
	
		return partition;
	}
	
	private static ArrayList<String> getHead(ArrayList<ArrayList<String>> connection){
		ArrayList<String> head=new ArrayList<String>();
		for(ArrayList<String> link:connection){
			boolean isHead=true;
			String s=link.get(0);
			for(ArrayList<String> temp:connection){
				String d=temp.get(1);
				if(s.equals(d)){
					isHead=false;
					break;
				}
			}
			if(isHead){
				if(!head.contains(s)){
					head.add(s);
				}
				
			}
		}
		
		return head;
	}
}
