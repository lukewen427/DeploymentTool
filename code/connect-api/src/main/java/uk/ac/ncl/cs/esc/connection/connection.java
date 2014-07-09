package uk.ac.ncl.cs.esc.connection;


import com.connexience.api.StorageClient;
import com.connexience.api.WorkflowClient;
import com.connexience.server.workflow.api.API;

public interface connection {
	public StorageClient getStorageAPI();
	public WorkflowClient getWorkflowAPI();
	public API getAPI() throws Exception;
	public void setInfo(String hostname,int port,Boolean secure,String username,String password);
	
}
