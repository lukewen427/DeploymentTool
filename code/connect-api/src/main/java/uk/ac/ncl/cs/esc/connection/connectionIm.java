package uk.ac.ncl.cs.esc.connection;

import com.connexience.api.StorageClient;
import com.connexience.api.WorkflowClient;

public class connectionIm implements connection {
	 String hostname;
     int port = 80;
     Boolean secure;
     String username; 
     String password;
  //   private IStorageService storageService;
//     private IWorkflowService workflowService;
	public StorageClient getStorageAPI() {
		StorageClient client = new StorageClient(hostname, port, secure, username, password);
		return client;
	}

	public WorkflowClient getWorkflowAPI() {
		 WorkflowClient wfClient = new WorkflowClient(hostname, port, secure, username, password);
		return wfClient;
	}

	public void setInfo(String hostname, int port, Boolean secure,
			String username, String password) {
		
		this.hostname=hostname;
		this.port=port;
		this.username=username;
		this.password=password;
		this.secure=secure;
		
	}

}
