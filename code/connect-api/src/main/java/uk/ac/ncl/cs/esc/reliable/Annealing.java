package uk.ac.ncl.cs.esc.reliable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import uk.ac.ncl.cs.esc.read.RandomInt;
import uk.ac.ncl.cs.esc.read.WorkflowModel;
import uk.ac.ncl.cs.esc.security.Security;



public class Annealing {

	int sNum;
	double slice;
	double RELStand;
	int M=100;
	//74 hours the reliability rate is 0.8
	 double [][] cpucost;
		double[][] workflow;
		int[][] dataSecurity;
//		int [][] deployment;
		double [] cloudStartTime;
//		double [] cloudUsedTime;
		double [] blockExecutionTime;
		double failRate=0.3;
		double [] criticalPath;
		ArrayList<Integer> root;
		Security checking;
		double[][] storageTime;
	    double[] storageCost;
	    double [][] ccost;
	    int [][] ssecurity;
	    int [] cloud;
	    // to store the valid deployments in decent order of cost 
	    LinkedList<deployment> deployments=new LinkedList<deployment>();
	    static double costFront;
	    static double costEntropy;
	    static double entropyFront;
	    static double entropyCost;
	    int [][] entropydeployment;
	    int[][] costdeplyment;
	    int [][] deployment;
	//    int [][] finalDeployment;
	    WorkflowModel getInfo;
	 // here for kanpasack 
	 // memoization table size
	    // greedy optimal
	   double standEntropy[];
	   double standVarEN[];
	   double standVarCost[];
	   double currentVarCost;
	   //result calculate
	   
	   static double finalEntropy;
	   static double finalCost;
	   HashMap<Integer,LinkedList<Integer>> enRanking;
	   HashMap<Integer,LinkedList<Integer>> costRanking;
	   
	   public Annealing(WorkflowModel getInfo){
	 	   this.getInfo=getInfo;
	 			this.workflow=getInfo.getWorkflow();
	 			 this.ccost=getInfo.getCcost();
	 				this.blockExecutionTime=getInfo.blockExecutionTime();
	 				this.checking=new Security(getInfo);
	 				this.cpucost=getInfo.getCpucost();
	 				this.storageCost=getInfo.getStorageCost();
	 		        this.storageTime=getInfo.getStorageTime();
	 		        this.cloudStartTime=getInfo.getCloudStartTime();
	 		        this.ssecurity=getInfo.getSsecurity();
	 		        this.dataSecurity = getInfo.getDataSecurity();
	 		        this.cloud = getInfo.getCloud();
//	 			    deployment=new int[workflow.length][cloudStartTime.length];
	 			    this.criticalPath=getInfo.getcriticalPath();
	 		//	    print(criticalPath);
	 			//    System.out.println(" ");
	 			    this.root=getInfo.getRootNodes();
	 			    /***************/ 
	 				this.sNum=workflow.length;
	 				
	 				
	 				standEntropy=new double[workflow.length];
	 				// last position is for average
	 				standVarEN=new double[workflow.length+1];
	 				standVarCost=new double[workflow.length+1];
	 				this.enRanking=new HashMap<Integer,LinkedList<Integer>>();
	 				
	    }
	public void annealingAlogirthm(){
		
		// used to store all selected case
		setCostFront();
		setEntropyFront();
		setSlice();
		setStandEntropy();
		/*double retioCost=costFront/costEntropy;
		double retioEN=entropyCost/entropyFront;*/
		if(slice==0){
			deployment entropyDep = new deployment(costdeplyment,costFront,
					costEntropy);
			deployments.addFirst(entropyDep);
		} else {
			List<List<Integer>> visited = new ArrayList<List<Integer>>();
			List<Integer> first = converser(entropydeployment);
			visited.add(first);
			deployment entropyDep = new deployment(entropydeployment,
					entropyCost, entropyFront);
			deployments.addFirst(entropyDep);
			
			List<List<Integer>> possible = getPossbileClouds();
			deployment=new int[workflow.length][ccost.length];
			double newEntropy = Double.MAX_VALUE;
			CreatRankingEN(possible);
			calculateCostVar(possible);
			calcualteVarEN(possible);
		//	print(standVarCost);
			deployment=new int[workflow.length][ccost.length];
			costRanking=new HashMap<Integer,LinkedList<Integer>>();
			deployment(possible);
			newEntropy=calculateEntropy(deployment);
			List<Integer> newdep=converser(deployment);
			if(newEntropy<=(entropyFront+slice) && !isVisited(visited, newdep)){
				double cost=theCost(root, 0,
						new ArrayList<Integer>(), deployment);
				deployment dp = new deployment(deployment,
						cost, newEntropy);
				addDeployment(dp);
			}
			
			int count=deployments.size();
			while(count<=M){
				
				boolean selected=true;
				int loop=0;
				while(newEntropy>(entropyFront+slice) || isVisited(visited, newdep) ){
					
					int per = RandomInt.randomInt(0, newdep.size()-1);
					LinkedList<Integer> cloudRank=costRanking.get(per);
			//		int get= RandomInt.randomInt(0, (cloudRank.size()-1));
					int get=benfordGenerate(cloudRank.size());
					int cloud=cloudRank.get(get-1);
					newdep.set(per, cloud);
					deployment=new int[workflow.length][ccost.length];
					costRanking=new HashMap<Integer,LinkedList<Integer>>();
					deployment = coveersetoMatrix(newdep);
					CostVarience( deployment, possible);
					newEntropy = calculateEntropy(deployment);
					
					if(loop==100000){
						selected=false;
						break;
					}
					loop++;
				}
				
				if (selected == true) {
					double cost = theCost(root, 0, new ArrayList<Integer>(),
							deployment);
					deployment dp = new deployment(deployment, cost, newEntropy);
					addDeployment(dp);
				}
				count++;
				
			}

		}
		deployment temp = deployments.getLast();
		deployment first = deployments.getFirst();
		System.out.println(first.cost);
		System.out.println(temp.cost);
//		this.finalCost = temp.getCost();
//		this.finalEntropy = temp.getEntropy();
//		System.out.println(finalCost);
//		System.out.println(finalEntropy);
//		System.out.println(costFront);
//		System.out.println(costEntropy);
//		System.out.println(entropyCost);
//		System.out.println(entropyFront);
		System.out.println(deployments.size());
	}
	
