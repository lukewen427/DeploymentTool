package uk.ac.ncl.cs.esc.test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.pipeline.core.drawing.layout.BlockModelPosition;
import org.pipeline.core.drawing.model.DefaultDrawingModel;

import com.connexience.api.StorageClient;
import com.connexience.api.WorkflowClient;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.workflow.WorkflowDocument;
import com.connexience.server.workflow.api.API;
import com.connexience.server.workflow.blocks.processor.DataProcessorBlock;

import uk.ac.ncl.cs.esc.connection.cloudConnection;
import uk.ac.ncl.cs.esc.connection.connection;
import uk.ac.ncl.cs.esc.workflow.WorkflowInfo;
import uk.ac.ncl.cs.esc.workflow.deployWF;
import uk.ac.ncl.cs.esc.workflow.deployWFIm;
import uk.ac.ncl.cs.esc.workflow.workflowInfoIm;

public class downloadWF {
	
	public  static void main(String args[]) throws Exception{
		cloudConnection test=new cloudConnection();
		connection con=test.creatCon("cloud0");
		String workflowId="937";
		WorkflowInfo workflow= new workflowInfoIm(con);
		HashMap<String, String>blocks=workflow.Blocklist(workflowId);
		ArrayList<ArrayList<String>> connection=workflow.getConnection(workflowId);
	//	System.out.println(blocks);
	//	System.out.println(connection);
		API api=con.getAPI();

		deployWF de=new deployWFIm(api);
		Iterator<String>blockIds=blocks.keySet().iterator();
		DefaultDrawingModel drawing = new DefaultDrawingModel();
		int b=0;
		HashMap<String,DataProcessorBlock> theblock=new HashMap<String,DataProcessorBlock>();
		while(blockIds.hasNext()){
			String blockId=blockIds.next();
			String serviceId=workflow.getBlockServiceId(blockId, workflowId);
			DataProcessorBlock Block=de.createBlock(serviceId);
			theblock.put(blockId, Block);
     		createblock(b,Block,null,serviceId,api,drawing,null);
			b++;
		}
		
		for(int a=0;a<connection.size();a++){
			ArrayList<String> link=connection.get(a);
			String s=link.get(0);
			String d=link.get(1);
			String output=link.get(4);
			String input=link.get(5);
			DataProcessorBlock outputBlock=theblock.get(s);
			DataProcessorBlock inputputBlock=theblock.get(d);
			drawing.connectPorts(outputBlock.getOutput(output),inputputBlock.getInput(input));
		}
		
		//    WorkflowDocument newworkflow=de.createWorkflow("test2", drawing);
		     HashMap<String, ByteArrayOutputStream> result=new HashMap<String, ByteArrayOutputStream>();
		     StorageClient Sclient =con.getStorageAPI();
		//	 WorkflowClient wfClient=con.getWorkflowAPI();
			de.executeWF(drawing, "test2", Sclient, result);
	}
	
	private static void createblock(int b,DataProcessorBlock Block,String documentId,String serviceId,API api,
													DefaultDrawingModel drawing,String theServiceName ) throws Exception{
		 System.out.println(serviceId);
		if(theServiceName==null){
			theServiceName="out";
		}
		if(serviceId.equals("blocks-core-io-csvimport-2")){
			 documentId="211";
			 DocumentRecord doc=api.getDocument(documentId);
			 DocumentRecord wrapper = new DocumentRecord();
			 wrapper.populateCopy(doc);
			 Block.getEditableProperties().add("Source", doc);
			
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
