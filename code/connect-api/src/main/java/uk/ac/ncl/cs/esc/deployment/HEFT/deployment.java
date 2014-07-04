package uk.ac.ncl.cs.esc.deployment.HEFT;

import java.util.ArrayList;
import java.util.HashMap;

public interface deployment {
	
	public void createpartitionGraph();
	
	public void createDeployGraph();
	
	public HashMap<Integer,ArrayList<Object>> getPartitionGraph();
	
	public ArrayList<Object> getDeployLink();
}
