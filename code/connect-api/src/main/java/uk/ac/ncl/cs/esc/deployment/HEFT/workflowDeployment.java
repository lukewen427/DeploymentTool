package uk.ac.ncl.cs.esc.deployment.HEFT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ncl.cs.esc.cloudchangehandle.costNewClouds;
import uk.ac.ncl.cs.esc.newpartitiontool.prepareDeployment.workflowInfo;
import uk.ac.ncl.cs.esc.read.Block;

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
	String worklfowStatues;
	public workflowDeployment (deploymentIm deploy,	ArrayList<ArrayList<String>> connections,workflowInfo workflowinfo){
		this.workflowinfo=workflowinfo;
		this.deploy=deploy;
		this.connections=connections;
		this.avaClouds =workflowinfo.getAvaClouds();
		this.worklfowStatues="running";
		setDeployOrder();
		setPartitionGraph();
		 initUNPParition();
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
						stopWorkers();
						this.worklfowStatues="cloudChange";
					//	int[][] deployment=c.getDeployment();
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
						this.worklfowStatues="fail";
					}
				}
			}
		}
		
		this.worklfowStatues="finish";
	}
	private void Deployment(ArrayList<Integer> step) {
		for(int node:step){
			ArrayList<Object> partition=partitionGraph.get(node);
			int cloud=(int) partition.get(0);
			String cloudName=avaClouds.get(cloud);
			LinkedList<String> currentCloud=workflowinfo.getAvaClouds();
			if(cloudName.equals(currentCloud.get(cloud))){
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
				costNewClouds c=new costNewClouds(avaClouds,unproPartition,deploy,workflowinfo);
				stopWorkers();
				this.worklfowStatues="cloudChange";
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
		this.unproPartition=partitionGraph.keySet();
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
		return worklfowStatues; 
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
