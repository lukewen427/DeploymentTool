package uk.ac.ncl.cs.esc.cloudchangehandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import uk.ac.ncl.cs.esc.deployment.HEFT.deploymentIm;
import uk.ac.ncl.cs.esc.newpartitiontool.prepareDeployment.workflowInfo;
import uk.ac.ncl.cs.esc.read.Block;

public class unpWorkflow {
	
	HashMap<Integer,ArrayList<Object>>partitionGraph;
	Set<Integer> unpPartition;
	ArrayList<ArrayList<String>> connections;
	HashMap<String,ArrayList<String>> blockInfo;
	deploymentIm deploy;
	workflowInfo workflowinfo;
	HashMap<String,ArrayList<String>> unprocessblocks= new HashMap<String,ArrayList<String>>();
	ArrayList<ArrayList<String>> unpconnections=new ArrayList<ArrayList<String>>();
	public unpWorkflow(Set<Integer> unpPartition,deploymentIm deploy,workflowInfo workflowinfo){
		this.unpPartition=unpPartition;
		this.deploy=deploy;
		this.workflowinfo=workflowinfo;
		setBlockInfo();
		setpartitionGraph();
		setConnection();
		unprocessBlocks();
		unprocessConnects();
	}
	
	void setConnection(){
		this.connections=workflowinfo.getConnections();
	}
	void setBlockInfo(){
		this.blockInfo=workflowinfo.getBlockInfo();
	}
	void setpartitionGraph(){
		partitionGraph=deploy.getPartitionGraph();
	}
	
	public HashMap<String,ArrayList<String>> getunprocessBlocks(){
		return unprocessblocks;
	}
	
	public ArrayList<ArrayList<String>> getunprocessConnections(){
		return unpconnections;
	}
	
	private void unprocessBlocks(){
		Iterator<Integer> unpp=unpPartition.iterator();
		while(unpp.hasNext()){
			ArrayList<Object> partition=partitionGraph.get(unpp.next());
			addBlocks(partition);
		}
	}
	
	private void unprocessConnects(){
		
		Set<String> block=unprocessblocks.keySet();
		Iterator<String> blocks=block.iterator();
		while(blocks.hasNext()){
			String blockid=blocks.next();
			for(ArrayList<String>link:connections){
				String s=link.get(0);
				String d=link.get(1);
				if(blockid.equals(s)||blockid.equals(d)){
					if(!isContain(link)){
						unpconnections.add(new ArrayList<String>((ArrayList<String>) link.clone())); 
					}
				}
			}
		}
		
	}
	
	boolean isContain(ArrayList<String> link){
		for(ArrayList<String> temp:unpconnections){
			if((temp.get(0).equals(link.get(0)))|| (temp.get(1).equals(link.get(1)))){
				return true;
			}
		}
		return false;
	}
	void addBlocks(ArrayList<Object> partition){
		for(int a=1;a<partition.size();a++){
			Block block=(Block) partition.get(a);
			String id=block.getBlockId();
			ArrayList<String> blockinfo=blockInfo.get(id);
			unprocessblocks.put(id, new ArrayList<String> (blockinfo));
		}
	}
}
