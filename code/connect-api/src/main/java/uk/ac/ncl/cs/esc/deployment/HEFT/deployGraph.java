package uk.ac.ncl.cs.esc.deployment.HEFT;

import java.util.ArrayList;
import java.util.LinkedList;

public class deployGraph {
	
	public deployGraph(){
	
	}
	
	public void createOrder(ArrayList<Integer>offspringNodes,ArrayList<Object> thelinks,
			LinkedList<ArrayList<Integer>> order,ArrayList<Integer> visited){
		
		if(offspringNodes.isEmpty()){
			return;
		}else{
			order.add((ArrayList<Integer>)offspringNodes.clone());
			ArrayList<Integer> nodeCollection=new ArrayList<Integer>();
			for(int a=0;a<offspringNodes.size();a++){
				int offspring=offspringNodes.get(a);
				for(int i=0;i<thelinks.size();i++){
					ArrayList<Object> link=(ArrayList<Object>) thelinks.get(i);
					ArrayList<Integer> pLink=(ArrayList<Integer>) link.get(0);
					int source=pLink.get(0);
					int destination=pLink.get(1);
					if(destination==offspring){
						if(!visited.contains(source)){
							ArrayList<Integer> brothers=findBrothers(source,thelinks);
							if(isready(offspringNodes,brothers)){
								if(!nodeCollection.contains(source)){
									nodeCollection.add(source);
									visited.add(source);
								}
							}else{
								nodeCollection.add(offspring);
							}
						}
					}
				}
			}
			createOrder(nodeCollection,thelinks,order,visited);
		}
		
	}
	
	private ArrayList<Integer> findBrothers(int startNode,ArrayList<Object> thelinks){
		ArrayList<Integer> brother=new ArrayList<Integer>();
		for(int i=0;i<thelinks.size();i++){
			ArrayList<Object> link=(ArrayList<Object>) thelinks.get(i);
			ArrayList<Integer> pLink=(ArrayList<Integer>) link.get(0);
			int source=pLink.get(0);
			int destination=pLink.get(1);
			if(startNode==source){
				if(!brother.contains(destination)){
					brother.add(destination);
				}
			}
		}
		return brother;
	}
	
	private boolean isready(ArrayList<Integer>offspringNodes,ArrayList<Integer> brothers){
		int size=brothers.size();
		int acount=0;
		for(int a=0;a<offspringNodes.size();a++){
			int offspring=offspringNodes.get(a);
			if(brothers.contains(offspring)){
				acount++;
			}
		}
		if(acount==size){
			return true;
		}
		return false;
	}
}
