package uk.ac.ncl.cs.esc.read;


 public class WorkflowModel implements WorkflowTemplate {
	 
	 double[][] workflow;
	    int[][] dataSecurity;
	    double [][] ccost;
	    double [][] cpucost;
	    int [] cloud;
	    int [][] ssecurity;
	    double [][] StorageTime;
	    double [] StorageCost;
	    void setWorkflow(double[][] workflow) {
	        this.workflow = workflow;
	    }

	    void setDataSecurity(int[][] dataSecurity) {
	        this.dataSecurity = dataSecurity;
	    }

	    void setCcost(double[][] ccost) {
	        this.ccost = ccost;
	    }

	    void setCpucost(double[][] cpucost) {
	        this.cpucost = cpucost;
	    }

	    void setCloud(int [] cloud) {
	        this.cloud = cloud;
	    }

	    void setSsecurity(int[][] ssecurity) {
	        this.ssecurity = ssecurity;
	    }
	    
	    void setStorageTime(double [][] StorageTime){
	    	this.StorageTime=StorageTime;
	    }
	    void setStorageCost(double [] StorageCost){
	    	this.StorageCost=StorageCost;
	    }
	    
	public double[][] getWorkflow() {
		// TODO Auto-generated method stub
		return workflow;
	}

	public int[][] getDataSecurity() {
		// TODO Auto-generated method stub
		return dataSecurity;
	}

	public double[][] getCcost() {
		// TODO Auto-generated method stub
		return ccost;
	}

	public double[][] getCpucost() {
		// TODO Auto-generated method stub
		return cpucost;
	}

	public int[] getCloud() {
		// TODO Auto-generated method stub
		return cloud;
	}

	public int[][] getSsecurity() {
		// TODO Auto-generated method stub
		return ssecurity;
	}
	
	public double[][] getStorageTime() {
		// TODO Auto-generated method stub
		
		return this.StorageTime;
	}
	@Override
	public double[] getStorageCost() {
		// TODO Auto-generated method stub
		return this.StorageCost;
	}

}
