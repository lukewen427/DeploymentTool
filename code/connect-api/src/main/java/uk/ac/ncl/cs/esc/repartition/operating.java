package uk.ac.ncl.cs.esc.repartition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.read.BlockSet;
import uk.ac.ncl.cs.esc.reliable.prepareWorkflow.ReliWorkflow;
 
public class operating {
	partitionWorkflow partition;
	HashMap<Block, Integer> option;
	LinkedList<ArrayList<String>> partitions;
	HashMap<Integer, ArrayList<ArrayList<String>>> links;

	public operating(ReliWorkflow workflowinfo) throws Exception {

		// map the blocks to clouds
		this.partition = new partitionWorkflowImp(workflowinfo);
		this.option = partition.mappingCloud();
		// System.out.println(option);
		// partition the workflow
		partition.workflowSplit(option);
		partitions = partition.getOrder();
		links = partition.getLinks();
		// System.out.println(partitions);
		// System.out.println(option);
		// System.out.println(workflowinfo.getCloudinfo().getAvaClouds());

		// / to be easier
		BlockSet blockSet = workflowinfo.getBlockSet();
		ArrayList<String> step4 = partitions.get(partitions.size() - 2);
		Block blo4 = blockSet.getBlock(step4.get(0));
		ArrayList<String> step5 = partitions.get(partitions.size() - 1);
		Block blo5 = blockSet.getBlock(step5.get(0));
		HashMap<Integer, String> cloudMap = workflowinfo.getCloudMap();
		int cloud4 = option.get(blo4);
		String cloudName4 = cloudMap.get(cloud4);
		int cloud5 = option.get(blo5);
		String cloudName5 = cloudMap.get(cloud5);
		if (cloudName4.equals("cloud2") && cloudName5.equals("cloud1")) {
			workflowDeployment dep = new workflowDeployment(partitions, links,
					workflowinfo, option);
			Thread t = new Thread(dep);
			t.start();
		}else{
			System.out.println("no");
		}
		/*workflowDeployment dep = new workflowDeployment(partitions, links,
				workflowinfo, option);
		Thread t = new Thread(dep);
		t.start();*/
	}

}
