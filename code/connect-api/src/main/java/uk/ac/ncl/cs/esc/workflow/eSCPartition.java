package uk.ac.ncl.cs.esc.workflow;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.pipeline.core.drawing.layout.BlockModelPosition;
import org.pipeline.core.drawing.model.DefaultDrawingModel;

import uk.ac.ncl.cs.esc.connection.cloudConnection;
import uk.ac.ncl.cs.esc.connection.connection;
import uk.ac.ncl.cs.esc.read.Block;

import com.connexience.api.StorageClient;
import com.connexience.api.WorkflowClient;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.workflow.api.API;
import com.connexience.server.workflow.blocks.processor.DataProcessorBlock;

public class eSCPartition {
	
	WorkflowInfo orgWorkflow;
	String orgWorkflowId;
	ArrayList<ArrayList<String>> connections;
	String importservice="blocks-core-io-csvimport-2";
	String exportservice="blocks-core-io-csvexport";
	// the output port of CSVImport
	String importPORT="imported-data";
	// the input port of CSVExport
	String exportPORT="input-data";
	deployWF wf;
	
	public HashMap<String,ByteArrayOutputStream> createSCWorkflow(String cloudName,String partitionName,HashMap<String, String> partition,
			ArrayList<String> heads,HashMap<String, ByteArrayOutputStream> theresults,ArrayList<ArrayList<String>> connections) throws Exception{
		this.connections=connections;
	//	System.out.println(connections);
		cloudConnection coCloud=new cloudConnection();
		connection con=coCloud.creatCon(cloudName);
		API api=con.getAPI();
		this.wf=new deployWFIm(api);
		 StorageClient Sclient =con.getStorageAPI();
	//	 WorkflowClient wfClient=con.getWorkflowAPI();
		 HashMap<String,String> resultInfo=null;
		if(!theresults.isEmpty()){
			resultInfo=wf.fileUpload(theresults, Sclient);
		}
		int b=0;
		DefaultDrawingModel drawing = new DefaultDrawingModel();
		for(String headNode: heads){
			String blockId=headNode;
			String serviceId=partition.get(blockId);
			DataProcessorBlock serviceBlock;
			if(serviceId.equals(importservice)){
				String documentId=null;
				String serviceName=null;
				
			//	IDynamicWorkflowService service = parser.getService("blocks-core-io-csvimport");
				serviceBlock = wf.createBlock(importservice);
				createblock(b,serviceBlock,documentId,serviceId,api,drawing,serviceName);
				b++;
				
			}else{
				
				serviceBlock = wf.createBlock(serviceId);
				createblock(b,serviceBlock,null,serviceId,api,drawing,null);
				b++;
				
			
				for(int a=0;a<connections.size();a++){
					ArrayList<String> link=connections.get(a);
					String s=link.get(0);
					String d=link.get(1);
					if(d.equals(blockId)){
						String findId=s+","+blockId;
				//		System.out.println(findId);
						String documentId=resultInfo.get(findId);
						String input=link.get(5);
						String theserviceId=importservice;
						DataProcessorBlock importBlock = wf.createBlock(theserviceId);
						createblock(b,importBlock,documentId,theserviceId,api,drawing,"import");
						b++;
						drawing.connectPorts(importBlock.getOutput(importPORT),serviceBlock.getInput(input));
					  }
					}
				}
			
			offspringNodes( serviceBlock,headNode,partition,
					drawing,b,wf,api,new ArrayList<String>(),resultInfo);
		  }
		  HashMap<String, ByteArrayOutputStream> result=new HashMap<String, ByteArrayOutputStream>();
		  //   StorageClient Sclient =con.getStorageAPI();
		//	 WorkflowClient wfClient=con.getWorkflowAPI();
		    wf.executeWF(drawing, partitionName, Sclient, result);
		
		return result;
		
	}
	
	
	public void killExecution(){
		String invId=wf.getInvocationId();
		wf.killExe(invId);
	}
	private void offspringNodes(DataProcessorBlock serviceBlock,
			String startNode, HashMap<String, String> partition, DefaultDrawingModel drawing,
			int b, deployWF wf, API api, ArrayList<String> visited,HashMap<String,String> resultInfo) throws Exception {
		 	visited.add(startNode);
		 	for(int a=0;a<connections.size();a++){
		 		ArrayList<String> connection=connections.get(a);
				String source=connection.get(0);
				String destination=connection.get(1);
				String outputPort=null;
				String inputPort=null;
				if(startNode.equals(source)){
					if(partition.containsKey(destination) && !visited.contains(destination)){
						String serviceName=null;
						String documentId=null;
						String offspringSeviceId=partition.get(destination);
					//	System.out.println(destination);
					//	System.out.println(offspringSeviceId);
						DataProcessorBlock Block = wf.createBlock(offspringSeviceId);
						createblock(b,Block,documentId,offspringSeviceId,api,drawing,serviceName);
						b++;
						for(int fx=0;fx<connections.size();fx++){
							ArrayList<String> connect=connections.get(fx);
							String sNode=connect.get(0);
							String eNode=connect.get(1);
							if(startNode.equals(sNode)&&eNode.equals(destination)){
								outputPort=connect.get(4);
								inputPort=connect.get(5);
								break;
							}
						}
						drawing.connectPorts(serviceBlock.getOutput(outputPort),Block.getInput(inputPort));
						// add the input block which not in the partition
						ArrayList<String>sNode=getSourceNode(destination);
						if(!sNode.isEmpty()){
							for(int i=0;i<sNode.size();i++){
								String nodeId=sNode.get(i);
								if(!partition.containsKey(nodeId)){
									for(int h=0;h<connections.size();h++){
										ArrayList<String> link=connections.get(h);
										String s=link.get(0);
										String d=link.get(1);
										if(d.equals(destination)&&s.equals(nodeId)){
											String findId=s+","+d;
										    documentId=resultInfo.get(findId);
											String input=link.get(5);
											String theserviceId=importservice;
											DataProcessorBlock importBlock = wf.createBlock(theserviceId);
											createblock(b,importBlock,documentId,theserviceId,api,drawing,"import");
											b++;
											drawing.connectPorts(importBlock.getOutput(importPORT),serviceBlock.getInput(input));
										  }
										}
								}
							}
						}
						
						offspringNodes(Block,destination,partition,drawing,b,wf,api,visited,resultInfo);
					}
					if(!partition.containsKey(destination)){
						String theserviceName=startNode+","+destination;
						String theserviceId=exportservice;
						DataProcessorBlock exportBlock = wf.createBlock(theserviceId);
						createblock(b,exportBlock,null,theserviceId, api,drawing,theserviceName);
						b++;
						for(int fx=0;fx<connections.size();fx++){
							ArrayList<String> connect=connections.get(fx);
							String sNode=connect.get(0);
							String eNode=connect.get(1);
					//		System.out.println(blockId);
					//		System.out.println(theserviceName);
							if(startNode.equals(sNode) && eNode.equals(destination)){
							
								outputPort=connect.get(4);
								inputPort=connect.get(5);
								break;
							}
							
						}
						drawing.connectPorts(serviceBlock.getOutput(outputPort),exportBlock.getInput(exportPORT));
					}
				}
				
		 	}
		
	}
	
