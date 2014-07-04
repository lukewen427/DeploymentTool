package uk.ac.ncl.cs.esc.deployment.HEFT;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.read.BlockSet;
import uk.ac.ncl.cs.esc.read.CloudSet;
import uk.ac.ncl.cs.esc.deployment.HEFT.dataCenter.dataStorage;
import uk.ac.ncl.cs.esc.newpartitiontool.prepareDeployment.workflowInfo;

public class deployWorkflow implements Runnable {
	LinkedList<ArrayList<Integer>> deployOrder;
	workflowInfo workflowinfo;
	ArrayList<ArrayList<String>> connections;
	deploymentIm deploy;
	HashMap<Integer,ArrayList<Object>>partitionGraph;
	ArrayList<Object> deploylinks;
	Hashtable<deployPartition,Thread>  runningPartitions=new Hashtable<deployPartition,Thread>();
	CloudSet cloudset;
	BlockSet blockset;
	boolean isRoot;
	ArrayList<Integer> exceutedNode=new ArrayList<Integer>();
	 public deployWorkflow(deploymentIm deploy,workflowInfo workflowinfo){
		 this.deploy=deploy;
		 this.workflowinfo=workflowinfo;
		 
	 }
	
	 void setConnections(){
		this.connections= workflowinfo.getConnections();
	 }
	 
	 void setDeployOrder(){
		 this.deployOrder=deploy.getOrder();
	 }
	 
	 void setPartitionGraph(){
		 this.partitionGraph=deploy.getPartitionGraph();
	 }
	 
	 void setDeployLinks(){
		 this.deploylinks=deploy.getDeployLink();
	 }
	 void setClouds(){
		 this.cloudset=workflowinfo.getClouds();
	 }
	 void setBlocks(){
		 this.blockset=workflowinfo.getBlockSet();
	 }
	 
	public void run() {
		for(int i=0;i<=deployOrder.size();i++){
			ArrayList<Integer> step=deployOrder.get(i);
			if(i==0){
				isRoot=true;
			}else{
				isRoot=false;
			}
			goDeployment(step);
			while(!runningPartitions.isEmpty()){
				Iterator<deployPartition> keys=runningPartitions.keySet().iterator();
				while(keys.hasNext()){
					deployPartition excu=keys.next();
					Thread t=runningPartitions.get(excu);
					String pName=t.getName();
					int node=Integer.valueOf(pName);
			
					while(excu.checkStautes().equals("running")||excu.checkStautes().equals("checking")){
						 try {
				    		 Thread.sleep(1000);
				    	 } catch (Exception e){}
					}
					
					if(excu.checkStautes().equals("finish")){
						removePartition(excu);
						exceutedNode.add(node);
					}
				}
			}
		}
		
	}
	
	
	private ArrayList<ArrayList<String>> getInputs(int partitionName){
		ArrayList<ArrayList<String>>inputs=new ArrayList<ArrayList<String>>();
		for(int i=0;i<deploylinks.size();i++){
			ArrayList<Object> link=(ArrayList<Object>)deploylinks.get(i);
			ArrayList<Integer> plink=(ArrayList<Integer>) link.get(0);
			ArrayList<Object> blockLinks=(ArrayList<Object>) link.get(1);
			int destination=plink.get(1);
			if(partitionName==destination){
				for(Object blocklink:blockLinks){
					ArrayList<String>bLink=(ArrayList<String>) blocklink;
					if(!isContain(inputs,bLink)){
						inputs.add((ArrayList<String>)bLink.clone());
					}
				}
			}
		}
		return inputs;
	}
	void goDeployment(ArrayList<Integer> step){
		
		for(int node:step){
			ArrayList<Object> partition=partitionGraph.get(node);
			if(exceutedNode.contains(node)){
				
			}else{
				ArrayList<ArrayList<String>>inputs=getInputs(node);
				if(checkSource(node)){
					deploy(partition,inputs,node);
				}
				
			}
			
		}
	}
	
	private boolean checkSource(int parentsNode){
		
		if(isRoot==true){
			return true;
		}else{
			ArrayList<ArrayList<String>>getinputs=getInputs(parentsNode);
			HashMap<String,ByteArrayOutputStream> results=dataStorage.getData();
			Set<String>dataSet=results.keySet();
			for(int i=0;i<getinputs.size();i++){
				ArrayList<String> input=getinputs.get(i);
				String inputNode=input.get(0)+","+input.get(1);
				if(!dataSet.contains(inputNode)){
					return false;
				}
			}
			
			return true;
		}
		
	}
	
	void deploy(ArrayList<Object> partition,ArrayList<ArrayList<String>>inputs,int node){
		
		ArrayList<String> heads=new ArrayList<String>();
		if(inputs.isEmpty()){
			 heads=headBlocks(partition);
		}else{
			for(ArrayList<String> connect:inputs){
				String headNode=connect.get(1);
				heads.add(headNode);
			}
		}
		
		deployPartition excu=new deployPartition(partition,cloudset,node,inputs,blockset,connections,heads);
		Thread t= new Thread(excu);
		t.setName(String.valueOf(node));
		t.start();
		addNewPartition(excu,t);
	}
	
	
private ArrayList<String> headBlocks(ArrayList<Object> partition){
		
		ArrayList<String> headBlocks=new ArrayList<String>();
		
		for(int a=1;a<partition.size();a++){
			Object block=partition.get(a);
			if(block instanceof Block){
				boolean isHead=true;
				String BlockId=((Block)block).getBlockId();
				for(int i=0;i<connections.size();i++){
					ArrayList<String> connect=connections.get(i);
					if(BlockId.equals(connect.get(1))){
						isHead=false;
					}
				}
				if(isHead){
					headBlocks.add(BlockId);
				}
			}
		}
		
		return headBlocks;
	}
private boolean isContain(ArrayList<ArrayList<String>> inputs,ArrayList<String> bLink){
		
		if(inputs.isEmpty()){
			return false;
		}else{
			boolean isContain=false;
			for(int a=0;a<inputs.size();a++){
				ArrayList<String> input=inputs.get(a);
				if((input.get(0).equals(bLink.get(0)))&&(input.get(1).equals(bLink.get(1)))){
					
					return true;
				}
			}
			return isContain;
		}
		
	}

public synchronized void addNewPartition(deployPartition excu,Thread t){
	
	 runningPartitions.put(excu, t);
}

public synchronized void removePartition(deployPartition excu){
	
	runningPartitions.remove(excu);
}

}
