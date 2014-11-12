package uk.ac.ncl.cs.esc.reliable;



import java.lang.Math;
import java.util.ArrayList;
import java.util.LinkedList;

import uk.ac.ncl.cs.esc.read.WorkflowModel;
import uk.ac.ncl.cs.esc.security.Security;

// 74 hours the reliability rate is 0.8
public class frontReliability {
    double [][] cpucost;
	double[][] workflow;
	int [][] deployment;
	double [] cloudStartTime;
//	double [] cloudUsedTime;
	double [] blockExecutionTime;
	double failRate=0.3;
	double [] criticalPath;
	Security checking;
	double[][] storageTime;
    double[] storageCost;
    double [][] ccost;
    double enValue=0;
    ArrayList<Integer> root;
    
	public frontReliability(WorkflowModel getInfo){
		this.workflow=getInfo.getWorkflow();
		this.cloudStartTime=getInfo.getCloudStartTime();
		 this.ccost=getInfo.getCcost();
		this.blockExecutionTime=getInfo.blockExecutionTime();
		this.checking=new Security(getInfo);
		this.cpucost=getInfo.getCpucost();
		this.storageCost=getInfo.getStorageCost();
        this.storageTime=getInfo.getStorageTime();
	//	this.cloudUsedTime=cloudStartTime;
	    deployment=new int[workflow.length][cloudStartTime.length];
	    this.criticalPath=getInfo.getcriticalPath();
	//    print(criticalPath);
	//	 System.out.print("-");
	 //   System.out.println(criticalPath);
	    this.root=getInfo.getRootNodes();
	}
	
	
	// Minimal the entropy 
	public void RELdeploy(){
		
		LinkedList<Integer> queue=getNodes();
		while(!queue.isEmpty()){
			for(int a=0;a<queue.size();a++){
				int block=queue.get(a);
				int selectedCloud = -1;
				double min=Double.MAX_VALUE;
				for(int i=0;i<cloudStartTime.length;i++){
					
					if(checking.allowedDeploy(block, i)){
						double ent=serviceEntrop(i,block);
					//	System.out.println(ent);
						if(ent<min){
							min=ent;
							selectedCloud=i;
						}
					}
				}
				enValue+=min;
				setDeployment(block,selectedCloud);
				queue.remove((Object)block);
			}
		}
	//	print(deployment);
	}
	/* calculate the reliability of a service 
	 *  deploy on a cloud
	*/
	 double serviceReliability(int cloud, int block){
		double cloudTime=getTime(cloud);
		double exeTime=getExeTime(block);
		return Math.exp(-failRate*(cloudTime+exeTime));
	}
	 
	double serviceEntrop(int cloud, int block){
		double re=serviceReliability(cloud,block);
		return (-re* Math.log(re));
	} 
	  private void setDeployment(int node,int cloud){
	        deployment[node][cloud]=1;
	    }
	     
	/* void REL(double cloudTime,double exeTime){
		 System.out.println(Math.exp(-failRate*(cloudTime+exeTime)));
	 }*/
	 
	 /*
	  * add the time of the clouds have been executed
	  * 
	  * this need the critical path from the root 
	  * */
	 
	 double getTime(int cloud){
		 return cloudStartTime[cloud];
	 }
	 
	 double getExeTime(int block){
		 return criticalPath[block];
	 }
	 
	
	 	 
	    LinkedList<Integer> getNodes(){
	    	 LinkedList<Integer> queue=new  LinkedList<Integer>();
	    	 for(int a=0;a<workflow.length;a++){
	    		 queue.add(a);
	    	 }
	    	 
	    	 return queue;
	    }
	    
	  void print(double [] b){
		  for(double x: b){
			  System.out.print(x+" ");
		  }
	  }
	  
      void print(int[][] matrix){
	      	 for(int h=0;h<matrix.length;h++){
	   	            for(int f=0;f<matrix[h].length;f++){
	   	                System.out.print(matrix[h][f]+",");
	   	            }
	   	            System.out.println("");
	   	        }
	      }
      
      // cost
      
      private double theCost(ArrayList<Integer> start, double cost,ArrayList<Integer> isVisited){
    	   
      	if(start.isEmpty()){
      		return cost;
      	}else{
      		ArrayList<Integer> offSpring=new ArrayList<Integer>();
      
      		for(int a=0;a<start.size();a++){
      		
      			int startNode=start.get(a);
      			int startCloud=isoccupied(startNode);
      			if(!isVisited.contains(startNode)){
      	//			System.out.println("Node:"+startNode);
      				cost+=cpucost[startNode][startCloud];
      	//			System.out.println(cpucost[startNode][startCloud]);
      				isVisited.add(startNode);
      			
      			// get nodes' offspring
      			for(int i=0;i<workflow.length;i++){
      				if(workflow[startNode][i]>0){
      				
      					int endNode=i;
      					int endCloud=isoccupied(endNode);
      					double comCost=communicationCost(startNode,endNode,startCloud,endCloud);
      					double storageCost=storageCost(startNode,endNode,startCloud,endCloud);
      			//		System.out.println(comCost);
      					cost+=comCost+storageCost;
      					if(!offSpring.contains(i)){
      						offSpring.add(i);
      					}
      				}
      			}
      			isVisited.add(startNode);
      		}
      		}
      		return theCost(new ArrayList<Integer>(offSpring),cost,isVisited);
      	}
      
      }
      
      private int isoccupied(int node){
       	 for(int a=0;a<deployment[node].length;a++){
    	            if(deployment[node][a]==1){
    	                return a;
    	            }
    	        }
    	        return -1;
       }
      
      private double storageCost(int startNode,int endNode,int startCloud,int endCloud){
      	if(startCloud==endCloud){
      		return 0;
      	}else{
      		return workflow[startNode][endNode]*storageTime[startNode][endNode]*storageCost[startCloud];
      	}
      }
      
      // return the communication cost between two deployed nodes
      private double communicationCost(int startNode,int endNode,int startCloud,int endCloud){
          if(startCloud==endCloud){
              return 0;
          }else{
              return workflow[startNode][endNode]*ccost[startCloud][endCloud];
          }
      }
      
      public double result(){
 
    	  return theCost(root,0,new ArrayList<Integer>());
      }
      
      public double getEN(){
    	  return enValue;
      }
      
      public int[][] getDeployment(){
    	  return deployment;
      }
      public double helpcalculate(int [][] deployment){
    	 
    	  double entropy = 0;
    	  for(int i=0; i<deployment.length;i++ ){
    		  for(int a=0;a<deployment[i].length;a++){
    			 if(deployment[i][a]==1){
    				 int cloud=a;
    				 int block=i;
   // 				 System.out.println(cloud);
    				 entropy+= serviceEntrop(cloud,block);
    			 }
    			 
    		  }
    	  }
    	  return entropy;
      }
	
}


