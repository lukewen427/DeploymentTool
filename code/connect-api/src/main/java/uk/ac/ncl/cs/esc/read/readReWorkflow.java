package uk.ac.ncl.cs.esc.read;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ncl.cs.esc.cloudMonitor.cloudMonitorIm;
import uk.ac.ncl.cs.esc.cloudMonitor.CloudPool.Clouds;
import uk.ac.ncl.cs.esc.reliable.Annealing;
import uk.ac.ncl.cs.esc.reliable.frontCost;
import uk.ac.ncl.cs.esc.security.Security;

import com.google.common.collect.HashBiMap;

public class readReWorkflow {
	// worklfow information
	double[][] workflow;
	int[][] dataSecurity;
	double[][] ccost;
	double[][] cpucost;
	int[] cloud;
	int[][] ssecurity;
	double []  blockExecutionTime;
    double [] StorageCost;
    double [] cloudStartTime;
    
    HashMap<Integer,String> cloudMap=new HashMap<Integer,String>();
	Set<Cloud> cloudSet = new HashSet<Cloud>();
	LinkedList<String> avaClouds;
	ArrayList<ArrayList<String>> connections;
	HashMap<String, ArrayList<String>> blockInfo;
	HashBiMap<String, Integer> biMap = HashBiMap.create();
	cloudMonitorIm cm;
	int[][] deployment;
	
	public readReWorkflow(ArrayList<ArrayList<String>> connections,
						HashMap<String,ArrayList<String>> blockInfo,cloudMonitorIm cm){
		this.cm=cm;
		this.connections=connections;
		this.blockInfo=blockInfo;
		this.cloudSet=getClouds();
		initial();	
	}
	
	public Set<Cloud> getClouds() {
		
		Set<Cloud> cloudSet=new HashSet<Cloud>();
		this.avaClouds=cm.getAvaClouds();

		for(String cloudName:avaClouds){
			Cloud c=Clouds.getCloud(cloudName);
	//		System.out.println(cloudName);
			if(!cloudSet.contains(c)){
				cloudSet.add(c);
			}
		}
		return cloudSet;
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
	public HashMap<Integer,String> getCloudMap(){
		return cloudMap;
	}
	void initial(){
		creatWorkflow();
		createdataSecurity();
		createCCost();
		createCPUcost();
		createCloud();
		createSsecurity();
		createExTime();
		createStorage();
		createStartTime();
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
		wm.setStorageCost(StorageCost);
		wm.setCloudStartTime(StorageCost);
		wm.setExecutionTime(blockExecutionTime);
		wm.initialPath();
	//	printInt(workflow);
		Security checking=new Security(wm);
		
		// for the optimal option
		if(checking.workflowSecurity()){
			Annealing n= new Annealing(wm);
			n.annealingAlogirthm();
			this.deployment=n.getDeloyment();
	//		printInt(deployment);
		}else{
			System.out.println("invalid workflow");
		}
		
		//for cost front
		
	/*	if(checking.workflowSecurity()){
			frontCost n=new frontCost(wm);
			System.out.println(n.NCFAlgorithm());
			this.deployment=n.getDeployment();
		
	//		printInt(deployment);
		}else{
			System.out.println("invalid workflow");
		}*/
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
	void createExTime(){
		blockExecutionTime=new double [blockInfo.size()];
	}
	void createStorage(){
		StorageCost=new double [cloudSet.size()];
	}	
	void createStartTime(){
		cloudStartTime=new double [cloudSet.size()];
	}
	
	
	 private void setWorkflow(){
		Iterator<String> blocks=blockInfo.keySet().iterator();
		int a=0;
		while(blocks.hasNext()){
		  String name=blocks.next();
		  biMap.put(name, a);
		  ArrayList<String> block=blockInfo.get(name);
		  int clearnce=Integer.valueOf(block.get(1));
		  int location=Integer.valueOf(block.get(0));
		  double time= Double.valueOf(block.get(3));
		  blockExecutionTime[a]=time;
		  setSsecurity(clearnce,location,a);
		  setCPU(a,time);
		  a++;
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
	 
		private  void setCloud(){
			Iterator<Cloud> thecloud=cloudSet.iterator();
			int a=0;
			while(thecloud.hasNext()){
				Cloud singlecloud=thecloud.next();
				String name=singlecloud.getCloudname();
				cloudMap.put(a, name);
				int cloudSecurity=Integer.valueOf(singlecloud.getCloudsecurityLevel());
				double incoming=Double.valueOf(singlecloud.getTransferin());
				double outgoing=Double.valueOf(singlecloud.getTransferout());
				double startTime=Double.valueOf(singlecloud.getStartTime());
				double storageCost=Double.valueOf(singlecloud.getStorageCost());
				setCloud(a,incoming,outgoing);
				cloud[a]=cloudSecurity;
				cloudStartTime[a]=startTime;
				StorageCost[a]=storageCost;
				a++;
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
				a++;
			}
		}
		
		void setComCost(int startCloud,int endCloud, double Cost){
			if(ccost[startCloud][endCloud]>0){
				
			}else{
				ccost[startCloud][endCloud]=Cost;
			}
		}
		
		private void setCommunication(){
			for(ArrayList<String> link:connections){
				String startNode=link.get(0);
				String endNode=link.get(1);
				int dataSecurity=Integer.valueOf(link.get(6));
				double dataSize=Double.valueOf(link.get(8));
				int start=biMap.get(startNode);
				int end=biMap.get(endNode);
				setDatasize(start,end,dataSize);
				setDataSecurity(start,end,dataSecurity);
			}
		}
		
		void setDatasize(int startNode,int endNode,double dataSize){
			workflow[startNode][endNode]=dataSize;
		}
		void setDataSecurity(int startNode,int endNode,int security){
			dataSecurity[startNode][endNode]=security;
		}
		
		void printInt( int [][] workflow){
			for(int a=0;a<workflow.length;a++){
				for(int i=0;i<workflow[a].length;i++){
					System.out.print(workflow[a][i]+",");
				}
				System.out.println("");
		 }
		}
}