	ArrayList<String> getSourceNode(String destination){
		ArrayList<String> dNode=new ArrayList<String>();
		for(int a=0;a<connections.size();a++){
		
			String source=connections.get(a).get(0);
			String des=connections.get(a).get(1);
			if(destination.equals(des)){
				if(!dNode.contains(source)){
					dNode.add(source);
				}
			}
			
		}
		
		return dNode;
	}
	
	private static void createblock(int b, DataProcessorBlock Block,
			String documentId, String serviceId, API api,
			DefaultDrawingModel drawing, String theServiceName)
			throws Exception {
	
		if (theServiceName == null) {
			theServiceName = "out";
		}
		if (serviceId.equals("blocks-core-io-csvimport-2")) {
			documentId = "211";
			DocumentRecord doc = api.getDocument(documentId);
			DocumentRecord wrapper = new DocumentRecord();
			wrapper.populateCopy(doc);
			Block.getEditableProperties().add("Source", doc);

		}

		if (serviceId.equals("blocks-core-io-csvexport")) {
			Block.getEditableProperties().add("FileName",
					theServiceName + ".csv");

		}
		drawing.addBlock(Block);
		BlockModelPosition p = new BlockModelPosition();
		p.setHeight(60);
		p.setWidth(60);
		p.setTop(100);
		p.setLeft(50 + b * 100);
		drawing.getDrawingLayout().addLocationData(Block, p);
	}
}
