package uk.ac.ncl.cs.esc.cloudchangehandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
	ArrayList<Object> partitionLinks;
	LinkedList<String>oldCloud;
	// first place is the source block cloud, second place is the link
	ArrayList<Object>inputLinks=new ArrayList<Object>();
	public unpWorkflow(Set<Integer> unpPartition,deploymentIm deploy,workflowInfo workflowinfo,LinkedList<String>oldCloud){
		this.unpPartition=unpPartition;
		this.deploy=deploy;
		this.workflowinfo=workflowinfo;
		this.oldCloud=oldCloud;
		setBlockInfo();
		setpartitionGraph();
		setConnection();
		setpartitionLinks();
		unprocessBlocks();
		unprocessConnects();
		inputLinks();
	}
	
	 void setpartitionLinks() {
		// TODO Auto-generated method stub
		this.partitionLinks=deploy.getDeployLink();
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
	public ArrayList<Object> getInputlinks(){
		return inputLinks;
	}
	
	private void unprocessBlocks(){
		Iterator<Integer> unpp=unpPartition.iterator();
		while(unpp.hasNext()){
			ArrayList<Object> partition=partitionGraph.get(unpp.next());
			addBlocks(partition);
		}
	}
	
	private void inputLinks(){
		for(int a=0;a<partitionLinks.size();a++){
			ArrayList<Object> link=(ArrayList<Object>) partitionLinks.get(a);
			ArrayList<Integer> plink=(ArrayList<Integer>) link.get(0);
			ArrayList<String> blink=(ArrayList<String>) link.get(1);
			int sp=plink.get(0);
			int dp=plink.get(1);
			if(!unpPartition.contains(sp) && unpPartition.contains(dp)){
				ArrayList<Object> temp=new ArrayList<Object>();
				ArrayList<Object> partition=partitionGraph.get(sp);
				String cloudName=oldCloud.get((int) partition.get(0));
				temp.add(cloudName);
				temp.add(blink);
				inputLinks.add(temp.clone());
			}
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
			if((temp.get(0).equals(link.get(0)))&& (temp.get(1).equals(link.get(1)))){
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
