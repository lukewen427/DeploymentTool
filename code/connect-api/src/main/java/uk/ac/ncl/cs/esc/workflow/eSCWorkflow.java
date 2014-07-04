package uk.ac.ncl.cs.esc.workflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.pipeline.core.drawing.layout.BlockModelPosition;
import org.pipeline.core.drawing.model.DefaultDrawingModel;

import com.connexience.api.StorageClient;
import com.connexience.api.WorkflowClient;
import com.connexience.server.workflow.blocks.processor.DataProcessorBlock;
import com.connexience.server.workflow.json.JSONDrawingExporter;

import uk.ac.ncl.cs.esc.connection.cloudConnection;
import uk.ac.ncl.cs.esc.connection.connection;
import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.read.BlockSet;

public class eSCWorkflow {
	
	public HashMap<String,ByteArrayOutputStream>creatWorkflow(String cloudName,ArrayList<Object> partition,String partitionName,
			ArrayList<ArrayList<String>> connections,BlockSet blockset,ArrayList<ArrayList<String>>inputs,
			HashMap<String, ByteArrayOutputStream> theresults,ArrayList<String> heads) throws Exception{

		cloudConnection coCloud=new cloudConnection();
		connection con=coCloud.creatCon(cloudName);
		StorageClient Sclient =con.getStorageAPI();
		WorkflowClient wfClient=con.getWorkflowAPI();
		HashMap<String,String> resultInfo=null;
		deployWF wf=new deployWFIm();
		if(!theresults.isEmpty()){
			resultInfo=wf.fileUpload(theresults, Sclient);
		}
		 int b=0;
			DefaultDrawingModel drawing = new DefaultDrawingModel();
		for(String headNode: heads){
			Block startBlock=blockset.getBlock(headNode);
			String blockId=startBlock.getBlockId();
			String serviceId=startBlock.getserviceId();
			DataProcessorBlock serviceBlock;
			if(serviceId.equals("blocks-core-io-csvimport")){
				String documentId=null;
				String serviceName=null;
				
				
				IDynamicWorkflowService service = parser.getService("blocks-core-io-csvimport");
				serviceBlock = parser.createBlock(service);
				createblock(b,serviceBlock,documentId,serviceId,api,drawing,serviceName);
				b++;
				
			}else{
				IDynamicWorkflowService service1 = parser.getService(serviceId);
				serviceBlock = parser.createBlock(service1);
				createblock(b,serviceBlock,null,serviceId,api,drawing,null);
				b++;
				
				for(int hh=0;hh<inputs.size();hh++){
					   ArrayList<String>input=inputs.get(hh);
					    if(blockId.endsWith(input.get(1))){
								String findId=input.get(0)+","+blockId;
								String documentId=resultInfo.get(findId);
								String theserviceId="blocks-core-io-csvimport";
								IDynamicWorkflowService service = parser.getService("blocks-core-io-csvimport");
								DataProcessorBlock Block = parser.createBlock(service);
								createblock(b,Block,documentId,theserviceId,api,drawing,partitionName);
								b++;
							     String inputportName=input.get(5);
								drawing.connectPorts(Block.getOutput("imported-data"), serviceBlock.getInput(inputportName));
							}
						}
			}
			
			offspringNodes( serviceBlock,startBlock,blockset,connections,partition,
					drawing,b,parser,api,new ArrayList<String>());
		}
		
		HashMap<String, ByteArrayOutputStream> result=new HashMap<String, ByteArrayOutputStream>();
		getResult(drawing,partitionName,parser,api,result);
        return result;  
	}
	
	/* create the workflow and execute the workflow*/
	private void getResult(DefaultDrawingModel drawing,String partitionName,getConnection parser,API api,HashMap<String, ByteArrayOutputStream> result) throws Exception{
		
		JSONDrawingExporter exporter = new JSONDrawingExporter(drawing); 
		IWorkflow wf = parser.createWorkflow(partitionName, drawing);
		IWorkflowParameterList params = (InkspotWorkflowParameterList)api.createObject(IWorkflowParameterList.XML_NAME);
		IWorkflowInvocation invocation = api.executeWorkflow(wf, params);
		 while(invocation.getStatus().equals(IWorkflowInvocation.WORKFLOW_RUNNING) || invocation.getStatus().equals(IWorkflowInvocation.WORKFLOW_WAITING)){
	    	 try {
	    		 Thread.sleep(1000);
	    	 } catch (Exception e){}
	    	 System.out.println("Checking status");
	    	 invocation =api.getWorkflowInvocation(invocation.getInvocationId());
	     }
		  List<IObject> results = api.getFolderContents(invocation);
	//	  HashMap<String,ByteArrayOutputStream> result=new HashMap<String,ByteArrayOutputStream>();
		  for (IObject r : results) {
			  if (r instanceof IDocument) {
				  IDocument d = (IDocument) r;
				  String getName= d.getName();
				  String[] thename=getName.split("\\.");
				  ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				  api.download(d, outStream);
				  outStream.flush();
				  result.put(thename[0], outStream);
			  }
		  }
		  
	} 
	
