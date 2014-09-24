package uk.ac.ncl.cs.esc.cloudchangehandle;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.ncl.cs.esc.cloudchangehandle.handleCloudChange.unpworkflowInfo;
import uk.ac.ncl.cs.esc.deployment.HEFT.newDeploymentIm;
import uk.ac.ncl.cs.esc.newpartitiontool.partitionWorkflow;
import uk.ac.ncl.cs.esc.newpartitiontool.partitionWorkflowImp;
import uk.ac.ncl.cs.esc.read.Block;

public class newoperating {
	partitionWorkflow partition;
	HashMap<Block,Integer> option;
	ArrayList<Object> partitions;
	ArrayList<Object> links;
	public newoperating(unpworkflowInfo upw){
		this.partition=new partitionWorkflowImp(upw);
		this.option= partition.mappingCloud();
		this.partitions=partition.workflowSplit(option);
		this.links=partition.getLinks();
		newDeploymentIm deploy=new newDeploymentIm();
    	setDeploy(deploy,upw);
    	deploy.createpartitionGraph();
		deploy.createDeployGraph();
	}
	private void setDeploy(newDeploymentIm deploy, unpworkflowInfo upw) {
		// TODO Auto-generated method stub
		deploy.setOption(option);
		deploy.setParitions(partitions);
		deploy.setWorkflowIn(upw);
		deploy.setpartitionLinks(links);
	}
}
