package uk.ac.ncl.cs.esc.workflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pipeline.core.drawing.BlockModel;
import org.pipeline.core.drawing.model.DefaultDrawingModel;
import org.pipeline.core.xmlstorage.XmlDataObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.io.XmlDataStoreStreamReader;

import com.connexience.api.StorageClient;
import com.connexience.api.WorkflowClient;
import com.connexience.api.model.EscDocument;
import com.connexience.api.model.EscWorkflow;
import com.connexience.server.workflow.json.JSONDrawingExporter;

import uk.ac.ncl.cs.esc.connection.cloudConnection;
import uk.ac.ncl.cs.esc.connection.connection;

public class workflowInfoIm implements WorkflowInfo {
	private connection con;
	public workflowInfoIm(connection con){
		this.con=con;
	}

	public JSONObject getWorkflowAsJsonObject(String workflowId)
			throws Exception {
		
		 StorageClient client =con.getStorageAPI();
		 EscDocument document=client.getDocument(workflowId);
	
		 ByteArrayOutputStream buffer=new ByteArrayOutputStream();
		 client.download(document, buffer);

		 buffer.flush();
		 buffer.close();
		 ByteArrayInputStream inStream=new ByteArrayInputStream(buffer.toByteArray());
		 XmlDataStoreStreamReader reader =new XmlDataStoreStreamReader(inStream);
		 XmlDataStore wfData =reader.read();
		//wfData.getNames();
		 DefaultDrawingModel drawing =new DefaultDrawingModel();
		 drawing.recreateObject(wfData);
		 JSONDrawingExporter exporter = new JSONDrawingExporter(drawing);
	//	 System.out.println(exporter.saveToJson());
		return exporter.saveToJson();
	}

	public HashMap<String, String> Blocklist(String workflowId)
			throws Exception {
		// TODO Auto-generated method stub
		HashMap<String,String> Blocklist=new HashMap<String,String>();
		JSONObject dataObject= getWorkflowAsJsonObject( workflowId);
		JSONObject blocks = dataObject.getJSONObject("blocks");
		
		JSONArray blockArray = blocks.getJSONArray("blockArray");
		
        int blockCount = blocks.getInt("blockCount");
        System.out.println(blockCount);
        for (int i = 0; i < blockCount; i++)
        {
        	
        	Blocklist.put((String) blockArray.getJSONObject(i).get("guid"),(String) blockArray.getJSONObject(i).get("label") );
        	
        }
  //      System.out.println(Blocklist);
		return Blocklist;
	}

	public HashMap<String, ArrayList<String>> ConnectionMap(String workflowId)
			throws Exception {
		  ArrayList<String> destinationBlocks=new ArrayList<String>();
		    HashMap<String,ArrayList<String>> connectionmap=new HashMap<String,ArrayList<String>>();
			JSONObject dataObject= getWorkflowAsJsonObject( workflowId);
			JSONObject connection=dataObject.getJSONObject("connections");
			JSONArray connectionArray=connection.getJSONArray("connectionArray");
			int connectionCount=connection.getInt("connectionCount");
			for(int i=0;i<connectionCount;i++){
			String destinationBlockGuid= (String) connectionArray.getJSONObject(i).get("destinationBlockGuid");
			String sourceBlockGuid=(String) connectionArray.getJSONObject(i).get("sourceBlockGuid");
			 ArrayList<String> temp=new ArrayList<String>();
			 
			if(connectionmap.containsKey(sourceBlockGuid)){
				temp.clear();
				temp=connectionmap.get(sourceBlockGuid);
				temp.add(destinationBlockGuid);
				destinationBlocks=(ArrayList)temp.clone();
				connectionmap.put(sourceBlockGuid, destinationBlocks);
			 }else{
				 temp.add(destinationBlockGuid);
				 destinationBlocks=(ArrayList)temp.clone();
				connectionmap.put(sourceBlockGuid, destinationBlocks);
			 }
			  temp.clear();
			}
	//		System.out.println(connectionmap);
			return connectionmap;
	}

	public HashMap<String, String> Workflowlist() throws Exception {
		 WorkflowClient wfClient=con.getWorkflowAPI();
		 HashMap<String,String> workflowmap=new HashMap<String,String>();
		 EscWorkflow[] workflow= wfClient.listWorkflows();
		 for(EscWorkflow w: workflow){
			 String workflowName=w.getName();
			 String workflowId=w.getId();
			 workflowmap.put(workflowId, workflowName);
			 
		 }
				return workflowmap;
	}

