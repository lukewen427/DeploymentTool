package uk.ac.ncl.cs.esc.read;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ncl.cs.esc.cloudMonitor.CloudPool.Clouds;
import uk.ac.ncl.cs.esc.newpartitiontool.NCF;

import com.google.common.collect.HashBiMap;

public class readUnpWorkflow {

	 double[][] workflow;
	    int[][] dataSecurity;
	    double [][] ccost;
	    double [][] cpucost;
	    int [] cloud;
	    int [][] ssecurity;
	    int [][] deployment;
	    double totalCost;
	    Set<Cloud> cloudSet=new HashSet<Cloud>();
	    LinkedList<String> clouds;
	    ArrayList<ArrayList<String>> connections;
		HashMap<String,ArrayList<String>> blockInfo;
		HashBiMap< String,Integer> biMap= HashBiMap.create();
		ArrayList<Object> inputLinks;
		
		HashMap<Integer,ArrayList<Object>> blockInputs=new HashMap<Integer,ArrayList<Object>> ();
	public readUnpWorkflow(ArrayList<ArrayList<String>> connections,
							HashMap<String,ArrayList<String>> blockInfo,
							  LinkedList<String> clouds,ArrayList<Object> inputLinks){
		this.connections=connections;
		this.blockInfo=blockInfo;
		this.cloudSet=getClouds();
		this.inputLinks=inputLinks;
		initial();
	}
	
	public HashBiMap< String,Integer> getMap(){
		return biMap;
	}
	
	public int [][] getDeployment(){
		return deployment;
	}
	
	public double[][] getWorkflow(){
		return workflow;
	}
	public double getTotalCost(){
		return totalCost;
	}
	
	
	void initial(){
		creatWorkflow();
		createdataSecurity();
		createCCost();
		createCPUcost();
		createCloud();
		createSsecurity();
		setWorkflow();
		setCloud();
		setCommunication();
		WorkflowModel wm=new WorkflowModel();
		wm.setWorkflow(workflow);
		wm.setSsecurity(ssecurity);
		wm.setCcost(ccost);
		wm.setCloud(cloud);
		wm.setCpucost(cpucost);
		wm.setDataSecurity(dataSecurity);
		
		NCF n5= new NCF(wm); 
		this.deployment=n5.NCFAlgorithm();
		this.totalCost=n5.getTotalCost();
	}
	
	void creatWorkflow(){
		workflow=new double[blockInfo.size()][blockInfo.size()];
		for(int a=0;a<blockInfo.size();a++){
			for(int i=0;i<blockInfo.size();i++){
				workflow[a][i]=-1;
			}
		}
	}
	
	void createdataSecurity(){
		dataSecurity=new int[blockInfo.size()][blockInfo.size()];
		for(int a=0;a<blockInfo.size();a++){
			for(int i=0;i<blockInfo.size();i++){
				dataSecurity[a][i]=-1;
			}
		}
	}
	
	void createCCost(){
		ccost=new double[cloudSet.size()][cloudSet.size()];
	}
	
	void createCPUcost(){
		cpucost=new double[blockInfo.size()][cloudSet.size()];
	}
	
	void createCloud(){
		cloud=new int[cloudSet.size()];
	}
	
	void createSsecurity(){
		ssecurity=new int[blockInfo.size()][2];
	}
	 private void setWorkflow(){
		Iterator<String> blocks=blockInfo.keySet().iterator();
		int a=0;
		while(blocks.hasNext()){
		  String name=blocks.next();
	//	  System.out.println(name);
		  biMap.put(name, a);
		  ArrayList<String> block=blockInfo.get(name);
		  int clearnce=Integer.valueOf(block.get(1));
		  int location=Integer.valueOf(block.get(0));
		  double time= Double.valueOf(block.get(3));
		  setSsecurity(clearnce,location,a);
		  setCPU(a,time);
		  a++;
		}
	}
	private  void setCloud(){
			Iterator<Cloud> thecloud=cloudSet.iterator();
			int a=0;
			while(thecloud.hasNext()){
				Cloud singlecloud=thecloud.next();
			//	int cloudName=singlecloud.getNumber();
				int cloudSecurity=Integer.valueOf(singlecloud.getCloudsecurityLevel());
				double incoming=Double.valueOf(singlecloud.getTransferin());
				double outgoing=Double.valueOf(singlecloud.getTransferout());
				setCloud(a,incoming,outgoing);
				cloud[a]=cloudSecurity;
			}
		}
	
