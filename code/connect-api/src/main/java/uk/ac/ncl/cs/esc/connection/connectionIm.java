package uk.ac.ncl.cs.esc.connection;

import com.connexience.api.StorageClient;
import com.connexience.api.WorkflowClient;
import com.connexience.server.ConnexienceException;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.workflow.api.API;
import com.connexience.server.workflow.api.ApiProvider;

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
	
	public API getAPI() throws Exception{
		ApiProvider apiProvider = new ApiProvider();
        apiProvider.setHostName(hostname);
        apiProvider.setHttpPort(8080);
        apiProvider.setServerContext("/workflow");
        API api = apiProvider.createApi();
        api.authenticate(username, password);
     //   Ticket t = api.getTicket();
   //     System.out.println("Authenticated: " + t.getUserId());
        
        return api;
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
