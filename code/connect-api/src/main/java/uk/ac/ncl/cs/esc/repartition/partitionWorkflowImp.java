package uk.ac.ncl.cs.esc.repartition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.common.collect.HashBiMap;

import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.read.BlockSet;
import uk.ac.ncl.cs.esc.reliable.prepareWorkflow.ReliWorkflow;

public class partitionWorkflowImp implements partitionWorkflow  {
	
 private final HashBiMap<String,Integer> biMap;
//	private final workflowInfo workflowinfo;
//	private final ArrayList<String> root;
	private final BlockSet blockSet;
	private final int[][] deployment;
	private final ArrayList<ArrayList<String>> connections;
//	private ArrayList<Object> links =new ArrayList<Object>();
	private final ArrayList<String> leaf;
	LinkedList<ArrayList<String>> partitionOrder=new LinkedList<ArrayList<String>>();
	HashMap<Integer,ArrayList<ArrayList<String>>> links=new HashMap<Integer,ArrayList<ArrayList<String>>>();
	
	public partitionWorkflowImp(ReliWorkflow workflowinfo){
		this.biMap=workflowinfo.getMap();
	//	this.workflowinfo=workflowinfo;
	//	this.root=workflowinfo.getRootNodes();
		this.leaf=workflowinfo.getLeafNodes();
		this.blockSet=workflowinfo.getBlockSet();
		this.deployment=workflowinfo.getDeployment();
		this.connections=workflowinfo.getConnections();
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
	
	// create the execution order from end to the beginning 
	public void workflowSplit(HashMap<Block,Integer> option) {
		LinkedList<ArrayList<String>> partitions=new LinkedList<ArrayList<String>>();
		
		ArrayList<String> leafNodes=new ArrayList<String>();
	//	System.out.println(biMap);
	//	System.out.println(root);
		ArrayList<String> visited=new ArrayList<String>();
		for(String node:leaf){
		//	String nodeId=biMap.inverse().get(node);
			leafNodes.add(node);
			visited.add(node);
		}
		 partitions.add((ArrayList<String>)leafNodes.clone());
		creatOrder order= new creatOrder();
		order.theOrder(leafNodes,new ArrayList<String>(), connections, partitions, visited);
		inverse(partitions);
	}
	

	 void creatLinks(ArrayList<String> heads, int count){
		if(count==partitionOrder.size()){
			return;
		}else{
			ArrayList<ArrayList<String>> linkSet= new ArrayList<ArrayList<String>>();
			ArrayList<String> offSet=partitionOrder.get(count);
			for(String node:heads){
				for(int a=0;a<connections.size();a++){
					ArrayList<String> temp=connections.get(a);
					String start=temp.get(0);
					String end=temp.get(1);
					if(node.equals(start) && offSet.contains(end)){
						if(!isContain(linkSet,temp)){
							linkSet.add(temp);
						}
					}	
				}
			}
			links.put(count, linkSet);
			count+=1;
			creatLinks(offSet,count);
		}
	
	}
	

	boolean isContain(ArrayList<ArrayList<String>> linkSet, ArrayList<String> link){
		String begin=link.get(0);
		String end=link.get(1);
		for(ArrayList<String>temp:linkSet){
			String s=temp.get(0);
			String e=temp.get(1);
			if(begin.equals(s)&& end.equals(e)){
				return true;
			}
		}
		return false;
	}

	private void inverse(LinkedList<ArrayList<String>> order){
		for(int a=order.size()-1;a>=0;a--){
			partitionOrder.add((ArrayList<String>) order.get(a).clone());
		}
	//	System.out.println(partitionOrder.get(0));
		creatLinks(partitionOrder.get(0),1);
	}


	@Override
	public HashMap<Integer,ArrayList<ArrayList<String>>> getLinks() {
		// TODO Auto-generated method stub
		return links;
	}


	@Override
	public LinkedList<ArrayList<String>> getOrder() {
		// TODO Auto-generated method stub
		return partitionOrder;
	}

}
