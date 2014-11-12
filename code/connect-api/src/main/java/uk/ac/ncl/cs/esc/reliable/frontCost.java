package uk.ac.ncl.cs.esc.reliable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import uk.ac.ncl.cs.esc.read.WorkflowModel;
import uk.ac.ncl.cs.esc.security.Security;



public class frontCost {
	double[][] workflow;
    double [][] ccost;
    double [][] cpucost;
    double[][] storageTime;
    double[] storageCost;
    int [][] deployment;
    int [][] finaldeployment;
    int aveCom=0;
    ArrayList<Integer> root;
    ArrayList<Integer> leaf=new ArrayList<Integer>();
    HashMap<Integer,Double> SOC=new  HashMap<Integer,Double>();
    Security checking;
    
    public frontCost(WorkflowModel getInfo){
    	this.workflow=getInfo.getWorkflow();
        this.ccost=getInfo.getCcost();
        this.cpucost=getInfo.getCpucost();
        this.storageCost=getInfo.getStorageCost();
        this.storageTime=getInfo.getStorageTime();
        this.checking=new Security(getInfo);
        this.root=getInfo.getRootNodes();
        deployment=new int[workflow.length][ccost.length];
        finaldeployment=new int[workflow.length][ccost.length];
    }
    
	public double NCFAlgorithm() {
	//	double total = 0;
		preDeploy();
		LinkedList<Integer> queue = getNodes();
		while (!queue.isEmpty()) {
			for (int i = 0; i < queue.size(); i++) {
				int block = queue.get(i);
				ArrayList<Integer> parentNodes = getParents(block);
				// if(!parentNodes.isEmpty()){
				if (deployCheck(parentNodes) || parentNodes.isEmpty()) {
					// System.out.println(block);
					ArrayList<Integer> offSprings = getOffSpring(block);

					// if(getParents(block).isEmpty()){
					ArrayList<Object> deploy = isCross(offSprings);
					ArrayList<Object> newDeploy = checkCross(offSprings, block,
							parentNodes);

					if (deploy.isEmpty() && newDeploy.isEmpty()) {
						// System.out.println("fffffff");

						double min = 0;
						int cloud = 0;
						for (int a = 0; a < ccost.length; a++) {
							if (checking.allowedDeploy(block, a)) {

								double SOCcost = newSOC(block, a, parentNodes);
								if (SOCcost == -1) {
									// show = true;
									System.out
											.println("parent node has not been deployed");
								} else {
									if (min == 0) {
										min = SOCcost;
										cloud = a;
									} else {
										if (min > SOCcost) {
											min = SOCcost;
											cloud = a;
										}
									}

								}
							}

						}

						setfianlDeploy(block, cloud);
						queue.remove((Object) block);
					} else {
						if (!deploy.isEmpty() && !newDeploy.isEmpty()) {

							if ((double) deploy.get(deploy.size() - 1) > (double) newDeploy
									.get(newDeploy.size() - 1)) {
								// System.out.println("aaaaaa");
								int cloud = (int) deploy.get(deploy.size() - 2);
								ArrayList<Integer> temp = (ArrayList<Integer>) deploy
										.get(0);
								for (int a = 0; a < temp.size(); a++) {
									int deployBlock = temp.get(a);
									if (isoccupied(deployBlock) == -1) {
										setfianlDeploy(deployBlock, cloud);
										queue.remove((Object) deployBlock);
									}
								}
							} else {
								// System.out.println("zzzzz");
								int cloud = (int) newDeploy.get(newDeploy
										.size() - 2);
								ArrayList<Integer> temp = (ArrayList<Integer>) newDeploy
										.get(0);
								for (int a = 0; a < temp.size(); a++) {
									int deployBlock = temp.get(a);
									if (isoccupied(deployBlock) == -1) {
										setfianlDeploy(deployBlock, cloud);
										queue.remove((Object) deployBlock);
									}
								}
							}
						} else {
							if (!deploy.isEmpty()) {
								// System.out.println("hhhhhhh");
								int cloud = (int) deploy.get(deploy.size() - 2);
								ArrayList<Integer> temp = (ArrayList<Integer>) deploy
										.get(0);
								for (int a = 0; a < temp.size(); a++) {
									int deployBlock = temp.get(a);
									if (isoccupied(deployBlock) == -1) {
										setfianlDeploy(deployBlock, cloud);
										queue.remove((Object) deployBlock);
									}
								}
							} else {
								// System.out.println("ssssssss");
								int cloud = (int) newDeploy.get(newDeploy
										.size() - 2);
								ArrayList<Integer> temp = (ArrayList<Integer>) newDeploy
										.get(0);
								for (int a = 0; a < temp.size(); a++) {
									int deployBlock = temp.get(a);
									if (isoccupied(deployBlock) == -1) {
										setfianlDeploy(deployBlock, cloud);
										queue.remove((Object) deployBlock);
									}
								}
							}
						}

					}

				}

			}
		}
//		print(finaldeployment);
		return theCost(root,0,new ArrayList<Integer>());
	}
    
	
    // sum of the communication cost depend of new deployment
    
