package uk.ac.ncl.cs.esc.newpartitiontool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ncl.cs.esc.read.WorkflowTemplate;
import uk.ac.ncl.cs.esc.security.Security;


// this class is to deploy block in the running time. 
public class DNCF {
	double[][] workflow;
    double [][] ccost;
    double [][] cpucost;
//    double[][] storageTime;
//    double[] storageCost;
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
    double total;
  //  ArrayList<Integer> queue=new ArrayList<Integer>();
    
    public DNCF(WorkflowTemplate getInfo, HashMap<Integer,ArrayList<Object>> blockInputs){
    	this.workflow=getInfo.getWorkflow();
        this.ccost=getInfo.getCcost();
        this.cpucost=getInfo.getCpucost();
 //       this.storageCost=getInfo.getStorageCost();
//        this.storageTime=getInfo.getStorageTime();
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
    
	public int[][] DyNCF() {
		preDeployment();
		ArrayList<Integer> queue = getQueue();
		while (!queue.isEmpty()) {
			for (int i = 0; i < queue.size(); i++) {
				int block = queue.get(i);
				ArrayList<Integer> parentNodes = getParents(block);
				if (root.contains(block) || deployCheck(parentNodes)) {
					ArrayList<Integer> offSprings = getOffSpring(block);
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
		
		 this.total=theCost(changeFormat(root),0,new ArrayList<Integer>());
	//	 print(finaldeployment);
		return finaldeployment;
	}
	
	
     public double getTotalCost(){
	    	return total;
	    }
	ArrayList<Integer> changeFormat(Set<Integer> r){
		ArrayList<Integer> b=new ArrayList<Integer>();
		Iterator<Integer> set=r.iterator();
		while(set.hasNext()){
			int block=set.next();
			b.add(block);
		}
		return b;
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
//    					double storageCost=storageCost(startNode,endNode,startCloud,endCloud);
    			//		System.out.println(comCost);
 //   					cost+=comCost+storageCost;
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
    			
    			double pCost=0;
    			if(root.contains(block)){
    				ArrayList<Object> inputs=blockInputs.get(block);
    				for(int i=0;i<inputs.size();i++){
    	   	    		ArrayList<Object> singleInput=(ArrayList<Object>) inputs.get(i);
    	   	    		int inputCloud=(int) singleInput.get(0);
    	   	    		ArrayList<String> connect=(ArrayList<String>) singleInput.get(1);
    	   	    		double dataSize=Double.valueOf(connect.get(8));
    	   	    		double comCost=ccost[inputCloud][cloud]*dataSize;
    	   	    		pCost+=comCost;
    	   	    	}
    			}else{
    				double comCost=thecommunication(parents,block,a);
        			double storageCost=thestorage(parents,block,a);
        			pCost=comCost+storageCost;
    			}
    			
    			double costoffSprings=0;
    			for(int h=0;h<offSprings.size();h++){
    				int offNode=offSprings.get(h);
    				costoffSprings+=cpucost[offNode][a];
    			}
    			if(min>costoffSprings+pCost){
    				min=costoffSprings+pCost;
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
        				double temp=miniCost(son,UDsiblingNode,a);
        				if(min>temp){
        					min=temp;
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
    
    private double parentCost(ArrayList<Integer> siblingNode){
    	double total=0;
    	for(int a=0;a<siblingNode.size();a++){
    		int node=siblingNode.get(a);
    		total+=SOC.get(node);
    	}
    	return total;
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
    
    /*
     * if the SOCcost+the cost of each node is greater than the minimize cost of put all node in one valid cloud
     * put them in one cloud. otherwise, keep using SOC
     * */
    
    private double miniCost(int son, ArrayList<Integer>  siblingNode, int cloud){
    	double totalcost=cpucost[son][cloud];
    	for(int a=0;a<siblingNode.size();a++){
    		int node=siblingNode.get(a);
    		if(!getParents(node).isEmpty()){
    			double cost=thecommunication(getParents(node),node,cloud);
    			double storecost=thestorage(getParents(node),node,cloud);
    			totalcost+=cost+storecost;
    		}
    		
    		if(root.contains(node)){
    			ArrayList<Object> inputs=blockInputs.get(node);
    			for(int i=0;i<inputs.size();i++){
    	    		ArrayList<Object> singleInput=(ArrayList<Object>) inputs.get(i);
    	    		int inputCloud=(int) singleInput.get(0);
    	    		ArrayList<String> connect=(ArrayList<String>) singleInput.get(1);
    	    		double dataSize=Double.valueOf(connect.get(8));
    	    		double comCost=ccost[inputCloud][cloud]*dataSize;
    	    		totalcost+=comCost;
    	    	}
    		}
    		totalcost+=cpucost[node][cloud];
    	}
    
    	return totalcost;
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
    
    private double thestorage(ArrayList<Integer> parents,int node,int nodeCloud){
    	double cost=0;
    	for(int a=0;a<parents.size();a++){
    		int parentNode=parents.get(a);
    		int parentCloud=isoccupied(parentNode);
    		if(parentCloud==-1){
    			return 0;
    		}
  //  		cost+=storageCost(parentNode,node,parentCloud,nodeCloud);
    	}
    	
    	return cost;
    }
    
    private int isoccupied(int node){
      	 for(int a=0;a<finaldeployment[node].length;a++){
   	            if(finaldeployment[node][a]==1){
   	                return a;
   	            }
   	        }
   	        return -1;
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
    		// can be added in connection 
    //		double storeCost=dataSize*Double.valueOf(connect.get(9)) *storageCost[inputCloud];;
    		sum+=comCost;
    	}
    	return sum;
    }
    
// sum of the communication cost depend of new deployment
    
    public double newSOC(int node,int cloud,ArrayList<Integer> parent){
    	double sum=0;
    	  double computCost=cpucost[node][cloud];
          sum+=computCost;
          
          if(parent.isEmpty()){
        	  ArrayList<Object> inputs=blockInputs.get(node);
        	  for(int a=0;a<inputs.size();a++){
          		ArrayList<Object> singleInput=(ArrayList<Object>) inputs.get(a);
          		int inputCloud=(int) singleInput.get(0);
          		ArrayList<String> connect=(ArrayList<String>) singleInput.get(1);
          		double dataSize=Double.valueOf(connect.get(8));
          		double comCost=ccost[inputCloud][cloud]*dataSize;
          		// can be added in connection 
          //		double storeCost=dataSize*Double.valueOf(connect.get(9)) *storageCost[inputCloud];;
          		sum+=comCost;
          	}
          }else{
        	    for(int a=0;a<parent.size();a++){
                    int singleNode=parent.get(a);
                    int parentCloud= isoccupied(singleNode);
                    if(parentCloud==-1){
                        return -1;
                    }else{
                        sum+=communicationCost(singleNode,node,parentCloud,cloud);
 //                       sum+=storageCost(singleNode,node,parentCloud,cloud);
                    }
                }
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
  //              sum+=storageCost(singleNode,node,parentCloud,cloud);
            }
        }
        return sum;
    }
    
 // return the storage cost: source cloud storagecost* datasize* storagetime
    
   /* private double storageCost(int startNode,int endNode,int startCloud,int endCloud){
    	if(startCloud==endCloud){
    		return 0;
    	}else{
    		return workflow[startNode][endNode]*storageTime[startNode][endNode]*storageCost[startCloud];
    	}
    }*/
    
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
    
    // final deployment
    private void setfianlDeploy(int node,int cloud){
    	
    	finaldeployment[node][cloud]=1;
    }
    
    private void print(int[][] matrix){
      	 for(int h=0;h<matrix.length;h++){
   	            for(int f=0;f<matrix[h].length;f++){
   	                System.out.print(matrix[h][f]+",");
   	            }
   	            System.out.println("");
   	        }
      }
}
