package uk.ac.ncl.cs.esc.deployment.HEFT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import uk.ac.ncl.cs.esc.read.Block;

public class workflowDeployment implements Runnable {
	LinkedList<ArrayList<Integer>> deployOrder;
	HashMap<Integer,ArrayList<Object>>partitionGraph;
	deploymentIm deploy;
	Hashtable<runningPartition,Thread> runningPartitions=new Hashtable<runningPartition,Thread>();
	ArrayList<Integer> exceutedNode=new ArrayList<Integer>();
	ArrayList<ArrayList<String>> connections;
	public workflowDeployment (deploymentIm deploy,	ArrayList<ArrayList<String>> connections){
		this.deploy=deploy;
		this.connections=connections;
		setDeployOrder();
		setPartitionGraph();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		for(int i=0;i<deployOrder.size();i++){
			ArrayList<Integer> step=deployOrder.get(i);
				Deployment(step);	
			while(!runningPartitions.isEmpty()){
				Iterator<runningPartition> keys=runningPartitions.keySet().iterator();
				while(keys.hasNext()){
					runningPartition excu=keys.next();
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
	private void Deployment(ArrayList<Integer> step) {
		for(int node:step){
			ArrayList<Object> partition=partitionGraph.get(node);
			int cloud=(int) partition.get(0);
			String cloudName="cloud"+cloud;
			if(exceutedNode.contains(node)){
				
			}else{
				HashMap<String,String> newPartition=converse(partition);
				runningPartition excu=new runningPartition(cloudName,newPartition,  connections, node);
				Thread t= new Thread(excu);
				t.setName(String.valueOf(node));
				t.start();
				addNewPartition(excu,t);
			}
		}
		
	}
	
	HashMap<String,String> converse(ArrayList<Object> partition){
		HashMap<String,String> newPartition=new HashMap<String,String>();
		for(int a=1;a<partition.size();a++){
			Block block=(Block) partition.get(a);
			String id=block.getBlockId();
			String serviceId=block.getserviceId();
			newPartition.put(id, serviceId);
		}
		return newPartition;
	}
	void setDeployOrder(){
		 this.deployOrder=deploy.getOrder();
	 }
	void setPartitionGraph(){
		 this.partitionGraph=deploy.getPartitionGraph();
	 }
	
	public synchronized void addNewPartition(runningPartition excu,Thread t){
		
		 runningPartitions.put(excu, t);
	}

	public synchronized void removePartition(runningPartition excu){
		
		runningPartitions.remove(excu);
	}
}
