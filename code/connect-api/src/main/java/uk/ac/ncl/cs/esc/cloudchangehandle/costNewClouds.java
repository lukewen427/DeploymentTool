package uk.ac.ncl.cs.esc.cloudchangehandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ncl.cs.esc.deployment.HEFT.deploymentIm;
import uk.ac.ncl.cs.esc.newpartitiontool.prepareDeployment.workflowInfo;

public class costNewClouds {
	
	LinkedList<String>avaClouds;
	Set<Integer> unpPartition;
	ArrayList<ArrayList<String>> connections;
	HashMap<String,ArrayList<String>> blockInfo;
	deploymentIm deploy;
	workflowInfo workflowinfo;
	LinkedList<String>oldClouds;
	unpWorkflow unpw;
	int [][]  deployment;
	boolean needChange=false;
	ArrayList<Object> inputLinks;
	// this is for cloud fail
	
	public costNewClouds(LinkedList<String>oldCloud, Set<Integer> unpPartition,deploymentIm deploy,workflowInfo workflowinfo){
		this.unpPartition=unpPartition;
		this.deploy=deploy;
		this.workflowinfo=workflowinfo;
		this.avaClouds=workflowinfo.getAvaClouds();
		unpw=new unpWorkflow(unpPartition, deploy, workflowinfo,oldCloud);
		this.oldClouds=null;
	     setunpBlocks();
	     setunpConnects();
	     setInputLinks();
	}
	
	// this is for testing new clouds
	public costNewClouds(LinkedList<String>avaClouds, LinkedList<String>oldClouds, Set<Integer> unpPartition, deploymentIm deploy,workflowInfo workflowinfo){
		this.avaClouds=avaClouds;
		this.unpPartition=unpPartition;
		this.deploy=deploy;
		this.workflowinfo=workflowinfo;
		this.oldClouds=oldClouds;
		unpw=new unpWorkflow(unpPartition, deploy, workflowinfo,oldClouds);
		 setunpBlocks();
	     setunpConnects();
	     setInputLinks();
	}
	// if the new clouds is cheaper to running the rest of tasks, shift to them. 
	public boolean needChange(){
		costCalculator();
		return needChange;
	}
	
	void setunpBlocks(){
		this.blockInfo=unpw.getunprocessBlocks();
	}
	void setunpConnects(){
		this.connections=unpw.getunprocessConnections();
	}
	void setInputLinks(){
		this.inputLinks=unpw.getInputlinks();
	}
	
	public  int [][] getDeployment(){
		if(oldClouds==null){
			costCalculator current= new costCalculator (avaClouds, connections, blockInfo,inputLinks);
			this.deployment=current.getDeployment();
		}
		return deployment;
	}
		
	private void costCalculator(){
		if(oldClouds!=null){
			costCalculator old= new costCalculator (oldClouds, connections, blockInfo,inputLinks);
			costCalculator current= new costCalculator (avaClouds, connections, blockInfo,inputLinks);
			// if the new clouds are cheaper than old clouds
			if(old.getCost()>current.getCost()){
				needChange=true;
				this.deployment=current.getDeployment();
			}
		}
	}
	
	
}
