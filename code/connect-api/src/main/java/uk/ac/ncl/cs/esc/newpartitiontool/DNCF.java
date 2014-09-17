package uk.ac.ncl.cs.esc.newpartitiontool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ncl.cs.esc.read.WorkflowTemplate;
import uk.ac.ncl.cs.esc.security.Security;


// this class is to deploy block in the running time. 
public class DNCF {
	double[][] workflow;
    double [][] ccost;
    double [][] cpucost;
    double[][] storageTime;
    double[] storageCost;
    int [][] deployment;
    int [][] finaldeployment;
    int aveCom=0;
 // a block has a few inputs, each ArrayList<Object> is a input : cloud and connection
    HashMap<Integer,ArrayList<Object>> blockInputs;
    Set<Integer> root;
    ArrayList<Integer> leaf=new ArrayList<Integer>();
    HashMap<Integer,Double> rank=new HashMap<Integer,Double>();
    HashMap<Integer,Double> SOC=new  HashMap<Integer,Double>();
    Security checking;
    double totalcost;
  //  ArrayList<Integer> queue=new ArrayList<Integer>();
    
    public DNCF(WorkflowTemplate getInfo, HashMap<Integer,ArrayList<Object>> blockInputs){
    	this.workflow=getInfo.getWorkflow();
        this.ccost=getInfo.getCcost();
        this.cpucost=getInfo.getCpucost();
        this.storageCost=getInfo.getStorageCost();
        this.storageTime=getInfo.getStorageTime();
        this.checking=new Security(getInfo);
        deployment=new int[workflow.length][ccost.length];
        finaldeployment=new int[workflow.length][ccost.length];
        this.blockInputs=blockInputs;
        setRoot();
    }
    
    void setRoot(){
    	this.root=blockInputs.keySet();
    }
    
    ArrayList<Integer> getQueue(){
    	ArrayList<Integer> queue=new ArrayList<Integer>();
    	for(int a=0;a<workflow.length;a++){
    		queue.add(a);
    	}
    	return queue;
    }
    
    public int[][] DyNCF(){
    	preDeployment();
    	ArrayList<Integer> queue=getQueue();
    	while(!queue.isEmpty()){
    		
    	}
    	
    	return finaldeployment;
    }
    
    private void preDeployment(){
    	ArrayList<Integer> queue=getQueue();
    	 while(!queue.isEmpty()){
    		for(int i=0;i<queue.size();i++){
    			int block=queue.get(i);
    			if(root.contains(block)){
    				ArrayList<Object> inputs=blockInputs.get(block);
    				double min=0;
    				int cloud=0;
    				for(int a=0; a<ccost.length;a++){
    					if(checking.allowedDeploy(block, a)){
    						double SOCcost=RSOC(block,a,inputs);
    						if(min==0){
    							min=SOCcost;
    							cloud=a;
    						}else{
    							if(min>SOCcost){
    								min=SOCcost;
    								cloud=a;
    							}
    							
    						}
    					}
    				}
    				setDeployment(block,cloud);
                    queue.remove((Object)block);
                    SOC.put(block, min);
    			}else{
    			    ArrayList<Integer> parent = getParents(block);
                    double min = 0;
                    int cloud = 0;
                    if (!getUndeploy(parent)) {
                        for (int a = 0; a < ccost.length; a++) {
                            if (checking.allowedDeploy(block, a)) {
                            
                                double SOCcost = SOC(block, a, parent);
                                if (SOCcost == -1) {
                                  //  show = true;
                                    System.out.println("parent node has not been deployed");
                                } else {
                                    if(min==0){
                                        min=SOCcost;
                                        cloud = a;
                                    }else{
                                        if (min > SOCcost) {
                                            min = SOCcost;
                                            cloud = a;
                                        }
                                    }

                                }
                            }

                        }
                        setDeployment(block,cloud);
                        queue.remove((Object)block);
                        SOC.put(block, min);
                  //      total+=min;
                    }
    			}
    			
    		}
    		
    	 }
    }
    
    // get parent nodes
    private ArrayList<Integer> getParents(int node){
        ArrayList<Integer> parent=new ArrayList<Integer>();
        for(int i=0;i<workflow[node].length;i++){
            if(workflow[i][node]>0){
                if(!parent.contains(i)){
                    parent.add(i);
                }
            }
        }
        return parent;
    }
    
    private boolean getUndeploy(ArrayList<Integer> parent){
        //	ArrayList<Integer> undeployedParent=new ArrayList<Integer>();
    
    //	print(deployment);
        for(int a=0;a<parent.size();a++){
            int node=parent.get(a);
            if(returnDeployedCloud(node)==-1){
                return true;
            }
        }

        return false;
    }
    
    // get deployed cloud of nodes
    private int returnDeployedCloud(int node){
        for(int a=0;a<deployment[node].length;a++){
            if(deployment[node][a]==1){
                return a;
            }
        }
        return -1;
    }
    
    // the sum of the communication cost of root node
    private double RSOC(int node, int cloud, ArrayList<Object> inputs){
    	double sum=0;
    	double computCost=cpucost[node][cloud];
    	sum+=computCost;
    	for(int a=0;a<inputs.size();a++){
    		ArrayList<Object> singleInput=(ArrayList<Object>) inputs.get(a);
    		int inputCloud=(int) singleInput.get(0);
    		ArrayList<String> connect=(ArrayList<String>) singleInput.get(1);
    		double dataSize=Double.valueOf(connect.get(8));
    		double comCost=ccost[inputCloud][cloud]*dataSize;
    		sum+=comCost;
    	}
    	return sum;
    }
    
    // the sum of the communication cost from parent nodes
    public double SOC(int node,int cloud,ArrayList<Integer> parent){
        double sum=0;
        double computCost=cpucost[node][cloud];
        sum+=computCost;
        for(int a=0;a<parent.size();a++){
            int singleNode=parent.get(a);
            int parentCloud= returnDeployedCloud(singleNode);
            if(parentCloud==-1){
                return -1;
            }else{
                sum+=communicationCost(singleNode,node,parentCloud,cloud);
                sum+=storageCost(singleNode,node,parentCloud,cloud);
            }
        }
        return sum;
    }
    
 // return the storage cost: source cloud storagecost* datasize* storagetime
    
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
    
    // pre deployment 
    private void setDeployment(int node,int cloud){
        deployment[node][cloud]=1;
    }
}
