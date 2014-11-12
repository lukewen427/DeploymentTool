package uk.ac.ncl.cs.esc.connection;

import com.connexience.api.StorageClient;
import com.connexience.api.WorkflowClient;
import com.connexience.api.model.EscUser;
import com.connexience.api.model.EscWorkflow;
import com.connexience.server.workflow.api.API;

public class cloudConnection {
	
	
	public connection creatCon(String cloudName){
		String hostname = null;
		switch(cloudName){
		case "cloud1": hostname="10.66.66.176";break;
		case "cloud2": hostname="10.66.66.252";break;
		case "cloud3": hostname="10.66.66.253";
		} 
	//	System.out.println(cloudName);
	//	System.out.println(hostname);
		connection con=new connectionIm();
		con.setInfo(hostname, 8080, false, "rawa_qasha@yahoo.com", "123");
		return con;
	}
	
	public static void main(String [] args) throws Exception{
		cloudConnection test=new cloudConnection();
		connection con=test.creatCon("cloud1");
		API api=con.getAPI();
	//	 WorkflowClient wfClient=con.getWorkflowAPI();
	//	 StorageClient client =con.getStorageAPI();
	//	 EscUser currentUser = client.currentUser();
//		 System.out.println(currentUser.getName());
    //	 System.out.println(api.getHostName());
		
	
		 
	}
}
