package uk.ac.ncl.cs.esc.deployment.HEFT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import uk.ac.ncl.cs.esc.cloudchangehandle.handleCloudChange.unpworkflowInfo;
import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.read.BlockSet;

public class newDeploymentIm implements deployment{

	unpworkflowInfo upw;
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
	public void setWorkflowIn(unpworkflowInfo upw){
		this.upw=upw;
		setBlockSet();
	}
	void setBlockSet(){
		this.blockSet=upw.getBlockSet();
	}
	public void setpartitionLinks(ArrayList<Object> partitionLinks){
		this.pLinks=partitionLinks;
	} 
	@Override
	public void createpartitionGraph(){
		PartitionGraph getGraph=new PartitionGraph();
		this.partitionGraph=getGraph.createpartitionGraph(partitions);
		setLinks(getGraph);
		setRoot(getGraph);
		setLeaf(getGraph);
		
	}
	
	private void setLinks(PartitionGraph getGraph){
		this.deploylinks=getGraph.getLinks(pLinks, partitionGraph, blockSet);
		System.out.println(deploylinks);
		
	}
	void setRoot(PartitionGraph getGraph){
		this.root=getGraph.getRootPartition(partitionGraph, upw, blockSet);
	//	System.out.println(root);
	}
	void setLeaf(PartitionGraph getGraph){
		this.leaf=getGraph.getLeafPartition(partitionGraph, upw, blockSet);
	//	System.out.println(leaf);
	}

	@Override
	public void createDeployGraph(){
		LinkedList<ArrayList<Integer>> order=new LinkedList<ArrayList<Integer>>();
		deployGraph getOrder=new deployGraph();
	//	System.out.println(leaf);
		getOrder.createOrder(leaf, deploylinks, order, new ArrayList<Integer>((ArrayList<Integer>)leaf.clone()));
		inverse(order);
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
