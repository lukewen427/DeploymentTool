package uk.ac.ncl.cs.esc.newpartitiontool;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.ncl.cs.esc.read.Block;

public interface partitionWorkflow {
	
	public HashMap<Block,Integer> mappingCloud(); 
    public ArrayList<Object> workflowSplit(HashMap<Block,Integer> option);
    public ArrayList<Object> getLinks();
}