    public double newSOC(int node,int cloud,ArrayList<Integer> parent){
    	double sum=0;
    	  double computCost=cpucost[node][cloud];
          sum+=computCost;
          for(int a=0;a<parent.size();a++){
              int singleNode=parent.get(a);
              int parentCloud= isoccupied(singleNode);
              if(parentCloud==-1){
                  return -1;
              }else{
                  sum+=communicationCost(singleNode,node,parentCloud,cloud);
              }
          }
          
    	return sum;
    }
    
 // final deployment
    private void setfianlDeploy(int node,int cloud){
    	
    	finaldeployment[node][cloud]=1;
    }
	
	 // when a node has lots of son
    private ArrayList<Object> checkCross(ArrayList<Integer> offSprings,int block,ArrayList<Integer> parents){
    	ArrayList<Object> deploySet=new ArrayList<Object>();
    	double min=Integer.MAX_VALUE;
    	int cloud=0;
    	// put all nodes in one cloud
    	for(int a=0;a<ccost.length;a++){
    		boolean isValid=false;
    		if(checking.allowedDeploy(block, a)){
    			isValid=true;
    			for(int i=0;i<offSprings.size();i++){
    				int node=offSprings.get(i);
    				if(!checking.allowedDeploy(node, a)){
    					isValid=false;
    					break;
    				}
    			}
    		}
    		
    		if(isValid){
    			double comCost=thecommunication(parents,block,a);
    			double storageCost=thestorage(parents,block,a);
    			double costoffSprings=0;
    			for(int h=0;h<offSprings.size();h++){
    				int offNode=offSprings.get(h);
    				costoffSprings+=cpucost[offNode][a];
    			}
    			if(min>comCost+costoffSprings+storageCost){
    				min=comCost+costoffSprings+storageCost;
    				cloud=a;
    			}
    		}
    	}
    	
    //	System.out.println(cloud);
    	
    	double predeploycost=SOC.get(block);
    	for(int f=0;f<offSprings.size();f++){
    		int offNode=offSprings.get(f);
    		predeploycost+=SOC.get(offNode);
    	}
    	 
    	if(min<predeploycost){
    	//	deploySet=(ArrayList<Integer>) offSprings.clone();
    		offSprings.add(block);
    		deploySet.add(offSprings);
    		deploySet.add(cloud);
    		deploySet.add(min);
    	}
    	
    	return deploySet;
    }

	
    private ArrayList<Object> isCross(ArrayList<Integer> offSprings){
    	double max=Integer.MIN_VALUE;
    	int son=0;
    	ArrayList<Object> deploySet=new ArrayList<Object>();
    	for(int a=0;a<offSprings.size();a++){
    		int node=offSprings.get(a);
    		double SOCcost=SOC.get(node);
    		if(max<SOCcost){
    			max=SOCcost;
    			son=node;
    		}
    	}
    	
    	ArrayList<Integer> siblingNode=getParents(son);
    	ArrayList<Integer> UDsiblingNode=unDeploySibling(siblingNode);
    	// check the siblingNode's parent's nodes are all deployed
    	if(isDeployed(UDsiblingNode)){
    		double min=Integer.MAX_VALUE;
        	int cloud=0;
        	for(int a=0;a<ccost.length;a++){
        		boolean isValid=false;
        		if(checking.allowedDeploy(son, a)){
        			isValid=true;
        			for(int i=0;i<UDsiblingNode.size();i++){
        				int parentNode=UDsiblingNode.get(i);
        				if(!checking.allowedDeploy(parentNode, a)){
        					isValid=false;
        					break;
        				}
        			}
        			if(isValid){
        				if(min>miniCost(son,UDsiblingNode,a)){
        					min=miniCost(son,UDsiblingNode,a);
        					cloud=a;
        							
        				}
        			}
        		}
        	}
        	
        	double currentCost=parentCost(UDsiblingNode);
        	if(min<(currentCost+max)){
        	//	deploySet=(ArrayList<Integer>) UDsiblingNode.clone();
        		UDsiblingNode.add(son);
        		deploySet.add(UDsiblingNode);
        		deploySet.add(cloud);
        		deploySet.add(min);
        	}
    	}
    	
    	return deploySet;
    }
	
    
    private ArrayList<Integer> unDeploySibling(ArrayList<Integer> siblingNode){
    	ArrayList<Integer> unDeploySi=new ArrayList<Integer>();
    	for(int a=0;a<siblingNode.size();a++){
    		int node=siblingNode.get(a);
    		if(isoccupied(node)==-1){
    			unDeploySi.add(node);
    		}
    	}
    	return unDeploySi;
    }
	
