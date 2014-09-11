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
	unpWorkflow unpw=new unpWorkflow(unpPartition, deploy, workflowinfo);
	
	// this is for cloud fail
	public costNewClouds(LinkedList<String>avaClouds, Set<Integer> unpPartition,deploymentIm deploy,workflowInfo workflowinfo){
		this.avaClouds=avaClouds;
		this.unpPartition=unpPartition;
		this.deploy=deploy;
		this.workflowinfo=workflowinfo;
		this.oldClouds=null;
	}
	
	// this is for testing new clouds
	public costNewClouds(LinkedList<String>avaClouds, LinkedList<String>oldClouds, Set<Integer> unpPartition, deploymentIm deploy,workflowInfo workflowinfo){
		this.avaClouds=avaClouds;
		this.unpPartition=unpPartition;
		this.deploy=deploy;
		this.workflowinfo=workflowinfo;
		this.oldClouds=oldClouds;
	}
	// if the new clouds is cheaper to running the rest of tasks, shift to them. 
	public boolean needChange(){
		return false;
	}
	
	void setunpBlocks(){
		this.blockInfo=unpw.getunprocessBlocks();
	}
	void setunpConnects(){
		this.connections=unpw.getunprocessConnections();
	}
	
}
