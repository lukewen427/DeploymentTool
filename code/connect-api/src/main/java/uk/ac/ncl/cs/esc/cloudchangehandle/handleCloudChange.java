package uk.ac.ncl.cs.esc.cloudchangehandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ncl.cs.esc.cloudMonitor.cloudMonitorIm;
import uk.ac.ncl.cs.esc.read.BlockSet;
import uk.ac.ncl.cs.esc.read.Cloud;

import com.google.common.collect.HashBiMap;

public class handleCloudChange {
	
	
	public static class unpworkflowInfo{
		
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
		    public unpworkflowInfo(ArrayList<ArrayList<String>> connections,
					HashMap<String,ArrayList<String>> blockInfo,cloudMonitorIm cm){
		    	
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
		
	}
}
