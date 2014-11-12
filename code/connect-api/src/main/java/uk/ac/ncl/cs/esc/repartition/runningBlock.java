package uk.ac.ncl.cs.esc.repartition;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.ncl.cs.esc.deployment.HEFT.dataCenter.dataStorage;
import uk.ac.ncl.cs.esc.read.Block;
import uk.ac.ncl.cs.esc.workflow.eSCBlock;
import uk.ac.ncl.cs.esc.workflow.eSCPartition;

public class runningBlock implements Runnable{
	 eSCPartition dep;
	 String results;
	 Block Block;
	 String staute="checking";
	 ArrayList<ArrayList<String>> inputs;
	 ArrayList<ArrayList<String>> outputs;
	 String cloudName;
	 HashMap<String,ByteArrayOutputStream>newresults=new HashMap<String,ByteArrayOutputStream>();
	 
	 public runningBlock(String cloudName,Block Block, ArrayList<ArrayList<String>> inputs,ArrayList<ArrayList<String>> outputs){
			this.cloudName=cloudName;
			this.Block=Block;
			this.inputs=inputs;
			this.outputs=outputs;
		
		}
	 
	 public String checkStautes(){
			return staute;
		}
	 
	@Override
	public void run() {
		// TODO Auto-generated method stub
		HashMap<String, ByteArrayOutputStream> results = dataStorage.getData();
		
		eSCBlock eB = new eSCBlock(cloudName, Block, results, inputs, outputs);

		try {
			newresults = eB.blockInv();
			staute = "finish";
		} catch (Exception e) {
			staute = "fail";
			e.printStackTrace();
		}

		if (staute.equals("finish")) {
			System.out.println("Start writting results");
			resultsStoring(newresults);
		}
	}
	
private synchronized void resultsStoring(HashMap<String,ByteArrayOutputStream> newResults){
		
		dataStorage.setData(newResults);
	}	
}
