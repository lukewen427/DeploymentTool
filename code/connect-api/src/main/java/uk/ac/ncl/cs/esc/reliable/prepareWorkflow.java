package uk.ac.ncl.cs.esc.reliable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.google.common.collect.HashBiMap;

import uk.ac.ncl.cs.esc.cloudMonitor.cloudMonitorIm;
import uk.ac.ncl.cs.esc.connection.cloudConnection;
import uk.ac.ncl.cs.esc.connection.connection;
import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.read.BlockSet;
import uk.ac.ncl.cs.esc.read.Cloud;
import uk.ac.ncl.cs.esc.read.readReWorkflow;
import uk.ac.ncl.cs.esc.repartition.operating;
import uk.ac.ncl.cs.esc.workflow.WorkflowInfo;
import uk.ac.ncl.cs.esc.workflow.workflowInfoIm;

public class prepareWorkflow {
	
	public prepareWorkflow(String workflowId, ArrayList<ArrayList<String>> connections,
			HashMap<String,ArrayList<String>> blockInfo,cloudMonitorIm cm){
		
		ReliWorkflow workflowinfo=new ReliWorkflow( workflowId, connections, blockInfo, cm);
		try {
		//	System.out.println(workflowinfo.getAvaClouds());
			new operating(workflowinfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static class ReliWorkflow extends workflowRepr{
		
		ArrayList<String> root=new ArrayList<String>();
		ArrayList<String> leaf=new ArrayList<String>();
		String workflowId;
		ArrayList<ArrayList<String>> connections;
		HashMap<String,ArrayList<String>> blockInfo;
		cloudMonitorIm cm;
		int [][] deployment;
		Set<Cloud> cloudSet;
		HashBiMap< String,Integer> biMap;
		double workflow[][];
		BlockSet blockSet;
		LinkedList<String> avaClouds;
		HashMap<Integer,String> cloudMap;
		
		public ReliWorkflow(String workflowId, ArrayList<ArrayList<String>> connections,
				HashMap<String,ArrayList<String>> blockInfo,cloudMonitorIm cm){
			this.cm=cm;
	    	setWorkflowId(workflowId);
	    	setConnections(connections);
	    	setBlockInfo(blockInfo);
	      	setAvaClouds(cm.getAvaClouds());
	    	readReWorkflow read=new readReWorkflow(connections,blockInfo,cm);
	    	setDeployment(read.getDeployment());
	    	setMaps(read.getMap());
	    	setCloudMap(read.getCloudMap());
	    	setWorkflow(read.getWorkflow());
	    	setRootNodes();
	    	setLeafNodes();
	    	try {
				setBlockSet();
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
	    void setAvaClouds(LinkedList<String> avaClouds){
	    
			this.avaClouds=(LinkedList<String>) avaClouds.clone();
		}
	    void setMaps(HashBiMap< String,Integer> biMap){
			  this.biMap=biMap;
		  }
	    void setWorkflow(double workflow[][]){
			  this.workflow=workflow;
		  }
	    
	    void setCloudMap(HashMap<Integer,String> cloudMap){
	    	this.cloudMap=cloudMap;
	    }
	    
	    void setRootNodes(){
			  for(int a=0;a<workflow.length;a++){
				  boolean isRoot=true;
				  for(int i=0;i<workflow.length;i++){
					  if(workflow[i][a]>=0){
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
					  if(workflow[a][i]>=0){
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
					int location=Integer.valueOf((String) element.get(0));
					int clearance=Integer.valueOf((String) element.get(1));
					String type=(String) element.get(2);
			//		double cpu=Double.valueOf((String) element.get(3));
					theblock=new Block(blockid,location,clearance,type,serviceId,blockName);
					theBlockSet.add(theblock);	
				}
				
			   this.blockSet=new BlockSet(theBlockSet);
				
		  }

		@Override
		public ArrayList<ArrayList<String>> getConnections() {
			// TODO Auto-generated method stub
			return connections;
		}

		@Override
		public HashBiMap<String, Integer> getMap() {
			// TODO Auto-generated method stub
			return biMap;
		}

		@Override
		public int[][] getDeployment() {
			// TODO Auto-generated method stub
			return deployment;
		}

		@Override
		public LinkedList<String> getAvaClouds() {
			// TODO Auto-generated method stub
			return avaClouds;
		}

		@Override
		public cloudMonitorIm getCloudinfo() {
			// TODO Auto-generated method stub
			return cm;
		}

		@Override
		public HashMap<String, ArrayList<String>> getBlockInfo() {
			// TODO Auto-generated method stub
			return blockInfo;
		}

		@Override
		public ArrayList<String> getRootNodes() {
			// TODO Auto-generated method stub
			return root;
		}

		@Override
		public ArrayList<String> getLeafNodes() {
			// TODO Auto-generated method stub
			return leaf;
		}
		public HashMap<Integer,String> getCloudMap(){
			return cloudMap;
		}
		@Override
		public BlockSet getBlockSet() {
			// TODO Auto-generated method stub
			return blockSet;
		}
		
	}
}
