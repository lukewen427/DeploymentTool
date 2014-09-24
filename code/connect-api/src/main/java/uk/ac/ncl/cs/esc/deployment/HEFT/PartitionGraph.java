package uk.ac.ncl.cs.esc.deployment.HEFT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import uk.ac.ncl.cs.esc.cloudchangehandle.handleCloudChange.unpworkflowInfo;
import uk.ac.ncl.cs.esc.newpartitiontool.prepareDeployment.workflowInfo;
import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.read.BlockSet;





public class PartitionGraph {

	public HashMap<Integer,ArrayList<Object>> createpartitionGraph(ArrayList<Object> partitions){
		
	 HashMap<Integer,ArrayList<Object>>partitionGraph=new HashMap<Integer,ArrayList<Object>>();
		for(int a=0;a<partitions.size();a++){
			ArrayList<Object>partition=(ArrayList<Object>) partitions.get(a);
			partitionGraph.put(a, partition);
		}
	//	System.out.println(partitionGraph);
		return partitionGraph;
	}
	
	public ArrayList<Object> getLinks(ArrayList<Object>pLinks,HashMap<Integer,ArrayList<Object>>partitionGraph,BlockSet blockSet){
		ArrayList<Object>links=new ArrayList<Object>();
	//	System.out.println(pLinks);
		for(Object temp:pLinks){
			ArrayList<Integer> templink=new ArrayList<Integer>();
			ArrayList<String> thelink=(ArrayList<String>) temp;
			String startNode=thelink.get(0);
			String endNode=thelink.get(1);
		
			Block start= blockSet.getBlock(startNode);
			Block end=blockSet.getBlock(endNode);
			Iterator<Integer> keys=partitionGraph.keySet().iterator();
			while(keys.hasNext()){
				int name=keys.next();
				ArrayList<Object> partition=partitionGraph.get(name);
		//		System.out.println(templink);
				if(templink.size()<2){
					if(partition.contains(start)){
						templink.add(0, name);
						
					}
					if(partition.contains(end)){
						if(templink.isEmpty()){
							templink.add(name);
						}
					}
				}else{
					break;
				}
				
			}
			int position=isContained(templink,links);
			if(position==-1){
				ArrayList<Object> newLink=new ArrayList<Object>();
				newLink.add(templink.clone());
				newLink.add(thelink);
				links.add(newLink.clone());
			}else{
				ArrayList<Object>copy=((ArrayList<Object>) links.get(position));
				((ArrayList<Object>)copy.get(1)).add(thelink);
			}
			
		}
		return links;
	}
	
	
	public ArrayList<Integer>  getRootPartition(HashMap<Integer,ArrayList<Object>>partitionGraph,workflowInfo workflowinfo,BlockSet blockSet){
		ArrayList<Integer> rootPartition=new ArrayList<Integer>();
		ArrayList<String> root=workflowinfo.getRootNodes();
		for(String node:root){
			Block rootNode=blockSet.getBlock(node);
			Iterator<Integer> keys=partitionGraph.keySet().iterator();
			while(keys.hasNext()){
				int partitionName=keys.next();
				ArrayList<Object> partition=partitionGraph.get(partitionName);
				if(partition.contains(rootNode)){
					if(!rootPartition.contains(partitionName)){
					 rootPartition.add(partitionName);
					}
				}
			}
		}
		
		
		return rootPartition;
	}
	
	public ArrayList<Integer>  getRootPartition(HashMap<Integer,ArrayList<Object>>partitionGraph,unpworkflowInfo upw,BlockSet blockSet){
		ArrayList<Integer> rootPartition=new ArrayList<Integer>();
		ArrayList<String> root=upw.getRootNodes();
		for(String node:root){
			Block rootNode=blockSet.getBlock(node);
			Iterator<Integer> keys=partitionGraph.keySet().iterator();
			while(keys.hasNext()){
				int partitionName=keys.next();
				ArrayList<Object> partition=partitionGraph.get(partitionName);
				if(partition.contains(rootNode)){
					if(!rootPartition.contains(partitionName)){
					 rootPartition.add(partitionName);
					}
				}
			}
		}
		
		
		return rootPartition;
	}
	
	public ArrayList<Integer> getLeafPartition(HashMap<Integer,ArrayList<Object>>partitionGraph,workflowInfo workflowinfo,BlockSet blockSet){
		ArrayList<Integer> leafPartition=new ArrayList<Integer>();
		ArrayList<String> leaf=workflowinfo.getLeafNodes();
		
		for(String node:leaf){
			Block leafNode=blockSet.getBlock(node);
			Iterator<Integer> keys=partitionGraph.keySet().iterator();
			while(keys.hasNext()){
				int partitionName=keys.next();
				ArrayList<Object> partition=partitionGraph.get(partitionName);
				if(partition.contains(leafNode)){
					if(!leafPartition.contains(partitionName)){
					leafPartition.add(partitionName);
					}
				 }
				}
			}
		
		return leafPartition;
	}
	
	public ArrayList<Integer> getLeafPartition(HashMap<Integer,ArrayList<Object>>partitionGraph,unpworkflowInfo upw,BlockSet blockSet){
		ArrayList<Integer> leafPartition=new ArrayList<Integer>();
		ArrayList<String> leaf=upw.getLeafNodes();
		
		for(String node:leaf){
			Block leafNode=blockSet.getBlock(node);
			Iterator<Integer> keys=partitionGraph.keySet().iterator();
			while(keys.hasNext()){
				int partitionName=keys.next();
				ArrayList<Object> partition=partitionGraph.get(partitionName);
				if(partition.contains(leafNode)){
					if(!leafPartition.contains(partitionName)){
					leafPartition.add(partitionName);
					}
				 }
				}
			}
		
		return leafPartition;
	}
	
	int isContained(ArrayList<Integer> templink,ArrayList<Object>links){
		
		for(int a=0;a<links.size();a++){
			ArrayList<Object> link=(ArrayList<Object>) links.get(a);
			ArrayList<Integer> plink=(ArrayList<Integer>) link.get(0);
			if(templink.get(0)==plink.get(0)&&templink.get(1)==plink.get(1)){
				return a;
			}
		}
		return -1;
	}
}
