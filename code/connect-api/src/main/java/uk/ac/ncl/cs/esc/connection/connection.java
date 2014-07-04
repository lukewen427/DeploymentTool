package uk.ac.ncl.cs.esc.connection;


import com.connexience.api.StorageClient;
import com.connexience.api.WorkflowClient;

public interface connection {
	public StorageClient getStorageAPI();
	public WorkflowClient getWorkflowAPI();
	public void setInfo(String hostname,int port,Boolean secure,String username,String password);
}