	/* this method is used the create the offspring nodes the start nodes*/
	private void offspringNodes(DataProcessorBlock serviceBlock,Block startBlock,BlockSet blockset,ArrayList<ArrayList<String>> connections,
			ArrayList<Object>partition,DefaultDrawingModel drawing,int b,getConnection parser,API api,ArrayList<String> visited) throws Exception{
		
		String blockId=startBlock.getBlockId();
		visited.add( blockId);
		for(int a=0;a<connections.size();a++){
			ArrayList<String> connection=connections.get(a);
			String source=connection.get(0);
			String destination=connection.get(1);
			Block destinationBlock=blockset.getBlock(destination);
			String outputPort=null;
			String inputPort=null;
			if(blockId.equals(source)){
				if(partition.contains(destinationBlock)&&!visited.contains(destination)){
					String serviceName=null;
					String documentId=null;
					String offspringSeviceId=destinationBlock.getserviceId();
					IDynamicWorkflowService service = parser.getService(offspringSeviceId);
					DataProcessorBlock Block = parser.createBlock(service);
					createblock(b,Block,documentId,offspringSeviceId,api,drawing,serviceName);
					b++;
					for(int fx=0;fx<connections.size();fx++){
						ArrayList<String> connect=connections.get(fx);
						String startNode=connect.get(0);
						String endNode=connect.get(1);
						if(startNode.equals(blockId)&&endNode.equals(destination)){
							outputPort=connect.get(4);
							inputPort=connect.get(5);
							break;
						}
					}
					drawing.connectPorts(serviceBlock.getOutput(outputPort),Block.getInput(inputPort));
					offspringNodes(Block,destinationBlock,blockset,connections,partition,drawing,b, parser,api,visited);
				}
				if(!partition.contains(destinationBlock)){
					String theserviceName=blockId+","+destination;
					String theserviceId="blocks-core-io-csvexport";
					IDynamicWorkflowService service3=parser.getService(theserviceId);
					DataProcessorBlock exportBlock = parser.createBlock(service3);
					createblock(b,exportBlock,null,theserviceId,api,drawing,theserviceName);
					b++;
					for(int fx=0;fx<connections.size();fx++){
						ArrayList<String> connect=connections.get(fx);
						String startNode=connect.get(0);
						String endNode=connect.get(1);
				//		System.out.println(blockId);
				//		System.out.println(theserviceName);
						if(startNode.equals(blockId)&&endNode.equals(destination)){
							outputPort=connect.get(4);
							inputPort=connect.get(5);
							break;
						}
					}
				///	System.out.println(outputPort);
				//	System.out.println(inputPort);
					drawing.connectPorts(serviceBlock.getOutput(outputPort),exportBlock.getInput("input-data"));
				}
			}
			
			
		}
			
	}
	
	private void createblock(int b,DataProcessorBlock Block,String documentId,String serviceId,API api,DefaultDrawingModel drawing,String theServiceName ) throws Exception{
		
		if(theServiceName==null){
			theServiceName="out";
		}
		if(serviceId.equals("blocks-core-io-csvimport")){
			if(documentId==null){
				documentId="ff8080813a022d6e013a0268e5310159";
			}
			
			 IDocument doc = api.getDocument(documentId);
			 DocumentRecordWrapper wrapper = new DocumentRecordWrapper(doc);
			 Block.getEditableProperties().add("Source", wrapper);
		}
		
		if(serviceId.equals("blocks-core-io-csvexport")){
			Block.getEditableProperties().add("FileName", theServiceName+".csv");
	       
		}
		 drawing.addBlock(Block);
	        BlockModelPosition p = new BlockModelPosition();
	      	p.setHeight(60);
	        p.setWidth(60);
	        p.setTop(100);
	        p.setLeft(50+b*100);
	        drawing.getDrawingLayout().addLocationData(Block, p);
	}
	
}
