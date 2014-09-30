package uk.ac.ncl.cs.esc.cloudchangehandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ncl.cs.esc.cloudMonitor.cloudMonitorIm;
import uk.ac.ncl.cs.esc.cloudchangehandle.costNewClouds.deployInfo;
import uk.ac.ncl.cs.esc.newpartitiontool.prepareDeployment.workflowInfo;
import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.read.BlockSet;
import uk.ac.ncl.cs.esc.read.Cloud;

import com.google.common.collect.HashBiMap;

public class handleCloudChange {
	final unpworkflowInfo upw;
	public handleCloudChange(deployInfo deinfo,workflowInfo workflowinfo){
		
		this.upw=new unpworkflowInfo(deinfo,workflowinfo);
		new newoperating(upw);
		
	}
	
	public static class unpworkflowInfo{
		
		 ArrayList<String> root=new ArrayList<String>();
		  ArrayList<String> leaf=new ArrayList<String>();
	   // 	String workflowId;
			ArrayList<ArrayList<String>> connections;
			HashMap<String,ArrayList<String>> blockInfo;
			int [][] deployment;
		    Set<Cloud> cloudSet;
		    HashBiMap< String,Integer> biMap;
		    double unpworkflow[][];
		    BlockSet blockSet;
		    LinkedList<String> avaClouds;
		    cloudMonitorIm cm;
		    deployInfo deinfo;
		    BlockSet theBlockSet;
		 // first place is the source block cloud, second place is the link
		    ArrayList<Object> inputLinks;
		    workflowInfo workflowinfo;
		   
		    public unpworkflowInfo(deployInfo deinfo,workflowInfo workflowinfo){
		    	
		    	this.workflowinfo=workflowinfo;
		    	this.cm=workflowinfo.getCloudinfo();
		    	this.deinfo=deinfo;
		    	this.theBlockSet=workflowinfo.getBlockSet();
		    	this.inputLinks=deinfo.getInputLinks();
		    	this.unpworkflow=deinfo.getWorkflow();
		    	setAvaClouds(cm.getAvaClouds());
		//    	setWorkflowId(workflowId);
		    	setConnections(deinfo.getConnection());
		        setMaps(deinfo.getMap());
		        setDeployment(deinfo.getDeployment());
		        setBlockInfo(deinfo.getBlockInfo());
		        setBlockSet();
		        setLeaf();
		        setRoot();
		        
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
			  public ArrayList<Object> getInput(){
				   	return inputLinks;
			  }
			  
			  public workflowInfo reCreateWorkflowinfo(){
				  
				  workflowInfo newInfo=new workflowInfo(deinfo,workflowinfo);
				  return newInfo;
			  }
			  
			  void setAvaClouds(LinkedList<String> avaClouds){
					this.avaClouds=avaClouds;
				}
		/*		void setWorkflowId(String workflowId){
					  this.workflowId=workflowId;
				  }*/
				  
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
				//	  System.out.println(biMap);
					  this.biMap=biMap;
				  }
				  void setBlockSet(){
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
				  void setRoot(){
					  for(Object temp:inputLinks){
						  ArrayList<String> blink=(ArrayList<String>) ((ArrayList<Object>)temp).get(1);
						  String endBlock=blink.get(1);
						  if(!root.contains(endBlock)){
							  root.add(endBlock);
						  }
					  }
				  }
				  
				  void setLeaf(){
					  for(int a=0;a<unpworkflow.length;a++){
						  boolean isleaf=true;
						  for(int i=0;i<unpworkflow.length;i++){
							  if(unpworkflow[a][i]>0){
								  isleaf=false;
							  }
						  }
						  
						  if(isleaf){
							  
						//	  System.out.println(biMap);
							  String name=biMap.inverse().get(a);
							  leaf.add(name);
						  }
					  }
				  }	  
	     }
}
