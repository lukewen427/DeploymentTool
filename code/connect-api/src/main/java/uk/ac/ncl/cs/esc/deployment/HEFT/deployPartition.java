package uk.ac.ncl.cs.esc.deployment.HEFT;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.ncl.cs.esc.deployment.HEFT.dataCenter.dataStorage;
import uk.ac.ncl.cs.esc.read.BlockSet;
import uk.ac.ncl.cs.esc.read.CloudSet;

import uk.ac.ncl.cs.esc.workflow.eSCWorkflow;

public class deployPartition implements Runnable{
	ArrayList<Object> partition;
	CloudSet cloudset;
	int partitionid;
	String staute="checking";
	String cloudName;
	ArrayList<ArrayList<String>> connections;
	BlockSet blockset;
	ArrayList<ArrayList<String>>inputs;
	ArrayList<String> heads;
	HashMap<String,ByteArrayOutputStream>newresults=new HashMap<String,ByteArrayOutputStream>();
	
	
	public deployPartition(ArrayList<Object> partition,CloudSet cloudset,int partitionid,
			ArrayList<ArrayList<String>>inputs,BlockSet blockset,ArrayList<ArrayList<String>> connections,ArrayList<String> heads){
			this.partition=partition;
			this.cloudset=cloudset;
			this.partitionid=partitionid;
		     this.connections= connections;
		     this.blockset=blockset;
		     this.inputs=inputs;
		     this.heads=heads;
		    
		}
private synchronized void resultsStoring(HashMap<String,ByteArrayOutputStream> newResults){
		
		dataStorage.setData(newResults);
	}

public String checkStautes(){
	return staute;
}
	public void run() {
		// TODO Auto-generated method stub
		String partitionName="Partition"+partitionid;
		eSCWorkflow deployworkflow=new eSCWorkflow();
		HashMap<String,ByteArrayOutputStream> results=dataStorage.getData();
		try {	
			staute="running";
		
			newresults=deployworkflow.creatWorkflow(cloudName,partition, partitionName,
						 connections,blockset,inputs, results,heads);
			while(newresults.isEmpty()){
				 try {
		    		 Thread.sleep(500);
		    	 } catch (Exception e){}
			}
			
			staute="finish";
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			staute="fail";
			e.printStackTrace();
		}
		
		if(staute.equals("finish")){
			System.out.println("Start writting results");
			 resultsStoring(newresults);
		 } 
	}

}
