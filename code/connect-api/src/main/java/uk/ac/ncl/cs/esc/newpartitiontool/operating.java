package uk.ac.ncl.cs.esc.newpartitiontool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import uk.ac.ncl.cs.esc.deployment.HEFT.deploymentIm;
import uk.ac.ncl.cs.esc.deployment.HEFT.workflowDeployment;
import uk.ac.ncl.cs.esc.newpartitiontool.prepareDeployment.workflowInfo;
import uk.ac.ncl.cs.esc.read.Block;
 
public class operating {
	partitionWorkflow partition;
	HashMap<Block,Integer> option;
	ArrayList<Object> partitions;
	ArrayList<Object> links;
	 
	public operating(workflowInfo workflowinfo) throws Exception{
		// map the blocks to clouds
		this.partition= new partitionWorkflowImp(workflowinfo);
		this.option= partition.mappingCloud();
	//	System.out.println(option);
		// partition the workflow 
		this.partitions=partition.workflowSplit(option);
	//	System.out.println(partitions);
		this.links=partition.getLinks();
	//	System.out.println(links);
		deploymentIm deploy=new deploymentIm();
    	setDeploy(deploy,workflowinfo);
    	deploy.createpartitionGraph();
		deploy.createDeployGraph();
		LinkedList<ArrayList<Integer>> deployOrder=deploy.getOrder();
	//	System.out.println(deployOrder);
		ArrayList<ArrayList<String>> connections=workflowinfo.getConnections();
		workflowDeployment escDe=new workflowDeployment(deploy,connections);
		Thread t= new Thread(escDe);
		t.start();
	}
	
	private void setDeploy(deploymentIm deploy,workflowInfo workflowinfo){
		deploy.setOption(option);
		deploy.setParitions(partitions);
		deploy.setWorkflowIn(workflowinfo);
		deploy.setpartitionLinks(links);
		
	}
	
	
}
