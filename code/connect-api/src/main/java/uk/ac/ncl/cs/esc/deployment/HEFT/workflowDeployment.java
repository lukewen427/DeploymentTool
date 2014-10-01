package uk.ac.ncl.cs.esc.deployment.HEFT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ncl.cs.esc.cloudMonitor.cloudMonitorIm;
import uk.ac.ncl.cs.esc.cloudchangehandle.costNewClouds;
import uk.ac.ncl.cs.esc.cloudchangehandle.costNewClouds.deployInfo;
import uk.ac.ncl.cs.esc.cloudchangehandle.handleCloudChange;
import uk.ac.ncl.cs.esc.newpartitiontool.prepareDeployment.workflowInfo;
import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.read.BlockSet;

public class workflowDeployment implements Runnable {
	LinkedList<ArrayList<Integer>> deployOrder;
	HashMap<Integer,ArrayList<Object>>partitionGraph;
	deploymentIm deploy;
	Hashtable<runningPartition,Thread> runningPartitions=new Hashtable<runningPartition,Thread>();
	ArrayList<Integer> exceutedNode=new ArrayList<Integer>();
	ArrayList<ArrayList<String>> connections;
	LinkedList<String> avaClouds;
	Set<Integer> unproPartition=new HashSet<Integer>();
	boolean killThread=false;
	workflowInfo workflowinfo;
	String workflowStatues;
	public workflowDeployment (deploymentIm deploy,	ArrayList<ArrayList<String>> connections,workflowInfo workflowinfo){
		this.workflowinfo=workflowinfo;
		this.deploy=deploy;
		this.connections=connections;
		this.avaClouds =(LinkedList<String>) workflowinfo.getAvaClouds().clone();
		setDeployOrder();
		setPartitionGraph();
		initUNPParition();
		this.workflowStatues="running";
	//	boolean killThread=false;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		for(int i=0;i<deployOrder.size();i++){
			if(killThread){
				break;
			}
			ArrayList<Integer> step=deployOrder.get(i);
				Deployment(step);	
			while(!runningPartitions.isEmpty() && (killThread==false)){
				if(isnewCloud()){
					// this is for calculate the cost of the new clouds. if cheaper, shift to new clouds.
					costNewClouds c=new costNewClouds(workflowinfo.getAvaClouds(),avaClouds,unproPartition,deploy,workflowinfo);
					if(c.needChange()){
						System.out.println("deploy to new clouds");
						stopWorkers();
						this.workflowStatues="cloudChange";		
						deployInfo deinfo=c.getDeployInfo();
						new handleCloudChange(deinfo,workflowinfo);
					}
				}else{
					
				}
				
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
						unproPartition.remove(node);
					}
					
					if(excu.checkStautes().equals("fail")){
						stopWorkers();
						this.workflowStatues="fail";
					}
				}
			}
		}
		
		this.workflowStatues="finish";
		
		if(workflowStatues.equals("finish")){
			System.out.println("worklfow execution completely");
		}
	}
	private void Deployment(ArrayList<Integer> step) {
		for(int node:step){
			ArrayList<Object> partition=partitionGraph.get(node);
			int cloud=(int) partition.get(0);
			String cloudName=avaClouds.get(cloud);
	//		System.out.println("needed cloud "+cloudName);
	//		System.out.println("old cloud "+avaClouds);
			
			LinkedList<String> currentCloud=workflowinfo.getAvaClouds();
	//		System.out.println("current cloud "+currentCloud);
			if(currentCloud.contains(cloudName)){
	//			System.out.println("deploy cloud:" +cloudName);
				if(exceutedNode.contains(node)){
					
				}else{
					HashMap<String,String> newPartition=converse(partition);
					runningPartition excu=new runningPartition(cloudName,newPartition, connections, node);
					Thread t= new Thread(excu);
					t.setName(String.valueOf(node));
					t.start();
					addNewPartition(excu,t);
				}
			}else{
				
				System.out.println("cloud fail");
				costNewClouds c=new costNewClouds(avaClouds,unproPartition,deploy,workflowinfo);
				stopWorkers();
				this.workflowStatues="cloudfail";
				deployInfo deinfo=c.getDeployInfo();
				new handleCloudChange(deinfo,workflowinfo);
				break;
			}
	
		}
		
	}
	
	private boolean isnewCloud(){
		if(avaClouds.size()<workflowinfo.getAvaClouds().size()){
			return true;
		}
		return false;
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
	void initUNPParition(){
		//this.unproPartition=partitionGraph.keySet();
		Iterator<Integer> keys=partitionGraph.keySet().iterator();
		while(keys.hasNext()){
			unproPartition.add(keys.next());
		}
	//	System.out.println(partitionGraph);
	}
	
	public synchronized void addNewPartition(runningPartition excu,Thread t){
		
		 runningPartitions.put(excu, t);
	}

	public synchronized void removePartition(runningPartition excu){
		
		runningPartitions.remove(excu);
	}
	
	public void stopThread(){
		killThread=true;
	}
	
	public Set<Integer> getUnPPartition(){
		return unproPartition;
	}
	
	public String getWorkflowStatue(){
		return workflowStatues; 
	}
	private void stopWorkers(){
		if(!runningPartitions.isEmpty()){
			Iterator<runningPartition> keys=runningPartitions.keySet().iterator();
			while(keys.hasNext()){
				runningPartition excu=keys.next();
				excu.stop();
				removePartition(excu);
			}
		}
		stopThread();
	}
}