	public int[][] getDeloyment(){
		deployment temp = deployments.getLast();
		
		return temp.getDeployment();
	}
	
	boolean isVisited(List<List<Integer>> visted,List<Integer> newdep){
		if(newdep.isEmpty()){
			return true;
		}
		 boolean ignore = false;
         for(List<Integer> exist:visted){
             for(int i =0;i<exist.size();i++){
                 if(exist.get(i) != newdep.get(i)) break;
                 if(i == exist.size() -1){
                     ignore = true;
                 }
             }
         }
         if(!ignore){
        	 visted.add((ArrayList<Integer>)((ArrayList<Integer>)newdep).clone());
        	 return false;
         }
     
		return true;
	}
	void addDeployment(deployment dp){
		double cost=dp.getCost();
		for (int i = 0; i < deployments.size(); i++) {
			deployment origin = deployments.get(i);
	//		System.out.println(cost <= origin.getCost());
			if (cost <= origin.getCost()) {
				deployments.add(i + 1, dp);
				break;
			}
		}
	}
    void setStandEntropy(){
    	List<Integer> entropyfront = converser(entropydeployment);
    	List<Integer> costfront = converser(entropydeployment);
    	for(int a=0;a<costfront.size();a++){
    		int serviceName=a;
    		double value=(serviceEntropy(entropyfront.get(serviceName), serviceName)+serviceEntropy(costfront.get(serviceName), serviceName))/2;
    		standEntropy[serviceName]=value;
    	}
    	
    }
	void setSlice(){
	//	System.out.println(costEntropy-entropyFront);
		slice=((double)(costEntropy-entropyFront))/2;
		
	}
	
	void setCostFront(){
		 frontCost n= new frontCost(getInfo);
		 this.costFront=n.NCFAlgorithm();
		 this.costdeplyment=n.getDeployment();
		 this.costEntropy=calculateEntropy(costdeplyment);
	}
	void setEntropyFront(){
		frontReliability test=new frontReliability(getInfo);
		 test.RELdeploy();
		 this.entropyCost=test.result();
		 this.entropydeployment=test.getDeployment();
		 this.entropyFront=calculateEntropy(entropydeployment);
	}
	
