package uk.ac.ncl.cs.esc.newpartitiontool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.google.common.collect.HashBiMap;

import uk.ac.ncl.cs.esc.cloudMonitor.cloudMonitorIm;
import uk.ac.ncl.cs.esc.cloudchangehandle.costNewClouds.deployInfo;
import uk.ac.ncl.cs.esc.connection.cloudConnection;
import uk.ac.ncl.cs.esc.connection.connection;
import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.read.BlockSet;
import uk.ac.ncl.cs.esc.read.Cloud;
import uk.ac.ncl.cs.esc.read.readWorkflow;
import uk.ac.ncl.cs.esc.workflow.WorkflowInfo;
import uk.ac.ncl.cs.esc.workflow.workflowInfoIm;

public class prepareDeployment {

	final workflowInfo workflowinfo;
  public prepareDeployment(String workflowId, ArrayList<ArrayList<String>> connections,
												HashMap<String,ArrayList<String>> blockInfo,cloudMonitorIm cm){
	this.workflowinfo= new workflowInfo(workflowId,connections,blockInfo,cm);
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
	    LinkedList<String> avaClouds;
	    cloudMonitorIm cm;
	    
	    // the variables are for creating workflowinfo from unpworkflowInfo 
	    workflowInfo workflowinfo;
	    deployInfo deinfo;
	    public workflowInfo(String workflowId, ArrayList<ArrayList<String>> connections,
				HashMap<String,ArrayList<String>> blockInfo,cloudMonitorIm cm){
	    	this.cm=cm;
	    	setWorkflowId(workflowId);
	    	setConnections(connections);
	    	setBlockInfo(blockInfo);
	    	readWorkflow read=new readWorkflow(connections,blockInfo,cm);
	    	setDeployment(read.getDeployment());
	     	setAvaClouds(cm.getAvaClouds());
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
	    
	    public workflowInfo(deployInfo deinfo,workflowInfo workflowinfo){
	    	this.workflowinfo=workflowinfo;
	    	this.cm=workflowinfo.getCloudinfo();
	    	this.deinfo=deinfo;
	    	BlockSet theBlockSet=workflowinfo.getBlockSet();
	    //	this.inputLinks=deinfo.getInputLinks();
	    	setWorkflow(deinfo.getWorkflow());
	    	setAvaClouds(cm.getAvaClouds());
	    	setConnections(deinfo.getConnection());
	        setMaps(deinfo.getMap());
	        setDeployment(deinfo.getDeployment());
	        setBlockInfo(deinfo.getBlockInfo());
	        setBlockSet(theBlockSet);
	        setRootNodes();
	        setLeafNodes();
	    }
	    
	   
	void setAvaClouds(LinkedList<String> avaClouds){
		this.avaClouds=avaClouds;
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
	  
	  public LinkedList<String> getAvaClouds(){
		  return avaClouds;
	  }
	  
	  public cloudMonitorIm getCloudinfo(){
		  return cm;
	  }
	
	  public HashMap<String,ArrayList<String>> getBlockInfo(){
		  return blockInfo;
	  }
	  
	  public ArrayList<String> getRootNodes(){
		  
		  return root;
	  }
	  
	  public ArrayList<String> getLeafNodes(){
		
		  return leaf;
	  }
	  public BlockSet getBlockSet() {
			
			return blockSet;
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

	  
	  void setBlockSet() throws Exception{
		     Block theblock;
			cloudConnection test=new cloudConnection();
			connection con=test.creatCon("cloud1");
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
				theblock=new Block(blockid,location,clearance,type,serviceId,blockName);
				theBlockSet.add(theblock);	
			}
			
		   this.blockSet=new BlockSet(theBlockSet);
			
	  }
	  
	  void setBlockSet(BlockSet theBlockSet){
		   Set<Block> blocks=new HashSet<Block>();
		   Set<String> BlockIds=blockInfo.keySet();
		   Iterator <String> ids=BlockIds.iterator();
		   while(ids.hasNext()){
			   String blockid=ids.next();
			   Block b=theBlockSet.getBlock(blockid);
			   if(!blocks.contains(b)){
				   blocks.add(b);
			   }
		   }
		   
		  this.blockSet= new BlockSet(blocks);
	  }
	    
  }
  
}