	public String getBlockServiceId(String BlockId, String workflowId)
			throws Exception {
		JSONObject dataObject= getWorkflowAsJsonObject( workflowId);
		JSONObject blocks = dataObject.getJSONObject("blocks");
		JSONArray blockArray = blocks.getJSONArray("blockArray");
        int blockCount = blocks.getInt("blockCount");
        String serviceId=null;
        for (int i = 0; i < blockCount; i++)
        {
        	
         String getBlockId=(String) blockArray.getJSONObject(i).get("guid");
         if(getBlockId.equals(BlockId)){
        	  serviceId=(String) blockArray.getJSONObject(i).get("serviceId");
        	 
         }	
         
        }
		if(serviceId!=null){
			return serviceId;
		}else{
			throw new Exception("The Block is not included in this workflow");
		}
	}
		

	public ArrayList<String> getPorts(String workflowId, String sourceId,
			String endId) throws Exception {
		// TODO Auto-generated method stub
	     ArrayList<String>ports=new ArrayList<String>();
		
		JSONObject dataObject= getWorkflowAsJsonObject( workflowId);
		JSONObject connection=dataObject.getJSONObject("connections");
		JSONArray connectionArray=connection.getJSONArray("connectionArray");
		int connectionCount=connection.getInt("connectionCount");
		for(int i=0;i<connectionCount;i++){
			String destinationPort= (String) connectionArray.getJSONObject(i).get("destinationPortName");
			String sourcePort=(String) connectionArray.getJSONObject(i).get("sourcePortName");
			String destinationBlockGuid= (String) connectionArray.getJSONObject(i).get("destinationBlockGuid");
			String sourceBlockGuid=(String) connectionArray.getJSONObject(i).get("sourceBlockGuid");
			if(sourceBlockGuid.equals(sourceId)&& destinationBlockGuid.equals(endId)){
				
				ArrayList<String>temp=new ArrayList<String>();
				temp.add(sourcePort);
				temp.add(destinationPort);
				ports=(ArrayList<String>) temp.clone();
			}
		}
		return ports;
		
	}

	public ArrayList<ArrayList<String>> getSource(String workflowId)
			throws Exception {
	ArrayList<ArrayList<String>> reSourceSet=new ArrayList<ArrayList<String>>();
		
		JSONObject dataObject= getWorkflowAsJsonObject( workflowId);
		JSONObject blocks = dataObject.getJSONObject("blocks");
		JSONArray blockArray = blocks.getJSONArray("blockArray");
	//	JSONArray sourceArray=blocks.getJSONArray("jsonValue");
        int blockCount = blocks.getInt("blockCount");
     
        String serviceId=null;
        for(int i=0;i<blockCount;i++){  
        	ArrayList<String> resource=new ArrayList<String>();
        	 serviceId=(String) blockArray.getJSONObject(i).get("serviceId");
        		ArrayList<String> temp=new ArrayList<String>();
        	// there may other import block 
        	 if(serviceId.equals("blocks-core-io-csvimport")){
        	     JSONObject value= blockArray.getJSONObject(i);
        	     JSONObject property=value.getJSONObject("properties");
        		 JSONArray propertyArray=property.getJSONArray("propertyArray");
        		 int propertyCount=property.getInt("propertyCount");
       	     	 for(int a=0;a<propertyCount;a++){
       	     	
       	     		JSONObject jsonValue=propertyArray.getJSONObject(a);
       	     		if(jsonValue.has("jsonValue")){
       	     			JSONObject thesource=jsonValue.getJSONObject("jsonValue");
       	     			String id=thesource.getString("id");
       	     			String name=thesource.getString("name");
       	     			temp.add(id);
       	     			temp.add(name);
       	     		}
       	   	
       		 }
        		 String getBlockId=(String) blockArray.getJSONObject(i).get("guid");
        		 temp.add(getBlockId);
        		 resource=(ArrayList<String>) temp.clone();
        	 }
        	 if(!resource.isEmpty()){
        		 reSourceSet.add(resource);
        	 }
        }
   //     System.out.println(reSourceSet);
		return reSourceSet;
	}