	 public double calculateEntropy(int [][] deployment){
	   	 
	  	  double entropy = 0;
	  	  for(int i=0; i<deployment.length;i++ ){
	  		  for(int a=0;a<deployment[i].length;a++){
	  			 if(deployment[i][a]==1){
	  				 int cloud=a;
	  				 int block=i;
	 // 				 System.out.println(cloud);
	  				 entropy+= serviceEntropy(cloud,block);
	  			 }
	  			 
	  		  }
	  	  }
	  	  return entropy;
	    }
	   int benfordGenerate(int num){
		  	  Random r = new Random();
		  	  int order = 0;
		  	  double max=Math.log10(1+1/1);
		  	  double min=Math.log10(1+1.0/num);
//		  	  System.out.println( max);
//		    	  System.out.println( min);
		  	  double ranValue=min+(max-min)*r.nextDouble();
//		    	  System.out.println(ranValue);
		  	  double value=1/(Math.pow(10,ranValue)-1);
		  	 
		  	//  System.out.println((int) Math.round(value));
		  	  order= (int) Math.round(value);
		  	  return order;
		    }
	 double serviceReliability(int cloud, int block){
			double cloudTime=cloudStartTime[cloud];
			double exeTime=criticalPath[block];
			return Math.exp(-failRate*(cloudTime+exeTime));
		}
		double serviceEntropy(int cloud, int block){
			double re=serviceReliability(cloud,block);
			return (-re* Math.log(re));
		}
	 List<List<Integer>> getPossbileClouds(){
   	  int maxLvl = Integer.MIN_VALUE;
   	  int minLvl= Integer.MAX_VALUE;
         final List<List<Integer>> sortedPlatform = new ArrayList<List<Integer>>();
         //find max security lvl
         for(int i =0;i<cloud.length;i++){
             final int current = cloud[i];
             if(current<minLvl){
                 minLvl = current;
             }
             if(current>maxLvl){
                 maxLvl = current;
             }
         }

         //init
         for(int i =0;i<maxLvl+1;i++){
             sortedPlatform.add(null);
         }
         // order and cluster clouds by its security
         for(int i =0;i<cloud.length;i++){
             final int current = cloud[i];
             List<Integer> list = sortedPlatform.get(current);
             if(null == list){
                 List<Integer> temp = new ArrayList<Integer>();
                 temp.add(i);
                 sortedPlatform.set(current,temp);
             } else {
                 list.add(i);
             }
         }
   	  
   	  final List<List<Integer>> possibleDeploy = new ArrayList<List<Integer>>();
         for(int i=0;i<ssecurity.length;i++){
             List<Integer> list = new ArrayList<Integer>();
             int min = ssecurity[i][1]; //location
             //int min = minLvl;
             int max = maxLvl;
             // consider data security
             int dataMin = calMinDataSecurity(i);
             if(min<dataMin) min = dataMin;

             while (min<=max){
                 if(sortedPlatform.get(min) != null){
                     list.addAll(sortedPlatform.get(min));
                 }
                 min++;
             }
             possibleDeploy.add(list);
         }
         return possibleDeploy;
     }
	 
	   private int calMinDataSecurity(int pos){
	          int result = -1;
	          for(int i = 0;i<this.workflow.length;i++){
	              if(this.workflow[pos][i] != -1){
	                  if(result< this.dataSecurity[pos][i]){
	                      result = this.dataSecurity[pos][i];
	                  }
	              }
	              if(this.workflow[i][pos] != -1){
	                  if(result<this.dataSecurity[i][pos]){
	                      result = this.dataSecurity[i][pos];
	                  }
	              }
	          }
	          return result;
	      }

		int [][] coveersetoMatrix(List<Integer> newdep){
			int [][] maxtrix=new int[workflow.length][storageCost.length];
			for(int a=0;a<newdep.size();a++){
				int cloud=newdep.get(a);
				maxtrix[a][cloud]=1;
			}
			return maxtrix;
		}
		List<Integer> converser(int [][] dep){
			List<Integer> list=new ArrayList<Integer>();
			for(int a=0;a<dep.length;a++){
				for(int i=0;i<dep[a].length;i++){
					if(dep[a][i]==1){
						list.add(i);
					}
				}
			}
			return list;
		}
		
	void CostVarience(int[][] deployment,List<List<Integer>> possible) {
		LinkedList<Integer> queue = getNodes();
		double ava=0;
		while (!queue.isEmpty()) {
			for (int i = 0; i < queue.size(); i++) {
				int block = queue.get(i);
				ArrayList<Integer> parents = getParents(block);
				if (parents.isEmpty()) {
					LinkedList<Integer> costOrder = cloudOrderCost(parents,
							block, possible);
					standVarCost[block]=currentVarCost;
					ava+=standVarCost[block];
					costRanking.put(block, costOrder);
					queue.remove((Object) block);

				} else {
					if (!getUndeploy(parents)) {
						LinkedList<Integer> costOrder = cloudOrderCost(parents,
								block, possible);
						standVarCost[block]=currentVarCost;
						ava+=standVarCost[block];
						costRanking.put(block, costOrder);
						queue.remove((Object) block);
					}
				}
			}
		}
		standVarCost[possible.size()]=ava/possible.size();
	}
		
