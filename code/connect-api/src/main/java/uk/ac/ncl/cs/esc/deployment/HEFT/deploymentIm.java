package uk.ac.ncl.cs.esc.deployment.HEFT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import uk.ac.ncl.cs.esc.newpartitiontool.prepareDeployment.workflowInfo;
import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.read.BlockSet;

public class deploymentIm implements deployment {
	
	workflowInfo workflowinfo;
	HashMap<Block,Integer> option;
	ArrayList<Object> partitions;
	HashMap<Integer,ArrayList<Object>>partitionGraph;
	ArrayList<Object> pLinks;
	ArrayList<Object> deploylinks;
	BlockSet blockSet;
	ArrayList<Integer> leaf;
	ArrayList<Integer> root;
	LinkedList<ArrayList<Integer>> deployOrder =new LinkedList<ArrayList<Integer>>();
	public void setOption(HashMap<Block,Integer> option){
		this.option=option;
	}
	
	public void setParitions(ArrayList<Object> partitions){
		this.partitions=partitions;
	}
	
	public void setWorkflowIn(workflowInfo workflowinfo){
		this.workflowinfo=workflowinfo;
		setBlockSet();
	}
	
	public void createpartitionGraph(){
		PartitionGraph getGraph=new PartitionGraph();
		this.partitionGraph=getGraph.createpartitionGraph(partitions);
		if(pLinks.isEmpty()){
			
		}else{
			setLinks(getGraph);
			setRoot(getGraph);
			setLeaf(getGraph);
		}
		
	}
	
	void setBlockSet(){
		this.blockSet=workflowinfo.getBlockSet();
	}
	public void setpartitionLinks(ArrayList<Object> partitionLinks){
		this.pLinks=partitionLinks;
	} 
	
	private void setLinks(PartitionGraph getGraph){
		this.deploylinks=getGraph.getLinks(pLinks, partitionGraph, blockSet);
	//	System.out.println(deploylinks);
		
	}
	void setRoot(PartitionGraph getGraph){
		this.root=getGraph.getRootPartition(partitionGraph, workflowinfo, blockSet);
	//	System.out.println(root);
	}
	void setLeaf(PartitionGraph getGraph){
		this.leaf=getGraph.getLeafPartition(partitionGraph, workflowinfo, blockSet);
	//	System.out.println(leaf);
	}
	
	public void setPartitionGraph(HashMap<Integer,ArrayList<Object>>partitionGraph){
		this.partitionGraph=(HashMap<Integer, ArrayList<Object>>) partitionGraph.clone();
	}
	public void setDeployLink(ArrayList<Object> deploylinks){
		this.deploylinks=(ArrayList<Object>) deploylinks.clone();
	}
	public void setOrder(LinkedList<ArrayList<Integer>> deployOrder){
		this.deployOrder=(LinkedList<ArrayList<Integer>>) deployOrder.clone();
	}
	
	public void createDeployGraph(){
		LinkedList<ArrayList<Integer>> order=new LinkedList<ArrayList<Integer>>();
		deployGraph getOrder=new deployGraph();
	//	System.out.println(leaf);
		if(deploylinks.isEmpty()){
			ArrayList<Integer> temp=new ArrayList<Integer>();
			Iterator<Integer> keys=partitionGraph.keySet().iterator();
			while(keys.hasNext()){
				int p=keys.next();
				temp.add(p);
			}
			order.add((ArrayList<Integer>)temp.clone());
		}else{
			getOrder.createOrder(leaf, deploylinks, order, new ArrayList<Integer>((ArrayList<Integer>)leaf.clone()));
			inverse(order);
		}
	}
	
	private void inverse(LinkedList<ArrayList<Integer>> order){
		for(int a=order.size()-1;a>=0;a--){
			deployOrder.add((ArrayList<Integer>) order.get(a).clone());
		}
		
	}

	public HashMap<Integer, ArrayList<Object>> getPartitionGraph() {
		// TODO Auto-generated method stub
		return partitionGraph;
	}

	public ArrayList<Object> getDeployLink() {
		// TODO Auto-generated method stub
		return deploylinks;
	}
    public LinkedList<ArrayList<Integer>> getOrder(){
		
		return deployOrder;
	}
}
