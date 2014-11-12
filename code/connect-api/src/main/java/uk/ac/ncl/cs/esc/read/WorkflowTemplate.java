package uk.ac.ncl.cs.esc.read;

import java.util.ArrayList;

public interface WorkflowTemplate {



	public double[][] getWorkflow();

    public int[][] getDataSecurity();

    public double[][] getCcost();

    public double[][] getCpucost();

    public int[] getCloud();

    public int[][] getSsecurity();
    
    public double[][] getStorageTime();
    
    public double[] getStorageCost();
    
    public double[] getCloudStartTime();
    
    public double[] blockExecutionTime();
	public double[] getcriticalPath();
	
	public ArrayList<Integer> getRootNodes();

}