    // check the siblingNode's parent's nodes are all deployed
    private boolean isDeployed(ArrayList<Integer> UDsiblingNode){
    	boolean allDeployed=true;
    	for(int a=0;a<UDsiblingNode.size();a++){
    		ArrayList<Integer> parentNodes=getParents(UDsiblingNode.get(a));
    		if(!deployCheck(parentNodes,UDsiblingNode)){
    			allDeployed=false;
    			break;
    		}
    	}
    	
    	return allDeployed;
    }
    
    private double miniCost(int son, ArrayList<Integer>  siblingNode, int cloud){
    	double totalcost=cpucost[son][cloud];
    	for(int a=0;a<siblingNode.size();a++){
    		int node=siblingNode.get(a);
    		if(!getParents(node).isEmpty()){
    			double cost=thecommunication(getParents(node),node,cloud);
    			double storecost=thestorage(getParents(node),node,cloud);
    			totalcost+=cost+storecost;
    		}
    		totalcost+=cpucost[node][cloud];
    	}
    
    	return totalcost;
    }
    
    private double thestorage(ArrayList<Integer> parents,int node,int nodeCloud){
    	double cost=0;
    	for(int a=0;a<parents.size();a++){
    		int parentNode=parents.get(a);
    		int parentCloud=isoccupied(parentNode);
    		if(parentCloud==-1){
    			return 0;
    		}
    		cost+=storageCost(parentNode,node,parentCloud,nodeCloud);
    	}
    	
    	return cost;
    }
    
    // communication cost with node's parent nodes
    private double thecommunication(ArrayList<Integer> parents,int node,int nodeCloud){
    	double cost=0;
    	for(int a=0;a<parents.size();a++){
    		int parentNode=parents.get(a);
    		int parentCloud=isoccupied(parentNode);
    		if(parentCloud==-1){
    			return 0;
    		}
    		cost+=communicationCost(parentNode,node,parentCloud,nodeCloud);
    	}
    	
    	return cost;
    }
    
