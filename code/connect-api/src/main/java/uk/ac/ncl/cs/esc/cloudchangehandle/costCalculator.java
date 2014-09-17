package uk.ac.ncl.cs.esc.cloudchangehandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.common.collect.HashBiMap;

import uk.ac.ncl.cs.esc.read.readUnpWorkflow;

public class costCalculator {
	
	LinkedList<String> clouds;
	ArrayList<ArrayList<String>> connections;
	HashMap<String,ArrayList<String>> blockInfo;
	double totalCost;
    int [][] deployment;
    HashBiMap< String,Integer> biMap;
    ArrayList<Object> inputLinks;
	public costCalculator(LinkedList<String> clouds,ArrayList<ArrayList<String>> connections,
								HashMap<String,ArrayList<String>> blockInfo,ArrayList<Object> inputLinks){
		this.clouds=clouds;
		this.connections=connections;
		this.blockInfo=blockInfo;
		this.inputLinks=inputLinks;
	}
	
	public HashBiMap< String,Integer> getMap(){
		return biMap;
	}
	
	public int [][] getDeployment(){
		return deployment;
	}
	
	public double getCost(){
		return totalCost;
	}
	
	void loadUnpWorkflow(){
		readUnpWorkflow ruw=new readUnpWorkflow(connections,blockInfo,clouds,inputLinks);
		this.totalCost=ruw.getTotalCost();
		this.biMap=ruw.getMap();
		this.deployment=ruw.getDeployment();
	}
}
