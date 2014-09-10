package uk.ac.ncl.cs.esc.read;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashBiMap;

import uk.ac.ncl.cs.esc.cloudMonitor.CloudPool;
import uk.ac.ncl.cs.esc.cloudMonitor.CloudPool.Clouds;
import uk.ac.ncl.cs.esc.cloudMonitor.cloudMonitorIm;
import uk.ac.ncl.cs.esc.newpartitiontool.*;


public class readWorkflow {
	    double[][] workflow;
	    int[][] dataSecurity;
	    double [][] ccost;
	    double [][] cpucost;
	    int [] cloud;
	    int [][] ssecurity;
	    int [][] deployment;
	    Set<Cloud> cloudSet=new HashSet<Cloud>();
	    LinkedList<String> avaClouds;
	String workflowId;
	ArrayList<ArrayList<String>> connections;
	HashMap<String,ArrayList<String>> blockInfo;
	HashBiMap< String,Integer> biMap= HashBiMap.create();
	cloudMonitorIm cm;
	public readWorkflow(String workflowId, ArrayList<ArrayList<String>> connections,HashMap<String,ArrayList<String>> blockInfo,cloudMonitorIm cm){
		this.cm=cm;
		this.workflowId=workflowId;
		this.connections=connections;
		this.blockInfo=blockInfo;
		this.cloudSet=getClouds();
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
	
	void initial(){
	//	getClouds();
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
	//	System.out.println("NCF:"+n5.NCFAlgorithm());
//		Normal n1 = new Normal(wm);
//		List<Integer> lists =n1.sortBest();
    //	System.out.println(lists);
//		System.out.println("Sort:"+n1.calCost(lists));
		printInt(deployment);
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
			int start=biMap.get(startNode);
			int end=biMap.get(endNode);
			setDatasize(start,end,dataSize);
			setDataSecurity(start,end,dataSecurity);
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
	public LinkedList<String> getAVAClouds(){
		return avaClouds;
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
		
		
		/*Cloud cloud0;
		Cloud cloud1;
		cloud0=new Cloud("Cloud0","0","10.66.66.176",2,2,5);
		cloud1=new Cloud("Cloud1","1","10.66.66.176",5,5,10);
		cloudSet.add(cloud0);
		cloudSet.add(cloud1);*/
		return cloudSet;
	}
	
	void print( double[][] workflow){
		for(int a=0;a<workflow.length;a++){
			for(int i=0;i<workflow[a].length;i++){
				System.out.print(workflow[a][i]+",");
			}
			System.out.println("");
	 }
	}
	void printInt( int[][] workflow){
		for(int a=0;a<workflow.length;a++){
			for(int i=0;i<workflow[a].length;i++){
				System.out.print(workflow[a][i]+",");
			}
			System.out.println("");
	 }
	}
}