	//pre-run deployment	
void calculateCostVar(List<List<Integer>> possible){
	int ava=0;
	LinkedList<Integer> queue = getNodes();
	while (!queue.isEmpty()) {
		for (int i = 0; i < queue.size(); i++) {
			int block = queue.get(i);
			ArrayList<Integer> parents = getParents(block);
			if (parents.isEmpty()) {
				int cloud = 0;
				LinkedList<Integer> costOrder = cloudOrderCost(parents,
						block, possible);
				standVarCost[block]=currentVarCost;
				ava+=standVarCost[block];
				
				recordDeployment(block, costOrder.get(0));
				queue.remove((Object) block);
			} else {
				 if (!getUndeploy(parents)) {
					 int cloud = 0;
						LinkedList<Integer> costOrder = cloudOrderCost(parents,
								block, possible);
						standVarCost[block]=currentVarCost;
						ava+=standVarCost[block];
						recordDeployment(block, costOrder.get(0));
						queue.remove((Object) block);
				 }
			}

		}
	}
	 standVarCost[possible.size()]=ava/possible.size();	
}		
	


// Heretic way to search deployment	   
	void deployment(List<List<Integer>> possible) {

		int ava = 0;
		LinkedList<Integer> queue = getNodes();

		double credit = 0;
		while (!queue.isEmpty()) {
			for (int i = 0; i < queue.size(); i++) {
				int block = queue.get(i);
				ArrayList<Integer> parents = getParents(block);
				if (parents.isEmpty()) {
					int cloud = 0;
					LinkedList<Integer> costOrder = cloudOrderCost(parents,
							block, possible);
					costRanking.put(block, costOrder);
					ava += currentVarCost;
					standVarCost[block] = currentVarCost;
					int bestCloud = costOrder.get(0);
					double en = serviceEntropy(bestCloud, block);
					if ((currentVarCost / standVarCost[possible.size()]) > 1) {
						cloud = bestCloud;
						recordDeployment(block, cloud);
						queue.remove((Object) block);
						credit += standEntropy[block] - en;
					} else {
						for (int a = 0; a < costOrder.size(); a++) {
							double temp = credit;
							int cloudName = costOrder.get(a);
							double entropy = serviceEntropy(cloudName, block);
							temp += standEntropy[block] - entropy;
							if (temp >= 0) {
								cloud = cloudName;
								recordDeployment(block, cloud);
								queue.remove((Object) block);
								credit += standEntropy[block] - entropy;
								break;
							}

							if (temp < 0 && a == costOrder.size() - 1) {
								LinkedList<Integer> enCloud = enRanking
										.get(block);
								cloudName = enCloud.get(0);
								entropy = serviceEntropy(cloudName, block);
								cloud = cloudName;
								recordDeployment(block, cloud);
								// System.out.println(a);
								credit += standEntropy[block] - entropy;
								queue.remove((Object) block);
							}
						}

					}
				} else {

					if (!getUndeploy(parents)) {
						int cloud = 0;
						LinkedList<Integer> costOrder = cloudOrderCost(parents,
								block, possible);
						costRanking.put(block, costOrder);
						ava += currentVarCost;
						standVarCost[block] = currentVarCost;
						int bestCloud = costOrder.get(0);
						double en = serviceEntropy(bestCloud, block);
						if ((currentVarCost / standVarCost[possible.size()]) > 1) {
							cloud = bestCloud;
							recordDeployment(block, cloud);
							queue.remove((Object) block);
							credit += standEntropy[block] - en;
						} else {
							for (int a = 0; a < costOrder.size(); a++) {
								double temp = credit;
								int cloudName = costOrder.get(a);
								double entropy = serviceEntropy(cloudName,
										block);
								temp += standEntropy[block] - entropy;
								if (temp >= 0) {
									cloud = cloudName;
									recordDeployment(block, cloud);
									queue.remove((Object) block);
									credit += standEntropy[block] - entropy;
									break;
								}

								if (temp < 0 && a == costOrder.size() - 1) {
									LinkedList<Integer> enCloud = enRanking
											.get(block);
									cloudName = enCloud.get(0);
									entropy = serviceEntropy(cloudName, block);
									cloud = cloudName;
									recordDeployment(block, cloud);
							//		 System.out.println(a);
									credit += standEntropy[block] - entropy;
									queue.remove((Object) block);
								}
							}

						}
					}
				}
			}
		}

		standVarCost[possible.size()] = ava / possible.size();
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
    

    
    double variance(double[] value){
    	double var=0;
    	double ave=0;
    	for(int a=0;a<value.length;a++){
    		ave+=value[a];
    	}
    	ave=ave/value.length;
    	for(int i=0;i<value.length;i++){
    		var+=Math.pow((ave-value[i]), 2);
    	}
    	return var/value.length;
    }
    
    
    double variance(double[] value,LinkedList<Integer> list){
    	double var=0;
    	double ave=0;
    	for(int a=0;a<list.size();a++){
    		ave+=value[a];
    	}
    	ave=ave/list.size();
    	for(int i=0;i<list.size();i++){
    		var+=Math.pow((ave-value[i]), 2);
    	}
    	return var/list.size();
    }
    
    
    
	LinkedList<Integer> cloudOrderCost(ArrayList<Integer> parent, int block,
			List<List<Integer>> possible) {
		LinkedList<Integer> order = new LinkedList<Integer>();
		List<Integer> servicesClouds = possible.get(block);
		double[] value = new double[cloud.length];
		if (parent.isEmpty()) {
			for (int i = 0; i < servicesClouds.size(); i++) {
				int cloudName = servicesClouds.get(i);
				double cost = cpucost[block][cloudName];
				if (order.isEmpty()) {
					order.addFirst(cloudName);
					value[cloudName] = cost;
				} else {
					for (int h = 0; h < order.size(); h++) {
						int storedCloud = order.get(h);
						if (cost <= value[storedCloud]) {
							order.add(h, cloudName);
							value[cloudName] = cost;
							break;
						}
						if (h == order.size() - 1 && cost > value[storedCloud]) {
							order.addLast(cloudName);
							value[cloudName] = cost;
							break;
						}
					}
				}
			}
		} else {
			for (int i = 0; i < servicesClouds.size(); i++) {
				int cloudName = servicesClouds.get(i);
				double cost=SOC(block,cloudName,parent);
				if (order.isEmpty()) {
					order.addFirst(cloudName);
					value[cloudName] = cost;
				} else {
					for (int h = 0; h < order.size(); h++) {
						int storedCloud = order.get(h);
						if (cost <= value[storedCloud]) {
							order.add(h, cloudName);
							value[cloudName] = cost;
							break;
						}
						if (h == order.size() - 1 && cost > value[storedCloud]) {
							order.addLast(cloudName);
							value[cloudName] = cost;
							break;
						}
					}
				}
			}
		}
		currentVarCost=variance(value,order);
	//	System.out.println(var);
		return order;
	}
    
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
    
    private int returnDeployedCloud(int node){
        for(int a=0;a<deployment[node].length;a++){
            if(deployment[node][a]==1){
                return a;
            }
        }
        return -1;
    }
    
  
    
    void calcualteVarEN(List<List<Integer>> possible){
    	double ava=0;
		for (int a = 0; a < possible.size(); a++) {
			// the last position is the value of the split position
			int serviceName = a;
			List<Integer> servicesClouds = possible.get(a);
			double[] value = new double[servicesClouds.size()];
			for (int i = 0; i < servicesClouds.size(); i++) {
				int cloudName = servicesClouds.get(i);
				// double
				// cost=getComputCost(cloudName,serviceName)+comAvaCost(cloudName,serviceName);
				double enp = serviceEntropy(cloudName, serviceName);
				// double mi=Math.abs(enp-standEntropy);
				value[i] = enp;

		//		enranking.put(serviceName, list);
				
			}
			double var=variance(value);
			ava+=var;
			standVarEN[serviceName]=var;
			
		}
		standVarEN[possible.size()]=ava/possible.size();
    }
    
	void CreatRankingEN(List<List<Integer>> possible) {
		for (int a = 0; a < possible.size(); a++) {
			// the last position is the value of the split position
			LinkedList<Integer> list = new LinkedList<Integer>();
			int serviceName = a;
			List<Integer> servicesClouds = possible.get(a);
			double[] value = new double[cloud.length];
			for (int i = 0; i < servicesClouds.size(); i++) {
				int cloudName = servicesClouds.get(i);
				// double
				// cost=getComputCost(cloudName,serviceName)+comAvaCost(cloudName,serviceName);
				double enp = serviceEntropy(cloudName, serviceName);
				// double mi=Math.abs(enp-standEntropy);
				if (list.isEmpty()) {
					list.addFirst(cloudName);
					value[cloudName] = enp;
				} else {
					for (int h = 0; h < list.size(); h++) {
						int storedCloud = list.get(h);
						if (enp <= value[storedCloud]) {
							list.add(h, cloudName);
							value[cloudName] = enp;
							break;
						}
						if (h == list.size() - 1 && enp > value[storedCloud]) {
							list.addLast(cloudName);
							value[cloudName] = enp;
							break;
						}
					}
				}

			}
			enRanking.put(serviceName, list);
		}
	}
    
    private void recordDeployment(int node,int cloud){
        deployment[node][cloud]=1;
    }
  
    LinkedList<Integer> getNodes(){
   	 LinkedList<Integer> queue=new  LinkedList<Integer>();
   	 for(int a=0;a<workflow.length;a++){
   		 queue.add(a);
   	 }
   	 
   	 return queue;
   }
    
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
    
    private double theCost(ArrayList<Integer> start, double cost,ArrayList<Integer> isVisited,int [][] deployment){
	 	   
      	if(start.isEmpty()){
      		return cost;
      	}else{
      		ArrayList<Integer> offSpring=new ArrayList<Integer>();
      
      		for(int a=0;a<start.size();a++){
      		
      			int startNode=start.get(a);
      			int startCloud=isoccupied(startNode,deployment);
      			if(!isVisited.contains(startNode)){
      	//			System.out.println("Node:"+startNode);
      				cost+=cpucost[startNode][startCloud];
      	//			System.out.println(cpucost[startNode][startCloud]);
      				isVisited.add(startNode);
      			
      			// get nodes' offspring
      			for(int i=0;i<workflow.length;i++){
      				if(workflow[startNode][i]>0){
      				
      					int endNode=i;
      					int endCloud=isoccupied(endNode,deployment);
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
      		return theCost(new ArrayList<Integer>(offSpring),cost,isVisited,deployment);
      	}
      	
      }
    
    private int isoccupied(int node, int[][] deployment){
     	 for(int a=0;a<deployment[node].length;a++){
  	            if(deployment[node][a]==1){
  	                return a;
  	            }
  	        }
  	        return -1;
     }
    
	public static void main(String args[]) throws ClassNotFoundException,
			IOException {
		String url = "/Users/zhenyuwen/Documents/workspaceproject/Multi-Object/";

		int x = 5;
		for (int y = 2; y <= 30; y++) {
			double resultEn = 0;
			double resultCost = 0;

			// double frontcost=0;
			// double frontcosten=0;
			// double frontentr=0;
			double frontentren = 0;
			for (int i = 0; i < 10; i++) {
				WorkflowModel workflowModel = WorkflowModel.read(url
						+ "newmodel" + x + "" + y + "" + i);
				Annealing t = new Annealing(workflowModel);
				// t.setCostFront();
				// t.setEntropyFront();
				// t.setSlice();
				t.annealingAlogirthm();
				// System.out.println("block: "+y+i);
				 resultEn+=finalEntropy;
			//	 resultCost+=finalCost;
				// frontcost and frontcost entropy
				// frontcost+=costFront;
				// frontcosten+=costEntropy;
				// frontentropy and frontentropen
				// frontentr+=entropyFront;
				// frontentren+= entropyCost;

			}

			 System.out.println(resultEn/10);
			// System.out.println(resultCost/10);
			// System.out.println("block: "+y);

			// System.out.println(frontcost/10);
			// System.out.println(frontcosten/10);

			// System.out.println(frontentr/10);
			// System.out.println(frontentren/10);

		}
	}
	
	void print(double [] path){
		for(int a=0;a<path.length;a++){
			System.out.println(path[a]+" ");
		}
	}
    
	
	void print(int[][] matrix){
		for(int a=0;a<matrix.length;a++){
			for(int i=0;i<matrix[a].length;i++){
				System.out.print(matrix[a][i]+" ");
			}
			System.out.println(" ");
		}
	}
    class deployment{
  	  int [][] deployment;
  	  double cost;
  	  double entropy;
  	  deployment (int [][] deployment,double cost,double entropy){
  		  this.deployment=deployment;
  		  this.cost=cost;
  		  this.entropy=entropy;
  	  }
  	  
  	  double getCost(){
  		  return cost;
  	  }
  	  double getEntropy(){
  		  return entropy;
  	  }
  	  int [][] getDeployment(){
  		  return deployment;
  	  }
    }
	
}