	void setCloud(int Cloud,double in,double out){
		Iterator<Cloud> thecloud=cloudSet.iterator();
		int a=0;
		while(thecloud.hasNext()){
			Cloud singlecloud=thecloud.next();
	//		int cloudName=singlecloud.getNumber();
			if(Cloud!=a){
				double incoming=Double.valueOf(singlecloud.getTransferin());
				double outgoing=Double.valueOf(singlecloud.getTransferout());
				setComCost(Cloud,a,out+incoming);
				setComCost(a,Cloud,outgoing+in);
			}
		}
	}
	private void setCommunication(){
		for(ArrayList<String> link:connections){
			String startNode=link.get(0);
			String endNode=link.get(1);
			int dataSecurity=Integer.valueOf(link.get(6));
			double dataSize=Double.valueOf(link.get(8));
			biMap.keySet();
			if(biMap.keySet().contains(startNode)&& biMap.keySet().contains(endNode)){
				int start=biMap.get(startNode);
				int end=biMap.get(endNode);
				setDatasize(start,end,dataSize);
				setDataSecurity(start,end,dataSecurity);
			}else{
				if(!biMap.keySet().contains(startNode) && biMap.keySet().contains(endNode)){
					ArrayList<Object> inputs=new ArrayList<Object>();
					int blockOrder=biMap.get(endNode);
					for(int i=0;i<inputLinks.size();i++){
						// this is single input link with cloud, id in matrix, security,data size 
						ArrayList<Object> temp=(ArrayList<Object>) inputLinks.get(i);
						String cloud=(String) temp.get(0);
						ArrayList<String> connect=(ArrayList<String>) temp.get(1);
						if(endNode.equals(connect.get(1))){
							ArrayList<Object> tempLink=new ArrayList<Object>();
							for(int h=0;h<clouds.size();h++){
								if(cloud.equals(clouds.get(h))){
									tempLink.add(h);
					//				tempLink.add(biMap.get(endNode));
				//					tempLink.add(Integer.valueOf(connect.get(6)));
				//					tempLink.add(Double.valueOf(connect.get(8)));
									tempLink.add(connect.clone());
									inputs.add(tempLink.clone());
								}
								
							}
						}
					}
					blockInputs.put(blockOrder, (ArrayList<Object>)inputs.clone());
				}
			}
		}
	}
	void setDatasize(int startNode,int endNode,double dataSize){
		workflow[startNode][endNode]=dataSize;
	}
	void setComCost(int startCloud,int endCloud, double Cost){
		if(ccost[startCloud][endCloud]>0){
			
		}else{
			ccost[startCloud][endCloud]=Cost;
		}
	}
	void setSsecurity(int C, int L, int num){
		ssecurity[num][0]=C;
		ssecurity[num][1]=L;
	}
	
	// input is the execution time of service
	 void setCPU(int block,double time){
		Iterator<Cloud> thecloud=cloudSet.iterator();
		int a=0;
		while(thecloud.hasNext()){
			Cloud cloud=thecloud.next();
		//	int cloudName=cloud.getNumber();
			double cpuCost=cloud.getCPUcost();
			double cost=cpuCost*time;
			cpucost[block][a]=cost;
			a++;
		}
	}
	
	void setDataSecurity(int startNode,int endNode,int security){
		dataSecurity[startNode][endNode]=security;
	}
	
	
	 Set<Cloud> getClouds() {
		
		Set<Cloud> cloudSet=new HashSet<Cloud>();
		for(String cloudName:clouds){
			Cloud c=Clouds.getCloud(cloudName);
			if(!cloudSet.contains(c)){
				cloudSet.add(c);
			}
		}
		
		
		return cloudSet;
	}
}
