package uk.ac.ncl.cs.esc.test;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.ncl.cs.esc.connection.cloudConnection;
import uk.ac.ncl.cs.esc.connection.connection;
import uk.ac.ncl.cs.esc.workflow.WorkflowInfo;
import uk.ac.ncl.cs.esc.workflow.workflowInfoIm;

public class downLoad {
	public  static void main(String args[]) throws Exception{
		cloudConnection test=new cloudConnection();
		connection con=test.creatCon("cloud1");
		String workflowId="1191";
		WorkflowInfo workflow= new workflowInfoIm(con);
		HashMap<String, String>blocks=workflow.Blocklist(workflowId);
		ArrayList<ArrayList<String>> connection=workflow.getConnection(workflowId);
		System.out.println(blocks);
		System.out.println(connection);
	}
}