    private double parentCost(ArrayList<Integer> siblingNode){
    	double total=0;
    	for(int a=0;a<siblingNode.size();a++){
    		int node=siblingNode.get(a);
    		total+=SOC.get(node);
    	}
    	return total;
    }
    
    // check the set of node is deployed or its parents include in the set
    private boolean deployCheck(ArrayList<Integer> parentNodes,ArrayList<Integer> UDsiblingNode){
    	boolean isDeployed=true;
    	for(int a=0;a<parentNodes.size();a++){
    		if(isoccupied(parentNodes.get(a))==-1){
    			if(!UDsiblingNode.contains(parentNodes.get(a))){
    				isDeployed=false;
        			break;
    			}
    		}
    	}
    	return isDeployed;
    }
    
    
    // check the set of node is deployed 
    private boolean deployCheck(ArrayList<Integer> parentNodes){
    	boolean isDeployed=true;
    	for(int a=0;a<parentNodes.size();a++){
    		if(isoccupied(parentNodes.get(a))==-1){
    				isDeployed=false;
        			break;
    		}
    	}
    	return isDeployed;
    }
    
    // get offspring node
    private ArrayList<Integer> getOffSpring(int node){
    	ArrayList<Integer> offSpring=new ArrayList<Integer>();
    	for(int i=0;i<workflow.length;i++){
    		if(workflow[node][i]>0){
    			if(!offSpring.add(i)){
    				offSpring.add(i);
    			}
    		}
    	}
    	return offSpring;
    }
    
    private int isoccupied(int node){
      	 for(int a=0;a<finaldeployment[node].length;a++){
   	            if(finaldeployment[node][a]==1){
   	                return a;
   	            }
   	        }
   	        return -1;
      }
    
    private void preDeploy(){

   	// no-descend order sort the nodes
	        LinkedList<Integer> queue = getNodes();
	        while (!queue.isEmpty()) {

	            for (int i = 0;i<queue.size();i++) {
	            	
	                int block = queue.get(i);
	             
	                if (getParents(block).isEmpty()) {
	                	
	                    int cloud = getCloud(block);
	                    setDeployment(block, cloud);
	                    queue.remove((Object)block);
	                    double SOCcost=cpucost[block][cloud];
	                    SOC.put(block, SOCcost);
	          //          total += SOCcost;

	                } else {
	                	
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
	     //   print(deployment);
   }
    
 // get cheapest computing cloud
    private int getCloud(int node){
        int cloud=0;
        double min=0;
        for(int a=0;a<cpucost[node].length;a++){
            if(min==0 &&checking.allowedDeploy(node, a)){
                min=cpucost[node][a];
                cloud=a;
            }else{
                if(min>cpucost[node][a] &&checking.allowedDeploy(node, a)){
                    min=cpucost[node][a];
                    cloud=a;
                }
            }

        }
        return cloud;
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
    LinkedList<Integer> getNodes(){
    	 LinkedList<Integer> queue=new  LinkedList<Integer>();
    	 for(int a=0;a<workflow.length;a++){
    		 queue.add(a);
    	 }
    	 
    	 return queue;
    }
    
    private void print(int[][] matrix){
      	 for(int h=0;h<matrix.length;h++){
   	            for(int f=0;f<matrix[h].length;f++){
   	                System.out.print(matrix[h][f]+",");
   	            }
   	            System.out.println("");
   	        }
      }
    
    public int [][] getDeployment(){
    	return finaldeployment;
    }
    // calculate the final cost
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
   /* private void  startNode(){
    	for (int a=0; a<workflow.length; a++){
    		boolean isroot=true;
    		for(int i=0;i<workflow.length;i++){
    			if(workflow[i][a]>0){
    				isroot=false;
    				break;
    			}
    		}
    		if(isroot==true){
    			root.add(a);
    		}
    	}
    }*/
    
}
