package uk.ac.ncl.cs.esc.repartition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import uk.ac.ncl.cs.esc.read.Block;

public interface partitionWorkflow {
	
	public HashMap<Block,Integer> mappingCloud(); 
    public void workflowSplit(HashMap<Block,Integer> option);
    public HashMap<Integer,ArrayList<ArrayList<String>>> getLinks();
    public LinkedList<ArrayList<String>> getOrder();
}
