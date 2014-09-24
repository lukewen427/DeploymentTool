package uk.ac.ncl.cs.esc.newpartitiontool;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.HashBiMap;

import uk.ac.ncl.cs.esc.cloudchangehandle.handleCloudChange.unpworkflowInfo;
import uk.ac.ncl.cs.esc.newpartitiontool.prepareDeployment.workflowInfo;
import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.read.BlockSet;

public class partitionWorkflowImp implements partitionWorkflow  {
	
 private final HashBiMap<String,Integer> biMap;
//	private final workflowInfo workflowinfo;
	private final ArrayList<String> root;
	private final BlockSet blockSet;
	private final int[][] deployment;
	private final ArrayList<ArrayList<String>> connections;
	private ArrayList<Object> links =new ArrayList<Object>();
	
	public partitionWorkflowImp(workflowInfo workflowinfo){
		this.biMap=workflowinfo.getMap();
	//	this.workflowinfo=workflowinfo;
		this.root=workflowinfo.getRootNodes();
		this.blockSet=workflowinfo.getBlockSet();
		this.deployment=workflowinfo.getDeployment();
		this.connections=workflowinfo.getConnections();
	}
	
	public partitionWorkflowImp(unpworkflowInfo upw){
		this.biMap=upw.getMap();
		this.root=upw.getRootNodes();
		this.blockSet=upw.getBlockSet();
		this.deployment=upw.getDeployment();
		this.connections=upw.getConnections();
		
	}
	public HashMap<Block,Integer> mappingCloud(){
		HashMap<Block,Integer>option=new HashMap<Block,Integer>();
	
			for(int a=0; a<deployment.length;a++){
				for(int i=0;i<deployment[a].length;i++){
					if(deployment[a][i]==1){
						String blockId=biMap.inverse().get(a);
						Block block=blockSet.getBlock(blockId);
						option.put(block, i);
					}
				}
			}
		
		return option;
	}
	public ArrayList<Object> workflowSplit(HashMap<Block,Integer> option) {
		ArrayList<Object> partitions=new ArrayList<Object>();
		
		ArrayList<String> startNodes=new ArrayList<String>();
		for(String node:root){
		//	String nodeId=biMap.inverse().get(node);
			startNodes.add(node);
		}
		partition(option,connections, blockSet,startNodes, new ArrayList<Object>(), partitions);
		
		return partitions;
	}
	
	public ArrayList<Object> getLinks(){
		return links;
	}
	
	private void partition(HashMap<Block,Integer> option,ArrayList<ArrayList<String>> connections,BlockSet blockSet,
			
			ArrayList<String> waitingNodes,ArrayList<Object> visited,ArrayList<Object>partitions){
	//	System.out.println(option);
		if(waitingNodes.isEmpty()){
			return;
		}
		
		ArrayList<String> offspringNodes=new ArrayList<String>();
		ArrayList<Object> collection=new ArrayList<Object>();
		for(int a=0;a<waitingNodes.size();a++){
			String Node=waitingNodes.get(a);
			
			Block block=blockSet.getBlock(Node);
			
			int cloudblock=option.get(block);
			if(visited.contains(block)){
				
			}else{
				visited.add(block);
				ArrayList<Object> partition=new ArrayList<Object>();
				if(partitions.isEmpty()){
					partition.add(cloudblock);
					partition.add(block);
				}else{
				//	ArrayList<Object> newPartition=new ArrayList<Object>();
					for(Object party:partitions){
						ArrayList<Object> theParty=(ArrayList<Object>)party;
						if(theParty.contains(block)){
							partition=(ArrayList<Object>) theParty.clone();
							partitions.remove(party);
							break;
						}
					}
					if(partition.isEmpty()){
						partition.add(cloudblock);
						partition.add(block);
					}
					
				}
				
				for(ArrayList<String> connection:connections){
					String sourceNode=connection.get(0);
					String destinationNode=connection.get(1);
					if(Node.equals(sourceNode)){
						Block offspringblock=blockSet.getBlock(destinationNode);
						int offspringCloud=option.get(offspringblock);
						if(cloudblock==offspringCloud){
							partition.add(offspringblock);
						}else{
							if(!isContained(sourceNode,destinationNode)){
								links.add(connection);
							}
							
							ArrayList<Object> newPartition=new ArrayList<Object>();
							newPartition.add(offspringCloud);
							newPartition.add(offspringblock);	
							collection.add(new ArrayList<Object>((ArrayList<Object>)newPartition.clone()));
						}
					}
					
					if(!offspringNodes.contains(destinationNode)){
						offspringNodes.add(destinationNode);
					}
				}
				collection.add(new ArrayList<Object>((ArrayList<Object>)partition.clone()));
			}
		}
			combination(partitions,collection);
			
		 partition(option,connections, blockSet,offspringNodes,visited, partitions);
	}
	
	boolean isContained(String start,String end){
		
		if(links.isEmpty()){
			return false;
		}else{
			for(Object temp:links){
				
				ArrayList<String> thelink=(ArrayList<String>) temp;
				String startNode=thelink.get(0);
				String endNode=thelink.get(1);
				if(startNode.equals(start)&&endNode.equals(end)){
					return true;
				}
				
			}
		}
		
		return false;
	}
	
	private void combination(ArrayList<Object>partitions,ArrayList<Object> collection){
		//	System.out.println(collection.size());
			ArrayList<Object> temp=new ArrayList<Object>();
			for(int a=0;a<collection.size();a++){
				ArrayList<Object>party=(ArrayList<Object>) collection.get(a);
				if(temp.isEmpty()){
					temp.add(party.clone());
				}else{
					boolean iscontain=false;
					for(int i=0;i<temp.size();i++){
						ArrayList<Object>partition=(ArrayList<Object>) temp.get(i);
						if(checkContain(party,partition)){
							getConbine(party,partition);
							iscontain=true;
						}
					}
					if(iscontain==false){
						temp.add(party.clone());
					}
				}
			}
			
			// adding to option
			for(int h=0;h<temp.size();h++){
				ArrayList<Object> partition=(ArrayList<Object>) temp.get(h);
				if(!partition.isEmpty()){
					partitions.add(partition.clone());
				}
				
			}
			
		}
	
	private void getConbine(ArrayList<Object>party,ArrayList<Object>party2){
		
		for(int a=0;a<party.size();a++){
			Object block=party.get(a);
			if(!party2.contains(block)){
				party2.add(block);
			}
		}
}
   private boolean checkContain(ArrayList<Object>party,ArrayList<Object>party2){
	
		for(int a=0;a<party2.size();a++){
			Object block=party2.get(a);
			if(party.contains(block)){
				return true;
			}
		}
			return false;
 }

}
