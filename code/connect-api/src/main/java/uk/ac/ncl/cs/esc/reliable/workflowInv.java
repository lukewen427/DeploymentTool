package uk.ac.ncl.cs.esc.reliable;

import uk.ac.ncl.cs.esc.connection.cloudConnection;
import uk.ac.ncl.cs.esc.connection.connection;
import uk.ac.ncl.cs.esc.workflow.deployWFIm;

import com.connexience.server.workflow.api.API;

public class workflowInv implements Runnable {
	String cloudName;
	String workflowId;
	public workflowInv(String workflowId, String cloudName){
		this.cloudName=cloudName;
		this.workflowId=workflowId;
		System.out.println(cloudName);
		System.out.println(workflowId);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		cloudConnection coCloud = new cloudConnection();
		connection con = coCloud.creatCon(cloudName);
		API api;
		try {
			api = con.getAPI();
			deployWFIm wf = new deployWFIm(api);
			wf.workflowInvo(workflowId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("done");
	}

}
