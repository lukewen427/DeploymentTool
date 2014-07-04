package uk.ac.ncl.cs.esc.newpartitiontool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.HashBiMap;

import uk.ac.ncl.cs.esc.connection.cloudConnection;
import uk.ac.ncl.cs.esc.connection.connection;
import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.read.BlockSet;
import uk.ac.ncl.cs.esc.read.Cloud;
import uk.ac.ncl.cs.esc.read.CloudSet;
import uk.ac.ncl.cs.esc.read.readWorkflow;
import uk.ac.ncl.cs.esc.workflow.WorkflowInfo;
import uk.ac.ncl.cs.esc.workflow.workflowInfoIm;

public class prepareDeployment {

	final workflowInfo workflowinfo;
  public prepareDeployment(String workflowId, ArrayList<ArrayList<String>> connections,
												HashMap<String,ArrayList<String>> blockInfo){
	this.workflowinfo= new workflowInfo(workflowId,connections,blockInfo);
	 try {
		new operating(workflowinfo);
	} catch (Exception e) {

		e.printStackTrace();
	}
  }
 
  public static class workflowInfo{
	  ArrayList<String> root=new ArrayList<String>();
	  ArrayList<String> leaf=new ArrayList<String>();
	  String workflowId;
		ArrayList<ArrayList<String>> connections;
		HashMap<String,ArrayList<String>> blockInfo;
		int [][] deployment;
	    Set<Cloud> cloudSet;
	    HashBiMap< String,Integer> biMap;
	    double workflow[][];
	    BlockSet blockSet;
	    CloudSet clouds;
	    
	    public workflowInfo(String workflowId, ArrayList<ArrayList<String>> connections,
				HashMap<String,ArrayList<String>> blockInfo){
	    	
	    	setWorkflowId(workflowId);
	    	setConnections(connections);
	    	setBlockInfo(blockInfo);
	    	readWorkflow read=new readWorkflow(workflowId, connections,blockInfo);
	    	setDeployment(read.getDeployment());
	    	setCloudset(read.getClouds());
	    	setMaps(read.getMap());
	    	setWorkflow(read.getWorkflow());
	    	setRootNodes();
	    	setLeafNodes();
	    	try {
	    		setBlockSet();
			} catch (Exception e) {
			
				e.printStackTrace();
			}
	    	
	    }
	  void setWorkflowId(String workflowId){
		  this.workflowId=workflowId;
	  }
	  
	  void setConnections(ArrayList<ArrayList<String>> connections){
		  this.connections=connections;
	  }
	  
	  void setBlockInfo(HashMap<String,ArrayList<String>> blockInfo){
		  this.blockInfo=blockInfo;
	  }
	  
	  void setDeployment(int [][] deployment){
		  this.deployment=deployment;
	  }
	  
	  void setCloudset( Set<Cloud> cloudSet){
		  
		  this.cloudSet=cloudSet;
		 this.clouds=new CloudSet(cloudSet);
	  } 
	  
	  void setMaps(HashBiMap< String,Integer> biMap){
		  this.biMap=biMap;
	  }
	  
	  void setWorkflow(double workflow[][]){
		  this.workflow=workflow;
	  }
	  
	  public ArrayList<ArrayList<String>> getConnections(){
		  return connections;
	  }
	  
	  public HashBiMap< String,Integer> getMap(){
		  return biMap;
	  }
	  
	  public int [][] getDeployment(){
			return deployment;
		}
	  public CloudSet getClouds(){
		  return clouds;
	  }
	  
	  void setRootNodes(){
		  for(int a=0;a<workflow.length;a++){
			  boolean isRoot=true;
			  for(int i=0;i<workflow.length;i++){
				  if(workflow[i][a]>0){
					  isRoot=false;
				  }
			  }
			  if(isRoot){
				  String name=biMap.inverse().get(a);
				  root.add(name);
			  }
		  }
		  
	  }
	  
	  void setLeafNodes(){
		  for(int a=0;a<workflow.length;a++){
			  boolean isleaf=true;
			  for(int i=0;i<workflow.length;i++){
				  if(workflow[a][i]>0){
					  isleaf=false;
				  }
			  }
			  
			  if(isleaf){
				  
				  String name=biMap.inverse().get(a);
				  leaf.add(name);
			  }
		  }
	  }
	  public ArrayList<String> getRootNodes(){
		  
		  return root;
	  }
	  
	  public ArrayList<String> getLeafNodes(){
		
		  return leaf;
	  }
	  
	  void setBlockSet() throws Exception{
		     Block theblock;
			cloudConnection test=new cloudConnection();
			connection con=test.creatCon("cloud0");
			WorkflowInfo parser= new workflowInfoIm(con);
			Set<String> BlockIds=blockInfo.keySet();
			Iterator <String> ids=BlockIds.iterator();
			Set<Block>theBlockSet=new HashSet<Block>();
			while(ids.hasNext()){
				String blockid=ids.next();
				String blockName=parser.getBlockName(blockid, workflowId);
				String serviceId=parser.getBlockServiceId(blockid, workflowId);
				ArrayList element=blockInfo.get(blockid);	
		//		System.out.println(element);
				int location=Integer.valueOf((String) element.get(0));
		//		int location=(Integer)element.get(0);
				int clearance=Integer.valueOf((String) element.get(1));
		//		int clearance=(Integer) element.get(1);
				String type=(String) element.get(2);
				int cpu=Integer.valueOf((String) element.get(3));
				theblock=new Block(blockid,location,clearance,type,serviceId,cpu,blockName);
				theBlockSet.add(theblock);	
			}
			
		   this.blockSet=new BlockSet(theBlockSet);
			
	  }
	    public BlockSet getBlockSet() {
			
			return blockSet;
		}
  }
  
}