	public ArrayList<String> getInputports(String workflowId, String blockId)
			throws Exception {
		ArrayList<String>inputports=new ArrayList<String>();
		String serviceId=getBlockServiceId(blockId,workflowId);
		inputports.add(blockId);
		inputports.add(serviceId);
		JSONObject dataObject= getWorkflowAsJsonObject( workflowId);
		JSONObject connection=dataObject.getJSONObject("connections");
		JSONArray connectionArray=connection.getJSONArray("connectionArray");
		int connectionCount=connection.getInt("connectionCount");
		for(int i=0;i<connectionCount;i++){
		 String destinationPort= (String) connectionArray.getJSONObject(i).get("destinationPortName");
		 String destinationBlockGuid= (String) connectionArray.getJSONObject(i).get("destinationBlockGuid");
		 if(destinationBlockGuid.equals(blockId)){
				inputports.add(destinationPort);
		 }
		}
		return inputports;
	}

	public ArrayList<String> getOutputports(String workflowId, String blockId)
			throws Exception {
		ArrayList<String>outputports=new ArrayList<String>();
		String serviceId=getBlockServiceId(blockId,workflowId);
		outputports.add(blockId);
		outputports.add(serviceId);
		JSONObject dataObject= getWorkflowAsJsonObject( workflowId);
		JSONObject connection=dataObject.getJSONObject("connections");
		JSONArray connectionArray=connection.getJSONArray("connectionArray");
		int connectionCount=connection.getInt("connectionCount");
		for(int i=0;i<connectionCount;i++){
		 String sourcePort=(String) connectionArray.getJSONObject(i).get("sourcePortName");
		 String sourceBlockGuid=(String) connectionArray.getJSONObject(i).get("sourceBlockGuid");
		 if(sourceBlockGuid.equals(blockId)){
			 outputports.add(sourcePort);
		 }
		}
		return outputports;
	}


	public ArrayList<ArrayList<String>> getConnection(String workflowId)
			throws Exception {
		ArrayList<ArrayList<String>> connections=new ArrayList<ArrayList<String>>();
		 ArrayList<String> theconnection=new ArrayList<String>();
		JSONObject dataObject= getWorkflowAsJsonObject( workflowId);
		JSONObject connection=dataObject.getJSONObject("connections");
		JSONArray connectionArray=connection.getJSONArray("connectionArray");
		int connectionCount=connection.getInt("connectionCount");
		for(int i=0;i<connectionCount;i++){
			ArrayList<String> temp=new ArrayList<String>();
			String destinationBlockGuid= (String) connectionArray.getJSONObject(i).get("destinationBlockGuid");
			String sourceBlockGuid=(String) connectionArray.getJSONObject(i).get("sourceBlockGuid");
			String destinationPortName=(String) connectionArray.getJSONObject(i).get("destinationPortName");
			String sourcePortName=(String) connectionArray.getJSONObject(i).get("sourcePortName");
			String destinationName=getBlockName(destinationBlockGuid,workflowId);
			String sourceBlockName=getBlockName(sourceBlockGuid,workflowId);
			temp.add(sourceBlockGuid);
			temp.add(destinationBlockGuid);
			temp.add(sourceBlockName);
			temp.add(destinationName);
			temp.add(sourcePortName);
			temp.add(destinationPortName);
			theconnection=(ArrayList<String>) temp.clone();
			connections.add(theconnection);
		}
		
		//System.out.println(connections);
		return connections;
	}

	public String getBlockName(String BlockId, String workflowId)
			throws Exception {
		// TODO Auto-generated method stub
		String name = null;
		JSONObject dataObject= getWorkflowAsJsonObject( workflowId);
		JSONObject blocks = dataObject.getJSONObject("blocks");
		JSONArray blockArray = blocks.getJSONArray("blockArray");
        int blockCount = blocks.getInt("blockCount");
       
        for (int i = 0; i < blockCount; i++)
        {
        	
         String getBlockId=(String) blockArray.getJSONObject(i).get("guid");
         if(getBlockId.equals(BlockId)){
        	  name=(String) blockArray.getJSONObject(i).get("label");
         }	
         
        }
		if(name!=null){
			return name;
		}else{
			throw new Exception("The Block is not included in this workflow");
		}
	
	}
	
	public static void main(String [] args) throws Exception{
		cloudConnection test=new cloudConnection();
		connection con=test.creatCon("cloud0");
		WorkflowInfo test2= new workflowInfoIm(con);
		test2.getWorkflowAsJsonObject("877");
	//	test2.Blocklist("877");
	//	test2.getSource("877");
	}

}
