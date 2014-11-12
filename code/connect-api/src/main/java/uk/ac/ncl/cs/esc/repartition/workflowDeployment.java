package uk.ac.ncl.cs.esc.repartition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ncl.cs.esc.deployment.HEFT.runningPartition;
import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.read.BlockSet;
import uk.ac.ncl.cs.esc.reliable.prepareWorkflow.ReliWorkflow;

public class workflowDeployment implements Runnable {
	LinkedList<ArrayList<String>> deployOrder;

	Hashtable<runningBlock,Thread> runningBlocks=new Hashtable<runningBlock,Thread>();
	ArrayList<String> exceutedNode=new ArrayList<String>();
//	ArrayList<ArrayList<String>> connections;
	//LinkedList<String> avaClouds;
	HashMap<Integer,String>cloudMap;
	boolean killThread=false;
	ReliWorkflow workflowinfo;
	String workflowStatues;
	HashMap<Block,Integer> option;
	HashMap<Integer, ArrayList<ArrayList<String>>> links;
	BlockSet blockSet;
	
	public workflowDeployment(LinkedList<ArrayList<String>> partitions,
			HashMap<Integer, ArrayList<ArrayList<String>>> links,
			ReliWorkflow workflowinfo, HashMap<Block, Integer> option) {
		this.workflowinfo = workflowinfo;
		// this.connections = connections;
		this.cloudMap = workflowinfo.getCloudMap();
		this.deployOrder = partitions;
		this.links = links;
		this.option = option;
		this.blockSet=workflowinfo.getBlockSet();
		this.workflowStatues = "running";
	//	System.out.println(links);
//		System.out.println(deployOrder);
		// boolean killThread=false;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (int i = 0; i < deployOrder.size(); i++) {
			if (killThread) {
				break;
			}
			ArrayList<String> blocks = deployOrder.get(i);
			Deployment(blocks, i);
			while (!runningBlocks.isEmpty() && (killThread == false)) {

				Iterator<runningBlock> key = runningBlocks.keySet().iterator();
				while (key.hasNext()) {
					runningBlock excu = null;
					try {
						excu = key.next();
					} catch (Exception e) {
						if (!runningBlocks.isEmpty()) {
							key = runningBlocks.keySet().iterator();
						}
					}
					if (excu != null) {
						Thread t = runningBlocks.get(excu);
						String bName = t.getName();

						while (excu.checkStautes().equals("running")
								|| excu.checkStautes().equals("checking")) {
							try {
								Thread.sleep(100);
							} catch (Exception e) {
							}
						}

						if (excu.checkStautes().equals("finish")) {
							removePartition(excu);
							exceutedNode.add(bName);

						}

						if (excu.checkStautes().equals("fail")) {
							stopWorkers();
							this.workflowStatues = "fail";
						}
					}

				}

			}

		}

		this.workflowStatues = "finish";

		if (workflowStatues.equals("finish")) {
			System.out.println("worklfow execution completely");
		}
	}
	
	private void Deployment(ArrayList<String> blocks,int step) {
		for(String block:blocks){
			Block blo=blockSet.getBlock(block);
			int cloud=option.get(blo);
			String cloudName=cloudMap.get(cloud);
			System.out.println("needed cloud "+cloudName);
			System.out.println("needed cloud "+blo.getBlockId());
	//		System.out.println("needed cloud "+cloudName);
	//		System.out.println("old cloud "+avaClouds);
			
			LinkedList<String> currentCloud=workflowinfo.getAvaClouds();
	//		System.out.println("current cloud "+currentCloud);
			if(currentCloud.contains(cloudName)){
	//			System.out.println("deploy cloud:" +cloudName);
				if(exceutedNode.contains(block)){
					
				}else{
					ArrayList<ArrayList<String>>inputs = null;
					ArrayList<ArrayList<String>> outputs = null;
					if(step==0){
						outputs=getOutputs(block,step);
					}
					if(step==deployOrder.size()-1){
						inputs=getInputs(block,step);
					}
					if(0<step && step<(deployOrder.size()-1)){
						
						inputs=getInputs(block,step);
						 outputs=getOutputs(block,step);
					}
				//	System.out.println(inputs);
				//	System.out.println(outputs);
				
					runningBlock excu=new runningBlock(cloudName, blo, inputs, outputs);
					Thread t= new Thread(excu);
					t.setName(block);
					t.start();
					addNewPartition(excu,t);
				}
			}else{
				
				System.out.println("cloud fail");
				stopWorkers();
				this.workflowStatues="cloudfail";
				break;
			}
	
		}
		
	}
	

	ArrayList<ArrayList<String>> getInputs(String block,int step){
		ArrayList<ArrayList<String>> inputs=new ArrayList<ArrayList<String>>();
		int value=step;
		ArrayList<ArrayList<String>> temp=links.get(value);
		
		for(ArrayList<String> single:temp){
			String end=single.get(1);
			if(end.equals(block)){
				inputs.add(single);
			}
		}
		return inputs;
	}
	
	ArrayList<ArrayList<String>> getOutputs(String block,int step){
		int value=step+1;
		ArrayList<ArrayList<String>> outputs=new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> temp=links.get(value);
		for(ArrayList<String> single:temp){
			String start=single.get(0);
			if(start.equals(block)){
				outputs.add(single);
			}
		}
		return outputs;
	}

	
	public synchronized void addNewPartition(runningBlock excu,Thread t){
		
		 runningBlocks.put(excu, t);
	}

	public synchronized void removePartition(runningBlock excu){
		
		 runningBlocks.remove(excu);
	}
	
	public void stopThread(){
		killThread=true;
	}
	
	
	
	public String getWorkflowStatue(){
		return workflowStatues; 
	}
	private synchronized void stopWorkers(){
		if(!runningBlocks.isEmpty()){
			Iterator<runningBlock> keys= runningBlocks.keySet().iterator();
			while(keys.hasNext()){
				
				runningBlock excu = null;
				try {
					excu = keys.next();
				} catch (Exception e) {
					if (!runningBlocks.isEmpty()) {
						keys = runningBlocks.keySet().iterator();
					}
				}
			//	runningBlock excu=keys.next();
				removePartition(excu);
			}
		}
		stopThread();
	}
}
