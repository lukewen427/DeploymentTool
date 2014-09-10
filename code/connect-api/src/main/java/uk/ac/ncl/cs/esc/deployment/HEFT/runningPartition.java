package uk.ac.ncl.cs.esc.deployment.HEFT;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import uk.ac.ncl.cs.esc.deployment.HEFT.dataCenter.dataStorage;
import uk.ac.ncl.cs.esc.workflow.eSCPartition;

public class runningPartition implements Runnable {
	   eSCPartition dep;
	   int partitionid;
	   String cloudName;
	   HashMap<String,String> partition;
	   String staute="checking";
	   boolean kill=false;
	   ArrayList<ArrayList<String>> connections;
		HashMap<String,ByteArrayOutputStream>newresults=new HashMap<String,ByteArrayOutputStream>();
	public runningPartition(String cloudName,HashMap<String,String> partition, ArrayList<ArrayList<String>> connections,int partitionid){
		this.partitionid=partitionid;
		this.cloudName=cloudName;
		this.partition=partition;
		this.connections=connections;
        this.dep=new eSCPartition();
	//	dep.createSCWorkflow("cloud0", partition, head,new HashMap<String, ByteArrayOutputStream>());
	}
	
	public String checkStautes(){
		return staute;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(kill==false){
			String partitionName="Partition"+partitionid;
			HashMap<String,ByteArrayOutputStream> results=dataStorage.getData();
		    try {
		    	 ArrayList<String> heads=getHead();
		    	 staute="running";
				 newresults=dep.createSCWorkflow(cloudName, partitionName, partition, heads, results, connections);
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
	
 public void stop(){
	 kill=true;
 }
	
private synchronized void resultsStoring(HashMap<String,ByteArrayOutputStream> newResults){
		
		dataStorage.setData(newResults);
	}
	
	ArrayList<String> getHead(){
		 ArrayList<String> heads=new ArrayList<String>();
		 Set<String> blocks=partition.keySet();
		 Iterator<String> blockset=blocks.iterator();
		 while(blockset.hasNext()){
			 String blockid=blockset.next();
			 ArrayList<String> sourceNodes=getSourceNode(blockid);
			 boolean isHead=true;
			 for(int i=0;i<sourceNodes.size();i++){
				 String sNode=sourceNodes.get(i);
				 if(blocks.contains(sNode)){
					 isHead=false;
					 break;
				 }
			 }
			 if(isHead){
				 heads.add(blockid);
			 }
		 }
			
			return heads;
	}
	
	
	ArrayList<String> getSourceNode(String destination){
		ArrayList<String> dNode=new ArrayList<String>();
		for(int a=0;a<connections.size();a++){
		
			String source=connections.get(a).get(0);
			String des=connections.get(a).get(1);
			if(destination.equals(des)){
				if(!dNode.contains(source)){
					dNode.add(source);
				}
			}
			
		}
		
		return dNode;
	}
}
