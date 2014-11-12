package uk.ac.ncl.cs.esc.repartition;

import java.util.ArrayList;
import java.util.LinkedList;
public class creatOrder {

	public creatOrder(){
	//	this.blockSet=blockSet;
	}
	public void theOrder(ArrayList<String>offspringNodes,ArrayList<String>copy,ArrayList<ArrayList<String>> connections,
			LinkedList<ArrayList<String>> order, ArrayList<String> visited) {
		if (offspringNodes.isEmpty()) {
			return;
		} else {
			if(!copy.isEmpty()){
				order.add((ArrayList<String>) copy.clone());
			}
			
			ArrayList<String> nodeCollection = new ArrayList<String>();
			ArrayList<String> Collection = new ArrayList<String>();
		//	ArrayList<Block> collect = new ArrayList<Block>();
			for (int a = 0; a < offspringNodes.size(); a++) {
				String blockId = offspringNodes.get(a);
				for (int i = 0; i < connections.size(); i++) {
					ArrayList<String> link = connections.get(i);
					String start = link.get(0);
					String end = link.get(1);
					if (blockId.equals(end)) {
						if (!visited.contains(start)) {
							ArrayList<String> brothers = findBrothers(start,
									connections);
							if (isready(offspringNodes, brothers)) {
								if (!nodeCollection.contains(start)) {
									nodeCollection.add(start);
									Collection.add(start);
									visited.add(start);
								} 
							}else {
								nodeCollection.add(blockId);
							}
						}
					}
				}

			}
			theOrder(nodeCollection,Collection,connections,order,visited);
		}
	}
	
	private ArrayList<String> findBrothers(String startNode,ArrayList<ArrayList<String>> connections){
		ArrayList<String> brother=new ArrayList<String>();
		for(int i=0;i< connections.size();i++){
			 ArrayList<String> link=connections.get(i);
			 String start=link.get(0);
			 String end=link.get(1);
			if(startNode==start){
				if(!brother.contains(end)){
					brother.add(end);
				}
			}
		}
		return brother;
	}
	
	private boolean isready(ArrayList<String>offspringNodes,ArrayList<String> brothers){
		int size=brothers.size();
		int acount=0;
		for(int a=0;a<offspringNodes.size();a++){
			String offspring=offspringNodes.get(a);
		//	String id=offspring.getBlockId();
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
