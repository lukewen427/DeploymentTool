package uk.ac.ncl.cs.esc.read;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


 public class WorkflowModel implements WorkflowTemplate {   double[][] workflow;
 int[][] dataSecurity;
 double [][] ccost;
 double [][] cpucost;
 // cloud means cloud security level
 int [] cloud;
 int [][] ssecurity;
 double [][] StorageTime;
 double [] StorageCost;
 double [] cloudStartTime;
 double []  blockExecutionTime;
 double []criticalPath;
 ArrayList<Integer> root=new ArrayList<Integer>();
 
 public static void store(WorkflowModel workflowModel,String address) throws IOException {
     FileOutputStream fout = new FileOutputStream(address);
     ObjectOutputStream oos = new ObjectOutputStream(fout);
     oos.writeObject(workflowModel);
 }
 public static WorkflowModel read(String address) throws IOException, ClassNotFoundException {
     FileInputStream fin = new FileInputStream(address);
     ObjectInputStream ois = new ObjectInputStream(fin);
     return (WorkflowModel) ois.readObject();
 }
 

 void setWorkflow(double[][] workflow) {
     this.workflow = workflow;
 }
 
 void setExecutionTime( double []  blockExecutionTime){
 	this.blockExecutionTime= blockExecutionTime;
 }

 void setDataSecurity(int[][] dataSecurity) {
     this.dataSecurity = dataSecurity;
 }

 void setCcost(double[][] ccost) {
     this.ccost = ccost;
 }

 void setCpucost(double[][] cpucost) {
     this.cpucost = cpucost;
 }

 void setCloud(int [] cloud) {
     this.cloud = cloud;
 }

 void setSsecurity(int[][] ssecurity) {
     this.ssecurity = ssecurity;
 }
 
 
 void setStorageCost(double [] StorageCost){
 	this.StorageCost=StorageCost;
 }
 
 void setCloudStartTime( double [] cloudStartTime){
 	this.cloudStartTime=cloudStartTime;
 }
 
 void initialPath(){
 	startNode();
 	this.criticalPath =new double[workflow.length];
 	createPath();
 	this.StorageTime=new double[workflow.length][workflow.length];
 	setStorageTime();
 }
 
 void createPath(){
		 for(int a=0;a<root.size();a++){
			 ArrayList<Integer> offspring=new ArrayList<Integer>();
			 int block=root.get(a);
			 double extime= blockExecutionTime[block];
			 criticalPath[block]=extime;
			 for(int i=0;i<workflow.length;i++){
				 if(workflow[block][i]!=-1){
					 if(!offspring.contains(i)){
						 offspring.add(i);
					 }
				 }
			 }
		
			 DFS(block,new ArrayList<Integer>((ArrayList<Integer>)offspring.clone()));
		 }
		
	 }
 
 void DFS(int start, ArrayList<Integer> ends){
		 if(ends.isEmpty()){
			 return;
		 }
		 for(int a=0;a<ends.size();a++){
			 int startNode=ends.get(a);
			 if(criticalPath[startNode]>0){
				 if(criticalPath[startNode]>criticalPath[start]+blockExecutionTime[startNode]){
					 
				 }else{
					 criticalPath[startNode]= criticalPath[start]+blockExecutionTime[startNode];
				 }
			 }else{
				 criticalPath[startNode]=criticalPath[start]+blockExecutionTime[startNode];
			 }
			 ArrayList<Integer> offspring=new ArrayList<Integer>();
			 for(int i=0;i<workflow.length;i++){
				
				 if(workflow[startNode][i]!=-1){
					 int end=i;
			//		 System.out.println(end);
					 if(!offspring.contains(end)){
						 offspring.add(end);
					 }
					 
				 }
			 }
			 
			 if(!offspring.isEmpty()){
				 DFS(startNode,new ArrayList<Integer>((ArrayList<Integer>)offspring.clone()));
			 }
			
		 }
		 
	 }
 
 private void  startNode(){
 	for (int a=0; a<workflow.length; a++){
 		boolean isroot=true;
 		for(int i=0;i<workflow.length;i++){
 			if(workflow[i][a]!=-1){
 				isroot=false;
 				break;
 			}
 		}
 		if(isroot==true){
 			root.add(a);
 		}
 	}
 }
 
 void setStorageTime(){
 	double  totaltime=0;
 	for(int a=0;a< criticalPath.length;a++){
 		if(totaltime<criticalPath[a]){
 			totaltime=criticalPath[a];
 		}
 	}
 	for(int a=0;a<workflow.length;a++){
 		for(int i=0;i<workflow[a].length;i++){
 			if(workflow[a][i]!=-1){
 				double time=criticalPath[a];
 				StorageTime[a][i]=totaltime-time;
 			}else{
 				StorageTime[a][i]=-1;
 			}
 		}
 	}
 	
 //	this.StorageTime=StorageTime;
 }
 @Override
 public double[][] getWorkflow() {
     return this.workflow;
 }

 @Override
 public int[][] getDataSecurity() {
     return this.dataSecurity;
 }

 @Override
 public double[][] getCcost() {
     return this.ccost;
 }

 @Override
 public double[][] getCpucost() {
     return this.cpucost;
 }

 @Override
 public int [] getCloud() {
     return this.cloud;
 }

 @Override
 public int[][] getSsecurity() {
     return this.ssecurity;
 }
	@Override
	public double[][] getStorageTime() {
		// TODO Auto-generated method stub
		
		return this.StorageTime;
	}
	@Override
	public double[] getStorageCost() {
		// TODO Auto-generated method stub
		return this.StorageCost;
	}
	@Override
	public double[] getCloudStartTime() {
		// TODO Auto-generated method stub
		return cloudStartTime;
	}
	
	@Override
	public double[] blockExecutionTime() {
		// TODO Auto-generated method stub
		return blockExecutionTime;
	}
	
	public double[] getcriticalPath(){
		return criticalPath;
	}
	@Override
	public ArrayList<Integer> getRootNodes() {
		// TODO Auto-generated method stub
		return root;
	}
	}
